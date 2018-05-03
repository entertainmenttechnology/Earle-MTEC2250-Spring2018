import processing.serial.*;  // import serial library 
Serial myPort;  // Create object from Serial class


void setup() {
  size(500, 500);

  // remember to set your serial port...
  printArray(Serial.list());
  myPort = new Serial(this, Serial.list()[1], 9600);
}

void draw() {
  background(0);

  stroke(255, 255, 0);
  strokeWeight(5);

  line(width/2, height/2, mouseX, mouseY);

  motors(mouseX, mouseY);

  PVector n = motors(mouseX, mouseY);
  
  println(int(n.x) + "," + int(n.y));

  myPort.write("" + int(n.x) + "," + int(n.y) + "\n");
}


// Jiaming and I derived this from 
// https://electronics.stackexchange.com/questions/19669/algorithm-for-mixing-2-axis-analog-input-to-control-a-differential-motor-drive
PVector motors(float x, float y) {

  //x = 2.5;
  //y = 2.5;

  x = map(x, 0, width, -5, 5);
  y = map(y, 0, height, 5, -5);

  float r = sqrt(x*x + y*y);
  float t = atan2(y, x);

  t += PI/4;

  float left = r * cos(t);
  float right = r * sin(t);

  left *= sqrt(2);
  right *= sqrt(2);

  //left = max(-1, min(left, 1));
  //right = max(-1, min(right, 1));

  left = constrain(left, -5, 5);
  right = constrain(right, -5, 5);

  left = map(left, -5, 5, -127, 127);
  right = map(right, -5, 5, -127, 127);

  //println(left, right);

  PVector temp = new PVector(int(left), int(right));
  return temp;
}

//def steering(x, y):
//# convert to polar
//r = math.hypot(x, y)
//t = math.atan2(y, x)

//# rotate by 45 degrees
//t += math.pi / 4

//# back to cartesian
//left = r * math.cos(t)
//right = r * math.sin(t)

//# rescale the new coords
//left = left * math.sqrt(2)
//right = right * math.sqrt(2)

//# clamp to -1/+1
//left = max(-1, min(left, 1))
//right = max(-1, min(right, 1))

//return left, right