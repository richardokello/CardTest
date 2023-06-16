package com.rich.app.card.cards.service;

import com.rich.app.card.cards.model.Card;
import com.rich.app.card.cards.model.Status;
import com.rich.app.card.cards.model.dao.CardRequest;
import com.rich.app.card.cards.repository.CardRepository;
import com.rich.app.card.users.models.ApplicationUser;
import com.rich.app.card.users.repository.UserRepository;
import jakarta.el.ELException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
   private final UserRepository userRepository;
   private final CardRepository cardRepository;
    @Override
    public Card createCard(CardRequest request) {
        ApplicationUser user = userRepository.findByEmail(request.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getUser().getEmail()));

        Card card = new Card();
        card.setUser(user);
        card.setName(request.getName());
        card.setDescription(request.getDescription());
        card.setColor(request.getColor());
        card.setStatus(request.getStatus());

        return cardRepository.save(card);
    }

    @Override
    public List<Card> searchCards(CardRequest cardRequest, Date createdDate, Pageable pageable, Sort sort) {
       // (String userEmail, String name, String color, Status status, Date createdDate, Pageable pageable, Sort sort) {
        ApplicationUser user = userRepository.findByEmail(cardRequest.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + cardRequest.getUser().getEmail()));

        Specification<Card> specification = Specification.where(CardSpecification.hasAccessToCard(user));

        if (cardRequest.getName() != null) {
            specification = specification.and(CardSpecification.hasName(cardRequest.getName()));
        }
        if (cardRequest.getColor() != null) {
            specification = specification.and(CardSpecification.hasColor(cardRequest.getColor()));
        }
        if (cardRequest.getStatus() != null) {
            specification = specification.and(CardSpecification.hasStatus(cardRequest.getStatus()));
        }
        if (createdDate != null) {
            specification = specification.and(CardSpecification.hasDateOfCreation(createdDate));
        }

        return cardRepository.findAll(specification,pageable.getSort());

    }

    @Override
    public Optional<Card> getCardByIdAndUserId(Long cardId, ApplicationUser userId) {
        return Optional.empty();
    }

    @Override
    public Optional<Card> getCardByIdAndUserId(Long cardId, Long userId) {
        return cardRepository.findByIdAndUserId(cardId, userId);
    }
    @Override
    public Optional<Card> getCardByIdAndUserEmail(Long cardId, String userId) {
        return cardRepository.findByIdAndUserEmail(cardId, userId);
    }
    @Override
    public Card getCard(String userEmail, Long cardId) {
        ApplicationUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        return cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new ELException("Card not found with ID: " + cardId));
    }

    @Override
    public Card updateCard( CardRequest request) {
        ApplicationUser user = userRepository.findByEmail(request.getUser().getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getUser().getEmail()));

        Card card = cardRepository.findByIdAndUser(request.getCardId(), user)
                .orElseThrow(() -> new ELException("Card not found with ID: " + request.getCardId()));
        if (request.getName() != null) {
            card.setName(request.getName());
        }
        if (request.getDescription() != null) {
            card.setDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            card.setColor(request.getColor());
        }
        if (request.getStatus() != null) {
            card.setStatus(request.getStatus());
        }

        return cardRepository.save(card);
    }
    public List<Card> getCards(ApplicationUser user, CardRequest request, Date dateOfCreation) {
        Specification<Card> specification = Specification.where(CardSpecification.hasAccessToCard(user))
                .and(CardSpecification.hasName(request.getName()))
                .and(CardSpecification.hasColor(request.getColor()))
                .and(CardSpecification.hasStatus(request.getStatus()))
                .and(CardSpecification.hasDateOfCreation(dateOfCreation));

        return cardRepository.findAll(specification);
    }
    @Override
    public void deleteCard(String userEmail, Long cardId) {
           ApplicationUser user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

            Card card = cardRepository.findByIdAndUser(cardId, user)
                    .orElseThrow(() -> new ELException("Card not found with ID: " + cardId));

            cardRepository.delete(card);
    }



    public void clearDescription(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + cardId));

        card.clearDescription();

        cardRepository.save(card);
    }

    public void clearColor(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + cardId));

        card.clearColor();

        cardRepository.save(card);
    }
}
