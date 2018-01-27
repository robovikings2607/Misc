package org.usfirst.frc.team2607.robot;

import java.util.ArrayList;

/*
 * maxSpeed (rotations/sec)
 * targetDist (# of rotations to travel)
 * 
 * NOTE:  sign of maxSpeed and targetDist must always match and reflect desired direction of travel.  
 * For example if I'm currently at position 25 and my new target is position -25, my maxSpeed is negative and my
 * targetDist is -50
 *
 * accelTime (ms)
 * decelTime (ms)
 * dt (ms) (standard is 10ms)
 * 
 * Time4 = (targetDist / maxSpeed) * 1000  (this is the theoretical minimum time in ms needed to reach target)
 * Filter1 = Math.ceil(accelTime / dt)
 * Filter2 = Math.ceil(decelTime / dt)
 * N = Time4 / dt			(doesn't appear to be used, effectively this is the min number of profile points 
 *
 *
 * step = 0, time = dt * step; 
 * 
 */

public class SRXMotionProfile {
	
	public class MotionProfilePoint {
		private double velocity, position, duration;
		
	}
	
	private ArrayList<MotionProfilePoint> profile;

	public SRXMotionProfile() {
		profile = new ArrayList<MotionProfilePoint>();
	}
	
	public void generate() {
		
		int step = 0;
		
		
	}

}
