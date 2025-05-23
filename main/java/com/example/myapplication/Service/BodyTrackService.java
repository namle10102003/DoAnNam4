package com.example.myapplication.Service;

import com.example.myapplication.ApiClient.BodyTrackApiService;
import com.example.myapplication.Domain.BodyTrack;

import java.util.List;

public class BodyTrackService {
    private BodyTrackApiService apiService;

    public BodyTrackService() {
        apiService = new BodyTrackApiService();
    }

    public List<BodyTrack> getByUserId(int userId) {
        return getSortedBodyTracks(apiService.getByUserId(userId));
    }

    public BodyTrack getById(int id) {
        return apiService.getById(id);
    }

    public void createBodyTrack(BodyTrack track) {
        apiService.createBodyTrack(track);
    }

    public void updateBodyTrack(BodyTrack track) {
        apiService.updateBodyTrack(track);
    }

    public void deleteBodyTrack(int id) {
        apiService.deleteBodyTrack(id);
    }

    private List<BodyTrack> getSortedBodyTracks(List<BodyTrack> bodyTrackList) {
        bodyTrackList.sort((t1, t2) -> t2.Date.compareTo(t1.Date));
        return bodyTrackList;
    }
}
