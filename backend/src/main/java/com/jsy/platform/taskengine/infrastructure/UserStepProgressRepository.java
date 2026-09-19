package com.jsy.platform.taskengine.infrastructure;

import com.jsy.platform.taskengine.domain.StepStatus;
import com.jsy.platform.taskengine.domain.UserStepProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserStepProgressRepository extends JpaRepository<UserStepProgress, Long> {
    Optional<UserStepProgress> findByUserIdAndStepId(Long userId, Long stepId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from UserStepProgress p where p.userId = :userId and p.stepId = :stepId")
    Optional<UserStepProgress> findForUpdate(@Param("userId") Long userId, @Param("stepId") Long stepId);
    List<UserStepProgress> findByUserId(Long userId);
    int countByUserIdAndStatus(Long userId, StepStatus status);
}
