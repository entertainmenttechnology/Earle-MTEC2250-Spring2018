void M1Backward(int spd) {
  //drive forward at full speed by pulling Motor1_A High
  //and Motor1_B low, while writing a full value to Motor1_PWM to
  //control speed
  digitalWrite(Motor1_A, HIGH);
  digitalWrite(Motor1_B, LOW);
  analogWrite(Motor1_PWM, spd);
}

void M1Forward(int spd) {
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
