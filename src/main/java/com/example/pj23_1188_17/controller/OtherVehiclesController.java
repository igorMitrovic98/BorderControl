package com.example.pj23_1188_17.controller;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.vehicles.Vehicle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class OtherVehiclesController implements Initializable {

    @FXML
    private GridPane restVehicleGrid;


    private static  int numRows = 9;
    private static int numCols = 5;
    private static int currentRow = 0;
    private static int currentCol = 0;

    private static Pane[][] help = new Pane[9][5];

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //System.out.println("\n");
        //System.out.println("Method running");
        for(int i = 0; i<9; i++){
            for(int j = 0; j<5; j++){
            help[i][j] =  new Pane();
            }
        }
        insertIntoGridPane();
//        for(Vehicle v: BorderSimulation.vehicleQueue){
//            //System.out.print(v.getPosition()+"//");
//        }
        for (Vehicle vehicle: BorderSimulation.vehicleQueue) {
            if(vehicle.getPosition() > 4){
                drawPicturesGui(vehicle);
            }
        }
        currentRow = 0;
        currentCol = 0;
    }

    public void insertIntoGridPane(){

        for(int i = 0; i<9; i++)
        {   for(int j = 0; j<5 ; j++){
            try {

                restVehicleGrid.add(help[i][j], j, i);
            }catch (NullPointerException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }}
        }
    }

    static void drawPicturesGui(Vehicle vehicle){

        ImageView imageView = new ImageView(new Image(vehicle.getPicture().toURI().toString()));
        imageView.setFitWidth(70);
        imageView.setFitHeight(50);
        Platform.runLater(()->{
//            System.out.println("\n");
//            System.out.println("Method called !!");
            try {
                if (currentRow < numRows && currentCol < numCols) {
                    Label tmp = new Label(String.valueOf(vehicle.getVehicleID()));
                    //tmp.setLayoutX(40);
                    tmp.setLayoutX(50);
                    tmp.setLayoutY(0);
                    tmp.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
                    help[currentRow][currentCol].getChildren().add(imageView);
                    help[currentRow][currentCol].getChildren().add(tmp);

                    //System.out.println(currentRow + "//" + currentCol);
                    currentCol++;
                    if (currentCol >= numCols) {
                        currentCol = 0;
                        currentRow++;
                    }}
            }catch (NullPointerException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }
        });


    }
}

