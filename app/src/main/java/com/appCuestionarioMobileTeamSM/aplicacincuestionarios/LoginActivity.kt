package com.appCuestionarioMobileTeamSM.aplicacincuestionarios

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appCuestionarioMobileTeamSM.R
import java.util.HashMap
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*



class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = FirebaseFirestore.getInstance()

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            createRegistro(db)
        }

    }

    private fun createRegistro(db: FirebaseFirestore) {
        val user = usernameEditText.text.toString()
        val pass = passwordEditText.text.toString()

        val registro = HashMap<String, Any>()
        registro.put("Nombre", user)
        registro.put("Grado", pass)

        // Add a new document with a generated ID
        db.collection("registros")
            .add(registro)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Registro creado con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Opcional: cierra la actividad actual si no quieres que el usuario vuelva a ella
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "Error al crear el registro", Toast.LENGTH_SHORT).show()
            }
    }
}
