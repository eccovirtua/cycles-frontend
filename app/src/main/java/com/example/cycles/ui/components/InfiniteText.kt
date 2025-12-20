package com.example.cycles.ui.components

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.cycles.R
import com.example.cycles.ui.theme.HelveticaFamily
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun ScrollingText(
    modifier: Modifier = Modifier,
    durationMillis: Int = 17000 // Aumentado un poco para que sea legible
) {
    val density = LocalDensity.current

    // Estado para guardar el ancho real del texto (en Píxeles para precisión)
    var textWidthPx by remember { mutableFloatStateOf(0f) }

    // Animación infinita de 0% a 100%
    val infiniteTransition = rememberInfiniteTransition(label = "scrolling_text")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset_anim"
    )

    // Contenedor que recorta lo que se salga de los bordes
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds(), // Importante: evita que el texto se pinte fuera del área
        contentAlignment = Alignment.CenterStart
    ) {
        // Lógica de Movimiento Izquierda -> Derecha (Infinito)
        // Dibujamos el texto DOS veces para cubrir el hueco

        val textWidthDp = with(density) { textWidthPx.toDp() }

        // Calculamos cuánto se ha movido hacia la izquierda (negativo)
        val moveDistance = -textWidthPx * progress

        // TEXTO 1: Se mueve hacia la izquierda (de 0 a -Ancho)
        val offset1 = with(density) { moveDistance.toDp() }

        // TEXTO 2: Viene desde la DERECHA (+Ancho) persiguiendo al texto 1
        val offset2 = with(density) { moveDistance.toDp() + textWidthDp }

        // Definimos el estilo una vez para reusarlo
        val commonStyle = TextStyle(
            fontFamily = HelveticaFamily,
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 15.sp
        )

        // Primer Texto
        Text(
            text = stringResource(R.string.home_slogan),
            style = commonStyle,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .offset(x = offset1)
                .wrapContentWidth(unbounded = true) // <--- CORRECCIÓN CLAVE
                .onSizeChanged {
                    // Actualizamos el ancho real
                    if (it.width.toFloat() != textWidthPx) {
                        textWidthPx = it.width.toFloat()
                    }
                }
        )

        // Segundo Texto (El "Fantasma" que rellena el bucle)
        // Solo lo mostramos si ya calculamos el ancho para evitar parpadeos iniciales
        if (textWidthPx > 0) {
            Text(
                text = stringResource(R.string.home_slogan),
                style = commonStyle,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .offset(x = offset2)
                    .wrapContentWidth(unbounded = true)
            )
        }
    }
}