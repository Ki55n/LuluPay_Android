package com.example.lulubanking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sdk.lulupay.activity.LoginScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the EditText and Button views
        val usernameEditText = findViewById<EditText>(R.id.usernameInput)
        val passwordEditText = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Set an OnClickListener on the login button
        loginButton.setOnClickListener {
//            val intent = Intent(this, Send_Money::class.java)
//
//
//            startActivity(intent)
            redirect();
            // Get the input from the EditText fields
//            val username = usernameEditText.text.toString()
//            val password = passwordEditText.text.toString()
//
//            // Show a Toast with the username and password
//            Toast.makeText(this, "Username: $username\nPassword: $password", Toast.LENGTH_LONG).show()
        }
    }

    private fun redirect() {
        val intent = Intent(this, LoginScreen::class.java)
        val bundle = Bundle().apply {
            putBoolean("ISREMITTANCE", true) // Key-value pair
        }
        intent.putExtras(bundle)

        startActivity(intent)
    }
}
