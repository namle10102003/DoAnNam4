package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.Plan;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlanApiService {
    @GET("plans")
    Call<List<Plan>> getByUserId(
            @Query("userId") int userId
    );

    @POST("plans")
    Call<Plan> create(@Body Plan plan);

    @PUT("plans")
    Call<Plan> update(@Body Plan plan);

    @DELETE("plans/{id}")
    Call<Void> delete(@Path("id") int id);
}
