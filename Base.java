

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Base implements ResourceHolder, Serializable{
	public static final int MASK_AUTOMATE = 1;
	public static final int MASK_TAX = 2;

	public static final int LIMIT = 12;

	protected ArrayList sites;
	protected ArrayList houses;
	protected int posx, posy;
	protected String name;
	protected int[] resources;
	protected int type;
	protected int owner;
	protected int border, torder;
	protected int rRate, rAmount, rType;

	private boolean selected;
	private int flag;

	public Base(GameData gd, int o, int t, int x, int y){
		posx = x;
		posy = y;
		owner = o;
		type = t;
		//build, train nothing
		border = -1;
		torder = -1;
		name = NameDatabase.getNextName();

		rType = 0;
		rRate = 0;
		rAmount = 0;

		sites = new ArrayList();
		houses = new ArrayList();

		//System.out.println("Creating sites");

		for (int j=y-1; j<y+2; j++){
			for (int i=x-1; i<x+2; i++){
				int b = gd.getBaseLand(i, j);
				if (b > -1){
					//int top = gd.getTopLand(i, j);
					//Site s = new Site(i, j, b, top);
					Site s = new Site(i, j, gd);
					sites.add(s);
				}
			}
		}

		//System.out.println("Creating resource holder");

		int s = GameWorld.RESOURCE_SIZE;
		resources = new int[s];
		for (int i=0; i<s; i++){
			resources[i] = 0;
		}

		flag = 0;
		//setFlag(MASK_AUTOMATE);
		//System.out.println("Created base");
	}

	public String getName(){
		return name;
	}

	public void setName(String str){
		name = str;
	}

	//graphical size
	public int getBaseSize(){
		if (getHouseCount() < 3){
			return 0;
		}else if (getHouseCount() < 5){
			return 1;
		}else{
			return 2;
		}
	}

	public void setRType(int t){
		if (t < 0 || t >= GameWorld.TECH_SIZE){
			return;
		}
		rType = t;
	}

	public int getRType(){
		return rType;
	}

	public void setRRate(int rate){
		rRate = rate;
	}

	public int getRRate(){
		return rRate;
	}

	public int getRAmount(){
		return rAmount;
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

	public int getX(){
		return posx;
	}

	public int getY(){
		return posy;
	}

	public void setBuildOrder(int bo){
		border = bo;
	}

	public int getBuildOrder(){
		return border;
	}

	public void setTrainOrder(int to){
		torder = to;
	}

	public int getTrainOrder(){
		return torder;
	}

	public void setSelection(boolean s){
		selected = s;
	}

	public boolean sell(int res, int amount, int stock){
		if (resources[res] < amount){
			return false;
		}
		int gain = (int)(amount * GameWorld.getResourceCost(res, stock));
		if (gain <= 0){
			return false;
		}
		resources[res] -= amount;
		resources[GameWorld.RESOURCE_CURRENCY] += gain;
		return true;
	}

	public boolean buy(int res, int amount, int stock){
		int cost = (int)(amount * GameWorld.getResourceCost(res, stock));
		if (resources[GameWorld.RESOURCE_CURRENCY] < cost){
			return false;
		}
		resources[res] += amount;
		resources[GameWorld.RESOURCE_CURRENCY] -= cost;
		return true;
	}

	protected void emergencyBuy(GameData gd, int res, int amount){
		int max = 0;
		int best = -1;
		for (int i=0; i<resources.length; i++){
			if (resources[i] > max){
				best = i;
				max = resources[i];
			}
		}
		if (best > -1){
			if (amount > resources[best]){
				amount = resources[best];
			}
			sell(best, amount, gd.getTrader().getResource(best));
			buy(res, amount, gd.getTrader().getResource(res));
		}
	}

	protected boolean hasBuilding(int t){
		for (int j=0; j<getHouseCount(); j++){
			House h = getHouse(j);
			if (h.getType() == t){
				return true;
			}
		}
		return false;
	}

	protected boolean hasResourceBuilder(UnitTypeLoader ul, int r){
		for (int i=0; i<getSiteCount(); i++){
			Site s = getSite(i);
			for (int j=0; j<s.getCount(); j++){
				Unit u = s.get(j);
				UnitType ut = ul.getUnitType(u.getType());
				if (ut != null){
					for (int p=0; p<ut.getProductions(); p++){
						if (ut.getProdType(p) == r){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	//to-do
	public void automate(GameData gd, UnitTypeLoader ul,	HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, TechLoader tel){

	try{
		//1.reorganize work force
		//1.1. if food store low, put more people on the field
		if (resources[GameWorld.RESOURCE_FOOD] < GameWorld.RESOURCE_CRITICAL){
			int moved = 0;
			for (int i=0; i<getSiteCount() && moved < GameWorld.RESOURCE_FACTOR; i++){
				Site s = getSite(i);
				int f = s.getCount();
				int p = s.getProduction()[GameWorld.RESOURCE_FOOD];
				// is it a good food production area?
				if (p >  f){
					boolean added = false;
					// get a person from an important house
					for (int j=0; j<getHouseCount() && !added; j++){
						House h = getHouse(j);
						//there is a free person?
						if (h.getCount() > 1){
							//transfer the FIRST PERSON to site
							if (s.transfer(h, 0, ul, hl, tl, ol, tel)){
								added = true;
							}
						}
					}
					//successfull
					if (added){
						moved++;
					}
				}
			}
		}else{
			//2.check building order
			// randomize
			if (border <= -1){
				if (hasBuilding(hl.getDefTrainer())){
					border = (int)(Randomizer.getNextRandom() * hl.getSize());
				}else{
					border = hl.getDefTrainer();
				}
			}
			//check resources
			HouseType htp = hl.getHouse(border);
			if (htp != null){
				int[] cost = htp.getCost();
				for (int i=0; i<cost.length && i<resources.length; i++){
					if (resources[i] < cost[i] && owner != GameWorld.PLAYER_OWNER){
						emergencyBuy(gd, i, cost[i] - resources[i]);
					}
				}
			}
			//find someone to build this house
			int moved = 0;
			for (int j=0; j<getHouseCount() && moved < GameWorld.RESOURCE_FACTOR; j++){
				House h = getHouse(j);
				HouseType ht = hl.getHouse(h.getType());
				if (ht.getProduction() == GameWorld.RESOURCE_HAMMER && h.getCount() < 1){
					for (int i=0; i<getSiteCount() && moved < GameWorld.RESOURCE_FACTOR; i++){
						Site s = getSite(i);
						//there is a free person
						for (int f=0; f<s.getCount() && moved < GameWorld.RESOURCE_FACTOR; f++){
							Unit u = s.get(f);
							UnitType ut = ul.getUnitType(u.getType());
							for (int p=0; p<ut.getProductions(); p++){
								if (ut.getProdType(p) == GameWorld.RESOURCE_HAMMER){
									float chance = 1.0f;
									if (u.getType() != ul.getDefWorkerUnit()){
										 chance = (float)Randomizer.getNextRandom();
									}
									if (chance > 0.8 && h.transfer(s, f, ul, hl, tl, ol, tel)){
										moved++;
									}
								}
							}
						}
					}
				}
			}
			//3.check training order
			// randomize
			if (torder <= -1){
				if (!hasResourceBuilder(ul, GameWorld.RESOURCE_WOOD)){
					torder = ul.getBuildableResourceWorker(GameWorld.RESOURCE_WOOD);
				}else if (!hasResourceBuilder(ul, GameWorld.RESOURCE_STONE)){
					torder = ul.getBuildableResourceWorker(GameWorld.RESOURCE_STONE);
					//System.out.println("stone miner " + torder);
				}else if (!hasResourceBuilder(ul, GameWorld.RESOURCE_METAL)){
					torder = ul.getBuildableResourceWorker(GameWorld.RESOURCE_METAL);
					//System.out.println("miner " + torder);
				}else{
					torder = (int)(Randomizer.getNextRandom() * ul.getCustomSize(owner));
				}
			}
			//check resources
			UnitType ut = ul.getCustom(owner, torder);
			if (ut != null){
				int[] cost = ut.getCost();
				for (int i=0; i<cost.length && i<resources.length; i++){
					if (resources[i] < cost[i]){
						emergencyBuy(gd, i, cost[i] - resources[i]);
					}
				}
			}
			//find someone to train in this house
			moved = 0;
			for (int j=0; j<getHouseCount() && moved < 1; j++){
				House h = getHouse(j);
				HouseType ht = hl.getHouse(h.getType());
				if (ht.getProduction() == GameWorld.RESOURCE_BOOK && h.getCount() < 1){
					for (int i=0; i<getSiteCount() && moved < 1; i++){
						Site s = getSite(i);
						//there is a free person
						for (int f=0; f<s.getCount() && moved < 1; f++){
							Unit u = s.get(f);
							//to-do build randomly
							if (u.getType() != torder){
								float chance = 1.0f;
								if (u.getType() != ul.getDefWorkerUnit()){
									 chance = (float)Randomizer.getNextRandom();
								}
								if (chance > 0.8 && h.transfer(s, f, ul, hl, tl, ol, tel)){
									moved++;
								}
							}
						}
					}
				}
			}
		}
		//4. research randomly
		if (rRate <= 0){
			rType = (int)(Randomizer.getNextRandom() * GameWorld.TECH_SIZE);
		}
		//to-do research rate
		rRate = resources[GameWorld.RESOURCE_CURRENCY] / 2;
	}catch(Exception e){
		e.printStackTrace();
	}
	}

	public boolean getSelection(){
		return selected;
	}

	public void updateSiteProduction(GameData gd, TerrainLoader tloader, OverlayLoader oloader, UnitTypeLoader uloader, TechLoader teloader){
		for (int i=0; i<sites.size(); i++){
			Site s = (Site)sites.get(i);
			s.updateTopLand(gd);
			s.recalProduction(tloader, oloader, uloader, teloader);
		}
	}

	public boolean addHouse(BaseTypeLoader bloader, int t){
		BaseType bt = bloader.getBaseType(type);
		if (bt != null && houses.size() < bt.getLimit()){
			//System.out.println("add house " + t);
			House h = new House(t);
			houses.add(h);
			return true;
		}
		return false;
	}

	public boolean removeHouse(House h){
		//cant remove the default house
		if (houses.size() < 2){
			return false;
		}
		//find the house
		int index = houses.indexOf(h);
		if (index < 0){
			return false;
		}else{
			houses.remove(index);
			return true;
		}
	}

	public boolean transfer(Army other, TerrainLoader tloader, OverlayLoader oloader,
			HouseTypeLoader hloader, UnitTypeLoader uloader, TechLoader teloader, int i){
		//System.err.println("transfer " + i);
		//cant transfer if this is the only unit
		//if (other.getCount() <= 1){
		if (other.getCount() <= 0){
			return false;
		}
		//MH106
		if (!other.canRemove(uloader, i)) {
			return false;
		}
		//MH106
		//go ahead
		Unit u = other.get(i);
		boolean result = addUnit(u, tloader, oloader, hloader, uloader, teloader);
		if (result){
			other.remove(i, uloader);
		}
		//System.err.println("end transfer " + i);
		return result;
	}

	public boolean addUnit(Unit u, TerrainLoader tloader, OverlayLoader oloader,
			HouseTypeLoader hloader, UnitTypeLoader uloader, TechLoader teloader){
		boolean added = false;
		for (int i=0; i<sites.size(); i++){
			Site s = (Site)sites.get(i);
			if (s.add(u)){
				s.recalProduction(tloader, oloader, uloader, teloader);
				added = true;
				break;
			}
		}
		if (!added){
			for (int i=0; i<houses.size(); i++){
				House h = (House)houses.get(i);
				if (h.add(u)){
					h.recalProduction(hloader, uloader, teloader);
					added = true;
					break;
				}
			}
		}

		return added;
	}

	public int getResourceCount(){
		return resources.length;
	}

	public int getResource(int r){
		return resources[r];
	}

	public int transfer(ResourceHolder other, int r, int amount){
		if (resources[r] >= GameWorld.RESOURCE_LIMIT[r]){
			return 0;
		}else if (amount + resources[r] > GameWorld.RESOURCE_LIMIT[r]){
			//cant store more than the limit
			amount = GameWorld.RESOURCE_LIMIT[r] - resources[r];
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
		if (resources[r] > GameWorld.RESOURCE_LIMIT[r]){
			resources[r] = GameWorld.RESOURCE_LIMIT[r];
		}
		if (resources[r]<0) {
			resources[r] = 0;
		}
	}

	public int getOwner(){
		return owner;
	}

	public Color getOwnerColor(){
		return GameWorld.OWNER_COLOR[owner];
	}

	public void changeOwner(int o){
		owner = o;
	}

	public int getType(){
		return type;
	}

	public void setType(int t){
		type = t;
	}

	public void setOwner(int o){
		owner = o;
	}

	public Site getSite(int s){
		if (s >=0 && s < sites.size()){
			return (Site)sites.get(s);
		}else{
			return null;
		}
	}

	public int getSiteCount(){
		return sites.size();
	}

	public House getHouse(int h){
		if (h >=0 && h < houses.size()){
			return (House)houses.get(h);
		}else{
			return null;
		}
	}

	public int getHouseCount(){
		return houses.size();
	}

	// percent
	public int getHappyBonus(){
		return (int)(resources[GameWorld.RESOURCE_HAPPY] * 100 / GameWorld.RESOURCE_LIMIT[GameWorld.RESOURCE_HAPPY]);
	}

	// percent
	public int getSecurityBonus(){
		return (int)(resources[GameWorld.RESOURCE_SECURITY] * 100 / GameWorld.RESOURCE_LIMIT[GameWorld.RESOURCE_SECURITY]);
	}

	public int getPopulation(){
		int pop = 0;
		for (int i=0; i<sites.size(); i++) {
			Site st = getSite(i);
			pop += st.getCount();
		}
		for (int i=0; i<houses.size(); i++) {
			House hs = getHouse(i);
			pop += hs.getCount();
		}

		return pop;
	}

	public void collectResource(){
		//Bonus from happiness
		float happyBonus = (float)(1 + getHappyBonus() / 100);

		for (int s=0; s<sites.size(); s++){
			int[] p = ((Site)sites.get(s)).getProduction();
			if (p.length >=0 && p.length <= GameWorld.RESOURCE_SIZE){
				for (int i=0; i<p.length; i++){
					resources[i] += (int)(p[i] * happyBonus);
				}
			}
		}
		for (int h=0; h<houses.size(); h++){
			int[] p = ((House)houses.get(h)).getProduction();
			if (p.length >=0 && p.length <= GameWorld.RESOURCE_SIZE){
				for (int i=0; i<p.length; i++){
					resources[i] += (int)(p[i] * happyBonus);
					//System.out.println(i+":"+resources[i]);
				}
			}
		}
	}

	public boolean grantConsumption(UnitTypeLoader ul){
		boolean result = true;
		//MH142 if happiness less than minimum and we have luxuries, convert some of them
		int rLux = resources[GameWorld.RESOURCE_LUXURIES];
		int rHap = resources[GameWorld.RESOURCE_HAPPY];
		if (rHap < GameWorld.RESOURCE_CRITICAL && rLux > 0) {
			//Use which ever smaller
			if (rHap > rLux) {
				resources[GameWorld.RESOURCE_LUXURIES] -= rLux;
				resources[GameWorld.RESOURCE_HAPPY] += rLux;
			}else{
				resources[GameWorld.RESOURCE_LUXURIES] -= rHap;
				resources[GameWorld.RESOURCE_HAPPY] += rHap;
			}
		}
		//End of MH142
		//sites
		for (int s=0; s<sites.size(); s++){
			Site st = (Site)sites.get(s);
			int[] c = st.getConsumption();
			if (c.length >=0 && c.length <= GameWorld.RESOURCE_SIZE){
				for (int i=0; i<c.length; i++){
					resources[i] -= c[i];
					if (resources[i] < 0){
						resources[i] = 0;
						result = false;
					}
				}
			}
			//Heal units
			for (int i=0 ; i<st.getCount(); i++) {
				Unit un = st.get(i);
				if (un != null) {
					un.heal();
				}
			}
		}
		//houses
		for (int h=0; h<houses.size(); h++){
			House hs = (House)houses.get(h);
			int[] c = hs.getConsumption();
			if (c.length >=0 && c.length <= GameWorld.RESOURCE_SIZE){
				for (int i=0; i<c.length; i++){
					resources[i] -= c[i];
					if (resources[i] < 0){
						resources[i] = 0;
						result = false;
					}
				}
			}
			//Heal units
			for (int i=0 ; i<hs.getCount(); i++) {
				Unit un = hs.get(i);
				if (un != null) {
					un.heal();
				}
			}
		}

		return result;
	}

	public void gainExperience(UnitTypeLoader ul){
		//sites
		for (int s=0; s<sites.size(); s++){
			Site st = (Site)sites.get(s);
			for (int i=0 ; i<st.getCount(); i++) {
				Unit un = st.get(i);
				if (un != null) {
					un.reward();
				}
			}
		}
		//houses
		for (int h=0; h<houses.size(); h++){
			House hs = (House)houses.get(h);
			for (int i=0 ; i<hs.getCount(); i++) {
				Unit un = hs.get(i);
				if (un != null) {
					un.reward();
				}
			}
		}
	}

	public boolean randomDestruction(){
		if (houses.size()>1) {
			int destroy = (int)(Randomizer.getNextRandom()*(houses.size()-1)+1);
			return removeHouse(getHouse(destroy));
		}else{
			return false;
		}
	}

	public void randomEvent(){
		//0 Taxing which is a pre-text to some random event
		if (getFlag(MASK_TAX) == MASK_TAX) {
			int gain = getPopulation();
			int loss = gain;
			//gain golds
			int origQuant = resources[GameWorld.RESOURCE_CURRENCY];
			setResourceDebug(GameWorld.RESOURCE_CURRENCY, origQuant + gain);
			//loose happiness
			reduce(GameWorld.RESOURCE_HAPPY, loss);
		}
		int happyBonus = getHappyBonus();
		int securityBonus = getSecurityBonus();
		//1 Theft
		int theftChance = (int)(Randomizer.getNextRandom() * 100);
		int catchChance = (int)(Randomizer.getNextRandom() * securityBonus + GameWorld.THEIVING_DIFFICULTY);
		if (theftChance > catchChance) {
			int itemStolen = (int)(Randomizer.getNextRandom() * GameWorld.RESOURCE_HIDDEN);
			int quantStolen = (int)(Randomizer.getNextRandom() * getResource(itemStolen));

			if (quantStolen > 0) {
				reduce(itemStolen, quantStolen);
				if (getOwner() == GameWorld.PLAYER_OWNER) {
					GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, thieves have broken into our storage and stole " + Integer.toString(quantStolen) + " " + GameWorld.RESOURCE_NAME[itemStolen] + "!!!");
				}
			}

		}
		//2 Revolt
		int revoltChance = (int)(Randomizer.getNextRandom() * (100 - happyBonus));
		int guardChance = (int)(Randomizer.getNextRandom() * securityBonus + GameWorld.REVOLT_DIFFICULTY);
		if (revoltChance > guardChance) {
			if (randomDestruction()) {
				if (getOwner() == GameWorld.PLAYER_OWNER) {
					//Game
					GameWorld.printMessage("<%" + GameWorld.IMG_STOP + "%>Mylord, " + getName() + "'s people has revolted against you and destroy a building!!!");
				}
			}
		}
		//3 Migration
	}

	public boolean newTurn	(
			GameData gd,
			TerrainLoader tloader,
			OverlayLoader oloader,
			UnitTypeLoader uloader,
			HouseTypeLoader hloader,
			BaseTypeLoader bloader,
			TechLoader teloader){

		boolean result = true;
		boolean something_done = false;
		//1.add production from site
		//2.add production from house
		collectResource();
		//3.reduce units consumptions
		if (!grantConsumption(uloader)){
			if (owner == GameWorld.PLAYER_OWNER){
				GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, there is not enough resources for all people in " + getName());
			}
			result = false;
		}
		//4.check for building completion
		if (border > -1){
			HouseType ht = hloader.getHouse(border);
			if (ht != null){
				int[] cost = ht.getCost();
				boolean ok = true;
				for (int i=0; i<cost.length && i<resources.length; i++){
					if (resources[i] < cost[i]){
						ok = false;
						break;
					}
				}
				if (ok){
					//taken resource
					for (int i=0; i<cost.length && i<resources.length; i++){
						resources[i] -= cost[i];
					}
					//build the house
					if (addHouse(bloader, border)){
						setFlag(ht.getMask());
					}
					//restart
					border = -1;
					if (owner == GameWorld.PLAYER_OWNER){
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, building in our city has completed");
					}

					something_done = true;
				}else{
					//GameWorld.printMessage("My lord, there is not enough resources to build");
				}
			}
		}
		//5.check for training completion
		if (torder > -1){
			UnitType ut = uloader.getCustom(owner, torder);
			int order = uloader.getIndex(ut);
			if (ut != null){
				boolean ok = true;
				int[] cost = ut.getCost();
				for (int i=0; i<cost.length && i<resources.length; i++){
					if (resources[i] < cost[i]){
						ok = false;
						break;
					}
				}
				if (ok){
					//taken resource
					for (int i=0; i<cost.length && i<resources.length; i++){
						resources[i] -= cost[i];
					}
					//train the unit
					int utrait = ut.getRequireTrait();
					boolean trained = false;
					for (int i=0; i<houses.size() && !trained; i++){
						House h = (House)houses.get(i);
						int[] p = h.getProduction();
						//is it a training house?
						if (p[GameWorld.RESOURCE_BOOK] > 0){
							for (int j=0; j<h.getCount() && !trained; j++){
								Unit u = h.get(j);
								//only different type can be trained
								//and unit must have the require trait
								if (u.getType() != order && (utrait == -1 || utrait == u.getTrait())){
									u.setType(order);
									//MIN27
									u.setUpgrade(ut.getDefTech());
									//quit
									trained = true;
									something_done = true;
								}
								//Move it to good use
								boolean moved = false;
								for (int l=0; l<getSiteCount() && !moved; l++){
									Site s = getSite(l);
									if (s.transfer(h, j, uloader, hloader, tloader, oloader, teloader)){
										moved = true;
									}
								}
							}
						}
					}
					if (trained){
						//restart
						torder = -1;
						if (owner == GameWorld.PLAYER_OWNER){
							GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, training in our city has completed");
						}
					}
				}else{
					//GameWorld.printMessage("My lord, there is not enough resources to train");
				}
			}
		}
		//5.2 gain experience for towner if something has been done
		if (something_done) {
			gainExperience(uloader);
		}
		//6.grow
		int growing_limit = GameWorld.getResourceGrow(getPopulation());
		if (resources[GameWorld.RESOURCE_FOOD] > growing_limit){
			//MIN27
			int t = uloader.getDefWorkerUnit();
			UnitType ut = uloader.getUnitType(t);
			//MIN36
			Unit u = new Unit(ut, t);
			//u.setUpgrade(ut.getDefTech());
			//MIN27

			if (!addUnit(u, tloader, oloader, hloader, uloader, teloader)){
				if (owner == GameWorld.PLAYER_OWNER){
					GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, this city has reach its population limit");
				}
			}else{
				resources[GameWorld.RESOURCE_FOOD] -= growing_limit;
			}
		}
		//7.post resources to player treasury if any applied
		//7.1 post research point
		if (rRate > 0){
			int rate = rRate;
			if (resources[GameWorld.RESOURCE_CURRENCY] < rate){
				rate = resources[GameWorld.RESOURCE_CURRENCY];
			}
			rAmount += rate;
			if (rAmount > 0){
				rAmount -= gd.addResearch(owner, rType, rAmount);
				reduce(GameWorld.RESOURCE_CURRENCY, rate);
			}
		}
		//8.random events
		randomEvent();
		//9.truncate resource
		for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
			if (resources[i] > GameWorld.RESOURCE_LIMIT[i]){
				resources[i] = GameWorld.RESOURCE_LIMIT[i];
			}else if (resources[i] < 0) {
				resources[i] = 0;
			}
		}
		return result;
	}
}