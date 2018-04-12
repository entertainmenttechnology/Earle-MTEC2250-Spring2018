import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import codeanticode.syphon.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class space_2 extends PApplet {

// ctl alt F

/* to do list:
*/





// final bools
final static boolean OSC = true;
final static boolean SYPHON = true;
boolean diagnostic = false;  // diagnostics on for debug

SyphonServer syphon1, syphon2;

OscP5 oscP5;
NetAddress PETER, BASE, VDMX;
String PETER_IP = "169.254.128.132";
String BASE_IP = "169.254.208.46";
String VDMX_IP = "127.0.0.1"; // (localhost)
// me 169.254.4.180

// 0: waiting 1: Suit Up 2: Gear Test 3: Blast Off 4: Emergency 5: Wormhole! 6: End
int scene = 0;

// general graphics and such
PFont font, fontGUI;
int textColor = color(255, 255, 0);
float near; // camera bounds
float far;
final static int FONT_SIZE = 80;

// universal stuff
long lastTime = 0;  // last time millis was queried
int lastScene = 0;  // to detect scene changes
int maxAsteroids = 2000;  // max number of 'stroids on screen

// arraylists :D :D :D
ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
ArrayList<Star> stars = new ArrayList<Star>();
ArrayList<Asteroid> streaks = new ArrayList<Asteroid>();
ArrayList<Crack> cracks = new ArrayList<Crack>();

PGraphics pg, pgD; // graphics contexts [pgD = diagnostics]
final static int SPACE_W = 1280;
final static int SPACE_H = 720;
final static int GUI_W = 1280;
final static int GUI_H = 720;

// images
PImage[] diffImg = new PImage[3]; // difficulty selection

// animatin timers
long currTime, prevTime;
float deltaTime;

public void setup() {
  // set up size so that there is extra space below for the gui to render
  size(1280,720, OPENGL);
  frameRate(30);

  pg = createGraphics(width, height, OPENGL);
  pgD = createGraphics(GUI_W, GUI_H, OPENGL);

  // init object lists
  asteroids = new ArrayList();
  streaks = new ArrayList();
  stars = new ArrayList();
  oscStack = new ArrayList();

  // general
  font = loadFont("font100.vlw");
  fontGUI = loadFont("font40.vlw");

  // 3d setup
  float fov = PI / 3.0f;
  float cameraZ = (height / 2.0f) / tan(fov / 2.0f);
  near = 1;
  far = -1000;
  perspective(fov, PApplet.parseFloat(width) / PApplet.parseFloat(height), near, far);

  // set up graphics context
  pg.beginDraw();
  pg.imageMode(CENTER);
  pg.background(0);
  pg.textFont(font, 40);
  pg.endDraw();

  image(pg, 0, 0);

  // Create syhpon server to send frames out
  if (SYPHON) {
    syphon1 = new SyphonServer(this, "Wormhole - graphics");
    syphon2 = new SyphonServer(this, "Wormhole - diagnostics");
  }

  // OSC setup
  oscP5 = new OscP5(this, 8000);
  PETER = new NetAddress(PETER_IP, 9000);
  BASE = new NetAddress(BASE_IP, 7000);
  VDMX = new NetAddress(VDMX_IP, 1234);

  resetVDMX();
  setupHUD(); // setup HUD display once at start

  // load in images
  //PImage[] diffImg = new PImage[3];
  diffImg[0] = loadImage("difficulty_0.jpg");
  diffImg[1] = loadImage("difficulty_1.jpg");
  diffImg[2] = loadImage("difficulty_2.jpg");

  // Initialise the timer
  currTime = prevTime = millis();
}

public void draw() {

  // update timers
  currTime = millis();
  // calculate the elapsed time in seconds
  deltaTime = (currTime - prevTime) / 1000.0f;
  // remember current time for the next frame
  prevTime = currTime;

  checkScene(); // check for scene change to issue scene specific commands

  switch (scene) {
  case 0: // WAITING FOR PLAYER
    waiting();
    break;
  case 1: // PILOT GEAR TEST
    gearTest();
    break;
  case 2: // HOUSTON COMM TEST
    commTest();
    break;
  case 3: // BLAST OFF
    blastOff();
    break;
  case 4: // EMERGENCY
    emergency();
    break;
  case 5: // WORMHOLE
    wormhole();
    break;
  case 6: // VICTORY
    victory();
    break;
  case 7: // DEFEAT
    defeat();
    break;
  case 8: // SELF-DESTRUCT
    selfDestruct();
    break;
  }

  // lastly, render visual fx .. is this still used?
  if (strobe) {
    pg.beginDraw();
    doStrobe();
    pg.endDraw();
  }

  // render game to screen (or syphon)
  if (!SYPHON) {
    noStroke();
    image(pg, 0, 0);
  } else {
    syphon1.sendImage(pg);
    syphon2.sendImage(pgD);
  }
}

public void doText(String t, int c) {
  pg.textAlign(CENTER);
  pg.textFont(font, FONT_SIZE);
  pg.fill(c);
  pg.text(t, width / 2, height / 2);
}

public void eraseDiag() {
  // erase diagnostic
  pgD.beginDraw();
  pgD.fill(0);
  pgD.noStroke();
  pgD.rect(0, height, width, height);
  pgD.endDraw();
}
float asteroidTrans = 120;  // global transparency modifier for asteroids

int[] colors = {
  color(0),
  color(255),
  color(79, 250, 247),
  color(168, 165, 212),
  color(177, 125, 186),
  color(149, 48, 57),
  color(246, 87, 153),
  color(100, 72, 130)
};

class Asteroid {
  PVector pos;

  int cFill, cStroke;

  float r = 0;  // rotation
  float rotateSpeed = .05f;

  float sw = 2; //strokeWeight

  float ld = 5; // line distance

  float p;  // phase
  float pRate = .3f;  // phase rate
  float pAdjust;  // use this number to add to s [or any other value)

  // boost speed as game goes on
  float speed = 10 + map(timeInWormhole, 0, wormholeDuration, 0, 20);
  float s = 1;  // starting size
  float growthRate = .5f; // growth per frame

  int type = 0; // type of asteroid

  // properties
  // phase pulsates size of asteroid
  // corona gives it a circular outline
  // death color makes it change space bg color on impact w screen

  // behaviors
  boolean ROTATE, PHASE;

  Asteroid(int type) {
    this.type = type;

    pos = new PVector(random(width), random(height), far);

    // randomize its initial rotation
    r = random(TWO_PI);

    switch (type) {
    case 0: // WHITE STAR STREAKS
      sw = 2;
      break;
    case 1: // BLACK ROTATING CUBES W WHITE OUTLINE
      sw = 1;
      cFill = color(0);
      cStroke = color(255);
      ROTATE = true;
      growthRate = .3f;
      s = .02f;
      break;
    case 2: // PULSING COLOR CIRCLES
      sw = 0;
      pRate = .2f;
      s = .4f;
      growthRate = .3f;
      cFill = colors[PApplet.parseInt(random(colors.length))]; // draw random color
      PHASE = true;
      break;
    case 3: // COLORFUL CORKSCREW LINE
      ld = 20;
      sw = 2.5f;
      PHASE = true;
      break;
    case 4: // ROTATING TRIANGLES
      cFill = color(255);
      rotateSpeed = .1f;
      ROTATE = true;
      PHASE = true;
      break;
    case 5: // CYCLONE
      s = 1;
      sw = 1;
      growthRate = .01f;
      rotateSpeed = .1f;
      ROTATE = true;
      PHASE = true;
      break;
    case 6: // BIRTH COLOR
      // dont choose black
      int rando = PApplet.parseInt(random(colors.length - 1)) + 1;
      bgColor = color(colors[rando], 24);
      speed = 10000;
      break;
    case 7: // BUTTERFLY
      cFill = colors[PApplet.parseInt(random(colors.length))];
      growthRate = .1f;
      ROTATE = true;
      PHASE = true;
      break;
    case 8:
      PHASE = true;
      growthRate = .25f;
      break;
    }
  }

  public void update() {
    // all asteroid types move towards you (and grow)
    pos.z += speed * 90 * deltaTime;
    s += growthRate;

    // rotating asteroids
    if (ROTATE) {
      r += rotateSpeed;
    }
    // phasing asteroids
    if (PHASE) {
      p += pRate; // inc phase
      pAdjust += sin(p) * .5f;
    }
  }

  public void display() {

    switch (type) {
    case 0: // WHITE STAR STREAK
      pg.pushMatrix();
      pg.translate(pos.x, pos.y, pos.z);
      pg.stroke(255);
      pg.strokeWeight(sw);
      pg.line(0, 0, 0, 0, 0, -10);
      pg.popMatrix();
      break;
    case 1: // BLACK ROTATING CUBES W WHITE OUTLINE
      pg.pushMatrix();
      pg.translate(pos.x, pos.y, pos.z);
      pg.rotateY(r);
      pg.rotateZ(r * 2);
      pg.rotateX(r * 3);
      pg.fill(cFill, asteroidTrans);
      pg.strokeWeight(sw);
      pg.stroke(cStroke);
      pg.box(s + pAdjust);
      pg.popMatrix();
      break;
    case 2: // PULSING COLOR CIRCLES
      pg.pushMatrix();
      pg.translate(pos.x, pos.y, pos.z);
      pg.strokeWeight(sw);
      pg.stroke(cStroke);
      pg.fill(cFill, asteroidTrans);
      pg.ellipse(0, 0, s + (pAdjust * 1.5f), s + (pAdjust * 1.5f));
      pg.popMatrix();
      break;
    case 3: // COLORFUL CORKSCREW LINE
      pg.pushMatrix();
      pg.translate(pos.x, pos.y, pos.z);
      pg.stroke(cStroke);
      pg.strokeWeight(sw);

      int segments = 8;

      PVector[] linePV = new PVector[segments];
      cStroke = color(10, 10, 10);
      pg.stroke(cStroke);
      linePV[0] = new PVector(0, 0, 0);
      for (int i = 1; i < segments; i++) {
        float x = cos( p + (segments / TWO_PI * i)) * ld * (i * .1f);
        float y = sin( p + (segments / TWO_PI * i)) * ld * (i * .1f);
        float z = linePV[i - 1].z - ld;

        linePV[i] = new PVector(x, y, z);

        cStroke = color(10, 20, map(sin(p), -1, 1, 0, 140));
        pg.stroke(cStroke, 100);
        pg.line(linePV[i - 1].x, linePV[i - 1].y, linePV[i - 1].z, x, y, z);
      }

      pg.popMatrix();
      break;
    case 4: // ROTATING TRIANGLES
      pg.pushMatrix();
      pg.translate(pos.x, pos.y, pos.z);
      pg.rotateZ(r);
      pg.rotateX(3 * PI / 2);
      pg.noStroke();
      float triangleRed = map(sin(p), -1, 1, 0, 255);
      float triangleGreen = map(cos(p), -1, 1, 0, 255);
      float triangleBlue = map(tan(p), -1, 1, 0, 255);
      cFill = color(triangleRed, triangleGreen, triangleBlue);
      pg.fill(cFill);
      pg.triangle(0, -16, 12, 16, -12, 16);
      pg.popMatrix();
      break;
    case 5: // CYCLONE
      float cycloneRed = map(sin(p), -1, 1, 0, 255);
      float cycloneGreen = map(cos(p), -1, 1, 0, 255);
      cFill = color(cycloneRed, cycloneGreen, 127);

      pg.stroke(cFill);
      pg.strokeWeight(sw + pAdjust * 2);

      pg.pushMatrix();
      pg.translate(pos.x + cos(p) * s * 2, pos.y + sin(p) * s * 2, pos.z);
      pg.rotateX(r);
      pg.point(0, 0);
      pg.popMatrix();

      pg.pushMatrix();
      pg.translate(pos.x + cos(p * -1) * s * 2, pos.y + sin(p * -1) * s * 2, pos.z - 10);
      pg.rotateX(r);
      pg.point(0, 0);
      pg.popMatrix();

      pg.pushMatrix();
      pg.translate(pos.x + sin(p * -1) * s * 2, pos.y + cos(p * -1) * s * 2, pos.z - 20);
      pg.rotateX(r);
      pg.point(0, 0);
      pg.popMatrix();

      pg.pushMatrix();
      pg.translate(pos.x + sin(p) * s * 2, pos.y + cos(p) * s * 2, pos.z - 30);
      pg.rotateX(r);
      pg.point(0, 0);
      pg.popMatrix();
      break;
    case 6: // BIRTH COLOR
      // n/a
      break;
    case 7: // BUTTERFLY
      pg.pushMatrix();
      pg.fill(cFill);
      pg.noStroke();
      pg.translate(pos.x, pos.y, pos.z);
      pg.rotateZ(r);
      pg.beginShape(TRIANGLES);
      pg.vertex(0, 0, 0);
      pg.vertex(0, 0, -s);
      pg.fill(cFill);
      pg.vertex(s, 0 + sin(p) * s / 2, -s);
      pg.fill(0);
      pg.vertex(0, 0, 0);
      pg.vertex(0, 0, -s);
      pg.fill(cFill);
      pg.vertex(-s, 0 + sin(p) * s / 2, -s);
      pg.endShape();
      pg.popMatrix();
      break;
    case 8:
      pg.sphereDetail(7);
      pg.pushMatrix();
      pg.noFill();
      pg.translate(pos.x, pos.y, pos.z);
      pg.rotateZ(r);pg.rotateY(r/2);pg.rotateZ(r/3);
      pg.sphere(s + pAdjust*8);
      pg.popMatrix();
      break;
    }
  }

  public boolean dead() {
    // if object gets totally offscreen...
    if (pos.z > 1000) {
      return true;  // destroy object
    } else {
      return false;
    }
  }
}
int doorOpen = 0;
int starCount = 120;
int blastOffTime = 10 * 1000;	// how long does this scene last?

float doorSpeed = 80;

public void blastOff() {
	doorSpeed = width/(blastOffTime/1000)/1.25f;
	pg.beginDraw();
	pg.background(0);

	// do we need to add stars?
	while (stars.size() < starCount) {
		stars.add(new Star());
	}

	// draw the stars
	for (Star s : stars) {
		s.display();
	}

	// continue opening doors if necessary
	if (doorOpen < width / 2) {
		// SHAKE!
		OscMessage m = new OscMessage("/pilot/shake");
		m.add(.1f);
		oscP5.send(m, VDMX);

		doorOpen += doorSpeed * deltaTime;
		pg.rectMode(CORNER);
		pg.fill(255);
		pg.noStroke();
		pg.rect(-4, 0, width / 2 - doorOpen + 4, height);
		pg.rect(width / 2 + doorOpen, 0, width, height);
	} else {
		OscMessage m = new OscMessage("/pilot/shake");
		m.add(0);
		oscP5.send(m, VDMX);
	}

	if (millis() > lastTime + blastOffTime) {
		scene++;
	}

	pg.endDraw();
}
public void checkScene() {
// if the scene has changed, recent timer
  if (scene != lastScene) {
    lastScene = scene;
    lastTime = millis();
    // send scene change out over OSC
    if (OSC) {
      OscMessage m = new OscMessage("/pilot/scene");
      m.add(scene);
      oscP5.send(m, PETER);

      // only send scene changes to monica if we are over scene 3
      // since she controls the first ones via Houston
      if (scene > 3 || scene == 0)
        oscP5.send(m, BASE);
    }

    removeAll();  // remove all stellar objects
    resetVDMX();  // reset VDMX every scene change

    // erase canvases
    pg.beginDraw();
    pg.background(0);
    pg.endDraw();
    pgD.beginDraw();
    pgD.background(0);
    pgD.endDraw();

    // reset difficulty selection
    whichDiff = 0;

    // reset approaching warp var
    approachingEnd = false;

    // reset targets and life
    /*
    for (int i = 0; i < actual.length; i++) {
      target[i] = range / 2;
      actual[i] = range / 2;
    }
    */
    
    // actually bias the oxygen for sake of the event pattern
    target[1] -= 1;
    life = startingLife;
    eventInterval = 2000; // starting event interval at 2 seconds

    // if its the gearTest tutorial, start at arbitrary values
    if (scene == 1) {
      target[0] = 90;
      target[1] = 10;
      target[2] = 32;
      targetSwitches = "101";
      eventReady[0] = false;
      eventReady[1] = false;
      eventReady[2] = false;
      eventReady[3] = false;
    }

    if (scene == 3) {
      // blast off! start with closed door
      doorOpen = 0;
    }

    if (scene != 4) {
      // no strobe for anything but emergency scene
      strobe = false;
    }

    if (scene == 5) {
      // if we are entering wormhole
      strobe = false;
      //shakeStrength = 10; (outdated, using VDMX now)
      startedWormhole = millis();
      life = startingLife;
      lastEvent = millis();
      // events are ready
      eventReady[0] = true;
      eventReady[1] = true;
      eventReady[2] = true;
      eventReady[3] = true;
    }
  }
}

public void removeAll() {
  // remove old asteroids and streaks, etc.
  synchronized (streaks) {
    for (int i = 0; i < streaks.size(); i++) {
      streaks.remove(i);
    }
  }
  synchronized (asteroids) {
    for (int i = 0; i < asteroids.size(); i++) {
      asteroids.remove(i);
    }
  }
}

public void resetVDMX() {
  OscMessage  m = new OscMessage("/pilot/data");
  m.add(0);//shake
  m.add(0);//junk
  m.add(0);//junk
  m.add(0);//junk
  m.add(0);//hyper
  m.add(0);//junk
  m.add(0);//oxygen
  m.add(0);//junk
  m.add(0);//modulation
  m.add(0);//switches target
  m.add(0);//switches actual
  m.add(0);//total diff
  m.add(0);//approaching ending
  oscP5.send(m, VDMX);
}
int whichDiff = 0;

public void commTest() {
	pg.beginDraw();
	pg.background(0);
	pg.textFont(font, FONT_SIZE);
	pg.textAlign(CENTER);
	pg.fill(255, 0, 0);
	pg.text("BASE COMMAND IS TESTING COMMS", width / 2, height / 2 - FONT_SIZE);
	pg.text("GIVE A THUMBS UP", width / 2, height / 2);
	pg.text("WHEN THE CHANNEL SOUNDS CLEAN", width / 2, height / 2 + FONT_SIZE);
	pg.endDraw();
/*
	// draw difficulty picker
	pg.imageMode(CENTER);
	// use hyperdrive as selector
	//whichDiff = int(map(actual[0],0,range,0,2));
	whichDiff = constrain(whichDiff, 0, 2);
	pg.image(diffImg[whichDiff], width / 2, height / 1.4);

	pg.endDraw();

	switch (whichDiff) {
		case 0:
			startingLife = 10000;	// easy
			break;
		case 1:
			startingLife = 8000;	// medium
			break;
		case 2:
			startingLife = 5000;	// hard
			break;
	}
	*/
}
int cOff = 1;	// offset for layering crack shadows
float lastCrackX = SPACE_W/2;
float lastCrackY = SPACE_H/2;
int crackLen = 50;	// maximum crack length

class Crack {
	float x1, x2, y1, y2;

	Crack() {
		x1 = lastCrackX;
		y1 = lastCrackY;
		x2 = lastCrackX += random(-crackLen, crackLen);
		y2 = lastCrackY += random(-crackLen, crackLen);

		lastCrackX = x2;
		lastCrackY = y2;

	}

	public void display() {
		pgD.strokeWeight(2);
		pgD.stroke(255);
		pgD.line(x1, y1, x2, y2);
	}
}
// 120 = 14.5s
// 110 = 12s
// 100 = 10s
// 75 = 5.7s
// 50 = 2.5s

// TO DO:
// error events shouldnt advance until they have been corrected

IntList eventSequence = new IntList();
IntList buttonSequence = new IntList();

// how many seconds a particular resource will leave you alone after being adjusted
final static int eventBuffer = 3000;
int[] lastCheck = {0, 0, 0, 0};	// check against buffer
// flags to keep track of whether or not the resource has been adjusted
boolean[] eventReady = {true, true, true, true};

public void doEvents() {

	// changing error events so that
	// one can only change if it has been correctly adjusted

	if (millis() - lastEvent >= eventInterval) {

		// change seed
		randomSeed(millis());

		// reset timer
		lastEvent = millis();

		// figure out next error event
		float temp = map(millis(), startedWormhole, startedWormhole + wormholeDuration, 14000, 4000);

		// adjust based on remaining life
		// take as much as half of the interval away if life is full
		temp -= (temp / 2 ) * (life / startingLife);

		eventInterval = temp;

		//println(eventInterval);

		// take care of random urn if its empty
		if (eventSequence.size() == 0) {
			// generate sequence, weigh switches 2x as much
			for (int i = 0; i < 5; i++) {
				eventSequence.append(i);
			}
			// shuffle
			eventSequence.shuffle();
		}

		// grab the first remaining number from urn
		int rando = eventSequence.get(0);
		if (rando == 4) rando = 3;	// switches weight hack
		eventSequence.remove(0);	// remove it

		// here's the new part: if this resource isnt ready to be
		// changed, dont change it
		long checkDiff = millis() - lastCheck[rando];
		if (eventReady[rando] && checkDiff > eventBuffer) {
			eventReady[rando] = false;	// not ready anymore!
			targetMonitor[rando] = false;	// stop monitoring target
			if (rando < 3) {
				// store previous value for a sec
				float tempTarget = target[rando];
				// change one of the sliders
				if (target[rando] < .5f * range) {
					target[rando] = random(range * .5f, range);
				} else {
					target[rando] = random(range * .5f);
				}
				// bump it if necessary
				if (abs(target[rando] - tempTarget) < targetBuffer * range * 2) {
					if (random(1) > .5f) {
						// try to bump up/right
						target[rando] += targetBuffer * range * 2;
						if (target[rando] > range) {
							target[rando] -= targetBuffer * range * 4;
						}
					} else {
						// try to bump down/left
						target[rando] -= targetBuffer * range * 2;
						if (target[rando] < 0) {
							target[rando] += targetBuffer * range * 4;
						}
					}
				}
			} else {
				// switches: first half of game just one switch changes, second half two switch
				String newSwitches[] = {str(targetSwitches.charAt(0)), str(targetSwitches.charAt(1)), str(targetSwitches.charAt(2))};
				int term = 1;
				if (timeInWormhole > wormholeDuration * .5f) {
					term = 2;
				}
				for (int i = 0; i < 3; i++) {
					buttonSequence.append(i);
				}
				buttonSequence.shuffle();
				for (int i = 0; i < term; i++) {
					if (newSwitches[buttonSequence.get(i)].equals("0") ) {
						newSwitches[buttonSequence.get(i)] = "1";
					} else {
						newSwitches[buttonSequence.get(i)] = "0";
					}
				}
				buttonSequence.clear();
				targetSwitches = newSwitches[0] + newSwitches[1] + newSwitches[2];
			}
		}
	}
}
int emergencyTime = 5 * 1000;

public void emergency() {
	pg.beginDraw();
	pg.background(0);

	// there are stars from last scene, animate those suckers
	for (Star s : stars) {
		s.display();
	}

	pg.endDraw();

	strobe = true;
	strobeTrans = 127;
	strobeColor = color(255, 0, 0);

	// advance scene...
	if (millis() > lastTime + emergencyTime) {
		scene++;
	}
}
int endgameTime = 25 * 1000;	// how long does this scene last?

public void victory() {

	if (millis() > lastTime + endgameTime) {
		scene = 0;
	}

	pg.beginDraw();
	pg.background(0, 255);
	doText("YOU SURVIVED THE WORMHOLE\nAND MADE IT TO\nTHE OTHER SIDE", textColor);
	pg.endDraw();
}

public void defeat() {

	if (millis() > lastTime + endgameTime) {
		scene = 0;
	}

	pg.beginDraw();
	pg.background(0, 255);
	doText("YOUR WURM WAS DESTROYED\n(AND SO WERE YOU)", textColor);
	pg.endDraw();
}

// ka-boom
public void selfDestruct() {
	if (millis() > lastTime + endgameTime) {
		scene = 0;
	}

	pg.beginDraw();
	pg.background(0, 255);
	doText("YOU BLEW YOURSELF UP\nFOR SOME REASON", textColor);
	pg.endDraw();
}
boolean strobe = false;
int strobeRate = 5;
int strobeTrans = 127;
int strobeColor = color(255,0,0);

public void doStrobe() {
	if (frameCount % strobeRate == 0) {
		pg.background(strobeColor, strobeTrans);
	}
}
float vertSpacing;
float sliderW, sliderThick;
float beginX, endX;
int hudFontSize;
float hudTextBuffer;

float targetWidth, targetHeight;

int hudSwitchSize;
float hudSwitchSpacing;

int sliderColor, hudFontColor, hudTargetColor, hudActualColor, hudOffColor, hudSwitchFill;

public void setupHUD() {
	// gather measurements for HUD
	vertSpacing = height / 5;	// spacing between rows
	sliderW = width * .5f;	// width of rows
	sliderThick = 4;

	// where do rows begin and end on X?
	beginX = (width / 2) - (sliderW / 2);
	endX = (width / 2) + (sliderW / 2);

	// target stuff
	targetHeight = 20;
	targetWidth = sliderW * targetBuffer * 2;

	// switch stuff
	hudSwitchSize = 30;
	hudSwitchSpacing = sliderW / 2 - (hudSwitchSize * 1.75f) ;

	// colors
	sliderColor = color(255);
	hudFontColor = color(255, 255, 0);
	hudOffColor = color(127);
	hudTargetColor = color(0, 255, 255);
	hudActualColor = color(255, 255, 0);
	hudSwitchFill = color(255);

	// font
	hudFontSize = 40;
	hudTextBuffer = hudFontSize + 4;
}

public void gearTest() {

	pgD.beginDraw();
	pgD.background(0);

	// draw pilot HUD

	// text
	pgD.textFont(fontGUI, hudFontSize);
	pgD.fill(hudFontColor);
	pgD.textAlign(LEFT);
	pgD.text("HYPERDRIVE POWER", beginX, vertSpacing * 1 - hudTextBuffer);
	pgD.text("OXYGEN FLOW", beginX, vertSpacing * 2 - hudTextBuffer);
	pgD.text("GRAVITY MODULATION", beginX, vertSpacing * 3 - hudTextBuffer);
	pgD.text("PARTICLE SPLITTER", beginX, vertSpacing * 4 - hudTextBuffer);

	// straight lines across for hyperdrive,o2,modulation
	pgD.fill(sliderColor);
	pgD.strokeWeight(sliderThick);
	pgD.line(beginX, vertSpacing * 1, endX, vertSpacing * 1);
	pgD.line(beginX, vertSpacing * 2, endX, vertSpacing * 2);
	pgD.line(beginX, vertSpacing * 3, endX, vertSpacing * 3);

	// targets for hyp, oxy, mod
	pgD.noStroke();
	pgD.rectMode(CENTER);
	for (int i = 0; i < 3; i++) {
		// if pilot is close enough to correct value, color change
		if (abs((target[i] - actual[i])) / range < targetBuffer) {
			pgD.fill(hudTargetColor);
		} else {
			pgD.fill(hudOffColor);
		}
		pgD.rect(map(target[i], 0, range, beginX, endX), vertSpacing * (i + 1), targetWidth, targetHeight);
	}

	// actuals
	pgD.strokeWeight(sliderThick);
	pgD.stroke(hudActualColor);
	for (int i = 0; i < 3; i++) {
		float x = map(actual[i], 0, range, beginX, endX);
		pgD.line(x, vertSpacing * (i + 1) - targetHeight / 2, x, vertSpacing * (i + 1) + targetHeight / 2);
	}

	// switch boxes
	pgD.rectMode(CENTER);
	float y = vertSpacing * 4;
	for (int i = 0; i < 3; i++) {
		float x = beginX + (i * hudSwitchSpacing);

		pgD.stroke(hudFontColor);
		pgD.strokeWeight(sliderThick);
		pgD.noFill();
		pgD.rect(x + hudSwitchSize / 2, y, hudSwitchSize, hudSwitchSize);

		// draw actual
		if (actualSwitches.charAt(i) == '1' ) {
			pgD.fill(hudSwitchFill);
			pgD.noStroke();
			pgD.rect(x + hudSwitchSize / 2, y, hudSwitchSize / 2, hudSwitchSize / 2);
		}

		// draw positive/negative feedback for switch correctness
		pgD.textAlign(LEFT);
		if (actualSwitches.charAt(i) == targetSwitches.charAt(i)) {
			// yep
			pgD.fill(0, 255, 0);
			pgD.text("Ok", x + hudSwitchSize * 1.5f, y + hudFontSize / 4);
		} else {
			pgD.fill(255, 0, 0);
			pgD.text("Error!", x + hudSwitchSize * 1.5f, y + hudFontSize / 4);
		}
	}

	pgD.endDraw();

	// send affirmatives
	for (int i = 0; i < 4; i++) {
		if (!eventReady[i]) {
			// for any event/resource that ISNT ready, check it agin
			if (i == 3) {	// switches?
				if (targetSwitches.equals(actualSwitches)) {
					eventReady[i] = true;
					// play affirmative sound, etc.
					if (OSC) {
						OscMessage m = new OscMessage("/pilot/affirmative");
						m.add(i);
						oscP5.send(m, PETER);
						oscP5.send(m, BASE);
						println("affirmative switch");
					}
				}
			} else {	// analogs?
				if (abs((target[i] - actual[i]) / range) < targetBuffer) {
					eventReady[i] = true;
					// play affirmative sound, etc.
					if (OSC) {
						OscMessage m = new OscMessage("/pilot/affirmative");
						m.add(i);
						oscP5.send(m, PETER);
						oscP5.send(m, BASE);
						println("affirmative analog/ " + millis());
					}
				}
			}
		}
		// reset if pilot screws up controls
		if (eventReady[i]) {
			if (i == 3) {	// switches?
				if (!targetSwitches.equals(actualSwitches)) {
					eventReady[i] = false;
				}
			} else {		// analogs?
				if (abs((target[i] - actual[i]) / range) > targetBuffer) {
					eventReady[i] = false;
				}
			}
		}
	}

	if (OSC)
		sendAll();

	drawHP(false);
}

public void drawHP(boolean doErase) {
	pgD.beginDraw();

	pgD.strokeWeight(sliderThick);
	
	if (doErase)
		pgD.background(0);
	pgD.textFont(fontGUI, hudFontSize * 2);
	pgD.textAlign(CENTER);
	pgD.fill(255, 0, 0);
	// nfs(life / startingLife * 100, 1, 2) draws two decimals
	pgD.text("HULL STRENGTH: " + PApplet.parseInt(life / startingLife * 100) + "%", width / 2 + 2, height - 50 + 2);
	pgD.fill(255);
	pgD.text("HULL STRENGTH: " + PApplet.parseInt(life / startingLife * 100) + "%", width / 2, height - 50);

	// draw timeline thing
	if (scene == 5) {
		pgD.stroke(180);
		pgD.line(100, 50, width - 100, 50);
		float shipX = map(timeInWormhole, 0, wormholeDuration, 100, width - 100);
		pgD.stroke(255, 255, 0);
		pgD.line(shipX, 40, shipX, 60);
	}

	// write affirmative messages to screen here
	pgD.textFont(fontGUI, hudFontSize * 2);
	pgD.textAlign(CENTER);
	pgD.rectMode(CENTER);
	pgD.stroke(255);
	if (affirm[0] && millis() - affirmTimer[0] < affirmDuration) {
		// hyperdrive good
		pgD.fill(0);
		pgD.rect(width / 2, height / 5 - hudFontSize / 2, width / 2, hudFontSize * 2);
		pgD.fill(255, 255, 0);
		pgD.text("HYPERDRIVE - OK!", width / 2, height / 5);
	}
	if (affirm[1] && millis() - affirmTimer[1] < affirmDuration) {
		// oxygen good
		pgD.fill(0);
		pgD.rect(width / 2, height / 5 * 2 - hudFontSize / 2, width / 2, hudFontSize * 2);
		pgD.fill(255, 255, 0);
		pgD.text("OXYGEN - OK!", width / 2, height / 5 * 2);
	}
	if (affirm[2] && millis() - affirmTimer[2] < affirmDuration) {
		// modulation good
		pgD.fill(0);
		pgD.rect(width / 2, height / 5 * 3 - hudFontSize / 2, width / 2, hudFontSize * 2);
		pgD.fill(255, 255, 0);
		pgD.text("MODULATION - OK!", width / 2, height / 5 * 3);
	}
	if (affirm[3] && millis() - affirmTimer[3] < affirmDuration) {
		// particle good
		pgD.fill(0);
		pgD.rect(width / 2, height / 5 * 4 - hudFontSize / 2, width / 2, hudFontSize * 2);
		pgD.fill(255, 255, 0);
		pgD.text("P. SPLITTER - OK!", width / 2, height / 5 * 4);
	}
	pgD.endDraw();
}
public void keyReleased() {

  if (parseInt(key) > 47 && parseInt(key) < 57) {
    scene = key - 48;
    println("scene " + scene);
  }

  // FX / OPTIONS
  if (key == 'd') {
    diagnostic = !diagnostic;
    eraseDiag();
  }

  int addCount = 10;

  // ADD STUFF
  if (scene == 5) {
    if (key == 'q') {
      //println("adding asteroid");
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(0));
      }
    }
    if (key == 'w' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(1));
      }
    }
    if (key == 'e' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(2)); // line monster
      }
    }
    if (key == 'r' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(3)); // triangle
      }
    }
    if (key == 't' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(4)); // streak
      }
    }
    if (key == 'y' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(5)); // corkscrew thing
      }
    }
    if (key == 'u' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(6));
      }
    }
    if (key == 'i' ) {
      for (int i = 0; i < addCount; i++) {
        asteroids.add(new Asteroid(7));
      }
    }
  }
}

public void keyPressed() {
  // diagnostic keys for changing oxygen, etc. values
  if (key == '[') {
    actual[0] -= 5;
    actual[0] = constrain(actual[0], 0, range);
  }
  if (key == ']') {
    actual[0] += 5;
    actual[0] = constrain(actual[0], 0, range);
  }

  if (key == ';') {
    actual[1] -= 5;
    actual[1] = constrain(actual[1], 0, range);
  }
  if (parseInt(key) == 39) {
    actual[1] += 5;
    actual[1] = constrain(actual[1], 0, range);
  }

  if (key == ',') {
    actual[2] -= 5;
    actual[2] = constrain(actual[2], 0, range);
  }
  if (key == '.') {
    actual[2] += 5;
    actual[2] = constrain(actual[2], 0, range);
  }

// switches
  if (key == 'z') {
    if (actualSwitches.charAt(0) == '0') {
      actualSwitches = "1" + actualSwitches.charAt(1) + actualSwitches.charAt(2);
    } else {
      actualSwitches = "0" + actualSwitches.charAt(1) + actualSwitches.charAt(2);
    }
  }
  if (key == 'x') {
    if (actualSwitches.charAt(1) == '0') {
      actualSwitches = actualSwitches.charAt(0) + "1" + actualSwitches.charAt(2);
    } else {
      actualSwitches = actualSwitches.charAt(0) + "0" + actualSwitches.charAt(2);
    }
  }
  if (key == 'c') {
    if (actualSwitches.charAt(2) == '0') {
      actualSwitches = "" + actualSwitches.charAt(0) + actualSwitches.charAt(1) + "1";
    } else {
      actualSwitches = "" + actualSwitches.charAt(0) + actualSwitches.charAt(1) + "0";
    }
  }

  // add crack ... ?
  if (key == 'v') {
    cracks.add(new Crack());
  }

  // DIFFICULTY OPTIONs
  if (key == CODED) {
    if (keyCode == DOWN) {
      whichDiff++;
    }
    if (keyCode == UP) {
      whichDiff--;
    }
  }
}
// need a buffer of OSC messages so we dont get concurrent mod error
ArrayList<OscMessage> oscStack = new ArrayList<OscMessage>();

volatile boolean isBusy = false;

/* incoming osc message are forwarded to the oscEvent method. */
public void oscEvent(OscMessage theOscMessage) {

	// add osc message to a stack so we can deal w them synchronously later
	if (!isBusy && scene == 5 ) {
		if (theOscMessage.addrPattern().equals("/peter/rhythm") || theOscMessage.addrPattern().equals("/peter/melody")) {
			oscStack.add(theOscMessage);
		}
	}

	// taking in values from monica based on arduino
	if (theOscMessage.addrPattern().equals("/base")) {
		for (int i = 0; i < actual.length; i++) {
			float percentage = theOscMessage.get(i).floatValue();
			actual[i] = percentage;
		}
		// the last one is a string of three digits
		actualSwitches = theOscMessage.get(3).stringValue();

		// the last one is self destruct...
		int selfDestructButton = theOscMessage.get(4).intValue();
	}

	// for scenes 0-3, houston controls scene change
	if (theOscMessage.addrPattern().equals("/base/scene")) {
		int newScene = theOscMessage.get(0).intValue();
		if (newScene < 4)
			scene = newScene;
		if (newScene == 8)
			scene = newScene;
	}
}

public void sendAll() {

	// 0: shake 0-1
	// 1: current time in wormhole
	// 2: life 0-1
	// 3: target hyper 4: actual hyper
	// 4: target oxyge 5: actual oxygen
	// 5: target mod   6: actual mod
	// 7: target switches 8: actual switches

	OscMessage m = new OscMessage("/pilot/data");
	m.add(map(lifeDist, 0, range * 3, 0, 1));	// shake 0-1, float
	m.add(PApplet.parseInt(currTime / 1000)); 			// current in wormhole (secs), int
	m.add(life / startingLife);	// life 0-1
	// send all targets and actuals 0-100, float
	// ordered: hyperdrive, oxygen, modulation
	for (int i = 0; i < target.length; i++) {
		m.add(target[i] / range);
		m.add(actual[i] / range);
	}
	m.add(targetSwitches);	// String
	m.add(actualSwitches);	// String
	m.add(map(lifeDist, 0, 315, 0, 1));	// difference between all targets and actuals
	m.add((approachingEnd) ? 1 : 0);	// 1 or zero true/false
	// to everyone, because why not
	oscP5.send(m, PETER);
	oscP5.send(m, BASE);
	if (scene == 5)
		oscP5.send(m, VDMX);
}
class Star {
	PVector pos;
	float s;	// size
	float p;	// phase
	float pSpeed;	// phase speed
	float shift;	// size shift
	int c;	// color

	Star() {
		pos = new PVector(random(-width / 3, width + width / 3), random(-height / 3, height + height / 3), -100);

		p = random(PI);	// start at random phase in sin wave

		c = color(random(200, 255), random(200, 255), random(200, 255));

		s = 1;

		pSpeed = random(.005f, .02f);
	}

	public void display() {
		p += pSpeed;	// increase phase
		shift = sin(p) * 4;	// pulsate star

		// draw star
		pg.pushMatrix();
		pg.translate(pos.x, pos.y, pos.z);

		// corona
		pg.noFill();
		pg.stroke(c);
		pg.strokeWeight(1);
		pg.ellipse(0, 0, s + shift * 2, s + shift * 2);

		pg.fill(c);
		pg.ellipse(0, 0, s + shift, s + shift);
		
		pg.popMatrix();
	}
}
public void waiting() {
	pg.beginDraw();
	pg.background(255);
	doText("WAITING FOR PILOT", color(255, 0, 0));
	pg.endDraw();
}
// think of it as 3 sliders, with target values and then actual values
// the goal is to get the actual to the target by adjusting sensors etc.
float range = 100;	// 0 - 9 range
float[] target = {range / 2, range / 2, range / 2};	// where value should be
float[] actual = {range / 2, range / 2, range / 2};	// where value is
// now we also have flip switches
String targetSwitches = "000";
String actualSwitches = "000";
int switchWeight = 5;	// how much damage does one off digit do?

float startingLife = 9000;	// varies by difficulty - 9000 is good
float life = startingLife;	// player hit points
float lifeDist = 0;	// for peter, what is diff between targets and actuals?
long lastLifeCheck;	// last millis when life was checked
int lifeCheckInterval = 1000;	// every second

int wormholeDuration = 150 * 1000;	// 2.5 minutes
long timeInWormhole;
boolean approachingEnd = false;

long startedWormhole = 0;	// when did we start wormhole?
float eventInterval = 2 * 1000;	// starting event interval
long lastEvent;	// when was the last 'error event'?
int lastEventType = 0;	// which system failed?

long lastOscSend = 0;	// only send out OSC messages every second

int bgColor = color(0);	// to fuck w bg color

float targetBuffer = .075f;	// how close target and actual needs to be
// also keep track of how long the stuff has been correct
boolean[] targetMonitor = {false, false, false, false};
long[] targetMonitorTime = {0, 0, 0, 0};
int targetTimeThreshold = 1000;	// 1 second to lock in values
boolean[] affirm = {false, false, false, false};
long[] affirmTimer = {0, 0, 0, 0};
int affirmDuration = 1000;

public void wormhole() {

	// this seems to get used a lot, so...
	timeInWormhole = millis() - startedWormhole;

	// go through OSC message stack
	isBusy = true;
	synchronized (oscStack) {
		for (OscMessage o : oscStack) {
			unPackOsc(o);	// in osc.pde for organization
		}
		for (int i = oscStack.size() - 1; i >= 0; i--) {
			oscStack.remove(i);
		}
	}
	isBusy = false;

	// add streaks
	for (int i = 0; i < 2; i++) {
		streaks.add(new Asteroid(0));
	}

	// remove old streaks
	for (int i = streaks.size() - 1; i >= 0; i--) {
		Asteroid a = streaks.get(i);
		if (a.dead()) {
			streaks.remove(i);
		}
	}

	// destroy any asteroids that are offscreen
	synchronized (asteroids) {
		for (int i = asteroids.size() - 1; i >= 0; i--) {
			Asteroid a = asteroids.get(i);
			if (a.dead()) {
				asteroids.remove(i);
			}
		}
	}

	// remove old stars from previous scene gradually
	if (timeInWormhole > 1000 && stars.size() > 0) {
		if (random(1) > .7f) {
			int rando = PApplet.parseInt(random(stars.size()));
			stars.remove(rando);
		}
	}

	// vars for end of wormhole
	float timeInWormholeSq = pow(timeInWormhole, 2);
	float durSq = pow(wormholeDuration, 2);

	pg.beginDraw();

	pg.background(bgColor);

	// render stars
	for (Star s : stars) {
		s.display();
	}

	// render streaks
	for (Asteroid a : streaks) {
		a.update();
		a.display();
	}

	if (wormholeDuration - timeInWormhole > wormholeDuration * .3f) {
		pg.noFill();
	} else {
		// last 30% of wormhole time, bright white light!
		pg.fill(map(timeInWormholeSq, durSq * .7f, durSq, 0, 22));
	}

	pg.strokeWeight(map(timeInWormholeSq, 0, durSq, .5f, 30));
	pg.stroke(100, 100, 255, 90);
	pg.pushMatrix();
	pg.translate(width / 2, height / 2, far);

	float temp = map(timeInWormholeSq, 0, durSq, 0, 3200);
	pg.ellipse(0, 0, temp, temp);
	pg.popMatrix();

	// update, display asteroids
	//synchronized (asteroids) {
	for (Asteroid a : asteroids) {
		a.update();
		a.display();
	}
	//}

	pg.endDraw();

	// and if we're REALLY CLOSE to the end
	if (wormholeDuration - timeInWormhole < wormholeDuration * .05f) {
		approachingEnd = true;
	}

	// reset background color for next time
	bgColor = color(0);

	if (diagnostic) {
		// draw sliders (diagnostic)
		gearTest();
	} else {
		eraseDiag();
		drawHP(true);
	}

	// check if the resources are properly set, as in
	// if oxygen was randomly messed with, and the pilot corrected it
	// set this resource/event to READY
	for (int i = 0; i < 4; i++) {
		if (!eventReady[i]) {
			// for any event/resource that ISNT ready, check it again
			if (i == 3) {	// switches?
				if (targetSwitches.equals(actualSwitches)) {
					// its in the zone, but needs to stay there for a bit
					if (!targetMonitor[i]) {
						targetMonitor[i] = true;
						targetMonitorTime[i] = millis();
					} else {
						// target already monitored, check time
						if (millis() - targetMonitorTime[i] > targetTimeThreshold) {
							eventReady[i] = true;
							// set up vars to display affirmative for a duration
							affirm[i] = true;
							affirmTimer[i] = millis();
							if (OSC) {
								OscMessage m = new OscMessage("/pilot/affirmative");
								m.add(i);
								oscP5.send(m, PETER);
								oscP5.send(m, BASE);
							}
						}
					}
				}
			} else {	// analogs?
				if (abs((target[i] - actual[i]) / range) < targetBuffer) {
					// its in the zone, but needs to stay there for a bit
					if (!targetMonitor[i]) {
						targetMonitor[i] = true;
						targetMonitorTime[i] = millis();
					} else {
						// target already monitored, check time
						if (millis() - targetMonitorTime[i] > targetTimeThreshold) {
							// its been there long enough, proceed
							eventReady[i] = true;

							// set up vars to display affirmative for a duration
							affirm[i] = true;
							affirmTimer[i] = millis();

							// play affirmative sound, etc.
							if (OSC) {
								OscMessage m = new OscMessage("/pilot/affirmative");
								m.add(i);
								oscP5.send(m, PETER);
								oscP5.send(m, BASE);
							}
						}
					}


				}
			}
			// give a second buffer between fixing a problem and it
			// potentially popping up again
			lastCheck[i] = millis();
		}
	}

	// !!! dont forget what this is ;p not for error handling
	doEvents();	// error events (resource changes, etc. THE MEAT!)


	//  +++ +++ +++ CHANGING LIFE +++ +++ +++

	if (millis() - lastLifeCheck >= lifeCheckInterval) {
		// reset timer
		lastLifeCheck = millis();

		// cycle and deduct
		lifeDist = 0;
		// the analog stuff
		for (int i = 0; i < target.length; i++) {
			if (abs((target[i] - actual[i]) / range) > targetBuffer) {
				lifeDist += abs(target[i] - actual[i]);
			}
		}
		// the switches
		int switchDist = 0;
		for (int i = 0; i < 3; i++) {
			switchDist += abs(actualSwitches.charAt(i) - targetSwitches.charAt(i));
		}
		lifeDist += (switchDist * switchWeight);
		life -= lifeDist;
	}

	// victory conditions
	if (timeInWormhole > wormholeDuration) {
		if (life > 0) {
			// you fuckin won!
			scene = 6;
		}
	}
	if (life < 0) {
		// you fuckin lost!
		scene = 7;
	}

	// send out necessary OSC data every X ms
	if (millis() - lastOscSend > 50 && OSC) {
		lastOscSend = millis();	// reset timer
		sendAll();	// broadcast all values
	}
}

public void unPackOsc(OscMessage o) {

	int i = o.get(0).intValue();

	// rhythm and melody come in 0-3 so add 4 if its melody
	if (o.addrPattern().equals("/peter/melody")) i += 4;

	if (asteroids.size() < maxAsteroids) {
		synchronized (asteroids) {
			asteroids.add(new Asteroid(i + 1));
		}
	}
}
  public void settings() {  size(1280,720, OPENGL); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "space_2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
