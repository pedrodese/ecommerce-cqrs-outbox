package com.marketflow.service;


import com.marketflow.domain.entity.RefreshToken;
import com.marketflow.domain.entity.User;
import com.marketflow.dto.auth.LoginRequest;
import com.marketflow.dto.auth.RefreshRequest;
import com.marketflow.dto.auth.RegisterRequest;
import com.marketflow.dto.auth.TokenResponse;
import com.marketflow.exception.EmailAlreadyExistsException;
import com.marketflow.exception.InvalidCredentialsException;
import com.marketflow.exception.InvalidTokenException;
import com.marketflow.repository.RefreshTokenRepository;
import com.marketflow.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Inject
    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.of(request);
        userRepository.persist(user);

        return generateTokenPair(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!BcryptUtil.matches(request.password(), user.passwordHash)) {
            throw new InvalidCredentialsException();
        }

        refreshTokenRepository.revokeAllByUserId(user.id);

        return generateTokenPair(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(InvalidTokenException::new);

        if (!refreshToken.isValid()) {
            throw new InvalidTokenException();
        }

        refreshToken.revoked = true;

        return generateTokenPair(refreshToken.user);
    }

    private TokenResponse generateTokenPair(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String rawRefreshToken = jwtService.generateRefreshToken();

        refreshTokenRepository.persist(
                RefreshToken.of(user, rawRefreshToken, jwtService.getRefreshTokenExpirationSeconds())
        );

        return TokenResponse.of(accessToken, rawRefreshToken);
    }
}