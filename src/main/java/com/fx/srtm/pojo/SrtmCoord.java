/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.pojo;

/**
 *
 * @author pscha
 */
public class SrtmCoord {

    private int lon;
    private int lat;
    
    public SrtmCoord(int lon,int lat){
        this.lon=lon;
        this.lat=lat;
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

    @Override
    public String toString() {
        return "SrtmCoordPOJO{" + "lon=" + lon + ", lat=" + lat + '}';
    }
}
