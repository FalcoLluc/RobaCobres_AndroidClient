package com.example.robacobres_androidclient;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;

import com.example.robacobres_androidclient.callbacks.UnityCallback;
import com.example.robacobres_androidclient.models.PartidaActual;
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

    public void sendAddCobre(int cobre){
        // Call the Retrofit method to send data
        serviceREST.sendAddCobre(cobre);
    }

    public void sendAddPuntosTotales(int cobre){
        // Call the Retrofit method to send data
        serviceREST.sendAddCobreTotal(cobre);
    }

    //PARTIDA
    public void sendSaveGame(String levelString,int level,int cobreActual,int cobreTotal){
        PartidaActual p=new PartidaActual();
        p.setTxt(levelString);
        p.setNivell(level);
        p.setCobreActual(cobreActual);
        p.setCobreTotal(cobreTotal);
        // Call the Retrofit method to send data
        serviceREST.sendSave(p);
    }
    // Method to close the Unity application
    public void exitUnity() {
        finish();
    }
    //CALLBACKS
    public void requestGame(){
        serviceREST.getLevel(this);
    }


    //AQUEST ES DE DEMO
    @Override
    public void onReturnHola() {
        UnityPlayer.UnitySendMessage("AndroidBridge", "OnServerResponse", "ECO HOLA");
    }

    @Override
    public void onReturnLevel(PartidaActual p) {
        //HO SEPARA
        UnityPlayer.UnitySendMessage("AndroidBridge", "OnLevelResponse", p.getTxt()+"|"+p.getNivell()+"|"+p.getCobreActual()+"|"+p.getCobreTotal());
    }

    @Override
    public void onReturnNoExistingLevel() {
        UnityPlayer.UnitySendMessage("AndroidBridge", "OnLevelResponseNoExisting", "");
    }
}
