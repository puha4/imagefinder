package com.imagefinder.app.io;

import com.imagefinder.app.model.FlickrPhotos;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RestServices {
    @GET("?method=flickr.auth.getToken&format=json&nojsoncallback=1")
    Call<String> getToken(
            @Query("api_key") String apiKey,
            @Query("frob") String frob,
            @Query("api_sig") String apiSig
    );

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrPhotos> getPhotos(
            @Query("api_key") String apiKey,
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );
}
