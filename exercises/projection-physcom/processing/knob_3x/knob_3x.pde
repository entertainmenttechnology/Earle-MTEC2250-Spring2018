import processing.serial.*;
Serial myPort;  // Create object from Serial class

PGraphics pg;

String inString;

int lf = 10;  // termination character

void setup() {
  size(500, 500, P3D);

  printArray(Serial.list());
  String portName = Serial.list()[0];
  myPort = new Serial(this, portName, 9600);
  // clear the port in case we started listening
  // in the middle of it transmitting data
  myPort.clear();
  inString = myPort.readStringUntil(lf);
  inString = null;

  pg = createGraphics(width, height, P3D);

  pg.beginDraw();
  pg.background(0);
  pg.endDraw();
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

      // now we have our values:
      // vals[0] is x, vals[1] is y, vals[2] is z and so on

      // at this point we can map them to radial values
      for (int i = 0; i < vals.length; i++) {
        vals[i] = map(vals[i], 0, 1023, 0, TWO_PI);
      }

      pg.beginDraw();
      pg.background(0);
      pg.translate(pg.width/2, pg.height/2);
      pg.fill(255, 255, 0);
      pg.arc(0, 0, 200, 200, PI*1.5, PI*1.5+vals[0]);
      pg.endDraw();
    }
  }



  image(pg, 0, 0);
}
