package de.Pruvex.growpal.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import de.Pruvex.growpal.data.FirestoreUserHelper
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import de.Pruvex.growpal.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

/**
 * Status der Authentifizierung für die UI.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel zur Verwaltung des Authentifizierungs-Status und der Authentifizierungsprozesse.
 */
class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    /**
     * Prüft, ob ein Nutzer eingeloggt und "Angemeldet bleiben" gesetzt ist.
     */
    fun checkAuthState(context: Context) {
        viewModelScope.launch {
            val stayLoggedIn = UserPreferences.stayLoggedInFlow(context).first()
            if (firebaseAuth.currentUser != null && stayLoggedIn) {
                _authState.value = AuthState.Success(firebaseAuth.currentUser?.uid)
            } else {
                _authState.value = AuthState.Idle
            }
        }
    }

    /**
     * Loggt einen Nutzer mit E-Mail und Passwort ein und setzt ggf. "Angemeldet bleiben".
     */
    fun login(email: String, password: String, stayLoggedIn: Boolean, context: Context) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch {
                        UserPreferences.setStayLoggedIn(context, stayLoggedIn)
                    }
                    _authState.value = AuthState.Success(firebaseAuth.currentUser?.uid)
                } else {
                    val errorMsg = task.exception?.localizedMessage ?: "Login fehlgeschlagen"
                    Log.e("AuthViewModel", "Login-Fehler: $errorMsg", task.exception)
                    _authState.value = AuthState.Error(errorMsg)
                }
            }
    }

    /**
     * Registriert einen neuen Nutzer und legt ein User-Dokument in Firestore an.
     */
    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        FirestoreUserHelper.createUserDocumentIfNotExists(user.uid, user.email)
                    }
                    _authState.value = AuthState.Success(user?.uid)
                } else {
                    val errorMsg = task.exception?.localizedMessage ?: "Registrierung fehlgeschlagen"
                    Log.e("AuthViewModel", "Registrierungs-Fehler: $errorMsg", task.exception)
                    _authState.value = AuthState.Error(errorMsg)
                }
            }
    }

    /**
     * Loggt den aktuellen Nutzer aus und entfernt das "Angemeldet bleiben"-Flag.
     */
    fun logout(context: Context) {
        firebaseAuth.signOut()
        viewModelScope.launch {
            UserPreferences.setStayLoggedIn(context, false)
            _authState.value = AuthState.Idle
        }
    }
}
