package com.fx.srtm.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author pscha
 */
public class PosSelectionAdapter extends MouseAdapter implements ActionListener {

    private static final Logger _log = LogManager.getLogger(PosSelectionAdapter.class);
    private final JXMapViewer viewer;

    private GeoPosition posTx;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemTx;

    public interface PosSelectionAdapterListener {

        public void setPosition(GeoPosition geoPosition);
    }
    private PosSelectionAdapterListener posSelectionAdapterListener;

    public PosSelectionAdapter(JXMapViewer viewer) {
        this.viewer = viewer;
        init();
    }

    private void init() {
        popupMenu = new JPopupMenu();
        menuItemTx = new JMenuItem("Set Position");
        menuItemTx.addActionListener(this);

        try {
            ImageIcon iconAdd = new ImageIcon(ImageIO.read(new File(System.getProperty("user.dir") + "/images/plus.png")));
            menuItemTx.setIcon(iconAdd);
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }
        popupMenu.add(menuItemTx);
    }

    private void showPopup(MouseEvent e) {
        //if (e.isPopupTrigger() && e.isControlDown()) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) {
            return;
        }

        Rectangle rect = viewer.getViewportBounds();
        double x = rect.getX() + e.getX();
        double y = rect.getY() + e.getY();

        posTx = viewer.getTileFactory().pixelToGeo(new Point((int) x, (int) y), viewer.getZoom());

        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemTx) {
            posSelectionAdapterListener.setPosition(posTx);
        }
    }

    public void setPosSelectionAdapterListener(PosSelectionAdapterListener posSelectionAdapterListener) {
        this.posSelectionAdapterListener = posSelectionAdapterListener;
    }
}
