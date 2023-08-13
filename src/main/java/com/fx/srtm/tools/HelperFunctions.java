package com.fx.srtm.tools;

import com.fx.srtm.Globals;
import com.fx.srtm.pojo.ColorRow;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.util.SloppyMath;
import org.jxmapviewer.viewer.GeoPosition;

public class HelperFunctions {

    private static final Logger _log = LogManager.getLogger(HelperFunctions.class);
    public static double SF = 180.0 / Math.PI;

    public static void centerWindow(Window window) {
        window.addEventHandler(WindowEvent.WINDOW_SHOWN, (WindowEvent event) -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
            window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
        });
    }

    public Node loadFxml(ResourceBundle bundle, String path, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path), bundle);
            loader.setController(controller);
            Node node = loader.load();
            return node;
        } catch (IOException ex) {
            _log.error(ex.getMessage());
        }
        return null;
    }

    public static Tab addTab(ResourceBundle bundle, TabPane tabPane, String path, Object controller, String tabName) {
        long start = System.currentTimeMillis();
        Tab tab = new Tab(tabName);
        tabPane.getTabs().add(tab);
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        tab.setContent(node);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
        return tab;
    }

    public static BorderPane createTab(ResourceBundle bundle, String path, Object controller) {
        long start = System.currentTimeMillis();
        BorderPane borderPane = new BorderPane();
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        borderPane.setCenter(node);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
        return borderPane;
    }

    public static Node addPlugin(ResourceBundle bundle, String path, Object controller) {
        long start = System.currentTimeMillis();
        HelperFunctions helperFunctions = new HelperFunctions();
        Node node = helperFunctions.loadFxml(bundle, path, controller);
        node.setUserData(controller);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
        return node;
    }

    public static byte[] doubleToByte(double coord, ByteOrder byteOrder) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).order(byteOrder).putDouble(coord);
        return bytes;
    }

    public static double byteToDouble(byte[] bytes, ByteOrder byteOrder) {
        return ByteBuffer.wrap(bytes).order(byteOrder).getDouble();
    }

    public static List<Short> readSrtmFile(String path, ByteOrder byteOrder) throws Exception {
        List<Short> rawList = new ArrayList<Short>();

        DataInputStream reader = new DataInputStream(new FileInputStream(new File(path)));
        int nBytesToRead = reader.available();

        if (nBytesToRead > 0) {

            long start = System.currentTimeMillis();

            byte[] bytes = new byte[nBytesToRead];
            reader.read(bytes);

            for (int i = 0; i < nBytesToRead / 2; i++) {
                byte[] slice = Arrays.copyOfRange(bytes, 0 + (2 * i), 2 + (2 * i));
                ByteBuffer buffer = ByteBuffer.wrap(slice);
                buffer.order(byteOrder);
                rawList.add(buffer.getShort());
            }

            long end = System.currentTimeMillis();
            if (Globals.DEBUG) {
                System.out.println("Time Read: " + (end - start) + " ms");
            }
        }
        reader.close();

        return rawList;
    }

    public static double getPercentFromHeight(int min, int max, double height) {
        double erg = 0;
        erg = ((height - min) / (max - min)) * 100.0;
        if (min == max && min == 0) {
            return 0;
        }
        return erg;
    }

    public static int RGBtoInt(int r, int g, int b) {
        int rgb;

        if ((r < 256 && g < 256 && b < 256) && (r >= 0 && g >= 0 && b >= 0)) {
            rgb = new Color(r, g, b).getRGB();
        } else {
            rgb = new Color(255, 255, 255).getRGB();
        }

        if (r < 0 && g < 0 && b < 0) {
            rgb = new Color(0, 0, 0).getRGB();
        }

        return rgb;
    }

    public static javafx.scene.paint.Color genColor(List<ColorRow> colors, double pct) {
        //pct = pct / 100.0;
        int z = 1;
        for (int i = 1; i < colors.size() - 1; i++) {
            if (pct < colors.get(i).getPercent()) {
                break;
            }
            z++;
        }
        //ColorRow lower = colors.get(z - 1);
        //ColorRow upper = colors.get(z);
        ColorRow lower = colors.get(0);
        ColorRow upper = colors.get(0);
        if (colors.size() - 1 > 0) {
            lower = colors.get(z - 1);
            upper = colors.get(z);
        }
        double range = upper.getPercent() - lower.getPercent();
        double rangePct = (pct - lower.getPercent()) / range;
        double pctLower = 1 - rangePct;
        double pctUpper = rangePct;
        int r = (int) Math.floor(lower.getRed() * pctLower + upper.getRed() * pctUpper);
        int g = (int) Math.floor(lower.getGreen() * pctLower + upper.getGreen() * pctUpper);
        int b = (int) Math.floor(lower.getBlue() * pctLower + upper.getBlue() * pctUpper);
        return javafx.scene.paint.Color.rgb(r, g, b);
    }

    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double dist = SloppyMath.haversinMeters(lat1, lon1, lat2, lon2) / 1000.0;
        return dist;
    }

    public static String getTileNameSRTM(double lon, double lat) {
        String tileName = "";

        int lod = (int) Math.floor(lon);
        int lad = (int) Math.floor(lat);

        if (lad >= 0) {
            tileName += "N";
        } else {
            tileName += "S";
        }
        tileName += String.format("%02d", Math.abs(lad));

        if (lod >= 0) {
            tileName += "E";
        } else {
            tileName += "W";
        }
        tileName += String.format("%03d", Math.abs(lod));

        tileName += ".hgt";

        return tileName;
    }

    public static int getHeightFromTile(short bigMap[][], GeoPosition geoPosition) {
        double dezLon = Math.abs(geoPosition.getLongitude());
        double dezLat = Math.abs(geoPosition.getLatitude());

        int intLon = (int) dezLon;
        int intLat = (int) dezLat;

        double diffLon = dezLon - intLon;
        double diffLat = dezLat - intLat;
        diffLat = 1.0 - diffLat;

        int sizeHor = bigMap[0].length;
        int sizeVer = bigMap.length;

        double x = sizeHor * diffLon;
        double y = sizeVer * diffLat;

        return bigMap[(int) y][(int) x];
    }
}
