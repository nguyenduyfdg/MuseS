#line 1 "D:/Project/TI Contest/Workspace_new/MuseS_v4.1/TEST_ONLY.ino"

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


#define MonaLisaID	"88C25532CE57"
#define JeromeID 	"20C38FF68CEB"

#include "Energia.h"

void setup();
void loop();
void Play_The2nd_INT();
void Change_Lang();
void INT2();

#line 23
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




static PCD8544 lcd;
void setup() {

  Serial.begin(9600);				
  Serial1.begin(9600);				

  
  Serial.print("AT+IMME1");
  delay(1000);
  Serial.print("AT+ROLE1");
  delay(1000);
  
  mp3_set_serial (Serial1);	
  mp3_set_volume (8);
  mp3_stop();

  
  lcd.begin(84,48);

  
  P3DIR |= 0x40;

  
  attachInterrupt(BTN1, Play_The2nd_INT, FALLING);
  attachInterrupt(BTN2, Change_Lang, FALLING);



  
  lcd.setCursor(0,0);
  lcd.print("__Welcome U__");
  lcd.setCursor(0,5);
  lcd.print("Lang: ENG");
}

void loop() {
	
	P3OUT|=(1<<6) ;

	Chosen_Beacon=Set_Default(Chosen_Beacon);

	bool CheckBeacon=false;


	
	if(Serial.available()==0){
		Serial.print("AT+DISI?");
	}
	Beacon_Raw =Serial.readString();

	
	for(int i=0;i<Max_NumofBeacons;i++)
		Beacon_Array[i]=Empty_String;

	
	if(Beacon_Raw.lastIndexOf("OK+DISIS")>nope && Beacon_Raw.length()>Beacon_Length)
	{
		Beacon_Catcher(Beacon_Raw);
	}

	
	
	uint8_t i=0;
	uint8_t Number_of_Beacons=0;
	while(Beacon_Array[i]!=Empty_String && i<Max_NumofBeacons){
		if (Beacon_Array[i].length()>Beacon_Length){
			
			break;
		}
		
		else if(Check_Beacon(Beacon_Array[Number_of_Beacons]) == true)
		{
			CheckBeacon=true;
			
			Beacon[Number_of_Beacons]=Beacon_Details(Beacon_Array[Number_of_Beacons]);
			Number_of_Beacons++;
		}
		i++;
	}
	
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
		
		if(Chosen_Beacon.RSSI>-75)
			Play_The1stIntro();
		
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
			}
		else if (Chosen_Beacon.MAC==JeromeID)
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




