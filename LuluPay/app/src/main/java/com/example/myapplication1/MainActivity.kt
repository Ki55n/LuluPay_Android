package com.example.myapplication1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication1.databinding.ActivityMainBinding
import com.sdk.lulupay.activity.PdfViewScreen
import com.sdk.lulupay.activity.RemittanceScreen

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val intent = Intent(
            this@MainActivity,
            RemittanceScreen::class.java
//            PdfViewScreen::class.java
        )

        /* Bundle bundle = new Bundle();
        bundle.putBoolean("ISAUTOLOGIN", true);  Key-value pair
        bundle.putString("USERNAME", "testAgent");
        bundle.putString("PASSWORD", "password");
        intent.putExtras(bundle);*/
        setSupportActionBar(binding!!.toolbar)
        binding?.container?.btnPayment?.setOnClickListener {
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.binding = null
    }
}