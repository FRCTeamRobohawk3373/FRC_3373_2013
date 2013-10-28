/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;

public class Drive {
    // Since drive is tied directly to PWM hardware ports, allow only one
    // instance of Drive to ever be created.  Provide a getInstance() method
    // to return the one and only Drive object to other classes that
    // need to use it. This is known as a Singleton pattern.
    private static final Drive instance = new Drive();
    private Drive() {}  //Prevents other classes from declaring new Drive()
    public static Drive getInstance() {
        return instance;
    }    
    RobotDrive mechanum = new RobotDrive(1,2,4,3);
    double speed = 0.50; //Default Speed
    //int driverPerspective = 0;
    
    public void drive(double driveLX, double driveRX, double driveLY){ //these are the correct variable names.....
    mechanum.setSafetyEnabled(false);
    mechanum.mecanumDrive_Cartesian(driveLX * speed, -driveRX * speed, -driveLY * speed, 0); //Sets the motor speeds; Negative is to set forward to be forward
    
    }
    
    public void setSpeed(boolean sniper, boolean turbo){
        
        if(turbo && !sniper){
            speed = 1.00; //In turbo mode
        } else if(!turbo && sniper){
            speed = 0.25; //In sniper mode
        } else {
            speed = 0.75; //In default mode
        }
    }
   /* public void perspectiveControl(boolean a, boolean b, boolean x, boolean y){
       if (y){
           driverPerspective = 0;
       } if (a){
           driverPerspective = 1;
       } if (x){
           driverPerspective = 2;
       } if (b){
           driverPerspective = 3;
       }
    }*/

}
