

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.tree.*;

public class UnitTypeLoader implements TreeModel{
	protected ArrayList units;
	protected Hashtable rfamily;
	protected Hashtable family;
	protected Hashtable config;
	protected TileLoader loader;
	protected TechLoader teloader;
	protected ArrayList buildable;

	private int unitCount;
	private int defCombatUnit = -1;
	private int defWorkerUnit = -1;
	private int defKingUnit = -1;
	private GameWorld world;
	private ArrayList[] customBuildable;

	private ClipLoader cloader;

	public UnitTypeLoader(GameWorld w, TileLoader tl, TechLoader tel){
		loader = tl;
		teloader = tel;
		world = w;

		cloader = new ClipLoader();

		units = new ArrayList();
		rfamily = new Hashtable();
		family = new Hashtable();
		config = new Hashtable();
		buildable = new ArrayList();
		customBuildable = new ArrayList[GameWorld.OWNER_SIZE];
		for (int i=0; i<customBuildable.length; i++){
			customBuildable[i] = new ArrayList();
		}

		readConfig();
		getUnits();
	}

	//standard version
	public int getBuildableSize(){
		return buildable.size();
	}

	public UnitType getBuildable(int ind){
		return (UnitType)buildable.get(ind);
	}

	public ArrayList getBuildableIconList(){
		ArrayList iconList = new ArrayList();

		try{
			for (int i=0; i<getBuildableSize(); i++){
				iconList.add(new ImageIcon(getBuildable(i).getSmallIcon()));
			}
		}catch(Exception e){
		}

		return iconList;
	}
//custom version
	public boolean addCustom(int o, int ind){
		if (o < 0 || o >= GameWorld.OWNER_SIZE){
			return false;
		}
		UnitType ut = getUnitType(ind);
		if (ut == null){
			return false;
		}
		if (customBuildable[o].indexOf(ut) == -1){
			customBuildable[o].add(ut);
			return true;
		}else{
			return false;
		}
	}

	public void initCustom(TechLoader teloader, PlayerLoader ploader, GameData gd){
		clearCustom();
		for (int i=0; i<customBuildable.length; i++){
			Player player = ploader.getPlayer(i);
			if (player != null) {
				int favor = player.getFavorUnitType();
				if (favor > -1) {
					if (addCustom(i, favor)) {
					}
				}
			}
			for (int j=0; j<gd.getAdvanceSize(i); j++){
				Tech tech = teloader.getTech(gd.getAdvance(i, j));
				int newUnit = tech.getUUpgrade();
				if (newUnit > -1){
					if (addCustom(i, newUnit)){
					}
				}
			}
		}
	}

	public void clearCustom(){
		for (int i=0; i<customBuildable.length; i++){
			customBuildable[i].clear();
			customBuildable[i].addAll(buildable);
		}
	}

	public int getCustomSize(int o){
		if (o < 0 || o >= GameWorld.OWNER_SIZE){
			return 0;
		}
		return customBuildable[o].size();
	}

	public UnitType getCustom(int o, int ind){
		if (o < 0 || o >= GameWorld.OWNER_SIZE || ind < 0 || ind >= getCustomSize(o)){
			return null;
		}
		return (UnitType)(customBuildable[o].get(ind));
	}

	public ArrayList getCustomIconList(int o){
		if (o < 0 || o >= GameWorld.OWNER_SIZE){
			return null;
		}
		ArrayList iconList = new ArrayList();

		try{
			for (int i=0; i<getCustomSize(o); i++){
				iconList.add(new ImageIcon(getCustom(o, i).getSmallIcon()));
			}
		}catch(Exception e){
		}

		return iconList;
	}

//standard non affected
//get the first available type
	public int getBuildableResourceWorker(int r){
		for (int i=0; i<getBuildableSize(); i++){
			UnitType ut = getBuildable(i);
			for (int p=0; p<ut.getProductions(); p++){
				if (ut.getProdType(p) == r){
					return i;
				}
			}
		}
		return -1;
	}

	//get the first
	public int getBuildableTransport(int b){
		for (int i=0; i<getBuildableSize(); i++){
			UnitType ut = getBuildable(i);
			if (ut.canMove(b)){
				return i;
			}
		}
		return -1;
	}

	//get the first
	public int getTransport(int b){
		for (int i=0; i<getSize(); i++){
			UnitType ut = getUnitType(i);
			if (ut.canMove(b)){
				return i;
			}
		}
		return -1;
	}

	public int getIndex(UnitType t){
		return units.indexOf(t);
	}

	public int getSize(){
		return units.size();
	}

	public int getDefCombatUnit(){
		return defCombatUnit;
	}

	public int getDefWorkerUnit(){
		return defWorkerUnit;
	}

	public int getDefKingUnit(){
		return defKingUnit;
	}

	protected void getUnits(){
		for (int i =0; i<unitCount; i++){
			 Hashtable section = (Hashtable)config.get(Integer.toString(i));
			 int t = Integer.parseInt((String)section.get("tile"));

			 Tile tl = loader.getUnitTile(t);
			 if (tl != null){
				 UnitType u = new UnitType(world, teloader, cloader, tl);

				 for (Enumeration ec = section.keys(); ec.hasMoreElements();){
					 String k = (String)ec.nextElement();
					 String v = (String)section.get(k);
					 u.addProp(k, v);
				 }
				 //default
				 if (defCombatUnit == -1 && u.isCombatUnit()){
					 defCombatUnit = units.size();
				 }else if (defWorkerUnit == -1 && !u.isCombatUnit()){
					 defWorkerUnit = units.size();
				 }else if (defKingUnit == -1 && u.isKing()){
					 defKingUnit = units.size();
				 }
				 units.add(u);

				 //MH160 drawing units as tree
				 String fam = u.getFamily();
				 if (family.contains(fam)) {
					 ArrayList al = (ArrayList)family.get(fam);
					 if (al != null) {
						 al.add(u);
					 }
				 }else{
					 ArrayList al = new ArrayList();
					 al.add(u);
					 Integer fai = new Integer(family.size());
					 //2 way indexing
					 family.put(fam, al);
					 family.put(fai, fam);
					 rfamily.put(fam, fai);
				 }
				 //MH160 drawing units as tree

				 //buildable?
				 if (u.isBuildable()){
					 //System.out.println(u.getName() + " " + u.getType());
					 buildable.add(u);
					 //for (int j=0; j<customBuildable.length; j++){
					 //	 customBuildable[j].add(u);
					 //}
				 }
			 }
	    }
 	}

	protected UnitType getUnitType(int u){
		if (u >= 0 && u < units.size()){
			return (UnitType)units.get(u);
		}else{
			return null;
		}
	}

	protected void readConfig(){
		BufferedReader bin = new BufferedReader(
			new InputStreamReader(this.getClass().getResourceAsStream("unit.ini")));
		String line, txt, lastSection = null;
		Hashtable section = null;
		try{
			unitCount = 0;
			while((txt = bin.readLine()) != null){
				//remove any extra spaces
				line = txt.trim();
				if (line.startsWith("#")){
					continue;
				}
				//section handling
				if (line.startsWith("[") && line.endsWith("]")){
					if (section != null){
						config.put(Integer.toString(unitCount), section);
						unitCount++;
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
				config.put(Integer.toString(unitCount), section);
				unitCount++;
			}
		}catch(IOException io){
			io.printStackTrace();
		}
	}

	//MH160
	public Object getChild(Object parent, int index){
		if (parent instanceof String) {
			String pstr = (String)parent;
			if (pstr.equals("Root")) {
				Integer fai = new Integer(index);
				//return string name of family
				return family.get(fai);
			}else{
				ArrayList al = (ArrayList)family.get(pstr);
				if (al != null) {
					if (index > -1 && al.size() > index) {
						return al.get(index);
					}else{
						return "Wrong";
					}
				}else{
					return "Wrong";
				}
			}
		}else{
			return "Wrong";
		}
	}

	public Object getRoot(){
		return "Root";
	}

	public int getChildCount(Object parent){
		if (parent instanceof String) {
			String pstr = (String)parent;
			if (pstr.equals("Root")) {
				return family.size();
			}else{
				ArrayList al = (ArrayList)family.get(pstr);
				if (al != null) {
					return al.size();
				}else{
					return 0;
				}
			}
		}else{
			return 0;
		}
	}

	public boolean isLeaf(Object node){
		if (node instanceof String) {
			return false;
		}else if (node instanceof UnitType) {
			return true;
		}else{
			return false;
		}
	}

	public void valueForPathChanged(TreePath path, Object newValue){
		//We do not allow value change
	}

	public int getIndexOfChild(Object parent, Object child){
		if (parent instanceof String) {
			String pstr = (String)parent;

			if (child instanceof String) {
				String cstr = (String)child;			
				if (pstr.equals("Root")) {
					Integer fai = (Integer)rfamily.get(cstr);
					if (fai != null) {
						return fai.intValue();					
					}else{
						return -1;
					}
				}else{
					return -1;
				}
			}else if (child instanceof UnitType) {
				UnitType cut = (UnitType)child;
				if (!pstr.equals("Root")) {
					ArrayList al = (ArrayList)family.get(pstr);
					if (al != null) {
						return al.indexOf(cut);
					}else{
						return -1;
					}
				}else{
					return -1;
				}
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}

	public void addTreeModelListener(TreeModelListener l){
		//We do not have data changes
	}

	public void removeTreeModelListener(TreeModelListener l){
		//We do not have data changes
	}
	//MH160
}