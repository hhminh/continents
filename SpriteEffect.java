import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class SpriteEffect implements MapEffect, Runnable{
  private boolean running;
  private MapPanel owner;
  private Image sheet;
  private int swidth, sheight, scurrent, stimer, wnum, hnum, total, tx, ty;
  private boolean reverse;
  private int loops;
  private int curloops;
  private int direction;
  
  public SpriteEffect(MapPanel mp){
	owner = mp;
	sheet = null;
	swidth = 48;
	sheight = 48;
	scurrent = 0;
	stimer = 250;
	wnum = 0;
	hnum = 0;
	total = 0;

	loops = 0;
	curloops = 0;
	direction = 1;
	reverse = false;
  }

  public void setReverse(boolean r){
	  reverse = r;
  }

  public void setLoops(int l){
	  loops = l;
  }

  public void setSpriteSheet(Image img){
	  sheet = img;
	  wnum = (int)(sheet.getWidth(owner) / swidth);
	  hnum = (int)(sheet.getHeight(owner) / sheight);
	  total = wnum * hnum;
  }

  public void setTarget(int x, int y){
	  tx = x;
	  ty = y;
  }

  public void setDimension(int w, int h){
	  swidth = w;
	  sheight = h;
  }

  public void setTimer(int tm){
	  stimer = tm;
  }

  public boolean isRunning(){
	  return running;
  }

  public void start(){
	  if (sheet == null) {
		return;
	  }
	  Thread thread = new Thread(this);
	  owner.setEffect(this);
	  scurrent = 0;
	  curloops = 0;
	  direction = 1;
	  running = true;
	  thread.start();
  }

  public void run(){
	  while(running && sheet != null){
		  owner.repaint();
		  try{
			  Thread.sleep(stimer);
		  }catch(Exception e){
		  }
	  }
  }

  public void stop(){
	  owner.clearEffect();
	  running = false;
  }

  public void drawEffect(Graphics2D g){
	  if (scurrent < 0 || scurrent >= total) {
		  curloops++;
		  if (curloops>loops) {
			stop();
			return;
		  }
		  if (reverse) {
			direction = -direction;
		  }
		  if (scurrent < 0) {
			  scurrent = total;
		  }else{
			  scurrent = 0;
		  }
	  }
	  //System.err.println("PLAYING " + scurrent);
	  int uy = (int)(scurrent / wnum);
	  int ux = scurrent % wnum;
	  int px = ux * swidth;
	  int py = uy * sheight;

	  g.drawImage(sheet, tx, ty, tx + swidth, ty + sheight, px, py, px + swidth, py + sheight, owner);

	  scurrent += direction;
  }
}
