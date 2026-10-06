package com.example.mobilechichewastudyapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme

// ---------- Quiz data model (temporary, will move to Models.kt) ----------

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

val sampleQuiz: List<QuizQuestion> = listOf(
    QuizQuestion(
        question = "What gas do plants take in during photosynthesis?",
        options = listOf("Oxygen", "Carbon dioxide", "Nitrogen", "Hydrogen"),
        correctIndex = 1
    ),
    QuizQuestion(
        question = "Where in the plant cell does photosynthesis happen?",
        options = listOf("Nucleus", "Mitochondria", "Chloroplast", "Ribosome"),
        correctIndex = 2
    ),
    QuizQuestion(
        question = "What does photosynthesis produce?",
        options = listOf("Glucose and oxygen", "Water and salt", "Protein and fat", "Carbon and nitrogen"),
        correctIndex = 0
    ),
    QuizQuestion(
        question = "Which of these is NOT needed for photosynthesis?",
        options = listOf("Sunlight", "Water", "Carbon dioxide", "Soil"),
        correctIndex = 3
    )
)

// ---------- Screen ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    questions: List<QuizQuestion> = sampleQuiz,
    quizTitle: String = "Biology Quiz",
    onBackClick: () -> Unit = {},
    onFinish: (score: Int, total: Int) -> Unit = { _, _ -> }
) {
    // Quiz state
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showAnswer by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    val current = questions.getOrNull(currentIndex)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(quizTitle, style = MaterialTheme.typography.titleLarge)
                        if (!finished && current != null) {
                            Text(
                                "Question ${currentIndex + 1} of ${questions.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (finished) {
                ScoreSummary(
                    score = score,
                    total = questions.size,
                    onRetry = {
                        currentIndex = 0
                        selectedIndex = null
                        showAnswer = false
                        score = 0
                        finished = false
                    },
                    onDone = {
                        onFinish(score, questions.size)
                        onBackClick()
                    }
                )
            } else if (current != null) {

                // Progress bar
                LinearProgressIndicator(
                    progress = { (currentIndex + 1f) / questions.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(4.dp))

                // Question
                Text(
                    current.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                // Options
                current.options.forEachIndexed { index, option ->
                    val isSelected = selectedIndex == index
                    val isCorrect = index == current.correctIndex
                    val isWrongPick = showAnswer && isSelected && !isCorrect

                    val borderColor = when {
                        showAnswer && isCorrect -> SuccessGreen
                        isWrongPick -> MaterialTheme.colorScheme.error
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected || (showAnswer && isCorrect)) 2.dp else 1.dp,
                            color = borderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                enabled = !showAnswer,
                                onClick = { selectedIndex = index }
                            )
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (!showAnswer) selectedIndex = index },
                                enabled = !showAnswer
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            if (showAnswer && isCorrect) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = SuccessGreen
                                )
                            } else if (isWrongPick) {
                                Icon(
                                    Icons.Default.Cancel,
                                    contentDescription = "Wrong",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Action button
                if (!showAnswer) {
                    Button(
                        onClick = {
                            if (selectedIndex == current.correctIndex) score++
                            showAnswer = true
                        },
                        enabled = selectedIndex != null,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Check Answer")
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentIndex + 1 < questions.size) {
                                currentIndex++
                                selectedIndex = null
                                showAnswer = false
                            } else {
                                finished = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            if (currentIndex + 1 < questions.size) "Next Question"
                            else "See Results"
                        )
                    }
                }
            } else {
                // Safety: empty questions list
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No questions available.")
                }
            }
        }
    }
}

// ---------- Score summary ----------

@Composable
private fun ScoreSummary(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onDone: () -> Unit
) {
    val percent = if (total > 0) (score * 100) / total else 0
    val message = when {
        percent >= 80 -> "Excellent work! 🎉"
        percent >= 60 -> "Good job! Keep practising."
        percent >= 40 -> "Not bad — review and try again."
        else -> "Keep studying, you'll get there!"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(160.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "$score",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "out of $total",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )
            }
        }

        Text(
            "$percent%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Try Again")
        }

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Done")
        }
    }
}

// ---------- Extra color ----------

private val SuccessGreen = Color(0xFF2E7D32)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizPreview() = MobileChichewaStudyAppTheme {
    QuizScreen()
}