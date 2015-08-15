
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class GameObjectLoader{
	protected ArrayList objects;
	protected ArrayList buildable;
	protected Hashtable config;
	protected String file;
	protected GameWorld world;
	protected int objectCount;

	public GameObjectLoader(GameWorld w, String f){
		world = w;
		file = f;
		objects = new ArrayList();
		buildable = new ArrayList();
		config = new Hashtable();

		readConfig();
		getObjects();
	}

	public int getSize(){
		return objects.size();
	}

	protected void add(Hashtable section, Image icon){
		 //Terrain tr = new Terrain(tl);

		 //for (Enumeration ec = section.keys(); ec.hasMoreElements();){
		 //	 String k = (String)ec.nextElement();
		 //	 String v = (String)section.get(k);
		 //	 tr.addProp(k, v);
		 //}
		 //objects.add(tr);
	}

	protected void getObjects(){
		for (int i =0; i<objectCount; i++){
			Hashtable section = (Hashtable)config.get(Integer.toString(i));
			String icon = (String)section.get("icon");

			if (icon != null){
				Image img = world.loadImage(icon);
				add(section, img);
			}
	    }
 	}

	public GameObjectLight getObjectLight(int i){
		try{
			return (GameObjectLight)objects.get(i);
		}catch(Exception e){
			return null;
		}
	}

	protected void readConfig(){
		BufferedReader bin = new BufferedReader(
			new InputStreamReader(this.getClass().getResourceAsStream(file)));
		String line, txt, lastSection = null;
		Hashtable section = null;
		try{
			objectCount = 0;
			while((txt = bin.readLine()) != null){
				//remove any extra spaces
				line = txt.trim();
				if (line.startsWith("#")){
					continue;
				}
				//section handling
				if (line.startsWith("[") && line.endsWith("]")){
					if (section != null){
						config.put(Integer.toString(objectCount), section);
						objectCount++;
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
				config.put(Integer.toString(objectCount), section);
				objectCount++;
			}
		}catch(IOException io){
			io.printStackTrace();
		}
	}
}