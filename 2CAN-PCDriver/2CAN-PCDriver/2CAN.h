#ifndef C2CAN__H_
#define C2CAN__H_

#include "CANInterfacePlugin.h"
#include "TxUDPSocket.h"
#include "RxUDPSocket.h"
#include "2CANDataObjects.h"
//#include <WinSock2.h>
#include <deque>
#include <process.h>

#define iC2CAN_OK					(0)
#define iC2CAN_ErrorTooManyJags		(1)
#define iC2CAN_ErrorInvalidID		(2)
#define iC2CAN_ErrorNoComm			(3)

typedef unsigned int C2CAN_Error_t;
typedef unsigned int JagID_t;

class C2CAN : public CANInterfacePlugin
{
	public:
		C2CAN();
		~C2CAN();

		int sendMessage(unsigned int messageID,
                                        const unsigned char *data,
                                        unsigned char dataSize);
		int receiveMessage(unsigned int *messageID,
                                         unsigned char *data,
                                         unsigned char *dataSize);

		void Discard();
		bool Find(int port);
		void Run();
		bool IsProcessing();
		void runStatic();
	private:

//		static void InitTask(C2CAN * pThis);
		static unsigned __stdcall ThreadStaticEntryPoint(void * pThis)
		{
			C2CAN *pThread = (C2CAN *)pThis;
			pThread->runStatic();		// change to call Run() instead to find 2CAN instead of using static IP
			return 1;
		}
		
		unsigned short CheckSum(unsigned short * data,unsigned long len);

		void Swap(STo2CAN_CANFrame & canFrame);
		void Swap(STo2CAN_TxCANPacket & Packet);
		void Swap(STo2CAN_RxCANPacket & Packet);

		bool QueryEndThread();

//		bool Find(int port);
		void Process();
		void ProcessHeartBeat();
		
//		CTask m_obTask;
		WSADATA m_wsaData;
		HANDLE m_thrHandle;
		unsigned m_thrID;
		CRITICAL_SECTION m_SemEndThrd;
		CRITICAL_SECTION m_SemRxFrames;
		CRITICAL_SECTION m_SemTxFrames;

		STo2CAN_TxCANPacket m_To2CAN_TxCANPacket;

//		SEM_ID m_SemEndThrd;
		bool m_bEndThrd;

		unsigned char m_ucRx[1536]; // only used in thread

//		SEM_ID m_SemRxFrames;
		std::deque<STo2CAN_CANFrame> m_RxFrames;

//		SEM_ID m_SemTxFrames;
		std::deque<STo2CAN_CANFrame> m_TxFrames;
		
		unsigned long m_ulBadFrame;
		unsigned long m_ulHeartBeatTimeout;
		
		enum
		{
			eDoNothing,
			eFind2CAN,
			eProcess,
		}eState;
		
		char m_sIP[20];
		
		TxUDPSocket *m_p2CANRxSockTx;
};



#pragma pack(push,2)

#define BL_COMM_CMD_POLL				(0)


typedef struct _BL_COMM_RxPacketInfo
{
	#define BL_COMM_RxPacketInfo_Status_InBootloader	(1)
	#define BL_COMM_RxPacketInfo_Status_InApplication	(2)
	unsigned short int iStatus;
	
	unsigned short int iFirmwareMajor;
	unsigned short int iFirmwareMinor;
	
	unsigned short int iBootloaderMajor;
	unsigned short int iBootloaderMinor;
	
	unsigned short int iSerialNo[4];
	
	unsigned short int iManDay;
	unsigned short int iManMonth;
	unsigned short int iManYear;
	
	unsigned short int iHardwareRevMajor;
	unsigned short int iHardwareRevMinor;
	
	unsigned short int iChipID;
	unsigned short int iLastError;

	unsigned short int uiIP[6];
	
	unsigned short int iReserved[4];
}BL_COMM_RxPacketInfo;

typedef struct _BL_COMM_Cmd
{
	unsigned short int iHeader;
	unsigned short int iLen;
	unsigned short int iCommand;
	unsigned short int iSeed;
	unsigned short int iChipID;
	unsigned short int iChkSum;
	unsigned short int iReserved[3];
	union
	{
		unsigned char iAppPacketBytes[64];
	};
}BL_COMM_Cmd;



typedef struct _BL_COMM_Resp
{
	unsigned short int iHeader;
	unsigned short int iLen;
	unsigned short int iCommand;
	unsigned short int iSeed;
	unsigned short int iChipID;
	unsigned short int iChkSum;
	unsigned short int iRespCode;
	unsigned short int iReserved[3];
	union
	{
		unsigned char iAppPacketBytes[64];
		BL_COMM_RxPacketInfo info;
	};
}BL_COMM_Resp;


#pragma pack(pop)


#endif // C2CAN__H_


