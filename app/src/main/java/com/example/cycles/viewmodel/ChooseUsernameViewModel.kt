package com.example.cycles.viewmodel

import android.net.Uri
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

    // Estado para la imagen que el usuario selecciona de la GALERÍA
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    // Estado para mostrar la foto de Google pre-cargada (si existe)
    private val _currentGooglePhotoUrl = MutableStateFlow<String?>(null)
    val currentGooglePhotoUrl = _currentGooglePhotoUrl.asStateFlow()
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

        // si no viene una pass es flujo google,
        // por ende cargar la foto de perfil que tiene el usuario en su cuenta google
        if (pass == null) {
            val user = FirebaseAuth.getInstance().currentUser
            _currentGooglePhotoUrl.value = user?.photoUrl?.toString()
        }
    }

    fun updateDateOfBirth(newDate: String) {
        _dateOfBirth.value = newDate
    }

    fun onNameChange(newName: String) {
        _name.value = newName
        _isAvailable.value = null
        _error.value = null
    }
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun checkUsernameAndRegister(navController: NavController) {
        val username = _name.value.trim()
        val safeEmail = emailArg
        val safePass = passwordArg
        val safeAge = ageArg
        val isGoogleFlow = (safePass == null)

        val newImageUri = _selectedImageUri.value

        if (isGoogleFlow) {
            // Caso raro: Cuenta Google sin foto y no seleccionó una
            if (newImageUri == null && _currentGooglePhotoUrl.value == null) {
                _error.value = "Por favor selecciona una imagen de perfil"
                return
            }
        } else {
            if (newImageUri == null) {
                _error.value = "Es obligatorio elegir una foto de perfil"
                return
            }
        }
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

            var finalPhotoUrl: String? = null

            if (newImageUri != null) {
                if (isGoogleFlow) {
                    val currentUser = FirebaseAuth.getInstance().currentUser!!
                    val url = repository.uploadProfilePicture(currentUser.uid, newImageUri)
                    if (url != null) {
                        finalPhotoUrl = url
                        // Actualizamos Firebase Auth
                        repository.updateUserProfileAuth(username, url)
                    }
                    // Finalizar
                    finalizeBackendRegistration(username, currentUser.email!!, finalAge!!, currentUser.uid, navController, finalPhotoUrl)
                }
                else {
                    // Flujo Email con foto seleccionada (Obligatorio)
                    repository.registerWithEmail(emailArg!!, passwordArg!!)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid
                            if (uid != null) {
                                viewModelScope.launch {
                                    val url = repository.uploadProfilePicture(uid, newImageUri)
                                    if (url != null) {
                                        finalPhotoUrl = url
                                        repository.updateUserProfileAuth(username, url)
                                    }
                                    finalizeBackendRegistration(username, emailArg!!, finalAge!!, uid, navController, finalPhotoUrl)
                                }
                            }
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                            _error.value = "Error registro: ${it.message}"
                        }
                }
            }
            // 2. Si NO eligió foto nueva (Solo válido para Google)
            else if (isGoogleFlow) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    // Usamos la URL que ya tenía Google
                    finalPhotoUrl = currentUser.photoUrl?.toString()

                    // Solo actualizamos el nombre en Auth (la foto ya es la correcta)
                    repository.updateUserProfileAuth(username, finalPhotoUrl)

                    finalizeBackendRegistration(username, currentUser.email!!, finalAge!!, currentUser.uid, navController, finalPhotoUrl)
                }
            }
        }
    }

    private fun finalizeBackendRegistration(
        username: String,
        email: String,
        age: Int,
        uid: String,
        nav: NavController,
        photoUrl: String?
    ) {
        viewModelScope.launch {
            // Ahora pasamos photoUrl al request
            val request = UserCreateRequest(username, age, email, uid, photoUrl)
            val success = repository.createUserBackend(request)

            if (success) {
                _isLoading.value = false
                nav.navigate("home") { popUpTo("auth_graph") { inclusive = true } }
            } else {
                val isGoogleFlow = (passwordArg == null)
                if (!isGoogleFlow) {
                    FirebaseAuth.getInstance().currentUser?.delete()
                }
                _isLoading.value = false
                _error.value = "Error al guardar perfil. Intenta nuevamente."
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