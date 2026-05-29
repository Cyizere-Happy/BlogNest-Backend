package com.blognest.services.impl;

import com.blognest.dtos.CreateDailyMessageRequest;
import com.blognest.dtos.DailyMessageResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.models.*;
import com.blognest.models.enums.NotificationType;
import com.blognest.repositories.DailyMessageRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.DailyMessageService;
import com.blognest.services.EmailService;
import com.blognest.services.NotificationService;
import com.blognest.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyMessageServiceImpl implements DailyMessageService {

    private final DailyMessageRepository dailyMessageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final AuthService authService;

    @Override
    public DailyMessageResponse createMessage(CreateDailyMessageRequest request) {
        UUID adminId = authService.getCurrentUserId();

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        // (OPTIONAL) role check placeholder
        // if (!admin.getRole().equals(Role.SUPERADMIN)) throw new UnauthorizedException();

        LocalDate today = LocalDate.now();

        // ❌ prevent duplicate daily message
        if (dailyMessageRepository.findByDate(today).isPresent()) {
            throw new RuntimeException("Daily message already exists for today");
        }

        DailyMessage message = DailyMessage.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .date(today)
                .createdBy(admin)
                .build();

        DailyMessage saved = dailyMessageRepository.save(message);

        broadcastToAllUsers(saved);

        return DailyMessageResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .message(saved.getMessage())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<DailyMessageResponse> getAllMessages() {

        return dailyMessageRepository.findAll()
                .stream()
                .map(m -> DailyMessageResponse.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .message(m.getMessage())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public DailyMessageResponse getTodayMessage() {

        DailyMessage message = dailyMessageRepository.findByDate(LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No message for today"));

        return DailyMessageResponse.builder()
                .id(message.getId())
                .title(message.getTitle())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }

    // 🔥 CORE BROADCAST ENGINE
    private void broadcastToAllUsers(DailyMessage message) {

        List<User> users = userRepository.findAll();

        for (User user : users) {

            // 1. Notification
            notificationService.createNotification(
                    user.getId(),
                    "Daily Message",
                    message.getTitle(),
                    NotificationType.DAILY_MESSAGE
            );

            // 2. Email
            try {
                Context context = new Context();
                context.setVariable("username", user.getFullName());
                context.setVariable("title", message.getTitle());
                context.setVariable("message", message.getMessage());

                emailService.sendTemplateEmail(
                        user.getEmail(),
                        "Daily Message: " + message.getTitle(),
                        "email/daily-message",
                        context
                );

            } catch (Exception e) {
                System.out.println("Daily message email failed: " + e.getMessage());
            }
        }
    }
}