#line 1 "D:/Project/TI Contest/Workspace_new/MuseS_Project_v1.0/MuseS_Project_v1.0.ino"
#include "Beacon.h"
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>



#include "Energia.h"

void setup();
void loop();

#line 8
String Beacon_Raw;


String Test_String;

void setup() {
  Serial.begin(9600);				
  Serial1.begin(9600);				
  Serial.println("-Beacon Scanner-");
  
  Serial1.print("AT+IMME1");
  Serial1.print("AT+ROLE1");
}

void loop() {





















}








