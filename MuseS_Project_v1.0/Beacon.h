//Include
#include <stdio.h>
#include <stdlib.h>
//#include "Energia.h"
#include <WString.h>

//Define
#define	Empty_String	"***********************************************************************"; //70 characters
#define	Beacon_Length	70
#define nope			-1

//Variables
//String 	Beacon_Array[10];

//Struct
typedef struct beacon {
	char 	Fac_ID[8];
	char 	Beacon_ID[32];
	char 	Major[4];
	char 	Minor[4];
	int		Mea_Pow;
	char 	MAC[12];
	int 	RSSI;
} beacon;
//typedef struct check_DISI{
//	int Pos;
//	boolean Check;
//} check_DISI;

//Prototype
//String Beacon_Raw_Filter(String Beacon_Raw);
//check_DISI Check_DISI(String Beacon_Raw);
void Beacon_Catcher(String Beacon_Raw);
beacon Set_Default(beacon Beacon);

