package com.jsy.platform.taskengine.application;

import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.taskengine.domain.Task;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

final class LearningNotesFactory {
    private static final Map<String, String[]> NOTES = new LinkedHashMap<>();

    static {
        note("ENTITY",
                "Entity, veritabanındaki bir satırı Java nesnesi olarak temsil eden sınıftır. @Entity sınıfı JPA'ya tanıtır; normal bir sınıfı otomatik olarak tabloya dönüştürmez, eşleme bilgisini verir.",
                "Entity'yi API yanıtı olarak doğrudan döndürmek yerine DTO kullanmayı düşün. İlişkiler ve lazy loading, JSON üretirken beklenmeyen sorgulara yol açabilir.",
                "Entity sınırlarını aggregate kurallarına göre tasarla. Persistence ayrıntılarının domain davranışını ele geçirmemesine dikkat et.");
        note("ID",
                "@Id, her kaydı diğerlerinden ayıran birincil anahtarı gösterir. Long id başlangıç için anlaşılır ve yaygın bir seçimdir.",
                "Teknik id ile email veya ISBN gibi iş anahtarlarını ayır. İş anahtarlarına ayrıca unique kısıtı gerekebilir.",
                "Dağıtık sistemlerde UUID/ULID ile veritabanı sequence seçeneklerinin indeks ve üretim maliyetlerini karşılaştır.");
        note("GENERATED_VALUE",
                "@GeneratedValue, yeni nesnenin id değerini uygulama yerine veritabanının üretmesini sağlar. Nesne kaydedilmeden önce id genellikle null olur.",
                "IDENTITY basittir; SEQUENCE toplu eklemelerde daha iyi performans seçenekleri sunabilir.",
                "Kimlik stratejisini veritabanı, batch insert ve servis sınırlarıyla birlikte değerlendir.");
        note("ENCAPSULATION",
                "Kapsülleme, alanları private tutup değişiklikleri metotlar üzerinden kontrol etmektir. Böylece geçersiz bir değer nesnenin içine kolayca giremez.",
                "Her alana otomatik setter yazmak yerine niyeti anlatan changeName, deposit veya complete gibi metotlar kullan.",
                "Invariant'ları aggregate içinde koru; nesneyi her ara durumda geçerli tutan küçük ve anlamlı davranışlar tasarla.");
        note("THIS_KEYWORD",
                "this, o anda üzerinde çalışılan nesneyi ifade eder. this.name = name ifadesinde soldaki alan, sağdaki constructor veya metot parametresidir.",
                "this kullanımı özellikle alan ve parametre aynı adı taşıdığında belirsizliği kaldırır.",
                "Constructor zincirlerinde this(...) ile aynı sınıftaki başka constructor çağrılabilir; okunabilirliği koru.");
        note("INTERFACE",
                "Interface bir sözleşmedir: hangi işlemlerin var olduğunu söyler, kullanan kodu uygulama ayrıntısından ayırır. Spring Data repository interface'inin gövdesini Spring çalışma anında üretir.",
                "Interface'i yalnızca 'her sınıfa interface' kuralıyla değil, değişebilen bir sınır veya test seam'i olduğunda kullan.",
                "Portları iş ihtiyacının dilinde tut; framework tiplerini domain sınırına sızdırmamaya çalış.");
        note("DEPENDENCY_INJECTION",
                "Dependency Injection, bir sınıfın ihtiyaç duyduğu nesneleri kendi içinde new ile üretmek yerine dışarıdan almasıdır. Constructor injection bağımlılıkları görünür ve test edilebilir yapar.",
                "Bağımlılıkları private final alanlarda tut. Constructor çok büyüyorsa sınıfın birden fazla sorumluluğu olabilir.",
                "Bağımlılık yönünü iş kurallarına doğru çevir; orchestration ile domain davranışını ayrı tut.");
        note("COMPOSITION",
                "Composition, bir nesnenin başka nesneleri içinde barındırarak davranış kurmasıdır. TaskBoard'un TodoItem listesi tutması buna örnektir: 'bir TaskBoard görevlerden oluşur'.",
                "Koleksiyonun kendisini dışarı vermek yerine değiştirilemeyen görünüm veya kopya döndür; kontrol sınıfta kalsın.",
                "Kalıtım yerine composition seçmek davranışları daha esnek birleştirir ve bağlanmayı azaltır.");
        note("JPA_RELATIONSHIPS",
                "JPA ilişkileri nesneler arasındaki bağı veritabanındaki foreign key veya ara tabloyla eşler. ManyToOne 'birçok kayıt tek kayda bağlı' anlamına gelir.",
                "İlişkinin owning side'ını ve fetch türünü bilinçli seç. İki yönlü ilişkilerde iki tarafı birlikte güncelleyen yardımcı metotlar kullan.",
                "N+1 sorgularını, cascade kapsamını ve aggregate sınırlarını gerçek erişim desenleriyle ölç.");
        note("TRANSACTION_MANAGEMENT",
                "Transaction, birden fazla veritabanı işlemini tek bir bütün yapar: hepsi başarılı olur veya hata halinde geri alınır. @Transactional genellikle service metodunda kullanılır.",
                "Transaction sınırını tek iş senaryosunu kapsayacak kadar geniş, gereksiz kilit tutmayacak kadar dar kur.",
                "Isolation, optimistic locking ve retry kararlarını eşzamanlı değişiklik senaryolarıyla birlikte ele al.");
        note("REST_BEST_PRACTICES",
                "REST endpoint'i HTTP isteğini uygulamadaki işleme bağlar. GET okur, POST yeni işlem/kaynak başlatır, uygun durum kodu sonucu anlatır.",
                "Controller'ı ince tut: girdiyi al, doğrula, service'i çağır ve açık bir response üret.",
                "Idempotency, hata sözleşmesi, pagination ve API sürümleme kararlarını istemci davranışıyla birlikte tasarla.");
        note("VALIDATION_PATTERNS",
                "Validation, hatalı veriyi iş mantığına ulaşmadan reddeder. @NotBlank metni; @Positive sayıyı kontrol eder. @Valid, DTO üzerindeki kuralları çalıştırır.",
                "DTO doğrulaması biçimi korur; domain doğrulaması iş kuralını korur. Kritik kuralları yalnız annotation'a bırakma.",
                "Cross-field ve zamana bağlı kuralları domain policy veya özel validator ile açıkça modelle.");
        note("EXCEPTION_HANDLING",
                "Exception, işlemin normal biçimde devam edemediğini anlatır. AccountNotFoundException gibi anlamlı bir ad, hatanın nedenini genel RuntimeException'dan daha iyi açıklar. @RestControllerAdvice hataları tek yerde HTTP yanıtına çevirir.",
                "Domain hatası ile teknik hatayı ayır. İstemciye tutarlı hata kodu ve mesaj döndürürken stack trace gibi iç ayrıntıları açma.",
                "Problem Details, hata kodu kataloğu, gözlemlenebilirlik ve retry edilebilir hata sınıflarını birlikte tasarla.");
        note("TESTING",
                "Test, kodun beklenen davranışını otomatik kontrol eden küçük bir örnektir. Arrange aşamasında veriyi hazırla, Act ile metodu çalıştır, Assert ile sonucu doğrula. Hem başarılı durumu hem hatalı girdiyi dene.",
                "Testi implementasyona değil gözlenen davranışa bağla. Mockito bağımlılığı kontrol eder; değer nesnelerinde çoğu zaman gerçek nesne daha anlaşılırdır.",
                "Contract, integration ve concurrency testlerini riskli sınırlara yerleştir; test piramidini bakım maliyetiyle dengele.");
    }

    private static void note(String code, String beginner, String intermediate, String advanced) {
        NOTES.put(code, new String[]{beginner, intermediate, advanced});
    }

    static String create(Task task, Level level) {
        int index = switch (level) {
            case BEGINNER -> 0;
            case INTERMEDIATE -> 1;
            case ADVANCED -> 2;
        };
        String heading = switch (level) {
            case BEGINNER -> "Sıfırdan başlayalım";
            case INTERMEDIATE -> "Neden böyle tasarlıyoruz?";
            case ADVANCED -> "Tasarım ve gerçek proje notu";
        };
        StringBuilder result = new StringBuilder(heading)
                .append("\nBu görevin amacı: ").append(task.getObjective()).append("\n\n");
        Arrays.stream((task.getConceptCodes() == null ? "" : task.getConceptCodes()).split(","))
                .map(String::trim)
                .filter(NOTES::containsKey)
                .distinct()
                .forEach(code -> result.append("• ").append(NOTES.get(code)[index]).append("\n\n"));
        if (level == Level.BEGINNER) {
            result.append("Nasıl ilerlemelisin?\n")
                    .append("1. Önce başlangıç kodundaki sınıfı ve yorumları oku.\n")
                    .append("2. Kontrol listesini tek tek uygula; hepsini aynı anda yazmaya çalışma.\n")
                    .append("3. Kırmızı geri bildirim hata değil, hangi parçanın eksik olduğunu gösteren öğrenme adımıdır.\n")
                    .append("4. Takılırsan önce küçük ipucunu aç; tam çözüme hemen geçme.");
        }
        return result.toString().trim();
    }

    private LearningNotesFactory() {
    }
}
