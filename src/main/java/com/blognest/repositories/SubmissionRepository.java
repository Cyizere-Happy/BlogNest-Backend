package com.blognest.repositories;

import com.blognest.models.Competition;
import com.blognest.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    List<Submission> findByCompetition(Competition competition);

    List<Submission> findByWriter(com.blognest.models.User writer);

    // 🔥 RANKED LEADERBOARD QUERY
    @Query("""
        SELECT s FROM Submission s
        WHERE s.competition.id = :competitionId
        ORDER BY s.score DESC, s.submittedAt ASC
    """)
    List<Submission> getLeaderboard(@Param("competitionId") UUID competitionId);
}