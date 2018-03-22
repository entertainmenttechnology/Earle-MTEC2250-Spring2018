// print the status of a button via wireless bluetooth

#include <SoftwareSerial.h>

// Rx on the Arduino should be connected to Tx on the BT module
// and vice versa.
int bluetoothTx = 3;  
int bluetoothRx = 2;  

SoftwareSerial bluetooth(bluetoothRx, bluetoothTx);

int buttonPin = 12;

void setup() {
  Serial.begin(9600);
  pinMode(13, OUTPUT);

  // startup bluetooth
  bluetooth.begin(115200);  // The Bluetooth Mate defaults to 115200bps
  bluetooth.print("$");  // Print three times individually
  bluetooth.print("$");
  bluetooth.print("$");  // Enter command mode
  delay(100);  // Short delay, wait for the Mate to send back CMD
  bluetooth.println("U,9600,N");  // Temporarily Change the baudrate to 9600, no parity
  bluetooth.begin(9600);  // Start bluetooth serial at 9600

  pinMode(buttonPin, INPUT_PULLUP);
}

int incomingByte = 0;

void loop() {

  //bluetooth.println(digitalRead(8));
  bluetooth.write(digitalRead(8));

  delay(50);
}
