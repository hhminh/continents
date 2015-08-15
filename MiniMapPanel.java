

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class MiniMapPanel extends JComponent{
	private static final int BEVEL_WIDTH = 5, BEVEL_HEIGHT = 5;

	protected int swidth;
	protected int sheight;

	protected TerrainLoader tloader;
	protected MapPanel mp;
	protected GameWorld world;

	protected Image buffer;
	protected Graphics2D gbuffer;

	private Thread thread;
	private int width, height;

	public MiniMapPanel(MapPanel m, int sw, int sh, TerrainLoader tl, GameWorld w){
		swidth = sw;
		sheight = sh;

		mp = m;
		tloader = tl;
		world = w;

		reset();

		addComponentListener(new ComponentListener(){
			public void componentResized(ComponentEvent e){
				//invalidate
				buffer = null;
				gbuffer = null;
			}
			public void componentMoved(ComponentEvent e){
			}
			public void componentShown(ComponentEvent e){
			}
			public void componentHidden(ComponentEvent e){
			}
		});
	}

	public Point getPosition(Point p){
		return new Point((int)((p.x - BEVEL_WIDTH) / swidth),
			(int)((p.y - BEVEL_HEIGHT) / sheight));
	}

	public void reset(){
		width = world.getGD().getMapWidth();
		height = world.getGD().getMapHeight();
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(swidth * width + BEVEL_WIDTH * 2,
			sheight * height + BEVEL_HEIGHT * 2);
	}

	public Dimension getMaximumSize(){
		return new Dimension(swidth * width + BEVEL_WIDTH * 2,
			sheight * height + BEVEL_HEIGHT * 2);
	}

	public Dimension getMinimumSize(){
		return new Dimension(swidth * width + BEVEL_WIDTH * 2,
			sheight * height + BEVEL_HEIGHT * 2);
	}

	public synchronized void paintComponent(Graphics g){
		if (buffer == null || gbuffer == null){
			buffer = createImage(getWidth(), getHeight());
			gbuffer = (Graphics2D)buffer.getGraphics();
			Image img = world.getGameIcon(GameWorld.IMG_FRAME2);
			gbuffer.drawImage(img, 0, 0, getWidth(), getHeight(),
				0,0, img.getWidth(this), img.getHeight(this), this);
		}

		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				int x = i * swidth + BEVEL_WIDTH;
				int y = j * sheight + BEVEL_HEIGHT;

				if (world.getGD().getMask(i, j) == GameData.MASK_INVISIBLE){
					gbuffer.setColor(Color.black);
					gbuffer.fillRect(x, y, swidth, sheight);
				}else{
					int b = world.getGD().getBaseLand(i, j);
					int a = world.getGD().getArmy(i, j);
					int c = world.getGD().getBase(i, j);

					Terrain t = tloader.getTerrain(b);
					gbuffer.setColor(t.getColor());
					gbuffer.fillRect(x, y, swidth, sheight);

					if (a > -1){
						Army army = world.getGD().getArmy(a);
						int owner = army.getOwner();
						Color banner = GameWorld.OWNER_COLOR[owner];
						gbuffer.setColor(banner);
						gbuffer.fillOval(x, y, swidth, sheight);
					}else if (c > -1){
						Base base = world.getGD().getBase(c);
						int owner = base.getOwner();
						Color banner = GameWorld.OWNER_COLOR[owner];
						gbuffer.setColor(banner);
						gbuffer.fillOval(x, y, swidth, sheight);
					}
				}
			}
		}

		//drawing current screen
		gbuffer.setColor(Color.white);
		gbuffer.drawRect(mp.getLeft() * swidth + BEVEL_WIDTH,
						mp.getTop() * sheight + BEVEL_HEIGHT,
						mp.getScreenWidth() * swidth - 1,
						mp.getScreenHeight() * sheight - 1);

		g.drawImage(buffer, 0, 0, this);
	}
}