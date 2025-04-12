package com.example.registroderiscos.data.model

data class Risk(
    val id: String = "",
    val description: String = "",
    val address: String? = null,
    val riskType: String? = null
)