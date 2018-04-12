# wurm_processing
WURM: Escape from a Dying Star p5 engine

[Click here](https://www.youtube.com/watch?v=9EvH_BHkFEQ&feature=youtu.be) to view a quick setup between VDMX, the WURM Processing engine, and the OSC emulator

##VDMX
[VDMX](http://vidvox.net) is used for two main reasons: 1) To enable projection mapping (for free) and 2) as a means of adding more visual effects
such as camera shake, kaleidoscope, etc.

The free version of VDMX cannot save, but it can open files. It's preferences will save, which is good news. To get setup:

1. Run 720p.vdmx (click smart relink if prompted)
2. Navigate to VMDX -> Preferences
3. Click on OSC
4. Make sure to add '1234' as an incoming OSC port
5. (OPTIONAL) To use projection mapping, with the "Layers" tab selected click on "Mapping"
6. (OPTIONAL) Under Mapping Composition click "Quad" and then drag corners as needed
7. To fullscreen, click Window -> Fullscreen Options in the menu bar
8. Click the fullscreen tab, click the box for whichever display the projector is on

##OSC Emulator
OSC Emulator emulates the signals that normally come from the WURM audio engine (in Pure Data). This can be used to test functionality before setting up audio, or to diagnose problems concerning OSC connections. So don't leave this running if the audio engine is running. In other words, it makes asteroids appear in the Processing engine when the audio engine is not operating.

##Processing Engine
Written in [Processing](http://processing.org) the engine uses Syphon to send the video output into VDMX, and uses OSC to transmit and receive messages. To set up:

1. Find this section of text:
```
String PETER_IP = "169.254.128.132";
String BASE_IP = "169.254.208.46";
String VDMX_IP = "127.0.0.1"; // (localhost)
```

2. Change PETER_IP to the IP address of the computer running Pure Data (if same computer, use 127.0.0.1)
3. Change BASE_IP to the IP address of the computer running Houston
4. Run the sketch, hop over to VDMX and insure that Syphon is indeed sending frames to VDMX. It should show "Waiting for Pilot"

Next, you can issue commands to test the Processing engine sketch using the keyboard. Make sure the Java application window is active (rather than VDMX) and use any of the following commands:
- Numbers 0 thru 8 select the associated scene (5 is inside the wormhole)
- 'd' Runs diagnostics, which displays your oxygen, hyperdrive, etc. while inside the wormhole
- brackets ] and [ control hyperdrive
- ; and ' control oxygen
- , and . control modulation
- 'z' 'x' and 'c' control the particle splitters
