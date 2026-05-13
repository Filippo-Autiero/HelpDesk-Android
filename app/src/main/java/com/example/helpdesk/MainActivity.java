package com.example.helpdesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etToken;
    private Button btnConnetti;
    private RecyclerView recyclerTickets;
    private FloatingActionButton fabNewTicket;

    private ArrayList<Ticket> ticketList;
    private TicketAdapter adapter;

    private String currentEmail = "";
    private String currentToken = "";

    private ActivityResultLauncher<Intent> ticketLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etToken = findViewById(R.id.etToken);
        btnConnetti = findViewById(R.id.btnConnetti);
        recyclerTickets = findViewById(R.id.recyclerTickets);
        fabNewTicket = findViewById(R.id.fabNewTicket);

        ticketList = new ArrayList<>();

        adapter = new TicketAdapter(ticketList, ticket -> {
            Intent intent = new Intent(MainActivity.this, ItemTicketActivity.class);
            intent.putExtra("MODE", "view");
            intent.putExtra("ID", ticket.ID);
            intent.putExtra("Titolo", ticket.Titolo);
            intent.putExtra("Descrizione", ticket.Descrizione);
            intent.putExtra("Dove", ticket.Dove);
            intent.putExtra("Annotazione", ticket.Annotazione);
            intent.putExtra("EMAIL", currentEmail);
            intent.putExtra("TOKEN", currentToken);
            ticketLauncher.launch(intent);
        });

        recyclerTickets.setLayoutManager(new LinearLayoutManager(this));
        recyclerTickets.setAdapter(adapter);

        ticketLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadTickets();
                    }
                }
        );

        btnConnetti.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String token = etToken.getText() != null ? etToken.getText().toString().trim() : "";

            if (email.isEmpty() || token.isEmpty()) {
                Toast.makeText(MainActivity.this, "Inserisci email e token", Toast.LENGTH_SHORT).show();
                return;
            }

            currentEmail = email;
            currentToken = token;

            loadTickets();
        });

        fabNewTicket.setOnClickListener(v -> {
            if (currentEmail.isEmpty() || currentToken.isEmpty()) {
                Toast.makeText(MainActivity.this, "Prima premi Connetti", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, ItemTicketActivity.class);
            intent.putExtra("MODE", "create");
            intent.putExtra("EMAIL", currentEmail);
            intent.putExtra("TOKEN", currentToken);
            ticketLauncher.launch(intent);
        });
    }

    private void loadTickets() {
        ApiUser user = new ApiUser(currentEmail, currentToken);
        ReadParams params = new ReadParams(0, 1, 50);
        ApiRequest<ReadParams> request = new ApiRequest<>(params, user);

        ApiClient.getClient().getTickets(request).enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticketList.clear();
                    ticketList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Connesso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Errore risposta server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ticket>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Errore connessione: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}