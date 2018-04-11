using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.IO.Ports;

public class serial : MonoBehaviour {

	public string PortName = "/dev/cu.usbmodem1421";

	SerialPort Stream;

	// Use this for initialization
	void Start () {

		GetPortNames ();

		Stream = new SerialPort(PortName, 9600);

		Stream.Open();
		Stream.DiscardInBuffer ();
	}
	
	// Update is called once per frame
	void Update () {
		string val = Stream.ReadLine ();

//		Debug.Log (val);

		// now break it up into multiple values
		string[] vals = val.Split (',');

		// but we want them as floats
		float x = float.Parse (vals [0]);
		float y = float.Parse (vals [1]);
		float z = float.Parse (vals [2]);

		x = Map (x, 0f, 1023f, 0.0f, 360.0f);
		y = Map (y, 0f, 1023f, 0.0f, 360.0f);
		z = Map (z, 0f, 1023f, 0.0f, 360.0f);

		Debug.Log (x + ", " + y + ", " + z);

		// clear the serial
		Stream.BaseStream.Flush ();

		transform.eulerAngles = new Vector3 (x, y, z);

	}


	void GetPortNames () {
		int p = (int)System.Environment.OSVersion.Platform;
		List<string> serial_ports = new List<string> ();

		// Are we on Unix?
		if (p == 4 || p == 128 || p == 6) {
			string[] ttys = System.IO.Directory.GetFiles ("/dev/", "tty.*");
			foreach (string dev in ttys) {
				if (dev.StartsWith ("/dev/tty.*"))
					serial_ports.Add (dev);
				Debug.Log (System.String.Format (dev));
				if (dev.Contains ("usb"))
					PortName = dev;
			}
		}
	}

	float Map(float value, float from2, float to2, float from, float to){
		if(value <= from2){
			return from;
		}else if(value >= to2){
			return to;
		}else{
			return (to - from) * ((value - from2) / (to2 - from2)) + from;
		}
	}
}
