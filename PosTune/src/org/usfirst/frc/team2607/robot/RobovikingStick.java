/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.usfirst.frc.team2607.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Driver
 */
public class RobovikingStick extends Joystick {  
	 
	private int previousState; 
	private boolean[] buttonStates; 
	 
	 
	public RobovikingStick(int port) { 
	     super(port); 
	     previousState = 0; 
	     buttonStates = new boolean[16]; 
	     for (int i = 0; i < buttonStates.length; i++) buttonStates[i] = false; 
	 } 
	 
	/**
	 * Runs once for every time button is pressed.
	 * @param buttonNumber - button ID number
	 * @return
	 */
	 public boolean getButtonPressedOneShot(int buttonNumber) {
		    int bitValue = 0x1 << (buttonNumber - 1);
		    boolean retValue = false;
		    boolean buttonWasOff = (bitValue & previousState) == 0;
		    boolean buttonIsOn = getRawButton(buttonNumber);
		    
		    if (buttonWasOff && buttonIsOn) retValue = true;
		    if (buttonIsOn) previousState = previousState | bitValue;
		    if (!buttonIsOn) previousState = previousState & ~bitValue;
		    
		    return retValue;
		}

	/**
	 * Runs once for every time button is released.
	 * @param buttonNumber - button ID number
	 * @return
	 */
	public boolean getButtonReleasedOneShot(int buttonNumber) {
		    int bitValue = 0x1 << (buttonNumber - 1);
		    boolean retValue = false;
		    boolean buttonWasOn = (bitValue & previousState) != 0;
		    boolean buttonIsOff = !getRawButton(buttonNumber);
			
		    if (buttonWasOn && buttonIsOff) retValue = true;
		    if (buttonIsOff) previousState = previousState & ~bitValue;
		    if (!buttonIsOff) previousState = previousState | bitValue; 
		    
		    return retValue;
		}
	
	/**
	 * If the button is pressed, its state changes and remains changed until it is pressed
	 * again.
	 * @param buttonNumber -button ID number
	 * @return
	 */
	 public boolean getToggleButton(int buttonNumber) { 
	 	int btn = buttonNumber - 1; 
	 	if (getButtonPressedOneShot(buttonNumber)) buttonStates[btn] = !buttonStates[btn]; 
	 	return buttonStates[btn]; 
	 } 
	 
	 
	 /**
	  * Treats the specified trigger as a button rather than an axis. (The trigger returns
	  * a boolean rather than returning a value from 0 to 1)
	  * @param
	  * triggerNumber - the index of the trigger (right is 1 , left is 2)
	  * @return 
	  * true if the trigger is more than 70% pressed, false otherwise
	  */
	 public boolean getTriggerPressed(int triggerNumber){
		 double threshold = 0.7;
		 boolean retValue = false;
		 int axisNumber = 5 - triggerNumber;
		 
		 if(this.getRawAxis(axisNumber) > threshold) retValue = true;
		 
		 return retValue;
	 }
	 
	 
	 /**
	  * This method applies dead zones to a specified axis. When
	  * the thumb stick is less than 15% pressed, the value returned is 0. Otherwise,
	  * a value will be returned. (Nobody actually understands it anymore, but hey, 
	  * it works...ALL HAIL JOHNBOT!)
	  * @param 
	  * driveVal - Axis Number that dead zones will be applied to
	  * @return
	  * A double that represents the Axis value after dead zones were applied
	  */
	 public double getRawAxisWithDeadzone(int axisNumber){
		 double deadVal = 0.15;
		 
		 double driveVal = getRawAxis(axisNumber);
		 if (Math.abs(driveVal) <= deadVal) {
             driveVal = 0;
         }
         if (driveVal > deadVal && driveVal <= deadVal * 2) {
             driveVal = (driveVal - .15) * 2;
         }
         if (driveVal < -deadVal && driveVal >= -2 * deadVal) {
             driveVal = (driveVal + .15) * 2;
         }
         
         return driveVal;
	 }
	 
} 