
#include "Joystick.h"


// "Saitek P990 Dual Analog Pad" = driveStick
// "Logitech Dual Action" = gameStick

// default ctor, will return first Joystick that DirectInput enumerates
Joystick::Joystick() {
	printf("Entering Joystick default ctor\n");
	m_buttonCallback = NULL;
	wcscpy(m_ProductName, L"");
	Initialize();
	printf("Leaving Joystick default ctor\n");
}

// this ctor allows a callback which will be triggered if button 1 is toggled
Joystick::Joystick(void (*cb)()) {
	printf("Entering Joystick callback ctor\n");
	m_buttonCallback = cb;
	wcscpy(m_ProductName, L""); 
	Initialize();
	printf("Leaving Joystick callback ctor\n");
}

// this ctor allows picking Joystick based on product name
Joystick::Joystick(wchar_t *name) {
	wprintf(L"Entering Joystick named ctor, will look for product name %s\n", name);
	wcscpy(m_ProductName, name);
	m_buttonCallback = NULL;
	Initialize();
	wprintf(L"Leaving Joystick named ctor for product name %s\n", name);
}

void Joystick::Initialize() {
	printf("Entering Joystick::Initialize\n");
	m_InitializedOK = false;
	CoInitialize(NULL);
	HINSTANCE hinst = GetModuleHandle(NULL);
	m_pDI = NULL;
	m_pJoystick = NULL;
	m_runThread = false;

    if FAILED(DirectInput8Create(hinst, DIRECTINPUT_VERSION, IID_IDirectInput8, (void**)&m_pDI, NULL)) { 
		printf("DirectInput8Create failed, leaving Joystick::Initialize()\n");
		return;
    }

    m_pDI->EnumDevices(DI8DEVCLASS_GAMECTRL, Joystick::EnumJoysticksCallback, this, DIEDFL_ATTACHEDONLY);

	if (!m_pJoystick) {
		printf("m_pJoystick NULL, leaving Joystick::Initialize()\n");
		return;
	}

	if (FAILED(m_pJoystick->SetDataFormat(&c_dfDIJoystick2))) {
        printf("SetDataFormat failed, leaving Joystick::Initialize()\n");
		return;
    }
   
    if (FAILED(m_pJoystick->SetCooperativeLevel(NULL, DISCL_NONEXCLUSIVE | DISCL_BACKGROUND))) {
		printf("SetCooperativeLevel failed, leaving Joystick::Initialize()\n");
		return;
    }

	m_DIDevCaps.dwSize = sizeof(DIDEVCAPS);
    if (FAILED(m_pJoystick->GetCapabilities(&m_DIDevCaps))) {
        printf("GetCapabilities failed, leaving Joystick::Initialize()\n");
		return;
    }
   
	if (FAILED(m_pJoystick->EnumObjects(Joystick::EnumAxesCallback, this, DIDFT_AXIS))) {
		printf("EnumObjects failed, leaving Joystick::Initialize()\n");
		return;
    }


	if (FAILED(m_pJoystick->Poll())) {
		while (m_pJoystick->Acquire() == DIERR_INPUTLOST) Sleep(10);
	}

	printf("Joystick::Initialize(): starting thread\n");
	m_runThread = true;
	InitializeCriticalSection(&m_cs);
	m_thrHandle = (HANDLE) _beginthreadex(NULL, 0, Joystick::ThreadStaticEntryPoint, this, 0, &m_thrID);
	if (m_thrHandle)
		m_InitializedOK = true;
	else
		printf("_beginthreadex failed, ");
	printf("Leaving Joystick::Initialize()\n");
}

Joystick::~Joystick() {
	printf("Entering Joystick dtor\n");
	if (m_InitializedOK) {
		EnterCriticalSection(&m_cs);
		m_runThread = false;
		LeaveCriticalSection(&m_cs);
		bool keepLooping = true;
		int exitCode;
		while (keepLooping) {
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
	}
	printf("Leaving Joystick dtor\n");
}

bool Joystick::InitializedOK() {
	return m_InitializedOK;
}

bool Joystick::EnumJoysticks(const DIDEVICEINSTANCE *pDevInst) {

	bool matchFound = false;
	wprintf(L"EnumJoysticks: found ProductName %s; desired ProductName ", pDevInst->tszProductName);
	if (!m_ProductName[0]) {
		wprintf(L"NULL\n Not seeking specific Joystick, creating device\n");
		matchFound = true;
	} 
	wprintf(L"%s\n", m_ProductName);
	if (!wcsicmp(m_ProductName, pDevInst->tszProductName)) {
		wprintf(L"enumerated joystick matches desired, creating device\n", m_ProductName);
		matchFound = true;
	}

	if (matchFound) {
		if (FAILED(m_pDI->CreateDevice(pDevInst->guidInstance, &m_pJoystick, NULL))) {
			 printf("EnumJoysticks failed, stopping enumeration\n");
			 return DIENUM_STOP;
		}
	} else {
		wprintf(L"No match found, continuing...\n");
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
		m_oldJoystickState = m_joystickState;
		m_pJoystick->GetDeviceState(sizeof(m_joystickState), &m_joystickState);
		LeaveCriticalSection(&m_cs);

		// if a callback has been provided, check if button 1 was toggled, and if so trigger callback
		if (m_buttonCallback)
			if (m_joystickState.rgbButtons[0] && !m_oldJoystickState.rgbButtons[0]) m_buttonCallback();
		
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

bool Joystick::GetButton(int button) {
	return m_joystickState.rgbButtons[button - 1];
}