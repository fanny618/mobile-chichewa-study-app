package com.example.mobilechichewastudyapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------- Data model (temporary — move to Models.kt later) ----------

data class QuizScore(
    val quizTitle: String,
    val score: Int,
    val total: Int,
    val timestamp: Long
)

// ---------- Sample data for the preview ----------

private val sampleScores = listOf(
    QuizScore("Biology Quiz", 4, 4, System.currentTimeMillis() - 1000 * 60 * 60),
    QuizScore("Chemistry Basics", 3, 5, System.currentTimeMillis() - 1000 * 60 * 60 * 26),
    QuizScore("Physics Quiz", 5, 6, System.currentTimeMillis() - 1000 * 60 * 60 * 72),
    QuizScore("Math Practice", 2, 5, System.currentTimeMillis() - 1000 * 60 * 60 * 120)
)

// ---------- Screen ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScoresScreen(
    scores: List<QuizScore> = emptyList(),
    onBackClick: () -> Unit = {},
    onStartQuiz: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Quiz Scores", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "${scores.size} attempt${if (scores.size == 1) "" else "s"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
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
        if (scores.isEmpty()) {
            EmptyState(
                modifier = Modifier.padding(padding),
                onStartQuiz = onStartQuiz
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary header
                item {
                    SummaryHeader(scores)
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Recent Attempts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Per-attempt rows
                items(scores, key = { it.timestamp }) { entry ->
                    ScoreRow(entry)
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(scores: List<QuizScore>) {
    val totalQuestions = scores.sumOf { it.total }
    val totalCorrect = scores.sumOf { it.score }
    val avgPercent = if (totalQuestions > 0) (totalCorrect * 100) / totalQuestions else 0
    val bestPercent = scores.maxOfOrNull {
        if (it.total > 0) (it.score * 100) / it.total else 0
    } ?: 0

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Overall",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$avgPercent%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                "average across ${scores.size} quiz${if (scores.size == 1) "" else "zes"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StatChip(
                    label = "Best",
                    value = "$bestPercent%",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatChip(
                    label = "Correct",
                    value = "$totalCorrect",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatChip(
                    label = "Questions",
                    value = "$totalQuestions",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun ScoreRow(entry: QuizScore) {
    val percent = if (entry.total > 0) (entry.score * 100) / entry.total else 0
    val (accentColor, label) = when {
        percent >= 80 -> SuccessGreen to "Excellent"
        percent >= 60 -> MaterialTheme.colorScheme.primary to "Good"
        percent >= 40 -> WarningAmber to "Fair"
        else -> MaterialTheme.colorScheme.error to "Needs work"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "$percent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    entry.quizTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${entry.score} of ${entry.total} correct",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    formatTimestamp(entry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onStartQuiz: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("No quiz scores yet", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Take a quiz and your results will show up here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onStartQuiz,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Start a Quiz")
            }
        }
    }
}

// ---------- Helpers ----------

private fun formatTimestamp(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    val minute = 60_000L
    val hour = 60 * minute
    val day = 24 * hour

    return when {
        diff < minute -> "Just now"
        diff < hour -> "${diff / minute} min ago"
        diff < day -> "${diff / hour} h ago"
        diff < 7 * day -> "${diff / day} d ago"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
    }
}

// ---------- Extra colors ----------

private val SuccessGreen = Color(0xFF2E7D32)
private val WarningAmber = Color(0xFFF9A825)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScoresPreview() = MobileChichewaStudyAppTheme {
    QuizScoresScreen(scores = sampleScores)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScoresEmptyPreview() = MobileChichewaStudyAppTheme {
    QuizScoresScreen(scores = emptyList())
}