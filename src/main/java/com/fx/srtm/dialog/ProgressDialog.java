package com.fx.srtm.dialog;

import com.fx.srtm.Globals;
import com.fx.srtm.tools.HelperFunctions;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author pscha
 */
public class ProgressDialog extends Dialog<Object> {

    @FXML
    private Label lbLeft;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lbRight;
    private Button btnClose;

    private final HelperFunctions helperFunctions = new HelperFunctions();
    private boolean inderminate = false;

    public interface ProgressDialogInterface {

        public void stopAction();
    }

    private ProgressDialogInterface progressDialogInterface;

    public ProgressDialog(Window window, ResourceBundle bundle) {
        init(window, bundle);
    }

    public ProgressDialog(Window window, ResourceBundle bundle, boolean inderminate) {
        this.inderminate = inderminate;
        init(window, bundle);
    }

    private void init(Window window, ResourceBundle bundle) {
        DialogPane dialogPane = (DialogPane) helperFunctions.loadFxml(bundle, Globals.DLG_PROGRESS_PATH, this);
        setDialogPane(dialogPane);

        btnClose = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        btnClose.setOnAction(e -> {
            progressDialogInterface.stopAction();
        });
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        stage.setOnCloseRequest(e -> {
            if (inderminate) {
                e.consume();
            } else {
                progressDialogInterface.stopAction();
            }
        });

        lbLeft.setText("0");
        lbRight.setText(bundle.getString("dlg.progress.right"));
        if (inderminate) {
            btnClose.setDisable(true);
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }

        initOwner(window);
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        HelperFunctions.centerWindow(window);
    }

    public void closeDialog() {
        if (getDialogPane().getScene() != null) {
            Stage stage = (Stage) getDialogPane().getScene().getWindow();
            stage.close();
        }
    }

    public Label getLbLeft() {
        return lbLeft;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getLbRight() {
        return lbRight;
    }

    public Button getBtnClose() {
        return btnClose;
    }

    public void setProgressDialogInterface(ProgressDialogInterface progressDialogInterface) {
        this.progressDialogInterface = progressDialogInterface;
    }
}
