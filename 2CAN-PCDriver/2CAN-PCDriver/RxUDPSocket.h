#ifndef RXUDPSockets__h_
#define RXUDPSockets__h_

#include "errno.h"
#include "signal.h"
#include <string>
#include "time.h"
//#include <winsock2.h>
#include <deque>
#include <Windows.h>
#include <process.h>


class RxUDPSocket
{
	public:
		RxUDPSocket(const char * sIP,unsigned long ulPort,unsigned long ulOptions);
		~RxUDPSocket();
		unsigned long  Receive(char * data, unsigned long len);
/*		
		int GetRxIP()
		{
			return m_RxIp;
		}
*/
		bool WaitForConnect(int timeout);
		bool IsConnected(void);
		void Run();
	private:

		static unsigned __stdcall ThreadStaticEntryPoint(void * pThis)
		{
			RxUDPSocket *pThread = (RxUDPSocket *)pThis;
			pThread->Run();
			return 1;
		}
		int CreateRxSocket(void);
		bool QueryEndThread(void);

		unsigned long m_ulOptions;
		int	socketFile;
		struct in_addr remoteIP;
//		CTask m_obTask;
		unsigned m_thrID;
		HANDLE m_thrHandle;
		CRITICAL_SECTION m_cs;
		CRITICAL_SECTION m_EndThrd;

		bool m_bConnected;
		bool m_bEndThrd;

		typedef struct _datagram
		{
			char data[1500];
			unsigned int len;
		}datagram;
		std::deque<datagram> m_datagrams;
		
		unsigned int m_iRxCount;
		
		char m_remoteIPString[20];
		unsigned long m_port;
		
//		int m_RxIp;
};


#endif // RXUDPSockets__h_


