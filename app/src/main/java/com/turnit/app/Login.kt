package com.turnit.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.turnit.app.auth.AuthService

class Login : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var errorText: TextView
    private lateinit var haventAcc: Button

    private val authService = AuthService() // Инициализация AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Инициализация элементов
        haventAcc = findViewById(R.id.haventAcc)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        errorText = findViewById(R.id.errorText)

        // Обработчик нажатия на кнопку входа
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = "Пожалуйста, заполните все поля"
                errorText.visibility = View.VISIBLE
            } else {
                errorText.text = " "
                performLogin(email, password)
            }
        }

        haventAcc.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.expand_in, R.anim.none)
        }

    }

    private fun performLogin(email: String, password: String) {
        authService.loginUser(
            email,
            password,
            onSuccess = {
                Toast.makeText(this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()
                // Создаем новый Intent для MainActivity
                val intent = Intent(this, MainActivity::class.java)

                // Добавляем флаги для завершения всех предыдущих активностей, включая Welcome
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                // Запускаем MainActivity
                startActivity(intent)

                // Завершаем текущую активность (Welcome)
                finish()
                overridePendingTransition(R.anim.expand_in, R.anim.none)

            },
            onError = { errorMessage ->
                errorText.text = errorMessage
            }
        )
    }

    override fun onBackPressed() {
        super.onBackPressed() // Стандартное завершение активности
        // Добавляем анимацию при возврате
        overridePendingTransition(R.anim.none, R.anim.shrink_out)
    }

}
