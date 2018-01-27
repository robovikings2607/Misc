package org.usfirst.frc.team2607.robot;

import com.team254.lib.trajectory.Trajectory.Segment;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Talon;

public class Transmission {
	
	private Talon m;
	public RobovikingModPIDController pidLoop;
	public Encoder enc;
	public ADXRS450_Gyro gyro;
	public PIDLogger log;
	private String name;
	
	public Transmission(Talon tal, boolean side) {
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		enc = new Encoder(0 , 1 , false, Encoder.EncodingType.k1X);
		enc.setPIDSourceType(PIDSourceType.kDisplacement);
		enc.reset();
		enc.setDistancePerPulse(0.004);	// ((Wheel Di. (in) / 12) * pi) / enc counts
		//0.00766990393942820614859043794746

		m =  tal;

		pidLoop = new RobovikingModPIDController(0.5, 0.001, 0.00001, .0335, .0025, 0, enc, m, gyro);
		//0.14, 0.001, 0.0, 0.0151, 0.0022, -3.0/80.0, enc, m, gyro
		pidLoop.setTurnDirection(side);
		pidLoop.setPositionInputRange(0, 7000.0);
		pidLoop.setAccelerationInputRange(-20, 20);
		pidLoop.setVelocityInputRange(-15.0, 15.0);
		pidLoop.setHeadingInputRange(-360, 360);
		
		log = new PIDLogger(this);
		
		log.start();
	}
	
	public void enableVelPID() {
		pidLoop.enable();
	}
	
	public void setSP(double pos, double vel, double acc, double ang) {
		pidLoop.setSetpoint(pos, vel, acc, ang);
	}
	
	public void setSP(Segment s){
		pidLoop.setSetpoint(s);
	}
	
	public void disableVelPID() {
		pidLoop.disable();
	}
	
	public void setName(String n){
		name = n;
	}
	
	public String getName(){
		return name;
	}
	
	public void displayValues() {
		//System.out.println("SP POS: " + pidLoop.getSetpoint()[0] + " SP VEL: " + pidLoop.getSetpoint()[1] + 
			//	" REAL POS: " + enc.getDistance() + " REAL VEL: " + enc.getRate());
		System.out.println(pidLoop.getSetpoint()[0] + "," + enc.getDistance() + "," +
				pidLoop.getSetpoint()[1] + "," + enc.getRate() + "\n" +
				pidLoop.getSetpoint()[3] + "," + gyro.getAngle());
		
	}
}
