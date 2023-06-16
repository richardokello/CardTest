package com.rich.app.card;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rich.app.card.cards.controller.CardController;
import com.rich.app.card.cards.model.Card;
import com.rich.app.card.cards.service.CardService;
import com.rich.app.card.security.config.JwtService;
import com.rich.app.card.users.models.ApplicationUser;
import com.rich.app.card.users.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CardControllerTest {
    @Mock
    private CardService cardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CardController cardController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }

    @Test
    public void testGetCardById_ValidCardIdAndUser() throws Exception {
        // Prepare test data
        Long cardId = 1L;
        String authToken = "valid_token";

        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@example.com");

        Card card = new Card();
        card.setId(cardId);

        when(jwtService.getUsernameFromJwt(authToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cardService.getCardByIdAndUserId(cardId, user)).thenReturn(Optional.of(card));

        // Perform the GET request

        MvcResult result = mockMvc.perform(get("/cards/getCardById/{cardId}", cardId)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode responseJson = new ObjectMapper().readTree(jsonResponse);

        Long id = responseJson.get("id").asLong();
        assertEquals(cardId, id);

        // Verify the interactions
        verify(jwtService).getUsernameFromJwt(authToken);
        verify(userRepository).findByEmail(user.getEmail());
        verify(cardService).getCardByIdAndUserId(cardId, user);
    }

    @Test
    public void testGetCardById_InvalidCardId() throws Exception {
        // Prepare test data
        Long cardId = 1L;
        String authToken = "valid_token";

        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@example.com");

        when(jwtService.getUsernameFromJwt(authToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cardService.getCardByIdAndUserId(cardId, user)).thenReturn(Optional.empty());

        // Perform the GET request
        mockMvc.perform(get("/cards/getCardById/{cardId}", cardId)
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());

        // Verify the interactions
        verify(jwtService).getUsernameFromJwt(authToken);
        verify(userRepository).findByEmail(user.getEmail());
        verify(cardService).getCardByIdAndUserId(cardId, user);
    }

    // Add more test cases for other controller methods here
    // ...

}
