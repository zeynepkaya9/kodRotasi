package com.jsy.platform.evaluation.application;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import javax.lang.model.element.Modifier;
import javax.tools.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/** Parses submissions only. Never compiles or executes learner code. */
public final class JavaSource {
    private final List<ClassTree> types = new ArrayList<>();
    private final List<AnnotationTree> annotations = new ArrayList<>();
    private final List<MethodInvocationTree> calls = new ArrayList<>();
    private final List<MemberSelectTree> selections = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    public JavaSource(String code) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("Java değerlendirmesi için JDK gereklidir.");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaFileObject source = new SimpleJavaFileObject(URI.create("string:///Submission.java"), JavaFileObject.Kind.SOURCE) {
            @Override public CharSequence getCharContent(boolean ignoreEncodingErrors) { return code; }
        };
        try (var files = compiler.getStandardFileManager(diagnostics, Locale.ROOT, null)) {
            var task = (JavacTask) compiler.getTask(null, files, diagnostics, List.of("-proc:none", "--release", "17"), null, List.of(source));
            for (CompilationUnitTree unit : task.parse()) {
                new TreeScanner<Void, Void>() {
                    @Override public Void visitClass(ClassTree tree, Void ignored) { types.add(tree); return super.visitClass(tree, ignored); }
                    @Override public Void visitAnnotation(AnnotationTree tree, Void ignored) { annotations.add(tree); return super.visitAnnotation(tree, ignored); }
                    @Override public Void visitMethodInvocation(MethodInvocationTree tree, Void ignored) { calls.add(tree); return super.visitMethodInvocation(tree, ignored); }
                    @Override public Void visitMemberSelect(MemberSelectTree tree, Void ignored) { selections.add(tree); return super.visitMemberSelect(tree, ignored); }
                }.scan(unit, null);
            }
        } catch (Exception e) { errors.add("Java kodu okunamadı."); }
        diagnostics.getDiagnostics().stream().filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
                .forEach(d -> errors.add("Satır " + d.getLineNumber() + ": " + d.getMessage(Locale.ROOT)));
        if (types.isEmpty()) errors.add("En az bir sınıf veya interface tanımlamalısın.");
    }
    public List<String> errors() { return errors; }
    public List<ClassTree> types() { return List.copyOf(types); }
    private static String simple(String name) { return name.substring(name.lastIndexOf('.') + 1); }
    private static boolean annotation(ModifiersTree modifiers, String name) {
        return modifiers.getAnnotations().stream().anyMatch(a -> simple(a.getAnnotationType().toString()).equals(name));
    }
    private static boolean usableMethod(MethodTree method, ClassTree owner) {
        return method.getBody() != null ? !method.getBody().getStatements().isEmpty()
                : owner.getKind() == Tree.Kind.INTERFACE || method.getModifiers().getFlags().contains(Modifier.ABSTRACT);
    }
    public boolean check(String check) {
        var matcher = Pattern.compile("'([^']*)'").matcher(check);
        List<String> args = new ArrayList<>();
        while (matcher.find()) args.add(matcher.group(1));
        String arg = args.isEmpty() ? "" : args.get(0);
        if (check.startsWith("classHasAnnotation(")) return types.stream().anyMatch(t -> annotation(t.getModifiers(), arg));
        if (check.startsWith("classNamed(")) return types.stream().anyMatch(t -> t.getSimpleName().contentEquals(arg));
        if (check.startsWith("field(")) {
            return types.stream().flatMap(t -> t.getMembers().stream()).filter(VariableTree.class::isInstance)
                    .map(VariableTree.class::cast).filter(f -> f.getName().contentEquals(arg))
                    .anyMatch(f -> check.endsWith(".isPrivate()") ? f.getModifiers().getFlags().contains(Modifier.PRIVATE)
                            : args.size() == 2 && annotation(f.getModifiers(), args.get(1)));
        }
        if (check.startsWith("hasMethod(")) return types.stream().anyMatch(t -> t.getMembers().stream()
                .filter(MethodTree.class::isInstance).map(MethodTree.class::cast)
                .anyMatch(m -> m.getName().contentEquals(arg) && usableMethod(m, t)));
        if (check.startsWith("typeHasMethod(") && args.size() == 2) return types.stream()
                .filter(t -> t.getSimpleName().contentEquals(arg)).anyMatch(t -> t.getMembers().stream()
                        .filter(MethodTree.class::isInstance).map(MethodTree.class::cast)
                        .anyMatch(m -> m.getName().contentEquals(args.get(1)) && usableMethod(m, t)));
        if (check.equals("hasConstructor()")) return types.stream().anyMatch(t -> t.getMembers().stream()
                .filter(MethodTree.class::isInstance).map(MethodTree.class::cast)
                .anyMatch(m -> m.getReturnType() == null && !m.getParameters().isEmpty() && usableMethod(m, t)));
        if (check.startsWith("classImplements(")) return types.stream().anyMatch(t -> t.getImplementsClause().stream()
                .anyMatch(i -> simple(i.toString()).equals(arg)));
        if (check.startsWith("codeContains(")) {
            if (arg.startsWith("@")) return annotations.stream().anyMatch(a -> simple(a.getAnnotationType().toString()).equals(arg.substring(1)));
            return switch (arg) {
                case "interface" -> types.stream().anyMatch(t -> t.getKind() == Tree.Kind.INTERFACE);
                case "JpaRepository" -> types.stream().filter(t -> t.getKind() == Tree.Kind.INTERFACE)
                        .anyMatch(t -> t.getImplementsClause().stream().anyMatch(i -> i.toString().matches("(?:[\\w.]+\\.)?JpaRepository<.+>")));
                case "extends RuntimeException" -> types.stream().anyMatch(t -> t.getExtendsClause() != null && simple(t.getExtendsClause().toString()).equals("RuntimeException"));
                case "this." -> selections.stream().anyMatch(m -> m.getExpression().toString().equals("this"));
                case "assert" -> calls.stream().anyMatch(c -> simple(c.getMethodSelect().toString()).startsWith("assert"));
                case "AccountRepository" -> types.stream().flatMap(t -> t.getMembers().stream()).filter(VariableTree.class::isInstance)
                        .map(VariableTree.class::cast).anyMatch(v -> simple(v.getType().toString()).equals("AccountRepository"));
                default -> false; // Unknown rules must never silently grant success.
            };
        }
        return false;
    }
}
