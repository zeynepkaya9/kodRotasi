package com.jsy.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.platform.evaluation.application.EvaluationService;
import com.jsy.platform.hintengine.application.HintService;
import com.jsy.platform.hintengine.infrastructure.HintRepository;
import com.jsy.platform.learningpath.api.AssessmentSubmitRequest;
import com.jsy.platform.learningpath.application.LevelAssessmentService;
import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.learningpath.infrastructure.LearningPathRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.BusinessException;
import com.jsy.platform.taskengine.application.TaskEngineService;
import com.jsy.platform.taskengine.domain.*;
import com.jsy.platform.taskengine.infrastructure.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LearningFlowTest {
    private final LearningPathRepository paths = mock(LearningPathRepository.class);
    private final StepRepository steps = mock(StepRepository.class);
    private final UserStepProgressRepository progress = mock(UserStepProgressRepository.class);
    private final UserProgressRepository users = mock(UserProgressRepository.class);
    private final TaskEngineService tasks = mock(TaskEngineService.class);
    private final EvaluationService evaluation = new EvaluationService(steps, progress, users, paths, tasks, new ObjectMapper());

    @Test void rejectsIncompleteAndInvalidAssessments() {
        var service = new LevelAssessmentService(paths);
        assertThrows(BusinessException.class, () -> service.submitAssessment(1L, new AssessmentSubmitRequest(List.of(1))));
        assertThrows(BusinessException.class, () -> service.submitAssessment(1L, new AssessmentSubmitRequest(null)));
        for (Integer invalid : Arrays.asList(null, -1, 4)) {
            var answers = new ArrayList<>(Collections.nCopies(12, 1));
            answers.set(0, invalid);
            assertThrows(BusinessException.class, () -> service.submitAssessment(1L, new AssessmentSubmitRequest(answers)));
        }
        verifyNoInteractions(paths);
    }

    @Test void completeCorrectAssessmentIsAdvanced() {
        var result = new LevelAssessmentService(paths).submitAssessment(1L,
                new AssessmentSubmitRequest(List.of(1, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1)));
        assertEquals(Level.ADVANCED, result.level());
    }

    private UserStepProgress prepare(StepStatus status, String spec) {
        var step = new Step(0, "instruction", "", "", spec);
        var state = new UserStepProgress(1L, 2L, status);
        when(steps.findById(2L)).thenReturn(Optional.of(step));
        when(progress.findForUpdate(1L, 2L)).thenReturn(Optional.of(state));
        return state;
    }

    @Test void lockedStepCannotBeEvaluatedOrHinted() {
        var state = prepare(StepStatus.LOCKED, "{}");
        assertThrows(BusinessException.class, () -> evaluation.evaluate(1L, 2L, "class A {}"));
        var hints = mock(HintRepository.class);
        assertThrows(BusinessException.class, () -> new HintService(hints, progress, users, paths).getNextHint(1L, 2L));
        assertEquals(0, state.getAttempts());
        verifyNoInteractions(hints, users, tasks);
    }

    @Test void repeatedSuccessAwardsXpOnlyOnce() {
        var state = prepare(StepStatus.ACTIVE,
                "{\"astRules\":[{\"id\":\"class\",\"check\":\"classNamed('A')\"}]}");
        var user = new UserProgress(1L);
        when(users.findByUserId(1L)).thenReturn(Optional.of(user));
        assertEquals(20, evaluation.evaluate(1L, 2L, "class A {}").xpEarned());
        var completedAt = state.getCompletedAt();
        var repeat = evaluation.evaluate(1L, 2L, "class A {}");
        assertTrue(repeat.passed());
        assertEquals(0, repeat.xpEarned());
        assertEquals(20, user.getTotalXp());
        assertEquals(completedAt, state.getCompletedAt());
        verify(tasks, times(1)).unlockNextStep(1L, 2L);
    }

    @Test void absentOrBrokenRulesNeverCompleteStep() {
        for (String spec : Arrays.asList(null, "", "{}", "{\"astRules\":[]}", "invalid")) {
            var state = prepare(StepStatus.ACTIVE, spec);
            assertFalse(evaluation.evaluate(1L, 2L, "class A {}").passed());
            assertEquals(StepStatus.ACTIVE, state.getStatus());
        }
        verifyNoInteractions(users, tasks);
    }
}
