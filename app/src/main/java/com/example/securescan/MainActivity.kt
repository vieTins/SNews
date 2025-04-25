package com.example.securescan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.securescan.data.network.FirebaseAuthService
import com.example.securescan.data.repository.FirebaseSeeder
import com.example.securescan.ui.components.BottomNavigation
import com.example.securescan.ui.screens.*
import com.example.securescan.ui.theme.AppTheme
import com.example.securescan.viewmodel.*
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
                            if (auth.currentUser == null) {
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
                            startDestination =  "welcome",
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
                            composable("edit_information_profile") {
                                EditProfileScreen(
                                    onSaveClick = {
                                        navController.navigate("edit_profile") {
                                            popUpTo("edit_profile") { inclusive = true }
                                        }

                                    }
                                )
                            }
                            composable("article") {
                                NewsScreen(
                                    onNavigateToNewsDetail = { postId ->
                                        navController.navigate("news_detail/$postId")
                                    }
                                )
                            }

                            composable("scan") {
                                ScanScreenTest(viewModel = scanViewModel)
                            }
                            composable("notifications") { NotificationScreen() }

                            composable(
                                route = "news_detail/{postId}",
                                arguments = listOf(navArgument("postId") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                                NewsDetailScreen(postId , viewModel = newsViewModel , onBackPressed = { navController.popBackStack() })
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
                        }
                    }
                }
            }
        }
    }
}
