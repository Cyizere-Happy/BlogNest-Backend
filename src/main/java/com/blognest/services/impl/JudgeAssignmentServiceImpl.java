package com.blognest.services.impl;

import com.blognest.dtos.AssignmentResponse;
import com.blognest.dtos.CreateAssignmentRequest;
import com.blognest.dtos.ScoreAssignmentRequest;
import com.blognest.exceptions.DuplicateResourceException;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.exceptions.UnauthorizedException;
import com.blognest.mappers.AssignmentMapper;
import com.blognest.models.*;
import com.blognest.models.enums.AssignmentStatus;
import com.blognest.models.enums.Role;
import com.blognest.repositories.*;
import com.blognest.services.AuthService;
import com.blognest.services.JudgeAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JudgeAssignmentServiceImpl implements JudgeAssignmentService {

    private final JudgeAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final WriterApplicationRepository applicationRepository;
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        UUID actorId = authService.getCurrentUserId();
        User superadmin = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Superadmin not found"));

        if (superadmin.getRole() != Role.SUPERADMIN) {
            throw new UnauthorizedException("Only SUPERADMIN can assign tasks.");
        }

        User judge = userRepository.findById(request.getJudgeId())
                .orElseThrow(() -> new ResourceNotFoundException("Judge not found"));

        if (judge.getRole() != Role.JUDGE) {
            throw new IllegalArgumentException("Target user is not a JUDGE.");
        }

        // Check if assignment already exists
        if (assignmentRepository.existsByJudge_IdAndTargetIdAndType(request.getJudgeId(), request.getTargetId(), request.getType())) {
            throw new DuplicateResourceException("This task is already assigned to this judge.");
        }

        // Conflict of interest validation
        switch (request.getType()) {
            case SUBMISSION:
                Submission submission = submissionRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
                if (submission.getWriter().getId().equals(request.getJudgeId())) {
                    throw new SecurityException("Conflict of Interest: A judge cannot review their own submission.");
                }
                break;
            case WRITER_APPLICATION:
                WriterApplication application = applicationRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new ResourceNotFoundException("Writer application not found"));
                if (application.getApplicant().getId().equals(request.getJudgeId())) {
                    throw new SecurityException("Conflict of Interest: A judge cannot review their own application.");
                }
                break;
        }

        JudgeAssignment assignment = JudgeAssignment.builder()
                .judge(judge)
                .targetId(request.getTargetId())
                .type(request.getType())
                .status(AssignmentStatus.PENDING)
                .assignedBy(superadmin)
                .assignedAt(LocalDateTime.now())
                .build();

        JudgeAssignment saved = assignmentRepository.save(assignment);

        // Publish event for email notification and audit trail
        eventPublisher.publishEvent(saved);

        eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                actorId,
                superadmin.getRole().name(),
                "JUDGE_ASSIGNMENT_CREATED",
                saved.getId(),
                "JudgeAssignment",
                null,
                "Judge: " + judge.getId() + ", Target: " + saved.getTargetId() + ", Type: " + saved.getType(),
                getClientIp(),
                true,
                com.blognest.models.enums.SeverityLevel.INFO
        ));

        return AssignmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AssignmentResponse scoreAssignment(UUID assignmentId, ScoreAssignmentRequest request) {
        UUID actorId = authService.getCurrentUserId();
        User currentUser = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        JudgeAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Only the assigned judge or SUPERADMIN can score it
        if (!assignment.getJudge().getId().equals(actorId) && currentUser.getRole() != Role.SUPERADMIN) {
            throw new UnauthorizedException("You are not authorized to score this assignment.");
        }

        if (assignment.getStatus() == AssignmentStatus.COMPLETED && currentUser.getRole() != Role.SUPERADMIN) {
            throw new IllegalStateException("This assignment has already been evaluated.");
        }

        assignment.setScore(request.getScore());
        assignment.setFeedback(request.getFeedback());
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());

        JudgeAssignment saved = assignmentRepository.save(assignment);

        // Update target resource score if it is a submission
        if (saved.getType() == com.blognest.models.enums.AssignmentType.SUBMISSION) {
            submissionRepository.findById(saved.getTargetId()).ifPresent(sub -> {
                sub.setScore(request.getScore());
                submissionRepository.save(sub);
            });
        }

        // Publish update event (for audit trail)
        eventPublisher.publishEvent(saved);

        eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                actorId,
                currentUser.getRole().name(),
                "JUDGE_ASSIGNMENT_SCORED",
                saved.getId(),
                "JudgeAssignment",
                null,
                "Score: " + saved.getScore() + ", Feedback: " + saved.getFeedback(),
                getClientIp(),
                true,
                com.blognest.models.enums.SeverityLevel.INFO
        ));

        return AssignmentMapper.toResponse(saved);
    }

    private String getClientIp() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attrs =
                    (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                jakarta.servlet.http.HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // fallback
        }
        return "127.0.0.1";
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getMyPendingAssignments() {
        UUID judgeId = authService.getCurrentUserId();
        return assignmentRepository.findByJudge_IdAndStatus(judgeId, AssignmentStatus.PENDING)
                .stream()
                .map(AssignmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getMyAssignments() {
        UUID judgeId = authService.getCurrentUserId();
        return assignmentRepository.findByJudge_Id(judgeId)
                .stream()
                .map(AssignmentMapper::toResponse)
                .toList();
    }
}
