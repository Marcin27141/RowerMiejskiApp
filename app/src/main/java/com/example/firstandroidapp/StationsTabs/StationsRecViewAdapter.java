package com.example.firstandroidapp.StationsTabs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<WrmStation> stations = new ArrayList<>();
    private ArrayList<String> likedStationsIds = new ArrayList<>();

    public StationsRecViewAdapter(Context context) {
        this.context = context;
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

        holder.starIconCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
                if (isChecked) {
                    dbHelper.addLikedStation(holder.stationIdTxt.getText().toString());
                }
                else
                {
                    dbHelper.removeLikedStation(holder.stationIdTxt.getText().toString());
                }
            }
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
