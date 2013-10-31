/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.templates.Team3373;

/**
 *
 * @author Jamie
 */
public class Elevator {
    // Since elevator is tied directly to PWM hardware ports, allow only one
    // instance of Elevator to ever be created.  Provide a getInstance() method
    // to return the one and only Elevator object to other classes that
    // need to use it. This is known as a Singleton pattern.
    private static final Elevator instance = new Elevator();
    private Elevator() {}  //Prevents other classes from declaring new Elevator()
    public static Elevator getInstance() {
        return instance;
    }
    
    //Talon elevatorTalon2 = new Talon(8);
    Talon elevatorTalonL = new Talon(7);
    Talon elevatorTalonR = new Talon(8);
    DigitalInput lowerLimitL = new DigitalInput(7);
    DigitalInput lowerLimitR = new DigitalInput(8);
    
    // Used by voltage averaging/ smoothing method
    int arraySize = 1000;
    double runningTotalVoltage[] = new double[arraySize];
    int bufferCount = 0;
    int currentIndex=0;
    double currentTotalVoltage = 0.0;
    double currentAverageVoltage = 0.0;
    double currentVoltage;
    double lastReading = 0.0;
    double whileCount = 0;

    AnalogChannel angleMeter = new AnalogChannel(1);
    AnalogChannel stringPotL = new AnalogChannel(5);
    AnalogChannel stringPotR = new AnalogChannel(6);    
    double minLimit = 0.824;//this must be changed to stringPot min
    double maxLimit = 2.703; //this must be changed to stingPot Max
    double minDegrees = 21.9;
    double maxDegrees = 50.6;
    double basePWM = .5; //based on calculations, this speed should be at 0.73 to maintain same speed for height, 
    double pwmModifier = .85;
    double elevatorTarget;
    boolean canRun = true;
    double currentAngleL = stringPotL.getVoltage(); //changed to string pot so that goTo works
    double currentAngleR = stringPotR.getVoltage(); //ditto
    double elevationTarget = angleMeter.getVoltage();;
    boolean goToFlag = false;
    double slope;
    double angleCalc;
    boolean elevateFlag = true;
    boolean isThreadRunningL = false;
    boolean isThreadRunningR = false;
    boolean isEnabledFlag = false;
    double shootTarget;
    static double smallerDBL = .05;
    //double angle = 41(voltage - 2.5) + 21.9;
    
    
    //angle scaler
    static double MAXVOLTAGE_R = 2.839;
    static double MAXANGLE_R = 36.3;
    static double MINANGLE_R = 16.2;
    static double MINVOLTAGE_R = 0.130;
    
    static double MAXVOLTAGE_L = 2.947;
    static double MAXANGLE_L = 36.3;
    static double MINANGLE_L = 16.2;
    static double MINVOLTAGE_L = .752;
    
    public void raise(){
        elevatorTalonL.set(basePWM);
        elevatorTalonR.set(basePWM * pwmModifier);
    }
    
    public void lower(){
        if (lowerLimitL.get() || currentAngleL < minLimit){
                off();
                return;
            }
        if (!lowerLimitL.get() && currentAngleL > minLimit){
            elevatorTalonL.set(-basePWM);
            elevatorTalonR.set(-basePWM * pwmModifier);
            }
            
        }
    public void off(){
        elevatorTalonL.set(0);
        elevatorTalonR.set(0);
    }
   public double elevatorAngleMath(){
       slope = (maxDegrees - minDegrees)/(maxLimit - minLimit);
       angleCalc = (slope*(getAverageVoltage2() - 2.5) + minDegrees);
       return angleCalc;
   } 

       public double getAverageVoltage2() {
       
        for (int i = 0; i < arraySize; i++){
            currentTotalVoltage += runningTotalVoltage[i];
        }
        currentAverageVoltage = currentTotalVoltage/arraySize;
       currentTotalVoltage = 0.0; 
        
       return currentAverageVoltage;
       
       //old code to get voltage KEEP THIS CODE, WAS POSSIBLY WORKING
       /*  currentVoltage = angleMeter.getVoltage(); //gets the non-average voltage of the sensor
       currentTotalVoltage = currentTotalVoltage - runningTotalVoltage[currentIndex] + currentVoltage; //adds the new data point while deleting the old
       runningTotalVoltage[currentIndex] = currentVoltage;//store the new data point
       currentIndex = (currentIndex + 1) %  arraySize;//currentIndex is the index to be changed
       if (bufferCount < arraySize) {
           bufferCount++;//checks to see jf the array is full of data points
       }
       return currentTotalVoltage/bufferCount;
        */
    }
    public void createDataSet(){//need to evaluate buffer(arraysize)
       final Thread thread = new Thread(new Runnable() {
        public void run(){
       while(true){//we want this running always, it is ok running while robot is disabled.
        whileCount++;
       currentVoltage = angleMeter.getVoltage(); //gets the non-average voltage of the sensor
       runningTotalVoltage[currentIndex] = currentVoltage;//store the new data point
       currentIndex = (currentIndex + 1) %  arraySize;//currentIndex is the index to be changed
       if (bufferCount < arraySize) {
           bufferCount++;//checks to see if the array is full of data points
       }
        }
        }
        });
        thread.start();
    }
    public void goToAngle(){
        //at the moment elevatorTarget is a voltage, 
        //TODO: make some sort of conversion from voltage to angle
        currentAngleL = getAverageVoltage2(); 
        if (Math.abs(elevationTarget - currentAngleL) <= .1){//TODO: check angle
            off();
           // System.out.println("off");
        } else if (elevationTarget > currentAngleL && elevationTarget < maxLimit){
            raise();
            //System.out.println("raise");
        } else if (elevationTarget < currentAngleL && elevationTarget > minLimit){
            //System.out.println("lower");
        } 
        
    }
    public void setTarget(double a){
        elevationTarget = a;
        goToFlag = true;
    }
    public void goTo(final double target){ //NOW uses StringPot.getVoltage to read voltage to move elevator, changes marked below
        final Thread thread = new Thread(new Runnable() {
        public void run(){
                goToFlag = false;
                isThreadRunningL = true;
                currentAngleL = stringPotL.getVoltage(); //change happened here
                shootTarget = target;
                    while(target > currentAngleL  && target < maxLimit && currentAngleL < maxLimit&& elevateFlag){
                        try {
                            Thread.sleep(40L);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        currentAngleL = stringPotL.getVoltage();//change happened here
                        //System.out.println("raise " + target);
                        raise();
                        if(target < currentAngleL){
                            elevateFlag = false;
                            canRun = true;
                            break;
                        }
                    }
                    while(target < currentAngleL && target > minLimit && currentAngleL > minLimit&& elevateFlag){
                        currentAngleL = stringPotL.getVoltage();//change happened here
                        try {
                            Thread.sleep(40L);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        //System.out.println("lower " + target);
                        lower();
                        if(target > currentAngleL){
                            elevateFlag = false;
                            canRun = true;
                            break;
                        }
                    }
                    //System.out.println("off");
                    off();
                    isThreadRunningL = false;
                }
            });
            if(!isThreadRunningL){
                thread.start();
            }
       }
    /*public void automaticElevatorTarget(boolean addTarget, boolean decreaseTarget){
        if (addTarget  && elevatorTarget <= 4.7){
            elevatorTarget += .1;
        } if (decreaseTarget && elevatorTarget >= 1.3){
            elevatorTarget += -.1;
        }
    }*/
    
    public void goToPotAngle(double target){
        double DBL = 1; //deadband Constant
        double deltaPosition = Math.abs(target - getDegreesL());
        if (deltaPosition > DBL){
            elevationThreadL(target);
            elevationThreadR(target);
        } else {
            elevatorTalonR.set(0);
            elevatorTalonL.set(0);
        }
    }
    
    public void elevationThreadL(final double target){
        
        final Thread thread = new Thread(new Runnable() {
            public void run(){
                isThreadRunningL = true;
                double speedL = 0;
                while (Math.abs(target - getDegreesL()) > smallerDBL && isEnabledFlag){
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (target > getDegreesL()){
                        speedL = basePWM - deltaV();
                    } else if (target < getDegreesL() && !lowerLimitL.get()){
                        speedL = -(.8*basePWM) - deltaV();
                        System.out.println("DeltaV: " + deltaV());
                    } else if (Math.abs(getDegreesR() - getDegreesL()) > 5){
                        speedL = 0;
                        break;
                    }
                    System.out.println("SpeedL:" + speedL);
                    elevatorTalonL.set(speedL);
                    
                }
                elevatorTalonL.set(0);
                isThreadRunningL = false;
                System.out.println("Exit Loop");
            }    
        });
        if (!isThreadRunningL){
            thread.start();
        }
    }
    
    public void elevationThreadR(final double target){
        
        final Thread thread = new Thread(new Runnable() {
            public void run(){
                isThreadRunningR = true;
                double speedR = 0;
                while (Math.abs(target - getDegreesR()) > smallerDBL && isEnabledFlag){
                    try {
                        Thread.sleep(1L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (target > getDegreesR() && (target - getDegreesR()) > smallerDBL){
                        speedR = basePWM + deltaV();
                    } else if (target < getDegreesR() && !lowerLimitR.get()){
                        speedR = -basePWM + (deltaV());
                    } else if (Math.abs(getDegreesR() - getDegreesL()) > 5){
                        speedR = 0;
                        break;
                    }
                    elevatorTalonR.set(speedR);
                    System.out.println("SpeedR: " + speedR);
                }
                elevatorTalonR.set(0);
                isThreadRunningR = false;
                System.out.println("Exit Loop");
            }    
        });
        if (!isThreadRunningR){
            thread.start();;
        }
    }
    
    public double deltaV(){
           double deltaV;
           double c = 0.05;//modifier to make an acceptable value at which to change the speed of elevator motors
           return deltaV = (getDegreesL()-getDegreesR()) * c;
    }    
        
    public double getDegreesR(){
        double angle = (((MAXANGLE_R - MINANGLE_R)/(MAXVOLTAGE_R - MINVOLTAGE_R))*(stringPotR.getVoltage() - MINVOLTAGE_R) + MINANGLE_R);
        return angle;
    }
    
    public double getDegreesL(){
        double angle = (((MAXANGLE_L - MINANGLE_L)/(MAXVOLTAGE_L - MINVOLTAGE_L))*(stringPotL.getVoltage() - MINVOLTAGE_L) + MINANGLE_L);
        return angle;
    }
}
