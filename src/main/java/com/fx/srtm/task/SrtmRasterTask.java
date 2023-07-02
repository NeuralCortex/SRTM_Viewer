package com.fx.srtm.task;

import com.fx.srtm.dialog.ProgressDialog;
import com.fx.srtm.painter.SrtmRasterPainter;
import com.fx.srtm.pojo.Dir;
import com.fx.srtm.pojo.SrtmRaster;
import com.fx.srtm.pojo.Topo;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

/**
 *
 * @author pscha
 */
public class SrtmRasterTask extends Task<Integer> {
    
    private static final Logger _log = LogManager.getLogger(SrtmRasterTask.class);
    private final ProgressDialog progressDialog;
    
    private final JXMapViewer mapViewer;
    private final List<Painter<JXMapViewer>> painters;
    
    private final File files[];
    private final TableView<Topo> tableView;
    private final TableView<Dir> tableViewDir;
    private final Color color;
    private final Dir dir;
    
    private boolean stop = false;
    
    public SrtmRasterTask(ProgressDialog progressDialog, File files[], JXMapViewer mapViewer, List<Painter<JXMapViewer>> painters, TableView<Topo> tableView, Color color, TableView<Dir> tableViewDir, Dir dir) {
        this.progressDialog = progressDialog;
        this.files = files;
        this.mapViewer = mapViewer;
        this.painters = painters;
        this.tableView = tableView;
        this.tableViewDir = tableViewDir;
        this.color = color;
        this.dir = dir;
        
        initProgressDlg();
    }
    
    private void initProgressDlg() {
        progressDialog.getLbLeft().setText("0");
        progressDialog.getProgressBar().progressProperty().bind(progressProperty());
        progressDialog.setProgressDialogInterface(() -> {
            stop = true;
        });
        progressDialog.show();
    }
    
    @Override
    protected Integer call() throws Exception {
        
        List<SrtmRaster> rasterList = new ArrayList<>();
        
        for (File file : files) {
            if (!file.isDirectory()) {
                rasterList.add(new SrtmRaster(file.getName()));
            }
        }
        
        Platform.runLater(() -> {
            progressDialog.getLbRight().setText(rasterList.size() - 1 + "");
        });
        
        for (int i = 0; i < rasterList.size(); i++) {
            if (stop) {
                break;
            }
            SrtmRaster srtmRaster = rasterList.get(i);
            SrtmRasterPainter srtmRasterPainter = new SrtmRasterPainter(srtmRaster, color);
            painters.add(srtmRasterPainter);
            
            Topo topo = new Topo(srtmRaster.getFileName(), true, srtmRasterPainter, color);
            
            addTopoListener(topo);
            
            dir.getListTopo().add(topo);
            
            Platform.runLater(() -> {
                tableView.getItems().add(topo);
            });
            
            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
            
            updateProgress(i, rasterList.size() - 1);
        }
        
        dir.activeProperty().addListener((ov, o, n) -> {
            if (n == false) {
                for (int k = dir.getListTopo().size() - 1; k >= 0; k--) {
                    Topo topo = dir.getListTopo().get(k);
                    topo.setActive(false);
                    painters.remove(topo.getSrtmRasterPainter());
                }
            } else {
                for (Topo t : dir.getListTopo()) {
                    t.setActive(true);
                    
                    addTopoListener(t);
                    
                    painters.add(t.getSrtmRasterPainter());
                }
            }
            
            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
        });
        
        return 1;
    }
    
    private void addTopoListener(Topo t) {
        t.activeProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue == false) {
                painters.remove(t.getSrtmRasterPainter());
            } else {
                painters.add(t.getSrtmRasterPainter());
            }
            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
        });
    }
    
    @Override
    protected void succeeded() {
        progressDialog.closeDialog();
    }
}
