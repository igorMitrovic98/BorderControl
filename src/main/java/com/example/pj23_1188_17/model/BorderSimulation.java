package com.example.pj23_1188_17.model;


import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.controller.FileWatcherController;
import com.example.pj23_1188_17.model.terminals.*;
import com.example.pj23_1188_17.model.vehicles.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class BorderSimulation {


    public static List<Terminal> terminals = new ArrayList<>(5);
    public static List<Vehicle> vehicles = new ArrayList<>();
    public static ConcurrentLinkedQueue<Vehicle> vehicleQueue = new ConcurrentLinkedQueue<>();
    public static List<Vehicle> startVehicles = new ArrayList<>();

    public static final int BUSES = 5, TRUCKS = 10, CARS = 35, CUSTOMS = 2, POLICE = 3;
    public static List<Terminal> getTerminals(){
        return terminals;
    }

    public FileWatcherController fwc = new FileWatcherController();
    public static final File punishedPassengersPolice = new File(System.getProperty("user.dir")+File.separator+"generatedFiles"+File.separator+"punishedPassengersPolice.ser");
    public static final File punishedPassengersCustoms = new File(System.getProperty("user.dir")+File.separator+"generatedFiles"+File.separator+"punishedPassengersCustoms.txt");

    public BorderSimulation(){

        addVehicles(vehicles);
        vehicles.forEach(vehicle -> vehicleQueue.add(vehicle));
        addTerminals(terminals);
        clearFile(punishedPassengersPolice);
        clearFile(punishedPassengersCustoms);

    }
    public void start(){
        for (Terminal terminal:terminals) {
            terminal.start();
        }
        for (Vehicle vehicle:vehicleQueue) {
            vehicle.start();
        }
        Thread fileWatcher = new Thread(new Runnable() { //need to do this , otherwise my threads work ,but main JAVAFX thread
            //doesn't do  a thing , goes into file watcher method check() and cant get out , so instancing a new thread for the
            //file watcher is the only solution that I have found
            @Override
            public void run() {
                fwc.check();
            }
        });
        fileWatcher.start();
    }


    public void addVehicles(List<Vehicle> list){

        for(int i = 0 ; i < BorderSimulation.BUSES; i++){
            Bus bus = new Bus();
           // System.out.println(bus.getCapacity() +"////"+bus.getPassengers().size());
            list.add(bus);
        }
        for(int i = 0; i < BorderSimulation.TRUCKS; i++){
            Truck truck = new Truck();
          //  System.out.println(truck.getCapacity() +"////"+truck.getPassengers().size());
            list.add(truck);
        }
        for(int i = 0; i < BorderSimulation.CARS; i++){
            Car car = new Car();
           // System.out.println(car.getCapacity() +"////"+car.getPassengers().size());
            list.add(car);
        }
        setTrucksOverload(list);
        randomFalseDocuments(list);
        Collections.shuffle(list);

        for (int i=0; i < list.size();i++){
            vehicles.get(i).setPosition(i);
        }
        for(Vehicle vehicle:list){
            if(vehicle.getMaxCapacity() == 5){
                startVehicles.add(new Car((Car) vehicle));
            }
            else if(vehicle.getMaxCapacity() == 3){
                startVehicles.add(new Truck((Truck) vehicle));
            }
            else if(vehicle.getMaxCapacity() == 52){
                startVehicles.add(new Bus((Bus) vehicle));
            }
        }


    }

    public void addTerminals(List<Terminal> list){

        for(int i = 0; i < BorderSimulation.POLICE;i++){
            if(i == 0){
                Terminal terminal = new PoliceTerminal();
                terminal.setNormalVehicles(false);
                list.add(terminal);
            }
            else{
                list.add(new PoliceTerminal());
            }
        }
        for(int i = 0 ; i < BorderSimulation.CUSTOMS; i++){
            if(i == 0){
                Terminal terminal = new CustomsTerminal();
                terminal.setNormalVehicles(false);
                list.add(terminal);
            }
            else{
                list.add(new CustomsTerminal());
            }
        }

    }
    public void randomFalseDocuments(List<Vehicle> list){
        int allDocs = 0;
        Random random = new Random();
        for (Vehicle vehicle:list) {
//            System.out.println(vehicle.getCapacity()+"   CAPACITY");
            allDocs += vehicle.getCapacity();
//            System.out.println(allDocs+"   TOTAL");
        }
        double falseMultiplier = 0.03;
        int numberOfFalseDocuments = (int) (allDocs * falseMultiplier);
        while (numberOfFalseDocuments > 0){
            Vehicle randomVehicle = list.get((random.nextInt(list.size())));
           // System.out.println(randomVehicle.getVehicleID() + ">>>>>>>>>>>>>>" + randomVehicle.getCapacity());
           // System.out.println(randomVehicle.getPassengers().size() + "NUMBEROFPASS");

            if(randomVehicle.getPassengers().size() == 1){
                Passenger randomPassenger = randomVehicle.getPassengers().get(0);
                if(randomPassenger.isValidDocumentation()){
                    randomPassenger.setValidDocumentation(false);
                    numberOfFalseDocuments--;
                }
            }else{
            Passenger randomPassenger2 = randomVehicle.getPassengers().get(random.nextInt(randomVehicle.getPassengers().size()));
            if(randomPassenger2.isValidDocumentation()){
                randomPassenger2.setValidDocumentation(false);
                numberOfFalseDocuments--;
            }
        }}

    }
    public void setTrucksOverload(List<Vehicle> list){
        Random random = new Random();
        int randomInt = (random.nextInt(30)+1);
        double multiplier = 1 + (double) randomInt/100;
        int i = 0;
        for (Vehicle vehicle:list) {
            if(vehicle.getMaxCapacity() == 3){
                double tmp = ((Truck)vehicle).getRealLoad() * multiplier;
                ((Truck) vehicle).setRealLoad(tmp);
                i++;
                if(i == 2)
                    break;
            }
        }

    }
    public static boolean moreVehiclesforPoliceTerminal(){
        for (int i = 0 ; i<3 ; i++){
            Terminal terminal = BorderSimulation.terminals.get(i);
            if(!vehicleQueue.isEmpty() || terminal.vehicle != null)
                return true;
        }
        return false;
    }
    public static boolean moreVehiclesforCustomsTerminal(){
        for (Vehicle vehicle:vehicleQueue) {
            if(!vehicle.finished){
                return true;
            }
        }
        return false;
    }

    public void clearFile(File file){
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(""); // empty string
            fileWriter.close();
        } catch (IOException exception) {
            Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
        }
    }
}

