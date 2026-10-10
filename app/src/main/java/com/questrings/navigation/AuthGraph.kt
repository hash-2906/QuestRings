package com.questrings.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.questrings.presentation.auth.AuthViewModel
import com.questrings.presentation.auth.screens.LoginScreen
import com.questrings.presentation.auth.screens.ProfileSetupScreen
import com.questrings.presentation.auth.screens.RegisterScreen

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onGoogleSignInTrigger: () -> Unit
) {
    navigation(
        startDestination = AppDestination.Login.route,
        route = AppDestination.AuthGraph.route
    ) {
        composable(
            route = AppDestination.Login.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(350)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutVertically(
                    targetOffsetY = { -100 },
                    animationSpec = tween(250)
                )
            }
        ) {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LoginScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToRegister = {
                    navController.navigate(AppDestination.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(AppDestination.HomeGraph.route) {
                        popUpTo(AppDestination.AuthGraph.route) { inclusive = true }
                    }
                },
                onNavigateToProfileSetup = {
                    navController.navigate(AppDestination.ProfileSetup.route)
                },
                onGoogleSignInClick = onGoogleSignInTrigger
            )
        }

        composable(
            route = AppDestination.Register.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(350)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutVertically(
                    targetOffsetY = { -100 },
                    animationSpec = tween(250)
                )
            }
        ) {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            RegisterScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToProfileSetup = {
                    navController.navigate(AppDestination.ProfileSetup.route)
                },
                onGoogleSignInClick = onGoogleSignInTrigger
            )
        }

        composable(
            route = AppDestination.ProfileSetup.route,
            enterTransition = {
                fadeIn(animationSpec = tween(350)) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(350)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250)) + slideOutVertically(
                    targetOffsetY = { -100 },
                    animationSpec = tween(250)
                )
            }
        ) {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ProfileSetupScreen(
                viewModel = viewModel,
                uiState = uiState,
                onNavigateToHome = {
                    navController.navigate(AppDestination.HomeGraph.route) {
                        popUpTo(AppDestination.AuthGraph.route) { inclusive = true }
                    }
                },
                onNavigateBackToAuth = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(AppDestination.AuthGraph.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
