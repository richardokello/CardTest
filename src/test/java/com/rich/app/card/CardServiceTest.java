package com.rich.app.card;

import com.rich.app.card.cards.model.Card;
import com.rich.app.card.cards.repository.CardRepository;
import com.rich.app.card.cards.service.CardService;
import com.rich.app.card.cards.service.CardServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class CardServiceTest {



        @Mock
        private CardRepository cardRepository;

        @InjectMocks
        private CardServiceImpl cardService;

       @org.junit.Test
        public void testClearDescription() {
            // Arrange
            Long cardId = 1L;
            Card card = new Card();
            card.setId(cardId);
            card.setDescription("Sample description");

            Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

            // Act
            cardService.clearDescription(cardId);

            // Assert
            Assert.assertNull(card.getDescription());
            Mockito.verify(cardRepository, Mockito.times(1)).save(card);
        }

        @org.junit.Test
        public void testClearColor() {
            // Arrange
            Long cardId = 1L;
            Card card = new Card();
            card.setId(cardId);
            card.setColor("#FF0000");

            Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

            // Act
            cardService.clearColor(cardId);

            // Assert
            Assert.assertNull(card.getColor());
            Mockito.verify(cardRepository, Mockito.times(1)).save(card);
        }


    @org.junit.Test
    public void testClearDescription_WhenCardNotFound() {
        // Arrange
        Long cardId = 1L;

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act and Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cardService.clearDescription(cardId);
        });

        assertEquals("Card not found with ID: " + cardId, exception.getMessage());
    }








}
