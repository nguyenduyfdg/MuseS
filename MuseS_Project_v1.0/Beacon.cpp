/*
 * Beacon.cpp
 *
 *  Created on: Jul 18, 2017
 *      Author: Hai Dotcom
 */
#include "Beacon.h"

/***********************************************Beacon Process**************************************************/
/**Filter some unneeded characters - Result:Raw String Beacon**/
/String Beacon_Raw_Filter(String Beacon)
{
	String Beacon_Raw=Empty_String;
}
int8_t i = Beacon_Length;
uint8_t j=0;
uint8_t length=Beacon.length();
	Serial.println(Beacon);
	Serial.println(Beacon[1]);
	while (i>=0)
	{
		Beacon_Raw[i]=Beacon[length-j];
		//Serial.print(j);
		//Serial.print(" ");
		i--;
		j++;
	}
	Serial.println(Beacon_Raw);
	return Beacon_Raw;
}
/**Find OK+DISI (Begin scan) in Beacon String**/
check_DISI Check_DISI(String Beacon_Raw)
{
	check_DISI Check_DISI;
	Check_DISI.Pos = -1;
	Check_DISI.Check = false;
	Check_DISI.Pos = Beacon_Raw.lastIndexOf("OK+DISIS");
	if(Check_DISI.Pos>-1)
		Check_DISI.Check=true;
	Serial.println(Check_DISI.Pos);
	return Check_DISI;
}
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





