const MOCK_USER = {
  email: 'demo@jsy.com',
  displayName: 'Demo Kullanici',
  accessToken: 'mock-token-12345',
  refreshToken: 'mock-refresh-12345',
}

const ASSESSMENT_QUESTIONS = [
  { id: 0, question: "Java'da 'private' erisim belirleyicisi ne ise yarar?", options: ["Sinifi herkese acar", "Sadece ayni sinif icinden erisim saglar", "Sadece alt siniflardan erisim saglar", "Ayni paketteki siniflardan erisim saglar"], topic: "OOP" },
  { id: 1, question: "'this' anahtar kelimesi ne anlama gelir?", options: ["Ust sinifi referans eder", "Statik metodu cagirir", "Mevcut nesneyi (instance) referans eder", "Yeni bir nesne olusturur"], topic: "THIS_SUPER" },
  { id: 2, question: "'super()' ne zaman kullanilir?", options: ["Ayni sinifin baska constructor'ini cagirmak icin", "Ust sinifin constructor'ini cagirmak icin", "Static metot cagirmak icin", "Nesneyi silmek icin"], topic: "THIS_SUPER" },
  { id: 3, question: "Interface ile abstract class arasindaki temel fark nedir?", options: ["Fark yoktur", "Interface coklu kalitimi destekler, abstract class desteklemez", "Abstract class instance olusturulabilir", "Interface'de constructor olabilir"], topic: "INTERFACE_ABSTRACT" },
  { id: 4, question: "Spring'de @Autowired ne ise yarar?", options: ["Veritabani baglantisi kurar", "Otomatik bagimlilik enjeksiyonu yapar", "REST endpoint tanimlar", "Transaction baslatir"], topic: "DI_BEAN" },
  { id: 5, question: "Spring Bean lifecycle'da hangi asama yoktur?", options: ["Instantiation", "Dependency Injection", "Compilation", "Destruction"], topic: "DI_BEAN" },
  { id: 6, question: "@Entity annotation'i ne ise yarar?", options: ["REST controller tanimlar", "Sinifi bir veritabani tablosuna esler", "Security filtresi ekler", "Unit test belirtir"], topic: "JPA" },
  { id: 7, question: "JPA'da @GeneratedValue(strategy = GenerationType.IDENTITY) ne yapar?", options: ["Alani nullable yapar", "Primary key'i veritabaninin otomatik uretmesini saglar", "Foreign key tanimlar", "Index olusturur"], topic: "JPA" },
  { id: 8, question: "JWT token'da hangi bilgi bulunmaz?", options: ["Kullanici kimligi", "Token suresi", "Veritabani sifresi", "Imza"], topic: "SECURITY" },
  { id: 9, question: "JUnit'te @BeforeEach ne ise yarar?", options: ["Tum testlerden sonra calisir", "Her testten once calisir", "Sadece ilk testten once calisir", "Test'i devre disi birakir"], topic: "TEST" },
  { id: 10, question: "@Transactional annotation'i ne saglar?", options: ["REST endpoint olusturur", "Veritabani islemlerini atomik yapar (ya hepsi ya hicbiri)", "Sinifi singleton yapar", "Cache aktif eder"], topic: "TRANSACTION" },
  { id: 11, question: "Spring'de @RestController ile @Controller farki nedir?", options: ["Fark yoktur", "@RestController otomatik @ResponseBody ekler (JSON doner)", "@Controller sadece GET isteklerini alir", "@RestController sadece POST alir"], topic: "REST" },
]

const CORRECT_ANSWERS = [1, 2, 1, 1, 1, 2, 1, 1, 2, 1, 1, 1]

const PROJECTS = [
  { id: 1, code: 'BANK', title: 'Banka Uygulamasi', description: 'Gercek bir banka uygulamasi gelistirerek Java ve Spring Boot\'un temel kavramlarini ogren. Kullanici yonetimi, hesap islemleri, para transferi ve guvenlik konularini kapsayan kapsamli bir proje.', minLevel: 'BEGINNER', taskCount: 8 },
  { id: 2, code: 'ECOMMERCE', title: 'E-ticaret Sistemi', description: 'Urun katalogu, kategori yonetimi, siparis takibi iceren kapsamli bir e-ticaret uygulamasi. Spring Boot ile profesyonel bir backend gelistirmeyi ogreneceksin.', minLevel: 'INTERMEDIATE', taskCount: 6 },
  { id: 3, code: 'LIBRARY', title: 'Kutuphane Yonetim Sistemi', description: 'Kitap, uye ve odunc islemlerini yoneten bir kutuphane otomasyon sistemi. Temel Java ve Spring Boot kavramlarini pratikte ogreneceksin.', minLevel: 'BEGINNER', taskCount: 5 },
]

const BANK_TASKS = [
  {
    id: 1, orderIndex: 0, title: 'Domain Modeli: User Entity',
    objective: 'Banka uygulamasinin temel kullanici entity\'sini olustur',
    conceptCodes: 'ENTITY,ID,GENERATED_VALUE,ENCAPSULATION,THIS_KEYWORD',
    steps: [{
      id: 1, orderIndex: 0, status: 'ACTIVE', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} User sinifini bir JPA entity olarak olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. Sinifin uzerine @Entity annotation\'i ekle\n2. id alani olustur (Long tipinde, @Id ve @GeneratedValue ile)\n3. name alani olustur (String tipinde, private)\n4. email alani olustur (String tipinde, private)\n5. Getter ve setter metotlarini yaz\n6. Constructor\'da this anahtar kelimesini kullan\n\n\u{1F4A1} Bilgi:\n\u2022 @Entity: Bu sinifi veritabani tablosuna baglar\n\u2022 @Id: Birincil anahtari (primary key) belirtir\n\u2022 @GeneratedValue: id\'nin otomatik uretilmesini saglar\n\u2022 private: Alanlari disaridan dogrudan erisime kapatir (Encapsulation)\n\u2022 this.name = name: \'Benim alanima parametre degerini ata\' demektir\n\u2022 super(): Ust sinifin constructor\'ini cagirir (simdilik gerekli degil)\n\nHer annotation ve her satirin ne ise yaradigini merak et \u2014 sistem sana aciklayacak!',
      starterCode: '// User.java dosyasini olustur\n// Gerekli alanlar: id, name, email\n\npublic class User {\n    // Alanlari buraya ekle\n}'
    }]
  },
  {
    id: 2, orderIndex: 1, title: 'Domain Modeli: Account Entity',
    objective: 'Banka hesap entity\'sini olustur ve User ile iliskilendir',
    conceptCodes: 'ENTITY,ENCAPSULATION',
    steps: [{
      id: 2, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} Account (Hesap) entity\'sini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Entity annotation\'i ekle\n2. id, accountNumber (String), balance (double) alanlarini olustur\n3. User ile @ManyToOne iliskisi kur (bir kullanicinin cok hesabi olabilir)\n4. deposit(double amount) \u2192 para yatirma metodu\n5. withdraw(double amount) \u2192 para cekme metodu (bakiye kontrolu!)\n6. balance alani private olmali \u2014 sadece metotlarla degismeli\n\n\u{1F4A1} Onemli kavram: Domain Driven Design\'da is kurallari entity icinde yazilir.\nBakiyenin negatife dusmemesi bir \'invariant\'tir \u2014 bunu entity korumali.',
      starterCode: '// Account.java dosyasini olustur\n\npublic class Account {\n    // id, accountNumber, balance alanlarini ekle\n    // User iliskisi kur (@ManyToOne)\n    // deposit ve withdraw metotlarini yaz\n}'
    }]
  },
  {
    id: 3, orderIndex: 2, title: 'Repository Katmani',
    objective: 'Spring Data JPA repository\'lerini olustur',
    conceptCodes: 'INTERFACE,DEPENDENCY_INJECTION',
    steps: [{
      id: 3, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} UserRepository ve AccountRepository interface\'lerini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. UserRepository interface\'ini olustur\n2. JpaRepository<User, Long> extend et\n3. findByEmail(String email) metodu ekle\n4. AccountRepository icin de ayni sekilde yap\n5. findByUserId ve findByAccountNumber metotlari ekle\n\n\u{1F4A1} Bilgi:\n\u2022 JpaRepository extend edince save, findById, findAll, delete otomatik gelir\n\u2022 Spring metot adindan SQL uretir: findByEmail \u2192 SELECT * FROM user WHERE email = ?',
      starterCode: '// UserRepository.java\n\npublic interface UserRepository {\n    // JpaRepository\'den extend et\n    // findByEmail metodu ekle\n}'
    }]
  },
  {
    id: 4, orderIndex: 3, title: 'Service Katmani: Is Mantigi',
    objective: 'AccountService ile para yatirma/cekme/transfer islemleri',
    conceptCodes: 'DEPENDENCY_INJECTION,ENCAPSULATION',
    steps: [{
      id: 4, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} AccountService sinifini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Service annotation\'i ekle\n2. AccountRepository\'yi constructor injection ile enjekte et\n3. deposit(String accountNumber, double amount) metodu yaz\n4. withdraw(String accountNumber, double amount) metodu yaz\n5. transfer(String from, String to, double amount) metodu yaz\n6. @Transactional annotation\'ini islem metotlarina ekle',
      starterCode: '@Service\npublic class AccountService {\n    // Repository\'yi constructor injection ile enjekte et\n    // deposit, withdraw, transfer metotlarini yaz\n}'
    }]
  },
  {
    id: 5, orderIndex: 4, title: 'REST API: Controller Katmani',
    objective: 'AccountController ile REST endpoint\'leri olustur',
    conceptCodes: 'DEPENDENCY_INJECTION',
    steps: [{
      id: 5, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} AccountController sinifini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @RestController ve @RequestMapping("/api/accounts") ekle\n2. AccountService\'i constructor injection ile enjekte et\n3. @PostMapping ile deposit endpoint\'i yaz\n4. @PostMapping ile withdraw endpoint\'i yaz',
      starterCode: '@RestController\n@RequestMapping("/api/accounts")\npublic class AccountController {\n    // Service\'i enjekte et ve endpoint\'leri yaz\n}'
    }]
  },
  {
    id: 6, orderIndex: 5, title: 'Exception Handling',
    objective: 'Global exception handler ve ozel exception\'lar olustur',
    conceptCodes: 'ENCAPSULATION',
    steps: [{
      id: 6, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} Custom exception ve GlobalExceptionHandler olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. AccountNotFoundException olustur (RuntimeException extend et)\n2. @RestControllerAdvice ile GlobalExceptionHandler olustur\n3. @ExceptionHandler ile her exception tipine ozel HTTP yaniti don',
      starterCode: '// AccountNotFoundException ve GlobalExceptionHandler olustur\n\npublic class AccountNotFoundException extends RuntimeException {\n    // constructor yaz\n}'
    }]
  },
  {
    id: 7, orderIndex: 6, title: 'Validation (Dogrulama)',
    objective: 'DTO\'lara Jakarta validation annotation\'lari ekle',
    conceptCodes: 'ENCAPSULATION',
    steps: [{
      id: 7, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} DepositRequest DTO\'su olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. amount alani ekle (Double tipinde)\n2. @NotNull ve @Positive annotation\'larini ekle\n3. Getter/setter yaz',
      starterCode: '// DepositRequest.java\n\npublic class DepositRequest {\n    // amount alani ve validasyon annotation\'lari ekle\n}'
    }]
  },
  {
    id: 8, orderIndex: 7, title: 'Test Yazimi: JUnit',
    objective: 'AccountService icin unit test yaz',
    conceptCodes: 'INTERFACE,DEPENDENCY_INJECTION',
    steps: [{
      id: 8, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} AccountService icin JUnit testleri yaz.\n\n\u{1F4CB} Yapman gerekenler:\n1. @SpringBootTest annotation\'i ekle\n2. @Test ile deposit testi yaz\n3. Negatif tutar icin exception testi yaz (assertThrows)\n4. Her testte en az bir assertion olsun',
      starterCode: '@SpringBootTest\npublic class AccountServiceTest {\n    // Test metotlarini yaz\n}'
    }]
  },
]

const ECOMMERCE_TASKS = [
  {
    id: 9, orderIndex: 0, title: 'Domain Modeli: Product Entity',
    objective: 'E-ticaret uygulamasinin temel urun entity\'sini olustur',
    conceptCodes: 'ENTITY,ID,GENERATED_VALUE,ENCAPSULATION,JPA_RELATIONSHIPS',
    steps: [{
      id: 9, orderIndex: 0, status: 'ACTIVE', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} Product sinifini bir JPA entity olarak olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Entity annotation\'i ekle\n2. id alani (Long, @Id, @GeneratedValue)\n3. name alani (String, private, @Column(nullable = false))\n4. description alani (String, private)\n5. price alani (BigDecimal, private) - para degerleri icin BigDecimal kullan\n6. stockQuantity alani (int, private)\n7. Getter/setter metotlarini yaz\n8. decreaseStock(int quantity) metodu ekle - stok kontrolu yap\n\n\u{1F4A1} Bilgi:\n\u2022 BigDecimal: Para hesaplamalarinda double yerine kullanilir (hassasiyet)\n\u2022 @Column(nullable = false): Veritabaninda NOT NULL kisitlamasi ekler\n\u2022 Stok azaltma islemi entity icinde olmali (DDD - domain invariant)',
      starterCode: '// Product.java\n\npublic class Product {\n    // id, name, description, price, stockQuantity alanlarini ekle\n    // decreaseStock metodu yaz\n}'
    }]
  },
  {
    id: 10, orderIndex: 1, title: 'Domain Modeli: Category Entity',
    objective: 'Kategori entity\'sini olustur ve Product ile iliskilendir',
    conceptCodes: 'ENTITY,JPA_RELATIONSHIPS,COMPOSITION',
    steps: [{
      id: 10, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} Category entity\'sini olustur ve Product ile iliski kur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Entity annotation\'i ekle\n2. id, name, description alanlarini olustur\n3. Product ile @OneToMany iliskisi kur (bir kategoride birden fazla urun)\n4. Product sinifina @ManyToOne ile Category iliskisi ekle\n5. @JoinColumn ile foreign key belirt\n\n\u{1F4A1} Bilgi:\n\u2022 @OneToMany(mappedBy = "category"): Iliskinin sahibi Product tarafindadir\n\u2022 @ManyToOne: Product sinifinda tanimlanir, foreign key bu tablodadir\n\u2022 Bidirectional iliski: Her iki taraftan da erisilebilir',
      starterCode: '// Category.java\n\n@Entity\npublic class Category {\n    // id, name, description alanlarini ekle\n    // Product ile @OneToMany iliskisi kur\n}'
    }]
  },
  {
    id: 11, orderIndex: 2, title: 'Repository Katmani: ProductRepository',
    objective: 'Product ve Category icin repository interface\'leri olustur',
    conceptCodes: 'INTERFACE,DEPENDENCY_INJECTION,SPRING_BEAN_LIFECYCLE',
    steps: [{
      id: 11, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} ProductRepository ve CategoryRepository olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. ProductRepository: JpaRepository<Product, Long> extend et\n2. findByNameContainingIgnoreCase(String name) metodu ekle\n3. findByCategoryId(Long categoryId) metodu ekle\n4. findByPriceBetween(BigDecimal min, BigDecimal max) ekle\n5. CategoryRepository: JpaRepository<Category, Long> extend et\n6. findByNameIgnoreCase(String name) ekle\n\n\u{1F4A1} Bilgi:\n\u2022 Containing: SQL LIKE %...% sorgusuna karsilik gelir\n\u2022 IgnoreCase: Buyuk/kucuk harf farketmez\n\u2022 Between: aralik sorgusu (min <= price <= max)\n\u2022 Spring Data sorgu metotlarini isimlendirme kurallarina gore otomatik uretir',
      starterCode: '// ProductRepository.java\n\npublic interface ProductRepository {\n    // JpaRepository\'den extend et\n    // Ozel sorgu metotlarini ekle\n}'
    }]
  },
  {
    id: 12, orderIndex: 3, title: 'Service Katmani: ProductService',
    objective: 'Urun CRUD islemleri ve is mantigini olustur',
    conceptCodes: 'DEPENDENCY_INJECTION,TRANSACTION_MANAGEMENT,ENCAPSULATION',
    steps: [{
      id: 12, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} ProductService sinifini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Service annotation\'i ekle\n2. ProductRepository ve CategoryRepository\'yi constructor injection ile enjekte et\n3. createProduct(ProductCreateRequest dto) metodu yaz\n4. updateStock(Long productId, int quantity) metodu yaz - @Transactional ekle\n5. searchProducts(String keyword) metodu yaz\n6. getProductsByCategory(Long categoryId) metodu yaz\n7. Urun bulunamazsa ProductNotFoundException firlat\n\n\u{1F4A1} Bilgi:\n\u2022 @Transactional: Stok guncelleme atomik olmali\n\u2022 DTO kullanimi: Entity\'yi dogrudan disariya acma\n\u2022 Constructor injection: Test edilebilirlik icin onemli',
      starterCode: '@Service\npublic class ProductService {\n    // Repository\'leri constructor injection ile enjekte et\n    // CRUD ve arama metotlarini yaz\n}'
    }]
  },
  {
    id: 13, orderIndex: 4, title: 'REST API: ProductController',
    objective: 'Urun yonetimi icin RESTful endpoint\'ler olustur',
    conceptCodes: 'DEPENDENCY_INJECTION,REST_BEST_PRACTICES',
    steps: [{
      id: 13, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} ProductController sinifini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @RestController ve @RequestMapping("/api/products") ekle\n2. @GetMapping ile tum urunleri listele\n3. @GetMapping("/{id}") ile tek urun getir\n4. @PostMapping ile yeni urun olustur - @Valid ekle\n5. @PutMapping("/{id}/stock") ile stok guncelle\n6. @GetMapping("/search") ile @RequestParam keyword ile arama yap\n7. ResponseEntity ile uygun HTTP status kodlari don\n\n\u{1F4A1} Bilgi:\n\u2022 @Valid: Request body validasyonunu tetikler\n\u2022 ResponseEntity.ok(): 200 OK\n\u2022 ResponseEntity.created(): 201 Created\n\u2022 @RequestParam: Query string parametresi (?keyword=telefon)',
      starterCode: '@RestController\n@RequestMapping("/api/products")\npublic class ProductController {\n    // Service\'i enjekte et\n    // CRUD endpoint\'lerini yaz\n}'
    }]
  },
  {
    id: 14, orderIndex: 5, title: 'DTO ve Validation Katmani',
    objective: 'Request/Response DTO\'lari ve validasyon kurallari olustur',
    conceptCodes: 'ENCAPSULATION,VALIDATION_PATTERNS',
    steps: [{
      id: 14, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} ProductCreateRequest ve ProductResponse DTO\'larini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. ProductCreateRequest olustur:\n   - @NotBlank String name\n   - @NotNull @Positive BigDecimal price\n   - @Min(0) int stockQuantity\n   - Long categoryId\n2. ProductResponse olustur:\n   - id, name, description, price, stockQuantity, categoryName\n3. ProductMapper sinifi olustur:\n   - toResponse(Product entity) metodu\n   - toEntity(ProductCreateRequest dto) metodu\n\n\u{1F4A1} Bilgi:\n\u2022 DTO: Entity\'yi dogrudan API\'ye acmak guvenlik ve bakim riski tasir\n\u2022 @NotBlank: null, bos string ve sadece bosluk iceren degerleri reddeder\n\u2022 Mapper: Entity <-> DTO donusumunu merkezi yapar',
      starterCode: '// ProductCreateRequest.java\n\npublic class ProductCreateRequest {\n    // Alanlar ve validasyon annotation\'lari ekle\n}\n\n// ProductResponse.java\npublic class ProductResponse {\n    // Response alanlari\n}'
    }]
  },
]

const LIBRARY_TASKS = [
  {
    id: 15, orderIndex: 0, title: 'Domain Modeli: Book Entity',
    objective: 'Kutuphane sisteminin temel kitap entity\'sini olustur',
    conceptCodes: 'ENTITY,ID,GENERATED_VALUE,ENCAPSULATION',
    steps: [{
      id: 15, orderIndex: 0, status: 'ACTIVE', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} Book sinifini bir JPA entity olarak olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Entity annotation\'i ekle\n2. id alani (Long, @Id, @GeneratedValue)\n3. title alani (String, private)\n4. author alani (String, private)\n5. isbn alani (String, private, @Column(unique = true))\n6. available alani (boolean, private, varsayilan true)\n7. Getter/setter metotlarini yaz\n8. markAsUnavailable() ve markAsAvailable() metotlari ekle\n\n\u{1F4A1} Bilgi:\n\u2022 @Column(unique = true): ISBN numarasi her kitap icin benzersiz olmali\n\u2022 boolean available: Kitabin odunc verilip verilmedigini takip eder\n\u2022 Durum degisikligini metotla yapmak encapsulation\'dir',
      starterCode: '// Book.java\n\npublic class Book {\n    // id, title, author, isbn, available alanlarini ekle\n    // markAsUnavailable ve markAsAvailable metotlarini yaz\n}'
    }]
  },
  {
    id: 16, orderIndex: 1, title: 'Domain Modeli: Member Entity',
    objective: 'Kutuphane uyesi entity\'sini olustur',
    conceptCodes: 'ENTITY,ENCAPSULATION,JPA_RELATIONSHIPS',
    steps: [{
      id: 16, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} Member (Uye) entity\'sini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Entity annotation\'i ekle\n2. id, name, email, membershipDate alanlarini olustur\n3. email alanina @Column(unique = true) ekle\n4. membershipDate icin LocalDate tipi kullan\n5. maxBooksAllowed alani ekle (int, varsayilan 3)\n6. Getter/setter metotlarini yaz\n\n\u{1F4A1} Bilgi:\n\u2022 LocalDate: Java 8+ tarih API\'si, Date yerine kullanilir\n\u2022 maxBooksAllowed: Is kurali - bir uye en fazla 3 kitap odunc alabilir\n\u2022 unique constraint: Ayni email ile birden fazla uyelik engellenr',
      starterCode: '// Member.java\n\n@Entity\npublic class Member {\n    // id, name, email, membershipDate, maxBooksAllowed alanlarini ekle\n}'
    }]
  },
  {
    id: 17, orderIndex: 2, title: 'Repository Katmani: BookRepository',
    objective: 'Book ve Member icin repository interface\'leri olustur',
    conceptCodes: 'INTERFACE,DEPENDENCY_INJECTION',
    steps: [{
      id: 17, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} BookRepository ve MemberRepository olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. BookRepository: JpaRepository<Book, Long> extend et\n2. findByIsbn(String isbn) metodu ekle\n3. findByAvailableTrue() metodu ekle (musait kitaplari listele)\n4. findByAuthorContainingIgnoreCase(String author) ekle\n5. MemberRepository: JpaRepository<Member, Long> extend et\n6. findByEmail(String email) ekle\n\n\u{1F4A1} Bilgi:\n\u2022 findByAvailableTrue(): WHERE available = true sorgusunu otomatik olusturur\n\u2022 ContainingIgnoreCase: Esnek arama icin idealdir\n\u2022 Optional<T> donus tipi: Sonuc yoksa null yerine Optional.empty()',
      starterCode: '// BookRepository.java\n\npublic interface BookRepository {\n    // JpaRepository\'den extend et\n    // Ozel sorgu metotlarini ekle\n}'
    }]
  },
  {
    id: 18, orderIndex: 3, title: 'Service Katmani: BorrowService',
    objective: 'Kitap odunc alma/iade islemlerinin is mantigini yaz',
    conceptCodes: 'DEPENDENCY_INJECTION,TRANSACTION_MANAGEMENT,ENCAPSULATION',
    steps: [{
      id: 18, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} BorrowService sinifini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @Service annotation\'i ekle\n2. BookRepository ve MemberRepository\'yi constructor injection ile enjekte et\n3. borrowBook(Long memberId, Long bookId) metodu yaz:\n   - Kitap musait mi kontrol et\n   - Uye limit asmis mi kontrol et\n   - Kitabi unavailable yap, kaydet\n4. returnBook(Long memberId, Long bookId) metodu yaz:\n   - Kitabi available yap, kaydet\n5. @Transactional annotation\'i ekle\n6. Hata durumlari icin anlamli exception firlat\n\n\u{1F4A1} Bilgi:\n\u2022 @Transactional: Odunc alma/iade atomik olmali\n\u2022 Is kurallari service katmaninda kontrol edilir\n\u2022 Exception: BookNotAvailableException, BorrowLimitExceededException',
      starterCode: '@Service\npublic class BorrowService {\n    // Repository\'leri enjekte et\n    // borrowBook ve returnBook metotlarini yaz\n}'
    }]
  },
  {
    id: 19, orderIndex: 4, title: 'REST API: BorrowController',
    objective: 'Odunc alma/iade endpoint\'lerini olustur',
    conceptCodes: 'DEPENDENCY_INJECTION,REST_BEST_PRACTICES',
    steps: [{
      id: 19, orderIndex: 0, status: 'LOCKED', attempts: 0, hintsUsed: 0,
      instruction: '\u{1F3AF} BorrowController sinifini olustur.\n\n\u{1F4CB} Yapman gerekenler:\n1. @RestController ve @RequestMapping("/api/borrows") ekle\n2. BorrowService\'i constructor injection ile enjekte et\n3. @PostMapping("/borrow") ile kitap odunc alma endpoint\'i\n4. @PostMapping("/return") ile kitap iade endpoint\'i\n5. @GetMapping("/available-books") ile musait kitaplari listele\n6. ResponseEntity ile uygun HTTP status kodlari don\n7. Hata durumlarinda 400 Bad Request don\n\n\u{1F4A1} Bilgi:\n\u2022 POST: Durum degistiren islemler (odunc al, iade et)\n\u2022 GET: Sadece veri okuyan islemler (musait kitaplar)\n\u2022 ResponseEntity.badRequest(): 400 status kodu\n\u2022 try-catch ile exception\'lari yakalayip anlamli mesaj don',
      starterCode: '@RestController\n@RequestMapping("/api/borrows")\npublic class BorrowController {\n    // Service\'i enjekte et\n    // borrow, return ve available-books endpoint\'lerini yaz\n}'
    }]
  },
]

const HINTS: Record<number, string[]> = {
  1: [
    'Bir sinifin veritabani tablosuna karsilik gelmesi icin bir annotation gerekir. jakarta.persistence paketine bak.',
    '@Entity annotation\'ini sinifin ustune ekle. @Id ve @GeneratedValue ekle. Alanlari private yap, getter/setter yaz.',
    '@Entity\npublic class User {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    // getter/setter ekle\n}',
    '@Entity\npublic class User {\n\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    private String email;\n\n    public User() {}\n    public User(String name, String email) {\n        this.name = name;\n        this.email = email;\n    }\n\n    public Long getId() { return id; }\n    public String getName() { return name; }\n    public void setName(String name) { this.name = name; }\n    public String getEmail() { return email; }\n    public void setEmail(String email) { this.email = email; }\n}',
  ],
  2: [
    'User ile iliski kurmak icin JPA iliski annotation\'larindan birine ihtiyacin var.',
    '@ManyToOne ile User iliskisi kur. deposit/withdraw icinde parametre kontrolu yap. balance\'i private tut.',
    '@ManyToOne(fetch = FetchType.LAZY)\n@JoinColumn(name = "user_id")\nprivate User user;\n\npublic void deposit(double amount) {\n    if (amount <= 0) throw new IllegalArgumentException("Pozitif olmali");\n    this.balance += amount;\n}',
    '@Entity\npublic class Account {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String accountNumber;\n    private double balance;\n\n    @ManyToOne(fetch = FetchType.LAZY)\n    @JoinColumn(name = "user_id")\n    private User user;\n\n    public void deposit(double amount) {\n        if (amount <= 0) throw new IllegalArgumentException("Pozitif olmali");\n        this.balance += amount;\n    }\n    public void withdraw(double amount) {\n        if (amount <= 0) throw new IllegalArgumentException("Pozitif olmali");\n        if (amount > balance) throw new IllegalArgumentException("Yetersiz bakiye");\n        this.balance -= amount;\n    }\n}',
  ],
  3: [
    'Spring Data JPA\'da repository bir interface olarak tanimlanir.',
    'interface UserRepository extends JpaRepository<User, Long> seklinde tanimla.',
    'public interface UserRepository extends JpaRepository<User, Long> {\n    Optional<User> findByEmail(String email);\n}',
    'public interface UserRepository extends JpaRepository<User, Long> {\n    Optional<User> findByEmail(String email);\n}\n\npublic interface AccountRepository extends JpaRepository<Account, Long> {\n    List<Account> findByUserId(Long userId);\n}',
  ],
  4: [
    'Service sinifi @Service annotation\'i alir ve repository\'leri constructor ile alir.',
    'Constructor injection kullan. @Transactional ile transaction guvenligini sagla.',
    '@Service\npublic class AccountService {\n    private final AccountRepository repo;\n    public AccountService(AccountRepository repo) { this.repo = repo; }\n\n    @Transactional\n    public void deposit(String accNo, double amount) { ... }\n}',
    '@Service\npublic class AccountService {\n    private final AccountRepository repo;\n    public AccountService(AccountRepository repo) { this.repo = repo; }\n\n    @Transactional\n    public void deposit(String accNo, double amount) {\n        Account acc = repo.findByAccountNumber(accNo).orElseThrow();\n        acc.deposit(amount);\n        repo.save(acc);\n    }\n\n    @Transactional\n    public void withdraw(String accNo, double amount) {\n        Account acc = repo.findByAccountNumber(accNo).orElseThrow();\n        acc.withdraw(amount);\n        repo.save(acc);\n    }\n\n    @Transactional\n    public void transfer(String from, String to, double amount) {\n        withdraw(from, amount);\n        deposit(to, amount);\n    }\n}',
  ],
  5: [
    '@RestController annotation\'i sinifin bir REST API controller\'i oldugunu belirtir.',
    '@RequestMapping ile base path tanimla. @PostMapping ile islem endpoint\'leri ekle.',
    '@RestController\n@RequestMapping("/api/accounts")\npublic class AccountController {\n    private final AccountService service;\n    public AccountController(AccountService service) { this.service = service; }\n\n    @PostMapping("/deposit")\n    public ResponseEntity<?> deposit(@RequestBody DepositRequest req) { ... }\n}',
    '@RestController\n@RequestMapping("/api/accounts")\npublic class AccountController {\n    private final AccountService service;\n    public AccountController(AccountService service) { this.service = service; }\n\n    @PostMapping("/deposit")\n    public ResponseEntity<?> deposit(@RequestBody DepositRequest req) {\n        service.deposit(req.getAccountNumber(), req.getAmount());\n        return ResponseEntity.ok().build();\n    }\n\n    @PostMapping("/withdraw")\n    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest req) {\n        service.withdraw(req.getAccountNumber(), req.getAmount());\n        return ResponseEntity.ok().build();\n    }\n}',
  ],
  6: [
    'RuntimeException\'dan turetilen ozel exception sinifi olustur.',
    '@RestControllerAdvice ile merkezi hata yonetimi sagla. @ExceptionHandler ile yakalama yap.',
    'public class AccountNotFoundException extends RuntimeException {\n    public AccountNotFoundException(String msg) { super(msg); }\n}\n\n@RestControllerAdvice\npublic class GlobalExceptionHandler {\n    @ExceptionHandler(AccountNotFoundException.class)\n    public ResponseEntity<?> handle(AccountNotFoundException ex) {\n        return ResponseEntity.status(404).body(ex.getMessage());\n    }\n}',
    'public class AccountNotFoundException extends RuntimeException {\n    public AccountNotFoundException(String msg) { super(msg); }\n}\n\n@RestControllerAdvice\npublic class GlobalExceptionHandler {\n    @ExceptionHandler(AccountNotFoundException.class)\n    public ResponseEntity<Map<String, String>> handleNotFound(AccountNotFoundException ex) {\n        return ResponseEntity.status(HttpStatus.NOT_FOUND)\n            .body(Map.of("error", ex.getMessage()));\n    }\n\n    @ExceptionHandler(IllegalArgumentException.class)\n    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {\n        return ResponseEntity.badRequest()\n            .body(Map.of("error", ex.getMessage()));\n    }\n}',
  ],
  7: [
    'Jakarta Validation annotation\'lari ile alanlara kisitlama ekle.',
    '@NotNull null degerini reddeder. @Positive sifir ve negatif degerleri reddeder.',
    'public class DepositRequest {\n    @NotNull\n    @Positive\n    private Double amount;\n\n    public Double getAmount() { return amount; }\n    public void setAmount(Double amount) { this.amount = amount; }\n}',
    'public class DepositRequest {\n    @NotNull(message = "Tutar bos olamaz")\n    @Positive(message = "Tutar pozitif olmali")\n    private Double amount;\n\n    @NotBlank(message = "Hesap numarasi bos olamaz")\n    private String accountNumber;\n\n    public Double getAmount() { return amount; }\n    public void setAmount(Double amount) { this.amount = amount; }\n    public String getAccountNumber() { return accountNumber; }\n    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }\n}',
  ],
  8: [
    'JUnit testlerinde @Test annotation\'i ile test metotlarini isaretlersin.',
    '@SpringBootTest ile uygulama context\'ini yukle. assertThrows ile exception test et.',
    '@SpringBootTest\npublic class AccountServiceTest {\n    @Autowired\n    private AccountService service;\n\n    @Test\n    void testDeposit() {\n        // deposit islemi ve assertion\n    }\n}',
    '@SpringBootTest\npublic class AccountServiceTest {\n    @Autowired\n    private AccountService service;\n\n    @Test\n    void testDeposit_success() {\n        service.deposit("ACC001", 100.0);\n        // bakiyeyi kontrol et\n    }\n\n    @Test\n    void testDeposit_negativeAmount_throwsException() {\n        assertThrows(IllegalArgumentException.class, () -> {\n            service.deposit("ACC001", -50.0);\n        });\n    }\n}',
  ],
  9: [
    'Product entity\'si icin @Entity, @Id ve @GeneratedValue annotation\'lari gerekli.',
    'BigDecimal tipi para degerleri icin kullanilir. @Column(nullable = false) zorunlu alan belirtir.',
    '@Entity\npublic class Product {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n\n    @Column(nullable = false)\n    private String name;\n    private BigDecimal price;\n    private int stockQuantity;\n}',
    '@Entity\npublic class Product {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n\n    @Column(nullable = false)\n    private String name;\n    private String description;\n    private BigDecimal price;\n    private int stockQuantity;\n\n    @ManyToOne(fetch = FetchType.LAZY)\n    @JoinColumn(name = "category_id")\n    private Category category;\n\n    public void decreaseStock(int quantity) {\n        if (quantity > this.stockQuantity) throw new IllegalArgumentException("Yetersiz stok");\n        this.stockQuantity -= quantity;\n    }\n\n    // getter/setter\n}',
  ],
  10: [
    'Category ile Product arasinda @OneToMany - @ManyToOne iliskisi kurulmali.',
    'mappedBy ile iliskinin sahibini belirt. Product tarafinda @ManyToOne kullan.',
    '@Entity\npublic class Category {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n\n    @OneToMany(mappedBy = "category")\n    private List<Product> products;\n}',
    '@Entity\npublic class Category {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    private String description;\n\n    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)\n    private List<Product> products = new ArrayList<>();\n\n    // getter/setter\n}',
  ],
  11: [
    'JpaRepository extend ederek temel CRUD metotlarini otomatik alirsin.',
    'Metot isimlendirme kurallari: findBy + Alan + Containing + IgnoreCase',
    'public interface ProductRepository extends JpaRepository<Product, Long> {\n    List<Product> findByNameContainingIgnoreCase(String name);\n    List<Product> findByCategoryId(Long categoryId);\n}',
    'public interface ProductRepository extends JpaRepository<Product, Long> {\n    List<Product> findByNameContainingIgnoreCase(String name);\n    List<Product> findByCategoryId(Long categoryId);\n    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);\n}\n\npublic interface CategoryRepository extends JpaRepository<Category, Long> {\n    Optional<Category> findByNameIgnoreCase(String name);\n}',
  ],
  12: [
    'Service sinifi @Service annotation\'i ile isaretlenir ve repository\'leri constructor injection ile alir.',
    '@Transactional ile stok guncelleme atomik yapilir. Exception durumlarini handle et.',
    '@Service\npublic class ProductService {\n    private final ProductRepository productRepo;\n    public ProductService(ProductRepository productRepo) { this.productRepo = productRepo; }\n\n    @Transactional\n    public void updateStock(Long productId, int quantity) {\n        Product product = productRepo.findById(productId).orElseThrow();\n        product.decreaseStock(quantity);\n        productRepo.save(product);\n    }\n}',
    '@Service\npublic class ProductService {\n    private final ProductRepository productRepo;\n    private final CategoryRepository categoryRepo;\n\n    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo) {\n        this.productRepo = productRepo;\n        this.categoryRepo = categoryRepo;\n    }\n\n    public Product createProduct(ProductCreateRequest dto) {\n        Product product = new Product();\n        product.setName(dto.getName());\n        product.setPrice(dto.getPrice());\n        product.setStockQuantity(dto.getStockQuantity());\n        if (dto.getCategoryId() != null) {\n            Category cat = categoryRepo.findById(dto.getCategoryId()).orElseThrow();\n            product.setCategory(cat);\n        }\n        return productRepo.save(product);\n    }\n\n    @Transactional\n    public void updateStock(Long productId, int quantity) {\n        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Urun bulunamadi"));\n        product.decreaseStock(quantity);\n        productRepo.save(product);\n    }\n\n    public List<Product> searchProducts(String keyword) {\n        return productRepo.findByNameContainingIgnoreCase(keyword);\n    }\n}',
  ],
  13: [
    '@RestController ve @RequestMapping ile REST API endpoint\'leri tanimlanir.',
    '@GetMapping, @PostMapping, @PutMapping ile HTTP metotlarina karsilik endpoint yaz. @Valid ile validasyon tetikle.',
    '@RestController\n@RequestMapping("/api/products")\npublic class ProductController {\n    private final ProductService service;\n    public ProductController(ProductService service) { this.service = service; }\n\n    @GetMapping\n    public ResponseEntity<List<Product>> getAll() {\n        return ResponseEntity.ok(service.getAllProducts());\n    }\n}',
    '@RestController\n@RequestMapping("/api/products")\npublic class ProductController {\n    private final ProductService service;\n    public ProductController(ProductService service) { this.service = service; }\n\n    @GetMapping\n    public ResponseEntity<List<Product>> getAll() {\n        return ResponseEntity.ok(service.getAllProducts());\n    }\n\n    @GetMapping("/{id}")\n    public ResponseEntity<Product> getById(@PathVariable Long id) {\n        return ResponseEntity.ok(service.getById(id));\n    }\n\n    @PostMapping\n    public ResponseEntity<Product> create(@Valid @RequestBody ProductCreateRequest dto) {\n        return ResponseEntity.status(HttpStatus.CREATED).body(service.createProduct(dto));\n    }\n\n    @GetMapping("/search")\n    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {\n        return ResponseEntity.ok(service.searchProducts(keyword));\n    }\n}',
  ],
  14: [
    'DTO siniflarinda Jakarta Validation annotation\'lari ile girdi dogrulamasi yapilir.',
    '@NotBlank, @NotNull, @Positive, @Min gibi annotation\'lar kullan. Mapper sinifi ile entity-DTO donusumu yap.',
    'public class ProductCreateRequest {\n    @NotBlank\n    private String name;\n    @NotNull @Positive\n    private BigDecimal price;\n    @Min(0)\n    private int stockQuantity;\n}',
    'public class ProductCreateRequest {\n    @NotBlank(message = "Urun adi bos olamaz")\n    private String name;\n    private String description;\n    @NotNull(message = "Fiyat bos olamaz")\n    @Positive(message = "Fiyat pozitif olmali")\n    private BigDecimal price;\n    @Min(value = 0, message = "Stok negatif olamaz")\n    private int stockQuantity;\n    private Long categoryId;\n\n    // getter/setter\n}\n\npublic class ProductResponse {\n    private Long id;\n    private String name;\n    private String description;\n    private BigDecimal price;\n    private int stockQuantity;\n    private String categoryName;\n\n    // getter/setter\n}\n\npublic class ProductMapper {\n    public static ProductResponse toResponse(Product p) {\n        ProductResponse r = new ProductResponse();\n        r.setId(p.getId());\n        r.setName(p.getName());\n        r.setPrice(p.getPrice());\n        r.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);\n        return r;\n    }\n}',
  ],
  15: [
    'Book entity icin @Entity, @Id, @GeneratedValue annotation\'larini kullan.',
    '@Column(unique = true) ile ISBN benzersizligini sagla. boolean available ile durum takibi yap.',
    '@Entity\npublic class Book {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String title;\n    private String author;\n    @Column(unique = true)\n    private String isbn;\n    private boolean available = true;\n}',
    '@Entity\npublic class Book {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String title;\n    private String author;\n    @Column(unique = true)\n    private String isbn;\n    private boolean available = true;\n\n    public void markAsUnavailable() { this.available = false; }\n    public void markAsAvailable() { this.available = true; }\n\n    // getter/setter\n}',
  ],
  16: [
    'Member entity icin LocalDate tipi tarih alanlarina uygundur.',
    '@Column(unique = true) ile email benzersizligini sagla. maxBooksAllowed ile odunc limiti belirle.',
    '@Entity\npublic class Member {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    @Column(unique = true)\n    private String email;\n    private LocalDate membershipDate;\n    private int maxBooksAllowed = 3;\n}',
    '@Entity\npublic class Member {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    @Column(unique = true)\n    private String email;\n    private LocalDate membershipDate;\n    private int maxBooksAllowed = 3;\n\n    public Member() {}\n    public Member(String name, String email) {\n        this.name = name;\n        this.email = email;\n        this.membershipDate = LocalDate.now();\n    }\n\n    // getter/setter\n}',
  ],
  17: [
    'BookRepository JpaRepository<Book, Long> extend etmeli.',
    'findByAvailableTrue() ile musait kitaplari listele. findByIsbn ile ISBN aramasiyap.',
    'public interface BookRepository extends JpaRepository<Book, Long> {\n    Optional<Book> findByIsbn(String isbn);\n    List<Book> findByAvailableTrue();\n}',
    'public interface BookRepository extends JpaRepository<Book, Long> {\n    Optional<Book> findByIsbn(String isbn);\n    List<Book> findByAvailableTrue();\n    List<Book> findByAuthorContainingIgnoreCase(String author);\n}\n\npublic interface MemberRepository extends JpaRepository<Member, Long> {\n    Optional<Member> findByEmail(String email);\n}',
  ],
  18: [
    'BorrowService @Service annotation\'i ile isaretlenir.',
    'borrowBook icinde kitap musaitligini ve uye limitini kontrol et. @Transactional ekle.',
    '@Service\npublic class BorrowService {\n    private final BookRepository bookRepo;\n    private final MemberRepository memberRepo;\n\n    @Transactional\n    public void borrowBook(Long memberId, Long bookId) {\n        Book book = bookRepo.findById(bookId).orElseThrow();\n        if (!book.isAvailable()) throw new RuntimeException("Kitap musait degil");\n        book.markAsUnavailable();\n        bookRepo.save(book);\n    }\n}',
    '@Service\npublic class BorrowService {\n    private final BookRepository bookRepo;\n    private final MemberRepository memberRepo;\n\n    public BorrowService(BookRepository bookRepo, MemberRepository memberRepo) {\n        this.bookRepo = bookRepo;\n        this.memberRepo = memberRepo;\n    }\n\n    @Transactional\n    public void borrowBook(Long memberId, Long bookId) {\n        Member member = memberRepo.findById(memberId).orElseThrow(() -> new RuntimeException("Uye bulunamadi"));\n        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Kitap bulunamadi"));\n        if (!book.isAvailable()) throw new RuntimeException("Kitap musait degil");\n        book.markAsUnavailable();\n        bookRepo.save(book);\n    }\n\n    @Transactional\n    public void returnBook(Long memberId, Long bookId) {\n        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Kitap bulunamadi"));\n        book.markAsAvailable();\n        bookRepo.save(book);\n    }\n}',
  ],
  19: [
    '@RestController ve @RequestMapping ile endpoint\'ler tanimlanir.',
    '@PostMapping ile odunc alma/iade endpoint\'leri yaz. @GetMapping ile musait kitaplari listele.',
    '@RestController\n@RequestMapping("/api/borrows")\npublic class BorrowController {\n    private final BorrowService service;\n\n    @PostMapping("/borrow")\n    public ResponseEntity<?> borrow(@RequestBody BorrowRequest req) {\n        service.borrowBook(req.getMemberId(), req.getBookId());\n        return ResponseEntity.ok().build();\n    }\n}',
    '@RestController\n@RequestMapping("/api/borrows")\npublic class BorrowController {\n    private final BorrowService service;\n    private final BookRepository bookRepo;\n\n    public BorrowController(BorrowService service, BookRepository bookRepo) {\n        this.service = service;\n        this.bookRepo = bookRepo;\n    }\n\n    @PostMapping("/borrow")\n    public ResponseEntity<?> borrow(@RequestBody BorrowRequest req) {\n        try {\n            service.borrowBook(req.getMemberId(), req.getBookId());\n            return ResponseEntity.ok(Map.of("message", "Kitap odunc verildi"));\n        } catch (RuntimeException e) {\n            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));\n        }\n    }\n\n    @PostMapping("/return")\n    public ResponseEntity<?> returnBook(@RequestBody BorrowRequest req) {\n        service.returnBook(req.getMemberId(), req.getBookId());\n        return ResponseEntity.ok(Map.of("message", "Kitap iade edildi"));\n    }\n\n    @GetMapping("/available-books")\n    public ResponseEntity<?> availableBooks() {\n        return ResponseEntity.ok(bookRepo.findByAvailableTrue());\n    }\n}',
  ],
}

const CONCEPTS: Record<string, any> = {
  ENTITY: {
    code: 'ENTITY', title: '@Entity Annotation',
    realLifeAnalogy: 'Bir Excel tablosu dusun. Her satir bir kayit, sutunlar ise alanlar. @Entity demek, Java sinifini bir Excel tablosuna (veritabani tablosu) baglamak demek. Her nesne = bir satir.',
    whyExplanation: 'JPA/Hibernate bu annotation olmadan sinifi bir tablo olarak taniyamaz. ORM (Object-Relational Mapping) mekanizmasinin baslangic noktasidir.',
    wrongExample: 'public class User {\n    public Long id;\n    public String name;\n}\n// @Entity yok \u2192 Hibernate bu sinifi gormezden gelir',
    wrongExampleExplanation: '@Entity olmadan JPA bu sinifi yonetmez. Repository ile kaydetmeye calisirsan hata alirsin.',
    rightExample: '@Entity\npublic class User {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n    private String name;\n    // getter/setter\n}',
    rightExampleExplanation: '@Entity ile Hibernate bu sinifi \'user\' tablosuna esler. @Id ile primary key tanimlanir. Artik CRUD islemleri yapilabilir.',
  },
  ENCAPSULATION: {
    code: 'ENCAPSULATION', title: 'Kapsulleme (Encapsulation)',
    realLifeAnalogy: 'Bir ATM dusun. Para cekme butonuna basarsin ama ATM\'nin icindeki mekanizmayi goremezsin. Disariya sadece \'para cek\' davranisi acilmis, ic detaylar gizli. private = kasanin kapagi, getter/setter = ATM ekrani.',
    whyExplanation: 'Encapsulation olmadan herkes her alana dogrudan erisir ve degistirir. Bu, hatali veriye (negatif bakiye gibi) yol acar.',
    wrongExample: 'public class Account {\n    public double balance;\n}\n// account.balance = -1000; \u2192 gecersiz ama engellenemiyor',
    wrongExampleExplanation: 'balance public oldugu icin herhangi bir yerden negatif deger atanabilir.',
    rightExample: 'public class Account {\n    private double balance;\n\n    public void deposit(double amount) {\n        if (amount <= 0) throw new IllegalArgumentException("Pozitif olmali");\n        this.balance += amount;\n    }\n}',
    rightExampleExplanation: 'balance private \u2192 dogrudan erisim yok. deposit() ile kontrollu erisim saglanir.',
  },
  THIS_KEYWORD: {
    code: 'THIS_KEYWORD', title: 'this Anahtar Kelimesi',
    realLifeAnalogy: 'Bir toplantida herkes \'ben\' dediginde kendini kasteder. Java\'da \'this\' de bir nesnenin \'ben\' demesidir.',
    whyExplanation: 'Constructor parametresi ile sinif alani ayni isimde oldugunda, \'this\' ile alani belirtmezsek parametre kendine atanir. this.name = name; \u2192 \'benim alanima parametreyi ata\' demektir.',
    wrongExample: 'public User(String name) {\n    name = name; // YANLIS: parametre kendine ataniyor\n}',
    wrongExampleExplanation: '\'name = name\' parametreyi kendine atar. Sinifin name alani null kalir.',
    rightExample: 'public User(String name) {\n    this.name = name; // DOGRU: sinifin alanina ataniyor\n}',
    rightExampleExplanation: 'this.name \u2192 sinifin alani, name \u2192 parametre.',
  },
  ID: {
    code: 'ID', title: '@Id Annotation',
    realLifeAnalogy: 'TC kimlik numarasi gibi \u2014 her vatandasi benzersiz yapar.',
    whyExplanation: 'Veritabani tablolarinda primary key zorunludur. @Id olmadan JPA hata verir.',
    wrongExample: '@Entity\npublic class User {\n    private Long id; // @Id yok\n}',
    wrongExampleExplanation: '@Id olmadan JPA \'No identifier specified\' hatasi verir.',
    rightExample: '@Entity\npublic class User {\n    @Id\n    @GeneratedValue(strategy = GenerationType.IDENTITY)\n    private Long id;\n}',
    rightExampleExplanation: '@Id ile primary key tanimlanir.',
  },
  GENERATED_VALUE: {
    code: 'GENERATED_VALUE', title: '@GeneratedValue Annotation',
    realLifeAnalogy: 'Banka sira numarasi makinesi: butona basarsin, makine sirayla numara verir.',
    whyExplanation: 'Elle id atamak cakisma riskini artirir. @GeneratedValue ile veritabani otomatik uretir.',
    wrongExample: 'user.setId(42L); // elle id atama \u2192 cakisma riski!',
    wrongExampleExplanation: 'Elle id vermek mevcut kayitla cakisabilir.',
    rightExample: '@GeneratedValue(strategy = GenerationType.IDENTITY)\nprivate Long id;',
    rightExampleExplanation: 'Her INSERT\'te veritabani otomatik artan id uretir.',
  },
  INTERFACE: {
    code: 'INTERFACE', title: 'Interface Kavrami',
    realLifeAnalogy: 'Bir priz dusun. Herhangi bir cihaz (telefon, laptop, supurge) ayni prize takılabilir cunku hepsi ayni standardi (interface\'i) kullanir. Cihazin ic yapisi farkli ama baglanti noktasi ayni.',
    whyExplanation: 'Interface, siniflar arasi sozlesme tanimlar. Implementasyon degisebilir ama davranis garantilenir. Polymorphism ve loose coupling saglar.',
    wrongExample: 'public class EmailNotifier {\n    public void send(String msg) { /* email gonder */ }\n}\n// Sadece email destekler, SMS eklemek icin her yeri degistirmek lazim',
    wrongExampleExplanation: 'Concrete sinifa dogrudan bagimlilik olusmus. Degisiklik pahalı.',
    rightExample: 'public interface Notifier {\n    void send(String msg);\n}\npublic class EmailNotifier implements Notifier { ... }\npublic class SmsNotifier implements Notifier { ... }',
    rightExampleExplanation: 'Notifier interface\'i sayesinde istedigimiz implementasyonu takabiliriz.',
  },
  DEPENDENCY_INJECTION: {
    code: 'DEPENDENCY_INJECTION', title: 'Bagimlilik Enjeksiyonu (DI)',
    realLifeAnalogy: 'Bir restoran dusun. Asci malzemeleri kendisi almaz, tedarikci getirir. Asci sadece yemek yapar. DI = tedarikci sistemi. Spring = tedarikci.',
    whyExplanation: 'DI olmadan siniflar kendi bagimliliklarini olusturur. Bu siki bağ (tight coupling) yaratir, test ve degisikligi zorlastirir.',
    wrongExample: 'public class OrderService {\n    private PaymentService payment = new PaymentService(); // YANLIS\n}',
    wrongExampleExplanation: 'OrderService, PaymentService\'in nasil olusturulduguna bagimli. Test icin mock koyamazsin.',
    rightExample: '@Service\npublic class OrderService {\n    private final PaymentService payment;\n    public OrderService(PaymentService payment) {\n        this.payment = payment; // Spring enjekte eder\n    }\n}',
    rightExampleExplanation: 'Constructor injection ile bagimlilik disaridan verilir. Test icin mock gecebilirsin.',
  },
  ABSTRACT_CLASS: {
    code: 'ABSTRACT_CLASS', title: 'Abstract (Soyut) Sinif',
    realLifeAnalogy: 'Bir arac tasarimi dusun. "Arac" soyut bir kavramdir - direksiyonu, motoru vardir ama kendisi surulemez. Ama "Otomobil" veya "Kamyon" gibi somut turleri surulebilir. Abstract class = arac tasarimi, concrete class = gercek arac.',
    whyExplanation: 'Abstract class, ortak davranisi ve yapisi olan ama tek basina kullanilmasi anlamsiz olan siniflari modellemek icindir. Kod tekrarina engel olur ve siniflararasi tutarliligi saglar.',
    wrongExample: 'public class Shape {\n    public double area() { return 0; } // anlamsiz varsayilan deger\n}\n// Her alt sinif override etmezse yanlis sonuc doner',
    wrongExampleExplanation: 'Concrete Shape nesnesi olusturulabilir ama area() anlamsiz. Alt sinifin override etmesi garanti degil.',
    rightExample: 'public abstract class Shape {\n    public abstract double area(); // alt sinif ZORUNDA\n    public void printInfo() {\n        System.out.println("Alan: " + area());\n    }\n}\npublic class Circle extends Shape {\n    private double radius;\n    public double area() { return Math.PI * radius * radius; }\n}',
    rightExampleExplanation: 'abstract area() alt sinifi uygulamaya zorlar. printInfo() ortak davranistir, tekrar yazilmaz. Shape nesnesi olusturulamaz - her shape somut olmali.',
  },
  COMPOSITION: {
    code: 'COMPOSITION', title: 'Composition (Bilesim)',
    realLifeAnalogy: 'Bir bilgisayar dusun: anakart, RAM, islemci ve disk parcalarindan olusur. Bilgisayar bunlarin SAHIBIDIR - bilgisayar yok edilirse parcalari da anlamsizlasir. Bu composition\'dir. Kalitim "X bir Y\'dir" (is-a), composition ise "X bir Y ICERIR" (has-a) iliskisidir.',
    whyExplanation: 'Kalitim (inheritance) katı bir bagimlilik olusturur ve sinif hiyerarsisini degistirmek zordur. Composition ile davranislari birlestirmek daha esnek ve surdurulebilir bir yapidir. "Favor composition over inheritance" modern OOP\'nin temel ilkesidir.',
    wrongExample: 'public class FlyingFish extends Bird {\n    // Yanlis: Balik kus degildir ama ucma davranisi lazim\n    // Kalitim ile yanlis hiyerarsi olusmustur\n}',
    wrongExampleExplanation: 'FlyingFish bir Bird degil. Kalitimla "is-a" iliskisi zorlanmis. ucma davranisi icin kus olmak gerekmez.',
    rightExample: 'public interface Flyable {\n    void fly();\n}\npublic class FlyingFish {\n    private FlyBehavior flyBehavior; // composition\n    public void fly() { flyBehavior.fly(); }\n}\n// Veya:\npublic class Engine {\n    public void start() { ... }\n}\npublic class Car {\n    private Engine engine; // Car HAS-A Engine\n    public void startCar() { engine.start(); }\n}',
    rightExampleExplanation: 'Car bir Engine ICERIR (has-a). Davranis delegasyonu yapilir. Engine degistirilebilir (ElectricEngine, DieselEngine).',
  },
  SPRING_BEAN_LIFECYCLE: {
    code: 'SPRING_BEAN_LIFECYCLE', title: 'Spring Bean Yasam Dongusu',
    realLifeAnalogy: 'Bir calisan dusun: ise alinir (instantiation), egitilir (dependency injection), calismaya baslar (initialization), is birakir (destruction). Spring Bean\'leri de ayni asamalardan gecer.',
    whyExplanation: 'Spring container bean\'leri yonetir. Lifecycle\'i anlamak, kaynaklari dogru zamanda acip kapatmak (DB baglantisi, cache, thread pool) icin kritiktir.',
    wrongExample: '@Component\npublic class CacheService {\n    private Cache cache;\n    // cache ne zaman initialize edilecek? ne zaman kapatilacak?\n    // yasam dongusu yonetimi eksik\n}',
    wrongExampleExplanation: 'Cache baslatilmamis, kapatilmamis. Memory leak veya NullPointerException riski var.',
    rightExample: '@Component\npublic class CacheService {\n    private Cache cache;\n\n    @PostConstruct\n    public void init() {\n        this.cache = new Cache();\n        cache.warmUp();\n    }\n\n    @PreDestroy\n    public void cleanup() {\n        cache.close();\n    }\n}',
    rightExampleExplanation: '@PostConstruct: Bean olusturulduktan sonra calisir (initialization). @PreDestroy: Bean yok edilmeden once calisir (cleanup). Kaynaklar duzgun yonetilir.',
  },
  JPA_RELATIONSHIPS: {
    code: 'JPA_RELATIONSHIPS', title: 'JPA Iliskileri (Entity Relationships)',
    realLifeAnalogy: 'Gercek dunyada nesneler birbirleriyle iliskilidir: Bir departmanda birden fazla calisan vardir (OneToMany), bir calisan bir departmana aittir (ManyToOne), bir ogrenci birden fazla ders alabilir ve bir ders birden fazla ogrenciye acilabilir (ManyToMany).',
    whyExplanation: 'Veritabani tablolari arasindaki iliskileri Java nesnelerine yansitmak icin JPA iliski annotation\'lari kullanilir. Dogru iliskiler, veri butunlugunu ve verimli sorgulari saglar.',
    wrongExample: '@Entity\npublic class Order {\n    private Long customerId; // sadece id tutma\n    // Customer nesnesine erisemezsin, JOIN yapamazsin\n}',
    wrongExampleExplanation: 'Sadece foreign key tutmak ORM avantajlarindan yoksun birakir. Navigation, lazy loading, cascade kullanilamaz.',
    rightExample: '@Entity\npublic class Order {\n    @ManyToOne(fetch = FetchType.LAZY)\n    @JoinColumn(name = "customer_id")\n    private Customer customer; // nesne iliskisi\n}\n@Entity\npublic class Customer {\n    @OneToMany(mappedBy = "customer")\n    private List<Order> orders; // bidirectional\n}',
    rightExampleExplanation: '@ManyToOne: Cok siparis bir musteriye aittir. @OneToMany(mappedBy): Iliskinin diger tarafi. FetchType.LAZY: Ihtiyac oldugunda yukle (performans). @JoinColumn: Foreign key sutununu belirtir.',
  },
  REST_BEST_PRACTICES: {
    code: 'REST_BEST_PRACTICES', title: 'REST API En Iyi Pratikleri',
    realLifeAnalogy: 'Bir restoran menusu dusun: Yemekler kategorilere ayrilmis (kaynak gruplama), her yemegin numarasi var (ID), garson siparis alir (POST), durumu sorar (GET), degistirir (PUT), iptal eder (DELETE). REST API de ayni mantikla calisir.',
    whyExplanation: 'Tutarli, anlasilir ve kullanilabilir API tasarimi hem gelistirici deneyimini hem de bakim kolayligini artirir. Standartlara uymak, API tuketicileri icin ogrenme egrisini dusurur.',
    wrongExample: '@PostMapping("/getUsers") // YANLIS: POST ile veri alma\n@GetMapping("/deleteUser/5") // YANLIS: GET ile silme\n@PostMapping("/api/v1/UserAccountCreation") // YANLIS: fiil ve CamelCase',
    wrongExampleExplanation: 'HTTP metotlari yanlis kullanilmis. URL\'de fiil var (REST kaynak-tabanlidir). CamelCase yerine kebab-case kullanilmali.',
    rightExample: '@GetMapping("/api/users")           // Tum kullanicilari listele\n@GetMapping("/api/users/{id}")      // Tek kullanici getir\n@PostMapping("/api/users")          // Yeni kullanici olustur\n@PutMapping("/api/users/{id}")      // Kullanici guncelle\n@DeleteMapping("/api/users/{id}")   // Kullanici sil\n@GetMapping("/api/users/{id}/orders") // Kullanicinin siparisleri',
    rightExampleExplanation: 'HTTP metotlari dogru kullanilmis (GET=oku, POST=olustur, PUT=guncelle, DELETE=sil). URL\'ler kaynak-tabanli ve kebab-case. Alt kaynaklar nested path ile ifade edilmis.',
  },
  TRANSACTION_MANAGEMENT: {
    code: 'TRANSACTION_MANAGEMENT', title: 'Transaction Yonetimi (@Transactional)',
    realLifeAnalogy: 'Banka havalesi dusun: Gondericiden para dusulur ve aliciya eklenir. Bu iki islem YA IKISI BIRDEN basarili olur, YA IKISI BIRDEN iptal olur. Yarisinda kalmak kabul edilemez. Transaction = "ya hep ya hic" garantisi.',
    whyExplanation: 'Birden fazla veritabani isleminin atomik olmasini saglar. Hata durumunda tum degisiklikler geri alinir (rollback). Veri tutarliligi icin kritiktir.',
    wrongExample: 'public void transfer(String from, String to, double amount) {\n    Account sender = repo.findByNumber(from);\n    sender.withdraw(amount);\n    repo.save(sender); // KAYDEDILDI\n    // Burada hata olursa? Para gitti ama aliciya gelmedi!\n    Account receiver = repo.findByNumber(to);\n    receiver.deposit(amount);\n    repo.save(receiver);\n}',
    wrongExampleExplanation: 'Islem ortasinda hata olursa gondericiden para duser ama aliciya ulasmaz. Veri tutarsizligi olusur.',
    rightExample: '@Transactional\npublic void transfer(String from, String to, double amount) {\n    Account sender = repo.findByNumber(from);\n    sender.withdraw(amount);\n    repo.save(sender);\n    Account receiver = repo.findByNumber(to);\n    receiver.deposit(amount);\n    repo.save(receiver);\n    // Herhangi bir hata \u2192 tum islemler rollback olur\n}',
    rightExampleExplanation: '@Transactional ile metot tek bir transaction icinde calisir. Hata olursa rollback yapilir. Gondericiden para dusulmus ama aliciya ulasmamissa, her sey geri alinir.',
  },
  VALIDATION_PATTERNS: {
    code: 'VALIDATION_PATTERNS', title: 'Validasyon Kaliplari (Validation Patterns)',
    realLifeAnalogy: 'Bir havaalanindan gecisi dusun: once kimlik kontrolu (null check), sonra bilet kontrolu (format), sonra guvenlik taramasi (is kurali). Her asama yanlis bir seyi yakalar ve net bir hata mesaji verir.',
    whyExplanation: 'Girdileri dogrulamak guvenlik, veri butunlugu ve kullanici deneyimi icin zorunludur. Katmanli validasyon (controller + service + entity) derinlemesine savunma saglar.',
    wrongExample: '@PostMapping("/transfer")\npublic void transfer(@RequestBody TransferRequest req) {\n    // Hicbir kontrol yok!\n    service.transfer(req.getFrom(), req.getTo(), req.getAmount());\n    // null, negatif, bos string hepsi kabul ediliyor\n}',
    wrongExampleExplanation: 'Validasyon olmadan null, negatif veya gecersiz degerler sisteme girer. NullPointerException, veri bozulmasi veya guvenlik aciklari olusur.',
    rightExample: '// DTO katmani - deklaratif validasyon\npublic class TransferRequest {\n    @NotBlank(message = "Gonderen hesap bos olamaz")\n    private String from;\n    @NotBlank(message = "Alici hesap bos olamaz")\n    private String to;\n    @NotNull @Positive(message = "Tutar pozitif olmali")\n    private Double amount;\n}\n\n// Controller - @Valid tetikler\n@PostMapping("/transfer")\npublic ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest req) {\n    service.transfer(req.getFrom(), req.getTo(), req.getAmount());\n    return ResponseEntity.ok().build();\n}\n\n// Service - is kurali validasyonu\npublic void transfer(...) {\n    if (from.equals(to)) throw new IllegalArgumentException("Ayni hesaba transfer yapilamaz");\n}',
    rightExampleExplanation: 'Uc katmanli validasyon: DTO\'da annotation ile format/null kontrol, Controller\'da @Valid ile tetikleme, Service\'te is kurali kontrolu. Her katman farkli tur hatalari yakalar.',
  },
}

const STEP_RULES: Record<number, { checks: any[], concepts: string[] }> = {
  1: {
    concepts: ['ENTITY', 'ID', 'GENERATED_VALUE', 'ENCAPSULATION', 'THIS_KEYWORD'],
    checks: [
      { id: 'has-entity', pat: '@Entity', onPass: '\u2705 @Entity dogru \u2014 sinif veritabani tablosuna eslenir.', onFail: '\u274C @Entity annotation eksik.', why: '@Entity Hibernate\'e bu sinifin bir tablo oldugunu soyler.', alternative: '@Table ile tablo adini ozellestirebilirsin.', realWorld: '@Entity sadece domain sinifina konur, DTO\'lara asla.' },
      { id: 'has-id', pat: '@Id', onPass: '\u2705 @Id ile primary key tanimlandi.', onFail: '\u274C @Id annotation eksik.', why: 'Her tabloda birincil anahtar zorunlu.', alternative: '@EmbeddedId composite key icin.', realWorld: 'Long veya UUID tercih edilir.' },
      { id: 'has-gv', pat: '@GeneratedValue', onPass: '\u2705 @GeneratedValue ile id otomatik uretilecek.', onFail: '\u26A0\uFE0F @GeneratedValue eksik.', why: 'Elle id \u2192 duplicate key riski.', alternative: 'IDENTITY, SEQUENCE, UUID stratejileri var.', realWorld: 'Postgres: IDENTITY veya SEQUENCE.' },
      { id: 'id-priv', regex: /private\s+\w+\s+id/i, onPass: '\u2705 id private \u2014 Encapsulation dogru.', onFail: '\u26A0\uFE0F id private olmali!', why: 'Kapsulleme ilkesi.', alternative: '', realWorld: 'Profesyonel standart.' },
      { id: 'name-priv', regex: /private\s+String\s+name/i, onPass: '\u2705 name private.', onFail: '\u26A0\uFE0F name private olmali.', why: 'Encapsulation.', alternative: '', realWorld: 'Standart pratik.' },
      { id: 'email-priv', regex: /private\s+String\s+email/i, onPass: '\u2705 email private.', onFail: '\u26A0\uFE0F email private olmali.', why: 'Hassas veri kapsullenmeli.', alternative: '', realWorld: '@Column(unique=true) eklenir.' },
      { id: 'getter', pat: 'getName(', onPass: '\u2705 getName() mevcut.', onFail: '\u26A0\uFE0F getName() eksik.', why: 'private alani okumak icin getter gerekir.', alternative: 'Lombok kullanilabilir.', realWorld: 'Framework bagimliligi.' },
      { id: 'this', pat: 'this.', onPass: '\u2705 this dogru kullanilmis.', onFail: '\u26A0\uFE0F this. kullanimi bekleniyor.', why: 'Shadowing onlenir.', alternative: '', realWorld: 'Her projede standart.' },
    ]
  },
  2: {
    concepts: ['ENTITY', 'ENCAPSULATION'],
    checks: [
      { id: 'has-entity', pat: '@Entity', onPass: '\u2705 @Entity dogru.', onFail: '\u274C @Entity eksik.', why: 'JPA entity zorunlu.', alternative: '', realWorld: '' },
      { id: 'manytoone', pat: '@ManyToOne', onPass: '\u2705 @ManyToOne iliskisi dogru.', onFail: '\u274C @ManyToOne eksik \u2014 User ile iliski kurulmali.', why: 'Bir kullanicinin birden fazla hesabi olabilir.', alternative: '@OneToOne tek hesapli senaryoda.', realWorld: 'Gercek bankalarda coklu hesap standart.' },
      { id: 'deposit', pat: 'deposit(', onPass: '\u2705 deposit metodu var.', onFail: '\u274C deposit metodu eksik.', why: 'Is mantigi entity icinde olmali (DDD).', alternative: 'Serviste de olabilir ama anemic model olur.', realWorld: 'Rich domain model tercih edilir.' },
      { id: 'withdraw', pat: 'withdraw(', onPass: '\u2705 withdraw metodu var.', onFail: '\u274C withdraw metodu eksik.', why: 'Bakiye kontrolu entity seviyesinde olmali.', alternative: '', realWorld: 'Domain invariant: bakiye negatife dusemez.' },
      { id: 'bal-priv', regex: /private\s+double\s+balance/i, onPass: '\u2705 balance private.', onFail: '\u26A0\uFE0F balance private olmali!', why: 'Disaridan dogrudan degistirilemez olmali.', alternative: '', realWorld: 'Finansal verilerde butunluk kritik.' },
    ]
  },
  3: {
    concepts: ['INTERFACE', 'DEPENDENCY_INJECTION'],
    checks: [
      { id: 'jpa-repo', pat: 'JpaRepository', onPass: '\u2705 JpaRepository dogru extend edilmis.', onFail: '\u274C JpaRepository\'den extend et.', why: 'CRUD metotlari otomatik gelir.', alternative: 'CrudRepository daha minimal.', realWorld: 'JpaRepository en yaygin tercih.' },
      { id: 'interface', pat: 'interface', onPass: '\u2705 Repository interface olarak tanimlanmis.', onFail: '\u274C interface olmali, class degil!', why: 'Spring proxy ile implementasyon uretir.', alternative: '', realWorld: 'Spring Data\'nin calisma mekanizmasi.' },
    ]
  },
  4: {
    concepts: ['DEPENDENCY_INJECTION', 'ENCAPSULATION'],
    checks: [
      { id: 'service', pat: '@Service', onPass: '\u2705 @Service annotation dogru.', onFail: '\u274C @Service eksik.', why: 'Spring bu sinifi bean olarak tanimlar.', alternative: '@Component da calisir ama @Service semantik.', realWorld: 'Katmanli mimaride standart.' },
      { id: 'repo', pat: 'AccountRepository', onPass: '\u2705 Repository bagimliligi var.', onFail: '\u274C AccountRepository bagimliligi eksik.', why: 'Service, Repository\'ye bagimli.', alternative: '', realWorld: 'Constructor injection onerilir.' },
      { id: 'deposit', pat: 'deposit(', onPass: '\u2705 deposit metodu var.', onFail: '\u274C deposit eksik.', why: 'Para yatirma is mantigi.', alternative: '', realWorld: 'Service transaction boundary\'dir.' },
      { id: 'tx', pat: '@Transactional', onPass: '\u2705 @Transactional ile transaction yonetimi.', onFail: '\u26A0\uFE0F @Transactional eksik \u2014 veri tutarsizligi riski!', why: 'Transfer ortasinda hata \u2192 rollback lazim.', alternative: 'Programmatic transaction da mumkun.', realWorld: 'Finansal islemlerde kritik.' },
    ]
  },
  5: {
    concepts: ['DEPENDENCY_INJECTION'],
    checks: [
      { id: 'rest', pat: '@RestController', onPass: '\u2705 @RestController dogru.', onFail: '\u274C @RestController eksik.', why: '@RestController = @Controller + @ResponseBody.', alternative: '', realWorld: 'REST API standardi.' },
      { id: 'mapping', pat: '@RequestMapping', onPass: '\u2705 Base path tanimli.', onFail: '\u26A0\uFE0F @RequestMapping ile base path tanimla.', why: 'Endpoint\'lerin ortak prefix\'i.', alternative: '', realWorld: 'Kaynak bazli URL yapisi.' },
      { id: 'post', pat: '@PostMapping', onPass: '\u2705 POST endpoint tanimli.', onFail: '\u274C @PostMapping eksik.', why: 'Durum degistiren islemler POST kullanir.', alternative: '', realWorld: 'REST: GET=oku, POST=olustur.' },
    ]
  },
  6: {
    concepts: ['ENCAPSULATION'],
    checks: [
      { id: 'extends-rt', pat: 'extends RuntimeException', onPass: '\u2705 Ozel exception olusturulmus.', onFail: '\u274C RuntimeException\'dan tureyen exception olustur.', why: 'Anlamli exception hata ayiklamayi kolaylastirir.', alternative: 'Checked exception da olabilir ama unchecked tercih edilir.', realWorld: 'Domain-specific exception standart.' },
      { id: 'advice', pat: '@RestControllerAdvice', onPass: '\u2705 Merkezi hata yonetimi.', onFail: '\u274C @RestControllerAdvice eksik.', why: 'Tek yerde tum hatalari yakala.', alternative: 'Her controller\'da ayri try-catch \u2192 tekrar.', realWorld: 'Production standardi.' },
      { id: 'handler', pat: '@ExceptionHandler', onPass: '\u2705 @ExceptionHandler tanimli.', onFail: '\u274C @ExceptionHandler eksik.', why: 'Her exception tipine ozel HTTP kodu dondurebilirsin.', alternative: '', realWorld: '404, 400, 500 gibi anlamli kodlar.' },
    ]
  },
  7: {
    concepts: ['ENCAPSULATION'],
    checks: [
      { id: 'notnull', pat: '@NotNull', onPass: '\u2705 @NotNull dogrulama eklendi.', onFail: '\u274C @NotNull eksik.', why: 'Null gonderilirse anlamli hata mesaji doner.', alternative: 'Optional da kullanilabilir.', realWorld: 'API girdisi her zaman dogrulanmali.' },
      { id: 'positive', pat: '@Positive', onPass: '\u2705 @Positive ile negatif tutar engellendi.', onFail: '\u26A0\uFE0F @Positive ekle.', why: 'Negatif tutar mantik hatasi.', alternative: 'Serviste if kontrolu de olabilir.', realWorld: 'Cift katmanli validasyon standart.' },
    ]
  },
  8: {
    concepts: ['INTERFACE', 'DEPENDENCY_INJECTION'],
    checks: [
      { id: 'test', pat: '@Test', onPass: '\u2705 @Test annotation ile test metodu tanimli.', onFail: '\u274C @Test annotation eksik.', why: 'JUnit @Test olmadan metodu calistirmaz.', alternative: '@ParameterizedTest ile parametrik test.', realWorld: 'Her metot icin en az 2 test: happy + edge.' },
      { id: 'assert', pat: 'assert', onPass: '\u2705 Assert ile sonuc dogrulandi.', onFail: '\u274C Assert eksik \u2014 sonucu dogrula.', why: 'Assert olmadan test anlamsiz.', alternative: 'AssertJ daha okunabilir.', realWorld: 'Her test en az bir assertion icermeli.' },
    ]
  },
  9: {
    concepts: ['ENTITY', 'ID', 'GENERATED_VALUE', 'ENCAPSULATION', 'JPA_RELATIONSHIPS'],
    checks: [
      { id: 'has-entity', pat: '@Entity', onPass: '\u2705 @Entity dogru.', onFail: '\u274C @Entity annotation eksik.', why: 'JPA entity sinifi icin zorunlu.', alternative: '', realWorld: 'Her domain sinifina @Entity eklenir.' },
      { id: 'has-id', pat: '@Id', onPass: '\u2705 @Id tanimli.', onFail: '\u274C @Id annotation eksik.', why: 'Primary key zorunlu.', alternative: '', realWorld: '' },
      { id: 'has-gv', pat: '@GeneratedValue', onPass: '\u2705 @GeneratedValue var.', onFail: '\u26A0\uFE0F @GeneratedValue eksik.', why: 'Otomatik id uretimi icin.', alternative: '', realWorld: '' },
      { id: 'name-priv', regex: /private\s+String\s+name/i, onPass: '\u2705 name private.', onFail: '\u26A0\uFE0F name private olmali.', why: 'Encapsulation.', alternative: '', realWorld: '' },
      { id: 'price', regex: /private\s+BigDecimal\s+price/i, onPass: '\u2705 price BigDecimal tipinde.', onFail: '\u26A0\uFE0F price alani BigDecimal tipinde olmali (para hesaplamasi hassasiyeti).', why: 'double/float para hesaplamalarinda yuvarlama hatasi verir.', alternative: '', realWorld: 'Tum finansal uygulamalarda BigDecimal standart.' },
      { id: 'stock', regex: /private\s+int\s+stockQuantity/i, onPass: '\u2705 stockQuantity tanimli.', onFail: '\u26A0\uFE0F stockQuantity alani eksik.', why: 'Stok takibi icin gerekli.', alternative: '', realWorld: '' },
      { id: 'decrease', pat: 'decreaseStock(', onPass: '\u2705 decreaseStock metodu var.', onFail: '\u274C decreaseStock metodu eksik - stok azaltma entity icinde olmali.', why: 'Domain invariant: stok negatife dusemez.', alternative: '', realWorld: 'DDD: is kurali entity\'de.' },
    ]
  },
  10: {
    concepts: ['ENTITY', 'JPA_RELATIONSHIPS', 'COMPOSITION'],
    checks: [
      { id: 'has-entity', pat: '@Entity', onPass: '\u2705 @Entity dogru.', onFail: '\u274C @Entity eksik.', why: 'JPA entity zorunlu.', alternative: '', realWorld: '' },
      { id: 'onetomany', pat: '@OneToMany', onPass: '\u2705 @OneToMany iliskisi tanimli.', onFail: '\u274C @OneToMany eksik - bir kategoride birden fazla urun olabilir.', why: 'Kategori-urun iliskisi OneToMany\'dir.', alternative: '', realWorld: 'E-ticaret sistemlerinde standart.' },
      { id: 'manytoone', pat: '@ManyToOne', onPass: '\u2705 @ManyToOne iliskisi dogru.', onFail: '\u274C @ManyToOne eksik - Product tarafinda Category iliskisi olmali.', why: 'Bidirectional iliski icin her iki tarafta tanimlanmali.', alternative: '', realWorld: '' },
      { id: 'mappedby', pat: 'mappedBy', onPass: '\u2705 mappedBy ile iliski sahibi belirtilmis.', onFail: '\u26A0\uFE0F mappedBy eksik - iliskinin sahibini belirt.', why: 'mappedBy olmadan JPA ek bir join tablosu olusturur.', alternative: '', realWorld: '' },
    ]
  },
  11: {
    concepts: ['INTERFACE', 'DEPENDENCY_INJECTION', 'SPRING_BEAN_LIFECYCLE'],
    checks: [
      { id: 'jpa-repo', pat: 'JpaRepository', onPass: '\u2705 JpaRepository extend edilmis.', onFail: '\u274C JpaRepository\'den extend et.', why: 'CRUD metotlari otomatik gelir.', alternative: '', realWorld: '' },
      { id: 'interface', pat: 'interface', onPass: '\u2705 interface olarak tanimli.', onFail: '\u274C interface olmali.', why: 'Spring Data proxy mekanizmasi.', alternative: '', realWorld: '' },
      { id: 'find-method', pat: 'findBy', onPass: '\u2705 Ozel sorgu metodu tanimli.', onFail: '\u26A0\uFE0F findBy ile ozel sorgu metodu ekle.', why: 'Spring Data metot adindan sorgu uretir.', alternative: '@Query ile custom JPQL.', realWorld: '' },
    ]
  },
  12: {
    concepts: ['DEPENDENCY_INJECTION', 'TRANSACTION_MANAGEMENT', 'ENCAPSULATION'],
    checks: [
      { id: 'service', pat: '@Service', onPass: '\u2705 @Service annotation dogru.', onFail: '\u274C @Service eksik.', why: 'Spring bean tanimlamasi.', alternative: '', realWorld: '' },
      { id: 'repo', pat: 'ProductRepository', onPass: '\u2705 ProductRepository bagimliligi var.', onFail: '\u274C ProductRepository bagimliligi eksik.', why: 'Service repository\'ye bagimli.', alternative: '', realWorld: '' },
      { id: 'tx', pat: '@Transactional', onPass: '\u2705 @Transactional var.', onFail: '\u26A0\uFE0F @Transactional eksik - stok guncelleme atomik olmali.', why: 'Veri tutarliligi icin transaction zorunlu.', alternative: '', realWorld: '' },
      { id: 'create', pat: 'createProduct(', onPass: '\u2705 createProduct metodu var.', onFail: '\u274C createProduct metodu eksik.', why: 'Urun olusturma is mantigi.', alternative: '', realWorld: '' },
    ]
  },
  13: {
    concepts: ['DEPENDENCY_INJECTION', 'REST_BEST_PRACTICES'],
    checks: [
      { id: 'rest', pat: '@RestController', onPass: '\u2705 @RestController dogru.', onFail: '\u274C @RestController eksik.', why: 'REST API controller.', alternative: '', realWorld: '' },
      { id: 'mapping', pat: '@RequestMapping', onPass: '\u2705 Base path tanimli.', onFail: '\u26A0\uFE0F @RequestMapping ekle.', why: 'Endpoint gruplama.', alternative: '', realWorld: '' },
      { id: 'get', pat: '@GetMapping', onPass: '\u2705 GET endpoint tanimli.', onFail: '\u274C @GetMapping eksik - okuma islemleri GET kullanir.', why: 'REST standardi.', alternative: '', realWorld: '' },
      { id: 'post', pat: '@PostMapping', onPass: '\u2705 POST endpoint tanimli.', onFail: '\u274C @PostMapping eksik - olusturma islemleri POST kullanir.', why: 'REST standardi.', alternative: '', realWorld: '' },
      { id: 'valid', pat: '@Valid', onPass: '\u2705 @Valid ile validasyon tetikleniyor.', onFail: '\u26A0\uFE0F @Valid eksik - request body dogrulanmiyor.', why: 'Validasyon annotation\'lari @Valid olmadan calismaz.', alternative: '', realWorld: '' },
    ]
  },
  14: {
    concepts: ['ENCAPSULATION', 'VALIDATION_PATTERNS'],
    checks: [
      { id: 'notblank', pat: '@NotBlank', onPass: '\u2705 @NotBlank ile bos string engellendi.', onFail: '\u274C @NotBlank eksik - urun adi bos olamaz.', why: 'Bos string gecerli bir urun adi degildir.', alternative: '@NotEmpty de kullanilabilir.', realWorld: '' },
      { id: 'notnull', pat: '@NotNull', onPass: '\u2705 @NotNull dogrulama var.', onFail: '\u274C @NotNull eksik.', why: 'Null degerleri engellemek icin.', alternative: '', realWorld: '' },
      { id: 'positive', pat: '@Positive', onPass: '\u2705 @Positive ile negatif fiyat engellendi.', onFail: '\u26A0\uFE0F @Positive ekle - fiyat pozitif olmali.', why: 'Negatif fiyat is kurali ihlalidir.', alternative: '', realWorld: '' },
      { id: 'mapper', pat: 'Mapper', onPass: '\u2705 Mapper sinifi/metodu var.', onFail: '\u26A0\uFE0F Mapper eksik - entity-DTO donusumu icin Mapper kullan.', why: 'Entity\'yi dogrudan API\'ye acmak guvenlik riski.', alternative: 'MapStruct kutuphanesi.', realWorld: '' },
    ]
  },
  15: {
    concepts: ['ENTITY', 'ID', 'GENERATED_VALUE', 'ENCAPSULATION'],
    checks: [
      { id: 'has-entity', pat: '@Entity', onPass: '\u2705 @Entity dogru.', onFail: '\u274C @Entity annotation eksik.', why: 'JPA entity zorunlu.', alternative: '', realWorld: '' },
      { id: 'has-id', pat: '@Id', onPass: '\u2705 @Id tanimli.', onFail: '\u274C @Id eksik.', why: 'Primary key zorunlu.', alternative: '', realWorld: '' },
      { id: 'has-gv', pat: '@GeneratedValue', onPass: '\u2705 @GeneratedValue var.', onFail: '\u26A0\uFE0F @GeneratedValue eksik.', why: 'Otomatik id uretimi.', alternative: '', realWorld: '' },
      { id: 'title-priv', regex: /private\s+String\s+title/i, onPass: '\u2705 title private.', onFail: '\u26A0\uFE0F title private olmali.', why: 'Encapsulation.', alternative: '', realWorld: '' },
      { id: 'isbn-priv', regex: /private\s+String\s+isbn/i, onPass: '\u2705 isbn private.', onFail: '\u26A0\uFE0F isbn private olmali.', why: 'Encapsulation.', alternative: '', realWorld: '' },
      { id: 'available', regex: /private\s+boolean\s+available/i, onPass: '\u2705 available alani tanimli.', onFail: '\u26A0\uFE0F available alani eksik - kitap durumu takibi icin gerekli.', why: 'Kitabin musait olup olmadigini belirler.', alternative: '', realWorld: '' },
      { id: 'mark-method', pat: 'markAs', onPass: '\u2705 Durum degistirme metodu var.', onFail: '\u26A0\uFE0F markAsUnavailable/markAsAvailable metotlari eksik.', why: 'Encapsulation - durum degisikligi metotla yapilmali.', alternative: '', realWorld: '' },
    ]
  },
  16: {
    concepts: ['ENTITY', 'ENCAPSULATION', 'JPA_RELATIONSHIPS'],
    checks: [
      { id: 'has-entity', pat: '@Entity', onPass: '\u2705 @Entity dogru.', onFail: '\u274C @Entity eksik.', why: 'JPA entity zorunlu.', alternative: '', realWorld: '' },
      { id: 'has-id', pat: '@Id', onPass: '\u2705 @Id tanimli.', onFail: '\u274C @Id eksik.', why: 'Primary key zorunlu.', alternative: '', realWorld: '' },
      { id: 'name-priv', regex: /private\s+String\s+name/i, onPass: '\u2705 name private.', onFail: '\u26A0\uFE0F name private olmali.', why: 'Encapsulation.', alternative: '', realWorld: '' },
      { id: 'email-priv', regex: /private\s+String\s+email/i, onPass: '\u2705 email private.', onFail: '\u26A0\uFE0F email private olmali.', why: 'Hassas veri.', alternative: '', realWorld: '' },
      { id: 'localdate', pat: 'LocalDate', onPass: '\u2705 LocalDate kullanilmis.', onFail: '\u26A0\uFE0F LocalDate kullan - Java 8+ tarih API\'si.', why: 'Date yerine LocalDate modern Java standardi.', alternative: '', realWorld: '' },
    ]
  },
  17: {
    concepts: ['INTERFACE', 'DEPENDENCY_INJECTION'],
    checks: [
      { id: 'jpa-repo', pat: 'JpaRepository', onPass: '\u2705 JpaRepository extend edilmis.', onFail: '\u274C JpaRepository\'den extend et.', why: 'CRUD metotlari otomatik gelir.', alternative: '', realWorld: '' },
      { id: 'interface', pat: 'interface', onPass: '\u2705 interface olarak tanimli.', onFail: '\u274C interface olmali.', why: 'Spring Data proxy mekanizmasi.', alternative: '', realWorld: '' },
      { id: 'find-isbn', pat: 'findByIsbn', onPass: '\u2705 ISBN ile arama metodu var.', onFail: '\u26A0\uFE0F findByIsbn metodu ekle.', why: 'ISBN benzersiz - tekil arama icin.', alternative: '', realWorld: '' },
      { id: 'find-available', pat: 'findByAvailable', onPass: '\u2705 Musait kitap sorgusu var.', onFail: '\u26A0\uFE0F findByAvailableTrue metodu ekle.', why: 'Musait kitaplari listelemek temel islevdir.', alternative: '', realWorld: '' },
    ]
  },
  18: {
    concepts: ['DEPENDENCY_INJECTION', 'TRANSACTION_MANAGEMENT', 'ENCAPSULATION'],
    checks: [
      { id: 'service', pat: '@Service', onPass: '\u2705 @Service annotation dogru.', onFail: '\u274C @Service eksik.', why: 'Spring bean tanimlamasi.', alternative: '', realWorld: '' },
      { id: 'book-repo', pat: 'BookRepository', onPass: '\u2705 BookRepository bagimliligi var.', onFail: '\u274C BookRepository bagimliligi eksik.', why: 'Kitap verilerine erisim icin gerekli.', alternative: '', realWorld: '' },
      { id: 'tx', pat: '@Transactional', onPass: '\u2705 @Transactional var.', onFail: '\u26A0\uFE0F @Transactional eksik - odunc islemleri atomik olmali.', why: 'Veri tutarliligi.', alternative: '', realWorld: '' },
      { id: 'borrow', pat: 'borrowBook(', onPass: '\u2705 borrowBook metodu var.', onFail: '\u274C borrowBook metodu eksik.', why: 'Kitap odunc alma is mantigi.', alternative: '', realWorld: '' },
      { id: 'return', pat: 'returnBook(', onPass: '\u2705 returnBook metodu var.', onFail: '\u274C returnBook metodu eksik.', why: 'Kitap iade is mantigi.', alternative: '', realWorld: '' },
    ]
  },
  19: {
    concepts: ['DEPENDENCY_INJECTION', 'REST_BEST_PRACTICES'],
    checks: [
      { id: 'rest', pat: '@RestController', onPass: '\u2705 @RestController dogru.', onFail: '\u274C @RestController eksik.', why: 'REST API controller.', alternative: '', realWorld: '' },
      { id: 'mapping', pat: '@RequestMapping', onPass: '\u2705 Base path tanimli.', onFail: '\u26A0\uFE0F @RequestMapping ekle.', why: 'Endpoint gruplama.', alternative: '', realWorld: '' },
      { id: 'post', pat: '@PostMapping', onPass: '\u2705 POST endpoint tanimli.', onFail: '\u274C @PostMapping eksik.', why: 'Durum degistiren islemler POST kullanir.', alternative: '', realWorld: '' },
      { id: 'get', pat: '@GetMapping', onPass: '\u2705 GET endpoint tanimli.', onFail: '\u274C @GetMapping eksik - musait kitaplari listelemek icin GET kullan.', why: 'Okuma islemleri GET kullanir.', alternative: '', realWorld: '' },
      { id: 'service-dep', pat: 'BorrowService', onPass: '\u2705 BorrowService bagimliligi var.', onFail: '\u274C BorrowService bagimliligi eksik.', why: 'Controller is mantigini service\'e devreder.', alternative: '', realWorld: '' },
    ]
  },
}

function evaluateCode(stepId: number, code: string) {
  const rules = STEP_RULES[stepId] || STEP_RULES[1]
  const feedback: any[] = []
  const normalized = code.replace(/\s+/g, ' ').trim()

  let passCount = 0
  const totalChecks = rules.checks.length

  rules.checks.forEach((c: any) => {
    let passed: boolean
    if (c.regex) {
      passed = c.regex.test(normalized)
      if (!passed) {
        const fallbackPattern = c.regex.source
          .replace(/\\s\+/g, '.*')
          .replace(/\\w\+/g, '.*')
        try {
          const fallback = new RegExp(fallbackPattern, 'i')
          passed = fallback.test(normalized)
        } catch (_) {
          // fallback regex failed, keep passed as false
        }
      }
    } else {
      passed = normalized.toLowerCase().includes(c.pat.toLowerCase())
    }
    if (passed) passCount++
    feedback.push({
      ruleId: c.id,
      status: passed ? 'PASS' : 'FAIL',
      message: passed ? c.onPass : c.onFail,
      why: c.why || null,
      alternative: c.alternative || null,
      realWorld: c.realWorld || null,
    })
  })

  const passRatio = passCount / totalChecks
  const allPass = passRatio >= 0.7

  return {
    passed: allPass,
    feedback,
    xpEarned: allPass ? 20 : (passCount >= totalChecks * 0.5 ? 10 : 0),
    conceptsToReview: allPass ? rules.concepts : [],
    passRatio: Math.round(passRatio * 100),
  }
}

let hintCounters: Record<number, number> = {}

function delay(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export const mockApi = {
  async get(url: string) {
    await delay(300)

    if (url === '/assessment/questions') {
      return { data: ASSESSMENT_QUESTIONS.map(q => ({ ...q, correctIndex: -1, weight: 0 })) }
    }
    if (url === '/projects') {
      return { data: PROJECTS }
    }
    if (url === '/progress') {
      return { data: { totalXp: 0, currentStreak: 0, badges: null, completedSteps: 0, totalSteps: 8 } }
    }
    if (url.startsWith('/concepts/')) {
      const code = url.split('/concepts/')[1]?.toUpperCase()
      return { data: CONCEPTS[code] || CONCEPTS['ENTITY'] }
    }
    if (url === '/learning-path') {
      return { data: { level: 'BEGINNER', recommendedArchitecture: 'LAYERED', chosenArchitecture: 'LAYERED' } }
    }
    if (url === '/my-tasks') {
      return { data: [] }
    }
    if (url.match(/\/steps\/(\d+)\/hints/)) {
      const stepId = parseInt(url.match(/\/steps\/(\d+)\/hints/)?.[1] || '1')
      const available = HINTS[stepId] || HINTS[1]
      const count = Math.min(hintCounters[stepId] || 0, available.length)
      const levels = ['SMALL', 'GUIDE', 'CODE', 'SOLUTION']
      return {
        data: {
          revealedHints: available.slice(0, count).map((content, index) => ({
            hintLevel: levels[index], content, xpPenalty: index < 2 ? 0 : 2,
            remainingHints: Math.max(0, available.length - index - 1),
          })),
          remainingHints: Math.max(0, available.length - count),
        }
      }
    }

    return { data: null }
  },

  async post(url: string, body?: any) {
    await delay(400)

    if (url === '/auth/register') {
      return { data: MOCK_USER }
    }
    if (url === '/auth/login') {
      return { data: MOCK_USER }
    }
    if (url === '/assessment/submit') {
      const answers: number[] = body?.answers || []
      let score = 0
      answers.forEach((a: number, i: number) => { if (a === CORRECT_ANSWERS[i]) score++ })
      const pct = score / CORRECT_ANSWERS.length
      const level = pct < 0.35 ? 'BEGINNER' : pct < 0.7 ? 'INTERMEDIATE' : 'ADVANCED'
      const arch = level === 'BEGINNER' ? 'LAYERED' : level === 'INTERMEDIATE' ? 'MODULAR_MONOLITH' : 'CLEAN'
      const reasonings: Record<string, string> = {
        BEGINNER: 'Katmanli Mimari (Controller \u2192 Service \u2192 Repository) en anlasilir baslangictir. Sorumluluklari net bir sekilde ayirarak temiz kod yazma aliskanligi kazanirsin. Ilk projende katmanlar arasi veri akisini ogrenecek, her katmanin tek bir sorumlulugu oldugunu goreceksin. Bu temel, ileride daha karmasik mimarilere gecisini kolaylastiracak.',
        INTERMEDIATE: 'Moduler Monolith, modul sinirlari ve bagimlilik yonetimini ogretir. Her modul kendi icinde bagimsiz calisir ama ayni uygulama icinde deploy edilir. Hexagonal Architecture\'a gecis icin guclu bir temel olusturur. Domain-driven design prensiplerini pratikte uygulayacaksin.',
        ADVANCED: 'Clean Architecture, bagimlilik kurali ile is mantigini altyapidan tamamen ayirir. Use case\'ler merkezdedir, framework ve veritabani detaylari disaridadir. Gercek projelerde olceklenebilir ve test edilebilir sistemler kurarsin. SOLID prensiplerinin tamamini ileri seviyede uygulayacaksin.'
      }
      return {
        data: {
          level,
          score: Math.round(pct * 100) / 100,
          recommendedArchitecture: arch,
          architectureReasoning: reasonings[level]
        }
      }
    }
    if (url.match(/\/projects\/(\w+)\/start/)) {
      const projectCode = url.match(/\/projects\/(\w+)\/start/)?.[1]?.toUpperCase()
      if (projectCode === 'ECOMMERCE') {
        return { data: ECOMMERCE_TASKS }
      }
      if (projectCode === 'LIBRARY') {
        return { data: LIBRARY_TASKS }
      }
      return { data: BANK_TASKS }
    }
    if (url.match(/\/steps\/(\d+)\/evaluate/)) {
      const stepId = parseInt(url.match(/\/steps\/(\d+)\/evaluate/)?.[1] || '1')
      const code = body?.code || ''
      return { data: evaluateCode(stepId, code) }
    }
    if (url.match(/\/steps\/(\d+)\/hint/)) {
      const stepId = parseInt(url.match(/\/steps\/(\d+)\/hint/)?.[1] || '1')
      const hints = HINTS[stepId] || HINTS[1]
      const count = hintCounters[stepId] || 0
      const idx = Math.min(count, hints.length - 1)
      hintCounters[stepId] = Math.min(count + 1, hints.length)
      const levels = ['SMALL', 'GUIDE', 'CODE', 'SOLUTION']
      return {
        data: {
          hintLevel: levels[idx],
          content: hints[idx],
          xpPenalty: count >= hints.length ? 0 : 2,
          remainingHints: Math.max(0, hints.length - count - 1)
        }
      }
    }

    return { data: null }
  },

  async patch(url: string, body?: any) {
    await delay(200)
    return { data: body }
  },

  async put(url: string, body?: any) {
    await delay(200)
    return { data: body }
  },

  async delete(url: string) {
    await delay(200)
    return { data: null }
  }
}
