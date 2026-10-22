package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
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

        // 1. Inicializar Firebase Database
        val database = FirebaseDatabase.getInstance()
        val refPuerta = database.getReference("gabinete/puerta")
        val refTemperatura = database.getReference("gabinete/temperatura")

        // 2. Vincular los elementos visuales
        val tvEstadoPuerta = findViewById<TextView>(R.id.tvEstadoPuerta)
        val tvTemperatura = findViewById<TextView>(R.id.tvTemperatura)
        val btnSimularPuerta = findViewById<Button>(R.id.btnSimularPuerta)
        val btnBajarTemp = findViewById<Button>(R.id.btnBajarTemp)
        val btnSubirTemp = findViewById<Button>(R.id.btnSubirTemp)
        val btnSimularTempAleatoria = findViewById<Button>(R.id.btnSimularTempAleatoria)
        val btnLogout = findViewById<Button>(R.id.btnLogout) // Nuevo botón

        // Enviar el estado inicial a Firebase al abrir la app
        refPuerta.setValue("CERRADA")
        refTemperatura.setValue(temperaturaActual)

        // 3. Lógica para el botón de la Puerta
        btnSimularPuerta.setOnClickListener {
            isPuertaAbierta = !isPuertaAbierta
            val nuevoEstado = if (isPuertaAbierta) "ABIERTA" else "CERRADA"
            tvEstadoPuerta.text = "Estado de Puerta: $nuevoEstado"
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
            temperaturaActual = Math.round(Random.nextDouble(15.0, 35.0) * 10.0) / 10.0
            actualizarTemperatura(tvTemperatura, refTemperatura)
        }

        // 5. NUEVA LÓGICA: Botón de Cerrar Sesión
        btnLogout.setOnClickListener {
            // Le decimos a Firebase que cierre la sesión
            FirebaseAuth.getInstance().signOut()

            // Volvemos a la pantalla de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Destruimos la pantalla actual (el panel) para que no se pueda volver atrás con el botón del celular
            finish()
        }
    }

    // Función auxiliar para temperatura
    private fun actualizarTemperatura(tv: TextView, ref: DatabaseReference) {
        tv.text = "Temperatura Interior: $temperaturaActual °C"
        ref.setValue(temperaturaActual)
    }
}