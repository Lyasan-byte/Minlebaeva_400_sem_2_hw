package com.lays.dto.security;

public record JwtResponse (
        String accessToken,
        String refreshToken
) {}
