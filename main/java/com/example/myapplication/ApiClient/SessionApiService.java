package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.Session;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SessionApiService {
    @GET("sessions")
    Call<List<Session>> getByPlanId(
            @Query("type") String type,
            @Query("planId") int planId
    );

    @POST("sessions")
    Call<Session> create(@Body Session session);

    @DELETE("sessions/{id}")
    Call<Void> delete(@Path("id") int id);
}
