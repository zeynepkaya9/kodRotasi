package com.jsy.platform.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jsy.platform.evaluation.application.JavaSource;
import com.jsy.platform.taskengine.domain.Project;
import com.sun.source.tree.MethodTree;
import java.util.*;
import java.util.regex.Pattern;

/** Imports and visible acceptance criteria shared by reference solutions and starters. */
public final class CurriculumSources {
    private CurriculumSources() {}
    private static final Map<String, String> IMPORTS = new LinkedHashMap<>();
    static {
        group("jakarta.persistence", "Entity Table Id GeneratedValue GenerationType Column ManyToOne OneToMany ManyToMany OneToOne JoinColumn JoinTable FetchType CascadeType Version");
        group("jakarta.validation.constraints", "NotNull NotBlank Positive PositiveOrZero Size Email");
        group("jakarta.validation", "Valid");
        group("org.springframework.stereotype", "Service Component Repository");
        group("org.springframework.transaction.annotation", "Transactional");
        group("org.springframework.web.bind.annotation", "RestController RestControllerAdvice RequestMapping GetMapping PostMapping PutMapping DeleteMapping PatchMapping RequestBody RequestParam PathVariable ExceptionHandler");
        group("org.springframework.http", "ResponseEntity HttpStatus");
        group("org.springframework.data.jpa.repository", "JpaRepository Query");
        group("org.springframework.data.repository.query", "Param");
        group("java.util", "List Set Map HashSet ArrayList Optional");
        group("java.time", "LocalDate LocalDateTime");
        group("java.math", "BigDecimal");
        group("org.junit.jupiter.api", "Test");
    }
    private static void group(String pkg, String names) { for (String name : names.split(" ")) IMPORTS.put(name, pkg + "." + name); }
    public static String importsFor(String code) {
        var result = new StringBuilder();
        IMPORTS.forEach((name, qualified) -> {
            if (Pattern.compile("\\b" + name + "\\b").matcher(code).find() && !code.contains("import " + qualified + ";")) result.append("import ").append(qualified).append(";\n");
        });
        if (code.contains("@Test")) result.append("import static org.junit.jupiter.api.Assertions.*;\nimport static org.mockito.Mockito.*;\n");
        return result.toString();
    }
    public static String codeHint(String solution) {
        for (var type : new JavaSource(solution).types()) for (var member : type.getMembers()) {
            if (member instanceof MethodTree method && method.getReturnType() != null) {
                return "Bu, " + type.getSimpleName() + " içine eklenecek tek bir metot örneğidir; tam çözüm değildir.\n\n" + method;
            }
        }
        return "Önce sınıf/interface bildirimini ve gerekli alanları yaz; görevdeki kontrol listesini izle.";
    }

    public static void prepare(Project project) {
        var mapper = new ObjectMapper();
        project.getTasks().forEach(task -> task.getSteps().forEach(step -> {
            String code = step.getSolutionCode();
            step.setSolutionCode(importsFor(code) + "\n" + code);
            // Imports are setup, not the exercise: provide the same dependencies to the learner.
            step.setStarterCode(importsFor(step.getSolutionCode().replaceAll("(?m)^import .*;\\s*", ""))
                    + "\n" + step.getStarterCode().replaceAll("(?m)^import .*;\\s*", ""));
            try {
                var spec = mapper.readTree(step.getValidationSpec());
                var rules = (ArrayNode) spec.get("astRules");
                List<String> requirements = new ArrayList<>();
                for (var type : new JavaSource(code).types()) {
                    String name = type.getSimpleName().toString();
                    rules.addObject().put("id", "type-" + name).put("check", "classNamed('" + name + "')")
                            .put("onFail", name + " sınıfını/interface'ini tanımla.").put("onPass", name + " tanımlı.");
                    List<String> methods = new ArrayList<>();
                    for (var member : type.getMembers()) if (member instanceof MethodTree method && method.getReturnType() != null) {
                        String methodName = method.getName().toString();
                        methods.add(methodName + "()");
                        rules.addObject().put("id", name + "-" + methodName)
                                .put("check", "typeHasMethod('" + name + "','" + methodName + "')")
                                .put("onFail", name + "." + methodName + " metodunu yaz; sınıf metotlarının gövdesi boş olmamalı.")
                                .put("onPass", name + "." + methodName + " tanımlı.");
                    }
                    requirements.add(name + (methods.isEmpty() ? "" : ": " + String.join(", ", methods)));
                }
                step.setValidationSpec(mapper.writeValueAsString(spec));
                String criteria = "\n\nKontrol listesi:\n" + String.join("\n", requirements)
                        + "\nBu ekran Java 17 sözdizimini ve yapısal kuralları kontrol eder; çalışma zamanı davranışını test etmez."
                        + "\nBirden çok public tip varsa aynı editöre yazabilirsin; gerçek projede her tipi kendi .java dosyasına ayır."
                        + "\nDiğer görevlerdeki sınıflar projenin parçalarıdır. İleri adımlarda bunlara başvurabilirsin.";
                step.setInstruction(step.getInstruction() + criteria);
                if (step.getInstructionBeginner() != null) step.setInstructionBeginner(step.getInstructionBeginner() + criteria);
                if (step.getInstructionAdvanced() != null) step.setInstructionAdvanced(step.getInstructionAdvanced() + criteria);
            } catch (Exception error) { throw new IllegalStateException("Geçersiz ders: " + task.getTitle(), error); }
        }));
    }
}
