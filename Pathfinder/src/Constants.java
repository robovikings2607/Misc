/*
 * This class holds the trajectories definitions
 */


import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class Constants {
	
	/*
	 * General Constants
	 */
	public static final double robotTrackWidth = 28.0;			// The width from center of center wheels
	public static final double ticksPerInch = 50.775;			// The number of encoder ticks to travel one inch
	public static final double trajectoryPointDuration = 0.010; // The duration of each trajectory point interval
	
	public static final int numberOfTrajectories = 7;

	/*
	 * Configuration for the Left Switch mission
	 */
	public static final String nameLeftSwitch = "LeftSwitch";
	public static final double velLeftSwitch = 120;
	public static final double accLeftSwitch = 100;
	public static final double jerkLeftSwitch = 100;
	public static final Waypoint[] pointsLeftSwitch = new Waypoint[] {
		     new Waypoint(168,16,Pathfinder.d2r(90)),
			 new Waypoint(104,124,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Right Switch mission
	 */
	public static final String nameRightSwitch = "RightSwitch";
	public static final double velRightSwitch = 120;
	public static final double accRightSwitch = 100;
	public static final double jerkRightSwitch = 100;
	public static final Waypoint[] pointsRightSwitch = new Waypoint[] {
		     new Waypoint(168,16,Pathfinder.d2r(90)),
			 new Waypoint(216,120,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Left Start Cross the Line mission
	 */
	public static final String nameLeftCrossLine = "LeftCrossLine";
	public static final double velLeftCrossLine = 120;
	public static final double accLeftCrossLine = 50;
	public static final double jerkLeftCrossLine = 50;
	public static final Waypoint[] pointsLeftCrossLine = new Waypoint[] {
		     new Waypoint(45.7,16,Pathfinder.d2r(90)),
			 new Waypoint(45.7,136,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Right Start Cross the Line mission
	 */
	public static final String nameRightCrossLine = "RightCrossLine";
	public static final double velRightCrossLine = 60;
	public static final double accRightCrossLine = 50;
	public static final double jerkRightCrossLine = 50;
	public static final Waypoint[] pointsRightCrossLine = new Waypoint[] {
		     new Waypoint(278.3,16.0,Pathfinder.d2r(90)),
			 new Waypoint(278.3,104,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Left Scale mission
	 */
	public static final String nameLeftScale = "LeftScale";
	public static final double velLeftScale = 120;
	public static final double accLeftScale = 100;
	public static final double jerkLeftScale = 100;
	public static final Waypoint[] pointsLeftScale = new Waypoint[] {
		     new Waypoint(43.7,17.75,Pathfinder.d2r(90)),
			 new Waypoint(30,298,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Right Scale mission
	 */
	public static final String nameRightScale = "RightScale";
	public static final double velRightScale = 120;
	public static final double accRightScale = 100;
	public static final double jerkRightScale = 100;
	public static final Waypoint[] pointsRightScale = new Waypoint[] {
		     new Waypoint(278.3,16,Pathfinder.d2r(90)),
			 new Waypoint(278.3,316,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for a Test mission
	 */
	public static final String nameTest = "Test";
	public static final double velTest = 60;
	public static final double accTest = 50;
	public static final double jerkTest = 50;
	public static final Waypoint[] pointsTest = new Waypoint[] {
		     new Waypoint(276,16,Pathfinder.d2r(90)),
			 new Waypoint(290,30,Pathfinder.d2r(180))
	};
	
}
