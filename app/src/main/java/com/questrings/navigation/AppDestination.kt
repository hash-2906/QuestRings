package com.questrings.navigation

sealed class AppDestination(val route: String) {
    data object Splash : AppDestination("splash")

    // Auth Graph
    data object AuthGraph : AppDestination("auth_graph")
    data object Login : AppDestination("login")
    data object Register : AppDestination("register")
    data object ProfileSetup : AppDestination("profile_setup")

    // Home Graph
    data object HomeGraph : AppDestination("home_graph")
    data object Home : AppDestination("home")
    data object Tasks : AppDestination("tasks")
    data object Stats : AppDestination("stats")

    // Phase 5 Rooms Graph
    data object Rooms : AppDestination("rooms")
    data object JoinRoom : AppDestination("join_room") {
        const val routeWithArgs: String = "join_room?code={code}"
        fun createRoute(code: String? = null): String =
            if (code != null) "join_room?code=$code" else "join_room"
    }
    data object RoomDetail : AppDestination("room_detail/{roomId}") {
        fun createRoute(roomId: String): String = "room_detail/$roomId"
    }
    data object RoomShare : AppDestination("room_share/{roomId}") {
        fun createRoute(roomId: String): String = "room_share/$roomId"
    }

    // Phase 6 Assignment Detail
    data object AssignmentDetail : AppDestination("rooms/{roomId}/assignments/{assignmentId}") {
        fun createRoute(roomId: String, assignmentId: String): String =
            "rooms/$roomId/assignments/$assignmentId"
    }

    // Phase 7 Profile & XP Ledger
    data object Profile : AppDestination("profile")
    data object XpLedger : AppDestination("profile/xp-history")

    // Phase 8 Social & Public Profile
    data object Social : AppDestination("social")
    data object PublicProfile : AppDestination("profile/public/{userId}") {
        fun createRoute(userId: String): String = "profile/public/$userId"
    }

    // Phase 1 Showcase
    data object Showcase : AppDestination("showcase")
}
