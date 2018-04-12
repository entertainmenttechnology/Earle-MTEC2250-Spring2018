class Particle {

  float x, y, z;

  float speed, size;

  float warp;  // what effect it has on the wave vertices

  // aesthetic stuff for visible particles
  int type;
  color cFill, cStroke;
  boolean ROTATE = false;
  boolean PHASE = false;
  float growthRate = .01;
  float rotation = 0;
  float rotateSpeed = .01;
  float phase = 0;
  float pAdjust = 0;
  float pRate = .01;
  float sw; // stroke weight

  Particle(int type) {
    z = far;
    // spawn it near the center
    x = random(width * .3, width * .7);
    y = random(height * .3, height * .7);
    //speed = random(10, waveSpeed);  //waveSpeed/2
    speed = waveSpeed;
    //speed = waveSpeed/2;
    //size = map(waveSpeed, minWaveSpeed, maxWaveSpeed, 60, 250);

    // how much this will effect the vertices
    if (random(1) > .5) {
      warp = 5;
    } else {
      warp = -5;
    }

    this.type = type;

    switch (type) {
    case 0: // WHITE STAR STREAKS
      size = 1;
      sw = 2;
      break;
    case 1: // BLACK ROTATING CUBES W WHITE OUTLINE
      sw = 1;
      cFill = color(0);
      cStroke = color(255);
      ROTATE = true;
      growthRate = .3;
      size = .02;
      break;
    case 2: // major warper
      size = 100;
      speed = waveSpeed * 4;
      cFill = color(random(255), random(255), random(255));
      break;
    }
  }

  void update() {
    // move forward
    z += speed * deltaTime;

    size += growthRate;

    // rotating asteroids
    if (ROTATE) {
      rotation += rotateSpeed;
    }
    // phasing asteroids
    if (PHASE) {
      phase += pRate; // inc phase
      pAdjust += sin(phase) * .5;
    }

    // go thru each wave
    for (Wave wave : waves) {
      // if it's close enough...
      if (abs(wave.z - z) < size) {
        // go thru this wave's vertices
        for (int i = 0; i < pointCount; i++) {
          // if its close enough...
          if (dist(x, y, wave.x[i], wave.y[i]) < size) {
            // distort vertex
            wave.pointOffset[i] += warp;
            // change color
            //wave.c[i] += warp * 3;
            wave.col[i] = lerpColor(wave.col[i], cFill, .2);
          }
        }
      }
    }
  }

  void display() {
    switch (type) {
    case 0: // WHITE STAR STREAK
      pilot.pushMatrix();
      pilot.translate(x, y, z);
      pilot.stroke(255);
      pilot.strokeWeight(sw);
      pilot.line(0, 0, 0, 0, 0, -10);
      pilot.popMatrix();
      break;

    case 1: // BLACK ROTATING CUBES W WHITE OUTLINE
      pilot.pushMatrix();
      pilot.translate(x, y, z);
      pilot.rotateY(rotation);
      pilot.rotateZ(rotation * 2);
      pilot.rotateX(rotation * 3);
      pilot.fill(cFill, 100);
      pilot.strokeWeight(sw);
      pilot.stroke(cStroke);
      pilot.box(size + pAdjust);
      pilot.popMatrix();
      break;

    case 2: // major warper
      // pilot.pushMatrix();
      // pilot.translate(x, y, z);
      // pilot.fill(255,0,0,100);
      // pilot.noStroke();
      // pilot.sphere(size);
      // pilot.popMatrix();
      break;
    }

    // pilot.pushMatrix();
    // pilot.translate(x, y, z);
    // pilot.noFill();
    // pilot.sphereDetail(8);
    // pilot.stroke(255, 0, 0, 50);
    // pilot.sphere(size);
    // pilot.popMatrix();
  }
}