package org.usfirst.frc.team2607.robot;



import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This sample program was based on the Motor Controller CTRE FRC Sample program in Github.
 * 
 * This program provides an example on how to setup and use a TalonSRX motor controller to move
 * the motor a specific position (in number of rotations or fractions of rotations) in a clockwise
 * or counter-clockwise direction.
 * 
 * This can be useful in getting the robot to move to a particular location in the field
 * in Autonomous mode by making the wheels turn so many times in one direction,
 * or to operate a game mechanism in the robot to a specific position (for example,
 * to more a shooter mechanism to a particular position).
 */

/*
 * IterativeRobot is a standard class defined in the FRC Java library.  We created a class named "Robot"
 * which is based on the class "IterativeRobot".  This class has a number of methods which we will
 * "Override" with our own code.  We need only two of the IterativeRobot methods for this sample code:
 * 
 * 1) robotInit() which is automatically called to initialize the robot at power up or when the 
 * code is reset.  We put our initialization statements there, and
 * 
 * 2) teleopPeriodic() which is called once every 20 milliseconds approximately (50 times a second)
 * to execute the code in it when the robot is "Enabled" in "TeleOperated" mode from the driver
 * station.
 * 
 * Here's the complete list of methods supported by the IterativeRobot class.
 * 
 * 1) robotInit() -- provide for initialization at robot power-on, robot reset, or code reset.
 * 
 * 2) init() functions -- each of the following functions is called once when the
 * appropriate mode is entered:
 * 
 *   2.a) disabledInit()   -- called only when first disabled
 *   2.b) autonomousInit() -- called each and every time autonomous is entered from
 *                         another mode
 *   2.c) teleopInit()     -- called each and every time teleop is entered from
 *                         another mode
 *   2.d) testInit()       -- called each and every time test is entered from
 *                         another mode
 *
 * 3) periodic() functions -- each of these functions is called on an interval:
 *   3.a) robotPeriodic()
 *   3.b) disabledPeriodic()
 *   3.c) autonomousPeriodic()
 *   3.d) teleopPeriodic()
 *   3.e) testPeriodic()
 */

public class Robot extends IterativeRobot {
	
/*
 * An Object based on the class TalonSRX is created to control the TalonSRX motor controller, and
 * hence control the motor that is controlled by the TalonSRX.  
 * The instantiation of an object based on this class requires one field, the CAN bus address of the
 * Talon SRX controller that we want to associate with this object.  In this case, the CAN bus
 * address of the TalonSRX is 15.  TalonSRX controllers are connected to the RoboRio computer via a
 * CAN bus.  Many TalonSRX controllers can be connected to the same CAN bus, and the RoboRio
 * differentiates between them via the CAN bus address.  You can check or change this address in the
 * TalonSRX controller using the "NI Web Based Configuration" interface.  Using Internet Explorer
 * with the Silverlight plug-in, you put the IP address of the RoboRio in the URL to access the
 * "NI Web Based Configuration" tool.  If using the USB port to connect to the RoboRio, the URL is
 * http://172.22.11.2 and if using the Ethernet (Wifi or Wired) the URL is http://10.26.7.2 for team
 * 2607 (the IP address on the Ethernet is derived from the team number).  For team 607 the IP
 * address of the RoboRio is 10.6.7.2 and the URL http://10.6.7.2.  Once connected to the RoboRio via
 * the NI Web Based Configuration tool, select the CAN bus icon and you should be able to find all
 * the devices that are connected to the RoboRio via the CAN bus.  You should familiarize yourself
 * with the TalonSRX LEDs to diagnose CAN bus issues.
 */
	TalonSRX _talon = new TalonSRX(15);
	
/*
 * We are using the targetPosition field to set the motor number of rotations in native
 * units either clockwise or counter-clockwise.  The TalonSRX is connected to an encoder to determine the
 * position of the mechanism.  Encoders have "native" units of measurements which are usually given
 * by the manufacturer in number of native units for a full turn.  An encoder may have 256 native
 * units for a full turn, or 64, or 128, or any number and you need to find out this number to
 * successfully control the mechanism you want to control.  Many mechanism have gears and the encoder
 * may not be right at the motor shaft but in some other place in the mechanism.  The end result is
 * that one encoder rotation may not correspond to one motor rotation.  For example, if the encoder
 * is located after gears and/or pulleys, you need to calculate the "gear ratio" which is a number
 * that indicates how many turns of the encoder correspond to how many turns of the motor.  For example,
 * if the gear ratio is 2:1 (2 to 1), then 1 motor rotation correspond to 2 encoder rotations.  The way
 * the gear ratios are expressed may be different than the way they expressed here, but the idea is the
 * same: you have to find out how many turns of the encoder correspond to one turn of the motor.  You
 * also need to know this information to properly control the mechanism. 
 */
	double targetPosition;

/*
 * The _loops field is an utility field and you will see how it is used later.	
 */
	int _display_Loops=0;
	int _adjustPosition_Loops=0;
	
	int timeoutMs = 0;  // A value of zero means not checks for correct setting
	int slotIdx = 0;    // I need to read about this.
	int pidIdx = 0;    // Zero is for the primary profile
	
	double PIDControlF;
	double PIDControlP;
	double PIDControlI;
	double PIDControlD;



/*
 * The statement below creates a new object _joy of the from the class Joystick.  The class invocation
 * takes one parameter, the Joystick number, in this case Joystick number zero.  We used an XBOX
 * control pad to run this program.
 */
	Joystick _joy = new Joystick(0);

/*
 * The robotInit method is called once when you power up or reset the code in the RoboRio.
 * We put all the initialization statements in this method.
 */

	@Override
	public void robotInit() {
		

	/*
	 * The method below set the type of encoder that the TalonSRX is connected to.  In this 
	 * particular case, we were using a Quad Encoder which is a specific type of encoder.  We used
	 * a Grayhill 63R256 quad encoder.  The 256 in the model number means 256 ticks per
	 * rotation, but since it is a quad encoder, in the RoboRio, we use 256 x 4 (Quad = four) or 1024
	 * native units or ticks per rotation.  It is important to set the correct encoder type so the
	 * RoboRio can understand the signals from the encoder.  And remember, these are 1024 ticks per
	 * encoder rotation which may not be the same as motor rotations.  Keep that in mind.
	 */
		
//	_talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		_talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidIdx, timeoutMs);

	/*
	 * The method below allows us to synchronize the direction of rotation of the motor with the direction
	 * of rotation of the encoder.  What's important here is to keep the direction of rotation of the motor
	 * synchronized with the direction of rotation of the encoder.  So, when the TalonSRX applies
	 * positive voltj ,age to the motor, it expects the encoder tick count to increase.  When the TalonSRX
	 * applies negative voltage to the motor, it expects the encoder tick count to decrease. For example,
	 * let's say we want the mechanism to move to position +100 ticks from an initial position of 0 ticks.
	 * The TalonSRX needs to move the mechanism from position 0 ticks to position +100 ticks.  The TalonSRX
	 * will apply positive voltage to the motor (it needs to move in the positive direction) and the 
	 * encoder should go (0, 1, 2, 3, 4 ...) and when it gets to +100 then the TalonSRX will stop the
	 * motor.  That works.  However, if the encoder and motor are out of sync, when the TalonSRX applies
	 * positive voltage, the encoder counter would decrease (0, -1, -2, -3, -4 ...), and the encoder counter
	 * will never reach +100 and the TalonSRX will never stops supplying voltage to the motor.  As a result,
	 * you will never reach the desired position of +100.
	 * To correct the situation where the motor and encoder are out of sync, this method is used to
	 * keep the synchronization (false) or to reverse the synchronization (true).  This is much easier
	 * than to mechanically make sure that the motor and the encoder are synchronized.
	 * The easiest way to verify synchronization is by trial and error.  Run the mechanism and look at the
	 * LEDs in the TalonSRX to see if positive or negative voltage is applied to the motor (you need to
	 * familiarize yourself with the TalonSRX LEDs).  If the LEDs indicate positive voltage and the 
	 * encoder count is increasing, your are good to go.  Otherwise, reverse the Sensor using the method
	 * below, and try again.
	 */
		
	//	_talon.reverseSensor(false);
		
		_talon.setInverted(false);
			
	/*
	 * The method below sets the number of encoder ticks per rotation (revolution).  As stated before, the
	 * Grayhill 63R256 quad encoder provides 1024 ticks per rotation (revolution).
	 */

		// This method does not seem to be supported in the 2018 TalonSRX class.  Commented out for now.
	//	_talon.configEncoderCodesPerRev(1024);
		

		
	/*
	 * The method below sets the voltage that the TalonSRX should send to the motor when it is
	 * at nominal throttle position.  This needs to be research more because it does not seem to do
	 * anything in the position closed loop.
	 * 
	 */
	//	_talon.configNominalOutputVoltage(+2f, -2f);
		
		_talon.configNominalOutputForward(0.0, timeoutMs);
		_talon.configNominalOutputReverse(0.0, timeoutMs);
		
	
	/*
	 * The method below sets the maximum output voltage that the TalonSRX can apply to the motor, for
	 * the forward direction and the backwards direction.  If you set these two values to zero, the
	 * motor won't move.  You can also prevent the motor to move forward if you set the forward voltage
	 * to zero, or to more backwards if you set the backwards voltage to zero.  The maximum value for
	 * the FRC robot is 12 volts in either direction.  In this example, I used 6 Volts in both directions
	 * since that fits the mechanism (less abrupt moves and we were moving less than one rotation so
	 * we did not need that much speed).  Also, the mechanism had no load, so did not need any 
	 * significant motor torque.
	 */
		
	//	_talon.configPeakOutputVoltage(+6f, -6f);
		
		_talon.configPeakOutputForward(0.50, timeoutMs);  // 50% peak voltage forward
		_talon.configPeakOutputReverse(-0.50, timeoutMs);  // 50% peak voltage reverse
		
		
	/*
	 * The method below sets the accepted error range in native ticks that we are willing to tolerate.
	 * Ideally, we would like the error to be zero (if we tell the TalonSRX to move to a position
	 * 100 native ticks clockwise, we want the encoder to stop at zero).  However, in practice, there
	 * will always be some error.  In the example below, we are telling the TalonSRX that if we are
	 * within 10 encoder ticks from the desired position, then it can stop there.  For example, if the
	 * desired position is -100, then it's Ok to stop once the encoder tick count is between -90 and
	 * -110 encoder ticks. This is useful because if we can live with some error window, it speeds up
	 * the process as the TalonSRX can stop searching for the exact position sooner. 
	 */
	//	_talon.setAllowableClosedLoopErr(10);
		
		_talon.configAllowableClosedloopError(slotIdx, 10, timeoutMs);
		
	/*
	 * The method below sets the ramp rate, or how fast the TalonSRX will increase the voltage
	 * in units of percent of throttle per millisecond.  It needs more research because it did not
	 * seem to do anything as we changed this parameter.
	 */
	//	_talon.setCloseLoopRampRate(0.0);
		
		_talon.configClosedloopRamp(0.0, timeoutMs);
		
/*
 * The TalonSRX allows you to store PID parameters into two different profiles (profiles 0 and 1).
 * For example, if you have need a high speed and low speed driving mode, you can store the high speed
 * PID settings in Profile 0 and the low speed PID settings in Profile 1, and then switch back and
 * forth depending on whether you're in high speed or low speed mode.  We use Profile 0 only in this
 * example as we do not have a need for a second profile.
 */
		
//		_talon.setProfile(0);
		
		_talon.selectProfileSlot(slotIdx, pidIdx);

/*
 * The statements below are to set the PID loop.  There are books about this stuff and you can Google
 * PID loops and you will find lots of information about PID loops.  For FRC purposes, the TalonSRX
 * programming guide document has procedures to set these parameters in the context of FRC, so this
 * would be your main reference.  At a high level, these PID parameters control the behavior of the
 * mechanism in terms of acceleration, jerk, velocity, speed, accuracy, etc.  For example, if you want
 * the motor to move from position 0 ticks to position 1000 ticks, the PID parameters can be used to
 * control how fast you get there, how to accelerate at the beginning and as you get closer to the 
 * target position, how quickly and accurately to resolve position error (error is the difference of
 * the target position and the actual position of the encoder), etc.  Sometimes you can get to 
 * the target position fast, but sometimes you need to slow down (for example, if the mechanism
 * would break if you try to move it too fast).
 * 
 * There is much to read about PID loops, and PID tuning is a skill you should develop.
 * 
 * For position closed loop control, do not use the Forward gain (F parameter).  Use mostly the 
 * Proportional gain (P parameter), and if necessary, use the Integral and Derivative (I and D
 * parameters) to complete the job.  For this example, just using P was sufficient for the job.
 */
		
//		_talon.setF(0.0);
//		_talon.setP(3.5);
//		_talon.setI(0.0);
//		_talon.setD(0.0);
		
		PIDControlF = 0.00;
		PIDControlP = 0.6;
		PIDControlI = 0.01;
		PIDControlD = 0.00;

		SmartDashboard.putNumber("PIDControl F: ", PIDControlF);
		SmartDashboard.putNumber("PIDControl P: ", PIDControlP);
		SmartDashboard.putNumber("PIDControl I: ", PIDControlI);
		SmartDashboard.putNumber("PIDControl D: ", PIDControlD);
		
		_talon.config_kF(slotIdx, PIDControlF, timeoutMs);
		_talon.config_kP(slotIdx, PIDControlP, timeoutMs);
		_talon.config_kI(slotIdx, PIDControlI, timeoutMs);
		_talon.config_kD(slotIdx, PIDControlD, timeoutMs);
		

	
/*
 * We can now set the TalonSRX in Closed Loop Position control mode.  You can do it in the 
 * robotInit() method if this TalonSRX will always be in Position control mode.  But if you need
 * to switch this TalonSRX between Position control mode and Speed control mode, for example,
 * then you can switch to the appropriate mode lated in the code as appropriate.
 * In this sample code, we are going to change to Position control later in the code.
 */
		
//		_talon.changeControlMode(TalonControlMode.Position);

	}
	
	@Override
	public void teleopInit() {


 		_talon.setIntegralAccumulator(0.00, pidIdx, timeoutMs);
		_talon.setSelectedSensorPosition(0, pidIdx, timeoutMs);
		
/*		
		PIDControlF = SmartDashboard.getNumber("PIDControl F: ", 0.00);
		PIDControlP = SmartDashboard.getNumber("PIDControl P: ", 0.00);
		PIDControlI = SmartDashboard.getNumber("PIDControl I: ", 0.00);
		PIDControlD = SmartDashboard.getNumber("PIDControl D: ", 0.00);
		
		_talon.config_kF(slotIdx, PIDControlF, timeoutMs);
		_talon.config_kP(slotIdx, PIDControlP, timeoutMs);
		_talon.config_kI(slotIdx, PIDControlI, timeoutMs);
		_talon.config_kD(slotIdx, PIDControlD, timeoutMs);
*/
	}	
	

	
/*
 * The method teleopPeriodic() is called approximately every 20 milliseconds when the robot is
 * enabled in Teleoperated mode from the drivers station.
 */
	

	@Override
	public void teleopPeriodic() {
		
	/*
	 * The code below instructs the TalonSRX to move to a target position depending on the position
	 * of the Left-Y Axis of the gamepad. We adjust the position once a second by using the variable
	 * adjustPosition_Loops.  We execute the code in the "if" block once every 50 times the
	 * teleopPeriodic() method is called and we know this method is called once every 20 milliseconds
	 * approximately.  Therefore, 50 * 20 milliseconds = 1,000 milliseconds = 1 second.  After the 
	 * variable _adjustPosition_Loops reaches the number 50, we execute the code inside the "if" 
	 * block and zero the _adjustPosition_Loops variable.
	 */

		if(_adjustPosition_Loops++ > 10)
		{
			/*
			 * Here we read the position of the Left-Y Axis of gamepad number
			 * 1 and multiply it by 2048. The Left-Y Axis range is from -1 (full up) to +1 (full down), 
			 * and zero is in the middle.  By multiplying it by 2048, we can set the target position
			 * range from -2048 (two complete rotation backwards) to +2048 (two complete rotations forward).
			 * We are using an encoder where one full rotation is 1024 ticks.
			 */
			targetPosition = _joy.getRawAxis(1) * 2048.0;
			
			/*
			 * We round the encoder position to the nearest 250.  For example, the resulting target
			 * position are -2000, -1750, -1500, -1250, .... +2000.  This is to prevent the mechanism to
			 * stutter because the inputs from the game pad controller are noisy such that the
			 * target position will be changing every seconds if we don't make the positions fall into
			 * fixed numbers.  You can experiment by commenting out the rounding instructions to
			 * understand what the effect of that is.
			 */
			targetPosition = Math.round(targetPosition/250.0) * 250.0;
			/*
			 * Here's how we set the TalonSRX to Position control mode.  The TalonSRX will then
			 * interpret instructions to move to a target position.  There are other control modes
			 * besides position: speed, current, etc.  We want to control position.
			 */
//			_talon.changeControlMode(TalonControlMode.Position);
			_talon.set(ControlMode.Position,targetPosition);

			/*
			 * Here's how we set the target position.
			 */
//			_talon.set(targetPosition);
			_adjustPosition_Loops=0;
		}
		

			
/*
 * This next code block prints the encoder position, the encoding error, the button
 * bring pressed, and the desired encoder position.  The error is the difference
 * between the target position and the actual position of the encoder.  Ideally the error should be
 * zero, meaning that the TalonSRX was able to position the wheel or mechanism exactly at the 
 * requested target position.  In reality, it is not practical to get to zero error because of a 
 * number of mechanical issues, like gears play, rubber band or chains play, etc.
 * 
 * We used the variable _loops to update this information NOT every single time the teleopPeriodic()
 * method is called, but once every twenty times the teleopPeriodic() method is called. This is the 
 * equivalent of displaying or updating the fields once every 0.4 seconds (20 calls * 20 milliseconds
 * per call = 400 milliseconds = 0.4 seconds).
 */
		
		if(_display_Loops<20) {
			
			/*
			 * If _loops is less than 20, do not display or update the information, just 
			 * increase _loops by one and wait for the teleopPeriodic() to be called again.
			 */
			_display_Loops++;
		}
		else
		{
			/*
			 * We use the SmartDashboard to display the information.  The SmartDashboard runs in the
			 * drivers station and it is invoked automatically when the Drive Station starts.  You
			 * can also start SmartDashboard manually.  The application resides in the "wpilib/tools"
			 * folder.  The wpilib folder is usually under the user's home folder unless it was
			 * installed in a different location.
			 * 
			 * After displaying or updating the information, the _loops counter is zeroed.  Failure
			 * to do this will result in the display/update statements to run every single time the 
			 * teleopPeriodic() method is called.
			 */
			
			/*
			 * The for loop below searches for which button is being pressed or zero if no button is
			 * being pressed.
			 */
			
			SmartDashboard.putNumber("Desired Position", targetPosition);
			SmartDashboard.putNumber("Numerical Position", _talon.getSelectedSensorPosition(pidIdx));
			SmartDashboard.putNumber("Error: ", _talon.getClosedLoopError(pidIdx));
			SmartDashboard.putNumber("Axis Position", _joy.getRawAxis(1));
			_display_Loops=0;
		}
	}	
}
