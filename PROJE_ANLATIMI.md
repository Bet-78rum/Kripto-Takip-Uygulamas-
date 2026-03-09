# KRİPTO TAKİP PROJESİ TEKNİK ANLATIMI

Bu belge, Kripto Takip uygulamasının teknik yapısını ve kod bileşenlerini açıklamak için hazırlanmıştır.

## 1. Mimari Yapı (MVVM)
Uygulama, verilerin UI'dan bağımsız yönetildiği profesyonel MVVM (Model-View-ViewModel) mimarisini kullanır.

## 2. Katmanlar ve Dosyalar

### A. Veri ve Sunucu Bağlantısı (Data Layer)
- **BinanceApi.kt:** Retrofit kullanarak Binance'in REST API'sine bağlanır. Uygulama açılışında tüm market verilerini (24h ticker) tek seferde çekmemizi sağlar.
- **BinanceSocketManager.kt:** Uygulamanın "canlı" kalmasını sağlayan motor burasıdır. WebSocket kullanarak Binance ile sürekli bir bağlantı kurar ve değişen fiyatları milisaniyeler içinde uygulamaya ulaştırır.
- **StorageManager.kt:** SharedPreferences kullanarak verileri telefonun kalıcı hafızasına yazar. Bu sayede uygulama kapansa da favorileriniz ve gece modu ayarınız silinmez.

### B. Veri Modelleri (Domain Layer)
- **CoinResponse.kt:** Sunucudan gelen karmaşık JSON verisini anlamlı Kotlin nesnelerine dönüştürür. `symbol`, `price` ve `percent` gibi temel alanları içerir.

### C. Mantıksal İşlemler (Presentation Layer)
- **CoinViewModel.kt:** Uygulamanın beynidir.
    - REST API ve WebSocket'ten gelen verileri birleştirir.
    - Arama filtreleme ve favori listesi yönetimi gibi UI mantığını yürütür.
    - `@Published` benzeri bir mantıkla (mutableStateOf) UI'ın veri değiştikçe otomatik yenilenmesini sağlar.

### D. Kullanıcı Arayüzü (UI Layer - Jetpack Compose)
- **MainActivity.kt:** Ana konteynırdır.
    - **TopBar:** Gece modu anahtarı, başlık ve dinamik arama çubuğunu içerir.
    - **TabRow:** Favoriler ve Popüler coinler arasında geçiş yapmayı sağlayan sekmeli yapıdır.
    - **Filtering:** Seçili sekme ve arama metnine göre listeyi anlık olarak süzgeçten geçirir.
- **CoinDetailCard.kt:** Listedeki her bir satırı temsil eder.
    - Dinamik Renklendirme: Fiyat yükseliyorsa yeşil, düşüyorsa kırmızı yanar.
    - Favori Butonu: Yıldıza tıklandığında ViewModel üzerinden hafızaya kayıt atar.

## 3. Kullanılan Teknolojiler
- **Jetpack Compose:** Modern, bildirimsel UI kütüphanesi.
- **Retrofit & Gson:** HTTP istekleri ve JSON işleme.
- **OkHttp WebSocket:** Canlı veri akışı.
- **Material Design 3:** Modern Google tasarım standartları.
- **Coroutines:** Arka plan işlemleri ve asenkron veri çekme.

---
*Hazırlayan: AI Asistanı*
