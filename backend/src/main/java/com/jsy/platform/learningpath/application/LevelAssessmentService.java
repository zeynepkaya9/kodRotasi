package com.jsy.platform.learningpath.application;

import com.jsy.platform.learningpath.api.AssessmentQuestionDto;
import com.jsy.platform.learningpath.api.AssessmentResultDto;
import com.jsy.platform.learningpath.api.AssessmentSubmitRequest;
import com.jsy.platform.learningpath.domain.Architecture;
import com.jsy.platform.learningpath.domain.LearningPath;
import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.learningpath.infrastructure.LearningPathRepository;
import org.springframework.stereotype.Service;
import com.jsy.platform.shared.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LevelAssessmentService {

    private final LearningPathRepository learningPathRepository;

    private static final List<AssessmentQuestionDto> QUESTIONS = List.of(
            new AssessmentQuestionDto(0,
                    "Java'da 'private' erişim belirleyicisi ne işe yarar?",
                    List.of("Sınıfı herkese açar", "Sadece aynı sınıf içinden erişim sağlar",
                            "Sadece alt sınıflardan erişim sağlar", "Aynı paketteki sınıflardan erişim sağlar"),
                    1, 1, "OOP"),
            new AssessmentQuestionDto(1,
                    "'this' anahtar kelimesi ne anlama gelir?",
                    List.of("Üst sınıfı referans eder", "Statik metodu çağırır",
                            "Mevcut nesneyi (instance) referans eder", "Yeni bir nesne oluşturur"),
                    2, 1, "THIS_SUPER"),
            new AssessmentQuestionDto(2,
                    "'super()' ne zaman kullanılır?",
                    List.of("Aynı sınıfın başka constructor'ını çağırmak için",
                            "Üst sınıfın constructor'ını çağırmak için",
                            "Static metot çağırmak için", "Nesneyi silmek için"),
                    1, 1, "THIS_SUPER"),
            new AssessmentQuestionDto(3,
                    "Interface ile abstract class arasındaki temel fark nedir?",
                    List.of("Fark yoktur", "Interface çoklu kalıtımı destekler, abstract class desteklemez",
                            "Abstract class instance oluşturulabilir", "Interface'de constructor olabilir"),
                    1, 2, "INTERFACE_ABSTRACT"),
            new AssessmentQuestionDto(4,
                    "Spring'de @Autowired ne işe yarar?",
                    List.of("Veritabanı bağlantısı kurar", "Otomatik bağımlılık enjeksiyonu yapar",
                            "REST endpoint tanımlar", "Transaction başlatır"),
                    1, 2, "DI_BEAN"),
            new AssessmentQuestionDto(5,
                    "Spring Bean lifecycle'da hangi aşama yoktur?",
                    List.of("Instantiation", "Dependency Injection", "Compilation", "Destruction"),
                    2, 2, "DI_BEAN"),
            new AssessmentQuestionDto(6,
                    "@Entity annotation'ı ne işe yarar?",
                    List.of("REST controller tanımlar", "Sınıfı bir veritabanı tablosuna eşler",
                            "Security filtresi ekler", "Unit test belirtir"),
                    1, 2, "JPA"),
            new AssessmentQuestionDto(7,
                    "JPA'da @GeneratedValue(strategy = GenerationType.IDENTITY) ne yapar?",
                    List.of("Alanı nullable yapar", "Primary key'i veritabanının otomatik üretmesini sağlar",
                            "Foreign key tanımlar", "Index oluşturur"),
                    1, 2, "JPA"),
            new AssessmentQuestionDto(8,
                    "JWT token'da hangi bilgi bulunmaz?",
                    List.of("Kullanıcı kimliği", "Token süresi", "Veritabanı şifresi", "İmza"),
                    2, 3, "SECURITY"),
            new AssessmentQuestionDto(9,
                    "JUnit'te @BeforeEach ne işe yarar?",
                    List.of("Tüm testlerden sonra çalışır", "Her testten önce çalışır",
                            "Sadece ilk testten önce çalışır", "Test'i devre dışı bırakır"),
                    1, 3, "TEST"),
            new AssessmentQuestionDto(10,
                    "@Transactional annotation'ı ne sağlar?",
                    List.of("REST endpoint oluşturur", "Veritabanı işlemlerini atomik yapar (ya hepsi ya hiçbiri)",
                            "Sınıfı singleton yapar", "Cache aktif eder"),
                    1, 3, "TRANSACTION"),
            new AssessmentQuestionDto(11,
                    "Spring'de @RestController ile @Controller farkı nedir?",
                    List.of("Fark yoktur", "@RestController otomatik @ResponseBody ekler (JSON döner)",
                            "@Controller sadece GET isteklerini alır", "@RestController sadece POST alır"),
                    1, 3, "REST")
    );

    public LevelAssessmentService(LearningPathRepository learningPathRepository) {
        this.learningPathRepository = learningPathRepository;
    }

    public List<AssessmentQuestionDto> getQuestions() {
        return QUESTIONS.stream()
                .map(q -> new AssessmentQuestionDto(q.id(), q.question(), q.options(), -1, 0, q.topic()))
                .toList();
    }

    @Transactional
    public AssessmentResultDto submitAssessment(Long userId, AssessmentSubmitRequest request) {
        List<Integer> answers = request.answers();
        if (answers == null || answers.size() != QUESTIONS.size()) {
            throw new BusinessException("Seviye tespiti için tüm soruları cevaplamalısın.");
        }
        for (int i = 0; i < answers.size(); i++) {
            Integer answer = answers.get(i);
            if (answer == null || answer < 0 || answer >= QUESTIONS.get(i).options().size()) {
                throw new BusinessException("Geçersiz cevap: soru " + (i + 1));
            }
        }
        double weightedScore = 0;
        double totalWeight = 0;

        for (int i = 0; i < QUESTIONS.size(); i++) {
            AssessmentQuestionDto q = QUESTIONS.get(i);
            totalWeight += q.weight();
            if (answers.get(i) == q.correctIndex()) {
                weightedScore += q.weight();
            }
        }

        double score = totalWeight > 0 ? weightedScore / totalWeight : 0;

        Level level;
        if (score < 0.35) {
            level = Level.BEGINNER;
        } else if (score < 0.70) {
            level = Level.INTERMEDIATE;
        } else {
            level = Level.ADVANCED;
        }

        Architecture recommended = recommendArchitecture(level);
        String reasoning = getArchitectureReasoning(level, recommended);

        LearningPath path = learningPathRepository.findByUserId(userId)
                .orElse(new LearningPath(userId, level, recommended));
        path.setLevel(level);
        path.setRecommendedArchitecture(recommended);
        path.setChosenArchitecture(recommended);
        learningPathRepository.save(path);

        return new AssessmentResultDto(level, Math.round(score * 100.0) / 100.0, recommended, reasoning);
    }

    private Architecture recommendArchitecture(Level level) {
        return switch (level) {
            case BEGINNER -> Architecture.LAYERED;
            case INTERMEDIATE -> Architecture.MODULAR_MONOLITH;
            case ADVANCED -> Architecture.CLEAN;
        };
    }

    private String getArchitectureReasoning(Level level, Architecture arch) {
        return switch (level) {
            case BEGINNER -> """
                    Senin için en uygun mimari: Katmanlı Mimari (Layered Architecture).
                    
                    Controller → Service → Repository şeklinde net katmanlar kullanacaksın. \
                    Bu mimari sorumlulukları açıkça ayırır ve Spring Boot'un temel yapısını öğrenmeni sağlar. \
                    Her annotation'ı, her katmanın neden var olduğunu adım adım açıklayacağız. \
                    Önerilen projeler: Banka Uygulaması veya Kütüphane Yönetim Sistemi.""";
            case INTERMEDIATE -> """
                    Senin için en uygun mimari: Modüler Monolith.
                    
                    Temel kavramları biliyorsun, şimdi modül sınırları ve bağımlılık yönetimini öğrenme zamanı. \
                    Her modül kendi domain'ini barındırır; modüller arası iletişim tanımlı arayüzler üzerinden olur. \
                    Bu yaklaşım ileride Hexagonal veya Microservice mimariye geçiş için güçlü bir temel oluşturur. \
                    Önerilen proje: E-ticaret Sistemi.""";
            case ADVANCED -> """
                    Senin için en uygun mimari: Clean Architecture.
                    
                    İş mantığını (domain) framework ve altyapıdan tamamen bağımsız tutacaksın. \
                    Dependency Inversion prensibi ile bağımlılıklar dış katmanlardan iç katmanlara doğru yönelir. \
                    Use Case'ler, Entity'ler ve Port/Adapter pattern'leri ile gerçek dünya ölçeğinde \
                    test edilebilir ve sürdürülebilir sistemler kurmayı deneyimleyeceksin.""";
        };
    }
}
