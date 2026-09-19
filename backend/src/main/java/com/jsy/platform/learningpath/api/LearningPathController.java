package com.jsy.platform.learningpath.api;

import com.jsy.platform.auth.domain.UserAccount;
import com.jsy.platform.auth.infrastructure.UserAccountRepository;
import com.jsy.platform.learningpath.application.LevelAssessmentService;
import com.jsy.platform.learningpath.domain.Architecture;
import com.jsy.platform.learningpath.domain.LearningPath;
import com.jsy.platform.learningpath.infrastructure.LearningPathRepository;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class LearningPathController {

    private final LevelAssessmentService assessmentService;
    private final LearningPathRepository learningPathRepository;
    private final UserAccountRepository userAccountRepository;

    public LearningPathController(LevelAssessmentService assessmentService,
                                  LearningPathRepository learningPathRepository,
                                  UserAccountRepository userAccountRepository) {
        this.assessmentService = assessmentService;
        this.learningPathRepository = learningPathRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/assessment/questions")
    public ResponseEntity<List<AssessmentQuestionDto>> getQuestions() {
        return ResponseEntity.ok(assessmentService.getQuestions());
    }

    @PostMapping("/assessment/submit")
    public ResponseEntity<AssessmentResultDto> submit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AssessmentSubmitRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(assessmentService.submitAssessment(userId, request));
    }

    @GetMapping("/learning-path")
    public ResponseEntity<LearningPathDto> getLearningPath(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        LearningPath path = learningPathRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenme yolu bulunamadı. Önce seviye testi yapın."));
        return ResponseEntity.ok(new LearningPathDto(
                path.getLevel(), path.getRecommendedArchitecture(), path.getChosenArchitecture()));
    }

    @PatchMapping("/learning-path/architecture")
    public ResponseEntity<Map<String, String>> changeArchitecture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        Long userId = getUserId(userDetails);
        LearningPath path = learningPathRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenme yolu bulunamadı"));
        Architecture arch = Architecture.valueOf(body.get("architecture"));
        path.setChosenArchitecture(arch);
        learningPathRepository.save(path);
        return ResponseEntity.ok(Map.of("chosenArchitecture", arch.name()));
    }

    private Long getUserId(UserDetails userDetails) {
        UserAccount account = userAccountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
        return account.getId();
    }
}
