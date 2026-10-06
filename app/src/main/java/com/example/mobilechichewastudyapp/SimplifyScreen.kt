package com.example.mobilechichewastudyapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplifyScreen(
    onBackClick: () -> Unit = {}
) {
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Simplify Text") },
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
            // Intro
            Text(
                "Paste any hard text from your notes and get a simple explanation.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )

            // Input field
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                placeholder = { Text("Paste or type your study text here…") },
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
            )

            // Paste + Clear row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        val clip = clipboard.getText()?.text.orEmpty()
                        if (clip.isNotBlank()) input = clip
                    },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ContentPaste, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Paste")
                }
                OutlinedButton(
                    onClick = { input = ""; result = "" },
                    enabled = !isLoading && input.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear")
                }
            }

            // Simplify button (fake loading)
            Button(
                onClick = {
                    if (input.isBlank()) return@Button
                    isLoading = true
                    result = ""
                    scope.launch {
                        delay(1200)  // fake "thinking"
                        result = FAKE_SIMPLIFIED_RESULT
                        isLoading = false
                    }
                },
                enabled = input.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Simplifying…")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Simplify")
                }
            }

            // Result card
            if (result.isNotBlank()) {
                ResultCard(result)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultCard(text: String) {
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
                    color = Sun.copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Ink,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Simplified",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// Placeholder text shown after the fake delay
private const val FAKE_SIMPLIFIED_RESULT =
    "Plants make their own food using sunlight, water and air. " +
            "This happens in the leaves. The plant takes in sunlight and turns it into energy. " +
            "It then uses that energy to make sugar for itself and gives out oxygen.\n\n" +
            "CHICHEWA: Zomera zimapanga chakudya chawo pogwiritsa ntchito kuwala kwa dzuwa, " +
            "madzi ndi mpweya. Izi zimachitika m'masamba."

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SimplifyPreview() = MobileChichewaStudyAppTheme {
    SimplifyScreen()
}