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
   double feedAngle = 0.84;
   double climbAngle = elevator.maxLimit;
   double autoTarget;
   double backAuto = 1.750;
   double frontAuto = 2.470;
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
                        autoTarget = backAuto;//lookUp.lookUpAngle(18, lookUp.distanceHigh, lookUp.angleHigh);
                        //System.out.println("Target1="+autoTarget);
                    } else { //close, right
                        autoTarget = frontAuto;//lookUp.lookUpAngle(10, lookUp.distanceHigh, lookUp.angleHigh);
                        SmartDashboard.putString("autonomus location: ", "From front of pyramid");
                        //System.out.println("Target3="+autoTarget);
                    }
                
                
                //autoTarget = 3.16;
                    while (autoFlag){
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                        if (autoTarget > 0) {
                            elevator.goTo(autoTarget);
                        }
                        if (!elevator.elevateFlag) {
                            autoFlag = false;
                        }
                    }
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
   //objTableLookUp.test();
   /****************
   **Shooter Code***
   ****************/
   //Resets the internal toggle flags when a previously pushed button has been released
       driveStick.clearButtons();
       shooterController.clearButtons();
       //SmartDashboard.putNumber("Target Voltage: ", elevator.shootTarget);
       //SmartDashboard.putNumber("Loop Count", elevator.whileCount);
       /*
       if(driveStick.isStartPushed()){
           objShooter.shoot();
          //LCD.println(Line.kUser5, 1, "Inside");
       }
       if(driveStick.isBackPushed()){
           objShooter.loadFrisbee(elevator);
       }*/
       /*
       //LCD.println(Line.kUser5, 1, "AngleVoltage: " + elevator.angleMeter.getVoltage());
       cameraControl.moveTest(shooterController.getRawAxis(LY));
       LCD.println(Line.kUser5, 1, "Servo Angle: " + cameraControl.cameraServo.get());
       */
       //LCD.println(Line.kUser2, 1, "running");
       
        //test = objShooter.shootLimit.get();
        //System.out.println(test);
       /*if(shooterController.isStartPushed()){
           LCD.println(Line.kUser5, 1, "Inside");//TODO
           camera.imageAnalysis();
           System.out.println("Inside");
           //objShooter.start();
           //arm.armUp();
       }*/
       /**********************
        * Shooter Algorithms *
        **********************/
/*
       if(shooterController.isAPushed()){
            objShooter.increaseSpeed();
       }
       if(shooterController.isBPushed()){
           objShooter.decreaseSpeed();
       }
       if(shooterController.isXPushed()){
           objShooter.increasePercentage();
       }
       if(shooterController.isYPushed()){
           objShooter.decreasePercentage();
       }
       if(shooterController.isBackPushed()){
           objShooter.stop();
       }*/
       //if (shooterController.isStartPushed()){
           //objShooter.start();

       
       //objShooter.printLCD(LCD);
       /*if(shooterController.isLStickPushed() && !armTestFlag){
           LCD.println(Line.kUser5, 1, "Inside");
           //camera.imageAnalysis();    TODO: Is this needed?
           System.out.println("Inside");
       }*/
       //arm.rotate(targetRotatePosition);
       //Arm.rotate(targetPosition);
       //objShooter.elevator();
       //Arm.grabFrisbee();
       //Arm.armUp();
       //Arm.armDown();
       //Arm.goToPosition(2.5);
       /*
       //try {Thread.sleep(1000);} catch(Exception e){}
       /*****************
        * Elevator Code *
 
       
       LCD.println(Line.kUser5, 1, "Motor Modifier: " + elevator.pwmModifier);*/
       //elevator.automaticElevatorTarget(shooterController.isLBPushed(), shooterController.isRBPushed());
       //LCD.println(Line.kUser1, 1, "ElevatorTarget: " + elevator.elevationTarget);
       //LCD.println(Line.kUser2, 1, "Elv(Volt): " + elevator.angleMeter.getAverageVoltage());
       //elevator.angleMeter.setAverageBits(128);
       //SmartDashboard.putNumber("Average Bits: ", elevator.angleMeter.getAverageBits());
       //SmartDashboard.putNumber("Oversampling Bits: ", elevator.angleMeter.getOversampleBits());
      // SmartDashboard.putNumber("Elevation (Our Average): ", elevator.getAverageVoltage2());
       //SmartDashboard.putNumber("Elevation (No Average): ", elevator.angleMeter.getVoltage());
       //SmartDashboard.putNumber("Elevation (Their Average): ", elevator.angleMeter.getAverageVoltage());
       //LCD.println(Line.kUser3, 1, "Current Angle:" + elevator.currentAngle);
       //SmartDashboard.putNumber("Elevator Angle: ", elevator.elevatorAngleMath());
       //SmartDashboard.putNumber("Servo Angle: ", cameraControl.servoAngleMath());
       //LCD.println(Line.kUser3, 1, "shootLimit: " + objShooter.shootLimit.get());
       //LCD.println(Line.kUser4, 1, "Switch1: " + frontBackSwitch.get());
       //LCD.println(Line.kUser5, 1, "Switch2: " + leftRightSwitch.get());
       
       /**************************
       * Manual Elevator Control *
       **************************/
       /*
       if (shooterController.isRBHeld()){
           elevator.raise();
       } else if (shooterController.isLBHeld()){
           elevator.lower();
       } else {
           elevator.off();
       }*/
       /*
       if(driveStick.isAPushed()){
           elevator.setTarget(2.5);
       }
       if(driveStick.isBPushed()){
           elevator.setTarget(2.65);
       }
       if(driveStick.isXPushed()){
           elevator.setTarget(2.75);
       }      
       if(driveStick.isYPushed()){
           elevator.setTarget(2.95);
       }*/
       //elevator.goTo();
       //elevator.goToAngle();
       /*if (shooterController.isRBPushed(){
        *   elevator.elevatorTarget = 2.9;
        * } else if (shooterController.isLBPushed()){
        *   elevator.elevatorTarget = 2.5;
        * }
       /*******************
        * Servo Test Code *
        ******************/
        /**************
         * Drive Code *
         **************/
         //drive.setSpeed(driveStick.isLBHeld(), driveStick.isRBHeld());
         //drive.drive(newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(RX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LY), 0.1)));
        /******************
         * Demo/Test Code *
         ******************/
       /*if (shooterController.isStartPushed()){
         arm.demoStatus = 0;
         arm.demoOnFlag = true;
         arm.armDemo();
       }*/
       /* if (shooterController.isStartPushed()){
           arm.rotate(2.7);
       }
       *//*
       if (!armTestFlag){
        if (shooterController.isStartPushed()){
            arm.demoStatus = 0;
            arm.demoOnFlag = true;
            arm.armDemo();
        }
       }
       SmartDashboard.putBoolean("ArmUp Bool:", arm.upDownFlag);
       SmartDashboard.putNumber("CurrentPosition :", arm.currentPosition);*/
       

       //if  (!armTestFlag) {
       //arm.rotateTalon.set(shooterController.getRawAxis(LX));
       //}
   
        
            //}

        
        //String potString = Double.toString(pot1.getVoltage());
        //LCD.println(Line.kUser2, 1, potString);
        LCD.updateLCD();
    
        
        /********************
         * Competition Code *
         ********************/
            /****************
             * Shooter Code *
             ****************/
        //if (controlFlag){    
             cameraControl.moveTest(-shooterController.getRawAxis(LY));
            
             //SmartDashboard.putBoolean("leftRightSwitch: ", leftRightSwitch.get());
             //SmartDashboard.putBoolean("frontBackSwitch: ", frontBackSwitch.get());
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
                elevator.goTo(frontAuto);//lookUp.lookUpAngle(10, lookUp.distanceHigh, lookUp.angleHigh));
             }
             if (shooterController.isYPushed()){
                controlFlag = true;
                //elevator.canRun = true;
                elevator.elevateFlag = true;
                elevator.goTo(backAuto);//lookUp.lookUpAngle(18, lookUp.distanceHigh, lookUp.angleHigh));
             }
            //SmartDashboard.putNumber("Voltage", elevator.angleMeter.getVoltage());
             if (shooterController.isBackPushed()){ 
                 //System.out.println("Going to target");
                 //SmartDashboard.putNumber("Target Voltage: ", lookUp.lookUpAngle(camera.middle_distance, targetSlot, targetAngle));
                 controlFlag = true;
                 //elevator.canRun = true;
                 elevator.elevateFlag = true;
                 elevator.goTo(lookUp.lookUpAngle(camera.middle_distance, targetSlot, targetAngle));
                 
             }
             if (shooterController.isBPushed()){
                 controlFlag = true;
                 elevator.elevateFlag = true;
                 elevator.goTo(backMiddle);
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
                elevator.goTo(climbAngle);
                
            }
            

            if(driveStick.isYPushed()){
                //elevator.canRun = true;
                controlFlag = false;
            }
            
            if (driveStick.isBPushed()) {
                //elevator.canRun = true;
                elevator.elevateFlag = true;
                elevator.goTo(feedAngle);
            }
           
           if (!controlFlag){
                elevator.lower();
                if (elevator.lowerLimit.get()){
                    elevator.off();
                    controlFlag = true;
                }
            }
           //SmartDashboard.putNumber("Servo: ", cameraControl.cameraServo.get());
            /************
             * TODO: 
             * Have autonomous spin up gradually, but still work within time
             * Add thread safety
             * Add a fourth shoot in autonomous (Maybe)
             * Goto starting height: 2.668
             ************/
            double stringPotVoltage;
            stringPotVoltage = elevator.stringPotL.getVoltage();
            //System.out.println(stringPotVoltage);
            SmartDashboard.putBoolean("canRun", elevator.canRun);
        
    }
  
}
    public void test() {
        
        while (isEnabled() && isTest()) {
            if (shooterController.isRBHeld()){
                elevator.raise();
            } else if (shooterController.isLBHeld()){
               elevator.lower(); 
            } else elevator.off();
            
            if (shooterController.isAPushed()){
                objShooter.shooterThread();
            }
            drive.setSpeed(driveStick.isLBHeld(), driveStick.isRBHeld());
            drive.drive(newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(RX), 0.1)), newMath.toTheThird(deadband.zero(driveStick.getRawAxis(LY), 0.1)));  
            SmartDashboard.putNumber("String Pot", elevator.stringPotL.getVoltage());
            //System.out.println("Limit: " + objShooter.shootLimit.get());
        }   
    }
}



