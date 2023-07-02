package com.fx.srtm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class XMLPropertyManager extends Properties {

    private static final long serialVersionUID = 1L;
    private String _path = null;

    public XMLPropertyManager(String path) {
        _path = path;
        load();
    }

    private void load() {
        FileInputStream fis = null;
        try {
            File file = new File(_path);
            if (!file.exists()) {
                save();
            }
            fis = new FileInputStream(file);
            clear();
            loadFromXML(fis);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (InvalidPropertiesFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_path);
            storeToXML(fos, "Comment");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
