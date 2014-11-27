
#include "Joystick.h"

Joystick::Joystick() {
	printf("Entering Joystick ctor\n");
	CoInitialize(NULL);
	HINSTANCE hinst = GetModuleHandle(NULL);
	m_pDI = NULL;
	m_pJoystick = NULL;
	m_runThread = false;

    if FAILED(DirectInput8Create(hinst, DIRECTINPUT_VERSION, IID_IDirectInput8, (void**)&m_pDI, NULL)) { 
		printf("DirectInput8Create failed, leaving ctor\n");
		return;
    }

    m_pDI->EnumDevices(DI8DEVCLASS_GAMECTRL, Joystick::EnumJoysticksCallback, this, DIEDFL_ATTACHEDONLY);

	if (!m_pJoystick) {
		printf("m_pJoystick NULL, leaving ctor without starting thread\n");
		return;
	}

	if (FAILED(m_pJoystick->SetDataFormat(&c_dfDIJoystick2))) {
        printf("SetDataFormat failed, leaving ctor\n");
		return;
    }
   
    if (FAILED(m_pJoystick->SetCooperativeLevel(NULL, DISCL_NONEXCLUSIVE | DISCL_BACKGROUND))) {
		printf("SetCooperativeLevel failed, leaving ctor\n");
		return;
    }

	m_DIDevCaps.dwSize = sizeof(DIDEVCAPS);
    if (FAILED(m_pJoystick->GetCapabilities(&m_DIDevCaps))) {
        printf("GetCapabilities failed, leaving ctor\n");
		return;
    }
   
	if (FAILED(m_pJoystick->EnumObjects(Joystick::EnumAxesCallback, this, DIDFT_AXIS))) {
		printf("EnumObjects failed, leaving ctor\n");
		return;
    }


	if (FAILED(m_pJoystick->Poll())) {
		while (m_pJoystick->Acquire() == DIERR_INPUTLOST) Sleep(10);
	}

	printf("Joystick ctor: starting thread\n");
	m_runThread = true;
	InitializeCriticalSection(&m_cs);
	m_thrHandle = (HANDLE) _beginthreadex(NULL, 0, Joystick::ThreadStaticEntryPoint, this, 0, &m_thrID);
	printf("Leaving Joystick ctor\n");
}

Joystick::~Joystick() {
	printf("Entering Joystick dtor\n");
	EnterCriticalSection(&m_cs);
	m_runThread = false;
	LeaveCriticalSection(&m_cs);
	bool keepLooping = true;
	int exitCode;
	while (keepLooping && m_pJoystick) {
		if (!GetExitCodeThread(m_thrHandle, (LPDWORD)&exitCode)) {
			printf("Joystick dtor: GetExitCodeThread returned 0 (error)\n");
			Sleep(20);
			continue;
		}
		if (exitCode == STILL_ACTIVE) {
			printf("Joystick dtor: thread still active, waiting\n");
			Sleep(20);
		} else
			keepLooping = false;
	}
	printf("Joystick dtor: thread should now be stopped, destroying...\n");
	DeleteCriticalSection(&m_cs);
	printf("Leaving Joystick dtor\n");
}

bool Joystick::EnumJoysticks(const DIDEVICEINSTANCE *pDevInst) {
     if(FAILED(m_pDI->CreateDevice(pDevInst->guidInstance, &m_pJoystick, NULL))) {
		 printf("EnumJoysticks failed, stopping enumeration\n");
         return DIENUM_STOP;
	 }
     return DIENUM_CONTINUE;
}

bool Joystick::EnumAxes(const DIDEVICEOBJECTINSTANCE *pDevObjInst) {

	 DIPROPRANGE propRange; 
     propRange.diph.dwSize       = sizeof(DIPROPRANGE); 
     propRange.diph.dwHeaderSize = sizeof(DIPROPHEADER); 
     propRange.diph.dwHow        = DIPH_BYID; 
     propRange.diph.dwObj        = pDevObjInst->dwType;
     propRange.lMin              = -100; 
     propRange.lMax              = 100; 
  
     // Set the range for the axis
     if (FAILED(m_pJoystick->SetProperty(DIPROP_RANGE, &propRange.diph))) {
		 printf("EnumAxesCallback failed, stopping enumeration\n");
         return DIENUM_STOP;
     }
  
     return DIENUM_CONTINUE;
}

unsigned Joystick::Process() {
	printf("Entering Joystick::Process()\n");
	while (m_runThread) {
		if (FAILED(m_pJoystick->Poll()))
			m_pJoystick->Acquire();
		EnterCriticalSection(&m_cs);
		m_pJoystick->GetDeviceState(sizeof(m_joystickState), &m_joystickState);
		LeaveCriticalSection(&m_cs);
		Sleep(20);
	}
	printf("Leaving Joystick::Process()\n");
	return 1;
}

int Joystick::GetXAxis() {
	return m_joystickState.lX;
}

int Joystick::GetYAxis() {
	return m_joystickState.lY;
}

int Joystick::GetZAxis() {
	return m_joystickState.lZ;
}

int Joystick::GetZAxisRotation() {
	return m_joystickState.lRz;
}

bool Joystick::GetButton(int buttonNumber) {
	return m_joystickState.rgbButtons[buttonNumber - 1];
}
