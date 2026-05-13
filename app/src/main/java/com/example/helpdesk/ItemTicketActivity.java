package com.example.helpdesk;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemTicketActivity extends AppCompatActivity {

    private static final String TAG = "API_DEBUG";

    private TextInputEditText etTitolo, etDescrizione, etDove, etAnnotazione;
    private LinearLayout layoutBottoni;
    private Button btnSalva, btnAnnulla;
    private String mode;
    private int ticketId = 0;

    private String email = "";
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_ticket);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitolo = findViewById(R.id.etTitolo);
        etDescrizione = findViewById(R.id.etDescrizione);
        etDove = findViewById(R.id.etDove);
        etAnnotazione = findViewById(R.id.etAnnotazione);
        layoutBottoni = findViewById(R.id.layoutBottoni);
        btnSalva = findViewById(R.id.btnSalva);
        btnAnnulla = findViewById(R.id.btnAnnulla);

        mode = getIntent().getStringExtra("MODE");
        if (mode == null) mode = "view";

        ticketId = getIntent().getIntExtra("ID", 0);

        email = getIntent().getStringExtra("EMAIL");
        token = getIntent().getStringExtra("TOKEN");

        if (email == null) email = "";
        if (token == null) token = "";

        switch (mode) {
            case "create":
                impostaModalita(true, "Nuovo Ticket", true);
                break;

            case "edit":
                impostaModalita(true, "Modifica Ticket", true);
                caricaDatiIntent();
                break;

            default:
                impostaModalita(false, "Dettaglio Ticket", false);
                caricaDatiIntent();
                break;
        }

        btnSalva.setOnClickListener(v -> salvaTicket());
        btnAnnulla.setOnClickListener(v -> finish());
    }

    private void caricaDatiIntent() {
        etTitolo.setText(getIntent().getStringExtra("Titolo"));
        etDescrizione.setText(getIntent().getStringExtra("Descrizione"));
        etDove.setText(getIntent().getStringExtra("Dove"));
        etAnnotazione.setText(getIntent().getStringExtra("Annotazione"));
    }

    private void impostaModalita(boolean abilitato, String titolo, boolean mostraBottoni) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titolo);
        }

        etTitolo.setEnabled(abilitato);
        etDescrizione.setEnabled(abilitato);
        etDove.setEnabled(abilitato);
        etAnnotazione.setEnabled(abilitato);

        layoutBottoni.setVisibility(mostraBottoni ? LinearLayout.VISIBLE : LinearLayout.GONE);
    }

    private void salvaTicket() {
        if (email.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Email o token mancanti", Toast.LENGTH_SHORT).show();
            return;
        }

        Ticket ticket = new Ticket();
        ticket.ID = ticketId;
        ticket.Titolo = etTitolo.getText() != null ? etTitolo.getText().toString().trim() : "";
        ticket.Descrizione = etDescrizione.getText() != null ? etDescrizione.getText().toString().trim() : "";
        ticket.Dove = etDove.getText() != null ? etDove.getText().toString().trim() : "";
        ticket.Annotazione = etAnnotazione.getText() != null ? etAnnotazione.getText().toString().trim() : "";

        if (ticket.Titolo.isEmpty()) {
            Toast.makeText(this, "Inserisci il titolo", Toast.LENGTH_SHORT).show();
            return;
        }

        ticket.Dataapertura = "2026-05-13";
        ticket.Datachiusura = null;
        ticket.Oreuomopreviste = 1;
        ticket.Prioritaautore = 1;
        ticket.Imgpath = "";
        ticket.Filepath = "";
        ticket.Marcaprodotto = "";
        ticket.Modelloprodotto = "";
        ticket.Serialeprodotto = "";
        ticket.Inventarioprodotto = "";
        ticket.Notificheallautore = 1;
        ticket.IDtiposegnalazione = 1;
        ticket.IDutentecreatore = 1;
        ticket.IDutenteultimoeditor = 1;

        ApiUser user = new ApiUser(email, token);
        ApiRequest<Ticket> request = new ApiRequest<>(ticket, user);

        if ("edit".equals(mode)) {
            ApiClient.getClient().updateTicket(request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    String errorText = leggiErrore(response);

                    Log.d(TAG, "UPDATE code: " + response.code());
                    Log.d(TAG, "UPDATE message: " + response.message());
                    Log.d(TAG, "UPDATE body: " + response.body());
                    Log.d(TAG, "UPDATE errorBody: " + errorText);

                    if (response.isSuccessful() && response.body() != null && response.body().id > 0) {
                        Toast.makeText(ItemTicketActivity.this, "Ticket aggiornato", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ItemTicketActivity.this,
                                "Errore aggiornamento: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "UPDATE failure", t);
                    Toast.makeText(ItemTicketActivity.this,
                            "Errore rete: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            ApiClient.getClient().createTicket(request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    String errorText = leggiErrore(response);

                    Log.d(TAG, "CREATE code: " + response.code());
                    Log.d(TAG, "CREATE message: " + response.message());
                    Log.d(TAG, "CREATE body: " + response.body());
                    Log.d(TAG, "CREATE errorBody: " + errorText);

                    if (response.isSuccessful() && response.body() != null && response.body().id > 0) {
                        Toast.makeText(ItemTicketActivity.this, "Ticket creato", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ItemTicketActivity.this,
                                "Errore salvataggio: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "CREATE failure", t);
                    Toast.makeText(ItemTicketActivity.this,
                            "Errore rete: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String leggiErrore(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("view".equals(mode)) {
            getMenuInflater().inflate(R.menu.menu_item_ticket, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if ("view".equals(mode)) {
            getMenuInflater().inflate(R.menu.menu_item_ticket, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            mode = "edit";
            impostaModalita(true, "Modifica Ticket", true);
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_delete) {
            eliminaTicket();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void eliminaTicket() {
        if (ticketId <= 0) {
            Toast.makeText(this, "ID ticket non valido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Email o token mancanti", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiUser user = new ApiUser(email, token);
        ApiRequest<DeleteParams> request = new ApiRequest<>(new DeleteParams(ticketId), user);

        ApiClient.getClient().deleteTicket(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                String errorText = leggiErrore(response);

                Log.d(TAG, "DELETE code: " + response.code());
                Log.d(TAG, "DELETE message: " + response.message());
                Log.d(TAG, "DELETE body: " + response.body());
                Log.d(TAG, "DELETE errorBody: " + errorText);

                if (response.isSuccessful() && response.body() != null && response.body().id > 0) {
                    Toast.makeText(ItemTicketActivity.this, "Ticket eliminato", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ItemTicketActivity.this,
                            "Errore eliminazione: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "DELETE failure", t);
                Toast.makeText(ItemTicketActivity.this,
                        "Errore rete: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}