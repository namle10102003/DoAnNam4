package com.example.myapplication.Service;

import android.util.Log;

import com.example.myapplication.ApiClient.ApiClient;
import com.example.myapplication.ApiClient.BodyTrackApiService;
import com.example.myapplication.Domain.BodyTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BodyTrackService {
    private final BodyTrackApiService apiService;

    public interface TrackDataListener {
        void onTracksLoaded(List<BodyTrack> tracks);
        void onError(String message);
    }

    public BodyTrackService() {
        apiService = ApiClient.getClient().create(BodyTrackApiService.class);
    }

    public void getTracksByUserId(int userId, TrackDataListener listener) {
        Call<List<BodyTrack>> call = apiService.getByUserId("userId", userId);
        call.enqueue(new Callback<List<BodyTrack>>() {
            @Override
            public void onResponse(Call<List<BodyTrack>> call, Response<List<BodyTrack>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BodyTrack> result = response.body();
                    Log.d("BodyTrack", result.get(0).toString());
                    listener.onTracksLoaded(getSortedBodyTracks(result));
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BodyTrack>> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createTrack(BodyTrack track, TrackDataListener listener) {
        Call<BodyTrack> call = apiService.create(track);
        call.enqueue(new Callback<BodyTrack>() {
            @Override
            public void onResponse(Call<BodyTrack> call, Response<BodyTrack> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BodyTrack reusult = response.body();
                    List<BodyTrack> list = new ArrayList<>();
                    list.add(reusult);
                    listener.onTracksLoaded(list);
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BodyTrack> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateTrack(BodyTrack track, TrackDataListener listener) {
        Call<BodyTrack> call = apiService.update(track);
        call.enqueue(new Callback<BodyTrack>() {
            @Override
            public void onResponse(Call<BodyTrack> call, Response<BodyTrack> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BodyTrack reusult = response.body();
                    List<BodyTrack> list = new ArrayList<>();
                    list.add(reusult);
                    listener.onTracksLoaded(list);
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BodyTrack> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteTrack(int trackId, TrackDataListener listener) {
        Call<Void> call = apiService.delete(trackId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onTracksLoaded(new ArrayList<>());
                }
                else {
                    listener.onError("Server returned error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError("Network error: " + t.getMessage());
            }
        });
    }

    private List<BodyTrack> getSortedBodyTracks(List<BodyTrack> bodyTrackList) {
        bodyTrackList.sort((t1, t2) -> t2.Date.compareTo(t1.Date));
        return bodyTrackList;
    }
}
