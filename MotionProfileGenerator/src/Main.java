import java.io.File;
import java.io.FileWriter;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.WaypointSequence.Waypoint;
import com.team254.lib.trajectory.io.TextFileSerializer;

public class Main {

	public static void main(String[] args) {
		System.out.println("Path generator");

//		SRXProfile test = new SRXProfile(-101.45, 0, -75, 200, 200, 10);
//		test.generateAndPushProfile(null);
		
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

        Path path = PathGenerator.makePath(p, config,
            kWheelbaseWidth, "Corn Dogs");

        TextFileSerializer tfs = new TextFileSerializer();
        String traj = tfs.serialize(path);

        try {
        	FileWriter f = new FileWriter(new File("testTrajctory.txt"));
        	f.write(traj);
        	f.flush();
        	f.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

}
