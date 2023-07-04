package com.fx.srtm.pojo;

/**
 *
 * @author pscha
 */
public class PngPos {

    private int lon;
    private int lat;

    public PngPos(int lon, int lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }
}
