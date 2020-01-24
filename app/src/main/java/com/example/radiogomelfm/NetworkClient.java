package com.example.radiogomelfm;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkClient {

    public static final String BASE_URL = "https://ws.audioscrobbler.com";

    public static Retrofit retrofit;

    public static Retrofit getRetrofitClient(){

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
