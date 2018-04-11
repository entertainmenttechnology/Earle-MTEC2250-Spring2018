import processing.serial.*;
Serial myPort;  // Create object from Serial class
import codeanticode.syphon.*;
SyphonServer server;

// intaking serial data from Arduino
String inString;
int lf = 10;  // termination character

// store previous values
float pX, pY;

void setup() {
  size(1280, 720, P3D);

  // Create syhpon server to send frames out.
  server = new SyphonServer(this, "Processing Syphon");

  // just a bunch of serial setup
  // a little more involved than usual because I want
  // to make sure the data is super clean
  printArray(Serial.list());
  String portName = Serial.list()[3];
  myPort = new Serial(this, portName, 9600);
  // clear the port in case we started listening
  // in the middle of it transmitting data
  myPort.clear();
  inString = myPort.readStringUntil(lf);
  inString = null;

  background(0);
  stroke(255);
  strokeWeight(4);
}

void draw() {

  // if data is available...
  while ( myPort.available() > 0) {  

    // store all incoming data to a string, but only
    // read until you hit the terminating character

    String inString = myPort.readStringUntil(lf);
    if (inString != null) {
      // print out resulting string for diagnostics
      println(inString);

      // now we need to split it up and
      // convert it to floats
      float[] vals = float(split(inString, ","));

      if (vals.length > 2) {
        // now we have our values:
        // vals[0] is x, vals[1] is y, vals[2] is z and so on

        // map to screen coords
        float x = map(vals[0], 0, 1010, 0, width);
        float y = map(vals[1], 0, 1010, 0, height);
        
        // just for fun, 3rd pot can do color
        float hue = map(vals[2], 0, 1010, 0, 360);
        colorMode(HSB, 360);
        
        stroke(hue,180,360);

        line(pX, pY, x, y);

        pX = x;
        pY = y;
      }
    }
  }

  server.sendScreen();
}

void keyPressed() {
  background(0);
}