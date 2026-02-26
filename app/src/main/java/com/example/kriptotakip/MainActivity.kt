package com.example.kriptotakip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kriptotakip.data.BinanceSocketYoneticisi
import com.example.kriptotakip.data.HafizaYoneticisi
import com.example.kriptotakip.presentation.CoinViewModel
import com.example.kriptotakip.ui.theme.KriptoTakipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KriptoUygulamaAnaEkran()
        }
    }
}

@Composable
fun KriptoUygulamaAnaEkran() {
    val context = LocalContext.current
    val hafiza = remember { HafizaYoneticisi(context) }
    val viewModel: CoinViewModel = viewModel()
    
    // Tema durumunu hafızadan okuyarak başlatıyoruz
    var isDarkMode by remember { mutableStateOf(hafiza.temaOku()) }
    var seciliSekme by remember { mutableStateOf(0) } // 0: Favoriler, 1: Popüler
    var aramaMetni by remember { mutableStateOf("") }
    
    val sekmeler = listOf("Favoriler ⭐", "Popüler")
    val popülerCoinler = listOf(
        "BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT",
        "ADAUSDT", "DOGEUSDT", "TRXUSDT", "DOTUSDT", "LTCUSDT",
        "AVAXUSDT", "SHIBUSDT", "LINKUSDT", "MATICUSDT", "UNIUSDT"
    )

    LaunchedEffect(Unit) {
        viewModel.herSeyiGetir()
        val socket = BinanceSocketYoneticisi { sembol, fiyat, yuzde ->
            viewModel.fiyatGuncelle(sembol, fiyat, yuzde)
        }
        socket.baslat()
    }

    KriptoTakipTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Canlı Kripto",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { yeniDeger ->
                                isDarkMode = yeniDeger
                                hafiza.temaKaydet(yeniDeger)
                            }
                        )
                    }

                    OutlinedTextField(
                        value = aramaMetni,
                        onValueChange = { aramaMetni = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                        placeholder = { Text("Coin Ara...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    TabRow(selectedTabIndex = seciliSekme) {
                        sekmeler.forEachIndexed { index, baslik ->
                            Tab(
                                selected = seciliSekme == index,
                                onClick = { seciliSekme = index },
                                text = { Text(baslik) }
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            val filtrelenmişListe = viewModel.tumCoinListesi.filter { coin ->
                val sekmeUygunmu = if (seciliSekme == 1) {
                    popülerCoinler.contains(coin.symbol)
                } else {
                    viewModel.favoriListesi.contains(coin.symbol)
                }
                val aramaUygunmu = coin.symbol.contains(aramaMetni, ignoreCase = true)
                sekmeUygunmu && aramaUygunmu
            }

            if (filtrelenmişListe.isEmpty() && seciliSekme == 0) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz favori coin eklemedin. Yıldızlara basmayı unutma!",
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtrelenmişListe, key = { it.symbol }) { coin ->
                        CoinDetayKarti(
                            symbol = coin.symbol,
                            fiyat = viewModel.fiyatlar[coin.symbol] ?: 0.0,
                            yuzde = viewModel.yuzdeler[coin.symbol] ?: "0.00",
                            eskiFiyat = viewModel.eskiFiyatlar[coin.symbol] ?: 0.0,
                            isFavori = viewModel.favoriListesi.contains(coin.symbol),
                            onFavoriClick = { viewModel.favoriDurumunuDegistir(coin.symbol) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CoinDetayKarti(
    symbol: String,
    fiyat: Double,
    yuzde: String,
    eskiFiyat: Double,
    isFavori: Boolean,
    onFavoriClick: () -> Unit
) {
    val yuzdeRengi = if (yuzde.startsWith("-")) Color.Red else Color(0xFF4CAF50)
    val fiyatRengi = when {
        fiyat > eskiFiyat && eskiFiyat != 0.0 -> Color(0xFF4CAF50)
        fiyat < eskiFiyat && eskiFiyat != 0.0 -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onFavoriClick) {
                    Icon(
                        imageVector = if (isFavori) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favori",
                        tint = if (isFavori) Color(0xFFFFD700) else Color.Gray
                    )
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = symbol, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(text = "% $yuzde", color = yuzdeRengi, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Text(text = "$fiyat $", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = fiyatRengi)
        }
    }
}
