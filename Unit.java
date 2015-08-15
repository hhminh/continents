
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Unit implements Serializable{
	protected int type;
	protected int trait;

	protected int hp;
	protected int maxhp;

	protected byte level;
	protected int xp;

	protected int combat;
	protected int upgrade;

	protected int fname;
	protected int lname;

	//temporary for graphics
	private int frame;

	public Unit(UnitType ut, int t){
		type = t;
		trait = getRandomTrait();
		//hp = UnitType.HP_START;
		//MIN36
		setUpgrade(ut.getDefTech());
		maxhp = ut.getXHP() + UnitType.HP_START;
		hp = maxhp;
		//code starts at 0
		level = 0;
		xp = 0;
		combat = 0;
		//upgrade = -1;

		//Random names
		fname = NameDatabase.getRandomFirstName();
		lname = NameDatabase.getRandomLastName();

		frame = 0;
	}

	public String getName(){
		return NameDatabase.getCompositeName(fname, lname);
	}

	public void setUpgrade(int u){
		upgrade = u;
	}

	public int getUpgrade(){
		return upgrade;
	}

	public void setFrame(int f){
		//System.out.println("Frame " + f);
		frame = f;
	}

	public void setType(int t){
		type = t;
	}

	public int getType(){
		return type;
	}

	public void setTrait(int t){
		if (t < GameWorld.TRAIT_NORMAL || t > GameWorld.TRAIT_CHARISMATIC) {
			t = GameWorld.TRAIT_NORMAL;
		}
		trait = t;
	}

	public int getTrait(){
		return trait;
	}

	public int getHP(){
		return hp;
	}

	public int getMaxHP(){
		return maxhp;
	}

	public boolean heal(){
		if (hp < maxhp){
			hp++;
		}
		return (hp == maxhp);
	}

	public int getCombat(){
		return combat;
	}

	public void setCombat(int c){
		combat = c + level;
	}

	public void doCombat(){
		if (combat > 0){
			combat--;
		}
	}

	public int getLevel(){
		return (int)level;
	}

	public int getXP(){
		return xp;
	}

	public boolean hurt(){
		if (hp > 1){
			hp--;
			return false;
		}else{
			//die
			return true;
		}
	}

	public boolean hurt(UnitType ut){
		int damage = 1 + ut.getPhysical();
		hp -= damage;

		if (hp > 0){
			return false;
		}else{
			//die
			hp = 0;
			return true;
		}
	}

	public boolean reward(){
		//MIN08 - return true if reach max level, false if not
		//therefore promotion can happen
		if (xp < UnitType.MAX_XP){
			xp++;
		}
		if (level < UnitType.MAX_LEVEL){
			int chance = (int)(Randomizer.getNextRandom() * xp + 1);
			if (chance >= UnitType.LEVEL_UP_CHANCE[level]){
				level++;
				maxhp++;
				//return true;
			}//else{
			//	return false;
			//}
			return false;
		}else{
			//return false;
			return true;
		}
	}

	//draw unit animation
	public void draw(Graphics g, Component c, Tile tile, int x, int y, int o){
		tile.drawTile(g, c, frame, x, y, o);
	}

	//draw unit animation, but only bit of the picture
	public void draw(Graphics g, Component c, Tile tile, int x, int y, int w, int h, int o){
		tile.drawTile(g, c, frame, x, y, w, h, o);
	}

	public int getRandomTrait(){
		int chance = (int)(100 * Randomizer.getNextRandom());
		//normal-50/brave-75/delligent-85/genius-90/heroic-95/charismatic-100
		if (chance <= 50) {
			return GameWorld.TRAIT_NORMAL;
		}else if (chance <= 75) {
			return GameWorld.TRAIT_BRAVE;
		}else if (chance <= 85) {
			return GameWorld.TRAIT_DELLIGENT;
		}else if (chance <= 90) {
			return GameWorld.TRAIT_GENIUS;
		}else if (chance <= 95) {
			return GameWorld.TRAIT_HEROIC;
		}else{
			return GameWorld.TRAIT_CHARISMATIC;
		}
	}
}