import processing.serial.*;  // import serial library

Serial myPort;  // Create object from Serial class

void setup() {
  size(500, 300);

  // remember to set your serial port...
  printArray(Serial.list());
  myPort = new Serial(this, Serial.list()[1], 9600);
}

void draw() {

  background(0);
  fill(255);

  ellipse(mouseX, mouseY, 20, 20);

  // map the mouse position from 0 -> window width to a range of degrees for the servo
  // the servo can handle 180 degrees, but I think the pointer is better with a limited range
  int posX = int(map(mouseX, 0, width, 1, 255));

  if (pmouseX != mouseX)
    myPort.write(posX);  // write x mouse position to serial
}