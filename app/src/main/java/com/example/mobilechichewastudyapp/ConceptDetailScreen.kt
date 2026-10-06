package com.example.mobilechichewastudyapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptDetailScreen(
    concept: Concept,
    isSaved: Boolean = false,
    onBackClick: () -> Unit = {},
    onToggleSave: () -> Unit = {},
    onPracticeQuiz: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            concept.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1
                        )
                        // At the top of the scrolling Column, after the title
                        val isEmpty = concept.definition.isBlank() && concept.simpleEnglish.isBlank()
                        if (isEmpty) {
                            Text(
                                "Content for this concept is coming soon.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            concept.subject,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleSave) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isSaved) "Remove from saved" else "Save concept",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
            // Academic Definition
            if (concept.definition.isNotBlank()) {
                SectionCard(
                    icon = Icons.Default.MenuBook,
                    title = "Academic Definition",
                    accent = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        concept.definition,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Simplified English
            if (concept.simpleEnglish.isNotBlank()) {
                SectionCard(
                    icon = Icons.Default.Lightbulb,
                    title = "In Simple English",
                    accent = Sun
                ) {
                    Text(
                        concept.simpleEnglish,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Chichewa
            if (concept.chichewa.isNotBlank()) {
                SectionCard(
                    icon = Icons.Default.Translate,
                    title = "Mu Chichewa",
                    accent = Lake
                ) {
                    Text(
                        concept.chichewa,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Key Points
            if (concept.keyPoints.isNotEmpty()) {
                SectionCard(
                    icon = Icons.Default.Lightbulb,
                    title = "Key Points",
                    accent = MaterialTheme.colorScheme.tertiary
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        concept.keyPoints.forEach { point ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("•", style = MaterialTheme.typography.bodyLarge)
                                Spacer(Modifier.width(8.dp))
                                Text(point, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            // Practical Example
            if (concept.example.isNotBlank()) {
                SectionCard(
                    icon = Icons.Default.MenuBook,
                    title = "Practical Example",
                    accent = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        concept.example,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Practice CTA
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onPracticeQuiz,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Practice this concept")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Reusable colored section card used for each block of content.
 */
@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    accent: Color,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = accent.copy(alpha = 0.15f)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConceptDetailPreview() = MobileChichewaStudyAppTheme {
    ConceptDetailScreen(
        concept = sampleConcepts.first(),
        isSaved = true
    )
}