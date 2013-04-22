/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PickupArm {
    
    
    Relay grabSpike = new Relay(2);
    Relay vacuumSpike = new Relay(1);
    Talon rotateTalon = new Talon(10); //used to be 9, moved to 10 to avoid conflicts
    AnalogChannel pot1 = new AnalogChannel(7);
    DigitalInput armLimit = new DigitalInput(3); //returns true if clicked
    Timer robotTimer = new Timer();
    
    int grabStatus = 1;
    int rotateStatus = 1;
    String grabString;
    double lastTime;
    double previousTime;
    boolean pickUpFlag = true;
    boolean rotateFlag = false;
    boolean upDownFlag = true;
    boolean vacuumFlag = false;
    boolean demoOnFlag = false;
    double currentPosition;
    int demoStatus = 0;
    double targetRotatePosition;
    double currentTime;
    boolean vacuumTrigger = false;
    //Team3373 team;
    

    public void rotateEnabled(){
        rotateFlag = true;
    }
    public void rotateDisabled(){
        rotateFlag = false;
    }
    public void armUp(){//TODO: make thread and sleep for 2 seconds while running
            /*final Thread thread = new Thread(new Runnable() {
           public void run(){
               grabSpike.set(Relay.Value.kForward);
               System.out.println("Going up");
               try{
               upDownFlag = true;
               isMovePossible = false;
               Thread.sleep(2000L);
               System.out.println("sleepingUp");
               }
               catch(InterruptedException e){
            
                }
               grabSpike.set(Relay.Value.kOff);
               upDownFlag = false;
               isMovePossible = true;
           } 
        });
            thread.start();*/
        if (pickUpFlag){//so we don't go up and go down
            lastTime =  robotTimer.get();
             grabSpike.set(Relay.Value.kForward);
            pickUpFlag = false;
            upDownFlag = true;
            
        } else if (!pickUpFlag && ( robotTimer.get() - lastTime) >= 2){
             grabSpike.set(Relay.Value.kOff);
            pickUpFlag = true;
            upDownFlag = false;
        }
    }
    public void armDown(){//TODO: make thread and sleep for 2 seconds while running
           /* final Thread thread = new Thread(new Runnable() {
           public void run(){
               grabSpike.set(Relay.Value.kReverse);
               try{
               upDownFlag = true;
               Thread.sleep(2000L);
               System.out.println("sleepingDown");
               }
               catch(InterruptedException e){
            
                }
               grabSpike.set(Relay.Value.kOff);
               upDownFlag = false;
           } 
        });
            thread.start();
        */
        if (pickUpFlag){
            lastTime =  robotTimer.get();
             grabSpike.set(Relay.Value.kReverse);
            pickUpFlag = false;
            upDownFlag = true;
        } else if (!pickUpFlag && ( robotTimer.get() - lastTime) >= 2){
             grabSpike.set(Relay.Value.kOff);
            pickUpFlag = true;
            upDownFlag = false;
            
        }
    
    }
    
    public void createVacuum(boolean vacuumBoolean) {//used to create suction after arm goes down to grab frisbee
        grabString = Integer.toString(grabStatus);
        
            //System.out.println("in Vacuum Creation Mode");
            /*System.out.println("Grab status :" + grabStatus);
            System.out.println("VacuumFlag: " + vacuumFlag);
            System.out.println("vacuumBoolean: " + vacuumBoolean);
            System.out.println("current - last time: " + (robotTimer.get() - currentTime));
            System.out.println("armLimit: " + armLimit.get());
            System.out.println("RobotTimer: " + robotTimer.get());
            System.out.println("UpDownFlag: " + upDownFlag);
            System.out.println("UpDown Time Comparison: " + (robotTimer.get() - lastTime));*/
            if (vacuumBoolean) {
            switch(grabStatus){

                case 1:// parked and signal to run
                    vacuumFlag = false; 
                    vacuumSpike.set(Relay.Value.kReverse);
                     boolean solenidFlag = true;
                     currentTime = robotTimer.get();
                     grabStatus = 2;
                    break;
                case 2: //running
                     vacuumSpike.set(Relay.Value.kReverse);
                    if( armLimit.get() && (robotTimer.get() - currentTime) >= .5){
                        grabStatus = 3;  
                    }
                    break;
                case 3: //back home, off
                     vacuumSpike.set(Relay.Value.kOff);
                    vacuumFlag = true;
                     grabStatus = 0;                   
                }
            }

        
         

    }
        public void rotate(double target){ //moves arm to target, or doesn't move if arm is close.
          if (Math.abs(target - currentPosition) <= .05){
              rotateTalon.set(0);
          } else {
            if(target > currentPosition && currentPosition <= 2.8){
               rotateTalon.set(-1);
            } 
            if (target < currentPosition && currentPosition >= 2.0){
               rotateTalon.set(1);
            }
            SmartDashboard.putNumber("CurrentPosition: ", currentPosition);
            SmartDashboard.putNumber("Target Difference: ", Math.abs(target-currentPosition));
            SmartDashboard.putNumber("RotateTarget: ", target);
          }
        }
        public boolean isArmClose(){
            if (Math.abs(2.7-currentPosition) <= .05){
                return true;
            } else {
               return false;
            }
        }
        public void demo(){
            rotate(2.7);
        }
        public void armDemo(){ 
         if (demoOnFlag){    
             //armDown();
             SmartDashboard.putNumber("DemoStatus: ", demoStatus);
             //System.out.println("In arm demo");
             //System.out.println(demoStatus);
             switch (demoStatus) {
                 
                  case 0:
                      targetRotatePosition = 2.7;
                      //rotate(team.targetRotatePosition);
                      //System.out.println("Moving");
                      if (Math.abs(2.7-currentPosition) <= .05){
                          System.out.println("In case 0 if");
                          demoStatus = 1;
                      }
                      break;
                  case 1:
                      
                        armDown();
                      
                      System.out.println("ArmDown");
                      if (!upDownFlag){
                        System.out.println("In case 1 if");
                          demoStatus = 2;
                          grabStatus = 1;
                          vacuumTrigger = true;
                      }
                      break;
                  case 2:
                      System.out.println("Creating Vacuum");
                      if (vacuumFlag){
                          vacuumTrigger = false;
                          System.out.println("In case 2 if");                          
                          demoStatus = 3;
                      }
                      break;
                  case 3:
                      
                        armUp();
                      
                      System.out.println("armUp");
                      //upDownFlag = false;
                      if (!upDownFlag){
                          System.out.println("In Case 3 if");
                          demoStatus = 4;
                      }
                      break;
                  case 4:
                      targetRotatePosition = 2.3;
                      //rotate(team.targetRotatePosition);
                      System.out.println("Going home");
                      vacuumFlag = false;
                      demoOnFlag = false;
                      break;
              }
              
         }
         }            
        }

