/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
/*  4 Jaguars (2 on left, 2 on right)
    1 Joystick
    1 Compressor
    1 Solenoid
    
    
*/
package edu.archwood.frc2607;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class OctaBot extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    Jaguar FR;
    Jaguar FL;
    Jaguar BR;
    Jaguar BL;
    robovikingStick theElderWand;
    Compressor compressor;
    Solenoid solenoid;
    RobotDrive robotDrive;
    boolean solenoidSwitch = false;
    boolean compressorSwitch = true;
    
    public void robotInit() {
        FR = new Jaguar(OctaBotConstants.pwmJaguarFR);
        FL = new Jaguar(OctaBotConstants.pwmJaguarFL);
        BR = new Jaguar(OctaBotConstants.pwmJaguarBR);
        BL = new Jaguar(OctaBotConstants.pwmJaguarBL);
        theElderWand = new robovikingStick(OctaBotConstants.joystick);
        compressor = new Compressor(OctaBotConstants.pressureSwitchChannel, OctaBotConstants.pressureRelayChannel);
        solenoid = new Solenoid(OctaBotConstants.solenoidChannel);
        robotDrive = new RobotDrive(FL, BL, FR, BR);
        System.out.println("OctaWheel is ALIVE!");
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    
    public void teleopInit() {
        compressor.start();
    }
    
    public void teleopPeriodic() {
           
        robotDrive.arcadeDrive(-theElderWand.getY(), -theElderWand.getX());
        
        if (theElderWand.getButtonToggle(2) == true){
            solenoidSwitch = !solenoidSwitch;
        }
        
        solenoid.set(solenoidSwitch);
       
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
    public void disabledPeriodic(){
       
    }
    
}