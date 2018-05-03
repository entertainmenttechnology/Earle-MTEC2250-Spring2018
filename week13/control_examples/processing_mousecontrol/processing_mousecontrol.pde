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

  // make the right wheels turn more when the mouse is
  // towards the left side of the screen
  int rightPWM = int(map(mouseX, 0, width, 255*2, 0));
  rightPWM = constrain(rightPWM, 0, 255);

  // vice versa
  int leftPWM = int(map(mouseX, 0, width, 0, 255*2));
  leftPWM = constrain(leftPWM, 0, 255);

  print("rightPWM : ");
  print(rightPWM);
  print(", leftPWM : ");
  println(leftPWM);

  // are we going forward or reverse?
  if (mouseY > height/2) {
    // reverse
    //myPort.print("r," + 
  } else {
    // forward
  }
}
