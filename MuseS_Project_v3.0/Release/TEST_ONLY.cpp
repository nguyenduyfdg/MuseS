#line 1 "D:/Project/TI Contest/Workspace_new/MuseS_v3.0/TEST_ONLY.ino"

#include "Beacon.h"
#include "DFPlayer_Mini_Mp3.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <PCD8544.h>


#include "Energia.h"

void setup();
void loop();

#line 10
uint8_t	Play=0;
uint8_t Play_status = 0;
beacon Beacon[5];
beacon Chosen_Beacon;
String Beacon_Raw="";
String Beacon_Array[5];


static PCD8544 lcd;
void setup() {

  Serial.begin(9600);				
  Serial1.begin(9600);				

  
  Serial.print("AT+IMME1");
  
  Serial.print("AT+ROLE1");
  
  
  mp3_set_serial (Serial1);	
  mp3_set_volume (7);

  
  lcd.begin(84,48);

  
  P3DIR |= 0x40;
}

void loop() {
	
	P3OUT|=(1<<6) ;
	lcd.setCursor(0,0);
	lcd.print("---Welcome---");
	lcd.setCursor(0,5);
	lcd.print("Language:Eng");

	Chosen_Beacon=Set_Default(Chosen_Beacon);

	bool CheckBeacon=false;


	Serial.print("AT+DISI?");
	delay(1000);
	
	if(Serial.available()==0){

		lcd.setCursor(0,1);
		lcd.print("Scanning...");

	}
	else
	{
		lcd.setCursor(0,1);
		lcd.print("Available !");

	}
	Beacon_Raw=Serial.readString();
	
	for(int i=0;i<5;i++)
		Beacon_Array[i]=Empty_String;


	
	if(Serial.readString().length()>14){

		
		
		lcd.setCursor(0,2);
		lcd.print("YES");
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
		delay(500);
		P3OUT&=~(1<<6);
		delay(500);
		Chosen_Beacon=RSSI_MAX(Beacon,Number_of_Beacons);


	}
	else
		P3OUT|=(1<<6);
	Play=0;
	
	if(Chosen_Beacon.MAC == "88C25532CE57" && Chosen_Beacon.RSSI>-65)
		{
			Play=1;
			lcd.setCursor(0,1);
			lcd.clearLine();
			lcd.print("Detected");
			lcd.setCursor(0,2);
			lcd.print("ART MUSEUM");
			if(Play_status!=Play)
					mp3_play(1);
			P3OUT&=~(1<<6);





		}
	else if (Chosen_Beacon.MAC=="E0E5CF267303" && Chosen_Beacon.RSSI>-65)
		{
			Play=2;

		}


























	Chosen_Beacon=Set_Default(Chosen_Beacon);
}








