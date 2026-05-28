package com.blognest.services;

import com.blognest.dtos.CreateDailyMessageRequest;
import com.blognest.dtos.DailyMessageResponse;

import java.util.List;
import java.util.UUID;

public interface DailyMessageService {

    DailyMessageResponse createMessage(UUID adminId, CreateDailyMessageRequest request);

    List<DailyMessageResponse> getAllMessages();

    DailyMessageResponse getTodayMessage();
}