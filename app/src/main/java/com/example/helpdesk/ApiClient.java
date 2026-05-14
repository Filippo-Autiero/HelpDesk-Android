package com.example.helpdesk;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class ApiClient {

    private static final String BASE_URL = "https://helpdesk.sviluppo.host/ws/services/";    private static Retrofit retrofit = null;

    public static ApiService getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public interface ApiService {

        @POST("ticket/create.php")
        Call<ApiResponse> createTicket(@Body ApiRequest<Ticket> request);

        @POST("ticket/read.php")
        Call<List<Ticket>> getTickets(@Body ApiRequest<ReadParams> request);

        @PUT("ticket/update.php")
        Call<ApiResponse> updateTicket(@Body ApiRequest<Ticket> request);

        @HTTP(method = "DELETE", path = "ticket/delete.php", hasBody = true)
        Call<ApiResponse> deleteTicket(@Body ApiRequest<DeleteParams> request);
    }
}