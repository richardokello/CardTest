package com.rich.app.card.cards.controller;

import com.rich.app.card.cards.model.Card;
import com.rich.app.card.cards.model.dao.CardRequest;
import com.rich.app.card.cards.service.CardService;
import com.rich.app.card.security.config.JwtService;
import com.rich.app.card.users.models.ApplicationUser;
import com.rich.app.card.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards")

@CrossOrigin("*")
public class CardController {
    @Autowired
    private  final CardService cardService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtService jwtService;

    public CardController(CardService cardService, UserRepository userRepository, JwtService jwtService) {
        this.cardService = cardService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Card>> searchCards(
            @RequestBody CardRequest cardRequest,
            @RequestParam(name = "createdDate", required = false) Date createdDate,
            Pageable pageable,
            Sort sort
    ) {
        List<Card> cards = cardService.searchCards(cardRequest, createdDate, pageable, sort);
        return ResponseEntity.ok(cards);
    }

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody CardRequest card) {
        Card createdCard = cardService.createCard(card);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }
    @GetMapping("getCardById/{cardId}")
    public ResponseEntity<Card> getCardById(@PathVariable Long cardId, @RequestHeader("Authorization") String authToken) {
       String  userEmail = jwtService.getUsernameFromJwt(authToken);
       Optional<ApplicationUser> user=userRepository.findByEmail(userEmail);

        Optional<Card> card = cardService.getCardByIdAndUserId(cardId, user.get());
        if (card.isPresent()) {
            return ResponseEntity.ok(card.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping("getCardById/{cardId}")
//    public ResponseEntity<Card> getCardById(@PathVariable Long cardId, @AuthenticationPrincipal ApplicationUser user) {
//        Optional<Card> card = cardService.getCardByIdAndUserId(cardId, user);
//        if (card.isPresent()) {
//            return ResponseEntity.ok(card.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


    @PutMapping("updateCard/{cardId}")
    public ResponseEntity<Void> updateCard(@PathVariable Long cardId, @RequestBody CardRequest request, @RequestHeader("Authorization") String authToken) {

        String  userEmail = jwtService.getUsernameFromJwt(authToken);
        Optional<ApplicationUser> user=userRepository.findByEmail(userEmail);
       Optional<Card> existingCard = cardService.getCardByIdAndUserId(cardId, user.get());
        if (existingCard.isPresent()) {
            cardService.updateCard(request);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/deleteCard/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId, @RequestHeader("Authorization") String authToken) {

        String  userEmail = jwtService.getUsernameFromJwt(authToken);
        Optional<ApplicationUser> user=userRepository.findByEmail(userEmail);

        Optional<Card> existingCard = cardService.getCardByIdAndUserId(cardId, user.get());
        if (existingCard.isPresent()) {
            cardService.deleteCard(userEmail,existingCard.get().getId());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/getAllCards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Card>> getCards(
            @RequestBody ApplicationUser user,
            @RequestBody CardRequest request,
            @RequestParam(name = "dateOfCreation", required = false) Date dateOfCreation
    ) {
        List<Card> cards = cardService.getCards(user, request, dateOfCreation);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{cardId}/clear-description")
    public ResponseEntity<Void> clearDescription(@PathVariable Long cardId) {
        cardService.clearDescription(cardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cardId}/clear-color")
    public ResponseEntity<Void> clearColor(@PathVariable Long cardId) {
        cardService.clearColor(cardId);
        return ResponseEntity.noContent().build();
    }

}
