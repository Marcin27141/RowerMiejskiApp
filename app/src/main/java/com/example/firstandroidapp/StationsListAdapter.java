package com.example.firstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;

public abstract class StationsListAdapter extends RecyclerView.Adapter<StationsListAdapter.StationViewHolder> {

    protected Context context;
    protected ArrayList<WrmStation> listItems;

    public StationsListAdapter(Context context, ArrayList<WrmStation> stations) {
        this.context = context;
        this.listItems = stations;
    }

    abstract void onLikedListChanged();
    abstract boolean isStationLiked(WrmStation station);
    abstract void updateList();

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_card_item, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        holder.stationLocationTxt.setText(listItems.get(position).location.name);
        holder.stationIdTxt.setText((listItems.get(position).id));
        holder.parent.setOnClickListener(view -> {
            showChooseBikeActivity(position);
        });

        holder.starIconCheckBox.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox)v).isChecked();

            String stationId = holder.stationIdTxt.getText().toString();

            try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
                if (isChecked) {
                    dbHelper.addLikedStation(stationId);
                }
                else
                {
                    dbHelper.removeLikedStation(stationId);
                }
                //likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
                updateList();
            }

            //getParentFragmentManager().setFragmentResult("LikedListChanged", Bundle.EMPTY);
            onLikedListChanged();
        });


        //holder.starIconCheckBox.setChecked(likedStationsIds.contains(holder.stationIdTxt.getText().toString()));
        holder.starIconCheckBox.setChecked(isStationLiked(listItems.get(position)));
    }



    private void showChooseBikeActivity(int stationsPosition) {
        Intent intent = new Intent(context, ChooseBikeActivity.class);
        intent.putExtra("SERIALIZED_STATION", listItems.get(stationsPosition));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void updateList(ArrayList<WrmStation> stations) {
        this.listItems = stations;
        notifyDataSetChanged();
    }

    public static class StationViewHolder extends RecyclerView.ViewHolder {
        private final CardView parent;
        private final TextView stationLocationTxt, stationIdTxt;
        private final CheckBox starIconCheckBox;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            stationLocationTxt = itemView.findViewById(R.id.stationLocation);
            stationIdTxt = itemView.findViewById(R.id.stationId);
            starIconCheckBox = itemView.findViewById(R.id.star_icon);
        }
    }


}
