//define the two direction logic pins and the speed / Motor1_PWM pin
const int Motor1_A = 5;
const int Motor1_B = 4;
const int Motor1_PWM = 6;

const int Motor2_A = 8;
const int Motor2_B = 7;
const int Motor2_PWM = 9;

void setup() {
  Serial.begin(9600);

  //set all pins as output
  pinMode(Motor1_A, OUTPUT);
  pinMode(Motor1_B, OUTPUT);
  pinMode(Motor1_PWM, OUTPUT);

  pinMode(Motor2_A, OUTPUT);
  pinMode(Motor2_B, OUTPUT);
  pinMode(Motor2_PWM, OUTPUT);
}

void loop() {

  if (Serial.available() > 0) {

    int val = Serial.read();

    if (val == 0) {
      M1Brake();
      M2Brake();
    }

    if (val == 1) {
      M1Forward(200);
      M2Forward(200);
    }

    if (val == 2) {
      M1Backward(200);
      M2Backward(200);
    }
  }

  //  M1Forward(200);
  //  M2Forward(200);
  //
  //  delay(500);
  //
  //  M1Brake();
  //  M2Brake();
  //
  //  delay(500);
  //
  //  M1Backward(200);
  //  M2Backward(200);
  //
  //  delay(500);
  //
  //  M1Brake();
  //  M2Brake();
  //
  //  delay(500);
}

