package com.example.myapplicationhector.network;

import retrofit2.Retrofit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.101.10:3001";

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            // Configura Gson en modo lenient
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Usa Gson con setLenient()
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitClient().create(ApiService.class);
    }
}
