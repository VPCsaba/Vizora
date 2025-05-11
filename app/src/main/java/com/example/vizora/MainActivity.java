package com.example.vizora;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

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
        Button buttom1  = findViewById(R.id.loginbutton);
        Button buttom2  = findViewById(R.id.registerbutton);
        ImageView i  = findViewById(R.id.imageView);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(slideIn);
        set.addAnimation(fadeIn);

        AnimationSet set2 = new AnimationSet(true);
        set.addAnimation(fadeIn);
        i.startAnimation(set2);

        buttom1.startAnimation(set);
        buttom2.startAnimation(set);
    }
    public void login(View view){
        //TODO login helyes működése
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void register(View view){
        //todo register helyes működése
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}