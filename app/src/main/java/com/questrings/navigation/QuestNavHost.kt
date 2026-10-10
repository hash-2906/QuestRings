package com.questrings.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.questrings.domain.usecase.auth.ObserveCurrentUserUseCase
import com.questrings.presentation.home.HomeScreen
import com.questrings.presentation.home.HomeViewModel
import com.questrings.presentation.rooms.detail.RoomDetailScreen
import com.questrings.presentation.rooms.detail.RoomDetailViewModel
import com.questrings.presentation.rooms.join.JoinRoomScreen
import com.questrings.presentation.rooms.join.JoinRoomViewModel
import com.questrings.presentation.rooms.list.RoomsScreen
import com.questrings.presentation.rooms.list.RoomsViewModel
import com.questrings.presentation.profile.ProfileScreen
import com.questrings.presentation.profile.public.PublicProfileScreen
import com.questrings.presentation.rooms.share.RoomShareScreen
import com.questrings.presentation.showcase.DesignSystemShowcaseScreen
import com.questrings.presentation.social.SocialScreen
import com.questrings.presentation.splash.SplashScreen
import com.questrings.presentation.stats.StatsScreen
import com.questrings.presentation.stats.StatsViewModel
import com.questrings.presentation.tasks.TasksScreen
import com.questrings.presentation.tasks.TasksViewModel
import com.questrings.presentation.xpledger.XpLedgerScreen

@Composable
fun QuestNavHost(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestination.Splash.route,
    onGoogleSignInTrigger: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Splash Gate
        composable(AppDestination.Splash.route) {
            SplashScreen(
                observeCurrentUserUseCase = observeCurrentUserUseCase,
                onNavigateToHome = {
                    navController.navigate(AppDestination.HomeGraph.route) {
                        popUpTo(AppDestination.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(AppDestination.AuthGraph.route) {
                        popUpTo(AppDestination.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToProfileSetup = {
                    navController.navigate(AppDestination.ProfileSetup.route) {
                        popUpTo(AppDestination.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Auth Graph (Login, Register, ProfileSetup)
        authGraph(
            navController = navController,
            onGoogleSignInTrigger = onGoogleSignInTrigger
        )

        // 3. Home Graph (HomeScreen, TasksScreen, StatsScreen, RoomsScreen, ProfileScreen, XpLedgerScreen, SocialScreen, PublicProfileScreen)
        navigation(
            startDestination = AppDestination.Home.route,
            route = AppDestination.HomeGraph.route
        ) {
            composable(AppDestination.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToShowcase = {
                        navController.navigate(AppDestination.Showcase.route)
                    },
                    onNavigateToTasks = {
                        navController.navigate(AppDestination.Tasks.route)
                    },
                    onNavigateToRooms = {
                        navController.navigate(AppDestination.Rooms.route)
                    },
                    onNavigateToSocial = {
                        navController.navigate(AppDestination.Social.route)
                    },
                    onNavigateToStats = {
                        navController.navigate(AppDestination.Profile.route)
                    },
                    onNavigateToAuth = {
                        navController.navigate(AppDestination.AuthGraph.route) {
                            popUpTo(AppDestination.HomeGraph.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppDestination.Tasks.route) {
                val tasksViewModel: TasksViewModel = hiltViewModel()
                TasksScreen(
                    viewModel = tasksViewModel,
                    onNavigateToRings = {
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(AppDestination.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToRooms = {
                        navController.navigate(AppDestination.Rooms.route)
                    },
                    onNavigateToSocial = {
                        navController.navigate(AppDestination.Social.route)
                    },
                    onNavigateToStats = {
                        navController.navigate(AppDestination.Profile.route)
                    }
                )
            }

            composable(AppDestination.Stats.route) {
                val statsViewModel: StatsViewModel = hiltViewModel()
                StatsScreen(
                    viewModel = statsViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Phase 5: Rooms List Screen
            composable(AppDestination.Rooms.route) {
                val roomsViewModel: RoomsViewModel = hiltViewModel()
                RoomsScreen(
                    viewModel = roomsViewModel,
                    onNavigateToRoom = { roomId ->
                        navController.navigate(AppDestination.RoomDetail.createRoute(roomId))
                    },
                    onNavigateToShare = { roomId ->
                        navController.navigate(AppDestination.RoomShare.createRoute(roomId))
                    },
                    onNavigateToJoin = {
                        navController.navigate(AppDestination.JoinRoom.route)
                    },
                    onNavigateToRings = {
                        navController.navigate(AppDestination.Home.route) {
                            popUpTo(AppDestination.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToTasks = {
                        navController.navigate(AppDestination.Tasks.route)
                    },
                    onNavigateToSocial = {
                        navController.navigate(AppDestination.Social.route)
                    },
                    onNavigateToStats = {
                        navController.navigate(AppDestination.Profile.route)
                    }
                )
            }

            // Phase 7: Profile Screen
            composable(AppDestination.Profile.route) {
                ProfileScreen(
                    onNavigateToXpLedger = {
                        navController.navigate(AppDestination.XpLedger.route)
                    },
                    onLogout = {
                        navController.navigate(AppDestination.AuthGraph.route) {
                            popUpTo(AppDestination.HomeGraph.route) { inclusive = true }
                        }
                    }
                )
            }

            // Phase 7: XP Ledger Screen
            composable(AppDestination.XpLedger.route) {
                XpLedgerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Phase 8: Social Screen
            composable(AppDestination.Social.route) {
                SocialScreen(
                    onNavigateToPublicProfile = { userId ->
                        navController.navigate(AppDestination.PublicProfile.createRoute(userId))
                    }
                )
            }

            // Phase 8: Public Profile Screen
            composable(
                route = AppDestination.PublicProfile.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType }
                )
            ) {
                PublicProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Phase 5: Join Room Screen (with optional deep link query param)
        composable(
            route = AppDestination.JoinRoom.routeWithArgs,
            arguments = listOf(
                navArgument("code") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "questrings://join/{code}" }
            )
        ) { backStackEntry ->
            val initialCode = backStackEntry.arguments?.getString("code")
            val joinViewModel: JoinRoomViewModel = hiltViewModel()
            JoinRoomScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoom = { roomId ->
                    navController.navigate(AppDestination.RoomDetail.createRoute(roomId)) {
                        popUpTo(AppDestination.Rooms.route)
                    }
                },
                viewModel = joinViewModel,
                initialCode = initialCode
            )
        }

        // Plain route alias for Join Room
        composable(AppDestination.JoinRoom.route) {
            val joinViewModel: JoinRoomViewModel = hiltViewModel()
            JoinRoomScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoom = { roomId ->
                    navController.navigate(AppDestination.RoomDetail.createRoute(roomId)) {
                        popUpTo(AppDestination.Rooms.route)
                    }
                },
                viewModel = joinViewModel
            )
        }

        // Phase 5: Room Detail Screen
        composable(
            route = AppDestination.RoomDetail.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType }
            )
        ) {
            val roomDetailViewModel: RoomDetailViewModel = hiltViewModel()
            RoomDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToShare = { roomId ->
                    navController.navigate(AppDestination.RoomShare.createRoute(roomId))
                },
                onNavigateToAssignmentDetail = { roomId, assignmentId ->
                    navController.navigate(AppDestination.AssignmentDetail.createRoute(roomId, assignmentId))
                },
                viewModel = roomDetailViewModel
            )
        }

        // Phase 6: Assignment Detail Screen
        composable(
            route = AppDestination.AssignmentDetail.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType },
                navArgument("assignmentId") { type = NavType.StringType }
            )
        ) {
            val assignmentDetailViewModel: com.questrings.presentation.assignment.detail.AssignmentDetailViewModel = hiltViewModel()
            com.questrings.presentation.assignment.detail.AssignmentDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = assignmentDetailViewModel
            )
        }

        // Phase 5: Room Share Screen
        composable(
            route = AppDestination.RoomShare.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            RoomShareScreen(
                roomId = roomId,
                onNavigateToRoom = { id ->
                    navController.navigate(AppDestination.RoomDetail.createRoute(id)) {
                        popUpTo(AppDestination.Rooms.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Design System Showcase
        composable(AppDestination.Showcase.route) {
            DesignSystemShowcaseScreen(
                onNavigateToHome = {
                    navController.navigate(AppDestination.HomeGraph.route)
                }
            )
        }
    }
}
