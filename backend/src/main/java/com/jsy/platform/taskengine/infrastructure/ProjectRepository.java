package com.jsy.platform.taskengine.infrastructure;

import com.jsy.platform.taskengine.domain.Project;
import com.jsy.platform.taskengine.domain.ProjectCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByCode(ProjectCode code);
}
