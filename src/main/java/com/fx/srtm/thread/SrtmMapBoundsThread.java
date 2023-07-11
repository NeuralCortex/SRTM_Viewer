package com.fx.srtm.thread;

import com.fx.srtm.pojo.ColorRow;
import com.fx.srtm.pojo.RectangleInfo;
import com.fx.srtm.pojo.Range;
import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.tools.HelperFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import javafx.scene.paint.Color;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoBounds;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.util.GeoUtil;

/**
 *
 * @author pscha
 */
public class SrtmMapBoundsThread extends Thread {

    private final SrtmRaster srtmRaster;
    private final RectangleInfo matrix[][];
    private final short bigMap[][];

    private final JXMapViewer map;

    private final int min;
    private final int max;

    private final List<ColorRow> colors;
    private int size = 0;

    public interface RangeListener {

        public void getRange(Range range);
    }
    private RangeListener rangeListener;

    public SrtmMapBoundsThread(RectangleInfo matrix[][], JXMapViewer map, short bigMap[][], SrtmRaster srtmRaster, int min, int max, List<ColorRow> colors) {
        this.matrix = matrix;
        this.map = map;
        this.bigMap = bigMap;
        this.srtmRaster = srtmRaster;
        this.min = min;
        this.max = max;
        this.colors = colors;
        this.size = bigMap.length;
    }

    @Override
    public void run() {

        GeoPosition geoPosition0 = new GeoPosition(srtmRaster.getCoord0().getLat(), srtmRaster.getCoord0().getLon());
        GeoPosition geoPosition1 = new GeoPosition(srtmRaster.getCoord1().getLat(), srtmRaster.getCoord1().getLon());
        GeoPosition geoPosition2 = new GeoPosition(srtmRaster.getCoord2().getLat(), srtmRaster.getCoord2().getLon());
        GeoPosition geoPosition3 = new GeoPosition(srtmRaster.getCoord3().getLat(), srtmRaster.getCoord3().getLon());

        Point2D pt0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
        Point2D pt1 = map.getTileFactory().geoToPixel(geoPosition1, map.getZoom());
        Point2D pt2 = map.getTileFactory().geoToPixel(geoPosition2, map.getZoom());
        Point2D pt3 = map.getTileFactory().geoToPixel(geoPosition3, map.getZoom());

        /*
        0-1
        3-2
         */
        int resHor = (int) ((pt1.getX() - pt0.getX()));
        int resVer = (int) ((pt3.getY() - pt0.getY()));

        int horStep = (int) (size / (double) resHor);
        int verStep = (int) (size / (double) resVer);

        //System.out.println("horStep: " + horStep + " " + verStep);
        if (horStep < 2 && verStep < 2) {
            horStep = 1;
            verStep = 1;
        }

        GeoBounds geoBounds = GeoUtil.getMapBounds(map);
        double lrLon = geoBounds.getSouthEast().getLongitude();
        double lrLat = geoBounds.getSouthEast().getLatitude();
        double ulLon = geoBounds.getNorthWest().getLongitude();
        double ulLat = geoBounds.getNorthWest().getLatitude();

        double links = geoBounds.getNorthWest().getLongitude();
        double rechts = geoBounds.getSouthEast().getLongitude();

        double oben = geoBounds.getNorthWest().getLatitude();
        double unten = geoBounds.getSouthEast().getLatitude();

        //System.out.println("l: " + links + " r: " + rechts);
        int linksGanz = (int) links;
        int rechtsGanz = (int) rechts;

        int obenGanz = (int) oben;
        int untenGanz = (int) unten;

        double linksDez = links - (double) linksGanz;
        double rechtsDez = rechts - (double) rechtsGanz;

        double obenDez = oben - (double) obenGanz;
        double untenDez = unten - (double) untenGanz;

        //System.out.println("ld: " + linksDez + " rd: " + rechtsDez);
        String fileName = srtmRaster.getFileName();

        int xStart = (int) (Math.floor(size * linksDez));
        int xEnde = (int) (Math.ceil(size * rechtsDez));

        int yStart = (int) (Math.ceil(size * obenDez));
        int yEnde = (int) (Math.floor(size * untenDez));

        if (fileName.toUpperCase().contains("N") && fileName.toUpperCase().contains("E")) {
            yStart = size - yStart;
            yEnde = size - yEnde;
        }

        if (fileName.toUpperCase().contains("N") && fileName.toUpperCase().contains("W")) {
            xStart = size + xStart;
            xEnde = size + xEnde;
            yStart = size - yStart;
            yEnde = size - yEnde;
        }

        if (fileName.toUpperCase().contains("S") && fileName.toUpperCase().contains("E")) {
            yStart = -yStart;
            yEnde = -yEnde;
        }

        if (fileName.toUpperCase().contains("S") && fileName.toUpperCase().contains("W")) {
            xStart = size + xStart;
            xEnde = size + xEnde;
            yStart = -yStart;
            yEnde = -yEnde;
        }

        if (lrLon > geoPosition1.getLongitude()) {
            xEnde = size;
        }

        if (ulLon < geoPosition0.getLongitude()) {
            xStart = 0;
        }

        if (ulLat > geoPosition0.getLatitude()) {
            yStart = 0;
        }

        if (lrLat < geoPosition2.getLatitude()) {
            yEnde = size;
        }

        /*
        System.out.println("xs: " + xStart + " xe: " + xEnde);
        System.out.println("ys: " + yStart + " ye: " + yEnde);

        System.out.println("ulLon: " + ulLon);
        System.out.println("ulLat: " + ulLat);
        System.out.println("lrLon: " + lrLon);
        System.out.println("lrLat: " + lrLat);
         */
        int visible = 0;

        for (int y = yStart; y < yEnde; y += verStep) {

            for (int x = xStart; x < xEnde; x += horStep) {

                double ver = y / (double) (size);
                double hor = x / (double) (size);

                GeoPosition geoPosition = new GeoPosition(srtmRaster.getCoord0().getLat() - ver, srtmRaster.getCoord0().getLon() + hor);

                Point2D ptHeight = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

                GeoPosition geoPositionLL = new GeoPosition(srtmRaster.getCoord0().getLat() - ver - (1.0 / size), srtmRaster.getCoord0().getLon() + hor);
                GeoPosition geoPositionLR = new GeoPosition(srtmRaster.getCoord0().getLat() - ver - (1.0 / size), srtmRaster.getCoord0().getLon() + hor + (1.0 / size));
                Point2D ptHeightLL = map.getTileFactory().geoToPixel(geoPositionLL, map.getZoom());

                GeoPosition geoPositionUR = new GeoPosition(srtmRaster.getCoord0().getLat() - ver, srtmRaster.getCoord0().getLon() + hor + (1.0 / size));
                Point2D ptHeightUR = map.getTileFactory().geoToPixel(geoPositionUR, map.getZoom());

                short height = bigMap[y][x];
                if (height < 0) {
                    height = 0;
                }

                double percent = HelperFunctions.getPercentFromHeight(min, max, height);
                Color colorHeight = HelperFunctions.genColor(colors, percent);

                double red = colorHeight.getRed();
                double green = colorHeight.getGreen();
                double blue = colorHeight.getBlue();

                java.awt.Color color = new java.awt.Color((int) (red * 255), (int) (green * 255), (int) (blue * 255), 255);

                if (height == 0) {
                    color = java.awt.Color.RED;
                }

                Point point = new Point((int) ptHeight.getX(), (int) ptHeight.getY());

                Rectangle rectangle = new Rectangle((int) point.getX(), (int) point.getY(), (int) (ptHeightUR.getX() - ptHeight.getX()), (int) (ptHeightLL.getY() - ptHeight.getY()));

                if (geoPosition.getLongitude() > ulLon && geoPositionUR.getLongitude() < lrLon) {
                    if (geoPosition.getLatitude() < ulLat && geoPositionLR.getLatitude() > lrLat) {
                        matrix[y][x] = new RectangleInfo(rectangle, color, height);
                        visible++;
                    }
                }

                GeoBounds rand = new GeoBounds(geoPositionLL.getLatitude(), geoPositionLL.getLongitude(), geoPositionUR.getLatitude(), geoPositionUR.getLongitude());
                if (geoBounds.intersects(rand)) {
                    matrix[y][x] = new RectangleInfo(rectangle, color, height);
                    visible++;
                }
            }
        } 
        //System.out.println("Visible Rects: " + visible);
        rangeListener.getRange(new Range(xStart, xEnde, yStart, yEnde, horStep, verStep));
    }

    public void setRangeListener(RangeListener rangeListener) {
        this.rangeListener = rangeListener;
    }
}
