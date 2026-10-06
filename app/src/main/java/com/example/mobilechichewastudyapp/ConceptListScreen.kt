package com.example.mobilechichewastudyapp



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConceptListScreen(
    subject: Subject,
    onConceptClick: (Concept) -> Unit = {},
    onBackClick: () -> Unit = {},
    savedConceptTitles: Set<String> = emptySet(),
    onToggleSave: (Concept) -> Unit = {}
) {
    // Local search state — filters concepts within this subject
    var query by remember { mutableStateOf("") }

    // Concepts belonging to this subject only
    val subjectConcepts = remember(subject.name) {
        sampleConcepts.filter { it.subject.equals(subject.name, ignoreCase = true) }
    }

    // Apply the local filter
    val filteredConcepts = remember(query, subjectConcepts) {
        if (query.isBlank()) subjectConcepts
        else subjectConcepts.filter {
            it.title.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(subject.name, style = MaterialTheme.typography.titleLarge)
                        Text(
                            "${subjectConcepts.size} concepts",
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search field
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                placeholder = { Text("Search ${subject.name} concepts") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Empty state
            if (filteredConcepts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(subject.emoji, style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (query.isBlank()) "No concepts yet in ${subject.name}."
                            else "No concepts match \"$query\".",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredConcepts, key = { it.title }) { concept ->
                        ConceptRow(
                            concept = concept,
                            isSaved = concept.title in savedConceptTitles,
                            onClick = { onConceptClick(concept) },
                            onToggleSave = { onToggleSave(concept) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConceptRow(
    concept: Concept,
    isSaved: Boolean,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    concept.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    // Show the simple English preview; fall back if empty
                    concept.simpleEnglish.ifBlank { "Tap to view explanation" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onToggleSave) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save concept",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConceptListPreview() = MobileChichewaStudyAppTheme {
    ConceptListScreen(subject = sampleSubjects.first())
}