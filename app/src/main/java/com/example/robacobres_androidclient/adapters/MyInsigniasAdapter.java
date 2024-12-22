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
import com.example.robacobres_androidclient.models.Insignia;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.util.List;

public class MyInsigniasAdapter extends RecyclerView.Adapter<MyInsigniasAdapter.ViewHolder> {
    private List<Insignia> insignias;
    private Context context;
    private ServiceBBDD service;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtInsigniaItem;
        public ImageView icon;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtInsigniaItem = (TextView) v.findViewById(R.id.insigniaName);
            icon=(ImageView) v.findViewById(R.id.icon);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyInsigniasAdapter(Context context, List<Insignia> myDataset) {
        this.context = context;
        this.service = ServiceBBDD.getInstance(context);
        insignias = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_myinsignias_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Insignia i = insignias.get(position);
        holder.txtInsigniaItem.setText("Name: "+i.getName());

        Glide.with(holder.icon.getContext())
                .load(i.getUrl())
                .into(holder.icon);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return insignias.size();
    }

    public void updateInsignias(List<Insignia> insignias) {
        this.insignias = insignias; // Actualitza la llista
        notifyDataSetChanged(); // Notifica al RecyclerView que actualitzi les dades
    }
}
