package com.blognest.repositories;

import com.blognest.models.WriterApplication;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WriterApplicationRepository extends JpaRepository<WriterApplication, UUID> {

    List<WriterApplication> findByApplicant(User applicant);

    List<WriterApplication> findByReviewedFalse();
}