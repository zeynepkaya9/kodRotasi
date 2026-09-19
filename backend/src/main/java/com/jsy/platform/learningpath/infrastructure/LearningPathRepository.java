package com.jsy.platform.learningpath.infrastructure;

import com.jsy.platform.learningpath.domain.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    Optional<LearningPath> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
