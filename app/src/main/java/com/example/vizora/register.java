package com.example.vizora;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {
    EditText email;
    EditText pwd;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.editTextTextEmailAddress2);
        pwd = findViewById(R.id.editTextTextPassword2);
        auth = FirebaseAuth.getInstance();

    }
    public void registerUser(View view){
        String emailtext = email.getText().toString();
        String password = pwd.getText().toString();
        if(!emailtext.isEmpty() && !password.isEmpty()){
            auth.createUserWithEmailAndPassword(emailtext,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //sikeres
                        Intent intent = new Intent(register.this, MainActivity.class); // register.this-t használunk
                        startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation(register.this).toBundle()); // register.this-t használunk
                    }
                    else
                    {
                        email.setError("Az email már foglalt!");
                        //sikertelen
                    }
                }
            });
        }
        else
        {
            if(emailtext.isEmpty())
            {
                email.setError("Az email nem lehet üres!");
            }
            if(password.isEmpty()){
                pwd.setError("A jelszó nem lehet üres!");
            }
        }
    }
}