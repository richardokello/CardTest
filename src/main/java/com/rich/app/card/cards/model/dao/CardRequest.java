package com.rich.app.card.cards.model.dao;

import com.rich.app.card.cards.model.Status;
import com.rich.app.card.users.models.ApplicationUser;
import jakarta.persistence.Basic;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {
    private  Long cardId;
    @NotNull
    private Long  userId;
    private ApplicationUser user;
    @Basic(optional = false)
    @NotNull
    private String name;
    @Basic
    private String description;
    @Pattern(regexp = "^#[a-zA-Z0-9]{6}$", message = "Color should be in the format '#RRGGBB'")
    private String color;
    @Enumerated(EnumType.STRING)
    private Status status;
}
