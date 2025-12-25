package com.example.cycles.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cycles.R
import com.example.cycles.ui.components.DateOfBirthPicker
import com.example.cycles.ui.theme.HelveticaFamily
import com.example.cycles.viewmodel.ChooseUsernameViewModel

@Composable
fun ChooseUsernameScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ChooseUsernameViewModel = hiltViewModel()
) {
    // --- ESTADOS ---
    val name by viewModel.name.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()
    val dob by viewModel.dateOfBirth.collectAsState()
    val showAgeInput by viewModel.showAgeInput.collectAsState()

    // Estados para la foto
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val googlePhotoUrl by viewModel.currentGooglePhotoUrl.collectAsState()

    val loaded = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // --- LÓGICA DE FOTO ---
    // 1. Calculamos qué mostrar (Prioridad: Nueva selección -> Foto Google -> Null)
    val imageModelToDisplay = selectedImageUri ?: googlePhotoUrl

    // 2. Configuramos el lanzador de la galería
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageSelected(uri) }
    )

    LaunchedEffect(Unit) {
        if (!loaded.value) {
            val previousBackStack = navController.previousBackStackEntry
            val savedHandle = previousBackStack?.savedStateHandle

            if (savedHandle != null) {
                val email = savedHandle.get<String>("email")
                val password = savedHandle.get<String>("password")
                val age = savedHandle.get<Int>("age")

                // Inyectamos datos al ViewModel
                viewModel.setRegistrationData(email, password, age)
                loaded.value = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 25.dp, vertical = 29.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(40.dp))

            // Título
            Text(
                text = stringResource(R.string.CU_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = HelveticaFamily
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            // Subtítulo
            Text(
                text = stringResource(R.string.cu_subtitle),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = HelveticaFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(35.dp))

            Text(
                text = "Foto de perfil",
                style = MaterialTheme.typography.labelLarge,
                fontFamily = HelveticaFamily,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(7.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                //  LA FOTO
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageModelToDisplay != null) {
                        AsyncImage(
                            model = imageModelToDisplay,
                            contentDescription = "Foto perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PermIdentity,
                            contentDescription = "Agregar foto",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))


                // LOS BOTONES
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Botón CAMBIAR FOTO
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(35),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Cambiar foto",
                            fontFamily = HelveticaFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Botón ELIMINAR FOTO
                    OutlinedButton(
                        onClick = { viewModel.onImageRemoved() },
                        shape = RoundedCornerShape(35),
                        enabled = imageModelToDisplay != null // Desactivado si ya está default
                    ) {
                        Text(
                            text = "Eliminar foto   ",
                            fontFamily = HelveticaFamily,
                            color = MaterialTheme.colorScheme.error // Rojo para indicar borrado
                        )
                    }
                }
            }

            // ---------------------------------------------------------

            Spacer(Modifier.height(30.dp))

            // Etiqueta del campo Usuario
            Text(
                text = stringResource(R.string.cu_topfield),
                style = MaterialTheme.typography.labelLarge,
                fontFamily = HelveticaFamily,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))

            // Campo de Texto Usuario
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                shape = RoundedCornerShape(35),
                placeholder = { Text(stringResource(R.string.cu_usernameplaceholder), style = MaterialTheme.typography.bodySmall, fontFamily = HelveticaFamily) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "Icono de usuario")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .height(53.dp)
                    .fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(Modifier.height(12.dp))

            // Input de Edad (Condicional)
            if (showAgeInput) {
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.cu_doptopfield), style = MaterialTheme.typography.labelLarge, fontFamily = HelveticaFamily, color = MaterialTheme.colorScheme.onBackground)

                DateOfBirthPicker(
                    selectedDate = dob,
                    onDateSelected = { newDateString ->
                        focusManager.clearFocus()
                        viewModel.updateDateOfBirth(newDateString)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Mensaje de Error
            if (!errorMsg.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Finalizar
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.checkUsernameAndRegister(navController)
                },
                shape = RoundedCornerShape(25),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && name.length >= 4
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(R.string.cu_finishbtn), fontFamily = HelveticaFamily)
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}