using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO.Ports;

public class serial : MonoBehaviour {

	public string PortName = "/dev/cu.usbmodem1421";

	SerialPort Stream = new SerialPort(PortName, 9600);

	// Use this for initialization
	void Start () {
		Stream.Open();
	}
	
	// Update is called once per frame
	void Update () {
		string val = Stream.ReadLine();

		// now break it up into multiple values
		string[] strArr = str.Split(",");

	}
}
