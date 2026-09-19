package com.jsy.platform.taskengine.infrastructure;

import com.jsy.platform.taskengine.domain.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByTaskIdOrderByOrderIndex(Long taskId);
}
