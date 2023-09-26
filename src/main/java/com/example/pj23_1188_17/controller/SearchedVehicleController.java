package com.example.pj23_1188_17.controller;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.vehicles.Vehicle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class SearchedVehicleController implements Initializable {

    @FXML
    private TextArea txtAreaInfo;

    @FXML
    private Pane vehiclePane;
    @FXML
    private TextArea txtStates;

    private  int vehicleID ;


    public SearchedVehicleController(int vehicleID){
        this.vehicleID = vehicleID;
    }

    @Override
    public void initialize(URL url, ResourceBundle rs){

        displayVehicle(vehicleID);
    }

    public void displayVehicle(int vehicleID){

        for (Vehicle vehicle: BorderSimulation.vehicles) {
            if(vehicle.getVehicleID() == vehicleID){
                ImageView imageView = new ImageView(new Image(vehicle.getPicture().toURI().toString()));
                imageView.setFitWidth(90);
                imageView.setFitHeight(90);
                Label tmp = new Label(String.valueOf(vehicle.getVehicleID()));
                tmp.setLayoutX(75);
                tmp.setLayoutY(0);
                tmp.setStyle("-fx-font-size: 20; -fx-text-fill: black;");
                Platform.runLater(()-> {
                    try {
                        vehiclePane.getChildren().add(imageView);
                        vehiclePane.getChildren().add(tmp);
                    } catch (NullPointerException exception) {
                        Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
                    }
                });
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(txtAreaInfo);
                txtAreaInfo.setText(vehicle.toString());
                setupStates(vehicle);
            }
        }
    }
    public void setupStates(Vehicle vehicle){
        boolean tmp;
        if(vehicle.doneCustoms && vehicle.finished){
            tmp = true;
        }else tmp = false;
        String string ="Processed at Police: "+vehicle.donePolice+"\n" +
                "Processed at Customs: "+vehicle.doneCustoms+"\n"+
                "Finished: "+vehicle.finished+"\n"+
                "Crossed the Border: "+tmp;

        txtStates.setText(string);

    }

}
