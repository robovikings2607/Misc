import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner; 

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class PathGenerator {

	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		

		double vel = 144.0;
		double acc = 100.0;
		double jerk = 50.0;
		double width = 28.0;
		double ticksperinch = 50.775;
		
		/*
		 * This code can be used to make this script interactive and run as a jar file (java -jar <name.jar>)
		 * Uncomment this and Export -> Runnable jar file
		 * 
		
		char answer = 'n';
				
		while (true)
		{	
			
			System.out.printf("Enter Max Velocity (inches/sec): ");
			vel = scan.nextDouble();
			System.out.printf("Enter Max Accelaration (inches/sec2): ");
			acc = scan.nextDouble();
			System.out.printf("Enter Max Jerk (inches/sec3): ");
			jerk = scan.nextDouble();
		
			System.out.printf("You Entered Velocity = %f  Acceleration = %f  Jerk = %f\n",vel,acc,jerk);
			System.out.printf("Accept (y/n) ? ");
			answer = scan.next(".").charAt(0);
		
			if ((answer == 'y') || (answer == 'Y')) {
				break;
			}
		}
		
		*/
			
		scan.close();
		
		Waypoint[] points = new Waypoint[] {
			    /*  This is the routine for the left switch drop */
				 new Waypoint(164,17.75,Pathfinder.d2r(90)),
			     new Waypoint(104,122.25,Pathfinder.d2r(90))
			    
				
				// These are the coordinates for the right switch drop
				
				//new Waypoint(135.5,17.75,Pathfinder.d2r(90)),
				//new Waypoint(158.5,122.25,Pathfinder.d2r(90))
				
				// These are the left start point waypoints
				
				// new Waypoint(40,17.75,Pathfinder.d2r(90)),
				// new Waypoint(40,242.0,Pathfinder.d2r(90))
				
				// Test Points
				
				//new Waypoint(40.0,17.75,Pathfinder.d2r(90)),
				//new Waypoint(40.0,47.75,Pathfinder.d2r(90))
				
			};
		
		/*
		 * a
		 */

// config parameters: time interval, velocity, acceleration, and jerk as the last four parameters		
			Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.010, vel,acc, jerk);
			Trajectory trajectory = Pathfinder.generate(points, config);
			TankModifier modifier = new TankModifier(trajectory).modify(width);
			
			Trajectory left  = modifier.getLeftTrajectory();       // Get the Left Side
			Trajectory right = modifier.getRightTrajectory();      // Get the Right Side
			
//			System.out.println("Left Side " + left.length());
//			System.out.println("Right Side " + right.length());
			
			Trajectory.Segment leftseg = left.get(0);
			Trajectory.Segment rightseg = right.get(0);
			
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
			
			PrintWriter trajectory_inch;
			
			try {
				
				trajectory_inch = new PrintWriter("C:\\Temp\\trajectory.csv");

				for (int i = 0; i < trajectory.length(); i++) {
					
				    Trajectory.Segment seg = trajectory.get(i);
				    
				    trajectory_inch.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
				        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
				            seg.acceleration, seg.jerk, seg.heading);
				    
//				    outfileleft_ticks.printf("{%f,%f,10},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/10);
				    
				}
				
				trajectory_inch.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PrintWriter outfileleft_inch, outfileleft_ticks, outfile_class;
			PrintWriter outfileright_inch, outfileright_ticks;
			
			try {
				
				outfileleft_inch = new PrintWriter("C:\\Temp\\left_trajectory_inch.csv");
				outfileleft_ticks = new PrintWriter("C:\\Temp\\left_trajectory_tick.csv");
				outfile_class = new PrintWriter("C:\\Temp\\MotionProfile_Class.csv");
				
				outfile_class.printf("package org.usfirst.frc.team2607.robot;\n\n");
				outfile_class.printf("public class MotionProfile {\n\n");
				outfile_class.printf("\t public static final int kNumPoints = %d;\n",left.length());
				outfile_class.printf("\t public static double [][] PointsLeft = new double [][] {\n\n");

				
				for (int i = 0; i < left.length(); i++) {
					
				    Trajectory.Segment seg = left.get(i);
				    
				    outfileleft_inch.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
				        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
				            seg.acceleration, seg.jerk, seg.heading);
				    
				    outfileleft_ticks.printf("{%f,%f,10},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/10);
				    outfile_class.printf("{%f,%f,10},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/10);
				    
				}
				
				outfileleft_inch.close();
				outfileleft_ticks.close();
				
				
				outfile_class.printf("\n }%C",59);
				outfile_class.printf("\n\n\t public static double [][] PointsRight = new double [][] {\n\n");
			
				outfileright_inch = new PrintWriter("C:\\Temp\\right_trajectory_inch.csv");
				outfileright_ticks = new PrintWriter("C:\\Temp\\right_trajectory_ticks.csv");
				
				for (int i = 0; i < right.length(); i++) {
					
				    Trajectory.Segment seg = right.get(i);
				    
				    outfileright_inch.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
				        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
				            seg.acceleration, seg.jerk, seg.heading);
				    
				    outfileright_ticks.printf("{%f,%f,10},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/10);
				    outfile_class.printf("{%f,%f,10},\n",seg.position*ticksperinch,seg.velocity*ticksperinch/10);
				    
				}
				
				outfile_class.printf("\n };");
				outfile_class.printf("\n }");
				
				outfileright_inch.close();
				outfileright_ticks.close();
				outfile_class.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Done !!!");

	}

}
