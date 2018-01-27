#include <windows.h>
//#include <WinSock2.h>
#include "2CAN.h"
#include "2CANDataObjects.h"
#include <deque>

//#define ENABLE_PRINT
#ifdef ENABLE_PRINT
#define tprintf printf
#else
#define tprintf(...) do{}while(0)
#endif

void Swap(int16 &var);
void Swap(int32 &var);

/**
 * C2CAN contructor.
 */
C2CAN::C2CAN() 
//	: m_obTask("C2CAN", (FUNCPTR)InitTask,60)
{
	m_To2CAN_TxCANPacket.iSig = SIG_TX_CAN_FRAMES;
	m_To2CAN_TxCANPacket.iByteLen = sizeof(m_To2CAN_TxCANPacket);

	m_p2CANRxSockTx = 0;

	m_ulBadFrame = 0;

	m_bEndThrd = false;
//	m_SemEndThrd = semMCreate(SEM_Q_PRIORITY | SEM_INVERSION_SAFE | SEM_DELETE_SAFE);

//	m_SemRxFrames = semMCreate(SEM_Q_PRIORITY | SEM_INVERSION_SAFE | SEM_DELETE_SAFE);

//	m_SemTxFrames = semMCreate(SEM_Q_PRIORITY | SEM_INVERSION_SAFE | SEM_DELETE_SAFE);

	if (WSAStartup(0x202, &m_wsaData) != 0) {
		printf("ERROR:  could not initialize WinSock\n");
		eState = eDoNothing;
	}
	else {
		printf("Initialized WinSock\n");
		eState = eFind2CAN;
	}

	m_ulHeartBeatTimeout = 0;
/*
	if (!m_obTask.Start((INT32)this)) {
		tprintf("2CAN Task did not start successfully\n");
		//wpi_fatal(DriverStationTaskError);
	}
*/
	InitializeCriticalSection(&m_SemEndThrd);
	InitializeCriticalSection(&m_SemTxFrames);
	InitializeCriticalSection(&m_SemRxFrames);
	m_thrHandle = (HANDLE) _beginthreadex(NULL, 0, C2CAN::ThreadStaticEntryPoint, this, 0, &m_thrID);

}

C2CAN::~C2CAN() {
	Discard();
	WSACleanup();
	DeleteCriticalSection(&m_SemEndThrd);
	DeleteCriticalSection(&m_SemTxFrames);
	DeleteCriticalSection(&m_SemRxFrames);
}

void C2CAN::Discard() {
//	CRITICAL_REGION(m_SemEndThrd);
	EnterCriticalSection(&m_SemEndThrd);
	m_bEndThrd = true;
	LeaveCriticalSection(&m_SemEndThrd);
//	END_REGION;

//	taskDelay(50);
	Sleep(50);

//	m_obTask.Stop();

//	CRITICAL_REGION(m_SemTxFrames);
	EnterCriticalSection(&m_SemTxFrames);
	if(m_p2CANRxSockTx)
        	delete m_p2CANRxSockTx;
	m_p2CANRxSockTx = 0;
	LeaveCriticalSection(&m_SemTxFrames);
//	END_REGION;
}

/*
void C2CAN::InitTask(C2CAN * pThis) {
	pThis->Run();
}
*/

void C2CAN::Swap(STo2CAN_TxCANPacket & Packet) {
	for (unsigned int i=0; i<sizeof(Packet)/2; ++i)
::		Swap(Packet.Words[i]);
	}
void C2CAN::Swap(STo2CAN_RxCANPacket & Packet) {
	for (unsigned int i=0; i<sizeof(Packet)/2; ++i)
::		Swap(Packet.Words[i]);
	}
void C2CAN::Swap(STo2CAN_CANFrame & Packet) {

}

unsigned short C2CAN::CheckSum(unsigned short * data, unsigned long byte_len) {
	unsigned int i=0;
	unsigned short c=0;

	c = 0;
	for (; i<byte_len/2; ++i) {
		c += data[i];
	}

	return ~c + 1;
}
bool C2CAN::QueryEndThread() {
	bool retval = false;
//	CRITICAL_REGION(m_SemEndThrd);
	EnterCriticalSection(&m_SemEndThrd);
	retval = m_bEndThrd;
	LeaveCriticalSection(&m_SemEndThrd);
//	END_REGION;
	return retval;
}

void C2CAN::runStatic() {
	printf("Entering C2CAN::runStatic()\n");
	strcpy(m_sIP,"10.26.7.10");
	eState = eProcess;
	EnterCriticalSection(&m_SemTxFrames);
	if(m_p2CANRxSockTx == 0)
		m_p2CANRxSockTx = new TxUDPSocket(m_sIP,1217,0);
	LeaveCriticalSection(&m_SemTxFrames);
	Process();
}

void C2CAN::Run() {
	printf("Entering C2CAN::Run()\n");
	int port = 1;
	while (QueryEndThread() == false) {
		switch (eState) {
		case eFind2CAN:
			if (Find(port)) {
				eState = eProcess;
			} else {
				Sleep(250);
				port = (port==1) ? 2 : 1;
			}
			break;

		case eProcess:
			Process();
			break;
		}
		Sleep(1);
	}
}

bool C2CAN::IsProcessing() {
	return eState == eProcess;
}

bool C2CAN::Find(int port) {
	char ip[20];

	if (port == 1)
		strcpy(ip, "255.255.255.255");
	else
		strcpy(ip, "192.168.0.255");

	TxUDPSocket g_BroadcastSocketTx(ip, 32000, 1);
	RxUDPSocket g_BroadcastSocketRx(ip, 32001, 1);

	char whos_there[] = { 0xcc, 0xaa, 0x00, 0x00, 0x00, 0x00, 0x8a, 0x80, 0x00,
			0x00, 0xaa, 0xd4, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	BL_COMM_Resp rx;

	unsigned long len=0;

	g_BroadcastSocketTx.WaitForConnect();
	g_BroadcastSocketRx.WaitForConnect(10);

	printf("C2CAN::Find(%d) Tx %s\n", port, (g_BroadcastSocketTx.IsConnected()) ? "connected" : "not connected");
	printf("C2CAN::Find(%d) Rx %s\n", port, (g_BroadcastSocketRx.IsConnected()) ? "connected" : "not connected");

	g_BroadcastSocketTx.Send(whos_there, sizeof(whos_there));

	Sleep(50); 

	len = g_BroadcastSocketRx.Receive((char*)&rx, sizeof(rx));

	if (len != 72)
		return 0;
/*
::	Swap(rx.iHeader);
	::Swap(rx.iLen);
	::Swap(rx.iCommand);
	::Swap(rx.iChipID);
*/

//	printf("C2CAN::Find(%d): rx.iHeader = %x\n", port, rx.iHeader);
//	printf("C2CAN::Find(%d): rx.iLen = %x\n", port, rx.iLen);
//	printf("C2CAN::Find(%d): rx.iCommand = %x\n", port, rx.iCommand);
//	printf("C2CAN::Find(%d): rx.iChipID = %d\n", port, rx.iChipID);
	if((rx.iHeader) != 0xaacc)
	return 0;
	if((rx.iLen) != 0x34)
	return 0;
	if((rx.iCommand) != 0x8000)
	return 0;
	if((rx.iChipID) != 0)
	return 0;

	BL_COMM_RxPacketInfo *packet = (BL_COMM_RxPacketInfo *)rx.iAppPacketBytes;
/*
	::Swap(packet->uiIP[0]);
	::Swap(packet->uiIP[1]);
	::Swap(packet->uiIP[2]);
	::Swap(packet->uiIP[3]);
*/
	sprintf(m_sIP,"%d.%d.%d.%d", (packet->uiIP[0]),
			(packet->uiIP[1]),
			(packet->uiIP[2]),
			(packet->uiIP[3]) );
//	CRITICAL_REGION(m_SemTxFrames);
	EnterCriticalSection(&m_SemTxFrames);
	if(m_p2CANRxSockTx == 0)
		m_p2CANRxSockTx = new TxUDPSocket(m_sIP,1217,0);
	LeaveCriticalSection(&m_SemTxFrames);
//	END_REGION;
	printf("C2CAN::Find(%d) - found 2CAN @ %s\n", port, m_sIP);
	return 1;

}
void C2CAN::Process() {
	unsigned long len;

	printf("Entering C2CAN::Process()\n");
	RxUDPSocket ob2CANRxSockRx(m_sIP, 1218, 0);

	while (QueryEndThread() == false) 
	{
		len = sizeof(m_ucRx);
		len = ob2CANRxSockRx.Receive((char*)m_ucRx, len);

		STo2CAN_RxCANPacket * pPack = (STo2CAN_RxCANPacket * )m_ucRx;

		unsigned short int header = pPack->iSig;
		//::Swap(header);

		if(len == 0)
		{
		}
		else if(len != sizeof(STo2CAN_RxCANPacket))
		{
			m_ulBadFrame++;
		}
		else if(header != SIG_RX_CAN_FRAMES)
		{
			m_ulBadFrame++;
		}
		else
		{
			STo2CAN_RxCANPacket *pSTo2CAN_RxCANPacket = (STo2CAN_RxCANPacket * )m_ucRx;
			//Swap(*pSTo2CAN_RxCANPacket);
			if(CheckSum((unsigned short*)m_ucRx,sizeof(STo2CAN_RxCANPacket)))
			{
				m_ulBadFrame++;
			}

			else
			{
//				CRITICAL_REGION(m_SemRxFrames);
				EnterCriticalSection(&m_SemRxFrames);
				m_RxFrames.push_back(pSTo2CAN_RxCANPacket->CANFrames[0]);
				LeaveCriticalSection(&m_SemRxFrames);
//				END_REGION;
			}
		}

		ProcessHeartBeat();

		Sleep(1);
	}
	printf("Leaving C2CAN::Process()\n");
}

void C2CAN::ProcessHeartBeat()
{
//	CRITICAL_REGION(m_SemTxFrames);
	EnterCriticalSection(&m_SemTxFrames);
	if(m_ulHeartBeatTimeout)
		--m_ulHeartBeatTimeout;
	else
	{
		if(m_p2CANRxSockTx)
		{
			m_To2CAN_TxCANPacket.iOptions = 0;
			m_To2CAN_TxCANPacket.iFrameCnt = 0;
			m_To2CAN_TxCANPacket.iCRC = 0;
			m_To2CAN_TxCANPacket.iCRC = CheckSum((unsigned short*)&m_To2CAN_TxCANPacket,sizeof(m_To2CAN_TxCANPacket));
			STo2CAN_TxCANPacket out = m_To2CAN_TxCANPacket;
//			Swap(out);
	
			if(m_p2CANRxSockTx->Send((char*)&out, sizeof(out)) == 0)
			{
			}
			
			m_ulHeartBeatTimeout = 10; //250
		}
		
	}
	
//	END_REGION;
	LeaveCriticalSection(&m_SemTxFrames);
}

/**
 * This entry-point of the CANInterfacePlugin is passed a message that the driver needs to send to
 * a device on the CAN bus.
 * 
 * This function may be called from multiple contexts and must therefore be reentrant.
 * 
 * @param messageID The 29-bit CAN message ID in the lsbs.
 * @param data A pointer to a buffer containing between 0 and 8 bytes to send with the message.  May be NULL if dataSize is 0.
 * @param dataSize The number of bytes to send with the message.
 * @return Return any error code.  On success return 0.
 */
/*int debug_cntr = 0;
 double debug_start;
 short last_dur = 0;*/

int C2CAN::sendMessage(unsigned int messageID, const unsigned char *data, unsigned char dataSize) {
	int ret = 0;
	int timeout = 1000;
//	printf("C2CAN::sendMessage(%x,%u)\n",messageID,dataSize);

	//TxUDPSocket ob2CANRxSockTx(m_sIP,1217,0);

	STo2CAN_CANFrame frame = { 0 };
	frame.len_options = ((unsigned short int)dataSize)<<8 | STo2CAN_CANFrameOption_ExtendedID;
	if(messageID & 0x80000000) // check bit31
	{
		frame.len_options |= STo2CAN_CANFrameOption_Remote; // bit31 => remote frame
		messageID &= ~0x80000000; // clear bit31
	}
	frame.arbid_h = messageID>>16;
	frame.arbid_l = messageID & 0xffff;
	if (dataSize > 8)
		dataSize = 8;

	for (unsigned char i=0; i<dataSize; ++i)
		frame.data[i] = data[i];

	//wait for 2CAN to be found
	// TODO put eState accessers in critical sections
	while (timeout--) {
		if (eState == eProcess)
			break;
		Sleep(10);
	}

//	CRITICAL_REGION(m_SemTxFrames);
	EnterCriticalSection(&m_SemTxFrames);
	if(m_p2CANRxSockTx==0)
	return -10;
	TxUDPSocket &ob2CANRxSockTx = *m_p2CANRxSockTx;

	m_To2CAN_TxCANPacket.CANFrames[0] = frame;
	m_To2CAN_TxCANPacket.iOptions = 0;
	m_To2CAN_TxCANPacket.iFrameCnt = 1;
	m_To2CAN_TxCANPacket.iCRC = 0;
	m_To2CAN_TxCANPacket.iCRC = CheckSum((unsigned short*)&m_To2CAN_TxCANPacket,sizeof(m_To2CAN_TxCANPacket));
	STo2CAN_TxCANPacket out = m_To2CAN_TxCANPacket;
//	Swap(out);

	ret = ob2CANRxSockTx.Send((char*)&out, sizeof(out));

	if(ret == 0)
		m_ulHeartBeatTimeout = 10; //250
//	END_REGION;
	LeaveCriticalSection(&m_SemTxFrames);
//	printf("return C2CAN::sendMessage() = %d\n",ret);

	/*double e = GetTimeS() - c;
	 if(e > 1000)
	 e = 1000;
	 last_dur = e;*/

	return ret;
}

/**
 * This entry-point of the CANInterfacePlugin is passed buffers which should be populated with
 * any received messages from devices on the CAN bus.
 * 
 * This function is always called by a single task in the Jaguar driver, so it need not be reentrant.
 * 
 * This function is expected to block for some period of time waiting for a message from the CAN bus.
 * It may timeout periodically (returning non-zero to indicate no message was populated) to allow for
 * shutdown and unloading of the plugin.
 * 
 * @param messageID A reference to be populated with a received 29-bit CAN message ID in the lsbs.
 * @param data A pointer to a buffer of 8 bytes to be populated with data received with the message.
 * @param dataSize A reference to be populated with the size of the data received (0 - 8 bytes).
 * @return This should return 0 if a message was populated, non-0 if no message was not populated.
 */
int C2CAN::receiveMessage(unsigned int *messageID, unsigned char *data, unsigned char *dataSize) {
	bool is_empty;

//	printf("C2CAN::receiveMessage()\n");

	Sleep(1);
	
//	CRITICAL_REGION(m_SemRxFrames);
	EnterCriticalSection(&m_SemRxFrames);
	is_empty = m_RxFrames.empty();
	LeaveCriticalSection(&m_SemRxFrames);
//	END_REGION;

	if (is_empty) {
//		printf("C2CAN::receiveMessage() is_empty = true, ret = %d\n",-2);
		*messageID = 0;
		*dataSize = 0;
		return -2;
	}

//	CRITICAL_REGION(m_SemRxFrames);
	EnterCriticalSection(&m_SemRxFrames);
	const STo2CAN_CANFrame & front = m_RxFrames.front();

	*messageID = ((INT32)front.arbid_h) << 16 | front.arbid_l;
	*dataSize = front.len_options>>8;
	for(unsigned char i=0;i<*dataSize;++i)
	data[i] = front.data[i];

	m_RxFrames.pop_front();
	LeaveCriticalSection(&m_SemRxFrames);
//	END_REGION;

//	printf("C2CAN::receiveMessage() rcv'd messageID = %u,dataSize = %u,\n",
//			*messageID,*dataSize);
	return 0;
}

void Swap(char &a, char &b) {
	char temp(a);
	a = b;
	b = temp;
}
void Swap(int16 & var) {
	union {
		int16 u16;
		char bytes[2];
	} b;
	b.u16 = var;
	Swap(b.bytes[0], b.bytes[1]);
	var = b.u16;
}
void Swap(int32 & var) {
	union {
		int32 u32;
		char bytes[4];
	} b;
	b.u32 = var;
	Swap(b.bytes[0], b.bytes[3]);
	Swap(b.bytes[1], b.bytes[2]);
	var = b.u32;
}

