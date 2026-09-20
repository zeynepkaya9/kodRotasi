package com.jsy.platform.shared.config;

import com.jsy.platform.concept.domain.Concept;
import com.jsy.platform.concept.infrastructure.ConceptRepository;
import com.jsy.platform.hintengine.domain.Hint;
import com.jsy.platform.hintengine.domain.HintLevel;
import com.jsy.platform.hintengine.infrastructure.HintRepository;
import com.jsy.platform.learningpath.domain.Level;
import com.jsy.platform.quiz.domain.Quiz;
import com.jsy.platform.quiz.infrastructure.QuizRepository;
import com.jsy.platform.taskengine.domain.*;
import com.jsy.platform.taskengine.infrastructure.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class DataSeeder implements CommandLineRunner {

    private final java.util.Map<Long, String> referenceSolutions = new java.util.HashMap<>();
    private final ProjectRepository projectRepository;
    private final ConceptRepository conceptRepository;
    private final HintRepository hintRepository;
    private final QuizRepository quizRepository;

    public DataSeeder(ProjectRepository projectRepository,
                      ConceptRepository conceptRepository,
                      HintRepository hintRepository,
                      QuizRepository quizRepository) {
        this.projectRepository = projectRepository;
        this.conceptRepository = conceptRepository;
        this.hintRepository = hintRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) {
        if (conceptRepository.count() == 0) seedConcepts();
        seedBankProject();
        seedECommerceProject();
        seedLibraryProject();
        seedTaskTrackerProject();
    }

    // =====================================================================
    //  CONCEPTS
    // =====================================================================

    private void seedConcepts() {
        seedEntityConcept();
        seedEncapsulationConcept();
        seedThisKeywordConcept();
        seedSuperKeywordConcept();
        seedInterfaceConcept();
        seedIdConcept();
        seedGeneratedValueConcept();
        seedDependencyInjectionConcept();
        seedAbstractClassConcept();
        seedCompositionConcept();
        seedSpringBeanLifecycleConcept();
        seedJpaRelationshipsConcept();
        seedRestBestPracticesConcept();
        seedTransactionManagementConcept();
        seedValidationPatternsConcept();
    }

    private Concept seedEntityConcept() {
        Concept c = new Concept("ENTITY", "@Entity Annotation");
        c.setRealLifeAnalogy(
                "Bir Excel tablosu düşün: her sütun bir alan (ad, soyad, e-posta), her satır bir kayıt. " +
                "@Entity annotation'ı, bir Java sınıfını bu Excel tablosuna (veritabanı tablosuna) bağlayan etiket gibidir. " +
                "Sınıftan oluşturduğun her nesne tablodaki bir satıra karşılık gelir; alanlar ise sütunlara eşlenir.");
        c.setWhyExplanation(
                "JPA/Hibernate, hangi sınıfların veritabanı tablosuna dönüştürüleceğini bilmek zorundadır. " +
                "@Entity olmadan ORM motoru sınıfı tamamen görmezden gelir ve repository işlemleri hata fırlatır. " +
                "Bu annotation, Object-Relational Mapping'in başlangıç noktasıdır — olmadan köprü kurulamaz.");
        c.setWrongExample(
                "public class User {\n" +
                "    public Long id;\n" +
                "    public String name;\n" +
                "}\n" +
                "// @Entity yok → Hibernate bu sınıfı görmezden gelir\n" +
                "// userRepository.save(user) → 'Not a managed type' hatası!");
        c.setWrongExampleExplanation(
                "@Entity annotation'ı olmadan JPA bu sınıfı yönetmez. Repository üzerinden kaydetmeye çalıştığında " +
                "'Not a managed type: class User' hatası alırsın. Veritabanı tablosu da oluşturulmaz.");
        c.setRightExample(
                "@Entity\npublic class User {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                "    private Long id;\n    private String name;\n    private String email;\n\n" +
                "    public User() {}\n\n    public User(String name, String email) {\n" +
                "        this.name = name;\n        this.email = email;\n    }\n\n" +
                "    // getter/setter\n}");
        c.setRightExampleExplanation(
                "@Entity ile Hibernate bu sınıfı 'user' tablosuna eşler. @Id birincil anahtarı tanımlar. " +
                "Parametresiz constructor JPA için zorunludur (reflection ile nesne oluşturur). " +
                "Artık CRUD işlemleri, JPQL sorguları ve ilişki tanımları yapılabilir.");
        Concept saved = conceptRepository.save(c);
        seedEntityQuizzes(saved.getId());
        return saved;
    }

    private void seedEntityQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "Aşağıdakilerden hangisi @Entity annotation'ı için doğrudur?",
                "[\"Sadece abstract sınıflarda kullanılır\"," +
                "\"Sınıfı bir veritabanı tablosuna eşler\"," +
                "\"Sınıfın singleton olmasını sağlar\"," +
                "\"Sadece Spring Boot projelerinde çalışır\"]",
                1,
                "@Entity, JPA standardının bir parçasıdır ve bir Java sınıfını veritabanı tablosuna eşler. " +
                "Spring'e özel değildir; herhangi bir JPA uyumlu ortamda çalışır."));
        quizRepository.save(new Quiz(conceptId,
                "@Entity ile işaretlenmiş bir sınıfta parametresiz constructor neden gereklidir?",
                "[\"Java dili zorunlu kılar\"," +
                "\"JPA, reflection ile nesne oluşturmak için kullanır\"," +
                "\"Lombok bunu gerektirir\"," +
                "\"Spring Security için zorunludur\"]",
                1,
                "JPA provider'ı (Hibernate) veritabanından veri okurken reflection ile nesne oluşturur. " +
                "Bunun için parametresiz (no-arg) constructor'a ihtiyaç duyar."));
        quizRepository.save(new Quiz(conceptId,
                "Bir sınıfa @Entity eklemezsen ve JpaRepository ile kullanmaya çalışırsan ne olur?",
                "[\"Otomatik olarak tablo oluşturulur\"," +
                "\"Derleme hatası alırsın\"," +
                "\"'Not a managed type' runtime hatası alırsın\"," +
                "\"Sınıf otomatik olarak @Entity kabul edilir\"]",
                2,
                "JPA, @Entity olmayan sınıfları yönetmez. Runtime'da 'Not a managed type' " +
                "IllegalArgumentException fırlatılır."));
    }

    private Concept seedEncapsulationConcept() {
        Concept c = new Concept("ENCAPSULATION", "Kapsülleme (Encapsulation)");
        c.setRealLifeAnalogy(
                "Bir ATM makinesini düşün: para çekmek için ekrandaki butona basarsın ama ATM'nin içindeki " +
                "mekanizmayı — kasayı, sayıcıyı, güvenlik sensörlerini — göremez ve dokunamazsın. " +
                "Dışarıya sadece 'para çek', 'bakiye sorgula' gibi davranışlar açılmış, iç detaylar gizli. " +
                "private alanlar kasanın kapağı, getter/setter metotlar ise ATM ekranıdır.");
        c.setWhyExplanation(
                "Encapsulation olmadan herkes her alana doğrudan erişip değiştirebilir; bu da negatif bakiye, " +
                "boş isim gibi geçersiz durumlara yol açar. Setter metotlar içine doğrulama (validation) koyarak " +
                "iş kurallarını zorlayabilirsin. Kapsülleme ayrıca iç implementasyonu değiştirdiğinde dış kodun " +
                "etkilenmemesini sağlar — bu da bakımı kolaylaştırır.");
        c.setWrongExample(
                "public class Account {\n" +
                "    public double balance; // herkes değiştirebilir!\n" +
                "}\n\n" +
                "Account acc = new Account();\n" +
                "acc.balance = -1000; // geçersiz ama engellenemiyor\n" +
                "acc.balance = Double.NaN; // tamamen bozuk veri!");
        c.setWrongExampleExplanation(
                "balance alanı public olduğu için herhangi bir yerden doğrudan negatif veya anlamsız değer " +
                "atanabilir. Hiçbir iş kuralı (bakiye >= 0, tutar pozitif olmalı) zorlanamaz. " +
                "Debugging sırasında 'bakiyeyi kim değiştirdi?' sorusuna cevap bulmak da imkansızlaşır.");
        c.setRightExample(
                "public class Account {\n" +
                "    private double balance;\n\n" +
                "    public double getBalance() { return balance; }\n\n" +
                "    public void deposit(double amount) {\n" +
                "        if (amount <= 0) throw new IllegalArgumentException(\"Tutar pozitif olmalı\");\n" +
                "        this.balance += amount;\n" +
                "    }\n\n" +
                "    public void withdraw(double amount) {\n" +
                "        if (amount <= 0) throw new IllegalArgumentException(\"Tutar pozitif olmalı\");\n" +
                "        if (amount > this.balance) throw new IllegalArgumentException(\"Yetersiz bakiye\");\n" +
                "        this.balance -= amount;\n" +
                "    }\n}");
        c.setRightExampleExplanation(
                "balance private → doğrudan erişim yok. deposit() ve withdraw() metotları ile kontrollü erişim sağlanır. " +
                "İş kuralları (pozitif tutar, yeterli bakiye) metot içinde zorlanır. " +
                "Bakiyeyi değiştiren tek yer bu iki metottur — hata ayıklama çok kolaylaşır.");
        Concept saved = conceptRepository.save(c);
        seedEncapsulationQuizzes(saved.getId());
        return saved;
    }

    private void seedEncapsulationQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "Encapsulation'ın temel amacı nedir?",
                "[\"Kodun daha hızlı çalışmasını sağlamak\"," +
                "\"Veri gizliliği ve kontrollü erişim sağlamak\"," +
                "\"Sınıfların birden fazla miras almasını sağlamak\"," +
                "\"Kodun daha az satırda yazılmasını sağlamak\"]",
                1,
                "Encapsulation, alanları private yaparak doğrudan erişimi engeller ve getter/setter ile " +
                "kontrollü erişim sağlar. Böylece iş kuralları zorlanabilir ve veri bütünlüğü korunur."));
        quizRepository.save(new Quiz(conceptId,
                "Aşağıdaki kodda encapsulation ihlali olan satır hangisidir?\n" +
                "public class User {\n    public String name;\n    private int age;\n    public int getAge() { return age; }\n}",
                "[\"private int age satırı\"," +
                "\"public String name satırı\"," +
                "\"getAge() metodu\"," +
                "\"class tanımı\"]",
                1,
                "'public String name' satırı encapsulation ihlalidir. name alanı herhangi bir yerden " +
                "doğrudan okunabilir ve değiştirilebilir; doğrulama yapılamaz."));
        quizRepository.save(new Quiz(conceptId,
                "Setter metotlarının en büyük avantajı nedir?",
                "[\"Kodun daha uzun görünmesini sağlar\"," +
                "\"Alan adını gizler\"," +
                "\"Değer atanmadan önce doğrulama (validation) yapılabilir\"," +
                "\"Sadece convention olarak kullanılır, teknik avantajı yoktur\"]",
                2,
                "Setter içine if kontrolü, format doğrulama gibi iş kuralları yazabilirsin. " +
                "Örneğin setAge(-5) çağrılırsa exception fırlatarak geçersiz veriyi engellersin."));
    }

    private Concept seedThisKeywordConcept() {
        Concept c = new Concept("THIS_KEYWORD", "this Anahtar Kelimesi");
        c.setRealLifeAnalogy(
                "Bir toplantıda herkes 'ben' dediğinde kendini kasteder — hangi kişi söylüyorsa o kişiyi ifade eder. " +
                "Java'da 'this' de bir nesnenin 'ben' demesidir: 'benim name alanım', 'benim getId metodum' gibi. " +
                "Özellikle parametre adı ile alan adı aynı olduğunda 'hangisi benim?' karışıklığını 'this' çözer.");
        c.setWhyExplanation(
                "Constructor veya setter parametresi ile sınıf alanı aynı isimde olduğunda Java parametre adını " +
                "öncelikli kabul eder (variable shadowing). this.name = name yazmazsan parametre kendine atanır, " +
                "sınıfın alanı null kalır. Derleyici hata vermez ama mantık hatası oluşur — tespit etmesi çok zordur.");
        c.setWrongExample(
                "public class User {\n    private String name;\n\n" +
                "    public User(String name) {\n        name = name; // YANLIŞ: parametre kendine atanıyor\n    }\n\n" +
                "    public void setName(String name) {\n        name = name; // YANLIŞ: aynı sorun\n    }\n}");
        c.setWrongExampleExplanation(
                "'name = name' ifadesinde her iki 'name' de parametreyi ifade eder (shadowing). Sınıfın name alanı " +
                "null olarak kalır. Derleyici hata vermez, uygulama çalışır ama User'ın adı hep null olur.");
        c.setRightExample(
                "public class User {\n    private String name;\n\n" +
                "    public User(String name) {\n        this.name = name; // DOĞRU: sınıfın alanına atanıyor\n    }\n\n" +
                "    public void setName(String name) {\n        this.name = name; // DOĞRU\n    }\n}");
        c.setRightExampleExplanation(
                "this.name → nesnenin kendi alanı, name → parametre. Böylece parametre değeri doğru yere atanır. " +
                "this kullanımı Java'da convention'dır ve okunabilirliği artırır.");
        Concept saved = conceptRepository.save(c);
        seedThisKeywordQuizzes(saved.getId());
        return saved;
    }

    private void seedThisKeywordQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "Aşağıdaki kodda this.name = name; ifadesinde 'this.name' neyi temsil eder?",
                "[\"Metot parametresini\"," +
                "\"Yerel değişkeni\"," +
                "\"Nesnenin kendi alanını (instance variable)\"," +
                "\"Statik değişkeni\"]",
                2,
                "this.name, o anki nesnenin (instance) name alanını ifade eder. " +
                "Sağdaki 'name' ise metot parametresidir."));
        quizRepository.save(new Quiz(conceptId,
                "this anahtar kelimesi kullanılmazsa ve parametre adı alan adıyla aynıysa ne olur?",
                "[\"Derleme hatası verir\"," +
                "\"Parametre kendine atanır, alan null kalır\"," +
                "\"Java otomatik olarak this ekler\"," +
                "\"Alan parametreye atanır\"]",
                1,
                "Variable shadowing nedeniyle parametre öncelikli kabul edilir. name = name ifadesi " +
                "parametreyi kendine atar; sınıfın name alanı değişmez ve null olarak kalır."));
    }

    private Concept seedSuperKeywordConcept() {
        Concept c = new Concept("SUPER_KEYWORD", "super Anahtar Kelimesi");
        c.setRealLifeAnalogy(
                "Bir çocuk doğduğunda önce ebeveynin DNA'sı (üst sınıf) aktarılır, sonra çocuğun kendi özellikleri " +
                "eklenir. super() = 'önce ebeveynin kurulumunu yap' demektir. Tıpkı bir evin önce temelinin " +
                "(üst sınıf) atılması, sonra duvarların (alt sınıf) örülmesi gibi — temel olmadan duvar olmaz.");
        c.setWhyExplanation(
                "Java'da her alt sınıf nesnesi, üst sınıfın alanlarını ve davranışlarını içerir. Bu alanların " +
                "doğru başlatılması için üst sınıfın constructor'ı çağrılmalıdır. super() yazılmazsa Java onu " +
                "otomatik ekler (parametresiz versiyon). Ama üst sınıfın sadece parametreli constructor'ı varsa " +
                "super(...) açıkça yazılmalıdır — yoksa derleme hatası alırsın.");
        c.setWrongExample(
                "class Animal {\n    private String type;\n    Animal(String type) { this.type = type; }\n}\n\n" +
                "class Dog extends Animal {\n    Dog() { } // DERLEME HATASI: Animal'ın parametresiz constructor'ı yok\n}");
        c.setWrongExampleExplanation(
                "Animal sınıfında sadece parametreli constructor var. Dog, super() olmadan Animal'ı oluşturamaz. " +
                "Java implicit olarak super() ekler ama parametresiz versiyon yoktur → derleme hatası.");
        c.setRightExample(
                "class Dog extends Animal {\n    private String breed;\n\n" +
                "    Dog(String breed) {\n        super(\"Köpek\"); // önce Animal kurulumu\n" +
                "        this.breed = breed; // sonra Dog'un kendi alanı\n    }\n}");
        c.setRightExampleExplanation(
                "super(\"Köpek\") ile önce Animal'ın type alanı set edilir, sonra Dog'un breed alanı atanır. " +
                "super() çağrısı her zaman constructor'ın ilk satırında olmalıdır.");
        Concept saved = conceptRepository.save(c);
        seedSuperKeywordQuizzes(saved.getId());
        return saved;
    }

    private void seedSuperKeywordQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "super() çağrısı constructor'da nereye yazılmalıdır?",
                "[\"Son satıra\"," +
                "\"Herhangi bir yere\"," +
                "\"İlk satıra\"," +
                "\"return'den önce\"]",
                2,
                "Java kuralı gereği super() çağrısı constructor'ın ilk satırında olmalıdır. " +
                "Üst sınıf önce başlatılmalıdır ki alt sınıf güvenle kullanabilsin."));
        quizRepository.save(new Quiz(conceptId,
                "Üst sınıfın parametresiz constructor'ı varsa alt sınıfta super() yazmak zorunlu mudur?",
                "[\"Evet, her zaman zorunludur\"," +
                "\"Hayır, Java otomatik ekler\"," +
                "\"Sadece abstract sınıflarda zorunludur\"," +
                "\"Sadece Spring ile zorunludur\"]",
                1,
                "Üst sınıfın parametresiz constructor'ı varsa Java derleyici implicit olarak super() ekler. " +
                "Ancak okunabilirlik için açıkça yazmak iyi bir pratiktir."));
    }

    private Concept seedInterfaceConcept() {
        Concept c = new Concept("INTERFACE", "Interface (Arayüz)");
        c.setRealLifeAnalogy(
                "Bir priz düşün: prize takılan her cihaz (telefon şarjı, lamba, bilgisayar) aynı standart fişi kullanır. " +
                "Priz interface'dir — 'elektrik vereceğim ama nasıl kullanacağın sana kalmış' der. " +
                "Fişin şekli (metot imzaları) standarttır, ama içerideki mekanizma (implementasyon) her cihazda farklıdır.");
        c.setWhyExplanation(
                "Interface, sınıflar arası sözleşme tanımlar ve bağımlılığı somut sınıfa değil soyutlamaya yönlendirir. " +
                "Bu sayede test'te mock kullanılabilir, implementasyon değişse bile sözleşme aynı kalır. " +
                "Spring Data JPA repository'leri bunun en güzel örneğidir — sen sadece interface tanımlarsın, " +
                "Spring çalışma zamanında implementasyonu otomatik üretir.");
        c.setWrongExample(
                "interface UserService {\n    void saveUserToPostgresWithHibernate();\n    List<User> fetchAllUsersFromPostgresCache();\n}\n" +
                "// Metot adlarında implementasyon detayı sızmış!");
        c.setWrongExampleExplanation(
                "'ToPostgresWithHibernate' ve 'FromPostgresCache' implementasyon detaylarıdır. Yarın MongoDB'ye " +
                "geçersen interface değişmek zorunda kalır. Interface sadece NE yapılacağını tanımlamalıdır, NASIL değil.");
        c.setRightExample(
                "interface UserService {\n    User save(User user);\n    Optional<User> findById(Long id);\n" +
                "    List<User> findAll();\n    void deleteById(Long id);\n}\n" +
                "// Sadece NE yapılacağı tanımlı — implementasyon sızıntısı yok");
        c.setRightExampleExplanation(
                "save(), findById() gibi metotlar sadece davranışı tanımlar. Postgres, Mongo veya in-memory — " +
                "hepsi bu sözleşmeyi uygulayabilir. Loose coupling ve test edilebilirlik sağlanır.");
        Concept saved = conceptRepository.save(c);
        seedInterfaceQuizzes(saved.getId());
        return saved;
    }

    private void seedInterfaceQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "Interface'in sınıftan (class) en temel farkı nedir?",
                "[\"Interface daha hızlı çalışır\"," +
                "\"Interface sadece metot imzaları tanımlar, implementasyon içermez\"," +
                "\"Interface'ten nesne oluşturulabilir\"," +
                "\"Interface sadece private metotlar içerir\"]",
                1,
                "Interface temel olarak bir sözleşmedir — metot imzalarını tanımlar ama gövdelerini (implementasyon) " +
                "sınıflara bırakır. Java 8+ ile default metotlar eklenmiştir ama temel felsefe budur."));
        quizRepository.save(new Quiz(conceptId,
                "Spring Data JPA'da repository neden interface olarak tanımlanır?",
                "[\"Java dili zorunlu kılar\"," +
                "\"Class olarak tanımlamak mümkün değildir\"," +
                "\"Spring, çalışma zamanında proxy ile implementasyonu otomatik üretir\"," +
                "\"Performans için\"]",
                2,
                "Spring Data JPA, interface'den runtime'da proxy-based implementasyon oluşturur. Sen sadece " +
                "metot imzası yazarsın, Spring metot adından SQL üretir."));
    }

    private Concept seedIdConcept() {
        Concept c = new Concept("ID", "@Id Annotation");
        c.setRealLifeAnalogy(
                "TC kimlik numarası düşün: Türkiye'deki her vatandaşı benzersiz olarak tanımlar. İki kişinin " +
                "adı aynı olabilir ama TC numarası asla aynı olamaz. Veritabanında da her satırın benzersiz bir " +
                "kimliği (primary key) olmalıdır — @Id bu kimliği tanımlar.");
        c.setWhyExplanation(
                "İlişkisel veritabanlarında her tabloda birincil anahtar (primary key) zorunludur. @Id olmadan " +
                "JPA hangi alanın PK olduğunu bilemez ve 'No identifier specified for entity' hatası verir. " +
                "Ayrıca equals/hashCode, ilişki tanımları ve caching mekanizmaları id alanına bağımlıdır.");
        c.setWrongExample(
                "@Entity\npublic class User {\n    private Long id; // @Id yok → JPA hata verir\n    private String name;\n}");
        c.setWrongExampleExplanation(
                "@Id olmadan JPA 'No identifier specified for entity: User' hatası verir. " +
                "Uygulama başlatılamaz çünkü JPA her entity'de bir PK bekler.");
        c.setRightExample(
                "@Entity\npublic class User {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                "    private Long id;\n    private String name;\n}");
        c.setRightExampleExplanation(
                "@Id ile primary key tanımlanır. Long tipi büyük veri setleri için Integer'dan daha güvenlidir. " +
                "@GeneratedValue ile değer otomatik üretilir — sen id'yi kendin atamak zorunda kalmazsın.");
        Concept saved = conceptRepository.save(c);
        seedIdQuizzes(saved.getId());
        return saved;
    }

    private void seedIdQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "@Id annotation'ı hangi paketten gelir?",
                "[\"org.springframework.data\"," +
                "\"jakarta.persistence\"," +
                "\"java.util\"," +
                "\"lombok\"]",
                1,
                "@Id, JPA standardının bir parçasıdır ve jakarta.persistence paketinden gelir. " +
                "Spring'e özel bir annotation değildir."));
        quizRepository.save(new Quiz(conceptId,
                "Bir entity'de @Id olmadan ne olur?",
                "[\"Hibernate id'yi otomatik bulur\"," +
                "\"İlk alan otomatik id kabul edilir\"," +
                "\"'No identifier specified' hatası alınır\"," +
                "\"Uygulama çalışır ama veri kaybolur\"]",
                2,
                "JPA her entity'de açıkça @Id ile işaretlenmiş bir alan bekler. Yoksa uygulama " +
                "başlatılırken 'No identifier specified for entity' hatası fırlatılır."));
    }

    private Concept seedGeneratedValueConcept() {
        Concept c = new Concept("GENERATED_VALUE", "@GeneratedValue Annotation");
        c.setRealLifeAnalogy(
                "Bir banka sıra numarası makinesi düşün: butona basarsın, makine sırayla numara verir — 1, 2, 3... " +
                "Sen numarayı seçmezsin, sistem otomatik ve benzersiz olarak üretir. @GeneratedValue da aynı " +
                "mantıkla çalışır: veritabanı her yeni kayıt için otomatik artan bir id üretir.");
        c.setWhyExplanation(
                "Elle id atamak, çakışma (duplicate key) riskini artırır — özellikle eşzamanlı isteklerde. " +
                "@GeneratedValue ile bu sorumluluğu veritabanına devredersin; güvenli, atomik ve otomatiktir. " +
                "IDENTITY stratejisi basit projelerde, SEQUENCE stratejisi yüksek performanslı batch operasyonlarda tercih edilir.");
        c.setWrongExample(
                "User user1 = new User();\nuser1.setId(42L); // elle id atama\n\n" +
                "User user2 = new User();\nuser2.setId(42L); // aynı id → duplicate key hatası!");
        c.setWrongExampleExplanation(
                "Elle id vermek, mevcut bir kaydın id'si ile çakışabilir. Özellikle birden fazla sunucu " +
                "aynı anda kayıt oluşturuyorsa çakışma kaçınılmazdır.");
        c.setRightExample(
                "@Id\n@GeneratedValue(strategy = GenerationType.IDENTITY)\nprivate Long id;\n\n" +
                "// id'yi sen verme — veritabanı her INSERT'te otomatik atar\n" +
                "// User user = new User(\"Ali\", \"ali@mail.com\");\n" +
                "// userRepository.save(user); → id otomatik atanır");
        c.setRightExampleExplanation(
                "IDENTITY stratejisi ile her INSERT'te veritabanı otomatik artan bir id üretir. " +
                "Çakışma riski sıfırdır. save() çağrıldıktan sonra user.getId() ile atanan id'yi görebilirsin.");
        Concept saved = conceptRepository.save(c);
        seedGeneratedValueQuizzes(saved.getId());
        return saved;
    }

    private void seedGeneratedValueQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "GenerationType.IDENTITY stratejisi ne yapar?",
                "[\"UUID üretir\"," +
                "\"Veritabanının auto-increment mekanizmasını kullanır\"," +
                "\"Uygulama tarafında rastgele id üretir\"," +
                "\"Her tabloda aynı id'yi paylaşır\"]",
                1,
                "IDENTITY stratejisi, veritabanının auto-increment (MySQL), SERIAL (PostgreSQL) gibi " +
                "mekanizmalarını kullanarak her INSERT'te otomatik artan bir id üretir."));
        quizRepository.save(new Quiz(conceptId,
                "@GeneratedValue kullanırken id alanına setter ile değer atamak neden tehlikelidir?",
                "[\"Derleme hatası verir\"," +
                "\"Performansı düşürür\"," +
                "\"Veritabanının ürettiği id ile çakışabilir\"," +
                "\"Sadece test'te sorun yaratır\"]",
                2,
                "Veritabanı kendi ürettiği id'leri yönetir. Elle değer atamak sequence'ın düzenini bozabilir " +
                "ve duplicate key hatalarına yol açabilir."));
    }

    private Concept seedDependencyInjectionConcept() {
        Concept c = new Concept("DEPENDENCY_INJECTION", "Dependency Injection (Bağımlılık Enjeksiyonu)");
        c.setRealLifeAnalogy(
                "Bir restoran düşün: aşçı malzemeleri kendisi marketten almak yerine, tedarikçi her sabah taze " +
                "malzemeleri mutfağa getirir. Aşçı sadece yemek yapmaya odaklanır, malzeme temini onu ilgilendirmez. " +
                "DI da aynıdır — bağımlılıkları sen oluşturma (new yapma), Spring sana hazır olarak versin. " +
                "Böylece sınıf kendi işine odaklanır, bağımlılık yönetimini framework'e bırakır.");
        c.setWhyExplanation(
                "DI olmadan sınıflar birbirine sıkı bağlanır (tight coupling): test yazmak, implementasyonu " +
                "değiştirmek ve bakım yapmak çok zorlaşır. Constructor injection ile bağımlılıklar açıkça " +
                "tanımlanır, final olur (immutable), ve test'te kolayca mock'lanabilir. " +
                "Spring resmi olarak constructor injection'ı field injection'a tercih eder.");
        c.setWrongExample(
                "public class OrderService {\n" +
                "    private ProductRepository repo = new ProductRepositoryImpl();\n" +
                "    private EmailService email = new SmtpEmailService();\n" +
                "    // Doğrudan new → tight coupling, test edilemez\n}");
        c.setWrongExampleExplanation(
                "new ile doğrudan oluşturma: test'te mock koyamazsın, implementasyon değişirse tüm " +
                "sınıflar değişmek zorunda. SmtpEmailService yerine MockEmailService kullanmak imkansız.");
        c.setRightExample(
                "@Service\npublic class OrderService {\n\n" +
                "    private final ProductRepository productRepository;\n" +
                "    private final EmailService emailService;\n\n" +
                "    public OrderService(ProductRepository productRepository,\n" +
                "                        EmailService emailService) {\n" +
                "        this.productRepository = productRepository;\n" +
                "        this.emailService = emailService;\n    }\n}");
        c.setRightExampleExplanation(
                "Constructor injection ile Spring uygun implementasyonları otomatik enjekte eder. " +
                "final alanlar değiştirilemez (immutable). Test'te new OrderService(mockRepo, mockEmail) " +
                "ile kolayca test edilir.");
        Concept saved = conceptRepository.save(c);
        seedDependencyInjectionQuizzes(saved.getId());
        return saved;
    }

    private void seedDependencyInjectionQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "Constructor injection'da bağımlılıklar neden final olarak tanımlanır?",
                "[\"Java zorunlu kılar\"," +
                "\"Performans için\"," +
                "\"Immutability sağlamak ve sonradan değiştirilmesini engellemek için\"," +
                "\"Spring bunu gerektirir\"]",
                2,
                "final ile bağımlılık constructor'da bir kez atanır ve sonra değiştirilemez. Bu, nesnenin " +
                "yaşam döngüsü boyunca tutarlı kalmasını ve thread-safety'yi garanti eder."));
        quizRepository.save(new Quiz(conceptId,
                "Spring'de constructor injection, field injection'a neden tercih edilir?",
                "[\"Daha az kod yazılır\"," +
                "\"Immutability, test edilebilirlik ve bağımlılıkların açık tanımı sağlar\"," +
                "\"Daha hızlı çalışır\"," +
                "\"Spring 6'da field injection kaldırıldı\"]",
                1,
                "Constructor injection: 1) final ile immutability, 2) new ile test kolaylığı, " +
                "3) Bağımlılıkların açıkça görülmesi. Spring ekibi resmi olarak constructor injection'ı önerir."));
        quizRepository.save(new Quiz(conceptId,
                "@Autowired annotation'ı tek constructor'lı sınıflarda neden gerekli değildir?",
                "[\"Spring 4.3'ten itibaren tek constructor'ı otomatik algılar\"," +
                "\"Hiçbir zaman gerekli olmamıştır\"," +
                "\"Sadece @Service sınıflarında gerekli değildir\"," +
                "\"Lombok bunu otomatik ekler\"]",
                0,
                "Spring 4.3'ten itibaren sınıfta tek bir constructor varsa Spring bunu otomatik olarak " +
                "injection noktası kabul eder. @Autowired yazmana gerek kalmaz."));
    }

    private Concept seedAbstractClassConcept() {
        Concept c = new Concept("ABSTRACT_CLASS", "Abstract Sınıf (Soyut Sınıf)");
        c.setRealLifeAnalogy(
                "Bir araç fabrikasının 'Araç' şablonunu düşün: her aracın motoru olacak, kapıları olacak, " +
                "hareket edecek — bunlar kesin. Ama 'hareket et' davranışı araca göre farklıdır: araba yolda gider, " +
                "gemi denizde yüzer. Abstract sınıf bu şablondur — ortak özellikleri ve bazı sabit davranışları tanımlar, " +
                "ama bazı metotları alt sınıflara bırakır.");
        c.setWhyExplanation(
                "Abstract sınıf, interface ile concrete class arasında bir köprüdür. Hem ortak implementasyon " +
                "paylaşabilir (code reuse) hem de alt sınıfları belirli metotları uygulamaya zorlayabilir. " +
                "Template Method pattern'ın temelidir — algoritmanın iskeletini tanımlar, detayları alt sınıfa bırakır.");
        c.setWrongExample(
                "class Shape {\n    double area() { return 0; } // Her şekilde 0 döner — anlamsız!\n}\n\n" +
                "class Circle extends Shape {\n    // area()'yı override etmeyi unutabilir → hep 0 döner\n}");
        c.setWrongExampleExplanation(
                "area() concrete metot olduğu için override etmek zorunlu değil. Geliştirici unutursa " +
                "her şeklin alanı 0 döner — runtime'da yakalanması zor bir bug.");
        c.setRightExample(
                "abstract class Shape {\n    abstract double area(); // alt sınıf ZORUNDA\n\n" +
                "    String describe() {\n        return \"Alan: \" + area(); // ortak davranış\n    }\n}\n\n" +
                "class Circle extends Shape {\n    private double radius;\n\n" +
                "    @Override\n    double area() { return Math.PI * radius * radius; }\n}");
        c.setRightExampleExplanation(
                "area() abstract olduğu için Circle onu implement etmek zorundadır — yoksa derleme hatası. " +
                "describe() ise tüm şekillerde ortak kullanılan concrete metottur. Kod tekrarı önlenir.");
        Concept saved = conceptRepository.save(c);
        seedAbstractClassQuizzes(saved.getId());
        return saved;
    }

    private void seedAbstractClassQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "Abstract sınıftan doğrudan nesne oluşturulabilir mi?",
                "[\"Evet, new ile oluşturulabilir\"," +
                "\"Hayır, sadece alt sınıflarından oluşturulabilir\"," +
                "\"Sadece factory method ile oluşturulabilir\"," +
                "\"Spring ile oluşturulabilir\"]",
                1,
                "Abstract sınıflar tamamlanmamış (incomplete) oldukları için doğrudan new ile " +
                "oluşturulamazlar. Önce concrete bir alt sınıf abstract metotları implement etmelidir."));
        quizRepository.save(new Quiz(conceptId,
                "Abstract sınıf ile interface arasındaki en önemli fark nedir?",
                "[\"Abstract sınıf daha hızlıdır\"," +
                "\"Abstract sınıf state (alan) ve implementasyon içerebilir, interface temel olarak sözleşme tanımlar\"," +
                "\"Interface'ten birden fazla miras alınamaz\"," +
                "\"Aralarında fark yoktur\"]",
                1,
                "Abstract sınıf: state (instance fields) + concrete metotlar + abstract metotlar. " +
                "Interface: temelde metot imzaları (Java 8+ default metotlar hariç). " +
                "Bir sınıf sadece bir abstract sınıftan miras alabilir ama birden fazla interface implement edebilir."));
    }

    private Concept seedCompositionConcept() {
        Concept c = new Concept("COMPOSITION", "Composition (Bileşim)");
        c.setRealLifeAnalogy(
                "Bir bilgisayar düşün: anakart, RAM, CPU, ekran kartı gibi parçalardan oluşur. Bu parçalar birbirinden " +
                "bağımsız olarak değiştirilebilir — RAM'i yükseltebilir, ekran kartını değiştirebilirsin. " +
                "Miras (inheritance) ise 'sen bir bilgisayarsın' demek gibidir; composition 'sende bir CPU var' demektir. " +
                "has-a ilişkisi, is-a ilişkisinden genelde daha esnek ve tercih edilen yaklaşımdır.");
        c.setWhyExplanation(
                "'Favor composition over inheritance' — GoF'un altın kuralı. Miras ile sınıflar sıkı bağlanır " +
                "ve üst sınıf değiştiğinde tüm alt sınıflar etkilenir (fragile base class problem). " +
                "Composition ile bileşenler bağımsız olarak test edilir, değiştirilir ve yeniden kullanılır.");
        c.setWrongExample(
                "class EmailNotifier extends Logger {\n    // Logger'ın tüm metotlarını miras alır — gereksiz bağımlılık\n    // Logger değişirse EmailNotifier bozulabilir\n}");
        c.setWrongExampleExplanation(
                "EmailNotifier bir Logger DEĞİLDİR (is-a ilişkisi yok). Sadece log özelliğini KULLANMAK " +
                "istiyor. Miras yerine composition ile Logger'ı bir alan olarak tutmak daha doğrudur.");
        c.setRightExample(
                "class EmailNotifier {\n    private final Logger logger; // has-a ilişkisi\n    private final EmailSender sender;\n\n" +
                "    EmailNotifier(Logger logger, EmailSender sender) {\n" +
                "        this.logger = logger;\n        this.sender = sender;\n    }\n\n" +
                "    void notify(String message) {\n        sender.send(message);\n        logger.info(\"Email gönderildi: \" + message);\n    }\n}");
        c.setRightExampleExplanation(
                "Logger ve EmailSender bağımsız bileşenlerdir. Her biri ayrı ayrı test edilebilir, " +
                "değiştirilebilir. Logger implementasyonu değişse bile EmailNotifier etkilenmez.");
        Concept saved = conceptRepository.save(c);
        seedCompositionQuizzes(saved.getId());
        return saved;
    }

    private void seedCompositionQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "'Favor composition over inheritance' ne anlama gelir?",
                "[\"Miras hiçbir zaman kullanılmamalıdır\"," +
                "\"Mümkünse has-a ilişkisini is-a ilişkisine tercih et\"," +
                "\"Composition daha hızlıdır\"," +
                "\"Java 17'de miras kaldırıldı\"]",
                1,
                "GoF tasarım prensiplerinden biridir. Miras yerine composition kullanmak daha esnek, " +
                "test edilebilir ve bakımı kolay tasarımlar oluşturur. Miras yanlış değildir ama " +
                "gerçek is-a ilişkisi yoksa composition tercih edilmelidir."));
        quizRepository.save(new Quiz(conceptId,
                "Aşağıdakilerden hangisi composition örneğidir?",
                "[\"Dog extends Animal\"," +
                "\"Car has-a Engine (private final Engine engine)\"," +
                "\"Circle extends Shape\"," +
                "\"ArrayList extends AbstractList\"]",
                1,
                "Car has-a Engine: Araba bir motor İÇERİR — bu composition. " +
                "Diğerleri miras (inheritance) örnekleridir."));
    }

    private Concept seedSpringBeanLifecycleConcept() {
        Concept c = new Concept("SPRING_BEAN_LIFECYCLE", "Spring Bean Yaşam Döngüsü");
        c.setRealLifeAnalogy(
                "Bir çalışanın şirketteki yaşam döngüsünü düşün: 1) İşe alınır (bean oluşturulur), " +
                "2) Oryantasyon eğitimi alır (bağımlılıklar enjekte edilir), 3) İlk gün hazırlıkları yapılır " +
                "(@PostConstruct), 4) Çalışır (uygulama ayakta), 5) İşten ayrılırken devir-teslim yapar " +
                "(@PreDestroy). Spring bean'leri de aynı aşamalardan geçer.");
        c.setWhyExplanation(
                "Bean lifecycle'ı anlamak, kaynakların doğru yönetilmesi için kritiktir. @PostConstruct ile " +
                "bean hazır olduktan sonra yapılacak başlatma işlemleri (cache yükleme, bağlantı açma) tanımlanır. " +
                "@PreDestroy ile uygulama kapanırken kaynaklar (bağlantılar, dosyalar) temiz bir şekilde serbest bırakılır.");
        c.setWrongExample(
                "@Service\npublic class CacheService {\n    private Map<String, Object> cache;\n\n" +
                "    public CacheService() {\n" +
                "        cache = loadFromDatabase(); // HATA: DB bağlantısı henüz hazır olmayabilir\n    }\n}");
        c.setWrongExampleExplanation(
                "Constructor'da veritabanı işlemi yapmak tehlikelidir çünkü bağımlılıklar henüz enjekte " +
                "edilmemiş olabilir. DataSource bean'i hazır değilse NullPointerException alırsın.");
        c.setRightExample(
                "@Service\npublic class CacheService {\n    private Map<String, Object> cache;\n" +
                "    private final DataRepository repo;\n\n" +
                "    public CacheService(DataRepository repo) { this.repo = repo; }\n\n" +
                "    @PostConstruct\n    public void init() {\n" +
                "        cache = repo.findAll().stream()\n" +
                "            .collect(Collectors.toMap(Data::getKey, Data::getValue));\n    }\n\n" +
                "    @PreDestroy\n    public void cleanup() {\n        cache.clear();\n    }\n}");
        c.setRightExampleExplanation(
                "@PostConstruct, tüm bağımlılıklar enjekte edildikten SONRA çalışır — güvenli. " +
                "@PreDestroy, uygulama kapanırken kaynakları temizler. Lifecycle hook'ları bean'in " +
                "düzgün başlatılmasını ve sonlandırılmasını garanti eder.");
        Concept saved = conceptRepository.save(c);
        seedSpringBeanLifecycleQuizzes(saved.getId());
        return saved;
    }

    private void seedSpringBeanLifecycleQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "@PostConstruct ne zaman çalışır?",
                "[\"Constructor'dan önce\"," +
                "\"Bağımlılıklar enjekte edildikten sonra, bean kullanıma hazır olduktan sonra\"," +
                "\"Uygulama kapanırken\"," +
                "\"Her metot çağrısından önce\"]",
                1,
                "@PostConstruct sırası: 1) Constructor çalışır, 2) Bağımlılıklar enjekte edilir, " +
                "3) @PostConstruct metodu çağrılır. Bu noktada tüm bağımlılıklar hazırdır."));
        quizRepository.save(new Quiz(conceptId,
                "Spring bean'inin varsayılan scope'u nedir?",
                "[\"Prototype — her istekte yeni nesne\"," +
                "\"Request — her HTTP isteğinde yeni\"," +
                "\"Singleton — uygulama boyunca tek nesne\"," +
                "\"Session — her oturumda yeni\"]",
                2,
                "Spring bean'leri varsayılan olarak singleton'dır: IoC container'da her bean'den sadece bir " +
                "tane oluşturulur ve tüm enjeksiyonlarda aynı nesne paylaşılır."));
    }

    private Concept seedJpaRelationshipsConcept() {
        Concept c = new Concept("JPA_RELATIONSHIPS", "JPA İlişkileri (Relationships)");
        c.setRealLifeAnalogy(
                "Gerçek hayattaki ilişkileri düşün: Bir öğretmenin ÇOĞU öğrencisi vardır (OneToMany). " +
                "Bir öğrencinin BİR okulu vardır (ManyToOne). Bir kişinin BİR pasaportu vardır (OneToOne). " +
                "Birçok öğrenci birçok ders alabilir (ManyToMany). JPA ilişkileri, bu gerçek dünya ilişkilerini " +
                "veritabanı tablolarında annotation'larla modellemeni sağlar.");
        c.setWhyExplanation(
                "İlişkisel veritabanlarında tablolar foreign key ile bağlanır. JPA ilişki annotation'ları bu " +
                "bağlantıları Java nesneleri üzerinde tanımlayarak SQL JOIN yazmadan nesne grafı üzerinden " +
                "navigasyon sağlar. Lazy/Eager loading stratejileri ile performans kontrol edilir. " +
                "Cascade seçenekleri ile ebeveyn silindiğinde çocuk kayıtların ne olacağı belirlenir.");
        c.setWrongExample(
                "@Entity\npublic class Order {\n    @ManyToMany(fetch = FetchType.EAGER) // N+1 tuzağı!\n" +
                "    private List<Product> products;\n}\n" +
                "// 100 sipariş çekersen 100 ek sorgu çalışır (N+1 problemi)");
        c.setWrongExampleExplanation(
                "EAGER fetch ile ilişkili tüm veriler hemen yüklenir. Büyük veri setlerinde performans " +
                "felaketi yaratır. ManyToMany + EAGER = N+1 sorgu problemi. Lazy tercih edilmelidir.");
        c.setRightExample(
                "@Entity\npublic class Order {\n    @ManyToOne(fetch = FetchType.LAZY)\n" +
                "    @JoinColumn(name = \"customer_id\")\n    private Customer customer;\n\n" +
                "    @OneToMany(mappedBy = \"order\", cascade = CascadeType.ALL)\n" +
                "    private List<OrderItem> items = new ArrayList<>();\n}");
        c.setRightExampleExplanation(
                "LAZY fetch ile veriler ihtiyaç duyulduğunda yüklenir — performanslı. " +
                "CascadeType.ALL ile Order silindiğinde OrderItem'lar da silinir. " +
                "mappedBy ile ilişkinin sahibi belirtilir — gereksiz ara tablo oluşmaz.");
        Concept saved = conceptRepository.save(c);
        seedJpaRelationshipsQuizzes(saved.getId());
        return saved;
    }

    private void seedJpaRelationshipsQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "@ManyToOne ilişkisinde foreign key hangi tarafta tutulur?",
                "[\"@ManyToOne annotation'ının olduğu tabloda (Many tarafı)\"," +
                "\"One tarafında\"," +
                "\"Her iki tarafta\"," +
                "\"Ayrı bir ara tabloda\"]",
                0,
                "@ManyToOne'da foreign key her zaman Many tarafındadır. Örneğin bir Account " +
                "sınıfında @ManyToOne User ilişkisi varsa, account tablosunda user_id sütunu oluşur."));
        quizRepository.save(new Quiz(conceptId,
                "FetchType.LAZY ne anlama gelir?",
                "[\"Veri hiçbir zaman yüklenmez\"," +
                "\"Veri hemen yüklenir\"," +
                "\"Veri sadece erişildiğinde (ihtiyaç duyulduğunda) yüklenir\"," +
                "\"Veri arka planda asenkron yüklenir\"]",
                2,
                "LAZY loading ile ilişkili veri, getter metodu çağrılana kadar yüklenmez. " +
                "Bu, gereksiz veritabanı sorgularını önler ve performansı artırır."));
        quizRepository.save(new Quiz(conceptId,
                "mappedBy ne işe yarar?",
                "[\"Tablo adını belirler\"," +
                "\"İlişkinin sahibinin hangi tarafta olduğunu belirtir (ters tarafı tanımlar)\"," +
                "\"Alanı veritabanında zorunlu yapar\"," +
                "\"İndeks oluşturur\"]",
                1,
                "mappedBy, ilişkinin 'inverse' (ters) tarafını tanımlar ve foreign key'in karşı " +
                "tarafta yönetildiğini belirtir. Bu olmadan JPA gereksiz bir ara tablo oluşturur."));
    }

    private Concept seedRestBestPracticesConcept() {
        Concept c = new Concept("REST_BEST_PRACTICES", "REST API En İyi Pratikleri");
        c.setRealLifeAnalogy(
                "Bir restoran menüsünü düşün: yemekler kategorilere ayrılır (başlangıçlar, ana yemekler, tatlılar), " +
                "her yemeğin adı açıklayıcıdır ve fiyatı bellidir. Garson (API) siparişi alır, mutfağa iletir, " +
                "sonucu tabakta sunar. REST API de aynı mantıkla çalışır: kaynaklar (menu items) URL ile tanımlanır, " +
                "HTTP metotları (GET=menüye bak, POST=sipariş ver, DELETE=iptal et) işlemi belirtir.");
        c.setWhyExplanation(
                "REST standartlarına uymak API'nin öğrenilmesini, kullanılmasını ve bakımını kolaylaştırır. " +
                "Tutarlı URL yapısı (/api/products, /api/products/{id}), doğru HTTP metotları " +
                "(GET=oku, POST=oluştur, PUT=güncelle, DELETE=sil) ve anlamlı HTTP status kodları " +
                "(200=başarılı, 201=oluşturuldu, 404=bulunamadı) profesyonel bir API'nin temelidir.");
        c.setWrongExample(
                "@RestController\npublic class ProductController {\n\n" +
                "    @GetMapping(\"/getProduct\")\n    public Product get(Long id) { ... }\n\n" +
                "    @PostMapping(\"/deleteProduct\")\n    public void delete(Long id) { ... }\n\n" +
                "    @GetMapping(\"/getAllProductsFromDatabase\")\n    public List<Product> list() { ... }\n}");
        c.setWrongExampleExplanation(
                "URL'de fiil kullanılmamalı (getProduct, deleteProduct). Silme işlemi POST ile yapılmamalı. " +
                "URL'de implementasyon detayı (FromDatabase) olmamalı. REST resource-based çalışır.");
        c.setRightExample(
                "@RestController\n@RequestMapping(\"/api/products\")\npublic class ProductController {\n\n" +
                "    @GetMapping\n    public ResponseEntity<List<ProductDTO>> list() { ... }\n\n" +
                "    @GetMapping(\"/{id}\")\n    public ResponseEntity<ProductDTO> get(@PathVariable Long id) { ... }\n\n" +
                "    @PostMapping\n    public ResponseEntity<ProductDTO> create(@Valid @RequestBody CreateProductDTO dto) {\n" +
                "        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));\n    }\n\n" +
                "    @DeleteMapping(\"/{id}\")\n    public ResponseEntity<Void> delete(@PathVariable Long id) { ... }\n}");
        c.setRightExampleExplanation(
                "Resource-based URL: /api/products (isim, çoğul). HTTP metodu fiili belirtir. " +
                "ResponseEntity ile uygun status kodu (201 Created). DTO ile entity doğrudan dışarıya sızmaz. " +
                "@Valid ile input validation yapılır.");
        Concept saved = conceptRepository.save(c);
        seedRestBestPracticesQuizzes(saved.getId());
        return saved;
    }

    private void seedRestBestPracticesQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "REST API URL tasarımında hangisi doğrudur?",
                "[\"/api/getProducts\"," +
                "\"/api/products\"," +
                "\"/api/product/fetchAll\"," +
                "\"/api/productList\"]",
                1,
                "REST'te URL'ler kaynak (resource) isimlerini temsil eder, fiil (verb) içermez. " +
                "Çoğul isim kullanılır: /api/products. İşlem HTTP metodu ile belirtilir (GET, POST, PUT, DELETE)."));
        quizRepository.save(new Quiz(conceptId,
                "Yeni bir kaynak oluşturulduğunda hangi HTTP status kodu dönülmelidir?",
                "[\"200 OK\"," +
                "\"201 Created\"," +
                "\"204 No Content\"," +
                "\"301 Moved Permanently\"]",
                1,
                "201 Created, bir kaynağın başarıyla oluşturulduğunu belirtir. Genellikle response body'de " +
                "oluşturulan kaynağın detayları ve Location header'da URL'si döndürülür."));
        quizRepository.save(new Quiz(conceptId,
                "REST API'de entity yerine DTO kullanmanın sebebi nedir?",
                "[\"DTO daha hızlıdır\"," +
                "\"Entity'nin iç yapısını (hassas alanlar, ilişkiler) dışarıya sızdırmamak için\"," +
                "\"JPA entity'ler JSON'a dönüştürülemez\"," +
                "\"Spring zorunlu kılar\"]",
                1,
                "DTO ile entity arasına bir katman koyarsın: password gibi hassas alanlar sızmaz, " +
                "lazy-loaded ilişkiler sorun çıkarmaz, API sözleşmesi entity değişikliklerinden etkilenmez."));
    }

    private Concept seedTransactionManagementConcept() {
        Concept c = new Concept("TRANSACTION_MANAGEMENT", "Transaction Yönetimi");
        c.setRealLifeAnalogy(
                "Bir banka havalesi düşün: A hesabından 1000 TL çekilir ve B hesabına 1000 TL yatırılır. " +
                "Bu iki işlem YA İKİSİ BİRDEN başarılı olmalı YA DA ikisi birden geri alınmalıdır. " +
                "Para A'dan çekilip B'ye yatırılamazsa, çekilen para geri konmalıdır — aksi halde para kaybolur! " +
                "Transaction, bu 'ya hep ya hiç' garantisini sağlayan mekanizmadır.");
        c.setWhyExplanation(
                "ACID prensipleri (Atomicity, Consistency, Isolation, Durability) veri bütünlüğünün temelidir. " +
                "@Transactional annotation'ı ile bir metottaki tüm veritabanı işlemleri tek bir birim olarak " +
                "yönetilir: hata olursa tüm değişiklikler geri alınır (rollback). Özellikle birden fazla tablo " +
                "etkileyen işlemlerde transaction olmadan veri tutarsızlığı kaçınılmazdır.");
        c.setWrongExample(
                "@Service\npublic class TransferService {\n\n" +
                "    public void transfer(String from, String to, double amount) {\n" +
                "        accountRepo.withdraw(from, amount); // başarılı\n" +
                "        // HATA OLUŞURSA → para çekildi ama yatırılmadı!\n" +
                "        accountRepo.deposit(to, amount);\n    }\n}\n" +
                "// @Transactional yok → rollback yok → para kaybolur!");
        c.setWrongExampleExplanation(
                "@Transactional olmadan withdraw başarılı olur, deposit'te hata olursa para A hesabından " +
                "çekilmiş ama B hesabına yatırılmamıştır. Veri tutarsızlığı — para 'kaybolur'.");
        c.setRightExample(
                "@Service\npublic class TransferService {\n\n" +
                "    @Transactional\n" +
                "    public void transfer(String from, String to, double amount) {\n" +
                "        Account sender = accountRepo.findByNumber(from).orElseThrow();\n" +
                "        Account receiver = accountRepo.findByNumber(to).orElseThrow();\n\n" +
                "        sender.withdraw(amount);\n" +
                "        receiver.deposit(amount);\n\n" +
                "        accountRepo.save(sender);\n" +
                "        accountRepo.save(receiver);\n" +
                "    }\n}" +
                "\n// Herhangi bir hata → TÜM değişiklikler geri alınır");
        c.setRightExampleExplanation(
                "@Transactional ile tüm işlemler tek atomik birim olur. deposit'te hata olursa " +
                "withdraw da geri alınır (rollback). RuntimeException varsayılan olarak rollback tetikler. " +
                "Checked exception için rollbackFor parametresi kullanılır.");
        Concept saved = conceptRepository.save(c);
        seedTransactionManagementQuizzes(saved.getId());
        return saved;
    }

    private void seedTransactionManagementQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "@Transactional annotation'ı varsayılan olarak hangi exception'larda rollback yapar?",
                "[\"Tüm exception'larda\"," +
                "\"Sadece checked exception'larda\"," +
                "\"Sadece RuntimeException ve Error'larda (unchecked)\"," +
                "\"Hiçbir zaman otomatik rollback yapmaz\"]",
                2,
                "Spring @Transactional varsayılan olarak sadece unchecked exception'larda (RuntimeException " +
                "ve alt sınıfları, Error) rollback yapar. Checked exception'lar için " +
                "@Transactional(rollbackFor = Exception.class) yazılmalıdır."));
        quizRepository.save(new Quiz(conceptId,
                "ACID prensiplerinde 'A' (Atomicity) ne anlama gelir?",
                "[\"Asenkron işlem\"," +
                "\"Otomatik commit\"," +
                "\"İşlem ya tamamen başarılı olur ya da tamamen geri alınır\"," +
                "\"İşlem arka planda çalışır\"]",
                2,
                "Atomicity: Transaction içindeki tüm işlemler tek bir birimdir. Ya hepsi başarılı olur " +
                "(commit) ya da hepsi geri alınır (rollback). Yarım kalmış işlem olamaz."));
        quizRepository.save(new Quiz(conceptId,
                "@Transactional annotation'ı hangi katmana konulmalıdır?",
                "[\"Controller katmanına\"," +
                "\"Repository katmanına\"," +
                "\"Service katmanına\"," +
                "\"Entity sınıfına\"]",
                2,
                "Service katmanı iş mantığını barındırır ve genellikle birden fazla repository işlemini " +
                "koordine eder. Transaction boundary bu katmanda tanımlanmalıdır."));
    }

    private Concept seedValidationPatternsConcept() {
        Concept c = new Concept("VALIDATION_PATTERNS", "Validation (Doğrulama) Desenleri");
        c.setRealLifeAnalogy(
                "Bir havalimanı güvenlik kontrolü düşün: önce kimlik kontrolü (null check), sonra bagaj taraması " +
                "(format/içerik doğrulama), sonra biniş kartı kontrolü (iş kuralı doğrulama). Her katmanda farklı " +
                "kurallar uygulanır. API'lerde de aynı katmanlı doğrulama yaklaşımı kullanılır: DTO'da format, " +
                "service'te iş kuralı, entity'de invariant koruması.");
        c.setWhyExplanation(
                "Doğrulama yapılmadan gelen her veri potansiyel bir güvenlik açığı veya bug kaynağıdır. " +
                "Jakarta Bean Validation (@NotNull, @Size, @Email, @Positive) ile DTO seviyesinde declarative " +
                "doğrulama yapılır. Service'te iş kuralları (stok kontrolü, bakiye kontrolü) imperative olarak " +
                "eklenir. Defense-in-depth: birden fazla katmanda doğrulama güvenliği artırır.");
        c.setWrongExample(
                "@PostMapping\npublic Product create(@RequestBody Product product) {\n" +
                "    // Hiçbir doğrulama yok!\n" +
                "    return productRepo.save(product);\n}\n" +
                "// name: null, price: -99 → veritabanına bozuk veri girer");
        c.setWrongExampleExplanation(
                "Doğrulama yoksa kullanıcı name: null, price: -99, email: 'asdf' gönderebilir. " +
                "Bozuk veri veritabanına girer, tutarsızlık oluşur ve hata mesajları anlamsız olur.");
        c.setRightExample(
                "public class CreateProductDTO {\n\n" +
                "    @NotBlank(message = \"Ürün adı boş olamaz\")\n" +
                "    @Size(min = 2, max = 100, message = \"Ad 2-100 karakter arası olmalı\")\n" +
                "    private String name;\n\n" +
                "    @NotNull(message = \"Fiyat zorunludur\")\n" +
                "    @Positive(message = \"Fiyat pozitif olmalıdır\")\n" +
                "    private BigDecimal price;\n\n" +
                "    @Size(max = 500, message = \"Açıklama en fazla 500 karakter olabilir\")\n" +
                "    private String description;\n}\n\n" +
                "// Controller'da: @Valid @RequestBody CreateProductDTO dto");
        c.setRightExampleExplanation(
                "@NotBlank, @Size, @Positive ile DTO seviyesinde declarative doğrulama. @Valid ile " +
                "doğrulama aktif edilir. Hata durumunda 400 Bad Request ve anlamlı hata mesajları döner. " +
                "Böylece service katmanına sadece geçerli veri ulaşır.");
        Concept saved = conceptRepository.save(c);
        seedValidationPatternsQuizzes(saved.getId());
        return saved;
    }

    private void seedValidationPatternsQuizzes(Long conceptId) {
        quizRepository.save(new Quiz(conceptId,
                "@Valid annotation'ı ne işe yarar?",
                "[\"Alanları private yapar\"," +
                "\"DTO üzerindeki validation annotation'larını aktif eder\"," +
                "\"Entity'yi veritabanına kaydeder\"," +
                "\"JSON dönüşümünü sağlar\"]",
                1,
                "@Valid, metot parametresinde kullanılarak o nesne üzerindeki @NotNull, @Size, @Positive " +
                "gibi constraint'lerin çalıştırılmasını sağlar. @Valid olmadan annotation'lar görmezden gelinir."));
        quizRepository.save(new Quiz(conceptId,
                "@NotNull ile @NotBlank arasındaki fark nedir?",
                "[\"Fark yoktur\"," +
                "\"@NotNull sadece null'ı reddeder; @NotBlank hem null hem boş string hem sadece boşlukları reddeder\"," +
                "\"@NotBlank sayısal alanlarda kullanılır\"," +
                "\"@NotNull String alanlarda, @NotBlank Integer alanlarda kullanılır\"]",
                1,
                "@NotNull: null ise hata, \"\" (boş string) geçer. @NotBlank: null, \"\", \"   \" (sadece boşluk) " +
                "hepsinde hata verir. String alanlarında @NotBlank genelde daha uygun tercihdir."));
        quizRepository.save(new Quiz(conceptId,
                "Validation neden sadece DTO'da değil, entity'de de yapılmalıdır?",
                "[\"JPA zorunlu kılar\"," +
                "\"Defense-in-depth: DTO dışından gelen veri de (batch job, event) doğrulanmalı\"," +
                "\"Performans için\"," +
                "\"Sadece DTO'da yapılması yeterlidir\"]",
                1,
                "DTO validasyonu sadece API katmanını korur. Entity'ye servis, batch job veya event listener " +
                "üzerinden de veri gelebilir. Entity seviyesinde invariant koruması son savunma hattıdır."));
    }

    // =====================================================================
    //  BANK PROJECT (UNCHANGED)
    // =====================================================================

    private void seedBankProject() {
        Project bank = new Project(ProjectCode.BANK,
                "Banka Uygulaması",
                "Gerçek bir banka uygulaması geliştirerek Java ve Spring Boot'un temel kavramlarını öğren. " +
                        "Kullanıcı yönetimi, hesap işlemleri, para transferi ve güvenlik konularını kapsayan " +
                        "kapsamlı bir proje.",
                Level.BEGINNER);

        // === TASK 1: User Entity ===
        Task task1 = new Task(0, "Domain Modeli: User Entity",
                "Banka uygulamasının temel kullanıcı entity'sini oluştur",
                "ENTITY,ID,GENERATED_VALUE,ENCAPSULATION,THIS_KEYWORD");

        Step step1_1 = new Step(0,
                "User sınıfını bir JPA entity olarak oluştur. id (Long), name (String), email (String) alanları olsun.",
                "// User.java dosyasını oluştur\n// Gerekli alanlar: id, name, email\n\npublic class User {\n    // Alanları buraya ekle\n}",
                "@Entity\npublic class User {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n\n    private String name;\n\n    private String email;\n\n    public User() {}\n\n    public User(String name, String email) {\n        this.name = name;\n        this.email = email;\n    }\n\n    public Long getId() { return id; }\n\n    public String getName() { return name; }\n    public void setName(String name) { this.name = name; }\n\n    public String getEmail() { return email; }\n    public void setEmail(String email) { this.email = email; }\n}",
                """
                {"astRules":[
                    {"id":"has-entity","check":"classHasAnnotation('Entity')","onPass":"✅ @Entity doğru kullanılmış — bu sınıf artık bir veritabanı tablosuna eşlenir.","onFail":"❌ @Entity annotation eksik. JPA bu sınıfı tablo olarak tanıyamaz.","why":"@Entity, Hibernate'e 'bu POJO bir veritabanı tablosudur' der. Olmadan ORM eşlemesi yapılmaz.","alternative":"@Table(name=\\"users\\") ile tablo adını özelleştirebilirsin ama @Entity her durumda zorunlu.","realWorld":"Gerçek projede @Entity sadece domain sınıfına konur; DTO'lara ASLA konmaz."},
                    {"id":"has-id","check":"field('id').hasAnnotation('Id')","onPass":"✅ @Id ile primary key doğru tanımlandı.","onFail":"❌ id alanında @Id annotation'ı eksik. Primary key tanımlanmamış.","why":"Her veritabanı tablosunda birincil anahtar (PK) zorunludur. @Id bunu JPA'ya bildirir.","alternative":"Composite key için @EmbeddedId kullanılabilir ama basit senaryolarda @Id yeterli.","realWorld":"Genelde Long veya UUID tercih edilir. Integer taşma riski nedeniyle Long daha güvenli."},
                    {"id":"has-generated-value","check":"field('id').hasAnnotation('GeneratedValue')","onPass":"✅ @GeneratedValue ile id otomatik üretilecek.","onFail":"⚠️ @GeneratedValue eksik. id'yi elle mi atayacaksın?","why":"@GeneratedValue ile PK'yi veritabanı üretir. Elle id atamak duplicate key riskini artırır.","alternative":"IDENTITY (basit), SEQUENCE (yüksek performans), UUID (dağıtık sistem) stratejileri var.","realWorld":"Postgres'te IDENTITY veya SEQUENCE tercih edilir. SEQUENCE + allocationSize yüksek hacimde hızlıdır."},
                    {"id":"id-private","check":"field('id').isPrivate()","onPass":"✅ id alanı private — Encapsulation doğru uygulanmış.","onFail":"⚠️ id alanı private değil. Encapsulation ihlali!","why":"private → Kapsülleme. Alan doğrudan dışarıdan değiştirilemez, kontrol getter/setter'da kalır.","alternative":"public yapabilirsin ama validasyon, lazy-loading ve güvenlik bozulur.","realWorld":"Neredeyse tüm profesyonel ekipler alanları private tutar; erişim metotlarla sağlanır."},
                    {"id":"name-private","check":"field('name').isPrivate()","onPass":"✅ name alanı private.","onFail":"⚠️ name alanı private olmalı.","why":"Encapsulation: alanlar private, erişim getter/setter ile kontrollü yapılır.","alternative":"public yapsan bile çalışır ama OOP prensiplerini ihlal eder.","realWorld":"Setter içinde doğrulama (örn. boş string reddetme) yapabilirsin — private olmazsa bu fırsat kaybolur."},
                    {"id":"email-private","check":"field('email').isPrivate()","onPass":"✅ email alanı private.","onFail":"⚠️ email alanı private olmalı.","why":"Email gibi hassas veriler mutlaka kapsüllenmelidir.","alternative":"Doğrudan erişim güvenlik açığı yaratabilir.","realWorld":"Email alanına genelde @Column(unique=true) eklenir."},
                    {"id":"has-getter-name","check":"hasMethod('getName')","onPass":"✅ getName() getter metodu mevcut.","onFail":"⚠️ getName() getter metodu eksik.","why":"private alanı dışarıdan okumak için getter gerekir. Hibernate de getter kullanır.","alternative":"Lombok @Getter ile otomatik üretebilirsin ama başlangıçta elle yazmak öğreticidir.","realWorld":"Spring Data JPA, Jackson (JSON) ve diğer framework'ler getter'lara bağımlıdır."},
                    {"id":"has-setter-name","check":"hasMethod('setName')","onPass":"✅ setName() setter metodu mevcut.","onFail":"⚠️ setName() setter metodu eksik.","why":"private alanı dışarıdan değiştirmek için setter gerekir. İçine doğrulama eklenebilir.","alternative":"Immutable tasarımda setter yerine builder pattern kullanılır.","realWorld":"DDD'de setter yerine domain metotları (changeName gibi) tercih edilir."},
                    {"id":"has-this-keyword","check":"codeContains('this.')","onPass":"✅ 'this' anahtar kelimesi doğru kullanılmış.","onFail":"⚠️ Constructor veya setter'da 'this.' kullanımı bekleniyor.","why":"this.name = name → 'benim alanıma parametreyi ata'. this olmadan parametre kendine atanır (shadowing).","alternative":"Parametre adını farklı yapabilirsin (n gibi) ama this kullanmak daha okunabilir ve standarttır.","realWorld":"Hemen her Java projesinde constructor ve setter'larda this kullanılır."}
                ],"conceptsToOffer":["ENTITY","ID","GENERATED_VALUE","ENCAPSULATION","THIS_KEYWORD"]}""");

        step1_1.setInstructionBeginner("""
                🎯 User sınıfını bir JPA entity olarak oluştur.

                📋 Yapman gerekenler:
                1. Sınıfın üzerine @Entity annotation'ı ekle
                2. id alanı oluştur (Long tipinde, @Id ve @GeneratedValue ile)
                3. name alanı oluştur (String tipinde, private)
                4. email alanı oluştur (String tipinde, private)
                5. Getter ve setter metotlarını yaz
                6. Constructor'da this anahtar kelimesini kullan

                💡 Bilgi:
                • @Entity: Bu sınıfı veritabanı tablosuna bağlar
                • @Id: Birincil anahtarı (primary key) belirtir
                • @GeneratedValue: id'nin otomatik üretilmesini sağlar
                • private: Alanları dışarıdan doğrudan erişime kapatır (Encapsulation)
                • this.name = name: 'Benim alanıma parametre değerini ata' demektir
                • super(): Üst sınıfın constructor'ını çağırır (şimdilik gerekli değil)

                Her annotation ve her satırın ne işe yaradığını merak et — sistem sana açıklayacak!""");

        step1_1.setInstructionAdvanced(
                "Banka domain modeli: User entity'sini oluştur. id, name, email alanları, " +
                        "gerekli JPA annotation'ları ve encapsulation kurallarını uygula.");

        task1.addStep(step1_1);

        // === TASK 2: Account Entity ===
        Task task2 = new Task(1, "Domain Modeli: Account Entity",
                "Banka hesap entity'sini oluştur ve User ile ilişkilendir",
                "ENTITY,ENCAPSULATION");

        Step step2_1 = new Step(0,
                "Account sınıfını oluştur: id, accountNumber (String), balance (BigDecimal), User ilişkisi (@ManyToOne). " +
                        "Bakiyenin negatife düşmesini engelleyen bir deposit/withdraw metodu yaz.",
                "public class Account {\n    // Alanları ve metotları buraya ekle\n}",
                "@Entity\npublic class Account {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n\n    @Column(unique = true, nullable = false)\n    private String accountNumber;\n\n    private BigDecimal balance = BigDecimal.ZERO;\n\n    @ManyToOne(fetch = FetchType.LAZY)\n    @JoinColumn(name = \"user_id\", nullable = false)\n    private User user;\n\n    public Account() {}\n\n    public Account(String accountNumber, User user) {\n        this.accountNumber = accountNumber;\n        this.user = user;\n        this.balance = BigDecimal.ZERO;\n    }\n\n    public void deposit(BigDecimal amount) {\n        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException(\"Tutar pozitif olmalı\");\n        this.balance = this.balance.add(amount);\n    }\n\n    public void withdraw(BigDecimal amount) {\n        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException(\"Tutar pozitif olmalı\");\n        if (amount.compareTo(this.balance) > 0) throw new IllegalArgumentException(\"Yetersiz bakiye\");\n        this.balance = this.balance.subtract(amount);\n    }\n\n    public BigDecimal getBalance() { return balance; }\n    public Long getId() { return id; }\n    public String getAccountNumber() { return accountNumber; }\n}",
                """
                {"astRules":[
                    {"id":"has-entity","check":"classHasAnnotation('Entity')","onPass":"✅ @Entity doğru.","onFail":"❌ @Entity eksik.","why":"JPA entity olması için zorunlu.","alternative":"","realWorld":"Her domain sınıfı @Entity ile işaretlenir."},
                    {"id":"has-manytoone","check":"codeContains('@ManyToOne')","onPass":"✅ @ManyToOne ilişkisi doğru tanımlanmış.","onFail":"❌ User ile ilişki eksik. @ManyToOne kullan.","why":"Bir kullanıcının birden fazla hesabı olabilir → Many Account to One User.","alternative":"@OneToOne tek hesaplı senaryoda kullanılabilir.","realWorld":"Gerçek bankalarda bir müşterinin birden çok hesabı olur."},
                    {"id":"has-deposit","check":"hasMethod('deposit')","onPass":"✅ deposit metodu mevcut.","onFail":"❌ Para yatırma (deposit) metodu eksik.","why":"Domain-driven design: iş mantığı entity içinde olmalı.","alternative":"Serviste de yapılabilir ama domain anemic olur.","realWorld":"Rich domain model'de iş kuralları entity'dedir."},
                    {"id":"has-withdraw","check":"hasMethod('withdraw')","onPass":"✅ withdraw metodu mevcut.","onFail":"❌ Para çekme (withdraw) metodu eksik.","why":"Bakiye kontrolü entity seviyesinde yapılmalı — invariant korunur.","alternative":"","realWorld":"Domain invariant: bakiye negatife düşemez."},
                    {"id":"balance-private","check":"field('balance').isPrivate()","onPass":"✅ balance private — dışarıdan doğrudan değiştirilemez.","onFail":"⚠️ balance private olmalı! Dışarıdan doğrudan balance değiştirilebilirse iş kuralı korunamaz.","why":"Encapsulation: bakiye sadece deposit/withdraw ile değişmeli.","alternative":"","realWorld":"Finansal uygulamalarda veri bütünlüğü kritiktir."}
                ],"conceptsToOffer":["ENTITY","ENCAPSULATION"]}""");

        step2_1.setInstructionBeginner("""
                🎯 Account (Hesap) entity'sini oluştur.

                📋 Yapman gerekenler:
                1. @Entity annotation'ı ekle
                2. id, accountNumber, balance alanlarını oluştur
                3. User ile @ManyToOne ilişkisi kur (bir kullanıcının çok hesabı olabilir)
                4. deposit(BigDecimal amount) → para yatırma metodu
                5. withdraw(BigDecimal amount) → para çekme metodu (bakiye kontrolü!)
                6. balance alanı private olmalı — sadece metotlarla değişmeli

                💡 Önemli kavram: Domain Driven Design'da iş kuralları entity içinde yazılır.
                Bakiyenin negatife düşmemesi bir 'invariant'tır — bunu entity korumalı.""");

        task2.addStep(step2_1);

        // === TASK 3: Repository ===
        Task task3 = new Task(2, "Repository Katmanı",
                "Spring Data JPA repository'lerini oluştur",
                "INTERFACE,DEPENDENCY_INJECTION");

        Step step3_1 = new Step(0,
                "UserRepository ve AccountRepository interface'lerini oluştur. JpaRepository'den extend et.",
                "// UserRepository.java\n\npublic interface UserRepository {\n    // JpaRepository'den extend et\n}",
                "public interface UserRepository extends JpaRepository<User, Long> {\n    Optional<User> findByEmail(String email);\n}\n\npublic interface AccountRepository extends JpaRepository<Account, Long> {\n    List<Account> findByUserId(Long userId);\n    Optional<Account> findByAccountNumber(String accountNumber);\n}",
                """
                {"astRules":[
                    {"id":"extends-jpa","check":"codeContains('JpaRepository')","onPass":"✅ JpaRepository doğru extend edilmiş.","onFail":"❌ JpaRepository'den extend etmelisin. Spring Data JPA tüm CRUD metotlarını otomatik sağlar.","why":"JpaRepository extend edilince save, findById, findAll, delete gibi metotlar otomatik gelir. Tek satır SQL yazmana gerek kalmaz.","alternative":"CrudRepository daha minimal bir alternatif; JpaRepository ek metotlar (flush, batch) sunar.","realWorld":"Neredeyse tüm Spring projelerinde JpaRepository tercih edilir."},
                    {"id":"is-interface","check":"codeContains('interface')","onPass":"✅ Repository bir interface olarak tanımlanmış.","onFail":"❌ Repository bir interface olmalı, class değil!","why":"Spring Data, interface'den otomatik implementasyon üretir. Sen sadece metot imzası yazarsın.","alternative":"","realWorld":"Bu pattern proxy-based — Spring runtime'da implementasyonu dinamik olarak oluşturur."}
                ],"conceptsToOffer":["INTERFACE","DEPENDENCY_INJECTION"]}""");

        step3_1.setInstructionBeginner("""
                🎯 UserRepository ve AccountRepository interface'lerini oluştur.

                📋 Yapman gerekenler:
                1. UserRepository interface'ini oluştur
                2. JpaRepository<User, Long> extend et
                3. findByEmail(String email) metodu ekle
                4. AccountRepository için de aynısını yap
                5. findByUserId ve findByAccountNumber metotları ekle

                💡 Bilgi:
                • JpaRepository extend ettiğinde save, findById, findAll, delete gibi metotlar otomatik gelir
                • Sen sadece özel sorgu metotlarını tanımlarsın — Spring metot adından SQL üretir
                • findByEmail → SELECT * FROM user WHERE email = ? olur
                • Bu bir interface — class değil! Spring implementasyonu otomatik oluşturur""");

        task3.addStep(step3_1);

        // === TASK 4: Service ===
        Task task4 = new Task(3, "Service Katmanı: İş Mantığı",
                "AccountService ile para yatırma/çekme/transfer işlemleri",
                "DEPENDENCY_INJECTION,ENCAPSULATION");

        Step step4_1 = new Step(0,
                "AccountService sınıfını oluştur. Constructor injection ile repository'leri enjekte et. " +
                        "deposit, withdraw ve transfer metotlarını yaz.",
                "@Service\npublic class AccountService {\n    // Repository'leri constructor injection ile enjekte et\n    // deposit, withdraw, transfer metotlarını yaz\n}",
                "@Service\npublic class AccountService {\n\n    private final AccountRepository accountRepository;\n\n    public AccountService(AccountRepository accountRepository) {\n        this.accountRepository = accountRepository;\n    }\n\n    @Transactional\n    public void deposit(String accountNumber, BigDecimal amount) {\n        Account account = accountRepository.findByAccountNumber(accountNumber)\n            .orElseThrow(() -> new RuntimeException(\"Hesap bulunamadı\"));\n        account.deposit(amount);\n        accountRepository.save(account);\n    }\n\n    @Transactional\n    public void withdraw(String accountNumber, BigDecimal amount) {\n        Account account = accountRepository.findByAccountNumber(accountNumber)\n            .orElseThrow(() -> new RuntimeException(\"Hesap bulunamadı\"));\n        account.withdraw(amount);\n        accountRepository.save(account);\n    }\n\n    @Transactional\n    public void transfer(String from, String to, BigDecimal amount) {\n        withdraw(from, amount);\n        deposit(to, amount);\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-service","check":"classHasAnnotation('Service')","onPass":"✅ @Service annotation doğru.","onFail":"❌ @Service annotation eksik. Spring bu sınıfı bean olarak tanıyamaz.","why":"@Service, Spring'e 'bu bir iş mantığı katmanı bileşenidir' der. Bean olarak IoC container'a kaydedilir.","alternative":"@Component da çalışır ama @Service semantik olarak doğru katmanı belirtir.","realWorld":"Controller → Service → Repository katmanlı mimaride Service iş kurallarını barındırır."},
                    {"id":"constructor-injection","check":"codeContains('AccountRepository')","onPass":"✅ Repository bağımlılığı tanımlanmış.","onFail":"❌ AccountRepository bağımlılığı eksik.","why":"Service, veritabanı işlemleri için Repository'ye bağımlıdır. Constructor injection ile enjekte edilir.","alternative":"@Autowired ile field injection da çalışır ama constructor injection test edilebilirlik için tercih edilir.","realWorld":"Spring ekibi constructor injection'ı resmi olarak önerir."},
                    {"id":"has-deposit","check":"hasMethod('deposit')","onPass":"✅ deposit metodu var.","onFail":"❌ deposit metodu eksik.","why":"Para yatırma iş mantığı service katmanında koordine edilir.","alternative":"","realWorld":"Service transaction boundary olarak çalışır."},
                    {"id":"has-transactional","check":"codeContains('@Transactional')","onPass":"✅ @Transactional ile transaction yönetimi sağlanmış.","onFail":"⚠️ @Transactional eksik. Transfer işleminde hata olursa veri tutarsızlığı oluşabilir!","why":"Transfer: A'dan çek + B'ye yatır. Ortasında hata olursa ikisi de geri alınmalı (rollback). @Transactional bunu garanti eder.","alternative":"Programmatic transaction da mümkün ama declarative (@Transactional) daha temiz.","realWorld":"Finansal işlemlerde transaction yönetimi kritiktir — veri kaybı kabul edilemez."}
                ],"conceptsToOffer":["DEPENDENCY_INJECTION","ENCAPSULATION"]}""");

        step4_1.setInstructionBeginner("""
                🎯 AccountService sınıfını oluştur.

                📋 Yapman gerekenler:
                1. @Service annotation'ı ekle
                2. AccountRepository'yi constructor injection ile enjekte et
                3. deposit(String accountNumber, BigDecimal amount) metodu yaz
                4. withdraw(String accountNumber, BigDecimal amount) metodu yaz
                5. transfer(String from, String to, BigDecimal amount) metodu yaz
                6. @Transactional annotation'ını işlem metotlarına ekle

                💡 Bilgi:
                • Constructor Injection: new ile oluşturmak yerine Spring'in bağımlılığı vermesini bekle
                • @Transactional: İşlem ortasında hata olursa tüm değişiklikleri geri al
                • Transfer = withdraw + deposit. Ortasında hata olursa ikisi de geri alınmalı""");

        task4.addStep(step4_1);

        // === TASK 5: REST Controller ===
        Task task5 = new Task(4, "REST API: Controller Katmanı",
                "AccountController ile REST endpoint'leri oluştur",
                "DEPENDENCY_INJECTION");

        Step step5_1 = new Step(0,
                "AccountController sınıfını oluştur. Deposit ve withdraw endpoint'lerini yaz.",
                "@RestController\n@RequestMapping(\"/api/accounts\")\npublic class AccountController {\n    // Service'i enjekte et ve endpoint'leri yaz\n}",
                "@RestController\n@RequestMapping(\"/api/accounts\")\npublic class AccountController {\n\n    private final AccountService accountService;\n\n    public AccountController(AccountService accountService) {\n        this.accountService = accountService;\n    }\n\n    @PostMapping(\"/{accountNumber}/deposit\")\n    public ResponseEntity<String> deposit(@PathVariable String accountNumber,\n                                           @RequestParam BigDecimal amount) {\n        accountService.deposit(accountNumber, amount);\n        return ResponseEntity.ok(\"Para yatırıldı\");\n    }\n\n    @PostMapping(\"/{accountNumber}/withdraw\")\n    public ResponseEntity<String> withdraw(@PathVariable String accountNumber,\n                                            @RequestParam BigDecimal amount) {\n        accountService.withdraw(accountNumber, amount);\n        return ResponseEntity.ok(\"Para çekildi\");\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-rest-controller","check":"classHasAnnotation('RestController')","onPass":"✅ @RestController doğru.","onFail":"❌ @RestController annotation eksik.","why":"@RestController = @Controller + @ResponseBody. Her metot otomatik JSON döner.","alternative":"@Controller + @ResponseBody ayrı ayrı da kullanılabilir.","realWorld":"REST API'lerde @RestController standart tercih."},
                    {"id":"has-request-mapping","check":"codeContains('@RequestMapping')","onPass":"✅ Base path tanımlanmış.","onFail":"⚠️ @RequestMapping ile base path tanımla.","why":"Tüm endpoint'lerin ortak prefix'ini tanımlar. /api/accounts gibi.","alternative":"Her metotta tam yol yazabilirsin ama DRY ilkesine aykırı.","realWorld":"REST API tasarımında kaynak bazlı URL yapısı standarttır."},
                    {"id":"has-postmapping","check":"codeContains('@PostMapping')","onPass":"✅ POST endpoint tanımlanmış.","onFail":"❌ @PostMapping ile deposit/withdraw endpoint'leri tanımla.","why":"Para yatırma/çekme durum değiştiren (state-changing) işlemlerdir → POST kullanılır.","alternative":"PUT de kullanılabilir ama semantik olarak POST daha uygun.","realWorld":"REST'te: GET=oku, POST=oluştur/işlem, PUT=güncelle, DELETE=sil."}
                ],"conceptsToOffer":["DEPENDENCY_INJECTION"]}""");

        task5.addStep(step5_1);

        // === TASK 6: Exception Handling ===
        Task task6 = new Task(5, "Exception Handling",
                "Global exception handler ve özel exception'lar oluştur",
                "EXCEPTION_HANDLING");

        Step step6_1 = new Step(0,
                "AccountNotFoundException ve InsufficientBalanceException oluştur. " +
                        "@RestControllerAdvice ile global exception handler yaz.",
                "// Custom exception'lar ve GlobalExceptionHandler oluştur",
                """
                public class AccountNotFoundException extends RuntimeException {
                    public AccountNotFoundException(String number) { super("Hesap bulunamadı: " + number); }
                }
                public class InsufficientBalanceException extends RuntimeException {
                    public InsufficientBalanceException() { super("Yetersiz bakiye"); }
                }
                @RestControllerAdvice
                public class GlobalExceptionHandler {
                    @ExceptionHandler(AccountNotFoundException.class)
                    public ResponseEntity<Map<String, String>> handleNotFound(AccountNotFoundException ex) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
                    }
                    @ExceptionHandler(InsufficientBalanceException.class)
                    public ResponseEntity<Map<String, String>> handleBalance(InsufficientBalanceException ex) {
                        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
                    }
                }
                """,
                """
                {"astRules":[
                    {"id":"has-custom-exception","check":"codeContains('extends RuntimeException')","onPass":"✅ Özel exception sınıfı oluşturulmuş.","onFail":"❌ RuntimeException'dan türeyen özel exception oluştur.","why":"Genel Exception fırlatmak yerine anlamlı isimli exception'lar kullanmak, hata ayıklamayı kolaylaştırır.","alternative":"Checked exception (Exception) da kullanılabilir ama Spring ekosisteminde unchecked (RuntimeException) tercih edilir.","realWorld":"AccountNotFoundException, InsufficientBalanceException gibi domain-specific exception'lar okunabilirliği artırır."},
                    {"id":"has-controller-advice","check":"classHasAnnotation('RestControllerAdvice')","onPass":"✅ @RestControllerAdvice ile merkezi hata yönetimi.","onFail":"❌ @RestControllerAdvice eksik. Her controller'da ayrı ayrı try-catch yazmak yerine merkezi handler kullan.","why":"Tek bir yerde tüm exception'ları yakalarsın. DRY prensibi + tutarlı hata yanıtları.","alternative":"Her controller'da @ExceptionHandler da mümkün ama tekrar (duplication) oluşur.","realWorld":"Production'da tüm Spring projelerinde @RestControllerAdvice kullanılır."},
                    {"id":"has-exception-handler","check":"codeContains('@ExceptionHandler')","onPass":"✅ @ExceptionHandler metodu tanımlanmış.","onFail":"❌ @ExceptionHandler ile hangi exception'ın nasıl yanıtlanacağını belirle.","why":"Her exception tipine özel HTTP status kodu ve mesaj döndürebilirsin.","alternative":"","realWorld":"404 (not found), 400 (bad request), 500 (server error) gibi anlamlı HTTP kodları API kalitesini artırır."}
                ],"conceptsToOffer":["ENCAPSULATION"]}""");

        task6.addStep(step6_1);

        // === TASK 7: Validation ===
        Task task7 = new Task(6, "Validation (Doğrulama)",
                "DTO'lara Jakarta validation annotation'ları ekle",
                "VALIDATION_PATTERNS,ENCAPSULATION");

        Step step7_1 = new Step(0,
                "DepositRequest DTO'su oluştur. amount alanına @Positive, @NotNull validasyonları ekle. " +
                        "Bu adımda yalnızca DTO'yu yaz; API entegrasyonunda @Valid ile kullanılır.",
                "// DepositRequest DTO'su oluştur\npublic class DepositRequest {\n    // amount alanı ve validasyon\n}",
                "public class DepositRequest {\n    @NotNull(message = \"Tutar boş olamaz\")\n    @Positive(message = \"Tutar pozitif olmalıdır\")\n    private BigDecimal amount;\n\n    public BigDecimal getAmount() { return amount; }\n    public void setAmount(BigDecimal amount) { this.amount = amount; }\n}",
                """
                {"astRules":[
                    {"id":"has-notnull","check":"codeContains('@NotNull')","onPass":"✅ @NotNull doğrulama eklendi.","onFail":"❌ @NotNull eksik. Null değer kontrolü yapılmıyor.","why":"Null değer gönderilirse NullPointerException yerine anlamlı hata mesajı döner.","alternative":"Optional kullanabilirsin ama DTO validasyonunda annotation daha okunabilir.","realWorld":"API'ye gelen her girdi doğrulanmalıdır — güvenlik kuralı."},
                    {"id":"has-positive","check":"codeContains('@Positive')","onPass":"✅ @Positive ile negatif tutar engellendi.","onFail":"⚠️ @Positive ekle — negatif tutar kabul edilmemeli.","why":"Negatif tutar ile para yatırmak mantıksız. Validasyonu DTO seviyesinde yap.","alternative":"Serviste if kontrolü de olabilir ama DTO'da yapmak defense-in-depth sağlar.","realWorld":"Finansal uygulamalarda çift katmanlı validasyon (DTO + domain) standarttır."}
                ],"conceptsToOffer":["ENCAPSULATION"]}""");

        task7.addStep(step7_1);

        // === TASK 8: Test ===
        Task task8 = new Task(7, "Test Yazımı: JUnit",
                "AccountService için unit test yaz",
                "TESTING,DEPENDENCY_INJECTION");

        Step step8_1 = new Step(0,
                "AccountService'in deposit ve withdraw metotları için JUnit test'leri yaz.",
                "public class AccountServiceTest {\n    // Mockito ile repository oluştur, servisi doğrudan kur ve sonuçları doğrula.\n}",
                """
                public class AccountServiceTest {
                    @Test
                    void shouldDepositMoney() {
                        AccountRepository repository = mock(AccountRepository.class);
                        Account account = new Account("123", new User("Ada", "ada@example.com"));
                        when(repository.findByAccountNumber("123")).thenReturn(Optional.of(account));
                        new AccountService(repository).deposit("123", new BigDecimal("100.00"));
                        assertEquals(0, new BigDecimal("100.00").compareTo(account.getBalance()));
                        verify(repository).save(account);
                    }
                    @Test
                    void shouldRejectNegativeDeposit() {
                        AccountRepository repository = mock(AccountRepository.class);
                        Account account = new Account("123", new User("Ada", "ada@example.com"));
                        when(repository.findByAccountNumber("123")).thenReturn(Optional.of(account));
                        assertThrows(IllegalArgumentException.class,
                            () -> new AccountService(repository).deposit("123", new BigDecimal("-1")));
                        assertEquals(0, account.getBalance().signum());
                        verify(repository, never()).save(any());
                    }
                    @Test
                    void shouldRejectOverdraft() {
                        AccountRepository repository = mock(AccountRepository.class);
                        Account account = new Account("123", new User("Ada", "ada@example.com"));
                        when(repository.findByAccountNumber("123")).thenReturn(Optional.of(account));
                        assertThrows(IllegalArgumentException.class,
                            () -> new AccountService(repository).withdraw("123", BigDecimal.ONE));
                        assertEquals(0, account.getBalance().signum());
                    }
                }
                """,
                """
                {"astRules":[
                    {"id":"has-test-annotation","check":"codeContains('@Test')","onPass":"✅ @Test annotation ile test metodu tanımlanmış.","onFail":"❌ @Test annotation eksik. JUnit test metotlarının üzerine @Test ekle.","why":"JUnit @Test olmadan metodu test olarak tanıyamaz ve çalıştırmaz.","alternative":"@ParameterizedTest ile aynı testi farklı parametrelerle çalıştırabilirsin.","realWorld":"Her public metot için en az 2 test: happy path + edge case."},
                    {"id":"has-assert","check":"codeContains('assert')","onPass":"✅ Assert ile sonuç doğrulanmış.","onFail":"❌ Assert eksik. Test sonucunu doğrulaman gerekiyor.","why":"Assert olmadan test 'çalıştı' ama 'doğru mu?' kontrolü yok. Anlamsız test.","alternative":"AssertJ kütüphanesi daha okunabilir assertion'lar sunar.","realWorld":"Her test en az bir assertion içermelidir."}
                ],"conceptsToOffer":["INTERFACE","DEPENDENCY_INJECTION"]}""");

        task8.addStep(step8_1);

        bank.addTask(task1);
        bank.addTask(task2);
        bank.addTask(task3);
        bank.addTask(task4);
        bank.addTask(task5);
        bank.addTask(task6);
        bank.addTask(task7);
        bank.addTask(task8);

        Project savedProject = saveCurriculum(bank);

        seedBankHints(savedProject);
    }

    private void seedBankHints(Project bank) {
        for (Task task : bank.getTasks()) {
            for (Step step : task.getSteps()) {
                Long stepId = step.getId();

                switch (task.getOrderIndex()) {
                    case 0 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "Bir sınıfın veritabanı tablosuna karşılık gelmesi için bir annotation gerekir. " +
                                        "jakarta.persistence paketine bak."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@Entity annotation'ını sınıfın üstüne ekle. Birincil anahtar için @Id kullan. " +
                                        "Otomatik id üretimi için @GeneratedValue(strategy = GenerationType.IDENTITY) ekle. " +
                                        "Alanları private yap ve getter/setter yaz."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Entity\npublic class User {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    // getter/setter ekle\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION,
                                step.getSolutionCode()));
                    }
                    case 1 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "User ile ilişki kurmak için JPA ilişki annotation'larından birine ihtiyacın var. Bir kullanıcının birçok hesabı olabilir."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@ManyToOne ile User ilişkisi kur. deposit/withdraw metotlarında parametre kontrolü yap. " +
                                        "balance alanını private tut."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@ManyToOne(fetch = FetchType.LAZY)\n@JoinColumn(name = \"user_id\")\nprivate User user;\n\npublic void deposit(BigDecimal amount) {\n    if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException(\"Pozitif olmalı\");\n    this.balance = this.balance.add(amount);\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 2 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "Spring Data JPA'da repository bir interface olarak tanımlanır ve JpaRepository'den extend eder."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "interface UserRepository extends JpaRepository<User, Long> şeklinde tanımla. Özel sorgu metotları için metot adlandırma kuralını kullan."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "public interface UserRepository extends JpaRepository<User, Long> {\n    Optional<User> findByEmail(String email);\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 3 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "Service sınıfı @Service annotation'ı alır ve repository'leri constructor ile alır."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "Constructor injection kullan: private final field + constructor parametresi. " +
                                        "Transfer = withdraw + deposit. @Transactional ile transaction güvenliği sağla."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Service\npublic class AccountService {\n    private final AccountRepository repo;\n    public AccountService(AccountRepository repo) { this.repo = repo; }\n\n    @Transactional\n    public void deposit(String accNo, BigDecimal amount) {\n        Account acc = repo.findByAccountNumber(accNo).orElseThrow(...);\n        acc.deposit(amount);\n        repo.save(acc);\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    default -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL, "İlgili annotation veya pattern'i hatırla."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE, "Önceki adımlardaki pattern'leri bu adıma uygula."));
                        saveHint(new Hint(stepId, HintLevel.CODE, "Çözüm kodunun bir kısmı..."));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                }
            }
        }
    }

    // =====================================================================
    //  E-COMMERCE PROJECT
    // =====================================================================

    private void seedECommerceProject() {
        Project ecommerce = new Project(ProjectCode.ECOMMERCE,
                "E-Ticaret Uygulaması",
                "Gerçek bir e-ticaret platformu geliştirerek Spring Boot'un ileri düzey özelliklerini öğren. " +
                        "Ürün yönetimi, kategori ilişkileri, custom query metotları, transaction yönetimi, " +
                        "REST best practices ve DTO validation konularını kapsayan orta seviye bir proje.",
                Level.INTERMEDIATE);

        // === TASK 0: Product Entity ===
        Task task0 = new Task(0, "Domain Modeli: Product Entity",
                "E-ticaret uygulamasının temel ürün entity'sini oluştur",
                "ENTITY,ENCAPSULATION");

        Step step0_1 = new Step(0,
                "Product sınıfını bir JPA entity olarak oluştur. id (Long), name (String), description (String), " +
                        "price (BigDecimal), stockQuantity (int) alanları olsun. Fiyat ve stok için iş kuralları ekle.",
                "// Product.java\nimport java.math.BigDecimal;\n\npublic class Product {\n    // Alanları ve metotları buraya ekle\n" +
                        "    // Fiyat pozitif olmalı, stok negatife düşmemeli\n}",
                "@Entity\npublic class Product {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                        "    private Long id;\n\n    @Column(nullable = false)\n    private String name;\n\n" +
                        "    @Column(columnDefinition = \"TEXT\")\n    private String description;\n\n" +
                        "    @Column(nullable = false, precision = 10, scale = 2)\n    private BigDecimal price;\n\n" +
                        "    @Column(name = \"stock_quantity\", nullable = false)\n    private int stockQuantity;\n\n" +
                        "    public Product() {}\n\n" +
                        "    public Product(String name, String description, BigDecimal price, int stockQuantity) {\n" +
                        "        this.name = name;\n        this.description = description;\n" +
                        "        setPrice(price);\n        setStockQuantity(stockQuantity);\n    }\n\n" +
                        "    public Long getId() { return id; }\n    public String getName() { return name; }\n" +
                        "    public void setName(String name) { this.name = name; }\n" +
                        "    public String getDescription() { return description; }\n" +
                        "    public void setDescription(String description) { this.description = description; }\n" +
                        "    public BigDecimal getPrice() { return price; }\n\n" +
                        "    public void setPrice(BigDecimal price) {\n" +
                        "        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)\n" +
                        "            throw new IllegalArgumentException(\"Fiyat pozitif olmalıdır\");\n" +
                        "        this.price = price;\n    }\n\n" +
                        "    public int getStockQuantity() { return stockQuantity; }\n\n" +
                        "    public void setStockQuantity(int stockQuantity) {\n" +
                        "        if (stockQuantity < 0)\n" +
                        "            throw new IllegalArgumentException(\"Stok negatif olamaz\");\n" +
                        "        this.stockQuantity = stockQuantity;\n    }\n\n" +
                        "    public void decreaseStock(int quantity) {\n" +
                        "        if (quantity <= 0) throw new IllegalArgumentException(\"Miktar pozitif olmalı\");\n        if (quantity > this.stockQuantity)\n" +
                        "            throw new IllegalArgumentException(\"Yetersiz stok\");\n" +
                        "        this.stockQuantity -= quantity;\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-entity","check":"classHasAnnotation('Entity')","onPass":"✅ @Entity ile Product veritabanı tablosuna eşlendi.","onFail":"❌ @Entity annotation eksik. JPA bu sınıfı yönetemez.","why":"@Entity olmadan Hibernate sınıfı görmezden gelir ve repository işlemleri hata verir.","alternative":"@Table(name=\\"products\\") ile tablo adını özelleştirebilirsin.","realWorld":"E-ticaret sistemlerinde Product entity'si en temel domain nesnesidir."},
                    {"id":"has-id","check":"field('id').hasAnnotation('Id')","onPass":"✅ @Id ile primary key tanımlı.","onFail":"❌ id alanında @Id eksik.","why":"Her entity'nin benzersiz bir kimliği (PK) olmalıdır.","alternative":"UUID de kullanılabilir ama Long daha yaygındır.","realWorld":"Ürün ID'si URL'lerde, ilişkilerde ve cache'te kullanılır."},
                    {"id":"price-private","check":"field('price').isPrivate()","onPass":"✅ price alanı private — fiyat kontrolü setter üzerinden yapılabilir.","onFail":"⚠️ price private olmalı! Negatif fiyat atanmasını engellemek için encapsulation gerekli.","why":"Fiyat gibi kritik veriler kapsüllenmelidir. Setter içinde doğrulama yapılır.","alternative":"public yapsan çalışır ama iş kuralı zorlanamaz.","realWorld":"E-ticaret'te yanlış fiyat büyük finansal kayba yol açar."},
                    {"id":"stock-private","check":"field('stockQuantity').isPrivate()","onPass":"✅ stockQuantity private — stok kontrolü metotlarla yapılır.","onFail":"⚠️ stockQuantity private olmalı. Stok doğrudan değiştirilebilirse negatif stok oluşur.","why":"Domain invariant: stok negatife düşmemeli. Bu kuralı entity içinde zorla.","alternative":"","realWorld":"Race condition'larda bile stok tutarlılığı sağlanmalıdır."},
                    {"id":"has-getter-name","check":"hasMethod('getName')","onPass":"✅ getName() getter metodu mevcut.","onFail":"⚠️ getName() getter eksik.","why":"Private alanlara kontrollü erişim için getter gerekir.","alternative":"Lombok kullanılabilir ama elle yazmak öğreticidir.","realWorld":"Jackson (JSON serialization) getter'lara bağımlıdır."},
                    {"id":"has-decrease-stock","check":"hasMethod('decreaseStock')","onPass":"✅ decreaseStock metodu ile stok kontrolü entity içinde.","onFail":"⚠️ Stok azaltma metodu ekle — iş kuralını entity'de tut.","why":"Rich domain model: iş mantığı entity içinde olmalı.","alternative":"Serviste de yapılabilir ama domain anemic olur.","realWorld":"Stok kontrolü entity'de yapılırsa tüm kullanım noktalarında tutarlıdır."}
                ],"conceptsToOffer":["ENTITY","ENCAPSULATION"]}""");

        step0_1.setInstructionBeginner("""
                🎯 Product (Ürün) entity'sini oluştur.

                📋 Yapman gerekenler:
                1. Sınıfın üzerine @Entity annotation'ı ekle
                2. id alanı: Long tipinde, @Id ve @GeneratedValue ile
                3. name alanı: String, @Column(nullable = false)
                4. description alanı: String, @Column(columnDefinition = "TEXT")
                5. price alanı: BigDecimal tipinde, private
                6. stockQuantity alanı: int tipinde, private
                7. setPrice() içinde pozitif kontrolü yap
                8. setStockQuantity() içinde negatif kontrolü yap
                9. decreaseStock(int quantity) metodu yaz — stok kontrolü ile
                10. Getter/setter metotlarını yaz

                💡 Bilgi:
                • BigDecimal: Finansal hesaplamalarda double yerine kullanılır (hassasiyet)
                • @Column(nullable = false): Veritabanında bu alanın boş olamayacağını belirtir
                • Encapsulation: price ve stockQuantity private olmalı, setter ile kontrollü erişim
                • decreaseStock(): Sipariş verildiğinde stok azaltılır — yetersiz stok kontrolü şart""");

        step0_1.setInstructionAdvanced(
                "E-ticaret Product entity'si oluştur. id, name, description, price (BigDecimal), " +
                        "stockQuantity alanları. Fiyat ve stok için domain invariant'ları (validation) " +
                        "entity metotları içinde zorla. decreaseStock metodu ekle.");

        task0.addStep(step0_1);

        // === TASK 1: Category Entity ===
        Task task1 = new Task(1, "Category Entity: @ManyToMany İlişki",
                "Kategori entity'sini oluştur ve ürünlerle çoka-çok ilişki kur",
                "ENTITY,JPA_RELATIONSHIPS");

        Step step1_1 = new Step(0,
                "Category sınıfını oluştur: id, name. Product ile @ManyToMany ilişki kur. " +
                        "Bir ürün birden fazla kategoriye, bir kategori birden fazla ürüne sahip olabilir.",
                "// Category.java\n\npublic class Category {\n    // id, name alanları\n" +
                        "    // Product ile @ManyToMany ilişki\n}",
                "@Entity\npublic class Category {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                        "    private Long id;\n\n    @Column(nullable = false, unique = true)\n    private String name;\n\n" +
                        "    @ManyToMany\n    @JoinTable(\n        name = \"product_category\",\n" +
                        "        joinColumns = @JoinColumn(name = \"category_id\"),\n" +
                        "        inverseJoinColumns = @JoinColumn(name = \"product_id\")\n    )\n" +
                        "    private Set<Product> products = new HashSet<>();\n\n" +
                        "    public Category() {}\n\n    public Category(String name) { this.name = name; }\n\n" +
                        "    public Long getId() { return id; }\n    public String getName() { return name; }\n" +
                        "    public void setName(String name) { this.name = name; }\n" +
                        "    public Set<Product> getProducts() { return products; }\n\n" +
                        "    public void addProduct(Product product) { this.products.add(product); }\n" +
                        "    public void removeProduct(Product product) { this.products.remove(product); }\n}",
                """
                {"astRules":[
                    {"id":"has-entity","check":"classHasAnnotation('Entity')","onPass":"✅ @Entity ile Category veritabanı tablosuna eşlendi.","onFail":"❌ @Entity eksik.","why":"JPA yönetimi için zorunlu.","alternative":"","realWorld":"Kategori sistemi e-ticaretin temel özelliğidir."},
                    {"id":"has-manytomany","check":"codeContains('@ManyToMany')","onPass":"✅ @ManyToMany ile çoka-çok ilişki doğru tanımlanmış.","onFail":"❌ Product ile @ManyToMany ilişki eksik. Bir ürün birçok kategoride, bir kategori birçok üründe olabilir.","why":"Gerçek dünyada bir ürün hem 'Elektronik' hem 'İndirimli Ürünler' kategorisinde olabilir.","alternative":"@OneToMany/@ManyToOne ile ara entity de kullanılabilir ama basit senaryolarda @ManyToMany yeterli.","realWorld":"Amazon'da bir ürün birden fazla kategoride görünür."},
                    {"id":"has-join-table","check":"codeContains('@JoinTable')","onPass":"✅ @JoinTable ile ara tablo tanımlanmış.","onFail":"⚠️ @JoinTable ile ara tablonun adını ve sütunlarını belirle.","why":"ManyToMany ilişkide JPA bir ara tablo oluşturur. @JoinTable ile adını ve sütunlarını kontrol edersin.","alternative":"@JoinTable olmadan JPA varsayılan isimlerle ara tablo oluşturur.","realWorld":"Tablo adlandırma convention'ı projede tutarlı olmalıdır."},
                    {"id":"id-private","check":"field('id').isPrivate()","onPass":"✅ id alanı private.","onFail":"⚠️ id private olmalı.","why":"Encapsulation: tüm alanlar private, erişim metotlarla.","alternative":"","realWorld":"Tüm entity alanları private tutulur."}
                ],"conceptsToOffer":["ENTITY","JPA_RELATIONSHIPS"]}""");

        step1_1.setInstructionBeginner("""
                🎯 Category (Kategori) entity'sini oluştur ve Product ile ilişkilendir.

                📋 Yapman gerekenler:
                1. @Entity annotation'ı ekle
                2. id (Long, @Id, @GeneratedValue) ve name (String) alanları
                3. @ManyToMany ile Product ilişkisi kur
                4. @JoinTable ile ara tablo tanımla (product_category)
                5. Set<Product> kullan (List yerine — duplicate engeller)
                6. addProduct() ve removeProduct() yardımcı metotları ekle

                💡 Bilgi:
                • @ManyToMany: Çoka-çok ilişki. Bir kategori çok ürüne, bir ürün çok kategoriye sahip olabilir
                • @JoinTable: İki tablo arasında ara tablo (junction table) oluşturur
                • Set kullanımı: Aynı ürünün aynı kategoriye iki kez eklenmesini engeller
                • joinColumns: Bu tarafın (Category) sütunu, inverseJoinColumns: karşı tarafın (Product) sütunu""");

        step1_1.setInstructionAdvanced(
                "Category entity'si oluştur. Product ile @ManyToMany ilişki, @JoinTable ile ara tablo, " +
                        "Set<Product> koleksiyonu ve yardımcı metotlar (addProduct/removeProduct).");

        task1.addStep(step1_1);

        // === TASK 2: ProductRepository ===
        Task task2 = new Task(2, "ProductRepository: Custom Query Metotları",
                "Spring Data JPA repository'si oluştur ve custom query metotları yaz",
                "INTERFACE,DEPENDENCY_INJECTION");

        Step step2_1 = new Step(0,
                "ProductRepository interface'ini oluştur. JpaRepository'den extend et. " +
                        "Fiyat aralığına göre, ada göre ve stokta olan ürünleri bulan custom metotlar yaz.",
                "// ProductRepository.java\n\npublic interface ProductRepository {\n" +
                        "    // JpaRepository'den extend et\n    // Custom query metotları ekle\n}",
                "public interface ProductRepository extends JpaRepository<Product, Long> {\n\n" +
                        "    List<Product> findByNameContainingIgnoreCase(String keyword);\n\n" +
                        "    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);\n\n" +
                        "    List<Product> findByStockQuantityGreaterThan(int minStock);\n\n" +
                        "    @Query(\"SELECT p FROM Product p WHERE p.price <= :maxPrice AND p.stockQuantity > 0\")\n" +
                        "    List<Product> findAvailableUnderPrice(@Param(\"maxPrice\") BigDecimal maxPrice);\n\n" +
                        "    Optional<Product> findByNameIgnoreCase(String name);\n}",
                """
                {"astRules":[
                    {"id":"extends-jpa","check":"codeContains('JpaRepository')","onPass":"✅ JpaRepository doğru extend edilmiş. CRUD metotları otomatik gelir.","onFail":"❌ JpaRepository'den extend etmelisin.","why":"JpaRepository ile save, findById, findAll, delete otomatik sağlanır.","alternative":"CrudRepository daha minimal bir alternatif.","realWorld":"Spring projelerde JpaRepository standart tercih."},
                    {"id":"is-interface","check":"codeContains('interface')","onPass":"✅ Repository bir interface.","onFail":"❌ Repository interface olmalı, class değil!","why":"Spring Data, interface'den proxy ile implementasyon üretir.","alternative":"","realWorld":"Tek satır implementasyon yazmadan CRUD + özel sorgular hazır."},
                    {"id":"has-find-by-name","check":"hasMethod('findByNameContainingIgnoreCase')","onPass":"✅ İsme göre arama metodu tanımlanmış.","onFail":"⚠️ findByNameContainingIgnoreCase metodu ekle — ürün arama özelliği için.","why":"Derived query: Spring metot adından SQL üretir. Containing = LIKE %keyword%.","alternative":"@Query ile JPQL de yazabilirsin ama derived query daha okunabilir.","realWorld":"E-ticaret uygulamalarında ürün arama temel özelliktir."},
                    {"id":"has-price-range","check":"hasMethod('findByPriceBetween')","onPass":"✅ Fiyat aralığı sorgusu tanımlanmış.","onFail":"⚠️ findByPriceBetween metodu ekle — fiyat filtresi için.","why":"Between keyword'ü ile SQL BETWEEN sorgusu otomatik üretilir.","alternative":"@Query ile daha karmaşık sorgular yazılabilir.","realWorld":"Hemen her e-ticaret sitesinde fiyat filtresi vardır."}
                ],"conceptsToOffer":["INTERFACE","DEPENDENCY_INJECTION"]}""");

        step2_1.setInstructionBeginner("""
                🎯 ProductRepository interface'ini oluştur.

                📋 Yapman gerekenler:
                1. interface olarak tanımla (class değil!)
                2. JpaRepository<Product, Long> extend et
                3. findByNameContainingIgnoreCase(String keyword) → isme göre arama
                4. findByPriceBetween(BigDecimal min, BigDecimal max) → fiyat filtresi
                5. findByStockQuantityGreaterThan(int min) → stokta olanlar
                6. @Query ile özel JPQL sorgusu yaz (opsiyonel)

                💡 Bilgi:
                • Spring Data JPA, metot adından otomatik SQL üretir (Derived Query)
                • findBy + AlanAdı + Keyword → SQL WHERE clause oluşturur
                • Containing = LIKE %keyword% | Between = BETWEEN min AND max
                • IgnoreCase = büyük/küçük harf duyarsız arama
                • @Query ile JPQL veya native SQL yazabilirsin — daha karmaşık sorgular için""");

        step2_1.setInstructionAdvanced(
                "ProductRepository interface'i oluştur. JpaRepository extend et. Derived query metotları " +
                        "(findByNameContaining, findByPriceBetween, findByStockQuantityGreaterThan) ve " +
                        "@Query ile JPQL sorgusu yaz.");

        task2.addStep(step2_1);

        // === TASK 3: ProductService ===
        Task task3 = new Task(3, "ProductService: İş Mantığı ve Transaction",
                "Ürün yönetimi için service katmanını oluştur",
                "DEPENDENCY_INJECTION,TRANSACTION_MANAGEMENT");

        Step step3_1 = new Step(0,
                "ProductService sınıfını oluştur. Constructor injection ile repository'yi enjekte et. " +
                        "Ürün oluşturma, güncelleme, stok azaltma ve listeleme metotlarını yaz. " +
                        "@Transactional ile transaction yönetimi sağla.",
                "@Service\npublic class ProductService {\n    // Repository'yi constructor injection ile enjekte et\n" +
                        "    // createProduct, updatePrice, decreaseStock, findAll metotlarını yaz\n}",
                "@Service\npublic class ProductService {\n\n" +
                        "    private final ProductRepository productRepository;\n\n" +
                        "    public ProductService(ProductRepository productRepository) {\n" +
                        "        this.productRepository = productRepository;\n    }\n\n" +
                        "    @Transactional\n    public Product createProduct(String name, String description,\n" +
                        "                                     BigDecimal price, int stock) {\n" +
                        "        Product product = new Product(name, description, price, stock);\n" +
                        "        return productRepository.save(product);\n    }\n\n" +
                        "    @Transactional\n    public Product updatePrice(Long productId, BigDecimal newPrice) {\n" +
                        "        Product product = productRepository.findById(productId)\n" +
                        "            .orElseThrow(() -> new RuntimeException(\"Ürün bulunamadı: \" + productId));\n" +
                        "        product.setPrice(newPrice);\n" +
                        "        return productRepository.save(product);\n    }\n\n" +
                        "    @Transactional\n    public void decreaseStock(Long productId, int quantity) {\n" +
                        "        Product product = productRepository.findById(productId)\n" +
                        "            .orElseThrow(() -> new RuntimeException(\"Ürün bulunamadı: \" + productId));\n" +
                        "        product.decreaseStock(quantity);\n" +
                        "        productRepository.save(product);\n    }\n\n" +
                        "    @Transactional(readOnly = true)\n    public List<Product> findAll() {\n" +
                        "        return productRepository.findAll();\n    }\n\n" +
                        "    @Transactional(readOnly = true)\n" +
                        "    public List<Product> searchByName(String keyword) {\n" +
                        "        return productRepository.findByNameContainingIgnoreCase(keyword);\n    }\n" +
                        "    @Transactional(readOnly = true)\n    public Optional<Product> findById(Long id) { return productRepository.findById(id); }\n" +
                        "    @Transactional\n    public void deleteById(Long id) { productRepository.deleteById(id); }\n}",
                """
                {"astRules":[
                    {"id":"has-service","check":"classHasAnnotation('Service')","onPass":"✅ @Service doğru — Spring bu sınıfı bean olarak yönetecek.","onFail":"❌ @Service eksik. Spring bu sınıfı tanıyamaz.","why":"@Service ile Spring IoC container'a bean olarak kaydedilir.","alternative":"@Component da çalışır ama @Service semantik olarak daha doğru.","realWorld":"Katmanlı mimaride service katmanı iş mantığını barındırır."},
                    {"id":"has-constructor","check":"hasConstructor()","onPass":"✅ Constructor injection ile bağımlılık enjekte edilmiş.","onFail":"❌ Constructor injection eksik. Repository'yi constructor ile al.","why":"Constructor injection: immutable, test edilebilir, bağımlılıklar açık.","alternative":"@Autowired field injection da çalışır ama önerilmez.","realWorld":"Spring ekibi constructor injection'ı resmi olarak önerir."},
                    {"id":"has-transactional","check":"codeContains('@Transactional')","onPass":"✅ @Transactional ile transaction yönetimi sağlanmış.","onFail":"⚠️ @Transactional eksik. Veri değişikliği yapan metotlarda transaction gerekli.","why":"Transaction olmadan hata durumunda yarım kalmış veri değişiklikleri oluşur.","alternative":"Programmatic transaction da mümkün ama declarative daha temiz.","realWorld":"Her write işlemi transaction içinde olmalıdır."},
                    {"id":"has-create","check":"hasMethod('createProduct')","onPass":"✅ createProduct metodu tanımlanmış.","onFail":"❌ createProduct metodu eksik.","why":"Ürün oluşturma temel CRUD işlemidir.","alternative":"","realWorld":"E-ticaret yönetim panelinin ilk özelliği ürün ekleme."},
                    {"id":"has-decrease-stock","check":"hasMethod('decreaseStock')","onPass":"✅ decreaseStock ile stok yönetimi service'de koordine ediliyor.","onFail":"⚠️ decreaseStock metodu ekle — sipariş verildiğinde stok azalmalı.","why":"Stok azaltma transaction içinde olmalı — concurrent request'lerde tutarsızlık engellenir.","alternative":"","realWorld":"Yüksek trafikli e-ticaret'te stok yönetimi kritiktir."}
                ],"conceptsToOffer":["DEPENDENCY_INJECTION","TRANSACTION_MANAGEMENT"]}""");

        step3_1.setInstructionBeginner("""
                🎯 ProductService sınıfını oluştur.

                📋 Yapman gerekenler:
                1. @Service annotation'ı ekle
                2. ProductRepository'yi constructor injection ile enjekte et (private final)
                3. createProduct(...) → yeni ürün oluştur ve kaydet
                4. updatePrice(Long id, BigDecimal newPrice) → fiyat güncelle
                5. decreaseStock(Long id, int quantity) → stok azalt
                6. findAll() → tüm ürünleri listele
                7. Write metotlarına @Transactional, read metotlarına @Transactional(readOnly = true) ekle

                💡 Bilgi:
                • Constructor Injection: private final field + constructor parametresi
                • @Transactional: Hata olursa tüm değişiklikleri geri al (rollback)
                • @Transactional(readOnly = true): Sadece okuma — Hibernate optimizasyonları aktif olur
                • orElseThrow(): Optional'dan değer al, yoksa exception fırlat""");

        step3_1.setInstructionAdvanced(
                "ProductService oluştur. Constructor injection, @Transactional yönetimi, " +
                        "CRUD operasyonları (create, updatePrice, decreaseStock, findAll, searchByName). " +
                        "Read-only metotlarda @Transactional(readOnly = true) kullan.");

        task3.addStep(step3_1);

        // === TASK 4: ProductController ===
        Task task4 = new Task(4, "ProductController: REST API",
                "RESTful endpoint'leri oluştur ve best practice'leri uygula",
                "DEPENDENCY_INJECTION,REST_BEST_PRACTICES");

        Step step4_1 = new Step(0,
                "ProductController sınıfını oluştur. GET (listeleme, detay), POST (oluşturma), " +
                        "PUT (güncelleme), DELETE endpoint'lerini yaz. ResponseEntity ve uygun HTTP status kodlarını kullan.",
                "@RestController\n@RequestMapping(\"/api/products\")\npublic class ProductController {\n" +
                        "    // Service'i enjekte et\n    // CRUD endpoint'lerini yaz\n}",
                "@RestController\n@RequestMapping(\"/api/products\")\npublic class ProductController {\n\n" +
                        "    private final ProductService productService;\n\n" +
                        "    public ProductController(ProductService productService) {\n" +
                        "        this.productService = productService;\n    }\n\n" +
                        "    @GetMapping\n    public ResponseEntity<List<Product>> list() {\n" +
                        "        return ResponseEntity.ok(productService.findAll());\n    }\n\n" +
                        "    @GetMapping(\"/{id}\")\n    public ResponseEntity<Product> getById(@PathVariable Long id) {\n" +
                        "        return productService.findById(id)\n" +
                        "            .map(ResponseEntity::ok)\n" +
                        "            .orElse(ResponseEntity.notFound().build());\n    }\n\n" +
                        "    @PostMapping\n    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductDTO dto) {\n" +
                        "        Product product = productService.createProduct(\n" +
                        "            dto.getName(), dto.getDescription(), dto.getPrice(), dto.getStock());\n" +
                        "        return ResponseEntity.status(HttpStatus.CREATED).body(product);\n    }\n\n" +
                        "    @PutMapping(\"/{id}/price\")\n" +
                        "    public ResponseEntity<Product> updatePrice(@PathVariable Long id,\n" +
                        "                                                @RequestParam BigDecimal newPrice) {\n" +
                        "        return ResponseEntity.ok(productService.updatePrice(id, newPrice));\n    }\n\n" +
                        "    @DeleteMapping(\"/{id}\")\n" +
                        "    public ResponseEntity<Void> delete(@PathVariable Long id) {\n" +
                        "        productService.deleteById(id);\n" +
                        "        return ResponseEntity.noContent().build();\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-rest-controller","check":"classHasAnnotation('RestController')","onPass":"✅ @RestController doğru — JSON response otomatik.","onFail":"❌ @RestController eksik.","why":"@RestController = @Controller + @ResponseBody. Her metot JSON döner.","alternative":"@Controller + @ResponseBody ayrı ayrı da kullanılabilir.","realWorld":"REST API'lerde standart tercih."},
                    {"id":"has-request-mapping","check":"codeContains('@RequestMapping')","onPass":"✅ Base path /api/products tanımlı.","onFail":"⚠️ @RequestMapping ile base path tanımla.","why":"REST'te resource-based URL yapısı: /api/products","alternative":"","realWorld":"URL'de fiil olmamalı, isim olmalı: /api/products (doğru), /api/getProducts (yanlış)."},
                    {"id":"has-get-mapping","check":"codeContains('@GetMapping')","onPass":"✅ GET endpoint tanımlı — okuma işlemleri için.","onFail":"❌ @GetMapping ile listeleme/detay endpoint'i ekle.","why":"GET = okuma işlemi. Server state'ini değiştirmez (idempotent + safe).","alternative":"","realWorld":"GET /api/products → liste, GET /api/products/5 → detay."},
                    {"id":"has-post-mapping","check":"codeContains('@PostMapping')","onPass":"✅ POST endpoint tanımlı — kaynak oluşturma için.","onFail":"❌ @PostMapping ile oluşturma endpoint'i ekle.","why":"POST = yeni kaynak oluştur. 201 Created status kodu dönülmeli.","alternative":"","realWorld":"POST /api/products ile yeni ürün oluşturulur."},
                    {"id":"has-delete-mapping","check":"codeContains('@DeleteMapping')","onPass":"✅ DELETE endpoint tanımlı — silme işlemi için.","onFail":"⚠️ @DeleteMapping ile silme endpoint'i ekle.","why":"DELETE = kaynak sil. 204 No Content status kodu dönülmeli.","alternative":"Soft delete (isDeleted flag) daha güvenli alternatif.","realWorld":"Çoğu production sistemde hard delete yerine soft delete tercih edilir."}
                ],"conceptsToOffer":["DEPENDENCY_INJECTION","REST_BEST_PRACTICES"]}""");

        step4_1.setInstructionBeginner("""
                🎯 ProductController REST API'sini oluştur.

                📋 Yapman gerekenler:
                1. @RestController ve @RequestMapping("/api/products") ekle
                2. GET /api/products → tüm ürünleri listele (200 OK)
                3. GET /api/products/{id} → id'ye göre ürün getir (200 OK / 404 Not Found)
                4. POST /api/products → yeni ürün oluştur (201 Created)
                5. PUT /api/products/{id}/price → fiyat güncelle (200 OK)
                6. DELETE /api/products/{id} → ürünü sil (204 No Content)
                7. ResponseEntity ile uygun HTTP status kodlarını dön

                💡 REST Best Practices:
                • URL'de fiil kullanma (getProducts ❌) — HTTP metodu fiili belirtir
                • Çoğul isim kullan: /products (doğru), /product (yanlış)
                • GET=oku, POST=oluştur, PUT=güncelle, DELETE=sil
                • 200=OK, 201=Created, 204=No Content, 404=Not Found
                • @Valid ile gelen veriyi doğrula""");

        step4_1.setInstructionAdvanced(
                "ProductController REST API: GET (list, detail), POST (create with @Valid), " +
                        "PUT (update price), DELETE endpoint'leri. ResponseEntity ve uygun HTTP status kodları.");

        task4.addStep(step4_1);

        // === TASK 5: DTO and Validation ===
        Task task5 = new Task(5, "DTO ve Validation Katmanı",
                "Request/Response DTO'ları oluştur ve validation annotation'ları ekle",
                "VALIDATION_PATTERNS,ENCAPSULATION");

        Step step5_1 = new Step(0,
                "CreateProductDTO sınıfını oluştur. name, description, price, stock alanlarına " +
                        "uygun validation annotation'ları (@NotBlank, @Positive, @Size vb.) ekle.",
                "// CreateProductDTO.java\npublic class CreateProductDTO {\n" +
                        "    // Alanlar ve validation annotation'ları\n}",
                "public class CreateProductDTO {\n\n" +
                        "    @NotBlank(message = \"Ürün adı boş olamaz\")\n" +
                        "    @Size(min = 2, max = 100, message = \"Ürün adı 2-100 karakter arası olmalıdır\")\n" +
                        "    private String name;\n\n" +
                        "    @Size(max = 500, message = \"Açıklama en fazla 500 karakter olabilir\")\n" +
                        "    private String description;\n\n" +
                        "    @NotNull(message = \"Fiyat zorunludur\")\n" +
                        "    @Positive(message = \"Fiyat pozitif olmalıdır\")\n" +
                        "    private BigDecimal price;\n\n" +
                        "    @NotNull(message = \"Stok miktarı zorunludur\")\n" +
                        "    @PositiveOrZero(message = \"Stok negatif olamaz\")\n" +
                        "    private Integer stock;\n\n" +
                        "    public String getName() { return name; }\n" +
                        "    public void setName(String name) { this.name = name; }\n" +
                        "    public String getDescription() { return description; }\n" +
                        "    public void setDescription(String description) { this.description = description; }\n" +
                        "    public BigDecimal getPrice() { return price; }\n" +
                        "    public void setPrice(BigDecimal price) { this.price = price; }\n" +
                        "    public Integer getStock() { return stock; }\n" +
                        "    public void setStock(Integer stock) { this.stock = stock; }\n}",
                """
                {"astRules":[
                    {"id":"has-notblank","check":"codeContains('@NotBlank')","onPass":"✅ @NotBlank ile boş isim engellendi.","onFail":"❌ @NotBlank eksik. Boş ürün adı kabul edilmemeli.","why":"@NotBlank: null, \\"\\", \\"   \\" hepsini reddeder. String alanlar için @NotNull yerine @NotBlank kullan.","alternative":"@NotNull sadece null'ı engeller, boş string geçer.","realWorld":"E-ticaret'te isimsiz ürün listelenmemeli."},
                    {"id":"has-positive","check":"codeContains('@Positive')","onPass":"✅ @Positive ile negatif fiyat engellendi.","onFail":"❌ @Positive ekle — fiyat pozitif olmalı.","why":"Negatif veya sıfır fiyat iş mantığına aykırıdır.","alternative":"@Min(1) da kullanılabilir ama @Positive daha semantik.","realWorld":"Fiyat validasyonu güvenlik açısından da kritiktir."},
                    {"id":"has-size","check":"codeContains('@Size')","onPass":"✅ @Size ile alan uzunluğu sınırlandırıldı.","onFail":"⚠️ @Size ile alan uzunluklarını sınırla.","why":"Çok uzun veya çok kısa değerleri engellemek veri kalitesini artırır.","alternative":"@Length (Hibernate Validator) da kullanılabilir.","realWorld":"Veritabanı sütun boyutuyla uyumlu olmalı — aksi halde truncation hatası."},
                    {"id":"name-private","check":"field('name').isPrivate()","onPass":"✅ name alanı private — encapsulation doğru.","onFail":"⚠️ name private olmalı.","why":"DTO'larda da encapsulation uygulanır. Jackson getter/setter kullanır.","alternative":"public record da kullanılabilir (Java 14+).","realWorld":"DTO = Data Transfer Object, API sözleşmesini tanımlar."}
                ],"conceptsToOffer":["VALIDATION_PATTERNS","ENCAPSULATION"]}""");

        step5_1.setInstructionBeginner("""
                🎯 CreateProductDTO sınıfını oluştur ve validation ekle.

                📋 Yapman gerekenler:
                1. name alanı: @NotBlank + @Size(min=2, max=100) — boş olamaz, uzunluk sınırlı
                2. description alanı: @Size(max=500) — opsiyonel ama uzunluk sınırlı
                3. price alanı: @NotNull + @Positive — zorunlu ve pozitif
                4. stock alanı: @NotNull + @PositiveOrZero — zorunlu ve negatif olamaz
                5. Tüm alanları private yap
                6. Getter/setter metotlarını yaz
                7. Her annotation'a Türkçe message parametresi ekle

                💡 Bilgi:
                • DTO (Data Transfer Object): API isteğini temsil eden sınıf — entity değil!
                • @NotBlank: null, "", "   " hepsini reddeder (String için ideal)
                • @Positive: 0'dan büyük olmalı | @PositiveOrZero: 0 veya daha büyük
                • @Size(min, max): String uzunluk sınırı
                • Controller'da @Valid ile aktif edilir: @Valid @RequestBody CreateProductDTO dto""");

        step5_1.setInstructionAdvanced(
                "CreateProductDTO: @NotBlank, @Size, @Positive, @PositiveOrZero, @NotNull annotation'ları. " +
                        "Türkçe message'lar. Tüm alanlar private + getter/setter.");

        task5.addStep(step5_1);

        ecommerce.addTask(task0);
        ecommerce.addTask(task1);
        ecommerce.addTask(task2);
        ecommerce.addTask(task3);
        ecommerce.addTask(task4);
        ecommerce.addTask(task5);

        Project savedProject = saveCurriculum(ecommerce);
        seedECommerceHints(savedProject);
    }

    private void seedECommerceHints(Project project) {
        for (Task task : project.getTasks()) {
            for (Step step : task.getSteps()) {
                Long stepId = step.getId();

                switch (task.getOrderIndex()) {
                    case 0 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "Fiyat için double yerine BigDecimal kullan — finansal hesaplamalarda hassasiyet önemli. " +
                                        "@Entity annotation'ını ve @Id/@GeneratedValue'yi unutma."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@Entity ekle. id (Long, @Id, @GeneratedValue), name (String), description (String), " +
                                        "price (BigDecimal), stockQuantity (int) alanları oluştur. " +
                                        "setPrice içinde BigDecimal.ZERO ile karşılaştır. decreaseStock metodu ekle."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Entity\npublic class Product {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                                        "    private Long id;\n    private String name;\n    private BigDecimal price;\n    private int stockQuantity;\n\n" +
                                        "    public void setPrice(BigDecimal price) {\n" +
                                        "        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)\n" +
                                        "            throw new IllegalArgumentException(\"Fiyat pozitif olmalı\");\n" +
                                        "        this.price = price;\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 1 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "Çoka-çok ilişki için @ManyToMany kullan. Koleksiyon tipi olarak Set tercih et — duplicate engeller."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@ManyToMany ile @JoinTable kullan. joinColumns = bu tarafın FK'si, " +
                                        "inverseJoinColumns = karşı tarafın FK'si. Ara tablo adını product_category yap."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@ManyToMany\n@JoinTable(name = \"product_category\",\n" +
                                        "    joinColumns = @JoinColumn(name = \"category_id\"),\n" +
                                        "    inverseJoinColumns = @JoinColumn(name = \"product_id\"))\n" +
                                        "private Set<Product> products = new HashSet<>();"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 2 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "interface olarak tanımla ve JpaRepository<Product, Long> extend et. " +
                                        "Metot adlandırma kuralını kullan: findByAlanAdıKeyword()."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "findByNameContainingIgnoreCase → LIKE %keyword% (case-insensitive). " +
                                        "findByPriceBetween → BETWEEN min AND max. " +
                                        "findByStockQuantityGreaterThan → stokta olanlar."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "public interface ProductRepository extends JpaRepository<Product, Long> {\n" +
                                        "    List<Product> findByNameContainingIgnoreCase(String keyword);\n" +
                                        "    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 3 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "@Service annotation'ı ve constructor injection ile başla. " +
                                        "Veri değiştiren metotlara @Transactional ekle."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "private final ProductRepository + constructor. " +
                                        "createProduct: new Product() + save(). " +
                                        "updatePrice: findById + setPrice + save. " +
                                        "Okuma metotlarına @Transactional(readOnly = true) ekle."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Service\npublic class ProductService {\n    private final ProductRepository repo;\n\n" +
                                        "    public ProductService(ProductRepository repo) { this.repo = repo; }\n\n" +
                                        "    @Transactional\n    public Product createProduct(...) {\n" +
                                        "        return repo.save(new Product(...));\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 4 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "@RestController + @RequestMapping(\"/api/products\"). " +
                                        "Her HTTP metodu bir CRUD işlemine karşılık gelir: GET=oku, POST=oluştur, PUT=güncelle, DELETE=sil."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@GetMapping → listeleme/detay, @PostMapping → oluşturma (201 Created), " +
                                        "@PutMapping → güncelleme (200 OK), @DeleteMapping → silme (204 No Content). " +
                                        "ResponseEntity ile uygun status kodu dön."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@RestController\n@RequestMapping(\"/api/products\")\npublic class ProductController {\n\n" +
                                        "    @GetMapping\n    public ResponseEntity<List<Product>> list() {\n" +
                                        "        return ResponseEntity.ok(service.findAll());\n    }\n\n" +
                                        "    @PostMapping\n    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductDTO dto) {\n" +
                                        "        return ResponseEntity.status(HttpStatus.CREATED).body(...);\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 5 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "String alanlara @NotBlank, sayısal alanlara @Positive veya @PositiveOrZero, " +
                                        "uzunluk sınırı için @Size kullan."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "name: @NotBlank + @Size(min=2, max=100). price: @NotNull + @Positive. " +
                                        "stock: @NotNull + @PositiveOrZero. Her annotation'a Türkçe message ekle."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "public class CreateProductDTO {\n    @NotBlank(message = \"Ürün adı boş olamaz\")\n" +
                                        "    @Size(min = 2, max = 100)\n    private String name;\n\n" +
                                        "    @NotNull @Positive(message = \"Fiyat pozitif olmalı\")\n" +
                                        "    private BigDecimal price;\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                }
            }
        }
    }

    // =====================================================================
    //  LIBRARY PROJECT
    // =====================================================================

    private void seedLibraryProject() {
        Project library = new Project(ProjectCode.LIBRARY,
                "Kütüphane Yönetim Sistemi",
                "Bir kütüphane yönetim sistemi geliştirerek Java ve Spring Boot'un temel kavramlarını pekiştir. " +
                        "Kitap ve üye yönetimi, ödünç alma/iade işlemleri ve REST API oluşturmayı kapsayan " +
                        "başlangıç seviyesinde bir proje. Banka projesinde öğrendiğin kavramları farklı bir " +
                        "domain'de uygulayarak deneyimini güçlendir.",
                Level.BEGINNER);

        // === TASK 0: Book Entity ===
        Task task0 = new Task(0, "Domain Modeli: Book Entity",
                "Kütüphane kitap entity'sini oluştur",
                "ENTITY,ENCAPSULATION,ID,GENERATED_VALUE");

        Step step0_1 = new Step(0,
                "Book sınıfını bir JPA entity olarak oluştur. id (Long), title (String), author (String), " +
                        "isbn (String, unique), available (boolean) alanları olsun.",
                "// Book.java\n\npublic class Book {\n    // Alanları buraya ekle\n" +
                        "    // isbn benzersiz olmalı, available varsayılan true\n}",
                "@Entity\npublic class Book {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                        "    private Long id;\n\n    @Column(nullable = false)\n    private String title;\n\n" +
                        "    @Column(nullable = false)\n    private String author;\n\n" +
                        "    @Column(unique = true, nullable = false)\n    private String isbn;\n\n" +
                        "    private boolean available = true;\n\n" +
                        "    public Book() {}\n\n" +
                        "    public Book(String title, String author, String isbn) {\n" +
                        "        this.title = title;\n        this.author = author;\n        this.isbn = isbn;\n" +
                        "        this.available = true;\n    }\n\n" +
                        "    public Long getId() { return id; }\n" +
                        "    public String getTitle() { return title; }\n    public void setTitle(String title) { this.title = title; }\n" +
                        "    public String getAuthor() { return author; }\n    public void setAuthor(String author) { this.author = author; }\n" +
                        "    public String getIsbn() { return isbn; }\n    public void setIsbn(String isbn) { this.isbn = isbn; }\n" +
                        "    public boolean isAvailable() { return available; }\n\n" +
                        "    public void borrow() {\n" +
                        "        if (!this.available) throw new IllegalStateException(\"Kitap zaten ödünç verilmiş\");\n" +
                        "        this.available = false;\n    }\n\n" +
                        "    public void returnBook() {\n        this.available = true;\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-entity","check":"classHasAnnotation('Entity')","onPass":"✅ @Entity ile Book veritabanı tablosuna eşlendi.","onFail":"❌ @Entity annotation eksik. JPA bu sınıfı tablo olarak tanıyamaz.","why":"@Entity, Hibernate'e bu sınıfın bir veritabanı tablosuna karşılık geldiğini bildirir.","alternative":"@Table(name=\\"books\\") ile tablo adını özelleştirebilirsin.","realWorld":"Kütüphane sisteminde kitap bilgileri veritabanında saklanır."},
                    {"id":"has-id","check":"field('id').hasAnnotation('Id')","onPass":"✅ @Id ile primary key tanımlı.","onFail":"❌ @Id eksik. Her entity'nin birincil anahtarı olmalıdır.","why":"Veritabanı tablolarında PK zorunludur. @Id bunu JPA'ya bildirir.","alternative":"Composite key için @EmbeddedId kullanılabilir.","realWorld":"Kitap id'si ödünç alma/iade işlemlerinde referans olarak kullanılır."},
                    {"id":"has-generated-value","check":"field('id').hasAnnotation('GeneratedValue')","onPass":"✅ @GeneratedValue ile id otomatik üretilecek.","onFail":"⚠️ @GeneratedValue eksik. id'yi elle mi atayacaksın?","why":"Otomatik id üretimi çakışma riskini sıfırlar.","alternative":"ISBN doğal anahtar olarak da kullanılabilir ama surrogate key (Long id) daha pratik.","realWorld":"Surrogate key + unique business key (isbn) yaygın bir pattern'dir."},
                    {"id":"id-private","check":"field('id').isPrivate()","onPass":"✅ id alanı private — encapsulation doğru.","onFail":"⚠️ id private olmalı.","why":"Kapsülleme: alanlar private, erişim getter/setter ile.","alternative":"","realWorld":"Tüm profesyonel projelerde alanlar private tutulur."},
                    {"id":"title-private","check":"field('title').isPrivate()","onPass":"✅ title alanı private.","onFail":"⚠️ title private olmalı.","why":"Encapsulation prensibi gereği tüm alanlar private olmalı.","alternative":"","realWorld":"Setter içinde boş string kontrolü eklenebilir."},
                    {"id":"has-borrow","check":"hasMethod('borrow')","onPass":"✅ borrow() metodu ile ödünç verme işlemi entity içinde.","onFail":"⚠️ borrow() metodu ekle — kitabın müsaitlik durumunu kontrol et.","why":"Rich domain model: iş kuralı (kitap zaten ödünçteyse tekrar verilemez) entity'de korunur.","alternative":"Serviste de yapılabilir ama entity'de yapmak domain invariant'ını korur.","realWorld":"Domain-driven design'da entity kendi iş kurallarını bilir."}
                ],"conceptsToOffer":["ENTITY","ENCAPSULATION","ID","GENERATED_VALUE"]}""");

        step0_1.setInstructionBeginner("""
                🎯 Book (Kitap) entity'sini oluştur.

                📋 Yapman gerekenler:
                1. @Entity annotation'ı ekle
                2. id alanı: Long, @Id, @GeneratedValue(strategy = GenerationType.IDENTITY)
                3. title alanı: String, @Column(nullable = false), private
                4. author alanı: String, @Column(nullable = false), private
                5. isbn alanı: String, @Column(unique = true, nullable = false), private
                6. available alanı: boolean, varsayılan değer true
                7. borrow() metodu: available false yap (ödünç verme)
                8. returnBook() metodu: available true yap (iade)
                9. borrow() içinde zaten ödünçteyse hata fırlat
                10. Getter/setter metotlarını yaz

                💡 Bilgi:
                • @Column(unique = true): ISBN her kitap için benzersizdir
                • @Column(nullable = false): Veritabanında boş olamaz
                • boolean available = true: Yeni eklenen kitap müsaittir
                • borrow()/returnBook(): Domain metotları — iş kuralı entity'de""");

        step0_1.setInstructionAdvanced(
                "Kütüphane Book entity'si oluştur. id, title, author, isbn (unique), available alanları. " +
                        "borrow() ve returnBook() domain metotları ile müsaitlik kontrolü.");

        task0.addStep(step0_1);

        // === TASK 1: Member Entity ===
        Task task1 = new Task(1, "Domain Modeli: Member Entity",
                "Kütüphane üye entity'sini oluştur",
                "ENTITY,ENCAPSULATION");

        Step step1_1 = new Step(0,
                "Member sınıfını oluştur. id, name, email (unique), membershipDate alanları olsun. " +
                        "Üye kaydı sırasında tarih otomatik atansın.",
                "// Member.java\n\npublic class Member {\n    // Alanları buraya ekle\n}",
                "@Entity\npublic class Member {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                        "    private Long id;\n\n    @Column(nullable = false)\n    private String name;\n\n" +
                        "    @Column(unique = true, nullable = false)\n    private String email;\n\n" +
                        "    @Column(name = \"membership_date\", nullable = false)\n    private LocalDate membershipDate;\n\n" +
                        "    public Member() {}\n\n" +
                        "    public Member(String name, String email) {\n" +
                        "        this.name = name;\n        this.email = email;\n" +
                        "        this.membershipDate = LocalDate.now();\n    }\n\n" +
                        "    public Long getId() { return id; }\n" +
                        "    public String getName() { return name; }\n    public void setName(String name) { this.name = name; }\n" +
                        "    public String getEmail() { return email; }\n    public void setEmail(String email) { this.email = email; }\n" +
                        "    public LocalDate getMembershipDate() { return membershipDate; }\n}",
                """
                {"astRules":[
                    {"id":"has-entity","check":"classHasAnnotation('Entity')","onPass":"✅ @Entity ile Member tablosu oluşacak.","onFail":"❌ @Entity eksik.","why":"JPA yönetimi için zorunlu.","alternative":"","realWorld":"Kütüphane üyeleri veritabanında saklanır."},
                    {"id":"has-id","check":"field('id').hasAnnotation('Id')","onPass":"✅ @Id ile primary key tanımlı.","onFail":"❌ @Id eksik.","why":"Her entity'de PK zorunludur.","alternative":"","realWorld":"Üye ID'si ödünç işlemlerinde referans olarak kullanılır."},
                    {"id":"name-private","check":"field('name').isPrivate()","onPass":"✅ name alanı private.","onFail":"⚠️ name private olmalı — encapsulation.","why":"Kontrollü erişim için alanlar private olmalı.","alternative":"","realWorld":"Setter içinde boşluk kontrolü eklenebilir."},
                    {"id":"email-private","check":"field('email').isPrivate()","onPass":"✅ email alanı private.","onFail":"⚠️ email private olmalı.","why":"Email hassas veridir, kapsüllenmelidir.","alternative":"","realWorld":"Email alanına @Email validation eklenebilir."},
                    {"id":"has-getter-name","check":"hasMethod('getName')","onPass":"✅ getName() getter mevcut.","onFail":"⚠️ getName() getter eksik.","why":"Private alanlara erişim için getter gerekir.","alternative":"Lombok @Getter ile otomatik üretebilirsin.","realWorld":"JSON serialization getter'lara bağımlıdır."},
                    {"id":"has-this","check":"codeContains('this.')","onPass":"✅ this anahtar kelimesi doğru kullanılmış.","onFail":"⚠️ Constructor'da this.name = name; kullanımı bekleniyor.","why":"Parametre adı ile alan adı aynı olduğunda this ile ayrım yapılır.","alternative":"","realWorld":"Java'da standart pratik."}
                ],"conceptsToOffer":["ENTITY","ENCAPSULATION"]}""");

        step1_1.setInstructionBeginner("""
                🎯 Member (Üye) entity'sini oluştur.

                📋 Yapman gerekenler:
                1. @Entity annotation'ı ekle
                2. id: Long, @Id, @GeneratedValue
                3. name: String, @Column(nullable = false), private
                4. email: String, @Column(unique = true, nullable = false), private
                5. membershipDate: LocalDate, @Column(name = "membership_date")
                6. Constructor'da membershipDate'i LocalDate.now() ile ata
                7. Getter/setter metotlarını yaz
                8. this anahtar kelimesini kullan

                💡 Bilgi:
                • LocalDate: Java 8+ tarih sınıfı — sadece tarih (saat yok)
                • LocalDate.now(): Bugünün tarihini verir
                • @Column(unique = true): Aynı email ile iki üye kaydı engellenır
                • membershipDate için setter yazmana gerek yok — otomatik atanıyor""");

        step1_1.setInstructionAdvanced(
                "Member entity: id, name, email (unique), membershipDate (LocalDate.now() ile otomatik). " +
                        "Encapsulation kuralları, constructor ile this kullanımı.");

        task1.addStep(step1_1);

        // === TASK 2: BookRepository ===
        Task task2 = new Task(2, "Repository Katmanı: BookRepository",
                "Kitap repository'sini oluştur",
                "INTERFACE");

        Step step2_1 = new Step(0,
                "BookRepository ve MemberRepository interface'lerini oluştur. JpaRepository'den extend et. " +
                        "ISBN'e göre, yazara göre ve müsait kitapları bulan metotlar ekle.",
                "// BookRepository.java\n\npublic interface BookRepository {\n    // JpaRepository'den extend et\n}",
                "public interface BookRepository extends JpaRepository<Book, Long> {\n\n" +
                        "    Optional<Book> findByIsbn(String isbn);\n\n" +
                        "    List<Book> findByAuthorContainingIgnoreCase(String author);\n\n" +
                        "    List<Book> findByTitleContainingIgnoreCase(String title);\n\n" +
                        "    List<Book> findByAvailableTrue();\n}\n\npublic interface MemberRepository extends JpaRepository<Member, Long> {}",
                """
                {"astRules":[
                    {"id":"extends-jpa","check":"codeContains('JpaRepository')","onPass":"✅ JpaRepository doğru extend edilmiş. save, findById, findAll otomatik gelir.","onFail":"❌ JpaRepository'den extend etmelisin.","why":"Spring Data JPA tüm temel CRUD metotlarını sağlar.","alternative":"CrudRepository daha minimal bir alternatif.","realWorld":"Repository pattern: veri erişim katmanını soyutlar."},
                    {"id":"is-interface","check":"codeContains('interface')","onPass":"✅ Repository bir interface olarak tanımlanmış.","onFail":"❌ Repository interface olmalı, class değil!","why":"Spring Data, interface'den otomatik implementasyon üretir.","alternative":"","realWorld":"Tek satır SQL yazmadan tüm CRUD işlemleri yapılabilir."},
                    {"id":"has-find-by-isbn","check":"hasMethod('findByIsbn')","onPass":"✅ findByIsbn metodu tanımlı — ISBN ile kitap arama.","onFail":"⚠️ findByIsbn metodu ekle — ISBN ile benzersiz kitap bulma.","why":"ISBN her kitap için benzersizdir — doğrudan erişim sağlar.","alternative":"@Query ile de yazılabilir ama derived query yeterli.","realWorld":"Kütüphane sistemlerinde ISBN temel arama kriteridir."},
                    {"id":"has-find-available","check":"hasMethod('findByAvailableTrue')","onPass":"✅ findByAvailableTrue ile müsait kitaplar bulunabilir.","onFail":"⚠️ findByAvailableTrue metodu ekle — müsait kitapları listele.","why":"Kullanıcılar sadece ödünç alabilecekleri kitapları görmek ister.","alternative":"@Query ile daha karmaşık filtre de eklenebilir.","realWorld":"Kütüphane kataloğunda müsaitlik filtresi temel özelliktir."}
                ],"conceptsToOffer":["INTERFACE"]}""");

        step2_1.setInstructionBeginner("""
                🎯 BookRepository interface'ini oluştur.

                📋 Yapman gerekenler:
                1. interface olarak tanımla (class değil!)
                2. JpaRepository<Book, Long> extend et
                3. findByIsbn(String isbn) → ISBN ile kitap bul (Optional<Book>)
                4. findByAuthorContainingIgnoreCase(String author) → yazara göre ara
                5. findByTitleContainingIgnoreCase(String title) → başlığa göre ara
                6. findByAvailableTrue() → müsait kitapları listele

                💡 Bilgi:
                • JpaRepository extend ettiğinde save, findById, findAll, delete otomatik gelir
                • Derived query: metot adından SQL üretilir
                • ContainingIgnoreCase = LIKE %keyword% (büyük/küçük harf duyarsız)
                • AvailableTrue = WHERE available = true
                • Optional<Book> → kitap bulunamazsa empty döner, NullPointerException riski yok""");

        step2_1.setInstructionAdvanced(
                "BookRepository interface: JpaRepository extend, findByIsbn, findByAuthor/Title (Containing), " +
                        "findByAvailableTrue derived query metotları.");

        task2.addStep(step2_1);

        // === TASK 3: BorrowService ===
        Task task3 = new Task(3, "Service Katmanı: BorrowService",
                "Ödünç alma/iade işlemleri için service oluştur",
                "DEPENDENCY_INJECTION,TRANSACTION_MANAGEMENT");

        Step step3_1 = new Step(0,
                "BorrowService sınıfını oluştur. Constructor injection ile repository'leri enjekte et. " +
                        "borrowBook ve returnBook metotlarını yaz. @Transactional ile transaction yönetimi sağla.",
                "@Service\npublic class BorrowService {\n    // Repository'leri enjekte et\n" +
                        "    // borrowBook, returnBook metotlarını yaz\n}",
                "@Service\npublic class BorrowService {\n\n" +
                        "    private final BookRepository bookRepository;\n" +
                        "    private final MemberRepository memberRepository;\n\n" +
                        "    public BorrowService(BookRepository bookRepository,\n" +
                        "                         MemberRepository memberRepository) {\n" +
                        "        this.bookRepository = bookRepository;\n" +
                        "        this.memberRepository = memberRepository;\n    }\n\n" +
                        "    @Transactional\n    public void borrowBook(Long memberId, Long bookId) {\n" +
                        "        Member member = memberRepository.findById(memberId)\n" +
                        "            .orElseThrow(() -> new RuntimeException(\"Üye bulunamadı: \" + memberId));\n" +
                        "        Book book = bookRepository.findById(bookId)\n" +
                        "            .orElseThrow(() -> new RuntimeException(\"Kitap bulunamadı: \" + bookId));\n\n" +
                        "        book.borrow();\n        bookRepository.save(book);\n    }\n\n" +
                        "    @Transactional\n    public void returnBook(Long bookId) {\n" +
                        "        Book book = bookRepository.findById(bookId)\n" +
                        "            .orElseThrow(() -> new RuntimeException(\"Kitap bulunamadı: \" + bookId));\n\n" +
                        "        book.returnBook();\n        bookRepository.save(book);\n    }\n\n" +
                        "    @Transactional(readOnly = true)\n    public List<Book> findAvailableBooks() {\n" +
                        "        return bookRepository.findByAvailableTrue();\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-service","check":"classHasAnnotation('Service')","onPass":"✅ @Service annotation doğru.","onFail":"❌ @Service eksik. Spring bu sınıfı bean olarak tanıyamaz.","why":"@Service ile Spring IoC container'a kaydedilir.","alternative":"@Component da çalışır ama @Service semantik olarak daha uygun.","realWorld":"Service katmanı iş mantığını barındırır."},
                    {"id":"has-constructor","check":"hasConstructor()","onPass":"✅ Constructor injection ile bağımlılıklar enjekte edilmiş.","onFail":"❌ Constructor injection eksik.","why":"Constructor injection: immutable, test edilebilir, bağımlılıklar açık.","alternative":"@Autowired field injection da çalışır ama önerilmez.","realWorld":"Spring ekibi constructor injection'ı resmi olarak önerir."},
                    {"id":"has-transactional","check":"codeContains('@Transactional')","onPass":"✅ @Transactional ile transaction yönetimi sağlanmış.","onFail":"⚠️ @Transactional eksik. Ödünç alma işlemi atomik olmalı.","why":"borrowBook: üye kontrol + kitap kontrol + durum güncelle → tek transaction olmalı.","alternative":"","realWorld":"Transaction olmadan yarım kalmış işlemler veri tutarsızlığına yol açar."},
                    {"id":"has-borrow","check":"hasMethod('borrowBook')","onPass":"✅ borrowBook metodu tanımlı.","onFail":"❌ borrowBook metodu eksik.","why":"Ödünç alma temel iş mantığıdır.","alternative":"","realWorld":"Kütüphane sisteminin en temel işlemi."},
                    {"id":"has-return","check":"hasMethod('returnBook')","onPass":"✅ returnBook metodu tanımlı.","onFail":"❌ returnBook metodu eksik.","why":"İade işlemi ödünç almanın tamamlayıcısıdır.","alternative":"","realWorld":"İade edilmeyen kitaplar için gecikme ücreti sistemi eklenebilir."}
                ],"conceptsToOffer":["DEPENDENCY_INJECTION","TRANSACTION_MANAGEMENT"]}""");

        step3_1.setInstructionBeginner("""
                🎯 BorrowService (Ödünç Alma Servisi) oluştur.

                📋 Yapman gerekenler:
                1. @Service annotation'ı ekle
                2. BookRepository ve MemberRepository'yi constructor injection ile enjekte et
                3. borrowBook(Long memberId, Long bookId) metodu yaz:
                   - Üyeyi ve kitabı veritabanından bul
                   - Bulunamazsa RuntimeException fırlat
                   - book.borrow() çağır (domain metodu)
                   - Kitabı kaydet
                4. returnBook(Long bookId) metodu yaz:
                   - Kitabı bul, book.returnBook() çağır, kaydet
                5. findAvailableBooks() → müsait kitapları listele
                6. Write metotlarına @Transactional, read'e @Transactional(readOnly = true)

                💡 Bilgi:
                • Constructor Injection: private final + constructor parametresi
                • @Transactional: Hata olursa tüm değişiklikleri geri al
                • orElseThrow(): Optional boşsa exception fırlat
                • Domain metotları (borrow/returnBook) entity içinde — service koordine eder""");

        step3_1.setInstructionAdvanced(
                "BorrowService: constructor injection (BookRepository + MemberRepository), " +
                        "borrowBook ve returnBook metotları (@Transactional), findAvailableBooks (readOnly). " +
                        "Domain metotlarını (book.borrow()) çağır.");

        task3.addStep(step3_1);

        // === TASK 4: BorrowController ===
        Task task4 = new Task(4, "REST API: BorrowController",
                "Ödünç alma/iade endpoint'lerini oluştur",
                "REST_BEST_PRACTICES,DEPENDENCY_INJECTION");

        Step step4_1 = new Step(0,
                "BorrowController sınıfını oluştur. Ödünç alma, iade ve müsait kitapları listeleme endpoint'lerini yaz.",
                "@RestController\n@RequestMapping(\"/api/borrows\")\npublic class BorrowController {\n" +
                        "    // Service'i enjekte et ve endpoint'leri yaz\n}",
                "@RestController\n@RequestMapping(\"/api/borrows\")\npublic class BorrowController {\n\n" +
                        "    private final BorrowService borrowService;\n\n" +
                        "    public BorrowController(BorrowService borrowService) {\n" +
                        "        this.borrowService = borrowService;\n    }\n\n" +
                        "    @PostMapping\n" +
                        "    public ResponseEntity<String> borrowBook(@RequestParam Long memberId,\n" +
                        "                                              @RequestParam Long bookId) {\n" +
                        "        borrowService.borrowBook(memberId, bookId);\n" +
                        "        return ResponseEntity.ok(\"Kitap başarıyla ödünç verildi\");\n    }\n\n" +
                        "    @PostMapping(\"/return\")\n" +
                        "    public ResponseEntity<String> returnBook(@RequestParam Long bookId) {\n" +
                        "        borrowService.returnBook(bookId);\n" +
                        "        return ResponseEntity.ok(\"Kitap başarıyla iade edildi\");\n    }\n\n" +
                        "    @GetMapping(\"/available-books\")\n" +
                        "    public ResponseEntity<List<Book>> availableBooks() {\n" +
                        "        return ResponseEntity.ok(borrowService.findAvailableBooks());\n    }\n}",
                """
                {"astRules":[
                    {"id":"has-rest-controller","check":"classHasAnnotation('RestController')","onPass":"✅ @RestController doğru — JSON response otomatik.","onFail":"❌ @RestController eksik.","why":"@RestController = @Controller + @ResponseBody.","alternative":"@Controller + @ResponseBody da kullanılabilir.","realWorld":"REST API'lerde standart."},
                    {"id":"has-request-mapping","check":"codeContains('@RequestMapping')","onPass":"✅ Base path tanımlı.","onFail":"⚠️ @RequestMapping ile base path tanımla.","why":"Tüm endpoint'ler /api/borrows altında gruplanır.","alternative":"","realWorld":"Resource-based URL yapısı REST standardıdır."},
                    {"id":"has-post-mapping","check":"codeContains('@PostMapping')","onPass":"✅ POST endpoint tanımlı — ödünç alma/iade state-changing işlemler.","onFail":"❌ @PostMapping ile ödünç alma endpoint'i tanımla.","why":"Ödünç alma durum değiştiren bir işlemdir → POST kullanılır.","alternative":"","realWorld":"GET ile state değiştirmek REST anti-pattern'dir."},
                    {"id":"has-get-mapping","check":"codeContains('@GetMapping')","onPass":"✅ GET endpoint tanımlı — müsait kitapları listeleme.","onFail":"⚠️ @GetMapping ile listeleme endpoint'i ekle.","why":"Müsait kitapları okumak safe bir işlemdir → GET kullanılır.","alternative":"","realWorld":"GET idempotent ve safe: server state değişmez."},
                    {"id":"has-constructor","check":"hasConstructor()","onPass":"✅ Constructor injection ile service enjekte edilmiş.","onFail":"❌ Constructor injection eksik.","why":"Controller → Service bağımlılığı DI ile yönetilir.","alternative":"","realWorld":"Katmanlı mimari: Controller → Service → Repository."}
                ],"conceptsToOffer":["REST_BEST_PRACTICES","DEPENDENCY_INJECTION"]}""");

        step4_1.setInstructionBeginner("""
                🎯 BorrowController REST API'sini oluştur.

                📋 Yapman gerekenler:
                1. @RestController ve @RequestMapping("/api/borrows") ekle
                2. POST /api/borrows → ödünç alma (memberId, bookId parametreleri)
                3. POST /api/borrows/return → iade (bookId parametresi)
                4. GET /api/borrows/available-books → müsait kitapları listele
                5. BorrowService'i constructor injection ile enjekte et
                6. ResponseEntity ile uygun yanıtları dön

                💡 REST Best Practices:
                • POST = durum değiştiren işlem (ödünç alma, iade)
                • GET = sadece okuma (listeleme)
                • ResponseEntity ile HTTP status kodu kontrol et
                • @RequestParam ile query parameter al
                • Base path'i resource ismi ile tanımla: /api/borrows""");

        step4_1.setInstructionAdvanced(
                "BorrowController: POST (borrow, return), GET (available-books). " +
                        "Constructor injection, ResponseEntity, @RequestParam.");

        task4.addStep(step4_1);

        library.addTask(task0);
        library.addTask(task1);
        library.addTask(task2);
        library.addTask(task3);
        library.addTask(task4);

        Project savedProject = saveCurriculum(library);
        seedLibraryHints(savedProject);
    }

    private void seedLibraryHints(Project project) {
        for (Task task : project.getTasks()) {
            for (Step step : task.getSteps()) {
                Long stepId = step.getId();

                switch (task.getOrderIndex()) {
                    case 0 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "@Entity annotation'ını unutma. ISBN için @Column(unique = true) kullan. " +
                                        "available alanını true olarak başlat."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@Entity ile sınıfı işaretle. @Id + @GeneratedValue ile id tanımla. " +
                                        "isbn alanına @Column(unique = true, nullable = false) ekle. " +
                                        "borrow() metodu: available false yapmalı, zaten ödünçteyse exception fırlat. " +
                                        "returnBook(): available true yap."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Entity\npublic class Book {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                                        "    private Long id;\n    private String title;\n    private String isbn;\n" +
                                        "    private boolean available = true;\n\n" +
                                        "    public void borrow() {\n" +
                                        "        if (!available) throw new IllegalStateException(\"Zaten ödünç\");\n" +
                                        "        this.available = false;\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 1 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "Email alanı unique olmalı: @Column(unique = true). " +
                                        "membershipDate'i constructor'da LocalDate.now() ile ata."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@Entity ile işaretle. email: @Column(unique = true, nullable = false). " +
                                        "Constructor(String name, String email) içinde this.membershipDate = LocalDate.now(). " +
                                        "Getter/setter yaz, membershipDate için setter gereksiz."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Entity\npublic class Member {\n    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                                        "    private Long id;\n    @Column(nullable = false) private String name;\n" +
                                        "    @Column(unique = true) private String email;\n" +
                                        "    private LocalDate membershipDate;\n\n" +
                                        "    public Member(String name, String email) {\n" +
                                        "        this.name = name; this.email = email;\n" +
                                        "        this.membershipDate = LocalDate.now();\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 2 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "interface olarak tanımla. JpaRepository<Book, Long> extend et. " +
                                        "Metot adlandırma kuralı ile custom sorgu metotları yaz."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "findByIsbn(String isbn) → ISBN ile bul. " +
                                        "findByAuthorContainingIgnoreCase → yazara göre ara. " +
                                        "findByAvailableTrue() → müsait kitaplar."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "public interface BookRepository extends JpaRepository<Book, Long> {\n" +
                                        "    Optional<Book> findByIsbn(String isbn);\n" +
                                        "    List<Book> findByAvailableTrue();\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 3 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "@Service annotation'ı ve constructor injection ile başla. " +
                                        "borrowBook ve returnBook metotlarına @Transactional ekle."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "BookRepository ve MemberRepository'yi constructor'da al. " +
                                        "borrowBook: findById ile üye ve kitabı bul, book.borrow() çağır, save(). " +
                                        "returnBook: findById + book.returnBook() + save(). " +
                                        "@Transactional ile transaction yönetimi."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@Service\npublic class BorrowService {\n" +
                                        "    private final BookRepository bookRepo;\n\n" +
                                        "    public BorrowService(BookRepository bookRepo) { this.bookRepo = bookRepo; }\n\n" +
                                        "    @Transactional\n    public void borrowBook(Long memberId, Long bookId) {\n" +
                                        "        Book book = bookRepo.findById(bookId).orElseThrow(...);\n" +
                                        "        book.borrow();\n        bookRepo.save(book);\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                    case 4 -> {
                        saveHint(new Hint(stepId, HintLevel.SMALL,
                                "@RestController + @RequestMapping(\"/api/borrows\"). " +
                                        "POST ile ödünç alma/iade, GET ile müsait kitap listeleme."));
                        saveHint(new Hint(stepId, HintLevel.GUIDE,
                                "@PostMapping → borrowBook(memberId, bookId), returnBook(bookId). " +
                                        "@GetMapping(\"/available-books\") → müsait kitap listesi. " +
                                        "ResponseEntity ile uygun yanıtlar dön."));
                        saveHint(new Hint(stepId, HintLevel.CODE,
                                "@RestController\n@RequestMapping(\"/api/borrows\")\npublic class BorrowController {\n\n" +
                                        "    @PostMapping\n    public ResponseEntity<String> borrow(@RequestParam Long memberId,\n" +
                                        "                                                          @RequestParam Long bookId) {\n" +
                                        "        borrowService.borrowBook(memberId, bookId);\n" +
                                        "        return ResponseEntity.ok(\"Ödünç verildi\");\n    }\n}"));
                        saveHint(new Hint(stepId, HintLevel.SOLUTION, step.getSolutionCode()));
                    }
                }
            }
        }
    }

    private Project saveCurriculum(Project incoming) {
        CurriculumSources.prepare(incoming);
        Project existing = projectRepository.findByCode(incoming.getCode()).orElse(null);
        if (existing == null) return rememberSolutions(projectRepository.save(incoming));
        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setMinLevel(incoming.getMinLevel());
        // Match stable task/step positions: never delete progress or recreate existing IDs.
        for (Task source : incoming.getTasks()) {
            Task target = existing.getTasks().stream().filter(t -> t.getOrderIndex() == source.getOrderIndex()).findFirst().orElse(null);
            if (target == null) { existing.addTask(source); continue; }
            target.setTitle(source.getTitle()); target.setObjective(source.getObjective()); target.setConceptCodes(source.getConceptCodes());
            for (Step sourceStep : source.getSteps()) {
                Step targetStep = target.getSteps().stream().filter(t -> t.getOrderIndex() == sourceStep.getOrderIndex()).findFirst().orElse(null);
                if (targetStep == null) { target.addStep(sourceStep); continue; }
                targetStep.setInstruction(sourceStep.getInstruction());
                targetStep.setInstructionBeginner(sourceStep.getInstructionBeginner());
                targetStep.setInstructionAdvanced(sourceStep.getInstructionAdvanced());
                targetStep.setStarterCode(sourceStep.getStarterCode());
                targetStep.setSolutionCode(sourceStep.getSolutionCode());
                targetStep.setValidationSpec(sourceStep.getValidationSpec());
            }
        }
        return rememberSolutions(projectRepository.save(existing));
    }

    private Project rememberSolutions(Project project) {
        for (Task task : project.getTasks()) for (Step step : task.getSteps()) referenceSolutions.put(step.getId(), step.getSolutionCode());
        return project;
    }

    private void saveHint(Hint incoming) {
        if (incoming.getHintLevel() == HintLevel.CODE && referenceSolutions.containsKey(incoming.getStepId())) {
            incoming.setContent(CurriculumSources.codeHint(referenceSolutions.get(incoming.getStepId())));
        }
        Hint existing = hintRepository.findByStepIdAndHintLevel(incoming.getStepId(), incoming.getHintLevel()).orElse(incoming);
        existing.setContent(incoming.getContent());
        hintRepository.save(existing);
    }

    private void seedTaskTrackerProject() {
        Project project = saveCurriculum(TaskTrackerCurriculum.create());
        for (Task task : project.getTasks()) for (Step step : task.getSteps()) {
            saveHint(new Hint(step.getId(), HintLevel.SMALL, "Önce bu sınıfın tek sorumluluğunu düşün. Hangi veriyi koruyor, hangi davranışı sunuyor?"));
            saveHint(new Hint(step.getId(), HintLevel.GUIDE, step.getInstruction()));
            saveHint(new Hint(step.getId(), HintLevel.CODE, step.getStarterCode() + "\n// Önce bir metodu tamamla; sonra kontrol listesindeki diğerlerine geç."));
            saveHint(new Hint(step.getId(), HintLevel.SOLUTION, step.getSolutionCode()));
        }
    }
}
