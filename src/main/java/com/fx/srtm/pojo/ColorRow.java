package com.fx.srtm.pojo;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class ColorRow implements Comparable<ColorRow>{

    private final int idx;
    private Double percent;
    private final ObjectProperty<Color> color;

    public ColorRow(int idx,double percent, Color color) {
        this.idx=idx;
        this.percent = percent;
        this.color = new SimpleObjectProperty<>(color);
    }

    public Double getPercent() {
        return percent*100;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Color getColor() {
        return color.get();
    }
    
    public void setColor(Color color){
        this.color.set(color);
    }
    
    public ObjectProperty<Color> colorProperty(){
        return this.color;
    }
    
    public double getRed(){
        return color.get().getRed()*255;
    }
    
    public double getGreen(){
        return color.get().getGreen()*255;
    }
    
    public double getBlue(){
        return color.get().getBlue()*255;
    }

    public int getIdx() {
        return idx;
    }
    
    

    @Override
    public int compareTo(ColorRow o) {
        return this.getPercent().compareTo(o.getPercent());
    }
}
