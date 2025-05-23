package com.example.myapplication.ApiClient;

import com.example.myapplication.Domain.BodyTrack;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BodyTrackApiService {
    @GET("bodyTracks")
    Call<List<BodyTrack>> getByUserId(
            @Query("type") String type,
            @Query("userId") int userId
    );

    @POST("bodyTracks")
    Call<BodyTrack> create(@Body BodyTrack bodyTrack);

    @PUT("bodyTracks")
    Call<BodyTrack> update(@Body BodyTrack bodyTrack);

    @DELETE("bodyTracks/{id}")
    Call<Void> delete(@Path("id") int id);
}
