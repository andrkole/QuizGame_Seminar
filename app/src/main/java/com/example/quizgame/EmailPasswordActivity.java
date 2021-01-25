package com.example.quizgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginButton;
    private Button registerButton;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ((EditText)findViewById(R.id.editTextEmail)).getText().toString();
                password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                signIn(email, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = ((EditText)findViewById(R.id.editTextEmail)).getText().toString();
                password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                createAccount(email, password);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        redirectToHome(currentUser);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            redirectToHome(user);
                        } else {
                            Log.w("EmailPass", "createUserFailure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Registracija neuspješna!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            redirectToHome(user);
                        } else {
                            Log.w("EmailPass", "signInFailure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Prijava neuspješna.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToHome(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
