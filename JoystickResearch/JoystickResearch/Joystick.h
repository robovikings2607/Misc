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
#include <tchar.h>
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

		wchar_t m_ProductName[MAX_PATH];
		CRITICAL_SECTION m_cs;
		DIJOYSTATE2 m_joystickState, m_oldJoystickState;
		bool m_runThread;
		bool m_InitializedOK;
		HANDLE m_thrHandle;
		unsigned m_thrID;
		void (*m_buttonCallback)();

		void Initialize();
		bool EnumJoysticks(const DIDEVICEINSTANCE*);
		bool EnumAxes(const DIDEVICEOBJECTINSTANCE*);
		unsigned Process();

	public:
		Joystick();
		Joystick(void (*buttonCallback)());
		Joystick(TCHAR *name);
		~Joystick();
		bool InitializedOK();
		int GetXAxis();
		int GetYAxis();
		int GetZAxis();
		int GetZAxisRotation();
		bool GetButton(int button);
};
