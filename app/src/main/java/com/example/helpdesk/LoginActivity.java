package com.example.helpdesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etToken;
    private Button btnConnetti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etToken = findViewById(R.id.etToken);
        btnConnetti = findViewById(R.id.btnConnetti);

        btnConnetti.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String token = etToken.getText() != null ? etToken.getText().toString().trim() : "";

            if (email.isEmpty() || token.isEmpty()) {
                Toast.makeText(this, "Inserisci email e token", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiUser user = new ApiUser(email, token);
            ReadParams params = new ReadParams(0, 1, 1);
            ApiRequest<ReadParams> request = new ApiRequest<>(params, user);

            ApiClient.getClient().getTickets(request).enqueue(new Callback<List<Ticket>>() {
                @Override
                public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("TOKEN", token);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenziali non valide", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Ticket>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Errore connessione: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
