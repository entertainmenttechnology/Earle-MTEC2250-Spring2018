import processing.serial.*;  // import serial library
 
Serial myPort;  // Create object from Serial class

void setup() {
  size(500, 300);
  // remember to set your serial port...
  printArray(Serial.list());

  myPort = new Serial(this, Serial.list()[1], 9600);
}

void draw() {
  
}

void keyReleased() {
  myPort.write(0);
}

void keyPressed() {
  if(key == 'w') {
    myPort.write(1); // move forward
  }
  
  if(key == 's') {
    myPort.write(2); // move forward
  }
  
  if(key == 'd') {
    myPort.write(3); // move left
  }
  
  if(key == 'a') {
    myPort.write(4); // move right
  }
}