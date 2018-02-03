/**
 * Example demonstrating the velocity closed-loop servo.
 * Tested with Logitech F350 USB Gamepad inserted into Driver Station]
 * 
 * Be sure to select the correct feedback sensor using SetFeedbackDevice() below.
 *
 * After deploying/debugging this to your RIO, first use the left Y-stick 
 * to throttle the Talon manually.  This will confirm your hardware setup.
 * Be sure to confirm that when the Talon is driving forward (green) the 
 * position sensor is moving in a positive direction.  If this is not the cause
 * flip the boolena input to the SetSensorDirection() call below.
 *
 * Once you've ensured your feedback device is in-phase with the motor,
 * use the button shortcuts to servo to target velocity.  
 *
 * Tweak the PID gains accordingly.
 */

package org.usfirst.frc.team469.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Robot extends TimedRobot {
  
	TalonSRX _talon = new TalonSRX(15);	
	Joystick _joy = new Joystick(0);	

	int _loops = 0;
	double Input_Multiplier = 1;
	double throttleInput = 0;
	int maxEncoderSpeed = 0;
	int minEncoderSpeed = 0;
	public PIDLogger logger;
	boolean firstTime = true;
	
	int slotIdx = 0;  // Profile
	int pidIdx = 0;   // PID within profile
	int timeoutMs = 10;
	double PIDControlF;  // This is used to hold the Forward Gain value of the PID loop
	double PIDControlP;  // This is used to hold the Proportional Gain value of the PID loop
	double PIDControlI;  // This is used to hold the Integral Gain value of the PID loop
	double PIDControlD;  // This is used to hold the Derivative Gain value of the PID loop
	
	boolean PID_enabled = false;

	Preferences prefs;
	
	@Override
	public void robotInit() {
	_talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidIdx, timeoutMs);
	_talon.configPeakOutputForward(1.0, timeoutMs);  // 50% peak voltage forward
	_talon.configPeakOutputReverse(-1.0, timeoutMs);  // 50% peak voltage reverse
	_talon.configNominalOutputForward(0.0, timeoutMs);
	_talon.configNominalOutputReverse(0.0, timeoutMs);
	_talon.configOpenloopRamp(0.0, timeoutMs);
	_talon.configClosedloopRamp(0.0, timeoutMs);
	_talon.setInverted(false);
	_talon.selectProfileSlot(slotIdx, pidIdx);
	
	
	prefs = Preferences.getInstance(); // To access Robot Preferences
	
	PIDControlF = prefs.getDouble("PID_F", 0);
	
	}
	
	@Override
	public void teleopInit() {
		
		prefs = Preferences.getInstance();
		
		PIDControlF = SmartDashboard.getNumber("PID_F ", 0);
		PIDControlP = SmartDashboard.getNumber("PID_P ", 0);
		PIDControlI = SmartDashboard.getNumber("PID_I ", 0);
		PIDControlD = SmartDashboard.getNumber("PID_D ", 0);
		
		maxEncoderSpeed = 0;
		minEncoderSpeed = 0;
		
		Input_Multiplier = SmartDashboard.getNumber("Throttle Multiplier ",1);
		
		PID_enabled = SmartDashboard.getBoolean("PID_Enabled ", false);
		
		throttleInput = SmartDashboard.getNumber("Throttle Input ",0);
		
		_talon.config_kF(slotIdx, PIDControlF, timeoutMs);
		_talon.config_kP(slotIdx, PIDControlP, timeoutMs);
		_talon.config_kI(slotIdx, PIDControlI, timeoutMs);
		_talon.config_kD(slotIdx, PIDControlD, timeoutMs);
		
 		_talon.setIntegralAccumulator(0.00, pidIdx, timeoutMs);
		_talon.setSelectedSensorPosition(0, pidIdx, timeoutMs);
		
		_talon.setNeutralMode(NeutralMode.Coast);
		_talon.setSensorPhase(false);
		
		logger = new PIDLogger(_talon, "TalonSRX");
		
	}
	
    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {

//    	logger.enableLogging(true);
    	
    	if (PID_enabled)
    	{
    		_talon.selectProfileSlot(slotIdx, pidIdx);
    		
    		_talon.set(ControlMode.Velocity,throttleInput * Input_Multiplier);
    	}
    	else
    	{
    		_talon.set(ControlMode.PercentOutput, throttleInput);
    	}
    	
        if(++_loops >= 5) {
        	_loops = 0;
//        	System.out.println(leftYstick);
//        	System.out.println(_joy.getRawButton(1));
        
        	if (PID_enabled)
        	{
//        		logger.run();
        		
        		SmartDashboard.putNumber("Throttle Input ", throttleInput);
        		SmartDashboard.putNumber("Encoder Speed ", _talon.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("Encoder Speed ", _talon.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("PID_F ", PIDControlF);	
        		SmartDashboard.putNumber("PID_P ", PIDControlP);	
        		SmartDashboard.putNumber("PID_I ", PIDControlI);	
        		SmartDashboard.putNumber("PID_D ", PIDControlD);
        		SmartDashboard.putBoolean("PID_Enabled ", PID_enabled);
        		SmartDashboard.putNumber("Throttle Multiplier ", Input_Multiplier);
        		SmartDashboard.putNumber("Requested ", throttleInput * Input_Multiplier);
        		SmartDashboard.putNumber("Calculated Error ",throttleInput * Input_Multiplier - _talon.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("Reported Request ", _talon.getClosedLoopTarget(0));
        		SmartDashboard.putNumber("Reported Error ", _talon.getClosedLoopError(0));
        		SmartDashboard.putNumber("Encoder Speed ", _talon.getSelectedSensorVelocity(0));
        	}
        	else
        	{
        		if (_talon.getSelectedSensorVelocity(0) > maxEncoderSpeed)
        		{
        			maxEncoderSpeed = _talon.getSelectedSensorVelocity(0);
        		}
        		
        		if (_talon.getSelectedSensorVelocity(0) < minEncoderSpeed)
        		{
        			minEncoderSpeed = _talon.getSelectedSensorVelocity(0);
        		}
        			SmartDashboard.putNumber("Throttle Input ", throttleInput);
        			SmartDashboard.putNumber("Encoder Speed ", _talon.getSelectedSensorVelocity(0));
        			SmartDashboard.putNumber("Maximum Encoder Speed ", maxEncoderSpeed);
        			SmartDashboard.putNumber("Minimum Encoder Speed ", minEncoderSpeed);
        	}
        }
    }
    
    @Override
    public void testInit() {
    	
    }
    
    @Override
    public void testPeriodic() {
    	
    }
    
    @Override
    public void disabledInit() {
//   	if(!firstTime) {
// 		logger.enableLogging(false);
//    		firstTime = false;
    	}
    
    @Override
    public void disabledPeriodic() {
    	
    }
}

