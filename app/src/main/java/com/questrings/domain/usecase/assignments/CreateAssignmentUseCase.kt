package com.questrings.domain.usecase.assignments

import com.questrings.domain.model.assignment.Assignment
import com.questrings.domain.model.assignment.AssignmentStatus
import com.questrings.domain.model.assignment.AssignmentType
import com.questrings.domain.model.assignment.ChecklistItem
import com.questrings.domain.repository.AssignmentRepository
import java.util.UUID
import javax.inject.Inject

class CreateAssignmentUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(
        roomId: String,
        createdBy: String,
        title: String,
        description: String,
        type: AssignmentType,
        checklistItems: List<String> = emptyList(),
        dueAt: Long,
        requiresVerification: Boolean = false
    ): Result<Assignment> = runCatching {
        require(title.isNotBlank()) { "Assignment title cannot be blank" }
        require(dueAt > System.currentTimeMillis()) { "Due date must be in the future" }
        if (type == AssignmentType.CHECKLIST) {
            require(checklistItems.size >= 2) { "Checklists must contain at least 2 items" }
        }

        val items = if (type == AssignmentType.CHECKLIST) {
            checklistItems.mapIndexed { index, label ->
                ChecklistItem(
                    id = UUID.randomUUID().toString(),
                    label = label.trim(),
                    sortOrder = index
                )
            }
        } else {
            emptyList()
        }

        val assignment = Assignment(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            createdBy = createdBy,
            title = title.trim(),
            description = description.trim(),
            type = type,
            checklistItems = items,
            status = AssignmentStatus.UPCOMING,
            requiresVerification = requiresVerification,
            createdAt = System.currentTimeMillis(),
            dueAt = dueAt,
            totalCompletions = 0,
            maxPoints = 100
        )

        assignmentRepository.createAssignment(assignment).getOrThrow()
        assignment
    }
}
