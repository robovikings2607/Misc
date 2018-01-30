package org.usfirst.frc.team469.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Robot extends IterativeRobot {
  
	TalonSRX _talon = new TalonSRX(15);	
	Joystick _joy = new Joystick(0);	

	int _loops = 0;
	
	int timeoutMs = 10;
	
	public void robotInit() {
 
	}
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	/* get gamepad axis */
    	double leftYstick = _joy.getRawAxis(1);

        	/* Percent voltage mode */
		_talon.configPeakOutputForward(0.5, timeoutMs);  // 50% peak voltage forward
		_talon.configPeakOutputReverse(-0.5, timeoutMs);  // 50% peak voltage reverse
		_talon.configNominalOutputForward(0.0, timeoutMs);
		_talon.configNominalOutputReverse(0.0, timeoutMs);
    		_talon.setInverted(true);
        	_talon.set(ControlMode.PercentOutput, leftYstick);

        if(++_loops >= 10) {
        	_loops = 0;
        	System.out.println(leftYstick);
        }
    }
}

