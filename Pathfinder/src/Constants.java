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
			 new Waypoint(220,124,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Left Start Cross the Line mission
	 */
	public static final String nameLeftCrossLine = "LeftCrossLine";
	public static final double velLeftCrossLine = 120;
	public static final double accLeftCrossLine = 100;
	public static final double jerkLeftCrossLine = 100;
	public static final Waypoint[] pointsLeftCrossLine = new Waypoint[] {
		     new Waypoint(48,16,Pathfinder.d2r(90)),
			 new Waypoint(48,160,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Right Start Cross the Line mission
	 */
	public static final String nameRightCrossLine = "RightCrossLine";
	public static final double velRightCrossLine = 120;
	public static final double accRightCrossLine = 100;
	public static final double jerkRightCrossLine = 100;
	public static final Waypoint[] pointsRightCrossLine = new Waypoint[] {
		     new Waypoint(272,16,Pathfinder.d2r(90)),
			 new Waypoint(272,160,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Left Scale mission
	 */
	public static final String nameLeftScale = "LeftScale";
	public static final double velLeftScale = 120;
	public static final double accLeftScale = 100;
	public static final double jerkLeftScale = 100;
	public static final Waypoint[] pointsLeftScale = new Waypoint[] {
		     new Waypoint(48,16,Pathfinder.d2r(90)),
			 new Waypoint(48,320,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Right Scale mission
	 */
	public static final String nameRightScale = "RightScale";
	public static final double velRightScale = 120;
	public static final double accRightScale = 100;
	public static final double jerkRightScale = 100;
	public static final Waypoint[] pointsRightScale = new Waypoint[] {
		     new Waypoint(276,16,Pathfinder.d2r(90)),
			 new Waypoint(276,320,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for a Test mission
	 */
	public static final String nameTest = "Test";
	public static final double velTest = 120;
	public static final double accTest = 100;
	public static final double jerkTest = 100;
	public static final Waypoint[] pointsTest = new Waypoint[] {
		     new Waypoint(276,16,Pathfinder.d2r(90)),
			 new Waypoint(276,320,Pathfinder.d2r(90))
	};
	
}
