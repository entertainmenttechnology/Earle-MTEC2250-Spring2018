// which analog pins are the pots connected to?
int potXpin = 5;

void setup() {
  Serial.begin(9600);
}

void loop() {
  // just read in the values
  int potX = analogRead(potXpin);

  // write it as a value between 0 and 255
  potX = map(potX,0,1023,0,255);

  Serial.write(potX);
 
  delay(10);
}
