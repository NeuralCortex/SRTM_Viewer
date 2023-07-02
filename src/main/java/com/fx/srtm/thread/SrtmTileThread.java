package com.fx.srtm.thread;

import com.fx.srtm.pojo.ColorRow;
import com.fx.srtm.pojo.RectangleInfo;
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
public class SrtmTileThread extends Thread {

    private final SrtmRaster srtmRaster;
    private final RectangleInfo matrix[][];
    private final short bigMap[][];

    private final JXMapViewer map;

    private final int start;
    private final int end;
    private final int min;
    private final int max;

    private final List<ColorRow> colors;
    private final int size = 1201;

    public SrtmTileThread(RectangleInfo matrix[][], JXMapViewer map, short bigMap[][], int start, int end, SrtmRaster srtmRaster, int min, int max, List<ColorRow> colors) {
        this.matrix = matrix;
        this.map = map;
        this.bigMap = bigMap;
        this.start = start;
        this.end = end;
        this.srtmRaster = srtmRaster;
        this.min = min;
        this.max = max;
        this.colors = colors;
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

        if (horStep < 1 && verStep < 1) {
            horStep = 1;
            verStep = 1;
        }

        GeoBounds geoBounds = GeoUtil.getMapBounds(map);
        double lrLon = geoBounds.getSouthEast().getLongitude();
        double lrLat = geoBounds.getSouthEast().getLatitude();
        double ulLon = geoBounds.getNorthWest().getLongitude();
        double ulLat = geoBounds.getNorthWest().getLatitude();

        for (int y = start; y < end; y += verStep) {

            for (int x = 0; x < size; x += horStep) {

                double ver = y / (double) (size);
                double hor = x / (double) (size);

                GeoPosition geoPosition = new GeoPosition(srtmRaster.getCoord0().getLat() - ver, srtmRaster.getCoord0().getLon() + hor);
                //geoposition ist UL
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
                Point point = new Point((int) ptHeight.getX(), (int) ptHeight.getY());

                Rectangle rectangle = new Rectangle((int) point.getX(), (int) point.getY(), (int) (ptHeightUR.getX() - ptHeight.getX()), (int) (ptHeightLL.getY() - ptHeight.getY()));
                //matrix[y][x] = new RectangleInfo(new Rectangle(point), color);
                if (geoPosition.getLongitude() > ulLon && geoPositionUR.getLongitude() < lrLon) {
                    if (geoPosition.getLatitude() < ulLat && geoPositionLR.getLatitude() > lrLat) {
                        matrix[y][x] = new RectangleInfo(rectangle, color, height);
                    }
                }

                GeoBounds rand = new GeoBounds(geoPositionLL.getLatitude(), geoPositionLL.getLongitude(), geoPositionUR.getLatitude(), geoPositionUR.getLongitude());
                if (geoBounds.intersects(rand)) {
                    matrix[y][x] = new RectangleInfo(rectangle, color, height);
                }
            }
        }
    }
}
