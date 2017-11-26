
#include "Beacon.h"
#include "DFPlayer_Mini_Mp3.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <PCD8544.h>



#define	ENG		0
#define VIE		1

#define BTN1	37
#define	BTN2	38
#define BTN3	35
#define BTN4	36

//Artifact define
#define MonaLisaID	"88C25532CE57"
#define JeromeID 	"20C38FF68CEB"

void Play_The1stIntro();
void Play_The2ndIntro();
uint8_t	Play=0;
uint8_t Play_status = 0;
uint8_t lang = ENG;
unsigned int	out_beacon = 0;
beacon Beacon[Max_NumofBeacons];
beacon Chosen_Beacon;

String Beacon_Raw;
String Beacon_Array[Max_NumofBeacons];
String TheLast_ChosenBecon="************";
String TheLast2nd="************";



//String Test_String;
static PCD8544 lcd;
void setup() {

  Serial.begin(9600);				//for Bluetooth scanner
  Serial1.begin(9600);				//for MP3 player

  //Set HM10 able to scan
  Serial.print("AT+IMME1");
  delay(1000);
  Serial.print("AT+ROLE1");
  delay(1000);
  //Mp3 init
  mp3_set_serial (Serial1);	//set Serial for DFPlayer-mini mp3 module
  mp3_set_volume (8);
  mp3_stop();

  //LCD init
  lcd.begin(84,48);

  //Led for debug - LED LCD
  P3DIR |= 0x40;

  //Button Intterupt
  attachInterrupt(BTN1, Play_The2nd_INT, FALLING);
  attachInterrupt(BTN2, Change_Lang, FALLING);
//  attachInterrupt(BTN4,INT2, FALLING);
//  attachInterrupt(BTN4, Play_4, FALLING);

  //Default Language
  lcd.setCursor(0,0);
  lcd.print("__Welcome U__");
  lcd.setCursor(0,5);
  lcd.print("Lang: ENG");
}

void loop() {
	//run repeatedly:
	P3OUT|=(1<<6) ;//P3.6 led lcd off

	Chosen_Beacon=Set_Default(Chosen_Beacon);
//	bool CheckError=false;
	bool CheckBeacon=false;


	//Wait for read buffer ready
	if(Serial.available()==0){
		Serial.print("AT+DISI?");
	}
	Beacon_Raw =Serial.readString();

	//Fill Beacon_Array with Empty_String;
	for(int i=0;i<Max_NumofBeacons;i++)
		Beacon_Array[i]=Empty_String;

	//Check Start_Scan signal
	if(Beacon_Raw.lastIndexOf("OK+DISIS")>nope && Beacon_Raw.length()>Beacon_Length)
	{
		Beacon_Catcher(Beacon_Raw);
	}

	//Print Beacon_Array after filter
	//Also convert to beacon struct (Beacon ID,RSSI, ...)
	uint8_t i=0;
	uint8_t Number_of_Beacons=0;
	while(Beacon_Array[i]!=Empty_String && i<Max_NumofBeacons){
		if (Beacon_Array[i].length()>Beacon_Length){
			//Check error
			break;
		}
		//If it's a beacon, convert to Beacon struct
		else if(Check_Beacon(Beacon_Array[Number_of_Beacons]) == true)
		{
			CheckBeacon=true;
			//Convert to beacon struct
			Beacon[Number_of_Beacons]=Beacon_Details(Beacon_Array[Number_of_Beacons]);
			Number_of_Beacons++;
		}
		i++;//Next
	}
	//Process RSSI value
	if(CheckBeacon==true)
	{
		if(Number_of_Beacons>1)
		{
			Sort_RSSI(Beacon,Number_of_Beacons-1);
			lcd.setCursor(0,1);
			lcd.clearLine();
			lcd.print("More than one");
		}
		else{
			lcd.setCursor(0,1);
			lcd.clearLine();
			lcd.print("Just one");
			lcd.setCursor(0,3);
			lcd.clearLine();
		}
		Chosen_Beacon=Beacon[0];
		Play=0;
		//Play intro of the nearest beacon
		if(Chosen_Beacon.RSSI>-75)
			Play_The1stIntro();
		//Print LCD the 2nd beacon
		if(Beacon[1].RSSI>-75 && Number_of_Beacons>1)
		{
			lcd.setCursor(0,3);
			lcd.clearLine();
			if(Beacon[1].MAC != TheLast_ChosenBecon)
			{
				TheLast2nd=Beacon[1].MAC;
				Play_The2ndIntro();
			}
			else
			{
				lcd.setCursor(0,3);
				lcd.clearLine();
			}
		}
	 }
}

void Play_The1stIntro(){
	if(Chosen_Beacon.MAC==MonaLisaID)
			{
				//out_beacon = 0;
				if(lang == ENG)
					Play=1;//Beacon 1
				else
					Play=2;
				lcd.setCursor(0,2);
				lcd.clearLine();
				lcd.print("Mona Lisa");
				if(Play_status!=Play)
					mp3_play(Play);
				P3OUT &=~(1<<6);
			}
		else if (Chosen_Beacon.MAC==JeromeID)
			{
				//out_beacon = 0;
				if(lang == ENG)
					Play=3;//Beacon 1
				else
					Play=4;
				lcd.setCursor(0,2);
				lcd.clearLine();
				lcd.print("Jerome");
				if(Play_status!=Play)
					mp3_play(Play);
				P3OUT &=~(1<<6);
			}
		TheLast_ChosenBecon=Chosen_Beacon.MAC;
		Chosen_Beacon=Set_Default(Chosen_Beacon);
}

void Play_The2nd_INT(){
	if(TheLast2nd==MonaLisaID)
	{
		if(lang == ENG)
			Play=1;
		else
			Play=2;
		lcd.setCursor(0,2);
		lcd.clearLine();
		lcd.print("Mona Lisa");
		if(Play_status!=Play)
			mp3_play(Play);
		P3OUT &=~(1<<6);
		lcd.setCursor(0,3);
		lcd.clearLine();
	}
	else if (TheLast2nd==JeromeID)
	{
		if(lang == ENG)
			Play=3;
		else
			Play=4;
		lcd.setCursor(0,2);
		lcd.clearLine();
		lcd.print("Jerome");
		if(Play_status!=Play)
			mp3_play(Play);
		P3OUT &=~(1<<6);
		lcd.setCursor(0,3);
		lcd.clearLine();
	}
}

void Play_The2ndIntro(){
	if(Beacon[1].MAC==MonaLisaID)
	{
		lcd.print("Next:Mona Lisa");
	}
	else if (Beacon[1].MAC==JeromeID)
	{
		lcd.print("Next:Jerome");
	}
}
void Change_Lang(){
	lcd.setCursor(0,5);
	if(lang == 0){
		lcd.print("Lang: VIE");
		lang = VIE;
	}
	else
	{
		lcd.print("Lang: ENG");
		lang = ENG;
	}

}
void INT2(){
	lcd.setCursor(0,4);
	lcd.print("TEST_INT2");
}

