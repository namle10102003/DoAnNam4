package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.Set;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SetApiService {
    @GET("sets")
    Call<List<Set>> getBySessionId(@Query("sessionId") int sessionId);

    @POST("sets")
    Call<Set> create(@Body Set set);

    @PUT("sets")
    Call<Set> update(@Body Set set);

    @DELETE("sets/{id}")
    Call<Void> delete(@Path("id") int id);
}
