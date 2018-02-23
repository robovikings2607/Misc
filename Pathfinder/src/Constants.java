/*
 * This class holds the trajectories definitions
 */


import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;

public class Constants {
	
	/*
	 * General Constants
	 */
	public static final double robotTrackWidth = 34.0;			// The width from center of center wheels
	public static final double ticksPerInch = 52.47;			// The number of encoder ticks to travel one inch
	public static final double trajectoryPointDuration = 0.010; // The duration of each trajectory point interval
	
	public static final int numberOfTrajectories = 17;

	/*
	 * Configuration for the Left Switch mission
	 */
	public static final String nameLeftSwitch = "LeftSwitch";
	public static final double velLeftSwitch = 120;
	public static final double accLeftSwitch = 100;
	public static final double jerkLeftSwitch = 100;
	public static final Waypoint[] pointsLeftSwitch = new Waypoint[] {
		     new Waypoint(167, 19.5,Pathfinder.d2r(90)), //171.375 , 
			 new Waypoint(104, 120,Pathfinder.d2r(90)) //107.25 , 121.25
	};
	
	/*
	 * Configuration for the Right Switch mission
	 */
	public static final String nameRightSwitch = "RightSwitch";
	public static final double velRightSwitch = 120;
	public static final double accRightSwitch = 100;
	public static final double jerkRightSwitch = 100;
	public static final Waypoint[] pointsRightSwitch = new Waypoint[] {
		     new Waypoint(167,19.5,Pathfinder.d2r(90)),
			 new Waypoint(224,120,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Left Start Cross the Line mission
	 */
	public static final String nameLeftCrossLine = "LeftCrossLine";
	public static final double velLeftCrossLine = 120;
	public static final double accLeftCrossLine = 50;
	public static final double jerkLeftCrossLine = 50;
	public static final Waypoint[] pointsLeftCrossLine = new Waypoint[] {
		     new Waypoint(46.7,19.5,Pathfinder.d2r(90)),
			 new Waypoint(46.7,165.75,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Right Start Cross the Line mission
	 */
	public static final String nameRightCrossLine = "RightCrossLine";
	public static final double velRightCrossLine = 60;
	public static final double accRightCrossLine = 50;
	public static final double jerkRightCrossLine = 50;
	public static final Waypoint[] pointsRightCrossLine = new Waypoint[] {
		     new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			 new Waypoint(277.3,165.75,Pathfinder.d2r(90))
	};
	
	/*
	 * Configuration for the Left Scale mission
	 */
	public static final String nameLeftScaleSide = "LeftScaleSide";
	public static final double velLeftScaleSide = 60;
	public static final double accLeftScaleSide = 50;
	public static final double jerkLeftScaleSide = 50;
	public static final Waypoint[] pointsLeftScaleSide = new Waypoint[] {
		     new Waypoint(43.7,19.5,Pathfinder.d2r(90)),
			 new Waypoint(43.7,180,Pathfinder.d2r(90)),
			 new Waypoint(65.7,280,Pathfinder.d2r(90))
	};
	
	public static final String nameLeftScaleFront = "LeftScaleFront";
	public static final double velLeftScaleFront = 60;
	public static final double accLeftScaleFront = 50;
	public static final double jerkLeftScaleFront = 50;
	public static final Waypoint[] pointsLeftScaleFront = new Waypoint[] {
		     new Waypoint(43.7,19.5,Pathfinder.d2r(90)),
			 new Waypoint(43.7,180,Pathfinder.d2r(90)),
			 new Waypoint(23.7,299.5,Pathfinder.d2r(90)),
			 new Waypoint(43.7,319.5,Pathfinder.d2r(0))
	};
	
	/*
	 * Configuration for the Right Scale mission
	 */
	public static final String nameRightScaleSide = "RightScaleSide";
	public static final double velRightScaleSide = 60;
	public static final double accRightScaleSide = 50;
	public static final double jerkRightScaleSide = 50;
	public static final Waypoint[] pointsRightScaleSide = new Waypoint[] {
		     new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			 new Waypoint(277.3,180,Pathfinder.d2r(90)),
			 new Waypoint(255.3,280,Pathfinder.d2r(90))
	};
	
	
	public static final String nameRightScaleFront = "RightScaleFront";
	public static final double velRightScaleFront = 60;
	public static final double accRightScaleFront = 50;
	public static final double jerkRightScaleFront = 50;
	public static final Waypoint[] pointsRightScaleFront = new Waypoint[] {
		     new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			 new Waypoint(277.3,180,Pathfinder.d2r(90)),
			 new Waypoint(297.3,299.5,Pathfinder.d2r(90)),
			 new Waypoint(277.3,319.5,Pathfinder.d2r(180))
	};
	
	public static final String nameRightScaleFailLeftFront = "RightScaleFailLeftFront";
	public static final double velRightScaleFailLeftFront = 60;
	public static final double accRightScaleFailLeftFront = 50;
	public static final double jerkRightScaleFailLeftFront = 50;
	public static final Waypoint[] pointsRightScaleFailLeftFront = new Waypoint[] {
			new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			new Waypoint(277.3,200,Pathfinder.d2r(90)),
			new Waypoint(250,220,Pathfinder.d2r(180)),
			new Waypoint(73.7,220,Pathfinder.d2r(180)),
			new Waypoint(23.7,240,Pathfinder.d2r(90)),
			new Waypoint(23.7,299.5,Pathfinder.d2r(90)),
			new Waypoint(43.7,319.5,Pathfinder.d2r(0))
	};
	
	public static final String nameRightScaleFailLeftSide = "RightScaleFailLeftSide";
	public static final double velRightScaleFailLeftSide = 120;
	public static final double accRightScaleFailLeftSide = 100;
	public static final double jerkRightScaleFailLeftSide = 100;
	public static final Waypoint[] pointsRightScaleFailLeftSide = new Waypoint[] {
			new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			new Waypoint(277.3,200,Pathfinder.d2r(90)),
			new Waypoint(250,220,Pathfinder.d2r(180)),
			new Waypoint(98.7,220,Pathfinder.d2r(180)),
			new Waypoint(78.7,240,Pathfinder.d2r(90)),
			new Waypoint(78.7,280,Pathfinder.d2r(90))
	};
	
	public static final String nameLeftScaleFailRightFront = "LeftScaleFailRightFront";
	public static final double velLeftScaleFailRightFront = 60;
	public static final double accLeftScaleFailRightFront = 50;
	public static final double jerkLeftScaleFailRightFront = 50;
	public static final Waypoint[] pointsLeftScaleFailRightFront = new Waypoint[] {
			 new Waypoint(43.7,19.5,Pathfinder.d2r(90)),
			 new Waypoint(43.7,200,Pathfinder.d2r(90)),
			 new Waypoint(71,220,Pathfinder.d2r(0)),
			 new Waypoint(277.3,220,Pathfinder.d2r(0)),
			 new Waypoint(297.3,240,Pathfinder.d2r(90)),
			 new Waypoint(297.3,299.5,Pathfinder.d2r(90)),
			 new Waypoint(277.3,319.5,Pathfinder.d2r(180))
	};
	
	public static final String nameLeftScaleFailRightSide = "LeftScaleFailRightSide";
	public static final double velLeftScaleFailRightSide = 60;
	public static final double accLeftScaleFailRightSide = 50;
	public static final double jerkLeftScaleFailRightSide = 50;
	public static final Waypoint[] pointsLeftScaleFailRightSide = new Waypoint[] {
			new Waypoint(43.7,19.5,Pathfinder.d2r(90)),
			new Waypoint(43.7,200,Pathfinder.d2r(90)),
			new Waypoint(71,220,Pathfinder.d2r(0)),
			new Waypoint(235.3,220,Pathfinder.d2r(0)),
			new Waypoint(255.3,240,Pathfinder.d2r(90)),
			new Waypoint(255.3,280,Pathfinder.d2r(90))
	};
	/*
	 * Configuration for Right Switch if the Scale is not on the right
	 */
	public static final String nameRightRightSwitch = "RightRightSwitch";
	public static final double velRightRightSwitch = 120;
	public static final double accRightRightSwitch = 100;
	public static final double jerkRightRightSwitch = 100;
	public static final Waypoint[] pointsRightRightSwitch = new Waypoint[] {
			new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			new Waypoint(277.3,140,Pathfinder.d2r(90)),
			new Waypoint(251.25,160,Pathfinder.d2r(180)),
			
	};
	
	public static final String nameRightLeftSwitch = "RightLeftSwitch";
	public static final double velRightLeftSwitch = 60;
	public static final double accRightLeftSwitch = 50;
	public static final double jerkRightLeftSwitch = 50;
	public static final Waypoint[] pointsRightLeftSwitch = new Waypoint[] {
			new Waypoint(277.3,19.5,Pathfinder.d2r(90)),
			new Waypoint(277.3,200,Pathfinder.d2r(90)),
			new Waypoint(251.25,220,Pathfinder.d2r(180)),
			new Waypoint(69.7,220,Pathfinder.d2r(180)),
			new Waypoint(43.7,200,Pathfinder.d2r(270)),
			new Waypoint(69.7,180,Pathfinder.d2r(0))		
	};
	
	public static final String nameLeftRightSwitch = "LeftRightSwitch";
	public static final double velLeftRightSwitch = 60;
	public static final double accLeftRightSwitch = 50;
	public static final double jerkLeftRightSwitch = 50;
	public static final Waypoint[] pointsLeftRightSwitch = new Waypoint[] {
			new Waypoint(43.7,19.5,Pathfinder.d2r(90)),
			new Waypoint(43.7,200,Pathfinder.d2r(90)),
			new Waypoint(69.7,220,Pathfinder.d2r(0)),
			new Waypoint(251.25,220,Pathfinder.d2r(0)),
			new Waypoint(277.3,200,Pathfinder.d2r(270)),
			new Waypoint(251.25,180,Pathfinder.d2r(180))
	};
	
	public static final String nameLeftLeftSwitch = "LeftLeftSwitch";
	public static final double velLeftLeftSwitch = 120;
	public static final double accLeftLeftSwitch = 100;
	public static final double jerkLeftLeftSwitch = 100;
	public static final Waypoint[] pointsLeftLeftSwitch = new Waypoint[] {
			new Waypoint(43.7,19.5,Pathfinder.d2r(90)),
			new Waypoint(43.7,140,Pathfinder.d2r(90)),
			new Waypoint(69.7,160,Pathfinder.d2r(0))
	};
	
	/*
	 * Configuration for a Test mission
	 */
	public static final String nameTest = "Test";
	public static final double velTest = 60;
	public static final double accTest = 50;
	public static final double jerkTest = 50;
	public static final Waypoint[] pointsTest = new Waypoint[] {
		     new Waypoint(279.375,16.75,Pathfinder.d2r(90)),
			 new Waypoint(293.375,30.75,Pathfinder.d2r(180))
	};
	
}
