package com.mateusandreatta.gabriellasbrigadeiria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
    EditText editTextPasswordConfirm;
    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Cadastro");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        loadingProgressBar = findViewById(R.id.loadingSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onSignUp(View view){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String passwordConfirm = editTextPasswordConfirm.getText().toString();

        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || passwordConfirm.isEmpty()){
            Toast.makeText(this, R.string.toast_erro_fill_all_inputs, Toast.LENGTH_SHORT).show();
        }else{
            if(!passwordConfirm.equals(password)){
                Toast.makeText(this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
            }else{
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
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage() != null ? Global.translateFirebaseException(this, task.getException().getMessage()) : task.getException().toString(), Toast.LENGTH_LONG).show();
                                Log.e("TAG-SignUpActivity","Create Error" +task.getException().toString());
                            }
                        });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}