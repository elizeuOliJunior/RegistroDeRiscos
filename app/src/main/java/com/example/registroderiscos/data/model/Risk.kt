package com.example.registroderiscos.data.model

data class Risk(
    val id: String = "",
    val description: String = "",
    val address: String? = null,
    val riskType: String? = null,
    val imageUrl: String? = null,
    val userId: String? = "",
    val date: String? = null,
    val Status: String? = null
)