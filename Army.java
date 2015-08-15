//MIN01 - added leader capability
//MIN02 - bug?
//MIN03 - allow moving combat unit to front even if no points incase no others can combat
//
// ******************************************
// ARMY CAN BE NULL, MEANING A DEAD ARMY
// ******************************************

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Army implements ResourceHolder, UnitSwappable, Serializable{
	public static final int DEFAULT_LIMIT = 4;
	public static final int ATTACK_MOVE_COST = 1;

	public static final int MASK_AUTO_ORGANIZE = 1;
	public static final int MASK_LEADER_ATTACK = 2;
	public static final int MASK_ALLOW_RETREAT = 4;
	public static final int MASK_LOAD_FOOD = 8;
	public static final int MASK_ALLOW_ENTERTAIN = 16;
	public static final int MASK_ALLOW_POLICE = 32;

	protected ArrayList units;
	protected int posx;
	protected int posy;
	protected int desx;
	protected int desy;
	protected int leader;

	protected int owner;
	protected int limit;
	protected int leaderLevel;

	protected int move;
	protected int moveMax, moveType;
	protected int[] resources;
	protected Job job;

	private boolean selected;
	private boolean allowAI;
	private int flag;

	public Army(int o){
		owner = o;
		if (owner < 0){
			owner = 0;
		}else if (owner > GameWorld.OWNER_MAX){
			owner = GameWorld.OWNER_MAX;
		}
		units = new ArrayList();

		//start up no leader
		leader = -1;
		limit = DEFAULT_LIMIT;
		//MIN10 - first created cant move yet
		move = 0;
		moveMax = 1;
		//MIN29
		moveType = GameWorld.TOWN_LAND;

		//resources
		resources = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<resources.length; i++){
			resources[i] = 0;
		}
		//job
		job = null;

		//run-time only
		selected = false;
		allowAI = true;
		setFlag(MASK_AUTO_ORGANIZE);
		setFlag(MASK_LEADER_ATTACK);
		setFlag(MASK_ALLOW_RETREAT);
		setFlag(MASK_LOAD_FOOD);
		setFlag(MASK_ALLOW_POLICE);
	}

	public int getAlternativeLeader(UnitTypeLoader ul, int index){
		int ldr = getLeader();
		if (index == ldr) {
			Unit u = get(index);
			if (u != null) {
				UnitType ut = ul.getUnitType(u.getType());
				if (ut != null && ut.getLeaderLevel() > 0) {
					for (int i=0; i<getCount(); i++) {
						if (i == index) {
							continue;
						}
						Unit u2 = get(i);
						if (u2 != null) {
							UnitType ut2 = ul.getUnitType(u2.getType());
							if (ut2.getLeaderLevel() >= getCount()-1) {
								return i;
							}
						}
					}
				}
			}
			return ldr;
		}else{
			return ldr;
		}
	}

	public int getAlternativeLeader(UnitTypeLoader ul, Unit unt){
		int index = getIndex(unt);
		int ldr = getLeader();

		if (index <0 || index>=getCount()) {
			return ldr;
		}

		if (index == ldr) {
			Unit u = get(index);
			if (u != null) {
				UnitType ut = ul.getUnitType(u.getType());
				if (ut != null && ut.getLeaderLevel() > 0) {
					for (int i=0; i<getCount(); i++) {
						if (i == index) {
							continue;
						}
						Unit u2 = get(i);
						if (u2 != null) {
							UnitType ut2 = ul.getUnitType(u2.getType());
							if (ut2.getLeaderLevel() >= getCount()-1) {
								return i;
							}
						}
					}
				}
			}
			return ldr;
		}else{
			return ldr;
		}
	}

	public boolean canRemove(UnitTypeLoader ul, int index){
		int ldr = getLeader();
		if (getCount() > DEFAULT_LIMIT+1 && index == ldr) {
			int alternative = getAlternativeLeader(ul, index);
			//No other alternatives
			if (alternative == ldr) {
				if (getOwner() == GameWorld.PLAYER_OWNER) {
					GameWorld.printMessage("The commander can not be removed from fleet because there is no alternative leader found. Please remove some units first to leave more rooms.");
				}
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}

	public boolean canRemove(UnitTypeLoader ul, Unit unt){
		int ldr = getLeader();
		int index = getIndex(unt);
		if (getCount() > DEFAULT_LIMIT+1 && index == ldr) {
			int alternative = getAlternativeLeader(ul, index);
			//No other alternatives
			if (alternative == ldr) {
				if (getOwner() == GameWorld.PLAYER_OWNER) {
					GameWorld.printMessage("The commander can not be removed from fleet because there is no alternative leader found. Please remove some units first to leave more rooms.");
				}
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}

	public void setJob(Job j){
		job = j;
	}

	public Job getJob(){
		return job;
	}

	public int getLimit(){
		return limit;
	}

	public boolean transfer(Army other, UnitTypeLoader ul, int i){
		//MH106
		if (!other.canRemove(ul, i)) {
			return false;
		}
		//MH106
		Unit u = other.get(i);
		boolean result = add(u, ul);
		if (result){
			other.remove(i, ul);
		}
		return result;
	}

	public boolean transfer(UnitSwappable other, int i, UnitTypeLoader ul, HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, TechLoader tel){
		//MH106
		if (other instanceof Army && !((Army)other).canRemove(ul, i)) {
			return false;
		}
		//MH106
		Unit u = other.get(i);
		boolean result = add(u, ul);
		if (result){
			//MIN17
			if (other instanceof Army){
				((Army)other).remove(i, ul);
			}else{
				other.remove(i);
			}
			if (other instanceof House){
				((House)other).recalProduction(hl, ul, tel);
			}else if (other instanceof Site){
				((Site)other).recalProduction(tl, ol, ul, tel);
			}
		}
		return result;
	}

	public boolean capture(Army enemy, UnitTypeLoader uloader){
		int count = 0;
		for (int i=0; i<enemy.getCount(); i++){
			Unit eu = enemy.get(i);
			if (eu == null){
				continue;
			}
			UnitType eut = uloader.getUnitType(eu.getType());
			if (eut != null){
				int captureChance = (int)(Randomizer.getNextRandom() * GameWorld.CAPTURE_CHANCE);
				int defendChance = (int)(Randomizer.getNextRandom() * (eu.getHP() + eut.getDefend(eu)));
				if (captureChance > defendChance && add(enemy.get(i), uloader)){
					enemy.remove(i, uloader);
					count++;
				}
			}
		}
		return (count > 0);
	}

	public void clearFlag(int f){
		flag &= ~f;
	}

	public void setFlag(int f){
		flag |= f;
	}

	public int getFlag(int f){
		//System.out.println(flag + "/" + f + "/" + (flag & f));
		return flag & f;
	}

	public int getCombat(){
		if (leader == -1){
			return units.size();
		}else{
			return units.size() + leaderLevel;
		}
	}

	public int getResourceCount(){
		return resources.length;
	}

	public int getResource(int r){
		return resources[r];
	}

	public int transfer(ResourceHolder other, int r, int amount){
		if (resources[r] >= GameWorld.RESOURCE_LIMIT_SMALL[r]){
			return 0;
		}else if (amount + resources[r] > GameWorld.RESOURCE_LIMIT_SMALL[r]){
			//cant store more than the limit
			amount = GameWorld.RESOURCE_LIMIT_SMALL[r] - resources[r];
		}
		int moved = other.reduce(r, amount);
		resources[r] += moved;
		return moved;
	}
	
	public int reduce(int r, int amount){
		if (amount > resources[r]){
			return 0;
		}else{
			resources[r] -= amount;
			return amount;
		}
	}

	public void setResourceDebug(int r, int amount){
		resources[r] = amount;
		if (resources[r] > GameWorld.RESOURCE_LIMIT_SMALL[r]){
			resources[r] = GameWorld.RESOURCE_LIMIT_SMALL[r];
		}
		if (resources[r]<0) {
			resources[r] = 0;
		}
	}

	public void setSelection(boolean s){
		selected = s;
	}

	public boolean getSelection(){
		return selected;
	}

	public void setDestination(int x, int y){
		desx = x;
		desy = y;
	}

	public void setPosition(int x, int y, int land){
		posx = x;
		posy = y;
		//MIN29
		moveType = land;
	}

	public void setPositionDynamic(int x, int y, int land, UnitTypeLoader ul){
		posx = x;
		posy = y;
		//MIN29
		if (moveType != land){
			moveType = land;
			recalMove(ul);
			//forcing proper display
			canMove(land, ul, true);
		}else{
			moveType = land;
		}
	}

	public int getX(){
		return posx;
	}

	public int getY(){
		return posy;
	}

	public Point getPosition(){
		return new Point(posx, posy);
	}

	public int getDX(){
		return desx;
	}

	public int getDY(){
		return desy;
	}

	public Point getDestination(){
		return new Point(desx, desy);
	}

	public int getOwner(){
		return owner;
	}

	public void changeOwner(int o){
		if (owner < 0 || owner > GameWorld.OWNER_MAX){
			return;
		}
		owner = o;
	}

	public Color getOwnerColor(){
		return GameWorld.OWNER_COLOR[owner];
	}

	public boolean add(Unit u, UnitTypeLoader uloader){
		//add unit only if in limit
		if (units.size () >= limit || u == null || uloader == null){
			return false;
		}
		//add unit only if valid type
		UnitType ut = uloader.getUnitType(u.getType());
		if (ut == null){
			return false;
		}
		//auto-assign leader
		int l = ut.getLeaderLevel();
		if (l > 0){
			if (leader <= -1){
				leader = units.size();
				leaderLevel = l;
				limit = DEFAULT_LIMIT + leaderLevel;
			}else{
				Unit u2 = (Unit)units.get(leader);
				UnitType ut2 = uloader.getUnitType(u2.getType());
				if (ut2.getLeaderLevel() < l){
					leader = units.size();
					leaderLevel = l;
					limit = DEFAULT_LIMIT + leaderLevel;
				}
			}
		}
		//add unit
		units.add(u);
		//MH106 Implement transport require complete look over army
		recalMove(uloader);
		//These are old rules
		/*
		//movement is the min of all unit
		//MIN29 added land type
		if (ut.canMove(moveType)){
			//MH106 transport rules
			if ((moveMax > ut.getMove(u)) || (ut.isTransport() && moveMax < ut.getMove(u))){
				moveMax = ut.getMove(u);
			//or when there is only 1, it is the max movement points
			}else if (units.size() < 2){
				moveMax = ut.getMove(u);
			//special case when the olders are not moveable and the newer will carry them
			}else if (moveMax == 0){
				moveMax = ut.getMove(u);
			}
			if (move > moveMax){
				move = moveMax;
			}
		//MIN29 when there is only 1
		}else if (units.size() < 2){
			moveMax = 0;
			move = 0;
		}
		*/
		return true;
	}

	public boolean chargeMove(int m){
		move -= m;
		//can move anymore?
		if (move <=0){
			move = 0;
			return true;
		}else{
			return false;
		}
	}

	public int getMoraleBonus(){
		return (int)(resources[GameWorld.RESOURCE_HAPPY] * 100 / GameWorld.RESOURCE_LIMIT_SMALL[GameWorld.RESOURCE_HAPPY]);
	}

	public int getRecoveryBonus(){
		return (int)(resources[GameWorld.RESOURCE_HAPPY] * GameWorld.RESOURCE_SLICE / GameWorld.RESOURCE_LIMIT_SMALL[GameWorld.RESOURCE_HAPPY]);
	}

	//restore
	public boolean newTurn(UnitTypeLoader uloader){
		move = moveMax;
		int noSupplies = 0;
		//Morales
		int morale_increase = 0;
		for (int i=0; i<units.size(); i++){
			Unit u = (Unit)units.get(i);
			//generate morales
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null){
				for (int j=0; j<ut.getProductions(); j++) {
					if (ut.getProdType(j) == GameWorld.RESOURCE_HAPPY) {
						morale_increase += ut.getProdQuant(j);
						break;
					}
				}
			}
		}
		setResourceDebug(GameWorld.RESOURCE_HAPPY, resources[GameWorld.RESOURCE_HAPPY] + morale_increase);
		//Guard bonus
		int guard_increase = 0;
		for (int i=0; i<units.size(); i++){
			Unit u = (Unit)units.get(i);
			//generate morales
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null){
				for (int j=0; j<ut.getProductions(); j++) {
					if (ut.getProdType(j) == GameWorld.RESOURCE_SECURITY) {
						guard_increase += ut.getProdQuant(j);
						break;
					}
				}
			}
		}
		setResourceDebug(GameWorld.RESOURCE_SECURITY, resources[GameWorld.RESOURCE_SECURITY] + guard_increase);
		//Morale bonus to healing
		int recoverBonus = getRecoveryBonus();
		//Consumption
		for (int i=0; i<units.size(); i++){
			Unit u = (Unit)units.get(i);
			//consume supplies
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null){
				boolean noFood = true;
				int failed = 0;
				//consumption
				for (int j=0; j<ut.getConsumptions(); j++){
					int c = ut.getConsume(j);
					if (c>= 0 && c <GameWorld.RESOURCE_SIZE){
						int q = ut.getConQuant(j);
						if (resources[c] < q){
							failed++;
						}else{
							if (c == GameWorld.RESOURCE_FOOD) {
								noFood = false;
							}
							resources[c] -= q;
						}
					}
				}
				//morale bonus to healing frontliners
				while (recoverBonus > 0 && u.getHP()<u.getMaxHP()) {
					u.heal();
					recoverBonus--;
				}
				//normal healing process
				//if (failed == 0){
				if (!noFood) {
					//heal unit
					u.heal();
				}
				if (failed == 0){
					//Restore combat points
					u.setCombat(ut.getCombat(u));
				}else{
					noSupplies++;
					//Apply penalty
					if (ut.getCombat(u) > 0){
						u.setCombat(GameWorld.RESOURCE_PENALTY);
					}else{
						u.setCombat(0);
					}
				}
			}
		}
		//is it that serious to do a report?
		if (noSupplies >= getCount()){
			//if (owner == GameWorld.PLAYER_OWNER){
				//GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>Sir, there is not enough supplies for the army");
			//}
			return false;
		}else{
			return true;
		}
	}

	//to-do need to change
	public boolean canMove(int t, UnitTypeLoader uloader, boolean display){
		//System.err.println("canMove");
		for (int i=0; i<units.size(); i++){
			Unit u = (Unit)units.get(i);
			if (u != null) {
				UnitType ut = uloader.getUnitType(u.getType());
				if (ut != null){
					if (ut.canMove(t)){
						//MIN09 - swap unit to display rightly
						if (display && i > 0){
							swap(i, 0);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public int getMove(){
		return move;
	}

	public int getMoveMax(){
		return moveMax;
	}

	protected void recalMove(UnitTypeLoader uloader){
		//System.err.println("recalMove " + units.size());
		//something really big
		moveMax = 999999;
		int moveTP = 0;
		int movable = 0;

		for (int i=0; i<units.size(); i++){
			Unit u = (Unit)units.get(i);
			if (u != null) {
				UnitType ut = uloader.getUnitType(u.getType());
				//MH105 implement transport here
				//Get the max of all transport
				//System.err.println("type " + u.getType());
				if (ut != null && ut.canMove(moveType)){
					//if (ut.isTransport() && ut.getMove(u) > moveMax) {
					//	if (moveTP < ut.getMove(u)) {
					//Transport unit carries others
					if (ut.isTransport() && ut.getMove(u) > moveTP) {
							moveMax = ut.getMove(u);
							moveTP = moveMax;
							movable++;
					//	}
					//get the min but not if there is transport
					}else if (moveMax > ut.getMove(u) && moveTP < ut.getMove(u)){
						moveMax = ut.getMove(u);
						movable++;
					}
				}
			}
		}

		//This army get stuck
		if (movable == 0) {
			moveMax = 0;
		}
		//Average out
		if (move > moveMax){
			move = moveMax;
		}
	}

	protected void findLeader(UnitTypeLoader uloader){
		//System.err.println("findLeader");
		leader = -1;
		leaderLevel = 0;
		limit = DEFAULT_LIMIT;
		for (int i=0; i<units.size(); i++){
			Unit u = (Unit)units.get(i);
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null){
				int lvl = ut.getLeaderLevel();
				if (lvl > leaderLevel){
					leader = i;
					leaderLevel = lvl;
					limit = DEFAULT_LIMIT + leaderLevel;
				}
			}
		}
	}

	public void remove(int i){
		//this is define to conform interface unitswappable but is not used
	}

	public void remove(int i, UnitTypeLoader uloader){
		if (i >= 0 && i < units.size()){
			Unit u = (Unit)units.get(i);
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null){
				//remove
				units.remove(i);
				//MIN02 - leader?
				//if (i == leader){
					findLeader(uloader);
				//}
				//movement?
				//if (ut.canMove(moveType) && moveMax <= ut.getMove(u)){
				//MH106 The new rule include transport which use larger move
				if (ut.canMove(moveType)){
					recalMove(uloader);
				}
			}
		}
	}

	public void reorganize(UnitTypeLoader uloader, boolean force){
		//for leader only
		if (leader == -1 && !force){
			return;
		}
		//user doesnt disable this option?
		if (getFlag(MASK_AUTO_ORGANIZE) == 0){
			return;
		}
		//sort by hp & attack
		Unit uleader = null;
		if (leader > -1 && leader < units.size()){
			uleader = (Unit)units.get(leader);
		}
		ArrayList sortlist = new ArrayList();
		for (int i=0; i<units.size(); i++){
			Unit current = (Unit)units.get(i);

			if (sortlist.size() == 0){
				sortlist.add(current);
			}else{
				boolean done = false;
				for (int j=0; j<sortlist.size(); j++){
					Unit besthp = (Unit)sortlist.get(j);
					if (besthp.getHP() < current.getHP()){
						sortlist.add(j, current);
						done = true;
						break;
					}else if (besthp.getHP() == current.getHP()){
						UnitType ut1 = (UnitType)uloader.getUnitType(besthp.getType());
						UnitType ut2 = (UnitType)uloader.getUnitType(current.getType());
						if (ut1.getAttack(besthp) < ut2.getAttack(current)){
							sortlist.add(j, current);
							done = true;
							break;
						}
					}
				}
				if (!done){
					sortlist.add(current);
				}
			}
		}

		//reassign
		units = sortlist;
		leader = units.indexOf(uleader);
	}

	public int getCount(){
		return units.size();
	}

	public Unit get(int i){
		if (i >= 0 && i < units.size()){
			return (Unit)units.get(i);
		}else{
			return null;
		}
	}

	public int getIndex(Unit u){
		return units.indexOf(u);
	}

	public void swap(int i1, int i2){
		//swap but keep intact
		Unit u1 = (Unit)units.get(i1);
		units.set(i1, units.get(i2));
		units.set(i2, u1);
		//swap if leader?
		if (leader == i1){
			leader = i2;
		}else if (leader == i2){
			leader = i1;
		}
	}

	public void moveCombatUnitToFront(UnitTypeLoader uloader){
		int u = -1;
		if (units.size() > 0){
			int i = 0;
			while (i < units.size()){
				Unit u2 = (Unit)units.get(i);
				UnitType ut = (UnitType)uloader.getUnitType(u2.getType());
				//MIN03, combat enabled
				if (ut.isCombatUnit()){
					u = i;
					//MIN03 - leader can only attack if specified
					//therefore continue to search if leader is encountered
					if ((i != leader || getFlag(MASK_LEADER_ATTACK) == MASK_LEADER_ATTACK) && u2.getCombat() > 0){
						break;
					}
				}
				i++;
			}
		}
		//move combat unit to front
		if (u > 0){
			swap(u, 0);
		}
	}

	public void moveRangeUnitToFront(UnitTypeLoader uloader, int r){
		int u = -1;
		if (units.size() > 0){
			int i = 0;
			while (i < units.size()){
				Unit u2 = (Unit)units.get(i);
				UnitType ut = (UnitType)uloader.getUnitType(u2.getType());
				//MIN03
				if (ut.isCombatUnit() && ut.getRange(u2) >= r){
					u = i;
					//MIN03 - leader can only attack if specified
					//therefore continue to search if leader is encountered
					if ((i != leader || getFlag(MASK_LEADER_ATTACK) == MASK_LEADER_ATTACK) && u2.getCombat() > 0){
						break;
					}
				}
				i++;
			}
		}
		//move combat unit to front
		if (u > 0){
			//System.out.println("Swap " + u + " to 0"); 
			swap(u, 0);
		}
	}

	public int getLeader(){
		return leader;
	}
}