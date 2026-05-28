package com.blognest.repositories;

import com.blognest.models.User;
import com.blognest.models.WriterApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WriterApplicationRepository extends JpaRepository<WriterApplication, UUID> {

    Optional<WriterApplication> findByApplicant(User applicant);

    boolean existsByApplicant(User applicant);
}