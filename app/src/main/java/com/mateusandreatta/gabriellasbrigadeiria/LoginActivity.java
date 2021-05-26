package com.mateusandreatta.gabriellasbrigadeiria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    EditText editTextEmail;
    EditText editTextPassword;
    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        loadingProgressBar = findViewById(R.id.loading);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onLogin(View view){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        loadingProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(LoginActivity.this,
                        task -> {
                            loadingProgressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                firebaseUser = firebaseAuth.getCurrentUser();
                                if(firebaseUser != null){
                                    Toast.makeText(this, firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(this, firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, task.getException().getMessage() != null ? task.getException().getMessage() : task.getException().toString(), Toast.LENGTH_SHORT).show();
                                Log.e("FIREBASEAUTH","SignIn Error" +task.getException().toString());
                            }
                        });
    }

    public void onSignUp(View view){
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }
}