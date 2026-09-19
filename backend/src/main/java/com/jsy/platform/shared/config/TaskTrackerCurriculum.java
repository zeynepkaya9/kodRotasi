package com.jsy.platform.shared.config;

import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.taskengine.domain.*;

final class TaskTrackerCurriculum {
    static Project create() {
        Project project = new Project(ProjectCode.TASK_TRACKER, "Görev Takibi: OOP'den Spring'e",
                "Önce saf Java ile kapsülleme ve composition öğren; sonra aynı modeli constructor injection ve REST API ile kullan. Veritabanı gerekmez; görevler bellekte tutulur.", Level.BEGINNER);
        add(project, "TodoItem: Kapsülleme", "ENCAPSULATION,THIS_KEYWORD",
                "TodoItem oluştur. title private final String, completed private boolean olsun. Constructor null/boş başlığı reddetsin ve başlığı trim etsin. complete() görevi tamamlasın; getTitle() ve isCompleted() ile oku.",
                "public class TodoItem {\n    // Alanları, constructor ve davranışları yaz.\n}",
                """
                public class TodoItem {
                    private final String title;
                    private boolean completed;
                    public TodoItem(String title) {
                        if (title == null || title.isBlank()) throw new IllegalArgumentException("Başlık boş olamaz");
                        this.title = title.trim();
                    }
                    public String getTitle() { return title; }
                    public boolean isCompleted() { return completed; }
                    public void complete() { this.completed = true; }
                }
                """, "field('title').isPrivate()", "Bir görevin başlığına dışarıdan doğrudan erişilmesini nasıl engellersin?");
        add(project, "TaskBoard: Composition", "COMPOSITION,ENCAPSULATION",
                "TaskBoard içinde private final List<TodoItem> items tut ve ArrayList ile başlat. add(String title) yeni TodoItem oluşturup listeye eklesin ve döndürsün. getItems() List.copyOf ile değiştirilemeyen kopya dönsün. complete(int index) ilgili görevi tamamlasın.",
                "public class TaskBoard {\n    // TaskBoard, TodoItem nesnelerini içerir.\n}",
                """
                public class TaskBoard {
                    private final List<TodoItem> items = new ArrayList<>();
                    public TodoItem add(String title) {
                        TodoItem item = new TodoItem(title);
                        items.add(item);
                        return item;
                    }
                    public List<TodoItem> getItems() { return List.copyOf(items); }
                    public void complete(int index) { items.get(index).complete(); }
                }
                """, "field('items').isPrivate()", "Listeyi doğrudan döndürürsen dışarıdaki kod görevleri silebilir mi?");
        add(project, "TodoService: Spring Bean ve İş Mantığı", "DEPENDENCY_INJECTION",
                "@Service TodoService oluştur. Constructor'da yeni TaskBoard oluşturup private final board alanına ata. add(String), findAll() ve complete(int) metotları board'a işi devretsin. Bellekte paylaşılan listeye erişimi synchronized metotlarla sırala. Bu demo verileri uygulama kapanınca silinir.",
                "public class TodoService {\n    // Spring tarafından yönetilen servis.\n}",
                """
                @Service
                public class TodoService {
                    private final TaskBoard board;
                    public TodoService() { this.board = new TaskBoard(); }
                    public synchronized TodoItem add(String title) { return board.add(title); }
                    public synchronized List<TodoItem> findAll() { return board.getItems(); }
                    public synchronized void complete(int index) { board.complete(index); }
                }
                """, "classHasAnnotation('Service')", "@Service nesnenin yaşam döngüsünü hangi yapıya bırakır?");
        add(project, "TodoController: Constructor Injection ve REST", "DEPENDENCY_INJECTION,REST_BEST_PRACTICES",
                "@RestController ve @RequestMapping(\"/api/todos\") ekle. TodoService'i constructor ile al. GET list(), POST add(@RequestParam String title) ve POST /{index}/complete complete(@PathVariable int index) metotlarını yaz. Bu temel demoda index kimlik olarak kullanılır; kalıcı sistemde id tercih edilir.",
                "public class TodoController {\n    // Servisi constructor parametresiyle al.\n}",
                """
                @RestController
                @RequestMapping("/api/todos")
                public class TodoController {
                    private final TodoService service;
                    public TodoController(TodoService service) { this.service = service; }
                    @GetMapping
                    public List<TodoItem> list() { return service.findAll(); }
                    @PostMapping
                    public ResponseEntity<TodoItem> add(@RequestParam String title) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(title));
                    }
                    @PostMapping("/{index}/complete")
                    public ResponseEntity<Void> complete(@PathVariable int index) {
                        service.complete(index);
                        return ResponseEntity.noContent().build();
                    }
                }
                """, "hasConstructor()", "Controller kendi servisini new ile oluşturmak yerine constructor'dan alırsa test nasıl kolaylaşır?");
        return project;
    }
    private static void add(Project project, String title, String concepts, String instruction, String starter, String solution, String check, String hint) {
        Task task = new Task(project.getTasks().size(), title, instruction, concepts);
        Step step = new Step(0, instruction, starter, solution,
                "{\"astRules\":[{\"id\":\"core\",\"check\":\"" + check + "\",\"onFail\":\"" + hint + "\"}],\"conceptsToOffer\":[]}");
        task.addStep(step);
        project.addTask(task);
    }
}
