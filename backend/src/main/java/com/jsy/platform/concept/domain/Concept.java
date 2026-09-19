package com.jsy.platform.concept.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "concept")
public class Concept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(name = "real_life_analogy", columnDefinition = "TEXT")
    private String realLifeAnalogy;

    @Column(name = "why_explanation", columnDefinition = "TEXT")
    private String whyExplanation;

    @Column(name = "wrong_example", columnDefinition = "TEXT")
    private String wrongExample;

    @Column(name = "wrong_example_explanation", columnDefinition = "TEXT")
    private String wrongExampleExplanation;

    @Column(name = "right_example", columnDefinition = "TEXT")
    private String rightExample;

    @Column(name = "right_example_explanation", columnDefinition = "TEXT")
    private String rightExampleExplanation;

    public Concept() {
    }

    public Concept(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public Long getId() { return id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRealLifeAnalogy() { return realLifeAnalogy; }
    public void setRealLifeAnalogy(String s) { this.realLifeAnalogy = s; }

    public String getWhyExplanation() { return whyExplanation; }
    public void setWhyExplanation(String s) { this.whyExplanation = s; }

    public String getWrongExample() { return wrongExample; }
    public void setWrongExample(String s) { this.wrongExample = s; }

    public String getWrongExampleExplanation() { return wrongExampleExplanation; }
    public void setWrongExampleExplanation(String s) { this.wrongExampleExplanation = s; }

    public String getRightExample() { return rightExample; }
    public void setRightExample(String s) { this.rightExample = s; }

    public String getRightExampleExplanation() { return rightExampleExplanation; }
    public void setRightExampleExplanation(String s) { this.rightExampleExplanation = s; }
}
