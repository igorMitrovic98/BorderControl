package com.example.pj23_1188_17.model.vehicles;

import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.Passenger;
import com.example.pj23_1188_17.model.terminals.Terminal;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Truck extends Vehicle{

    private double declaredLoad;
    private double realLoad;
    private boolean requiredDocumentation;
    private Random random = new Random();

    public Truck (Truck vehicle){ //Deep copy constructor
        this.setPosition(vehicle.getPosition());
        this.realID = vehicle.realID;
        this.waitingTime=vehicle.waitingTime;
        this.maxCapacity =vehicle.maxCapacity;
        this.declaredLoad =vehicle.declaredLoad;
        this.realLoad = vehicle.realLoad;
        this.requiredDocumentation = vehicle.requiredDocumentation;
        this.picture = new File(System.getProperty("user.dir") + File.separator + "pictures" + File.separator + "truck.png");
        this.capacity = vehicle.getCapacity();
        for(Passenger passenger: vehicle.passengers) {
            this.passengers.add(new Passenger(passenger));
        }
    }


    public Truck(){
        this.waitingTime=500;
    this.maxCapacity = 3;
    this.declaredLoad = random.nextInt(10)+2.5;
    this.realLoad = declaredLoad;
    this.requiredDocumentation = random.nextBoolean();
    this.picture = new File(System.getProperty("user.dir") + File.separator + "pictures" + File.separator + "truck.png");
    this.capacity = random.nextInt(maxCapacity-1)+1;
    addPassengers(passengers);
    }

    public void setDeclaredLoad(double declaredLoad) {
        this.declaredLoad = declaredLoad;
    }

    public boolean isRequiredDocumentation() {
        return requiredDocumentation;
    }
    public void setRequiredDocumentation(boolean set){
        this.requiredDocumentation = set;
    }

    public double getRealLoad() {
        return realLoad;
    }

    public void setRealLoad(double realLoad) {
        this.realLoad = realLoad;
    }

    public double getDeclaredLoad() {
        return declaredLoad;
    }

    public boolean isOverloaded(){
        if(realLoad > declaredLoad){
            return true;
        }
        else return false;
    }

    @Override
    public boolean checkValidityofTerminal(Terminal terminal){
        if(!terminal.isNormalVehicles()){
            return true;
        }else return false;
    }
    @Override
    public String toString(){
        String passengersInfo = "";
        for (Passenger passenger:this.getPassengers()) {
            passengersInfo += passenger.toString();
        }
        String tmp = String.valueOf(this.getPosition());
        if(this.getPosition()<0){
            tmp = "No longer in the queue";
        }
        String string ="Vehicle ID: "+this.getVehicleID()+ "\nCapacity: "+this.capacity+
                "\nPosition in queue: "+tmp+"\n" +"Has required documentation: "+this.isRequiredDocumentation()
                +"\n"+"Overloaded: "+this.isOverloaded()+"\n"+
                "Information about passengers: \n"+passengersInfo;
        return string;
    }


}
