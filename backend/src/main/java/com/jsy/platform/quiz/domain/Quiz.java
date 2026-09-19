package com.jsy.platform.quiz.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concept_id", nullable = false)
    private Long conceptId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String options;

    @Column(name = "correct_index", nullable = false)
    private int correctIndex;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    public Quiz() {
    }

    public Quiz(Long conceptId, String question, String options, int correctIndex, String explanation) {
        this.conceptId = conceptId;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public Long getId() { return id; }

    public Long getConceptId() { return conceptId; }
    public void setConceptId(Long conceptId) { this.conceptId = conceptId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
