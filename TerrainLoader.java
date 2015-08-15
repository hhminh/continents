import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class TerrainLoader{
	protected ArrayList terrains;
	protected Hashtable config;
	protected TileLoader loader;

	private int terrainCount;

	public TerrainLoader(TileLoader tl){
		loader = tl;

		terrains = new ArrayList();
		config = new Hashtable();

		readConfig();
		getTerrains();
	}

	public int getSize(){
		return terrains.size();
	}

	protected void getTerrains(){
		for (int i =0; i<terrainCount; i++){
			 Hashtable section = (Hashtable)config.get(Integer.toString(i));
			 int t = Integer.parseInt((String)section.get("tile"));

			 Tile tl = loader.getBaseTile(t);
			 if (tl != null){
				 Terrain tr = new Terrain(tl);

				 for (Enumeration ec = section.keys(); ec.hasMoreElements();){
					 String k = (String)ec.nextElement();
					 String v = (String)section.get(k);
					 tr.addProp(k, v);
				 }
				 terrains.add(tr);
			 }
	    }
 	}

	protected Terrain getTerrain(int t){
		if (t >= 0 && t < terrains.size()){
			return (Terrain)terrains.get(t);
		}else{
			return null;
		}
	}

	protected void readConfig(){
		BufferedReader bin = new BufferedReader(
			new InputStreamReader(this.getClass().getResourceAsStream("terrain.ini")));
		String line, txt, lastSection = null;
		Hashtable section = null;
		try{
			terrainCount = 0;
			while((txt = bin.readLine()) != null){
				//remove any extra spaces
				line = txt.trim();
				if (line.startsWith("#")){
					continue;
				}
				//section handling
				if (line.startsWith("[") && line.endsWith("]")){
					if (section != null){
						config.put(Integer.toString(terrainCount), section);
						terrainCount++;
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
				config.put(Integer.toString(terrainCount), section);
				terrainCount++;
			}
		}catch(IOException io){
			io.printStackTrace();
		}
	}
}