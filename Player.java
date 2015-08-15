import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Player extends GameObjectLight{
	protected ArrayList areas;
	protected ArrayList techs;
	protected int bases, armies;
	protected int defBase;
	protected int defUnit;
	protected int favorUnit;

	public Player(Image icon){
		super(icon);

		areas = new ArrayList();
		techs = new ArrayList();
		defBase = 0;
		defUnit = 0;
		favorUnit = -1;
	}

	public void colorise(int gi){
		icon = GameWorld.getReplaceColor(icon, GameWorld.MASK_COLOR_STANDARD, GameWorld.OWNER_COLOR[gi]);
	}
	
	public int getBaseCount(){
		return bases;
	}

	public int getArmyCount(){
		return armies;
	}

	public void setArmyCount(int c){
		armies = c;
	}

	public void setBaseCount(int c){
		bases = c;
	}

	public int getAreaSize(){
		return areas.size();
	}

	public Rectangle getArea(int i){
		if (i < 0 || i >= getAreaSize()){
			return null;
		}
		return (Rectangle)areas.get(i);
	}

	public int getTechSize(){
		return techs.size();
	}

	public int getTech(int i){
		if (i < 0 || i >= getTechSize()){
			return 0;
		}
		return ((Integer)techs.get(i)).intValue();
	}

	public boolean inArea(int x, int y){
		if (getAreaSize() == 0){
			return true;
		}
		for (int i=0; i<getAreaSize(); i++){
			if (getArea(i).contains(x, y)){
				return true;
			}
		}
		return false;
	}

	public boolean inArea(Point p){
		if (getAreaSize() == 0){
			return true;
		}
		for (int i=0; i<getAreaSize(); i++){
			if (getArea(i).contains(p)){
				return true;
			}
		}
		return false;
	}

	public int getDefBaseType(){
		return defBase;
	}

	public int getDefUnitType(){
		return defUnit;
	}

	public int getFavorUnitType(){
		return favorUnit;
	}

	public void addProp(String k, String v){
		if (k.compareToIgnoreCase("name") == 0){
			name = v;
		}else if (k.compareToIgnoreCase("description") == 0){
			description = v;
		}else if (k.startsWith("cost")){
			try{
				int comma = v.indexOf(",");
				int c = Integer.parseInt(v.substring(0, comma));
				int q = Integer.parseInt(v.substring(comma+1, v.length()));
				if (c < 0 || c >= GameWorld.RESOURCE_SIZE){
					return;
				}
				cost[c] = q;
			}catch(Exception e){
			}
		}else if (k.startsWith("area")){
			ArrayList temp = new ArrayList();
			StringTokenizer tok = new StringTokenizer(v, ",");
			while (tok.hasMoreElements()){
				temp.add((String)tok.nextElement());
			}
			if (temp.size() >= 4){
				try{
					areas.add(new Rectangle(Integer.parseInt((String)temp.get(0)),
											Integer.parseInt((String)temp.get(1)),
											Integer.parseInt((String)temp.get(2)),
											Integer.parseInt((String)temp.get(3))));
				}catch(NumberFormatException ne){
				}
			}
		}else if (k.compareToIgnoreCase("tech") == 0){
			StringTokenizer tok = new StringTokenizer(v, ",");
			while (tok.hasMoreElements()){
				try{
					techs.add(new Integer(Integer.parseInt(tok.nextToken())));
				}catch(Exception e){
				}
			}
		}else if (k.compareToIgnoreCase("base") == 0){
			try{
				defBase = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("unit") == 0){
			try{
				defUnit = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("favorUnit") == 0){
			try{
				favorUnit = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else{
			properties.put(k, v);
		}
	}
}

class PlayerLoader extends GameObjectLoader{
	public PlayerLoader(GameWorld world){
		super(world, "player.ini");
	}

	protected void add(Hashtable section, Image icon){
		Player p = new Player(icon);

		for (Enumeration ec = section.keys(); ec.hasMoreElements();){
			String k = (String)ec.nextElement();
			String v = (String)section.get(k);
			p.addProp(k, v);
		}

		if (objects.size() < GameWorld.OWNER_SIZE){
			int index = objects.size();
			objects.add(p);
			p.colorise(index);
		}
	}

	public Player getPlayer(int p){
		if (p < 0 || p >= objects.size()){
			return null;
		}else{
			return (Player)objects.get(p);
		}
	}
}
