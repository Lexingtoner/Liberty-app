package com.svoboden.app.domain.model

data class StartSession(
    val id: Long = 0,
    val habitId: Long,
    val startedAt: Long,
    val restartedAt: Long? = null,
    val isActive: Boolean = true
) {
    /** Момент, от которого реально считается текущий стрик. */
    val effectiveStart: Long get() = restartedAt ?: startedAt
}
