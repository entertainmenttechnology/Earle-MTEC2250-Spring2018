using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.IO.Ports;

public class serial_gyro : MonoBehaviour {

	public string PortName = "/dev/cu.usbmodem1421";

	SerialPort Stream;

	public float xOffset = 0.0f;
	public float yOffset = 0.0f;
	public float zOffset = 0.0f;

	private float pX = 0.0f;
	private float pY = 0.0f;
	private float pZ = 0.0f;

	public bool Calibrate = false;

	// Use this for initialization
	void Start () {

		GetPortNames ();

		Stream = new SerialPort(PortName, 115200);

		Stream.Open();
		Stream.DiscardInBuffer ();
	}

	// Update is called once per frame
	void Update () {
			string val = Stream.ReadLine ();

			//		Debug.Log (val);

			// now break it up into multiple values
			string[] vals = val.Split (',');

		if (vals.Length > 2) {

			// but we want them as floats
//			float inx = GetFloat (vals [0], pX);
			float iny = GetFloat (vals [1], pY);
			float inz = GetFloat (vals [2], pZ);

			if(Calibrate) {
				// calibration
//				xOffset = inx * -1.0f;
				yOffset = iny * -1.0f;
				zOffset = inz * -1.0f;

				Calibrate = false;
			}

//			Debug.Log (inz);

//			float x = (inx * .1f) + (pX * .9f);
			float y = (iny * .1f) + (pY * .9f);
			float z = (inz * .1f) + (pZ * .9f);

//			x += xOffset;
			y += yOffset;
			z += zOffset;

//			pX = x;
			pY = y;
			pZ = z;
	
//			Debug.Log (x + ", " + y + ", " + z);

			// clear the serial
			Stream.BaseStream.Flush ();
			Stream.DiscardInBuffer ();

			transform.eulerAngles = new Vector3 (z, 0f, y);
		}


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

	private float GetFloat(string stringValue, float defaultValue)
	{
		float result = defaultValue;
		float.TryParse(stringValue, out result);
		return result;
	}
}
