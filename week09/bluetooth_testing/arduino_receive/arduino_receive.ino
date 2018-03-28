#include <SoftwareSerial.h>

// Rx on the Arduino should be connected to Tx on the BT module
// and vice versa.
int bluetoothTx = 2;
int bluetoothRx = 3;

SoftwareSerial bluetooth(bluetoothRx, bluetoothTx);

int ledPin = 13;

int inByte = 1; // store data from bluetooth serial

void setup() {

  Serial.begin(9600);

  bluetooth.begin(38400); // Default communication rate of the Bluetooth module

  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

}

void loop() {
  while (bluetooth.available() > 0) { // Checks whether data is comming from the serial port
    inByte = bluetooth.read(); // Reads the data from the serial port

    Serial.println(inByte);

    
  }

  digitalWrite(ledPin, HIGH);
  delay(inByte * 2);
  digitalWrite(ledPin, LOW);
  delay(inByte * 2);

  

}
