# SRTM3-Viewer 1.0.0

## Funktionsweise des Programms

Der SRTM-Viewer ist ein JavaFX Projekt, welches ermöglicht topografische Daten</br>
der NASA Mission STS-99 [Shuttle Radar Topography Mission (SRTM)](https://de.wikipedia.org/wiki/STS-99) grafisch anzuzeigen und auszuwerten.</br>
Dabei ist es möglich die für eine bestimme Gegend benötigte Kachel grafisch zu ermitteln, oder die geladenen Kachel als PNG zu speichern.</br>
Das Programm kann zur Zeit nur SRTM3-Dateien verarbeiten, das heißt nur Daten mit einer Auflösung von 3 Winkelsekunden.

## How the program works

The SRTM viewer is a JavaFX project that enables topographical data</br>
of the NASA Mission STS-99 [Shuttle Radar Topography Mission (SRTM)](https://de.wikipedia.org/wiki/STS-99) graphically and evaluate.</br>
It is possible to determine the tile required for a specific area graphically, or to save the loaded tile as PNG.</br>
The program can currently only process SRTM3 files , ie only data with a resolution of 3 arc-seconds.

## Aufbau einer Datei

Der Dateiname hat typischerweise die Form: "N50E010.hgt". Eine Datei enthält 1201 x 1201 Werte.

### Interner Aufbau

Die ".hgt" Datei enthält 16-Bit signed Integer Werte, ohne Header oder Trailer.

<pre>
                1x1 Grad SRTM3-Kachel
Nord X=0,Y=1201 ********************* X=1201,Y=1201</br>
                *********************</br>
                *********************</br>
                *********************</br>
    Süd X=0,Y=0 ********************* X=1201,Y=0</br>
                West              Ost
</pre>

## Structure of a file

The file name typically has the form: "N50E010.hgt". The file contains 1201 x 1201 values.

### Internal structure

The ".hgt" file contains 16-bit signed integer values, with no header or trailer.

<pre>
                 1x1 degree SRTM3 tile
North X=0,Y=1201 ********************* X=1201,Y=1201</br>
                 *********************</br>
                 *********************</br>
                 *********************</br>
   South X=0,Y=0 ********************* X=1201,Y=0</br>
                 West             East
</pre>

## Hinweis

Für den Betrieb des Programms ist eine ständige Internetverbindung erforderlich.

## A notice

A constant internet connection is required to run the program.

## Erster Tab

Im ersten Tab kann der Nutzer durch die Bewegung der Maus, die für z.B. eine gewünschte Stadt, entsprechende Kachel auswählen.</br>
Dabei werden die Grenzen der Kachel und der Dateiname angezeigt. Weiterhin wird die nördliche- und südliche Länge, sowie die Höhe der Kachel berechnet.</br>
Die Berechnung erfolgt unter Berücksichtigung der Wölbung der Erde.</br>
Die Fläche der Kachel wird in Quadratkilometer berechnet.

## Zweiter Tab

Im zweiten Tab kann der Nutzer bereits auf der Festplatte befindliche ".hgt"-Dateien als Raster in OpenStreetMap einzeichnen lassen.</br>
Ab einen bestimmten Zoom-Level werden die Dateinamen der Kachel angezeigt.</br>
In den beiden rechten Tabellen können einzelne Kacheln, oder ganze Bereiche ein- und ausgeblendet werden.</br>
Die Rasteransicht benutzt zur Darstellung nicht die Inhalte der Dateien sondern nur die Dateinamen.</br>
Die Dateien sollten ohne Unterverzeichnisse in einen Ordner vorliegen. Zum Beispiel:
- Europa
- Asien
- Nordamerika
- Japan

## Dritter Tab

Der dritte Tab ist das Herzstück des Programms und bietet den Nutzer die grafische Analyse einer SRTM3-Kachel.</br>
Nach den Import der gewünschten Kachel wird diese entsprechend der Farbtabelle einfärbt angezeigt.</br>
Die Färbung der Kachel kann durch Editieren der Farbtabelle geändert werden. Die neue Farbtabelle wird dabei</br>
in der Konfigurationsdatei gespeichert.</br>

Ab einen bestimmten Zoom der Kachel, kann sich der Nutzer durch Bewegung der Maus die Höhe über Normalnull in Meter anzeigen lassen.</br>
Durch Aktivierung des Alpha-Kanals wird die Kachel halbtransparent und mann kann somit besser überschauen, wo z.B. eine Stadt von einem</br>
Gebirge umgeben ist, oder wie sich die Führung einer Autobahn gestaltet.</br>
Durch Rechtsklick auf die Karte kann ein Marker in der Karte gesetzt werden, weiterhin kann der Marker auch durch Copy & Paste der Länge und Breite</br>
aus Google-Maps angezeigt werden. Durch Drücken der Tastenkombination STRG+V werden beide Felder für Längengrad und Breitengrad durch die</br>
Zwischenablage gefüllt.</br>

Die Schaltflächen auf der rechten Seite speichern die Kachel als Schwarz-Weiss Höhenprofil, oder als eingefärbtes PNG, mit oder ohne Marker ab.</br>
Bevor gespeichert wird erhält der Nutzer eine Vorschau. Marker ausserhalb der Kachel werden nicht berücksichtigt.</br>
Weiterhin hat der Nutzer die Möglichkeit die Kachel auch in 3D darzustellen. Hierfür wird der POV-Export verwendet.</br>
Beim POV-Export werden 2 Dateien angelegt - das Höhenprofil in Schwarz-Weiss und die POV-Datei.</br>
Die POV-Datei enthält eine 3D Beschreibungssprache, die durch den [POV-Ray](http://www.povray.org/) Raytracer in ein Bild umgesetzt wird.</br>
Im Raytracer sollte als Bildgröße mindestens "FullHD - 1920 x 1080 Pixel" eingestellt sein.

## First tab

In the first tab, the user can move the mouse to select the appropriate tile, e.g. for a desired city.</br>
The borders of the tile and the file name are displayed. Furthermore, the northern and southern longitude and the height of the tile are calculated.</br>
The calculation takes into account the curvature of the earth.</br>
The area of ​​the tile is calculated in square kilometers.

## Second tab

In the second tab, the user can draw ".hgt" files already on the hard drive as a grid in OpenStreetMap.</br>
From a certain zoom level, the file names of the tile are displayed.</br>
In the two tables on the right, individual tiles or entire areas can be shown or hidden.</br>
The grid view does not use the contents of the files for display, only the file names.</br>
The files should be in one folder without subdirectories. For example:
- Europe
- Asia
- North America
- Japan

## Third tab

The third tab is the heart of the program and offers the user the graphical analysis of an SRTM3 tile.</br>
After the desired tile has been imported, it is displayed colored according to the color table.</br>
The coloring of the tile can be changed by editing the color table. The new color table will be</br>
saved in the configuration file.</br>

From a certain zoom level on the tile, the user can display the height above sea level in meters by moving the mouse.</br>
By activating the alpha channel, the tile becomes semi-transparent and you can thus get a better overview of where, for example, a city is from a</br>
surrounded by mountains, or how a motorway is routed.</br>
A marker can be placed on the map by right-clicking on the map, and the marker can also be copied and pasted by longitude and latitude</br>
displayed from Google Maps. Pressing CTRL+V replaces both longitude and latitude fields with the</br>
Clipboard filled.</br>

The buttons on the right save the tile as a black and white elevation profile, or as a colored PNG, with or without a marker.</br>
Before saving, the user receives a preview. Markers outside the tile are ignored.</br>
Furthermore, the user has the option of displaying the tile in 3D. POV export is used for this.</br>
Two files are created during the POV export - the height profile in black and white and the POV file.</br>
The POV file contains a 3D description language that is converted into an image by the [POV-Ray](http://www.povray.org/) ray tracer.</br>
In the raytracer, at least "FullHD - 1920 x 1080 pixels" should be set as the image size.

## Verwendete Technologie

Dieses JavaFX-Projekt wurde erstellt mit der Apache NetBeans 17 IDE [NetBeans 17](https://netbeans.apache.org/).

Folgende Frameworks sollten installiert sein:

- JAVA-SDK [JAVA 19](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
- SceneBuilder für GUI-Entwicklung [Gluon SceneBuilder](https://gluonhq.com/products/scene-builder/)
- JAVA-FX-SDK [JavaFX](https://gluonhq.com/products/javafx/)

## Technology used

This JavaFX project was built with the Apache NetBeans 17 IDE [NetBeans 17](https://netbeans.apache.org/).

The following frameworks should be installed:

- JAVA SDK [JAVA 19](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
- SceneBuilder for GUI development [Gluon SceneBuilder](https://gluonhq.com/products/scene-builder/)
- JAVA FX SDK [JavaFX](https://gluonhq.com/products/javafx/)
