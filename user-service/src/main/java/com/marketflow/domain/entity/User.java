package com.marketflow.domain.entity;

import com.marketflow.domain.enums.UserRole;
import com.marketflow.domain.enums.UserStatus;
import com.marketflow.dto.auth.RegisterRequest;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    public UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public UserStatus status = UserStatus.ACTIVE;

    @Column(unique = true)
    public String phone;

    @Column(name = "created_at", updatable = false, nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at")
    public Instant updatedAt;

    public static User of(RegisterRequest request) {
        User user = new User();
        user.name = request.name();
        user.email = request.email();
        user.passwordHash = BcryptUtil.bcryptHash(request.password());
        user.role = request.role();
        user.phone = request.phone();
        return user;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}