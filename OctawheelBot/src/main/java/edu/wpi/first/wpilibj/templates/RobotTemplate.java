/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    
    Jaguar frontLeft , frontRight , rearLeft , rearRight;
    robovikingStick AController, BController;
    RobotDrive roboDrive;
    
    Constants c = new Constants();
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    // added this comment for git testing
    public void robotInit() {
        AController = new robovikingStick(c.getLeftStickPort());
        BController = new robovikingStick(c.getRightStickPort());
        
        frontLeft = new Jaguar(c.getFLMotorChannel());
        frontRight = new Jaguar(c.getFRMotorChannel());
        rearLeft = new Jaguar(c.getRLMotorChannel());
        rearRight = new Jaguar(c.getRRMotorChannel());
        
        roboDrive = new RobotDrive(frontLeft , rearLeft , frontRight , rearRight);
//        roboDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
//        roboDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        //*high pitch* bweh-bweh-bweh-bwaaaaaaaahh!
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
        if(AController.getToggleButton(c.getButtonA())){
          roboDrive.tankDrive(-AController.getY(), -BController.getY());
          c.displayTankMode();
        } else {
            roboDrive.arcadeDrive(-AController.getY() , -AController.getX(), true);
            c.displayXboxMode();
        }
    }

    public void disabledInit() {

        roboDrive.tankDrive(0 , 0);
    }

    public void testPeriodic() {

        if (AController.getToggleButton(5)) {
            if (AController.getToggleButton(6))
                roboDrive.tankDrive(0.5, 0.5);
            else
                roboDrive.tankDrive(-.5, -.5);
        } else {
            roboDrive.tankDrive(0,0);
        }
    }
}
