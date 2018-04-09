import processing.serial.*;
Serial myPort;  // Create object from Serial class

PGraphics pg;

int inByte;  // incoming data from Arduino
float mappedByte;

void setup() {
  size(500, 500, P3D);

  printArray(Serial.list());
  String portName = Serial.list()[0];
  myPort = new Serial(this, portName, 9600);

  pg = createGraphics(width, height, P3D);

  pg.beginDraw();
  pg.background(0);
  pg.endDraw();
}

void draw() {
  while ( myPort.available() > 0) {  // If data is available,
    inByte = myPort.read();          // read it and store it in val

    mappedByte = map(inByte, 0, 255, 0, TWO_PI);
  }

  // testing...
  float knobR = map(mouseX, 0, width, 0, TWO_PI);

  pg.beginDraw();
  pg.background(0);
  pg.translate(pg.width/2, pg.height/2);
  pg.fill(255, 255, 0);
  pg.arc(0, 0, 200, 200, PI*1.5, PI*1.5+knobR);
  pg.endDraw();

  image(pg, 0, 0);
}
