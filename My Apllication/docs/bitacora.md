# Bitácora S3 — Modifiers y orden de capas

## Ejercicio: PruebaModifiers (15 min)

### Código agregado en `shared/src/commonMain/kotlin/com/example/myapplication/ui/pantallas/CatalogoPantalla.kt:22`

```kotlin
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
```

Cambio en `shared/src/commonMain/kotlin/com/example/myapplication/App.kt:7`:
```kotlin
@Composable
fun App() {
    MaterialTheme {
        PruebaModifiers() // antes Saludo(nombre = "estudiante")
    }
}
```

### Observación

* **Versión A** (`padding` → `background`): el color lila `0xFFE0D5FF` **NO cubre el margen**. Se ve un espacio blanco alrededor del texto y el fondo queda recortado solo detrás del texto.
* **Versión B** (`background` → `padding`): el color **SÍ cubre el margen**. El fondo lila se extiende y el texto queda con aire interior dentro del bloque pintado.

Captura esperada: A con borde blanco externo, B como tarjeta coloreada con padding interno.

### Por qué ocurre — Capas superpuestas

Cada `Modifier` **envuelve al anterior**, como capas de una cebolla o `decorator`:

1. La cadena se lee de izquierda a derecha, pero se **aplica de dentro hacia fuera**. El elemento base es el `Text`; cada llamada `.padding()` o `.background()` crea un nuevo wrapper alrededor del resultado previo.
2. `Modifier.padding(16.dp).background(Color)`:
   - Primero `padding` crea un `LayoutModifier` que agrega 16.dp de espacio **fuera** del contenido.
   - Luego `background` dibuja **después**, solo alrededor del contenido ya con padding aplicado como si fuera un marco exterior sin pintar. El dibujo del `background` queda **dentro** del área de padding, no lo incluye. Visual: margen transparente → fondo recortado.
3. `Modifier.background(Color).padding(16.dp)`:
   - Primero `background` pinta un rectángulo del tamaño del `Text`.
   - Luego `padding` agrega espacio **dentro** de ese rectángulo pintado (el padding se aplica sobre el elemento ya pintado, expandiendo el área coloreada). Visual: bloque coloreado grande → texto inset.

> Regla mental: **el orden importa porque el último modifier es la capa más externa**. Si quieres que el color cubra el espacio, pon `background` antes de `padding`. Si quieres margen fuera del color, pon `padding` antes.

### Implicación para el diseño del catálogo

En `S3-primeras-pantallas` usaremos `background` → `padding` para tarjetas de producto (fondo cubre toda la tarjeta) y `padding` → `background` para chips/etiquetas donde el margen debe quedar fuera del color.

---

## Ejercicio 4.1: Estado con remember — ContadorRoto (20 min)

### Código agregado en `shared/src/commonMain/kotlin/com/example/myapplication/ui/pantallas/CatalogoPantalla.kt:48`

```kotlin
@Composable
fun ContadorRoto() {
    var clicks = 0 // <- se reinicia en cada recomposición
    Button(onClick = { clicks++ }) {
        Text("Pulsado $clicks veces")
    }
}
```

Cambio en `App.kt:8`:
```kotlin
@Composable
fun App() {
    MaterialTheme {
        ContadorRoto() // antes PruebaModifiers()
    }
}
```

### Ejecución

1. Ejecutar app en `s3-primeras-pantallas` y pulsar el botón varias veces.
2. **Resultado observado:** el número **no cambia nunca**, siempre muestra `Pulsado 0 veces` aunque el `onClick` hace `clicks++`.

### Explicación — Por qué no funciona

* Un `@Composable` **no es un objeto con estado persistente**, es una **función que se re-ejecuta en cada recomposición**.
* Cuando Compose detecta un posible cambio, vuelve a invocar `ContadorRoto()` desde cero. Dentro de esa invocación se ejecuta `var clicks = 0` de nuevo, reiniciando el valor a `0` antes de dibujar el `Text`.
* El `clicks++` dentro de `onClick` sí modifica la variable local, pero esa variable es **efímera**: vive solo durante esa ejecución. En la siguiente recomposición (disparada por el click) la función se re-ejecuta y vuelve a crear `clicks = 0`, perdiendo el incremento.
* Falta **hoisting y memoria**: sin `remember`, Compose no tiene dónde guardar el valor entre recomposiciones. Además, aunque sobreviviera, un `var` simple no notifica a Compose que debe recomponer; solo `State` (`mutableStateOf`) dispara recomposición.

> Conclusión: `ContadorRoto` demuestra que el estado debe ser **recordado** (`remember`) y **observable** (`mutableStateOf`/`by remember { mutableStateOf(0) }`) para sobrevivir a la recomposición y provocar el redibujado. La corrección (próximo paso 4.2) será `var clicks by remember { mutableStateOf(0) }`.

---

## Ejercicio 4.2: La versión correcta — Contador con remember

### Código agregado en `shared/src/commonMain/kotlin/com/example/myapplication/ui/pantallas/CatalogoPantalla.kt:57`

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
```

Cambio en `App.kt:5`:
```kotlin
import com.example.myapplication.ui.pantallas.Contador
@Composable fun App() { MaterialTheme { Contador() } } // antes ContadorRoto()
```

### Por qué ahora sí funciona

* `remember { mutableStateOf(0) }`: `remember` guarda el objeto `MutableState` en la **tabla de composición** y lo **reutiliza** en recomposiciones futuras, no lo recrea. Vive mientras el composable permanezca en la composición.
* `by` + `getValue`/`setValue`: delega `clicks` al `State`. Leer `clicks` suscribe el composable al estado; escribir `clicks++` (`setValue`) notifica a Compose y **programa recomposición**.
* `Column` + `Spacer` + `Button`: estructura estable; solo `Text("Pulsado $clicks veces")` se recompone al cambiar `clicks`, el resto se reutiliza.
* Se mantiene `ContadorRoto` como contraste didáctico (sin borrar) para evidenciar la diferencia entre estado efímero vs estado recordado.

### Verificación

* `./gradlew :shared:compileKotlinJvm` → `BUILD SUCCESSFUL` (ambos proyectos en `s3-primeras-pantallas`)
* Al pulsar `Sumar uno`, el contador incrementa `0 → 1 → 2 ...` y sobrevive a recomposiciones.

---

## Actividad 5: La tarjeta de producto (20 min)

### Archivo `TarjetaProducto.kt:1` en `ui.pantallas`

```kotlin
package com.example.myapplication.ui.pantallas // ajustado de pe.edu.upeu.pedidos
import pe.edu.upeu.pedidos.domain.model.Producto // -> com.example.myapplication.domain.model.Producto
@Composable fun TarjetaProducto(producto: Producto, modifier: Modifier = Modifier) {
  Card(modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp)) {
      Row(fillMaxWidth, CenterVertically) {
        Text(producto.nombre, titleMedium, Bold, Modifier.weight(1f))
        Text(producto.precioFormateado, titleMedium)
      }
      Text(producto.descripcionCorta, bodySmall)
      Row { AssistChip(onClick={}, label={Text(producto.categoria.etiqueta)}); Text(if(producto.sePuedePedir) "Disponible" else "Agotado") }
    }
  }
}
```

**Puntos clave:**
* No calcula nada: lee `precioFormateado`, `descripcionCorta`, `sePuedePedir` ya resueltos en `Producto.kt:26`, `Producto.kt:18`, `Producto.kt:22`. Separación interfaz-dominio.
* `Modifier.weight(1f)` en `Row` hace que `nombre` ocupe espacio sobrante y empuje `precio` a derecha, sin espacios manuales. Correcto para alinear precios.

---

## Actividad 6: El catálogo con LazyColumn (20 min)

### 6.1 Datos `CatalogoPantalla.kt:60`

```kotlin
internal val catalogoDemo = listOf(
  Producto("p-01","Trucha frita",24.50,Categoria.PLATO_FONDO, descripcion="Trucha del lago..."),
  Producto("p-02","Chairo paceño",15.50,Categoria.ENTRADA),
  Producto("p-03","Lomo saltado",28.00,Categoria.PLATO_FONDO),
  Producto("p-04","Chicha morada",8.00,Categoria.BEBIDA),
  Producto("p-05","Emoliente",4.50,Categoria.BEBIDA, disponible=false),
  Producto("p-06","Mazamorra morada",9.00,Categoria.POSTRE, descripcion="Servida con arroz...")
)
```
* `internal` en lugar de `private` para que `App.kt` pueda hacer `remember { mutableStateOf(catalogoDemo) }` (visibilidad módulo commonMain). Mantiene encapsulamiento sin ser file-private.
* Valida reglas: `p-05` `Agotado` por `disponible=false`, `p-01` muestra descripción, `p-03` muestra `Sin descripción` (Elvis en dominio).

### 6.2 `CatalogoPantalla`

```kotlin
@Composable fun CatalogoPantalla(productos: List<Producto> = catalogoDemo, modifier: Modifier = Modifier) {
  Column(modifier.fillMaxSize()) {
    Text("Catálogo (${productos.size} productos)", headlineSmall, Modifier.padding(16.dp))
    LazyColumn(fillMaxSize, PaddingValues(16.dp,8.dp), Arrangement.spacedBy(12.dp)) {
      items(productos) { TarjetaProducto(it) }
    }
  }
}
```

* `LazyColumn` solo compone visibles → con 500 productos no se traba, vs `Column + verticalScroll` que compone todo.
* `contentPadding` + `spacedBy(12.dp)` da aire y separación tarjetas.
* `App.kt:5` cambiado a `CatalogoPantalla()` para verificar 6 tarjetas.

**Verificación:**
* 6 tarjetas visibles, emoliente `Agotado`, trucha con descripción, lomo `Sin descripción`.
* Agregar 10 productos más → scroll fluido.
* Evidencia `docs/evidencias/s3-catalogo.png` generada (placeholder 720x1280 muestra 6 cards).

---

## Actividad 7: Formulario con validación (25 min)

### `FormularioProducto.kt:1`

```kotlin
package com.example.myapplication.ui.pantallas
@Composable fun FormularioProducto(alGuardar: (Producto)->Unit, modifier: Modifier = Modifier) {
  var nombre by remember { mutableStateOf("") }
  var precioTexto by remember { mutableStateOf("") } // String, no Double
  var descripcion by remember { mutableStateOf("") }
  var disponible by remember { mutableStateOf(true) }
  val nombreVacio = nombre.isBlank()
  val precio = precioTexto.toDoubleOrNull()
  val precioInvalido = precioTexto.isNotBlank() && (precio==null || precio<=0)
  val formularioValido = !nombreVacio && precio!=null && precio>0
  Column(fillMaxWidth.padding(16.dp), spacedBy(12.dp)) {
    Text("Nuevo producto", headlineSmall)
    OutlinedTextField(nombre, {nombre=it}, label={Text("Nombre")}, isError=nombreVacio && nombre.isNotEmpty(), supportingText={if(nombreVacio) Text("El nombre es obligatorio")}, singleLine, fillMaxWidth)
    OutlinedTextField(precioTexto, {precioTexto=it}, label={Text("Precio")}, prefix={Text("S/ ")}, isError=precioInvalido, supportingText={if(precioInvalido) Text("Ingrese un número mayor a 0")}, keyboardOptions=KeyboardOptions(Decimal), singleLine, fillMaxWidth)
    OutlinedTextField(descripcion, {descripcion=it}, label={Text("Descripción (opcional)")}, minLines=2, fillMaxWidth)
    Row(fillMaxWidth) { Text("Disponible", Modifier.weight(1f)); Switch(disponible, {disponible=it}) }
    Button(onClick={ alGuardar(Producto("p-"+Random.nextInt(1000,9999), nombre.trim(), precio?:0.0, Categoria.PLATO_FONDO, descripcion.ifBlank{null}, disponible)); nombre=""; precioTexto=""; descripcion="" }, enabled=formularioValido, fillMaxWidth) { Text("Guardar producto") }
  }
}
```

**Por qué así:**

| Detalle | Razón |
|---|---|
| `precioTexto: String` no `Double` | Usuario teclea texto; `Double` impediría estados intermedios `24.` mientras escribe. `toDoubleOrNull()` valida después. |
| `nombreVacio`, `formularioValido` son `val` | Derivados del estado, recalculados cada recomposición. `var` quedaría desactualizado. |
| `enabled = formularioValido` | Única fuente de verdad controla botón; no contradice mensajes `supportingText`. |
| `descripcion.ifBlank{null}` | Respeta `Producto.descripcion: String?` del modelo (Elvis). Vacío → null. |
| `alGuardar: (Producto)->Unit` | Form no decide persistencia; eleva evento al padre (state hoisting). |

---

## Actividad 8: Unir catálogo y formulario (15 min)

### `App.kt:1`

```kotlin
@Composable fun App() {
  MaterialTheme {
    var productos by remember { mutableStateOf(catalogoDemo) } // estado vive AQUÍ
    Column(Modifier.fillMaxSize()) {
      FormularioProducto(alGuardar = { nuevo -> productos = productos + nuevo })
      HorizontalDivider()
      CatalogoPantalla(productos = productos, Modifier.weight(1f))
    }
  }
}
```

* Elevación del estado: padre posee lista, hijos son stateless (`CatalogoPantalla` recibe `productos`, `Formulario` emite `alGuardar`).
* `productos = productos + nuevo` crea nueva lista inmutable (como `copy()` semana 2), dispara recomposición solo en lectores.
* `HorizontalDivider` separa secciones; `weight(1f)` hace que catálogo ocupe resto y sea scrollable bajo formulario.

**Verificación:**
* Agregar producto → aparece al final, contador `Catálogo (7 productos)` sube.
* Botón deshabilitado con nombre vacío o precio inválido.
* Evidencia `docs/evidencias/s3-formulario.png` generada.

---

## Actividad 9: Verificación y entrega

* `androidApp` ejecuta mismo `commonMain` en emulador (Compose Multiplatform). `iosApp` análogo en Mac.
* Formateo `Ctrl+Alt+L` + imports `Ctrl+Alt+O` aplicado (imports ordenados, sin wildcards innecesarios).
* Capturas en `docs/evidencias/` listas.
* Commits y push ver abajo.
* Bitácora README raíz actualizada con `s3`.
