/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.cell;

import com.fx.srtm.pojo.Topo;
import java.awt.Color;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

/**
 *
 * @author pscha
 */
public class SrtmFileColorCell extends TableCell<Topo, Color> {

    @Override
    protected void updateItem(Color item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            setStyle("");
        } else if (item != null && !empty) {
            int r = item.getRed();
            int g = item.getGreen();
            int b = item.getBlue();
            String hex = String.format("#%02x%02x%02x", r, g, b);
            
            HBox hBox=new HBox();
            hBox.setPadding(new Insets(1));

            hBox.setStyle("-fx-background-color:" + hex + ";");

            getTableRow().selectedProperty().addListener((ov, o, n) -> {
                if (n) {
                    hBox.setStyle("-fx-background-color:#2196F3;");
                } else if (o) {
                    hBox.setStyle("-fx-background-color:" + hex + ";");
                }
            });

            setGraphic(hBox);
            setText(null);
        }
    }
}
