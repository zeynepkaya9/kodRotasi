package com.jsy.platform.hintengine.infrastructure;

import com.jsy.platform.hintengine.domain.Hint;
import com.jsy.platform.hintengine.domain.HintLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HintRepository extends JpaRepository<Hint, Long> {
    Optional<Hint> findByStepIdAndHintLevel(Long stepId, HintLevel hintLevel);
    List<Hint> findByStepIdOrderByHintLevel(Long stepId);
}
