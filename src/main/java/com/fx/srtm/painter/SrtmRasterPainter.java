/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.painter;

import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.tools.HelperFunctions;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.PolygonArea;
import net.sf.geographiclib.PolygonResult;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author pscha
 */
public class SrtmRasterPainter implements Painter<JXMapViewer> {

    private final SrtmRaster srtmRaster;
    private final Color color;
    private final boolean showName = true;
    private boolean showInfos = false;
    private GeoPosition geoPosition;

    public SrtmRasterPainter(SrtmRaster srtmRaster, Color color) {
        this.srtmRaster = srtmRaster;
        this.color = color;
    }

    public SrtmRasterPainter(SrtmRaster srtmRaster, Color color, boolean showInfos) {
        this(srtmRaster, color);
        this.showInfos = showInfos;
    }

    public SrtmRasterPainter(SrtmRaster srtmRaster, Color color, boolean showInfos, GeoPosition geoPosition) {
        this(srtmRaster, color, showInfos);
        this.geoPosition = geoPosition;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(color);
        g.setStroke(new BasicStroke(2));

        drawSrtmRaster(g, map);

        g.dispose();
    }

    private void drawSrtmRaster(Graphics2D g, JXMapViewer map) {

        GeoPosition geoPosition0 = new GeoPosition(srtmRaster.getCoord0().getLat(), srtmRaster.getCoord0().getLon());
        GeoPosition geoPosition1 = new GeoPosition(srtmRaster.getCoord1().getLat(), srtmRaster.getCoord1().getLon());
        GeoPosition geoPosition2 = new GeoPosition(srtmRaster.getCoord2().getLat(), srtmRaster.getCoord2().getLon());
        GeoPosition geoPosition3 = new GeoPosition(srtmRaster.getCoord3().getLat(), srtmRaster.getCoord3().getLon());

        /*
        0 - 1
        3 - 2
         */
        Point2D pt0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
        Point2D pt1 = map.getTileFactory().geoToPixel(geoPosition1, map.getZoom());
        Point2D pt2 = map.getTileFactory().geoToPixel(geoPosition2, map.getZoom());
        Point2D pt3 = map.getTileFactory().geoToPixel(geoPosition3, map.getZoom());

        GeneralPath generalPath = new GeneralPath();
        generalPath.moveTo(pt0.getX(), pt0.getY());
        generalPath.lineTo(pt1.getX(), pt1.getY());
        generalPath.lineTo(pt2.getX(), pt2.getY());
        generalPath.lineTo(pt3.getX(), pt3.getY());
        generalPath.closePath();
        g.draw(generalPath);

        g.setColor(Color.BLACK);
        
        if(showInfos){
            g.setStroke(new BasicStroke(1));
            
            Point2D marker=map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
            g.drawLine((int)pt0.getX(), (int)marker.getY(), (int)pt1.getX(), (int)marker.getY());
            g.drawLine((int)marker.getX(), (int)pt0.getY(), (int)marker.getX(), (int)pt3.getY());
            
            g.setStroke(new BasicStroke(2));
        }

        if (showName) {
            if (map.getZoom() <= 11) {
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString(srtmRaster.getFileName(), (int) pt0.getX() + 5, (int) pt0.getY() + 25);
            }
            //System.out.println("Zoom: "+map.getZoom());
        }

        if (showInfos) {
            if (map.getZoom() <= 11) {
                g.setColor(Color.BLUE);
                
                int length=20;
                
                g.setStroke(new BasicStroke(2));
                
                g.drawLine((int)pt0.getX(),(int)pt0.getY(),(int)pt0.getX(), (int)pt0.getY()-length);
                g.drawLine((int)pt1.getX(),(int)pt1.getY(),(int)pt1.getX(), (int)pt1.getY()-length);
                
                 g.drawLine((int)pt2.getX(),(int)pt2.getY(),(int)pt2.getX(), (int)pt2.getY()+length);
                g.drawLine((int)pt3.getX(),(int)pt3.getY(),(int)pt3.getX(), (int)pt3.getY()+length);
                
                 g.drawLine((int)pt1.getX(),(int)pt1.getY(),(int)pt1.getX()+length, (int)pt1.getY());
                g.drawLine((int)pt2.getX(),(int)pt2.getY(),(int)pt2.getX()+length, (int)pt2.getY());
                
                g.setStroke(new BasicStroke(2));

                double widthUp = HelperFunctions.getDistance(geoPosition0.getLongitude(), geoPosition0.getLatitude(), geoPosition1.getLongitude(), geoPosition1.getLatitude());
                double widthDown = HelperFunctions.getDistance(geoPosition3.getLongitude(), geoPosition3.getLatitude(), geoPosition2.getLongitude(), geoPosition2.getLatitude());
                double height = HelperFunctions.getDistance(geoPosition0.getLongitude(), geoPosition0.getLatitude(), geoPosition3.getLongitude(), geoPosition3.getLatitude());

                Font font = new Font("Arial", Font.BOLD, 15);
                g.setFont(font);

                String strWidthUp = formatLength(widthUp) + " km";
                String strWidthDown = formatLength(widthDown) + " km";
                String strHeight = formatLength(height) + " km";

                Rectangle2D rectangle2dUp = g.getFontMetrics(font).getStringBounds(strWidthUp, g);
                Rectangle2D rectangle2dDown = g.getFontMetrics(font).getStringBounds(strWidthDown, g);
                Rectangle2D rectangle2dHeight = g.getFontMetrics(font).getStringBounds(strHeight, g);

                float xUp = (float) ((pt0.getX() + pt1.getX()) / 2.0 - rectangle2dUp.getCenterX());
                float xDown = (float) ((pt0.getX() + pt1.getX()) / 2.0 - rectangle2dDown.getCenterX());
                float yHeight = (float) ((pt1.getY() + pt2.getY()) / 2.0 - rectangle2dHeight.getCenterY());

                g.drawString(strWidthUp, xUp, (float) (pt0.getY() - 5));
                g.drawString(strWidthDown, xDown, (float) (pt3.getY() + rectangle2dDown.getHeight()));
                g.drawString(strHeight, (float) pt1.getX() + 5, (float) (yHeight));

                PolygonArea polygonArea = new PolygonArea(Geodesic.WGS84, false);
                polygonArea.AddPoint(geoPosition0.getLatitude(), geoPosition0.getLongitude());
                polygonArea.AddPoint(geoPosition1.getLatitude(), geoPosition1.getLongitude());
                polygonArea.AddPoint(geoPosition2.getLatitude(), geoPosition2.getLongitude());
                polygonArea.AddPoint(geoPosition3.getLatitude(), geoPosition3.getLongitude());

                PolygonResult result = polygonArea.Compute();

                double area = Math.abs(result.area);
                String unit = " m²";
                if (area >= 1000000.0f) {
                    area = area / 1000000.0f;
                    unit = " km²";
                }

                String erg = formatLength(area) + unit;

                float mx = (float) ((pt0.getX() + pt1.getX()) / 2.0);
                float my = (float) ((pt0.getY() + pt3.getY()) / 2.0);

                Rectangle2D rectangle2dArea = g.getFontMetrics(font).getStringBounds(erg, g);
                g.setColor(Color.WHITE);
                g.fillRect((int)(mx-rectangle2dArea.getCenterX()),(int)(my+rectangle2dArea.getCenterY()-3),(int)rectangle2dArea.getWidth(),(int)rectangle2dArea.getHeight());
                g.setColor(Color.RED);
                g.drawString(erg, (float) (mx - rectangle2dArea.getCenterX()), (float) (my - rectangle2dArea.getCenterY()));
            }
        }
    }

    private String formatLength(double d) {
        return String.format("%.4f", d);
    }
}
