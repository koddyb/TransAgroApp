package com.example.transagro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.transagro.R;
import com.example.transagro.model.LoginResponse;
import com.example.transagro.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private Button btnLogin;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.btn_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getLoginResponse().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                if(loginResponse != null && loginResponse.isSuccess()){
                    // Récupérer le rôle du chauffeur
                    String role = loginResponse.getDriver().getRole();
                    Toast.makeText(LoginActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "Driver ID transmis: " + loginResponse.getDriver().getId());

                    if (role != null && role.equalsIgnoreCase("admin")) {
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        intent.putExtra("driverId", loginResponse.getDriver().getId());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        intent.putExtra("driverId", loginResponse.getDriver().getId());
                        startActivity(intent);
                    }
                    finish();


                } else {
                    Toast.makeText(LoginActivity.this, "Échec de la connexion: " +
                            (loginResponse != null ? loginResponse.getError() : ""), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                authViewModel.login(email, password);
            }
        });
    }
}
