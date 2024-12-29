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

import com.example.robacobres_androidclient.adapters.VideosAdapter;
import com.example.robacobres_androidclient.callbacks.VideoCallback;
import com.example.robacobres_androidclient.models.Video;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity implements VideoCallback {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Context context;
    ServiceBBDD serviceREST;
    List<Video> obtainedVideo;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_videos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.obtainedVideo=new ArrayList<>();

        context=VideosActivity.this;

        //INSTANCIA Service
        serviceREST=ServiceBBDD.getInstance(context);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new VideosAdapter(obtainedVideo,this);
        recyclerView.setAdapter(mAdapter);
        progressBar = findViewById(R.id.progressBar);

        getMyVideos();


    }

    public void getMyVideos(){
        progressBar.setVisibility(View.VISIBLE);
        serviceREST.getMedia(this);
    }

    @Override
    public void onVideoCallback(List<Video> objects) {
        // Actualizar la lista de items y notificar al adapter
        obtainedVideo.clear();
        obtainedVideo.addAll(objects);
        mAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }
    @Override
    public void onMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    public void onClickBotonRetroceder(View V){
        finish();
    }

}