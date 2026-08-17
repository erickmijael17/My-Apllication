import com.example.myapplication.domain.model.Categoria
import com.example.myapplication.domain.model.Producto

fun main() {
    // FORMA 1 — Posicional: el orden debe coincidir con el constructor
    val trucha = Producto("p-01", "Trucha frita", 24.50, Categoria.PLATO_FONDO)

    // FORMA 2 — Con nombres: el orden no importa y se lee solo <- RECOMENDADA
    val chicha = Producto(
        nombre = "Chicha morada",
        id = "p-04",
        precio = 8.00,
        categoria = Categoria.BEBIDA
    )

    // FORMA 3 — Aprovechando todos los parámetros
    val chairo = Producto(
        id = "p-02",
        nombre = "Chairo paceño",
        precio = 15.50,
        categoria = Categoria.ENTRADA,
        descripcion = "Sopa tradicional con chuño, carne y verduras",
        disponible = true
    )

    // FORMA 4 — Un producto agotado, para probar las reglas
    val emoliente = Producto(
        id = "p-05",
        nombre = "Emoliente",
        precio = 4.50,
        categoria = Categoria.BEBIDA,
        disponible = false
    )

    // Una lista de instancias: así nace el catálogo simulado
    val catalogo = listOf(trucha, chicha, chairo, emoliente)

    println("=== CATALOGO (${catalogo.size} productos) ===")
    catalogo.forEach { producto ->
        println(producto)
    }

    println()
    println("=== REPORTE ===")
    catalogo.forEach { p ->
        val estado = if (p.sePuedePedir) "DISPONIBLE" else "AGOTADO"
        println("[$estado] ${p.nombre} - ${p.precioFormateado}")
        println("   ${p.descripcionCorta}")
    }

    println()
    println("Pedibles: ${catalogo.count { it.sePuedePedir }}")
    println("Suma de precios: ${catalogo.sumOf { it.precio }}")
    println("Mas caro: ${catalogo.maxByOrNull { it.precio }?.nombre}")
    println("Solo bebidas: ${catalogo.filter { it.categoria == Categoria.BEBIDA }.map { it.nombre }}")
    println("Por categoria: ${catalogo.groupBy { it.categoria }.mapValues { it.value.size }}")

    println()
    println("=== COPY E IGUALDAD ===")
    // copy(): crea un objeto NUEVO cambiando solo lo indicado
    val truchaEnOferta = trucha.copy(precio = 19.90)
    println("Original: ${trucha.precio}") // 24.5, intacto
    println("En oferta: ${truchaEnOferta.precio}") // 19.9

    // Igualdad por CONTENIDO, no por dirección de memoria
    val copiaExacta = trucha.copy()
    println("Son iguales: ${trucha == copiaExacta}") // true

    // Desestructuración: extraer varias propiedades a la vez
    val (identificador, nombreProd, precioProd) = trucha
    println("$identificador | $nombreProd | S/ $precioProd")
}