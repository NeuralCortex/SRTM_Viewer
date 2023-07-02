/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.srtm.cell;

import com.fx.srtm.pojo.ColorRow;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *
 * @author pscha
 */
public class ColorRowCell extends TableCell<ColorRow, Color> {

    @Override
    protected void updateItem(Color item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            setStyle("");
        } else if (item != null && !empty) {
            double r = Math.round(item.getRed() * 255);
            double g = Math.round(item.getGreen() * 255);
            double b = Math.round(item.getBlue() * 255);
            String hex = String.format("#%02x%02x%02x", (int) r, (int) g, (int) b);

            HBox hBox = new HBox();
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
