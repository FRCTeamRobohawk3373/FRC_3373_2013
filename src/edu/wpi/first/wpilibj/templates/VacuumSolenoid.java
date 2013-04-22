/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author RoboHawks
 */
public class VacuumSolenoid {
    Solenoid grabSolenoid = new Solenoid(8);
    public void releaseVacuum(){
        grabSolenoid.set(true);
    }
    public void holdVaccum(){
        grabSolenoid.set(false);
    }
}