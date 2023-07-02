package com.fx.srtm.pojo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Dir {
    
    private final BooleanProperty active = new SimpleBooleanProperty();
    private final String path;
    private final Color color;
    private List<Topo> listTopo=new ArrayList<>();
    
    public Dir(String path, Color color, boolean active) {
        this.path = path;
        this.color = color;
        setActive(active);
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
    
    public String getPath() {
        return path;
    }
    
    public Color getColor() {
        return color;
    }

    public List<Topo> getListTopo() {
        return listTopo;
    }
}
