package com.example.securescan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.securescan.data.network.FirebaseAuthService
import com.example.securescan.data.repository.FirebaseSeeder
import com.example.securescan.ui.components.BottomNavigation
import com.example.securescan.ui.screens.AllNewsScreen
import com.example.securescan.ui.screens.HomeScreen
import com.example.securescan.ui.screens.LoginScreen
import com.example.securescan.ui.screens.NewsDetailScreen
import com.example.securescan.ui.screens.NewsScreen
import com.example.securescan.ui.screens.NotificationScreen
import com.example.securescan.ui.screens.PersonalInformationScreen
import com.example.securescan.ui.screens.RegisterScreen
import com.example.securescan.ui.screens.ReportDataScreen
import com.example.securescan.ui.screens.ReportScren
import com.example.securescan.ui.screens.ScanPhoneAndCardScreen
import com.example.securescan.ui.screens.ScanScreenTest
import com.example.securescan.ui.screens.SettingsScreen
import com.example.securescan.ui.screens.WelcomeScreen
import com.example.securescan.ui.theme.AppTheme
import com.example.securescan.viewmodel.AuthViewModel
import com.example.securescan.viewmodel.AuthViewModelFactory
import com.example.securescan.viewmodel.NewsViewModel
import com.example.securescan.viewmodel.ReportsViewModel
import com.example.securescan.viewmodel.ScanPhoneCardViewModel
import com.example.securescan.viewmodel.ScanViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val scanViewModel: ScanViewModel by viewModels()
    private val newsViewModel: NewsViewModel by viewModels()
    private val reportsViewModel: ReportsViewModel by viewModels()
    private val scanphonecardViewModel : ScanPhoneCardViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authService = FirebaseAuthService()
            val factory = AuthViewModelFactory(authService)
            val viewModel: AuthViewModel = viewModel(factory = factory)

            AppTheme {
                FirebaseSeeder.seedData()
                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

                    DisposableEffect(Unit) {
                        val listener = FirebaseAuth.AuthStateListener { auth ->
                            currentUser = auth.currentUser
                            if (auth.currentUser == null && currentRoute !in listOf("welcome", "login", "register")) {
                                navController.navigate("welcome") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        FirebaseAuth.getInstance().addAuthStateListener(listener)
                        onDispose {
                            FirebaseAuth.getInstance().removeAuthStateListener(listener)
                        }
                    }

                    Scaffold(
                        bottomBar = {
                            if (currentRoute !in listOf("welcome", "login", "register")) {
                                BottomNavigation { destination ->
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = if (currentUser != null) "home" else "welcome",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("welcome") {
                                WelcomeScreen(
                                    onLoginClick = { navController.navigate("login") },
                                    onRegisterClick = { navController.navigate("register") }
                                )
                            }
                            composable("login") {
                                LoginScreen(
                                    viewModel = viewModel,
                                    onLoginSuccess = {
                                        navController.navigate("home") {
                                            popUpTo(0)
                                            launchSingleTop = true
                                        }
                                    },
                                    onCreateAccountClick = { navController.navigate("register") },
                                    onForgotPasswordClick = {},
                                    onGoogleSignInClick = {},
                                    onFacebookSignInClick = {},
                                    onAppleSignInClick = {}
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    viewModel = viewModel,
                                    onRegisterSuccess = {
                                        navController.navigate("home") {
                                            popUpTo(0)
                                            launchSingleTop = true
                                        }
                                    },
                                    onLoginClick = { navController.navigate("login") },
                                    onGoogleSignInClick = {},
                                    onFacebookSignInClick = {},
                                    onAppleSignInClick = {}
                                )
                            }
                            composable("home") { HomeScreen(
                                navController = navController,
                            ) }
                            composable("settings") {
                                SettingsScreen(
                                    navController = navController,
                                    authViewModel = viewModel,
                                    onLogout = {
                                        viewModel.logout()
                                        navController.navigate("welcome") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("edit_profile") {
                                PersonalInformationScreen(
                                    navController = navController,
                                )
                            }

                            composable("article") {
                                NewsScreen(
                                    onNavigateToNewsDetail = { postId ->
                                        navController.navigate("news_detail/$postId")
                                    }
                                    , navController
                                )
                            }

                            composable("scan") {
                                ScanScreenTest(viewModel = scanViewModel)
                            }
                            composable("notifications") { 
                                NotificationScreen(navController = navController) 
                            }

                            composable(
                                route = "news_detail/{postId}",
                                arguments = listOf(navArgument("postId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                                NewsDetailScreen(postId , viewModel = newsViewModel , onBackPressed = { navController.popBackStack()  } ,navController)
                            }
                            composable("report") {
                                ReportScren(
                                    viewModel = reportsViewModel
                                )
                            }
                            composable("report_data") {
                                ReportDataScreen(
                                    viewModel = reportsViewModel,
                                    userId = currentUser?.email ?: "",
                                    onNavigateBack = { navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        } },
                                )
                            }
                            composable("check_phone_bank") {
                                ScanPhoneAndCardScreen(
                                    viewModel = scanphonecardViewModel ,
                                    onNavigateBack = { navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        } },
                                    onViewHistory = {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("all_news") {
                                AllNewsScreen(navController = navController)
                            }

                        }
                    }
                }
            }
        }
    }
}
