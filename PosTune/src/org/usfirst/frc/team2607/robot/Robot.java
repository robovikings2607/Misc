
package org.usfirst.frc.team2607.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	private CANTalon armMotor;
	private RobovikingStick stick;
	private double targetPos;
	private SRXProfileDriver profile;
	private int modeIndex;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	armMotor = new CANTalon(10);
    	profile = new SRXProfileDriver(armMotor);
    	stick = new RobovikingStick(0);
//    	armMotor.setEncPosition(armMotor.getPulseWidthPosition() & 0xFFF);
    	armMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
    	armMotor.reverseSensor(true);
    	armMotor.setProfile(0);
//    	armMotor.setF(0.02461501443695861405197305101059);	// 1023 / 41560
    	armMotor.setF(0.001);
    	armMotor.setP(.022);
    	armMotor.setI(0);
    	armMotor.setD(0);
    	targetPos = 0.0;
    	modeIndex = 0;
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
    private int tick = 0;
    private String mode[] = {"UNKOWN","VBUS","PID","MPROF"};
    
    public void disabledPeriodic() {
    	armMotor.changeControlMode(TalonControlMode.PercentVbus);
    	armMotor.setPosition(0);
    	armMotor.set(0);
    	profile.reset();
    	targetPos = 0;
    }
    
    public void teleopPeriodic() {
    	
    	if (stick.getButtonPressedOneShot(3)) {		// xBox Button X 
    		if (++modeIndex > 3) modeIndex = 0;
    	}
    	
    	switch (modeIndex) {
    		
    		case 1:			// VBUS
        		armMotor.changeControlMode(TalonControlMode.PercentVbus);
        		profile.reset();
        		if (stick.getToggleButton(4)) {				// xBox Button Y
        			armMotor.set(1.0);
        		} else if (stick.getToggleButton(1)) {
        			armMotor.set(-1.0);						// xBox Button A
        		} else {
        			armMotor.set(0);
        		}
        		break;
    		case 2:			// PID
    			armMotor.changeControlMode(TalonControlMode.Position);
    			armMotor.configPeakOutputVoltage(4.0, -4.0);
    			if (stick.getButtonPressedOneShot(4)) {	// Button Y on xBox Controller
    				targetPos += 25;
    			}
    			if (stick.getButtonPressedOneShot(1)) { // Button A on xBox Controller
    				targetPos -= 25;
    			}
    			armMotor.set(targetPos);
    			break;
    		case 3:			// MPROF
    			armMotor.changeControlMode(TalonControlMode.MotionProfile);
    			armMotor.configPeakOutputVoltage(12.0, -12.0);
    			armMotor.set(profile.getSetValue().value);
    			profile.control();
        		if (stick.getButtonPressedOneShot(4)) {	// Button Y on xBox Controller
        			profile.setMotionProfile(new SRXProfile(101.45, 25, 250, 250, 10));
        			profile.startMotionProfile();
        		}
        		if (stick.getButtonPressedOneShot(1)) { // Button A on xBox Controller
        			profile.setMotionProfile(new SRXProfile(-101.45, -25, 250, 250, 10));
        			profile.startMotionProfile();
        		}
        		
    			break;
    	}
    	    	
        if (stick.getButtonPressedOneShot(10)) {
        	armMotor.setEncPosition(0);
        	armMotor.setPosition(0);
        	targetPos = 0;
        }
        
        if (++tick > 25) {
        	tick = 0;
        	System.out.println("Mode: " + mode[modeIndex] + 
        					   "\t\tSP: " + targetPos +
        						"\t\tPV: " + armMotor.getPosition() +
        						"\t\tOUTP: " + armMotor.getOutputVoltage() +
        						"\t\tSPEED: " + armMotor.getSpeed() + 
        						"\t\tABS POS: " + armMotor.getPulseWidthPosition());
        }
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}

/*
armMotor.configNominalOutputVoltage(+0.0, -0.0);
armMotor.configPeakOutputVoltage(12.0, -12.0);
armMotor.setAllowableClosedLoopErr(0);
armMotor.setProfile(0);
armMotor.setPID(0.05, 0.0, 0.0);
*/

/*
// position control mode
mode = "POS";
if (stick.getButtonPressedOneShot(4)) {	// Button Y on xBox Controller
	targetPos += 100;
	startTime = System.currentTimeMillis();
	totalTime = 0;
}
if (stick.getButtonPressedOneShot(1)) { // Button A on xBox Controller
	targetPos -= 100;
	startTime = System.currentTimeMillis();
	totalTime = 0;
}
armMotor.changeControlMode(TalonControlMode.Position);
armMotor.set(targetPos);
*/