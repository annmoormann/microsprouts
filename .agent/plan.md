# Project Plan

Refine MicroSprouts Task with enhanced recurrence logic (Start Time, Missed Behavior) and a Settings menu for global configuration.

## Project Brief

# Project Brief: MicroSprouts Task (Refined Recurrence)

MicroSprouts Task is a specialized productivity application within the MicroSprouts ecosystem. It is designed to help users manage complex workflows through a hierarchical task structure, dependency tracking, and automated recurrence, all wrapped in a modern, adaptive Android interface.

## New Features
*   **Enhanced Recurrence Configuration**: Tasks now include a "Start Time of Day" and specific behaviors for incomplete tasks in a cycle:
  *   **Add New**: Add next instance (default).
  *   **Replace**: Replace the incomplete instance with a new one.
  *   **Skip**: Do not add a new instance if the previous one is incomplete.
*   **Settings Management**: A new Settings screen accessible via a gear icon in the top-left to configure global defaults like the task start time.
*   **Data-Driven Persistence**: Use Room for tasks and DataStore for user preferences.

## Technical Stack (Existing + New)
*   **Persistence**: Room Database (Tasks), **DataStore Preferences** (Settings).
*   **UI**: Jetpack Compose, Material 3, Navigation 3, Compose Material Adaptive.
*   **Logic**: Kotlin Coroutines/Flow, WorkManager (for recurrence checks if needed).

## Implementation Steps
**Total Duration:** 1h 3m 10s

### Task_1_DataLayer: Implement Room database for task persistence, including entities for Tasks, Subtasks, and Dependencies. Setup a Repository to handle data operations and dependency logic.
- **Status:** FAILED / REOPENED
- **Current Issue:** The core database entity structures are missing or completely broken. `TaskRepository` lacks concrete Room operations, and the dependency logic is completely unwritten.
- **Acceptance Criteria:**
  - Room entities for Tasks and Dependencies defined
  - DAO for CRUD operations implemented
  - Repository with Flow support created
  - Build passes

### Task_2_NavigationAndTheme: Set up Material 3 energetic theme, Edge-to-Edge display, and Navigation 3 architecture for the app.
- **Status:** FAILED / REOPENED
- **Current Issue:** Custom type-safe navigation definitions (`Destination`) are missing or unrecognized by the build engine.
- **Acceptance Criteria:**
  - M3 Theme with vibrant colors implemented
  - Edge-to-Edge enabled
  - Navigation 3 host configured
  - App runs with empty home screen

### Task_6_CoreUIAndAdaptiveDashboard: Implement a simple task list and creation UI, then evolve it into an adaptive multi-pane dashboard featuring hierarchical subtasks and dependency/blocker identification.
- **Status:** FAILED / REOPENED
- **Current Issue:** Screen files were either deleted or generated as empty shells. Hierarchical components like `TaskTree` are completely missing structural layouts.
- **Acceptance Criteria:**
  - Simple list and basic add screen functional
  - Adaptive layout (ListDetailPaneScaffold) implemented for different screen sizes
  - Hierarchical tasks and blockers visually identified
  - The implemented UI must match the design provided in /home/ann/AndroidStudioProjects/MicroSprouts/input_images/image_0.png

### Task_7_FinalPolishAndVerify: Implement recurrence scheduling, generate adaptive app icon, perform final UI polish, and verify the app for stability and requirements alignment.
- **Status:** FAILED / REOPENED
- **Current Issue:** The application stability is zero; it currently fails basic compilation phases.
- **Acceptance Criteria:**
  - Recurrence scheduling (daily/weekly/custom) implemented
  - Adaptive icon created from /home/ann/AndroidStudioProjects/MicroSprouts/input_images/image_0.png
  - App does not crash
  - Build passes
  - All existing tests pass
  - The implemented UI must match the design provided in /home/ann/AndroidStudioProjects/MicroSprouts/input_images/image_0.png

### Task_8_SettingsAndDataRefinement: Implement DataStore for global settings and update the Task data model to support enhanced recurrence (Start Time and Missed Behavior). Create the Settings UI.
- **Status:** FAILED / REOPENED
- **Current Issue:** `SettingsRepository` was erased or left empty, breaking the global data persistence loop.
- **Acceptance Criteria:**
  - DataStore implemented for global preferences
  - Settings screen created and accessible via gear icon
  - Task Room entity updated with new recurrence fields
  - Build passes

### Task_9_EnhancedRecurrenceAndFinalVerify: Refine Task creation UI with new recurrence options and implement the logic for missed behaviors (Add New, Replace, Skip). Perform final stability verification.
- **Status:** FAILED / REOPENED
- **Current Issue:** No functional time pickers or logic for missed-task replacement/skipping exists in the codebase.
- **Acceptance Criteria:**
  - AddEditTaskScreen updated with Start Time and Missed Behavior selection
  - Missed behavior logic (Add New, Replace, Skip) functional
  - App does not crash
  - All existing tests pass
  - The implemented UI must match the design provided in /home/ann/AndroidStudioProjects/MicroSprouts/input_images/image_0.png
