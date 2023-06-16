package com.rich.app.card.users.repository;

import com.rich.app.card.security.token.Token;
import com.rich.app.card.users.models.ApplicationUser;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;


import java.util.Optional;
@Component
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser>findByEmail(String email);
}
