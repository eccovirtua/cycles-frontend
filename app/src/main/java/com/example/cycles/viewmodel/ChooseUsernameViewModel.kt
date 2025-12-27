package com.example.cycles.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserCreateRequest // Asegúrate de importar tu modelo correcto
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Importante para evitar callbacks
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import androidx.navigation.NavController

@HiltViewModel
class ChooseUsernameViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // --- Variables de Argumentos ---
    private var emailArg: String? = null
    private var passwordArg: String? = null
    private var ageArg: Int? = null

    // --- Estados UI ---
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    private val _currentGooglePhotoUrl = MutableStateFlow<String?>(null)
    val currentGooglePhotoUrl = _currentGooglePhotoUrl.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth = _dateOfBirth.asStateFlow()

    private val _showAgeInput = MutableStateFlow(false)
    val showAgeInput = _showAgeInput.asStateFlow()

    // Inicialización de datos
    fun setRegistrationData(email: String?, pass: String?, age: Int?) {
        this.emailArg = email
        this.passwordArg = pass
        this.ageArg = age

        // Determinar si pedimos fecha de nacimiento (Flujo Google o Edad no recibida)
        _showAgeInput.value = age == null || age == -1

        // Si es flujo Google (sin pass), intentamos cargar la foto de la cuenta
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
        _error.value = null
    }

    // Esta función reemplaza a onImageSelected
    fun onCropSuccess(uri: Uri?) {
        _selectedImageUri.value = uri
        // Si el usuario elige una foto propia, limpiamos la de Google visualmente para que no haya confusión
        if (uri != null) {
            _currentGooglePhotoUrl.value = null
        }
    }

    fun onImageRemoved() {
        _selectedImageUri.value = null
        // Si borra su selección, intentamos recuperar la de Google si existe
        if (passwordArg == null) {
            _currentGooglePhotoUrl.value = FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()
        }
    }

    fun checkUsernameAndRegister(navController: NavController) {
        val username = _name.value.trim()
        val imageUri = _selectedImageUri.value
        val isGoogleFlow = (passwordArg == null)

        // 1. Validaciones Iniciales
        if (username.length < 4) {
            _error.value = "El nombre de usuario debe tener al menos 4 caracteres"
            return
        }

        // Validación de Foto
        if (isGoogleFlow) {
            if (imageUri == null && _currentGooglePhotoUrl.value == null) {
                _error.value = "Por favor selecciona una imagen de perfil"
                return
            }
        } else {
            if (imageUri == null) {
                _error.value = "Es obligatorio elegir una foto de perfil"
                return
            }
        }

        // 2. Cálculo de Edad
        val finalAge: Int? = if (isGoogleFlow) {
            val dateString = _dateOfBirth.value
            if (dateString.isBlank()) {
                _error.value = "Por favor ingresa tu fecha de nacimiento"
                return
            }
            calculateAgeFromString(dateString)
        } else {
            ageArg
        }

        if (finalAge == null || finalAge < 18) {
            _error.value = "Debes ser mayor de 18 años para registrarte"
            return
        }

        // 3. Inicio del proceso asíncrono
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // A. Verificar disponibilidad de Username
                val isAvailable = repository.checkUsernameAvailability(username)
                if (!isAvailable) {
                    _error.value = "El nombre de usuario ya está ocupado"
                    _isLoading.value = false
                    return@launch
                }

                // B. Ejecutar Registro según el flujo
                if (isGoogleFlow) {
                    performGoogleRegistration(username, finalAge, imageUri, navController)
                } else {
                    performEmailRegistration(username, finalAge, imageUri!!, navController)
                }

            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private suspend fun performGoogleRegistration(
        username: String,
        age: Int,
        newImageUri: Uri?,
        navController: NavController
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: throw Exception("Usuario no autenticado")

        // 1. Determinar URL de la foto
        var finalPhotoUrl: String? = currentUser.photoUrl?.toString()

        // Si el usuario eligió una foto nueva, la subimos
        if (newImageUri != null) {
            val uploadedUrl = repository.uploadProfilePicture(currentUser.uid, newImageUri)
            if (uploadedUrl != null) {
                finalPhotoUrl = uploadedUrl
            }
        }

        // 2. Actualizar perfil en Auth (Nombre y Foto)
        repository.updateUserProfileAuth(username, finalPhotoUrl)

        // 3. Guardar en Backend
        finalizeBackendRegistration(username, currentUser.email ?: "", age, currentUser.uid, navController, finalPhotoUrl)
    }

    private suspend fun performEmailRegistration(
        username: String,
        age: Int,
        imageUri: Uri,
        navController: NavController
    ) {
        // 1. Crear usuario en Firebase Auth y ESPERAR
        // Asumimos que registerWithEmail retorna un Task
        val authResult = repository.registerWithEmail(emailArg!!, passwordArg!!).await()
        val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el UID")

        try {
            // 2. Subir Foto (Obligatoria en este flujo)
            val photoUrl = repository.uploadProfilePicture(uid, imageUri)

            // 3. Actualizar perfil Auth
            if (photoUrl != null) {
                repository.updateUserProfileAuth(username, photoUrl)
            }

            // 4. Guardar en Backend
            finalizeBackendRegistration(username, emailArg!!, age, uid, navController, photoUrl)

        } catch (e: Exception) {
            // Si algo falla después de crear el Auth, borramos el usuario para no dejar "zombies"
            FirebaseAuth.getInstance().currentUser?.delete()
            throw e
        }
    }

    private suspend fun finalizeBackendRegistration(
        username: String,
        email: String,
        age: Int,
        uid: String,
        nav: NavController,
        photoUrl: String?
    ) {
        // Aquí se soluciona el BUG anterior: Se pasa la photoUrl correcta
        val request = UserCreateRequest(
            firebaseUid = uid,
            email = email,
            username = username,
            age = age,
            profilePicture = photoUrl
        )

        val success = repository.createUserBackend(request)

        if (success) {
            _isLoading.value = false
            nav.navigate("home") { popUpTo("auth_graph") { inclusive = true } }
        } else {
            // Falló el backend
            if (passwordArg != null) { // Si era flujo email, revertimos
                FirebaseAuth.getInstance().currentUser?.delete()
            }
            _error.value = "Error al guardar perfil en servidor."
            _isLoading.value = false
        }
    }

    private fun calculateAgeFromString(dateString: String): Int? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateString, formatter)
            Period.between(birthDate, LocalDate.now()).years
        } catch (_: Exception) {
            null
        }
    }
}