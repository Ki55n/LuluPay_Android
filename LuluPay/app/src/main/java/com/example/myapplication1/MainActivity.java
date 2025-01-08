package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication1.databinding.ActivityMainBinding;
import com.sdk.lulupay.activity.LoginScreen;
import com.sdk.lulupay.activity.RemittanceScreen;

public class MainActivity extends AppCompatActivity {
	
	  private ActivityMainBinding binding;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		    binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        Intent intent = new Intent(MainActivity.this, RemittanceScreen.class);
       /* Bundle bundle = new Bundle();
        bundle.putBoolean("ISAUTOLOGIN", true);  Key-value pair
        bundle.putString("USERNAME", "testAgent");
        bundle.putString("PASSWORD", "password");
        intent.putExtras(bundle);*/
        
		    setSupportActionBar(binding.toolbar);

		    binding.fab.setOnClickListener(v ->

        // Start SecondActivity
        startActivity(intent)
        );
        
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}