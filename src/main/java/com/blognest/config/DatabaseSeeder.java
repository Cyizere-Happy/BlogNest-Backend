package com.blognest.config;

import com.blognest.models.User;
import com.blognest.models.enums.Role;
import com.blognest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@blognest.com")) {
            User admin = User.builder()
                    .fullName("BlogNest Admin")
                    .username("admin")
                    .email("admin@blognest.com")
                    .password("admin123")
                    .bio("System Administrator")
                    .role(Role.ADMIN)
                    .verified(true)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Successfully seeded Admin user: admin/admin123");
        } else {
            log.info("Admin user already exists, skipping seeding.");
        }
    }
}
