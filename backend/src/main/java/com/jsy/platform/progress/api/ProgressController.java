package com.jsy.platform.progress.api;

import com.jsy.platform.auth.domain.UserAccount;
import com.jsy.platform.auth.infrastructure.UserAccountRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import com.jsy.platform.taskengine.domain.StepStatus;
import com.jsy.platform.taskengine.infrastructure.UserStepProgressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final UserProgressRepository userProgressRepository;
    private final UserStepProgressRepository stepProgressRepository;
    private final UserAccountRepository userAccountRepository;

    public ProgressController(UserProgressRepository userProgressRepository,
                              UserStepProgressRepository stepProgressRepository,
                              UserAccountRepository userAccountRepository) {
        this.userProgressRepository = userProgressRepository;
        this.stepProgressRepository = stepProgressRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public ResponseEntity<ProgressDto> getProgress(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        UserProgress progress = userProgressRepository.findByUserId(userId)
                .orElse(new UserProgress(userId));
        int completed = stepProgressRepository.countByUserIdAndStatus(userId, StepStatus.COMPLETED);
        int total = stepProgressRepository.findByUserId(userId).size();
        return ResponseEntity.ok(new ProgressDto(
                progress.getTotalXp(),
                progress.getCurrentStreak(),
                progress.getBadges(),
                completed, total
        ));
    }

    private Long getUserId(UserDetails userDetails) {
        UserAccount account = userAccountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
        return account.getId();
    }
}
