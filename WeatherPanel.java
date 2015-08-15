import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class WeatherPanel extends Component implements Runnable{
	protected int width, height;
	protected boolean running;

    protected Image BackImg;
    protected Image OffScreenImage;
    protected Graphics OffScreenGraphics;
    protected Color RainColor;
    protected int Season;
    protected int LRdir;
    protected int MaxCount;
    protected int Radius;
    protected int MaxSpeed;
    protected int WndSpeed;
    protected int Xpos[];
    protected int Ypos[];
    protected int Height[];
    protected int Speed[];
    protected boolean RndDirection;
    protected boolean Accumulate;
    protected boolean DevInfo;

	public WeatherPanel(int w, int h){
		width = w;
		height = h;

        RainColor = new Color(145, 164, 164);
        Season = 1;
        LRdir = 2;
        MaxCount = 100;
        Radius = 2;
        MaxSpeed = 8;
        WndSpeed = 0;
        Xpos = new int[1024];
        Ypos = new int[1024];
        Height = new int[1024];
        Speed = new int[1024];
        RndDirection = true;
        Accumulate = false;
        DevInfo = false;
	}

	public Dimension getPreferredSize(){
		return new Dimension(width, height);
	}

	public void start(){
		running = true;
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop(){
		running = false;
	}

	public void run(){
		while (running){
			repaint();
			try{
				Thread.sleep(100);
			}catch(Exception e){
			}
		}
	}

	public void paint(Graphics g){
		if (OffScreenImage == null){
			OffScreenImage = createImage(getWidth(), getHeight());
			OffScreenGraphics = OffScreenImage.getGraphics();
			for (int I = 0; I < MaxCount; I++){
				Speed[I] = (int)(Randomizer.getNextRandom() * MaxSpeed) + 3;
				Xpos[I] = (int)(Randomizer.getNextRandom() * getWidth());
				Ypos[I] = (int)(Randomizer.getNextRandom() * getHeight());
			}

			int height = getHeight();
			for (int I = 0; I < 1024; I++){
				Height[I] = height;
			}
		}

		rain(g);
	}

    protected void rain(Graphics g){
        int Stat = 0;
        OffScreenGraphics.setColor(getBackground());
        OffScreenGraphics.fillRect(0, 0, getWidth(), getHeight());
        OffScreenGraphics.setColor(RainColor);
        //OffScreenGraphics.drawImage(BackImg, 0, 0, getWidth(), getHeight(), this);
        Stat = (int)(Randomizer.getNextRandom() * 100.0);
        if (RndDirection){
            if (Stat > 0 && Stat < 5){
                LRdir = 1;
                WndSpeed = (int)(Randomizer.getNextRandom() * -5.0);
            }
            else if (Stat > 55 && Stat < 60){
                LRdir = 3;
                WndSpeed = (int)(Randomizer.getNextRandom() * 5.0);
            }else if (Stat > 98){
                LRdir = 2;
                WndSpeed = 0;
            }
        }

		for (int I = 0; I < MaxCount; I++){
            Ypos[I] += Speed[I];
            if (LRdir == 1)
                Xpos[I] += WndSpeed;
            else if (LRdir == 3)
                Xpos[I] += WndSpeed;
            if (Xpos[I] < 0)
                Xpos[I] = getWidth() - 1;
            if (Xpos[I] > getWidth())
                Xpos[I] = 1;
            if (Ypos[I] > getHeight()){
                Ypos[I] = 0;
                Xpos[I] = (int)(Randomizer.getNextRandom() * getWidth());
            }
            OffScreenGraphics.drawLine(Xpos[I], Ypos[I], Xpos[I] + WndSpeed / 2, Ypos[I] + 2);
            OffScreenGraphics.drawLine(Xpos[I], Ypos[I] + 1, Xpos[I] + WndSpeed / 2, Ypos[I] + 3);
        }
        g.drawImage(OffScreenImage, 0, 0, getWidth(), getHeight(), this);
    }

	public static void main(String[] args){
		Frame frame = new Frame("Weather Panel");
		WeatherPanel panWeather = new WeatherPanel(300, 300);
		frame.setLayout(new BorderLayout());
		frame.add(panWeather, BorderLayout.CENTER);
		frame.pack();
		frame.show();
		panWeather.start();
	}
}