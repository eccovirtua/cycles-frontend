package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserCreateRequest
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@HiltViewModel
class ChooseUsernameViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private var emailArg: String? = null
    private var passwordArg: String? = null
    private var ageArg: Int? = null
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private val _isAvailable = MutableStateFlow<Boolean?>(null) // null=sin verificar, true=libre, false=ocupado
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth = _dateOfBirth.asStateFlow()

    private val _showAgeInput = MutableStateFlow(false)
    val showAgeInput = _showAgeInput.asStateFlow()


    fun setRegistrationData(email: String?, pass: String?, age: Int?) {
        this.emailArg = email
        this.passwordArg = pass
        this.ageArg = age

        _showAgeInput.value = age == null || age == -1

    }

    fun updateDateOfBirth(newDate: String) {
        _dateOfBirth.value = newDate
    }

    fun onNameChange(newName: String) {
        _name.value = newName
        _isAvailable.value = null
        _error.value = null
    }

    fun checkUsernameAndRegister(navController: NavController) {

        val username = _name.value.trim()

        val safeEmail = emailArg
        val safePass = passwordArg
        val safeAge = ageArg
        val isGoogleFlow = (safePass == null)

        if (username.length < 4) {
            _error.value = "El nombre de usuario es muy corto"
            return
        }
        var finalAge = safeAge
        if (isGoogleFlow) {
            val dateString = _dateOfBirth.value
            if (dateString.isBlank()) {
                _error.value = "Por favor ingresa tu fecha de nacimiento"
                return
            }
            finalAge = calculateAgeFromString(dateString) // Usamos tu función privada

            if (finalAge == null || finalAge < 18) {
                _error.value = "Debes ser mayor de 18 años"
                return
            }
        }
        if (!isGoogleFlow && (safeEmail == null || finalAge == null)) {
            _error.value = "Error de datos. Reinicia el registro."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // Verificar disponibilidad de Username en Backend
            val isUserFree = repository.checkUsernameAvailability(username)
            if (!isUserFree) {
                _error.value = "El nombre de usuario ya está ocupado"
                _isLoading.value = false
                return@launch
            }

            if (isGoogleFlow) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    finalizeBackendRegistration(username, currentUser.email!!, finalAge!!, currentUser.uid, navController)
                } else {
                    _error.value = "Error de sesión. Intenta loguearte de nuevo."
                    _isLoading.value = false
                }
            } else {
                // CAMINO EMAIL: Creamos usuario en Firebase primero
                repository.registerWithEmail(emailArg!!, passwordArg!!)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid
                        if (uid != null) {
                            finalizeBackendRegistration(username, emailArg!!, finalAge!!, uid, navController)
                        }
                    }
                    .addOnFailureListener {
                        _isLoading.value = false
                        _error.value = "Error: ${it.message}"
                    }
            }
        }
    }

    private fun finalizeBackendRegistration(username: String, email: String, age: Int, uid: String, nav: NavController) {
        viewModelScope.launch {
            val request = UserCreateRequest(username, age, email, uid)
            val success = repository.createUserBackend(request)

            if (success) {
                _isLoading.value = false
                nav.navigate("home") { popUpTo("auth_graph") { inclusive = true } }
            } else {
                // Borrar de Firebase si falla Mongo
                FirebaseAuth.getInstance().currentUser?.delete()
                _isLoading.value = false
                _error.value = "Error al guardar perfil. Intenta de nuevo."
            }
        }
    }
    private fun calculateAgeFromString(dateString: String): Int? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateString, formatter)
            Period.between(birthDate, LocalDate.now()).years
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}