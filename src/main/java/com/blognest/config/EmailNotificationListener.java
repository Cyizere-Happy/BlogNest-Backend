package com.blognest.config;

import com.blognest.models.JudgeAssignment;
import com.blognest.models.enums.AssignmentStatus;
import com.blognest.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleJudgeAssignment(JudgeAssignment assignment) {
        if (assignment.getStatus() == AssignmentStatus.PENDING) {
            try {
                Context context = new Context();
                context.setVariable("judgeName", assignment.getJudge().getFullName());
                context.setVariable("assignmentType", assignment.getType().name());
                context.setVariable("targetId", assignment.getTargetId().toString());
                context.setVariable("assignedAt", assignment.getAssignedAt().toString());
                context.setVariable("dashboardLink", "https://blognest.com/judge/dashboard");

                emailService.sendTemplateEmail(
                        assignment.getJudge().getEmail(),
                        "New BlogNest Evaluation Task Assigned",
                        "email/judge-assigned",
                        context
                );
            } catch (Exception e) {
                // Fail gracefully so background thread exceptions don't pollute logs or impact core flow
                System.err.println("Failed to send judge assignment email: " + e.getMessage());
            }
        }
    }
}
