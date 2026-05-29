package com.blognest.services;

import com.blognest.dtos.CreateInviteRequest;
import com.blognest.dtos.InviteResponse;

import java.util.List;
import java.util.UUID;

public interface InviteService {

    InviteResponse sendInvite(UUID adminId, CreateInviteRequest request);

    InviteResponse validateInvite(String token);

    void markInviteAsUsed(String token);

    List<InviteResponse> getAllInvites();
}
