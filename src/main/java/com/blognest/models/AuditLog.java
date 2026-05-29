package com.blognest.models;

import com.blognest.models.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_actor_id", columnList = "actor_id"),
                @Index(name = "idx_audit_timestamp", columnList = "timestamp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Column(name = "actor_role", nullable = false)
    private String actorRole;

    @Column(nullable = false)
    private String action; // login/logout, role change, ban, judge assignment, scoring, etc.

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    private boolean success;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity;
}
