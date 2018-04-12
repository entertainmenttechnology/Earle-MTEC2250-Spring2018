class Star {
	PVector pos;
	float s;	// size
	float p;	// phase
	float pSpeed;	// phase speed
	float shift;	// size shift
	color c;	// color

	Star() {
		pos = new PVector(random(-width / 3, width + width / 3), random(-height / 3, height + height / 3), -100);

		p = random(PI);	// start at random phase in sin wave

		c = color(random(200, 255), random(200, 255), random(200, 255));

		s = 1;

		pSpeed = random(.005, .02);
	}

	void display() {
		p += pSpeed;	// increase phase
		shift = sin(p) * 4;	// pulsate star

		// draw star
		pilot.pushMatrix();
		pilot.translate(pos.x, pos.y, pos.z);

		// corona
		pilot.noFill();
		pilot.stroke(c);
		pilot.strokeWeight(1);
		pilot.ellipse(0, 0, s + shift * 2, s + shift * 2);

		pilot.fill(c);
		pilot.ellipse(0, 0, s + shift, s + shift);
		
		pilot.popMatrix();
	}
}