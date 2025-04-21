package de.Pruvex.growpal.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success(firebaseAuth.currentUser?.uid)
                } else {
                    _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Login fehlgeschlagen")
                }
            }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success(firebaseAuth.currentUser?.uid)
                } else {
                    _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Registrierung fehlgeschlagen")
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Idle
    }
}
