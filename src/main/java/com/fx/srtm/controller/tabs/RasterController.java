package com.fx.srtm.controller.tabs;

import com.fx.srtm.Globals;
import com.fx.srtm.cell.DirColorCell;
import com.fx.srtm.cell.SrtmFileColorCell;
import com.fx.srtm.controller.MainController;
import com.fx.srtm.controller.PopulateInterface;
import com.fx.srtm.dialog.ProgressDialog;
import com.fx.srtm.pojo.Dir;
import com.fx.srtm.pojo.Topo;
import com.fx.srtm.task.SrtmRasterTask;
import com.fx.srtm.tools.MousePositionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class RasterController implements Initializable, PopulateInterface {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnReset;
    @FXML
    private Label lbTable;
    @FXML
    private Label lbDir;
    @FXML
    private SwingNode swingNode;
    @FXML
    private HBox hboxTable;
    @FXML
    private HBox hboxOben;
    @FXML
    private HBox hboxDir;
    @FXML
    private VBox vboxData;
    @FXML
    private TableView<Topo> tableView;
    @FXML
    private TableView<Dir> tableViewDir;

    private final MainController mainController;

    private final JXMapViewer mapViewer = new JXMapViewer();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();

    private final double lon = 10.671745101119196;
    private final double lat = 50.661742127393836;

    public RasterController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        vboxData.setPrefWidth(300.0);
        tableViewDir.setPrefHeight(170.0);

        hboxTable.setId("hec-background-blue");
        hboxOben.setId("hec-background-blue");
        hboxDir.setId("hec-background-blue");
        lbTable.setId("hec-text-white");
        lbDir.setId("hec-text-white");

        lbTable.setText(bundle.getString("table.srtm.tiles"));
        lbDir.setText(bundle.getString("table.srtm.dir"));
        btnLoad.setText(bundle.getString("btn.load.srtm"));
        btnReset.setText(bundle.getString("btn.reset"));

        TableColumn<Topo, Boolean> colActive = new TableColumn<>(bundle.getString("map.table.active"));
        TableColumn<Topo, String> colFileName = new TableColumn<>(bundle.getString("map.table.filename"));
        TableColumn<Topo, Color> colColor = new TableColumn<>(bundle.getString("map.table.color"));

        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));
        colActive.setCellFactory(CheckBoxTableCell.forTableColumn(colActive));
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));

        colColor.setCellFactory((param) -> {
            return new SrtmFileColorCell();
        });

        tableView.setEditable(true);
        tableView.getColumns().addAll(colActive, colFileName, colColor);

        TableColumn<Dir, Boolean> colActiveDir = new TableColumn<>(bundle.getString("map.table.active"));
        TableColumn<Dir, String> colPath = new TableColumn<>(bundle.getString("map.table.path"));
        TableColumn<Dir, Color> colColorDir = new TableColumn<>(bundle.getString("map.table.color"));

        colActiveDir.setCellValueFactory(new PropertyValueFactory<>("active"));
        colActiveDir.setCellFactory(CheckBoxTableCell.forTableColumn(colActiveDir));
        colPath.setCellValueFactory(new PropertyValueFactory<>("path"));
        colColorDir.setCellValueFactory(new PropertyValueFactory<>("color"));
        colColorDir.setCellFactory((param) -> {
            return new DirColorCell();
        });

        colPath.setMinWidth(100.0);

        tableViewDir.setEditable(true);
        tableViewDir.getColumns().addAll(colActiveDir, colPath, colColorDir);

        initOsmMap(bundle);

        borderPane.widthProperty().addListener(e -> {
            mapViewer.repaint();
        });
        borderPane.heightProperty().addListener(e -> {
            mapViewer.repaint();
        });

        btnLoad.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();

            String saveDir = Globals.propman.getProperty(Globals.SRTM_DIR, System.getProperty("user.dir"));

            directoryChooser.setInitialDirectory(new File(saveDir));
            File dir = directoryChooser.showDialog(mainController.getStage());
            
            List<File> dirs=getAllDirsRecursive(dir);

            if (dir != null) {

                Globals.propman.setProperty(Globals.SRTM_DIR, dir.getParent());
                Globals.propman.save();

                for(File sub:dirs){
                    ProgressDialog progressDialog = new ProgressDialog(mainController.getStage(), bundle);

                Color color = genColor();

                Dir dirRow = new Dir(sub.getAbsolutePath(), color, true);
                tableViewDir.getItems().add(dirRow);

                SrtmRasterTask srtmRasterTask = new SrtmRasterTask(progressDialog, sub.listFiles(), mapViewer, painters, tableView, color, tableViewDir, dirRow);
                new Thread(srtmRasterTask).start();
                }
            }
        });

        btnReset.setOnAction(e -> {
            tableViewDir.getItems().clear();
            tableView.getItems().clear();
            mapViewer.setOverlayPainter(null);
            painters.clear();
            mapViewer.repaint();
        });
    }
    
    private List<File> getAllDirsRecursive(File dir) {
        List<File> allDirs = new ArrayList<>();
        collectDirectoriesRecursive(dir, allDirs);
        return allDirs;
    }
    
    private void collectDirectoriesRecursive(File dir, List<File> dirs) {
        if (!dir.isDirectory()) return;
        
        dirs.add(dir); // Add current directory
        
        File[] items = dir.listFiles();
        if (items == null) return;
        
        for (File item : items) {
            if (item.isDirectory()) {
                collectDirectoriesRecursive(item, dirs);
            }
        }
    }

    private Color genColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);

        Color color = new Color(r, g, b);

        return color;
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

        mapViewer.setZoom(7);
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
