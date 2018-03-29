#include <SoftwareSerial.h>

#define rxPin 3
#define txPin 2

SoftwareSerial bluetooth(rxPin, txPin); // RX, TX
int inByte = 255; 

void setup() {
  // set up both serials
  Serial.begin(9600);   
  bluetooth.begin(9600);
}

void loop(){
  while(bluetooth.available()){
    inByte = bluetooth.read();
    Serial.println(inByte);
  }

  while(Serial.available()){
   inByte = Serial.read();
   bluetooth.println(inByte);
  }

  digitalWrite(13, HIGH);
  delay(inByte);
  digitalWrite(13, LOW);
  delay(inByte);
}
