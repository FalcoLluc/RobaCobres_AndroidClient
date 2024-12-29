package com.example.robacobres_androidclient.callbacks;

import com.example.robacobres_androidclient.models.Ranking;

import java.util.List;

public interface PartidasCallback {
    void onChargePuntuacionMax(String punct);
    void onChargeRanking(List<Ranking> ranking);
    void onMessage(String message);
}
