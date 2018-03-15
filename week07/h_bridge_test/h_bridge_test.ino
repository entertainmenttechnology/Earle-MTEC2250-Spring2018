//define the two direction logic pins and the speed / Motor1_PWM pin
const int Motor1_A = 5;
const int Motor1_B = 4;
const int Motor1_PWM = 6;

const int Motor2_A = 8;
const int Motor2_B = 7;
const int Motor2_PWM = 9;

int motorSpeed = 255;

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

   // automation test
    M1Forward(motorSpeed);
    M2Forward(motorSpeed);
  
    delay(1000);
  
    M1Brake();
    M2Brake();
  
    delay(1000);
  
    M1Backward(motorSpeed);
    M2Backward(motorSpeed);
  
    delay(1000);
  
    M1Brake();
    M2Brake();
  
    delay(1000);
}

