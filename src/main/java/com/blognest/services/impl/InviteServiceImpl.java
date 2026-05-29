package com.blognest.services.impl;

import com.blognest.dtos.CreateInviteRequest;
import com.blognest.dtos.InviteResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.InviteMapper;
import com.blognest.models.Invite;
import com.blognest.models.User;
import com.blognest.repositories.InviteRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.EmailService;
import com.blognest.services.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public InviteResponse sendInvite(UUID adminId, CreateInviteRequest request) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        // Generate a secure unique token
        String token = UUID.randomUUID().toString().replace("-", "");

        Invite invite = Invite.builder()
                .email(request.getEmail())
                .token(token)
                .role(request.getRole())
                .used(false)
                .sentBy(admin)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        Invite saved = inviteRepository.save(invite);

        // Send invite email asynchronously
        try {
            Context context = new Context();
            context.setVariable("email", request.getEmail());
            context.setVariable("role", request.getRole().name());
            context.setVariable("token", token);
            context.setVariable("inviteLink", "https://blognest.com/register?token=" + token);
            context.setVariable("sentByName", admin.getFullName());

            emailService.sendTemplateEmail(
                    request.getEmail(),
                    "You have been invited to BlogNest",
                    "email/invite",
                    context
            );
        } catch (Exception e) {
            System.out.println("Invite email failed: " + e.getMessage());
        }

        return InviteMapper.toResponse(saved);
    }

    @Override
    public InviteResponse validateInvite(String token) {

        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or non-existent invite token"));

        if (invite.isUsed()) {
            throw new IllegalStateException("This invite has already been used");
        }

        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("This invite has expired");
        }

        return InviteMapper.toResponse(invite);
    }

    @Override
    public void markInviteAsUsed(String token) {

        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

        if (invite.isUsed()) {
            throw new IllegalStateException("This invite has already been used");
        }

        invite.setUsed(true);
        inviteRepository.save(invite);
    }

    @Override
    public List<InviteResponse> getAllInvites() {

        return inviteRepository.findAll()
                .stream()
                .map(InviteMapper::toResponse)
                .toList();
    }
}
