package com.example.vizora;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void login(View view){
        //TODO login helyes működése
        Intent intent = new Intent(this, login.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    public void register(View view){
        //todo register helyes működése
        Intent intent = new Intent(this, register.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}