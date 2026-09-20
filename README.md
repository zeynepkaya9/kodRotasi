# KodRotası — Java & Spring Öğrenme Yolu

Kullanıcının gerçek bir proje geliştirirken Java ve Spring Boot’u kalıcı şekilde öğrendiği interaktif eğitim platformu.

## Hızlı Başlangıç

### Gereksinimler

- Java 17+
- Node.js 18+
- Docker & Docker Compose (PostgreSQL ve Redis için)

### 1. Veritabanı ve Redis’i başlatma

```bash
docker-compose up -d
```

### 2. Backend’i başlatma (dev profili – H2 dosya veritabanı)

```bash
cd backend
./mvnw spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışır. Dev profilinde `backend/data/jsy_platform` konumunda kalıcı H2 dosya veritabanı kullanılır; Docker gerekmez.

### 3. Frontend’i başlatma

```bash
cd frontend
npm install
npm run dev
```

Frontend `http://localhost:5173` adresinde çalışır.

### 4. Kullanım

1. `http://localhost:5173` adresine git.
2. Kayıt ol.
3. Seviye tespiti quizini tamamla.
4. Bir proje seç (örneğin Banka Uygulaması).
5. Workspace’te kod yaz ve öğrenmeye başla.

## Mimari

- **Backend:** Spring Boot 3 (Modular Monolith) – Java 17
- **Frontend:** React 18 + TypeScript + Vite + Monaco Editor + Tailwind CSS
- **Veritabanı:** PostgreSQL 16 (prod), H2 (dev)
- **Cache:** Redis 7

## Modüller

| Modül | Açıklama |
|---|---|
| auth | Kayıt, giriş, JWT |
| learningpath | Seviye tespiti, mimari önerisi |
| taskengine | Görev/adım yönetimi, ilerleme |
| hintengine | 4 kademeli ipucu sistemi |
| evaluation | JDK AST ile Java sözdizimi ve yapısal kod değerlendirme, “neden” motoru |
| concept | Kavram kartları (benzetme, doğru/yanlış örnek) |
| quiz | Mini quiz sistemi |
| progress | XP, streak, rozet |

## Kaldığın Yerden Devam Etme

Gerçek API modu (`frontend/.env`: `VITE_USE_MOCK=false`) kullanılmalıdır. Girişte seviye, aktif proje, adımlar ve XP sunucudan yüklenir. Kod, 600 ms duraklamadan sonra otomatik kaydedilir; çıkışta ve sayfa geçişinde bekleyen kayıt gönderilir. Bağlantı kesilirse taslak aynı tarayıcıda kullanıcıya özel saklanır. Başka cihazdan devam etmek için “Taslak kaydedildi” durumunu bekleyin. Sekme zorla kapatılırsa son yerel taslak aynı tarayıcıda korunur; son değişiklik sunucuya ulaşmamış olabilir.

Backend yeniden başladığında dev ders içeriği, mevcut adım kimlikleri korunarak güncellenir. Eski kullanıcı ilerlemesi silinmez. Yeni sürümü görmek için backend’i yeniden başlatın; mevcut veritabanı dosyasını silmeyin.

## Projeler

- Banka: 8 adım; BigDecimal, entity, repository, service, REST, hata yönetimi, DTO, Mockito/JUnit.
- E-ticaret: 6 adım; fiyat/stok kuralları, kategori, sorgular, service, REST ve validation.
- Kütüphane: 5 adım; kitap, üye, repository, ödünç alma/iade ve REST.
- Görev takibi: 4 adım; saf Java kapsülleme ve composition’dan Spring bean ve constructor injection’a geçiş.

## Kontroller

```bash
cd backend
./mvnw test
```

```bash
cd frontend
npm test
npm run build
```

Backend testleri ayrı, bellek içi H2 kullanır; kullanıcı veritabanını değiştirmez. 23 referans çözümün kurallardan geçmesi, başlangıç kodlarının doğrudan geçmemesi ve dört projenin Java 17 ile derlenmesi kontrol edilir. Seçili domain davranışları ile çıkış/giriş sonrası taslak, XP ve adım devam akışı da test edilir.

Öğrenci kodu çalıştırılmaz. JDK parser sözdizimi, annotation hedefleri ve metot bildirimlerini kontrol eder. Tip çözümü, bütün iş kuralları ve gerçek veritabanına karşı uygulama davranışı bu kontrolün kapsamı dışındadır. Örnekler eğitim amaçlıdır; tam bir production sistemi değildir.
