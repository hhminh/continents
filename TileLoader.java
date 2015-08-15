import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class TileLoader{
	protected int twidth;
	protected int theight;

	protected ArrayList baseTiles;
	protected ArrayList topTiles;
	protected ArrayList townTiles;
	protected ArrayList unitTiles;
	protected Hashtable config;

	//temporary, no meaning
	private int tileCount;
	private GameWorld world;
	private CellScaler cs;

	public TileLoader(GameWorld w, int tw, int th){
		world = w;
		twidth = tw;
		theight = th;
		cs = new CellScaler();

		baseTiles = new ArrayList();
		topTiles = new ArrayList();
		unitTiles = new ArrayList();
		townTiles = new ArrayList();
		config = new Hashtable();

		readConfig();
		getTiles();
	}

	protected void getTiles(){
		int last = 0;
		//for (Enumeration ec = config.keys(); ec.hasMoreElements();) {
		for (int i =0; i<tileCount; i++){
		     //Hashtable section = (Hashtable)config.get(ec.nextElement());
			 Hashtable section = (Hashtable)config.get(Integer.toString(i));
			 String type = (String)section.get("type");
			 String icon = (String)section.get("icon");
			 String crop = (String)section.get("crop");

			 if (icon != null){
				 Image img;
		 		if (crop != null){
					try{
						double ratio = Double.parseDouble(crop);
						int nw = (int)(twidth * ratio);
						int nh = (int)(theight * ratio);
						String original = (String)section.get("original");
						if (original != null){
							int comma = original.indexOf(",");
							int ow = Integer.parseInt(original.substring(0, comma));
							int oh = Integer.parseInt(original.substring(comma+1, original.length()));
							cs.setOldSize(ow, oh);
						}else{
							cs.setOldSize(twidth, theight);
						}
						cs.setNewSize(nw, nh);
						cs.setFile(icon);
						cs.doScaling();
						img = cs.getScaledImage();
					}catch(Exception e){
						img = world.loadImage(icon);
					}
				}else{
					img = world.loadImage(icon);
				}
				 if (img != null){
					 Tile t = new Tile(img, twidth, theight);
					 //count variations by automatically and add properties
					 int vars = 0;
					 for (Enumeration ec = section.keys(); ec.hasMoreElements();){
						 String k = (String)ec.nextElement();
						 String v = (String)section.get(k);

						 if (k.startsWith("offset")){
							 vars++;
						 }else{
							 t.addProp(k, v);
						 }
					 }
					 //put variations
					 for (int j=0; j<vars; j++){
						 String v = (String)section.get("offset" + Integer.toString(j));
						 if (v != null){
							 int comma = v.indexOf(",");
							 int x = Integer.parseInt(v.substring(0,comma));
							 int y = Integer.parseInt(v.substring(comma + 1, v.length()));
							 Point p = new Point(x, y);
							 t.addOffset(p);
						 }
					 }
					 if ("base".equals(type)){
						 baseTiles.add(t);
					 }else if ("top".equals(type)){
						 topTiles.add(t);
					 }else if ("town".equals(type)){
						 townTiles.add(t);
					 }else if ("unit".equals(type)){
						 unitTiles.add(t);
					 }
				 }
			 }
	    }
 	}

	protected void readConfig(){
		BufferedReader bin = new BufferedReader(
			new InputStreamReader(this.getClass().getResourceAsStream("tile.ini")));
		String line, txt, lastSection = null;
		Hashtable section = null;
		try{
			tileCount = 0;
			while((txt = bin.readLine()) != null){
				//remove any extra spaces
				line = txt.trim();
				if (line.startsWith("#")){
					continue;
				}
				//section handling
				if (line.startsWith("[") && line.endsWith("]")){
					if (section != null){
						config.put(Integer.toString(tileCount), section);
						tileCount++;
					}
					//get the name without [,]
					lastSection = line.substring(1, line.length()-1);
					section = new Hashtable();
					section.put("name", lastSection);
				//key handling
				}else if (lastSection != null && section != null){
					int p = line.indexOf("=");
					if (p != -1){
						String k = line.substring(0, p);
						String v = line.substring(p + 1, line.length());
						//put new key
						section.put(k.trim(), v.trim());
					}
				}
			}
			if (section != null){
				config.put(Integer.toString(tileCount), section);
				tileCount++;
			}
		}catch(IOException io){
			io.printStackTrace();
		}
	}

	public int getBaseSize(){
		return baseTiles.size();
	}

	public int getTopSize(){
		return topTiles.size();
	}

	public int getTownSize(){
		return townTiles.size();
	}

	public int getUnitSize(){
		return unitTiles.size();
	}

	public int getBaseVars(int t){
		if (t >= 0 && t < baseTiles.size()){
			return ((Tile)baseTiles.get(t)).getVars();
		}else{
			return 0;
		}
	}

	public int getTopVars(int t){
		if (t >= 0 && t < topTiles.size()){
			return ((Tile)topTiles.get(t)).getVars();
		}else{
			return 0;
		}
	}

	public int getTownVars(int t){
		if (t >= 0 && t < townTiles.size()){
			return ((Tile)townTiles.get(t)).getVars();
		}else{
			return 0;
		}
	}

	public int getUnitVars(int t){
		if (t >= 0 && t < unitTiles.size()){
			return ((Tile)unitTiles.get(t)).getVars();
		}else{
			return 0;
		}
	}

	public int getWidth(){
		return twidth;
	}

	public int getHeight(){
		return theight;
	}

	public Point getPosition(int x, int y){
		return new Point(x * twidth, y * theight);
	}

	public Tile getBaseTile(int t){
		if (t >= 0 && t < baseTiles.size()){
			return (Tile)baseTiles.get(t);
		}else{
			return null;
		}
	}

	public Image getBaseTileImage(int t){
		if (t >= 0 && t < baseTiles.size()){
			return ((Tile)baseTiles.get(t)).getImage();
		}else{
			return null;
		}
	}

	public Tile getTopTile(int t){
		if (t >= 0 && t < topTiles.size()){
			return (Tile)topTiles.get(t);
		}else{
			return null;
		}
	}

	public Image getTopTileImage(int t){
		if (t >= 0 && t < topTiles.size()){
			return ((Tile)topTiles.get(t)).getImage();
		}else{
			return null;
		}
	}

	public Image getTownTileImage(int t){
		if (t >= 0 && t < townTiles.size()){
			return ((Tile)townTiles.get(t)).getImage();
		}else{
			return null;
		}
	}

	public Tile getUnitTile(int t){
		if (t >= 0 && t < unitTiles.size()){
			return (Tile)unitTiles.get(t);
		}else{
			return null;
		}
	}

	public Image getUnitTileImage(int t){
		if (t >= 0 && t < unitTiles.size()){
			return ((Tile)unitTiles.get(t)).getImage();
		}else{
			return null;
		}
	}

	public String getBaseTileProp(int t, String k){
		if (t >= 0 && t < baseTiles.size()){
			return ((Tile)baseTiles.get(t)).getProp(k);
		}else{
			return null;
		}
	}

	public String getTopTileProp(int t, String k){
		if (t >= 0 && t < topTiles.size()){
			return ((Tile)topTiles.get(t)).getProp(k);
		}else{
			return null;
		}
	}

	public String getTownTileProp(int t, String k){
		if (t >= 0 && t < townTiles.size()){
			return ((Tile)townTiles.get(t)).getProp(k);
		}else{
			return null;
		}
	}

	public String getUnitTileProp(int t, String k){
		if (t >= 0 && t < unitTiles.size()){
			return ((Tile)unitTiles.get(t)).getProp(k);
		}else{
			return null;
		}
	}

	public Point getBaseTileOffset(int t, int v){
		if (t >= 0 && t < baseTiles.size()){
			Tile tl = (Tile)baseTiles.get(t);
			return tl.getOffset(v);
		}else{
			return null;
		}
	}

	public Point getTopTileOffset(int t, int v){
		if (t >= 0 && t < topTiles.size()){
			Tile tl = (Tile)topTiles.get(t);
			return tl.getOffset(v);
		}else{
			return null;
		}
	}

	public Point getTownTileOffset(int t, int v){
		if (t >= 0 && t < townTiles.size()){
			Tile tl = (Tile)townTiles.get(t);
			return tl.getOffset(v);
		}else{
			return null;
		}
	}

	public Point getUnitTileOffset(int t, int v){
		if (t >= 0 && t < unitTiles.size()){
			Tile tl = (Tile)unitTiles.get(t);
			return tl.getOffset(v);
		}else{
			return null;
		}
	}
}