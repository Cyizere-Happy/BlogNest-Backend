package com.blognest.repositories;

import com.blognest.models.DailyMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyMessageRepository extends JpaRepository<DailyMessage, UUID> {

    Optional<DailyMessage> findByDate(LocalDate date);
}