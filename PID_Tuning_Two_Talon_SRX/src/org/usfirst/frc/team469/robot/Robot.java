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
  
	TalonSRX _talonRight = new TalonSRX(15);
	TalonSRX _talonLeft = new TalonSRX(16);
	Joystick _joy = new Joystick(0);	

	int _loops = 0;
	double Input_Multiplier = 1;
	double throttleInput = 0;
	int maxEncoderSpeedRight = 0;
	int minEncoderSpeedRight = 0;
	
	int maxEncoderSpeedLeft = 0;
	int minEncoderSpeedLeft = 0;
	
	public PIDLogger logger;
	boolean firstTime = true;
	
	int slotIdx = 0;  // Profile
	int pidIdx = 0;   // PID within profile
	int timeoutMs = 10;
	
	double PIDControlRight_F;  // This is used to hold the Forward Gain value of the PID loop
	double PIDControlRight_P;  // This is used to hold the Proportional Gain value of the PID loop
	double PIDControlRight_I;  // This is used to hold the Integral Gain value of the PID loop
	double PIDControlRight_D;  // This is used to hold the Derivative Gain value of the PID loop
	
	double PIDControlLeft_F;  // This is used to hold the Forward Gain value of the PID loop
	double PIDControlLeft_P;  // This is used to hold the Proportional Gain value of the PID loop
	double PIDControlLeft_I;  // This is used to hold the Integral Gain value of the PID loop
	double PIDControlLeft_D;  // This is used to hold the Derivative Gain value of the PID loop
	
	boolean PID_enabled = false;

	Preferences prefs;
	
	@Override
	public void robotInit() {
	_talonRight.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidIdx, timeoutMs);
	_talonRight.configPeakOutputForward(1.0, timeoutMs);  // 50% peak voltage forward
	_talonRight.configPeakOutputReverse(-1.0, timeoutMs);  // 50% peak voltage reverse
	_talonRight.configNominalOutputForward(0.0, timeoutMs);
	_talonRight.configNominalOutputReverse(0.0, timeoutMs);
	_talonRight.configOpenloopRamp(0.0, timeoutMs);
	_talonRight.configClosedloopRamp(0.0, timeoutMs);
	_talonRight.setInverted(false);
	_talonRight.selectProfileSlot(slotIdx, pidIdx);
	
	_talonLeft.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidIdx, timeoutMs);
	_talonLeft.configPeakOutputForward(1.0, timeoutMs);  // 50% peak voltage forward
	_talonLeft.configPeakOutputReverse(-1.0, timeoutMs);  // 50% peak voltage reverse
	_talonLeft.configNominalOutputForward(0.0, timeoutMs);
	_talonLeft.configNominalOutputReverse(0.0, timeoutMs);
	_talonLeft.configOpenloopRamp(0.0, timeoutMs);
	_talonLeft.configClosedloopRamp(0.0, timeoutMs);
	_talonLeft.setInverted(false);
	_talonLeft.selectProfileSlot(slotIdx, pidIdx);
	
	
	prefs = Preferences.getInstance(); // To access Robot Preferences
	
//	PIDControlF = prefs.getDouble("PID_F", 0);
	
	}
	
	@Override
	public void teleopInit() {
		
//		prefs = Preferences.getInstance();
		
		PIDControlRight_F = SmartDashboard.getNumber("PIDRight_F ", 0);
		PIDControlRight_P = SmartDashboard.getNumber("PIDRight_P ", 0);
		PIDControlRight_I = SmartDashboard.getNumber("PIDRight_I ", 0);
		PIDControlRight_D = SmartDashboard.getNumber("PIDRight_D ", 0);
		
		PIDControlLeft_F = SmartDashboard.getNumber("PIDLeft_F ", 0);
		PIDControlLeft_P = SmartDashboard.getNumber("PIDLeft_P ", 0);
		PIDControlLeft_I = SmartDashboard.getNumber("PIDLeft_I ", 0);
		PIDControlLeft_D = SmartDashboard.getNumber("PIDLeft_D ", 0);
		
		maxEncoderSpeedRight = 0;
		minEncoderSpeedRight = 0;
		
		maxEncoderSpeedLeft = 0;
		minEncoderSpeedLeft = 0;
		
		Input_Multiplier = SmartDashboard.getNumber("Throttle Multiplier ",1);
		
		PID_enabled = SmartDashboard.getBoolean("PID_Enabled ", false);
		
		throttleInput = SmartDashboard.getNumber("Throttle Input ",0);
		
		_talonRight.config_kF(slotIdx, PIDControlRight_F, timeoutMs);
		_talonRight.config_kP(slotIdx, PIDControlRight_P, timeoutMs);
		_talonRight.config_kI(slotIdx, PIDControlRight_I, timeoutMs);
		_talonRight.config_kD(slotIdx, PIDControlRight_D, timeoutMs);
		
 		_talonRight.setIntegralAccumulator(0.00, pidIdx, timeoutMs);
		_talonRight.setSelectedSensorPosition(0, pidIdx, timeoutMs);
		
		_talonRight.setNeutralMode(NeutralMode.Coast);
		_talonRight.setSensorPhase(false);
		
		_talonLeft.config_kF(slotIdx, PIDControlLeft_F, timeoutMs);
		_talonLeft.config_kP(slotIdx, PIDControlLeft_P, timeoutMs);
		_talonLeft.config_kI(slotIdx, PIDControlLeft_I, timeoutMs);
		_talonLeft.config_kD(slotIdx, PIDControlLeft_D, timeoutMs);
		
 		_talonLeft.setIntegralAccumulator(0.00, pidIdx, timeoutMs);
		_talonLeft.setSelectedSensorPosition(0, pidIdx, timeoutMs);
		
		_talonLeft.setNeutralMode(NeutralMode.Coast);
		_talonLeft.setSensorPhase(false);
		
		logger = new PIDLogger(_talonRight, "TalonSRX");
		logger = new PIDLogger(_talonLeft, "TalonSRX");
		
	}
	
    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {

//    	logger.enableLogging(true);
    	
    	if (_joy.getRawButton(1))
    	{
//    		System.out.println("Button Pressed");
    	
    		if (PID_enabled)
    		{
    			_talonRight.selectProfileSlot(slotIdx, pidIdx);
    			_talonLeft.selectProfileSlot(slotIdx, pidIdx);
    		
    			_talonRight.set(ControlMode.Velocity,throttleInput * Input_Multiplier);
    			_talonLeft.set(ControlMode.Velocity,throttleInput * Input_Multiplier);
    		}
    		else
    		{
    			_talonRight.set(ControlMode.PercentOutput, throttleInput);
    			_talonLeft.set(ControlMode.PercentOutput, throttleInput);
    		}
    	}
    	else
    	{
        	if (PID_enabled)
        	{
        		_talonRight.set(ControlMode.Velocity,0.0);
        		_talonLeft.set(ControlMode.Velocity,0.0);
        	}
        	else
        	{
        		_talonRight.set(ControlMode.PercentOutput, 0.0);
        		_talonLeft.set(ControlMode.PercentOutput, 0.0);
        	}
    	}
    	
    	
        if(++_loops >= 5) {
        	_loops = 0;
//        	System.out.println(leftYstick);
//        	System.out.println(_joy.getRawButton(1));
        
        	if (PID_enabled)
        	{
//        		logger.run();
        		
        		SmartDashboard.putNumber("Throttle Input ", throttleInput);
        		SmartDashboard.putNumber("Throttle Multiplier ", Input_Multiplier);
        		SmartDashboard.putBoolean("PID_Enabled ", PID_enabled);
        		SmartDashboard.putNumber("Requested ", throttleInput * Input_Multiplier);
        		
        		SmartDashboard.putNumber("Encoder Speed_R ", _talonRight.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("PID_F_R ", PIDControlRight_F);	
        		SmartDashboard.putNumber("PID_P_R ", PIDControlRight_P);	
        		SmartDashboard.putNumber("PID_I_R ", PIDControlRight_I);	
        		SmartDashboard.putNumber("PID_D_R ", PIDControlRight_D);
        		SmartDashboard.putNumber("Calculated Error_R ",throttleInput * Input_Multiplier - _talonRight.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("Reported Request_R ", _talonRight.getClosedLoopTarget(0));
        		SmartDashboard.putNumber("Reported Error_R ", _talonRight.getClosedLoopError(0));
        		SmartDashboard.putNumber("Encoder Speed_R ", _talonRight.getSelectedSensorVelocity(0));
        		
        		SmartDashboard.putNumber("Encoder Speed_L ", _talonLeft.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("PID_F_L ", PIDControlLeft_F);	
        		SmartDashboard.putNumber("PID_P_L ", PIDControlLeft_P);	
        		SmartDashboard.putNumber("PID_I_L ", PIDControlLeft_I);	
        		SmartDashboard.putNumber("PID_D_L ", PIDControlLeft_D);
        		SmartDashboard.putNumber("Calculated Error_L ",throttleInput * Input_Multiplier - _talonLeft.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("Reported Request_L ", _talonLeft.getClosedLoopTarget(0));
        		SmartDashboard.putNumber("Reported Error_L ", _talonLeft.getClosedLoopError(0));
        		SmartDashboard.putNumber("Encoder Speed_L ", _talonLeft.getSelectedSensorVelocity(0));
        	}
        	else
        	{
        		if (_talonRight.getSelectedSensorVelocity(0) > maxEncoderSpeedRight)
        		{
        			maxEncoderSpeedRight = _talonRight.getSelectedSensorVelocity(0);
        		}
        		
        		if (_talonRight.getSelectedSensorVelocity(0) < minEncoderSpeedRight)
        		{
        			minEncoderSpeedRight = _talonRight.getSelectedSensorVelocity(0);
        		}
        		
           		SmartDashboard.putNumber("Throttle Input ", throttleInput);
        		SmartDashboard.putNumber("Encoder Speed_R ", _talonRight.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("Maximum Encoder Speed_R ", maxEncoderSpeedRight);
        		SmartDashboard.putNumber("Minimum Encoder Speed_R ", minEncoderSpeedRight);
        		
        		if (_talonLeft.getSelectedSensorVelocity(0) > maxEncoderSpeedLeft)
        		{
        			maxEncoderSpeedLeft = _talonLeft.getSelectedSensorVelocity(0);
        		}
        		
        		if (_talonLeft.getSelectedSensorVelocity(0) < minEncoderSpeedLeft)
        		{
        			minEncoderSpeedLeft = _talonLeft.getSelectedSensorVelocity(0);
        		}
        		
           		SmartDashboard.putNumber("Throttle Input ", throttleInput);
        		SmartDashboard.putNumber("Encoder Speed_L ", _talonLeft.getSelectedSensorVelocity(0));
        		SmartDashboard.putNumber("Maximum Encoder Speed_L ", maxEncoderSpeedLeft);
        		SmartDashboard.putNumber("Minimum Encoder Speed_L ", minEncoderSpeedLeft);
        			
        			
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

