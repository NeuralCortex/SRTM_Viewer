package com.fx.srtm.pojo;

import java.awt.Color;
import java.awt.Rectangle;

/**
 *
 * @author pscha
 */
public class RectangleInfo {

    private Rectangle rectangle;
    private Color color;
    private short height;

    public RectangleInfo(Rectangle rectangle, Color color, short height) {
        this.rectangle = rectangle;
        this.color = color;
        this.height = height;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "RectangleInfo{" + "rectangle=" + rectangle + ", color=" + color + ", height=" + height + '}';
    }
}
