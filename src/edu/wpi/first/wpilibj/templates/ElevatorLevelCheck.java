package edu.wpi.first.wpilibj.templates;

//(new Thread(new ElevatorLevelCheck())).start();

import edu.wpi.first.wpilibj.AnalogChannel;

public class ElevatorLevelCheck implements Runnable{

    public void run(){
        //(L-R)*A= +-DeltaV
        //LV - DeltaV = L
        //RV + DeltaV = R
        while (!(isInDeadband(currentPosition))){
            AnalogChannel stringPotL = new AnalogChannel(3);//just a place holder
            AnalogChannel stringPotR = new AnalogChannel(7);//just a place holder
        
            double deltaV;
            double c = 0.05;//modifier to make an acceptable value at which to change the speed of elevator motors
        
            deltaV = (getDegreesL()-getDegreesR()) * c;
            
            try {
            Thread.sleep(100);
            } catch (InterruptedException e){}
        }
    }
}
