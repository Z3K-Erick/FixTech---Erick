package com.fixtech.domain.model

data class Guide(
    val id: String,
    val deviceId: String,
    val title: String,
    val category: String = "Laptop",
    val difficulty: String,
    val estimatedTime: String,
    val toolsList: List<String>,
    val precautions: String,
    val technicianId: String,
    val technicianName: String,
    val modelSpec: String = "",
    val imageUrl: String = "",
    val steps: List<GuideStep> = emptyList(),
    val isFavorite: Boolean = false,
    val pinnedAt: Long = 0L
) {
    val authorId: String get() = technicianId
    val author: String get() = technicianName
    val description: String get() = precautions
    val tools: List<String> get() = toolsList
    val imageUri: String get() = imageUrl
}
