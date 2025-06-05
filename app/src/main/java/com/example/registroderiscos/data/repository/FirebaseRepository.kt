package com.example.registroderiscos.repository

import android.net.Uri
import com.example.registroderiscos.data.model.Risk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.UUID

class FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val risksCollection = firestore.collection("risks")

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("risk_images")

    suspend fun uploadImageToStorage(uri: Uri): String {
        try {
            val fileName = "risk_image_${UUID.randomUUID()}.png"
            val imageRef = storageRef.child(fileName)

            val uploadResult = imageRef.putFile(uri).await()

            if (uploadResult.task.isSuccessful) {
                val downloadUrl = imageRef.downloadUrl.await()
                return downloadUrl.toString()
            } else {
                throw Exception("Falha no upload da imagem: ${uploadResult.error?.message ?: "Erro desconhecido"}")
            }
        } catch (e: Exception) {
            throw Exception("Erro ao fazer upload para o Firebase Storage: ${e.message}", e)
        }
    }



    suspend fun addRisk(risk: Risk): Result<Unit> {
        return try {
            val riskMap = hashMapOf(
                "description" to risk.description,
                "address" to risk.address,
                "riskType" to risk.riskType,
                "imageUrl" to risk.imageUrl,
                "userId" to risk.userId,
                "date" to risk.date,
                "status" to risk.status
            )
            risksCollection.add(riskMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}