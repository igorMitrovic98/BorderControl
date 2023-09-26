package com.example.pj23_1188_17.model.terminals;

import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.controller.MainWindowController;
import com.example.pj23_1188_17.model.Passenger;
import com.example.pj23_1188_17.model.vehicles.Vehicle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public abstract  class Terminal extends Thread {

    private static int terminalID = 1;
    protected int realID;
    public static List<Vehicle> illegalVehicle = new ArrayList<>();
    protected boolean normalVehicles;
    public  ReentrantLock Tlock = new ReentrantLock();
    public Object pauseInserted = new Object();
    protected boolean pause = false;
    public Vehicle vehicle;
    protected File picture;



    public Terminal(){

        this.realID = terminalID++;
    }

    public static List<Vehicle> getIllegalVehicle(){
        return illegalVehicle;
    }
    public boolean isNormalVehicles(){
        return normalVehicles;
    }
    public void setNormalVehicles(boolean normalVehicles){

        this.normalVehicles = normalVehicles;
    }
    public boolean isPause(){

        return pause;
    }
    public void setPause(boolean pause){

        this.pause = pause;
    }
    public int getTerminalID(){

        return realID;
    }

    public File getPicture() {
        return picture;
    }

    public abstract void processVehicle();
    protected void isPauseClicked(){
        while(MainWindowController.pause) {
            try {
                synchronized (MainWindowController.pauseLock) {
                    System.out.println("THIS TERMINAL WAITSSSS   "+this.getTerminalID());
                    MainWindowController.pauseLock.wait();
                    System.out.println("THIS Terminal CONTINUES !!!   ");
                }
            } catch (InterruptedException exception) {
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString()+this.getTerminalID());
            }
        }
    }

    protected void isPauseInserted(){
        while(this.pause){
            try{
                synchronized (this.pauseInserted){
                    this.pauseInserted.wait();
                }
            }catch (InterruptedException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());
            }
        }
    }

}
