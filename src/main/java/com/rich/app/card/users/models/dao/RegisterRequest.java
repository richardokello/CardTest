package com.rich.app.card.users.models.dao;

import com.rich.app.card.users.models.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String description;
    private String email;
    private String password;
    private Roles role;
}
