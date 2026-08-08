package com.example.gymmate.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymmate.domain.model.Exercise
import com.example.gymmate.presentation.GymMateAction
import com.example.gymmate.presentation.GymMateUiState
import com.example.gymmate.presentation.component.CustomTooltip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.example.gymmate.presentation.component.RestTimerBar
import com.example.gymmate.presentation.timer.RestTimerAction
import com.example.gymmate.presentation.timer.RestTimerUiState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.lazy.rememberLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.material.icons.filled.DragHandle
import sh.calvin.reorderable.ReorderableItem
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.rememberDateRangePickerState
import java.time.Instant
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AssistChip
import androidx.compose.material3.OutlinedButton



private fun getDaysStartMillis(
    nowMillis: Long,
    days: Long
): Long {

    val zone = ZoneId.systemDefault()

    val startDate = Instant
        .ofEpochMilli(nowMillis)
        .atZone(zone)
        .toLocalDate()
        .minusDays(days - 1)

    return startDate
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
}

private fun getPeriodStartMillis(
    nowMillis: Long,
    period: Period
): Long {

    val zone = ZoneId.systemDefault()

    val startDate = Instant
        .ofEpochMilli(nowMillis)
        .atZone(zone)
        .toLocalDate()
        .minus(period)

    return startDate
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
}

private fun pickerMillisToLocalStart(
    pickerMillis: Long
): Long {

    val localDate = Instant
        .ofEpochMilli(pickerMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()

    return localDate
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun pickerMillisToLocalEndExclusive(
    pickerMillis: Long
): Long {

    val localDate = Instant
        .ofEpochMilli(pickerMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .plusDays(1)

    return localDate
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun GymMateScreen(
    state: GymMateUiState,
    timerState: RestTimerUiState,
    onAction: (GymMateAction) -> Unit,
    onTimerAction: (RestTimerAction) -> Unit
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf("") }
    var categoryToRename by remember { mutableStateOf("") }

    state.errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { onAction(GymMateAction.DismissError) },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { onAction(GymMateAction.DismissError) }) {
                    Text("OK")
                }
            }
        )
    }

    var reorderedExercises by remember(
        state.selectedCategory,
        state.exercises
    ) {
        mutableStateOf(state.exercises)
    }

    val lazyListState = rememberLazyListState()

    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            reorderedExercises = reorderedExercises
                .toMutableList()
                .apply {
                    add(
                        index = to.index,
                        element = removeAt(from.index)
                    )
                }
        }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                RestTimerBar(
                    time = timerState.formattedTime,
                    isRunning = timerState.isRunning,
                    onToggleTimer = {
                        onTimerAction(RestTimerAction.ToggleTimer)
                    },
                    onSubtractThirtySeconds = {
                        onTimerAction(RestTimerAction.SubtractThirtySeconds)
                    },
                    onAddThirtySeconds = {
                        onTimerAction(RestTimerAction.AddThirtySeconds)
                    },
                    onResetTimer = {
                        onTimerAction(RestTimerAction.ResetTimer)
                    }
                )
            },
            floatingActionButton = {
                if (state.activeSession == null) {
                    GymMateFAB {
                        state.selectedCategory?.let { selectedCategory ->

                            val nextPosition = state.exercises
                                .filter { it.category == selectedCategory }
                                .maxOfOrNull { it.position }
                                ?.plus(1) ?: 0

                            val newExercise = Exercise(
                                id = UUID.randomUUID().toString(),
                                exerciseName = "",
                                sets = 0,
                                reps = 0,
                                weight = 0f,
                                date = SimpleDateFormat(
                                    "dd/MM",
                                    Locale.ENGLISH
                                ).format(Date()),
                                category = selectedCategory,
                                position = nextPosition
                            )

                            onAction(
                                GymMateAction.AddExercise(newExercise)
                            )
                        }
                    }
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "gymmate",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(18.dp),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(modifier = Modifier.padding(18.dp)) {
                        items(state.categories) { category ->
                            Button(
                                onClick = { onAction(GymMateAction.SelectCategory(category.name)) },
                                enabled = state.activeSession == null,
                                modifier = Modifier.padding(1.dp),
                                colors = if (category.name == state.selectedCategory)
                                    ButtonDefaults.buttonColors()
                                else
                                    ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 4.dp)
                                ) {
                                    Text(text = category.name)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            categoryToRename = category.name
                                            showRenameDialog = true
                                        },
                                        enabled = state.activeSession == null,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            contentDescription = "Rename Category",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    if (state.categories.size > 1) {
                                        IconButton(
                                            onClick = {
                                                categoryToDelete = category.name
                                                showDeleteDialog = true
                                            },
                                            enabled = state.activeSession == null,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .padding(start = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                tint = MaterialTheme.colorScheme.error,
                                                contentDescription = "Delete Category",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            Button(
                                onClick = { showCategoryDialog = true },
                                enabled = state.activeSession == null,
                                modifier = Modifier.padding(1.dp),
                                colors = ButtonDefaults.buttonColors()
                            ) {
                                Text(text = "+")
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (state.activeSession == null) {
                                onAction(GymMateAction.StartWorkout)
                            } else {
                                onAction(GymMateAction.FinishWorkout)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (state.activeSession == null) {
                                "Start Workout"
                            } else {
                                "Finish Workout"
                            }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        state = lazyListState
                    ) {
                        items(
                            items = reorderedExercises,
                            key = { exercise -> exercise.id }
                        ) { exercise ->

                            ReorderableItem(
                                reorderableLazyListState,
                                key = exercise.id
                            ) { _ ->

                                val sessionExercise = state.sessionExercises
                                    .firstOrNull { it.exerciseId == exercise.id }


                                ExerciseCard(
                                    canEditStructure = state.activeSession == null,
                                    exercise = exercise,

                                    onUpdateExercise = { updatedExercise ->
                                        onAction(
                                            GymMateAction.UpdateExercise(updatedExercise)
                                        )
                                    },

                                    onDeleteExercise = { exerciseToDelete ->
                                        onAction(
                                            GymMateAction.DeleteExercise(exerciseToDelete)
                                        )
                                    },

                                    onOpenHistory = {
                                        onAction(
                                            GymMateAction.OpenExerciseHistory(exercise.id)
                                        )
                                    },

                                    dragHandleModifier = Modifier.draggableHandle(
                                        onDragStopped = {
                                            onAction(
                                                GymMateAction.ReorderExercises(
                                                    reorderedExercises
                                                )
                                            )
                                        }
                                    ),

                                    completedSetsFromSession = sessionExercise?.completedSets ?: 0,

                                    onCompletedSetsChange = { completedSets ->
                                        onAction(
                                            GymMateAction.UpdateCompletedSets(
                                                exerciseId = exercise.id,
                                                completedSets = completedSets
                                            )
                                        )
                                    }

                                )
                            }
                        }
                    }
                }
            }
        )

        if (showCategoryDialog) {
            AddCategoryDialog(
                onConfirm = { newCategoryName ->
                    if (newCategoryName.isNotBlank() && newCategoryName !in state.categories.map { it.name }) {
                        onAction(GymMateAction.AddCategory(newCategoryName))
                    }
                    showCategoryDialog = false
                },
                onDismiss = { showCategoryDialog = false }
            )
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                title = "Delete Category",
                message = "Are you sure you want to delete '$categoryToDelete'? This will also delete all associated exercises.",
                onConfirm = {
                    onAction(GymMateAction.DeleteCategory(categoryToDelete))
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        if (showRenameDialog) {
            RenameCategoryDialog(
                currentName = categoryToRename,
                onConfirm = { newName ->
                    if (newName.isNotBlank() && newName !in state.categories.map { it.name }) {
                        onAction(GymMateAction.RenameCategory(categoryToRename, newName))
                    }
                    showRenameDialog = false
                },
                onDismiss = { showRenameDialog = false }
            )
        }

        if (state.historyExerciseId != null) {

            val locale = LocalLocale.current.platformLocale

            var selectedPeriod by rememberSaveable(
                state.historyExerciseId
            ) {
                mutableIntStateOf(1)
            }

            var showCustomPeriodDialog by rememberSaveable {
                mutableStateOf(false)
            }

            var showDateRangePicker by rememberSaveable {
                mutableStateOf(false)
            }

            var customStartMillis by rememberSaveable {
                mutableLongStateOf(Long.MIN_VALUE)
            }

            var customEndExclusiveMillis by rememberSaveable {
                mutableLongStateOf(Long.MAX_VALUE)
            }

            var customPeriodLabel by rememberSaveable {
                mutableStateOf("All history")
            }

            val now = remember(state.historyExerciseId) {
                System.currentTimeMillis()
            }

            val filteredHistory = remember(
                state.exerciseHistory,
                selectedPeriod,
                customStartMillis,
                customEndExclusiveMillis
            ) {

                when (selectedPeriod) {

                    // 30 days
                    0 -> {

                        val cutoff = getDaysStartMillis(
                            nowMillis = now,
                            days = 30
                        )

                        state.exerciseHistory.filter {
                            it.performedAt >= cutoff
                        }
                    }

                    // 3 months
                    1 -> {

                        val cutoff =
                            getPeriodStartMillis(
                                nowMillis = now,
                                period = Period.ofMonths(3)
                            )

                        state.exerciseHistory.filter {
                            it.performedAt >= cutoff
                        }
                    }

                    // Custom
                    else -> {

                        state.exerciseHistory.filter {
                            it.performedAt >= customStartMillis &&
                                    it.performedAt < customEndExclusiveMillis
                        }
                    }
                }
            }

            AlertDialog(
                onDismissRequest = {
                    onAction(
                        GymMateAction.CloseExerciseHistory
                    )
                },

                title = {

                    Column {

                        Text(
                            text = state.exerciseHistory
                                .firstOrNull()
                                ?.exerciseName
                                ?: "Exercise History",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = "Performance history",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },

                text = {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {

                        /*
                         * PERSONAL RECORD
                         */

                        state.personalRecord?.let { record ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.EmojiEvents,
                                        contentDescription =
                                            "Personal Record",
                                        tint =
                                            MaterialTheme.colorScheme
                                                .onTertiaryContainer
                                    )

                                    Spacer(
                                        modifier = Modifier.width(12.dp)
                                    )

                                    Column {

                                        Text(
                                            text = "PERSONAL RECORD",
                                            style =
                                                MaterialTheme.typography.labelMedium,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onTertiaryContainer
                                        )

                                        Text(
                                            text = String.format(
                                                locale,
                                                "%.1f kg",
                                                record
                                            ),
                                            style =
                                                MaterialTheme.typography.headlineMedium,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onTertiaryContainer
                                        )

                                        Text(
                                            text = "Highest weight recorded",
                                            style =
                                                MaterialTheme.typography.bodySmall,
                                            color =
                                                MaterialTheme.colorScheme
                                                    .onTertiaryContainer
                                        )
                                    }
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(20.dp)
                            )
                        }


                        /*
                         * PERIOD FILTER
                         */

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            FilterChip(
                                selected = selectedPeriod == 0,
                                onClick = {
                                    selectedPeriod = 0
                                },
                                label = {
                                    Text("30 days")
                                }
                            )

                            FilterChip(
                                selected = selectedPeriod == 1,
                                onClick = {
                                    selectedPeriod = 1
                                },
                                label = {
                                    Text("3 months")
                                }
                            )

                            FilterChip(
                                selected = selectedPeriod == 2,
                                onClick = {
                                    showCustomPeriodDialog = true
                                },
                                label = {
                                    Text("Custom")
                                }
                            )
                        }

                        if (selectedPeriod == 2) {

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = customPeriodLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )


                        /*
                         * HISTORY HEADER
                         */

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = "HISTORY",
                                style = MaterialTheme.typography.labelMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text =
                                    "${filteredHistory.size} " +
                                            if (filteredHistory.size == 1) {
                                                "WORKOUT"
                                            } else {
                                                "WORKOUTS"
                                            },
                                style = MaterialTheme.typography.labelSmall,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        /*
                         * HISTORY LIST
                         */

                        if (filteredHistory.isEmpty()) {

                            Text(
                                text = "No workouts found in this period.",
                                modifier =
                                    Modifier.padding(vertical = 24.dp),
                                style =
                                    MaterialTheme.typography.bodyMedium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )

                        } else {

                            LazyColumn(
                                modifier = Modifier.heightIn(
                                    max = 320.dp
                                )
                            ) {

                                items(
                                    items = filteredHistory,
                                    key = {
                                        it.sessionId
                                    }
                                ) { history ->

                                    /*
                                     * Usa o histórico COMPLETO para saber
                                     * se naquele treino realmente houve PR.
                                     *
                                     * Assim o filtro não interfere
                                     * no cálculo.
                                     */

                                    val originalIndex =
                                        state.exerciseHistory
                                            .indexOfFirst {
                                                it.sessionId ==
                                                        history.sessionId
                                            }

                                    val previousRecord =
                                        if (originalIndex >= 0) {

                                            state.exerciseHistory
                                                .drop(originalIndex + 1)
                                                .maxOfOrNull {
                                                    it.weight
                                                }

                                        } else {
                                            null
                                        }

                                    val brokePersonalRecord =
                                        previousRecord != null &&
                                                history.weight >
                                                previousRecord


                                    val formattedDate =
                                        SimpleDateFormat(
                                            "dd MMM yyyy",
                                            locale
                                        )
                                            .format(
                                                Date(
                                                    history.performedAt
                                                )
                                            )
                                            .uppercase(locale)


                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = 12.dp
                                            )
                                    ) {

                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth(),
                                            horizontalArrangement =
                                                Arrangement.SpaceBetween,
                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = formattedDate,
                                                style =
                                                    MaterialTheme.typography
                                                        .labelMedium,
                                                color =
                                                    MaterialTheme.colorScheme
                                                        .onSurfaceVariant
                                            )

                                            Text(
                                                text = String.format(
                                                    locale,
                                                    "%.1f kg",
                                                    history.weight
                                                ),
                                                style =
                                                    MaterialTheme.typography
                                                        .titleMedium
                                            )
                                        }

                                        Spacer(
                                            modifier =
                                                Modifier.height(4.dp)
                                        )

                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth(),
                                            horizontalArrangement =
                                                Arrangement.SpaceBetween,
                                            verticalAlignment =
                                                Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text =
                                                    "${history.sets} sets × " +
                                                            "${history.reps} reps",
                                                style =
                                                    MaterialTheme.typography
                                                        .bodyMedium,
                                                color =
                                                    MaterialTheme.colorScheme
                                                        .onSurfaceVariant
                                            )

                                            if (brokePersonalRecord) {

                                                Row(
                                                    verticalAlignment =
                                                        Alignment.CenterVertically
                                                ) {

                                                    Icon(
                                                        imageVector =
                                                            Icons.Default
                                                                .EmojiEvents,
                                                        contentDescription =
                                                            "New Personal Record",
                                                        modifier =
                                                            Modifier.size(16.dp),
                                                        tint =
                                                            MaterialTheme
                                                                .colorScheme
                                                                .tertiary
                                                    )

                                                    Spacer(
                                                        modifier =
                                                            Modifier.width(4.dp)
                                                    )

                                                    Text(
                                                        text = "NEW PR",
                                                        style =
                                                            MaterialTheme.typography
                                                                .labelMedium,
                                                        color =
                                                            MaterialTheme
                                                                .colorScheme
                                                                .tertiary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    HorizontalDivider(
                                        color =
                                            MaterialTheme.colorScheme
                                                .outlineVariant
                                    )
                                }
                            }
                        }
                    }
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            onAction(
                                GymMateAction.CloseExerciseHistory
                            )
                        }
                    ) {
                        Text("Close")
                    }
                }
            )

            if (showCustomPeriodDialog) {

                fun selectQuickDays(
                    days: Long,
                    label: String
                ) {
                    customStartMillis =
                        getDaysStartMillis(
                            nowMillis = now,
                            days = days
                        )

                    customEndExclusiveMillis = now + 1

                    customPeriodLabel = label
                    selectedPeriod = 2
                    showCustomPeriodDialog = false
                }

                fun selectQuickPeriod(
                    period: Period,
                    label: String
                ) {
                    customStartMillis =
                        getPeriodStartMillis(
                            nowMillis = now,
                            period = period
                        )

                    customEndExclusiveMillis = now + 1

                    customPeriodLabel = label
                    selectedPeriod = 2
                    showCustomPeriodDialog = false
                }

                AlertDialog(
                    onDismissRequest = {
                        showCustomPeriodDialog = false
                    },

                    title = {
                        Column {
                            Text(
                                text = "Custom period",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "Choose a quick range or select specific dates.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },

                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = "QUICK RANGES",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {

                                AssistChip(
                                    onClick = {
                                        selectQuickDays(
                                            days = 7,
                                            "Last 7 days"
                                        )
                                    },
                                    label = {
                                        Text("7 days")
                                    }
                                )

                                AssistChip(
                                    onClick = {
                                        selectQuickDays(
                                            days = 15,
                                            "Last 15 days"
                                        )
                                    },
                                    label = {
                                        Text("15 days")
                                    }
                                )

                                AssistChip(
                                    onClick = {
                                        selectQuickPeriod(
                                            Period.ofMonths(1),
                                            "Last month"
                                        )
                                    },
                                    label = {
                                        Text("1 month")
                                    }
                                )

                                AssistChip(
                                    onClick = {
                                        selectQuickPeriod(
                                            Period.ofMonths(2),
                                            "Last 2 months"
                                        )
                                    },
                                    label = {
                                        Text("2 months")
                                    }
                                )

                                AssistChip(
                                    onClick = {
                                        selectQuickPeriod(
                                            Period.ofMonths(6),
                                            "Last 6 months"
                                        )
                                    },
                                    label = {
                                        Text("6 months")
                                    }
                                )

                                AssistChip(
                                    onClick = {
                                        selectQuickPeriod(
                                            Period.ofYears(1),
                                            "Last year"
                                        )
                                    },
                                    label = {
                                        Text("1 year")
                                    }
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(20.dp)
                            )

                            HorizontalDivider()

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            OutlinedButton(
                                onClick = {
                                    customStartMillis = Long.MIN_VALUE
                                    customEndExclusiveMillis = Long.MAX_VALUE

                                    customPeriodLabel = "All history"
                                    selectedPeriod = 2

                                    showCustomPeriodDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text("All history")
                            }

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Button(
                                onClick = {
                                    showCustomPeriodDialog = false
                                    showDateRangePicker = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text("Choose dates")
                            }
                        }
                    },

                    confirmButton = {},

                    dismissButton = {
                        TextButton(
                            onClick = {
                                showCustomPeriodDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showDateRangePicker) {

                val dateRangePickerState =
                    rememberDateRangePickerState()

                DatePickerDialog(
                    onDismissRequest = {

                        showDateRangePicker = false
                        showCustomPeriodDialog = true
                    },

                    confirmButton = {

                        val start =
                            dateRangePickerState
                                .selectedStartDateMillis

                        val end =
                            dateRangePickerState
                                .selectedEndDateMillis

                        TextButton(
                            enabled =
                                start != null &&
                                        end != null,

                            onClick = {

                                if (
                                    start != null &&
                                    end != null
                                ) {

                                    val localStart =
                                        pickerMillisToLocalStart(
                                            start
                                        )

                                    val localEnd =
                                        pickerMillisToLocalStart(
                                            end
                                        )

                                    customStartMillis =
                                        localStart

                                    customEndExclusiveMillis =
                                        pickerMillisToLocalEndExclusive(
                                            end
                                        )

                                    val formatter =
                                        SimpleDateFormat(
                                            "dd/MM/yyyy",
                                            locale
                                        )

                                    customPeriodLabel =
                                        "${formatter.format(Date(localStart))} - " +
                                                formatter.format(Date(localEnd))

                                    selectedPeriod = 2

                                    showDateRangePicker =
                                        false
                                }
                            }
                        ) {
                            Text("Apply")
                        }
                    },

                    dismissButton = {

                        TextButton(
                            onClick = {

                                showDateRangePicker = false
                                showCustomPeriodDialog = true
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {

                    DateRangePicker(
                        state = dateRangePickerState,
                        title = {
                            Text(
                                text = "Select date range",
                                modifier = Modifier.padding(
                                    start = 24.dp,
                                    top = 16.dp
                                )
                            )
                        },
                        showModeToggle = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .padding(16.dp)
                    )
                }
            }
        }

        if (state.exercises.isEmpty()) {
            CustomTooltip(
                text = "Add an exercise to start",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-68).dp, y = (-145).dp)
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onUpdateExercise: (Exercise) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    dragHandleModifier: Modifier = Modifier,
    completedSetsFromSession: Int = 0,
    onCompletedSetsChange: (Int) -> Unit = {},
    canEditStructure: Boolean = true,
    onOpenHistory: () -> Unit,
) {
    //var isExpanded by remember { mutableStateOf(false) }
    var isExpanded by rememberSaveable(exercise.id) {
        mutableStateOf(exercise.exerciseName.isBlank())
    }
    var showDialog by remember { mutableStateOf(false) }

    var completedSets by rememberSaveable(exercise.id) {
        mutableStateOf(completedSetsFromSession)
    }

    LaunchedEffect(completedSetsFromSession) {
        completedSets = completedSetsFromSession
    }

    var exerciseName by remember { mutableStateOf(exercise.exerciseName) }
    var exerciseSets by remember { mutableStateOf(if (exercise.sets == 0) "" else exercise.sets.toString()) }
    var exerciseReps by remember { mutableStateOf(if (exercise.reps == 0) "" else exercise.reps.toString()) }
    var exerciseWeight by remember { mutableStateOf(if (exercise.weight.toDouble() == 0.0) "" else exercise.weight.toString()) }
    var exerciseDate by remember { mutableStateOf(exercise.date) }

    LaunchedEffect(exercise) {
        exerciseName = exercise.exerciseName
        exerciseSets = if (exercise.sets == 0) "" else exercise.sets.toString()
        exerciseReps = if (exercise.reps == 0) "" else exercise.reps.toString()
        exerciseWeight = if (exercise.weight.toDouble() == 0.0) "" else exercise.weight.toString()
        exerciseDate = exercise.date
    }

    LaunchedEffect(exercise.sets) {
        completedSets = completedSets.coerceAtMost(exercise.sets)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
            ) {
                Text(
                    text = exercise.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(text = exercise.date)

                if (canEditStructure) {

                    IconButton(
                        onClick = onOpenHistory
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Exercise History"
                        )
                    }

                    IconButton(
                        modifier = dragHandleModifier,
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Reorder exercise"
                        )
                    }

                    IconButton(
                        onClick = { showDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp),
                            contentDescription = "Delete Exercise"
                        )
                    }
                }

                if (showDialog) {
                    ConfirmDeleteDialog(
                        onConfirm = {
                            onDeleteExercise(exercise)
                            showDialog = false
                        },
                        onDismiss = { showDialog = false }
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Show less" else "Show more"
                )
            }

            if (exercise.sets > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            val newValue = (completedSets - 1).coerceAtLeast(0)

                            completedSets = newValue
                            onCompletedSetsChange(newValue)
                        },
                        enabled = completedSets > 0
                    ) {
                        Text("-")
                    }

                    Text(
                        text = "$completedSets of ${exercise.sets} sets",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (completedSets == exercise.sets) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    TextButton(
                        onClick = {
                            val newValue =
                                (completedSets + 1).coerceAtMost(exercise.sets)

                            completedSets = newValue
                            onCompletedSetsChange(newValue)
                        },
                        enabled = completedSets < exercise.sets
                    ) {
                        Text("+")
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.padding(top = 16.dp))
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("New Exercise") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                TextField(
                    value = exerciseSets,
                    onValueChange = { exerciseSets = it },
                    label = { Text("Sets") },
                    placeholder = { Text("Sets Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = exerciseReps,
                    onValueChange = { exerciseReps = it },
                    label = { Text("Reps") },
                    placeholder = { Text("Reps Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = exerciseWeight,
                    onValueChange = { exerciseWeight = it },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("Weight") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(
                    value = exerciseDate,
                    onValueChange = { exerciseDate = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Button(
                    onClick = {
                        val updatedExercise: Exercise = exercise.copy(
                            exerciseName = exerciseName,
                            sets = exerciseSets.toIntOrNull() ?: 0,
                            reps = exerciseReps.toIntOrNull() ?: 0,
                            weight = exerciseWeight.toFloatOrNull() ?: 0f,
                            date = exerciseDate
                        )
                        onUpdateExercise(updatedExercise)
                        isExpanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun GymMateFAB(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = onFabClick,
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add exercise"
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    title: String = "Confirm Deletion",
    message: String = "Are you sure you want to delete this exercise?",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddCategoryDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Category") },
        text = {
            Column {
                Text("Enter the name of the new category:")
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameCategoryDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Category") },
        text = {
            Column {
                Text("Enter the new name for '$currentName':")
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("New Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank() && categoryName != currentName
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}