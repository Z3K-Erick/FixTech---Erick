package com.fixtech.domain.model

data class Device(
    val id: String,
    val name: String,
    val model: String,
    val type: String, // "Laptop" o "PC"
    val technicianId: String = "admin-current",
    val technicianName: String = "Carlos M.",
    val imageUrl: String = "",
    val ram: String = "16 GB",
    val storage: String = "512 GB SSD",
    val processor: String = "Intel Core",
    val isBookmarked: Boolean = false,
    val guidesCount: Int = 0
) {
    val category: String get() = type
    val authorId: String get() = technicianId
    val authorName: String get() = technicianName
    val imageUri: String get() = imageUrl
}
