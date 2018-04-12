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

void doEvents() {

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
				if (target[rando] < .5 * range) {
					target[rando] = random(range * .5, range);
				} else {
					target[rando] = random(range * .5);
				}
				// bump it if necessary
				if (abs(target[rando] - tempTarget) < targetBuffer * range * 2) {
					if (random(1) > .5) {
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
				if (timeInWormhole > wormholeDuration * .5) {
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