import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Overlay extends GameObject{
	public final static int H_LAND = 0;
	public final static int H_SEA = 1;
	public final static int H_AIR = 2;

	protected int move;
	protected int height;
	//Probability
	private double prob;
	private int block;
	private int base;
	//Extra bonuses
	private int bonus;
	//MH130
	private int order;

	public Overlay(Tile t){
		super(t);

		move = 1;
		prob = 0.5;
		block = (int)(prob * GameData.MAX_CELLS);
		base = GameWorld.TOWN_LAND;
		height = H_LAND;
		bonus = -1;
		//MH130
		order = 0;
	}

	public int getOrder(){
		return order;
	}

	public int getBonus(){
		return bonus;
	}

	public int getMove(){
		return move;
	}

	public int getHeight(){
		return height;
	}

	public double getProbability(){
		return prob;
	}

	public int getBlock(){
		return block;
	}

	public int getBase(){
		return base;
	}

	public void addProp(String k, String v){
		if (k.compareToIgnoreCase("move") == 0){
			try{
				move = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("prob") == 0){
			try{
				prob = Double.parseDouble(v);
				block = (int)(prob * GameData.MAX_CELLS);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("block") == 0){
			try{
				block = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("base") == 0){
			try{
				base = Integer.parseInt(v);
			}catch(Exception e){
			}
		//MH130
		}else if (k.compareToIgnoreCase("order") == 0){
			try{
				order = Integer.parseInt(v);
			}catch(Exception e){
			}
		//MH130
		}else if (k.compareToIgnoreCase("bonus") == 0){
			try{
				bonus = Integer.parseInt(v);
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