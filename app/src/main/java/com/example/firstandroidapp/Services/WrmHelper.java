package com.example.firstandroidapp.Services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.Location;
import com.example.firstandroidapp.WrmModel.WrmStation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WrmHelper {
    private static ArrayList<WrmStation> _wrmStationsList = new ArrayList<>();
    private final static String CELLS_SEPARATOR = ", ";

    public static void loadWrmStationsList(Consumer<ArrayList<WrmStation>> postHandler) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        ArrayList<WrmStation> result = new ArrayList<>();

        executor.execute(() -> {
            try {
                populateStationsList(result);
                _wrmStationsList = result;
            } catch (IOException ignored) {
            }
            handler.post(() -> {
                postHandler.accept(result);
            });
        });
    }

    public static ArrayList<WrmStation> getWrmStations() {
        return _wrmStationsList;
    }

    public static ArrayList<String> getLikedWrmStationsIds(Context context) {
        try(DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            return dbHelper.getLikedStationsIds();
        }
    }

    public static ArrayList<WrmStation> getLikedWrmStations(Context context) {
        return getLikedWrmStations(context, _wrmStationsList);
    }

    public static ArrayList<WrmStation> getLikedWrmStations(Context context, ArrayList<WrmStation> stations) {
        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            ArrayList<String> liked = dbHelper.getLikedStationsIds();
            return stations.stream().filter(s -> liked.contains(s.id)).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    private static void populateStationsList(ArrayList<WrmStation> result) throws IOException {
        Document doc = Jsoup.connect("https://wroclawskirower.pl/mapa-stacji/").get();
        Element divContainer = doc.getElementsByClass("text station_list col-xs-12").first();
        Element table = divContainer.select("table").first();

        if (table != null) {
            WrmGenerator stationGenerator = new WrmGenerator();
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size() - 1; i++) {
                Element row = rows.get(i);
                Elements cells = row.select("td");
                result.add(stationGenerator.generateWrmStation(cells));
            }
        }
    }

    private static class WrmGenerator {
        public WrmStation generateWrmStation(Elements cells) {
            String stationId = getStationIdFromCells(cells);
            Location location = getLocationFromCells(cells);
            ArrayList<String> bikes = getBikesFromCells(cells);
            return new WrmStation(stationId, location, bikes);
        }

        private String getStationIdFromCells(Elements cells) {
            return cells.get(0).text();
        }

        private ArrayList<String> getBikesFromCells(Elements cells) {
            String bikesString = cells.get(5).text();
            String[] bikesArray = bikesString.length() == 0 ? new String[0] : bikesString.split(CELLS_SEPARATOR);
            return new ArrayList<>(Arrays.asList(bikesArray));
        }

        private Location getLocationFromCells(Elements cells) {
            String locationName = cells.get(1).text();
            String[] coordinates = cells.get(3).text().split(CELLS_SEPARATOR);
            float xCoordinate = Float.parseFloat(coordinates[0]);
            float yCoordinate = Float.parseFloat(coordinates[1]);
            return new Location(locationName, xCoordinate, yCoordinate);
        }
    }
}


