package org.usfirst.frc.team2607.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Talon;

public class Transmission {
	
	private Talon m;
	public RobovikingModPIDController pidLoop;
	public Encoder enc;
	
	public Transmission() {
		enc = new Encoder(0 , 1 , false, Encoder.EncodingType.k1X);
		enc.setPIDSourceType(PIDSourceType.kDisplacement);
		enc.reset();
		enc.setDistancePerPulse(0.00766990393942820614859043794746);	// ((Wheel Di. (in) / 12) * pi) / enc counts

		m =  new Talon(0);

		pidLoop = new RobovikingModPIDController(0.05, 0.001, 0.0, 0.0151, 0.00121, enc, m);
		pidLoop.setPositionInputRange(0, 7000.0);
		pidLoop.setAccelerationInputRange(-20, 20);
		pidLoop.setVelocityInputRange(-15.0, 15.0);
	}
	
	public void enableVelPID() {
		pidLoop.enable();
	}
	
	public void setVelSP(double pos, double vel, double acc) {
		pidLoop.setSetpoint(pos, vel, acc);
	}
	
	public void disableVelPID() {
		pidLoop.disable();
	}
	
	public void displayValues() {
		//System.out.println("SP POS: " + pidLoop.getSetpoint()[0] + " SP VEL: " + pidLoop.getSetpoint()[1] + 
			//	" REAL POS: " + enc.getDistance() + " REAL VEL: " + enc.getRate());
		System.out.println(pidLoop.getSetpoint()[0] + "," + enc.getDistance() + "," +
				pidLoop.getSetpoint()[1] + "," + enc.getRate());
	}
}
