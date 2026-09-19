package com.jsy.platform.taskengine.infrastructure;

import com.jsy.platform.taskengine.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByOrderIndex(Long projectId);
}
