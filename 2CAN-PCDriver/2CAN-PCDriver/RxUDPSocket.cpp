#include "RxUDPSocket.h"

/*
	this needs to be threaded since UDP isn't buffered; packets will be dropped otherwise
*/

RxUDPSocket::RxUDPSocket(const char * sIP,unsigned long ulPort,unsigned long ulOptions)
//: m_obTask("2CAN Rx",(FUNCPTR)InitTask,50)
{
	m_ulOptions = ulOptions;
	m_iRxCount = 0;
	m_bConnected = false;
	m_bEndThrd = false;
	socketFile = -1	;
//	m_ThrID = semMCreate(SEM_Q_PRIORITY);

	strcpy(m_remoteIPString,sIP);
	m_port = ulPort;
	InitializeCriticalSection(&m_cs);
	InitializeCriticalSection(&m_EndThrd);

	m_thrHandle = (HANDLE)_beginthreadex(NULL, 0, RxUDPSocket::ThreadStaticEntryPoint, this, 0, &m_thrID);

//	m_RxIp = ~0;

/*
	int ret = m_obTask.Start((INT32)this); 
	if (!ret)
	{
		//wpi_fatal(UDPTaskError);
		printf("2CAN Task did not start successfully\n");
	}
*/
}

RxUDPSocket::~RxUDPSocket()
{
	EnterCriticalSection(&m_EndThrd);
	m_bEndThrd = true;
	LeaveCriticalSection(&m_EndThrd);
	bool keepLooping = true;
	int exitCode;
	while (keepLooping) {
		int ts = GetExitCodeThread(m_thrHandle, (LPDWORD)&exitCode);
		if (!ts) {
			printf("RxUDPSocket dtor: GetExitCodeThread returned 0 (error)\n");
			Sleep(500);
			continue;
		}
		if (exitCode == STILL_ACTIVE) {
			printf("RxUDPSocket dtor: thread still active, waiting\n");
			Sleep(500);
		} else
			keepLooping = false;
	}
	printf("RxUDPSocket dtor: thread should now be stopped, destroying...\n");
	if(socketFile != -1)
		closesocket(socketFile);
	socketFile = -1;
	DeleteCriticalSection(&m_cs);
	DeleteCriticalSection(&m_EndThrd);
	printf("RxUPDSocket port %d destroyed\n", m_port);
}

bool RxUDPSocket::QueryEndThread() {
	EnterCriticalSection(&m_EndThrd);
	bool retval = m_bEndThrd;
	LeaveCriticalSection(&m_EndThrd);
	return retval;
}

void RxUDPSocket::Run()
{
	struct sockaddr_in      serverSockAddr;
	int				sin_len;

	
	remoteIP.S_un.S_addr = inet_addr(m_remoteIPString);
/*
	if (!inet_addr(m_remoteIPString, &remoteIP))
	{
		//return ;
	}
*/

	while(CreateRxSocket() != 0)
		Sleep(10);

	EnterCriticalSection(&m_cs);
	m_bConnected = true;
	LeaveCriticalSection(&m_cs);

	try
	{
	   while (!QueryEndThread())
		{
			datagram dgram = {0};
			sin_len = sizeof(serverSockAddr);
	
			dgram.len = recvfrom(	socketFile,
									dgram.data, 
									sizeof(dgram.data),
									0,
									(struct sockaddr*)&serverSockAddr,
									&sin_len);
			if (dgram.len > 0) {
				if ( (m_ulOptions & 1) || (serverSockAddr.sin_addr.s_addr == remoteIP.s_addr)) {
//					printf("RxUDPSocket::Run() port %d; rcv'd dgram.len = %ul, pushing onto queue\n",
//							m_port, dgram.len);
					EnterCriticalSection(&m_cs);
					m_datagrams.push_back(dgram);
					m_iRxCount++;
					LeaveCriticalSection(&m_cs);
//					printf("RxUDPSocket::Run() port %d; msg count = %u\n", m_port, m_iRxCount);
				}
			} 
/*
			if ( (m_ulOptions&1) || (serverSockAddr.sin_addr.s_addr == remoteIP.s_addr))
			{
				if (dgram.len > 0)
				{
//					CRITICAL_REGION(m_ThrID);
					m_datagrams.push_back(dgram);
					m_iRxCount++;
					
//					m_RxIp = inet_netof(remoteIP);
//					END_REGION;
				}
			}
*/	
			Sleep(1);

	}
	}catch(...)
	{
	}
	printf("RxUDPSocket::Run() exiting....\n");
}
int RxUDPSocket::CreateRxSocket(void)
{
	struct sockaddr_in	sin;

	if ((socketFile = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
	{
		socketFile = -1;
		printf("CreateRxSocket socket() ret err %d\n", errno);
		return (errno);
	}

	sin.sin_family = AF_INET;
	sin.sin_port = htons(m_port);
	sin.sin_addr.s_addr = INADDR_ANY;

	if (bind(socketFile, (struct sockaddr*)&sin, sizeof(sin)) < 0)
	{
		closesocket(socketFile);
		socketFile = -1;
		printf("CreateRxSocket bind() ret err %d\n", errno);
		return (errno);
	}

	// If iMode != 0, non-blocking mode is enabled.
	u_long iMode=1;
	ioctlsocket(socketFile,FIONBIO,&iMode);
	printf("CreateRxSocket port %d SUCCESS!!\n", m_port);
	return (0);
}
unsigned long RxUDPSocket::Receive(char * data, unsigned long len)
{
//	CRITICAL_REGION(m_ThrID);
	EnterCriticalSection(&m_cs);
	if(m_datagrams.empty()) {
		LeaveCriticalSection(&m_cs);
		return 0;
	}
	
//	printf("RxUDPSocket::Receive() port %d, queue not empty, processing msg\n", m_port);
	datagram &dgram = m_datagrams.front();
	
	if(len > dgram.len)
		len = dgram.len;
	memcpy(data,dgram.data,len);

	m_datagrams.pop_front();
	LeaveCriticalSection(&m_cs);
//	END_REGION;
//	printf("RxUDPSocket::Receive() port %d, returning len = %ul\n", m_port, len);
	return len;
}

bool RxUDPSocket::WaitForConnect(int timeout)
{
	bool con;
	while(!(con = IsConnected()) && --timeout) {
//		printf("RxUDPSocket %s, time left %d\n",(con) ? "connected" : "not connected", timeout);
		Sleep(1);
	}

	return con;
		
}
bool RxUDPSocket::IsConnected(void)
{
//	CRITICAL_REGION(m_ThrID);
	EnterCriticalSection(&m_cs);
	bool retValue = m_bConnected;
	LeaveCriticalSection(&m_cs);
	return retValue;
//	END_REGION;
}



	
