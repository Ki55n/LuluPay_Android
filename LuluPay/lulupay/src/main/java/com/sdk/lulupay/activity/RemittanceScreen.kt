package com.sdk.lulupay.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sdk.lulupay.R
import com.sdk.lulupay.database.LuluPayDB
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class RemittanceScreen : AppCompatActivity() {

  private lateinit var errorDialog: AlertDialog

  private lateinit var luluPayDB: LuluPayDB

  private val AUTO_LOGIN: String = "ISAUTOLOGIN"
  private val MANUAL_LOGIN = "ISMANUALLOGIN"
  private val USERNAME: String = "USERNAME"
  private val PASSWORD: String = "PASSWORD"

  private lateinit var addNewReceipientFab: FloatingActionButton
  private lateinit var recyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.remittance)

    luluPayDB = LuluPayDB(this)

    handleIntentExtras()
    setupViews()
    getHistoryRemittance()
    setClickListener()
  }

  private fun setupViews() {
    addNewReceipientFab = findViewById(R.id.fab)
    recyclerView = findViewById(R.id.recyclerView)
  }

  private fun setClickListener() {
    addNewReceipientFab.setOnClickListener {
      val intent = Intent(this, AddNewReceipient::class.java)
      startActivity(intent)
    }
  }

  private fun handleIntentExtras() {
    val intent = getIntent()

    if (intent.getBooleanExtra(MANUAL_LOGIN, false)) {
        handleManualLogin()
        return
    }

    if (intent.getBooleanExtra(AUTO_LOGIN, false)) {
        handleAutoLogin(intent)
    } else {
        // Redirect to login screen
        showMessage("Please login")
        redirectToLoginScreen()
    }
}

private fun handleManualLogin() {
    if (SessionManager.username.isNullOrEmpty() ||
        SessionManager.password.isNullOrEmpty() ||
        SessionManager.grantType.isNullOrEmpty() ||
        SessionManager.clientId.isNullOrEmpty() ||
        SessionManager.clientSecret.isNullOrEmpty()
    ) {
        finish()
    }
}

private fun handleAutoLogin(intent: Intent) {
    val username = intent.getStringExtra(USERNAME)
    val password = intent.getStringExtra(PASSWORD)

    when {
        username.isNullOrEmpty() -> {
            showError(
                "Error Occurred",
                "Username intent bundle extra is null: When auto login is set to true the login credentials are required"
            )
        }

        password.isNullOrEmpty() -> {
            showError(
                "Error Occurred",
                "Password intent bundle extra is null: When auto login is set to true the login credentials are required"
            )
        }

        else -> {
            addSession(
                username,
                password,
                "password",
                "cdp_app",
                "",
                "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y"
            )
        }
    }
}

  private fun showError(title: String, errorMessage: String) {
    val builder = AlertDialog.Builder(this) // 'this' refers to the current activity context
    builder.setTitle(title)
    builder.setMessage(errorMessage)
    builder.setPositiveButton("Close") { dialog, _ ->
        finish() // Close the dialog when "OK" is clicked
    }
    val dialog = builder.create()
    dialog.show()
}

  private fun getHistoryRemittance() {
    lifecycleScope.launch {
      try {
        withContext(Dispatchers.IO) {
          val remittanceList = luluPayDB.getAllData()
          // Use the data (e.g., log it or display it in a RecyclerView)
          remittanceList.forEach { remittance ->
            // println("Sender: ${remittance.senderName}, Channel: ${remittance.channelName}")
          }
        }
      } catch (e: Exception) {
        // Handle error
        showMessage(e.message ?: "An unexpected error occurred")
      }
    }
  }

  private fun redirectToLoginScreen() {
    val intent = Intent(this, LoginScreen::class.java)
    startActivity(intent)
    finish()
  }

  private fun addSession(
      username: String,
      password: String,
      grantType: String,
      clientId: String,
      scope: String,
      clientSecret: String
  ) {
    SessionManager.username = username
    SessionManager.password = password
    SessionManager.grantType = grantType
    SessionManager.clientId = clientId
    SessionManager.scope = scope
    SessionManager.clientSecret = clientSecret
  }

  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}
