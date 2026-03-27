package com.betul.kriptotakip.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.betul.kriptotakip.presentation.CoinViewModel
import com.betul.kriptotakip.ui.auth.LoginScreen
import com.betul.kriptotakip.ui.auth.RegisterScreen
import com.betul.kriptotakip.ui.main.CryptoMainScreen
import com.betul.kriptotakip.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun KriptoAppNavigator() {
    val auth = FirebaseAuth.getInstance()
    
    // Giriş durumunu Firebase'in kendisine bağlayalım ki daha garantici olsun
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var showRegisterScreen by remember { mutableStateOf(false) }
    var showProfileScreen by remember { mutableStateOf(false) }
    
    val coinViewModel: CoinViewModel = viewModel()

    if (currentUser != null) {
        if (showProfileScreen) {
            ProfileScreen(
                onBack = { showProfileScreen = false },
                onLogout = { 
                    auth.signOut()
                    currentUser = null
                    showProfileScreen = false
                }
            )
        } else {
            CryptoMainScreen(
                onNavigateToProfile = { showProfileScreen = true }
            )
        }
    } else {
        if (showRegisterScreen) {
            RegisterScreen(
                onBackToLogin = { showRegisterScreen = false }
            )
        } else {
            LoginScreen(
                onLoginSuccess = { 
                    currentUser = auth.currentUser
                    coinViewModel.fetchAllData()
                },
                onNavigateToRegister = { showRegisterScreen = true }
            )
        }
    }
}
