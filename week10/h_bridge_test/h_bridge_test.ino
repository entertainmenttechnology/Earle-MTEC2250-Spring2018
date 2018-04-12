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

    M1Forward(motorSpeed);
    M2Backward(motorSpeed);
  
    delay(1000);
  
    M1Brake();
    M2Brake();
  
    delay(1000);

    M1Backward(motorSpeed);
    M2Forward(motorSpeed);
  
    delay(1000);
  
    M1Brake();
    M2Brake();
  
    delay(1000);
}


void M1Forward(int spd) {
  //drive forward at full speed by pulling Motor1_A High
  //and Motor1_B low, while writing a full value to Motor1_PWM to
  //control speed
  digitalWrite(Motor1_A, HIGH);
  digitalWrite(Motor1_B, LOW);
  analogWrite(Motor1_PWM, spd);
}

void M1Backward(int spd) {
  //change direction to reverse by flipping the states
  //of the direction pins from their forward state
  digitalWrite(Motor1_A, LOW);
  digitalWrite(Motor1_B, HIGH);
  analogWrite(Motor1_PWM, spd);
}

void M1Brake() {
  //Brake the motor by pulling both direction pins to
  //the same state (in this case LOW). Motor1_PWM doesn't matter
  //in a brake situation, but set as 0.
  digitalWrite(Motor1_A, LOW);
  digitalWrite(Motor1_B, LOW);
  analogWrite(Motor1_PWM, 0);
}

void M2Forward(int spd) {
  digitalWrite(Motor2_A, HIGH);
  digitalWrite(Motor2_B, LOW);
  analogWrite(Motor2_PWM, spd);
}

void M2Backward(int spd) {
  digitalWrite(Motor2_A, LOW);
  digitalWrite(Motor2_B, HIGH);
  analogWrite(Motor2_PWM, spd);
}

void M2Brake() {
  digitalWrite(Motor2_A, LOW);
  digitalWrite(Motor2_B, LOW);
  analogWrite(Motor2_PWM, 0);
}
