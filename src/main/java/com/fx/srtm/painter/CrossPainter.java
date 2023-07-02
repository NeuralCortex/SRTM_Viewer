package com.fx.srtm.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author pscha
 */
public class CrossPainter implements Painter<JXMapViewer> {

    private final Color color = Color.BLACK;
    private final boolean antiAlias = true;
    private final GeoPosition geoPosition;

    public CrossPainter(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
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
        g.setStroke(new BasicStroke(1));

        drawPoint(g, map);

        g.dispose();
    }

    private void drawPoint(Graphics2D g, JXMapViewer map) {

        Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

        int halfLine = 20;

        g.setColor(Color.BLACK);

        g.drawLine((int) pt.getX() - halfLine, (int) pt.getY(), (int) pt.getX() + halfLine, (int) pt.getY());
        g.drawLine((int) pt.getX(), (int) pt.getY() - halfLine, (int) pt.getX(), (int) pt.getY() + halfLine);
    }
}
