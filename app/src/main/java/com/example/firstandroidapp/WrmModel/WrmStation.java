package com.example.firstandroidapp.WrmModel;

import java.io.Serializable;
import java.util.ArrayList;

public class WrmStation implements Serializable {
    public final String id;
    public final Location location;
    public ArrayList<String> bikes;

    public WrmStation(String id, Location location, ArrayList<String> bikes) {
        this.id = id;
        this.location = location;
        this.bikes = bikes;
    }
}
