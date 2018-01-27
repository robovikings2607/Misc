import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class PathGenerator {

	public static void main(String[] args) {
		
		Waypoint[] points = new Waypoint[] {
			    /*  This is the routine for the left switch drop
				new Waypoint(135.5,17.75,Pathfinder.d2r(90)),
			    new Waypoint(105.5,122.25,Pathfinder.d2r(90))
			    */
				
				// These are the coordinates for the right switch drop
				
				//new Waypoint(135.5,17.75,Pathfinder.d2r(90)),
				//new Waypoint(158.5,122.25,Pathfinder.d2r(90))
				
				// These are the left start point waypoints
				
				new Waypoint(40,17.75,Pathfinder.d2r(90)),
				new Waypoint(40,242.0,Pathfinder.d2r(90))
			};

			Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.010, 20, 8.0, 20.0);
			Trajectory trajectory = Pathfinder.generate(points, config);
			TankModifier modifier = new TankModifier(trajectory).modify(14);
			
			Trajectory left  = modifier.getLeftTrajectory();       // Get the Left Side
			Trajectory right = modifier.getRightTrajectory();      // Get the Right Side
			
//			System.out.println("Left Side " + left.length());
//			System.out.println("Right Side " + right.length());
			
			Trajectory.Segment leftseg = left.get(0);
			Trajectory.Segment rightseg = right.get(0);
			double ticksperinch = 30; // this is equal to (2 * pi * wheel radius in inches) / 1024
			
			System.out.printf("Left Length %d; Right Length %d; Robot Width Track %f\n",left.length(),right.length(),rightseg.x - leftseg.x);
					
			/*
			for (int i = 0; i < left.length(); i++) {
			    Trajectory.Segment seg = left.get(i);
			    
			    System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
			        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
			            seg.acceleration, seg.jerk, seg.heading);
			}
			

		
			System.out.println("Right Side" + right.length());
			
			for (int i = 0; i < right.length(); i++) {
			    Trajectory.Segment seg = right.get(i);
			    
			    System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
			        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
			            seg.acceleration, seg.jerk, seg.heading);
			}
			*/
			
			File myFile = new File("trajectory.csv");
			Pathfinder.writeToCSV(myFile, trajectory);		
							
			PrintWriter outfileleft_inch, outfileleft_ticks;
			try {
				
				outfileleft_inch = new PrintWriter("left_trajectory_inch.csv");
				outfileleft_ticks = new PrintWriter("left_trajectory_tick.csv");
				
				for (int i = 0; i < left.length(); i++) {
					
				    Trajectory.Segment seg = left.get(i);
				    
				    outfileleft_inch.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
				        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
				            seg.acceleration, seg.jerk, seg.heading);
				    
				    outfileleft_ticks.printf("{%f,%f,10.0},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/100);
				    
				}
				
				outfileleft_inch.close();
				outfileleft_ticks.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			PrintWriter outfileright_inch, outfileright_ticks;
			try {
				outfileright_inch = new PrintWriter("right_trajectory_inch.csv");
				outfileright_ticks = new PrintWriter("right_trajectory_ticks.csv");
				
				for (int i = 0; i < right.length(); i++) {
					
				    Trajectory.Segment seg = right.get(i);
				    
				    outfileright_inch.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
				        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
				            seg.acceleration, seg.jerk, seg.heading);
				    
				    outfileright_ticks.printf("{%f,%f,10},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/100);
				    
				}
				
				outfileright_inch.close();
				outfileright_ticks.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			
			System.out.println("Done !!!");

	}

}
