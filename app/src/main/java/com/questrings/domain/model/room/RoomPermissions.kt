package com.questrings.domain.model.room

object RoomPermissions {
    fun canCreateAssignment(role: RoomRole) =
        role == RoomRole.ADMIN || role == RoomRole.MODERATOR

    fun canDeleteAssignment(role: RoomRole) =
        role == RoomRole.ADMIN

    fun canRemoveMember(actorRole: RoomRole, targetRole: RoomRole): Boolean {
        return when (actorRole) {
            RoomRole.ADMIN -> true
            RoomRole.MODERATOR -> targetRole == RoomRole.MEMBER
            RoomRole.MEMBER -> false
        }
    }

    fun canAssignModerator(role: RoomRole) =
        role == RoomRole.ADMIN

    fun canDeleteRoom(role: RoomRole) =
        role == RoomRole.ADMIN

    fun canManageJoinRequests(role: RoomRole) =
        role == RoomRole.ADMIN || role == RoomRole.MODERATOR
}
