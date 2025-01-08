package com.sdk.lulupay.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sdk.lulupay.R

class DetailsScreen : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.remittance_details)

    getQuoteDetails()
    setupViews()
    populateData()
  }

  private fun setupViews() {}

  private fun getQuoteDetails() {}

  private fun populateData() {}
}
