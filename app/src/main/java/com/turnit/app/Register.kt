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

class Register : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var errorText: TextView
    private lateinit var authService: AuthService
    private lateinit var haveAcc: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Инициализация элементов
        haveAcc = findViewById(R.id.haveAcc)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        registerButton = findViewById(R.id.registerButton)
        errorText = findViewById(R.id.errorText)
        // Инициализация AuthService
        authService = AuthService()



        // Обработчик нажатия на кнопку
        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                errorText.text = "Пожалуйста, заполните все поля"
            } else {
                errorText.text = " "
                // Вызов метода регистрации через AuthService
                authService.registerUser(email, password, onSuccess = {
                    // Успешная регистрация
                    Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show()


                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.expand_in, R.anim.none)

                }, onError = { errorMessage ->
                    // Ошибка при регистрации
                    errorText.text = errorMessage
                    errorText.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                })
            }
        }

        haveAcc.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.expand_in, R.anim.none)
        }


    }
    override fun onBackPressed() {
        super.onBackPressed() // Стандартное завершение активности
        // анимации при возврате
        overridePendingTransition(R.anim.none, R.anim.shrink_out)
    }
}
