package com.example.robacobres_androidclient.callbacks;

import com.example.robacobres_androidclient.models.Insignia;

import java.util.List;

public interface InsigniasCallback {
    void onInsigniaCallback(List<Insignia> objects);
    void onError(String errorMessage);
    void onMessage(String errorMessage);
}
