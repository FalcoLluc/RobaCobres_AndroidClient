package com.example.robacobres_androidclient;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;

import com.example.robacobres_androidclient.callbacks.UnityCallback;
import com.example.robacobres_androidclient.services.ServiceBBDD;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class UnityHostActivity extends UnityPlayerActivity implements UnityCallback {
    ServiceBBDD serviceREST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceREST=ServiceBBDD.getInstance(this);

        // If you need to set up anything specific, do it here before starting Unity
        // You can pass any data, like extra information or settings, if needed.
    }

    // Expose this method to Unity to send data to the server
    public void sendStateToServer(String itemsStateText) {
        // Call the Retrofit method to send data
        serviceREST.sendStateToServer(itemsStateText,this);
    }

    // Expose this method to Unity to close the activity
    public void exitUnity() {
        finish();  // This will close the UnityHostActivity and return to the previous activity
    }

    //CALLBACKS
    @Override
    public void onReturnHola() {
        UnityPlayer.UnitySendMessage("AndroidBridge", "OnServerResponse", "ECO HOLA");
    }
}
