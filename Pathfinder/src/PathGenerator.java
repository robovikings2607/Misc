import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner; 

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class PathGenerator {
	
	static double width = Constants.robotTrackWidth;
	static double ticksperinch = Constants.ticksPerInch;
	static double trajPointDuration = Constants.trajectoryPointDuration;
	
	static String name;
	static double vel;
	static double acc;
	static double jerk;
	static Waypoint [] points;

	public static void main(String[] args) {
				
		int i,answer;
		
		answer = selectTrajectory();
		populateParameters(answer);
		

		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 
				trajPointDuration, vel,acc, jerk);
		Trajectory trajectory = Pathfinder.generate(points, config);
		TankModifier modifier = new TankModifier(trajectory).modify(width);
			
		Trajectory left  = modifier.getLeftTrajectory();       // Get the Left Side
		Trajectory right = modifier.getRightTrajectory();      // Get the Right Side
			
		Trajectory.Segment leftseg = left.get(0);
		Trajectory.Segment rightseg = right.get(0);
			
		System.out.printf("Left Length %d; Right Length %d; Robot Width Track %f\n",left.length(),right.length(),rightseg.x - leftseg.x);

		PrintWriter trajectory_inch;
			
		try {
				
			trajectory_inch = new PrintWriter("C:\\Temp\\traj_" + name + ".csv");

			for (i = 0; i < trajectory.length(); i++)
			{
					
			    Trajectory.Segment seg = trajectory.get(i);
				    
			    trajectory_inch.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
			        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
			            seg.acceleration, seg.jerk, seg.heading);
			}
				
				trajectory_inch.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			PrintWriter outfileleft_inch, outfileleft_ticks, outfile_class;
			PrintWriter outfileright_inch, outfileright_ticks;
			
			try {
				
				outfileleft_inch = new PrintWriter("C:\\Temp\\lw_traj_" + name + "_inch.csv");
				outfileleft_ticks = new PrintWriter("C:\\Temp\\lw_traj_" + name + "_ticks.csv");
				outfile_class = new PrintWriter("C:\\Temp\\MotionProfile_Class.csv");
				
				outfile_class.printf("package org.usfirst.frc.team2607.robot;\n\n");
				outfile_class.printf("public class MotionProfile {\n\n");
				outfile_class.printf("\t public static final int kNumPoints = %d;\n",left.length());
				outfile_class.printf("\t public static double [][] PointsLeft = new double [][] {\n\n");

				
				for (i = 0; i < left.length(); i++) {
					
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
			
				outfileright_inch = new PrintWriter("C:\\Temp\\rw_traj_" + name + "_inch.csv");
				outfileright_ticks = new PrintWriter("C:\\Temp\\rw_traj_" + name + "_ticks.csv");
				
				for (i = 0; i < right.length(); i++) {
					
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
				e.printStackTrace();
			}
			
			System.out.println("Done !!!");

	}
	
	static int selectTrajectory() {
		
		int answer = 0;
		boolean flag=true;
		Scanner scan = new Scanner(System.in);
		
		while (flag)
		{
			System.out.println("Select One Trajectory:");
			System.out.println("1): " + Constants.nameLeftSwitch);
			System.out.println("2): " + Constants.nameRightSwitch);
			System.out.println("3): " + Constants.nameLeftCrossLine);
			System.out.println("4): " + Constants.nameRightCrossLine);
			System.out.println("5): " + Constants.nameLeftScale);
			System.out.println("6): " + Constants.nameRightScale);
			System.out.println("7): " + Constants.nameTest);
			System.out.printf("Enter your Choice: ");
			answer = scan.nextInt();
			
			if((answer > 0 ) && (answer <= Constants.numberOfTrajectories))
			{
				flag = false;
			}
		}
		
		scan.close();
		
		return answer;
	}
	
	static void populateParameters(int answer) {
		
		switch (answer) {
        case 1: 
        	name = Constants.nameLeftSwitch;
        	vel = Constants.velLeftSwitch;
        	acc = Constants.accLeftSwitch;
        	jerk = Constants.jerkLeftSwitch;
        	points = Constants.pointsLeftSwitch;
        	break;
        case 2: 
        	name = Constants.nameRightSwitch;
        	vel = Constants.velRightSwitch;
        	acc = Constants.accRightSwitch;
        	jerk = Constants.jerkRightSwitch;
        	points = Constants.pointsRightSwitch;
            break;
        case 3:  
        	name = Constants.nameLeftCrossLine;
        	vel = Constants.velLeftCrossLine;
        	acc = Constants.accLeftCrossLine;
        	jerk = Constants.jerkLeftCrossLine;
        	points = Constants.pointsLeftCrossLine;
            break;        	
        case 4:
        	name = Constants.nameRightCrossLine;
        	vel = Constants.velRightCrossLine;
        	acc = Constants.accRightCrossLine;
        	jerk = Constants.jerkRightCrossLine;
        	points = Constants.pointsRightCrossLine;
        	break;
        case 5:
        	name = Constants.nameLeftScale;
        	vel = Constants.velLeftScale;
        	acc = Constants.accLeftScale;
        	jerk = Constants.jerkLeftScale;
        	points = Constants.pointsLeftScale;
        	break;
        case 6:
        	name = Constants.nameRightScale;
        	vel = Constants.velRightScale;
        	acc = Constants.accRightScale;
        	jerk = Constants.jerkRightScale;
        	points = Constants.pointsRightScale;
        	break;
        case 7:
        	name = Constants.nameTest;
        	vel = Constants.velTest;
        	acc = Constants.accTest;
        	jerk = Constants.jerkTest;
        	points = Constants.pointsTest;
        	break;
        default:
        	break;
		}
	}

}
