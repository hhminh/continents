
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class FireWorks implements MapEffect, Runnable{
  private static final AlphaComposite COVER_COMPOSITE =
		AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

  Dimension	d;

  final int	numrockets=16;
  final int	num=24;
  int[]		xcoord, ycoord, xspeed, yspeed;
  int[]		count;
  boolean[]	exploding;
  
  boolean running;
  float randoms[];
  int current;
  MapPanel owner;
  
  public FireWorks(MapPanel mp)
  {
    int i;
	owner = mp;
    d = owner.getSize();

	xcoord=new int[numrockets*num];
    ycoord=new int[numrockets*num];
    xspeed=new int[numrockets*num];
    yspeed=new int[numrockets*num];
    count=new int[numrockets];
    exploding=new boolean[numrockets];

	current = -1;
	randoms = new float[100];
	for (i=0; i<randoms.length; i++){
		randoms[i] = (float)Randomizer.getNextRandom();
	}

    for (i=0; i<numrockets*num; i++)
    {
      xcoord[i]=((int)((getNextRandom()*d.width)))<<3;
      ycoord[i]=d.height<<3;
      xspeed[i]=0;
      yspeed[i]=0;
    }
    for (i=0; i<numrockets; i++)
    {
      count[i]=1+i*16;
      exploding[i]=true;
    }
  }

  protected float getNextRandom(){
	current++;
	if (current < 0  || current >= randoms.length){
		current = 0;
	}
	return randoms[current];
  }

  public void start(){
	  Thread thread = new Thread(this);
	  owner.setEffect(this);
	  running = true;
	  thread.start();
  }

  public void run(){
	  while(running){
		  owner.repaint();
		  try{
			  Thread.sleep(50);
		  }catch(Exception e){
		  }
	  }
  }

  public void stop(){
	  owner.clearEffect();
	  running = false;
  }

  public void drawEffect(Graphics2D g)
  {
    int	i,j,index;
    int	x,y,xspd,yspd;

	/*
	g.setComposite(COVER_COMPOSITE);
	Point p1 = owner.getPosition(0, 0);
	Point p2 = owner.getPosition(owner.getScreenWidth()-1, owner.getScreenHeight()-1);
	g.setColor(Color.black);
	g.fillRect(p1.x, p1.y, p2.x, p2.y);
	g.setComposite(owner.getDefComposite());
	*/

    for (i=0; i<numrockets; i++)
    {
      if (!exploding[i] && yspeed[i*num]>0)
      { // explode
        exploding[i]=true;
        for (j=0; j<num; j++)
        {
          index=i*num+j;
          yspeed[index]=(int)((getNextRandom()*28.0))-15;
          xspeed[index]=(int)((getNextRandom()*47.0))-24;
          if (xspeed[index]>=0) xspeed[index]+= 1;
        }
      }
      for (j=0; j<num; j++){
        index=i*num+j;
        if (exploding[i]){
          switch(i&3)
          {
            case 0:
              g.setColor(new Color(192,(count[i])+32,(count[i])+127));
              break;
            case 1:
              g.setColor(new Color(count[i]+32,192,count[i]+127));
              break;
            case 2:
              g.setColor(new Color(192, 192, count[i]+32));
              break;
            default:
              g.setColor(new Color(count[i]+32, count[i]+127, 192));
          }
        }else{
			g.setColor(Color.white);
		}
		x = xcoord[index] >> 3;
		y = ycoord[index] >> 3;

		if (owner.isPointInFrame(x, y)){
			g.fillRect(x, y,2,2);
		}
		xcoord[index]+=xspeed[index];
		ycoord[index]+=yspeed[index];
		yspeed[index]+= 8;
      }
      count[i]--;
      if (count[i]<=0){
        count[i]=128;
        exploding[i]=false;
        x=((int)((getNextRandom()*d.width)))<<3;
        y=d.height<<3;
        yspd=(int)((getNextRandom()*40))-260;
        xspd=(int)((getNextRandom()*15.0))-8;
          if (xspd>=0) xspd++;
        for (j=0; j<num; j++)
        {
          index=i*num+j;
          xcoord[index]=x;
          ycoord[index]=y;
          xspeed[index]=xspd;
          yspeed[index]=yspd;
        }
      }
    }
  }
}
