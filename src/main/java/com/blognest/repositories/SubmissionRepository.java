package com.blognest.repositories;

import com.blognest.models.Competition;
import com.blognest.models.Submission;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    List<Submission> findByCompetition(Competition competition);

    List<Submission> findByWriter(User writer);
}