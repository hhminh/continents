
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Terrain extends GameObject{
	public final static int H_LAND = 0;
	public final static int H_SEA = 1;
	public final static int H_AIR = 2;

	protected int move;
	protected int height;
	protected Color col;

	public Terrain(Tile t){
		super(t);

		move = 1;
		height = H_LAND;
		col = Color.green;
	}

	public int getMove(){
		return move;
	}

	public int getHeight(){
		return height;
	}

	public Color getColor(){
		return col;
	}

	public void addProp(String k, String v){
		if (k.compareToIgnoreCase("move") == 0){
			try{
				move = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("height") == 0){
			if (v.compareToIgnoreCase("land") == 0){
				height = H_LAND;
			}else if (v.compareToIgnoreCase("sea") == 0){
				height = H_SEA;
			}else{
				height = H_AIR;
			}
		}else if (k.compareToIgnoreCase("color") == 0){
			StringTokenizer st = new StringTokenizer(v, ",");
			ArrayList temp = new ArrayList();
			while (st.hasMoreTokens()){
				temp.add(st.nextToken());
			}
			if (temp.size() > 2){
				try{
					col = new Color(Integer.parseInt((String)temp.get(0)),
						Integer.parseInt((String)temp.get(1)),
						Integer.parseInt((String)temp.get(2)));
				}catch(Exception e){
				}
			}
		}else if (k.startsWith("production")){
			if (pcount < P_MAX){
				try{
					int comma = v.indexOf(",");
					int p = Integer.parseInt(v.substring(0, comma));
					int q = Integer.parseInt(v.substring(comma+1, v.length()));
					if (p >= GameWorld.RESOURCE_SIZE){
						return;
					}
					production[pcount] = p;
					quantity[pcount] = q;
					pcount++;
				}catch(Exception e){
				}
			}
		}else{
			properties.put(k, v);
		}
	}

	public void draw(Graphics g, Component c, int x, int y){
		tile.drawTile(g, c, x, y);
	}

	//draw variation
	public void draw(Graphics g, Component c, int v, int x, int y){
		tile.drawTile(g, c, v, x, y);
	}
}