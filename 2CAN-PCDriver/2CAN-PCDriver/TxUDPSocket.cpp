#include "TxUDPSocket.h"

/* 
	does not need to be threaded since TxUPDSocket::Send() does the UDP send directly
*/

TxUDPSocket::TxUDPSocket(	const char * sIP,
							unsigned long ulPort,
							unsigned long ulOptions) //: m_obTask("2CAN Tx",(FUNCPTR)InitTask,50)
{
	m_ulOptions = ulOptions;
	m_bConnected = false;
	socketFile = -1;
//	m_ThrID = semMCreate(SEM_Q_PRIORITY);

	strcpy(m_remoteIPString,sIP);
	m_port = ulPort;
	
	/*
	if (!m_obTask.Start((INT32)this))
	{
		//wpi_fatal(UDPTaskError);
		printf("2CAN Task did not start successfully\n");
	}*/
	InitializeCriticalSection(&m_cs);
	Run();
//	m_thrHandle = (HANDLE)_beginthreadex(NULL, 0, TxUDPSocket::ThreadStaticEntryPoint, this, 0, &m_thrID);
}

TxUDPSocket::~TxUDPSocket()
{
//	printf("Entering TxUDPSocket dtor\n");
	EnterCriticalSection(&m_cs);
//	printf("TxUDPSocket dtor:  inside cs\n");
	if(socketFile != -1) {
//		printf("TxUDPSocket dtor: closing socket\n");
		closesocket(socketFile);
	}
	socketFile = -1;
	m_bConnected = false;
//	printf("TxUDPSocket dtor:  leaving cs\n");
	LeaveCriticalSection(&m_cs);
//	printf("TxUDPSocket dtor:  deleting cs\n");
	DeleteCriticalSection(&m_cs);
	printf("TxUDPSocket port %d destroyed\n", m_port);
//	m_obTask.Stop();
}

/*
void TxUDPSocket::InitTask(TxUDPSocket * pThis)
{
	pThis->Run();
}
*/

int TxUDPSocket::Send(char * data, unsigned long len)
{
	struct sockaddr_in      serverSockAddr;
	int						bytes;

//	CRITICAL_REGION(m_ThrID);
	
	/*if(CreateTxSocket() != 0)
	{ 
		if(socketFile != -1)
			close(socketFile);
		socketFile = -1;
		return -1;
	}*/
//	printf("Entering TxUDPSocket::Send() port %d, len = %ul\n", m_port, len);

	if(IsConnected() == false) {
		printf("TxUDPSocket::Send() port %ul not connected, ret -10\n", m_port); 
		return -10;
	}

	remoteIP.S_un.S_addr = inet_addr(m_remoteIPString);
/*
	if (!inet_addr(m_remoteIPString, &remoteIP))
	{
		//return ;
	}
*/

	memset(&serverSockAddr, 0, sizeof(serverSockAddr));
	serverSockAddr.sin_family = AF_INET;
	serverSockAddr.sin_addr.s_addr = inet_addr(m_remoteIPString);
	serverSockAddr.sin_port = htons(m_port);
	
	bytes = sendto(socketFile, data, len, 0,
		(struct sockaddr*)&serverSockAddr, sizeof(serverSockAddr));

//	printf("TxUDPSocket::Send() port %d sent %d bytes to %s\n", m_port, bytes, m_remoteIPString);

	if(bytes < 0)
	{
		printf("TxUDPSocket::Send() port %ul failed sending to %s, %d bytes < 0, returning -2\n", 
				m_port, m_remoteIPString, bytes);
		closesocket(socketFile);
		socketFile = -1;
		return -2;
	}
	if( (unsigned long)bytes != len)
	{
		printf("TxUDPSocket::Send() port %ul failed sending to %s, %d bytes != %ul len, returning -3\n",
				m_port, m_remoteIPString, bytes, len);
		closesocket(socketFile);
		socketFile = -1;
		return -3;
	}

	/*close(socketFile);
	socketFile = -1;*/
	
//	END_REGION;	
	return 0;
	
}

void TxUDPSocket::Run(void)
{

	while(CreateTxSocket() != 0)
		Sleep(10);

//	CRITICAL_REGION(m_ThrID);
	EnterCriticalSection(&m_cs);
	m_bConnected = true;
	LeaveCriticalSection(&m_cs);
/*
	while (true)
		Sleep(50);
*/
//	END_REGION;
/*	
	while(1)
		taskDelay(100);	
*/
}

int TxUDPSocket::CreateTxSocket(void)
{
	struct sockaddr_in	sin;

	if ((socketFile = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
	{
		printf("CreateTxSocket socket() ret err %d\n",errno);
		return (errno);
	}

	sin.sin_family = AF_INET;
	sin.sin_port = htons(m_port);
	sin.sin_addr.s_addr = INADDR_ANY;

	if (bind(socketFile, (struct sockaddr*)&sin, sizeof(sin)) < 0)
	{
		closesocket(socketFile);
		socketFile = -1;
		printf("CreateTxSocket bind() ret err %d\n", errno);
		return (errno);
	}
	
	if(m_ulOptions & 1)
	{
		BOOL bOptVal = 1;
		int bOptLen = sizeof(BOOL);
		if(setsockopt(socketFile,SOL_SOCKET,SO_BROADCAST,(char*)&bOptVal,bOptLen) < 0)
		{
			closesocket(socketFile);
			socketFile = -1;
			printf("CreateTxSocket setsockopt() ret err %d\n", errno);
			return (errno);
		}
	}
	
//	printf("CreateTxSocket port %ul SUCCESS!\n", m_port);
	return (0);
}

bool TxUDPSocket::WaitForConnect(void)
{
	int timeout = 1000;
	while(!IsConnected() && timeout--)
		Sleep(1);
		
	return IsConnected();
}

bool TxUDPSocket::IsConnected(void)
{
	bool retval;
//	CRITICAL_REGION(m_ThrID);
	EnterCriticalSection(&m_cs);
	retval = m_bConnected;
	LeaveCriticalSection(&m_cs);
//	END_REGION;
	return retval;
}

	
