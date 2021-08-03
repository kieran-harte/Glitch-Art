// Reverses horizontal segments of the image
void lineReverse(int numLines) {

  for (int iterations=0; iterations < numLines; iterations++) {

    int lines = (int)random(20, 400);
    int startLine = (int)random(0, (canvas.height-lines)-1);

    for (int c=startLine; c<startLine+lines; c++) {

      int startPoint = c*canvas.width;
      int endOfLine = startPoint+canvas.width-(startPoint%canvas.width);

      int[] linePixels = new int[canvas.width];
      for (int i=startPoint; i<endOfLine; i++) {
        linePixels[i-startPoint] = canvas.pixels[i];
      }

      for (int i=endOfLine-1; i>startPoint; i--) {
        canvas.pixels[startPoint+(endOfLine-i)] = linePixels[i-startPoint];
      }
    }
  }
}

// Sorts the pixels in a horizontal line
void lineSort(int numLines) {

  for (int i=0; i<numLines; i++) {

    int line = (int)random(0, canvas.height-1);
    int[] linePixels = new int[canvas.width];

    // Extract each pixel in a line to the linePixels[] array
    for (int pix=line*canvas.width; pix<(line+1)*canvas.width; pix++) {
      linePixels[pix-(line*canvas.width)] = canvas.pixels[pix];
    }

    // Sort the pixels
    linePixels = sort(linePixels);

    // Put the new values back into the pixels[] array
    for (int pix=line*canvas.width; pix<(line+1)*canvas.width; pix++) {
      canvas.pixels[pix] = linePixels[pix-(line*canvas.width)];
    }
  }
}

// Switches the rgb channels of random blocks of the image
void channelSwitch(int iterations) {
  colorMode(RGB, 255);
  pushMatrix();

  for (int i=0; i<iterations; i++) {

    int lStart = (int)random(0, canvas.height-1);
    int lEnd = lStart + (int)random(0, (canvas.height-lStart) -1);
    int r1 = (int)random(0, canvas.width-1);
    int r2 = (int)random(0, canvas.width-1);

    // Randomly shifts the ratios of each channel a little
    float shiftR = random(0.9, 1.1);
    float shiftG = random(0.9, 1.1);
    float shiftB = 3-shiftR-shiftG;

    for (int l=lStart; l<lEnd; l++) {

      int start = r1 + (l*canvas.width);
      int end = r2 + (l*canvas.width);

      for (int p=start; p<end; p++) {

        canvas.pixels[p] = color(green(canvas.pixels[p])*shiftG, blue(canvas.pixels[p])*shiftB, red(canvas.pixels[p])*shiftR);
      }
    }
  }

  popMatrix();
}

// Offsets the hue and brightness of the image
void hueShift(int offset) {
  colorMode(HSB, 255);
  int p = (canvas.pixels.length-offset)%canvas.pixels.length;
  for (int i=0; i<canvas.pixels.length; i++) {
    canvas.pixels[i] = color(
      hue(canvas.pixels[p]), 
      saturation(canvas.pixels[i]), 
      brightness(canvas.pixels[p]));
    p = (p+1)%canvas.pixels.length;
  }
}

// Multiplies rows of pixels by a value then mods it
void pixelMult(int iterations) {
  pushMatrix();
  colorMode(RGB, 255);
  for (int c=0; c<iterations; c++) {
    int r1 = (int)random(1, canvas.height/2) -1;
    int r2 =  r1 + (int)random(2, canvas.height/20) -1;
    for (int i=r1; i<r2; i++) {
      for (int p=0; p<canvas.width; p++) {
        canvas.pixels[(i*canvas.width)+p] = color(32, 255, 200) * (abs(canvas.pixels[(i*canvas.width)+p])+1)%255255255;
      }
    }
  }

  popMatrix();
}

// Averages each pixel and the previous pixel in a line
void lineGlitch(int numLines) {
  for (int c=0; c<numLines; c++) {
    int startPoint = (int)random(1, canvas.pixels.length-100);
    int endOfLine = startPoint+canvas.width-(startPoint%canvas.width);

    for (int i=startPoint; i<endOfLine; i++) {
      canvas.pixels[i] = (canvas.pixels[i-1] + canvas.pixels[i])/ 2;
    }
  }
}
