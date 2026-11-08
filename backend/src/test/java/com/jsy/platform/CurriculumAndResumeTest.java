package com.jsy.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.platform.evaluation.application.JavaSource;
import com.jsy.platform.shared.config.DataSeeder;
import com.jsy.platform.taskengine.domain.*;
import com.jsy.platform.taskengine.infrastructure.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import javax.tools.*;
import java.nio.file.*;
import java.net.URLClassLoader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:curriculum;DB_CLOSE_DELAY=-1", "spring.jpa.show-sql=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class CurriculumAndResumeTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired ProjectRepository projects;
    @Autowired PlatformTransactionManager transactions;
    @Autowired DataSeeder seeder;
    @Autowired com.jsy.platform.taskengine.application.TaskEngineService taskEngine;
    @Autowired com.jsy.platform.taskengine.infrastructure.UserStepProgressRepository stepProgress;
    @TempDir Path temp;

    @Test void everyReferenceSolutionPassesRulesAndProjectsCompile() {
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            assertEquals(4, projects.count());
            for (Project project : projects.findAll()) {
                try {
                    Path output = Files.createDirectories(temp.resolve(project.getCode().name()));
                    List<Path> files = new ArrayList<>();
                    for (Task task : project.getTasks()) for (Step step : task.getSteps()) {
                        JavaSource source = new JavaSource(step.getSolutionCode());
                        assertTrue(source.errors().isEmpty(), task.getTitle() + ": " + source.errors());
                        for (var rule : json.readTree(step.getValidationSpec()).get("astRules"))
                            assertTrue(source.check(rule.get("check").asText()), task.getTitle() + ": " + rule.get("check"));
                        String imports = step.getSolutionCode().lines().filter(line -> line.startsWith("import ")).reduce("", (a, b) -> a + b + "\n");
                        for (var type : source.types()) {
                            Path file = output.resolve(type.getSimpleName() + ".java");
                            Files.writeString(file, imports + "\n" + source.typeSource(type));
                            files.add(file);
                        }
                        JavaSource starter = new JavaSource(step.getStarterCode());
                        boolean allPass = starter.errors().isEmpty();
                        for (var rule : json.readTree(step.getValidationSpec()).get("astRules")) allPass &= starter.check(rule.get("check").asText());
                        assertFalse(allPass, "Başlangıç kodu doğrudan geçmemeli: " + task.getTitle());
                    }
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                    try (var manager = compiler.getStandardFileManager(diagnostics, null, null)) {
                        boolean ok = compiler.getTask(null, manager, diagnostics,
                                List.of("-proc:none", "--release", "17", "-classpath", System.getProperty("java.class.path"), "-d", output.toString()), null,
                                manager.getJavaFileObjectsFromPaths(files)).call();
                        assertTrue(ok, project.getCode() + ": " + diagnostics.getDiagnostics());
                    }
                    checkReferenceBehavior(project.getCode(), output);
                } catch (Exception e) { throw new AssertionError(project.getCode().name(), e); }
            }
        });
    }

    private void checkReferenceBehavior(ProjectCode code, Path output) throws Exception {
        try (var loader = new URLClassLoader(new java.net.URL[]{output.toUri().toURL()}, getClass().getClassLoader())) {
            if (code == ProjectCode.BANK) {
                checkBankInterviewBehavior(loader);
                Class<?> test = loader.loadClass("AccountServiceTest");
                Object instance = test.getConstructor().newInstance();
                for (var method : test.getDeclaredMethods()) if (method.isAnnotationPresent(Test.class)) {
                    method.setAccessible(true); method.invoke(instance);
                }
            }
            if (code == ProjectCode.ECOMMERCE) {
                Class<?> product = loader.loadClass("Product");
                Object item = product.getConstructor(String.class, String.class, BigDecimal.class, int.class).newInstance("Kitap", "Java", BigDecimal.TEN, 5);
                product.getMethod("decreaseStock", int.class).invoke(item, 2);
                assertEquals(3, product.getMethod("getStockQuantity").invoke(item));
                for (int amount : new int[]{-1, 0, 4}) assertThrows(java.lang.reflect.InvocationTargetException.class,
                        () -> product.getMethod("decreaseStock", int.class).invoke(item, amount));
                assertEquals(3, product.getMethod("getStockQuantity").invoke(item));
            }
            if (code == ProjectCode.LIBRARY) {
                Class<?> book = loader.loadClass("Book");
                Object item = book.getConstructor().newInstance();
                book.getMethod("borrow").invoke(item);
                assertThrows(java.lang.reflect.InvocationTargetException.class, () -> book.getMethod("borrow").invoke(item));
                book.getMethod("returnBook").invoke(item);
                book.getMethod("borrow").invoke(item);
            }
            if (code == ProjectCode.TASK_TRACKER) {
                Class<?> item = loader.loadClass("TodoItem");
                assertThrows(java.lang.reflect.InvocationTargetException.class, () -> item.getConstructor(String.class).newInstance(" "));
                Class<?> board = loader.loadClass("TaskBoard");
                Object instance = board.getConstructor().newInstance();
                Object todo = board.getMethod("add", String.class).invoke(instance, " Java ");
                assertEquals("Java", item.getMethod("getTitle").invoke(todo));
                List<?> list = (List<?>) board.getMethod("getItems").invoke(instance);
                assertThrows(UnsupportedOperationException.class, list::clear);
                board.getMethod("complete", int.class).invoke(instance, 0);
                assertEquals(true, item.getMethod("isCompleted").invoke(todo));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkBankInterviewBehavior(ClassLoader loader) throws Exception {
        var command = loader.loadClass("TransferCommand").getConstructor(Long.class, Long.class, BigDecimal.class);
        Object valid = command.newInstance(1L, 2L, new BigDecimal("10.100"));
        assertEquals(new BigDecimal("10.10"), valid.getClass().getMethod("amount").invoke(valid));
        for (BigDecimal amount : Arrays.asList(null, BigDecimal.ZERO, new BigDecimal("-1"), new BigDecimal("1.001"))) {
            assertThrows(java.lang.reflect.InvocationTargetException.class, () -> command.newInstance(1L, 2L, amount));
        }
        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> command.newInstance(1L, 1L, BigDecimal.ONE));
        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> command.newInstance(null, 2L, BigDecimal.ONE));

        var redis = org.mockito.Mockito.mock(org.springframework.data.redis.core.StringRedisTemplate.class);
        org.springframework.data.redis.core.ValueOperations<String, String> values = org.mockito.Mockito.mock(org.springframework.data.redis.core.ValueOperations.class);
        org.mockito.Mockito.when(redis.opsForValue()).thenReturn(values);
        var cache = loader.loadClass("BranchCache").getConstructor(org.springframework.data.redis.core.StringRedisTemplate.class).newInstance(redis);
        var get = cache.getClass().getMethod("get", String.class, java.util.function.Supplier.class);
        java.util.concurrent.atomic.AtomicInteger dbCalls = new java.util.concurrent.atomic.AtomicInteger();
        java.util.function.Supplier<String> database = () -> { dbCalls.incrementAndGet(); return "09:00–17:00"; };
        org.mockito.Mockito.when(values.get("branch:info:1")).thenReturn("cached");
        assertEquals("cached", get.invoke(cache, "1", database));
        assertEquals(0, dbCalls.get());
        org.mockito.Mockito.when(values.get("branch:info:1")).thenReturn(null);
        assertEquals("09:00–17:00", get.invoke(cache, "1", database));
        org.mockito.Mockito.verify(values).set("branch:info:1", "09:00–17:00", java.time.Duration.ofMinutes(5));
        org.mockito.Mockito.when(values.get("branch:info:1")).thenThrow(new org.springframework.data.redis.RedisConnectionFailureException("offline"));
        org.mockito.Mockito.doThrow(new org.springframework.data.redis.RedisConnectionFailureException("offline"))
                .when(values).set(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(java.time.Duration.class));
        assertEquals("09:00–17:00", get.invoke(cache, "1", database));
        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> get.invoke(cache, "1", (java.util.function.Supplier<String>) () -> { throw new IllegalStateException("db down"); }));
        org.mockito.Mockito.clearInvocations(values);
        assertNull(get.invoke(cache, "1", (java.util.function.Supplier<String>) () -> null));
        org.mockito.Mockito.verify(values, org.mockito.Mockito.never()).set(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(java.time.Duration.class));

        var eventType = loader.loadClass("TransferEvent");
        var eventConstructor = eventType.getConstructor(String.class, String.class);
        Object event = eventConstructor.newInstance("evt-1", "transfer-1");
        var delivered = new java.util.concurrent.atomic.AtomicInteger();
        java.util.function.Consumer<Object> sink = e -> delivered.incrementAndGet();
        var projectorType = loader.loadClass("NotificationProjector");
        Object projector = projectorType.getConstructor(java.util.function.Consumer.class).newInstance(sink);
        var onEvent = projectorType.getMethod("onEvent", eventType);
        assertEquals(true, onEvent.invoke(projector, event));
        assertEquals(false, onEvent.invoke(projector, eventConstructor.newInstance("evt-1", "transfer-1")));
        assertEquals(1, delivered.get());
        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> onEvent.invoke(projector, eventConstructor.newInstance("evt-1", "different")));
        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> eventConstructor.newInstance("", "transfer-1"));
        var attempts = new java.util.concurrent.atomic.AtomicInteger();
        Object retryProjector = projectorType.getConstructor(java.util.function.Consumer.class).newInstance((java.util.function.Consumer<Object>) e -> {
            if (attempts.incrementAndGet() == 1) throw new IllegalStateException("temporary failure");
        });
        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> onEvent.invoke(retryProjector, event));
        assertEquals(true, onEvent.invoke(retryProjector, event));
        assertEquals(false, onEvent.invoke(retryProjector, event));
        assertEquals(2, attempts.get());
    }

    @Test void completedOldCurriculumUnlocksFirstNewStepWithoutLosingDrafts() {
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            Project bank = projects.findByCode(ProjectCode.BANK).orElseThrow();
            Long userId = 99999L;
            for (Task task : bank.getTasks()) if (task.getOrderIndex() < 8) for (Step step : task.getSteps()) {
                var progress = new UserStepProgress(userId, step.getId(), StepStatus.COMPLETED);
                progress.saveDraft("preserved draft");
                stepProgress.save(progress);
            }
            var result = taskEngine.getProjectTasks(userId, bank.getId());
            assertEquals(11, result.size());
            assertEquals(StepStatus.ACTIVE, result.get(8).steps().get(0).status());
            assertEquals(StepStatus.LOCKED, result.get(9).steps().get(0).status());
            assertEquals("preserved draft", result.get(0).steps().get(0).savedCode());
            assertEquals(StepStatus.ACTIVE, taskEngine.getProjectTasks(userId, bank.getId()).get(8).steps().get(0).status());
        });
    }

    @Test void loginRestoresDraftProgressAndCurriculumUpdateKeepsIds() throws Exception {
        String auth = mvc.perform(post("/api/v1/auth/register").contentType("application/json")
                .content("{\"email\":\"resume@example.com\",\"password\":\"password123\",\"displayName\":\"Resume\"}"))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        String token = json.readTree(auth).get("accessToken").asText();
        String tasks = mvc.perform(post("/api/v1/projects/TASK_TRACKER/start").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        long step = json.readTree(tasks).get(0).get("steps").get(0).get("id").asLong();
        assertTrue(json.readTree(tasks).get(0).get("steps").get(0).get("learningNotes").asText()
                .contains("Sıfırdan başlayalım"));
        for (int i = 0; i < 4; i++) {
            mvc.perform(post("/api/v1/steps/" + step + "/hint").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.remainingHints").value(3 - i));
        }
        mvc.perform(get("/api/v1/steps/" + step + "/hints").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.revealedHints.length()").value(4))
                .andExpect(jsonPath("$.remainingHints").value(0));
        mvc.perform(post("/api/v1/steps/" + step + "/hint").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.xpPenalty").value(0))
                .andExpect(jsonPath("$.remainingHints").value(0));
        String draft = "public class TodoItem { /* benim taslağım */ }";
        mvc.perform(put("/api/v1/steps/" + step + "/draft").header("Authorization", "Bearer " + token)
                .contentType("application/json").content(json.writeValueAsString(Map.of("code", draft)))).andExpect(status().isNoContent());
        seeder.run();
        String login = mvc.perform(post("/api/v1/auth/login").contentType("application/json")
                .content("{\"email\":\"resume@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String nextToken = json.readTree(login).get("accessToken").asText();
        mvc.perform(get("/api/v1/my-tasks").header("Authorization", "Bearer " + nextToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].steps[0].id").value(step))
                .andExpect(jsonPath("$[0].steps[0].savedCode").value(draft));
        mvc.perform(put("/api/v1/steps/" + step + "/draft").header("Authorization", "Bearer " + nextToken)
                .contentType("application/json").content("{\"code\":\"\"}")).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/my-tasks").header("Authorization", "Bearer " + nextToken))
                .andExpect(jsonPath("$[0].steps[0].savedCode").value(""));
        mvc.perform(get("/api/v1/progress").header("Authorization", "Bearer " + nextToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalXp").value(0));
        String solution = new TransactionTemplate(transactions).execute(status -> projects.findByCode(ProjectCode.TASK_TRACKER).orElseThrow()
                .getTasks().get(0).getSteps().get(0).getSolutionCode());
        mvc.perform(post("/api/v1/steps/" + step + "/evaluate").header("Authorization", "Bearer " + nextToken)
                .contentType("application/json").content(json.writeValueAsString(Map.of("code", solution))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.passed").value(true)).andExpect(jsonPath("$.xpEarned").value(12));
        String relogin = mvc.perform(post("/api/v1/auth/login").contentType("application/json")
                .content("{\"email\":\"resume@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String resumedToken = json.readTree(relogin).get("accessToken").asText();
        mvc.perform(get("/api/v1/my-tasks").header("Authorization", "Bearer " + resumedToken))
                .andExpect(jsonPath("$[0].steps[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$[0].steps[0].savedCode").value(solution))
                .andExpect(jsonPath("$[1].steps[0].status").value("ACTIVE"));
        mvc.perform(get("/api/v1/progress").header("Authorization", "Bearer " + resumedToken))
                .andExpect(jsonPath("$.totalXp").value(12));
    }

    @Test void commentsStringsCallsAndWrongAnnotationTargetsCannotPass() {
        JavaSource source = new JavaSource("class A { String text = \"@Entity\"; @Deprecated private Long other; private Long id; void run() { deposit(); } /* @Id */ }");
        assertFalse(source.check("classHasAnnotation('Entity')"));
        assertFalse(source.check("field('id').hasAnnotation('Deprecated')"));
        assertFalse(source.check("hasMethod('deposit')"));
        assertFalse(new JavaSource("class A { void deposit() {} }").check("hasMethod('deposit')"));
        assertFalse(new JavaSource("class A {").errors().isEmpty());
        assertTrue(new JavaSource("interface A { void deposit(); }").check("hasMethod('deposit')"));
    }
}
