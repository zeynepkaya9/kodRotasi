package com.jsy.platform.taskengine.api;

import com.jsy.platform.auth.domain.UserAccount;
import com.jsy.platform.auth.infrastructure.UserAccountRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import com.jsy.platform.taskengine.application.TaskEngineService;
import com.jsy.platform.taskengine.domain.ProjectCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TaskEngineController {

    private final TaskEngineService taskEngineService;
    private final UserAccountRepository userAccountRepository;
    private final UserProgressRepository userProgressRepository;

    public TaskEngineController(TaskEngineService taskEngineService,
                                UserAccountRepository userAccountRepository,
                                UserProgressRepository userProgressRepository) {
        this.taskEngineService = taskEngineService;
        this.userAccountRepository = userAccountRepository;
        this.userProgressRepository = userProgressRepository;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> getProjects() {
        return ResponseEntity.ok(taskEngineService.getAllProjects());
    }

    @PostMapping("/projects/{code}/start")
    public ResponseEntity<List<TaskDto>> startProject(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable ProjectCode code) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(taskEngineService.startProject(userId, code));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskDto>> getMyTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        UserProgress progress = userProgressRepository.findByUserId(userId).orElse(null);
        if (progress == null || progress.getActiveProjectId() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(taskEngineService.getProjectTasks(userId, progress.getActiveProjectId()));
    }

    @GetMapping("/steps/{stepId}")
    public ResponseEntity<StepDto> getStep(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long stepId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(taskEngineService.getStep(userId, stepId));
    }

    @PutMapping("/steps/{stepId}/draft")
    public ResponseEntity<Void> saveDraft(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long stepId, @jakarta.validation.Valid @RequestBody DraftRequest request) {
        taskEngineService.saveDraft(getUserId(userDetails), stepId, request.code());
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(UserDetails userDetails) {
        UserAccount account = userAccountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
        return account.getId();
    }
}
