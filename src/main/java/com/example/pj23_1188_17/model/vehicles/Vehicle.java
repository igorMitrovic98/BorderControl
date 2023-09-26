package com.example.pj23_1188_17.model.vehicles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.controller.MainWindowController;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.terminals.*;

import com.example.pj23_1188_17.model.*;

public abstract class Vehicle extends Thread {

    protected int capacity;
    protected File picture;
    private static int vehicleID = 1;
    protected int realID;
    protected int position;//private
    protected List<Passenger> passengers = new ArrayList<>();
    public boolean finished = false;
    protected int maxCapacity;
    protected int waitingTime; //in miliseconds

    //for locking the resources, synchronization
    public static Object resource1 = new Object();
    public  Object resource2 = new Object();
    public static Object pom = new Object();
    public static Object resource3 = new Object();

    public static ReentrantLock customsLock = new ReentrantLock();

    //for positioning the vehicle
    public boolean waitingPolice = true;
    public boolean waitingCustoms;
    public boolean atPolice = false;
    public boolean atCustoms = false;
    public boolean donePolice = false;
    public boolean doneCustoms = false;
    public boolean waitingInQueue = true;
    public boolean processedC ;
    public boolean processedP ;


    protected PoliceTerminal policeTerminal;
    protected CustomsTerminal customsTerminal;

    public Vehicle() {

        this.realID = vehicleID++;
        this.processedP = true;
        this.processedC = false;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public int getVehicleID() {
        return realID;
    } //REALID,not vehicleID !!!!!

    public void setVehicleID(int newID) {
        this.realID = newID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public File getPicture() {
        return picture;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    //redifine in car,bus,truck
    public abstract boolean checkValidityofTerminal(Terminal terminal);

    protected void addPassengers(List<Passenger> list) {

        for (int i = 0; i < this.capacity; i++) {
            Passenger passenger = new Passenger();
            if (i == 0) {
                passenger.setDriver(true);
            }
            if (this.maxCapacity == 52 && !passenger.isDriver()) {
                passenger.setSuitcase();
            }
            list.add(passenger);
        }

    }

    public void run() {

        while (!this.finished) {
            if (this.position == 0) {
//                resource3.lock();
//                try {

                do {
                //    System.out.print("HERE IS THE VEHICLE:"+this.getVehicleID() + "//////\n");
                    gotoPoliceTerminal();

                } while (!this.atPolice);
//            }finally {
//                  resource3.unlock();
//                }
                synchronized (this) {
                    while (this.processedP) {
                        try {
                            //System.out.println("THIS VEHICLE IS WAITING ///" + this.getVehicleID());
                            this.wait();
                        } catch (InterruptedException exception) {
                            Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
                        }
                        this.processedP =false;
                        //System.out.println("THIS VEHICLE IS WORKING ///" + this.getVehicleID());
                    }}
            } else if(this.position > 0 && !this.donePolice){
                synchronized (resource3) {
                    if (this.waitingInQueue) {
                        try {
                           // System.out.println("I am the one who WAITS:"+this.getVehicleID());
                            resource3.wait();
                        } catch (InterruptedException e) {
                            Run.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                    }
                    synchronized (this) {
                        this.position--;
                        this.waitingInQueue = true;
           }
                if(this.position < 5 && this.position>=0){
                    MainWindowController.removeFromQueue(position);
                MainWindowController.updateQueueGUI(this);
                }
                }
            }

            else if (this.donePolice) {
//                resource3.lock();
//                try {
                    do {
                        gotoCustomsTerminal();
                    } while (!this.atCustoms);


//                } finally {
//                    resource3.unlock();
//                }
                //System.out.println("FOR VEHICLE: "+this.getVehicleID()+"////"+"CUSTOMS IS: "+this.customsTerminal.getTerminalID());

            }
        }
    }


    public void gotoPoliceTerminal() {
//        resource3.lock();
//        try {
            do {
                choosePoliceTerminal();

            } while (!this.atPolice);
//        }
//        finally {
//            resource3.unlock();

    }



    public void choosePoliceTerminal() {
        for (int i = 0; i < 3 && i < BorderSimulation.terminals.size(); i++) {
            Terminal terminal = BorderSimulation.terminals.get(i);
            //check if terminal is empty, if the car is not already on terminal,if the terminal is the correct one
            if (!this.atPolice && terminal.vehicle == null && checkValidityofTerminal(terminal)) {
               // synchronized (BorderSimulation.vehicleQueue) {
                    synchronized (this) {
                       // System.out.println(this.getVehicleID() + " This is id");
                        //placing vehicle on the police terminal
                        this.waitingPolice = false;
                        this.atPolice = true;
                        this.policeTerminal = (PoliceTerminal) terminal;
                        this.waitingInQueue = false;
                    }
                synchronized (terminal) {
                    terminal.vehicle = this;
                   // System.out.println(this.getVehicleID()+">>>>>>>>>>>>>>"+terminal.getTerminalID() +"+"+terminal.vehicle.getVehicleID());
                    MainWindowController.removeFromQueue(0);
                    MainWindowController.updateTerminalGUI(this, terminal);
                }
               // System.out.println(terminal.getTerminalID() + "////" + terminal.vehicle.getVehicleID());
                //System.out.println("VEHICLE ON THE TERMINAL :" + this.getVehicleID());

                synchronized (pom) {
                    reorganizeQueue();
                    //System.out.println("DO YOU EVEN WORK?");
                }
                this.position--; //basicly any negative number , so it doesn't move with the rest of the queue
                if (!this.donePolice && this.policeTerminal.vehicle == this){
                 //   System.out.println(this.getVehicleID()+"--------------");
                    synchronized (policeTerminal.Releaselock) {
                       // System.out.println("This is sending notify !"+this.getVehicleID()+"+"+this.policeTerminal.getTerminalID());
                       // System.out.println("TERMINALS VEHICLE:" + this.policeTerminal.vehicle.getVehicleID());
                        this.policeTerminal.Releaselock.notify();
                    }}

                //break;
                //
            }

           // System.out.println("GOOD BREAK"+"="+this.getVehicleID());
        }
        checkIfStuckPolice();

    }
    public void checkIfStuckPolice(){
        if (this.policeTerminal == null) {
            synchronized (this) {
                waitingPolice = true;
            }
            while (waitingPolice) {
                synchronized (resource1) {
                    try {
                        resource1.wait();// after .notify(), should be able to go to police terminal
                    } catch (InterruptedException e) {
                        Run.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                    }
                    this.waitingPolice=false;
                }
            }
        }
    }
    public void checkIfStuckCustoms(){
        if (this.customsTerminal == null) {
            waitingCustoms = true;
            while (waitingCustoms) {
                synchronized (resource2) {
                    try {
                        resource2.wait(); // same like police , should work with notify()
                    } catch (InterruptedException e) {
                       Run.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                    }
                    waitingCustoms = false;
                }
            }
        }
    }

    public void gotoCustomsTerminal() {
       // resource3.lock();
//        try {
            do {
                chooseCustomsTerminal();
            } while (!this.atCustoms);
//        } finally {
//            resource3.unlock();
        synchronized (this.policeTerminal) {
            this.policeTerminal.vehicle = null;
            MainWindowController.removeFromPoliceTerminal(this.policeTerminal);
        }
        synchronized (customsTerminal.lock){

            customsTerminal.lock.notify();//was calling the wrong object the whole time ....
        }

        for (Vehicle vehicle:BorderSimulation.vehicleQueue) {
            if(vehicle.checkValidityofTerminal(this.policeTerminal) && vehicle.getPosition() == 0 && vehicle.waitingPolice){
                synchronized (vehicle){
                    if(vehicle.waitingPolice){
                        synchronized (vehicle.resource1){
                            vehicle.resource1.notify();
                        }
                        break;
                    }
                }
            }
        }
            //MainWindowController.removeFromPoliceTerminal(this.policeTerminal);
        synchronized (this) {
            processedC = true;
            atCustoms = true;
            while (processedC) {
                try {

                        this.wait();
                } catch (InterruptedException exception) {
                    Run.logger.log(Level.SEVERE, exception.fillInStackTrace().toString());
                }
            }
            this.finished = true;
            BorderSimulation.vehicleQueue.remove(this);
            //System.out.println("Vehicle " + this.getVehicleID() + " successfully crossed the BORDER!!!");
        }
    }


    public void chooseCustomsTerminal() {
        for (int i = 3; i < BorderSimulation.terminals.size(); i++) {
            Terminal terminal = BorderSimulation.terminals.get(i);
            //check if terminal is empty, if the car is not already on terminal,if the terminal is the correct one
            if (!this.atCustoms && terminal.vehicle == null && checkValidityofTerminal(terminal)) {

                //placing vehicle on the customs terminal
                synchronized (terminal){
                terminal.vehicle = this;
             //   System.out.println("carinski>>>"+terminal.vehicle.getVehicleID());
                MainWindowController.updateTerminalGUI(this, terminal);
                }
                //this.waitingCustoms = false;

                this.customsTerminal = (CustomsTerminal) terminal;
                synchronized (this) {
                    this.atCustoms = true;
                }
                break;

            }


            }
        checkIfStuckCustoms();

        }


    public void reorganizeQueue() {
        synchronized (BorderSimulation.vehicleQueue){
            try{
                this.sleep(300);
            }catch (InterruptedException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }
            for (Vehicle vehicle : BorderSimulation.vehicleQueue) {
                //making it false so the vehicles can get past the if statement in run()
                ////making vehicles in queue one position closer to the head of the queue, but still not allowed to search for police terminal
                synchronized (vehicle) {
                    vehicle.waitingInQueue = false;
                }
            }
                //System.out.println("Vehicle: "+vehicle.getVehicleID()+"---position---"+vehicle.getPosition());


                synchronized (resource3){
                    resource3.notifyAll();
                }
        }
               // }
                //System.out.println("FINISHED: "+vehicle.getVehicleID()+"   "+vehicle.finished );
                }
            //}


    }




