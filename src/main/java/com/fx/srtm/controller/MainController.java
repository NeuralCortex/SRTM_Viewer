/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.controller;

import com.fx.srtm.Globals;
import com.fx.srtm.controller.tabs.ChoiseController;
import com.fx.srtm.controller.tabs.RasterController;
import com.fx.srtm.controller.tabs.TileController;
import com.fx.srtm.tools.HelperFunctions;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author pscha
 */
public class MainController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label lbStatus;
    @FXML
    private HBox hboxStatus;
    @FXML
    private Label lbInfo;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem miClose;
    @FXML
    private MenuItem miAbout;
    @FXML
    private MenuBar menuBar;

    private static final Logger _log = LogManager.getLogger(MainController.class);
    private final Stage stage;

    public MainController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle bundle) {
        borderPane.setPrefSize(Globals.WIDTH, Globals.HEIGHT);

        init(bundle);

        Tab tabChoise = HelperFunctions.addTab(bundle, tabPane, Globals.FXML_TILE_CHOISE_PATH, new ChoiseController(this), bundle.getString("tab.choise"));
        HelperFunctions.addTab(bundle, tabPane, Globals.FXML_RASTER_PATH, new RasterController(this), bundle.getString("tab.raster"));
        HelperFunctions.addTab(bundle, tabPane, Globals.FXML_TILE_PATH, new TileController(this), bundle.getString("tab.tile"));
        /*
        HelperFunctions.addTab(bundle, tabPane, Globals.FXML_RESULT_PATH, new ResultController(this), bundle.getString("tab.result"));
        //HelperFunctions.addTab(bundle, tabPane, Globals.FXML_TRANS_PATH, new TransController(this), bundle.getString("tab.trans"));

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
            ((PopulateInterface) n.getContent().getUserData()).populate();
            if (n.equals(tabMap)) {
                ((PopulateInterface) n.getContent().getUserData()).reset();
            }
        });
         */
        miClose.setOnAction(e -> {
            System.exit(0);
        });

        miAbout.setOnAction(e -> {
            showAboutDlg(bundle);
        });

        ((PopulateInterface) tabChoise.getContent().getUserData()).populate();
    }

    private void showAboutDlg(ResourceBundle bundle) {
        Alert alert = new Alert(AlertType.INFORMATION);
        HelperFunctions.centerWindow(alert.getDialogPane().getScene().getWindow());

        Stage stageDlg = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            stageDlg.getIcons().add(new Image(new FileInputStream(new File(Globals.APP_LOGO_PATH))));
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        alert.setTitle(bundle.getString("dlg.about.info"));
        alert.setHeaderText(bundle.getString("dlg.about.header"));
        String programmer = bundle.getString("dlg.about.content");
        alert.setContentText(MessageFormat.format(programmer, String.format("%d", LocalDate.now().getYear())));

        alert.showAndWait();
    }

    private void init(ResourceBundle bundle) {
        hboxStatus.setId("hec-background-blue");
        lbStatus.setId("hec-text-white");
        lbInfo.setId("hec-text-white");

        menuFile.setText(bundle.getString("menu.file"));
        menuHelp.setText(bundle.getString("menu.help"));

        miAbout.setText(bundle.getString("mi.about"));
        miClose.setText(bundle.getString("mi.close"));

        String programmer = bundle.getString("dlg.about.content");
        lbInfo.setText(MessageFormat.format(programmer, String.format("%d", LocalDate.now().getYear())));
    }

    public Stage getStage() {
        return stage;
    }

    public Label getLbStatus() {
        return lbStatus;
    }

    public Label getLbInfo() {
        return lbInfo;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
