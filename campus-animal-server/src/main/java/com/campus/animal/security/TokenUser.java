package com.campus.animal.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenUser {
    private final Long userId;
    private final String username;
    private final String role;
}
