package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.FindFriendTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            FindFriendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        // --- Authentications and Onboardings ---
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("onboarding") {
                            OnboardingScreen(navController)
                        }
                        composable("login") {
                            LoginScreen(navController, viewModel)
                        }
                        composable("signup") {
                            SignupScreen(navController, viewModel)
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController)
                        }
                        
                        // --- Main Dashboard Panel ---
                        composable("home") {
                            MainTabScreen(navController, viewModel)
                        }
                        
                        // --- Custom Match Engine Flow Screen ---
                        composable("search_processing") {
                            SearchProcessingScreen(navController, viewModel)
                        }
                        composable("results") {
                            ResultsScreen(navController, viewModel)
                        }
                        
                        // --- Compliance Safety Portals ---
                        composable("privacy_policy") {
                            PrivacyPolicyScreen(navController)
                        }
                        composable("terms") {
                            TermsScreen(navController)
                        }
                        
                        // --- Abuse Reporting (Decodes deep social parameters safely) ---
                        composable(
                            route = "report_abuse/{profileName}/{platformName}/{profileLink}",
                            arguments = listOf(
                                navArgument("profileName") { type = NavType.StringType },
                                navArgument("platformName") { type = NavType.StringType },
                                navArgument("profileLink") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val rawName = backStackEntry.arguments?.getString("profileName") ?: ""
                            val rawPlatform = backStackEntry.arguments?.getString("platformName") ?: ""
                            val rawLink = backStackEntry.arguments?.getString("profileLink") ?: ""
                            
                            val decodedName = try {
                                URLDecoder.decode(rawName, StandardCharsets.UTF_8.toString())
                            } catch (e: Exception) {
                                rawName
                            }
                            val decodedPlatform = try {
                                URLDecoder.decode(rawPlatform, StandardCharsets.UTF_8.toString())
                            } catch (e: Exception) {
                                rawPlatform
                            }
                            val decodedLink = try {
                                URLDecoder.decode(rawLink, StandardCharsets.UTF_8.toString())
                            } catch (e: Exception) {
                                rawLink
                            }
                            
                            ReportAbuseScreen(
                                navController = navController,
                                viewModel = viewModel,
                                profileName = decodedName,
                                platformName = decodedPlatform,
                                profileLink = decodedLink
                            )
                        }
                    }
                }
            }
        }
    }
}
