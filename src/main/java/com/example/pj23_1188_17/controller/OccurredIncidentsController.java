package com.example.pj23_1188_17.controller;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.Passenger;
import com.example.pj23_1188_17.model.terminals.Terminal;
import com.example.pj23_1188_17.model.vehicles.Truck;
import com.example.pj23_1188_17.model.vehicles.Vehicle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class OccurredIncidentsController implements Initializable {

    @FXML
    private TextArea txtAreaGood;
    @FXML
    private TextArea txtAreaBad;

    @Override
    public void initialize(URL url, ResourceBundle rs){
        setTxtAreaGood();
        setTxtAreaBad();

    }

    //need another data structure with starting vehicles states , so I can compare to BorderSimulation.vehicles
    private void setTxtAreaGood(){
        Platform.runLater(()->{
        for (Vehicle vehicle: BorderSimulation.vehicles) {
            //System.out.println(BorderSimulation.startVehicles.size()); //prints correct size
            for(int i = 0 ; i<BorderSimulation.startVehicles.size();i++){
                Vehicle start = BorderSimulation.startVehicles.get(i);
                //System.out.println(start);//also correct
                if(vehicle.finished && vehicle.doneCustoms && vehicle.getVehicleID() == start.getVehicleID() && vehicle.getPassengers().size() != start.getPassengers().size()){
                    txtAreaGood.appendText("Vehicle: "+vehicle.getVehicleID()+"\n");
                    List<Passenger> punishedPassengers = new ArrayList<>(start.getPassengers());
                    punishedPassengers.removeAll(vehicle.getPassengers());
                    for(Passenger passenger:punishedPassengers){
                        if(!passenger.isValidDocumentation()){
                            txtAreaGood.appendText("    Passenger: "+passenger.getID()+" had an illegal documentation\n");
                        }else if(passenger.isSuitcasePresent() && !passenger.getSuitcase().isLegal()) {
                            txtAreaGood.appendText("    Passenger: " + passenger.getID() + " had an illegal suitcase\n");
                        }
                    }
                }
            }
        }
//        String text = txtAreaGood.getText();
//        String[] lines = text.split("\n");
//        checktxtAreaGood(lines);
        });
    }


    private void setTxtAreaBad(){
        Platform.runLater(()->{
            List<Vehicle> removedVehicles = new ArrayList<>();
            //Terminal terminal = new Terminal();
                for (Vehicle vehicle : Terminal.getIllegalVehicle()){
                    removedVehicles.add(vehicle);
                }

            for(Vehicle vehicle:removedVehicles) {
                txtAreaBad.appendText("Vehicle: " + vehicle.getVehicleID() + "\n");
                if (vehicle.getPassengers().get(0).isDriver() && !vehicle.getPassengers().get(0).isValidDocumentation()) {
                    txtAreaBad.appendText("    Driver had an illegal documentation\n");
                    for (int i = 0; i < vehicle.getPassengers().size(); i++) {
                        txtAreaBad.appendText("    Removed passenger: " + vehicle.getPassengers().get(i).getID()+"\n");
                    }
                }
                else if (vehicle.getMaxCapacity() == 3) {
                    Truck truck = (Truck) vehicle;
                    if (truck.isOverloaded()) {
                        txtAreaBad.appendText("    Vehicle was overloaded\n");
                        for (int i = 0; i < vehicle.getPassengers().size(); i++) {
                            txtAreaBad.appendText("    Removed passenger: " + vehicle.getPassengers().get(i).getID()+"\n");
                        }
                    }
                }
            }
        });
    }

}

