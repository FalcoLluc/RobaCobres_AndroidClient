package com.example.robacobres_androidclient.adapters;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.robacobres_androidclient.ItemsActivity;
import com.example.robacobres_androidclient.MultiActivity;
import com.example.robacobres_androidclient.R;
import com.example.robacobres_androidclient.callbacks.CharacterCallback;
import com.example.robacobres_androidclient.callbacks.ItemCallback;
import com.example.robacobres_androidclient.models.GameCharacter;
import com.example.robacobres_androidclient.models.Item;
import com.example.robacobres_androidclient.services.ServiceBBDD;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Item> items;
    private List<GameCharacter> characters;
    private List<Object> combinedList;
    private Context context;
    private ServiceBBDD service;
    private String username;
    private ItemCallback itemCallback;
    private CharacterCallback characterCallback;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtIdObj;
        public TextView txtNameItem;
        public TextView txtPriceItem;
        public ImageView icon;
        public Button comprar;


        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtIdObj = (TextView) v.findViewById(R.id.itemId);
            txtNameItem = (TextView) v.findViewById(R.id.userName);
            txtPriceItem = (TextView) v.findViewById(R.id.comentario);
            icon=(ImageView) v.findViewById(R.id.icon);
            comprar = (Button) v.findViewById(R.id.Comprar);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context,List<Object> combinedList , String username, ItemCallback _itemCallback, CharacterCallback _characterCallback) {
        this.context = context;
        this.username = username;
        this.service = ServiceBBDD.getInstance(context);
        this.itemCallback=_itemCallback;
        this.characterCallback=_characterCallback;
        this.combinedList = combinedList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Object obj = combinedList.get(position);

        if (obj instanceof Item) {
            Item item = (Item) obj;
            holder.txtIdObj.setText(item.getId());
            holder.txtNameItem.setText(item.getName());
            holder.txtPriceItem.setText(String.valueOf(item.getCost()));

            Glide.with(holder.icon.getContext())
                    .load(item.getItem_url())
                    .into(holder.icon);

            holder.comprar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    buy(item);
                }
            });

        } else if (obj instanceof GameCharacter) {
            GameCharacter character = (GameCharacter) obj;
            holder.txtIdObj.setText("");
            holder.txtNameItem.setText(character.getName());
            holder.txtPriceItem.setText(String.valueOf(character.getCost()));

            Glide.with(holder.icon.getContext())
                    .load(character.getCharacter_url())
                    .into(holder.icon);

            holder.comprar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    buy(character);
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return combinedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (combinedList.get(position) instanceof Item) {
            return 0; // Tipus per a Item
        } else if (combinedList.get(position) instanceof GameCharacter) {
            return 1; // Tipus per a GameCharacter
        }
        return -1;
    }

    public void buy(Object itemcharacter){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.confirm_purchase);

        Button buttonCancel = dialog.findViewById(R.id.ButtonCancel);
        Button buttonDelete = dialog.findViewById(R.id.ButtonComprar);

        // Si realmente no se quiere comprar
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Si se confirma que quiere comprar
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemcharacter instanceof Item){
                    Item item = (Item) itemcharacter;
                    service.userBuysItem(item.getName(), itemCallback);
                }
                else if (itemcharacter instanceof GameCharacter){
                    GameCharacter character = (GameCharacter) itemcharacter;
                    service.userBuysCharacter(character.getName(), characterCallback);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
