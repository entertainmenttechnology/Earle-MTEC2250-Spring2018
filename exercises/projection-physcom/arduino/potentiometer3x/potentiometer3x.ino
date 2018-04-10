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

  // ~ will act as a signal that the next
  // piece of serial data are pot values
  Serial.print(potX);
  Serial.print(",");
  Serial.print(potY);
  Serial.print(",");
  Serial.print(potZ);
  Serial.print("/");  // terminating character
}
