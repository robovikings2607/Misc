import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.WaypointSequence.Waypoint;

public class Main {

	public static void main(String[] args) {
		System.out.println("Path generator");

		SRXProfile test = new SRXProfile(-101.45, 0, -75, 200, 200, 10);
		test.generateAndPushProfile(null);
		
/*		
		TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();

		config.dt = .01;			// i.e. 10ms
		config.max_vel = 101.45;	// motor rotations / sec
		config.max_acc = 500.0; 	// ?  (difference in velocity)
		config.max_jerk = 1500.0;		// ?  (difference in acceleration)

		WaypointSequence p = new WaypointSequence(2);
		p.addWaypoint(new Waypoint(0,0,0));
		p.addWaypoint(new Waypoint(100,0,0));
		
		Path path = PathGenerator.makePath(p, config, 0, "ArmTest");
		
		Trajectory t = path.getLeftWheelTrajectory();
		
		System.out.println("Num segments: " + t.getNumSegments());
		for (int i = 0; i < t.getNumSegments(); i++) {
			Trajectory.Segment seg = t.getSegment(i);
			System.out.println("Pos: " + seg.pos + " Vel: " + seg.vel + " Duration: " + seg.dt);
		}
*/	
	
	}

}
