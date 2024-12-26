package com.example.robacobres_androidclient;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robacobres_androidclient.adapters.MyCharacterAdapter;
import com.example.robacobres_androidclient.adapters.MyItemsAdapter;
import com.example.robacobres_androidclient.callbacks.CharacterCallback;
import com.example.robacobres_androidclient.models.GameCharacter;
import com.example.robacobres_androidclient.models.Item;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.util.ArrayList;
import java.util.List;

public class MyCharactersActivity extends AppCompatActivity implements CharacterCallback {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Context context;
    ServiceBBDD serviceREST;
    List<GameCharacter> obtainedCharacters;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_characters);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.obtainedCharacters=new ArrayList<>();

        context=MyCharactersActivity.this;

        //INSTANCIA Service
        serviceREST=ServiceBBDD.getInstance(context);

        //RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyCharacterAdapter(context,obtainedCharacters);
        recyclerView.setAdapter(mAdapter);
        progressBar = findViewById(R.id.progressBar);

        getMyCharacters();
    }
    public void getMyCharacters(){
        progressBar.setVisibility(View.VISIBLE);
        serviceREST.getMyCharacters(this);
    }

    @Override
    public void onPurchaseOk(String idItem) {

    }

    @Override
    public void onCharacterCallback(List<GameCharacter> objects) {
        // Actualizar la lista de characters y notificar al adapter
        obtainedCharacters.clear();
        obtainedCharacters.addAll(objects);
        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(context,"Error: "+errorMessage,Toast.LENGTH_SHORT).show();
    }

    public void onClickBotonRetroceder(View V){
        finish();
    }
}