package com.questrings.domain.model.assignment

enum class CompletionVerificationStatus {
    AUTO_APPROVED,   // verification not required for this room
    PENDING,         // submitted, awaiting Admin/Mod approval
    APPROVED,
    REJECTED
}
