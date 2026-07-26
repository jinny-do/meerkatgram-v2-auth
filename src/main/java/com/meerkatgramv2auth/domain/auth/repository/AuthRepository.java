package com.meerkatgramv2auth.domain.auth.repository;

import com.meerkatgramv2auth.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Long> {
    // findById는 자동으로 되지만 나머지는 추상메소드를 만들어서 관리해야함
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
