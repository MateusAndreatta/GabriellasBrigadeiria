package com.mateusandreatta.gabriellasbrigadeiria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.Global;

public class SignUpActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextName;
    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        loadingProgressBar = findViewById(R.id.loadingSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onSignUp(View view){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();

        loadingProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this,
                        task -> {
                            if(task.isSuccessful()){

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        loadingProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    });
                            }else{
                                loadingProgressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage() != null ? Global.translateFirebaseException(task.getException().getMessage()) : task.getException().toString(), Toast.LENGTH_LONG).show();
                                Log.e("TAG-SignUpActivity","Create Error" +task.getException().toString());
                            }
                        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        }
    }
}