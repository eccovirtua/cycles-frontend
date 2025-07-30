package com.example.cycles.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cycles.viewmodel.RecsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cycles.data.RecItem


@Composable
fun RecommendationsScreen(
    navController: NavHostController,
    itemId: Int,
    viewModel: RecsViewModel = hiltViewModel()
) {
    //estado de la respuesta
    val recsState by viewModel.recs.collectAsState()

    //lanzar la carga la primera vez
    LaunchedEffect(itemId) {
        viewModel.loadRecs(itemId)
    }


    //UI
    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            // Mientras carga (recsState == null), muestro un spinner
            recsState == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Si la lista está vacía, muestro mensaje
            recsState?.recommendations.isNullOrEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay recomendaciones disponibles.")
                }
            }

            //caso exitoso: muestra la lista
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recsState!!.recommendations) { rec ->
                        RecItemRow(rec)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecItemRow(rec: RecItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = rec.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Similitud: ${"%.2f".format(1f - rec.distance)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }



}
