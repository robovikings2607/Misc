#ifndef TXUDPSockets__h_
#define TXUDPSockets__h_

#include "errno.h"
#include "signal.h"
#include <string>
#include "time.h"
//#include <WinSock2.h>
#include <Windows.h>
#include <process.h>

class TxUDPSocket
{
	public:
		TxUDPSocket(const char * sIP,unsigned long ulPort,unsigned long ulOptions);
		~TxUDPSocket();
		
		int Send(char * data, unsigned long len);

		bool WaitForConnect(void);
		bool IsConnected(void);
		void Run(void);
	private:
//		static void InitTask(TxUDPSocket * pThis);
		
		int CreateTxSocket(void);

		static unsigned __stdcall ThreadStaticEntryPoint(void * pThis)
		{
			TxUDPSocket *pThread = (TxUDPSocket *)pThis;
			pThread->Run();
			return 1;
		}
		
//		bool IsConnected(void);
		
		HANDLE m_thrHandle;
		unsigned m_thrID;

		int	socketFile;
		struct in_addr remoteIP;
//		CTask m_obTask;
//		SEM_ID m_ThrID;
		bool m_bConnected;
		CRITICAL_SECTION m_cs;

		char m_remoteIPString[20];
		unsigned long m_port;
		unsigned long m_ulOptions;
};


#endif // TXUDPSockets__h_


