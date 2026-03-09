package com.example.kriptotakip.ui.profile

// Gerekli arayüz ve sistem kütüphanelerini içeri aktarıyoruz
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kriptotakip.data.StorageManager
import com.example.kriptotakip.presentation.CoinViewModel
import com.google.firebase.auth.FirebaseAuth

// Material 3'ün bazı özellikleri hala geliştirme aşamasında olduğu için bu onayı veriyoruz
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,    // Geri tuşuna basıldığında ne yapılacağını belirleyen fonksiyon
    onLogout: () -> Unit   // Çıkış yapıldığında ne yapılacağını belirleyen fonksiyon
) {
    // Firebase ve uygulama mantığını yöneten araçlarımızı hazırlıyoruz
    val auth = FirebaseAuth.getInstance() // Kullanıcı giriş/çıkış işlemleri için
    val viewModel: CoinViewModel = viewModel() // Verilerin (renk, isim vb.) bulutla senkronu için
    val uiState = viewModel.uiState // Ekrandaki güncel durumları takip eden "çekmece"
    
    // Telefonun hafızasına erişmek için gerekli araç
    val context = androidx.compose.ui.platform.LocalContext.current
    val storageManager = remember { StorageManager(context) }

    // --- Ekranın Kendi Hafızası (State) ---
    // user: O an giriş yapmış olan kullanıcının bilgilerini tutar
    var user by remember { mutableStateOf(auth.currentUser) }
    // isEditMode: Kullanıcı şu an ismini değiştiriyor mu? (Düzenleme modu açık/kapalı)
    var isEditMode by remember { mutableStateOf(false) }
    // editedName: Yazı kutusuna (TextField) yazılan yeni ismi geçici olarak tutar
    var editedName by remember { mutableStateOf(user?.displayName ?: "") }

    // Kullanıcının isminin baş harfini alıyoruz (Avatarın ortasında büyükçe göstermek için)
    val userInitial = user?.displayName?.firstOrNull()?.toString()?.uppercase() ?: "?"

    // Kullanıcının seçebileceği şık renklerin listesi
    val colorOptions = listOf(
        0xFF1ABC9C, 0xFF2ECC71, 0xFF3498DB, 0xFF9B59B6, 
        0xFFE67E22, 0xFFE74C3C, 0xFF34495E, 0xFFFF0000, 0xFF000075
    )

    // Sayfa Düzeni (Üst bar ve içerik)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }, // Başlık
                navigationIcon = {
                    // Geri butonu
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Ekranın orta kısmındaki dikey dizilim
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Her şeyi yatayda ortala
        ) {
            // --- PROFIL AVATARI ---
            Box(
                modifier = Modifier
                    .size(120.dp) // Boyut
                    .clip(CircleShape) // Yuvarlak yap
                    .background(Color(uiState.profileColor)), // Arka planı seçili renge boya
                contentAlignment = Alignment.Center
            ) {
                // İsmin baş harfini büyükçe yazdırıyoruz
                Text(
                    text = userInitial,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Boşluk bırak

            // --- İSİM DÜZENLEME ALANI ---
            if (isEditMode) {
                // Eğer kalem butonuna basıldıysa (Düzenleme modu aktifse):
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Yeni ismi yazmak için giriş kutusu
                    TextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Enter Name") },
                        singleLine = true
                    )
                    // Kaydetme (Tik) Butonu
                    IconButton(onClick = {
                        viewModel.updateName(editedName) // İsmi Firebase'e gönder
                        isEditMode = false // Düzenleme modundan çık
                        user = auth.currentUser // UI'daki kullanıcı bilgisini tazele
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Green)
                    }
                }
            } else {
                // Normal modda: Sadece isim ve yanındaki kalem butonu görünür
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user?.displayName ?: "User Name",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    // Kalem butonu: Basınca düzenleme modunu açar
                    IconButton(onClick = { isEditMode = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Name", modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Kullanıcının e-posta adresi (Değiştirilemez, gri renkli)
            Text(
                text = user?.email ?: "Email not found",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- ÇIKIŞ YAP BUTONU ---
            Button(
                onClick = {
                    storageManager.clearFavorites() // Telefon hafızasını temizle (Veri karışıklığını önlemek için)
                    onLogout() // Çıkış işlemini başlat ve giriş ekranına dön
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Kırmızı uyarı rengi
            ) {
                Text("Logout", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- RENK SEÇİCİ ALANI ---
            Text(text = "Choose Profile Color", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Renk paletini 2 satırda gösteriyoruz
            Column {
                // İlk 5 renk
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.take(5).forEach { colorHex ->
                        ColorCircle(colorHex, uiState.profileColor) {
                            viewModel.updateProfileColor(colorHex) // Tıklanan rengi buluta kaydet
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Geri kalan 4 renk
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.drop(5).forEach { colorHex ->
                        ColorCircle(colorHex, uiState.profileColor) {
                            viewModel.updateProfileColor(colorHex) // Tıklanan rengi buluta kaydet
                        }
                    }
                }
            }
        }
    }
}

// Her bir küçük renk dairesini çizen yardımcı "bileşen" (Composable)
@Composable
fun ColorCircle(colorHex: Long, selectedColor: Long, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(45.dp) // Daire boyutu
            .clip(CircleShape) // Yuvarlak form
            .background(Color(colorHex)) // Kendi rengi
            .clickable { onClick() } // Tıklanabilir yap
            .then(
                // Eğer bu renk seçili olan renk ise, hafif bir beyazlık/vurgu ekle
                if (selectedColor == colorHex) {
                    Modifier.background(Color.White.copy(alpha = 0.4f))
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Seçili olan rengin tam ortasına küçük bir beyaz nokta koyuyoruz
        if (selectedColor == colorHex) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
        }
    }
}
