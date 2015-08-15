
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class GameObjectLight{
	protected String name;
	protected String description;
	protected Image icon;
	protected Image[] iconWithColor;
	protected Image sicon;
	protected int[] cost;

	protected Hashtable properties;

	public GameObjectLight(Image ico){
		icon = ico;
		properties = new Hashtable();

		sicon = GameWorld.getScaledImage(icon, GameWorld.SMALL_ICON_SIZE, GameWorld.SMALL_ICON_SIZE);

		iconWithColor = new Image[GameWorld.OWNER_COLOR.length];
		for (int i=0; i<GameWorld.OWNER_COLOR.length; i++){
			iconWithColor[i] = GameWorld.getReplaceColor(icon, 
				GameWorld.MASK_COLOR_STANDARD, GameWorld.OWNER_COLOR[i]);
		}
		cost = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<cost.length; i++){
			cost[i] = 0;
		}
	}

	public Image getSmallIcon(){
		return sicon;
	}

	public int[] getCost(){
		return cost;
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public Image getIcon(){
		return icon;
	}

	public Image getIcon(int o){
		if (o >= 0 && o < iconWithColor.length){
			return iconWithColor[o];
		}
		return icon;
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
		}else{
			properties.put(k, v);
		}
	}

	public String getProp(String k){
		return (String)properties.get(k);
	}
}