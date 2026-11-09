package com.fixtech.domain.model

data class Certificate(
    val id: String,
    val technicianId: String = "admin-current",
    val title: String,
    val institution: String,
    val fileName: String
)
