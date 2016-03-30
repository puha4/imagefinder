package com.imagefinder.app.io;

import com.imagefinder.app.model.AuthUser;
import com.imagefinder.app.model.FlickrPhotos;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RestServices {
    @GET("?method=flickr.auth.getToken&format=json&nojsoncallback=1")
    Call<AuthUser> getToken(
            @Query("api_key") String apiKey,
            @Query("frob") String frob,
            @Query("api_sig") String apiSig
    );

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrPhotos> getPhotosByGeo(
            @Query("api_key") String apiKey,
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrPhotos> searchPhotos(
            @Query("api_key") String apiKey,
            @Query("text") String searchKey
    );
}
