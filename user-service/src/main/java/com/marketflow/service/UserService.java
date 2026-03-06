package com.marketflow.service;


import com.marketflow.domain.entity.User;
import com.marketflow.domain.enums.UserStatus;
import com.marketflow.dto.user.UpdateUserRequest;
import com.marketflow.exception.UserNotFoundException;
import com.marketflow.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    private final JsonWebToken jwt;
    private final UserRepository userRepository;

    @Inject
    public UserService(JsonWebToken jwt, UserRepository userRepository) {
        this.jwt = jwt;
        this.userRepository = userRepository;
    }

    public User findMe() {
        UUID userId = UUID.fromString(jwt.getSubject());
        return findByIdOrThrow(userId);
    }

    public User findById(UUID id) {
        return findByIdOrThrow(id);
    }

    @Transactional
    public User update(UUID id, UpdateUserRequest request) {
        User user = findByIdOrThrow(id);
        user.name = request.name();
        user.phone = request.phone();
        return user;
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = findByIdOrThrow(id);
        user.status = UserStatus.INACTIVE;
    }

    private User findByIdOrThrow(UUID id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}