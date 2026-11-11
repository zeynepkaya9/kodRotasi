package com.jsy.platform.shared.config;

import com.jsy.platform.taskengine.domain.Project;
import com.jsy.platform.taskengine.domain.Step;
import com.jsy.platform.taskengine.domain.Task;

/** Append-only exercises: existing task positions and learner progress remain stable. */
public final class BankInterviewCurriculum {
    private BankInterviewCurriculum() {}

    public static void extend(Project bank) {
        bank.setDescription("Java ve Spring Boot temellerinden banka teknik mülakatına: para hassasiyeti, transaction, Redis önbelleği ve Kafka olaylarında tekrar işleme. Mülakat rotasında SQL, güvenlik ve sistem tasarımını da çalış.");
        String[] questions = {
            "Entity ile DTO neden ayrılır? private alan güvenlik yetkilendirmesi sağlar mı? Hayır: alan erişimini kapsüller; müşteri verisine erişim sunucuda ayrıca kontrol edilir.",
            "Para için neden BigDecimal? String ile oluştur; sayısal eşitlikte compareTo kullan. Para birimini ve izin verilen ondalık basamakları açıkça belirle.",
            "Hesap hareketlerinde N+1 nasıl oluşur? Fetch join veya projection seçimini açıkla; indeks ve sayfalama için gerçek sorgu planını ölç.",
            "Aynı hesaptan iki eşzamanlı çekim gelirse ne olur? @Transactional tek başına lost update sorununu çözmez. @Version veya veritabanı kilidi ve sınırlı retry stratejisi gerekir.",
            "İstemci transfer isteğini yeniden gönderirse? Kimliği doğrulanmış kullanıcıya bağlı idempotency key, istek özeti ve sonuç aynı transaction tasarımına dâhil edilmelidir.",
            "Bakiye yetersizliği ile veritabanı kesintisini nasıl ayırırsın? Domain hatasını açık sözleşmeyle dön; teknik ayrıntıları yanıta koyma, korelasyon kimliğiyle izle.",
            "@Valid yetkilendirme yapar mı? Hayır. DTO biçimini doğrular; kaynak hesabın giriş yapan kullanıcıya ait olduğu service katmanında doğrulanır.",
            "Mockito testi rollback ve kilit davranışını kanıtlar mı? Hayır. Gerçek PostgreSQL ile integration testi ve iki bağlantıyla eşzamanlı işlem testi gerekir."
        };
        for (Task task : bank.getTasks()) for (Step step : task.getSteps()) {
            String note = "\n\nMülakatta açıkla:\n" + questions[task.getOrderIndex()]
                    + "\nBu temel görev tam bir bankacılık sistemi değildir. Ayrıntılı senaryolar: menüde Banka Mülakatı.";
            step.setInstruction(step.getInstruction() + note);
            step.setInstructionBeginner((step.getInstructionBeginner() == null ? step.getInstruction() : step.getInstructionBeginner() + note));
            step.setInstructionAdvanced((step.getInstructionAdvanced() == null ? step.getInstruction() : step.getInstructionAdvanced() + note));
        }
        add(bank, 8, "Mülakat: güvenli transfer girdisi", "Para tutarını ve hesap kimliklerini doğrula", "VALIDATION_PATTERNS,ENCAPSULATION",
                "TransferCommand record'unu yaz: Long fromId, Long toId, BigDecimal amount. Compact constructor içinde null kimlikleri, pozitif olmayan kimlikleri, aynı hesabı ve null/pozitif olmayan tutarı reddet. amount değerini setScale(2, RoundingMode.UNNECESSARY) ile normalize et; kuruştan küçük kesri sessizce yuvarlama. Bu görev tek para birimi ve 2 ondalık basamak varsayar. Bakiye, hesap sahipliği, kur dönüşümü ve kilitleme ayrı service kurallarıdır.",
                "public record TransferCommand(Long fromId, Long toId, BigDecimal amount) {\n    // Doğrulayan compact constructor yaz.\n}",
                """
                import java.math.BigDecimal;
                import java.math.RoundingMode;
                public record TransferCommand(Long fromId, Long toId, BigDecimal amount) {
                    public TransferCommand {
                        if (fromId == null || toId == null || fromId <= 0 || toId <= 0 || fromId.equals(toId)) {
                            throw new IllegalArgumentException("Hesaplar farklı ve geçerli olmalı");
                        }
                        if (amount == null || amount.signum() <= 0) {
                            throw new IllegalArgumentException("Tutar pozitif olmalı");
                        }
                        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
                    }
                }
                """, "hasConstructor()", "usesMember('RoundingMode','UNNECESSARY')", "callsMethod('signum')", "callsMethod('setScale')");
        add(bank, 9, "Redis: şube bilgisi önbelleği", "Cache-aside akışını ve kesinti durumunu kodla", "REDIS_CACHE,DEPENDENCY_INJECTION",
                "BranchCache sınıfında StringRedisTemplate'i constructor ile al. get(String branchId, Supplier<String> database) yaz: boş kimliği reddet; branch:info: önekli anahtarı Redis'ten oku. Cache hit varsa database çağırma. Cache miss veya Redis DataAccessException durumunda database.get() çağır; null sonucu önbellekleme. Dolu sonucu Duration.ofMinutes(5) TTL ile kaydet. Redis yazma hatası okuma sonucunu engellemesin; database hatasını yutma. Bunu transfer bakiyesinde kullanma: eski şube saatleri tolere edilebilir, eski bakiye ile ödeme onayı verilemez. Redis erişim timeout'u ayrıca yapılandırılır. Şube güncellemesi sonrası cache invalidation ayrıca tasarlanmalıdır.",
                "public class BranchCache {\n    // Redis bağımlılığı ve get metodu\n}",
                """
                import org.springframework.data.redis.core.StringRedisTemplate;
                import org.springframework.dao.DataAccessException;
                import java.time.Duration;
                import java.util.function.Supplier;
                public class BranchCache {
                    private final StringRedisTemplate redis;
                    public BranchCache(StringRedisTemplate redis) { this.redis = redis; }
                    public String get(String branchId, Supplier<String> database) {
                        if (branchId == null || branchId.isBlank()) throw new IllegalArgumentException("Şube gerekli");
                        String key = "branch:info:" + branchId;
                        try {
                            String cached = redis.opsForValue().get(key);
                            if (cached != null) return cached;
                        } catch (DataAccessException unavailable) {
                            // Eğitim örneği: üretimde cache hata metriğini artır.
                        }
                        String value = database.get();
                        if (value != null) {
                            try { redis.opsForValue().set(key, value, Duration.ofMinutes(5)); }
                            catch (DataAccessException unavailable) { /* DB sonucu yine döner. */ }
                        }
                        return value;
                    }
                }
                """, "hasConstructor()", "callsMethod('ofMinutes')", "callsMethod('get')", "callsMethod('set')");
        add(bank, 10, "Kafka hazırlığı: tekrar gelen olay", "Consumer tekrar teslimini küçük bir Java modeliyle anla", "KAFKA_EVENTS,TESTING",
                "Bu görev Kafka broker'ı çalıştırmaz; consumer'ın çağıracağı davranışı bellekte modeller. TransferEvent(String eventId, String transferId) record'u boş/null kimlikleri reddetsin. NotificationProjector sınıfı Consumer<TransferEvent> sink alsın. synchronized onEvent metodu aynı eventId ve aynı içerik için false dönsün; aynı id/farklı içerikte IllegalArgumentException atsın. İlk olayda sink.accept çağır, yalnız başarıdan sonra Map'e kaydet ve true dön. Sink hata atarsa olayı kaydetme ki retry mümkün olsun.\n\nMülakat sınırı: Bu bellek içi Map restart sonrası silinir ve yalnız tek nesne üzerinde kilit sağlar. Sink yan etkiyi yaptıktan sonra süreç çökerse tekrar işlenebilir! Üretimde DB unique event_id ile inbox kaydı ve yerel iş etkisini aynı transaction'da yaz; offset'i commit sonrasında ilerlet. Dış bildirim sağlayıcısı ayrıca idempotency desteklemeli. Kafka exactly-once özelliği dış DB/HTTP etkisini kendiliğinden kapsamaz.",
                "public record TransferEvent(String eventId, String transferId) {\n    // Kimlikleri doğrula\n}\npublic class NotificationProjector {\n    // Constructor, Map ve onEvent metodu\n}",
                """
                import java.util.HashMap;
                import java.util.Map;
                import java.util.function.Consumer;
                public record TransferEvent(String eventId, String transferId) {
                    public TransferEvent {
                        if (eventId == null || eventId.isBlank() || transferId == null || transferId.isBlank()) {
                            throw new IllegalArgumentException("Olay ve transfer kimliği gerekli");
                        }
                    }
                }
                public class NotificationProjector {
                    private final Map<String, TransferEvent> processed = new HashMap<>();
                    private final Consumer<TransferEvent> sink;
                    public NotificationProjector(Consumer<TransferEvent> sink) { this.sink = sink; }
                    public synchronized boolean onEvent(TransferEvent event) {
                        TransferEvent previous = processed.get(event.eventId());
                        if (previous != null) {
                            if (!previous.equals(event)) throw new IllegalArgumentException("Olay kimliği çakıştı");
                            return false;
                        }
                        sink.accept(event);
                        processed.put(event.eventId(), event);
                        return true;
                    }
                }
                """, "hasConstructor()", "methodHasModifier('onEvent','synchronized')", "callsMethod('put')", "callsMethod('accept')");
        add(bank, 11, "Eşzamanlı çekim: optimistic güncelleme", "Çakışan iki çekimin aynı bakiyeyi harcamasını engelle", "TRANSACTION_MANAGEMENT,ENCAPSULATION",
                "OptimisticBalance sınıfını AtomicReference<AccountState> ile kur. AccountState(BigDecimal balance, long version) record'u null/negatif bakiyeyi ve negatif version'ı reddetsin. Constructor başlangıç bakiyesini alsın. snapshot() anlık durumu döndürsün. withdraw(BigDecimal amount) null, sıfır, negatif ve ikiden fazla ondalık basamağı reddetsin. Her denemede güncel state'i oku, yeterli bakiyeyi kontrol et; compareAndSet başarısızsa baştan oku. Başarılı işlem yeni balance ve version+1 döndürsün. İki eşzamanlı 80 TL çekimi, 100 TL başlangıç bakiyesinde en fazla bir kez başarılı olmalı.\n\nMülakatta sınırı açıkla: Bu AtomicReference yalnız aynı Java nesnesinin belleğini korur; çoklu uygulama instance'ı, restart veya DB için çözüm değildir. Üretimde JPA @Version/optimistic locking ya da koşullu SQL UPDATE kullan, conflict sonrası yeni transaction'da güncel veriyi okuyup tüm iş kurallarını tekrar değerlendir. Para hareketi kayıtlarını ve idempotency key'ini de transaction'a dâhil et.",
                "public record AccountState(BigDecimal balance, long version) {\n    // Geçerli durum kuralları\n}\npublic class OptimisticBalance {\n    // AtomicReference, constructor, snapshot ve withdraw\n}",
                """
                import java.math.BigDecimal;
                import java.math.RoundingMode;
                import java.util.concurrent.atomic.AtomicReference;
                public record AccountState(BigDecimal balance, long version) {
                    public AccountState {
                        if (balance == null || balance.signum() < 0 || version < 0) {
                            throw new IllegalArgumentException("Geçersiz hesap durumu");
                        }
                    }
                }
                public class OptimisticBalance {
                    private final AtomicReference<AccountState> state;
                    public OptimisticBalance(BigDecimal initial) {
                        if (initial == null) throw new IllegalArgumentException("Bakiye gerekli");
                        state = new AtomicReference<>(new AccountState(initial.setScale(2, RoundingMode.UNNECESSARY), 0));
                    }
                    public AccountState snapshot() { return state.get(); }
                    public AccountState withdraw(BigDecimal amount) {
                        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Tutar pozitif olmalı");
                        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
                        while (true) {
                            AccountState current = state.get();
                            if (current.balance().compareTo(amount) < 0) throw new IllegalArgumentException("Yetersiz bakiye");
                            AccountState next = new AccountState(current.balance().subtract(amount), current.version() + 1);
                            if (state.compareAndSet(current, next)) return next;
                        }
                    }
                }
                """, "hasConstructor()", "callsMethod('compareAndSet')", "callsMethod('compareTo')", "callsMethod('setScale')");
    }

    private static void add(Project bank, int order, String title, String objective, String concepts,
                            String instruction, String starter, String solution, String... checks) {
        Task task = new Task(order, title, objective, concepts);
        var rules = new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        var array = rules.putArray("astRules");
        for (int i = 0; i < checks.length; i++) array.addObject().put("id", "interview-" + i)
                .put("check", checks[i]).put("onPass", "Yapısal kontrol sağlandı.")
                .put("onFail", "Görev talimatındaki gereksinimi tamamla: " + checks[i]);
        rules.putArray("conceptsToOffer");
        Step step = new Step(0, instruction, starter, solution, rules.toString());
        step.setInstructionBeginner("Önce kullanılan tipleri tanı: record değişmez veri taşır; Supplier sonuç üretir, Consumer girdiyi işler.\n\n" + instruction);
        step.setInstructionAdvanced(instruction + "\n\nBaşarı, kesinti, tekrar ve eşzamanlılık durumlarını ayrı ayrı test ederek tasarım sınırlarını açıkla.");
        task.addStep(step);
        bank.addTask(task);
    }
}
