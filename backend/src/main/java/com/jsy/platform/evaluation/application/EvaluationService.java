package com.jsy.platform.evaluation.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.platform.evaluation.api.EvaluationResponse;
import com.jsy.platform.evaluation.domain.LineFeedback;
import com.jsy.platform.learningpath.domain.LearningPath;
import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.learningpath.infrastructure.LearningPathRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import com.jsy.platform.shared.exception.BusinessException;
import com.jsy.platform.taskengine.domain.Step;
import com.jsy.platform.taskengine.domain.StepStatus;
import com.jsy.platform.taskengine.domain.UserStepProgress;
import com.jsy.platform.taskengine.infrastructure.StepRepository;
import com.jsy.platform.taskengine.infrastructure.UserStepProgressRepository;
import com.jsy.platform.taskengine.application.TaskEngineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class EvaluationService {

    private final StepRepository stepRepository;
    private final UserStepProgressRepository stepProgressRepository;
    private final UserProgressRepository userProgressRepository;
    private final LearningPathRepository learningPathRepository;
    private final TaskEngineService taskEngineService;
    private final ObjectMapper objectMapper;

    public EvaluationService(StepRepository stepRepository,
                             UserStepProgressRepository stepProgressRepository,
                             UserProgressRepository userProgressRepository,
                             LearningPathRepository learningPathRepository,
                             TaskEngineService taskEngineService,
                             ObjectMapper objectMapper) {
        this.stepRepository = stepRepository;
        this.stepProgressRepository = stepProgressRepository;
        this.userProgressRepository = userProgressRepository;
        this.learningPathRepository = learningPathRepository;
        this.taskEngineService = taskEngineService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public EvaluationResponse evaluate(Long userId, Long stepId, String code) {
        if (code == null || code.isBlank() || code.length() > 100000) {
            throw new BusinessException("1–100000 karakter arasında Java kodu göndermelisin.");
        }
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım bulunamadı"));

        UserStepProgress progress = stepProgressRepository.findForUpdate(userId, stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım ilerlemesi bulunamadı"));

        if (progress.getStatus() == StepStatus.LOCKED) {
            throw new BusinessException("Önce önceki adımları tamamlamalısın.");
        }
        boolean alreadyCompleted = progress.getStatus() == StepStatus.COMPLETED;
        progress.incrementAttempts();
        progress.setLastSubmission(code);
        progress.saveDraft(code);

        Level userLevel = learningPathRepository.findByUserId(userId)
                .map(LearningPath::getLevel)
                .orElse(Level.BEGINNER);

        List<LineFeedback> feedbackList = runAstRules(step.getValidationSpec(), code, userLevel);

        boolean allPassed = feedbackList.stream()
                .allMatch(f -> "PASS".equals(f.status()));

        List<String> conceptsToReview = Collections.emptyList();
        int xpEarned = 0;

        if (allPassed && !alreadyCompleted) {
            progress.markCompleted();
            xpEarned = calculateXp(progress);

            UserProgress userProgress = userProgressRepository.findByUserId(userId)
                    .orElse(new UserProgress(userId));
            userProgress.addXp(xpEarned);
            userProgress.incrementStreak();
            userProgressRepository.save(userProgress);

            taskEngineService.unlockNextStep(userId, stepId);

            conceptsToReview = extractConcepts(step.getValidationSpec());
        }

        stepProgressRepository.save(progress);

        List<LineFeedback> filteredFeedback = filterByLevel(feedbackList, userLevel);

        return new EvaluationResponse(allPassed, filteredFeedback, xpEarned, conceptsToReview);
    }

    private List<LineFeedback> runAstRules(String validationSpec, String code, Level level) {
        List<LineFeedback> results = new ArrayList<>();

        if (validationSpec == null || validationSpec.isBlank()) {
            results.add(new LineFeedback("no-spec", "FAIL", "Bu adımın doğrulama kuralları henüz hazır değil.",
                    null, null, null));
            return results;
        }

        try {
            JsonNode spec = objectMapper.readTree(validationSpec);
            JsonNode astRules = spec.get("astRules");
            if (astRules == null || !astRules.isArray() || astRules.isEmpty()) {
                return List.of(new LineFeedback("no-rules", "FAIL",
                        "Bu adımın doğrulama kuralları henüz hazır değil.", null, null, null));
            }

            JavaSource source = new JavaSource(code);
            if (!source.errors().isEmpty()) {
                return List.of(new LineFeedback("java-syntax", "FAIL", String.join("\n", source.errors()),
                        "Önce Java sözdizimi hatalarını düzelt.", null, null));
            }
            for (JsonNode rule : astRules) {
                String ruleId = rule.get("id").asText();
                String check = rule.get("check").asText();
                boolean passed = source.check(check);

                String message = passed
                        ? (rule.has("onPass") ? rule.get("onPass").asText() : "Doğru!")
                        : (rule.has("onFail") ? rule.get("onFail").asText() : "Hata var");

                String why = rule.has("why") ? rule.get("why").asText() : null;
                String alternative = rule.has("alternative") ? rule.get("alternative").asText() : null;
                String realWorld = rule.has("realWorld") ? rule.get("realWorld").asText() : null;

                results.add(new LineFeedback(
                        ruleId,
                        passed ? "PASS" : "FAIL",
                        message, why, alternative, realWorld
                ));
            }
        } catch (Exception e) {
            results.add(new LineFeedback("parse-error", "FAIL",
                    "Doğrulama kuralı okunamadı", null, null, null));
        }

        return results;
    }

    private List<LineFeedback> filterByLevel(List<LineFeedback> feedback, Level level) {
        return switch (level) {
            case BEGINNER -> feedback;
            case INTERMEDIATE -> feedback.stream()
                    .map(f -> new LineFeedback(f.ruleId(), f.status(), f.message(), f.why(), null, null))
                    .toList();
            case ADVANCED -> feedback.stream()
                    .filter(f -> "FAIL".equals(f.status()))
                    .map(f -> new LineFeedback(f.ruleId(), f.status(), f.message(), null, null, null))
                    .toList();
        };
    }

    private int calculateXp(UserStepProgress progress) {
        int baseXp = 20;
        int attemptPenalty = Math.max(0, (progress.getAttempts() - 1) * 3);
        int hintPenalty = progress.getHintsUsed() * 2;
        return Math.max(5, baseXp - attemptPenalty - hintPenalty);
    }

    private List<String> extractConcepts(String validationSpec) {
        if (validationSpec == null) return Collections.emptyList();
        try {
            JsonNode spec = objectMapper.readTree(validationSpec);
            JsonNode concepts = spec.get("conceptsToOffer");
            if (concepts == null || !concepts.isArray()) return Collections.emptyList();
            List<String> result = new ArrayList<>();
            for (JsonNode c : concepts) result.add(c.asText());
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
