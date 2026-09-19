# KodRotası canlıya alma planı

İlk sürüm tek bir Render web servisi içinde React arayüzünü ve Spring Boot API'yi sunar. Kalıcı veriler Neon PostgreSQL'de tutulur. Böylece iki ayrı uygulama ve CORS ayarı yönetmek gerekmez.

## Tahmini maliyet

- GitHub: ücretsiz.
- Neon PostgreSQL: ücretsiz planın sınırları içinde $0/ay.
- Render web service: ücretsiz planın sınırları içinde $0/ay; kullanılmadığında uyur ve ilk istek yavaş açılabilir.
- `kodrotasi.net`: domain firmasının yıllık ücreti.

## Yayın sırası

1. GitHub'da `kodrotasi` isimli private repository oluştur ve bu klasörü `main` dalına gönder.
2. Neon'da ücretsiz proje oluştur. Bağlantı bilgisinden host, veritabanı adı, kullanıcı ve parolayı al.
3. Render'da **New > Blueprint** seçip GitHub repository'sini bağla. `render.yaml` servisi otomatik tanımlar.
4. Render ortam değişkenlerini gir:
   - `JDBC_DATABASE_URL`: `jdbc:postgresql://HOST/DATABASE?sslmode=require`
   - `DATABASE_USERNAME`: Neon kullanıcı adı
   - `DATABASE_PASSWORD`: Neon parolası
   - `JWT_SECRET`: Render otomatik üretir; sonradan değiştirmek mevcut oturumları kapatır.
5. İlk yayında Render geçici adresinde kayıt, giriş, seviye testi, ipucu, taslak, çıkış/giriş ve ilerleme testlerini çalıştır.
6. Render servisinde **Settings > Custom Domains** bölümünden `kodrotasi.net` ve `www.kodrotasi.net` ekle.
7. Domain firmasındaki DNS kayıtlarını Render'ın gösterdiği değerlerle değiştir. HTTPS sertifikası otomatik oluşturulduktan sonra son canlı testi yap.

## Veri ve güvenlik

Yerel H2 veritabanı GitHub'a gönderilmez ve ilk yayına taşınmaz. Canlı sistem boş kullanıcı veritabanıyla başlar. Ders içerikleri uygulama ilk açıldığında oluşturulur.

Neon parolası ve JWT anahtarı yalnızca Render ortam değişkenlerinde tutulur. Bunları kaynak koda, GitHub dosyalarına veya ekran görüntülerine ekleme.

İlk sürümde Hibernate şemayı oluşturur ve günceller. Gerçek kullanıcı sayısı büyümeden önce şema değişiklikleri Flyway migration dosyalarına geçirilmelidir.
