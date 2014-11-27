// Joystick.h
#define DIRECTINPUT_VERSION 0x0800
#include <windows.h>
#include <commctrl.h>
#include <basetsd.h>
#include <dinput.h>
#include <dinputd.h>
#include <assert.h>
#include <oleauto.h>
#include <shellapi.h>
#include <strsafe.h>
#include <stdio.h>
#include <process.h>

class Joystick {

	private:
		LPDIRECTINPUT8  m_pDI;
		LPDIRECTINPUTDEVICE8 m_pJoystick; 
		DIDEVCAPS m_DIDevCaps;
		static BOOL CALLBACK EnumJoysticksCallback(const DIDEVICEINSTANCE* pDevInstance,void *pThis) {
			return ((Joystick *)pThis)->EnumJoysticks(pDevInstance);
		}
		static BOOL CALLBACK EnumAxesCallback(const DIDEVICEOBJECTINSTANCE* pDevObjInstance,void *pThis) {
			return ((Joystick *)pThis)->EnumAxes(pDevObjInstance);
		}
		static unsigned __stdcall ThreadStaticEntryPoint(void * pThis) {
			return ((Joystick *)pThis)->Process();
		}

		CRITICAL_SECTION m_cs;
		DIJOYSTATE2 m_joystickState;
		bool m_runThread;
		HANDLE m_thrHandle;
		unsigned m_thrID;

	public:
		Joystick();
		~Joystick();
		bool EnumJoysticks(const DIDEVICEINSTANCE*);
		bool EnumAxes(const DIDEVICEOBJECTINSTANCE*);
		unsigned Process();
		int GetXAxis();
		int GetYAxis();
		int GetZAxis();
		int GetZAxisRotation();
		bool GetButton(int buttonNumber);
};
