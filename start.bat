@echo off
REM Batch file to run a JavaFX JAR application

REM Set the path to your JavaFX JAR file
SET JAR_FILE=SRTM_Viewer-1.1.0.jar

REM Set the path to your JavaFX SDK lib folder (update this path to match your JavaFX SDK location)
SET JAVAFX_LIB=D:\JavaFX SDK 21.0.6\lib

REM Set the main class (optional, only if not specified in the JAR's manifest)
SET MAIN_CLASS=your.main.ClassName

REM Run the JavaFX application
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml,javafx.swing -jar %JAR_FILE% 

REM Pause to keep the window open (optional, remove if not needed)
pause