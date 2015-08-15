//MIN07 - put frame over map, changing clipping position, not allow drawing over frame


import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class MapPanel extends JComponent implements Runnable{
	//private static final int BEVEL_WIDTH = 24, BEVEL_HEIGHT = 23;
	private static final int BEVEL_WIDTH = 0, BEVEL_HEIGHT = 0;
	private static final AlphaComposite FOG_COMPOSITE =
			AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);

	protected int left;
	protected int top;
	protected int swidth;
	protected int sheight;

	protected TileLoader loader;
	protected TerrainLoader tloader;
	protected OverlayLoader oloader;
	protected UnitTypeLoader uloader;
	protected BaseTypeLoader bloader;
	protected TechLoader teloader;

	protected GameWorld world;

	private Image buffer;
	private Graphics2D gbuffer;
	private Composite defComposite;
	//MH112
	private VolatileImage vBuffer;
	private Graphics2D vGBuffer;
	private GraphicsConfiguration vGC;

	//Asynch drawing
	private Thread thread;
	//Internal flag
	private boolean running, bufferSaved;
	private boolean busy, drawing;
	//Internal params
	private int width, height, cright, cbot, lright, lbot, dwidth, dheight;
	//Special effect like firework, lightnight, etc
	private MapEffect effector;
	//Highlight
	private ArrayList highlight;

	public MapPanel(int sw, int sh, TileLoader l, TerrainLoader tl, OverlayLoader ol, 
					UnitTypeLoader ul, BaseTypeLoader bl, TechLoader tel, GameWorld w){

		//Continue loading data
		loader = l;
		tloader = tl;
		oloader = ol;
		uloader = ul;
		bloader = bl;
		teloader = tel;

		world = w;

		//reset sizes
		reset();

		//differences
		dwidth = loader.getWidth() - BEVEL_WIDTH;
		dheight = loader.getHeight() - BEVEL_HEIGHT;
		//screen
		swidth = sw;
		sheight = sh;
		//right/bottom displayable spot
		cright = swidth * loader.getWidth();
		cbot = sheight * loader.getHeight();
		//left-top displayable box
		lright = BEVEL_WIDTH + cright - loader.getWidth();
		lbot = BEVEL_HEIGHT + cbot - loader.getHeight();

		effector = null;
		highlight = new ArrayList();

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

		//MIN07 - maximizing output performance
		try{
			Thread me = Thread.currentThread();
			me.setPriority(Thread.MAX_PRIORITY);
		}catch(Exception e){
			System.err.println(e);
		}
	}

	public Composite getDefComposite(){
		return defComposite;
	}

	public void clearHighlight(){
		highlight.clear();
	}

	public void addHighlight(Point p){
		highlight.add(p);
	}

	public void setCombatHighlight(int x, int y){
		clearHighlight();
		addHighlight(new Point(x-1, y));
		addHighlight(new Point(x+1, y));
		addHighlight(new Point(x, y-1));
		addHighlight(new Point(x, y+1));
	}

	public void setNewSize(int sw, int sh){
		//screen
		swidth = sw;
		sheight = sh;
		//right/bottom displayable spot
		cright = swidth * loader.getWidth();
		cbot = sheight * loader.getHeight();
		//left-top displayable box
		lright = BEVEL_WIDTH + cright - loader.getWidth();
		lbot = BEVEL_HEIGHT + cbot - loader.getHeight();
	}

	public boolean getRunning(){
		return running;
	}

	public void setEffect(MapEffect me){
		effector = me;
	}

	public void clearEffect(){
		effector = null;
	}

	public boolean isPointInFrame(int x, int y){
		if (x > BEVEL_WIDTH && x < cright + BEVEL_WIDTH &&
			y > BEVEL_HEIGHT && y < cbot + BEVEL_HEIGHT){
			return true;
		}
		//System.err.println(x + ":" + y);
		return false;
	}

	public boolean isDoubleBuffered(){
		return true;
	}

	public void reset(){
		left = 0;
		top = 0;
		width = world.getGD().getMapWidth();
		height = world.getGD().getMapHeight();
	}
	
	//MIN16 - optimize drawing
	public void repaintClipCell(int x, int y){
		Point p = getClipPosition(x, y);

		repaint(p.x, p.y, loader.getWidth(), loader.getHeight());
	}

	public void repaintClipCell(int x, int y, int w, int h){
		repaint(x, y, w, h);
	}

	public void start(){
		//MIN06
		//thread = new Thread(this);
		running = true;
		repaint();
		//MIN06
		//thread.start();
	}

	public void stop(){
		running = false;
	}

	public void run(){
		while(running){
			if (!drawing){
				repaint();
			}
			try{
				Thread.sleep(500);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void setBusy(boolean b){
		busy = b;
	}

	public boolean isBusy(){
		return busy;
	}

	public int getCellWidth(){
		return loader.getWidth();
	}

	public int getCellHeight(){
		return loader.getHeight();
	}

	public int getLeft(){
		return left;
	}

	public int getTop(){
		return top;
	}

	public int getScreenWidth(){
		return swidth;
	}

	public int getScreenHeight(){
		return sheight;
	}

	public boolean isDisplayable(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return false;
		}

		if (world.getGD().getMask(x, y) != GameData.MASK_INVISIBLE){
			if (x >= left && x < left + swidth && y >= top && y < top + sheight){
				return true;
			}
		}
		return false;
	}

	public Point getMapCell(int x, int y){
		//MIN07
		//return new Point(x / loader.getWidth() + left, y / loader.getHeight() + top);
		//MIN18
		int l, t;
		if (x < BEVEL_WIDTH){
			l = left - 1;
		}else{
			l = (x - BEVEL_WIDTH) / loader.getWidth() + left;
		}
		if (y < BEVEL_HEIGHT){
			t = top - 1;
		}else{
			t = (y - BEVEL_HEIGHT) / loader.getHeight() + top;
		}
		//System.out.println(l + "/" + t);
		return new Point(l, t);
	}

	public Point getMovementSpin(int x, int y){
		int dx = 0, dy = 0;
		if (x < BEVEL_WIDTH){
			dx = -1;
		}else if (x > getWidth() - BEVEL_WIDTH){
			dx = 1;
		}
		if (y < BEVEL_HEIGHT){
			dy = -1;
		}else if (x > getHeight() - BEVEL_HEIGHT){
			dy = 1;
		}
		return new Point(dx, dy);
	}

	public void setViewCenter(int x, int y){
		int sw2 = swidth / 2;
		int sh2 = sheight / 2;
		int wbound = width - swidth;
		int hbound = height - sheight;
		int l = x - sw2;
		int t = y - sh2;

		if (l < 0){
			left = 0;
		}else if (l > wbound){
			left = wbound;
		}else{
			left = l;
		}

		if (t < 0){
			top = 0;
		}else if (t > hbound){
			top = hbound;
		}else{
			top = t;
		}
		//MIN06
		repaint();
	}

	public Dimension getPreferredSize(){
		return new Dimension(cright + BEVEL_WIDTH * 2, cbot + BEVEL_HEIGHT * 2);
	}

	public Dimension getMaximumSize(){
		return new Dimension(cright + BEVEL_WIDTH * 2, cbot + BEVEL_HEIGHT * 2);
	}

	public Dimension getMinimumSize(){
		return new Dimension(cright + BEVEL_WIDTH * 2, cbot + BEVEL_HEIGHT * 2);
	}

	public Dimension getSize(){
		return new Dimension(cright + BEVEL_WIDTH * 2, cbot + BEVEL_HEIGHT * 2);
	}

	public Point getPosition(int x, int y){
		//MIN07 - allow the frame
		//return loader.getPosition(x, y);
		return new Point(x * loader.getWidth() + BEVEL_WIDTH,
			y * loader.getHeight() + BEVEL_HEIGHT);
	}

	public Point getClipPosition(int x, int y){
		//return loader.getPosition(x - left, y - top);
		return getPosition(x - left, y - top);
	}

	//MIN06
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height){
		if (isShowing() && (infoflags & ALLBITS) != 0){
			repaint();
		}
		if (isShowing() && (infoflags & FRAMEBITS) != 0){
			repaint();
		}
		return isShowing();
	}

	protected void drawFrame(){
		drawBlackScreen();
		//Image img = world.getGameIcon(GameWorld.IMG_FRAME);
		//gbuffer.drawImage(img, 0, 0, getWidth(), getHeight(),
		//	0,0, img.getWidth(this), img.getHeight(this), this);
	}

	//synchronized to avoid slickering when menus over it
	public void paintComponent(Graphics g){
		if (buffer == null || gbuffer == null){
			//MH112
			vGC = getGraphicsConfiguration();
			vBuffer = createVolatileImage(getWidth(), getHeight());
			vGBuffer = vBuffer.createGraphics();
			//MH112
			buffer = createImage(getWidth(), getHeight());
			gbuffer = (Graphics2D)buffer.getGraphics();
			defComposite = gbuffer.getComposite();
			drawFrame();
		}

		//automatic mode
		if (running){
			//drawing = true;
			drawMap();
			//drawing = false;
		}

		if (effector != null){
			effector.drawEffect(gbuffer);
		}

		//MH112
		if (vBuffer.validate(vGC) == VolatileImage.IMAGE_INCOMPATIBLE){
			// old vImg doesn't work with new GraphicsConfig; re-create it
			vBuffer = createVolatileImage(getWidth(), getHeight());
			vGBuffer.dispose();
			vGBuffer = vBuffer.createGraphics();
	    }
		vGBuffer.drawImage(buffer, 0, 0, this);
		//MH112
		//MIN25 remove Graphics2D casting
		((Graphics2D)g).drawImage(vBuffer, 0, 0, this);
		//MH112
		//vGBuffer.dispose();
		//MH112
	}

	public Graphics getBuffer(){
		return gbuffer;
	}

	public void drawResource(Graphics g, Component c, int r, int x, int y, int s){
		Image img = world.getGameIcon(GameWorld.IMG_RESOURCE);
		if (img != null){
			int rx = r * 32;
			g.drawImage(img, x, y, x + s, y + s, rx, 0, rx + 32, 32, c);
		}
	}

	//this is used inside so no clipping
	public void drawMapCell(int x, int y, int px, int py){
		GameData gd = world.getGD();
		//terrain is required, if error lets it die
		tloader.getTerrain(gd.getBaseLand(x,y)).draw(gbuffer, this, gd.getBaseVar(x,y), px, py);
		//overlay is optional
		for (int v=0; v<gd.getTopCount(x,y); v++) {
			int to = gd.getTopLand(x,y,v);
			if (to != -1){
				oloader.getOverlay(to).draw(gbuffer, this, gd.getTopVar(x,y,v), px, py);
			}
		}
	}

	//exclusively, not having masking or clipping
	public void drawMapCell(Graphics g, Component c, int x, int y, int px, int py){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return;
		}
		GameData gd = world.getGD();
		//terrain is required, if error lets it die
		tloader.getTerrain(gd.getBaseLand(x,y)).draw(g, c, gd.getBaseVar(x,y), px, py);
		//overlay is optional
		for (int v=0; v<gd.getTopCount(x,y); v++) {
			int to = gd.getTopLand(x,y,v);
			if (to != -1){
				oloader.getOverlay(to).draw(g, c, gd.getTopVar(x,y,v), px, py);
			}
		}
	}

	public void drawMapCell(int x, int y){
		//MIN07 clip size
		if (x < left || x >= left + swidth || y < top || y >= top + sheight){
			return;
		}
		GameData gd = world.getGD();
		Point p = getClipPosition(x, y);

		if (gd.getMask(x, y) == GameData.MASK_INVISIBLE){
			drawBlackFog(x, y, p.x, p.y);
		}else{
			//terrain is required, if error lets it die
			tloader.getTerrain(gd.getBaseLand(x,y)).draw(gbuffer, this, gd.getBaseVar(x,y), p.x, p.y);
			//overlay is optional
			for (int v=0; v<gd.getTopCount(x,y); v++) {
				int to = gd.getTopLand(x,y,v);
				if (to != -1){
					oloader.getOverlay(to).draw(gbuffer, this, gd.getTopVar(x,y,v), p.x, p.y);
				}
			}
			//city is optional
			drawBase(x, y, p.x, p.y);
			//fog
			if (gd.getMask(x, y) == GameData.MASK_CLOUD){
				drawFog(x, y, p.x, p.y);
			}
		}
	}

	public boolean drawTechEffect(int t, int x, int y, int frame){
		//MIN07 clip size
		if (x < left || x >= left + swidth || y < top || y >= top + sheight ||
			t < 0 || t > teloader.getSize()){
			return false;
		}
		Tech tech = teloader.getTech(t);
		if (!tech.hasEffect()){
			return false;
		}
		//MIN41
		if (tech.getEffectLen() <= frame){
			return false;
		}
		Point p = getClipPosition(x, y);
		gbuffer.drawImage(tech.getEffectImage(), p.x, p.y, this);
		repaintClipCell(p.x, p.y, loader.getWidth(), loader.getHeight());
		return true;
	}

	public void drawArmy(int ind, int px, int py){
		if (ind != -1){
			Army a = world.getGD().getArmy(ind);
			if (a == null) {
				return;
			}
			//draw selection
			if (a.getSelection()){
				Image si = world.getGameIcon(GameWorld.IMG_SELECT);
				gbuffer.drawImage(si, px, py, loader.getWidth(), loader.getHeight(), this);
			}
			//banner colours
			Color banner = a.getOwnerColor();
			int owner = a.getOwner();
			//MH120 experiment with drawing up to 5 units
			/*
			int lrow = 1;
			int lcol = 1;
			if (a.getCount()<4) {
				lcol = a.getCount();
			}else if (a.getCount()<5) {
				lrow = 2;
				lcol = 2;
			}else if (a.getCount()<7) {
				lrow = 2;
				lcol = 3;
			}else{
				lrow = 3;
				lcol = 3;
			}
			int lc = 0;
			for (int l=0; l<a.getCount(); l++) {
			*/
				Unit u = a.get(0);
			//	Unit u = a.get(l);
				if (u != null){
					UnitType ut = uloader.getUnitType(u.getType());
					if (ut != null){
			//			if (ut.isTransport()) {
			//				continue;
			//			}
			//			int ly = (int)(lc / lcol);
			//			int lx = lc % lcol;
			//			int pxp = (lx - 1) * 8;
			//			int pyp = (ly - 1) * 8;
						u.draw(gbuffer, this, ut.getTile(), px, py, owner);
			//			u.draw(gbuffer, this, ut.getTile(), px + pxp, py + pyp, owner);
			//			lc++;
					}
				}
			//}
			//draw banner and numbers
			if (a.getCount()>1) {
				gbuffer.setColor(banner);
				for (int i=0; i<a.getCount(); i++){
					//gbuffer.fillRect(px + 5 * i, py, 3, 3);
					gbuffer.fillRect(px, py + 4 * i, 3, 3);
					//gbuffer.fill3DRect(px + 5 * i, py, 3, 3, true);
				}
			}
		}
	}

	public void drawBlackFog(int x, int y, int px, int py){
		gbuffer.setColor(Color.black);
		gbuffer.fillRect(px, py, loader.getWidth(), loader.getHeight());
	}

	public void drawBlackScreen(){
		Point p0 = getPosition(0, 0);
		gbuffer.setColor(Color.black);
		gbuffer.fillRect(p0.x, p0.y, cright, cbot);
	}

	public void drawFog(int x, int y, int px, int py){
		gbuffer.setColor(Color.black);
		gbuffer.setComposite(FOG_COMPOSITE);
		gbuffer.fillRect(px, py, loader.getWidth(), loader.getHeight());
		gbuffer.setComposite(defComposite);
	}

	public void drawHighlight(int px, int py){
		gbuffer.setColor(Color.red);
		gbuffer.setComposite(FOG_COMPOSITE);
		gbuffer.fillRect(px, py, loader.getWidth(), loader.getHeight());
		gbuffer.setComposite(defComposite);
	}

	/*
	public void drawBase(int ind, int px, int py){
		if (ind != -1){
			Base b = world.getGD().getBase(ind);
			Color banner = b.getOwnerColor();
			int owner = b.getOwner();
			if (b != null){
				BaseType bt = bloader.getBaseType(b.getType());
				if (b.getSelection()){
					Image si = world.getGameIcon(GameWorld.IMG_SELECT);
					gbuffer.drawImage(si, px, py, loader.getWidth(), loader.getHeight(), this);
				}
				gbuffer.drawImage(bt.getIcon(owner), px, py, this);
				//draw banner
				//gbuffer.setColor(banner);
			}
		}
	}
	*/

	//to-do
	public void drawBase(int x, int y, int px, int py){
		for (int i=-1; i<=1; i++){
			for (int j=-1; j<=1; j++){
				int ind = world.getGD().getBase(x+i, y+j);

				if (ind > -1){
					Base b = world.getGD().getBase(ind);
					Color banner = b.getOwnerColor();

					int owner = b.getOwner();
					int w = loader.getWidth();
					int h = loader.getHeight();
					//offset on the town tile
					int x1 = w * (1 - i + b.getBaseSize() * 3);
					int y1 = h * (1 - j);
					int x2 = x1 + w;
					int y2 = y1 + h; 
					//bottom right corner
					int px1 = px + w;
					int py1 = py + h;
					//base
					BaseType bt = bloader.getBaseType(b.getType());
					gbuffer.drawImage(bt.getIcon(owner), px, py, px1, py1, x1, y1, x2, y2, this);
					//selection highlight
					if (i == 0 && j == 0 && b.getSelection()){
						Image si = world.getGameIcon(GameWorld.IMG_SELECT);
						gbuffer.drawImage(si, px, py, w, h, this);
					}
					//should you overlay more than one cities over?
					break;
				}
			}
		}
	}

	//MIN18
	public void drawArmy(Army a, int px, int py){
		Unit u = a.get(0);
		if (u != null){
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null){
				int owner = a.getOwner();
				int l = px, t = py, w = 0, h = 0, clipped = 0;
				//MIN07 clip size
				if (px < BEVEL_WIDTH){
					l = BEVEL_WIDTH;
					w = px + dwidth;
					clipped++;
				}else if (px > lright){
					w = cright + BEVEL_WIDTH - px;
					clipped++;
				}
				if (py < BEVEL_HEIGHT){
					t = BEVEL_HEIGHT;
					h = py + dheight;
					clipped++;
				}else if (py > lbot){
					h = cbot + BEVEL_HEIGHT - py;
					clipped++;
				}
				if (clipped > 0){
					if (w < 0 || h < 0 || (w == 0 && h == 0)){
						return;
					}
					u.draw(gbuffer, this, ut.getTile(), l, t, w, h, owner);
				}else{
					u.draw(gbuffer, this, ut.getTile(), px, py, owner);
					//System.out.println(l + "/" + t + "/" + w + "/" + h);
				}
			}
		}
	}

	public void drawCell(int x, int y){
		//MIN07 clip size
		if (x < left || x >= left + swidth || y < top || y >= top + sheight){
			return;
		}
		Point p = getClipPosition(x, y);

		if (world.getGD().getMask(x, y) == GameData.MASK_INVISIBLE){
			drawBlackFog(x, y, p.x, p.y);
		}else{
			//map cell
			drawMapCell(x, y, p.x, p.y);
			//city is optional
			drawBase(x, y, p.x, p.y);
			//unit is optional
			drawArmy(world.getGD().getArmy(x,y), p.x, p.y);
			//fogs
			if (world.getGD().getMask(x, y) == GameData.MASK_CLOUD){
				drawFog(x, y, p.x, p.y);
			}
		}
	}

	public void drawMap(){
		//drawBlackScreen();

		for (int i=0; i<swidth; i++){
			for (int j=0; j<sheight; j++){
				int x = i+left;
				int y = j+top;
				int m = world.getGD().getMask(x, y);

				Point p = getPosition(i,j);

				if (m == GameData.MASK_INVISIBLE){
					drawBlackFog(x, y, p.x, p.y);
				}else{
					//map cell
					drawMapCell(x, y, p.x, p.y);
					//city is optional
					drawBase(x, y, p.x, p.y);
					//unit is optional
					drawArmy(world.getGD().getArmy(x,y), p.x, p.y);
					//foggy
					if (m == GameData.MASK_CLOUD){
						drawFog(x, y, p.x, p.y);
					}
					//Highlight
					if (highlight.size()>0) {
						Point pc = new Point(x, y);
						if (highlight.indexOf(pc) > -1) {
							drawHighlight(p.x, p.y);
						}
					}
				}
			}
		}
	}
}