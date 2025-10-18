package com.fx.srtm.controller.tabs;

import com.fx.srtm.Globals;
import com.fx.srtm.controller.MainController;
import com.fx.srtm.controller.PopulateInterface;
import com.fx.srtm.painter.SrtmRasterPainter;
import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.tools.HelperFunctions;
import com.fx.srtm.tools.MousePositionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class ChoiseController implements Initializable, PopulateInterface {

    @FXML
    private BorderPane borderPane;
    @FXML
    private SwingNode swingNode;
    @FXML
    private HBox hboxOben;
    @FXML
    private Button btnReset;

    private final MainController mainController;

    private final JXMapViewer mapViewer = new JXMapViewer();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();

    private final double lon = 10.671745101119196;
    private final double lat = 50.661742127393836;

    public ChoiseController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        hboxOben.setId("hec-background-blue");
        btnReset.setText(bundle.getString("btn.reset"));

        initOsmMap(bundle);

        borderPane.widthProperty().addListener(e -> {
            mapViewer.repaint();
        });
        borderPane.heightProperty().addListener(e -> {
            mapViewer.repaint();
        });

        btnReset.setOnAction(e -> {
            mapViewer.setOverlayPainter(null);
            painters.clear();
            mapViewer.repaint();
        });
    }

    private void initOsmMap(ResourceBundle bundle) {

        TileFactoryInfo tileFactoryInfo = new OSMTileFactoryInfo();
        DefaultTileFactory defaultTileFactory = new DefaultTileFactory(tileFactoryInfo);
        defaultTileFactory.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        mapViewer.setTileFactory(defaultTileFactory);

        final JLabel labelAttr = new JLabel();
        mapViewer.setLayout(new BorderLayout());
        mapViewer.add(labelAttr, BorderLayout.SOUTH);
        labelAttr.setText(defaultTileFactory.getInfo().getAttribution() + " - " + defaultTileFactory.getInfo().getLicense());

        // Set the focus
        GeoPosition zm = new GeoPosition(lat, lon);

        mapViewer.setZoom(11);
        mapViewer.setAddressLocation(zm);

        // Add interactions
        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        MousePositionListener mousePositionListener = new MousePositionListener(mapViewer);
        mousePositionListener.setGeoPosListener((GeoPosition geoPosition) -> {
            Platform.runLater(() -> {
                String lat = String.format("%.5f", geoPosition.getLatitude());
                String lon = String.format("%.5f", geoPosition.getLongitude());
                mainController.getLbStatus().setText(bundle.getString("col.lat") + ": " + lat + " " + bundle.getString("col.lon") + ": " + lon);

                String fileName = HelperFunctions.getTileNameSRTM(geoPosition.getLongitude(), geoPosition.getLatitude());
                SrtmRaster srtmRaster = new SrtmRaster(fileName);
                SrtmRasterPainter srtmRasterPainter = new SrtmRasterPainter(srtmRaster, Color.BLACK, true, geoPosition);

                painters.clear();

                painters.add(srtmRasterPainter);
                CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                mapViewer.setOverlayPainter(painter);
                mapViewer.repaint();
            });
        });
        mapViewer.addMouseMotionListener(mousePositionListener);

        try {
            SwingUtilities.invokeAndWait(() -> {
                swingNode.setContent(mapViewer);
                swingNode.requestFocus();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                swingNode.getContent().repaint();
            }
        }, Globals.WAIT_TIME_SWING);
    }

    @Override
    public void populate() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void clear() {

    }
}
