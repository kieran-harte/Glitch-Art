import processing.video.*; // Video library must be installed
import de.bezier.guido.*;  // GUIDO library required for the UI

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


void setup() {
  size(1000, 1000);
  background(0);
  smooth(4);

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
  s3.setRange(0, 0.999);
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
void settings() {
  fullScreen();
}

// Sets random values for the effect sliders
void randomiseParameters() {
  s1.setValue(random(1, 4));
  s2.setValue(4);
  s3.setValue(random(0.625, 0.909));
  s4.setValue(floor(random(0, 2))==0 ? 4 : 0);
  s5.setValue(10);
  s6.setValue(random(1, 35));
}

// Called to update changes to the screen
void draw() {  

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
void loadImageThreaded(String path) {
  loading = true;
  redraw();
  imageToLoad = path;
  thread("_loadImageThreaded");
}

// Reloads whichever image is already displayed
void loadImageThreaded() {
  loadImageThreaded(imageToLoad);
}

// Thread for loading of image and applying of glitch
void _loadImageThreaded() {
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
void computeGlitch() {
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
    canvas.text(overlayText, random(canvas.width/4, canvas.width/1.333), random(canvas.height/4, canvas.height/1.333));
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
void drawCanvas() {
  image(canvas, (width-canvas.width)/2, (height-canvas.height)/2);
}

// Draws the button which opens/closes the menu
void drawMenuButton() {
  pg_options = createGraphics(100, 100);
  pg_options.beginDraw();
  pg_options.fill(255, 0, 0, 125);
  pg_options.rect(0, 0, 100, 100);
  pg_options.endDraw();
  image(pg_options, 0, 0);
}

void drawMenu() {
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
void drawLoading() {
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


void keyPressed() {
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
void saveImage(File f) {
  if (f!=null) canvas.save(f.getAbsolutePath());
}

// Opens the image from the given file path
void openImage(File f) {
  selectedImgId = -1;
  if (f!=null) loadImageThreaded(f.getAbsolutePath());
}

// Initialises the webcam/ check if there are any available
void initCam() {
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
