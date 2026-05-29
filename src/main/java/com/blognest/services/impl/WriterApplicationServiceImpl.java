package com.blognest.services.impl;

import com.blognest.dtos.CreateWriterApplicationRequest;
import com.blognest.dtos.WriterApplicationResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.WriterApplicationMapper;
import com.blognest.models.*;
import com.blognest.models.enums.NotificationType;
import com.blognest.models.enums.Role;
import com.blognest.repositories.WriterApplicationRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.EmailService;
import com.blognest.services.NotificationService;
import com.blognest.services.WriterApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WriterApplicationServiceImpl implements WriterApplicationService {

    private final WriterApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public WriterApplicationResponse apply(UUID userId, CreateWriterApplicationRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        WriterApplication application = WriterApplication.builder()
                .applicant(user)
                .motivation(request.getMotivation())
                .sampleWriting(request.getSampleWriting())
                .approved(false)
                .reviewed(false)
                .build();

        WriterApplication saved = applicationRepository.save(application);

        return WriterApplicationMapper.toResponse(saved);
    }

    @Override
    public List<WriterApplicationResponse> getAllApplications() {

        return applicationRepository.findAll()
                .stream()
                .map(WriterApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<WriterApplicationResponse> getPendingApplications() {

        return applicationRepository.findByReviewedFalse()
                .stream()
                .map(WriterApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public WriterApplicationResponse approveApplication(UUID applicationId) {

        WriterApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setApproved(true);
        app.setReviewed(true);

        WriterApplication saved = applicationRepository.save(app);

        User user = saved.getApplicant();

        // ✅ Promote user to WRITER role
        user.setRole(Role.WRITER);
        userRepository.save(user);

        // 🔥 NOTIFICATION
        notificationService.createNotification(
                user.getId(),
                "Writer Application Approved",
                "Congratulations! You are now a writer.",
                NotificationType.SUBSCRIPTION
        );

        // 🔥 EMAIL INVITE
        try {
            Context context = new Context();
            context.setVariable("username", user.getFullName());

            emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Writer Approval",
                    "email/writer-approved",
                    context
            );

        } catch (Exception e) {
            System.out.println("Writer approval email failed: " + e.getMessage());
        }

        return WriterApplicationMapper.toResponse(saved);
    }

    @Override
    public WriterApplicationResponse rejectApplication(UUID applicationId) {

        WriterApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setApproved(false);
        app.setReviewed(true);

        WriterApplication saved = applicationRepository.save(app);

        User user = saved.getApplicant();

        notificationService.createNotification(
                user.getId(),
                "Writer Application Rejected",
                "Unfortunately your application was not approved.",
                NotificationType.SUBSCRIPTION
        );

        // 🔥 EMAIL NOTIFICATION
        try {
            Context context = new Context();
            context.setVariable("username", user.getFullName());

            emailService.sendTemplateEmail(
                    user.getEmail(),
                    "Writer Application Update",
                    "email/writer-rejected",
                    context
            );

        } catch (Exception e) {
            System.out.println("Writer rejection email failed: " + e.getMessage());
        }

        return WriterApplicationMapper.toResponse(saved);
    }
}