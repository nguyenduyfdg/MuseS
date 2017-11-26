////Include
#include <stdio.h>
#include <stdlib.h>
#include "Energia.h"
#include <WString.h>

//
////Define
#define	Empty_String	"**********************************************************************"//70 characters
#define	Beacon_Length	70
#define nope			-1
#define Next_Part		i=i+2;
#define	Max_NumofBeacons	6


typedef struct beacon {
	String 	Fac_ID;
	String 	Beacon_ID;
	String 	Major;
	String 	Minor;
	int		Mea_Pow;
	String 	MAC;
	int 	RSSI;
} beacon;
////typedef struct check_DISI{
////	int Pos;
////	boolean Check;
////} check_DISI;
//
////Prototype
void Beacon_Catcher(String Beacon_Raw);
beacon Beacon_Details(String Beacon_Fixed);
beacon Set_Default(beacon Beacon);
beacon RSSI_MAX(beacon Beacon[],uint8_t Number_of_Beacons);
void Sort_RSSI(beacon Beacon[],uint8_t Number_of_Beacons);
bool Check_Beacon(String Beacon_Fixed);
