// ******************************************
// ARMY CAN BE NULL, MEANING A DEAD ARMY
// ******************************************
//Experimental

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class GameData implements Serializable{
	public static final int DIP_WAR = 0;
	public static final int DIP_REC_WAR = 1;
	public static final int DIP_UNKNOWN = 2;
	public static final int DIP_REC_PEACE = 3;
	public static final int DIP_PEACE = 4;
	public static final int DIP_REC_ALLY = 5;
	public static final int DIP_ALLY = 6;

	public static final String DIP_NAME[] = {"war", "all out war", "unknown",
											"peace?", "peace", "ally?", "ally"};
	public final static Color [] DIP_COLOR = {
		Color.red, Color.red, Color.black,
		Color.black, Color.blue, Color.blue, Color.blue};

	public static final int DIFF_EASY = 0;
	public static final int DIFF_NORMAL = 1;
	public static final int DIFF_HARD = 2;

	public static final int ATT_DISGUST = 0;
	public static final int ATT_HATE = 1;
	public static final int ATT_NORMAL = 2;
	public static final int ATT_LIKE = 3;
	public static final int ATT_FAVOR = 4;

	public static final String ATT_NAME[] = {"Disgust", "Hate", "Normal",
											"Like", "Favor"};

	public static final int MAX_TOP_TILES = 3;

	public static final int MASK_VISIBLE = 0;
	public static final int MASK_CLOUD = 1;
	public static final int MASK_INVISIBLE = 2;
	// fogs of war
	//     xxx
	//    xxxxx
	//   xxxoxxx
	//    xxxxx
	//     xxx

	public static final int[] MASK_X = {0,-1,-1,0,1,1,1,0,-1,-2,2,-2,-2,-1,0,1,2,2,1,0,-1,-3,3};
	public static final int[] MASK_Y = {0,0,-1,-1,-1,0,1,1,1,0,0,1,-1,-2,-2,-2,-1,1,2,2,2,0,0};
	public static final int MASK_LENGTH = MASK_X.length;
	public static final int MASK_CLEAR_LENGTH = 11;
	public static final int MASK_R_X = 4;
	public static final int MASK_R_Y = 3;
	public static final int MASK_RANGE_X = MASK_R_X * 2;
	public static final int MASK_RANGE_Y = MASK_R_Y * 2;

	//22222
	//21112
	//21X12
	//21112
	//22222
	//protected static final int ROTATE_X[] = {-1,0,1,0,1,0,0,-1};
	//protected static final int ROTATE_Y[] = {0,-1,0,0,1,1,0,0};
	protected static final int ROTATE_X[] = {-1,0,1,1,0,0,-1,-1};
	protected static final int ROTATE_Y[] = {0,-1,0,0,1,1,0,0};
	protected static final int MAX_BLOCK = 3;
	protected static final int MAX_CELLS = MAX_BLOCK*MAX_BLOCK-1;

	protected int[][] baseTiles;
	protected int[][] baseVar;
	//MH130
	protected int[][][] topTiles;
	protected int[][][] topVar;
	protected int[][] topCount;
	//MH130
	protected int[][] mask;

	protected int[][] armyHolder;
	protected int[][] baseHolder;

	protected int[][] diplomacy;
	protected int[][] attitude;
	protected int[][] techs;
	protected ArrayList[] jobs;
	protected ArrayList[] advances;

	//protected TerrainLoader tloader;
	//protected OverlayLoader oloader;
	//protected UnitTypeLoader uloader;

	protected int width;
	protected int height;
	protected int turn;
	protected int difficulty;
	protected ArrayList armies;
	protected ArrayList bases;
	protected GameTrader trader;
	protected boolean masking;

	protected ArrayList messages;

	private transient ArrayList lands;

	public GameData(int w, int h){//, TerrainLoader tl, OverlayLoader ol, UnitTypeLoader ul){

		//tloader = tl;
		//oloader = ol;
		//uloader = ul;

		width = w;
		height = h;
		turn = 0;
		difficulty = DIFF_EASY;

		//instantiate
		baseTiles = new int[width][height];
		baseVar = new int[width][height];
		//MH130
		topTiles = new int[width][height][MAX_TOP_TILES];
		topVar = new int[width][height][MAX_TOP_TILES];
		topCount = new int[width][height];
		//MH130
		armyHolder = new int[width][height];
		baseHolder = new int[width][height];
		mask = new int[width][height];
		//turn on/off fog of war
		masking = true;
		//diplomacy
		diplomacy = new int[GameWorld.OWNER_SIZE][GameWorld.OWNER_SIZE];
		attitude = new int[GameWorld.OWNER_SIZE][GameWorld.OWNER_SIZE];
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			for (int j=0; j<GameWorld.OWNER_SIZE; j++){
				diplomacy[i][j] = DIP_UNKNOWN;
				attitude[i][j] = ATT_NORMAL;
			}
		}
		//technology
		techs = new int[GameWorld.OWNER_SIZE][GameWorld.TECH_SIZE];
		advances = new ArrayList[GameWorld.OWNER_SIZE];
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			advances[i] = new ArrayList();
			for (int j=0; j<GameWorld.TECH_SIZE; j++){
				techs[i][j] = 0;
			}
		}
		jobs = new ArrayList[GameWorld.OWNER_SIZE];
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			jobs[i] = new ArrayList();
		}
		//clear them
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				baseTiles[i][j] = -1;
				baseVar[i][j] = -1;
				topCount[i][j] = 0;
				for (int v=0; v<MAX_TOP_TILES; v++) {
					topTiles[i][j][v] = -1;
					topVar[i][j][v] = -1;
				}
				armyHolder[i][j] = -1;
				baseHolder[i][j] = -1;
				mask[i][j] = MASK_INVISIBLE;
			}
		}

		lands = new ArrayList();
		armies = new ArrayList();
		bases = new ArrayList();

		messages = new ArrayList();

		trader = new GameTrader();
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		lands = new ArrayList();
	}

	public void logMessage(String msg){
		messages.add(msg);
	}

	public int getMessageCount(){
		return messages.size();
	}

	public String getMessage(int i){
		if (i<0 || i>=messages.size()) {
			return "";
		}
		String tmp = (String)(messages.get(i));
		return tmp;
	}
	public int getTurn(){
		return turn;
	}

	public void setDifficulty(int d){
		difficulty = d;
	}

	public int getDifficulty(){
		return difficulty;
	}

	public void nextTurn(){
		turn++;
		//wrap-up
		if (turn > 999999){
			turn = 0;
		}
	}

	public void setTurn(int t){
		turn = t;
	}

	public void initData(){
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			for (int j=0; j<GameWorld.OWNER_SIZE; j++){
				diplomacy[i][j] = DIP_UNKNOWN;
				attitude[i][j] = ATT_NORMAL;
			}
		}
		//technology
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			advances[i] = new ArrayList();
			for (int j=0; j<GameWorld.TECH_SIZE; j++){
				techs[i][j] = 0;
			}
		}
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			jobs[i] = new ArrayList();
		}
	}

	public int getDiplomacy(int i, int j){
		if (i < 0 || i >= GameWorld.OWNER_SIZE || j < 0 || j >= GameWorld.OWNER_SIZE){
			return DIP_UNKNOWN;
		}
		return diplomacy[i][j];
	}

	public void setDiplomacy(int i, int j, int d){
		if (i < 0 || i >= GameWorld.OWNER_SIZE || j < 0 || j >= GameWorld.OWNER_SIZE){
			return;
		}
		diplomacy[i][j] = d;
	}

	//to-do let the function indexOf take care of this
	public int advanceIndex(int o, int a){
		for (int i=0; i<advances[o].size(); i++){
			if (((Integer)advances[o].get(i)).intValue() == a){
				return i;
			}
		}
		return -1;
	}

	//Add an advancement upgrade for a player
	public boolean addAdvance(int o, int a){
		Integer adv = new Integer(a);
		//ensure uniqueness of values
		if (advanceIndex(o, a) != -1){
			return false;
		}
		advances[o].add(adv);
		return true;
	}

	public int getAdvanceSize(int o){
		if (o < 0 || o >= GameWorld.OWNER_SIZE){
			return 0;
		}
		return advances[o].size();
	}

	public int getAdvance(int o, int i){
		if (o < 0 || o >= GameWorld.OWNER_SIZE || i < 0 || i >= getAdvanceSize(o)){
			return -1;
		}
		//System.err.println(i);
		try{
			return ((Integer)advances[o].get(i)).intValue();
		}catch(Exception e){
			return 0;
		}
	}

	public int getTech(int i, int j){
		if (i < 0 || i >= GameWorld.OWNER_SIZE || j < 0 || j >= GameWorld.TECH_SIZE){
			return 0;
		}
		return techs[i][j];
	}

	public int[] getTech(int i){
		if (i < 0 || i >= GameWorld.OWNER_SIZE){
			return null;
		}
		return techs[i];
	}

	public void setTech(int i, int j, int a){
		if (i < 0 || i >= GameWorld.OWNER_SIZE || j < 0 || j >= GameWorld.TECH_SIZE){
			return;
		}
		if (a > GameWorld.TECH_LIMIT){
			a = GameWorld.TECH_LIMIT;
		}
		techs[i][j] = a;
	}

	//return the remainder
	public int addResearch(int o, int t, int m){
		if (o < 0 || o >= GameWorld.OWNER_SIZE || t < 0 || t >= GameWorld.TECH_SIZE){
			return 0;
		}
		int a = m / GameWorld.TECH_COST[t];
		if (a < 1){
			return 0;
		}else{
			techs[o][t] += a;
			if (techs[o][t] > GameWorld.TECH_LIMIT){
				techs[o][t] = GameWorld.TECH_LIMIT;
			}
			return a * GameWorld.TECH_COST[t];
		}
	}

	public int getAttitude(int i, int j){
		if (i < 0 || i >= GameWorld.OWNER_SIZE || j < 0 || j >= GameWorld.OWNER_SIZE){
			return ATT_NORMAL;
		}
		return attitude[i][j];
	}

	public void setAttitude(int i, int j, int d){
		attitude[i][j] = d;
	}

	public boolean assignJob(Army a, int o, int j){
		if (a == null || j < 0 || getJobSize(o) <= j) {
			return false;
		}
		a.setJob(getJob(o, j));
		removeJob(o, j);
		return true;
	}

	public void addJob(int o, Job j){
		jobs[o].add(j);
	}

	public int getJobSize(int o){
		if (o < 0 || o >= jobs.length){
			return 0;
		}
		return jobs[o].size();
	}

	public GameTrader getTrader(){
		return trader;
	}

	public Job getJob(int o, int i){
		if (o < 0 || o >= jobs.length || i < 0 || i >= jobs[o].size()){
			return null;
		}
		return (Job)jobs[o].get(i);
	}

	public void removeJob(int o, int i){
		if (o < 0 || o >= jobs.length || i < 0 || i >= jobs[o].size()){
			return;
		}
		jobs[o].remove(i);
	}

	public void setMasking(boolean m){
		masking = m;
	}

	public boolean getMasking(){
		return masking;
	}

	public int getMask(int x, int y){
		if (!masking){
			return MASK_VISIBLE;
		}
		return mask[x][y];
	}

	public void initFog(){
		//clear them
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				mask[i][j] = MASK_INVISIBLE;
			}
		}
		//armies
		for (int i=0; i<getArmySize(); i++){
			Army a = getArmy(i);
			if (a != null && a.getOwner() == GameWorld.PLAYER_OWNER){
				clearFog(a.getX(), a.getY());
			}
		}
		//bases
		for (int i=0; i<getBaseSize(); i++){
			Base b = getBase(i);
			if (b.getOwner() == GameWorld.PLAYER_OWNER){
				clearFog(b.getX(), b.getY());
			}
		}
	}

	public void clearFog(int x, int y){
		for (int i=0; i<MASK_LENGTH; i++){
			int cx = x + MASK_X[i];
			int cy = y + MASK_Y[i];

			if (cx < 0 || cx >= width || cy < 0 || cy >= height){
				continue;
			}

			if (i < MASK_CLEAR_LENGTH){
				mask[cx][cy] = MASK_VISIBLE;
			}else{
				if (mask[cx][cy] == MASK_CLOUD){
					mask[cx][cy] = MASK_VISIBLE;
				}else if (mask[cx][cy] == MASK_INVISIBLE){
					mask[cx][cy] = MASK_CLOUD;
				}
			}
		}
	}

	protected void clearFogNonAcc(int x, int y, int x0, int y0, int rx, int ry){
		for (int i=0; i<MASK_LENGTH; i++){
			int cx = x + MASK_X[i];
			int cy = y + MASK_Y[i];

			if (cx < 0 || cx >= width || cy < 0 || cy >= height){
				continue;
			}

			int dx = Math.abs(cx - x0);
			int dy = Math.abs(cy - y0);

			if (dx > rx || dy > ry){
				continue;
			}

			if (i < MASK_CLEAR_LENGTH){
				mask[cx][cy] = MASK_VISIBLE;
			}else{
				//MIN39
				//mask[cx][cy] = MASK_CLOUD;
				if (mask[cx][cy] == MASK_CLOUD){
					mask[cx][cy] = MASK_VISIBLE;
				}else if (mask[cx][cy] == MASK_INVISIBLE){
					mask[cx][cy] = MASK_CLOUD;
				}
			}
		}
	}

	public void cloud(int x, int y){
		for (int i=0; i<MASK_LENGTH; i++){
			int cx = x + MASK_X[i];
			int cy = y + MASK_Y[i];

			if (cx < 0 || cx >= width || cy < 0 || cy >= height){
				continue;
			}

			mask[cx][cy] = MASK_INVISIBLE;
		}
		//restore nearby view
		for (int ox = x - MASK_RANGE_X; ox <= x + MASK_RANGE_X; ox++){
			for (int oy = y - MASK_RANGE_Y; oy <= y + MASK_RANGE_Y; oy++){
				int ind = getArmy(ox, oy);
				if (ind > -1){
					Army a = getArmy(ind);
					if (a.getOwner() == GameWorld.PLAYER_OWNER){
						clearFogNonAcc(ox, oy, x, y, MASK_R_X, MASK_R_Y);
					}
				}
				int ind2 = getBase(ox, oy);
				if (ind2 > -1){
					Base b = getBase(ind2);
					if (b.getOwner() == GameWorld.PLAYER_OWNER){
						//if (ind > -1){
						//	clearFog(ox, oy);
						//}else{
							clearFogNonAcc(ox, oy, x, y, MASK_R_X, MASK_R_Y);
						//}
					}
				}
			}
		}
	}

	public int getArmySize(){
		return armies.size();
	}

	public int getBaseSize(){
		return bases.size();
	}

	//to-do: return boolean
	public void createArmy(int owner, int x, int y, int udefault, UnitTypeLoader uloader){
		//MIN27
		UnitType ut = uloader.getUnitType(udefault);
		//MIN36
		Unit u = new Unit(ut, udefault);
		//u.setUpgrade(ut.getDefTech());
		//MIN27		
		Army a = new Army(owner);
		a.add(u, uloader);
		int dead = armies.indexOf(null);
		if (dead != -1){
			armies.set(dead, a);
			setArmy(dead, x, y);
		}else{
			armies.add(a);
			setArmy(armies.size()-1, x, y);
		}
	}

	//to-do: create empty army
	public int createArmy(int owner, int x, int y){
		if (getArmy(x, y) != -1){
			return -1;
		}

		Army a = new Army(owner);

		int dead = armies.indexOf(null);
		if (dead != -1){
			armies.set(dead, a);
			setArmy(dead, x, y);
			return dead;
		}else{
			armies.add(a);
			setArmy(armies.size()-1, x, y);
			return armies.size()-1;
		}
	}

	public boolean addArmy(Army a, int x, int y){
		if (a == null){
			return false;
		}else{
			int dead = armies.indexOf(null);
			if (dead != -1){
				armies.set(dead, a);
				setArmy(dead, x, y);
			}else{
				armies.add(a);
				setArmy(armies.size()-1, x, y);
			}
			return true;
		}
	}

	public boolean createBase(int owner, int type, int x, int y, int udefault,
			TerrainLoader tloader, OverlayLoader oloader, HouseTypeLoader hloader,
			UnitTypeLoader uloader, BaseTypeLoader bloader, TechLoader teloader){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return false;
		}

		if (udefault == -1){
			//MIN07 - allowing empty city
			// return false;
			//End MIN07
			//add default house
			Base b = new Base(this, owner, type, x, y);
			if (b.addHouse(bloader, hloader.getDefBuilder())){
				//setbase position on map
				setBase(bases.size(), x, y);
				//require loader to cal productions
				bases.add(b);
				return true;
			}
		}else{
			//MIN27
			UnitType ut = uloader.getUnitType(udefault);
			//MIN36
			Unit u = new Unit(ut, udefault);
			//u.setUpgrade(ut.getDefTech());
			//MIN27
			Base b = new Base(this, owner, type, x, y);
			if (b.addUnit(u, tloader, oloader, hloader, uloader, teloader)){
				//add default house
				BaseType bt = bloader.getBaseType(type);
				if (bt.getLimit() <= 0 || b.addHouse(bloader, hloader.getDefBuilder())){
					//setbase position on map
					setBase(bases.size(), x, y);
					//require loader to cal productions
					bases.add(b);
					return true;
				}
			}
		}
		//destroy
		return false;
	}

	public boolean addDebugUnit(int a, int t, UnitTypeLoader uloader){
		if (a >= 0 && a < armies.size()){
			Army army = (Army)armies.get(a);

			if (army == null) {
				return false;
			}

			//MIN27
			UnitType ut = uloader.getUnitType(t);
			if (ut == null) {
				return false;
			}
			//MIN36
			Unit u = new Unit(ut, t);
			//u.setUpgrade(ut.getDefTech());
			//MIN27

			return army.add(u, uloader);
		}
		return false;
	}

	public boolean addDebugUnit(int b, int t, TerrainLoader tloader, 
			OverlayLoader oloader, HouseTypeLoader hloader, UnitTypeLoader uloader, TechLoader teloader){
		if (b >= 0 && b < bases.size()){
			Base base = (Base)bases.get(b);
			//MIN27
			UnitType ut = uloader.getUnitType(t);
			//MIN36
			Unit u = new Unit(ut, t);
			//u.setUpgrade(ut.getDefTech());
			//MIN27
			return base.addUnit(u, tloader, oloader, hloader, uloader, teloader);
		}
		return false;
	}

	public int armyIndex(Army a){
		return armies.indexOf(a);
	}

	public int baseIndex(Base b){
		return bases.indexOf(b);
	}

	public void removeDebugArmy(int a){
		if (a >= 0 && a < armies.size()){
			//remember index is in used
			armies.set(a, null);
		}
	}
	public void removeArmy(int a){
		if (a >= 0 && a < armies.size()){
			Army army = (Army)armies.get(a);
			Point p = army.getPosition();
			//to-do:null
			clearArmy(p.x, p.y);
			//cloud
			if (army.getOwner() == GameWorld.PLAYER_OWNER){
				cloud(p.x, p.y);
			}
			//remember index is in used
			armies.set(a, null);
		}
	}

	public Army getArmy(int i){
		if (i >= 0 && i < armies.size()){
			return (Army)armies.get(i);
		}else{
			return null;
		}
	}

	public void setArmy(int a, int x, int y){
		Army army = getArmy(a);
		if (army != null){
			//temporarily clear
			setArmyTile(-1, army.getX(), army.getY());
			if (army.getOwner() == GameWorld.PLAYER_OWNER){
				cloud(army.getX(), army.getY());
			}
			//set position
			army.setPosition(x, y, getBaseLand(x, y));
			setArmyTile(a, x, y);
			//restore new view
			if (army.getOwner() == GameWorld.PLAYER_OWNER){
				clearFog(x, y);
			}
		}
	}

	public void setArmyDynamic(int a, int x, int y, UnitTypeLoader ul){
		Army army = getArmy(a);
		if (army != null){
			//temporarily clear
			setArmyTile(-1, army.getX(), army.getY());
			if (army.getOwner() == GameWorld.PLAYER_OWNER){
				cloud(army.getX(), army.getY());
			}
			army.setPositionDynamic(x, y, getBaseLand(x, y), ul);
			setArmyTile(a, x, y);
			//restore new view
			if (army.getOwner() == GameWorld.PLAYER_OWNER){
				clearFog(x, y);
			}
		}
	}

	public void clearArmy(Army a){
		Point p = a.getPosition();
		setArmyTile(-1, p.x, p.y);
	}

	public void clearArmy(int x, int y){
		setArmyTile(-1, x, y);
	}

	public Base getBase(int i){
		if (i >= 0 && i < bases.size()){
			return (Base)bases.get(i);
		}else{
			return null;
		}
	}

	public int getMapWidth(){
		return width;
	}

	public int getMapHeight(){
		return height;
	}

	public void setArmyTile(int a, int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return;
		}
		armyHolder[x][y] = a;
	}

	public void setBase(int t, int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return;
		}
		baseHolder[x][y] = t;
	}

	public int getBase(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}

		return baseHolder[x][y];
	}

	public int getMoveCost(int x, int y, TerrainLoader tloader, OverlayLoader oloader){
		int mc = 1;
		if (x < 0 || x >= width || y < 0 || y >= height){
			return mc;
		}

		Terrain tb = tloader.getTerrain(baseTiles[x][y]);
		mc = tb.getMove();
		for (int v=0; v<topCount[x][y] && v<MAX_TOP_TILES; v++) {
			Overlay to = oloader.getOverlay(topTiles[x][y][v]);
			if (to != null) {
				mc += to.getMove();
			}
		}

		return mc;
	}

	public int getArmy(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}

		return armyHolder[x][y];
	}

	public int getBaseLand(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}
		return baseTiles[x][y];
	}

	public int getBaseVar(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}

		return baseVar[x][y];
	}

	public boolean removeTopLand(int x, int y, int i, TerrainLoader tloader, OverlayLoader oloader, UnitTypeLoader uloader, TechLoader teloader){
		if (x < 0 || x >= width || y < 0 || y >= height || i < 0 || i >= topCount[x][y]){
			return false;
		}
		//Push up
		for (int j=i+1 ; j<topCount[x][y]; j++) {
			topTiles[x][y][j-1] = topTiles[x][y][j];
			topVar[x][y][j-1] = topVar[x][y][j];
		}
		topCount[x][y]--;
		//Update production on cities around the area
		for (int dx=-1; dx<2; dx++) {
			for (int dy=-1; dy<2; dy++) {
				int b = getBase(x + dx, y + dy);
				if (b > -1) {
					Base base = getBase(b);
					if (base != null) {
						base.updateSiteProduction(this, tloader, oloader, uloader, teloader);
					}
				}
			}
		}
		return true;
	}

	public int getTopCount(int x, int y){
		if (x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}

		return topCount[x][y];
	}

	public int getTopLand(int x, int y, int i){
		if (x < 0 || x >= width || y < 0 || y >= height || i < 0 || i >= topCount[x][y]){
			return -1;
		}

		return topTiles[x][y][i];
	}
	
	public int getTopVar(int x, int y, int i){
		if (x < 0 || x >= width || y < 0 || y >= height || i < 0 || i >= topCount[x][y]){
			return -1;
		}

		return topVar[x][y][i];
	}

	public void addTopLand(int x, int y, int n, OverlayLoader ol){
		if (x < 0 || x >= width || y < 0 || y >= height || topCount[x][y] >= MAX_TOP_TILES){
			return;
		}
		Overlay newOl = ol.getOverlay(n);
		if (newOl == null) {
			return;
		}
		for (int v=0; v<topCount[x][y]; v++) {
			Overlay ov = ol.getOverlay(topTiles[x][y][v]);
			if (ov != null) {
				if (ov.getOrder() == newOl.getOrder()) {
					return;
				}
			}
		}

		topTiles[x][y][topCount[x][y]] = n;
		topVar[x][y][topCount[x][y]] = (int)(newOl.getVars() * Randomizer.getNextRandom());
		topCount[x][y]++;
		//if (topCount[x][y]>1) {
		//	System.err.println(x + "/" + y + ":" + topCount[x][y]);
		//}
	}

	public void setTopLand(int x, int y, int n, int i){
		if (x < 0 || x >= width || y < 0 || y >= height || i < 0 || i >= topCount[x][y]){
			return;
		}

		topTiles[x][y][i] = n;
	}

	public ArrayList getLands(){
		return lands;
	}

	public void randomize(double chance, TerrainLoader tloader, OverlayLoader oloader){
		lands.clear();
		armies.clear();
		bases.clear();

		//MIN29 implementing continents
		int numContinents = (int)(chance * 10) + 1;
		int driftDistance = (int)(Math.sqrt(width * width + height * height) / numContinents);
		ArrayList continents = new ArrayList();
		for (int i=0; i < numContinents; i++){
			int x = (int)(Randomizer.getNextRandom() * (width-MAX_BLOCK*2)) + MAX_BLOCK;
			int y = (int)(Randomizer.getNextRandom() * (height-MAX_BLOCK*2)) + MAX_BLOCK;

			continents.add(new Point(x, y));
		}

		int bmax = tloader.getSize();
		int tmax = oloader.getSize();

		if (bmax > 0){
			//set to smallest tile
			for (int i=0; i<width; i++){
				for (int j=0; j<height; j++){
					baseTiles[i][j] = 0;
					//no variations
					baseVar[i][j] = 0;
					armyHolder[i][j] = -1;
					baseHolder[i][j] = -1;
					for (int v=0; v<MAX_TOP_TILES; v++) {
						topTiles[i][j][v] = -1;
						topVar[i][j][v] = -1;
					}
				}
			}
			//use seed to create world
			long worldSize = width * height;
			double prob = chance;

			for (int l = 1; l < bmax; l++){
				long mass = (long)(worldSize * prob) + 1;
				//System.err.println(mass);
				int vars = tloader.getTerrain(l).getVars();
				//MIN20
				while (mass > 0){
					int cells = (int)((MAX_CELLS) * Randomizer.getNextRandom())+1;
					//double attractor = Randomizer.getNextRandom() / 10 + 0.45;
					//System.err.println(cells);
					int x = (int)(Randomizer.getNextRandom() * (width-MAX_BLOCK*2)) +
						MAX_BLOCK;
					int y = (int)(Randomizer.getNextRandom() * (height-MAX_BLOCK*2)) +
						MAX_BLOCK;

					//MIN29 find closest continent
					int bestDistance = driftDistance;
					Point closest = null;
					for (int i=0; i<continents.size(); i++){
						Point p = (Point)continents.get(i);
						int dx = p.x - x;
						int dy = p.y - y;
						int distance = (int)Math.sqrt(dx * dx + dy * dy);
						if (distance < bestDistance){
							closest = p;
							bestDistance = distance;
						}
					}
					//Drift the land closer to the continent
					if (closest != null){
						//x = (int)((x + closest.x) * attractor);
						//y = (int)((y + closest.y) * attractor);
						x = (x + closest.x) / 2;
						y = (y + closest.y) / 2;
					}
					//End MIN29

					if (l == 1){
						lands.add(new Point(x, y));
					}

					//MIN20
					//center point
					baseTiles[x][y] = l;
					if (vars > 0){
						baseVar[x][y] = (int)(Randomizer.getNextRandom() * vars);
					}else{
						baseVar[x][y] = 0;
					}
					//filling points
					int ox = x, oy = y;
					int counter = 0, rotating = 0, expander = 1;
					for (int m = 0; m < cells; m++){
						//first point
						if (counter == 0){
							x = ox + ROTATE_X[counter] * expander;
							y = oy + ROTATE_Y[counter] * expander;
						}else{
						//remaining points
							x += ROTATE_X[counter];
							y += ROTATE_Y[counter];
						}
						rotating++;
						if (rotating >= expander){
							rotating = 0;
							counter++;
							if (counter >= ROTATE_X.length){
								counter = 0;
							}
						}
						//clipping
						if (x < 0){
							x = 0;
						}else if (x >= width){
							x = width-1;
						}
						if (y < 0){
							y = 0;
						}else if (y >= height){
							y = height-1;
						}
						baseTiles[x][y] = l;
						if (vars > 0){
							baseVar[x][y] = (int)(Randomizer.getNextRandom() * vars);
						}else{
							baseVar[x][y] = 0;
						}
					}
					mass -= cells;
				}
				//End MIN20
				prob *= chance;
			}

			//MIN26
			//prob = chance / 4;

			//overlays
			for (int l = 0; l < tmax; l++){
				Overlay til = oloader.getOverlay(l);
				//MIN30 added probability
				long mass = (long)(worldSize * til.getProbability() * prob) + 1;
				//System.err.println(mass);
				int vars = til.getVars();
				//MIN20
				while (mass > 0){
					int cells = 1;
					int x = (int)(Randomizer.getNextRandom() * (width-MAX_BLOCK*2)) + MAX_BLOCK;
					int y = (int)(Randomizer.getNextRandom() * (height-MAX_BLOCK*2)) + MAX_BLOCK;

					//bonus doesnt go in blocks
					if (til.getProp("bonus") != null){
						if (baseTiles[x][y] == til.getBase()){
							//MH130
							addTopLand(x, y, l, oloader);
							//topTiles[x][y] = l;
							//variations
							//if (vars > 0){
							//	topVar[x][y] = (int)(Randomizer.getNextRandom() * vars);
							//}else{
							//	topVar[x][y] = 0;
							//}
						}
					}else{
						//MIN20
						//center point
						if (baseTiles[x][y] == til.getBase()){
							//MH130
							addTopLand(x, y, l, oloader);
							//topTiles[x][y] = l;
							//variations
							//if (vars > 0){
							//	topVar[x][y] = (int)(Randomizer.getNextRandom() * vars);
							//}else{
							//	topVar[x][y] = 0;
							//}
						}
						//filling points
						int ox = x, oy = y;
						int counter = 0, rotating = 0, expander = 1;
						cells = (int)((til.getBlock()) * Randomizer.getNextRandom())+1;

						for (int m = 0; m < cells; m++){
							//first point
							if (counter == 0){
								x = ox + ROTATE_X[counter] * expander;
								y = oy + ROTATE_Y[counter] * expander;
							}else{
							//remaining points
								x += ROTATE_X[counter];
								y += ROTATE_Y[counter];
							}
							rotating++;
							if (rotating >= expander){
								rotating = 0;
								counter++;
								if (counter >= ROTATE_X.length){
									counter = 0;
								}
							}
							//clipping
							if (x < 0){
								x = 0;
							}else if (x >= width){
								x = width-1;
							}
							if (y < 0){
								y = 0;
							}else if (y >= height){
								y = height-1;
							}
							if (baseTiles[x][y] == til.getBase()){
								//MH130
								addTopLand(x, y, l, oloader);
								//topTiles[x][y] = l;
								//variations
								//if (vars > 0){
								//	topVar[x][y] = (int)(Randomizer.getNextRandom() * vars);
								//}else{
								//	topVar[x][y] = 0;
								//}
							}
						}
					}
					mass -= cells;
					//System.err.println(mass);
					//End MIN20
				}
				//prob *= chance;
			}

			//set variations for water tile
			for (int i=0; i<width; i++){
				for (int j=0; j<height; j++){
					//0  is water tile
					if (baseTiles[i][j] == 0){
						int mask[][] = new int[3][3];
						for (int x=0; x<3; x++){
							for (int y=0; y<3; y++){
								//get surrounding tiles
								int dx = i + x - 1;
								int dy = j + y - 1;
								if (dx >=0 && dx < width && dy >=0 && dy < height){
									mask[x][y] = baseTiles[dx][dy];
								}else{
									//make it water
									mask[x][y] = 0;
								}
							}
						}
						//calculate variations
						// 0:0 1:0 2:0
						// 0:1 1:1 2:1
						// 0:2 1:2 2:2
						if (mask[2][1] > 0 && mask[1][0] > 0 && mask[0][1] > 0 && mask[1][2] > 0){
							baseVar[i][j] = 17;
						}else if (mask[1][0] > 0 && mask[0][1] > 0 && mask[1][2] > 0){
							baseVar[i][j] = 13;
						}else if (mask[0][1] > 0 && mask[1][2] > 0 && mask[2][1] > 0){
							baseVar[i][j] = 14;
						}else if (mask[1][2] > 0 && mask[2][1] > 0 && mask[1][0] > 0){
							baseVar[i][j] = 15;
						}else if (mask[2][1] > 0 && mask[1][0] > 0 && mask[0][1] > 0){
							baseVar[i][j] = 16;
						}else if (mask[1][0] > 0 && mask[0][1] > 0){
							baseVar[i][j] = 9;
						}else if (mask[0][1] > 0 && mask[1][2] > 0){
							baseVar[i][j] = 10;
						}else if (mask[1][2] > 0 && mask[2][1] > 0){
							baseVar[i][j] = 11;
						}else if (mask[2][1] > 0 && mask[1][0] > 0){
							baseVar[i][j] = 12;
						}else if (mask[0][1] > 0 && mask[2][1] > 0){
							baseVar[i][j] = 19;
						}else if (mask[1][0] > 0 && mask[1][2] > 0){
							baseVar[i][j] = 18;
						}else if (mask[1][0] > 0){
							baseVar[i][j] = 1;
						}else if (mask[0][1] > 0){
							baseVar[i][j] = 2;
						}else if (mask[1][2] > 0){
							baseVar[i][j] = 3;
						}else if (mask[2][1] > 0){
							baseVar[i][j] = 4;
						}else if (mask[0][0] > 0){
							baseVar[i][j] = 5;
						}else if (mask[0][2] > 0){
							baseVar[i][j] = 6;
						}else if (mask[2][2] > 0){
							baseVar[i][j] = 7;
						}else if (mask[2][0] > 0){
							baseVar[i][j] = 8;
						}else{
							baseVar[i][j] = 0;
						}
					}
				}
			}
		}
	}
}