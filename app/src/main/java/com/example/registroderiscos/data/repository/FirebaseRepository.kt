// data/repository/FirebaseRepository.kt
package com.example.registroderiscos.repository

import com.example.registroderiscos.data.model.Risk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val risksCollection = firestore.collection("risks")

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRisk(risk: Risk): Result<Unit> {
        return try {
            val riskMap = hashMapOf(
                "description" to risk.description,
                "address" to risk.address,
                "riskType" to risk.riskType
            )
            risksCollection.add(riskMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}