package com.example.mobilechichewastudyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.mobilechichewastudyapp.ui.theme.MobileChichewaStudyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileChichewaStudyAppTheme {
                StudyBuddyApp()
            }
        }
    }
}

/**
 * Top-level navigation host.
 *
 * Uses a simple "when" on a sealed [Screen] type instead of Navigation Compose
 * so the whole app fits in one file while we're prototyping.
 */
@Composable
fun StudyBuddyApp() {
    // ---- Navigation state ----
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    // ---- Shared app state (in-memory for now; Room DB later) ----
    val savedTitles = remember { mutableStateListOf<String>() }
    val recentSearches = remember { mutableStateListOf<String>() }
    val quizScores = remember { mutableStateListOf<QuizScore>() }

    // ---- Helper: toggle save state ----
    fun toggleSave(concept: Concept) {
        if (concept.title in savedTitles) savedTitles.remove(concept.title)
        else savedTitles.add(concept.title)
    }

    // ---- Helper: record a search term (max 5, most recent first) ----
    fun recordSearch(term: String) {
        if (term.isBlank()) return
        recentSearches.remove(term)
        recentSearches.add(0, term)
        while (recentSearches.size > 5) recentSearches.removeLast()
    }

    // ---- System back button ----
    BackHandler(enabled = currentScreen !is Screen.Home) {
        currentScreen = when (val s = currentScreen) {
            is Screen.ConceptList -> Screen.Home
            is Screen.ConceptDetail -> Screen.ConceptList(findSubject(s.concept.subject))
            is Screen.Simplify -> Screen.Home
            is Screen.Quiz -> Screen.Home
            is Screen.Saved -> Screen.Home
            is Screen.Search -> Screen.Home
            is Screen.QuizScores -> Screen.Home
            else -> Screen.Home
        }
    }

    // ---- Render current screen ----
    when (val screen = currentScreen) {

        // ---------- HOME ----------
        is Screen.Home -> HomeScreen(
            onSubjectClick = { subject -> currentScreen = Screen.ConceptList(subject) },
            onSearchClick = { currentScreen = Screen.Search },
            onSavedClick = { currentScreen = Screen.Saved },
            onScoresClick = { currentScreen = Screen.QuizScores },
            onQuizClick = { currentScreen = Screen.Quiz },
            onSimplifyClick = { currentScreen = Screen.Simplify }
        )

        // ---------- CONCEPT LIST ----------
        is Screen.ConceptList -> ConceptListScreen(
            subject = screen.subject,
            onConceptClick = { concept -> currentScreen = Screen.ConceptDetail(concept) },
            onBackClick = { currentScreen = Screen.Home },
            savedConceptTitles = savedTitles.toSet(),
            onToggleSave = { concept -> toggleSave(concept) }
        )

        // ---------- CONCEPT DETAIL ----------
        is Screen.ConceptDetail -> ConceptDetailScreen(
            concept = screen.concept,
            isSaved = screen.concept.title in savedTitles,
            onBackClick = {
                currentScreen = Screen.ConceptList(findSubject(screen.concept.subject))
            },
            onToggleSave = { toggleSave(screen.concept) },
            onPracticeQuiz = { currentScreen = Screen.Quiz }
        )

        // ---------- SIMPLIFY ----------
        is Screen.Simplify -> SimplifyScreen(
            onBackClick = { currentScreen = Screen.Home }
        )

        // ---------- QUIZ ----------
        is Screen.Quiz -> QuizScreen(
            onBackClick = { currentScreen = Screen.Home },
            onFinish = { score, total ->
                quizScores.add(
                    0,
                    QuizScore(
                        quizTitle = "Biology Quiz",
                        score = score,
                        total = total,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        )

        // ---------- SAVED CONCEPTS ----------
        is Screen.Saved -> SavedConceptsScreen(
            savedConcepts = sampleConcepts.filter { it.title in savedTitles },
            onConceptClick = { concept -> currentScreen = Screen.ConceptDetail(concept) },
            onRemove = { concept ->
                if (concept.title in savedTitles) savedTitles.remove(concept.title)
            },
            onBackClick = { currentScreen = Screen.Home },
            onBrowse = { currentScreen = Screen.Home }
        )

        // ---------- SEARCH ----------
        is Screen.Search -> SearchScreen(
            allConcepts = sampleConcepts,
            recentSearches = recentSearches.toList(),
            onConceptClick = { concept -> currentScreen = Screen.ConceptDetail(concept) },
            onBackClick = { currentScreen = Screen.Home },
            onSearchSubmitted = { term -> recordSearch(term) },
            onClearRecent = { recentSearches.clear() }
        )

        // ---------- QUIZ SCORES ----------
        is Screen.QuizScores -> QuizScoresScreen(
            scores = quizScores.toList(),
            onBackClick = { currentScreen = Screen.Home },
            onStartQuiz = { currentScreen = Screen.Quiz }
        )
    }
}

/**
 * Navigation destinations.
 */
sealed interface Screen {
    data object Home : Screen
    data class ConceptList(val subject: Subject) : Screen
    data class ConceptDetail(val concept: Concept) : Screen
    data object Simplify : Screen
    data object Quiz : Screen
    data object Saved : Screen
    data object Search : Screen
    data object QuizScores : Screen
}

/**
 * Finds a [Subject] by name so we can build a ConceptList destination
 * when navigating back from a concept detail.
 */
private fun findSubject(name: String): Subject =
    sampleSubjects.firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: sampleSubjects.first()