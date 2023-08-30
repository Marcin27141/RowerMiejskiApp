package com.example.firstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlinx.coroutines.channels.ActorKt;

public class BasicWrmActivity extends MenuBarActivity {

    private RecyclerView stationsRecView;
    private ArrayList<WrmStation> wrmStationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_wrm);

        stationsRecView = findViewById(R.id.stationsRecView);
        wrmStationsList = getWrmStationsList();
    }

    private ArrayList<WrmStation> getWrmStationsList() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        ArrayList<WrmStation> result = new ArrayList<>();

        executor.execute(() -> {
            try {
                Document doc = Jsoup.connect("https://wroclawskirower.pl/mapa-stacji/").get();
                Element divContainer = doc.getElementsByClass("text station_list col-xs-12").first();
                Element table = divContainer.select("table").first();

                if (table != null) {
                    Elements rows = table.select("tr");
                    for (int i = 1; i < rows.size() - 1; i++) {
                        Element row = rows.get(i);
                        Elements cells = row.select("td"); // Select all cells in the row
                        result.add(generateWrmStation(cells));
                    }
                }
            } catch (IOException ignored) {
            }
            handler.post(() -> {
                /*TextView resultSumText = findViewById(R.id.totalStationsNumber);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);
                resultSumText.setText(String.format(Locale.ENGLISH, "%d", result.size()));*/
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                StationsRecViewAdapter adapter = new StationsRecViewAdapter(this);
                adapter.setStations(result);
                stationsRecView.setAdapter(adapter);
                stationsRecView.setLayoutManager(new GridLayoutManager(this, 2));
            });
        });

        return result;
    }

    private static int tryParseString(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static WrmStation generateWrmStation(Elements cells) {
        String SEPARATOR = ", ";

        String stationId = cells.get(0).text();

        String locationName = cells.get(1).text();
        String[] coordinates = cells.get(3).text().split(SEPARATOR);
        float xCoordinate = Float.parseFloat(coordinates[0]);
        float yCoordinate = Float.parseFloat(coordinates[1]);
        Location location = new Location(locationName, xCoordinate, yCoordinate);

        String bikesString = cells.get(5).text();
        String[] bikesArray = bikesString.length() == 0 ? new String[0] : bikesString.split(SEPARATOR);
        ArrayList<Integer> bikes = new ArrayList<>();
        for (String bikeNumber : bikesArray) {
            bikes.add(tryParseString(bikeNumber));
        }

        return new WrmStation(stationId, location, bikes);
    }
}

class Location implements Serializable {
    public final String name;
    public final float xCoordinate;
    public final float yCoordinate;

    public Location(String name, float xCoordinate, float yCoordinate) {
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }
}

class WrmStation implements Serializable {
    public final String id;
    public final Location location;
    public final ArrayList<Integer> bikes;

    public WrmStation(String id, Location location, ArrayList<Integer> bikes) {
        this.id = id;
        this.location = location;
        this.bikes = bikes;
    }
}

class StationsRecViewAdapter extends RecyclerView.Adapter<StationsRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WrmStation> stations = new ArrayList<>();
    public StationsRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_card_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.stationLocationTxt.setText(stations.get(position).location.name);
        holder.stationIdTxt.setText((stations.get(position).id));
        holder.parent.setOnClickListener(view -> {
            //Toast.makeText(context, stations.get(position).id, Toast.LENGTH_SHORT).show();
            showChooseBikeActivity(position);
        });
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
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView parent;
        private TextView stationLocationTxt, stationIdTxt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            stationLocationTxt = itemView.findViewById(R.id.stationLocation);
            stationIdTxt = itemView.findViewById(R.id.stationId);
        }
    }
}