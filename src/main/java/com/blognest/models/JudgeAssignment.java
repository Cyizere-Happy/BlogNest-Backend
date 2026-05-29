package com.blognest.models;

import com.blognest.models.enums.AssignmentStatus;
import com.blognest.models.enums.AssignmentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "judge_assignments",
        indexes = {
                @Index(name = "idx_assignment_judge_id", columnList = "judge_id"),
                @Index(name = "idx_assignment_target_id", columnList = "target_id"),
                @Index(name = "idx_assignment_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;

    @Column(name = "target_id", nullable = false)
    private UUID targetId; // UUID of Submission or WriterApplication

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType type; // SUBMISSION, WRITER_APPLICATION

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status; // PENDING, COMPLETED

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;
}
