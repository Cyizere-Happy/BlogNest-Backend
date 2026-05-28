package com.blognest.services.impl;

import com.blognest.dtos.*;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.SubmissionMapper;
import com.blognest.models.*;
import com.blognest.models.enums.NotificationType;
import com.blognest.repositories.*;
import com.blognest.services.NotificationService;
import com.blognest.services.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CompetitionRepository competitionRepository;
    private final NotificationService notificationService;

    @Override
    public SubmissionResponse submit(UUID writerId, UUID competitionId, CreateSubmissionRequest request) {

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found"));

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        Submission submission = Submission.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .writer(writer)
                .competition(competition)
                .approved(false)
                .score(0)
                .build();

        return SubmissionMapper.toResponse(
                submissionRepository.save(submission)
        );
    }

    @Override
    public List<SubmissionResponse> getByCompetition(UUID competitionId) {

        Competition comp = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        return submissionRepository.findByCompetition(comp)
                .stream()
                .map(SubmissionMapper::toResponse)
                .toList();
    }

    @Override
    public List<SubmissionResponse> getByWriter(UUID writerId) {

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found"));

        return submissionRepository.findByWriter(writer)
                .stream()
                .map(SubmissionMapper::toResponse)
                .toList();
    }

    @Override
    public SubmissionResponse scoreSubmission(UUID submissionId, double score) {

        Submission sub = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        sub.setScore(score);

        Submission saved = submissionRepository.save(sub);

        notificationService.createNotification(
                saved.getWriter().getId(),
                "Competition Update",
                "Your submission received a score: " + score,
                NotificationType.COMPETITION
        );

        return SubmissionMapper.toResponse(saved);
    }

    @Override
    public SubmissionResponse approveSubmission(UUID submissionId) {

        Submission sub = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        sub.setApproved(true);

        Submission saved = submissionRepository.save(sub);

        notificationService.createNotification(
                saved.getWriter().getId(),
                "Submission Approved",
                "Your competition submission was approved!",
                NotificationType.COMPETITION
        );

        return SubmissionMapper.toResponse(saved);
    }
}