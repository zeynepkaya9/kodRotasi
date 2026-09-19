package com.jsy.platform.quiz.api;

import com.jsy.platform.concept.domain.Concept;
import com.jsy.platform.concept.infrastructure.ConceptRepository;
import com.jsy.platform.quiz.application.QuizService;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class QuizController {

    private final QuizService quizService;
    private final ConceptRepository conceptRepository;

    public QuizController(QuizService quizService, ConceptRepository conceptRepository) {
        this.quizService = quizService;
        this.conceptRepository = conceptRepository;
    }

    @GetMapping("/concepts/{code}/quiz")
    public ResponseEntity<List<QuizDto>> getQuizzes(@PathVariable String code) {
        Concept concept = conceptRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Kavram bulunamadı"));
        return ResponseEntity.ok(quizService.getQuizzesByConcept(concept.getId()));
    }

    @PostMapping("/quiz/{quizId}/answer")
    public ResponseEntity<QuizResultDto> answer(
            @PathVariable Long quizId,
            @RequestBody QuizAnswerRequest request) {
        return ResponseEntity.ok(quizService.answerQuiz(quizId, request.selectedIndex()));
    }
}
