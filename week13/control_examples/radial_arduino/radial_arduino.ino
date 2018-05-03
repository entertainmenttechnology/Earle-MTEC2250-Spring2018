#include <SoftwareSerial.h>
#define rxPin 3
#define txPin 2
SoftwareSerial bluetooth(rxPin, txPin); // RX, TX
int inByte;

//define the two direction logic pins and the speed / Motor1_PWM pin
const int Motor1_A = 5;
const int Motor1_B = 4;
const int Motor1_PWM = 6;

const int Motor2_A = 8;
const int Motor2_B = 7;
const int Motor2_PWM = 9;

int motorSpeed = 255;

void setup() {
  // set up both serials
  //Serial.begin(9600);
  bluetooth.begin(9600);

  //set all pins as output
  pinMode(Motor1_A, OUTPUT);
  pinMode(Motor1_B, OUTPUT);
  pinMode(Motor1_PWM, OUTPUT);

  pinMode(Motor2_A, OUTPUT);
  pinMode(Motor2_B, OUTPUT);
  pinMode(Motor2_PWM, OUTPUT);
}

void loop() {

  while (bluetooth.available()) {

    String val = bluetooth.readStringUntil(10);
    // read until new line ^

    //Serial.println(val);  // debug

    // incoming values are separated by a comma, so...
    int commaIndex = val.indexOf(',');
    String firstValue = val.substring(0, commaIndex);
    String secondValue = val.substring(commaIndex + 1);

    int m1 = firstValue.toInt();
    int m2 = secondValue.toInt();

    if (m1 < 0) {
      M1Backward(m1 * -2);
    }
    if (m2 < 0) {
      M2Backward(m2 * -2);
    }
    if (m1 > 0) {
      M1Forward(m1 * 2);
    }
    if (m2 > 0) {
      M2Forward(m1 * 2);
    }
    if (m1 == 0) {
      M1Brake();
    }
    if (m2 == 0) {
      M2Brake();
    }
  }
}

