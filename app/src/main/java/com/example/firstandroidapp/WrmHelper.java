package com.example.firstandroidapp;

import android.os.Handler;
import android.os.Looper;

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

public class WrmHelper {
    private static ArrayList<WrmStation> _wrmStationsList = new ArrayList<>();

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

    private static void populateStationsList(ArrayList<WrmStation> result) throws IOException {
        Document doc = Jsoup.connect("https://wroclawskirower.pl/mapa-stacji/").get();
        Element divContainer = doc.getElementsByClass("text station_list col-xs-12").first();
        Element table = divContainer.select("table").first();

        if (table != null) {
            Elements rows = table.select("tr");
            for (int i = 1; i < rows.size() - 1; i++) {
                Element row = rows.get(i);
                Elements cells = row.select("td");
                result.add(generateWrmStation(cells));
            }
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
        ArrayList<String> bikes = new ArrayList<>(Arrays.asList(bikesArray));

        return new WrmStation(stationId, location, bikes);
    }
}


