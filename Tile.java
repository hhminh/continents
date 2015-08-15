import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Tile{ // extends Component{
	protected Image tile;
	protected Image[] tileWithColor;

	//protected BufferedImage tile;
	//protected BufferedImage[] tileWithColor;

	protected ArrayList offsets;
	protected Hashtable properties;
	protected int twidth, theight;


	public Tile(Image img, int w, int h){
		tile = img;
		//int tmpW = img.getWidth(this);
		//int tmpH = img.getHeight(this);
		//TYPE_INT_ARGB
		//tile = new BufferedImage(tmpW, tmpH, BufferedImage.TYPE_INT_RGB);
		twidth = w;
		theight = h;
		offsets = new ArrayList();
		properties = new Hashtable();

		//System.err.println(tmpW + "/" + tmpH);

		tileWithColor = new Image[GameWorld.OWNER_COLOR.length];
		//tileWithColor = new BufferedImage[GameWorld.OWNER_COLOR.length];
		for (int i=0; i<GameWorld.OWNER_COLOR.length; i++){
			tileWithColor[i] = GameWorld.getReplaceColor(tile, GameWorld.MASK_COLOR_STANDARD, GameWorld.OWNER_COLOR[i]);
			//TYPE_INT_ARGB
			//tileWithColor[i] = new BufferedImage(tmpW, tmpH, BufferedImage.TYPE_INT_RGB);
			//Graphics gTmp = tileWithColor[i].getGraphics();
			//gTmp.drawImage(GameWorld.getReplaceColor(tile, GameWorld.MASK_COLOR_STANDARD, GameWorld.OWNER_COLOR[i]), 0, 0, tmpW, tmpH, this);
		}
	}

	public int getWidth(){
		return twidth;
	}

	public int getHeight(){
		return theight;
	}

	public Image getImage(){
		return tile;
	}

	public Image getImage(int i){
		if (i >= 0 && i < tileWithColor.length){
			return tileWithColor[i];
		}else{
			return tile;
		}
	}

	public void addOffset(Point p){
		offsets.add(p);
	}

	public Point getOffset(int o){
		if (o >= 0 && o < offsets.size()){
			return (Point)offsets.get(o);
		}else{
			return null;
		}
	}

	public int getVars(){
		return offsets.size();
	}

	public void addProp(String k, String v){
		properties.put(k, v);
	}

	public String getProp(String k){
		return (String)properties.get(k);
	}

	public void drawTile(Graphics g, Component c, int x, int y){
		g.drawImage(tile, x, y, twidth, theight, c);
	}

	//draw variation
	public void drawTile(Graphics g, Component c, int v, int x, int y){
		Point p = getOffset(v);
		if (p != null){
			g.drawImage(tile, x, y, x + twidth, y + theight, p.x, p.y,
				p.x + twidth, p.y + theight, c);
		}else{
			g.drawImage(tile, x, y, twidth, theight, c);
		}
	}

	//draw variation
	public void drawTile(Graphics g, Component c, int v, int x, int y, int o){
		Point p = getOffset(v);
		if (o >= 0 && o < tileWithColor.length){
			if (p != null){
				g.drawImage(tileWithColor[o], x, y, x + twidth, y + theight, p.x, p.y,
					p.x + twidth, p.y + theight, c);
			}else{
				g.drawImage(tileWithColor[o], x, y, twidth, theight, c);
			}
		}else{
			if (p != null){
				g.drawImage(tile, x, y, x + twidth, y + theight, p.x, p.y,
					p.x + twidth, p.y + theight, c);
			}else{
				g.drawImage(tile, x, y, twidth, theight, c);
			}
		}
	}

	//draw variation, part of picture
	public void drawTile(Graphics g, Component c, int v, int x, int y, int w, int h, int o){
		Point p = getOffset(v);
		if (w == 0){
			w = twidth;
		}
		if (h == 0){
			h = theight;
		}
		if (o >= 0 && o < tileWithColor.length){
			if (p != null){
				g.drawImage(tileWithColor[o], x, y, x + w, y + h, p.x, p.y,
					p.x + w, p.y + h, c);
			}else{
				g.drawImage(tileWithColor[o], x, y, w, h, c);
			}
		}else{
			if (p != null){
				g.drawImage(tile, x, y, x + w, y + h, p.x, p.y,
					p.x + w, p.y + h, c);
			}else{
				g.drawImage(tile, x, y, w, h, c);
			}
		}
	}
}