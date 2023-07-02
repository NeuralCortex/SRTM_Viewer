/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.painter;

import com.fx.srtm.pojo.ColorRow;
import com.fx.srtm.pojo.RectangleInfo;
import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.thread.SrtmMapBoundsThread;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author pscha
 */
public class SrtmTilePainter implements Painter<JXMapViewer> {

    private Color color = Color.BLACK;
    private final boolean antiAlias = true;
    private final SrtmRaster srtmRaster;
    private final short[][] bigMap;
    private int min;
    private int max;
    private boolean showHeight;
    private boolean showAlpha;
    private boolean raster = false;
    private List<ColorRow> colors;
    private GeoPosition geoPositionSel;

    public SrtmTilePainter(SrtmRaster srtmRaster, short[][] bigMap, int min, int max) {
        this.srtmRaster = srtmRaster;
        this.bigMap = bigMap;
        this.min = min < 0 ? 0 : min;
        this.max = max;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setColor(color);
        g.setStroke(new BasicStroke(2));

        drawSrtmTile(g, map);

        g.dispose();
    }

    private void drawSrtmTile(Graphics2D g, JXMapViewer map) {

        GeoPosition geoPosition0 = new GeoPosition(srtmRaster.getCoord0().getLat(), srtmRaster.getCoord0().getLon());
        GeoPosition geoPosition1 = new GeoPosition(srtmRaster.getCoord1().getLat(), srtmRaster.getCoord1().getLon());
        GeoPosition geoPosition2 = new GeoPosition(srtmRaster.getCoord2().getLat(), srtmRaster.getCoord2().getLon());
        GeoPosition geoPosition3 = new GeoPosition(srtmRaster.getCoord3().getLat(), srtmRaster.getCoord3().getLon());

        Point2D pt0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
        Point2D pt1 = map.getTileFactory().geoToPixel(geoPosition1, map.getZoom());
        Point2D pt2 = map.getTileFactory().geoToPixel(geoPosition2, map.getZoom());
        Point2D pt3 = map.getTileFactory().geoToPixel(geoPosition3, map.getZoom());

        Point2D point2D0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
        Point2D point2D1 = map.getTileFactory().geoToPixel(geoPosition1, map.getZoom());
        Point2D point2D3 = map.getTileFactory().geoToPixel(geoPosition3, map.getZoom());

        int resHor = (int) (pt1.getX() - pt0.getX());
        int resVer = (int) (pt3.getY() - pt0.getY());

        int size = 1201;

        int horStep = (int) (size / (double) resHor);
        int verStep = (int) (size / (double) resVer);

        /*
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int blockSize = size / cpuCount;

        SrtmTileThread threadArray[] = new SrtmTileThread[cpuCount];
         */
        RectangleInfo matrix[][] = new RectangleInfo[size][size];

        //long startTime = System.currentTimeMillis();

        /*
        for (int i = 0; i < cpuCount; i++) {
            SrtmTileThread srtmTileThread;
            if (i == cpuCount - 1) {
                srtmTileThread = new SrtmTileThread(matrix, map, bigMap, 0 + (i * blockSize), size, srtmRaster, min, max, colors);
            } else {
                srtmTileThread = new SrtmTileThread(matrix, map, bigMap, 0 + (i * blockSize), blockSize + (i * blockSize), srtmRaster, min, max, colors);
            }
            threadArray[i] = srtmTileThread;
        }
        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i].start();
        }
        for (int i = 0; i < threadArray.length; i++) {
            try {
                threadArray[i].join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
         */
        long boundStart = System.currentTimeMillis();

        SrtmMapBoundsThread srtmMapBoundsThread = new SrtmMapBoundsThread(matrix, map, bigMap, srtmRaster, min, max, colors);
        srtmMapBoundsThread.start();
        try {
            srtmMapBoundsThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        long boundEnd = System.currentTimeMillis();

        //System.out.println("BoundTime: " + (boundEnd - boundStart) + " ms");
        if (horStep < 1 && verStep < 1) {
            horStep = 1;
            verStep = 1;
        }

        int visible = 0;

        Point2D marker = null;
        if (geoPositionSel != null) {
            marker = map.getTileFactory().geoToPixel(geoPositionSel, map.getZoom());
        }

        for (int y = 0; y < size; y += verStep) {

            for (int x = 0; x < size; x += horStep) {

                if (matrix[y][x] != null) {
                    Color c = matrix[y][x].getColor();
                    if (showAlpha) {
                        g.setColor(new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                    } else {
                        g.setColor(c);
                    }

                    g.fill(matrix[y][x].getRectangle());
                    g.draw(matrix[y][x].getRectangle());

                    visible++;
                }
            }
        }

        for (int y = 0; y < size; y += verStep) {

            for (int x = 0; x < size; x += horStep) {
                if (matrix[y][x] != null) {
                    if (marker != null) {
                        if (matrix[y][x].getRectangle().contains(marker)) {
                            g.setColor(Color.BLACK);

                            g.draw(matrix[y][x].getRectangle());
                            Rectangle rectangle = matrix[y][x].getRectangle();
                            g.fillOval((int) rectangle.getCenterX(), (int) rectangle.getCenterY(), 3, 3);

                            g.setFont(new Font("Arial", Font.PLAIN, 10));
                            g.drawString(matrix[y][x].getHeight() + " m", (float) rectangle.getCenterX() + 5, (float) rectangle.getCenterY() - 5);

                        }
                    }
                }
            }
        }

        /*
        for (int i = 0; i < cpuCount; i++) {
            int s = 0 + (i * blockSize);
            int e = blockSize + (i * blockSize);

            for (int y = s; y < e; y += verStep) {

                for (int x = 0; x < size; x += horStep) {

                    if (matrix[y][x] != null) {
                        Color c = matrix[y][x].getColor();
                        if (showAlpha) {
                            //System.out.println("aplha");
                            g.setColor(new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                        } else {
                            g.setColor(c);
                        }

                        if (marker != null) {
                            if (matrix[y][x].getRectangle().contains(marker)) {
                                g.setColor(Color.WHITE);
                            }
                        }

                        g.fill(matrix[y][x].getRectangle());
                        g.draw(matrix[y][x].getRectangle());

                        visible++;
                    }
                }
            }
        }
         */
        g.setColor(Color.BLACK);

        GeneralPath generalPath = new GeneralPath();
        generalPath.moveTo(pt0.getX(), pt0.getY());
        generalPath.lineTo(pt1.getX(), pt1.getY());
        generalPath.lineTo(pt2.getX(), pt2.getY());
        generalPath.lineTo(pt3.getX(), pt3.getY());
        generalPath.closePath();
        g.draw(generalPath);

        /*
        double width = HelperFunctions.getDistance(geoPosition0.getLongitude(), geoPosition0.getLatitude(), geoPosition1.getLongitude(), geoPosition1.getLatitude());
        double height = HelperFunctions.getDistance(geoPosition0.getLongitude(), geoPosition0.getLatitude(), geoPosition3.getLongitude(), geoPosition3.getLatitude());

      
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(formatLength(width) + " km", (float) pt0.getX(), (float) pt0.getY() - 5);
        g.drawString(formatLength(height) + " km", (float) pt2.getX() + 5, (float) pt2.getY());
         */
    }

    private String formatLength(double d) {
        return String.format("%.4f", d);
    }

    public void setColors(List<ColorRow> colors) {
        this.colors = colors;
    }

    public void setMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public void setShowHeight(boolean showHeight) {
        this.showHeight = showHeight;
    }

    public void setShowAlpha(boolean showAlpha) {
        this.showAlpha = showAlpha;
    }

    public void setGeoPositionSel(GeoPosition geoPositionSel) {
        this.geoPositionSel = geoPositionSel;
    }
}
