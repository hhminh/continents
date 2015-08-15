
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.applet.*;

public class UnitType extends GameObject{
	public final static int CACHE = 1;

	public final static int TYPE_BUILDABLE = 0;
	public final static int TYPE_UPGRADE = 1;
	public final static int TYPE_KING = 2;

	public final static int MAX_XP = 999999;
	public final static int HP_START = 3;

	//levelup = random * xp > level_up_chance[current_level]
	public final static int[] LEVEL_UP_CHANCE = {2,4,16,32,64};
	public final static int MAX_LEVEL = LEVEL_UP_CHANCE.length - 1;
	public final static int[] LEVEL_UP_ATTACK = {0,1,1,2,2};
	public final static int[] LEVEL_UP_DEFEND = {0,0,1,1,1};
	public final static String[] LEVEL_NAME = {"Recruit","Regular","Veteran","Hardened","Elite"};

	public final static int C_MAX = P_MAX;

	protected String name;
	protected String description;
	protected TechLoader teloader;
	protected String family;

	protected int type;
	protected int deftech;
	protected int promotion;
	protected int move;
	protected int speed;
	protected int combat;
	protected int physical;
	protected int toughness;
	protected int xhp;
	protected int attack;
	protected int defend;
	protected int range;
	protected int leaderLevel;

	protected boolean transport;

	protected ArrayList moveable;
	protected ArrayList sounds;
	protected ArrayList workable;
	protected Image sicon;

	protected int[] consume;
	protected int[] cquant;
	protected int ccount;
	protected int[] xattack;
	protected int[] xaquant;
	protected int xcount;

	//-1 if do not require
	protected int require_trait;

	protected int[] cost;

	protected int pref_house, pref_terrain, pref_overlay;

	private GameWorld world;
	private ClipLoader cloader;

	public UnitType(GameWorld w, TechLoader tel, ClipLoader cl, Tile t){
		super(t);

		//MIN15
		world = w;
		//sicon = null;
		//MIN17
		sicon = world.getSubImage(t.getImage(),0,0,t.getWidth(),t.getHeight(),0,0,GameWorld.SMALL_ICON_SIZE,GameWorld.SMALL_ICON_SIZE);
		//End MIN17
		//MIN15
		teloader = tel;
		cloader = cl;

		move = 1;
		speed = 1;
		physical = 0;
		toughness = 0;
		xhp = 0;
		range = 1;
		combat = 1;
		type = TYPE_BUILDABLE;
		promotion = -1;
		deftech = -1;

		family = "";

		transport = false;

		require_trait = -1;

		pref_house = -1;
		pref_terrain = -1;
		pref_overlay = -1;

		ccount = 0;
		consume = new int[C_MAX];
		cquant = new int[C_MAX];
		xcount = 0;
		xattack = new int[C_MAX];
		xaquant = new int[C_MAX];
		cost = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<cost.length; i++){
			cost[i] = 0;
		}

		moveable = new ArrayList();
		sounds = new ArrayList();
		workable = new ArrayList();
	}

	public int getPrefHouse(){
		return pref_house;
	}

	public int getPrefTerrain(){
		return pref_terrain;
	}

	public int getPrefOverlay(){
		return pref_overlay;
	}

	public void setRequireTrait(int rt){
		require_trait = rt;
	}

	public int getRequireTrait(){
		return require_trait;
	}

	public int getDefTech(){
		return deftech;
	}

	public Image getSmallIcon(){
		return sicon;
	}

	public int getXAttacks(){
		return xcount;
	}

	public int getXAttack(int i){
		if (i < C_MAX){
			return xattack[i];
		}else{
			return -1;
		}
	}

	public int getXAQuant(int i){
		if (i < C_MAX){
			return xaquant[i];
		}else{
			return -1;
		}
	}

	public int getConsumptions(){
		return ccount;
	}

	public int getConsume(int i){
		if (i < C_MAX){
			return consume[i];
		}else{
			return -1;
		}
	}

	public int getConQuant(int i){
		if (i < C_MAX){
			return cquant[i];
		}else{
			return -1;
		}
	}

	public int[] getCost(){
		return cost;
	}

	public void setFamily(String f){
		family = f;
	}

	public String getFamily(){
		return family;
	}

	public boolean isTransport(){
		return transport;
	}

	public boolean canWork(){
		int criteria = 0;
		for (int i=0; i<pcount; i++){
			if (production[i] == GameWorld.RESOURCE_HAMMER ||
					production[i] == GameWorld.RESOURCE_FOOD){
				criteria++;
			}
		}

		return (criteria > 1);
	}

	public int getMove(Unit u){
		if (u.getUpgrade() > -1){
			return move + teloader.getTech(u.getUpgrade()).getUMove();
		}
		return move;
	}

	public int getSpeed(Unit u){
		if (u.getUpgrade() > -1){
			return speed + teloader.getTech(u.getUpgrade()).getUSpeed();
		}
		return speed;
	}

	public boolean canMove(int base){
		//System.err.println("canMove " + base);
		Integer num = new Integer(base);
		boolean rslt = moveable.contains(num);
		//System.err.println("end canMove " + base);
		return rslt;
	}

	public boolean canWork(int base){
		Integer num = new Integer(base);
		return workable.contains(num);
	}

	//added with weapon to get attack value
	public int getPhysical(){
		return physical;
	}

	public int getToughness(){
		return toughness;
	}

	public int getXHP(){
		return xhp;
	}

	public boolean isBuildable(){
		return (type == TYPE_BUILDABLE);
	}

	public int getAttack(Unit u){
		int value = attack;
		int lvl = u.getLevel();
		//System.err.println(lvl);
		if (lvl < 0) {
			lvl = 0;
		}else if (lvl > MAX_LEVEL) {
			lvl = MAX_LEVEL;
		}
		value += LEVEL_UP_ATTACK[lvl];
		if (u.getUpgrade() > -1){
			value += teloader.getTech(u.getUpgrade()).getUAttack();
		}
		return value;
	}

	public int getDefend(Unit u){
		int value = defend;
		int lvl = u.getLevel();
		if (lvl < 0 || lvl >= LEVEL_UP_DEFEND.length) {
			lvl = 0;
		}
		value += LEVEL_UP_DEFEND[u.getLevel()];
		if (u.getUpgrade() > -1){
			value += teloader.getTech(u.getUpgrade()).getUDefend();
		}
		return value;
	}

	public int getRange(Unit u){
		if (u.getUpgrade() > -1){
			return range + teloader.getTech(u.getUpgrade()).getURange();
		}
		return range;
	}

	public int getCombat(Unit u){
		if (u.getUpgrade() > -1){
			return combat + teloader.getTech(u.getUpgrade()).getUCombat();
		}
		return combat;
	}

	public int getAttackChance(Unit u){
		//physical is now part of damage
		//int chance = (int)((getAttack(u) + 1) * Randomizer.getNextRandom() + physical);
		int chance = (int)((getAttack(u) + 1) * Randomizer.getNextRandom() + speed);
		return chance;
	}

	public int getDefendChance(Unit u){
		int chance = (int)((getDefend(u) + 1) * Randomizer.getNextRandom() + toughness);
		return chance;
	}

	public int getLeaderLevel(){
		return leaderLevel;
	}

	public String getName(){
		return name;
	}

	public boolean isCombatUnit(){
		return attack > 0;
	}

	public boolean isCombatable(){
		//physical is now part of damage
		return attack + physical > 0;
		//return attack > 0;
	}

	public int getType(){
		return type;
	}

	public boolean isKing(){
		return type == TYPE_KING;
	}

	public int getPromotion(){
		return promotion;
	}

	public String getDescription(){
		return description;
	}

	public void addProp(String k, String v){
		if (k.startsWith("production")){
			if (pcount < P_MAX){
				try{
					int comma = v.indexOf(",");
					int p = Integer.parseInt(v.substring(0, comma));
					int q = Integer.parseInt(v.substring(comma+1, v.length()));
					if (p < 0 || p >= GameWorld.RESOURCE_SIZE){
						return;
					}
					production[pcount] = p;
					quantity[pcount] = q;
					pcount++;
				}catch(Exception e){
				}
			}
		}else if (k.startsWith("consume")){
			if (ccount < C_MAX){
				try{
					int comma = v.indexOf(",");
					int c = Integer.parseInt(v.substring(0, comma));
					int q = Integer.parseInt(v.substring(comma+1, v.length()));
					if (c < 0 || c >= GameWorld.RESOURCE_SIZE){
						return;
					}
					consume[ccount] = c;
					cquant[ccount] = q;
					ccount++;
				}catch(Exception e){
				}
			}
		}else if (k.startsWith("xattack")){
			if (ccount < C_MAX){
				try{
					int comma = v.indexOf(",");
					int c = Integer.parseInt(v.substring(0, comma));
					int q = Integer.parseInt(v.substring(comma+1, v.length()));
					if (c < 0 || c >= GameWorld.RESOURCE_SIZE){
						return;
					}
					xattack[ccount] = c;
					xaquant[ccount] = q;
					xcount++;
				}catch(Exception e){
				}
			}
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
		//}else if (k.compareToIgnoreCase("icon") == 0){
		//	sicon = GameWorld.getScaledImage(world.loadImage(v), 16, 16);
		}else if (k.compareToIgnoreCase("name") == 0){
			name = v;
		}else if (k.compareToIgnoreCase("description") == 0){
			description = v;
		//MH105 transport type implementation needed here
		}else if (k.compareToIgnoreCase("family") == 0){
			family = v;
		}else if (k.compareToIgnoreCase("transport") == 0){
			transport = true;
		}else if (k.compareToIgnoreCase("type") == 0){
			if ("king".compareToIgnoreCase(v) == 0){
				type = TYPE_KING;
			}else if ("upgrade".compareToIgnoreCase(v) == 0){
				//MIN18 - fixed bugs
				type = TYPE_UPGRADE;
			}else{
				type = TYPE_BUILDABLE;
			}
		}else if (k.compareToIgnoreCase("pref_house") == 0){
			try{
				pref_house = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("pref_terrain") == 0){
			try{
				pref_terrain = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("pref_overlay") == 0){
			try{
				pref_overlay = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("require_trait") == 0){
			try{
				require_trait = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("promotion") == 0){
			try{
				promotion = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("move") == 0){
			try{
				move = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("speed") == 0){
			try{
				speed = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("physical") == 0){
			try{
				physical = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("toughness") == 0){
			try{
				toughness = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("xhp") == 0){
			try{
				xhp = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("attack") == 0){
			try{
				attack = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("defend") == 0){
			try{
				defend = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("range") == 0){
			try{
				range = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("leader") == 0){
			try{
				leaderLevel = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("combat") == 0){
			try{
				combat = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("deftech") == 0){
			try{
				deftech = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.startsWith("allowmove")){
			try{
				 moveable.add(new Integer(v));
			}catch(Exception e){
			}
		}else if (k.startsWith("allowwork")){
			try{
				 workable.add(new Integer(v));
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("sound") == 0){
			try{
				StringTokenizer st = new StringTokenizer(v, ",");
				while (st.hasMoreTokens()) {
					//if (CACHE == 1){
					//	sounds.add(loadSound((String)st.nextToken()));
					//}else{
					//	sounds.add(st.nextToken());
					//}
					sounds.add(cloader.getClip((String)st.nextToken()));
				}
			}catch(Exception e){
			}
		}else{
			properties.put(k, v);
		}
	}

	public void playSound(int s){
		if (s >= 0 && s < sounds.size()){
			AudioClip clip;
			//to-do cache wav
			//if (CACHE == 1){
				clip = (AudioClip)sounds.get(s);
			//}else{
			//	clip = Applet.newAudioClip(
			//		this.getClass().getResource("sounds/"+(String)sounds.get(s)));
			//}
			if (clip != null){
				clip.play();
				//if (CACHE != 1){
				//	clip = null;
				//}
			}
		}
	}

	//to-do uncache wav
	//protected AudioClip loadSound(String file){
	//	AudioClip clip = 
	//		java.applet.Applet.newAudioClip(this.getClass().getResource("sounds/"+file));
	//	return clip;
	//}
}