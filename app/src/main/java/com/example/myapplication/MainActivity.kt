package com.ejemplo.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { WalletScreen() } }
    }
}

@Composable
fun WalletScreen() {
    // --- Estado (se conserva en recomposiciones y rotación) ---
    var destinatario by remember { mutableStateOf("") }
    var montoTexto by remember { mutableStateOf("") }
    var saldo by remember { mutableStateOf(2000.0) }        // saldo inicial
    var mensaje by remember { mutableStateOf("") }          // para decir a quien
    var error by remember { mutableStateOf<String?>(null) } // mensaje de error

    fun soles(v: Double) = "S/ " + String.format(Locale.US, "%,.2f", v)

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Billetera")
            // Monto actual
            OutlinedTextField(
                value = soles(saldo),
                onValueChange = {},
                label = { Text("Monto actual") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            // Nombre destinatario
            OutlinedTextField(
                value = destinatario,
                onValueChange = { destinatario = it },
                label = { Text("Nombre destinatario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Monto a enviar
            OutlinedTextField(
                value = montoTexto,
                onValueChange = { montoTexto = it },
                label = { Text("Monto a enviar S/") },
                singleLine = true,
                //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = error != null
            )
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            // btn registrar
            Button(
                onClick = {
                    error = null

                    val monto = montoTexto.replace(",", ".").toDoubleOrNull()
                    when {
                        destinatario.isBlank() -> {
                            error = "Ingresa el nombre del destinatario."
                        }
                        monto == null -> {
                            error = "Ingresa un monto válido (número)."
                        }
                        monto <= 0.0 -> {
                            error = "El monto debe ser mayor que 0."
                        }
                        monto > saldo -> {
                            error = "Fondos insuficientes. Saldo: ${soles(saldo)}"
                        }
                        else -> {
                            saldo -= monto
                            mensaje = "Se envió: $destinatario – ${soles(monto)}"
                            // opcional: limpiar campos
                            // destinatario = ""
                            montoTexto = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("REGISTRAR")
            }

            // Mensaje inferior
            if (mensaje.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(text = mensaje, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun WalletScreenPreview() {
    MaterialTheme { WalletScreen() }
}