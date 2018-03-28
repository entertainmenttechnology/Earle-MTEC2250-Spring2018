//define the two direction logic pins and the speed / Motor1_PWM pin
const int Motor1_A = 5;
const int Motor1_B = 4;
const int Motor1_PWM = 6;

const int Motor2_A = 8;
const int Motor2_B = 7;
const int Motor2_PWM = 9;

int motorSpeed = 255;

void setup() {
  Serial.begin(38400);

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

    // if we receive a '0' then brake
    if (val == 0) {
      M1Brake();
      M2Brake();
    }

    // if we receive a '1' then go forward
    if (val == 1) {
      M1Forward(motorSpeed);
      M2Forward(motorSpeed);
    }

    // back
    if (val == 2) {
      M1Backward(motorSpeed);
      M2Backward(motorSpeed);
    }

    // left
    if (val == 3) {
      M1Forward(motorSpeed);
      M2Backward(motorSpeed);
    }
    
    // right
    if (val == 4) {
      M1Backward(motorSpeed);
      M2Forward(motorSpeed);
    }
  }

  // // or automate it
  //  M1Forward(motorSpeed);
  //  M2Forward(motorSpeed);
  //
  //  delay(500);
  //
  //  M1Brake();
  //  M2Brake();
  //
  //  delay(500);
  //
  //  M1Backward(motorSpeed);
  //  M2Backward(motorSpeed);
  //
  //  delay(500);
  //
  //  M1Brake();
  //  M2Brake();
  //
  //  delay(500);
}

