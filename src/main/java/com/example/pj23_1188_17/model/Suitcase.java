package com.example.pj23_1188_17.model;


import java.io.Serializable;
import java.util.Random;

public class Suitcase implements Serializable {

    private boolean legal;
    Random random = new Random();

    public Suitcase(Suitcase suitcase){//Deep copy constructor
        this.legal = suitcase.legal;
    }

    public Suitcase(){
        setLegality();
    }

    public void setLegal(boolean legal){
        this.legal = legal;
    }

    public boolean isLegal() {
        return legal;
    }

    public void setLegality(){
        int chance = random.nextInt(100);
        if(chance <= 10){
            setLegal(false);
        }
        else{
            setLegal(true);
        }
    }

    @Override
    public String toString(){
        if(legal){
            return "Suitcase: Legal";
        }
        else return "Suitcase: Illegal";
    }
}
