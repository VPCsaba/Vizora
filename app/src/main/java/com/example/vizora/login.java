package com.example.vizora;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class login extends AppCompatActivity {
    EditText email;
    EditText pwd;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.editTextTextEmailAddress);
        pwd = findViewById(R.id.editTextTextPassword);
        auth = FirebaseAuth.getInstance();
    }
    public void loginUser(View view){
        String emailtext = email.getText().toString();
        String password = pwd.getText().toString();
        if(!emailtext.isEmpty() && !password.isEmpty()){
            auth.signInWithEmailAndPassword(emailtext,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //sikeres
                        Intent intent = new Intent(login.this, menu.class); // register.this-t használunk
                        startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation(login.this).toBundle()); // register.this-t használunk
                    }
                    else
                    {
                        //sikertelen
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            // The email address is not associated with any existing user account
                            email.setError("Nincs ilyen email cím regisztrálva");
                            pwd.setError(null);
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // The password did not match
                            pwd.setError("Rossz jelszó");
                            email.setError(null);
                        } else {
                            // Handle other errors
                            Toast.makeText(login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            email.setError(null);
                            pwd.setError(null);
                        }
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