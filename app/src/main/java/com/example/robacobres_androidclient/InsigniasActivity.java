package com.example.robacobres_androidclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robacobres_androidclient.adapters.MyInsigniasAdapter;
import com.example.robacobres_androidclient.callbacks.InsigniasCallback;
import com.example.robacobres_androidclient.models.Insignia;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.util.List;

public class InsigniasActivity extends AppCompatActivity implements InsigniasCallback {
    List<Insignia> insignia;
    ServiceBBDD serviceREST;
    private RecyclerView recyclerView;
    private Button btnClose;
    private MyInsigniasAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnSend;
    private EditText badgeName;
    private EditText badgeUrl;
    Context context;
    String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insignias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        serviceREST = ServiceBBDD.getInstance(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        btnSend = findViewById(R.id.savebtn);
        badgeName = findViewById(R.id.nameText);
        badgeUrl = findViewById(R.id.urltext);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        context=InsigniasActivity.this;
        this.insignia = (List<Insignia>) getIntent().getSerializableExtra("insignias");
        this.name = getIntent().getStringExtra("name");
        mAdapter = new MyInsigniasAdapter(context, insignia);
        recyclerView.setAdapter(mAdapter);
    }

    public void onClickSave(View v){
        Insignia i1 = new Insignia(badgeName.getText().toString().trim(), badgeUrl.getText().toString().trim());
        badgeName.setText("");
        badgeUrl.setText("");
        serviceREST.putInsignia(this,name,i1);
    }

    public void onClickBotonRetroceder(View v){
        this.finish();
    }

    @Override
    public void onError(String message){

    }

    @Override
    public void onInsigniaCallback(List<Insignia> objects) {
        mAdapter.updateInsignias(objects);
    }

    @Override
    public void onMessage(String errorMessage){
        Toast.makeText(InsigniasActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
