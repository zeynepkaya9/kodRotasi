package com.jsy.platform.quiz.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.platform.quiz.api.QuizDto;
import com.jsy.platform.quiz.api.QuizResultDto;
import com.jsy.platform.quiz.domain.Quiz;
import com.jsy.platform.quiz.infrastructure.QuizRepository;
import com.jsy.platform.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    public QuizService(QuizRepository quizRepository, ObjectMapper objectMapper) {
        this.quizRepository = quizRepository;
        this.objectMapper = objectMapper;
    }

    public List<QuizDto> getQuizzesByConcept(Long conceptId) {
        return quizRepository.findByConceptId(conceptId).stream()
                .map(q -> new QuizDto(q.getId(), q.getQuestion(), parseOptions(q.getOptions())))
                .toList();
    }

    public QuizResultDto answerQuiz(Long quizId, int selectedIndex) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz bulunamadı"));
        boolean correct = quiz.getCorrectIndex() == selectedIndex;
        return new QuizResultDto(correct, quiz.getExplanation());
    }

    private List<String> parseOptions(String optionsJson) {
        try {
            return objectMapper.readValue(optionsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of(optionsJson);
        }
    }
}
