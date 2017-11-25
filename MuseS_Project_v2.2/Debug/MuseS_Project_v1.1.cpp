#line 1 "D:/Project/TI Contest/Workspace_new/MuseS_Project_v2.2/MuseS_Project_v1.1.ino"
#include "Beacon.h"
#include "DFPlayer_Mini_Mp3.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>



#include "Energia.h"

void setup();
void loop();

#line 9
uint8_t	play=0;
uint8_t play_status=0;
beacon Beacon[5];
beacon Chosen_Beacon;
String Beacon_Raw;
String Beacon_Array[5];



void setup() {

  Serial.begin(9600);				
  Serial1.begin(9600);				

  
  Serial1.print("AT+IMME1");
  Serial1.print("AT+ROLE1");

  
  mp3_set_serial (Serial);	
  mp3_set_volume (5);

  
  P1DIR |= 0x01;
}

void loop() {
	
	P1OUT &= 0x01;
	Chosen_Beacon=Set_Default(Chosen_Beacon);

	bool CheckBeacon=false;


	
	if(Serial1.available()==0){
		delay(1400);
		Serial1.print("AT+DISI?");

	}

	
	for(int i=0;i<5;i++)
		Beacon_Array[i]=Empty_String;
	Beacon_Raw =Serial1.readString();


	
	if(Beacon_Raw.lastIndexOf("OK+DISIS")>nope && Beacon_Raw.length()>Beacon_Length){

		
		
		Beacon_Catcher(Beacon_Raw);
	}

	
	
	uint8_t i=0;
	uint8_t Number_of_Beacons=0;
	while(Beacon_Array[i]!=Empty_String && i<Max_NumofBeacons){
		if (Beacon_Array[i].length()>Beacon_Length){
			

			break;
		}
		else{
			
			if(Check_Beacon(Beacon_Array[Number_of_Beacons]) == true)
				{
					CheckBeacon=true;

					
					Beacon[Number_of_Beacons]=Beacon_Details(Beacon_Array[Number_of_Beacons]);
					Number_of_Beacons++;
				}
		}
		i++;
	}
	
	if (CheckBeacon==true)
	{
		P1OUT |= 0x00;
		delay(500);
		Chosen_Beacon=RSSI_MAX(Beacon,Number_of_Beacons);

	}
	else
		P1OUT &= 0x01;

	play=0;
	if(Chosen_Beacon.Fac_ID == "4C000215")
		play=1;
	switch(play){
		case 1 :
			if(play_status!=play)
				mp3_play (1);
			break;


	}

	Chosen_Beacon=Set_Default(Chosen_Beacon);
}








