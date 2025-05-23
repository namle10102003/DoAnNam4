package com.example.myapplication.ApiClient;

import android.telecom.Call;

import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.example.myapplication.Domain.BodyTrack;

import java.util.ArrayList;
import java.util.List;

public class BodyTrackApiService {
    private MockingDatabase mockingDatabase;

    public BodyTrackApiService() {
        mockingDatabase = MockingDatabase.getInstance();
    }

    public List<BodyTrack> getByUserId(int userId) {
        List<BodyTrack> result = new ArrayList<>();

        for(BodyTrack item : mockingDatabase.bodyTracks) {
            if (item.UserId == userId)
                result.add(item);
        }

        return result;
    }

    public BodyTrack getById(int id) {
        for(BodyTrack item : mockingDatabase.bodyTracks) {
            if(item.Id == id)
                return item;
        }

        return null;
    }

    public void createBodyTrack(BodyTrack track) {
        track.Id = mockingDatabase.bodyTracks.size();
        mockingDatabase.bodyTracks.add(track);
    }

    public void updateBodyTrack(BodyTrack track) {
        BodyTrack oldTrack = getById(track.Id);

        oldTrack.Height = track.Height;
        oldTrack.Weight = track.Weight;
    }

    public void deleteBodyTrack(int id) {
        mockingDatabase.bodyTracks.removeIf(item -> item.Id == id);
    }
}
