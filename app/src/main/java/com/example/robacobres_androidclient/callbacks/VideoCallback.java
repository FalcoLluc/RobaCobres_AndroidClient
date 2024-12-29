package com.example.robacobres_androidclient.callbacks;

import com.example.robacobres_androidclient.models.Video;

import java.util.List;

public interface VideoCallback {
    void onVideoCallback(List<Video> objects);
    void onMessage(String message);
}
