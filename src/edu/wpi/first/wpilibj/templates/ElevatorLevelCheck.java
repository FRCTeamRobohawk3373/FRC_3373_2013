package edu.wpi.first.wpilibj.templates;

//(new Thread(new ElevatorLevelCheck())).start();

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.templates.Elevator;

public class ElevatorLevelCheck implements Runnable{

    Elevator elevator = Elevator.getInstance();
    public void run(){
        //(L-R)*A= +-DeltaV
        //LV - DeltaV = L
        //RV + DeltaV = R
        while (true){

        
            double deltaV;
            double c = 0.05;//modifier to make an acceptable value at which to change the speed of elevator motors
        
            deltaV = (elevator.getDegreesL()-elevator.getDegreesR()) * c;
            
            try {
            Thread.sleep(100);
            } catch (InterruptedException e){}
        }
    }
}
