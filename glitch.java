import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import de.bezier.guido.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class glitch extends PApplet {

 // Video library must be installed
  // GUIDO library required for the UI

PImage img, img_1, img_2, img_3, img_4, img_5, img_6, img_7, img_8, img_9, img_0;
HashMap<String, PImage> loadedImages = new HashMap<String, PImage>();
PGraphics canvas, pg_options, pg_menu, pg_loading;
String imageToLoad;
float pd = 1; // This is used to scale the UI for different screen resolutions

// Webcam
boolean camAvailable = false;
Capture cam;
String[] cameras;

PFont font;

boolean recomputeGlitch = true;
boolean drawCanvas = true;
boolean drawMenuButton = true;
boolean menuOpen = true;
boolean loading = true;
boolean randomGlitch = true;
String overlayText = "";
int selectedImgId = 1;

// Menu elements
Button menuButton;
Slider s1, s2, s3, s4, s5, s6;
UpdateButton mbUpdate;
RandomButton randomButton;
CheckBox cbShowText;
SaveButton saveButton;
OpenButton openButton;
CamButton camButton;


public void setup() {
  
  background(0);
  

  // Multiply by pd to scale things for high density display
  pd = (float)displayWidth / 1920;

  // Create Font 
  font = createFont("Roboto", 42*pd);
  textFont(font);

  // Check for webcams
  cameras = Capture.list();
  cameras = sort(cameras);
  for (String c : cameras) println(c);
  camAvailable = cameras.length > 0 ? true : false;

  // Create menu elements
  Interactive.make(this);
  menuButton = new Button(10*pd, 10*pd, 40*pd, 40*pd);
  menuButton.hide();
  // Sliders
  s1 = new Slider(10*pd, 150*pd, 280*pd, 20*pd);
  s1.setLabel("Pixel Multiply");
  s1.setRange(0, 10);
  s1.hide();
  s2 = new Slider(10*pd, 225*pd, 280*pd, 20*pd);
  s2.setLabel("Reverse");
  s2.setRange(0, 10);
  s2.hide();
  s3 = new Slider(10*pd, 300*pd, 280*pd, 20*pd);
  s3.setLabel("Hue&Brightness Shift");
  s3.setRange(0, 0.999f);
  s3.hide();
  s4 = new Slider(10*pd, 375*pd, 280*pd, 20*pd);
  s4.setLabel("Channel Switch");
  s4.setRange(0, 10);
  s4.hide();
  s5 = new Slider(10*pd, 450*pd, 280*pd, 20*pd);
  s5.setLabel("Line Sort");
  s5.setRange(0, 50);
  s5.hide();
  s6 = new Slider(10*pd, 525*pd, 280*pd, 20*pd);
  s6.setLabel("Line Glitch"); 
  s6.setRange(0, 50);
  s6.hide();
  cbShowText = new CheckBox("Show text", 10*pd, 575*pd, 15*pd, 15*pd);
  cbShowText.hide();
  // Buttons
  mbUpdate = new UpdateButton(10*pd, height-(50*pd), 280*pd, 40*pd);
  mbUpdate.setText("Update (U)");
  mbUpdate.hide();
  randomButton = new RandomButton(10*pd, height-(100*pd), 280*pd, 40*pd);
  randomButton.setText("Random (R)");
  randomButton.hide();
  saveButton = new SaveButton(10*pd, height-(150*pd), 280*pd, 40*pd);
  saveButton.setText("Save (S)");
  saveButton.hide();
  openButton = new OpenButton(10*pd, height-(200*pd), 280*pd, 40*pd);
  openButton.setText("Open (O)");
  openButton.hide();
  camButton = new CamButton(10*pd, height-(250*pd), 280*pd, 40*pd);
  camButton.setText("Webcam (W)");
  camButton.hide();

  // Initialise with random values
  randomiseParameters();
  randomGlitch = true;
}

// Fullscreen
public void settings() {
  fullScreen();
}

// Sets random values for the effect sliders
public void randomiseParameters() {
  s1.setValue(random(1, 4));
  s2.setValue(4);
  s3.setValue(random(0.625f, 0.909f));
  s4.setValue(floor(random(0, 2))==0 ? 4 : 0);
  s5.setValue(10);
  s6.setValue(random(1, 35));
}

// Called to update changes to the screen
public void draw() {  

  // resets the canvas each frame, but not for the loading screen as this is overlayed on top of whats already on the screen 
  if (!loading)background(0);

  // Load first image if there is no image displayed
  if (img == null) {
    loadImageThreaded("city.bmp");
  }

  // Canvas
  if (img != null) drawCanvas();

  // Menu
  if (menuOpen && img != null) {
    drawMenu();
  }

  // Loading
  if (loading) {
    drawLoading();
  }

  // Menu Button
  if (!loading) {
    menuButton.show();
  }

  // Screen is updated manually
  noLoop();
}

// Loads an image asynchronously given a path
public void loadImageThreaded(String path) {
  loading = true;
  redraw();
  imageToLoad = path;
  thread("_loadImageThreaded");
}

// Reloads whichever image is already displayed
public void loadImageThreaded() {
  loadImageThreaded(imageToLoad);
}

// Thread for loading of image and applying of glitch
public void _loadImageThreaded() {
  if (loadedImages.containsKey(imageToLoad)) {
    img = loadedImages.get(imageToLoad);
  } else if (imageToLoad != "custom") {
    // 'custom' is used for the webcam and manually opened images, where 'img' is already set.
    img = loadImage(imageToLoad);
    loadedImages.put(imageToLoad, img);
  }

  background(0);
  // Set text to be overlaid
  switch(imageToLoad) {
  case "city.bmp":
  case "street.bmp":
  case "ny.bmp":
    overlayText = "NYC";
    break;
  case "ggb2.bmp":
    overlayText = "BRIDGE";
    break;
  case "paris4.bmp":
    overlayText = "PARIS";
    break;
  case "earth.bmp":
    overlayText = "EARTH";
    break;
  case "liberty.bmp":
    overlayText = "NEW YORK";
    break;
  case "tiger.bmp":
    overlayText = "TIGER";
    break;
  case "model.bmp":
    overlayText = "MODEL";
    break;
  case "sm.bmp":
    overlayText = "CALIFORNIA";
    break;
  default:
    overlayText = "GLITCH";
  }

  computeGlitch();
  loading = false;
  redraw();
}

// Applies the glitching effects
public void computeGlitch() {
  // Scale to fit image to the window with no cropping
  float screenRatio = (float)width/height;
  float imageRatio = (float)img.width/img.height;
  float scaleFactor = 1.0f;
  if (imageRatio < screenRatio) {
    scaleFactor = (float)img.height/height;
  } else {
    scaleFactor = (float)img.width/width;
  }

  // Glitch pgraphic on canvas
  canvas = createGraphics((int)(img.width/scaleFactor), (int)(img.height/scaleFactor));
  canvas.beginDraw();
  canvas.image(img, 0, 0, canvas.width, canvas.height);

  // Add text over image before glitching
  if (cbShowText.isChecked()) {
    canvas.textSize(170);
    canvas.textAlign(CENTER, CENTER);
    canvas.fill(0, 125);
    canvas.text(overlayText, random(canvas.width/4, canvas.width/1.333f), random(canvas.height/4, canvas.height/1.333f));
  }

  // Loads pixels into the pixels[] array
  canvas.loadPixels();
  colorMode(HSB, 255);

  // Glitch!
  if (randomGlitch) {
    randomiseParameters();
  }

  pixelMult((int)s1.getValue());
  channelSwitch((int)s4.getValue());
  lineGlitch((int)s6.getValue());

  hueShift((int)(canvas.pixels.length*s3.getValue()));
  hueShift((int)(canvas.pixels.length*s3.getValue()));

  lineGlitch((int)s6.getValue());
  lineReverse((int)s2.getValue());
  lineSort((int)s5.getValue());

  canvas.updatePixels();
  canvas.endDraw();
}

// Draws the canvas (glitched image) to the screen.
public void drawCanvas() {
  image(canvas, (width-canvas.width)/2, (height-canvas.height)/2);
}

// Draws the button which opens/closes the menu
public void drawMenuButton() {
  pg_options = createGraphics(100, 100);
  pg_options.beginDraw();
  pg_options.fill(255, 0, 0, 125);
  pg_options.rect(0, 0, 100, 100);
  pg_options.endDraw();
  image(pg_options, 0, 0);
}

public void drawMenu() {
  pg_menu = createGraphics((int)(400*pd), height);
  pg_menu.beginDraw();
  pg_menu.fill(0, 0, 0, 230);
  pg_menu.rect(0, 0, 300*pd, height);
  pg_menu.textSize(18*pd);
  pg_menu.fill(255);
  pg_menu.text( (selectedImgId <= 0 ? (selectedImgId == 0 ? "Webcam" : "Custom") : selectedImgId + "/10") + "\nUse number keys to select \nimages", 10*pd, 675*pd);
  pg_menu.endDraw();
  image(pg_menu, 0, 0);

  // Show the sliders and buttons
  s1.show();  
  s2.show(); 
  s3.show();  
  s4.show();
  s5.show(); 
  s6.show(); 
  camButton.show();
  openButton.show();
  saveButton.show();
  cbShowText.show();
  mbUpdate.show();  
  randomButton.show();
}

// Renders the loading screen
public void drawLoading() {
  pg_loading = createGraphics(width, height);
  pg_loading.beginDraw();
  pg_loading.background(0, 100);
  pg_loading.fill(255);
  pg_loading.textAlign(CENTER, CENTER);
  pg_loading.textSize(32*pd);
  pg_loading.text("loading...", pg_loading.width/2, pg_loading.height/2);
  pg_loading.endDraw();
  image(pg_loading, 0, 0);
}


public void keyPressed() {
  if (loading) return;
  switch(key) {
  case 'r':
    randomGlitch = true;
    loadImageThreaded();
    break;
  case 'u':
    randomGlitch = false;
    loadImageThreaded();
    break;
  case 's':
    selectOutput("Select file to save to:", "saveImage", new File(sketchPath()+"/"+ System.currentTimeMillis()/1000L +".bmp"));
    break;
  case 'o':
    selectInput("Select image to open:", "openImage");
    break;
  case '1':
    selectedImgId = 1;
    loadImageThreaded("city.bmp");
    break;
  case '2':
    selectedImgId = 2;
    loadImageThreaded("ggb2.bmp");
    break;
  case '3':
    selectedImgId = 3;
    loadImageThreaded("paris4.bmp");
    break;
  case '4':
    selectedImgId = 4;
    loadImageThreaded("earth.bmp");
    break;
  case '5':
    selectedImgId = 5;
    loadImageThreaded("street.bmp");
    break;
  case '6':
    selectedImgId = 6;
    loadImageThreaded("liberty.bmp");
    break;
  case '7':
    selectedImgId = 7;
    loadImageThreaded("ny.bmp");
    break;
  case '8':
    selectedImgId = 8;
    loadImageThreaded("tiger.bmp");
    break;
  case '9':
    selectedImgId = 9;
    loadImageThreaded("model.bmp");
    break;
  case '0':
    selectedImgId = 10;
    loadImageThreaded("sm.bmp");
    break;
  case 'w':
    initCam();
    break;
  }
}

// Saves the image to the given file
public void saveImage(File f) {
  if (f!=null) canvas.save(f.getAbsolutePath());
}

// Opens the image from the given file path
public void openImage(File f) {
  selectedImgId = -1;
  if (f!=null) loadImageThreaded(f.getAbsolutePath());
}

// Initialises the webcam/ check if there are any available
public void initCam() {
  if (!camAvailable) return;

  if (cam == null) {
    cam = new Capture(this, cameras[0]);
    cam.start();
  }

  while (cam.available() != true) {
    delay(500);
  }

  if (cam.available() == true) {
    cam.read();
    img = cam;
    selectedImgId = 0;
    loadImageThreaded("custom");
  }
}
// Note: Extending classes does not work with the Button class so there is a whole new class for each button
// This code is modified from the GUDIO library; https://github.com/fjenett/Guido

public class UpdateButton {
  float x, y, width, height;
  boolean on;
  boolean hidden = false;
  String text = "";

  UpdateButton ( float _x, float _y, float _w, float _h ) {
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    Interactive.add( this );
  }

  public void mousePressed () {
    randomGlitch = false;
    loadImageThreaded();
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5f);
      popMatrix();
    }

    this.hide();
  }
}

public class RandomButton {
  float x, y, width, height;
  boolean on;
  boolean hidden = false;
  String text = "";

  RandomButton ( float _x, float _y, float _w, float _h ) {
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    Interactive.add( this );
  }

  public void mousePressed () {
    randomGlitch = true;
    loadImageThreaded();
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5f);
      popMatrix();
    }

    this.hide();
  }
}


public class SaveButton {
  float x, y, width, height;
  boolean on;
  boolean hidden = false;
  String text = "";

  SaveButton ( float xx, float yy, float ww, float hh ) {
    x = xx; 
    y = yy; 
    width = ww; 
    height = hh;
    Interactive.add(this);
  }

  public void mousePressed () {
    selectOutput("Select file to save to:", "saveImage", new File(sketchPath()+"/"+ System.currentTimeMillis()/1000L +".bmp"));
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5f);
      popMatrix();
    }

    this.hide();
  }
}



public class CamButton {
  float x, y, width, height;
  boolean on;
  boolean hidden = false;
  String text = "";

  CamButton ( float _x, float _y, float _w, float _h ) {
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    Interactive.add(this);
  }

  public void mousePressed () {
    initCam();
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void draw () {
    if (hidden)return;

    fill( camAvailable ? 230 : 150 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5f);
      popMatrix();
    }

    this.hide();
  }
}


public class OpenButton {
  float x, y, width, height;
  boolean on;
  boolean hidden = false;
  String text = "";

  OpenButton ( float _x, float _y, float _w, float _h ) {
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    Interactive.add(this);
  }

  public void mousePressed () {
    selectInput("Select image to open:", "openImage");
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5f);
      popMatrix();
    }

    this.hide();
  }
}
// Reverses horizontal segments of the image
public void lineReverse(int numLines) {

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
public void lineSort(int numLines) {

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
public void channelSwitch(int iterations) {
  colorMode(RGB, 255);
  pushMatrix();

  for (int i=0; i<iterations; i++) {

    int lStart = (int)random(0, canvas.height-1);
    int lEnd = lStart + (int)random(0, (canvas.height-lStart) -1);
    int r1 = (int)random(0, canvas.width-1);
    int r2 = (int)random(0, canvas.width-1);

    // Randomly shifts the ratios of each channel a little
    float shiftR = random(0.9f, 1.1f);
    float shiftG = random(0.9f, 1.1f);
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
public void hueShift(int offset) {
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
public void pixelMult(int iterations) {
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
public void lineGlitch(int numLines) {
  for (int c=0; c<numLines; c++) {
    int startPoint = (int)random(1, canvas.pixels.length-100);
    int endOfLine = startPoint+canvas.width-(startPoint%canvas.width);

    for (int i=startPoint; i<endOfLine; i++) {
      canvas.pixels[i] = (canvas.pixels[i-1] + canvas.pixels[i])/ 2;
    }
  }
}
// This code uses the GUDIO library; https://github.com/fjenett/Guido

public class Button {
  float x, y, width, height;
  boolean on = true;
  boolean hidden = false;

  Button(float _x, float _y, float _w, float _h) {
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    Interactive.add(this);
  }

  public void mousePressed () {
    menuOpen = !menuOpen;
    on = !on;
    redraw();
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void draw () {
    fill(0);
    noStroke();
    //rect( x, y, width+1, height+1);
    if (hidden)return;
    if (on) {
      stroke(255);
      strokeWeight(2);
      line(x, y, x+width, y + height);
      line(x+width, y, x, y+ height);
    } else {
      strokeWeight(2);
      stroke(255);
      line(x, y, x + width, y);
      line(x, y+height/2, x + width, y + height/2);
      line(x, y + height, x + width, y + height);
    }
  }
}

public class Slider {
  float x, y, width, height;
  float valueX = 0;
  float value;
  boolean hidden = false;
  String label = "";
  float min, max;

  Slider(float _x, float _y, float _w, float _h) {
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    valueX = x;
    Interactive.add( this );
  }

  public void mouseDragged(float mx, float my, float dx, float dy) {

    if (!menuOpen) return;

    valueX = mx - height/2;

    if ( valueX < x ) valueX = x;
    if ( valueX > x+width-height ) valueX = x+width-height;

    value = map( valueX, x, x+width-height, min, max );

    redraw();
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setRange(float min, float max) {
    this.min = min;
    this.max = max;
    this.value = min; // initialise value
  }

  public float getValue() {
    return this.value;
  }

  public void setValue(float value) {
    this.value = constrain(value, this.min, this.max);
    valueX = map(this.value, min, max, x, x+width-height);
  }

  public void draw () {
    if (hidden) return;
    noStroke();

    fill(100);
    rect(x, y, width, height);

    fill(160);
    rect(valueX, y, height, height);

    if (label != "") {
      textAlign(LEFT, BASELINE);
      fill(255);
      textSize(24*pd);
      text(label, x, y - 10*pd);
    }

    this.hide();
  }
}


public class CheckBox {
  boolean checked = true;
  float x, y, width, height;
  String label;
  float padx = 7;
  boolean hidden = false;

  CheckBox ( String l, float _x, float _y, float _w, float _h ) {
    label = l;
    x = _x; 
    y = _y; 
    width = _w; 
    height = _h;
    Interactive.add( this );
  }

  public void mouseReleased ( float mx, float my ) {
    checked = !checked;
    redraw();
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    hidden = false;
  }

  public boolean isChecked() {
    return checked;
  }

  public void draw () {
    if (hidden) return;
    noStroke();
    fill( 200 );
    rect( x, y, width, height );
    if ( checked )
    {
      fill( 80 );
      rect( x+2, y+2, width-4, height-4 );
    }
    fill( 255 );
    textSize(24*pd);
    textAlign( LEFT );
    text( label, x+width+padx, y+height );
    this.hide();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "glitch" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
