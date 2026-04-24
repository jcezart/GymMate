<div align="center">

# 💪 GymMate

**A native Android app to manage gym workouts — available on the Google Play Store**

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_26%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room-Database-FF6F00?style=for-the-badge&logo=android&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-6A0DAD?style=for-the-badge)
![Play Store](https://img.shields.io/badge/Google_Play-Published-34A853?style=for-the-badge&logo=googleplay&logoColor=white)

<br/>

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.juliocezar.gymmate&hl=pt_BR)

</div>

---

## 📖 About

**GymMate** is a native Android workout tracker built entirely in Kotlin with Jetpack Compose. It lets users organize their training into named workout categories (e.g., Workout A, Workout B) and manage exercises within each — tracking name, sets, reps, weight, and date. All data is stored locally using **Room Database**, and the UI reacts to state changes through **MVVM + StateFlow**.

The app is **published on the Google Play Store** and was built from scratch as a complete, production-grade project.

---

## ✨ Features

- 🗂️ **Workout categories** — Create, rename, and delete workout groups (e.g., Push, Pull, Legs) displayed in a horizontal `LazyRow`
- 🏋️ **Exercise management** — Add, update, and delete exercises within each category, with fields for name, sets, reps, weight, and date
- 🃏 **Expandable exercise cards** — Tap to expand cards for inline editing without leaving the screen
- 💬 **Dialogs** — Dedicated dialogs for adding/renaming categories and confirming deletions
- 💾 **Local persistence** — Full offline support via Room Database — no account or internet required
- 🔄 **Reactive state** — UI driven by `StateFlow` and `ViewModel`, updating seamlessly on every data change
- 🏗️ **MVVM architecture** — Clean separation between data, business logic, and UI layers

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin (100%) |
| UI Framework | Jetpack Compose |
| Architecture | MVVM (ViewModel + StateFlow) |
| Local Database | Room Database (Entity + DAO + Database) |
| Async Operations | Kotlin Coroutines + `viewModelScope` |
| State Management | `StateFlow` + `collectAsState()` |
| Build System | Gradle (Kotlin DSL) |
| Distribution | Google Play Store |

---

## 🔍 Key Implementation Details

### MVVM Architecture

The app follows a strict MVVM pattern with three clear layers, keeping the Compose UI dumb and the ViewModel in full control of state and business logic.

```
Model (Room)         ViewModel              View (Compose)
─────────────        ──────────────         ───────────────
ExerciseEntity  ←──  GymMateViewModel  ──►  GymMateScreen
CategoryEntity       StateFlow<UiState>     ExerciseCard
ExerciseDAO          viewModelScope         CategoryRow
```

### Room Database — Entity + DAO + Database

```kotlin
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Float,
    val date: String
)

@Dao
interface ExerciseDAO {
    @Query("SELECT * FROM exercises WHERE categoryId = :categoryId")
    fun getExercisesByCategory(categoryId: Int): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)
}
```

### ViewModel with StateFlow

State is exposed via `StateFlow` and all database operations run inside `viewModelScope`, keeping them lifecycle-aware and off the main thread.

```kotlin
class GymMateViewModel(private val dao: ExerciseDAO) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntity>> = _exercises.asStateFlow()

    fun addExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            dao.insertExercise(exercise)
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            dao.deleteExercise(exercise)
        }
    }
}
```

### Jetpack Compose UI — LazyRow + Expandable Cards

Categories are displayed in a horizontal `LazyRow` and each exercise is an expandable card for inline editing — no navigation required.

```kotlin
@Composable
fun GymMateScreen(viewModel: GymMateViewModel) {
    val categories by viewModel.categories.collectAsState()
    val exercises by viewModel.exercises.collectAsState()

    Column {
        // Horizontal category selector
        LazyRow {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    onRename = { viewModel.renameCategory(it) },
                    onDelete = { viewModel.deleteCategory(it) }
                )
            }
        }

        // Exercise list with expandable cards
        LazyColumn {
            items(exercises) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onUpdate = { viewModel.updateExercise(it) },
                    onDelete = { viewModel.deleteExercise(it) }
                )
            }
        }
    }
}
```

### Confirmation & Input Dialogs

All destructive actions (delete category, delete exercise) require confirmation through `AlertDialog`, and add/rename actions use custom input dialogs — keeping the UX safe and explicit.

```kotlin
if (showDeleteDialog) {
    AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Delete exercise?") },
        text = { Text("This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = {
                onDelete(exercise)
                showDeleteDialog = false
            }) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
        }
    )
}
```

---

## 🏗️ Project Structure

```
GymMate/
└── app/src/main/java/com/juliocezar/gymmate/
    ├── data/
    │   ├── model/
    │   │   ├── ExerciseEntity.kt       # Room entity for exercises
    │   │   └── CategoryEntity.kt       # Room entity for workout categories
    │   ├── dao/
    │   │   └── ExerciseDAO.kt          # DAO interface (CRUD operations)
    │   └── database/
    │       └── GymMateDatabase.kt      # Room database builder
    ├── ui/
    │   ├── screens/
    │   │   └── GymMateScreen.kt        # Main screen composable
    │   ├── components/
    │   │   ├── ExerciseCard.kt         # Expandable exercise card
    │   │   ├── CategoryChip.kt         # Horizontal category selector
    │   │   └── AddExerciseDialog.kt    # Input dialog for new exercises
    │   └── theme/
    │       └── Theme.kt                # Material 3 theme
    ├── viewmodel/
    │   └── GymMateViewModel.kt         # State + business logic
    └── MainActivity.kt
```

---

## 📲 Download

GymMate is live on the **Google Play Store**:

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.juliocezar.gymmate&hl=pt_BR)

---

## 🚀 Getting Started (Local Build)

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android device or emulator running API 26+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/jcezart/GymMate.git
```

2. **Open in Android Studio**

Open the cloned folder and wait for Gradle sync to complete.

3. **Run the app**

Select an emulator or connected device and press `▶ Run`.

---

## 📚 Concepts Practiced

- Declarative UI with **Jetpack Compose** (`LazyRow`, `LazyColumn`, `AlertDialog`, expandable cards)
- **MVVM architecture** with clear separation between Model, ViewModel, and View
- **Room Database** — Entity, DAO, and Database setup for full local CRUD
- **Kotlin Coroutines** with `viewModelScope` for async, lifecycle-aware operations
- **StateFlow** + `collectAsState()` for reactive, unidirectional data flow
- **Gradle Kotlin DSL** for build configuration
- **App publishing** — signing, Play Store listing, and production release pipeline

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

<div align="center">
  Made with ❤️ and Kotlin · <a href="https://github.com/jcezart">@jcezart</a>
</div>
