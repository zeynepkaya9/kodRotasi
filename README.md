# KodRotası — Java & Spring Öğrenme Yolu

Kullanicinin gercek bir proje gelistirirken Java ve Spring Boot'u kalici sekilde ogrendigi interaktif egitim platformu.

## Hizli Baslangic

### Gereksinimler
- Java 17+
- Node.js 18+
- Docker & Docker Compose (PostgreSQL ve Redis icin)

### 1. Veritabani ve Redis baslatma
```bash
docker-compose up -d
```

### 2. Backend baslat (dev profili - H2 dosya veritabani)
```bash
cd backend
./mvnw spring-boot:run
```
Backend `http://localhost:8080` adresinde calisir. Dev profilinde `backend/data/jsy_platform` konumunda kalici H2 dosya veritabani kullanilir, Docker gerekmez.

### 3. Frontend baslat
```bash
cd frontend
npm install
npm run dev
```
Frontend `http://localhost:5173` adresinde calisir.

### 4. Kullanim
1. `http://localhost:5173` adresine git
2. Kayit ol
3. Seviye tespiti quizini tamamla
4. Proje sec (Banka Uygulamasi)
5. Workspace'te kod yaz ve ogrenmeye basla!

## Mimari

- **Backend:** Spring Boot 3 (Modular Monolith) - Java 17
- **Frontend:** React 18 + TypeScript + Vite + Monaco Editor + TailwindCSS
- **Veritabani:** PostgreSQL 16 (prod), H2 (dev)
- **Cache:** Redis 7

## Moduller

| Modul | Aciklama |
|-------|----------|
| auth | Kayit, giris, JWT |
| learningpath | Seviye tespiti, mimari onerisi |
| taskengine | Gorev/adim yonetimi, ilerleme |
| hintengine | 4 kademeli ipucu sistemi |
| evaluation | JDK AST ile Java sozdizimi ve yapisal kod degerlendirme, "neden" motoru |
| concept | Kavram kartlari (benzetme, dogru/yanlis ornek) |
| quiz | Mini quiz sistemi |
| progress | XP, streak, rozet |

## Kaldigin yerden devam etme

Gercek API modu (`frontend/.env`: `VITE_USE_MOCK=false`) kullanilmalidir. Giriste seviye, aktif proje, adimlar ve XP sunucudan yuklenir. Kod 600 ms duraklamadan sonra otomatik kaydedilir; cikis ve sayfa gecisinde bekleyen kayit gonderilir. Baglanti kesilirse taslak ayni tarayicida kullaniciya ozel saklanir. Baska cihazdan devam etmek icin “Taslak kaydedildi” durumunu bekleyin. Sekme zorla kapatilirsa son yerel taslak ayni tarayicida korunur; son degisiklik sunucuya ulasmamis olabilir.

Backend yeniden basladiginda dev ders icerigi mevcut adim kimlikleri korunarak guncellenir. Eski kullanici ilerlemesi silinmez. Yeni surumu gormek icin backend'i yeniden baslatin; mevcut veritabani dosyasini silmeyin.

## Projeler

- Banka: 8 adim; BigDecimal, entity, repository, service, REST, hata yonetimi, DTO, Mockito/JUnit.
- E-ticaret: 6 adim; fiyat/stok kurallari, kategori, sorgular, service, REST ve validation.
- Kutuphane: 5 adim; kitap, uye, repository, odunc alma/iade ve REST.
- Gorev takibi: 4 adim; saf Java kapsulleme ve composition'dan Spring bean ve constructor injection'a gecis.

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

Backend testleri ayri, bellek ici H2 kullanir; kullanici veritabanini degistirmez. 23 referans cozumun kurallardan gecmesi, baslangic kodlarinin dogrudan gecmemesi ve dort projenin Java 17 ile derlenmesi kontrol edilir. Secili domain davranislari ve cikis/giris sonrasi taslak, XP ve adim devam akisi da test edilir.

Ogrenci kodu calistirilmaz. JDK parser sozdizimi, annotation hedefleri ve metot bildirimlerini kontrol eder. Tip cozumu, butun is kurallari ve gercek veritabanina karsi uygulama davranisi bu kontrolun kapsami disindadir. Ornekler egitim amaclidir; tam bir production sistemi degildir.
