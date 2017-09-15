package com.example.meire.agendatarefas.api;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
       OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100000, TimeUnit.MILLISECONDS).readTimeout(10000, TimeUnit.MILLISECONDS)
               .build();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()).client(client)
                    .build();
        }
        return retrofit;
    }
}
