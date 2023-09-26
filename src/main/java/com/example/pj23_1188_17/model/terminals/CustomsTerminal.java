package com.example.pj23_1188_17.model.terminals;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.controller.MainWindowController;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.Passenger;
import com.example.pj23_1188_17.model.vehicles.Truck;
import com.example.pj23_1188_17.model.vehicles.Vehicle;
import javafx.application.Platform;

import java.io.*;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class CustomsTerminal extends Terminal {

    public  Object lock = new Object();
    public MainWindowController mwc = new MainWindowController();
    String name;

    public CustomsTerminal() {
        setNormalVehicles(true);
        this.picture = new File(System.getProperty("user.dir") + File.separator + "pictures" + File.separator + "border.png");
    }

    public void run() {
        while (BorderSimulation.moreVehiclesforCustomsTerminal()) { //Add if simulation has unfinished vehicles
           // lock.lock();
            //Waiting for the vehicle!
           // try {
            synchronized (Tlock){
            while (this.vehicle == null){
                    try {
                        synchronized (lock) {
                           // System.out.println("CUSTOMS TERMINAL: WAITING");
                                String string = "Unoccupied";
                                MainWindowController.writeEventOnGUI(this.getTerminalID(),string);
                            lock.wait();
                        }
                    } catch (InterruptedException exception) {
                        Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
                    }
                }
//            } finally {
//                lock.unlock();
//            }
                synchronized (this) {
                        String string = "Processing vehicle: "+this.vehicle.getVehicleID();
                        MainWindowController.writeEventOnGUI(this.getTerminalID(),string);
                        isPauseClicked();
                        isPauseInserted();
                        MainWindowController.updateTerminalGUI(this.vehicle, this);
                    //System.out.println("Processing vehicle CUSTOMS:  " + this.vehicle.getVehicleID());
                    if(this.getTerminalID() == 4){
                        name="TruckCustoms: ";
                    }
                    else {
                        name="Car/BusCustoms: ";
                    }
                        processVehicle();
                }
//            lock.lock();
//            try {
                synchronized (this.vehicle){
                vehicle.processedC = false;
                //vehicle.doneCustoms = true;
                vehicle.notifyAll();

                }
                this.vehicle.processedC = false;
            if(!this.vehicle.finished){
                this.vehicle.doneCustoms = true;}//probably needs if
                synchronized (this) {
//                    vehicle.processedC = false;
//                    vehicle.notify();
                    this.vehicle = null;
                }
                isPauseClicked();
                isPauseInserted();
                MainWindowController.removeFromCustomsTerminal(this);
                for (Vehicle vehicle:BorderSimulation.vehicleQueue) {
                    if(vehicle.waitingCustoms){
                        vehicle.waitingCustoms = false;
                        synchronized (vehicle.resource2){
                            vehicle.resource2.notifyAll();
                        }
                        break;
                    }
                }
//            }finally {
//                lock.unlock();
//            }
            }}

        MainWindowController.isSimulationActive = false;
        //MainWindowController.gameOver = true;
        }

    @Override
    public void processVehicle() {
        if (this.vehicle != null && this.vehicle.getMaxCapacity() == 52) {
            try {
                Thread.sleep((long) this.vehicle.getWaitingTime() * this.vehicle.getPassengers().size());
            } catch (InterruptedException exception) {
                Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
            }
            // Need to use iterator, foreach doesn't allow removing the element from data structure --> ConcurrentModificationException
            Iterator<Passenger> iterator = this.vehicle.getPassengers().iterator();
            while (iterator.hasNext()) {
                Passenger passenger = iterator.next();
                if (passenger.isSuitcasePresent() && !passenger.getSuitcase().isLegal()) {
                    System.out.println("Passenger:"+passenger.getID()+">>> Illegal suitcase");
                        String string = name+"Passenger: "+passenger.getID()+ " has an illegal suitcase\n"+
                                "Removing passenger: "+passenger.getID()+ " from the vehicle: "+this.vehicle.getVehicleID();
                        MainWindowController.writeEventOnGUI(0,string);
                    writeIllegalPassenger(passenger,this.vehicle);
                    iterator.remove(); // The element removed safely!
                    this.vehicle.setCapacity(this.vehicle.getCapacity() - 1);
                }
            }

        } else if (this.vehicle != null && this.vehicle.getMaxCapacity() == 5) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException exception) {
                Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
            }

        } else if (this.vehicle != null && this.vehicle.getMaxCapacity() == 3) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException exception) {
                Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
            }
            if(!((Truck)vehicle).isRequiredDocumentation()){
                //generate the required documentation
                ((Truck)vehicle).setRequiredDocumentation(true);
                System.out.println("Documentation generated!");
                String string3 = name+"Documentation generated for vehicle: "+this.vehicle.getVehicleID();
                    MainWindowController.writeEventOnGUI(0,string3);
            }
            if(((Truck)vehicle).isOverloaded()){
                illegalVehicle.add(this.vehicle);
                writeIllegalVehicle(this.vehicle);
                System.out.println("Vehicle "+this.vehicle.getVehicleID()+" is ILLEGAL!!!");
                String string = name+"Vehicle: "+this.vehicle.getVehicleID()+" is overloaded\n"+
                        "Removing the vehicle and all of the passengers";
                    MainWindowController.writeEventOnGUI(0,string);
                synchronized (this.vehicle){
                    this.vehicle.finished = true;
                }
                BorderSimulation.vehicleQueue.remove(this.vehicle);
            }
        }
    }

    public void writeIllegalPassenger(Passenger passenger,Vehicle vehicle) {
        try {
            BufferedWriter bus = new BufferedWriter(new PrintWriter(new FileWriter(BorderSimulation.punishedPassengersCustoms, true)));
            bus.write("In the vehicle ID:"+vehicle.getVehicleID()+ "\nThe passenger ID:"+passenger.getID()+" had an illegal suitcase.\n");
            bus.close();
        }catch (Exception exception){
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }
    }

    public void writeIllegalVehicle(Vehicle vehicle){
        try {
            String tmp ="";
            for(int i = 0;i<vehicle.getPassengers().size();i++){
                Passenger passenger = vehicle.getPassengers().get(i);
                tmp +="The passenger ID:"+passenger.getID()+" was in the vehicle ID:"+vehicle.getVehicleID()+"\n";
            }
            BufferedWriter bus = new BufferedWriter(new PrintWriter(new FileWriter(BorderSimulation.punishedPassengersCustoms, true)));
            bus.write("The vehicle ID:"+vehicle.getVehicleID()+" was overloaded.\n"+tmp);
            bus.close();
        }catch (Exception exception){
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }
    }
}

