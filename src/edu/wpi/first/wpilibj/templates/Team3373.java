/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.lang.Math;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.RobotDrive;
//import edu.wpi.first.wpilibj.SimpleRobot;
//import edu.wpi.first.wpilibj.templates.Shooter;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory. 
 */
public class Team3373 extends SimpleRobot{
    /**
     * This function is called once each time the robot enters autonomous mode.
     */

   DriverStationLCD LCD = DriverStationLCD.getInstance();
   //SmartDashboard smartDashboard;
   SuperJoystick driveStick = new SuperJoystick(1); 
   SuperJoystick shooterController = new SuperJoystick(2);
   ElevatorLevelCheck levelCheck = new ElevatorLevelCheck();
   
   //Deadband objDeadband = new Deadband();
   Timer robotTimer = new Timer();
   PickupArm arm = new PickupArm();
   Elevator elevator = Elevator.getInstance();
   Shooter objShooter = Shooter.getInstance();
   Camera camera = new Camera();
   DigitalInput frontBackSwitch = new DigitalInput(13);
   DigitalInput leftRightSwitch = new DigitalInput(12);
   CameraControl cameraControl = new CameraControl(); //TODO: Fix camera PWM 
   double rotateLimitMaximum = 4.8;//are these used?
   double rotateLimitMinimum = 0.2;//are these used?
   Drive drive = Drive.getInstance();
   Deadband deadband = new Deadband();
   NewMath newMath = new NewMath();
   TableLookUp lookUp = new TableLookUp();
   boolean test;
   boolean solenidFlag=false;
   
  /*********************************
   * Math/Shooter Action Variables *
   *********************************/
   
   TableLookUp objTableLookUp = new TableLookUp();
   
   double ShooterSpeedStage2 = 0;//was StageTwoTalon.get()
   double percentageScaler = 0.75;
   double ShooterSpeedStage1 = ShooterSpeedStage2 * percentageScaler;//was StageOneTalon.get()
   
   double ShooterSpeedMax = 5300.0;
   double ShooterSpeedAccel = 250;
   double stageOneScaler = .5; //What stage one is multiplied by in order to make it a pecentage of stage 2
   double PWMMax = 1; //maximum voltage sent to motor
   double MaxScaler = PWMMax/5300;
   double ShooterSpeedScale = MaxScaler * ShooterSpeedMax; //Scaler for voltage to RPM. Highly experimental!!
   double target;
   double RPMModifier = 250;
   double idle = 1 * ShooterSpeedScale;
   double off = 0;
   double change;
   
   double startTime = 9000000;
   double backTime = 90000000;
   double aTime = 900000000;
   double bTime = 900000000;
   double targetRotatePosition;
   boolean manualToggle;
   double manualStatus;
   boolean armTestFlag;
   boolean canShoot;
   int LX = 1;
   int LY = 2;
   int Triggers = 3;
   int RX = 4;
   int RY = 5;
   int DP = 6;
   double rotateTest = 2.7;
   double autonomousSpeedTarget = 1;
   boolean autoFlag = true;
   double feedAngle = 26.7;
   double climbAngle = 36.3;
   double autoTarget;
   double backAuto = 29.5;
   double frontAuto = 37.0;
   double backMiddle = 1.567; 
   double[] targetSlot;
   double[] targetAngle;
   
   //double climbingPosition = 2.75;
   boolean controlFlag = true;
           
   public Team3373(){
      //camera.robotInit();
   }

    public void autonomous() {
        cameraControl.moveTest(0);
        if (isAutonomous() && isEnabled()){
            elevator.canRun = true;
            camera.canRun = true;
            objShooter.canRun = true;                
            if (frontBackSwitch.get()){ //further away, right side, value returned is also feed/climb position
                SmartDashboard.putString("autonomus location: ", "From back of period");
                elevator.goToPotAngle(backAuto);;//lookUp.lookUpAngle(18, lookUp.distanceHigh, lookUp.angleHigh);
                //System.out.println("Target1="+autoTarget);
            } else { //close, right
                elevator.goToPotAngle(frontAuto);;//lookUp.lookUpAngle(10, lookUp.distanceHigh, lookUp.angleHigh);
                SmartDashboard.putString("autonomus location: ", "From front of pyramid");
                //System.out.println("Target3="+autoTarget);
            }
            
                
                
            //autoTarget = 3.16;

            
            objShooter.goToSpeed(autonomousSpeedTarget*.33);
                  
            try {
                Thread.sleep(300L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            objShooter.goToSpeed(autonomousSpeedTarget * .66);

            try {
                Thread.sleep(300L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            objShooter.goToSpeed(autonomousSpeedTarget);

            try {
                Thread.sleep(1500L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
                    
            if (leftRightSwitch.get()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } 
            
            for (int i = 0; i <= 2; i++){
                
                objShooter.shoot();
                
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                objShooter.loadFrisbee(elevator);
                
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
                    //drive.drive(-1, 0, 0);
        }

    }
    

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void disabled(){
        if (isDisabled()){
            for (int i = 0; i < 5; i++){
                camera.distanceTimeOut = 0;
                manualToggle = false;
                armTestFlag = false;
                arm.demoOnFlag = false;
                targetRotatePosition = arm.pot1.getVoltage(); 
                arm.demoStatus = 0;
                elevator.elevationTarget = elevator.angleMeter.getVoltage();
                cameraControl.servoTarget = .79;
                objShooter.busyStatus = true;
                camera.distanceFlag = false;
                controlFlag = true;
                autoFlag = true;
                objShooter.stageOneTalon.set(0);
                objShooter.stageTwoTalon.set(0);
                camera.distFlag = false;
                elevator.elevatorTalonL.set(0);
                elevator.elevatorTalonR.set(0);
                elevator.canRun = false;
                camera.canRun = false;
                objShooter.canRun = false;
                elevator.elevateFlag = true;
                elevator.isThreadRunningR = false;
                elevator.isThreadRunningL = false;
                elevator.elevatorTalonR.set(0);
                elevator.elevatorTalonL.set(0);
                elevator.isEnabledFlag = false;
            }
        }
    }
    
    public void operatorControl() {
        camera.distFlag = true;
        robotTimer.start();
        
        if (isEnabled()){
            elevator.canRun = true;
            camera.canRun = true;
            objShooter.canRun = true;
        }
        
        while (isOperatorControl() & isEnabled()){
            try {
                Thread.sleep(10L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        
            elevator.isEnabledFlag = true;
            //objTableLookUp.test();
            /****************
            **Shooter Code***
            ****************/
            
            //Resets the internal toggle flags when a previously pushed button has been released
            
            driveStick.clearButtons();
            shooterController.clearButtons();
       
            LCD.updateLCD();
            boolean isTest = true;
            
            if (!isTest) {
            /********************
            * Competition Code *
            ********************/
                /****************
                * Shooter Code *
                ****************/
                cameraControl.moveTest(-shooterController.getRawAxis(LY));
            
                //SmartDashboard.putNumber("Distance: ", camera.middle_distance);
                //SmartDashboard.putBoolean("LowerLimt", elevator.lowerLimit.get());

                /*
                if (shooterController.isYPushed()){ //says the shooter is aiming at high
                     targetSlot = lookUp.distanceHigh;
                     targetAngle = lookUp.angleHigh;
                     //SmartDashboard.putString("Looking at: ", "high distance");
                } else if (shooterController.isXPushed()){ //says the shooter is aiming at middle
                     targetSlot = lookUp.distanceMiddle;
                    targetAngle = lookUp.angleMiddle;
                    //SmartDashboard.putString("Looking at: ", "middle distance");
                 }*/
                
                if (shooterController.isXPushed()){
                    controlFlag = true;
                    //elevator.canRun = true;
                    elevator.elevateFlag = true;
                    elevator.goToPotAngle(frontAuto);//lookUp.lookUpAngle(10, lookUp.distanceHigh, lookUp.angleHigh));
                }
                
                if (shooterController.isYPushed()){
                    controlFlag = true;
                    //elevator.canRun = true;
                    elevator.elevateFlag = true;
                    elevator.goToPotAngle(backAuto);//lookUp.lookUpAngle(18, lookUp.distanceHigh, lookUp.angleHigh));
                }
                
                //SmartDashboard.putNumber("Voltage", elevator.angleMeter.getVoltage());
                if (shooterController.isBackPushed()){ 
                     //System.out.println("Going to target");
                    //SmartDashboard.putNumber("Target Voltage: ", lookUp.lookUpAngle(camera.middle_distance, targetSlot, targetAngle));
                    controlFlag = true;
                    //elevator.canRun = true;
                    elevator.elevateFlag = true;
                    elevator.goToPotAngle(lookUp.lookUpAngle(camera.middle_distance, targetSlot, targetAngle));
                }
                
                if (shooterController.isBPushed()){
                    controlFlag = true;
                    elevator.elevateFlag = true;
                    elevator.goToPotAngle(backMiddle);
                }
                
                //SmartDashboard.putNumber("Current Voltage: ", elevator.currentAngle);
                //SmartDashboard.putBoolean("Shooting: ", objShooter.busyStatus); 
                if (shooterController.isAPushed() && objShooter.busyStatus){  
                    objShooter.shooterThread();
                }
                
                /*if (shooterController.isRBHeld()){
                    elevator.raise();
                } else if (shooterController.isLBHeld()){
                    elevator.lower();
                } else {
                    elevator.off();
                }*/
                
                if (shooterController.isStartPushed()){
                    objShooter.goToSpeed(.25);
                }
                
                /***************
                * Driver Code *
                ***************/
            
                drive.setSpeed(driveStick.isLBHeld(), driveStick.isRBHeld());
                drive.drive(newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(RX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LY), 0.1)));
            
                /*************
                * Feed Code *
                *************/

                if (driveStick.isBackPushed()){
                    controlFlag = true;
                    objShooter.loadFrisbee(elevator);
                }
   
                /**************
                * Climb Code *
                **************/
                if(driveStick.isStartHeld() && shooterController.isLBHeld() && shooterController.isRBHeld()){
                    controlFlag = true;
                }
            
                if(driveStick.isXPushed()){
                    controlFlag = true;
                    //elevator.canRun = true;
                    elevator.elevateFlag = true;
                    elevator.goToPotAngle(climbAngle);
                
                }
            

                if(driveStick.isYPushed()){
                    elevator.goToPotAngle(16.2);       

                }
            
                if (driveStick.isBPushed()) {
                    //elevator.canRun = true;
                    elevator.elevateFlag = true;
                    elevator.goToPotAngle(feedAngle);
                }
           
                
                //SmartDashboard.putNumber("Servo: ", cameraControl.cameraServo.get());
            
                /************
                * TODO: 
                * Have autonomous spin up gradually, but still work within time
                * Add thread safety
                * Add a fourth shoot in autonomous (Maybe)
                * Goto starting height: 2.668
                */

            }
        
            if (isTest){ //ENABLE TEST MODE!
                double testSpeed = .5;

               /* if (shooterController.isAHeld()){
                    elevator.elevatorTalonL.set(testSpeed);
                } else if (shooterController.isBHeld() && !elevator.lowerLimitL.get()){
                    elevator.elevatorTalonL.set(-testSpeed);
                } else elevator.elevatorTalonL.set(0);


                if (shooterController.isXHeld()) {
                    elevator.elevatorTalonR.set(testSpeed);
                } else if (shooterController.isYHeld() && !elevator.lowerLimitR.get()){
                    elevator.elevatorTalonR.set(-testSpeed);
                } else {
                    elevator.elevatorTalonR.set(0);
                }*/

                if(shooterController.isBackPushed()){
                    elevator.goToPotAngle(17);
                    System.out.println("Sent Command");
                } 

                /*if (shooterController.isLBPushed()){
                    elevator.goToPotAngle(36);
                }*/
                
                if (shooterController.isStartPushed()){
                    elevator.goToPotAngle(elevator.MINANGLE_L);
                }

                SmartDashboard.putNumber("Left Pot", elevator.stringPotL.getVoltage());
                SmartDashboard.putNumber("Right Pot", elevator.stringPotR.getVoltage());
                SmartDashboard.putBoolean("LeftLimit: ", elevator.lowerLimitL.get());
                SmartDashboard.putBoolean("RightLimit", elevator.lowerLimitR.get());
                SmartDashboard.putNumber("DeltaV: ", elevator.deltaV());
                SmartDashboard.putNumber("LDegrees", elevator.getDegreesL());
                SmartDashboard.putNumber("RDegrees", elevator.getDegreesR());
                SmartDashboard.putNumber("basePWM", elevator.basePWM);
                
                if (shooterController.isAPushed()){
                    elevator.goToPotAngle((elevator.getDegreesL() + .5));
                }
                
                if (shooterController.isBPushed()){
                    elevator.goToPotAngle(elevator.getDegreesL() - .5);
                }
                
                if (shooterController.isLBPushed()){
                    objShooter.shooterThread();
                }
                
                if (shooterController.isRBPushed()){
                    objShooter.goToSpeed(.25);
                }
                
                drive.drive(newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(RX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LY), 0.1)));

                
                /*if (shooterController.isRBHeld()){
                    if (shooterController.isAHeld()){
                        elevator.elevatorTalonR.set(testSpeed);
                    } else if (shooterController.isBHeld() && !elevator.lowerLimitL.get()){
                        elevator.elevatorTalonR.set(-testSpeed);
                    } else elevator.elevatorTalonR.set(0);


                    if (shooterController.isXHeld()) {
                        elevator.elevatorTalonL.set(testSpeed);
                    } else if (shooterController.isYHeld() && !elevator.lowerLimitR.get()){
                        elevator.elevatorTalonL.set(-testSpeed);
                    } else {
                        elevator.elevatorTalonL.set(0);
                    }
                }*/


            }
        }   
    }
}




