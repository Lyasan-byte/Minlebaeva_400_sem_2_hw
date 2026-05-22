package com.lays.dto.security;

public record JwtRequest (
        String login,
        String password
) {}
