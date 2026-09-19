# İlerleme ve ders kalitesi güncellemesi

## Tamamlananlar

- Çıkış sonrasında kullanıcıya ait yerel taslak ve adım seçimi korunur; farklı hesabın çalışma alanına yüklenmez.
- Taslaklar ayrıca sunucuda tutulur. Daha önceki son gönderimler de editöre geri döner. Boş taslak, kaydedilmemiş taslaktan ayrılır.
- Giriş ve doğrudan çalışma alanına erişimde sunucudan seviye, görev, XP ve seri yüklenir. Bağlantı hatası seviye testine yönlendirmez; tekrar deneme gösterilir.
- Proje tekrar seçildiğinde erişilebilir adım seçilir. Bitmiş projelerde son tamamlanan adım açılır.
- Tamamlanmış adım tekrar XP kazandırmaz; kilitli adım gönderimi ve ipucu isteği engellenir.
- Gerçek Java 17 AST ayrıştırıcısı eklendi. Yorum/string içindeki annotation'lar, yanlış alana konmuş annotation'lar, metot çağrıları ve boş metot gövdeleri ilgili kuralları geçirmez.
- Kabul edilen sınıf ve metotlar yönergelerde kontrol listesi olarak gösterilir.
- Banka örnekleri BigDecimal kullanır; boş JUnit örneği yerine Mockito ile servis testleri vardır.
- E-ticarette eksik servis metotları ve sıfır/negatif stok azaltma hatası düzeltildi.
- Kütüphanede eksik MemberRepository ve importlar tamamlandı.
- Kod ipuçları artık doğrulanmış çözümden bir metot gösterir; placeholder çözüm metni kullanılmaz.
- Yeni Görev Takibi projesi: TodoItem → TaskBoard → TodoService → TodoController.
- Dev ders güncellemeleri aynı görev/adım kimliklerini korur; kullanıcı ilerlemesi silinmez.

## Doğrulama

- 8 backend testi: HTTP API üzerinden kayıt/giriş, taslak geri yükleme, boş taslak, tamamlanan adımın sonraki adımı açması, XP geri yükleme, ders güncellemesinde kimliklerin korunması, değerlendirme sınırları.
- Dört projedeki 23 referans çözüm yapısal kurallardan geçer. Hiçbir başlangıç kodu doğrudan başarı sayılmaz.
- Her projenin referans kaynakları ayrı .java dosyalarına ayrılarak Java 17 ile derlenir.
- Seçili davranışlar: banka servisinde para yatırma ve geçersiz işlem, e-ticarette stok miktarı sınırları, kitapta çift ödünç almanın reddi/iade, görevlerde boş başlık ve koleksiyonun korunması.
- 5 frontend testi: hesap ayrımı, çıkış/giriş, sunucu taslağı, boş taslak, eski sunucu verisinin yeni yazılanı ezmemesi, erişilebilir adım ve oturum sıfırlama.
- TypeScript ve Vite üretim derlemesi.
- Tarayıcı üzerinden uçtan uca görsel test yapılmadı.

## Önerdiğim sonraki dersler

1. **Rezervasyon uygulaması:** Aynı zaman aralığına iki rezervasyonun engellenmesi; zaman aralığı nesnesi, transaction ve eşzamanlılık. Başarı ölçütü yalnız CRUD değil, çakışmanın doğru reddedilmesi olmalı.
2. **Bildirim merkezi:** EmailSender/SmsSender gibi aynı interface'in farklı uygulamaları; polymorphism, constructor injection ve Strategy. Önce sahte göndericilerle test edilir, dış servis sonra eklenir.
3. **Hata avı görevleri:** Çalışan ama yanlış sonuç üreten küçük kod verilir. Öğrenci önce hatayı gösteren test, sonra düzeltmeyi yazar.
4. **Konu bazlı öğrenme haritası:** OOP, DI, JPA ve test yetkinlikleri ayrı tutulur. Zayıf konuda kısa tekrar görevi önerilir; genel seviye tek başına karar vermez.
5. **Kütüphane projesinin ikinci aşaması:** Loan entity ile kimin hangi kitabı aldığı, teslim tarihi ve geçmiş tutulur. Mevcut örnek müsaitlik ve üye varlığı kontrolü öğretir; tam bir ödünç kayıt sistemi değildir.

## Açık sınırlar

Öğrenci kodu yalnız ayrıştırılır; çalıştırılmaz ve bütün iş kuralları kanıtlanmaz. Sonraki aşama kaynakları sınırlandırılmış ayrı bir ortamda davranış testleridir. Referans derleme ve seçili davranış testleri gerçek DB entegrasyonu, HTTP hata sözleşmeleri veya çok kullanıcılı üretim güvenliğinin tamamını doğrulamaz.

Yerel çevrimdışı taslaklar tarayıcı depolaması temizlenirse kaybolur; cihazlar arası devam için sunucu kaydının bitmesi gerekir. Aynı hesabın birden çok sekmede eşzamanlı düzenlemesi için ayrıca sürüm/çatışma yönetimi gerekir.

Seri hâlâ gün yerine başarılı adım sayar. İpucu XP modeli hem anlık kesinti hem ödül indirimi içerir; pedagojik tasarımı ayrıca ele alınmalı. Mock API gerçek API'nin tüm davranışlarını taklit etmez; devam akışı gerçek API modunda doğrulanmıştır.
