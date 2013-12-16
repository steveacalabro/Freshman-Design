//import processing.video.*;
import processing.serial.*;
import hypermedia.video.*;
import java.awt.Rectangle; 

OpenCV opencv; 

//Screen Size Parameters
int width = 800;
int height = 600;

int minx=51; 
int maxx=105;
int xdiff = maxx-minx;
int xpix = (width/xdiff);

int miny=116;
int maxy=154;
int ydiff = maxy-miny;
int ypix = (height/ydiff);

int newX=0, newY=0;

int contrast_value    = 0;
int brightness_value  = 0;

Serial port;

char servoTiltPosition = 90;
char servoPanPosition = 90;
char tiltChannel = 0;
char panChannel = 1;

int midFaceY=0;
int midFaceX=0;

int midScreenY = (height/2);
int midScreenX = (width/2);
int midScreenWindow = 10;  
int stepSize=1;

void setup() {
 
  size( width, height );

  opencv = new OpenCV( this );
  opencv.capture( width, height );                 
  opencv.cascade( OpenCV.CASCADE_FRONTALFACE_ALT );  

  println(Serial.list()); 

  port = new Serial(this, Serial.list()[2], 9600); 

  port.write(tiltChannel);    
  port.write(servoTiltPosition);  
  port.write(panChannel);        
  port.write(servoPanPosition);   
}


public void stop() {
  opencv.stop();
  super.stop();
}



void draw() {
  // grab a new frame
  // and convert to gray
  opencv.read();
  //opencv.convert( GRAY );
  opencv.contrast( contrast_value );
  opencv.brightness( brightness_value );

  // proceed detection
  Rectangle[] faces = opencv.detect( 1.2, 2, OpenCV.HAAR_DO_CANNY_PRUNING, 40, 40 );

  // display the image
  image( opencv.image(), 0, 0 );

  // draw face area(s)
  noFill();
  stroke(255,0,0);
  for( int i=0; i<faces.length; i++ ) {
    rect( faces[i].x, faces[i].y, faces[i].width, faces[i].height );
  }
  
  //Find out if any faces were detected.
  if(faces.length > 0){
    midFaceY = faces[0].y + (faces[0].height/2);
    midFaceX = faces[0].x + (faces[0].width/2);
    
    newX=(midFaceX/xpix)+minx;
    newY=(midFaceY/ypix)+miny;
    
  }
  port.write(tiltChannel);
  port.write(newY);
  println("Y: "+newY);
  port.write(panChannel);
  port.write(newX);
  println("X: "+newX);
  //delay(1);
}



/**
 * Changes contrast/brigthness values
 */
void mouseDragged() {
  contrast_value   = (int) map( mouseX, 0, width, -128, 128 );
  brightness_value = (int) map( mouseY, 0, width, -128, 128 );
}
