#include "2CAN.h"
#include "Joystick.h"
#define FIRST_FIRMWARE_VERSION
#include "can_proto.h"
#include <stdio.h>

//#define kFullMessageIDMask (CAN_MSGID_API_M | CAN_MSGID_MFR_M | CAN_MSGID_DTYPE_M)
UINT32 GetFirmwareVersion(UINT8 deviceNumber);
void getTransaction(UINT32 messageID, UINT8 *data, UINT8 *dataSize, UINT8 deviceNumber);
void setTransaction(UINT32 messageID, const UINT8 *data, UINT8 dataSize, UINT8 deviceNumber);
INT32 receiveMessage(UINT32 messageID, UINT8 *data, UINT8 *dataSize); //, float timeout);
INT32 sendMessage(UINT32 messageID, const UINT8 *data, UINT8 dataSize);

bool robotEnabled = false, keepRunning = true, useArcade = false, usePID = false, configPID = false;
/* for DAWGMA testing with Bob Bellini, Paul Gehman, etc....
rear left CANID 2 
rear right CANID 4 inverted
front left CANID 3
front right CANID 5 inverted
*/

#define FRONT_LEFT_CAN_ID	9		//5			// 12
#define FRONT_RIGHT_CAN_ID	14		//11		// 10
#define REAR_LEFT_CAN_ID	8		//10		// 11
#define REAR_RIGHT_CAN_ID	7		//12		// 5
//#define SHOULDER_LEFT_CAN_ID 8
//#define SHOULDER_RIGHT_CAN_ID 20
//#define ELBOW_LEFT_CAN_ID 14
//#define ELBOW_RIGHT_CAN_ID 6

static unsigned __stdcall kbInputHandler(void *p) {
	while (keepRunning) {
		printf("%s drive, \"d\" to switch to %s,\n%s mode, \"p\" to switch to %s\n", 
					useArcade ? "arcade" : "mecanum", useArcade ? "mecanum" : "arcade",
					usePID ? "PID" : "VOLT", usePID ? "VOLT": "PID");
		printf("\"c\" to config PID, \"e\" to %s, \"x\" to exit\n", robotEnabled ? "disable" : "enable");
		switch (tolower(getchar())) {
			case 'e' : robotEnabled = !robotEnabled;
					   break;
			case 'd' : useArcade = !useArcade;
					   break;
			case 'p' : usePID = !usePID;
					   break;
			case 'c' : configPID = !configPID;
					   break;
			case 'x' : keepRunning = false;
				       break;
			default :  break;
		}
	}
	return 1;
}

C2CAN my2CAN;

/*
void setElbowMotorSpeed(double motorSpeed) {
	UINT8 buffer[8];
	buffer[2] = 0x03;  // set sync group
	*((INT16 *)buffer) = (INT16)(motorSpeed * 32767.0);
	setTransaction(LM_API_VOLT_SET, buffer, 3, ELBOW_LEFT_CAN_ID);
	setTransaction(LM_API_VOLT_SET, buffer, 3, ELBOW_RIGHT_CAN_ID);

	// tell Jags to update
	buffer[0] = 0x03;
	sendMessage(CAN_MSGID_API_SYNC, buffer, 1);

}

void setShoulderMotorSpeeds(double motorSpeed) {
	// motorSpeed > 0 means lower, motorSpeed < 0 means lift
	UINT8 buffer[8];
	buffer[2] = 0x02;  // set sync group
	*((INT16 *)buffer) = (INT16)(motorSpeed * 32767.0);
	setTransaction(LM_API_VOLT_SET, buffer, 3, SHOULDER_LEFT_CAN_ID);
	*((INT16 *)buffer) = (INT16)(motorSpeed * 32767.0);
	setTransaction(LM_API_VOLT_SET, buffer, 3, SHOULDER_RIGHT_CAN_ID);


}
*/

void makeMotorSpeedsRPM(double *frontLeft, double *frontRight, double *rearLeft, double *rearRight) {
	// convert motor speed commands from % throttle to rpm
	double minRPM = 30, maxRPM = 800;
	double rangeRPM = (maxRPM - minRPM);

	double absFrontLeft = abs(*frontLeft), absFrontRight = abs(*frontRight), absRearLeft = abs(*rearLeft), 
			absRearRight = abs(*rearRight);
	int signFrontLeft = (*frontLeft < 0) ? -1 : ((*frontLeft > 0) ? 1 : 0), 
		signFrontRight = (*frontRight < 0) ? -1 : ((*frontRight > 0) ? 1 : 0),
		signRearLeft = (*rearLeft < 0 ) ? -1 : ((*rearLeft > 0) ? 1 : 0), 
		signRearRight = (*rearRight < 0) ? -1 : ((*rearRight > 0) ? 1 : 0);

	
	*frontLeft = signFrontLeft * (minRPM + (absFrontLeft * rangeRPM));
	*frontRight = signFrontRight * (minRPM + (absFrontRight * rangeRPM));
	*rearLeft = signRearLeft * (minRPM + (absRearLeft * rangeRPM));
	*rearRight = signRearRight * (minRPM + (absRearRight * rangeRPM));
}

void setDriveMotorSpeeds(double frontLeft, double frontRight, double rearLeft, double rearRight) {
	// "forward" voltage from the Jag makes the motor shaft turn counter-clockwise 
	// (as seen when looking at the shaft head-on).
	//		this means that right-handed motors need reverse speed (-1 > s < 0) to move the robot forwards
	//      left-handed motors need forward speed (0 > s < 1) to move the robot forwads
	// since the Joystick returns negative when pushed forward, we flip the sign for the left-handed
	// motors. (i.e. Joystick drives the right-handed motor correctly, and left-handed is "reversed" per
	// the Joystick's perspective)
	UINT8 buffer[8];
	if (usePID) {
		buffer[5] = 0x01;
		makeMotorSpeedsRPM(&frontLeft, &frontRight, &rearLeft, &rearRight);
		*((INT32*)buffer) = -(INT32)(frontLeft * 65536.0);
		setTransaction(LM_API_SPD_SET, buffer, 5, FRONT_LEFT_CAN_ID);			
		*((INT32*)buffer) = (INT32)(frontRight * 65536.0);
		setTransaction(LM_API_SPD_SET, buffer, 5, FRONT_RIGHT_CAN_ID);			
		*((INT32*)buffer) = -(INT32)(rearLeft * 65536.0);
		setTransaction(LM_API_SPD_SET, buffer, 5, REAR_LEFT_CAN_ID);			
		*((INT32*)buffer) = (INT32)(rearRight * 65536.0);
		setTransaction(LM_API_SPD_SET, buffer, 5, REAR_RIGHT_CAN_ID);			
	} else {
		buffer[2] = 0x01;  // set sync group
		*((INT16 *)buffer) = -(INT16)(frontLeft * 32767.0);
		setTransaction(LM_API_VOLT_SET, buffer, 3, FRONT_LEFT_CAN_ID);
		*((INT16 *)buffer) = (INT16)(frontRight * 32767.0);
		setTransaction(LM_API_VOLT_SET, buffer, 3, FRONT_RIGHT_CAN_ID);
		*((INT16 *)buffer) = -(INT16)(rearLeft * 32767.0);
		setTransaction(LM_API_VOLT_SET, buffer, 3, REAR_LEFT_CAN_ID);
		*((INT16 *)buffer) = (INT16)(rearRight * 32767.0);
		setTransaction(LM_API_VOLT_SET, buffer, 3, REAR_RIGHT_CAN_ID);
	}
//	printf("leftMotorSpeed %lf (0x%04x), rightMotorSpeed %lf (0x%04x)\n", leftMotorSpeed, *((UINT16 *)leftBuffer),
//				rightMotorSpeed, *((UINT16 *)rightBuffer));

	// tell Jags to update
	buffer[0] = 0x01;
	sendMessage(CAN_MSGID_API_SYNC, buffer, 1);
}

void setDriveMotorSpeeds(double left, double right) {
	setDriveMotorSpeeds(left, right, left, right);
}

void normalizeMotorSpeeds(double *frontLeftMotorSpeed, double *frontRightMotorSpeed, double *rearLeftMotorSpeed, double *rearRightMotorSpeed) {
	double max = abs(*frontLeftMotorSpeed);
	double temp = abs(*frontRightMotorSpeed);
	if (temp > max) max = temp;
	temp = abs(*rearLeftMotorSpeed);
	if (temp > max) max = temp;
	temp = abs(*rearRightMotorSpeed);
	if (temp > max) max = temp;

	if (max > 1) {
		*frontLeftMotorSpeed /= max;
		*frontRightMotorSpeed /= max;
		*rearLeftMotorSpeed /= max;
		*rearRightMotorSpeed /= max;
	}

}

void mecanumDrivePolar(double magnitude, double direction, double rotation) {
        double frontLeftMotorSpeed, rearLeftMotorSpeed, frontRightMotorSpeed, rearRightMotorSpeed;
 
		// Normalized for full power along the Cartesian axes.
		if (magnitude > 1.0) magnitude = 1.0;
		if (magnitude < -1.0) magnitude = -1.0;
		magnitude *= sqrt(2.0);
        // The rollers are at 45 degree angles.
        double dirInRad = (direction + 45.0) * 3.14159 / 180.0;
        double cosD = cos(dirInRad);
        double sinD = sin(dirInRad);

        frontLeftMotorSpeed = (sinD * magnitude + rotation);
        frontRightMotorSpeed = (cosD * magnitude - rotation);
        rearLeftMotorSpeed = (cosD * magnitude + rotation);
        rearRightMotorSpeed = (sinD * magnitude - rotation);
		
		normalizeMotorSpeeds(&frontLeftMotorSpeed, &frontRightMotorSpeed, &rearLeftMotorSpeed, &rearRightMotorSpeed);
		setDriveMotorSpeeds(frontLeftMotorSpeed, frontRightMotorSpeed, rearLeftMotorSpeed, rearRightMotorSpeed);
}

void mecanumDrive(long forward, long turn, long rotate) {
	double frontLeftMotorSpeed, frontRightMotorSpeed, rearLeftMotorSpeed, rearRightMotorSpeed;

	if ((forward > -2) && (forward < 2)) forward = 0;
	if ((turn > -2) && (turn < 2)) turn = 0;
	if ((rotate > -2) && (rotate < 2)) rotate = 0;

	double forwardValue = forward / 100.0;
	double turnValue = turn / 100.0;
	double rotateValue = (rotate / 100.0); // / 2.0;

//	if ((turnValue > -0.1) && (turnValue < 0.1)) turnValue = 0.0;

	frontLeftMotorSpeed = forwardValue + rotateValue + turnValue;
	frontRightMotorSpeed = forwardValue - rotateValue - turnValue;
	rearLeftMotorSpeed = forwardValue + rotateValue - turnValue;
	rearRightMotorSpeed = forwardValue - rotateValue + turnValue;

//	rearLeftMotorSpeed *= 1.02;
//	frontRightMotorSpeed *= 1.15;
	normalizeMotorSpeeds(&frontLeftMotorSpeed, &frontRightMotorSpeed, &rearLeftMotorSpeed, &rearRightMotorSpeed);
	setDriveMotorSpeeds(frontLeftMotorSpeed, frontRightMotorSpeed, rearLeftMotorSpeed, rearRightMotorSpeed);
}


void arcadeDrive(long move, long rotate) {
	
	double leftMotorSpeed, rightMotorSpeed;
	double moveValue = move / 100.0;
	double rotateValue = rotate / 100.0;

	if (moveValue > 0.0) {
		if (rotateValue > 0.0) {
			leftMotorSpeed = moveValue - rotateValue;
            rightMotorSpeed = max(moveValue, rotateValue);
        } else {
            leftMotorSpeed = max(moveValue, -rotateValue);
            rightMotorSpeed = moveValue + rotateValue;
        }
    } else {
        if (rotateValue > 0.0) {
            leftMotorSpeed = -max(-moveValue, rotateValue);
            rightMotorSpeed = moveValue + rotateValue;
        } else {
            leftMotorSpeed = moveValue - rotateValue;
            rightMotorSpeed = -max(-moveValue, -rotateValue);
        }
    }

	setDriveMotorSpeeds(leftMotorSpeed, rightMotorSpeed);
}

void checkFirmware(UINT8 deviceNumber, char *desc) {
	UINT32 fwVer = GetFirmwareVersion(deviceNumber);
	printf("%s is id: %d, firmware %u\n", desc, fwVer);
	if (fwVer > 80 && fwVer < 100) {
		printf("\tFRC firmware detected, sending LM_API_UNTRUST_EN\n");
		setTransaction(LM_API_UNTRUST_EN, NULL, 0, deviceNumber);
	}
}

void configControl() {

	UINT8 dataBuffer[8];
	// config encoder lines, speed ref, and PID gains
	
	// the 4 encoders have different ticks per rev, have determined experimentally by reading position in bdc-comm
	// see "Encoder-UseNotes.txt" for 2011 drive train configuration as of 12-Feb-2011
	*((INT16 *)dataBuffer) = (INT16)250;
	setTransaction(LM_API_CFG_ENC_LINES, dataBuffer, 2, REAR_LEFT_CAN_ID);
	setTransaction(LM_API_CFG_ENC_LINES, dataBuffer, 2, REAR_RIGHT_CAN_ID);
	*((INT16 *)dataBuffer) = (INT16)350;
	setTransaction(LM_API_CFG_ENC_LINES, dataBuffer, 2, FRONT_LEFT_CAN_ID);
	*((INT16 *)dataBuffer) = (INT16)360;
	setTransaction(LM_API_CFG_ENC_LINES, dataBuffer, 2, FRONT_RIGHT_CAN_ID);

	// data value 0 sent as parameter to LM_API_SPD_REF sets encoder as speed reference
	dataBuffer[0] = 0x00;	
	setTransaction(LM_API_SPD_REF, dataBuffer, 1, FRONT_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_REF, dataBuffer, 1, FRONT_RIGHT_CAN_ID);
	setTransaction(LM_API_SPD_REF, dataBuffer, 1, REAR_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_REF, dataBuffer, 1, REAR_RIGHT_CAN_ID);

	// data value 1 sent as parameter to LM_API_CFG_BRAKE_COAST sets Jag to brake mode
	dataBuffer[0] = 0x01;	
	setTransaction(LM_API_CFG_BRAKE_COAST, dataBuffer, 1, FRONT_LEFT_CAN_ID);
	setTransaction(LM_API_CFG_BRAKE_COAST, dataBuffer, 1, FRONT_RIGHT_CAN_ID);
	setTransaction(LM_API_CFG_BRAKE_COAST, dataBuffer, 1, REAR_LEFT_CAN_ID);
	setTransaction(LM_API_CFG_BRAKE_COAST, dataBuffer, 1, REAR_RIGHT_CAN_ID);

	// proportional gain (P)
	*((INT32*)dataBuffer) = (INT32)(0.350 * 65536.0);
	printf("P: %d ", *dataBuffer);
	setTransaction(LM_API_SPD_PC, dataBuffer, 4, FRONT_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_PC, dataBuffer, 4, FRONT_RIGHT_CAN_ID);
	setTransaction(LM_API_SPD_PC, dataBuffer, 4, REAR_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_PC, dataBuffer, 4, REAR_RIGHT_CAN_ID);

	// integral gain (I)
	*((INT32*)dataBuffer) = (INT32)(0.003 * 65536.0);
	printf("I: %d ", *dataBuffer);
	setTransaction(LM_API_SPD_IC, dataBuffer, 4, FRONT_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_IC, dataBuffer, 4, FRONT_RIGHT_CAN_ID);
	setTransaction(LM_API_SPD_IC, dataBuffer, 4, REAR_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_IC, dataBuffer, 4, REAR_RIGHT_CAN_ID);

	// derivative gain (D)
	*((INT32*)dataBuffer) = (INT32)(0.001 * 65536.0);
	printf("D: %d ", *dataBuffer);
	setTransaction(LM_API_SPD_DC, dataBuffer, 4, FRONT_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_DC, dataBuffer, 4, FRONT_RIGHT_CAN_ID);
	setTransaction(LM_API_SPD_DC, dataBuffer, 4, REAR_LEFT_CAN_ID);
	setTransaction(LM_API_SPD_DC, dataBuffer, 4, REAR_RIGHT_CAN_ID);

}

void enableControl() {

	// drive motors
	UINT32 messageID = usePID ? LM_API_SPD_EN : LM_API_VOLT_EN;

	setTransaction(messageID, NULL, 0, FRONT_LEFT_CAN_ID);
	setTransaction(messageID, NULL, 0, FRONT_RIGHT_CAN_ID);
	setTransaction(messageID, NULL, 0, REAR_LEFT_CAN_ID);
	setTransaction(messageID, NULL, 0, REAR_RIGHT_CAN_ID);

	// arm motors
//	setTransaction(LM_API_VOLT_EN, NULL, 0, SHOULDER_LEFT_CAN_ID);
//	setTransaction(LM_API_VOLT_EN, NULL, 0, SHOULDER_RIGHT_CAN_ID);
//	setTransaction(LM_API_VOLT_EN, NULL, 0, ELBOW_LEFT_CAN_ID);
//	setTransaction(LM_API_VOLT_EN, NULL, 0, ELBOW_RIGHT_CAN_ID);
}

void disableControl() {
	
	setDriveMotorSpeeds(0.0, 0.0, 0.0, 0.0);
//	setShoulderMotorSpeeds(0.0);
//	setElbowMotorSpeed(0.0);

	//drive motors
	UINT32 messageID = usePID ? LM_API_SPD_DIS : LM_API_VOLT_DIS;

	setTransaction(messageID, NULL, 0, FRONT_LEFT_CAN_ID);
	setTransaction(messageID, NULL, 0, FRONT_RIGHT_CAN_ID);
	setTransaction(messageID, NULL, 0, REAR_LEFT_CAN_ID);
	setTransaction(messageID, NULL, 0, REAR_RIGHT_CAN_ID);

	// arm motors
//	setTransaction(LM_API_VOLT_DIS, NULL, 0, SHOULDER_LEFT_CAN_ID);
//	setTransaction(LM_API_VOLT_DIS, NULL, 0, SHOULDER_RIGHT_CAN_ID);
//	setTransaction(LM_API_VOLT_DIS, NULL, 0, ELBOW_LEFT_CAN_ID);
//	setTransaction(LM_API_VOLT_DIS, NULL, 0, ELBOW_RIGHT_CAN_ID);
}

int main(int argc, char *argv[]) {
	printf("Starting 2CAN Driver Main thread\n");
	while (!my2CAN.IsProcessing()) {
		printf("Main thread waiting for 2CAN to be found\n");
		Sleep(500);
	}

	printf("Main thread now processing\n");
//	checkFirmware(FRONT_LEFT_CAN_ID, "Front Left Jag");
//	checkFirmware(REAR_LEFT_CAN_ID, "Rear Left Jag");
//	checkFirmware(FRONT_RIGHT_CAN_ID, "Front Right Jag");
//	checkFirmware(REAR_RIGHT_CAN_ID, "Rear Right Jag");
//	int timeout = 30 * 10;  
	
	Joystick driveStick;
	unsigned thrID;
	_beginthreadex(NULL, 0, kbInputHandler, 0, 0, &thrID);

//	while (--timeout) {
	bool wasDisabled = true;
	int loopCount = 0;
	while (keepRunning) {
		
		if (configPID) { configControl(); configPID = false; }

		if (robotEnabled) {
//			my2CAN.sendMessage(0x80000000 | CAN_MSGID_API_HEARTBEAT, NULL, 0);
			
			if (wasDisabled) { enableControl(); wasDisabled = false; }

			if (useArcade) arcadeDrive(driveStick.GetYAxis(), driveStick.GetXAxis());
			else 
/*
				if (driveStick.GetButton(1))		// strafe left @ full speed
					mecanumDrive(0,100,0);
				else if(driveStick.GetButton(3))	// strafe right @ full speed
					mecanumDrive(0,-100,0);
				else
*/			
				if (!driveStick.GetButton(5)) mecanumDrive(driveStick.GetYAxis(), -driveStick.GetXAxis(), -driveStick.GetZAxisRotation());
			
/*
			if (++loopCount >= 20) {
				printf("YAxis: %d XAxis: %d\n", driveStick.GetYAxis(), -driveStick.GetXAxis());
				loopCount = 0;
			}
*/
/*
			// on Logitech Dual Action gamepad, button 4 raises arm, button 2 lowers arm
			if (driveStick.GetButton(4)) setShoulderMotorSpeeds(-.5);
			else if (driveStick.GetButton(2)) setShoulderMotorSpeeds(0.5);
			else setShoulderMotorSpeeds(0.0);

			// on Logitech Dual Action gamepad, button 1 extends elbow, button 3 retracts elbow
			// if holding button 5, use buttons 1 & 3 to strafe instead of operating elbow
			if (driveStick.GetButton(5)) {
				if (driveStick.GetButton(1)) mecanumDrive(0, 30, 0);
				else if (driveStick.GetButton(3)) mecanumDrive(0, -30, 0);
				else mecanumDrive(0, 0, 0);
			} else {
				if (driveStick.GetButton(1)) setElbowMotorSpeed(-.4);
				else if (driveStick.GetButton(3)) setElbowMotorSpeed(.4);
				else setElbowMotorSpeed(0.0);
			}
*/
		} else {
			if (!wasDisabled) { disableControl(); wasDisabled = true; }
		}
			
		Sleep(20);
	}
	
	disableControl();
	printf("Leaving 2CAN Driver\n");
}

UINT32 GetFirmwareVersion(UINT8 deviceNumber)
{
	UINT8 dataBuffer[8];
	UINT8 dataSize;

	// Set the MSB to tell the 2CAN that this is a remote message.
	printf("Entering GetFirmwareVersion(%u)\n", deviceNumber);
	getTransaction(0x80000000 | CAN_MSGID_API_FIRMVER, dataBuffer, &dataSize, deviceNumber);
	printf("GetFirmwareVersion: getTransaction returned dataSize = %u\n", dataSize);
	if (dataSize == sizeof(UINT32))
	{
//		return unpackINT32(dataBuffer);
		return *((INT32*)dataBuffer);
	}
	return 0;
}

void getTransaction(UINT32 messageID, UINT8 *data, UINT8 *dataSize, UINT8 deviceNumber)
{
	UINT32 targetedMessageID = messageID | deviceNumber;
	INT32 status = 0;

	// Make sure we don't have more than one transaction with the same Jaguar outstanding.
//	semTake(m_transactionSemaphore, WAIT_FOREVER);

	// Send the message requesting data.
//	printf("Entering getTransaction msgID = 0x%08x, dataSize = %u, deviceNumber = %u\n", 
//				messageID, *dataSize, deviceNumber);
	status = sendMessage(targetedMessageID, NULL, 0);
//	wpi_assertCleanStatus(status);
	//caller may have set bit31 for remote frame transmission so clear invalid bits[31-22]
	targetedMessageID &= 0x1FFFFFFF;
	// Wait for the data.
	status = receiveMessage(targetedMessageID, data, dataSize);
//	wpi_assertCleanStatus(status);

	// Transaction complete.
//	semGive(m_transactionSemaphore);
}

void setTransaction(UINT32 messageID, const UINT8 *data, UINT8 dataSize, UINT8 deviceNumber)
{
	//LM_API_ACK = 0x020220
	//ACK msgId including device addr will be 0x020220nn, where nn is 00 to 3F
	UINT32 ackMessageID = LM_API_ACK | deviceNumber;
	INT32 status = 0;

	UINT8 trashBuffer[8];
	UINT8 trashSize = 8;
	// Make sure we don't have more than one transaction with the same Jaguar outstanding.
//	semTake(m_transactionSemaphore, WAIT_FOREVER);

//	printf("Entering setTransaction msgID = 0x%08x, dataSize = %u, deviceNumber = %u\n", 
//			messageID, dataSize, deviceNumber);

	// Throw away any stale acks.
//	receiveMessage(ackMessageID, NULL, 0);
	receiveMessage(ackMessageID, trashBuffer, &trashSize);
	// Send the message with the data.
	status = sendMessage(messageID | deviceNumber, data, dataSize);
//	wpi_assertCleanStatus(status);
	// Wait for an ack.
//	status = receiveMessage(ackMessageID, NULL, 0);
	receiveMessage(ackMessageID, trashBuffer, &trashSize);
	//	wpi_assertCleanStatus(status);

	// Transaction complete.
//	semGive(m_transactionSemaphore);
}

INT32 sendMessage(UINT32 messageID, const UINT8 *data, UINT8 dataSize)
{

//	FRC_NetworkCommunication_JaguarCANDriver_sendMessage(messageID, data, dataSize, &status);
//	return status;
	return my2CAN.sendMessage(messageID, data, dataSize);	
}

INT32 receiveMessage(UINT32 messageID, UINT8 *data, UINT8 *dataSize) //, float timeout)
{
//	INT32 status = 0;
//	FRC_NetworkCommunication_JaguarCANDriver_receiveMessage(messageID, data, dataSize,
//			(UINT32)(timeout * 1000), &status);
//	return status;
	INT32 status = my2CAN.receiveMessage(&messageID, data, dataSize);
//	printf("receiveMessage ret status %d (recv'd msgID 0x%08x, dataSize %u)\n", status, messageID, *dataSize);
	return status;
}

