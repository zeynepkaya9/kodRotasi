package com.jsy.platform.taskengine.application;

import com.jsy.platform.learningpath.domain.LearningPath;
import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.learningpath.infrastructure.LearningPathRepository;
import com.jsy.platform.progress.domain.UserProgress;
import com.jsy.platform.progress.infrastructure.UserProgressRepository;
import com.jsy.platform.shared.exception.BusinessException;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import com.jsy.platform.taskengine.api.ProjectDto;
import com.jsy.platform.taskengine.api.StepDto;
import com.jsy.platform.taskengine.api.TaskDto;
import com.jsy.platform.taskengine.domain.*;
import com.jsy.platform.taskengine.infrastructure.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskEngineService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final StepRepository stepRepository;
    private final UserStepProgressRepository progressRepository;
    private final UserProgressRepository userProgressRepository;
    private final LearningPathRepository learningPathRepository;

    public TaskEngineService(ProjectRepository projectRepository,
                             TaskRepository taskRepository,
                             StepRepository stepRepository,
                             UserStepProgressRepository progressRepository,
                             UserProgressRepository userProgressRepository,
                             LearningPathRepository learningPathRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.progressRepository = progressRepository;
        this.userProgressRepository = userProgressRepository;
        this.learningPathRepository = learningPathRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(p -> {
                    int taskCount = taskRepository.findByProjectIdOrderByOrderIndex(p.getId()).size();
                    return new ProjectDto(p.getId(), p.getCode(), p.getTitle(),
                            p.getDescription(), p.getMinLevel(), taskCount);
                })
                .toList();
    }

    @Transactional
    public List<TaskDto> startProject(Long userId, ProjectCode projectCode) {
        Project project = projectRepository.findByCode(projectCode)
                .orElseThrow(() -> new ResourceNotFoundException("Proje bulunamadı: " + projectCode));

        UserProgress userProgress = userProgressRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı ilerlemesi bulunamadı"));
        userProgress.setActiveProjectId(project.getId());
        userProgressRepository.save(userProgress);

        List<Task> tasks = taskRepository.findByProjectIdOrderByOrderIndex(project.getId());
        for (Task task : tasks) {
            List<Step> steps = stepRepository.findByTaskIdOrderByOrderIndex(task.getId());
            for (Step step : steps) {
                if (progressRepository.findByUserIdAndStepId(userId, step.getId()).isEmpty()) {
                    StepStatus status = (task.getOrderIndex() == 0 && step.getOrderIndex() == 0)
                            ? StepStatus.ACTIVE : StepStatus.LOCKED;
                    progressRepository.save(new UserStepProgress(userId, step.getId(), status));
                }
            }
        }

        return getProjectTasks(userId, project.getId());
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getProjectTasks(Long userId, Long projectId) {
        List<Task> tasks = taskRepository.findByProjectIdOrderByOrderIndex(projectId);
        return tasks.stream().map(task -> {
            List<Step> steps = stepRepository.findByTaskIdOrderByOrderIndex(task.getId());
            List<StepDto> stepDtos = steps.stream().map(step -> {
                UserStepProgress usp = progressRepository.findByUserIdAndStepId(userId, step.getId())
                        .orElse(new UserStepProgress(userId, step.getId(), StepStatus.LOCKED));

                Level userLevel = getUserLevel(userId);
                String instruction = resolveInstruction(step, userLevel);

                return new StepDto(step.getId(), step.getOrderIndex(), instruction,
                        LearningNotesFactory.create(task, userLevel),
                        step.getStarterCode(), usp.getStatus(), usp.getAttempts(), usp.getHintsUsed(),
                        usp.getDraftCode() != null ? usp.getDraftCode() : usp.getLastSubmission(), usp.getDraftUpdatedAt());
            }).toList();
            return new TaskDto(task.getId(), task.getOrderIndex(), task.getTitle(),
                    task.getObjective(), task.getConceptCodes(), stepDtos);
        }).toList();
    }

    @Transactional(readOnly = true)
    public StepDto getStep(Long userId, Long stepId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım bulunamadı"));
        UserStepProgress usp = progressRepository.findByUserIdAndStepId(userId, stepId)
                .orElse(new UserStepProgress(userId, stepId, StepStatus.LOCKED));
        Level level = getUserLevel(userId);
        String instruction = resolveInstruction(step, level);
        return new StepDto(step.getId(), step.getOrderIndex(), instruction,
                LearningNotesFactory.create(step.getTask(), level),
                step.getStarterCode(), usp.getStatus(), usp.getAttempts(), usp.getHintsUsed(),
                        usp.getDraftCode() != null ? usp.getDraftCode() : usp.getLastSubmission(), usp.getDraftUpdatedAt());
    }

    @Transactional
    public void saveDraft(Long userId, Long stepId, String code) {
        UserStepProgress progress = progressRepository.findForUpdate(userId, stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım ilerlemesi bulunamadı"));
        if (progress.getStatus() == StepStatus.LOCKED) {
            throw new BusinessException("Kilitli adım için taslak kaydedilemez.");
        }
        progress.saveDraft(code);
        progressRepository.save(progress);
    }

    @Transactional
    public void unlockNextStep(Long userId, Long completedStepId) {
        Step completed = stepRepository.findById(completedStepId)
                .orElseThrow(() -> new ResourceNotFoundException("Adım bulunamadı"));

        List<Step> taskSteps = stepRepository.findByTaskIdOrderByOrderIndex(completed.getTask().getId());
        int nextIndex = completed.getOrderIndex() + 1;

        Step nextStep = taskSteps.stream()
                .filter(s -> s.getOrderIndex() == nextIndex)
                .findFirst()
                .orElse(null);

        if (nextStep != null) {
            UserStepProgress usp = progressRepository.findByUserIdAndStepId(userId, nextStep.getId())
                    .orElse(new UserStepProgress(userId, nextStep.getId(), StepStatus.LOCKED));
            if (usp.getStatus() == StepStatus.LOCKED) {
                usp.setStatus(StepStatus.ACTIVE);
                progressRepository.save(usp);
            }
        } else {
            Task currentTask = completed.getTask();
            List<Task> projectTasks = taskRepository.findByProjectIdOrderByOrderIndex(
                    currentTask.getProject().getId());
            Task nextTask = projectTasks.stream()
                    .filter(t -> t.getOrderIndex() == currentTask.getOrderIndex() + 1)
                    .findFirst()
                    .orElse(null);
            if (nextTask != null) {
                List<Step> nextTaskSteps = stepRepository.findByTaskIdOrderByOrderIndex(nextTask.getId());
                if (!nextTaskSteps.isEmpty()) {
                    Step firstStep = nextTaskSteps.get(0);
                    UserStepProgress usp = progressRepository.findByUserIdAndStepId(userId, firstStep.getId())
                            .orElse(new UserStepProgress(userId, firstStep.getId(), StepStatus.LOCKED));
                    if (usp.getStatus() == StepStatus.LOCKED) {
                        usp.setStatus(StepStatus.ACTIVE);
                        progressRepository.save(usp);
                    }
                }
            }
        }
    }

    private Level getUserLevel(Long userId) {
        return learningPathRepository.findByUserId(userId)
                .map(LearningPath::getLevel)
                .orElse(Level.BEGINNER);
    }

    private String resolveInstruction(Step step, Level level) {
        return switch (level) {
            case BEGINNER -> step.getInstructionBeginner() != null
                    ? step.getInstructionBeginner() : step.getInstruction();
            case ADVANCED -> step.getInstructionAdvanced() != null
                    ? step.getInstructionAdvanced() : step.getInstruction();
            default -> step.getInstruction();
        };
    }
}
