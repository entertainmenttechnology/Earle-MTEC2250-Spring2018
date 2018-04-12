// which analog pins are the pots connected to?
int potXpin = 0;
int potYpin = 1;
int potZpin = 2;

int pX, pY, pZ; // store previous values for smoothing

void setup() {
  Serial.begin(9600);
}

void loop() {
  // just read in the values
  int potX = analogRead(potXpin);
  int potY = analogRead(potYpin);
  int potZ = analogRead(potZpin);

  // smoothing function basically just puts a bias
  // on the data such that it is influenced by its
  // previous values
  int smoothX = int((pX * .9) + (potX * .1));
  int smoothY = int((pY * .9) + (potY * .1));
  int smoothZ = int((pZ * .9) + (potZ * .1));

  // update previous values so smoothing works
  pX = smoothX;
  pY = smoothY;
  pZ = smoothZ;
  
  Serial.print(smoothX);
  Serial.print(",");
  Serial.print(smoothY);
  Serial.print(",");
  Serial.println(smoothZ);
  // make sure to printLINE so that it spits
  // out the terminating character

  delay(20);
}
