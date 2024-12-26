package com.turnit.app

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Welcome : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)


        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.expand_in, R.anim.none)

        }

        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.expand_in, R.anim.none)
        }


    }
}