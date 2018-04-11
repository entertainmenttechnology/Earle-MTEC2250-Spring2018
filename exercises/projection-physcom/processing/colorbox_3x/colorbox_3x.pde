/*
  This sketch draws 3 greyscale boxes based on potentiometer input
  As the potentiometer is turned, the corresonding box brightness changes
*/

import processing.serial.*;
Serial myPort;  // Create object from Serial class
import codeanticode.syphon.*;
SyphonServer server;

// intaking serial data from Arduino
String inString;
int lf = 10;  // termination character

void setup() {
  size(1280, 720, P3D);

  // Create syhpon server to send frames out.
  server = new SyphonServer(this, "Processing Syphon");

  printArray(Serial.list());
  String portName = Serial.list()[3];
  myPort = new Serial(this, portName, 9600);
  // clear the port in case we started listening
  // in the middle of it transmitting data
  myPort.clear();
  inString = myPort.readStringUntil(lf);
  inString = null;
}

void draw() {
  // if data is available...
  while (myPort.available() > 0) {  

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
        // now we have our values in an array
        // at this point we can map them to appropriate color values
        for (int i = 0; i < vals.length; i++) {
          vals[i] = map(vals[i], 0, 1010, 0, 255);
        }

        background(0);

        // draw left box based on left potentiometer...
        fill(vals[0]);
        rect(0, 0, width/3, height);

        // and so on...
        fill(vals[1]);
        rect(width/3, 0, width/3, height);

        fill(vals[2]);
        rect(width/3 * 2, 0, width/3, height);
      }
    }
  }
  server.sendScreen();
}