import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class OverlayLoader{
	protected ArrayList overlays;
	protected Hashtable config;
	protected TileLoader loader;

	private int overlayCount;

	public OverlayLoader(TileLoader tl){
		loader = tl;

		overlays = new ArrayList();
		config = new Hashtable();

		readConfig();
		getOverlays();
	}

	public int getSize(){
		return overlays.size();
	}

	protected void getOverlays(){
		for (int i =0; i<overlayCount; i++){
			 Hashtable section = (Hashtable)config.get(Integer.toString(i));
			 int t = Integer.parseInt((String)section.get("tile"));

			 Tile tl = loader.getTopTile(t);
			 if (tl != null){
				 Overlay tr = new Overlay(tl);

				 for (Enumeration ec = section.keys(); ec.hasMoreElements();){
					 String k = (String)ec.nextElement();
					 String v = (String)section.get(k);
					 tr.addProp(k, v);
				 }
				 overlays.add(tr);
			 }
	    }
 	}

	protected Overlay getOverlay(int t){
		if (t >= 0 && t < overlays.size()){
			return (Overlay)overlays.get(t);
		}else{
			return null;
		}
	}

	protected void readConfig(){
		BufferedReader bin = new BufferedReader(
			new InputStreamReader(this.getClass().getResourceAsStream("overlay.ini")));
		String line, txt, lastSection = null;
		Hashtable section = null;
		try{
			overlayCount = 0;
			while((txt = bin.readLine()) != null){
				//remove any extra spaces
				line = txt.trim();
				if (line.startsWith("#")){
					continue;
				}
				//section handling
				if (line.startsWith("[") && line.endsWith("]")){
					if (section != null){
						config.put(Integer.toString(overlayCount), section);
						overlayCount++;
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
				config.put(Integer.toString(overlayCount), section);
				overlayCount++;
			}
		}catch(IOException io){
			io.printStackTrace();
		}
	}
}