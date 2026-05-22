package com.lays.service.security;

import com.lays.dto.security.JwtRefreshRequest;
import com.lays.dto.security.JwtRequest;
import com.lays.dto.security.JwtResponse;
import com.lays.filter.JwtProvider;
import com.lays.model.User;
import com.lays.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Map<String, String> refreshTokens = new HashMap<>();
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public JwtResponse login(JwtRequest jwtRequest) {
        User user = userRepository.findByUsername(jwtRequest.login())
                .orElseThrow(() -> new UsernameNotFoundException(jwtRequest.login()));

        if (bCryptPasswordEncoder.matches(jwtRequest.password(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refershToken = jwtProvider.generateRefreshToken(user);
            refreshTokens.put(accessToken, refershToken);
            return new JwtResponse(accessToken, refershToken);
        }
        throw new UsernameNotFoundException(jwtRequest.login());
    }

    @Override
    public JwtResponse refreshToken(JwtRefreshRequest jwtRefreshRequest) {
        if (jwtProvider.validateRefreshToken(jwtRefreshRequest.token())) {
            Claims claims = jwtProvider.getRefreshClaims(jwtRefreshRequest.token());
            String username = claims.getSubject();
            String refreshToken = refreshTokens.get(username);

            if (refreshToken != null && refreshToken.equals(jwtRefreshRequest.token())) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username));

                String accessToken = jwtProvider.generateAccessToken(user);
                String newRefresh = jwtProvider.generateRefreshToken(user);
                refreshTokens.put(username, newRefresh);
                return new JwtResponse(accessToken, newRefresh);
            }

        }
        throw new BadCredentialsException("Refresh token is invalid");
    }

    @Override
    public JwtResponse token(JwtRefreshRequest jwtRefreshRequest) {
        return null;
    }
}
