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

  void mousePressed () {
    randomGlitch = false;
    loadImageThreaded();
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void setText(String text) {
    this.text = text;
  }

  void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5);
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

  void mousePressed () {
    randomGlitch = true;
    loadImageThreaded();
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void setText(String text) {
    this.text = text;
  }

  void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5);
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

  void mousePressed () {
    selectOutput("Select file to save to:", "saveImage", new File(sketchPath()+"/"+ System.currentTimeMillis()/1000L +".bmp"));
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void setText(String text) {
    this.text = text;
  }

  void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5);
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

  void mousePressed () {
    initCam();
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void setText(String text) {
    this.text = text;
  }

  void draw () {
    if (hidden)return;

    fill( camAvailable ? 230 : 150 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5);
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

  void mousePressed () {
    selectInput("Select image to open:", "openImage");
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void setText(String text) {
    this.text = text;
  }

  void draw () {
    if (hidden)return;

    fill( on ? 230 : 240 );
    rect( x, y, width, height );

    if (this.text != "") {
      pushMatrix();
      fill(0);
      textSize(20 * pd);
      textAlign(CENTER, CENTER);
      text(this.text, x+width/2, y+height/2.5);
      popMatrix();
    }

    this.hide();
  }
}
