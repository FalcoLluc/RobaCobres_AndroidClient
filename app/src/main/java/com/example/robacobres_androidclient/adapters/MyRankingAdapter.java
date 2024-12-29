package com.example.robacobres_androidclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.robacobres_androidclient.R;
import com.example.robacobres_androidclient.models.GameCharacter;
import com.example.robacobres_androidclient.models.Item;
import com.example.robacobres_androidclient.models.Ranking;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.util.List;

public class MyRankingAdapter extends RecyclerView.Adapter<MyRankingAdapter.ViewHolder>{
    private List<Ranking> rank;
    private Context context;
    private ServiceBBDD service;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombrejugador;
        public TextView puntosjugador;
        public ImageView pointsicon;


        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            nombrejugador = (TextView) v.findViewById(R.id.nombrejugador);
            puntosjugador = (TextView) v.findViewById(R.id.puntosjugador);
            pointsicon=(ImageView) v.findViewById(R.id.points);
        }
    }


    public MyRankingAdapter(Context context, List<Ranking> myDataset) {
        this.context = context;
        this.service = ServiceBBDD.getInstance(context);
        rank = myDataset;
    }


    @Override
    public MyRankingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_jugador, parent, false);

        MyRankingAdapter.ViewHolder vh = new MyRankingAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(MyRankingAdapter.ViewHolder holder, final int position) {
        final Ranking r = rank.get(position);
        holder.nombrejugador.setText(r.getUsername());
        holder.puntosjugador.setText(String.valueOf(r.getMaxpuntuacion()));
        holder.pointsicon.setImageResource(R.drawable.puntos);
    }


    @Override
    public int getItemCount() {
        return rank.size();
    }

}
