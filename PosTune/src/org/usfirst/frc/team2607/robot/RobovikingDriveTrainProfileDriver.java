package org.usfirst.frc.team2607.robot;

import java.util.ArrayList;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.Trajectory.Segment;

import edu.wpi.first.wpilibj.Notifier;

public class RobovikingDriveTrainProfileDriver {
	
	private Transmission leftMotors;
	//private PIDController positionPID;
	private double dtSeconds;
	private Path path;
	private ArrayList<Segment> leftVelPts;
	private int numPoints;
	private Trajectory lt;
	private boolean running = false, done = false;
	private long step;
	
	private class PeriodicRunnable implements java.lang.Runnable {
		private long startTime;
		private boolean firstTime;
		
		public PeriodicRunnable() {
			firstTime = true;
		}
		
		public void run() {
	    	if (firstTime) {
	    		firstTime = false;
	    		startTime = System.currentTimeMillis();
	    		running = true;
	    		done = false;
	    		leftMotors.enableVelPID();
	    	}
	    	step = (System.currentTimeMillis() - startTime) / (long)(dtSeconds * 1000);
	    	try {
	    		double pos = leftVelPts.get((int)step).pos,
	    				acc = leftVelPts.get((int)step).acc,
	    				vel = leftVelPts.get((int)step).vel;
	    		//System.out.println("Step: " + step + " left SP: " + pos + ", " + vel + ", " + acc);
	    		leftMotors.setVelSP(pos,vel, acc);
	    		
	    	} catch (Exception e) {
	    		pointExecutor.stop();
	    		running = false;
	    		done = true;
	    		leftMotors.disableVelPID();

	    	}
	    }
	}

	Notifier pointExecutor = new Notifier(new PeriodicRunnable());

	public RobovikingDriveTrainProfileDriver(Transmission leftMotors, Path path) {
		this.leftMotors = leftMotors;
		this.path = path;
		this.leftVelPts = new ArrayList<Segment>();
		//store the velocity pts
		numPoints = path.getLeftWheelTrajectory().getNumSegments();
		lt = this.path.getLeftWheelTrajectory();
		
		for (int i = 0; i < numPoints; i++) {
			leftVelPts.add(lt.getSegment(i));
			if (i==0) dtSeconds = lt.getSegment(i).dt;
		}
	}

	public boolean isRunning() {
		return running;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public void followPath() {
		pointExecutor.startPeriodic(dtSeconds / 2.0);
	}
	
}
