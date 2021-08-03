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

  void mousePressed () {
    menuOpen = !menuOpen;
    on = !on;
    redraw();
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void draw () {
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

  void mouseDragged(float mx, float my, float dx, float dy) {

    if (!menuOpen) return;

    valueX = mx - height/2;

    if ( valueX < x ) valueX = x;
    if ( valueX > x+width-height ) valueX = x+width-height;

    value = map( valueX, x, x+width-height, min, max );

    redraw();
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  void setLabel(String label) {
    this.label = label;
  }

  void setRange(float min, float max) {
    this.min = min;
    this.max = max;
    this.value = min; // initialise value
  }

  float getValue() {
    return this.value;
  }

  void setValue(float value) {
    this.value = constrain(value, this.min, this.max);
    valueX = map(this.value, min, max, x, x+width-height);
  }

  void draw () {
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

  void mouseReleased ( float mx, float my ) {
    checked = !checked;
    redraw();
  }

  void hide() {
    hidden = true;
  }

  void show() {
    hidden = false;
  }

  boolean isChecked() {
    return checked;
  }

  void draw () {
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
