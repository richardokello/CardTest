package com.rich.app.card.cards.service;

import com.rich.app.card.cards.model.Card;
import com.rich.app.card.cards.model.Status;
import com.rich.app.card.users.models.ApplicationUser;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class CardSpecification {
    public static Specification<Card> hasAccessToCard(ApplicationUser user) {
        return (root, query, criteriaBuilder) -> {
            // Joining the card's owner to the query
            root.fetch("owner");
            return criteriaBuilder.equal(root.get("owner"), user);
        };
    }

    public static Specification<Card> hasName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<Card> hasColor(String color) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("color"), color);
    }

    public static Specification<Card> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Card> hasDateOfCreation(Date dateOfCreation) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dateOfCreation"), dateOfCreation);
    }
}
