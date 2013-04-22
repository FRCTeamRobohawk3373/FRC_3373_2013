/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author Nick
 */
public class CameraControl {

    Servo cameraServo = new Servo(9);
    double servoTarget = .79;
    double servoMin = .6;
    double servoMax = 1;
    double minDegrees = 89.5;
    double maxDegrees = 45;
    double slope;
    double servoAngle;

    public void move(double joystick) {

        //Change this variable to change the speed that the servo moves. 1000 is most likely too low.
        double change = joystick / 1000;//This number is to compensate for it running this code millions of times per second
        //System.out.println("Change: " + change);
        if (!((cameraServo.get() + change) < 0.00) & !((cameraServo.get() + change) > 1.00)) {//Checks for correct values in the range of the servo

            cameraServo.set(cameraServo.get() + change);//Sets the servo to move

        }
    }
    public void moveTest(double joystick){
        if (joystick > .2 || joystick <-.2){    
        servoTarget += (joystick)/500;
        }
        if (servoTarget > 1) {servoTarget = 1;}
        if (servoTarget < 0) {servoTarget = 0;}
            //System.out.println("Servo: " + servoTarget);
            if (servoTarget > .6 && servoTarget < 1);
            cameraServo.set(servoTarget);
        
    }
    public double servoAngleMath(){
        slope = (maxDegrees - minDegrees)/(servoMax - servoMin);
        servoAngle = (slope * cameraServo.get());
        return servoAngle;
    }
}
