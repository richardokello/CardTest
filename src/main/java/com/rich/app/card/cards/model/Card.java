package com.rich.app.card.cards.model;

import com.rich.app.card.users.models.ApplicationUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Card {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @ManyToOne
        @JoinColumn(name = "User-Id", referencedColumnName = "userId", insertable = false,updatable = false)
        private ApplicationUser user;
        @Column(name = "User-Id")
        private Long userId;
        @NotNull
        @Size(max = 255)
        @Basic(optional = false)
        private String name;
        @Basic()
        @Size(max = 255)
        private String description;
        @Basic()
        @Pattern(regexp = "^#[a-zA-Z0-9]{6}$", message = "Color should be in the format '#RRGGBB'")
        @Size(max = 6)
        private String color;
        @Enumerated(EnumType.STRING)
        @Size(max = 255)
        @Basic()
        private Status status;


        public void clearDescription() {
                this.description = null;
        }

        public void clearColor() {
                this.color = null;
        }
}
