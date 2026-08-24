package com.example.myapplication.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Categoria
import com.example.myapplication.domain.model.Producto

@Composable
fun FormularioProducto(
    alGuardar: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Un estado por campo ---
    var nombre by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var disponible by remember { mutableStateOf(true) }
    // --- Validaciones DERIVADAS del estado: son val, no var ---
    val nombreVacio = nombre.isBlank()
    val precio = precioTexto.toDoubleOrNull()
    val precioInvalido = precioTexto.isNotBlank() && (precio == null || precio <= 0)
    val formularioValido = !nombreVacio && precio != null && precio > 0

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nuevo producto", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            isError = nombreVacio && nombre.isNotEmpty(),
            supportingText = {
                if (nombreVacio) Text("El nombre es obligatorio")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precioTexto,
            onValueChange = { precioTexto = it },
            label = { Text("Precio") },
            prefix = { Text("S/ ") },
            isError = precioInvalido,
            supportingText = {
                if (precioInvalido) Text("Ingrese un número mayor a 0")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción (opcional)") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Disponible", modifier = Modifier.weight(1f))
            Switch(
                checked = disponible,
                onCheckedChange = { disponible = it }
            )
        }
        Button(
            onClick = {
                alGuardar(
                    Producto(
                        id = "p-" + kotlin.random.Random.nextInt(1000, 9999),
                        nombre = nombre.trim(),
                        precio = precio ?: 0.0,
                        categoria = Categoria.PLATO_FONDO,
                        descripcion = descripcion.ifBlank { null },
                        disponible = disponible
                    )
                )
                nombre = ""
                precioTexto = ""
                descripcion = ""
            },
            enabled = formularioValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar producto")
        }
    }
}
