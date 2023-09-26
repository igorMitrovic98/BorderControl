package com.example.pj23_1188_17.model;

import java.io.Serializable;
import java.util.Random;

public class Passenger implements Serializable {

    private static int ID = 1;
    private int realID;
    private boolean Driver;
    private boolean validDocumentation;
    private Suitcase suitcase = null;
    private boolean suitcasePresent;
    Random random = new Random();

    public Passenger(Passenger passenger){//Deep copy constructor
        this.realID = passenger.realID;
        this.Driver = passenger.Driver;
        this.suitcasePresent = passenger.suitcasePresent;
        if(this.suitcasePresent){
            this.suitcase=new Suitcase(passenger.suitcase);
        }
        this.validDocumentation=passenger.validDocumentation;
    }

    public Passenger(){

        this.validDocumentation = true;
        this.realID = ID++;
    }

    public void setDriver(boolean driver){
        this.Driver = driver;
    }
    public boolean isDriver (){
        return this.Driver;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return realID;
    }

    public boolean isSuitcasePresent() {
        return suitcasePresent;
    }

    public void setSuitcasePresent(boolean suitcasePresent) {
        this.suitcasePresent = suitcasePresent;
    }

    public boolean isValidDocumentation() {
        return validDocumentation;
    }

    public void setValidDocumentation(boolean validDocumentation) {
        this.validDocumentation = validDocumentation;
    }

    public Suitcase getSuitcase(){return suitcase;}

    public void setSuitcase(){
        int chance = random.nextInt(100);
        if(chance <= 70){
            this.suitcase = new Suitcase();
            setSuitcasePresent(true);
        }
        else {
            setSuitcasePresent(false);
        }
    }

    @Override
    public String toString(){
        String tmp ="";
        if(this.isSuitcasePresent()) {
            tmp = this.suitcase.toString()+"\n";
        }
        return "     Passenger ID: "+ this.getID()+";Valid Documents: "+this.isValidDocumentation()+"; Is Driver: "
                + this.isDriver()+"\n"+"Has suitcase: "+ this.isSuitcasePresent()+";\n"+ tmp;
    }
}
