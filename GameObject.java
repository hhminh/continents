//to-do: find relationship between gameobjectlight and gameobject


import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class GameObject{
	public final static int P_MAX = 5;

	protected Hashtable properties;
	protected Tile tile;

	protected int[] production;
	protected int[] quantity;
	protected int pcount;

	public GameObject(Tile t){
		tile = t;
		properties = new Hashtable();

		pcount = 0;
		production = new int[P_MAX];
		quantity = new int[P_MAX];
	}

	public Point getOffset(int v){
		return tile.getOffset(v);
	}

	public int getVars(){
		return tile.getVars();
	}

	public int getProductions(){
		return pcount;
	}

	public int getProdType(int i){
		if (i < P_MAX){
			return production[i];
		}else{
			return -1;
		}
	}

	public Tile getTile(){
		return tile;
	}

	public int getProdQuant(int i){
		if (i < P_MAX){
			return quantity[i];
		}else{
			return -1;
		}
	}

	//can work on a certain production
	public boolean canWork(int p){
		for (int i=0; i<production.length; i++){
			if (production[i] == p){
				return true;
			}
		}
		return false;
	}

	public void addProp(String k, String v){
		if (k.startsWith("production")){
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

	public String getProp(String k){
		return (String)properties.get(k);
	}

	public void draw(Graphics g, Component c, int x, int y){
		tile.drawTile(g, c, x, y);
	}

	//draw variation
	public void draw(Graphics g, Component c, int v, int x, int y){
		tile.drawTile(g, c, v, x, y);
	}
}