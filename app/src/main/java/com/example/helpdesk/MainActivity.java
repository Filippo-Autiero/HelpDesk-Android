package com.example.helpdesk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ricevi email e token da LoginActivity
        currentEmail = getIntent().getStringExtra("EMAIL");
        currentToken = getIntent().getStringExtra("TOKEN");
        if (currentEmail == null) currentEmail = "";
        if (currentToken == null) currentToken = "";

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

        fabNewTicket.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ItemTicketActivity.class);
            intent.putExtra("MODE", "create");
            intent.putExtra("EMAIL", currentEmail);
            intent.putExtra("TOKEN", currentToken);
            ticketLauncher.launch(intent);
        });

        // Carica subito i ticket
        loadTickets();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Torna alla login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}