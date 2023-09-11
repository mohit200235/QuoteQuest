package com.example.tester;

import android.media.Image;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {

    @GET("v1/quotes")
    Call<List<Quote>>  getQuote(
//            @Query("limit") int limits,
            @Header("X-Api-Key") String key
    );

    @GET("v1/randomimage")
    Call<ResponseBody> getImage(
            @Query("category") String category,
            @Header("X-Api-Key") String key,
            @Header("Accept") String imgUrl
    );
}
