void keyReleased() {

  if (parseInt(key) > 47 && parseInt(key) < 57) {
    scene = key - 48;
    println("scene " + scene);
  }

  // FX / OPTIONS
  if (key == 'd') {
    diagnostic = !diagnostic;
    eraseDiag();
  }

  int addCount = 10;

  if (scene == 5) {

  }
}

void keyPressed() {
  // keys from makey makey (Houston)
  // w,s,f,g
  // down, up, right, left
  


  // ------ diagnostic keys for changing w/o Arduino -----
  // hyperdrive
  if (key == '[') {
    actual[0] -= 5;
    actual[0] = constrain(actual[0], 0, range);
  }
  if (key == ']') {
    actual[0] += 5;
    actual[0] = constrain(actual[0], 0, range);
  }
  // oxygen
  if (key == ';') {
    actual[1] -= 5;
    actual[1] = constrain(actual[1], 0, range);
  }
  if (parseInt(key) == 39) {
    actual[1] += 5;
    actual[1] = constrain(actual[1], 0, range);
  }
  // modulation
  if (key == ',') {
    actual[2] -= 5;
    actual[2] = constrain(actual[2], 0, range);
  }
  if (key == '.') {
    actual[2] += 5;
    actual[2] = constrain(actual[2], 0, range);
  }

  // switches
  if (key == 'z') {
    if (actualSwitches.charAt(0) == '0') {
      actualSwitches = "1" + actualSwitches.charAt(1) + actualSwitches.charAt(2);
    } else {
      actualSwitches = "0" + actualSwitches.charAt(1) + actualSwitches.charAt(2);
    }
  }
  if (key == 'x') {
    if (actualSwitches.charAt(1) == '0') {
      actualSwitches = actualSwitches.charAt(0) + "1" + actualSwitches.charAt(2);
    } else {
      actualSwitches = actualSwitches.charAt(0) + "0" + actualSwitches.charAt(2);
    }
  }
  if (key == 'c') {
    if (actualSwitches.charAt(2) == '0') {
      actualSwitches = "" + actualSwitches.charAt(0) + actualSwitches.charAt(1) + "1";
    } else {
      actualSwitches = "" + actualSwitches.charAt(0) + actualSwitches.charAt(1) + "0";
    }
  }
}