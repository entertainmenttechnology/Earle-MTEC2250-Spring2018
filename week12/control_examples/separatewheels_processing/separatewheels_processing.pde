import processing.serial.*;  // import serial library 
Serial myPort;  // Create object from Serial class


void setup() {
  size(510, 510);

  // remember to set your serial port...
  printArray(Serial.list());
  //myPort = new Serial(this, Serial.list()[1], 9600);
}

void draw() {
}

void keyPressed() {
  println(int(key));

  if (key == 'w') {
    // left wheel forward
    myPort.write(1);
  }
  if (key == 's') {
    // left wheel backward
    myPort.write(2);
  }
  if (key == CODED) {
    if (keyCode == UP) {
      // right wheel forward
      myPort.write(3);
    }
    if (keyCode == DOWN) {
      // right wheel backward
      myPort.write(4);
    } 
  }
  
  if(key == 32) {
    // spacebar, brake!
    myPort.write(0);
  }
}