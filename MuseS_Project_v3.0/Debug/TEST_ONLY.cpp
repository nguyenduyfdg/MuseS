#line 1 "D:/Project/TI Contest/Workspace_new/TEST_ONLY/TEST_ONLY.ino"

#include "Energia.h"

void setup();
void loop();

#line 2
void setup() {
  
	pinMode(GREEN_LED,OUTPUT);

}

void loop() {
  
	digitalWrite(GREEN_LED,HIGH);
	delay(1000);
	digitalWrite(GREEN_LED,LOW);
	delay(1000);
}



