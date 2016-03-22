package com.imagefinder.app.io;

import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class RestClient {

    private static final String BASE_URL = "https://api.flickr.com/services/rest/";
    private RestServices restClient;

    private RestClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.client().interceptors().add(logging);

        restClient = retrofit.create(RestServices.class);
    }

    private RestServices getRestClient() {
        return restClient;
    }

    public static RestServices build() {
        return new RestClient().getRestClient();
    }
}
