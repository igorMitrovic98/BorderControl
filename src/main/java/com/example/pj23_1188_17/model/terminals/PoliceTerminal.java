package com.example.pj23_1188_17.model.terminals;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.controller.MainWindowController;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.Passenger;
import com.example.pj23_1188_17.model.vehicles.Vehicle;
import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class PoliceTerminal extends Terminal{

    public  Object  Releaselock = new Object();
    String name;

    public PoliceTerminal(){
        setNormalVehicles(true);
        this.picture = new File(System.getProperty("user.dir") + File.separator + "pictures" + File.separator + "passport.png");
    }

    public void run () {
        while (this.vehicle != null || BorderSimulation.moreVehiclesforPoliceTerminal()) { //Add if simulation has unprocessed vehicles
//            lock.lock();
//            //Waiting for the vehicle!
//            try {
            synchronized (Tlock) {

                    while(vehicle == null) {
                        synchronized (Releaselock) {
                            try {
                              //  System.out.println("Police terminal:should be empty "+this.getTerminalID());
                                    String string = "Unoccupied";
                                MainWindowController.writeEventOnGUI(this.getTerminalID(),string);
                                Releaselock.wait();//having a problem here... NO LONGER!!!!!!
                           // System.out.println("POLICE TERMINAL ID "+this.getTerminalID()+"; PROCESSING: "+this.vehicle.getVehicleID());


                            } catch (InterruptedException exception) {
                                Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
                            }
                        }
                    }
                synchronized (this){
                        try{
                if (!this.vehicle.donePolice) {
                    //System.out.println("Processing vehicle POLICE:  " + this.vehicle.getVehicleID());
                        String string = "Processing vehicle: "+this.vehicle.getVehicleID();
                        MainWindowController.writeEventOnGUI(this.getTerminalID(),string);
                    isPauseClicked();
                    isPauseInserted();
                    if(this.getTerminalID() == 1){
                        name="TruckPolice: ";
                    }
                    else if(this.getTerminalID() ==2) {
                        name="Car/BusPolice Right: ";
                    }
                    else{
                        name="Car/BusPolice Left: ";
                    }
                    processVehicle();

                    //System.out.println("TERMINAL STATE:"+this.vehicle);
                }}catch (NullPointerException exception){
                            System.out.println("");
                        }
                      // Run.logger.log(Level.INFO,exception.fillInStackTrace().toString());}
                }
//            }finally {
//                lock.unlock();
//            }
              // break;
            }
        }
    }

    @Override
    public void processVehicle(){
        try {
            Thread.sleep((long) vehicle.getWaitingTime() * vehicle.getPassengers().size());
        }catch (InterruptedException exception){
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }
        for(int i = 0; i < this.vehicle.getPassengers().size(); i++){
            //Check if the driver is illegal ,if so, then remove all the passengers
            Passenger tmp = this.vehicle.getPassengers().get(i);
           // System.out.println("processing the guy :" +tmp.getID());
            if(!tmp.isValidDocumentation()){
                if(tmp.isDriver()){
                    System.out.println("Illegal Driver, PassengerID: "+tmp.getID()+" in Vehicle: "+this.vehicle.getVehicleID());
                    String string1 = name+"Illegal driver: "+tmp.getID()+" in the vehicle: "+this.vehicle.getVehicleID()+"\n"+
                            "Removing vehicle: "+this.vehicle.getVehicleID()+" and all of its passengers";
                        MainWindowController.writeEventOnGUI(0,string1);
                    for(int n = 0; n < this.vehicle.getPassengers().size() ; n++){
                        isPauseClicked();// need to put it in the loop here, if the bus comes , it can take a while to pause!!
                        isPauseInserted();
                        Passenger illegal = this.vehicle.getPassengers().get(n);
                        writeIllegalPassenger(illegal); //serialized passenger
                    }
//                    lock.lock();
//                    try {
                        illegalVehicle.add(this.vehicle);
                        System.out.println("Vehicle"+this.vehicle.getVehicleID()+" is ILLEGAL!!!");

                        synchronized (this.vehicle) {

                            this.vehicle.donePolice = false;
                            this.vehicle.finished = true;
                            BorderSimulation.vehicleQueue.remove(this.vehicle);
                            vehicle.notifyAll();
                            isPauseClicked();
                            isPauseInserted();
                            this.vehicle = null;
                            for(Vehicle veh:BorderSimulation.vehicleQueue){
                                if(veh.checkValidityofTerminal(this) && veh.waitingPolice && veh.getPosition()==0){
                                    synchronized (veh){
                                        veh.waitingPolice = false;
                                    }
                                    synchronized (veh.resource1){
                                        veh.resource1.notifyAll();
                                    }
                                    break;
                                }
                            }

                        }
                        synchronized (this) {
                            MainWindowController.removeFromPoliceTerminal(this);
                        }
//                    }finally {
//                        lock.unlock();
//                    }
                }
                else {
                    System.out.println("Illegal non-Driver,PassengerID:"+tmp.getID()+"in Vehicle: "+this.vehicle.getVehicleID());
                    String string2 = name+"Illegal passenger: "+tmp.getID()+" in the vehicle: "+this.vehicle.getVehicleID()+"\n"+
                            "Removing passenger: "+tmp.getID()+" from the vehicle";
                    MainWindowController.writeEventOnGUI(0,string2);
                    this.vehicle.getPassengers().remove(tmp);
                    writeIllegalPassenger(tmp); //serialized passenger

                }
            }

        }
//        lock.lock();
//        try{
        if (this.vehicle != null) {
        synchronized (vehicle) {
            //System.out.println("PRINT ME THE VEHICLE ID : "+vehicle.getVehicleID()+"  "+vehicle.processedP);
            this.vehicle.donePolice = true;
            //System.out.println("VEhicle: "+this.vehicle.getVehicleID() + ">>> done :"+this.vehicle.donePolice);
            //System.out.println("VEhicle: "+this.vehicle.getVehicleID() + ">>> finished :"+this.vehicle.finished);
            if(this.vehicle.processedP) {
                vehicle.notify();

            }
            }}
//        }finally {
//            lock.unlock();
//        }
    }

    public void writeIllegalPassenger(Passenger passenger){
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(BorderSimulation.punishedPassengersPolice,true));
            ObjectOutputStream objectOut = new ObjectOutputStream(bos);
            objectOut.writeObject(passenger);
            System.out.println("OBJECT "+ passenger.getID()+" SERIALIZED");
            objectOut.close(); // need to close the stream , otherwise it writes nothing!
        }catch (Exception exception) {
            exception.printStackTrace();
            Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
        }

    }
}


