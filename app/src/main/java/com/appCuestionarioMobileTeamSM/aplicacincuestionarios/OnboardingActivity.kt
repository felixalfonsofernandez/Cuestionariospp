package com.appCuestionarioMobileTeamSM.aplicacincuestionarios

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.appCuestionarioMobileTeamSM.R


class OnboardingActivity : AppCompatActivity() {

    private lateinit var academic_grade_spinner: Spinner
    private lateinit var difficulty_level_spinner: Spinner
    private lateinit var interest_course_spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Inicializa las variables de Spinner con las vistas correspondientes
        academic_grade_spinner = findViewById(R.id.academic_grade_spinner)
        difficulty_level_spinner = findViewById(R.id.difficulty_level_spinner)
        interest_course_spinner = findViewById(R.id.interest_course_spinner)

        val submitButton: Button = findViewById(R.id.onboarding_submit_button)
        submitButton.setOnClickListener {
            submitForm()
        }
    }

    private fun saveOnboardingCompleted() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()
    }

    private fun savePreferences(grade: String, difficulty: String, course: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putString("academic_grade", grade)
        editor.putString("difficulty_level", difficulty)
        editor.putString("interest_course", course)
        editor.apply()
    }

    private fun submitForm() {
        val grade = academic_grade_spinner.selectedItem.toString()
        val difficulty = difficulty_level_spinner.selectedItem.toString()
        val course = interest_course_spinner.selectedItem.toString()
        savePreferences(grade, difficulty, course)
        saveOnboardingCompleted()  // Llama a saveOnboardingCompleted para marcar el onboarding como completado
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
