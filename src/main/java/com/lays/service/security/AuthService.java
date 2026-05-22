package com.lays.service.security;

import com.lays.dto.security.JwtRefreshRequest;
import com.lays.dto.security.JwtRequest;
import com.lays.dto.security.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest jwtRequest);
    JwtResponse refreshToken(JwtRefreshRequest jwtRefreshRequest);
    JwtResponse token(JwtRefreshRequest jwtRefreshRequest);
}
