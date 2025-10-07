import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CyclesScreenStructure(
    paddingValues: PaddingValues, // Padding del Scaffold (sistema de barras)
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit // Contenido flexible (formulario, botones, etc.)
) {
    // 🎯 CLAVE: Usamos Box para simular la estructura de Login/Register
    Box(modifier = modifier.fillMaxSize()) {

        // 2. Columna Principal con Estructura
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 🎯 CLAVE 1: Aplicamos el padding del sistema (paddingValues) al contenido.
                .padding(paddingValues)
                // 🎯 CLAVE 2: Aplicamos los Márgenes Laterales Consistentes DENTRO del padding del sistema.
                .padding(horizontal = 1.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {


            // 3. Empuje Vertical
            Spacer(Modifier.height(40.dp))

            // 4. Título Principal
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            // 5. Subtítulo Descriptivo
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 6. Espacio antes del Formulario
            Spacer(Modifier.height(15.dp))

            // 7. Contenido Único de la Pantalla (El Formulario/Botones)
            content()
        }
    }
}