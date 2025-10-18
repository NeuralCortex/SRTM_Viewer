# SRTM-Viewer 1.1.0

![Application Screenshot](https://github.com/NeuralCortex/SRTM_Viewer/blob/main/images/srtm.png)

## Overview

SRTM-Viewer is a JavaFX application designed to visualize and analyze topographical data from NASA's Shuttle Radar Topography Mission (SRTM) [STS-99](https://en.wikipedia.org/wiki/STS-99). Users can graphically select and evaluate SRTM tiles, save them as PNG files, and analyze elevation data. The application supports both SRTM3 (3 arc-second resolution) and SRTM1 (1 arc-second resolution) files.

## Requirements

- A stable internet connection is required to run the application.
- Java Runtime Environment (JRE) or Java Development Kit (JDK) version 24 is required.
- JavaFX SDK is necessary for GUI functionality ([JavaFX](https://gluonhq.com/products/javafx/)).

## File Structure

SRTM files (`.hgt`) contain 16-bit signed integer values representing elevation data, with no header or trailer. File names follow the format `N50E010.hgt`.

### SRTM3 Tile (1x1 degree)
```
North X=0,Y=1201 ********************* X=1201,Y=1201
                 *********************
                 *********************
                 *********************
South X=0,Y=0    ********************* X=1201,Y=0
                 West             East
```

### SRTM1 Tile (1x1 degree)
```
North X=0,Y=3601 ********************* X=3601,Y=3601
                 *********************
                 *********************
                 *********************
South X=0,Y=0    ********************* X=3601,Y=0
                 West             East
```

## Usage Instructions

### Tab 1: Tile Selection
- Move the mouse over the map to select an SRTM tile (e.g., for a specific city).
- Displays tile boundaries, file name, northern/southern longitude, and elevation.
- Accounts for Earth's curvature in calculations.
- Calculates the tile's area in square kilometers.

### Tab 2: Grid View
- Visualize `.hgt` files from a local directory as a grid on OpenStreetMap.
- File names appear at a certain zoom level.
- Two tables on the right allow showing or hiding individual tiles or entire regions.
- Grid view uses only file names, not file contents.
- Store files in a single folder without subdirectories (e.g., `Europe`, `Asia`, `North America`, `Japan`).

### Tab 3: Graphical Analysis
- Import an SRTM3 or SRTM1 tile for color-coded visualization based on a configurable color table.
- Edit the color table to adjust tile coloring; changes are saved to the configuration file.
- At higher zoom levels, hover to display elevation in meters above sea level.
- Enable the alpha channel for semi-transparency to view underlying map features (e.g., cities or highways).
- Right-click to place a marker; copy/paste longitude and latitude from Google Maps using `CTRL+V` to update coordinates.
- Save options:
  - Export the tile as a black-and-white elevation profile or colored PNG (with or without a marker).
  - Preview is provided before saving; markers outside the tile are ignored.
- 3D visualization via POV export:
  - Generates a black-and-white elevation profile and a POV file for the [POV-Ray](http://www.povray.org/) ray tracer.
  - Set POV-Ray image size to at least Full HD (1920x1080 pixels).

## Technologies Used

- **IDE**: Apache NetBeans 27 ([NetBeans 27](https://netbeans.apache.org/))
- **Java SDK**: Java 24 ([JDK 24](https://www.oracle.com/java/technologies/downloads/#jdk24-windows))
- **GUI Development**: Gluon Scene Builder ([Scene Builder](https://gluonhq.com/products/scene-builder/))
- **Framework**: JavaFX ([JavaFX](https://gluonhq.com/products/javafx/))