package com.blognest.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWriterApplicationRequest {

    private String motivation;

    private String sampleWriting;
}