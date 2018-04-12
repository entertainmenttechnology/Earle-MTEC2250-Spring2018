

class Wave {

  float z;

  float d = whDiameter;  // diameter, actually radius?

  float s = 2;  // size for drawing points

  float[] p = new float[pointCount];  // phase
  float[] x = new float[pointCount];
  float[] y = new float[pointCount];
  float[] c = new float[pointCount];
  color[] col = new color[pointCount];

  // semi-random fluxuations for vertices
  float[] pointOffsetPhase = new float[pointCount];
  float[] pointOffsetSpeed = new float[pointCount];
  float[] pointOffsetMult = new float[pointCount];
  float[] pointOffset = new float[pointCount];  // random cave-in of node

  boolean turned = false;  // is it a rotated ring?

  Wave(float z) {
    this.z = z;
    this.d = whDiameter;

    for (int i = 0; i < p.length; i++) {
      p[i] = map(i, 0, pointCount, 0, TWO_PI) - phaseOffset;
      //c[i] = noise(cos(p[i]),sin(p[i])) * 255;
      c[i] = random(255);

      // now with color!
      //col[i] = color(noise(cos(p[i]), sin(p[i])) * 255, random(100, 255), abs(tan(p[i])) * 255);
      col[i] = color(random(255));

      //pointOffset[i] = random(-2, 2);
    }

    if (phaseOffset > 0) {
      turned = true;
    }
  }

  void update() {
    z += waveSpeed * deltaTime;
    for (int i = 0; i < p.length; i++) {
      // rotate vertex by increasing phase
      //p[i] += TWO_PI * .0025;
      p[i] += TWO_PI * rotateMult;
      // randomly increase phase (rotation) of vertex
      //p[i] += random(-.01, .01);
      // randomly change color of vertex
      //c[i] += random(-3, 3);

      // derive X and Y position from phase, using offset for movement
      x[i] = width / 2 + cos(p[i]) * (d + pointOffset[i]);
      y[i] = height / 2 + sin(p[i]) * (d + pointOffset[i]);

      // don't let point offset get too out of hand (close to center)
      pointOffset[i] = constrain(pointOffset[i], -30, 30);

    }
  }

  void display() {

    if (diagnostic) {
      for (int i = 0; i < p.length; i++) {
        pilot.fill(255);
        pilot.pushMatrix();
        pilot.translate(x[i], y[i], z);
        pilot.fill(255);
        pilot.ellipse(0, 0, s, s);
        pilot.fill(255, 255, 0);
        pilot.textFont(font_small, font_small_size);
        pilot.text(i, 2, 2);
        pilot.popMatrix();
      }
    }
  }
}