package com.sdk.lulupay.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.sdk.lulupay.R
import com.sdk.lulupay.requestId.RequestId
import com.sdk.lulupay.session.SessionManager
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.token.AccessToken
import kotlinx.coroutines.launch

class LoginScreen : AppCompatActivity() {

  private lateinit var dialog: AlertDialog
  private lateinit var errorDialog: AlertDialog
  private lateinit var login_btn: MaterialButton
  private lateinit var username_edittext: EditText
  private lateinit var password_edittext: TextInputEditText
 /* private var isRemittance: Boolean = false
  private var isPayment: Boolean = false
  private val REMMITTANCE_INTENT_EXTRA: String = "ISREMITTANCE"
  private val PAYMENT_INTENT_EXTRA: String = "ISPAYMENT"
  private val AUTO_LOGIN: String = "ISAUTOLOGIN"
  private val USERNAME: String = "USERNAME"
  private val PASSWORD: String = "PASSWORD"
  private val GRANTTYPE: String = "GRANTTYPE"
  private val CLIENTID: String = "CLIENTID"
  private val SCOPE: String = "SCOPE"
  private val CLIENTSECRET: String = "CLIENTSECRET"*/
  private val MANUAL_LOGIN = "ISMANUALLOGIN"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login)

    setupViews()
  }

  private fun loginUser(username: String, password: String) {
    // Add session information
    addSession(
        username = username,
        password = password,
        grantType = "password",
        clientId = "cdp_app",
        scope = null,
        clientSecret = "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y"
    )

    lifecycleScope.launch {
        try {
            // Get access token
            val result = AccessToken.getAccessToken(
                username = SessionManager.username.orEmpty(),
                password = SessionManager.password.orEmpty(),
                requestId = "Test-${RequestId.generateRequestId()}",
                grantType = SessionManager.grantType ?: "password",
                clientId = SessionManager.clientId ?: "cdp_app",
                scope = SessionManager.scope,
                clientSecret = SessionManager.clientSecret ?: "mSh18BPiMZeQqFfOvWhgv8wzvnNVbj3Y"
            )

            val error = result.exceptionOrNull()
            if (error != null) {
                dismissDialog()
                showMessage(error.message ?: "Error occurred: Null")
                return@launch
            }

            val newToken = result.getOrNull()?.access_token
            if (newToken.isNullOrEmpty()) {
                dismissDialog()
                showMessage("Access token is null or empty")
                return@launch
            }

            // Redirect on successful login
            redirect()
        } catch (e: Exception) {
            dismissDialog()
            showMessage(e.message ?: "An unexpected error occurred")
        }
    }
}

 private fun redirect() {
 dismissDialog()
    val intent = Intent(this, RemittanceScreen::class.java)
    val bundle = Bundle() // Use 'val' and correct Kotlin syntax for instantiation
    bundle.putBoolean(MANUAL_LOGIN, true) // Key-value pair
    intent.putExtras(bundle)
    startActivity(intent)
    finish()
}

  private fun setupViews() {
    login_btn = findViewById(R.id.login_btn)
    username_edittext = findViewById(R.id.username)
    password_edittext = findViewById(R.id.password)

    login_btn.setOnClickListener {
      val username: String = username_edittext.text.toString()
      val password: String = password_edittext.text.toString()
      if (username.isNotEmpty()) {
        if (password.isNotEmpty()) {
          showDialog()
          loginUser(username, password)
        } else {
          showMessage("Password is required!")
        }
      } else {
        showMessage("Username is required!")
      }
    }
  }

  private fun showDialog() {
    // Build the AlertDialog
    dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
        .setView(R.layout.custom_dialog) // Set custom layout as the dialog's content
        .setCancelable(false) // Disable back button dismiss
        .create()

    // Prevent dialog from dismissing on outside touch
    dialog.setCanceledOnTouchOutside(false)

    // Show the dialog
    dialog.show()
}

  private fun dismissDialog() {
    if (dialog.isShowing == true) {
      dialog.dismiss()
    }
  }

  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun addSession(
      username: String,
      password: String,
      grantType: String,
      clientId: String,
      scope: String?,
      clientSecret: String
  ) {
    SessionManager.username = username
    SessionManager.password = password
    SessionManager.grantType = grantType
    SessionManager.clientId = clientId
    SessionManager.scope = scope
    SessionManager.clientSecret = clientSecret
  }
}
