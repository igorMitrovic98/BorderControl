package com.example.pj23_1188_17;

import com.example.pj23_1188_17.controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.*;

public class Run extends Application {

    public static FileHandler fileHandler;
    //public static ConsoleHandler consoleHandler = new ConsoleHandler();
    public static Logger logger = Logger.getLogger(Run.class.getName());
    static{
    try
    {
        fileHandler =  new FileHandler("generatedFiles/log.txt", true);
        logger.setLevel(Level.ALL);
        logger.addHandler(fileHandler);
//        consoleHandler.setLevel(Level.SEVERE);
//        logger.addHandler(consoleHandler);

    }catch (IOException exception){
        exception.fillInStackTrace();
    }}

    @Override
    public void start(Stage primaryStage) throws Exception{

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        MainWindowController mainWindowController = new MainWindowController();
        loader.setController(mainWindowController);
        try{
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Border Simulation!");
            primaryStage.setResizable(false);
           primaryStage.show();
      }catch(IOException exception){
            exception.printStackTrace();
           Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }

    }

    public static void main(String args[]){
        launch(args);
    }
}
