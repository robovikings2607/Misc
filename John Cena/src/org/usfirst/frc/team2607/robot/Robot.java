
package org.usfirst.frc.team2607.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Solenoid; 

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
     
	Solenoid onSwitch ; 
	Joystick move ; 
	ADXRS450_Gyro gyro ;
//	ADIS16448_IMU imuThing;
	AHRS navX_Gyro ;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	onSwitch = new Solenoid(1,4);
    	move = new Joystick (0) ;
    	gyro = new ADXRS450_Gyro();
//    	imuThing = new ADIS16448_IMU();
    	navX_Gyro = new AHRS(SPI.Port.kMXP);
    	
//    	imuThing.calibrate();
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	
    	}
    

    /**
     * This function is called periodically during operator control
     */
    int counter = 0;
    public void teleopPeriodic() {
    	if (move.getRawButton(1)){
    		onSwitch.set(true);
    	} else {
    		onSwitch.set(false);
    	}
    	counter = counter + 1;
    	if(counter == 25){
    		System.out.print("ad gyro: " + gyro.getAngle());
    		System.out.println("     navX gyro: " + navX_Gyro.getAngle() + " cal: " + navX_Gyro.isCalibrating());
//    		System.out.println("  ,  IMU: " + imuThing.getAngle());
    		counter = 0; 
    	}  
    	
    	if(move.getRawButton(7)){
    		//gyro.calibrate();
 //   		imuThing.calibrate();
    	}
    	if(move.getRawButton(8)){
    		gyro.reset();
//    		imuThing.reset();
    		navX_Gyro.reset();
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
