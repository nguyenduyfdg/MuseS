/*
 * Beacon.cpp
 *
 *  Created on: Jul 19, 2017
 *      Author: Hai Dotcom
 */


#include "Beacon.h"
extern String Beacon_Array[Max_NumofBeacons];

//Filter all unneeded characters
//Result: Array of Beacon-string
void Beacon_Catcher(String Beacon_Raw)
{
	int k=0;

	for(unsigned int i = Beacon_Raw.lastIndexOf("OK+DISIS")+7; i<Beacon_Raw.length(); i++){
			for(int j = 0;j<Beacon_Length;j++){
				Beacon_Array[k][j]=Beacon_Raw[Beacon_Raw.lastIndexOf("OK+DISC:")+8+j];
			}
			if(Beacon_Raw.lastIndexOf("OK+DISC:")>nope){
				Beacon_Raw.remove(Beacon_Raw.lastIndexOf("OK+DISC:"),78);
				k++;
			}
			else
				break;
		}
}

//Init Beacon struct
beacon Set_Default(beacon Beacon)
{
	Beacon.Fac_ID="********";								//8
	Beacon.Beacon_ID="*******************************";		//32
	Beacon.Major="****";									//4
	Beacon.Minor="****";									//4
	Beacon.Mea_Pow=-1;
	Beacon.MAC="************";								//12
	Beacon.RSSI=0000;
//	Serial.println("Set default");
	return Beacon;
}

//Separate each part of Beacon String
//Result: Beacon Stuct, include: Beacon_ID, RSSI, ...
beacon Beacon_Details(String Beacon_Fixed)
{
	beacon Beacon;
	Beacon=Set_Default(Beacon);

	unsigned int i=0;

	//Fac_ID
	if(i<=7)
		for(int j=0;j<8;j++){
			Beacon.Fac_ID[j]=Beacon_Fixed[i];
			if(j<7)
				i++;
		}

	Next_Part
	//Beacon_ID
	if(i>=9 && i<=40)
		for(int j=0;j<32;j++){
			Beacon.Beacon_ID[j]=Beacon_Fixed[i];
			if(j<31)
				i++;
		}

	Next_Part
	//Major
	if(i>=42 && i<=45)
		for(int j=0;j<4;j++){
			Beacon.Major[j]=Beacon_Fixed[i];
			if(j<3)
				i++;
		}

	i++;// go to minor
	//Minor
	if(i>=46 && i<=49)
		for(int j=0;j<4;j++){
			Beacon.Minor[j]=Beacon_Fixed[i];
			if(j<3)
				i++;
		}

	i++;//go to mea_po
	//Measure Power
	if(i>=50 && i<=51){
		String Mea_Pow="**";
		for(int j=0;j<2;j++){
			Mea_Pow[j]=Beacon_Fixed[i];
			if(j<1)
				i++;
		}
		Beacon.Mea_Pow=Mea_Pow.toInt();
	}

	Next_Part
	//MAC
	if(i>=53 && i<=64)
		for(int j=0;j<12;j++){
			Beacon.MAC[j]=Beacon_Fixed[i];
			if(j<11)
				i++;
		}

	Next_Part
	//RSSI
	if(i>=66 && i<=69){
		String RSSI="****";
		for(int j=0;j<4;j++){
			RSSI[j]=Beacon_Fixed[i];
			if (j<3)
				i++;
		}
		Beacon.RSSI=RSSI.toInt();
	}
	return Beacon;
}

//Find the nearest node bluetooth (RSSI MAX)
beacon RSSI_MAX(beacon Beacon[],uint8_t Number_of_Beacons){
	beacon Beacon_MAX=Beacon[0];
	for (int i=1; i<Number_of_Beacons; i++){
		if(Beacon_MAX.RSSI<Beacon[i].RSSI)
			Beacon_MAX=Beacon[i];
	}
	return Beacon_MAX;
}

void Sort_RSSI(beacon Beacon[],uint8_t Number_of_Beacons){
	beacon temp=Beacon[0];
	for (int i=0;i<Number_of_Beacons-1;i++){
		for(int j=i+1;j<Number_of_Beacons;j++){
			if(Beacon[i].RSSI<Beacon[j].RSSI){
				temp=Beacon[i];
				Beacon[i]=Beacon[j];
				Beacon[j]=temp;
			}
		}
	}
}
//Check Beacon
bool Check_Beacon(String Beacon_Fixed){
	bool Check_Beacon=false;
	String Fac_ID="********";
	//Fac_ID
	for(int j=0;j<8;j++){
		Fac_ID[j]=Beacon_Fixed[j];
	}
	if(Fac_ID!="00000000")
		Check_Beacon=true;
	return Check_Beacon;
}

