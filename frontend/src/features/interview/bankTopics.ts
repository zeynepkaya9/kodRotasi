export type StudyLevel = 'Başlangıç' | 'Orta' | 'İleri'
export interface BankTopic {
  id: string; title: string; category: string; minutes: number
  notes: Record<StudyLevel, string>; question: string; answer: string
  checklist: string[]; exercise: string; followUp: string
  quiz: { question: string; options: string[]; correct: number; explanation: string }
  source?: { label: string; url: string }
}

export const bankTopics: BankTopic[] = [
  {
    id: 'java', title: 'Java, OOP ve para modeli', category: 'Temel', minutes: 30,
    notes: {
      Başlangıç: 'Sınıf veri ve davranışı bir araya getirir. Kapsülleme, bakiyeyi doğrudan değiştirmek yerine deposit/withdraw gibi kurallı metotlarla değiştirmektir. Interface bir sözleşme, composition başka nesnelerin yeteneklerini kullanmaktır. Para için BigDecimal ve açık para birimi kullan.',
      Orta: 'BigDecimal("0.1") ile ondalık değeri korursun; new BigDecimal(0.1) double temsil hatasını taşır. equals ölçeği de karşılaştırır; compareTo sayısal karşılaştırmadır. HashMap anahtarı için equals/hashCode tutarlı ve anahtar değişmez olmalı. Stream yan etkilerini ve Optional boş durumunu açık ele al.',
      İleri: 'Money değer nesnesinin ölçek, rounding ve currency kurallarını tasarla. Singleton Spring bean içindeki değişken alanlar eşzamanlı isteklerde paylaşılır. synchronized tek JVM içinde etkilidir; birden fazla instance için DB bütünlüğü gerekir. Algoritma karmaşıklığını ve bellek maliyetini açıklayabil.'
    },
    question: '100,00 TL bakiyeden 10,10 TL düşeceksin. Veri tipini, doğrulamaları ve testi anlat.',
    answer: 'BigDecimal değerlerini String ile oluştururum. Pozitif tutar, para birimi, desteklenen ölçek ve yeterli bakiye kurallarını uygularım. 2 basamak bu TL örneğinin kuralıdır; bütün para birimlerine genellenmez. 89,90 sonucunu compareTo ile doğrular, null/sıfır/negatif/fazla hassasiyet durumlarını test ederim. Eşzamanlı yazma koruması ayrıca gerekir.',
    checklist: ['Para tipi ve rounding politikasını açıkladım', 'Null, negatif ve hassasiyet testlerini saydım', 'Kapsülleme ile yetkilendirmenin farkını anlattım'],
    exercise: 'Banka projesinin hesap görevini ve yeni TransferCommand görevini çöz. Ardından HashMap ile işlem kimliklerindeki tekrarları O(n) zamanda bulmayı kâğıtta kodla.',
    followUp: 'BigDecimal("1.0") ve BigDecimal("1.00") HashSet içinde neden farklı eleman olabilir?',
    quiz: { question: 'BigDecimal için sayısal eşitliği hangisi kontrol eder?', options: ['a == b', 'a.compareTo(b) == 0', 'Her zaman a.equals(b)'], correct: 1, explanation: 'compareTo ölçek farkından bağımsız sayısal karşılaştırır; equals ölçeği de dikkate alır.' }
  },
  {
    id: 'spring', title: 'Spring ve transaction sınırı', category: 'Temel', minutes: 30,
    notes: {
      Başlangıç: 'Controller isteği alır, service iş akışını yürütür, repository veriye erişir. Constructor injection bağımlılıkları dışarıdan verir. Transaction, aynı veritabanındaki birden fazla yazmanın birlikte commit veya rollback edilmesini sağlar.',
      Orta: 'Transferde borç ve alacak aynı transaction içinde olmalıdır. Spring varsayılan olarak RuntimeException ve Error için rollback yapar; checked exception için kural belirlenir. Hata yakalayıp gizlemek istenmeyen commit doğurabilir. Proxy modunda aynı nesnenin kendi metodunu çağırması yeni @Transactional davranışını tetiklemez.',
      İleri: 'REQUIRED mevcut transaction’a katılır; REQUIRES_NEW bağımsız transaction açar ve ek bağlantı gerektirebilir. Uzun HTTP çağrılarını transaction içinde bekletmek kilit/connection pool baskısı oluşturur. Annotation, farklı servislerin DB’lerini otomatik tek transaction yapmaz.'
    },
    question: 'Para gönderen hesaptan düştü, alıcıya yazılırken hata oldu. Ne yaparsın?',
    answer: 'İki DB yazmasını aynı transaction sınırına alırım; hata rollback kuralına uygun biçimde dışarı taşınır. Gerçek DB ile ikinci yazmada hata üreten testte iki bakiyenin de değişmediğini doğrularım. Servisler ayrı DB kullanıyorsa yerel transaction yetmez; durum makinesi ve telafi/uzlaşma akışı tasarlarım.',
    checklist: ['Transaction sınırını çizdim', 'Rollback ve self-invocation tuzağını anlattım', 'Gerçek DB ile testi tarif ettim'],
    exercise: 'Mevcut AccountService görevinde transaction’ın hangi metotta başladığını işaretle. Checked exception ve yakalanan exception için iki ayrı test senaryosu yaz.',
    followUp: 'Bir bildirim servisi çökerse tamamlanmış transferi geri almak doğru mu?',
    quiz: { question: 'Varsayılan proxy modunda this.transfer() çağrısı ne yapar?', options: ['Her durumda yeni transaction açar', 'Kafka transaction’ı açar', 'Kendi başına transactional proxy’den geçmez'], correct: 2, explanation: 'Self-invocation proxy’yi atlar. Dışarıdan proxied bean çağrısı veya ayrı service sınırı gerekir.' },
    source: {label:'Spring transaction davranışı',url:'https://docs.spring.io/spring/reference/6.2/data-access/transaction/declarative/annotations.html'}
  },
  {
    id: 'concurrency', title: 'Eşzamanlılık ve çift harcama', category: 'Banka', minutes: 35,
    notes: {
      Başlangıç: 'İki istek aynı anda 100 TL görüp 80 TL çekmeye çalışabilir. Her istek tek başına geçerli görünse de toplam talep bakiyeyi aşar. Transaction ve eşzamanlılık kontrolü farklı sorunları çözer.',
      Orta: '@Version optimistic locking eski sürümle yazmayı reddeder. Pessimistic locking satırı transaction boyunca kilitler. Alternatif olarak koşullu UPDATE ve etkilenen satır sayısı kullanılabilir. Conflict sonrası yeni transaction içinde güncel bakiyeyi okuyup tüm kuralları yeniden kontrol et.',
      İleri: 'İki hesap kilitleniyorsa tutarlı ID sırası deadlock riskini azaltır; timeout ve sınırlı retry yine gerekir. Isolation seviyesi ve gerçek SQL davranışını ölç. Retry yalnız tekrar çalıştırılması güvenli operasyonlarda uygulanır; dış yan etkiler tekrar üretilemez.'
    },
    question: 'İki uygulama instance’ı aynı hesaptan para çekiyor. synchronized yeterli mi?',
    answer: 'Hayır, JVM’ler ortak kilit paylaşmaz. DB’de version kontrolü, satır kilidi veya atomik koşullu yazma gerekir. İki ayrı bağlantıyı bariyerle aynı anda başlatır; 100 TL’den iki kez 80 TL çekimde sadece bir başarı ve son bakiyenin 20 TL olduğunu test ederim.',
    checklist: ['Lost update senaryosunu çizdim', 'Optimistic ve pessimistic seçenekleri karşılaştırdım', 'İki bağlantılı concurrency testi anlattım'],
    exercise: 'A→B ve B→A transferlerinin kilit sırasını çiz. Her iki akışta küçük hesap ID’sini önce kilitlemenin etkisini açıkla.',
    followUp: 'Version conflict sonrası aynı eski nesneyi tekrar save etmek neden yanlış?',
    quiz: {question:'@Transactional tek başına neyi garanti etmez?',options:['Aynı DB transaction’ında atomik commit', 'Her izolasyon seviyesinde lost update’in önlenmesini', 'Rollback kurallarının uygulanmasını'],correct:1,explanation:'İzolasyon ve güncelleme yöntemi önemlidir; eşzamanlılık stratejisi ayrıca seçilmelidir.'}
  },
  {
    id: 'idempotency', title: 'Tekrarlanan transfer isteği', category: 'Banka', minutes: 25,
    notes: {
      Başlangıç: 'Mobil uygulama yanıt alamadığında isteği tekrar gönderebilir. Idempotency, aynı iş isteğinin ikinci kez para hareketi üretmemesidir; aynı isteğin sonucu tekrar döner.',
      Orta: 'Anahtarı kullanıcı ve operasyon kapsamında sakla. İstek özetini, durumu ve sonucu kaydet; aynı anahtar farklı tutarla gelirse reddet. Unique constraint yarışan istekleri engeller. Önce exists sonra insert yapmak tek başına yarış güvenliği sağlamaz.',
      İleri: 'İstek kaydı, hareketler ve sonuç mümkünse aynı DB transaction’ında yazılır. İşlem sürüyor, tamamlandı ve başarısız durumlarını açık modelle. Retention süresi ve expired key tekrar kullanım politikası belirle. Redis TTL dolması kalıcı finansal tekillik garantisinin yerine geçmez.'
    },
    question:'Transfer başarılı ama HTTP yanıtı kayboldu. Retry geldiğinde ne dönersin?',
    answer:'Kimliği doğrulanmış kullanıcıya bağlı key ve istek özetiyle tamamlanmış sonucu bulup aynı transfer kimliğini döndürürüm. Yeniden para düşmem. Farklı payload için conflict dönerim; devam eden istekte bekleme veya işlem durumu endpoint’i kullanırım.',
    checklist:['Key kapsamını ve payload kontrolünü anlattım','DB unique kısıtını belirttim','Yanıt kaybı ve eşzamanlı retry testlerini anlattım'],
    exercise:'POST /transfers için aynı key/aynı gövde, aynı key/farklı gövde ve farklı kullanıcı/aynı key sonuçlarını tabloya yaz.',
    followUp:'Key’i sadece istemci hafızasında tutarsan uygulama yeniden başladığında ne olur?',
    quiz:{question:'Hangi kontrol yarışan isteklerde kalıcı tekillik sağlar?',options:['Önce SELECT exists çalıştırmak','Frontend butonunu kapatmak','Uygun kapsamlı DB unique constraint ve transaction'],correct:2,explanation:'Buton ve SELECT yararlıdır ama eşzamanlı istekleri kalıcı biçimde tekilleştirmez.'}
  },
  {
    id:'sql',title:'SQL, JPA ve performans',category:'Temel',minutes:35,
    notes:{Başlangıç:'Primary key kaydı tanımlar, foreign key kayıtlar arası bağı korur. JOIN tabloları ilişkilendirir. İndeks aramayı hızlandırabilir ama yazma ve depolama maliyeti vardır.',Orta:'N+1: önce hesapları, sonra her hesap için ayrı müşteri sorgusu çalıştırmak. Projection, fetch join veya entity graph ile erişim desenine uygun çözüm seç. İşlem listesini sayfala; parametreli sorgu kullan.',İleri:'account_id, created_at, id üzerinde erişim desenine uygun birleşik indeks ve keyset pagination düşün. EXPLAIN ANALYZE gerçek sorguyu çalıştırır; güvenli test verisinde planı incele. DB connection pool doluluğu ile yavaş sorguyu birbirinden ayır.'},
    question:'Müşterinin son 20 hesap hareketi yavaş geliyor. Nereden başlarsın?',
    answer:'Önce ölçerim: sorgu süresi, satır sayısı, execution plan, N+1 ve pool beklemesi. Hesap filtresi ve sıralamaya uygun indeks düşünürüm; aynı zaman damgasındaki kayıtlar için id ile kararlı sıra sağlarım. Entity yerine gereken alanları döndürür; indeksi ekledikten sonra tekrar ölçerim.',
    checklist:['Sorgu ve indeks ilişkisini anlattım','N+1 ve pagination riskini belirttim','Ölçüm öncesi/sonrası planını kurdum'],
    exercise:'account_id ile filtrelenen hareketleri created_at DESC, id DESC sırasıyla ilk 20 kayıt döndüren parametreli SQL yaz. İkinci sayfanın sınırını açıkla.',followUp:'Bir collection fetch join ile pagination birlikte kullanılırsa hangi sorunlar olabilir?',
    quiz:{question:'Her sütuna indeks eklemek neden iyi bir varsayılan değildir?',options:['Yazma ve depolama maliyetini artırır','İndeksler yalnız String alanlarda çalışır','İndeksler JOIN ile kullanılamaz'],correct:0,explanation:'İndeks seçimi sorgu desenine ve ölçümlere dayanmalıdır.'}
  },
  {
    id:'redis',title:'Redis: nerede, neden, hangi sınırla?',category:'Redis & Kafka',minutes:30,
    notes:{Başlangıç:'Redis, anahtarlarla erişilen hızlı bir veri deposudur. Bu banka örneğinde şube çalışma saatleri gibi tekrar okunan bilgilerin önbelleği olabilir. GET okur, SET yazar, TTL ömrü sınırlar.',Orta:'Cache-aside: cache’te varsa dön, yoksa DB’den oku ve TTL ile yaz. Güncelleme sonrası invalidation planla. Redis kesilince kısa timeout ve DB fallback uygula; hata metriği tut.',İleri:'TTL bayat veri olasılığını sıfırlamaz. Aynı anda çok sayıda miss oluşması DB’yi zorlar; jitter ve istek birleştirme düşün. Redis rate limit için de kullanılabilir; atomik sayaç + süreyi Lua ile birlikte uygulamak gerekir. Transfer bakiyesi ve tekillik kararında DB asıl kaynaktır.'},
    question:'Banka uygulamasında Redis’i nereye koyarsın? Nerede kullanmazsın?',
    answer:'Şube bilgisi okuma yolunda DB önüne koyarım. Transferin harcama kararı için cache bakiyesini kullanmam. Cache’in tutarlılık sınırını ve kesinti fallback’ini tanımlarım. Rate limiting gibi ayrı kullanımda fail-open/fail-closed kararı risk ihtiyacına göre değişir; cache fallback politikasını körlemesine uygulamam.',
    checklist:['Cache hit/miss/TTL akışını anlattım','Bayat bakiye riskini açıkladım','Kesinti ve yük artışı planını anlattım'],
    exercise:'Banka projesindeki BranchCache görevini çöz. Cache hit, miss, Redis okuma/yazma hatası, DB hatası ve null DB sonucu için beklenen davranışı yaz.',followUp:'DB güncellendiği sırada eski bir okuyucu cache’i yeniden doldurursa ne olur?',
    quiz:{question:'TTL tek başına ne sağlar?',options:['Sıfır bayat veri','Kayıt için sınırlı yaşam süresi','Finansal işlemde exactly-once'],correct:1,explanation:'TTL ömrü sınırlar; cache ile DB arasında güçlü tutarlılık garantisi vermez.'},
    source:{label:'Redis cache-aside',url:'https://redis.io/docs/latest/develop/use-cases/cache-aside/'}
  },
  {
    id:'kafka',title:'Kafka: transfer sonrası olay akışı',category:'Redis & Kafka',minutes:35,
    notes:{Başlangıç:'Producer olayı topic’e yazar; consumer okur. Partition topic’in bir parçası, offset o parçada konumdur. TransferCompleted olayını bildirim ve raporlama servisleri ayrı ayrı okuyabilir.',Orta:'Bir consumer group partition’ları paylaşır; ayrı gruplar aynı akışı bağımsız okur. Sıra yalnız partition içinde vardır. Başarılı işleme sonrası offset ilerletmek tekrar teslim ihtimalini ortadan kaldırmaz; consumer idempotent olmalıdır.',İleri:'Anahtar seçimi sıralama ve yük dağılımını etkiler. Kafka transaction’larının kapsamını dış DB/HTTP yan etkilerinden ayır. Retention, schema evolution, rebalance, retry backoff, dead-letter topic ve consumer lag için işletim planı kur.'},
    question:'Aynı TransferCompleted olayı iki kez geldi. Kullanıcıya iki bildirim gitmesini nasıl önlersin?',
    answer:'eventId ile kalıcı tekilleştirme yaparım. Inbox kaydı ile yerel DB etkisini aynı transaction’da yazar, offset’i sonrasında ilerletirim. Dış bildirim API’sinde aynı idempotency key kullanırım veya ayrı outbox kurarım. Bellekteki Set restart ve farklı instance’lar karşısında yeterli değildir.',
    checklist:['Topic/partition/group/offset kavramlarını anlattım','Tekrar teslim ve kalıcı tekilleştirmeyi anlattım','Kafka ile dış sistem garantilerinin sınırını belirttim'],
    exercise:'Banka projesindeki NotificationProjector görevini çöz. Bu bir Java davranış modeli; gerçek Kafka bağlantısı değildir. Sonra aynı davranışı kalıcı inbox ve @KafkaListener sınırına nasıl taşıyacağını anlat.',followUp:'6 partition ve aynı grupta 8 consumer varsa hepsi paralel veri alabilir mi?',
    quiz:{question:'Kafka sıralama garantisinin temel kapsamı nedir?',options:['Bütün topic’ler','Bütün consumer group’lar','Bir partition'],correct:2,explanation:'Farklı partition’lar arasında tek bir toplam sıra garantisi yoktur.'},
    source:{label:'Apache Kafka teslim semantiği',url:'https://kafka.apache.org/40/design/design/'}
  },
  {
    id:'outbox',title:'Outbox, saga ve hata senaryoları',category:'Sistem tasarımı',minutes:35,
    notes:{Başlangıç:'DB’ye transfer yazıldıktan sonra olay gönderimi başarısız olabilir. Outbox, gönderilecek olayı iş verisiyle aynı DB transaction’ına kaydeder. Ayrı bir relay kayıtları Kafka’ya taşır.',Orta:'Relay gönderdi ama gönderildi işaretini koyamadan çöktü: tekrar yayın mümkündür. Consumer tekilleştirir. Saga, ayrı servislerdeki yerel transaction’ları ve telafi adımlarını koordine eder.',İleri:'Saga global ACID rollback değildir; telafi de başarısız olabilir. Para hareketinde ters kayıt, denetim izi ve manuel uzlaşma gerekebilir. Outbox backlog, relay gecikmesi ve dead-letter yeniden oynatma için alarm/sorumluluk belirle.'},
    question:'DB commit oldu, Kafka kapalı. Transferi kaybetmeden ne yaparsın?',
    answer:'Transfer ve outbox kaydı birlikte commit olur. Relay broker geri geldiğinde yeniden dener; olay kimliği değişmez. Consumer tekrarları karşılar. Biriken outbox yaşı ve miktarını izlerim. İstemciye transferin kalıcı sonucu ile bildirim durumunu ayrı anlatırım.',
    checklist:['DB+olay atomikliğini açıkladım','Relay çökmesi ve duplicate senaryosunu anlattım','Telafi ve rollback farkını belirttim'],exercise:'DB commit öncesi, commit sonrası, broker ack sonrası üç çökme noktası çiz. Her biri için tekrar denemede hangi kayıtların bulunduğunu söyle.',followUp:'Outbox tablosu sürekli büyüyorsa olayı kaybetmeden nasıl temizlersin?',
    quiz:{question:'Outbox hangi sorunu hedefler?',options:['DB yazması ile olay yayınlama arasındaki kayıp penceresini','Bütün servisleri tek DB kullanmaya zorlamayı','Tüm tekrar teslimleri otomatik engellemeyi'],correct:0,explanation:'İş kaydı ve olay niyetini atomik saklar; tüketicide duplicate kontrolü yine gerekir.'},
    source:{label:'Debezium outbox event router',url:'https://debezium.io/documentation/reference/transformations/outbox-event-router.html'}
  },
  {
    id:'ddd',title:'DDD ve bağımsız servis sınırları',category:'İlandaki beklentiler',minutes:30,
    notes:{Başlangıç:'Domain iş alanıdır. Ubiquitous language, geliştirici ve iş analistinin aynı anlamla kullandığı dildir. Bounded context bir modelin geçerli olduğu sınırdır; müşteri, hesap, ödeme ve bildirim aynı şey değildir.',Orta:'Entity kimlikle, value object değerle tanımlanır. Aggregate kendi tutarlılık kurallarını korur. Her tablo ayrı mikroservis değildir. Sınırları iş yeteneklerine, transaction ihtiyacına ve takım sahipliğine göre belirle.',İleri:'Bağımsız deploy için API/event sözleşmeleri ve veri sahipliği gerekir. Aynı DB tablolarına farklı servislerin doğrudan yazması bağlılığı artırır. Dağıtık transaction ihtiyacını azaltacak sınırları tartış; gerekirse modüler monolitle başla.'},
    question:'Banka sistemini hangi servis sınırlarına ayırırsın ve neden?',
    answer:'Önce iş akışlarını ve tutarlılık gereksinimlerini netleştiririm. Hesap/ledger, ödeme akışı ve bildirim farklı sorumluluk adaylarıdır. Para hareketinin atomik sınırını korurum. Her sınırın sahibi, verisi ve sözleşmesi olur; yalnız “mikroservis olsun” diye her sınıfı ayırmam.',
    checklist:['Bounded context ve aggregate’i ayırdım','Veri sahipliğini belirttim','Bağımsız deploy ile sözleşme ilişkisini açıkladım'],exercise:'Transfer talebinden bildirime kadar üç sorumluluk çiz. Hangi veri kime ait, hangi çağrı senkron, hangi olay asenkron?',followUp:'İki servis her sürümde birlikte deploy ediliyorsa hangi bağlılıkları araştırırsın?',
    quiz:{question:'Servis sınırını belirlemek için en iyi başlangıç hangisi?',options:['Her tabloya bir servis','İş yetenekleri ve tutarlılık gereksinimleri','Her Java package’ına bir servis'],correct:1,explanation:'İş sınırları, operasyonel maliyet ve takım sahipliği birlikte değerlendirilir.'},
    source:{label:'DDD ile mikroservis modelleme',url:'https://learn.microsoft.com/en-us/azure/architecture/microservices/model/tactical-ddd'}
  },
  {
    id:'patterns',title:'Aggregator, Proxy, Pipeline ve Shared Resources',category:'İlandaki beklentiler',minutes:35,
    notes:{Başlangıç:'Aggregator birkaç servisten gelen sonuçları birleştirir. Proxy isteği başka servise iletir. Pipeline işlemi aşamalara böler. Asynchronous messaging gönderici ile alıcının aynı anda hazır olma ihtiyacını azaltır.',Orta:'Müşteri paneli hesap + kart özetini aggregator ile toplayabilir. Gateway proxy routing ve ortak kontroller yapabilir. İşlem doğrulama → risk kontrolü → yürütme bir pipeline olarak modellenebilir; aşamaların hata ve tekrar politikası gerekir.',İleri:'Aggregator gecikmesi bağımlılıkların timeout ve kısmi yanıt kararlarına bağlıdır. Shared Resources terimi görüşmede netleştirilmeli: ortak altyapı ile ortak veri sahipliği farklıdır. Paylaşılan DB/table bağımsız deploy’u zorlaştırabilir. Circuit breaker, retry ve bulkhead birbirinin yerine geçmez.'},
    question:'Müşteri panelindeki kart servisi cevap vermiyor. Bütün ekranı başarısız mı yaparsın?',
    answer:'İş ihtiyacına göre timeout sonrası açık bir “kart bilgisi alınamadı” kısmıyla diğer verileri gösterebilirim; kritik ödeme onayında eksik veriyle başarı vermem. Aggregator’a deadline, bağımlılık bazlı timeout ve circuit breaker ekler, retry fırtınasını önlerim.',
    checklist:['Dört deseni banka örneğiyle anlattım','Kısmi yanıt ve kritik akış farkını açıkladım','Ortak kaynakların bağlılık maliyetini söyledim'],exercise:'Panel okuması ve para transferi için ayrı hata politikaları yaz. “Shared Resources ile tam olarak neyi kastediyorsunuz?” sorusunu nasıl soracağını prova et.',followUp:'Üç katmanda üçer retry varsa tek kullanıcı isteği kaç aşağı akış denemesi doğurabilir?',
    quiz:{question:'Aggregator’ın temel amacı nedir?',options:['Her isteği Kafka’ya çevirmek','Servis DB’lerini birleştirmek','Birden fazla servisin sonuçlarını bir yanıtta toplamak'],correct:2,explanation:'Birleştirme yapar; veri sahipliğini veya transaction sınırlarını kendiliğinden değiştirmez.'},
    source:{label:'Mikroservis tasarım desenleri',url:'https://learn.microsoft.com/en-us/azure/architecture/microservices/design/patterns'}
  },
  {
    id:'security',title:'API güvenliği ve denetim izi',category:'Banka',minutes:25,
    notes:{Başlangıç:'Authentication kim olduğunu, authorization neye erişebileceğini belirler. Kullanıcının giriş yapmış olması başka müşterinin hesabını kullanabileceği anlamına gelmez.',Orta:'Kaynak hesap sahipliğini sunucuda doğrula. Girdi validasyonu yetkilendirme değildir. DTO ile gereksiz alanları gizle; parametrik sorgu kullan; token, şifre ve tam hesap bilgilerini loglama.',İleri:'Audit kaydı “kim, ne zaman, hangi işlem, hangi sonuç” sorularını yanıtlar. Retention ve erişim kuralları kurum gereksinimine göre belirlenir. JWT imza, süre ve beklenen issuer/audience kontrolü gerekir. CSRF kararı credential taşıma biçimine bağlıdır; CORS yetkilendirme değildir.'},
    question:'İstemci fromAccountId değerini başka müşterinin hesabıyla değiştirdi. Nasıl engellersin?',
    answer:'Kimliği doğrulanmış kullanıcının hesabın sahibi/işlem yetkilisi olduğunu service/repository sınırında kontrol ederim. İstemcinin userId iddiasına güvenmem. Yetkisiz istekte hiçbir hareket oluşmadığını test eder, denetim olayına hassas veri sızdırmam.',
    checklist:['Authentication/authorization farkını açıkladım','Nesne düzeyinde yetki testi tasarladım','Log ve audit ayrımını anlattım'],exercise:'Kendi hesabı, başkasının hesabı, süresi bitmiş token ve eksik token için dört API test senaryosu yaz.',followUp:'Frontend’de transfer butonunu gizlemek neden yeterli değil?',
    quiz:{question:'@Valid tek başına ne yapmaz?',options:['Hesap sahipliğini ve erişim yetkisini doğrulamak','DTO constraint’lerini çalıştırmak','Geçersiz DTO için hata üretmek'],correct:0,explanation:'İş yetkisi uygulamanın güvenlik ve domain kontrollerinde uygulanmalıdır.'}
  },
  {
    id:'testing',title:'Test stratejisi ve CI/CD',category:'İlandaki beklentiler',minutes:35,
    notes:{Başlangıç:'Unit test küçük iş kuralını, integration test parçaların birlikte çalışmasını, E2E kullanıcı akışını doğrular. CI her değişiklikte kontrolleri çalıştırır; CD güvenilir dağıtım akışı kurar.',Orta:'Mockito repository davranışını taklit eder, DB rollback ve kilit semantiğini kanıtlamaz. PostgreSQL/Redis/Kafka integration testleri için izole test ortamı kur. Contract testleri bağımsız servislerin sözleşmesini korur.',İleri:'Pipeline: derle → unit/integration/contract → güvenlik kontrolleri → değişmez imaj → staging smoke → kontrollü dağıtım. Geriye uyumlu expand/contract migration planla; eski sürümün yeni şemayla çalışmasını düşün. Rollback veri değişikliklerini her zaman geri alamaz.'},
    question:'Yeni transfer endpoint’ini canlıya almadan hangi testleri ve kontrolleri koşarsın?',
    answer:'Para kuralları için unit; rollback/unique/locking için gerçek PostgreSQL; broker ve cache kesintileri için integration; servis kontratları; kullanıcı transfer akışı için E2E; eşzamanlı istekler ve yük testi. Pipeline kanıtlarını ve rollout/rollback planını paylaşırım. Başarı ölçütü yalnız test sayısı değildir.',
    checklist:['Risklere uygun test katmanını seçtim','Bağımsız deploy ve contract testi ilişkilendirdim','Migration ve rollback planını anlattım'],exercise:'Bir test planı yaz: senaryo, hazırlık, eylem, beklenen DB etkisi ve HTTP sonucu. En az bir kesinti ve bir eşzamanlılık durumu ekle.',followUp:'Testler yeşil ama canlıda p95 gecikme arttı. Deploy’u sürdürür müsün?',
    quiz:{question:'Mockito ile başarılı save doğrulaması neyi kanıtlamaz?',options:['Metodun mock ile etkileşimini','Gerçek PostgreSQL rollback ve lock davranışını','Beklenen çağrı argümanlarını'],correct:1,explanation:'Mock gerçek DB motoru değildir; transaction ve kilitler integration testi ister.'}
  },
  {
    id:'incident',title:'Sorun giderme ve ekip iletişimi',category:'İlandaki beklentiler',minutes:25,
    notes:{Başlangıç:'Önce etkiyi belirle: kim etkileniyor, ne zamandır, hangi işlem? Kanıt toplamadan rastgele yeniden başlatma. Log, metrik ve trace farklı gözlemler sağlar.',Orta:'Hata oranı, p95 gecikme, DB pool, Redis hata oranı ve Kafka consumer lag’i birlikte incele. Correlation/trace ID ile isteği takip et. Son deploy/config değişikliklerini karşılaştır.',İleri:'Önce zararı sınırla, sonra kök nedeni doğrula. Hipotez → kanıt → küçük değişiklik → ölçüm döngüsü kur. İş analisti ve product owner ile öncelik, acceptance criteria ve riskleri netleştir; olay sonrası suçlayıcı olmayan aksiyonlar ve runbook yaz.'},
    question:'Kullanıcı “para gitti ama işlem başarısız görünüyor” dedi. İlk beş adımın ne?',
    answer:'İşlem kimliğini ve etkiyi toplarım; hassas veri istemem. Kalıcı transfer/ledger durumunu kontrol ederim. Trace ve idempotency kayıtlarıyla yanıt kaybı mı gerçek hata mı ayırırım. Tekrar borçlandırmayı önler, müşteriye doğrulanmış durumu destek kanalıyla iletirim. Sonra test ve izleme açığını kapatırım.',
    checklist:['Etki ve verinin gerçek durumunu önceledim','Kanıta dayalı hipotez kurdum','Teknik ve iş paydaşlarına açık iletişim planladım'],exercise:'Kendi deneyiminden bir problemi Durum–Görev–Eylem–Sonuç sırasıyla 2 dakikada anlat. Ölçmediğin kazanım için sayı uydurma.',followUp:'İki acil iş aynı anda geldiğinde product owner ile önceliği nasıl netleştirirsin?',
    quiz:{question:'Consumer lag artarken ilk yaklaşımın ne olmalı?',options:['Offset’i atlayıp gecikmeyi sıfırlamak','Partition sayısını düşünmeden artırmak','Üretim/tüketim hızını, hataları ve bağımlılık gecikmesini incelemek'],correct:2,explanation:'Lag belirti olabilir. Offset atlamak veri kaybına, plansız ölçekleme yeni sorunlara yol açabilir.'}
  },
  {
    id:'fullstack',title:'React, Python ve tasarımını anlatma',category:'İlandaki beklentiler',minutes:25,
    notes:{Başlangıç:'İlan Java, React, Python veya alan dilini sayıyor; hepsinde aynı derinlik varsayma. Ana güçlü alanını söyle. React’te props/state, kontrollü form, loading/error durumları; Python’da koleksiyonlar, exception ve Decimal temelini tekrar et.',Orta:'React useEffect temizliği ve eski isteğin yeni sonucu ezmesi gibi yarışları düşün. Transfer butonunu beklerken kapat ama sunucuda idempotency uygula. Python ile mutabakat örneğinde float yerine Decimal, dosya/DB sınırları ve büyük veride generator kullanımı konuşulabilir.',İleri:'Sistem tasarımını 5 dakikada gereksinim → varsayım → veri modeli → API → hata akışı → test/ölçüm şeklinde anlat. Bağımsız deploy, dokümantasyon ve bilgi aktarımını tasarımın parçası yap. Bilmediğin teknolojide tahminini gerçek bilgiden ayır.'},
    question:'Banka transfer akışını teknik olmayan bir product owner’a nasıl açıklarsın?',
    answer:'“Kullanıcının yetkisini ve paranın yeterli olduğunu kontrol ediyoruz. İşlemin iki hesabı birlikte güncelleniyor. Bağlantı koparsa aynı işlem yeniden para düşürmüyor. Bildirim gecikse de işlemin gerçek durumu kayıtlarımızda takip edilebiliyor.” Sonra kabul kriterlerini örneklerle doğrularım.',
    checklist:['Ana teknoloji güçlü yanımı somut örnekledim','React’te yükleme/hata/tekrar davranışını açıkladım','Teknik kararı iş sonucuna bağladım'],exercise:'Bir transfer formunun başarılı, yükleniyor, timeout ve tekrar deneme ekranlarını çiz. Ardından çözümünü 90 saniye Türkçe, 90 saniye İngilizce anlat.',followUp:'Yeni bir teknolojiye ihtiyaç olduğunu hangi ölçümler veya iş gereksinimleriyle savunursun?',
    quiz:{question:'Frontend’de çift tıklamayı engellemek ne sağlar?',options:['Kullanıcı deneyimini iyileştirir; sunucu idempotency’sinin yerini almaz','Dağıtık exactly-once sağlar','Yetkilendirme ihtiyacını kaldırır'],correct:0,explanation:'Ağ retry’ları, başka istemciler ve eşzamanlı istekler backend’de korunmalıdır.'}
  }
]
