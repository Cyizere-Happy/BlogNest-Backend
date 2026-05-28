package com.blognest.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDailyMessageRequest {

    private String title;

    private String message;
}