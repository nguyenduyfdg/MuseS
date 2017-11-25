#include "Beacon.h"
#include "DFPlayer_Mini_Mp3.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>



uint8_t	play=0;
uint8_t play_status=0;
beacon Beacon[5];
beacon Chosen_Beacon;
String Beacon_Raw;
String Beacon_Array[5];

//String Test_String;

void setup() {

  Serial.begin(9600);				//for MP3
  Serial1.begin(9600);				//for Bluetooth scanner P3.4 RX

  //Set HM10 able to scan
  Serial1.print("AT+IMME1");
  Serial1.print("AT+ROLE1");

  //Mp3 init
  mp3_set_serial (Serial);	//set Serial for DFPlayer-mini mp3 module
  mp3_set_volume (5);

  //Led for debug
  P1DIR |= 0x01;
}

void loop() {
	//run repeatedly:
	P1OUT &= 0x01;//LED off
	Chosen_Beacon=Set_Default(Chosen_Beacon);
//	bool CheckError=false;
	bool CheckBeacon=false;
//	mp3_stop ();

	//Wait for read buffer ready
	if(Serial1.available()==0){
		delay(1400);//1400
		Serial1.print("AT+DISI?");
//		Serial.println("Scan");
	}

	//Fill Beacon_Array with Empty_String;
	for(int i=0;i<5;i++)
		Beacon_Array[i]=Empty_String;
	Beacon_Raw =Serial1.readString();
//	Serial.println(Beacon_Raw);

	//Check Start_Scan signal
	if(Beacon_Raw.lastIndexOf("OK+DISIS")>nope && Beacon_Raw.length()>Beacon_Length){
//		Serial.println("YES");//Found Ble device(at least 1)
		//Divide Beacon_Raw string into specific Beacons
		//Result: An Beacon array
		Beacon_Catcher(Beacon_Raw);
	}

	//Print Beacon_Array after filter
	//Also convert to beacon struct (Beacon ID,RSSI, ...)
	uint8_t i=0;
	uint8_t Number_of_Beacons=0;
	while(Beacon_Array[i]!=Empty_String && i<Max_NumofBeacons){
		if (Beacon_Array[i].length()>Beacon_Length){
			//Check error
//			CheckError=true;
			break;
		}
		else{
			//If it's a beacon, convert to Beacon struct
			if(Check_Beacon(Beacon_Array[Number_of_Beacons]) == true)
				{
					CheckBeacon=true;
//					Serial.println(Beacon_Array[Number_of_Beacons]);
					//Convert to beacon struct
					Beacon[Number_of_Beacons]=Beacon_Details(Beacon_Array[Number_of_Beacons]);
					Number_of_Beacons++;
				}
		}
		i++;//Next
	}
	//Find RSSI Max
	if (CheckBeacon==true)
	{
		P1OUT |= 0x00;//LED on
		delay(500);
		Chosen_Beacon=RSSI_MAX(Beacon,Number_of_Beacons);
//		Serial.println(Chosen_Beacon.Fac_ID);
	}
	else
		P1OUT &= 0x01;//LED off

	play=0;
	if(Chosen_Beacon.Fac_ID == "4C000215")
		play=1;
	switch(play){
		case 1 :
			if(play_status!=play)
				mp3_play (1);
			break;
//		default:
//			mp3_stop ();
	}
//	Serial.println(Chosen_Beacon.Fac_ID);
	Chosen_Beacon=Set_Default(Chosen_Beacon);
}





