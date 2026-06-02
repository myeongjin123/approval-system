package com.portfolio.approval.domain.user.repository;

import com.portfolio.approval.domain.user.entity.User;
import com.portfolio.approval.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRole(UserRole role);
}
