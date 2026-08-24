package com.example.myapplication.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Categoria
import com.example.myapplication.domain.model.Producto

@Composable
fun Saludo(nombre: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Hola, $nombre",
            style = MaterialTheme.typography.headlineSmall
        )
        Text("Bienvenido al catálogo")
    }
}

@Composable
fun PruebaModifiers() {
    Column(modifier = Modifier.padding(24.dp)) {
        // Versión A: primero padding, después background
        Text(
            text = "A: padding y luego background",
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFE0D5FF))
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Versión B: primero background, después padding
        Text(
            text = "B: background y luego padding",
            modifier = Modifier
                .background(Color(0xFFE0D5FF))
                .padding(16.dp)
        )
    }
}

@Composable
fun ContadorRoto() {
    var clicks = 0 // <- se reinicia en cada recomposición
    Button(onClick = { clicks++ }) {
        Text("Pulsado $clicks veces")
    }
}

@Composable
fun Contador() {
    var clicks by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Pulsado $clicks veces")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { clicks++ }) {
            Text("Sumar uno")
        }
    }
}

internal val catalogoDemo = listOf(
    Producto("p-01", "Trucha frita", 24.50, Categoria.PLATO_FONDO, descripcion = "Trucha del lago con papas doradas"),
    Producto("p-02", "Chairo paceño", 15.50, Categoria.ENTRADA),
    Producto("p-03", "Lomo saltado", 28.00, Categoria.PLATO_FONDO),
    Producto("p-04", "Chicha morada", 8.00, Categoria.BEBIDA),
    Producto("p-05", "Emoliente", 4.50, Categoria.BEBIDA, disponible = false),
    Producto("p-06", "Mazamorra morada", 9.00, Categoria.POSTRE, descripcion = "Servida con arroz con leche")
)

@Composable
fun CatalogoPantalla(
    productos: List<Producto> = catalogoDemo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Catálogo (${productos.size} productos)",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                TarjetaProducto(producto = producto)
            }
        }
    }
}
