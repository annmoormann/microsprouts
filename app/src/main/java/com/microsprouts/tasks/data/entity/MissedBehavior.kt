package com.microsprouts.tasks.data.entity

/**
 * Represents how the system handles a missed recurring task.
 */
enum class MissedBehavior {
    ADD_NEW,
    REPLACE,
    SKIP
}
