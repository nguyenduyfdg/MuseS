#include "Beacon.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

//beacon Beacon[5];
//check_DISI Check_DISI_va;
String Beacon_Raw;
//extern String Beacon_Array[10];

String Test_String;

void setup() {
  Serial.begin(9600);				//for feedback
  Serial1.begin(9600);				//for Bluetooth scanner
  Serial.println("-Beacon Scanner-");
  //Set HM10 able to scan
  Serial1.print("AT+IMME1");
  Serial1.print("AT+ROLE1");
}

void loop() {
	//run repeatedly:
	if(Serial1.available()==0){
		delay(1400);
		Serial1.print("AT+DISI?");
	}
	uint8_t i=0;
//	Serial.println(Serial1.readString());
	Beacon_Raw =Serial1.readString();
	if(Beacon_Raw.lastIndexOf("OK+DISIS")>nope){
		Beacon_Catcher(Beacon_Raw);
	}
	while(Beacon_Array[i]!=NULL){
		Serial.println(Beacon_Array[i]);
		i++;
	}
	//Check_DISI_va = Check_DISI(Beacon_Raw);
	Check_DISI_va="xxxxOK+DISI";
	//if (Check_DISI_va.Check == true);

	Test_String="01234OK+DISI";
	Serial.print(Test_String.lastIndexOf("OK+DISI"));
}





