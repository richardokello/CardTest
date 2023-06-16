package com.rich.app.card.cards.service;

import com.rich.app.card.cards.model.Card;
import com.rich.app.card.cards.model.Status;
import com.rich.app.card.cards.model.dao.CardRequest;
import com.rich.app.card.users.models.ApplicationUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CardService {
   // Card createCard(String userEmail, String name, String description, String color);
    Card createCard(CardRequest request);

    List<Card> searchCards(CardRequest cardRequest, Date createdDate, Pageable pageable, Sort sort);
  Optional<Card> getCardByIdAndUserId(Long cardId, ApplicationUser userId);

    List<Card> getCards(ApplicationUser user, CardRequest request, Date dateOfCreation);
 Optional<Card> getCardByIdAndUserEmail(Long cardId, String userId);

 Card getCard(String userEmail, Long cardId);

   // Card updateCard(String userEmail, CardRequest request);
 Card updateCard(CardRequest request);
 Optional<Card> getCardByIdAndUserId(Long cardId, Long userId);

    void deleteCard(String userEmail, Long cardId);
    void clearColor(Long cardId);
    void clearDescription(Long cardId);
}
