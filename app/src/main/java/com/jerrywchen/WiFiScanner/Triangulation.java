package com.jerrywchen.WiFiScanner;

import android.widget.Toast;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.HashMap;



/**
 * Created by Varun on 5/2/2017.
 */



public class Triangulation {

   // List<double,double> triangluation_coordinates;

   // public Triangulation(List<Double,Double> coordinates){

        //triangluation_coordinates = coordinates;

   //
    private final double min = 0.1; 
    private List<Double> lattitude;
    private List<Double> longitude;
    List<Integer> RSSI;
    private double setPoint;
    public Triangulation(List<Double> lattitude, List<Double> longitude, List<Integer> RSSI){
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.RSSI = RSSI;
    }

    public void restartDataMining(double beginningLattitude, double beginningLongitude){

        for (Double lat:
             lattitude) {

        }

        reset();
    }

    /**Runtime could be of concern**/
    public void testHorizontalTriangulation(double setPoint){
        if(this.lattitude.isEmpty())
            return;

        Double iter;
        Double curMin = (Double) this.min;

        int arraySize = this.lattitude.size();

        //List<Double> horizontalData = new ArrayList<Double>();
        HashMap<Double,Double> horizontalData = new HashMap<>();

        if(this.lattitude.size() != this.RSSI.size()){
            System.out.println("WTF This shit isn't supposed to happen");
            return;
        }

        //Iterate through the list and get the data for partial derivatives
       for(int i = 0; i < arraySize; i++){
           double lattitude = this.lattitude.get(i);
           double RSSI = this.RSSI.get(i);
           if((lattitude - (Double)setPoint) <= curMin)
               horizontalData.put(lattitude,RSSI);
       }

        //Take triangulation data points
        //Compare RSSI values

        //Compare lattitudes
        
    }

    private Double abs(Double value){
        Double abs_value = (value > 0) ? value : (value * -1);

        return abs_value;
    }

    public void testVerticalTriangulation(Double setPoint){
        if(this.longitude.isEmpty())
            return;

        Double curMin = (Double)this.min;

        List<Double> verticalData = new ArrayList<Double>();
        List<Double> verticalSignalStrength = new ArrayList<Double>();
        for (Double longitude:
             this.longitude) {
            if(abs(longitude - setPoint) <= curMin) {
                verticalData.add(longitude);
            }
        }
    }

    public void totalTriangulation(){

    }

    public void finalizeTriangulation(){

        //Toast.makeText(this,"Triangulation finished", Toast.LENGTH_LONG).show();
        reset();
    }
    /**Restart triangulation**/
    private void reset(){
        lattitude.clear();
        lattitude.clear();
        RSSI.clear();
    }

}


