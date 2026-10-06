package com.example.mobilechichewastudyapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme

@Composable
fun HomeScreen(
    onSubjectClick: (Subject) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onScoresClick: () -> Unit = {},
    onQuizClick: () -> Unit = {},
    onSimplifyClick: () -> Unit = {}
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Greeting
            item {
                Column {
                    Text("Muli bwanji?", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "What would you like to study today?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            // Search bar (display only)
            item {
                OutlinedCard(
                    onClick = onSearchClick,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text("Search for a concept", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f))
                    }
                }
            }

            // Simplify banner: the app's standout feature
            item {
                Card(
                    onClick = onSimplifyClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Lake)
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Stuck on your notes?", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Paste any hard text and get a simple explanation.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = onSimplifyClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Sun, contentColor = Ink)
                            ) { Text("Simplify text") }
                        }
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Default.AutoAwesome, null, tint = Sun, modifier = Modifier.size(48.dp))
                    }
                }
            }

            // Quick links
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickLink(Icons.Default.Quiz, "Quiz", Modifier.weight(1f), onQuizClick)
                    QuickLink(Icons.Default.Bookmark, "Saved", Modifier.weight(1f), onSavedClick)
                    QuickLink(Icons.Default.BarChart, "Scores", Modifier.weight(1f), onScoresClick)
                }
            }

            // Subjects
            item { Text("Subjects", style = MaterialTheme.typography.titleLarge) }
            item {
                // Fixed height grid inside a LazyColumn: fine for 6 sample items
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(((sampleSubjects.size + 1) / 2 * 128).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(sampleSubjects) { SubjectCard(it) { onSubjectClick(it) } }
                }
            }
        }
    }
}

@Composable
private fun QuickLink(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(vertical = 14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SubjectCard(subject: Subject, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.height(116.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(subject.emoji, style = MaterialTheme.typography.headlineLarge)
            Column {
                Text(subject.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${subject.conceptCount} concepts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() = MobileChichewaStudyAppTheme { HomeScreen() }