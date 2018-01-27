#include "Joystick.h"
#include <stdio.h>

void ButtonCallback() {
	printf("ButtonCallback!\n");
}

int main(int argc, char **argv) {
	printf("Entering main\n");

//	Joystick driveStick(L"logitech dual action");
//	Joystick gameStick(L"logitech attack 3");
	Joystick myStick;

	bool keepRunning = true, driveButtonPressed = false, gameButtonPressed = false;
/*	
	while (keepRunning) {
		if (driveStick.GetButton(1)) {
			printf("Logitech Dual Action button pressed\n");
			driveButtonPressed = true;
		}
		if (gameStick.GetButton(1)) {
			printf("Logitech Attack 3 button pressed\n");
			gameButtonPressed = true;
		}
		keepRunning = !(driveButtonPressed && gameButtonPressed);
		Sleep(100);
	}
*/
/*
	for (int i=0; i < 2000; i++) {
		printf("loop %d: X = %d, Y = %d, Z = %d, Zr = %d\n", i, myJoystick.GetXAxis(), myJoystick.GetYAxis(), 
															myJoystick.GetZAxis(), myJoystick.GetZAxisRotation());
		Sleep(10);
	}
*/
	printf("Exiting main\n");
}

