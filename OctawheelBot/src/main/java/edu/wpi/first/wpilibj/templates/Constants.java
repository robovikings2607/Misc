/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 *
 * @author Driver
 */
public class Constants {
    
    //Variables to represent electrical channel and port numbers
    private static final int leftStickOfSupremacy        = 1   ,
                            rightStickOfJustice         = 2   ,
            
                            frontLeftMotorChannel       = 1   ,
                            rearLeftMotorChannel        = 2   ,
                            frontRightMotorChannel      = 3   ,
                            rearRightMotorChannel       = 4   ;

    
    //Displays a message on the Driver Station to indicate that the robot is in tank drive mode
    public void displayTankMode(){ 
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2 , 1 , "Tank Drive!");
    }
    
    //Displays a message on the Driver Station to indicates that the robot is in arcade drive (Xbox controller) mode
    public void displayXboxMode(){
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2 , 1 , "Xbox Controller!");
    }
    
    //TODO put these methods or some equivalent in an extention of the robovikingStick class
    //Get methods for Xbox button numbers 
    public int getButtonA(){
        return 1;
    }
    
    public int getButtonB(){
        return 2;
    }
    
    public int getButtonX(){
        return 3;
    }
    
    public int getButtonY(){
        return 4;
    }
    
    public int getButtonLB(){
        return 5;
    }
    
    public int getButtonRB(){
        return 6;
    }
    
    public int getButtonBack(){
        return 7;
    }
    
    public int getButtonStart(){
        return 8;
    }
    
    public int getButtonLeftStickClick(){
        return 9;
    }
    
    public int getButtonRightStickClick(){
        return 10;
    }
    
    public int getLeftStickPort(){
        return leftStickOfSupremacy;
    }
    
    public int getRightStickPort(){
        return rightStickOfJustice;
    }
    
    public int getFLMotorChannel(){
        return frontLeftMotorChannel;
    }
    
    public int getRLMotorChannel(){
        return rearLeftMotorChannel;
    }
    
    public int getFRMotorChannel(){
        return frontRightMotorChannel;
    }
    
    public int getRRMotorChannel(){
        return rearRightMotorChannel;
    }
}
