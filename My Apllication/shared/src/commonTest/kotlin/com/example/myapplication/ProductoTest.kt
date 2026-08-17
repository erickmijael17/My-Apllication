package com.example.myapplication

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.example.myapplication.domain.model.Categoria
import com.example.myapplication.domain.model.Producto

class ProductoTest {
    private fun productoDemo(
        precio: Double = 24.50,
        disponible: Boolean = true,
        descripcion: String? = null
    ) = Producto(
        id = "p-01",
        nombre = "Trucha frita",
        precio = precio,
        categoria = Categoria.PLATO_FONDO,
        descripcion = descripcion,
        disponible = disponible
    )

    @Test
    fun sinDescripcionUsaElTextoPorDefecto() {
        val producto = productoDemo()
        assertEquals("Sin descripción", producto.descripcionCorta)
    }

    @Test
    fun conDescripcionDevuelveLaDescripcionReal() {
        val producto = productoDemo(descripcion = "Con papas doradas")
        assertEquals("Con papas doradas", producto.descripcionCorta)
    }

    @Test
    fun unProductoAgotadoNoSePuedePedir() {
        assertFalse(productoDemo(disponible = false).sePuedePedir)
        assertTrue(productoDemo(disponible = true).sePuedePedir)
    }

    @Test
    fun copyGeneraUnObjetoNuevoSinTocarElOriginal() {
        val original = productoDemo()
        val enOferta = original.copy(precio = 19.90)
        assertEquals(24.50, original.precio)
        assertEquals(19.90, enOferta.precio)
        assertEquals(original.nombre, enOferta.nombre)
    }
}