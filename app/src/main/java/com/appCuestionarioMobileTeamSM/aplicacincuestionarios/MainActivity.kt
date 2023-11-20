package com.appCuestionarioMobileTeamSM.aplicacincuestionarios

import android.content.SharedPreferences
import android.os.Bundle
import org.tensorflow.lite.Interpreter
import android.app.Activity
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.preference.PreferenceManager
import androidx.core.view.children
import com.appCuestionarioMobileTeamSM.R
import com.google.android.gms.common.util.CollectionUtils.listOf
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var answerRadioGroup: RadioGroup
    private lateinit var submitButton: Button
    private lateinit var resetButton: Button
    private lateinit var tflite: Interpreter
    //private lateinit var loginButton: Button
    //private lateinit var classifyButton: Button
    private var numCorrectAnswers = 0
    private var numIncorrectAnswers = 0
    private var totalTimespent = 0L
    private var numQuestionsAnswered = 0
    private var startTime: Long = 0

    private val questions = listOf(
        //Question("What is 5 + 3?", listOf("6", "7", "8", "9"), 2),
        //Question("Simplify: 12x + 7 - 5x + 3", listOf("7x + 10", "7x + 4", "5x + 10", "5x + 4"), 0),
        //Question("Factorize: x^2 - 5x + 6", listOf("x(x - 6)", "(x - 2)(x - 3)", "(x - 1)(x - 6)", "(x - 1)(x - 5)"), 1),
        //Question("Solve for x: 2x + 3 = 7", listOf("x = 2", "x = 3", "x = 4", "x = 5"), 0),
        //Question("What is the derivative of 3x^2?", listOf("6x", "6x^2", "6", "3x"), 0),
        //Question("Simplify: (2x - 3)(x + 4)", listOf("2x^2 + 5x - 12", "2x^2 + 8x - 12", "2x^2 + 8x + 12", "2x^2 - x - 12"), 0),
        //Question("If f(x) = 2x + 1, find f(3)", listOf("5", "6", "7", "8"), 2),
        //Question("What is the slope of the line y = 5x + 3?", listOf("3", "5", "5x", "3x"), 1),
        //Question("Solve for x: 3x - 2 = 2x + 1", listOf("x = 3", "x = 0", "x = 1", "x = -1"), 0),
        //Question("What is the area of a triangle with a base of 6 cm and a height of 4 cm?", listOf("10 cm^2", "12 cm^2", "24 cm^2", "48 cm^2"), 2)
        // ... (añade más preguntas según sea necesario)

        // Preguntas para Primaria
        Question("¿Cuál es la raíz cuadrada de 16?", listOf("3", "4", "5", "6"), 1, "Primaria", "Fácil", "Aritmética"),
        Question("Si tienes 10 manzanas y das 2 a tu amigo, ¿cuántas manzanas tienes?", listOf("8", "7", "10", "12"), 0, "Primaria", "Fácil", "Aritmética"),
        Question("¿Cuántos lados tiene un triángulo?", listOf("3", "4", "5", "6"), 0, "Primaria", "Fácil", "Geometría"),

        // Preguntas para Secundaria
        Question("Resuelve la ecuación: 3x + 2 = 8", listOf("x = 2", "x = 3", "x = 4", "x = 5"), 0, "Secundaria", "Intermedio", "Álgebra"),
        Question("¿Cuál es el perímetro de un rectángulo si la longitud es 10 y la anchura es 5?", listOf("30", "20", "15", "25"), 0, "Secundaria", "Fácil", "Álgebra"),
        Question("¿Cuál es el área de un triángulo con base 10 y altura 5?", listOf("25", "50", "20", "100"), 0, "Secundaria", "Intermedio", "Geometría"),

        // Preguntas para Universitario
        Question("Resuelve la ecuación cuadrática: x^2 + 5x + 6 = 0", listOf("x = -2, -3", "x = 2, 3", "x = -1, -6", "x = 1, 6"), 0, "Universitario", "Difícil", "Álgebra"),
        Question("Si un triángulo tiene ángulos de 45, 45 y 90 grados, ¿qué tipo de triángulo es?", listOf("Triángulo rectángulo", "Triángulo isósceles", "Triángulo escaleno", "Triángulo equilátero"), 0, "Universitario", "Difícil", "Geometría"),
        Question("¿Cuál es la derivada de f(x) = 3x^2 + 2x + 1?", listOf("f'(x) = 6x + 2", "f'(x) = 6x", "f'(x) = 3x + 1", "f'(x) = 6x + 1"), 0, "Secundaria", "Difícil", "Álgebra"),

        Question("Si tienes una secuencia aritmética donde el primer término es 3 y la diferencia común es 2, ¿cuál es el quinto término?", listOf("5", "7", "9", "11"), 3, "Universitario", "Difícil", "Aritmética"),
        Question("Si el radio de un círculo es 5 cm, ¿cuál es la circunferencia del círculo?", listOf("10 cm", "15 cm", "25 cm", "31.415 cm"), 3, "Universitario", "Difícil", "Aritmética"),

        // Secundaria, Intermedio, Aritmética
        Question("Si compras 3 camisetas a $20 cada una, ¿cuánto gastas en total?", listOf("$40", "$60", "$80", "$100"), 1, "Secundaria", "Intermedio", "Aritmética"),
        Question("Si un coche viaja a 60 km/h, ¿cuántos km recorrerá en 2 horas?", listOf("30 km", "60 km", "120 km", "240 km"), 2, "Secundaria", "Intermedio", "Aritmética"),

        // Primaria, Fácil, Álgebra
        Question("Si tienes 3 manzanas y consigues 2 más, ¿cuántas manzanas tienes?", listOf("3", "4", "5", "6"), 2, "Primaria", "Fácil", "Álgebra"),
        Question("Si tienes 4 caramelos y das 2 a tu amigo, ¿cuántos caramelos te quedan?", listOf("1", "2", "3", "4"), 1, "Primaria", "Fácil", "Álgebra")
    )


    private var currentQuestionIndex = 0
    private var filteredQuestions: List<Question> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            tflite = Interpreter(loadModelFile(this))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

        //if (isFirstTime()) {
          //  startActivity(Intent(this, LoginActivity::class.java))
          //  finish()
          //  return
        // }

        setContentView(R.layout.activity_main)

        questionTextView = findViewById(R.id.question_text_view)
        answerRadioGroup = findViewById(R.id.answer_radio_group)
        submitButton = findViewById(R.id.submit_button)

        filteredQuestions = getFilteredQuestions()
        showQuestion()

        submitButton.setOnClickListener {
            questionAnswered()
            if (checkAnswer()) {
                nextQuestion()
            }
        }

        resetButton = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            resetForm()
        }

       // loginButton = findViewById(R.id.loginButton)
       // loginButton.setOnClickListener {
        //    val intent = Intent(this, LoginActivity::class.java)
        //    startActivity(intent)
       // }

        /*classifyButton = findViewById(R.id.classify_button)
        classifyButton.setOnClickListener {
            classifyQuestion()
        }*/
    }

    /*private fun showQuestion() {
        val question = questions[currentQuestionIndex]
        questionTextView.text = question.text
        answerRadioGroup.clearCheck()
        for (index in 0 until answerRadioGroup.childCount) {
            val radioButton = answerRadioGroup.getChildAt(index) as RadioButton
            radioButton.text = question.answers[index]
        }
    }*/

    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd("mi_modelo.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun questionAnswered() {
        questions[currentQuestionIndex].respondida = true
        val unansweredQuestions = questions.filter { !it.respondida }
        filteredQuestions = classifyAndSortQuestions(unansweredQuestions)
    }

    private fun showQuestion() {
        if (filteredQuestions.isNotEmpty()) {
            startTime = System.currentTimeMillis()
            val question = filteredQuestions[currentQuestionIndex]
            questionTextView.text = question.text
            answerRadioGroup.clearCheck()
            answerRadioGroup.children.forEachIndexed { index, view ->
                (view as RadioButton).text = question.answers[index]
            }
        } else {
            questionTextView.text = "No hay preguntas disponibles para los criterios seleccionados."
        }
    }

    /*private fun classifyQuestion(): String {
        val averagedTimespent = if (numQuestionsAnswered > 0) totalTimespent / numQuestionsAnswered else 0L
        val averagedComplexity = 49.60465116f

        val features = floatArrayOf(numCorrectAnswers.toFloat(), numIncorrectAnswers.toFloat(), averagedTimespent.toFloat(), averagedComplexity)

        val output = Array(1) { FloatArray(3) }  // Asume que hay 3 clases: Baja, Media, Alta
        tflite.run(features, output)
        val outputIndex = output[0].indexOfFirst { it == (output[0].maxOrNull() ?: Float.NEGATIVE_INFINITY) }
        return when(outputIndex) {
            0 -> "Baja"
            1 -> "Media"
            2 -> "Alta"
            else -> "Desconocido"
        }
    }*/

    private fun classifyAndSortQuestions(unansweredQuestions: List<Question>): List<Question> {
        val averagedTimespent = if (numQuestionsAnswered > 0) totalTimespent / numQuestionsAnswered else 0L
        val averagedComplexity = 49.60465116f

        val features = floatArrayOf(numCorrectAnswers.toFloat(), numIncorrectAnswers.toFloat(), averagedTimespent.toFloat(), averagedComplexity)
        val output = Array(1) { FloatArray(3) }
        tflite.run(features, output)
        val outputIndex = output[0].indexOfFirst { it == (output[0].maxOrNull() ?: Float.NEGATIVE_INFINITY) }
        val classification = when(outputIndex) {
            0 -> "Baja"
            1 -> "Media"
            2 -> "Alta"
            else -> "Desconocido"
        }
        return unansweredQuestions.sortedBy {
            when (it.difficulty) {
                "Difícil" -> if (classification == "Alta") 0 else 2
                "Intermedio" -> if (classification == "Media") 0 else 2
                "Fácil" -> if (classification == "Baja") 0 else 2
                else -> 2
            }
        }
    }




    /*private fun classifyAndSortQuestions() {
        val averagedTimespent = if (numQuestionsAnswered > 0) totalTimespent / numQuestionsAnswered else 0L
        val averagedComplexity = 49.60465116f

        val features = floatArrayOf(numCorrectAnswers.toFloat(), numIncorrectAnswers.toFloat(), averagedTimespent.toFloat(), averagedComplexity)

        val classifiedQuestions = questions.map { question ->
            val classification = classifyQuestion()
            Pair(question, classification)
        }

        val sortedQuestions = classifiedQuestions.sortedBy { (_, classification) ->
            when (classification) {
                "Alta" -> 0
                "Media" -> 1
                "Baja" -> 2
                else -> 3
            }
        }.map { it.first }

        filteredQuestions = sortedQuestions
        currentQuestionIndex = 0
        showQuestion()
    }*/


    private fun resetForm() {
        // Resetear el estado de onboarding completado
        val sharedPreferences: SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboarding_completed", false).apply()

        // Iniciar OnboardingActivity
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }

    private fun checkAnswer(): Boolean {
        val endTime = System.currentTimeMillis()  // Captura el tiempo final
        totalTimespent += (endTime - startTime)
        numQuestionsAnswered++
        val selectedAnswerIndex = answerRadioGroup.checkedRadioButtonId
        if (selectedAnswerIndex != -1) {
            val correctAnswerIndex = questions[currentQuestionIndex].correctAnswerIndex
            if (selectedAnswerIndex == answerRadioGroup.getChildAt(correctAnswerIndex).id) {
                numCorrectAnswers++
                Toast.makeText(this, "Correcto!", Toast.LENGTH_SHORT).show()
            } else {
                numIncorrectAnswers++
                Toast.makeText(this, "Incorrecto!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, selecciona una respuesta", Toast.LENGTH_SHORT).show()
        }
        nextQuestion()
        return true
    }


    /*private fun nextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questions.size
        showQuestion()
    }*/

    private fun nextQuestion() {
        if (filteredQuestions.isNotEmpty()) {
            currentQuestionIndex = (currentQuestionIndex + 1) % filteredQuestions.size
            showQuestion()
        } else {
            Toast.makeText(this, "No hay más preguntas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isFirstTime(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)
        return !sharedPreferences.getBoolean("onboarding_completed", false)
    }

    private fun getPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun getFilteredQuestions(): List<Question> {
        val preferences = getPreferences()
        val grade = preferences.getString("academic_grade", "")
        val difficulty = preferences.getString("difficulty_level", "")
        val course = preferences.getString("interest_course", "")

        val filteredQuestions = questions.filter { it.grade == grade && it.difficulty == difficulty && it.course == course }
        val otherQuestions = questions - filteredQuestions
        return filteredQuestions + otherQuestions // Combina las listas
    }

    //data class Question(val text: String, val answers: List<String>, val correctAnswerIndex: Int)
    data class Question(
        val text: String,
        val answers: List<String>,
        val correctAnswerIndex: Int,
        val grade: String,
        val difficulty: String,
        val course: String,
        var respondida: Boolean = false
    )
}
