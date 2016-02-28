package org.usfirst.frc.team2607.robot;

import java.io.File;
import java.io.PrintWriter;

import edu.wpi.first.wpilibj.DriverStation;

public class PIDLogger extends Thread {
	private PrintWriter logFile = null;
	private PrintWriter sameNameLogFile = null;
	private boolean loggingEnabled = false;
	String deviceName;
	
	Robot theBot;
	
	
	@Override
	public void run(){
		while (true){
			logEntry();
			try {Thread.sleep(10); } catch (Exception e) {}
		}
		
	}
	
	public PIDLogger(Robot robot){
		theBot = robot;					
	}
	
	 public void enableLogging(boolean enable) {
	    	if (enable && !loggingEnabled) {
	    		if (logFile != null) {
	    			logFile.close();
	    			sameNameLogFile.close();
	    			logFile = null;
	    			sameNameLogFile = null;
	    		}
	    		try {
	    			String s = "/home/lvuser/" + "TuneFiles" + "." + System.currentTimeMillis() + ".csv";
	    			logFile = new PrintWriter(new File(s));
	    			String t = "/home/lvuser/" + "TuneFile" + ".csv";
	    			sameNameLogFile = new PrintWriter(new File(t));
	    			sameNameLogFile.println("Time,SetPos,RealPos,SetVel,RealVel,SetHead,RealHead,Error,PCon,ICon,DCon,VCon,ACon");
	    			logFile.println("Time,SetPos,RealPos,SetVel,RealVel,SetHead,RealHead,Error,PCon,ICon,DCon,VCon,ACon");
	    		} catch (Exception e) {}
	    	} 
	    	
	    	if (!enable && loggingEnabled) {
	    		if (logFile != null) {
	    			logFile.close();
	    			sameNameLogFile.close();
	    			logFile = null;
	    			sameNameLogFile = null;
	    		}
	    	}
	    	
	    	loggingEnabled = enable;
	    }
	 
	 public void logEntry() {
	        if (loggingEnabled) {
	        	logFile.println(System.currentTimeMillis() + "," +
	        					theBot.t.pidLoop.getSetpoint()[0] + "," + 
	        					theBot.t.enc.getDistance() + "," +
	        				    theBot.t.pidLoop.getSetpoint()[1] + "," + 
	        					theBot.t.enc.getRate() + "," + 
	        				    theBot.t.pidLoop.getSetpoint()[3] + "," +
	        				    (theBot.t.gyro.getAngle() * (Math.PI/180.0)) + "," +
	        				    theBot.t.pidLoop.getError() + "," +
	        				    (theBot.t.pidLoop.getError() * theBot.t.pidLoop.getP()) + "," +
	        				    (theBot.t.pidLoop.getError() * theBot.t.pidLoop.getI()) + "," +
	        				    (theBot.t.pidLoop.getError() * theBot.t.pidLoop.getD()) + "," +
	        				    (theBot.t.pidLoop.getSetpoint()[1] * theBot.t.pidLoop.getV()) + "," +
	        				    (theBot.t.pidLoop.getSetpoint()[2] * theBot.t.pidLoop.getA()));
	        	logFile.flush();
	        	sameNameLogFile.println(System.currentTimeMillis() + "," +
    					theBot.t.pidLoop.getSetpoint()[0] + "," + 
    					theBot.t.enc.getDistance() + "," +
    				    theBot.t.pidLoop.getSetpoint()[1] + "," + 
    					theBot.t.enc.getRate() + "," + 
    				    theBot.t.pidLoop.getSetpoint()[3] + "," +
    				    (theBot.t.gyro.getAngle() * (Math.PI/180.0)) + "," +
    				    theBot.t.pidLoop.getError() + "," +
    				    (theBot.t.pidLoop.getError() * theBot.t.pidLoop.getP()) + "," +
    				    (theBot.t.pidLoop.getError() * theBot.t.pidLoop.getI()) + "," +
    				    (theBot.t.pidLoop.getError() * theBot.t.pidLoop.getD()) + "," +
    				    (theBot.t.pidLoop.getSetpoint()[1] * theBot.t.pidLoop.getV()) + "," +
    				    (theBot.t.pidLoop.getSetpoint()[2] * theBot.t.pidLoop.getA()));
	        	sameNameLogFile.flush();
	        }
	    }

}
