/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

//(new Thread(new ElevatorLevelCheck())).start();

import edu.wpi.first.wpilibj.AnalogChannel;

public class ElevatorLevelCheck implements Runnable{
    

    
    public ElevatorLevelCheck(){
    }
    
    public void run(){
        //(L-R)*A= +-DeltaV
        //LV - DeltaV = L
        //RV + DeltaV = R
        while (!(isInDeadband(currentPosition))){
            AnalogChannel stringPotL = new AnalogChannel(3);//just a place holder
            AnalogChannel stringPotR = new AnalogChannel(7);//just a place holder
        
            double deltaV;
            double c = 5;//modifier to make an acceptable value at which to change the speed of elevator motors
        
            deltaV = (stringPotL.getVoltage()-stringPotR.getVoltage()) * c;
            
            Thread.sleep(100);
            
        }
    }
    
}
