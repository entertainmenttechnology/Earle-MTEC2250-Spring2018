boolean strobe = false;
int strobeRate = 5;
int strobeTrans = 127;
color strobeColor = color(255,0,0);

void doStrobe() {
	if (frameCount % strobeRate == 0) {
		pilot.background(strobeColor, strobeTrans);
	}
}