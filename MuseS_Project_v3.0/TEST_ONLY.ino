
#include "Beacon.h"
#include "DFPlayer_Mini_Mp3.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <PCD8544.h>


uint8_t	Play=0;
uint8_t Play_status = 0;
beacon Beacon[5];
beacon Chosen_Beacon;
String Beacon_Raw="";
String Beacon_Array[5];

//String Test_String;
static PCD8544 lcd;
void setup() {

  Serial.begin(9600);				//for Bluetooth scanner
  Serial1.begin(9600);				//for MP3 player

  //Set HM10 able to scan
  Serial.print("AT+IMME1");
  //delay(1000);
  Serial.print("AT+ROLE1");
  //delay(1000);
  //Mp3 init
  mp3_set_serial (Serial1);	//set Serial for DFPlayer-mini mp3 module
  mp3_set_volume (7);

  //LCD init
  lcd.begin(84,48);

  //Led for debug - LED LCD
  P3DIR |= 0x40;
}

void loop() {
	//run repeatedly:
	P3OUT|=(1<<6) ;//P3.6 led lcd off
	lcd.setCursor(0,0);
	lcd.print("---Welcome---");
	lcd.setCursor(0,5);
	lcd.print("Language:Eng");

	Chosen_Beacon=Set_Default(Chosen_Beacon);
//	bool CheckError=false;
	bool CheckBeacon=false;


	Serial.print("AT+DISI?");
	delay(1000);//1400
	//Wait for read buffer ready
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
	//Fill Beacon_Array with Empty_String;
	for(int i=0;i<5;i++)
		Beacon_Array[i]=Empty_String;


	//Check Start_Scan signal
	if(Serial.readString().length()>14){
//		Serial.println("YES");//Found Ble device(at least 1)
		//Divide Beacon_Raw string into specific Beacons
		//Result: An Beacon array
		lcd.setCursor(0,2);
		lcd.print("YES");
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
		delay(500);
		P3OUT&=~(1<<6);
		delay(500);
		Chosen_Beacon=RSSI_MAX(Beacon,Number_of_Beacons);
//		lcd.setCursor(0,3);
//		lcd.print("Detected Beacon");
	}
	else
		P3OUT|=(1<<6);
	Play=0;
	//Choose Music
	if(Chosen_Beacon.MAC == "88C25532CE57" && Chosen_Beacon.RSSI>-65)
		{
			Play=1;//Beacon 1
			lcd.setCursor(0,1);
			lcd.clearLine();
			lcd.print("Detected");
			lcd.setCursor(0,2);
			lcd.print("ART MUSEUM");
			if(Play_status!=Play)
					mp3_play(1);
			P3OUT&=~(1<<6);

//			lcd.setCursor(0,3);
//			lcd.print("Beacon");


		}
	else if (Chosen_Beacon.MAC=="E0E5CF267303" && Chosen_Beacon.RSSI>-65)
		{
			Play=2;//Beacon 2

		}

//	else if (Chosen_Beacon.MAC=="39DA7028EBC7" && Chosen_Beacon.RSSI>-40)
//		{
//			Play=3; // Laptop
//			Serial.println(Chosen_Beacon.MAC);
//		}

//	switch(Play){
//		case 1 :
//			if(Play_status!=Play)
//				mp3_play(1);
//
//			break;
//		case 2 :
//			if(Play_status!=Play)
//				mp3_play(2);
////			P3OUT &= ~(1<<6);
////			delay(700);
//			break;
//		case 3 :
//				mp3_stop();
//			break;
//		default:
//			mp3_stop ();
//	}
//	Serial.println(Chosen_Beacon.Fac_ID);
	Chosen_Beacon=Set_Default(Chosen_Beacon);
}





