// which analog pins are the pots connected to?
int potXpin = 0;
int potYpin = 1;
int potZpin = 2;

void setup() {
  Serial.begin(9600);
}

void loop() {
  // just read in the values
  int potX = analogRead(potXpin);
  int potY = analogRead(potYpin);
  int potZ = analogRead(potZpin);

  // the terminating character from println
  // will serve to tell the serial listener (in Processing
  // or Unity) that the message has ended
  Serial.print(potX);
  Serial.print(",");
  Serial.print(potY);
  Serial.print(",");
  Serial.println(potZ);

  delay(10);
}
