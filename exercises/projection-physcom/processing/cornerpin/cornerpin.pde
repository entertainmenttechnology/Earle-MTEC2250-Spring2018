import processing.serial.*;
Serial myPort;  // Create object from Serial class
// intaking serial data from Arduino
String inString;
int lf = 10;  // termination character


import deadpixel.keystone.*;

Keystone ks;
CornerPinSurface surface;

PGraphics offscreen;

float pX, pY;

void setup() {
  // Keystone will only work with P3D or OPENGL renderers, 
  // since it relies on texture mapping to deform
  size(1280, 720, P3D);

  ks = new Keystone(this);
  surface = ks.createCornerPinSurface(1280, 720, 20);

  // We need an offscreen buffer to draw the surface we
  // want projected
  // note that we're matching the resolution of the
  // CornerPinSurface.
  // (The offscreen buffer can be P2D or P3D)
  offscreen = createGraphics(1280, 720, P3D);


  printArray(Serial.list());
  String portName = Serial.list()[1];
  myPort = new Serial(this, portName, 9600);
  // clear the port in case we started listening
  // in the middle of it transmitting data
  myPort.clear();
  inString = myPort.readStringUntil(lf);
  inString = null;
}

void draw() {

  // Convert the mouse coordinate into surface coordinates
  // this will allow you to use mouse events inside the 
  // surface from your screen. 
  PVector surfaceMouse = surface.getTransformedMouse();

  // Draw the scene, offscreen
  offscreen.beginDraw();
  offscreen.background(255);
  offscreen.fill(0, 255, 0);
  offscreen.ellipse(surfaceMouse.x, surfaceMouse.y, 75, 75);
  offscreen.endDraw();

  // most likely, you'll want a black background to minimize
  // bleeding around your projection area
  background(0);

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
      int whichCorner = 0;

      if (vals.length > 2) {
        // now we have our values in an array
        // at this point we can map them to appropriate color values
        for (int i = 0; i < 2; i++) {
          vals[i] = map(vals[i], 0, 1010, 0, width);
        }

        whichCorner = int(map(vals[2], 0, 1010, 0, 4));
        println(whichCorner);


        switch(whichCorner) {
        case 0:
          surface.moveMeshPointBy(surface.TL, vals[0] - pX, vals[1] - pY);
          break;
        case 1:
          surface.moveMeshPointBy(surface.TR, vals[0] - pX, vals[1] - pY);
          break;
        case 2:
          surface.moveMeshPointBy(surface.BR, vals[0] - pX, vals[1] - pY);
          break;
        case 3:
          surface.moveMeshPointBy(surface.BL, vals[0] - pX, vals[1] - pY);
          break;
        case 4:
          break;
        }

        pX = vals[0];
        pY = vals[1];
      }
    }
  }


  // render the scene, transformed using the corner pin surface
  surface.render(offscreen);
}

void keyPressed() {
  switch(key) {
  case 'c':
    // enter/leave calibration mode, where surfaces can be warped 
    // and moved
    ks.toggleCalibration();
    break;

  case 'l':
    // loads the saved layout
    ks.load();
    break;

  case 's':
    // saves the layout
    ks.save();
    break;
  }
}