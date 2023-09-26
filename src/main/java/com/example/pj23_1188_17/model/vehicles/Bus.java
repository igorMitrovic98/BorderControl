package com.example.pj23_1188_17.model.vehicles;

import java.io.File;
import java.util.Random;

import com.example.pj23_1188_17.model.Passenger;
import com.example.pj23_1188_17.model.terminals.*;

public class Bus extends Vehicle{

    Random random = new Random();

    public Bus(Bus vehicle){//Deep copy constructor
        this.setPosition(vehicle.getPosition());
        this.realID = vehicle.realID;
        this.waitingTime = 100;
        this.maxCapacity=vehicle.maxCapacity;
        this.capacity=vehicle.capacity;
        for(Passenger passenger: vehicle.passengers) {
            this.passengers.add(new Passenger(passenger));
        }
        this.picture= new File(System.getProperty("user.dir") + File.separator + "pictures" + File.separator + "bus.png");

    }

    public Bus(){
        this.waitingTime = 100;
        this.maxCapacity = 52;
        this.capacity = random.nextInt(maxCapacity-1)+1;
        this.picture = new File(System.getProperty("user.dir") + File.separator + "pictures" + File.separator + "bus.png");
        addPassengers(passengers);
    }

    @Override
    public boolean checkValidityofTerminal(Terminal terminal){
        if(terminal.isNormalVehicles()){
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
                "\nPosition in queue: "+tmp+"\n" +
                "Information about passengers: \n"+passengersInfo;
        return string;
    }
}
