package com.ejemplo.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { WalletScreen() } }
    }
}

@Composable
fun WalletScreen() {
    // --- Estado ---
    var destinatario by rememberSaveable { mutableStateOf("") }
    var montoTexto by rememberSaveable { mutableStateOf("") }            // monto a enviar (texto)
    var saldoTexto by rememberSaveable { mutableStateOf("2000.00") }     // saldo editable (texto)
    var mensaje by rememberSaveable { mutableStateOf("") }               // “Se envió…”
    var error by rememberSaveable { mutableStateOf<String?>(null) }      // mensaje de error

    // Helpers
    fun soles(v: Double) = "S/ " + String.format(Locale.US, "%,.2f", v)
    fun parseDoubleOrNull(s: String): Double? =
        s.trim().replace(",", ".").toDoubleOrNull()

    // Saldo numérico derivado del texto (si es inválido, tratamos como 0.0)
    val saldoActual = parseDoubleOrNull(saldoTexto) ?: 0.0

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Billetera", style = MaterialTheme.typography.headlineSmall)

            // --- Saldo editable ---
            OutlinedTextField(
                value = saldoTexto,
                onValueChange = { nuevo ->
                    // Permite: dígitos, opcional separador . o , y hasta 2 decimales
                    if (nuevo.matches(Regex("""\d*([.,]\d{0,2})?"""))) {
                        saldoTexto = nuevo
                    }
                },
                label = { Text("Monto actual (S/)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // --- Nombre destinatario ---
            OutlinedTextField(
                value = destinatario,
                onValueChange = { destinatario = it },
                label = { Text("Nombre destinatario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // --- Monto a enviar ---
            OutlinedTextField(
                value = montoTexto,
                onValueChange = { input ->
                    if (input.matches(Regex("""\d*([.,]\d{0,2})?"""))) {
                        montoTexto = input
                    }
                },
                label = { Text("Monto a enviar (S/)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = error != null,
                supportingText = { error?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Botón registrar ---
            Button(
                onClick = {
                    error = null

                    val monto = parseDoubleOrNull(montoTexto)
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
                        monto > saldoActual -> {
                            error = "Fondos insuficientes. Saldo: ${soles(saldoActual)}"
                        }
                        else -> {
                            // Actualiza saldo (y refleja en el campo de texto)
                            val nuevoSaldo = saldoActual - monto
                            saldoTexto = String.format(Locale.US, "%.2f", nuevoSaldo)

                            // Mensaje de confirmación
                            mensaje = "Se envió: $destinatario – ${soles(monto)}"

                            // Limpia monto a enviar (opcional, también podrías limpiar destinatario)
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

            // --- Mensaje inferior ---
            if (mensaje.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(mensaje, style = MaterialTheme.typography.bodyLarge)
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalletScreenPreview() {
    MaterialTheme { WalletScreen() }
}
