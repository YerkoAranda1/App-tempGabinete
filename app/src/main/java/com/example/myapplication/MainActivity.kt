// IMPORTANTE: Mantén aquí arriba tu línea original "package com.example.monitoreoiot"

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

/**
 * 1. MODELO DE DATOS
 * Representa el paquete de información que entrega el sistema de sensores.
 */
data class DatosSensores(
    val puertaAbierta: Boolean,
    val temperaturaGrados: Double
)

/**
 * 2. MÓDULO PROVEEDOR DE DATOS (CAPA AISLADA)
 * En esta Fase 1, almacena y simula los valores en la memoria del teléfono.
 * En la Fase 2, solo modificaremos esta clase para hacer la petición HTTP/JSON
 * al Arduino Uno WiFi sin tener que cambiar la interfaz gráfica.
 */
class ProveedorDatosIoT {

    // Estado inicial simulado: Puerta cerrada (false) y 22.5 °C
    private var estadoPuertaAbierta: Boolean = false
    private var temperaturaActual: Double = 22.5

    // Función principal que consulta la interfaz para obtener la lectura actual
    fun obtenerLecturaActual(): DatosSensores {
        return DatosSensores(
            puertaAbierta = estadoPuertaAbierta,
            temperaturaGrados = temperaturaActual
        )
    }

    // --- Funciones exclusivas de simulación (Fase 1) ---

    fun simularCambioPuerta() {
        estadoPuertaAbierta = !estadoPuertaAbierta
    }

    fun simularVariacionTemperatura(delta: Double) {
        temperaturaActual += delta
    }

    fun simularTemperaturaAleatoria() {
        // Genera una temperatura realista entre 18.0 °C y 30.0 °C
        temperaturaActual = Random.nextDouble(18.0, 30.0)
    }
}

/**
 * 3. ACTIVIDAD PRINCIPAL (INTERFAZ DE USUARIO)
 * Se encarga únicamente de mostrar en pantalla lo que entrega ProveedorDatosIoT.
 */
class MainActivity : AppCompatActivity() {

    // Instancia de nuestro módulo de datos
    private val proveedorIoT = ProveedorDatosIoT()

    // Referencias a los textos de la pantalla
    private lateinit var tvEstadoPuerta: TextView
    private lateinit var tvTemperatura: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Vincular variables con los componentes visuales del XML
        tvEstadoPuerta = findViewById(R.id.tvEstadoPuerta)
        tvTemperatura = findViewById(R.id.tvTemperatura)

        val btnSimularPuerta: Button = findViewById(R.id.btnSimularPuerta)
        val btnBajarTemp: Button = findViewById(R.id.btnBajarTemp)
        val btnSubirTemp: Button = findViewById(R.id.btnSubirTemp)
        val btnSimularTempAleatoria: Button = findViewById(R.id.btnSimularTempAleatoria)

        // 2. Mostrar los valores iniciales al abrir la app
        actualizarPantalla()

        // 3. Configurar eventos de los botones de simulación
        btnSimularPuerta.setOnClickListener {
            proveedorIoT.simularCambioPuerta()
            actualizarPantalla()
        }

        btnBajarTemp.setOnClickListener {
            proveedorIoT.simularVariacionTemperatura(-0.5)
            actualizarPantalla()
        }

        btnSubirTemp.setOnClickListener {
            proveedorIoT.simularVariacionTemperatura(0.5)
            actualizarPantalla()
        }

        btnSimularTempAleatoria.setOnClickListener {
            proveedorIoT.simularTemperaturaAleatoria()
            actualizarPantalla()
        }
    }

    /**
     * Consulta los datos al módulo proveedor y refresca los colores y textos en pantalla.
     */
    private fun actualizarPantalla() {
        val datos = proveedorIoT.obtenerLecturaActual()

        // Actualizar visualmente el estado de la puerta
        if (datos.puertaAbierta) {
            tvEstadoPuerta.text = "Estado de Puerta: ABIERTA"
            tvEstadoPuerta.setTextColor(Color.parseColor("#C62828")) // Rojo alerta
        } else {
            tvEstadoPuerta.text = "Estado de Puerta: CERRADA"
            tvEstadoPuerta.setTextColor(Color.parseColor("#2E7D32")) // Verde seguro
        }

        // Formatear temperatura a 1 decimal (ej. 22.5 °C) y mostrar en pantalla
        val tempFormateada = String.format(Locale.US, "%.1f", datos.temperaturaGrados)
        tvTemperatura.text = "Temperatura Interior: $tempFormateada °C"
    }
}