package com.fx.srtm.thread;

import com.fx.srtm.pojo.ColorRow;
import com.fx.srtm.pojo.RectangleInfo;
import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.tools.HelperFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.scene.paint.Color;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Processes a matrix of elevation data for map visualization using all
 * available CPU cores.
 *
 * @author Neural Cortex
 */
public class MatrixThread {

    private final SrtmRaster srtmRaster;
    private final RectangleInfo matrix[][];
    private final short bigMap[][];
    private final JXMapViewer map;
    private final int min;
    private final int max;
    private final List<ColorRow> colors;
    private final int size;
    private final int alpha;

    public MatrixThread(RectangleInfo matrix[][], JXMapViewer map, short bigMap[][],
            SrtmRaster srtmRaster, int min, int max, List<ColorRow> colors,int alpha) {
        this.matrix = matrix;
        this.map = map;
        this.bigMap = bigMap;
        this.srtmRaster = srtmRaster;
        this.min = min;
        this.max = max;
        this.colors = colors;
        this.size = bigMap.length;
        this.alpha=alpha;
    }

    public void process() {
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
            executor.submit(() -> processRows(startRow, endRow));
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

    private void processRows(int startRow, int endRow) {
        for (int x = startRow; x < endRow; x++) {
            for (int y = 0; y < size; y++) {
                // Get height and ensure non-negative
                short height = bigMap[x][y];
                if (height < 0) {
                    height = 0;
                }

                // Calculate percentage of height within min-max range
                double percent = HelperFunctions.getPercentFromHeight(min, max, height);
                Color colorHeight = HelperFunctions.genColor(colors, percent);

                // Convert JavaFX Color to AWT Color
                double red = colorHeight.getRed();
                double green = colorHeight.getGreen();
                double blue = colorHeight.getBlue();
                java.awt.Color color = new java.awt.Color(
                        (int) (red * 255), (int) (green * 255), (int) (blue * 255), alpha);

                // Set color to red for zero height (e.g., sea level)
                if (height == 0) {
                    //color = java.awt.Color.RED;
                }

                // Calculate normalized coordinates
                double ver = x / (double) size;
                double hor = y / (double) size;

                // Get geographic position for the current point
                GeoPosition geoPosition = new GeoPosition(
                        srtmRaster.getCoord0().getLat() - ver,
                        srtmRaster.getCoord0().getLon() + hor);
                Point2D ptHeight = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

                // Convert to AWT Point
                Point point = new Point((int) ptHeight.getX(), (int) ptHeight.getY());

                // Calculate lower-left and upper-right corners for the rectangle
                GeoPosition geoPositionLL = new GeoPosition(
                        srtmRaster.getCoord0().getLat() - ver - (1.0 / size),
                        srtmRaster.getCoord0().getLon() + hor);
                Point2D ptHeightLL = map.getTileFactory().geoToPixel(geoPositionLL, map.getZoom());

                GeoPosition geoPositionUR = new GeoPosition(
                        srtmRaster.getCoord0().getLat() - ver,
                        srtmRaster.getCoord0().getLon() + hor + (1.0 / size));
                Point2D ptHeightUR = map.getTileFactory().geoToPixel(geoPositionUR, map.getZoom());

                int pixelScale = 1; // Adjust as needed
                Rectangle rectangle = new Rectangle(
                        (int)point.getX(), (int)point.getY(),
                        Math.max(1, (int) ((ptHeightUR.getX() - ptHeight.getX()) * pixelScale)),
                        Math.max(1, (int) ((ptHeightLL.getY() - ptHeight.getY()) * pixelScale)));

                // Store rectangle, color, and height in matrix
                matrix[x][y] = new RectangleInfo(rectangle, color, height);
            }
        }
    }
}
