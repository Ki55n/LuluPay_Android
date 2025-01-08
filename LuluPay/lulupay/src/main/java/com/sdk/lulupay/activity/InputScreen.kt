package com.sdk.lulupay.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sdk.lulupay.R

class InputScreen : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.remittance_input)

    setupViews()
  }

  private fun setupViews() {}
}
