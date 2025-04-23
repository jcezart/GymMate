# GymMate - Fitness App
A mobile app built in Kotlin to manage gym workouts.

## Features
- **List workout categories**: Display workout categories (e.g., Workout A, Workout B) in a horizontal LazyRow using Jetpack Compose, with options to add, rename, or delete categories.
- **Exercise management**: Add, update, and delete exercises within categories, with details such as exercise name, sets, reps, weight, and date.
- **Interactive UI**: Expandable exercise cards for editing details, dialogs for adding/renaming categories, and confirmation dialogs for deletions.
- **Local data storage**: Store workout categories and exercises locally using Room Database.
- **State management**: Leverage ViewModel with StateFlow for reactive state management, ensuring seamless UI updates when data changes.
- **MVVM Architecture**: Follows Model-View-ViewModel pattern with clear separation of concerns:
  -  **Model**: Exercise and Category entities with ExerciseDAO for database operations.
  -  **View**: Jetpack Compose composables (GymMateScreen, ExerciseCard, etc.) for UI rendering and user interactions.
  -  **ViewModel**: GymMateViewModel for managing state, business logic, and database interactions.

## Tech Stack
- **Kotlin**: Primary programming language for development.
- **Jetpack Compose**: Modern Android UI toolkit for building responsive and declarative user interfaces.
- **Room Database**: For local persistence of workout categories and exercises.
- **Coroutines**: For asynchronous programming, handling database operations and state updates.
- **Android Architecture Components**: ViewModel, StateFlow, and viewModelScope for lifecycle-aware state management and reactive data flows.

## Get the App
<a href="https://play.google.com/store/apps/details?id=com.juliocezar.gymmate&hl=pt_BR">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="150">
</a>
