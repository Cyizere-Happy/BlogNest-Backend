package com.blognest.repositories;

import com.blognest.models.JudgeAssignment;
import com.blognest.models.enums.AssignmentStatus;
import com.blognest.models.enums.AssignmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JudgeAssignmentRepository extends JpaRepository<JudgeAssignment, UUID> {

    Optional<JudgeAssignment> findByJudge_IdAndTargetIdAndStatus(UUID judgeId, UUID targetId, AssignmentStatus status);

    List<JudgeAssignment> findByJudge_Id(UUID judgeId);

    List<JudgeAssignment> findByJudge_IdAndStatus(UUID judgeId, AssignmentStatus status);

    boolean existsByJudge_IdAndTargetIdAndType(UUID judgeId, UUID targetId, AssignmentType type);
}
