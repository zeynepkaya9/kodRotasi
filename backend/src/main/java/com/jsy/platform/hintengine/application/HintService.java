package com.jsy.platform.hintengine.application;

import com.jsy.platform.hintengine.api.HintResponseDto;
import com.jsy.platform.hintengine.domain.Hint;
import com.jsy.platform.hintengine.domain.HintLevel;
import com.jsy.platform.hintengine.infrastructure.HintRepository;
import com.jsy.platform.learningpath.domain.LearningPath;
import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.learningpath.infrastructure.LearningPathRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.BusinessException;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import com.jsy.platform.taskengine.domain.UserStepProgress;
import com.jsy.platform.taskengine.domain.StepStatus;
import com.jsy.platform.taskengine.infrastructure.UserStepProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class HintService {

    private final HintRepository hintRepository;
    private final UserStepProgressRepository stepProgressRepository;
    private final UserProgressRepository userProgressRepository;
    private final LearningPathRepository learningPathRepository;

    private static final HintLevel[] HINT_ORDER = {
            HintLevel.SMALL, HintLevel.GUIDE, HintLevel.CODE, HintLevel.SOLUTION
    };

    public HintService(HintRepository hintRepository,
                       UserStepProgressRepository stepProgressRepository,
                       UserProgressRepository userProgressRepository,
                       LearningPathRepository learningPathRepository) {
        this.hintRepository = hintRepository;
        this.stepProgressRepository = stepProgressRepository;
        this.userProgressRepository = userProgressRepository;
        this.learningPathRepository = learningPathRepository;
    }

    @Transactional
    public HintResponseDto getNextHint(Long userId, Long stepId) {
        UserStepProgress stepProgress = stepProgressRepository.findForUpdate(userId, stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım ilerlemesi bulunamadı"));

        if (stepProgress.getStatus() != StepStatus.ACTIVE) {
            throw new BusinessException("İpuçları yalnızca aktif adımlarda kullanılabilir.");
        }

        Level userLevel = learningPathRepository.findByUserId(userId)
                .map(LearningPath::getLevel)
                .orElse(Level.BEGINNER);

        int hintsUsed = stepProgress.getHintsUsed();
        int maxHints = getMaxHints(userLevel);

        if (hintsUsed >= maxHints) {
            // Hak dolduğunda son ipucunu tekrar göstermek yeni hak tüketmez ve XP kesmez.
            HintResponseDto last = revealedHints(stepId, userLevel, hintsUsed).get(hintsUsed - 1);
            return new HintResponseDto(last.hintLevel(), last.content(), 0, 0);
        }

        int targetIndex = getTargetHintIndex(userLevel, hintsUsed);
        HintLevel targetLevel = HINT_ORDER[targetIndex];

        Hint hint = hintRepository.findByStepIdAndHintLevel(stepId, targetLevel)
                .orElseGet(() -> hintRepository.findByStepIdOrderByHintLevel(stepId)
                        .stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Bu adım için ipucu bulunamadı")));

        int xpPenalty = calculateXpPenalty(userLevel, targetIndex);

        stepProgress.incrementHintsUsed();
        stepProgressRepository.save(stepProgress);

        if (xpPenalty > 0) {
            UserProgress userProgress = userProgressRepository.findByUserId(userId)
                    .orElse(new UserProgress(userId));
            userProgress.deductXp(xpPenalty);
            userProgressRepository.save(userProgress);
        }

        int remaining = maxHints - stepProgress.getHintsUsed();
        return new HintResponseDto(targetLevel, hint.getContent(), xpPenalty, remaining);
    }

    @Transactional(readOnly = true)
    public com.jsy.platform.hintengine.api.HintHistoryDto getHintHistory(Long userId, Long stepId) {
        UserStepProgress progress = stepProgressRepository.findByUserIdAndStepId(userId, stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım ilerlemesi bulunamadı"));
        if (progress.getStatus() == StepStatus.LOCKED) {
            throw new BusinessException("Kilitli adımın ipuçları görüntülenemez.");
        }
        Level level = learningPathRepository.findByUserId(userId)
                .map(LearningPath::getLevel)
                .orElse(Level.BEGINNER);
        int maxHints = getMaxHints(level);
        int used = Math.min(progress.getHintsUsed(), maxHints);
        return new com.jsy.platform.hintengine.api.HintHistoryDto(
                revealedHints(stepId, level, used), Math.max(0, maxHints - used));
    }

    private List<HintResponseDto> revealedHints(Long stepId, Level level, int count) {
        List<HintResponseDto> result = new ArrayList<>();
        for (int usedBefore = 0; usedBefore < count; usedBefore++) {
            int targetIndex = getTargetHintIndex(level, usedBefore);
            HintLevel targetLevel = HINT_ORDER[targetIndex];
            Hint hint = hintRepository.findByStepIdAndHintLevel(stepId, targetLevel)
                    .orElseGet(() -> hintRepository.findByStepIdOrderByHintLevel(stepId)
                            .stream().findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Bu adım için ipucu bulunamadı")));
            result.add(new HintResponseDto(
                    targetLevel,
                    hint.getContent(),
                    calculateXpPenalty(level, targetIndex),
                    Math.max(0, getMaxHints(level) - usedBefore - 1)));
        }
        return List.copyOf(result);
    }

    private int getTargetHintIndex(Level level, int hintsUsed) {
        return switch (level) {
            case BEGINNER -> Math.min(hintsUsed, HINT_ORDER.length - 1);
            case INTERMEDIATE -> Math.min(1 + hintsUsed, HINT_ORDER.length - 1);
            case ADVANCED -> Math.min(2 + hintsUsed, HINT_ORDER.length - 1);
        };
    }

    private int getMaxHints(Level level) {
        return switch (level) {
            case BEGINNER -> 4;
            case INTERMEDIATE -> 3;
            case ADVANCED -> 1;
        };
    }

    private int calculateXpPenalty(Level level, int hintIndex) {
        return switch (level) {
            case BEGINNER -> hintIndex <= 1 ? 0 : (hintIndex == 2 ? 2 : 5);
            case INTERMEDIATE -> hintIndex <= 1 ? 2 : 5;
            case ADVANCED -> 10;
        };
    }
}
