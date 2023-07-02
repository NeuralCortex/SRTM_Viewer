/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.pojo;

import com.fx.srtm.painter.SrtmRasterPainter;
import java.awt.Color;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author pscha
 */
public class Topo {
    
    private final BooleanProperty active = new SimpleBooleanProperty();
    private final StringProperty fileName = new SimpleStringProperty();
    private final SrtmRasterPainter srtmRasterPainter;
    private final Color color;
    
    public Topo(String fileName, boolean active, SrtmRasterPainter srtmRasterPainter,Color color) {
        setFileName(fileName);
        setActive(active);
        this.srtmRasterPainter = srtmRasterPainter;
        this.color=color;
    }
    
    public String getFileName() {
        return fileName.get();
    }
    
    public void setFileName(String value) {
        fileName.set(value);
    }
    
    public StringProperty fileNameProperty() {
        return fileName;
    }
    
    public boolean isActive() {
        return active.get();
    }
    
    public void setActive(boolean value) {
        active.set(value);
    }
    
    public BooleanProperty activeProperty() {
        return active;
    }

    public SrtmRasterPainter getSrtmRasterPainter() {
        return srtmRasterPainter;
    }

    public Color getColor() {
        return color;
    }
}
