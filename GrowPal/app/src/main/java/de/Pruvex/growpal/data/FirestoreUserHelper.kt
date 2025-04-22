package de.Pruvex.growpal.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

/**
 * Hilfsklasse für Firestore-Operationen rund um User-Dokumente.
 * Wird bei Registrierung genutzt, um User-Daten in Firestore zu speichern.
 */
object FirestoreUserHelper {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Legt ein User-Dokument in Firestore an, falls es noch nicht existiert.
     * Erweiterbar für Profilfelder.
     */
    fun createUserDocumentIfNotExists(uid: String, email: String?) {
        val userRef = db.collection("users").document(uid)
        userRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                val userData = hashMapOf(
                    "email" to (email ?: ""),
                    "createdAt" to Date()
                    // Weitere Felder wie "displayName", "profileImageUrl" etc. können hier ergänzt werden
                )
                userRef.set(userData)
            }
        }
    }
}
