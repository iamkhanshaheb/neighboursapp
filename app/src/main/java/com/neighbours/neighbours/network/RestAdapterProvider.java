package com.neighbours.neighbours.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neighbours.neighbours.config.AppConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapterProvider {

    private static final RestAdapterProvider instance = new RestAdapterProvider();


    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();


    private final GoromchaApi restApiForRetrofit = new Retrofit.Builder()
            .baseUrl(AppConfig.END_POINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GoromchaApi.class);

    private RestAdapterProvider() {
    }

    public static RestAdapterProvider getProvider() {
        return instance;
    }

    public GoromchaApi getRestApiForRetrofit() {
        return restApiForRetrofit;
    }
}
