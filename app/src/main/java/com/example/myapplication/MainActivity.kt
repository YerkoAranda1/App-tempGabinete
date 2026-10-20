package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Variables locales para simular los sensores
    private var isPuertaAbierta = false
    private var temperaturaActual = 22.5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inicializar Firebase y crear dos rutas en la base de datos
        val database = FirebaseDatabase.getInstance()
        val refPuerta = database.getReference("gabinete/puerta")
        val refTemperatura = database.getReference("gabinete/temperatura")

        // 2. Vincular los elementos visuales de tu XML con Kotlin usando los IDs reales
        val tvEstadoPuerta = findViewById<TextView>(R.id.tvEstadoPuerta)
        val tvTemperatura = findViewById<TextView>(R.id.tvTemperatura)
        val btnSimularPuerta = findViewById<Button>(R.id.btnSimularPuerta)
        val btnBajarTemp = findViewById<Button>(R.id.btnBajarTemp)
        val btnSubirTemp = findViewById<Button>(R.id.btnSubirTemp)
        val btnSimularTempAleatoria = findViewById<Button>(R.id.btnSimularTempAleatoria)

        // Enviar el estado inicial a Firebase al abrir la app
        refPuerta.setValue("CERRADA")
        refTemperatura.setValue(temperaturaActual)

        // 3. Lógica para el botón de la Puerta
        btnSimularPuerta.setOnClickListener {
            isPuertaAbierta = !isPuertaAbierta
            val nuevoEstado = if (isPuertaAbierta) "ABIERTA" else "CERRADA"

            // Actualiza el texto en la pantalla del celular
            tvEstadoPuerta.text = "Estado de Puerta: $nuevoEstado"

            // Envía el nuevo estado a la base de datos
            refPuerta.setValue(nuevoEstado)
        }

        // 4. Lógica para los botones de Temperatura
        btnBajarTemp.setOnClickListener {
            temperaturaActual -= 0.5
            actualizarTemperatura(tvTemperatura, refTemperatura)
        }

        btnSubirTemp.setOnClickListener {
            temperaturaActual += 0.5
            actualizarTemperatura(tvTemperatura, refTemperatura)
        }

        btnSimularTempAleatoria.setOnClickListener {
            // Genera un número aleatorio entre 15.0 y 35.0 y lo redondea a 1 decimal
            temperaturaActual = Math.round(Random.nextDouble(15.0, 35.0) * 10.0) / 10.0
            actualizarTemperatura(tvTemperatura, refTemperatura)
        }
    }

    // Función auxiliar para no repetir código de temperatura
    private fun actualizarTemperatura(tv: TextView, ref: DatabaseReference) {
        tv.text = "Temperatura Interior: $temperaturaActual °C"
        ref.setValue(temperaturaActual)
    }
}