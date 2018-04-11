# Physical Computing / Projection Mapping

## Goal

Use an Arduino -> Processing/Unity -> Projection Mapping exercise as an interesting way to explore the signal chain that moves from hardware components to high level software.

## Demo

+ Projection mapping boxes based on potentiometer values
	+ [processing code](processing/colorbox_3x)
	+ [arduino code](arduino/potentiometer3x)

Signal flow:
![signal flow w syphon](flow_syphon.png)

1. Potentiometer
	+ Turned by the user, changing the output voltage on the middle pin.
2. Arduino
	+ Senses the potentiometer middle pin. Allows you to measure the voltage as a number between 0 -> 1023.
	+ Using Serial communication, you can output values from Arduino to Processing.
3. Processing (or Unity)
	+ Using Serial communication, you can input values from Arduino. You can then use these values to create audiovisual output.
	+ Optionally, if you would like to use projection mapping, you can use Syphon to publish the visual output of your Processing sketch to other applications.
4. MadMapper
	+ Allows you to use video sources (including Syphon) and warp them to match the geometry of a room or set of objects.

Wiring:
![wiring guide](tinkercad1.png)

## Relevant Work

+ WURM
+ 

The first half has to be about projection mapping

Signal flow:
1. Arduino interpretting analog reading from potentiometer
2. Arduino sends converted value (byte) over Serial
3. MAX/MSP (or Processing) reads incoming Serial data

Ideas:
+ VJing
	+ strobing / pulsing
	+ changing colors
+ Unity
	+ change speed at which blocks fall
+ Projection mapping
	+ Corner pinning
+ Smart objects~
	+ Project down onto a space that has a potentiomer, scrolling the pot projects an increasing arc. Or project just above it so that my hand doesn't interrupt
	+ Live color mixing