/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.painter;

import com.fx.srtm.pojo.ColorRow;
import com.fx.srtm.pojo.RectangleInfo;
import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.thread.MatrixThread;
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
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author pscha
 */
public class SrtmTilePainter implements Painter<JXMapViewer> {

    private final boolean antiAlias = true;
    private final SrtmRaster srtmRaster;
    private final short[][] bigMap;
    private int min;
    private int max;
    private boolean showAlpha;
    private List<ColorRow> colors;
    private GeoPosition geoPositionSel;
    private GeneralPath generalPath;
    private JXMapViewer mapViewer;
    private int oldZoom;
    private RectangleInfo matrix[][];
    private BufferedImage bufferedImage;
    private RectangleInfo infoCursor;

    public SrtmTilePainter(SrtmRaster srtmRaster, short[][] bigMap, int min, int max) {
        this.srtmRaster = srtmRaster;
        this.bigMap = bigMap;
        this.min = min < 0 ? 0 : min;
        this.max = max;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        mapViewer = map;
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setStroke(new BasicStroke(2));

        drawSrtmTile(g, map);

        g.dispose();
    }

    private BufferedImage createImageFromRectangles(RectangleInfo matrix[][], int pixelSize) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Matrix cannot be null or empty");
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int width = cols * pixelSize;
        int height = rows * pixelSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Color color = matrix[i][j].getColor();

                // Fill the pixel area for this rectangle
                for (int y = i * pixelSize; y < (i + 1) * pixelSize; y++) {
                    for (int x = j * pixelSize; x < (j + 1) * pixelSize; x++) {
                        image.setRGB(x, y, color.getRGB());
                    }
                }
            }
        }

        return image;
    }

    private BufferedImage createImageFromMatrix() {
        long start = System.currentTimeMillis();
        int size = bigMap.length;
        matrix = new RectangleInfo[size][size];
        MatrixThread matrixThread = new MatrixThread(matrix, mapViewer, bigMap, srtmRaster, min, max, colors, showAlpha ? 100 : 255);
        matrixThread.process();
        System.out.println("Timediff: " + (System.currentTimeMillis() - start));
        return bufferedImage = createImageFromRectangles(matrix, 1);
    }

    private void drawSrtmTile(Graphics2D g, JXMapViewer map) {
        if (oldZoom != map.getZoom()) {
            bufferedImage = createImageFromMatrix();
            oldZoom = mapViewer.getZoom();
        }

        GeoPosition geoPositionTL = new GeoPosition(srtmRaster.getCoord0().getLat(), srtmRaster.getCoord0().getLon());
        Point2D pTL = map.getTileFactory().geoToPixel(geoPositionTL, map.getZoom());

        GeoPosition geoPositionLR = new GeoPosition(srtmRaster.getCoord2().getLat(), srtmRaster.getCoord2().getLon());
        Point2D pLR = map.getTileFactory().geoToPixel(geoPositionLR, map.getZoom());

        int width = (int) (pLR.getX() - pTL.getX());
        int height = (int) (pLR.getY() - pTL.getY());
        g.drawImage(bufferedImage, (int) pTL.getX(), (int) pTL.getY(), width, height, null);

        if (map.getZoom() <= 11) {
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

            generalPath = new GeneralPath();
            generalPath.moveTo(pt0.getX(), pt0.getY());
            generalPath.lineTo(pt1.getX(), pt1.getY());
            generalPath.lineTo(pt2.getX(), pt2.getY());
            generalPath.lineTo(pt3.getX(), pt3.getY());
            generalPath.closePath();

            g.setColor(Color.BLACK);
            g.draw(generalPath);

            g.setColor(Color.BLUE);

            int length = 20;

            g.setStroke(new BasicStroke(2));

            g.drawLine((int) pt0.getX(), (int) pt0.getY(), (int) pt0.getX(), (int) pt0.getY() - length);
            g.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) pt1.getX(), (int) pt1.getY() - length);

            g.drawLine((int) pt2.getX(), (int) pt2.getY(), (int) pt2.getX(), (int) pt2.getY() + length);
            g.drawLine((int) pt3.getX(), (int) pt3.getY(), (int) pt3.getX(), (int) pt3.getY() + length);

            g.drawLine((int) pt1.getX(), (int) pt1.getY(), (int) pt1.getX() + length, (int) pt1.getY());
            g.drawLine((int) pt2.getX(), (int) pt2.getY(), (int) pt2.getX() + length, (int) pt2.getY());

            g.setStroke(new BasicStroke(2));

            double widthUp = HelperFunctions.getDistance(geoPosition0.getLongitude(), geoPosition0.getLatitude(), geoPosition1.getLongitude(), geoPosition1.getLatitude());
            double widthDown = HelperFunctions.getDistance(geoPosition3.getLongitude(), geoPosition3.getLatitude(), geoPosition2.getLongitude(), geoPosition2.getLatitude());
            double h = HelperFunctions.getDistance(geoPosition0.getLongitude(), geoPosition0.getLatitude(), geoPosition3.getLongitude(), geoPosition3.getLatitude());

            Font font = new Font("Arial", Font.BOLD, 15);
            g.setFont(font);

            String strWidthUp = formatLength(widthUp) + " km";
            String strWidthDown = formatLength(widthDown) + " km";
            String strHeight = formatLength(h) + " km";

            Rectangle2D rectangle2dUp = g.getFontMetrics(font).getStringBounds(strWidthUp, g);
            Rectangle2D rectangle2dDown = g.getFontMetrics(font).getStringBounds(strWidthDown, g);
            Rectangle2D rectangle2dHeight = g.getFontMetrics(font).getStringBounds(strHeight, g);

            float xUp = (float) ((pt0.getX() + pt1.getX()) / 2.0 - rectangle2dUp.getCenterX());
            float xDown = (float) ((pt0.getX() + pt1.getX()) / 2.0 - rectangle2dDown.getCenterX());
            float yHeight = (float) ((pt1.getY() + pt2.getY()) / 2.0 - rectangle2dHeight.getCenterY());

            g.drawString(strWidthUp, xUp, (float) (pt0.getY() - 5));
            g.drawString(strWidthDown, xDown, (float) (pt3.getY() + rectangle2dDown.getHeight()));
            g.drawString(strHeight, (float) pt1.getX() + 5, (float) (yHeight));

        }

        if (map.getZoom() <= 3) {
            if (geoPositionSel != null) {
                Point2D marker = map.getTileFactory().geoToPixel(geoPositionSel, map.getZoom());
                findRectangleInMatrix(matrix, marker);
            }

            if (infoCursor != null) {
                g.setColor(Color.BLACK);

                g.draw(infoCursor.getRectangle());
                Rectangle rectangle = infoCursor.getRectangle();
                g.fillOval((int) rectangle.getCenterX(), (int) rectangle.getCenterY(), 3, 3);

                g.setFont(new Font("Arial", Font.PLAIN, 10));
                g.drawString(infoCursor.getHeight() + " m", (float) rectangle.getCenterX() + 5, (float) rectangle.getCenterY() - 5);
            }
        }
    }

    private void findRectangleInMatrix(RectangleInfo matrix[][], Point2D marker) {
        int size = matrix.length;
        // Get the number of available cores
        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numCores);

        // Calculate rows per thread
        int rowsPerThread = size / numCores;
        if (rowsPerThread == 0) {
            rowsPerThread = 1; // Ensure at least one row per thread
        }

        // Submit tasks for each chunk of rows
        for (int i = 0; i < size; i += rowsPerThread) {
            final int startRow = i;
            final int endRow = Math.min(i + rowsPerThread, size);
            executor.submit(() -> processMatrix(matrix, marker, startRow, endRow));
        }

        // Shutdown executor and wait for tasks to complete
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Force shutdown if tasks don't complete
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }

    private void processMatrix(RectangleInfo matrix[][], Point2D marker, int startRow, int endRow) {
        for (int x = startRow; x < endRow; x++) {
            for (int y = 0; y < matrix.length; y++) {
                if (matrix[y][x] != null) {
                    if (marker != null) {
                        if (matrix[y][x].getRectangle().contains(marker)) {
                            infoCursor = matrix[y][x];
                            break;
                        }
                    }
                }
            }
        }
    }

    private String formatLength(double d) {
        return String.format("%.4f", d);
    }

    public void setColors(List<ColorRow> colors) {
        this.colors = colors;
        if (matrix != null) {
            createImageFromMatrix();
        }
    }

    public void setMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public void setShowAlpha(boolean showAlpha) {
        this.showAlpha = showAlpha;
        bufferedImage = createImageFromMatrix();
    }

    public void setGeoPositionSel(GeoPosition geoPositionSel) {
        this.geoPositionSel = geoPositionSel;
    }

    public boolean isOut(GeoPosition geoPosition) {
        boolean isOut = true;
        if (generalPath != null && geoPosition != null) {
            Point2D marker = mapViewer.getTileFactory().geoToPixel(geoPosition, mapViewer.getZoom());
            isOut = !generalPath.contains(marker);
        }
        return isOut;
    }
}
