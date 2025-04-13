package com.example.registroderiscos.data.model

sealed class RiskType(val displayName: String) {
    object Biologico : RiskType("Biológico")
    object Quimico : RiskType("Químico")
    object Ergonomico : RiskType("Ergonômico")
    object Fisico : RiskType("Físico")
    object Mecanico : RiskType("Mecânico")

    companion object {
        fun getAllTypes(): List<RiskType> = listOf(Biologico, Quimico, Ergonomico, Fisico, Mecanico)

        fun fromDisplayName(displayName: String): RiskType? {
            return getAllTypes().find { it.displayName == displayName }
        }
    }
}