package com.rich.app.card.cards.repository;

import com.rich.app.card.cards.model.Card;
import com.rich.app.card.users.models.ApplicationUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(ApplicationUser user);

    List<Card> findAll(Specification<Card> specification, Sort sort);

    Optional<Card> findByIdAndUser(Long cardId, ApplicationUser user);


    List<Card> findAll(Specification<Card> specification);

   // Optional<Card> findByIdAndUserId(Long cardId, Long userId);
//
    Optional<Card> findByIdAndUserEmail(Long cardId, String userEmail);

    Optional<Card> findByIdAndUserId(Long cardId, Long userId);
}
