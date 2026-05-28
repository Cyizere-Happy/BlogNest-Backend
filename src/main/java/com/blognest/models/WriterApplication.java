package com.blognest.models;

import com.blognest.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "writer_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriterApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    @Column(columnDefinition = "TEXT")
    private String sampleWriting;

    private boolean approved = false;

    private boolean reviewed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private User applicant;

    @CreationTimestamp
    private LocalDateTime createdAt;
}