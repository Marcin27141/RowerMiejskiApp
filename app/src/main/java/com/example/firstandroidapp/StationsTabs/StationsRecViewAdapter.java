package com.example.firstandroidapp.StationsTabs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstandroidapp.ChooseBikeActivity;
import com.example.firstandroidapp.DatabaseHelper;
import com.example.firstandroidapp.R;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;

public class StationsRecViewAdapter extends RecyclerView.Adapter<StationsRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OnStationLikedListener> onStationLikedListeners = new ArrayList<>();
    private ArrayList<WrmStation> stations = new ArrayList<>();
    private ArrayList<String> likedStationsIds = new ArrayList<>();

    public StationsRecViewAdapter(Context context) {
        this.context = context;
    }

    public void addOnStationLikedListener(OnStationLikedListener listener) {
        this.onStationLikedListeners.add(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.stationLocationTxt.setText(stations.get(position).location.name);
        holder.stationIdTxt.setText((stations.get(position).id));
        holder.parent.setOnClickListener(view -> {
            showChooseBikeActivity(position);
        });

        holder.starIconCheckBox.setOnClickListener(v -> {
            // you might keep a reference to the CheckBox to avoid this class cast
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
                }

                for (OnStationLikedListener listener : onStationLikedListeners)
                    listener.onStationLiked(stations.get(position), isChecked);
        });


        holder.starIconCheckBox.setChecked(likedStationsIds.contains(holder.stationIdTxt.getText().toString()));
    }

    private void showChooseBikeActivity(int stationsPosition) {
        Intent intent = new Intent(context, ChooseBikeActivity.class);
        intent.putExtra("SERIALIZED_STATION", stations.get(stationsPosition));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public void setStations(ArrayList<WrmStation> stations) {
        this.stations = stations;
        likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView parent;
        private TextView stationLocationTxt, stationIdTxt;
        private CheckBox starIconCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            stationLocationTxt = itemView.findViewById(R.id.stationLocation);
            stationIdTxt = itemView.findViewById(R.id.stationId);
            starIconCheckBox = itemView.findViewById(R.id.star_icon);
        }
    }
}
