import processing.serial.*;  // import serial library 
Serial myPort;  // Create object from Serial class


void setup() {
  size(510, 510);

  // remember to set your serial port...
  printArray(Serial.list());
  //myPort = new Serial(this, Serial.list()[1], 9600);
}

void draw() {
  background(0);

  stroke(255, 255, 0);
  strokeWeight(4);

  line(width/2, height/2, mouseX, mouseY);

  // INPUTS
  int     nJoyX = int(map(mouseX, width, 0, -128, 127));
  int     nJoyY = int(map(mouseY, height, 0, -128, 127));

  // OUTPUTS
  int     nMotMixL;           // Motor (left)  mixed output           (-128..+127)
  int     nMotMixR;           // Motor (right) mixed output           (-128..+127)

  // CONFIG
  // - fPivYLimt  : The threshold at which the pivot action starts
  //                This threshold is measured in units on the Y-axis
  //                away from the X-axis (Y=0). A greater value will assign
  //                more of the joystick's range to pivot actions.
  //                Allowable range: (0..+127)
  float fPivYLimit = 32.0;

  // TEMP VARIABLES
  float   nMotPremixL;    // Motor (left)  premixed output        (-128..+127)
  float   nMotPremixR;    // Motor (right) premixed output        (-128..+127)
  int     nPivSpeed;      // Pivot Speed                          (-128..+127)
  float   fPivScale;      // Balance scale b/w drive and pivot    (   0..1   )


  // Calculate Drive Turn output due to Joystick X input
  if (nJoyY >= 0) {
    // Forward
    nMotPremixL = (nJoyX>=0)? 127.0 : (127.0 + nJoyX);
    nMotPremixR = (nJoyX>=0)? (127.0 - nJoyX) : 127.0;
  } else {
    // Reverse
    nMotPremixL = (nJoyX>=0)? (127.0 - nJoyX) : 127.0;
    nMotPremixR = (nJoyX>=0)? 127.0 : (127.0 + nJoyX);
  }

  // Scale Drive output due to Joystick Y input (throttle)
  nMotPremixL = nMotPremixL * nJoyY/128.0;
  nMotPremixR = nMotPremixR * nJoyY/128.0;

  // Now calculate pivot amount
  // - Strength of pivot (nPivSpeed) based on Joystick X input
  // - Blending of pivot vs drive (fPivScale) based on Joystick Y input
  nPivSpeed = nJoyX;
  fPivScale = (abs(nJoyY)>fPivYLimit)? 0.0 : (1.0 - abs(nJoyY)/fPivYLimit);

  // Calculate final mix of Drive and Pivot
  nMotMixL = int((1.0-fPivScale)*nMotPremixL + fPivScale*( nPivSpeed));
  nMotMixR = int((1.0-fPivScale)*nMotPremixR + fPivScale*(-nPivSpeed));

  // Convert to Motor PWM range
  // ...
  
  println(nMotMixL, nMotMixR);
  
  myPort.write("" + nMotMixL + "," + nMotMixR + "\n");
}