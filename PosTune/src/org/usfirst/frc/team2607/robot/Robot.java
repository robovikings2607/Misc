package org.usfirst.frc.team2607.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.io.TextFileDeserializer;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Talon;

public class Robot extends IterativeRobot {

	public Transmission t;
	private RobovikingDriveTrainProfileDriver mp;
	private PIDLogger log;
	@Override
	public void robotInit() {
		t = new Transmission();
		Path path = null;
		
		// generate path here in the robot program
//        path = generatePathOnTheFly();
//		mp = new RobovikingDriveTrainProfileDriver(t, path);

		// read a path from a text file saved on the roboRIO
		try {
			Scanner trajFile = new Scanner(new FileReader(new File("/home/lvuser/testProfile.txt")));
			trajFile.useDelimiter("\\Z");
			String traj = trajFile.next();
			TextFileDeserializer tfds = new TextFileDeserializer();
			path = tfds.deserialize(traj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mp = new RobovikingDriveTrainProfileDriver(t, path);
		
		log = new PIDLogger(this);
	}

	private Path generatePathOnTheFly() {
    	TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
        config.dt = .01;
        config.max_acc = 8.0;
        config.max_jerk = 60.0;
        config.max_vel = 8.0;
        
        final double kWheelbaseWidth = 25.25/12;

        WaypointSequence p = new WaypointSequence(10);
        p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
        //p.addWaypoint(new WaypointSequence.Waypoint(5, 7.0, 0));
        p.addWaypoint(new WaypointSequence.Waypoint(50.0, 0.0, 0));

        return PathGenerator.makePath(p, config,
            kWheelbaseWidth, "Corn Dogs");

	}
	
	@Override
	public void disabledInit() {
	}

	@Override
	public void teleopInit() {
		mp.followPath();
		log.start();
		log.enableLogging(true);
	}

	@Override
	public void disabledPeriodic() {
		log.enableLogging(false);
	}

	int tick = 0;
	@Override
	public void teleopPeriodic() {
		if (++tick >= 1) {
			tick = 0;
			t.displayValues();
		}
	}
	
	

}
