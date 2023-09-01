package com.example.firstandroidapp.WrmModel;

import java.io.Serializable;

public class Location implements Serializable {
    public final String name;
    public final float xCoordinate;
    public final float yCoordinate;

    public Location(String name, float xCoordinate, float yCoordinate) {
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }
}