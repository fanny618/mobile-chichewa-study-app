package com.example.mobilechichewastudyapp

data class Subject(val name: String, val conceptCount: Int, val emoji: String)

data class Concept(
    val title: String,
    val subject: String,
    val definition: String,
    val simpleEnglish: String,
    val chichewa: String,
    val keyPoints: List<String>,
    val example: String
)

val sampleSubjects = listOf(
    Subject("Biology", 24, "🌱"),
    Subject("Chemistry", 18, "⚗️"),
    Subject("Physics", 21, "⚡"),
    Subject("Mathematics", 30, "➗"),
    Subject("Geography", 16, "🌍"),
    Subject("Computer Studies", 14, "💻")
)

val sampleConcepts = listOf(
    Concept(
        title = "Photosynthesis",
        subject = "Biology",
        definition = "The process by which green plants synthesise glucose from carbon dioxide and water using light energy absorbed by chlorophyll.",
        simpleEnglish = "Plants make their own food using sunlight, water and air.",
        chichewa = "Zomera zimapanga chakudya chawo pogwiritsa ntchito kuwala kwa dzuwa, madzi ndi mpweya.",
        keyPoints = listOf("Happens in the chloroplasts", "Needs light, water and carbon dioxide", "Produces glucose and oxygen"),
        example = "A maize plant in the field uses sunlight to grow tall and make grain."
    ),
    Concept("Osmosis", "Biology", "", "", "", emptyList(), ""),
    Concept("Cell Division", "Biology", "", "", "", emptyList(), ""),
    Concept("Respiration", "Biology", "", "", "", emptyList(), "")
)