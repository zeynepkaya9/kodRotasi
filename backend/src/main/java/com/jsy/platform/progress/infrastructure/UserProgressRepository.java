package com.jsy.platform.progress.infrastructure;

import com.jsy.platform.progress.domain.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserId(Long userId);
}
