package com.jsy.platform.taskengine.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "step")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String instruction;

    @Column(name = "instruction_beginner", columnDefinition = "TEXT")
    private String instructionBeginner;

    @Column(name = "instruction_advanced", columnDefinition = "TEXT")
    private String instructionAdvanced;

    @Column(name = "starter_code", columnDefinition = "TEXT")
    private String starterCode;

    @Column(name = "solution_code", columnDefinition = "TEXT")
    private String solutionCode;

    @Column(name = "validation_spec", columnDefinition = "TEXT")
    private String validationSpec;

    public Step() {
    }

    public Step(int orderIndex, String instruction, String starterCode,
                String solutionCode, String validationSpec) {
        this.orderIndex = orderIndex;
        this.instruction = instruction;
        this.starterCode = starterCode;
        this.solutionCode = solutionCode;
        this.validationSpec = validationSpec;
    }

    public Long getId() { return id; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }

    public String getInstructionBeginner() { return instructionBeginner; }
    public void setInstructionBeginner(String s) { this.instructionBeginner = s; }

    public String getInstructionAdvanced() { return instructionAdvanced; }
    public void setInstructionAdvanced(String s) { this.instructionAdvanced = s; }

    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }

    public String getSolutionCode() { return solutionCode; }
    public void setSolutionCode(String solutionCode) { this.solutionCode = solutionCode; }

    public String getValidationSpec() { return validationSpec; }
    public void setValidationSpec(String validationSpec) { this.validationSpec = validationSpec; }
}
