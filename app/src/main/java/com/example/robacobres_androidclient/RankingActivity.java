package com.example.robacobres_androidclient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robacobres_androidclient.adapters.MyItemsAdapter;
import com.example.robacobres_androidclient.adapters.MyRankingAdapter;
import com.example.robacobres_androidclient.callbacks.PartidasCallback;
import com.example.robacobres_androidclient.models.GameCharacter;
import com.example.robacobres_androidclient.models.Ranking;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity implements PartidasCallback {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<Ranking> rank;
    List<Ranking> restojugadores;

    TextView MaxPuntuacionUsuario;
    TextView OroUsername;
    TextView OroMaxPuntu;
    TextView PlataUsername;
    TextView PlataMaxPuntu;
    TextView BronceUsername;
    TextView BronceMaxPuntu;

    Context context;
    ServiceBBDD service;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.rank=new ArrayList<>();
        this.restojugadores=new ArrayList<>();

        context=RankingActivity.this;
        service = ServiceBBDD.getInstance(context);
        MaxPuntuacionUsuario = findViewById(R.id.best_score);

        //oro
        OroUsername = findViewById(R.id.oro_nombre);
        OroMaxPuntu = findViewById(R.id.oro_puntuacion);

        //plata
        PlataUsername = findViewById(R.id.plata_nombre);
        PlataMaxPuntu = findViewById(R.id.plata_puntuacion);

        //bronce
        BronceUsername = findViewById(R.id.bronce_nombre);
        BronceMaxPuntu = findViewById(R.id.bronce_puntuacion);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyRankingAdapter(context, restojugadores);
        recyclerView.setAdapter(mAdapter);
        UpdatePuntuacionMax();
        UpdateRanking();

    }

    public void UpdatePuntuacionMax(){
        progressBar.setVisibility(View.VISIBLE);
        service.getMaxPuntuacion(this);
    }

    public void UpdateRanking(){
        progressBar.setVisibility(View.VISIBLE);
        service.getRanking(this);
    }


    public void onClickBotonRetroceder(View V){
        finish();
    }

    public void onClickBotonCompartir(View V){
        progressBar.setVisibility(View.VISIBLE);
        service.getMailRanking(this);
    }

    @Override
    public void onChargePuntuacionMax(String punct) {
        MaxPuntuacionUsuario.setText(punct);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onChargeRanking(List<Ranking> ranking) {
        if (ranking == null || ranking.isEmpty()) {
            Toast.makeText(context, "No hay datos disponibles", Toast.LENGTH_SHORT).show();
        }
        else{
            if (ranking.size() > 0) {
                OroUsername.setText(ranking.get(0).getUsername());
                OroMaxPuntu.setText(String.valueOf(ranking.get(0).getMaxpuntuacion()));
            }
            if (ranking.size() > 1) {
                PlataUsername.setText(ranking.get(1).getUsername());
                PlataMaxPuntu.setText(String.valueOf(ranking.get(1).getMaxpuntuacion()));
            }
            if (ranking.size() > 2) {
                BronceUsername.setText(ranking.get(2).getUsername());
                BronceMaxPuntu.setText(String.valueOf(ranking.get(2).getMaxpuntuacion()));
            }
            if (ranking.size() > 3) {
                List<Ranking> recycledlist = new ArrayList<>(ranking.subList(3, ranking.size()));
                restojugadores.clear();
                restojugadores.addAll(recycledlist);
                mAdapter.notifyDataSetChanged();
            }

        }
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

}