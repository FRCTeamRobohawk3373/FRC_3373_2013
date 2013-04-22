/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;
import java.util.Hashtable;

/**
 *
 * @author Jamie Dyer
 */
public class TableLookUp {
    double[] distanceMiddle = {10,15,20,25,30,35};// this is for the middle target
    double[] angleMiddle = {2.279,2.017,1.318,1.256,0.865,0.824};//these values are in voltage not Angles! for the middle target
    double[] distanceHigh = {10,15,20,25,30,35};// this is for the middle target
    double[] angleHigh = {2.454,2.017,1.467,1.601,1.220,0.824};//these values are in voltage not Angles! for the middle target
   
    //double[] rpmArray = {max, max, max};//these are just demonstration code, not actual code, TODO: test!
    int match;
    double anglePercentage;
    double difference;
    double rpmPercentage;
    double result;
    public void test(){
    Hashtable hash = new Hashtable();
    hash.put(new Double(1.0), new Double(1.0));
    hash.put(new Double(2.0), new Double(2.0));
    hash.put(new Double(3.0), new Double(3.0));
    hash.put(new Double(4.0), new Double(4.0));
    hash.put(new Double(5.0), new Double(5.0));
    //double hashDouble = hash.getValue(1.0);
    //System.out.println(hash.toString());
    }

    public double lookUpAngle(double currentDistance, double[] distanceArray, double[] angleArray){//pass in our distance, and the arrays for the target we are aiming array
        int i;
        if(currentDistance < distanceArray[distanceArray.length - 1]){
            for (i = 0; i < distanceArray.length; i++){//searches for a match of distance in angle array
                if (currentDistance == distanceArray[i]){
                    return angleArray[i];
                } else if(currentDistance>distanceArray[i] && currentDistance < distanceArray[i+1]){//used to find two values to interperlate 
                    difference = distanceArray[i+1] - distanceArray[i];
                    anglePercentage = (currentDistance-distanceArray[i])/difference;
                    result = angleArray[i]+((angleArray[i+1]-angleArray[i]) * anglePercentage);
                    return result;
                }   
        }
        } else{
            return 0;
        }
        //we will never get here
        return 0;
    }
    /*public double lookUpRpm(double currentDistance){
        int i;
        if(currentDistance < distanceArray[distanceArray.length - 1]){
            for (i = 0; i < distanceArray.length; i++){//searches for a match of distance in RPM array
                if (currentDistance == distanceArray[i]){
                    return rpmArray[i];
                } else if(currentDistance>distanceArray[i] && currentDistance < distanceArray[i+1]){//used to find two values to interperlate 
                    difference = distanceArray[i+1] - distanceArray[i];
                    rpmPercentage = (currentDistance-distanceArray[i])/difference;
                    result = rpmArray[i]+((rpmArray[i+1]-rpmArray[i]) * rpmPercentage);
                    return result;
                }   
        }
        } else{
            return 0;
        }
        //we will never get here
        return 0;
    }*/
}
