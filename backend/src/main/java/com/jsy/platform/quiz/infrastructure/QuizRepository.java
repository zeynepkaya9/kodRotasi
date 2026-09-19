package com.jsy.platform.quiz.infrastructure;

import com.jsy.platform.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByConceptId(Long conceptId);
}
