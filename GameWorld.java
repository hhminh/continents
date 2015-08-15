// tiles
// offsets
// overlays
// index-arraylist component when remov is remove from index(-1) and set to null in list
//
// added combat points to limit battle length
// speed is used to determined if unit can retreat
// range is determined if unit can counter attack or attack from range
//
// MIN03 capturing army
// MIN08 fix combat counting, resource transfer between army to city, 
//			combat drawing in black fog
// MIN10 fix unit drawing, form army, building/training panel selection, add ai
// MIN16 paintComponent to optimize JPanel
// MIN17 smaller saved game
// MIN31 boat can descend, man can swim up, man can tow boat onland or load onto boat
// MH34  Disable about box in beginning
// MH40  Take basepanel, unitpanel and comment to jdialogs to allow show and hide
// MH105 Incomplete????????????????????????????????????????
// MH106 Incomplete????????????????????????????????????????
// MH107 TRANSPORT MOVEMENT, BUY/SELL IN 100, DIALOGUE BUTTONS TOO CLOSED
// MH108 JOB, REWARD, NEED TO DO THE ANIMATION AND REWARD COUNTING
// MH110 Working is now check by terrain type so ship dont work on land 
//			and man dont work on water, also fix bug of eating up overlay
//		Bribery and barter now works
//		SOLVE MAJOR BUG: WHEN THERE ARE ONLY 2 LEFT (1 BOAT, 1 MAN) REMOVE THE MAN CAUSE
//			MOVEMAX TO 9999999, PAUSE GAME FOR A WHILE
//
// MH112 Using VolatileImage to optimize drawing
// OVERDUE: JInternalFrame, JDesktopPane, TAXING, HAPPINESS, REVOLT
// MH115 Fix col/row index bug
// MH130 Added 3 overlays per cell, combination is now in
// MH131 Global message log saved with game save7/18/2004
//			A fleet which exhausted its movement can not merge with other fleets
// MH141 Right click to select army, left click to select base, left click + shift to bombard, left click to move (with army selected), left click + alt to talk to other armies
// MH142 Allow luxuries to be consumed for happiness, allow decision on attacking allies
// MH160 Experiment with jtree
//
// to-do: reorganize panels, hardcode resource & production, group icons & production into gameIcon class
//        implement base, split animal into tiles to implement resource type
//        resource: gold, hammer (building), book (training), food, wood, stone, metal, horse, weapon, armour, good
//        resource convert to gold table, resource indicator hardcoded (gold_type = 0, etc)
//


import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import java.applet.*;
import javax.sound.midi.*;
import java.util.jar.*;
import java.util.zip.*;
import java.net.URL;
import java.net.URI;

public class GameWorld implements GameIconStorage{
	public final static String TURN_UNIT = "day";
	public final static String TURN_UNIT_PLURAL = "days";
	
	public final static int CHEAT_LIMIT = 40;

	public final static int BEVEL_SIZE = 1;

	public final static int CITY_RANGE = 5;

	public final static int CAPTURE_CHANCE = 5;

	public final static String GAME_NAME = "Continents v0.98";

	public final static int SMALL_CELL_LIMIT = 3;

	public final static int SMALL_ICON_SIZE = 24;

	public final static Color [] OWNER_COLOR = {
		new Color(0, 0, 255), new Color(26, 26, 26), new Color(255, 0, 0),
		new Color(0, 255, 0), new Color(218, 218, 218), new Color(253, 198, 137),
		new Color(140, 102, 54)};

	public final static Color [] OWNER_TEXT_COLOR = {
		Color.white, Color.white, Color.white,
		Color.black, Color.black, Color.black,
		Color.black};

	public final static int OWNER_MAX = OWNER_COLOR.length - 1;
	public final static int OWNER_SIZE = OWNER_COLOR.length;
	public final static int PLAYER_OWNER = 0;
	public final static int NEUTRAL_OWNER = OWNER_MAX;
	public final static int PLAYABLE_OWNER_SIZE = OWNER_MAX;
	public final static int PLAYABLE_OWNER_MAX = PLAYABLE_OWNER_SIZE-1;

	public final static int ARMY_LIMIT = 200;
	public final static Color MASK_COLOR_STANDARD = new Color(122, 120, 255);

	public static final Color COL_GREEN = new Color(165, 173, 67);
	public static final Color COL_GREEN_WHITE = new Color(199, 225, 158);
	public static final Color COL_HIGHLIGHT = new Color(79, 100, 253);

	public final static int SEA_LAND = 0;
	public final static int TOWN_LAND = 1;

	public final static int THEIVING_DIFFICULTY = 50;
	public final static int REVOLT_DIFFICULTY = 70;

	public final static float ECONOMIC_RATE = 0.65f;

	public final static String TECH_NAME[] = {"Metalurgy","Mechanical","Chemistry",
											"Meelee Weapons","Ranged Weapons","Social Advances"};
	public final static int TECH_SIZE = TECH_NAME.length;
	public final static int TECH_LIMIT = 200;
	public final static int TECH_COST[] = {4, 3, 3, 4, 4, 3};

	public final static String RESOURCE_NAME[] = {"Golds","Foods","Woods",
												"Stones","Metals", "Gunpowder",
												"Chemical", "Goods", "Luxuries",
												"Tools","Hammers", "Books", "Happiness", "Security"};

	public final static String MONTH_NAME[] = {"The Rabbit","The Bull", "The Scorpion",
												"The King", "The Bird", "The Dwarf",
												"The Scorpion", "The Sun", "The Dog",
												"The Rooster", "The Beast", "Ice"};
	public final static int MONTHS = 30;
	public final static int MONTH_DAYS = 30;
	public final static int YEAR_DAYS = MONTH_DAYS * MONTHS;

	//selling-hurry production
	public final static float RESOURCE_COST[] = {1, 0.25f, 2, 2, 3, 5, 4, 6, 12, 8, 4, 6, 25, 20};

	//public final static int RESOURCE_LIMIT = 500;
	public final static int RESOURCE_LIMIT[] = {4000, 1000, 500, 500, 500, 300, 300, 300, 200, 200, 200, 200, 200, 200};
	public final static int RESOURCE_LIMIT_SMALL[] = {400, 100, 50, 50, 50, 30, 30, 30, 20, 20, 0, 0, 200, 200};

	public final static int RESOURCE_CURRENCY = 0;
	public final static int RESOURCE_FOOD = 1;
	public final static int RESOURCE_WOOD = 2;
	public final static int RESOURCE_STONE = 3;
	public final static int RESOURCE_METAL = 4;
	public final static int RESOURCE_LUXURIES = 8;
	public final static int RESOURCE_TOOL = 9;
	public final static int RESOURCE_HAMMER = 10;
	public final static int RESOURCE_BOOK = 11;
	public final static int RESOURCE_HAPPY = 12;
	public final static int RESOURCE_SECURITY = 13;
	public final static int RESOURCE_SIZE = RESOURCE_NAME.length;
	public final static int RESOURCE_REWARD_LIMIT = 50;
	public final static int RESOURCE_GROW = 100;
	public final static int RESOURCE_CRITICAL = 20;
	public final static int RESOURCE_FACTOR = 3;
	public final static int RESOURCE_PENALTY = 1;
	public final static int RESOURCE_HIDDEN = 12;
	public final static int RESOURCE_TRANSFERABLE = 10;

	//Slice for display
	public final static int RESOURCE_SLICE = 10;
	//Resource upper cap
	public final static int RESOURCE_CAP = 20;
	//Resource to build new base
	public final static int RESOURCE_PIONEER = 20;

	public final static int IMG_SELECT = 0;
	public final static int IMG_RESOURCE = 1;
	public final static int IMG_FRAME = 2;
	public final static int IMG_ADVISOR_SMALL = 3;
	public final static int IMG_ADVISOR_BIG = 4;
	public final static int IMG_HELPER = 5;
	public final static int IMG_FRAME2 = 6;
	public final static int IMG_GLOBE = 7;
	public final static int IMG_TRADER = 8;
	public final static int IMG_TECH = 9;
	public final static int IMG_HOURGLASS = 10;
	public final static int IMG_DOLLAR = 11;
	public final static int IMG_SCROLL = 12;
	public final static int IMG_IDEA = 13;
	public final static int IMG_LOAD = 14;
	public final static int IMG_URBAN = 15;
	public final static int IMG_TALKING001 = 16;
	public final static int IMG_JOB001 = 17;
	public final static int IMG_JOB002 = 18;
	public final static int IMG_JOB003 = 19;
	public final static int IMG_STOP = 20;
	public final static int IMG_WARN = 21;
	public final static int IMG_DEFEAT01 = 22;
	public final static int IMG_WATERDROP01 = 23;
	public final static int IMG_BRIBE = 24;
	public final static int IMG_GET_HIT = 25;
	public final static int IMG_UNHAPPY = 26;
	public final static int IMG_HEALING = 27;

	public final static int TRAIT_NORMAL = 0;
	public final static int TRAIT_BRAVE = 1;
	public final static int TRAIT_DELLIGENT = 2;
	public final static int TRAIT_GENIUS = 3;
	public final static int TRAIT_HEROIC = 4;
	public final static int TRAIT_CHARISMATIC = 5;
	public final static int TRAIT_LOYALTY[] = {10,12,25,7,15,8};
	public final static String TRAIT_NAME[] = {"Normal", "Brave", "Delligent",
		"Genius", "Heroic", "Charismatic"};
	public final static Color TRAIT_COLOR[] = {Color.blue, Color.pink, Color.orange,
		Color.green, Color.red, Color.yellow};

	public final static int TALK_ACCEPT = 0;
	public final static int TALK_REJECT = 1;
	public final static int TALK_INSULT = 2;
	public final static int TALK_ADMIRE = 3;
	public final static int TALK_CONSULT = 4;

	public final static int TALK_WAR = 5;
	public final static int TALK_PEACE = 6;
	public final static int TALK_ALLIANCE = 7;
	public final static int TALK_CANCEL_ALLIANCE = 8;
	public final static int TALK_INSULT2 = 9;
	public final static int TALK_ADMIRE2 = 10;
	public final static int TALK_CONSULT2 = 11;

	public final static int TALK_CON_RES = 12;
	public final static int TALK_CON_MIL = 13;
	public final static int TALK_CON_TOWN = 14;
	public final static int TALK_CONSULT3 = 11;

	public final static String[] TALK_LINE =
		{"Accept the proposal", "Reject the proposal",
		 "[Insult] You are such a fool ...", "[Admire] Wow you look really great ...",
		 "[Consult with advisor]"};
	public final static String[] TALK_LINE1 =
		{"Declare war", "Propose peace treaty", "Propose alliance treaty",
		 "Cancel alliance", "[Insult] You are such an idiot ...",
		 "[Admire] Wow what a wonderfull ...", "[Consult with advisor]"};
	public final static String[] TALK_LINE2 =
		{"Contribute resources", "Contribute military forces",
		 "Contribute towns", "[Consult with advisor]"};

	private TileLoader l;
	private TerrainLoader tloader;
	private OverlayLoader oloader;
	private UnitTypeLoader uloader;
	private BaseTypeLoader bloader;
	private HouseTypeLoader hloader;
	private PlayerLoader ploader;
	private TechLoader teloader;
	//to-do in game data or loader
	private Scenario scenario;

	private MapPanel mp;
	private MiniMapPanel mnp;
	private JDialog dlgMap;
	private JDialog dlgComment;
	private JDialog dlgAction;
	private JDialog dlgBuilding;
	private DiplomacyDialog dlgDip;
	private TalkDialog dlgTalk, dlgTalk1, dlgTalk2;
	private UnitTypeDialog dlgUnitType;
	private ResearchDialog dlgResearch;
	private TechChooserDialog dlgAdvance;
	private ScrollingTextDialog dlgScrollText;
	private MapGenDialog dlgMapGen;
	private EnquiryDialog dlgEnquiry;
	private UnitEditorDialog dlgUnitEditor;
	private JobChooserDialog dlgJob;
	private CurrentJobDialog dlgMission;

	private JFrame frame;
	private UnitPanel panRight;
	private BasePanel panBot;
	private JPanel panBut; //, panCommand;
	private JMenu mnuGame, mnuDip, mnuOption, mnuHelp, mnuTool;
	private JMenuItem mnuGenerate, mnuEndTurn, mnuSave, mnuMap,
		mnuLoad, mnuDipReport, mnuResearch, mnuIntelligence, mnuAbout,
		mnuSizeLarge, mnuSizeSmall, mnuTech, mnuNextArmy, mnuNextBase, mnuTrader,
		mnuUnitEditor, mnuMission;
	private JCheckBoxMenuItem mnuGod, mnuMask, mnuSound;

	private JMenuBar mnuBar;
	private JTextPane txtComment, txtMessage;
	//MIN22
	private JButton btnMap, btnEndTurn, btnNextArmy, btnNextTown, btnClear, btnMoveAll;
	private JEditorPane txtHTML;
	private JFileChooser jfcGame;

	private GameData gd;
	private Object selection;
	private	GameMoveTimer mover;
	private	GameBombardTimer bombarder;
	private NewTurnTimer turner;
	private SoundList singer;
	private ArrayList icons;

	private UnitTransferDialog dlgUnitTransfer;
	private InventoryReportDialog dlgInvReport;
	private ResourceTransferDialog dlgResTransfer;
	private YesNoDialog dlgYesNo;

	//Text colour output streams
	private PrintStream msgOut;
	//All cursor are preloaded
	private Cursor[] cursors;
	//Common timer
	private javax.swing.Timer tmrAbout;
	//God mode
	private boolean god = false;
	//Special animation layer1
	private SpriteEffect effLayer1;
	//Record the time of a click/release
	private long lastTimeMs = 0;

	public static void setComponentFont(Font font_standard){
		FontUIResource font_ui_res_standard = new FontUIResource(font_standard);

		ColorUIResource color_ui_back_standard = new ColorUIResource(COL_GREEN_WHITE);
		ColorUIResource color_ui_fore_standard = new ColorUIResource(Color.black);
		ColorUIResource color_ui_thumb_standard = new ColorUIResource(COL_GREEN);
		ColorUIResource color_ui_track_standard = new ColorUIResource(COL_GREEN);
		ColorUIResource color_ui_select_standard = new ColorUIResource(COL_GREEN);
		ColorUIResource color_ui_back_tip = new ColorUIResource(Color.cyan);
		//ColorUIResource color_ui_fore_tip = new ColorUIResource(Color.black);

		BorderUIResource border_ui_standard = new BorderUIResource(
			BorderFactory.createCompoundBorder(
			BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(0,1,0,1),
			BorderFactory.createBevelBorder(BevelBorder.LOWERED,
			COL_GREEN, COL_GREEN_WHITE)),
			BorderFactory.createEmptyBorder(2,5,2,5)));

		
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource){
				UIManager.put(key,font_ui_res_standard);
			//}else if (value instanceof ColorUIResource){
			//	String kname = key.toString();
			//	if (kname.compareToIgnoreCase("JMenuBar.background") == 0){
			//		UIManager.put(key,color_ui_back_standard);
			//	}
			}else if (value instanceof ColorUIResource){
				String kname = key.toString();
				if (kname.indexOf("ackground") != -1){
					if (kname.indexOf("ToolTip") != -1){
						UIManager.put(key, color_ui_back_tip);
					//}else if (kname.indexOf("utton") != -1){
					//	UIManager.put(key,color_ui_thumb_standard);
					}else if (kname.indexOf("select") != -1){
						UIManager.put(key,color_ui_select_standard);
					}else{
						UIManager.put(key,color_ui_back_standard);
					}
				}//else if (kname.indexOf("oreground") != -1){
				//	UIManager.put(key,color_ui_fore_standard);
				//}//else if (kname.indexOf("track") != -1){
				//	UIManager.put(key,color_ui_track_standard);
				//}
			}
		}
		//UIManager.put("ScrollBar.background", color_ui_back_standard);
		//UIManager.put("ScrollBar.foreground", color_ui_fore_standard);
		UIManager.put("ScrollBar.track", color_ui_track_standard);
		UIManager.put("ScrollBar.thumb", color_ui_thumb_standard);
		//UIManager.put("Button.textIconGap",new Integer(0));
		UIManager.put("Button.border", border_ui_standard);
		//UIManager.put("Panel.background",color_ui_back_standard);
		UIManager.put("ScrollBar.width", new Integer(12));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e){
			e.printStackTrace(System.err);
		}
	}

	public static void main(String[] args){
		try{
			setComponentFont(new Font("Verdana",Font.PLAIN,11));
		}catch(Exception e){
			setComponentFont(new Font("Arial",Font.PLAIN,11));
		}finally{
			setComponentFont(new Font("System",Font.PLAIN,11));
		}

		new GameWorld();
	}

	public static Point getDir(int sx, int sy, int dx, int dy){
		int dirx = 0;
		int diry = 0;

		if (dx > sx){
			dirx = 1;
		}else if (dx < sx){
			dirx = -1;
		}

		if (dy > sy){
			diry = 1;
		}else if (dy < sy){
			diry = -1;
		}

		return new Point(dirx, diry);
	}

	public static Image getReplaceColor(Image in, Color oldc, Color newc){
		Image out;
		// Get an image producer for the image
		ImageProducer ip = in.getSource();
		// Construct a suitable filter
		//RGBImageFilter filter = new ColorReplaceImageFilter(oldc, newc);
		RGBImageFilter filter = new ColorFilter(oldc, newc);
		// Create an image producer for the filtered image
		ImageProducer filt_ip = new FilteredImageSource(ip,	filter);

		// Produce the output image
		out = Toolkit.getDefaultToolkit().createImage(filt_ip);

		return out;
		//return in;
	}

	public static void printMessage(Object str){
		System.out.println(str);
		System.out.println();
	}

	public static Image getScaledImage(Image in, int x, int y){
		Image out;
		// Get an image producer for the image
		ImageProducer ip = in.getSource();
		// Construct a suitable filter
		//ReplicateScaleFilter filter = new ReplicateScaleFilter(x, y);
		AreaAveragingScaleFilter filter = new AreaAveragingScaleFilter(x, y);
		// Create an image producer for the filtered image
		ImageProducer filt_ip = new FilteredImageSource(ip,	filter);

		// Produce the output image
		out = Toolkit.getDefaultToolkit().createImage(filt_ip);

		return out;
	}

	public static float getResourceCost(int type, int stock){
		//The more in stock, the less the cost
		//The less in stock, the more the cost
		if (stock < 0) {
			return RESOURCE_COST[type];
		}
		float halfFull = (float)(RESOURCE_LIMIT[type] * ECONOMIC_RATE);
		float inflation = (float)((1 - stock/halfFull) * RESOURCE_COST[type]);
		return RESOURCE_COST[type] + inflation;
	}

	public static int getResourceGrow(int pop){
		return (int)(RESOURCE_GROW + (pop - 1) * RESOURCE_CAP);
	}

	public Image getSubImage(Image in, int x1, int y1, int w1, int h1,
		int x2, int y2, int w2, int h2){
		BufferedImage bmgTmp = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
		Graphics gTmp = bmgTmp.getGraphics();
		gTmp.drawImage(in, x2,y2,w2,h2,x1,y1,w1,h1,frame);
		return bmgTmp.getSubimage(x2,y2,w2,h2);
	}

	public void clearSelection(){
		if (selection instanceof Army){
			//MH40
			dlgAction.hide();
			//MH40
			Army a = (Army)selection;
			a.setSelection(false);
			selection = null;
			panRight.setArmy(null, null);
			//MH150
			mp.clearHighlight();
			//MH150
			mp.setCursor(Cursor.getDefaultCursor());
			mp.repaintClipCell(a.getX(), a.getY());
		}else if (selection instanceof Base){
			//MH40
			//dlgAction.hide();
			dlgBuilding.hide();
			//MH40
			Base b = (Base)selection;
			b.setSelection(false);
			selection = null;
			panBot.setBase(null);
			//MH150
			mp.clearHighlight();
			//MH150
			mp.repaintClipCell(b.getX(), b.getY());
			//frame.pack();
		}
	}

	public void enable(){
		mnuEndTurn.setEnabled(true);
		mnuNextArmy.setEnabled(true);
		mnuNextBase.setEnabled(true);
		btnMap.setEnabled(true);
		btnEndTurn.setEnabled(true);
		btnNextArmy.setEnabled(true);
		btnNextTown.setEnabled(true);
		btnMoveAll.setEnabled(true);
		mnuGame.setEnabled(true);
		mnuDip.setEnabled(true);
		mnuHelp.setEnabled(true);
		mnuOption.setEnabled(true);
		btnClear.setEnabled(true);
	}

	public void disable(){
		mnuEndTurn.setEnabled(false);
		mnuNextArmy.setEnabled(false);
		mnuNextBase.setEnabled(false);
		btnMap.setEnabled(false);
		btnEndTurn.setEnabled(false);
		btnNextArmy.setEnabled(false);
		btnNextTown.setEnabled(false);
		btnMoveAll.setEnabled(false);
		mnuGame.setEnabled(false);
		mnuDip.setEnabled(false);
		mnuHelp.setEnabled(false);
		mnuOption.setEnabled(false);
		btnClear.setEnabled(false);
	}

	public void gameOver(){
		mnuEndTurn.setEnabled(false);
		mnuNextArmy.setEnabled(false);
		mnuNextBase.setEnabled(false);
		btnMap.setEnabled(false);
		btnEndTurn.setEnabled(false);
		btnNextArmy.setEnabled(false);
		btnNextTown.setEnabled(false);
		btnMoveAll.setEnabled(false);
		mnuDip.setEnabled(false);
		mnuOption.setEnabled(false);
		btnClear.setEnabled(false);

		mnuGame.setEnabled(true);
		mnuHelp.setEnabled(true);
	}

	public void updateMiniMap(){
		if (dlgMap.isShowing()){
			mnp.repaint();
		}
	}

	public void showDay(){
		int yday = gd.getTurn() % YEAR_DAYS;
		int month = yday / MONTHS;
		int day = yday % MONTHS + 1;

		frame.setTitle(GAME_NAME + "  -  " + getDayAsString()); 
	}

	public String getDayAsString(){
		int yday = gd.getTurn() % YEAR_DAYS;
		int month = yday / MONTHS;
		int day = yday % MONTHS + 1;

		return TURN_UNIT + " " + Integer.toString(day) + ", " + MONTH_NAME[month]; 
	}

	public void showUnitTransfer(UnitSwappable c1, UnitSwappable c2, int owner){
		dlgUnitTransfer.show(c1, c2, owner);

		//MIN32 make sure to transfer all resources if
		//the whole armies merged
		if (c1 instanceof Army && c2 instanceof Army){
			Army army1 = (Army)c1;
			Army army2 = (Army)c2;
			if (army1.getCount() < 1){
				for (int i=0; i<army1.getResourceCount(); i++){
					army2.transfer(army1, i, army1.getResource(i));
				}
			}else if (army2.getCount() < 1){
				for (int i=0; i<army2.getResourceCount(); i++){
					army1.transfer(army2, i, army2.getResource(i));
				}
			}

		}
	}

	public void showResTransfer(ResourceHolder res1, ResourceHolder res2, boolean full){
		dlgResTransfer.show(res1, res2, full);
	}

	public void showInvReport(String txt){
		dlgInvReport.setReport(txt);
		dlgInvReport.show();
	}

	public void showTraderInventory(){
		String strReport = "<%Independent Trader's Inventory%>\n";
		strReport += "-----------------------------------------------------\n";
		//resources
		GameTrader trader = gd.getTrader();
		for (int i=0; i<GameWorld.RESOURCE_HIDDEN; i++){
			strReport += GameWorld.RESOURCE_NAME[i] + ":\t\t";
			strReport += Integer.toString(trader.getResource(i)) + "\n";
		}

		showInvReport(strReport);
	}

	public void showResearch(int o){
		dlgResearch.showPlayer(getGameIcon(IMG_TECH), gd, o);
	}

	//MIN34
	protected String repeatCell(int times, int max){
		String result = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\"><tr>";
		for (int i=0; i<times; i++){
			result += "<td bgcolor=\"red\" width=\"10\">&nbsp;</td>";
		}
		for (int i=times; i<max; i++){
			result += "<td bgcolor=\"gray\" width=\"10\">&nbsp;</td>";
		}
		result += "</tr></table>";
		return result;
	}

	public void showIntelligence(int owner){
		String msg = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\">";
		msg += "<tr><td colspan=\"3\"><font color=\"blue\"><b>Intelligence Report</b>";
		msg += "</font></td></tr><tr><td></td><td>Sources:</td><td>Spy's analysis";
		msg += " (80% confidence)</td></tr>";

		int maxArmies = 0, maxBases = 0;
		//MIN25
		for (int i=0; i<PLAYABLE_OWNER_SIZE; i++){
			Player p = ploader.getPlayer(i);
			if (maxArmies < p.getArmyCount()){
				maxArmies = p.getArmyCount();
			}
			if (maxBases < p.getBaseCount()){
				maxBases = p.getBaseCount();
			}
		}
		//MIN25
		for (int i=0; i<PLAYABLE_OWNER_SIZE; i++){
			Player p = ploader.getPlayer(i);
			int ap = (int)(p.getArmyCount() * (8 + Randomizer.getNextRandom()*4) / maxArmies);
			int bp = (int)(p.getBaseCount() * (8 + Randomizer.getNextRandom()*4) / maxBases);
			msg += "<tr><td colspan=\"3\"><font color=\"blue\"><b>"+p.getName()+"</b>";
			msg += "</font></td></tr><tr><td></td><td>Military Power</td><td>";
			msg += repeatCell(ap, 10) + "</td></tr><tr>";
			msg += "<td></td><td>Border Size</td><td>";
			msg += repeatCell(bp, 10) + "</td></tr>";
		}
		showScrollingText(msg);
	}

	public void showScrollingText(String msg){
		dlgScrollText.showMessage(msg);
	}

	public void showAbout(){
		//"<p><b>Extra Credits for ideas, algorithms and advices go to:</b> (Not yet)<p>"+
		dlgScrollText.showMessage(
			"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"000000\">"+
			"<tr><td align=\"center\"><img src=\""+getURL("images/continent.gif")+"\"></td></tr></table>"+
			"<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">"+
			"<tr><td valign=\"top\" nowrap>"+
			"&copy;opyrighted </td><td>by Minh, Hoang 2003."+
			" All rights reserved.</td></tr><tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Version</td><td>0.9 build 55</td></tr><tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">License</td><td>This turn based strategy "+
			"game is <b>free</b> and can be redistributed given that <b>the "+
			"game package and the copyright notice remained untouched</b>."+
			"There is no guarantee on how the game will behave on your machine. "+
			"At this stage, the game is playable but still in further developments."+
			"It has been designed as modular as possible and all game data coming from "+
			"a database which allows the creation of totally new games very easily "+
			"by simply creating new sets of graphics and new databases</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr><tr><td valign=\"top\">Author<br><img src=\""+getURL("images/portrait.gif")+
			"\"></td><td>"+
			"This game is totally free, I would like you to "+
			"<b>send me feedback with bugs, ideas to make the game more enjoyable</b>. "+
			"I also look for anybody who wants to help in graphics, sounds, programming. "+
			"<b>Email me:</b> <font color=\"blue\">vb_user@yahoo.com</font></td></tr>"+
			"<tr><td colspan=\"2\"></td></tr><tr><td valign=\"top\">Tips</td><td>Most controls in the game have tooltips. "+
			"Simply pause your mouse over the pictures, buttons, etc and wait for 1 second "+
			"for the tips to come up. For more help, please visit <font color=\"blue\">"+
			"http://www.geocities.com/vb_user/</font> or continue to the <b>manual section</b>.</td></tr><tr><td colspan=\"2\"></td></tr><tr><td valign=\"top\">"+
			"Progress</td><td><table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">"+
			"<tr><td>Object Model</td><td><b>80%</b></td><td>Tested</td><td>90%</td></tr>"+
			"<tr><td>Main Interface</td><td><b>90%</b></td><td>Tested</td><td>80%</td></tr>"+
			"<tr><td>Mini Map</td><td><b>100%</b></td><td>Tested</td><td>90%</td></tr>"+
			"<tr><td>Graphics</td><td><b>70%</b></td><td>Tested</td><td>70%</td></tr>"+
			"<tr><td>Effects</td><td><b>50%</b></td><td>Tested</td><td>50%</td></tr>"+
			"<tr><td>Game Save/Load</td><td><b>70%</b></td><td>Tested</td><td>90%</td></tr>"+
			"<tr><td>Scripts</td><td><b>60%</b></td><td>Tested</td><td>80%</td></tr>"+
			"<tr><td>Bug fixing</td><td><b>On going</b></td><td>Tested</td><td>On going</td></tr>"+
			"</table></td></tr><tr><td colspan=\"2\"></td></tr><tr><td valign=\"top\">"+
			"Manual</td><td><table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">"+
			"<tr><td valign=\"top\">Fog of war</td><td>Also referred to as visible areas."+
			" Only objects (armies, bases, etc) under your control are visible "+
			"and also exert an area surrounding them. Non-visible areas are clouded."+
			" To turn off fog of war, go to JMenu <b>Options &gt; Turn off Fog of War</b>."+
			" I dont recommended playing with this option as it's not real.</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Shortcuts</td><td>On most dialogs, press ESCAPE will close them<br>On confirmation dialog, [ALT] + [Y] to choose Yes, [ALT] + [N] to choose No</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Moving armies</td><td>First, on your map, click on your army."+
			" You will see a selection circle animated around your army."+
			" Click on any visible square to move your army. Please note that"+
			" any army has a limited number of movement per turn. Have a look"+
			" at the right hand side panel to read all your armies attributes."+
			" See the picture below for details.<br>"+
			"<img src=\""+getURL("images/help04.gif")+"\"></td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Combat</td><td>Following the steps of moving"+
			" an army but select a square where an enemy army is in the path. "+
			"If your army has enough movement points, they will attempt to "+
			"engage the enemy. Each units in an army has a number of combat points"+
			" each turn. Some units can not attack or defend themselves (worker, etc)."+
			" You should have some military units to defend them.</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Load/Unload resources</td><td>Select your army"+
			" then on the resource panel, left click to unload resource, right "+
			"click to load resource. Resource will be moved by 10 each time. "+
			"Hold [Shift] to move 1 at a time. Please note that an army need to"+
			" be inside a base for resource load/unloading. See picture below.</td></tr>"+
			"<tr><td colspan=\"2\"><img src=\""+getURL("images/help07.gif")+"\"></td></tr>"+
			"<tr><td valign=\"top\">Upgrade equipments</td><td>Select your army, then"+
			" right click on units inside the army. A popup appears with unit details"+
			" with a button called <b>Upgrade Equipments</b>.</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Organizing bases/cities</td><td>Select your base"+
			" by clicking it on the map. To move your citizens around, simply click on"+
			" the source square and then the destination square. A popup appears "+
			" that let you move citizens between work sites/houses. See picture "+
			"below.</td></tr>"+
			"<tr><td colspan=\"2\"><img src=\""+getURL("images/help05.gif")+"\"></td></tr>"+
			"<tr><td valign=\"top\">Sell/Buy resources</td><td>The process is "+
			"similar to load/unload resources except that you now select a base. "+
			"Please note that you can only sell/buy resource in a base. Hold [Shift]"+
			" to move in small quantities. See picture below.</td></tr>"+
			"<tr><td colspan=\"2\"><img src=\""+getURL("images/help01.gif")+"\"></td></tr>"+
			"<tr><td valign=\"top\">Buildings</td><td>Select a building you want to"+
			" build from the list (picture above). Then move a few citizens to "+
			"a house or work site capable of constructing buildings (Normally "+
			"a builder hall).</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Recruit new units</td><td>Select your base "+
			"with an army or move your army to a base. Then select a site or house"+
			" with the citizens (military types or citizen types). Then click on"+
			" the button <b>Enlist</b> (See picture above). Your city will grow "+
			"when your food reserve reach a large amount (around 100).</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Form new armies</td><td>Similar to recruiting"+
			" units except that no army is currently occupying the base. A new "+
			"army will be formed in this base/city.</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Researching</td><td>To allocate resources for"+
			" research, select a base, click on the <b>gold icon</b> in the resource"+
			" listing panel to show the research popup. <b>Note:</b> This game"+
			" doesnt use linear research scheme which means advancements and "+
			" discoveries are achieved in different order for each time you play.</td></tr>"+
			"<tr><td colspan=\"2\"></td></tr>"+
			"<tr><td valign=\"top\">Split/Join Army</td><td>You need to move your army"+
			" into a base, then select one unit you want to split by clicking on it from"+
			" the army view panel. Then click <b>Join City</b>. Repeat the step until"+
			" you are done. Now you can move the remaining army out of the city."+
			" Then go to the city view, select the site/house with the units you splitted"+
			" and then click <b>Enlist</b>. Now you have two armies. The process is done so"+
			" to make it more realistic.</td></tr>"+
			"</table></td></tr></table>");
	}

	public void doResearch(Base b){
		//dlgResearch.showChooser(getGameIcon(IMG_TECH), gd, b.getOwner());
		dlgResearch.showChooser(getGameIcon(IMG_TECH), gd, b.getOwner(), b.getRType(), b.getRRate());
		
		b.setRType(dlgResearch.getValue());
		b.setRRate(dlgResearch.getRate());

		if (panBot.isShowing()){
			panBot.refresh();
		}
	}

	public void doConquer(Base base, Army army){
		int oldOwner = base.getOwner();
		int newOwner = army.getOwner();
		base.changeOwner(newOwner);
		int hidden = bloader.getBaseType(base.getType()).getHiddenTech();
		if (hidden > -1 && gd.advanceIndex(newOwner, hidden) == -1){
			Tech tech = teloader.getTech(hidden);
			if (tech != null) {
				gd.addAdvance(newOwner, hidden);

				if (newOwner == PLAYER_OWNER){
					printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + 
						"%>My lord, we have acquired new technology!!!");
				}

				int newUnit = tech.getUUpgrade();
				if (newUnit > -1){
					if (uloader.addCustom(newOwner, newUnit)){
						if (newOwner == PLAYER_OWNER){
							printMessage("<%"+GameWorld.IMG_IDEA+
								"%>A new unit is now available!");
						}
					}
				}
			}
		}
		if (oldOwner == PLAYER_OWNER){
			gd.cloud(base.getX(), base.getY());
			printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + 
				"%>My lord, we have lost " + base.getName() + " ..!!!");
		}
	}

	public void doTradeWithAI(Army army){
		if (selection != null && selection instanceof Army){
			Army initiator = (Army)selection;
			if (army.getOwner() != initiator.getOwner()){
				if (gd.getDiplomacy(initiator.getOwner(), army.getOwner()) > 
					GameData.DIP_UNKNOWN){
					showResTransfer(initiator, army, false);
				}else{
					printMessage("We need to establish a peace treaty before we can barter resources");
				}
			}else{
				showResTransfer(initiator, army, true);
			}
		}else{
			printMessage("Please select one of our companies closed by, then [ALT] [LEFT CLICK] on the company you want to trade with.");
		}
	}

	public boolean doAIUnitTransfer(Army a1, Army a2){
		int count = 0;
		while (a2.getCount() > 0 && 
			a1.transfer(a2, 0, uloader, hloader, tloader, oloader, teloader)){
			count++;
		}
		//MIN32 make sure to transfer all resources if
		//the whole armies merged
		if (a2.getCount() < 1){
			for (int i=0; i<a2.getResourceCount(); i++){
				a1.transfer(a2, i, a2.getResource(i));
			}
		}

		if (count > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean getAIDiplomacyDecision(int p1, int p2, int deal){
		return true;
	}

	public void doTalk(int p1, int p2, int deal){
		dlgTalk.showDialog(ploader, p1, p2, deal);
	}

	public void doTalk(int p1, int p2){
		dlgTalk1.showDialog(ploader, p1, p2);
	}

	public void doTalkAction(TalkDialog dialog, int p1, int p2, int deal, int index){
		int current = gd.getDiplomacy(p1, p2);
		//initiator
		if (p1 == PLAYER_OWNER){
			switch(index){
				case TALK_ACCEPT:
					break;
				case TALK_REJECT:
					break;
				case TALK_INSULT:
					break;
				case TALK_ADMIRE:
					break;
				case TALK_CONSULT:
					break;
				case TALK_WAR:
					if (current == GameData.DIP_WAR){
						dialog.setConsult("You are already at war!");
					}else if (current == GameData.DIP_REC_WAR){
						gd.setDiplomacy(p1, p2, GameData.DIP_WAR);
						gd.setDiplomacy(p2, p1, GameData.DIP_WAR);
						dialog.hide();
					}else{
						gd.setDiplomacy(p1, p2, GameData.DIP_REC_WAR);
						gd.setDiplomacy(p2, p1, GameData.DIP_REC_WAR);
						dialog.hide();
					}
					break;
				case TALK_PEACE:
					if (current == GameData.DIP_PEACE){
						dialog.setConsult("You are already at peace!");
					}/*else if (current == GameData.DIP_REC_PEACE){
						if (getAIDiplomacyDecision(p2, p1, GameData.DIP_PEACE)){
							gd.setDiplomacy(p1, p2, GameData.DIP_PEACE);
							gd.setDiplomacy(p2, p1, GameData.DIP_PEACE);
							dialog.setMessage("We are now at peace");
						}
					}*/else{
						gd.setDiplomacy(p1, p2, GameData.DIP_REC_PEACE);
						gd.setDiplomacy(p2, p1, GameData.DIP_REC_PEACE);
						dialog.setMessage("Give me some more time, i will return with an answer");
					}
					break;
				case TALK_ALLIANCE:
					if (current == GameData.DIP_ALLY){
						dialog.setConsult("You are already allies!");
					}/*else if (current == GameData.DIP_REC_ALLY){
						if (getAIDiplomacyDecision(p2, p1, GameData.DIP_ALLY)){
							gd.setDiplomacy(p1, p2, GameData.DIP_ALLY);
							gd.setDiplomacy(p2, p1, GameData.DIP_ALLY);
							dialog.setMessage("My dear friend, we will share the power and a long lasting friendship together");
						}
					}*/else if (current < GameData.DIP_PEACE){
						dialog.setMessage("What do you want with that idea?");
					}else{
						gd.setDiplomacy(p1, p2, GameData.DIP_REC_ALLY);
						gd.setDiplomacy(p2, p1, GameData.DIP_REC_ALLY);
						dialog.setMessage("Give me some more time, i will return with an answer");
					}
					break;
				case TALK_CANCEL_ALLIANCE:
					if (current == GameData.DIP_ALLY){
						gd.setDiplomacy(p1, p2, GameData.DIP_PEACE);
						gd.setDiplomacy(p2, p1, GameData.DIP_PEACE);
						dialog.setMessage("Why do you do that?");
					}else{
						dialog.setMessage("Huh? What are you talking about? We are not allied at all.");
					}
					break;
				case TALK_INSULT2:
					dialog.setMessage("Ah huh");
					break;
				case TALK_ADMIRE2:
					dialog.setMessage("Well, thank you for saying that ...");
					break;
				case TALK_CONSULT2:
					//MIN44
					Player player1 = ploader.getPlayer(p1);
					Player player2 = ploader.getPlayer(p2);
					if (player2.getBaseCount() > player1.getBaseCount()){
						dialog.setConsult(player2.getName()+" has more cities than us."+
							" We should find out where they are located.");
					}else{
						dialog.setConsult(player2.getName()+"'s land is comparable to ours.");
					}
					if (player2.getArmyCount() > player1.getArmyCount()){
						dialog.setConsult(player2.getName()+"'s military is impressive"+
							" We should be carefull dealing with them.");
					}else{
						dialog.setConsult(player2.getName()+"'s military is comparable to ours."+
							" Lets find out their deployment location and plan our moves.");
					}
					break;
				case TALK_CON_RES:
					break;
				case TALK_CON_MIL:
					break;
				case TALK_CON_TOWN:
					break;
				default:
			}
		//receiver
		}else if (p2 == PLAYER_OWNER){
			switch(index){
				case TALK_ACCEPT:
					gd.setDiplomacy(p1, p2, deal);
					gd.setDiplomacy(p2, p1, deal);
					dialog.setMessage("We are now at " + GameData.DIP_NAME[deal] + 
						"\nIs there anything else on your mind?");
					dialog.setConsult("My lord, we can end the dialog here.");
					break;
				case TALK_REJECT:
					//revert back
					if (deal != -1){
						if (current == deal){
							dialog.setConsult("My lord, We just accepted the proposal");
						}if (current > GameData.DIP_UNKNOWN){
							gd.setDiplomacy(p1, p2, current - 1);
							gd.setDiplomacy(p2, p1, current - 1);
						}
					}else{
						//what is this option?
					}
					dialog.setMessage("Very well then, hope you would think about it some other times");
					break;
				case TALK_INSULT:
					dialog.setMessage("Could you be a bit more polite?");
					break;
				case TALK_ADMIRE:
					dialog.setMessage("Well, thank you for saying that ...");
					break;
				case TALK_CONSULT:
					switch(deal){
						case GameData.DIP_PEACE:
							dialog.setConsult("We should accept this offer.");
							break;
						case GameData.DIP_ALLY:
							dialog.setConsult("We should accept this offer.");
							break;
						default:
							dialog.setConsult("My lord, could you be more specific.");
					}
					//MIN44
					Player player1 = ploader.getPlayer(p1);
					Player player2 = ploader.getPlayer(p2);
					if (player1.getBaseCount() > player2.getBaseCount()){
						dialog.setConsult(player1.getName()+" has more cities than us.");
					}else{
						dialog.setConsult(player1.getName()+"'s land is comparable to ours.");
					}
					if (player1.getArmyCount() > player2.getArmyCount()){
						dialog.setConsult(player1.getName()+"'s military is impressive.");
					}else{
						dialog.setConsult(player1.getName()+"'s military is comparable to ours.");
					}
					break;
				case TALK_WAR:
					break;
				case TALK_PEACE:
					break;
				case TALK_ALLIANCE:
					break;
				case TALK_CANCEL_ALLIANCE:
					break;
				case TALK_INSULT2:
					break;
				case TALK_ADMIRE2:
					break;
				case TALK_CONSULT2:
					break;
				case TALK_CON_RES:
					break;
				case TALK_CON_MIL:
					break;
				case TALK_CON_TOWN:
					break;
				default:
			}
		}
	}

	public void clearConsole(){
		txtComment.setText("");
	}

	public boolean formArmy(Base b, UnitSwappable us){
		int x = b.getX();
		int y = b.getY();
		int old = gd.getArmy(x, y);

		if (b.getOwner() == PLAYER_OWNER){
			Army newArmy;
			//new or old army?
			if (old > -1){
				newArmy = gd.getArmy(old);
			}else{
				//Create a temporary emptied army
				newArmy = new Army(PLAYER_OWNER);
			}
			if (newArmy == null){
				return false;
			}
			//do the transfer
			showUnitTransfer(us, newArmy, PLAYER_OWNER);
			//new or old army?
			if (old <= -1){
				//now check if army is not empty?
				if (newArmy.getCount() > 0){
					//form it
					if (gd.addArmy(newArmy, x, y)){
						//MIN16
						mp.repaintClipCell(x, y);
						updateMiniMap();
						return true;
					}
				}
			}else{
				//MIN16
				mp.repaintClipCell(x, y);
				updateMiniMap();
				return true;
			}
		}else{
			//pick a military unit from site
			for (int i=0; i<b.getSiteCount(); i++){
				Site s = b.getSite(i);
				for (int j=0; j<s.getCount(); j++){
					Unit u = s.get(j);
					UnitType ut = uloader.getUnitType(u.getType());
					if (ut.isCombatable()){
						//new or old army?
						if (old > -1){
							Army a = gd.getArmy(old);
							if (a != null){
								a.transfer(s, j, uloader, hloader, tloader, oloader, teloader);
								//do we display this change?
								if (mp.isDisplayable(x, y)){
									//MIN16
									mp.repaintClipCell(x, y);
									updateMiniMap();
								}
								return true;
							}
						}else{
							//transfer to an empty army
							int index = gd.createArmy(b.getOwner(), x, y);
							Army a = gd.getArmy(index);
							if (a != null){
								a.transfer(s, j, uloader, hloader, tloader, oloader, teloader);
								//do we display this change?
								if (mp.isDisplayable(x, y)){
									//MIN16
									mp.repaintClipCell(x, y);
									updateMiniMap();
								}
								return true;
							}
						}
					}
				}
			}
			//pick a military unit from house
			for (int i=0; i<b.getHouseCount(); i++){
				House h = b.getHouse(i);
				for (int j=0; j<h.getCount(); j++){
					Unit u = h.get(j);
					UnitType ut = uloader.getUnitType(u.getType());
					if (ut.isCombatable()){
						//new or old army?
						if (old > -1){
							Army a = gd.getArmy(old);
							if (a != null){
								a.transfer(h, j, uloader, hloader, tloader, oloader, teloader);
								//do we display this change?
								if (mp.isDisplayable(x, y)){
									//MIN16
									mp.repaintClipCell(x, y);
									updateMiniMap();
								}
								return true;
							}
						}else{
							//transfer to an empty army
							int index = gd.createArmy(b.getOwner(), x, y);
							Army a = gd.getArmy(index);
							if (a != null){
								a.transfer(h, j, uloader, hloader, tloader, oloader, teloader);
								//do we display this change?
								if (mp.isDisplayable(x, y)){
									//MIN16
									mp.repaintClipCell(x, y);
									updateMiniMap();
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean buildable(int x, int y){
		if (gd.getBaseLand(x, y) != TOWN_LAND){
			return false;
		}
		//you cant build city so close each other
		for (int i=x-CITY_RANGE; i<=x+CITY_RANGE; i++){
			for (int j=y-CITY_RANGE; j<=y+CITY_RANGE; j++){
				if (gd.getBase(i, j) > -1){
					return false;
				}
			}
		}
		return true;
	}

	public boolean buildCity(Army a){
		int x = a.getX();
		int y = a.getY();

		//if (gd.getBase(x, y) > -1){
		//	return false;
		//}

		//you cant build city so close each other
		if (!buildable(x, y)){
			return false;
		}
		//required resources
		if (a.getResource(RESOURCE_TOOL) < RESOURCE_PIONEER) {
			return false;
		}

		int aOwner = a.getOwner();
		int defBase = bloader.getDefBaseType();
		Player aPlayer = ploader.getPlayer(aOwner);
		if (aPlayer != null) {
			defBase = bloader.getDefBaseType(aPlayer.getDefBaseType());
		}
		//create empty city
		if (gd.createBase(aOwner, defBase,
					x, y, -1, tloader, oloader,	hloader, uloader, bloader, teloader)){
			//clear any selection as things have changed
			clearSelection();
			//proceed
			Base base = gd.getBase(gd.getBase(x, y));
			//transfer a worker
			boolean transferWorker = false;
			for (int i=0; i<a.getCount() && !transferWorker; i++){
				Unit u = a.get(i);
				UnitType ut = uloader.getUnitType(u.getType());
				if (ut != null && ut.canWork()){
					if (base.transfer(a, tloader, oloader, hloader, uloader, teloader, i)){
						transferWorker = true;
					}
				}
			}
			if (!transferWorker){
				//transfer the first unit, do not care about error
				base.transfer(a, tloader, oloader, hloader, uloader, teloader, 0);
			}
			//remove if no people in army
			if (a.getCount() == 0){
				gd.removeArmy(gd.armyIndex(a));
			}
			//do we display this change?
			if (mp.isDisplayable(x, y)){
				mp.repaintClipCell(x, y);
			}
			return true;
		}else{
			//System.err.println("Create city failed");
			return false;
		}
	}

	public boolean transferResource(ResourceHolder res1, ResourceHolder res2, int index, int amount){
		int	res = res2.transfer(res1, index, amount);
		if (res > 0){
			if (panRight.isShowing() && 
				((res1 == selection && res1 instanceof Army) ||
				(res2 == selection && res2 instanceof Army))){
				panRight.repaint();
			}
			return true;
		}
		return false;
	}

	public boolean hurryBuilding(Base base){
		int order = base.getBuildOrder();
		if (order < 0) {
			if (base.getOwner() == PLAYER_OWNER) {
				printMessage("<%" + IMG_WARN + "%>Our town is currently has no building order!");
			}
			return false;
		}
		HouseType ht = hloader.getHouse(order);

		if (ht != null){
			int[] cost = ht.getCost();
			int hammer = base.getResource(RESOURCE_HAMMER);
			int need = cost[RESOURCE_HAMMER] - hammer;
			if (buyResource(base, RESOURCE_HAMMER, need)){
				return true;
			}
		}
		return false;
	}

	public boolean hurryTraining(Base base){
		int order = base.getTrainOrder();
		if (order < 0) {
			if (base.getOwner() == PLAYER_OWNER) {
				printMessage("<%" + IMG_WARN + "%>Our town is currently has no training order!");
			}
			return false;
		}
		UnitType ut = uloader.getCustom(base.getOwner(), order);

		if (ut != null){
			int[] cost = ut.getCost();
			int book = base.getResource(RESOURCE_BOOK);
			int need = cost[RESOURCE_BOOK] - book;
			if (buyResource(base, RESOURCE_BOOK, need)){
				return true;
			}
		}
		return false;
	}

	public boolean sellResource(ResourceHolder res, int index, int amount){
		if (index == RESOURCE_CURRENCY){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>Can not sell "+RESOURCE_NAME[RESOURCE_CURRENCY]+"!");
			return false;
		}

		if (res.getResource(index) < amount){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We do not have " + amount+ " " + RESOURCE_NAME[index] + " (Please try again holding SHIFT)");
			return false;
		}

		GameTrader trader = gd.getTrader();
		float price = getResourceCost(index, trader.getResource(index));

		int max = (int)(trader.getResource(RESOURCE_CURRENCY) / price);
		//get maximum we can get
		if (amount > max){
			amount = max;
		}
		//not enough false
		if (amount <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>Trader doesnt have enough " + RESOURCE_NAME[RESOURCE_CURRENCY]);
			return false;
		}

		int proposed = (int)(amount * price);
		if (proposed <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We would not gain any by selling " + amount + " " + RESOURCE_NAME[index] + "!");
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER),
			"Merchant can afford to buy " + amount + " " + RESOURCE_NAME[index] + " (offered at " + BasePanel.CURRENCY_FORMAT.format(price) + " " + RESOURCE_NAME[RESOURCE_CURRENCY] + " per " + RESOURCE_NAME[index] + ")\nAre you sure you want to proceed?");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}
		
		int	result = trader.transfer(res, index, amount);
		if (result > 0){
			int gain = (int)(result * price);
			int	res1 = res.transfer(trader, RESOURCE_CURRENCY, gain);

			if (panRight.isShowing() && (res == selection && res instanceof Army)){
				panRight.repaint();
			}
			if (panBot.isShowing() && (res == selection && res instanceof Base)){
				panBot.repaint();
			}
			return true;
		}else{
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, our inventory is full!");
			return false;
		}
	}

	public boolean sellResource(ResourceHolder res1, ResourceHolder res2,
		int index, int amount){
		if (index == RESOURCE_CURRENCY){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>Can not sell "+RESOURCE_NAME[RESOURCE_CURRENCY]+"!");
			return false;
		}

		if (res1.getResource(index) < amount){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We do not have " + amount+ " " + RESOURCE_NAME[index] + " (Please try again holding SHIFT)");
			return false;
		}

		float price = getResourceCost(index, res2.getResource(index));

		int proposed = (int)(amount * price);
		if (proposed <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We would not gain any by selling " +amount+ " " + RESOURCE_NAME[index]+"!");
			return false;
		}

		int max = (int)(res2.getResource(RESOURCE_CURRENCY) / price);
		//get maximum we can get
		if (amount > max){
			amount = max;
		}
		//not enough false
		if (amount <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>Buyer doesnt have enough " + RESOURCE_NAME[RESOURCE_CURRENCY]);
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER),
			"Are you sure you want to sell " + amount + " " + RESOURCE_NAME[index] + " (offered at " + BasePanel.CURRENCY_FORMAT.format(price) + " " + RESOURCE_NAME[RESOURCE_CURRENCY] + " per " + RESOURCE_NAME[index] + ")?");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}
		
		int	result = res2.transfer(res1, index, amount);
		if (result > 0){
			int gain = (int)(result * price);
			int	res = res1.transfer(res2, RESOURCE_CURRENCY, gain);

			if (panRight.isShowing() && (res1 == selection && res1 instanceof Army)){
				panRight.repaint();
			}
			if (panBot.isShowing() && (res1 == selection && res1 instanceof Base)){
				panBot.repaint();
			}
			return true;
		}else{
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, our inventory is full!");
			return false;
		}
	}

	public void doBribe(Army army){
		if (selection != null && selection instanceof Army){
			Army initiator = (Army)selection;
			if (army != null && army.getOwner() != initiator.getOwner()){
				if (sendBribe(initiator, army)) {
					printMessage("<%" + IMG_HELPER + "%>We have successfully bribed the enemy fleet!!!");
				}
				panRight.repaint();
				//playing animation
				//int ox = army.getX();
				//int oy = army.getY();
				//Point p = mp.getClipPosition(ox, oy);
				//if (p != null && mp.isDisplayable(ox, oy)){
				//	startSpriteEffect(IMG_BRIBE, p.x, p.y);
				//}
			}
		}else{
			printMessage("Please select one of our companies closed by, then [ALT] [LEFT CLICK] on the enemy fleet you want to bribe.");
		}
	}

	public boolean sendBribe(Army we, Army army){
		int costToBribe = 0;
		int loyaltyStrength = 0;
		for (int i=0; i<army.getCount(); i++) {
			Unit u = army.get(i);
			if (u != null) {
				costToBribe += (u.getLevel() + 1) * TRAIT_LOYALTY[u.getTrait()];
				loyaltyStrength += TRAIT_LOYALTY[u.getTrait()] + u.getLevel();
			}
		}
		//System.out.println(costToBribe);

		if (costToBribe > we.getResource(RESOURCE_CURRENCY)) {
			if (we.getOwner() == PLAYER_OWNER) {
				printMessage("<%" + IMG_WARN + "%>We do not have enough " + RESOURCE_NAME[RESOURCE_CURRENCY] + "!!! Please load more onto fleet inventory.");
			}
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER), "Attempt to bribe the enemy fleet will cost us " + costToBribe + " " + RESOURCE_NAME[RESOURCE_CURRENCY] + ".\nDo you want to continue?");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}

		costToBribe = we.reduce(RESOURCE_CURRENCY, costToBribe);
	
		int defendChance1 = (int)(Randomizer.getNextRandom() * (army.getResource(RESOURCE_HAPPY) + loyaltyStrength));
		int defendChance2 = (int)(Randomizer.getNextRandom() * army.getResource(RESOURCE_SECURITY));
		int bribeChance = (int)(Randomizer.getNextRandom() * costToBribe);
		if (bribeChance > defendChance1 && bribeChance > defendChance2) {
			army.changeOwner(we.getOwner());
			return true;
		}else{
			if (we.getOwner() == PLAYER_OWNER) {
				printMessage("<%" + IMG_WARN + "%>We have failed to bribe them!!! Looks like their loyalty is still high, maybe some spies could help in this situation.");
			}
			return false;
		}
	}

	public boolean sendCharity(Base base, int amount){
		if (base.getResource(RESOURCE_CURRENCY) < amount){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We do not have " + amount+ " " + RESOURCE_NAME[RESOURCE_CURRENCY] + " (Please try to sell some stocks to raise the fund)");
			return false;
		}

		float price = getResourceCost(RESOURCE_HAPPY, base.getResource(RESOURCE_HAPPY));

		int proposed = (int)(amount / price);
		if (proposed <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>The available fund is too small!");
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER), "Are you sure you want to give " + amount + " " + RESOURCE_NAME[RESOURCE_CURRENCY] + " to charity?");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}
		
		//MH111 reduce gold and increase happiness
		base.setResourceDebug(RESOURCE_CURRENCY, base.getResource(RESOURCE_CURRENCY) - amount);
		base.setResourceDebug(RESOURCE_HAPPY, base.getResource(RESOURCE_HAPPY) + proposed);

		return true;
	}

	public boolean setupCurfew(Base base, int amount){
		int tradeoff = (int)(amount / 4);
		if (base.getResource(RESOURCE_HAPPY) < tradeoff){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>The town people are too unhappy to obey orders!!! We need to increase the happiness level.");
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER), "Are you sure you want to setup curfew?\nThis order will increase town security but lower the happiness level.");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}
		
		//MH111 reduce happiness and increase security by quadruple
		base.setResourceDebug(RESOURCE_HAPPY, base.getResource(RESOURCE_HAPPY) - tradeoff);
		base.setResourceDebug(RESOURCE_SECURITY, base.getResource(RESOURCE_SECURITY) + amount);

		return true;
	}

	public void showUnitType(ResourceHolder res, Unit u, int o){
		dlgUnitType.showUnitType(u, res, o);
	}

	public boolean doAutoArrange(Base base){
		boolean result = false;

		for (int s=0; s<base.getSiteCount(); s++){
			Site st = base.getSite(s);
			for (int i=0; i<st.getCount(); i++) {
				Unit un = st.get(i);
				if (un != null) {
					UnitType ut = uloader.getUnitType(un.getType());
					if (ut != null) {
						int pref_h = ut.getPrefHouse();
						int pref_t = ut.getPrefTerrain();
						int pref_o = ut.getPrefOverlay();
						//Assign on rural areas
						if (pref_h < 0) {
							//nothing right now
						//Assign on urban areas
						}else{
							for (int h=0; h<base.getHouseCount(); h++){
								House hs = base.getHouse(h);
								int ht = hs.getType();
								if (ht == pref_h) {
									if (hs.transfer(st, i, uloader,	hloader, tloader, oloader, teloader)){
										result = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		for (int h=0; h<base.getHouseCount(); h++){
			House hs = base.getHouse(h);
			for (int i=0; i<hs.getCount(); i++) {
				Unit un = hs.get(i);
				if (un != null) {
					UnitType ut = uloader.getUnitType(un.getType());
					if (ut != null) {
						int pref_h = ut.getPrefHouse();
						int pref_t = ut.getPrefTerrain();
						int pref_o = ut.getPrefOverlay();
						//Assign on rural areas
						if (pref_h < 0) {
							for (int s=0; s<base.getSiteCount(); s++){
								Site st = base.getSite(s);
								//Worry about overlays
								if (pref_t == st.getBase()) {
									if (st.transfer(hs, i, uloader, hloader, tloader, oloader, teloader)){
										result = true;
										break;
									}
								}
							}
						//Assign on urban areas
						}else{
							if (pref_h != hs.getType()) {
								for (int h2=0; h2<base.getHouseCount(); h2++){
									if (h2 == h) {
										continue;
									}
									House hs2 = base.getHouse(h2);
									int ht2 = hs2.getType();
									if (ht2 == pref_h) {
										if (hs2.transfer(hs, i, uloader, hloader, tloader, oloader, teloader)){
											result = true;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public boolean doDemolition(Base base, House h){
		if (h.getCount() > 0){
			dlgYesNo.showMessage(getGameIcon(IMG_ADVISOR_BIG),
				"There are residents in the house, they will be forced to abandon our city.\nDo you want to proceed?");
			if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
				return false;
			}
		}else{
			dlgYesNo.showMessage(getGameIcon(IMG_ADVISOR_BIG),
				"Do you want to demolish this house?");
			if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
				return false;
			}
		}
		HouseType ht = hloader.getHouse(h.getType());
		if (!base.removeHouse(h)){
			JOptionPane.showMessageDialog(frame, "We can not demolish the founder house!",
				GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
			return false;
		}else{
			int[] cost = ht.getCost();
			//get half the amount
			for (int i=0; i<cost.length; i++){
				int quant = base.getResource(i) + cost[i] / 2;
				base.setResourceDebug(i, quant);
			}
			return true;
		}
	}

	public boolean doDismissal(UnitSwappable holder, int ind){
		if (ind < 0 || ind >= holder.getCount()){
			return false;
		}

		int lowerLimit = 0;
		if (holder instanceof Army) {
			lowerLimit = 1;
		}

		if (holder.getCount() > lowerLimit){
			Unit u = holder.get(ind);
			if (u != null){
				UnitType ut = uloader.getUnitType(u.getType());
				if (ut.getType() == UnitType.TYPE_KING){
					JOptionPane.showMessageDialog(frame, "You can not dismiss the top army commanders!", GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
					return false;
				}
				dlgYesNo.showMessage(getGameIcon(IMG_ADVISOR_BIG),
					"Do you want to dismiss this unit?");
				if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
					return false;
				}
				if (holder instanceof Army) {
					Army army = (Army)holder;
					int[] cost = ut.getCost();
					//get half the amount
					for (int i=0; i<cost.length; i++){
						int quant = army.getResource(i) + cost[i] / 2;
						army.setResourceDebug(i, quant);
					}
					army.remove(ind, uloader);
				}else{
					holder.remove(ind);
				}
				return true;
			}
		}
		return false;
	}

	public boolean doUpgrade(ResourceHolder res, Unit u, int owner, int selected){
		//you need the army inside a base to perform upgrade
		if (res instanceof Army){
			Army a = (Army)res;
			if (gd.getBase(a.getX(), a.getY()) == -1){
				return false;
			}
		}
		Tech tech = teloader.getTech(selected);
		//is the technology for upgrade?
		if (!tech.isUpgrade()){
			return false;
		}
		//check cost
		for (int i=0; i<RESOURCE_SIZE; i++){
			if (res.getResource(i) < tech.getResource(i)){
				return false;
			}
		}
		//check range requirements
		UnitType ut = uloader.getUnitType(u.getType());
		int crange = ut.getRange(u);
		if (ut == null || (tech.getRRange() == 0 && crange > 0) ||
			crange < tech.getRRange()){
			return false;
		}
		//reduce
		for (int i=0; i<RESOURCE_SIZE; i++){
			res.reduce(i, tech.getResource(i));
		}
		//do upgrade
		u.setUpgrade(selected);
		return true;
	}

	public boolean showUpgrade(ResourceHolder res, Unit u, int owner){
		if (gd.getAdvanceSize(owner) <= 0){
			if (owner == GameWorld.PLAYER_OWNER){
				printMessage("<%"+GameWorld.IMG_HELPER+"%>We do not have any advancements or upgrades yet.");
			}
			return false;
		}
		if (res instanceof Army){
			Army a = (Army)res;
			if (gd.getBase(a.getX(), a.getY()) == -1){
				if (owner == GameWorld.PLAYER_OWNER){
					printMessage("<%"+GameWorld.IMG_HELPER+"%>We need to move to the nearest base to be able to perform any upgrades.");
				}
				return false;
			}
		}
		dlgAdvance.showAdvances(gd, owner, true);
		//System.err.println(dlgAdvance.getValue());
		int selected = gd.getAdvance(owner, dlgAdvance.getUpgradeValue());
		//System.err.println(selected);
		if (selected > -1){
			Tech tech = teloader.getTech(selected);
			//is the technology for upgrade?
			if (!tech.isUpgrade()){
				//System.err.println("Non upgrade");
				return false;
			}
			String strCost = "";
			//check cost
			for (int i=0; i<RESOURCE_SIZE; i++){
				if (tech.getResource(i) > 0) {
					strCost += "\n" + Integer.toString(tech.getResource(i)) + " " + RESOURCE_NAME[i];
				}
			}
			dlgYesNo.showMessage(tech.getIcon(), "Do you want to perform this upgrade - " + tech.getName() + "?" + strCost);
			if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
				return false;
			}
			if (!doUpgrade(res, u, owner, selected)){
				printMessage("<%"+GameWorld.IMG_HELPER+"%>We can not finish this upgrade, please check the requirements. Try to load more resources onto the army's inventory and choose upgrade again.");
			}
			return true;
		}else{
			return false;
		}
	}

	public int showJobs(int p){
		dlgJob.showJobs(gd, p);
		return dlgJob.getValue();
	}

	public int showMission(int p){
		dlgMission.showJobs(gd, p);
		return dlgMission.getValue();
	}

	public Job getJob(int p, int i){
		return gd.getJob(p, i);
	}

	public Tech gwGetTech(int i){
		return teloader.getTech(i);
	}

	public int showYesNoDialog(int ico, String msg){
		dlgYesNo.showMessage(getGameIcon(ico), msg);
		return dlgYesNo.getValue();
	}

	public void doTechEffect(Unit u, int t){
		teloader.damage(u, t);
	}
	
	public boolean buyResource(ResourceHolder res, int index, int amount){
		//you cant buy money
		if (index == RESOURCE_CURRENCY){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, we can not trade "+
				RESOURCE_NAME[RESOURCE_CURRENCY]+"!");
			return false;
		}

		GameTrader trader = gd.getTrader();
		
		float price = getResourceCost(index, trader.getResource(index));

		int max = trader.getResource(index);

		//get maximum we can get
		if (amount > max){
			amount = max;
		}
		//not enough false
		if (amount <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, trader doesnt have enough resources!");
			return false;
		}

		int cost = (int)(amount * price);

		if (cost <= 0) {
			cost = 1;
			//printMessage("<%"+GameWorld.IMG_HELPER+"%>The amount you want to buy (" + amount + " " + GameWorld.RESOURCE_NAME[index] + ") does not make up to the minimum fund!");
			//return false;
		}

		if (cost > res.getResource(RESOURCE_CURRENCY)){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We do not have enough "+
				RESOURCE_NAME[RESOURCE_CURRENCY]+" to buy " +
				amount + " " + GameWorld.RESOURCE_NAME[index]+"!");
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER),
			amount + " " + RESOURCE_NAME[index] + " will cost " + cost + " " +
			RESOURCE_NAME[RESOURCE_CURRENCY] + ". Do you want to continue?");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}
		
		int	result = trader.transfer(res, RESOURCE_CURRENCY, cost);
		if (result > 0){
			int	res1 = res.transfer(trader, index, amount);

			if (panRight.isShowing() && (res == selection && res instanceof Army)){
				panRight.repaint();
			}
			if (panBot.isShowing() && (res == selection && res instanceof Base)){
				panBot.repaint();
			}
			return true;
		}else{
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, trader's inventory is full!");
			return false;
		}
	}

	//MIN41
	public boolean buyResource(ResourceHolder res1, ResourceHolder res2, 
		int index, int amount){
		//you cant buy money
		if (index == RESOURCE_CURRENCY){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, we can not trade "+
				RESOURCE_NAME[RESOURCE_CURRENCY]+"!");
			return false;
		}

		int max = res2.getResource(index);

		float price = getResourceCost(index, res2.getResource(index));

		//get maximum we can get
		if (amount > max){
			amount = max;
		}
		//not enough false
		if (amount <= 0){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, seller doesnt have enough resources!");
			return false;
		}

		int cost = (int)(amount * price);

		if (cost <= 0) {
			cost = 1;
			//printMessage("<%"+GameWorld.IMG_HELPER+"%>The amount you want (" + amount + " " + GameWorld.RESOURCE_NAME[index] + ") to buy does not make up to the minimum fund!");
			//return false;
		}

		if (cost > res1.getResource(RESOURCE_CURRENCY)){
			printMessage("<%"+GameWorld.IMG_HELPER+"%>We do not have enough "+
				RESOURCE_NAME[RESOURCE_CURRENCY]+" to buy " +
				amount + " " + GameWorld.RESOURCE_NAME[index]+"!");
			return false;
		}

		dlgYesNo.showMessage(getGameIcon(IMG_TRADER),
			amount + " " + RESOURCE_NAME[index] + " will cost " + cost + " " +
			RESOURCE_NAME[RESOURCE_CURRENCY] + ". Do you want to continue?");
		if (dlgYesNo.getValue() == YesNoDialog.STATE_NO){
			return false;
		}
		
		int	result = res2.transfer(res1, RESOURCE_CURRENCY, cost);
		if (result > 0){
			int	res = res1.transfer(res2, index, amount);

			if (panRight.isShowing() && (res1 == selection && res1 instanceof Army)){
				panRight.repaint();
			}
			if (panBot.isShowing() && (res1 == selection && res1 instanceof Base)){
				panBot.repaint();
			}
			return true;
		}else{
			printMessage("<%"+GameWorld.IMG_HELPER+"%>My lord, res2's inventory is full!");
			return false;
		}
	}

	public boolean autoLoadResource(Army a, int r){
		if (a == null){
			return false;
		}
		Base b = gd.getBase(gd.getBase(a.getX(), a.getY()));
		if (b == null){
			return false;
		}
		if (a.getResource(r) < RESOURCE_CRITICAL){
			return transferResource(b, a, r, RESOURCE_CRITICAL);
		}
		return true;
	}

	public boolean autoUnloadResource(Army a, int r){
		if (a == null){
			return false;
		}
		Base b = gd.getBase(gd.getBase(a.getX(), a.getY()));
		if (b == null){
			return false;
		}
		if (a.getResource(r) > RESOURCE_CRITICAL){
			return transferResource(a, b, r, a.getResource(r) - RESOURCE_CRITICAL);
		}
		return true;
	}

	//MH131
	public void updateMessagePanel(){
		//Clear
		txtMessage.setText("");
		//get messages
		for (int i=0; i<gd.getMessageCount(); i++) {
			msgOut.println(gd.getMessage(i));
			msgOut.flush();
		}
	}

	//MH131
	public void logMessage(String msg){
		gd.logMessage(msg);
		updateMessagePanel();
	}

	public boolean hasBuilder(Army a){
		for (int i=0; i<a.getCount(); i++){
			Unit u = a.get(i);
			UnitType ut = uloader.getUnitType(u.getType());
			if (ut != null && ut.canWork()){
				return true;
			}
		}
		return false;
	}

	public void startSpriteEffect(int f, int x, int y){
		//Now global in game world
		//SpriteEffect effector = new SpriteEffect(mp);

		//effLayer1.setReverse(true);
		//effLayer1.setLoops(2);
		effLayer1.setTimer(150);
		effLayer1.setSpriteSheet(getGameIcon(f));
		effLayer1.setTarget(x, y);

		boolean status = mp.getRunning();
		if (!status){
			mp.start();
		}
		effLayer1.start();
		while (effLayer1.isRunning()) {
			try{
				//System.err.println("WAITING SPRITE");
				Thread.sleep(100);
			}catch(Exception e){
			}
		}
		//System.err.println("ENDING SPRITE");
		effLayer1.stop();
		if (!status){
			mp.stop();
		}
		mp.repaint();
	}

	public void startSpriteEffect(int f, Object obj){
		if (obj instanceof Army) {
			Army army = (Army)obj;
			int x = army.getX();
			int y = army.getY();
			if (mp.isDisplayable(x, y)) {
				Point p = mp.getClipPosition(x, y);
				startSpriteEffect(f, p.x, p.y);
			}
		}else if (obj instanceof Base) {
			Base base = (Base)obj;
			int x = base.getX();
			int y = base.getY();
			if (mp.isDisplayable(x, y)) {
				Point p = mp.getClipPosition(x, y);
				startSpriteEffect(f, p.x, p.y);
			}
		}
	}

	public void startFireWork(){
		FireWorks effector = new FireWorks(mp);

		boolean status = mp.getRunning();
		if (!status){
			mp.start();
		}
		effector.start();
		try{
			Thread.sleep(8000);
		}catch(Exception e){
		}
		effector.stop();
		try{
			Thread.sleep(500);
		}catch(Exception e){
		}
		if (!status){
			mp.stop();
		}
		mp.repaint();
	}

	protected void rewardJob(ResourceHolder holder, Job job){
		if (holder instanceof Army || holder instanceof Base) {
			if (job.getReward() == Job.REWARD_RESOURCE) {
				int[] res = job.getRewardResource();
				for (int i=0; i<res.length; i++) {
					if (holder instanceof Army) {
						Army amy = (Army)holder;
						amy.setResourceDebug(i, amy.getResource(i) + res[i]);
					}else if (holder instanceof Base) {
						Base bs = (Base)holder;
						bs.setResourceDebug(i, bs.getResource(i) + res[i]);
					}
				}
			}else if (job.getReward() == Job.REWARD_UNIT) {
				boolean result = false;
				int t = job.getRewardValue();
				UnitType ut = uloader.getUnitType(t);
				if (ut != null) {
					Unit u = new Unit(ut, t);
					if (holder instanceof Army) {
						result = ((Army)holder).add(u, uloader);
					}else if (holder instanceof Base) {
						result = ((Base)holder).addUnit(u, tloader, oloader, hloader, uloader, teloader);
					}
				}
			}
		}
	}

	protected void executeJob(Army army, Job job){
		boolean dead = false;
		int dies = 0;
		int owner = army.getOwner();
		switch(job.getOrder()){
			//MH108
			//case Job.ORDER_ATTACK:
			//	dest = job.getDestination();
			//	moveArmyTo(army, dest.x, dest.y, false);
			//	break;
			//case Job.ORDER_PICKUP:
			//	dest = job.getDestination();
			//	moveArmyTo(army, dest.x, dest.y, false);
			//	break;
			case Job.ORDER_CUSTOM:
				int damage = (int)((job.getDifficulty()+1) * army.getCount() * Randomizer.getNextRandom() + 1);
				for (int i=0; i<damage; i++) {
					army.reorganize(uloader, true);
					Unit unit = army.get(0);
					if (unit.hurt()){
						dies++;
						army.remove(0, uloader);
					}
				}
				if (army.getCount()<=0) {
					getGD().removeArmy(getGD().armyIndex(army));
					dead = true;
				}
				break;
			default:
		}
		if (dead) {
			if (owner == PLAYER_OWNER) {
				printMessage("While carrying out a missing, your army has fallen!!!");
			}
		}else{
			boolean success = false;
			//Clear the mission
			army.setJob(null);
			//get the leader
			UnitType screamer = null;
			int leader = army.getLeader();
			if (leader > -1) {
				Unit uld = army.get(leader);
				if (uld != null) {
					screamer = uloader.getUnitType(uld.getType());
				}
			}else{
				Unit ufn = army.get(0);
				if (ufn != null) {
					screamer = uloader.getUnitType(ufn.getType());
				}
			}
			//Put them back where they were
			//1 Put them right back if no occupied
			//2 Transfer them into the occupier
			//3 Transfer them to city
			int cur = getGD().armyIndex(army);
			int cx = army.getX();
			int cy = army.getY();
			int ind = getGD().getArmy(cx, cy);
			if (ind < 0) {
				rewardJob(army, job);
				getGD().setArmy(cur, cx, cy);
				success = true;
			}else{
				Army their = getGD().getArmy(ind);
				if (their != null && their.getOwner() == owner) {
					//Transfer old stock
					for (int i=0; i<their.getResourceCount(); i++) {
						their.transfer(army, i, army.getResource(i));
					}
					//2 Transfer to occupier
					for (int i=0 ; i<army.getCount(); i++) {
						if (!their.transfer(army, uloader, i)) {
							break;
						}
					}
				}
				//3 Transfer to city
				if (army.getCount()>0) {
					int ind2 = getGD().getBase(cx, cy);
					if (ind2 > -1) {
						Base bs = getGD().getBase(ind2);
						if (bs != null && bs.getOwner() == owner) {
							//Transfer old stock
							for (int i=0; i<bs.getResourceCount(); i++) {
								bs.transfer(army, i, army.getResource(i));
							}
							for (int i=0 ; i<army.getCount(); i++) {
								if (!bs.transfer(army, tloader, oloader, hloader, uloader, teloader, i)) {
									break;
								}
							}
							success = true;
							rewardJob(bs, job);
						}
					}
				}else{
					success = true;
					rewardJob(their, job);
				}
			
				getGD().removeDebugArmy(cur);
			}
			if (owner == PLAYER_OWNER) {
				if (dies > 0) {
					printMessage(dies + " units in the group we sent on a quest have died!!!");
				}
				if (success) {
					if (screamer != null) {
						screamer.playSound(2);
					}
					printMessage("Our army has been back, the quest is a great success!!!");
					Point p = mp.getClipPosition(cx, cy);
					startSpriteEffect(IMG_TALKING001, p.x, p.y);
				}else{
					printMessage("The army that was sent on a quest has been lost!!!");
				}
			}
		}
	}

	protected void finalize() throws Throwable{
		msgOut.close();
	}

	public boolean doNewTurn(){
		//ok do new turn
		for (int i=0; i<gd.getArmySize(); i++){
			Army a = gd.getArmy(i);
			if (a != null){
				Job job = a.getJob();
				if (job != null){
					executeJob(a, job);
				}

				if (a.newTurn(uloader)){
					//MH131
					startSpriteEffect(IMG_HEALING, a);
				}//else{
					//MH131
					//startSpriteEffect(IMG_UNHAPPY, a);
				//}
				//MIN32 autoload for player if allowed
				//since only AI do this
				if (a.getOwner() == PLAYER_OWNER){
					if (a.getFlag(Army.MASK_LOAD_FOOD) == Army.MASK_LOAD_FOOD){
						autoLoadResource(a, RESOURCE_FOOD);
					}
					if (a.getFlag(Army.MASK_ALLOW_ENTERTAIN) == Army.MASK_ALLOW_ENTERTAIN){
						autoLoadResource(a, RESOURCE_HAPPY);
					}
					if (a.getFlag(Army.MASK_ALLOW_POLICE) == Army.MASK_ALLOW_POLICE){
						autoUnloadResource(a, RESOURCE_SECURITY);
					}
				}
			}
		}
		for (int i=0; i<gd.getBaseSize(); i++){
			Base b = gd.getBase(i);
			if (b != null){
				if (!b.newTurn(gd, tloader, oloader, uloader, hloader, bloader, teloader)){
					//MH131
					startSpriteEffect(IMG_UNHAPPY, b);
				}
			}
		}
		//research stuffs
		ArrayList non = new ArrayList();
		for (int i=0; i<OWNER_SIZE; i++){
			Player p = ploader.getPlayer(i);
			non.clear();
			for (int j=0; j<teloader.getBuildableSize(); j++){
				int index = teloader.getTechIndex(j);
				if (gd.advanceIndex(i, index) == -1){
					non.add(new Integer(index));
				}
			}
			//non available
			if (non.size() <= 0){
				continue;
			}
			//let you try by the number of your research base
			//to-do: make it active research base
			//System.out.println(i + " : " + non.size());
			int tries = 0;
			boolean upgraded = false;
			while (!upgraded && tries < p.getBaseCount() * 2){
				int chosen = (int)(Randomizer.getNextRandom() * non.size());
				int index = ((Integer)non.get(chosen)).intValue();
				//System.out.println(i + " " + index);
				Tech tech = teloader.getTech(index);
				boolean ok = true;
				//check cost
				for (int j=0; j<TECH_SIZE && ok; j++){
					if (gd.getTech(i, j) < tech.getCost(j)){
						ok = false;
					}
				}
				//check tech requirement
				int rt = tech.getRTech();
				int rp = tech.getRPlayer();
				//rt < 0: no tech required, else is there?
				if (ok && (rt < 0 || gd.advanceIndex(i, rt) != -1) &&
					(rp == -1 || rp == i)){
					//Reduce the cost
					for (int j=0; j<TECH_SIZE; j++){
						gd.setTech(i, j, gd.getTech(i, j) - tech.getCost(j));
					}
					if (gd.addAdvance(i, index)){
						if (i == PLAYER_OWNER){
							printMessage("<%"+GameWorld.IMG_IDEA+
								"%>Our scientist have finished a project called: " + 
								tech.getName() + ", " + tech.getDescription() + "!");
						}
						int newUnit = tech.getUUpgrade();
						if (newUnit > -1){
							if (uloader.addCustom(i, newUnit)){
								if (i == PLAYER_OWNER){
									printMessage("<%"+GameWorld.IMG_IDEA+
										"%>A new unit is now available!");
								}
							}
						}
						upgraded = true;
					}
				}
				tries++;
			}
			if (!upgraded && i == PLAYER_OWNER){
				printMessage("<%"+GameWorld.IMG_HELPER+
					"%>To increase our chance of research & discoveries, "+
					"we should build new cities and invest more on research.");
			}
		}
		//collecting statistics
		collectPlayerStatistics();

		boolean result = true;
		Player human = ploader.getPlayer(PLAYER_OWNER);
		if (human.getArmyCount() <= 0 && human.getBaseCount() <= 0){
			JOptionPane.showMessageDialog(frame, "You have lost the game! Better luck next time!", GAME_NAME, JOptionPane.INFORMATION_MESSAGE);
			gameOver();
			result = false;
		}else{
			//run scripts
			runScenarioScript();
			//increase counter
			gd.nextTurn();
			//show it
			showDay();
		}
		//MIN32 collect gabbage
		System.gc();
		//Return status
		return result;
	}

	protected void collectPlayerStatistics(){
		for (int p=0; p<OWNER_SIZE; p++){
			Player player = ploader.getPlayer(p);
			int acount = 0;
			for (int i=0; i<gd.getArmySize(); i++){
				Army a = gd.getArmy(i);
				// ******************************************
				// ARMY CAN BE NULL, MEANING A DEAD ARMY
				// ******************************************
				if (a != null && a.getOwner() == p){
					acount++;
				}
			}
			player.setArmyCount(acount);

			int bcount = 0;
			for (int i=0; i<gd.getBaseSize(); i++){
				Base b = gd.getBase(i);
				if (b != null && b.getOwner() == p){
					bcount++;
				}
			}
			player.setBaseCount(bcount);
		}
	}

	public Image getGameIcon(int i){
		return (Image)icons.get(i);
	}

	public int getGameIcons(){
		return icons.size();
	}

	public GameData getGD(){
		return gd;
	}

	public GameWorld(){
		frame = new JFrame(GAME_NAME);
		//Continue with frame essentials: icon, border, background
		frame.setIconImage(loadImage("icon.gif"));
		frame.setUndecorated(false);
		frame.setBackground(COL_GREEN_WHITE);
		//frame.setDefaultLookAndFeelDecorated(true);

		//MIN18
		ProgressMonitor progress = new ProgressMonitor(frame, "Loading " + GAME_NAME +
			"...", "Please wait while the game is unpacking, this may take a few seconds", 0, 10);
		progress.setProgress(0);
		progress.setMillisToPopup(0);

		icons = new ArrayList();
		//0
		icons.add(loadImage("selection.gif"));
		//1
		//icons.add(loadImage("resource.gif"));
		icons.add(loadImage("resource3.gif"));
		//2
		icons.add(loadImage("frame.gif"));
		//3
		icons.add(loadImage("advisorsmall.gif"));
		//4
		icons.add(loadImage("advisorbig.gif"));
		//5
		icons.add(loadImage("helper.gif"));
		//6
		icons.add(loadImage("frame3.gif"));
		//icons.add(loadImage("frame2.gif"));
		//7
		icons.add(loadImage("earth01.gif"));
		//8
		icons.add(loadImage("trader.gif"));
		//9
		icons.add(loadImage("techs2.gif"));
		//10
		icons.add(loadImage("hourglass.gif"));
		//11
		icons.add(loadImage("dollar.gif"));
		//12
		icons.add(loadImage("scroll.gif"));
		//13
		icons.add(loadImage("book.gif"));
		//14
		icons.add(loadImage("load.gif"));
		//15
		icons.add(loadImage("urban002.gif"));
		//16
		icons.add(loadImage("sign002.gif"));
		//17
		icons.add(loadImage("job001.gif"));
		//18
		icons.add(loadImage("job002.gif"));
		//19
		icons.add(loadImage("job003.gif"));
		//20
		icons.add(loadImage("stop.gif"));
		//21
		icons.add(loadImage("warning.gif"));
		//22
		icons.add(loadImage("defeat.gif"));
		//23
		icons.add(loadImage("waterdrop01.gif"));
		//24
		icons.add(loadImage("bribe.gif"));
		//25
		icons.add(loadImage("hit.gif"));
		//26
		icons.add(loadImage("unhappy.gif"));
		//27
		icons.add(loadImage("healing.gif"));

		//MIN18
		progress.setProgress(1);

		l = new TileLoader(this, 48, 48);
		//these objects use tile
		tloader = new TerrainLoader(l);
		oloader = new OverlayLoader(l);
		teloader = new TechLoader(this);
		uloader = new UnitTypeLoader(this, l, teloader);
		//these objects use possible custom image
		bloader = new BaseTypeLoader(this);
		hloader = new HouseTypeLoader(this);
		ploader = new PlayerLoader(this);
		//System.out.println(hloader.getSize());
		scenario = new Scenario("scenario.ini");

		//MIN18
		progress.setProgress(2);

		//Game data engine
		gd = new GameData(0, 0);

		//Main map
		mp = new MapPanel(15, 9, l, tloader, oloader, uloader, bloader, teloader, this);
		mp.setEnabled(false);

		//Special effect layers
		effLayer1 = new SpriteEffect(mp);

		//Mini map
		mnp = new MiniMapPanel(mp, 3, 2, tloader, this);
		mnp.setEnabled(false);
		mnp.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (mp.isBusy()){
					return;
				}
				Point p = e.getPoint();
				Point p1 = mnp.getPosition(p);
				mp.setViewCenter(p1.x, p1.y);
				updateMiniMap();
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});
		dlgMap = new JDialog(frame, "World Map");
		dlgMap.setDefaultLookAndFeelDecorated(false);
		dlgMap.setResizable(false);
		dlgMap.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dlgMap.getContentPane().setLayout(new BorderLayout());
		dlgMap.getContentPane().add(mnp, BorderLayout.CENTER);
		dlgMap.pack();
		dlgMap.setLocationRelativeTo(frame);

		mover = new GameMoveTimer(mp, uloader, tloader, oloader, teloader, 100, this);
		bombarder = new GameBombardTimer(mp, uloader, tloader, 200, this);
		turner = new NewTurnTimer(mp, scenario, uloader, hloader, tloader, oloader, ploader, teloader, mover, this);

		//MIN18
		progress.setProgress(3);

		singer = new SoundList();
		//This only works with normal directories
		//singer.loadGameSound();
		singer.add("fantasy8.mid");
		singer.add("fantasy7.mid");
		singer.add("gaia.mid");
		singer.add("zelda.mid");
		singer.add("romance.mid");
		singer.add("theme.mid");
		singer.add("silence.mid");
		singer.add("mountainking.mid");
		singer.add("celine01.mid");
		singer.add("celine02.mid");
		singer.add("celine03.mid");
		singer.add("sad.mid");
		singer.add("town.mid");
		singer.add("reunion.mid");
		singer.add("auria.mid");
		singer.add("Midi - Haiducii - Dragostea Din Tei (Remix).mid");
		singer.add("Pulp Fiction (midi).mid");
		singer.add("Midi - We are the champions.mid");
		singer.add("The Matrix.mid");
		singer.add("O-zone - Dragostea Din Tei  Haiducii midi.mid");
		singer.add("Donna Summer - I Feel Love.mid");
		singer.add("Toxic Midi.mid");
		singer.add("Final Fantasy X - Piano Theme(Midi).mid");
		singer.add("MIDI - My Immortal.mid");
		singer.add("The Beatles - Yesterday.mid");
		singer.add("3-12 Fiddle de Chocobo Midi.mid");

		//dialogs
		dlgUnitTransfer = new UnitTransferDialog(frame, uloader, hloader,
			tloader, oloader, teloader, 4, this);
		dlgInvReport = new InventoryReportDialog(frame);
		dlgResTransfer = new ResourceTransferDialog(frame, this, mp);
		dlgDip = new DiplomacyDialog(frame, this, ploader);
		dlgTalk = new TalkDialog(frame, this, TALK_LINE, TALK_ACCEPT);
		dlgTalk1 = new TalkDialog(frame, this, TALK_LINE1, TALK_WAR);
		dlgTalk2 = new TalkDialog(frame, this, TALK_LINE2, TALK_CON_RES);
		dlgUnitType = new UnitTypeDialog(frame, this, teloader, ploader, uloader);
		dlgYesNo = new YesNoDialog(frame);
		dlgResearch = new ResearchDialog(frame);
		dlgAdvance = new TechChooserDialog(frame, teloader, ploader);
		dlgScrollText = new ScrollingTextDialog(frame);
		dlgMapGen = new MapGenDialog(frame);
		dlgEnquiry = new EnquiryDialog(frame, this, uloader, ploader, tloader, oloader);
		dlgUnitEditor = new UnitEditorDialog(frame, this);
		dlgJob = new JobChooserDialog(frame, this);
		dlgMission = new CurrentJobDialog(frame, this);

		//MIN18
		progress.setProgress(4);

		mp.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (mp.isBusy() || !mp.isEnabled()){
					return;
				}

				Point p = e.getPoint();

				if (e.getButton() == MouseEvent.BUTTON1){
					Point p1 = mp.getMapCell(p.x, p.y);

					if (gd.getMask(p1.x, p1.y) == GameData.MASK_INVISIBLE){
						if (selection != null){
							printMessage("<%"+ IMG_STOP +"%>Please select a visible region!!!");
							clearSelection();
						}
						return;
					}

					int a = gd.getArmy(p1.x, p1.y);
					int b = gd.getBase(p1.x, p1.y);

					if (selection == null){
						if (e.isAltDown()) {
							dlgEnquiry.setData(p1.x, p1.y);
						}else{
							if (b != -1){
								Base base = gd.getBase(b);
								if (base.getOwner() != PLAYER_OWNER && !god){
									dlgEnquiry.setData(p1.x, p1.y);
									return;
								}
								selection = base;
								base.setSelection(true);
								printMessage("<%"+ IMG_HELPER +"%>Click on city view's panels to organize labour.");
								panBot.setBase(base);
								//MIN06
								mp.repaintClipCell(base.getX(), base.getY());
								//MH40
								//dlgAction.show();
								dlgBuilding.show();
							}else if (a != -1){
								Army army = gd.getArmy(a);
								if (army.getOwner() != PLAYER_OWNER && !god){
									dlgEnquiry.setData(p1.x, p1.y);
									return;
								}
								selection = army;
								army.setSelection(true);
								if (hasBuilder(army)){
									printMessage("<%"+ IMG_HELPER +
										"%>We can build a new town with the workers (Find a good area where there is no nearby town).");
								}//else{
									printMessage("<%"+ IMG_HELPER +
										"%>Click on map to set destination or engage combat."+
										" Hold [Shift] and click on enemy's armies to use bombard option."+
										" Hold [Alt] to view enemy's armies or town.");
								//}
								if (b != -1){
									panRight.setArmy(army, gd.getBase(b));
								}else{
									panRight.setArmy(army, null);
								}
								//MH150
								mp.setCombatHighlight(army.getX(), army.getY());
								//MIN06
								mp.setCursor(Cursor.getPredefinedCursor(
									Cursor.HAND_CURSOR));
								mp.repaintClipCell(army.getX(), army.getY());
								//MH40
								dlgAction.show();
							}else{
								dlgEnquiry.setData(p1.x, p1.y);
							}
						}
					}else{
						//MIN29
						if (e.isAltDown()){
							if (a != -1){
								Army army = gd.getArmy(a);
								if (army.getOwner() != PLAYER_OWNER){
									dlgEnquiry.setData(p1.x, p1.y);
									return;
								}
							}
							if (b != -1){
								Base base = gd.getBase(b);
								if (base.getOwner() != PLAYER_OWNER){
									dlgEnquiry.setData(p1.x, p1.y);
									return;
								}
							}
						}
						//MIN29
						if (selection instanceof Army){
							//mp.setEnabled(false);
							Army army = (Army)selection;
							//move & combat in one
							if (e.isShiftDown()){
								bombarder.setAction(army, p1.x, p1.y);
								bombarder.start();
							}else{
								//MH105 There is no movement points how can we move?
								if (army.getMove()>0) {
									mover.setAction(army, p1.x, p1.y);
									mover.start();
								}else{
									army.setDestination(p1.x, p1.y);
								}
							}
							//MIN30
							clearSelection();
							//while (mp.isBusy()){
							//	try{
							//		Thread.sleep(20);
							//	}catch(Exception ex){
							//	}
							//}
							//if (army.getMove() <= 0){
							//	clearSelection();
							//}else{
							//	panRight.refresh();
							//}
							//mp.setEnabled(true);
				//MH141
						}else if (b != -1){
							clearSelection();

							Base base = gd.getBase(b);
							if (base.getOwner() != PLAYER_OWNER && !god){
								dlgEnquiry.setData(p1.x, p1.y);
								return;
							}
							selection = base;
							base.setSelection(true);
							printMessage("<%"+ IMG_HELPER +"%>Click on city view's panels to organize labour.");
							panBot.setBase(base);
							//MIN06
							mp.repaintClipCell(base.getX(), base.getY());
							//MH40
							//dlgAction.show();
							dlgBuilding.show();
				//MH141
						}/*else if (selection instanceof Base){
							clearSelection();
							if (a != -1){
								Army army = gd.getArmy(a);
								if (army.getOwner() != PLAYER_OWNER && !god){
									dlgEnquiry.setData(p1.x, p1.y);
									return;
								}
								selection = army;
								army.setSelection(true);
								if (hasBuilder(army)){
									printMessage("<%"+ IMG_HELPER +
										"%>We can build a new town with the workers (Find a good area where there is no nearby town).");
								}//else{
									printMessage("<%"+ IMG_HELPER +
										"%>Click on map to set destination or engage combat."+
										" Hold [Shift] and click on enemy's armies to use bombard option."+
										" Hold [Alt] to view enemy's armies or town.");
								//}
								if (b != -1){
									panRight.setArmy(army, gd.getBase(b));
								}else{
									panRight.setArmy(army, null);
								}
								//MIN06, MIN16
								mp.setCursor(Cursor.getPredefinedCursor(
									Cursor.HAND_CURSOR));
								mp.repaintClipCell(army.getX(), army.getY());
								//MH40
								dlgAction.show();
							}
						}*/else{
							dlgEnquiry.setData(p1.x, p1.y);
						}
					}
				}else{
					Point p1 = mp.getMapCell(p.x, p.y);
					//MH131
					long nowTimeMs = System.currentTimeMillis();
					//System.err.println(nowTimeMs);
					//Intelligent pressing mouse indicate spaning not selection
					if (nowTimeMs-lastTimeMs > 500) {
					//MH131
						mp.setViewCenter(p1.x, p1.y);
						updateMiniMap();
						//mp.debugVar(p.x, p.y);
					//MH131
					}else if (selection != null && selection instanceof Army) {
						clearSelection();
					}else{
						int a = gd.getArmy(p1.x, p1.y);
						int b = gd.getBase(p1.x, p1.y);

						if (a != -1){
							Army army = gd.getArmy(a);
							if (army.getOwner() != PLAYER_OWNER && !god){
								mp.setViewCenter(p1.x, p1.y);
								updateMiniMap();
								return;
							}
							if (selection != null) {
								clearSelection();							
							}
							selection = army;
							army.setSelection(true);
							if (hasBuilder(army)){
								printMessage("<%"+ IMG_HELPER +
									"%>We can build a new town with the workers (Find a good area where there is no nearby town).");
							}//else{
								printMessage("<%"+ IMG_HELPER +
									"%>Click on map to set destination or engage combat."+
									" Hold [Shift] and click on enemy's armies to use bombard option."+
									" Hold [Alt] to view enemy's armies or town.");
							//}
							if (b != -1){
								panRight.setArmy(army, gd.getBase(b));
							}else{
								panRight.setArmy(army, null);
							}
							//MH150
							mp.setCombatHighlight(army.getX(), army.getY());
							//MIN06
							mp.setCursor(Cursor.getPredefinedCursor(
								Cursor.HAND_CURSOR));
							mp.repaintClipCell(army.getX(), army.getY());
							//MH40
							dlgAction.show();
						}else{
							mp.setViewCenter(p1.x, p1.y);
							updateMiniMap();
						}
					}
					//MH131
				}
			}
			public void mousePressed(MouseEvent e){
				//MH131
				if (e.getButton() == MouseEvent.BUTTON1){
				}else{
					lastTimeMs = System.currentTimeMillis();
					//System.err.println(lastTimeMs);
				}
				//MH131
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		//MIN18
		progress.setProgress(5);

		//JMenu
		mnuGenerate = new JMenuItem("New Game");
		mnuGenerate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
			ActionEvent.CTRL_MASK));
		mnuGenerate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				disable();
				mp.stop();

				clearSelection();
				clearConsole();

				dlgMapGen.showParameters();

				gd = new GameData(dlgMapGen.getWidth(), dlgMapGen.getHeight());
				gd.setMasking(true);
				gd.setTurn(0);
				gd.setDifficulty(dlgMapGen.getDifficulty());
				//System.out.println(gd.getDifficulty());
				gd.randomize(dlgMapGen.getRatio(), tloader, oloader);
////////////////////////////////////////////////////////////////////////////////////
				Point starting = null;
				ArrayList lands = gd.getLands();
				//MIN25
				int amount = PLAYABLE_OWNER_SIZE * PLAYABLE_OWNER_MAX + 1;
				if (amount >= lands.size()){
					amount = lands.size() - 1;
				}

				int land = 0;
				int ucount = 0;
				int bcount = 0;

				//MIN25
				for (int i=PLAYABLE_OWNER_MAX; i>=PLAYER_OWNER; i--){
					Player player = ploader.getPlayer(i);
					int baseType = bloader.getDefBaseType(player.getDefBaseType());

					if (teloader.getSize() > 0 && player.getTechSize() > 0){
						for (int j=0; j < player.getTechSize(); j++){
							gd.addAdvance(j, player.getTech(j));
						}
					}

					int count = 0;
					int loop = 0;
					while (count < amount && loop < 2){
						Point p = (Point)lands.get(land);
						if (gd.getArmy(p.x, p.y) == -1 && player.inArea(p) &&
								buildable(p.x, p.y)){
							gd.createArmy(i, p.x, p.y, uloader.getDefCombatUnit(), uloader);
							if (count == 0){
								gd.addDebugUnit(ucount, uloader.getDefKingUnit(), uloader);
							}
							ucount++;
							if (gd.getBase(p.x, p.y) == -1){
								gd.createBase(i, baseType, p.x, p.y, uloader.getDefWorkerUnit(),
									tloader, oloader, hloader, uloader, bloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, oloader, hloader, uloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, oloader, hloader,  uloader, teloader);
								if (i > PLAYER_OWNER){
									gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
										tloader, oloader, hloader,  uloader, teloader);
									gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
										tloader, oloader, hloader,  uloader, teloader);
									gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
										tloader, oloader, hloader,  uloader, teloader);
								}
								bcount++;
							}
							if (i == PLAYER_OWNER){
								starting = p;
							}
							count++;
						}
						if ((++land) >= lands.size()){
							land = 0;
							loop++;
						}
					}
					//MIN25
					amount -= PLAYABLE_OWNER_SIZE;
				}
//Generating some worker units around the maps
////////////////////////////////////////////////////////////////////////////////////
/*
				for (int i=0; i<lands.size(); i++){
					Point p = (Point)lands.get(i);
					if (gd.getArmy(p.x, p.y) == -1){
						int owner = (int)(OWNER_SIZE * Randomizer.getNextRandom());
						gd.createArmy(owner, (int)(uloader.getSize()*Randomizer.getNextRandom()),
							uloader);
						gd.setArmy(ucount, p.x, p.y);
						gd.addDebugUnit(ucount, (int)(uloader.getSize()*Randomizer.getNextRandom()),
							uloader, teloader);
						gd.addDebugUnit(ucount, (int)(uloader.getSize()*Randomizer.getNextRandom()),
							uloader, teloader);
						ucount++;
						if (gd.getBase(p.x, p.y) == -1){
							if (bcount < OWNER_SIZE * 3 && Randomizer.getNextRandom() > 0.8){
								//System.out.println("Creating base");
								gd.createBase(owner, (int)(bloader.getSize()*Randomizer.getNextRandom()), p.x, p.y,
									uloader.getDefWorkerUnit(), tloader, hloader, uloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, hloader, uloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, hloader,  uloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, hloader,  uloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, hloader,  uloader, teloader);
								gd.addDebugUnit(bcount, uloader.getDefWorkerUnit(),
									tloader, hloader,  uloader, teloader);
								bcount++;
							}
						}
					}
				}
*/
////////////////////////////////////////////////////////////////////////////////////
				gd.initData();
				gd.initFog();

				uloader.initCustom(teloader, ploader, gd);
				mp.reset();
				mnp.reset();
				dlgMap.pack();

				//refresh
				doNewTurn();

				mp.start();
				if (starting != null){
					mp.setViewCenter(starting.x, starting.y);
					updateMiniMap();
				}
				mnuEndTurn.setEnabled(true);
				mnuSave.setEnabled(true);
				mnuDipReport.setEnabled(true);
				mnuTech.setEnabled(true);
				mnuResearch.setEnabled(true);
				mnuTrader.setEnabled(true);
				mnuIntelligence.setEnabled(true);
				mnuNextArmy.setEnabled(true);
				mnuNextBase.setEnabled(true);
				mnuMap.setEnabled(true);
				mnuMask.setEnabled(true);
				mnuMask.setSelected(gd.getMasking());
				mnuGod.setEnabled(true);
				mnuGod.setSelected(god);
				mnuMission.setEnabled(true);
				btnEndTurn.setEnabled(true);
				btnMap.setEnabled(true);
				//MIN10
				enable();
				mp.setEnabled(true);
				showDay();
			}
		});

		mnuEndTurn = new JMenuItem("End Turn");
		mnuEndTurn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, 
			ActionEvent.CTRL_MASK));
		mnuEndTurn.setEnabled(false);
		mnuEndTurn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//to-do: clear selection
				clearSelection();
				turner.start();
			}
		});

		mnuSave = new JMenuItem("Save Game");
		mnuSave.setEnabled(false);
		mnuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
			ActionEvent.CTRL_MASK));
		mnuSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clearSelection();
				disable();
				mp.stop();
				try{
					String savePath = "saves/game.sav";

					int returnVal = jfcGame.showSaveDialog(frame);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File test = jfcGame.getSelectedFile();
						savePath = test.getPath();
						if (!savePath.endsWith(".sav")) {
							savePath += ".sav";
						}
					}else{
						return;
					}
					//FileOutputStream ostream = new FileOutputStream("saves/game.sav");
					JarOutputStream ostream = new JarOutputStream(new FileOutputStream(savePath));
					ostream.putNextEntry(new ZipEntry("world"));
					//MIN17
					ObjectOutputStream p = new ObjectOutputStream(ostream);
					p.writeObject(gd);
					p.flush();
					ostream.close();
 				}catch(Exception x){
					//System.err.println(x);
				}
				mp.start();
				enable();
				JOptionPane.showMessageDialog(frame, "Game has been successfully saved!");
			}
		});

		mnuLoad = new JMenuItem("Load Game");
		mnuLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 
			ActionEvent.CTRL_MASK));
		mnuLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//to-do: clear selection
				clearSelection();
				clearConsole();
				disable();
				mp.stop();
				try{
					String savePath = "saves/game.sav";

					int returnVal = jfcGame.showOpenDialog(frame);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File test = jfcGame.getSelectedFile();
						if (test.exists()) {
							savePath = test.getPath();
						}else{
							printMessage("<%"+ IMG_WARN +"%>Error: Saved game not found");
							mnuGame.setEnabled(true);
							return;
						}
					}else{
						mnuGame.setEnabled(true);
						return;
					}
					//FileInputStream istream = new FileInputStream("saves/game.sav");
					JarInputStream istream = new JarInputStream(new FileInputStream(savePath));
					ZipEntry zip = istream.getNextEntry();
					//MIN17
					ObjectInputStream p = new ObjectInputStream(istream);
					GameData tmp = (GameData)p.readObject();
					gd = tmp;
					//relink
					gd.initFog();
					uloader.initCustom(teloader, ploader, gd);
					mp.reset();
					mnp.reset();
					dlgMap.pack();

					collectPlayerStatistics();
					//MH131
					updateMessagePanel();

					istream.close();
 				}catch(Exception x){
					//System.err.println(x);
					//System.exit(-1);
					printMessage("<%"+ IMG_WARN +"%>Error: Saved game not found or different version and can not be loaded");
					mnuGame.setEnabled(true);
					return;
				}
				//find next base
				findNextBase();
				//drawings
				mp.start();
				mnuEndTurn.setEnabled(true);
				mnuSave.setEnabled(true);
				mnuDipReport.setEnabled(true);
				mnuTech.setEnabled(true);
				mnuResearch.setEnabled(true);
				mnuTrader.setEnabled(true);
				mnuIntelligence.setEnabled(true);
				mnuNextArmy.setEnabled(true);
				mnuNextBase.setEnabled(true);
				mnuMap.setEnabled(true);
				mnuMask.setEnabled(true);
				mnuMask.setSelected(gd.getMasking());
				mnuGod.setEnabled(true);
				mnuGod.setSelected(god);
				mnuMission.setEnabled(true);
				btnEndTurn.setEnabled(true);
				btnMap.setEnabled(true);
				//MIN10
				enable();
				mp.setEnabled(true);
				showDay();
				JOptionPane.showMessageDialog(frame, "Game has been successfully loaded!");
			}
		});

		mnuDipReport = new JMenuItem("Diplomacy");
		mnuDipReport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
			ActionEvent.CTRL_MASK));
		mnuDipReport.setEnabled(false);
		mnuDipReport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dlgDip.show();
			}
		});

		mnuResearch = new JMenuItem("Research");
		mnuResearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 
			ActionEvent.CTRL_MASK));
		mnuResearch.setEnabled(false);
		mnuResearch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showResearch(PLAYER_OWNER);
			}
		});

		mnuTrader = new JMenuItem("Trader Inventory");
		mnuTrader.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 
			ActionEvent.CTRL_MASK));
		mnuTrader.setEnabled(false);
		mnuTrader.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showTraderInventory();
			}
		});

		mnuIntelligence = new JMenuItem("Intelligence");
		mnuIntelligence.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, 
			ActionEvent.CTRL_MASK));
		mnuIntelligence.setEnabled(false);
		mnuIntelligence.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showIntelligence(PLAYER_OWNER);
			}
		});

		mnuTech = new JMenuItem("Discoveries & Advancements");
		mnuTech.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 
			ActionEvent.CTRL_MASK));
		mnuTech.setEnabled(false);
		mnuTech.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dlgAdvance.showAdvances(gd, PLAYER_OWNER, false);
			}
		});

		mnuMission = new JMenuItem("Current Quests");
		mnuMission.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, 
			ActionEvent.CTRL_MASK));
		mnuMission.setEnabled(false);
		mnuMission.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showMission(PLAYER_OWNER);
			}
		});

		mnuNextArmy = new JMenuItem("Next Army");
		mnuNextArmy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 
			ActionEvent.CTRL_MASK));
		mnuNextArmy.setEnabled(false);
		mnuNextArmy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				findNextArmy();
			}
		});

		mnuNextBase = new JMenuItem("Next Town");
		mnuNextBase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, 
			ActionEvent.CTRL_MASK));
		mnuNextBase.setEnabled(false);
		mnuNextBase.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				findNextBase();
			}
		});

		mnuMap = new JMenuItem("World Map");
		mnuMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, 
			ActionEvent.CTRL_MASK));
		mnuMap.setEnabled(false);
		mnuMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dlgMap.show();
			}
		});

		mnuAbout = new JMenuItem(GAME_NAME + "'s Help");
		mnuAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, 
			ActionEvent.CTRL_MASK));
		mnuAbout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAbout();
			}
		});

		mnuMask = new JCheckBoxMenuItem("Fog of War");
		mnuMask.setEnabled(false);
		mnuMask.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				gd.setMasking(!gd.getMasking());
				mp.repaint();
				if (dlgMap.isShowing()){
					mnp.repaint();
				}
			}
		});

		mnuGod = new JCheckBoxMenuItem("God Mode");
		mnuGod.setEnabled(false);
		mnuGod.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				god = !god;
			}
		});

		mnuSound = new JCheckBoxMenuItem("Play Music");
		mnuSound.setSelected(true);
		mnuSound.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (mnuSound.isSelected()){
					singer.start();
				}else{
					singer.stop();
				}
			}
		});

		mnuSizeLarge = new JMenuItem("Large Screen");
		mnuSizeLarge.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mnuSizeLarge.setEnabled(false);
				mnuSizeSmall.setEnabled(true);

				mp.setNewSize(17,11);
				frame.hide();
				frame.pack();
				frame.show();
				if (mnp.isShowing()){
					mnp.repaint();
				}
				//mp.repaint();
			}
		});

		mnuSizeSmall = new JMenuItem("Small Screen");
		mnuSizeSmall.setEnabled(false);
		mnuSizeSmall.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mnuSizeSmall.setEnabled(false);
				mnuSizeLarge.setEnabled(true);

				mp.setNewSize(15,9);
				frame.hide();
				frame.pack();
				frame.show();
				if (mnp.isShowing()){
					mnp.repaint();
				}
				//mp.repaint();
			}
		});

		mnuUnitEditor = new JMenuItem("Unit Editor");
		mnuUnitEditor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dlgUnitEditor.showEditor();
			}
		});

		mnuGame = new JMenu("Game");
		mnuGame.add(mnuGenerate);
		mnuGame.addSeparator();
		mnuGame.add(mnuLoad);
		mnuGame.add(mnuSave);
		mnuGame.addSeparator();
		mnuGame.add(mnuEndTurn);

		mnuDip = new JMenu("Reports");
		mnuDip.add(mnuDipReport);
		mnuDip.add(mnuIntelligence);
		mnuDip.add(mnuResearch);
		mnuDip.add(mnuTech);
		mnuDip.add(mnuTrader);
		mnuDip.add(mnuMission);
		mnuDip.addSeparator();
		mnuDip.add(mnuNextArmy);
		mnuDip.add(mnuNextBase);
		mnuDip.add(mnuMap);

		mnuOption = new JMenu("Options");
		mnuOption.add(mnuMask);
		mnuOption.add(mnuGod);
		mnuOption.addSeparator();
		mnuOption.add(mnuSound);
		mnuOption.add(mnuSizeLarge);
		mnuOption.add(mnuSizeSmall);

		mnuHelp = new JMenu("Help");
		mnuHelp.add(mnuAbout);

		mnuTool = new JMenu("Tool");
		mnuTool.add(mnuUnitEditor);

		mnuBar = new JMenuBar();
		mnuBar.add(mnuGame);
		mnuBar.add(mnuDip);
		mnuBar.add(mnuOption);
		mnuBar.add(mnuTool);
		mnuBar.add(mnuHelp);

		//MIN18
		progress.setProgress(6);

		panRight = new UnitPanel(mp, tloader, oloader, hloader, uloader, ploader, teloader, this);
		panRight.setBackground(frame.getContentPane().getBackground());

		txtComment = new JTextPane();
		txtComment.setBackground(frame.getContentPane().getBackground());
		txtComment.setEnabled(false);
		//MH40
		//ListScrollPane scrPane = new ListScrollPane(txtComment, 160, 160);
		//MH40
		ListScrollPane scrPane = new ListScrollPane(txtComment, 240, 240);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		//MH131
		txtMessage = new JTextPane();
		txtMessage.setBackground(frame.getContentPane().getBackground());
		txtMessage.setEnabled(false);

		ListScrollPane scrPane2 = new ListScrollPane(txtMessage, 240, 160);
		scrPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		panBot = new BasePanel(this, mp, uloader, tloader, hloader, bloader);

		panBut = new JPanel();
		panBut.setLayout(new GridLayout(2, 3));


		btnMoveAll = new JButton("",new ImageIcon(loadImage("btn003.gif")));
		btnMoveAll.setToolTipText("Move all your armies to preset destination");
		btnMoveAll.setEnabled(false);
		btnMoveAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveAllArmy(PLAYER_OWNER);
			}
		});
		panBut.add(btnMoveAll);

		btnNextArmy = new JButton("",new ImageIcon(loadImage("btn001.gif")));
		btnNextArmy.setToolTipText("Switch to next available army");
		btnNextArmy.setEnabled(false);
		btnNextArmy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				findNextArmy();
			}
		});
		panBut.add(btnNextArmy);

		btnNextTown = new JButton("",new ImageIcon(loadImage("btn002.gif")));
		btnNextTown.setToolTipText("Switch to next town");
		btnNextTown.setEnabled(false);
		btnNextTown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				findNextBase();
			}
		});
		panBut.add(btnNextTown);

		btnClear = new JButton("Clear logs");
		btnClear.setEnabled(false);
		btnClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				txtComment.setText("");
			}
		});
		panBut.add(btnClear);

		btnMap = new JButton("World Map");
		btnMap.setEnabled(false);
		btnMap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dlgMap.show();
			}
		});
		panBut.add(btnMap);

		btnEndTurn = new JButton("End Turn");
		btnEndTurn.setEnabled(false);
		btnEndTurn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clearSelection();
				turner.start();
			}
		});
		panBut.add(btnEndTurn);

		//MIN18
		progress.setProgress(7);

		//panCommand = new Panel(new BorderLayout());

		//panCommand.add(new DummyPanel(), BorderLayout.NORTH);
		//panCommand.add(btnNextArmy, BorderLayout.WEST);
		//panCommand.add(btnNextTown, BorderLayout.EAST);

		//MIN18
		progress.setProgress(8);

		jfcGame = new JFileChooser();
		javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter(){
			public boolean accept(File pathname){
				if (pathname.isDirectory()) {
					return true;
				}else if (pathname.isFile()) {
					String name = pathname.getName();
					if (name.endsWith(".sav")){
						return true;
					}else{
						return false;
					}
				}else if (pathname.isHidden()) {
					return false;
				}else{
					return false;
				}
			}
			public String getDescription(){
				return GAME_NAME + " saved games (*.sav)";
			}
		};
		jfcGame.setFileFilter(filter);
		jfcGame.setCurrentDirectory(new File("saves"));
		//MH40
		//JPanel panTemp3 = new JPanel(new BorderLayout());
		//panTemp3.add(panRight, BorderLayout.CENTER);
		//panTemp3.add(panCommand, BorderLayout.SOUTH);

		//JPanel panTemp = new JPanel();
		//panTemp.setLayout(new BorderLayout());
		//panTemp.add(scrPane, BorderLayout.NORTH);
		//panTemp.add(panBut, BorderLayout.SOUTH);
		//MH40
		
		//MH40
		//JPanel panTemp2 = new JPanel();
		//panTemp2.setLayout(new BorderLayout());
		//panTemp2.add(panBot, BorderLayout.WEST);
		//panTemp2.add(new DummyPanel(), BorderLayout.CENTER);
		//panTemp2.add(panTemp, BorderLayout.EAST);
		//panTemp2.add(panBut, BorderLayout.EAST);
		//panTemp2.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
		//MH40
		
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setJMenuBar(mnuBar);
		frame.addWindowListener(new WindowListener(){
			public void windowOpened(WindowEvent e){
			}
			public void windowClosing(WindowEvent e){
				dlgYesNo.showMessage(getGameIcon(IMG_ADVISOR_BIG), "Are you sure you want to quit?");
				if (dlgYesNo.getValue() == YesNoDialog.STATE_YES){
					singer.stop();
					dlgMap.hide();
					dlgUnitTransfer.hide();
					//try{
					//	Thread.sleep(1000);
					//}catch(Exception x){}
					System.exit(0);
				}
			}
			public void windowClosed(WindowEvent e){
			}
			public void windowIconified(WindowEvent e){
			}
			public void windowDeiconified(WindowEvent e){
			}
			public void windowActivated(WindowEvent e){
				//mp.repaint();
			}
			public void windowDeactivated(WindowEvent e){
			}
		});

		frame.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent e){
			}
			public void componentMoved(ComponentEvent e){
				Point mainPnt = frame.getLocation();
				Rectangle mainRec = frame.getBounds();
				dlgComment.setLocation(mainPnt.x + mainRec.width, mainPnt.y);
				dlgBuilding.setLocation(mainPnt.x, mainPnt.y + mainRec.height);
				dlgAction.setLocation(mainPnt.x, mainPnt.y + mainRec.height);
				//MH107
				Rectangle secRec = dlgComment.getBounds();
				dlgMap.setLocation(secRec.x, secRec.y + secRec.height);
				//MH107
			}
			public void componentResized(ComponentEvent e){
				if (!frame.isShowing()) {
					return;
				}
				Point mainPnt = frame.getLocation();
				Rectangle mainRec = frame.getBounds();
				dlgComment.setLocation(mainPnt.x + mainRec.width, mainPnt.y);
				dlgBuilding.setLocation(mainPnt.x, mainPnt.y + mainRec.height);
				dlgAction.setLocation(mainPnt.x, mainPnt.y + mainRec.height);
				//MH107
				Rectangle secRec = dlgComment.getBounds();
				dlgMap.setLocation(secRec.x, secRec.y + secRec.height);
				//MH107
			}
			public void componentShown(ComponentEvent e){
			}
		});

		//GridBagLayout gridbag = new GridBagLayout();
        //GridBagConstraints c = new GridBagConstraints();
		//c.gridwidth = 1;
		//gridbag.setConstraints(mp, c);
		//c.gridwidth = GridBagConstraints.REMAINDER;
		//gridbag.setConstraints(panRight, c);
		//c.gridwidth = 1;
		//gridbag.setConstraints(panTemp, c);
		//c.gridwidth = GridBagConstraints.REMAINDER;
		//gridbag.setConstraints(panBut, c);
		//frame.getContentPane().setLayout(gridbag);

		//MIN18
		progress.setProgress(9);

		//MH40
		//JPanel panTemp4 = new JPanel();
		//panTemp4.setLayout(new BorderLayout());
		//panTemp4.add(panRight, BorderLayout.NORTH);
		//panTemp4.add(panBot, BorderLayout.CENTER);

		//MH40
		//JPanel panTemp5 = new JPanel();
		//panTemp5.setLayout(new BorderLayout());
		//panTemp5.add(panTemp4, BorderLayout.CENTER);
		//panTemp5.add(panBut, BorderLayout.EAST);
		//MH40

		frame.getRootPane().setDefaultButton(btnEndTurn);
		frame.getContentPane().setLayout(new BorderLayout());
		//frame.getContentPane().add(panTemp2, BorderLayout.EAST);
		//frame.getContentPane().add(panTemp3, BorderLayout.SOUTH);
		//frame.getContentPane().add(panTemp5, BorderLayout.SOUTH);
		//frame.getContentPane().add(panTemp4, BorderLayout.SOUTH);
		//frame.getContentPane().add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
		frame.getContentPane().add(mp, BorderLayout.CENTER);
		//frame.getContentPane().add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.CENTER);

		frame.pack();
		Point mainPnt = frame.getLocation();
		Rectangle mainRec = frame.getBounds();
		//frame.setLocationRelativeTo(null);

		//MH40
		dlgComment = new JDialog(frame, "Events", false);
		//MH107
		dlgComment.setDefaultLookAndFeelDecorated(false);
		dlgComment.setResizable(false);
		//MH107
		dlgComment.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dlgComment.getContentPane().setBackground(frame.getContentPane().getBackground());
		dlgComment.getContentPane().setLayout(new BorderLayout());
		dlgComment.getContentPane().add(scrPane2, BorderLayout.NORTH);
		dlgComment.getContentPane().add(scrPane, BorderLayout.CENTER);
		dlgComment.getContentPane().add(panBut, BorderLayout.SOUTH);
		//Top right corner
		dlgComment.setLocation(mainPnt.x + mainRec.width, mainPnt.y);
		dlgComment.pack();
		dlgComment.show();

		//MH107
		Rectangle secRec = dlgComment.getBounds();
		dlgMap.setLocation(secRec.x, secRec.y + secRec.height);
		//MH107

		dlgBuilding = new JDialog(frame, "Base view", false);
		//MH107
		dlgBuilding.setDefaultLookAndFeelDecorated(false);
		dlgBuilding.setResizable(false);
		//MH107
		dlgBuilding.getContentPane().setBackground(frame.getContentPane().getBackground());
		dlgBuilding.getContentPane().add(panBot);
		dlgBuilding.setLocation(mainPnt.x, mainPnt.y + mainRec.height);
		dlgBuilding.pack();

		dlgAction = new JDialog(frame, "Fleet view", false);
		//MH107
		dlgAction.setDefaultLookAndFeelDecorated(false);
		dlgAction.setResizable(false);
		//MH107
		dlgAction.getContentPane().setBackground(frame.getContentPane().getBackground());
		dlgAction.getContentPane().add(panRight);
		//dlgAction.getContentPane().setLayout(new BorderLayout());
		//dlgAction.getContentPane().add(panRight, BorderLayout.NORTH);
		//dlgAction.getContentPane().add(panBot, BorderLayout.CENTER);
		//Hide the titlebar and exit button
		//dlgAction.setUndecorated(true);
		//Bottom left corner
		dlgAction.setLocation(mainPnt.x, mainPnt.y + mainRec.height);
		//panRight.setVisible(false);
		dlgAction.pack();
		//panRight.setVisible(false);
		//panBot.setVisible(false);
		//dlgAction.show();
		//MH40

		//MIN18
		progress.setProgress(10);

		//sing
		singer.start();

		//about
		//MH34
		//showAbout();

		//redirecting
		System.setOut(new PrintStream(new ConsoleOutStream(this, txtComment, Color.BLUE), true));
		//Setup nice text coloring
		msgOut = new PrintStream(new ConsoleOutStream(this, txtMessage, Color.BLUE), true);

		//MIN18
		progress.close();

		frame.show();
	}

	protected void moveAllArmy(int p){
		//start from the next available
		for (int i=0; i<gd.getArmySize(); i++){
			Army a = gd.getArmy(i);
			if (a != null && a.getOwner() == p && a.getMove() > 0){
				Point p1 = a.getPosition();
				Point p2 = a.getDestination();

				if (p1.x != p2.x || p1.y != p2.y) {
					mover.setAction(a, p2.x, p2.y);
					mover.start();
					//The timer already started
					return;
					//while (mp.isBusy()){
					//	try{
							//System.out.println("Sleep 2");
					//		Thread.sleep(20);
					//	}catch(Exception e){
					//	}
					//}
				}
			}
		}
	}

	protected void findNextArmy(){
		int start = -1;
		if (selection instanceof Army){
			start = gd.armyIndex((Army)selection);
		}
		//find it
		Army next = null;
		//start from the next available
		for (int i=start+1; i<gd.getArmySize(); i++){
			Army a = gd.getArmy(i);
			if (a != null && a.getOwner() == PLAYER_OWNER){
				next = a;
				break;
			}
		}
		//start from beginning
		if (next == null){
			for (int i=0; i<start; i++){
				Army a = gd.getArmy(i);
				if (a != null && a.getOwner() == PLAYER_OWNER){
					next = a;
					break;
				}
			}
		}
		//anything found?
		if (next != null){
			if (selection != null){
				clearSelection();
			}
			selection = next;
			next.setSelection(true);
			//MH150
			mp.setCombatHighlight(next.getX(), next.getY());
			//MH150
			mp.setViewCenter(next.getX(), next.getY());
			int b = gd.getBase(next.getX(), next.getY());
			if (b != -1){
				panRight.setArmy(next, gd.getBase(b));
			}else{
				panRight.setArmy(next, null);
			}
			updateMiniMap();
			mp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			//MIN28
			if (hasBuilder(next)){
				printMessage("<%"+ IMG_HELPER +
					"%>We can build a new town with the workers (Find a good area where there is no nearby town).");
			}//else{
				printMessage("<%"+ IMG_HELPER +
					"%>Click on map to set destination or engage combat."+
					" Hold [Shift] and click on enemy's armies to use bombard option."+
					" Hold [Alt] to view enemy's armies or town.");
			//}
			//MH40
			dlgAction.show();
		}
	}

	protected void findNextBase(){
		int start = -1;
		if (selection instanceof Base){
			start = gd.baseIndex((Base)selection);
		}
		//find it
		Base next = null;
		//start from the next available
		for (int i=start+1; i<gd.getBaseSize(); i++){
			Base a = gd.getBase(i);
			if (a != null && a.getOwner() == PLAYER_OWNER){
				next = a;
				break;
			}
		}
		//start from beginning
		if (next == null){
			for (int i=0; i<start; i++){
				Base a = gd.getBase(i);
				if (a != null && a.getOwner() == PLAYER_OWNER){
					next = a;
					break;
				}
			}
		}
		//anything found?
		if (next != null){
			if (selection != null){
				clearSelection();
			}
			selection = next;
			next.setSelection(true);
			mp.setViewCenter(next.getX(), next.getY());
			panBot.setBase(next);
			updateMiniMap();
			//MH40
			//dlgAction.show();
			dlgBuilding.show();
		}
	}

	protected Base findNextBase(int owner){
		int start = -1;
		if (selection instanceof Base){
			start = gd.baseIndex((Base)selection);
		}
		//find it
		Base next = null;
		//start from the next available
		for (int i=start+1; i<gd.getBaseSize(); i++){
			Base a = gd.getBase(i);
			if (a != null && a.getOwner() == owner){
				next = a;
				break;
			}
		}
		//start from beginning
		if (next == null){
			for (int i=0; i<start; i++){
				Base a = gd.getBase(i);
				if (a != null && a.getOwner() == owner){
					next = a;
					break;
				}
			}
		}
		//anything found?
		return next;
	}

	public void runScenarioScript(){
		for (Enumeration e = scenario.getKeys(); e.hasMoreElements(); ){
			String key = (String)e.nextElement();
			String val = scenario.getProperty(key);
			//System.out.println(key);
			ArrayList tmp = new ArrayList();
			StringTokenizer tok = new StringTokenizer(val, ",");
			while (tok.hasMoreTokens()){
				tmp.add(tok.nextToken());
			}
			if (key.startsWith("spawn")){
				//from,to,player,x1,y1,x2,y2,type,number
				if (tmp.size() >= 9){
				try{
					int frTurn = Integer.parseInt((String)tmp.get(0));
					int toTurn = Integer.parseInt((String)tmp.get(1));
					if (frTurn <= gd.getTurn() && toTurn >= gd.getTurn()){
						//System.out.println("Spawning");
						int p = Integer.parseInt((String)tmp.get(2));
						if (p < 0 || p > OWNER_MAX){
							continue;
						}
						int x1 = Integer.parseInt((String)tmp.get(3));
						int y1 = Integer.parseInt((String)tmp.get(4));
						int x2 = Integer.parseInt((String)tmp.get(5));
						int y2 = Integer.parseInt((String)tmp.get(6));
						int tp = Integer.parseInt((String)tmp.get(7));
						int nr = Integer.parseInt((String)tmp.get(8));
						//System.out.println(frTurn + "-" + toTurn + ":" + tp);
						//MIN21 enemy suprise attack
						if (tmp.size() == 11){
							int enemy = Integer.parseInt((String)tmp.get(9));
							int cycle = Integer.parseInt((String)tmp.get(10));
							Base target = null;
							for (int c=0; c<cycle; c++){
								target = findNextBase(enemy);
							}
							if (target != null){
								x1 = target.getX() - GameData.MASK_RANGE_X;
								y1 = target.getY() - GameData.MASK_RANGE_Y;
								x2 = target.getX() + GameData.MASK_RANGE_X;
								y2 = target.getY() + GameData.MASK_RANGE_Y;
							}
						}
						boolean spawned = false;
						int tries = 0;
						while (!spawned && tries < 20){
							int x = (int)(Randomizer.getNextRandom()*(x2-x1) + x1);
							int y = (int)(Randomizer.getNextRandom()*(y2-y1) + y1);
							//MIN26 creating random unit only if no town is there
							if (gd.getBaseLand(x, y) == GameWorld.TOWN_LAND && 
									gd.getArmy(x, y) == -1 && gd.getBase(x, y) == -1){
								int aindex = gd.createArmy(p, x, y);
								for (int j=0; j<nr; j++){
									gd.addDebugUnit(aindex, tp,	uloader);
								}
								if (mp.isDisplayable(x, y)){
									//MIN16
									mp.repaintClipCell(x, y);
									//MIN34
									updateMiniMap();
								}
								//System.out.println(x + "/" + y);
								spawned = true;
							}else{
								tries++;
							}
						}
						if (spawned){
							Player player = ploader.getPlayer(p);
							if (player != null){
								if (p == PLAYER_OWNER){
									printMessage("<%" + GameWorld.IMG_SCROLL +
										"%>We have received new re-inforcements from the loyalists.");
								}else if (p == NEUTRAL_OWNER){
									printMessage("<%" + GameWorld.IMG_SCROLL +
										"%>Our scouts reported some bandits are scattering in the area.");
								}else{
									printMessage("<%" + GameWorld.IMG_SCROLL +
										"%>Our scouts have heard some of " +
										player.getName() + 
										" companies were dispatched secretly, we must be carefull.");
								}
							}
						}
					}
				}catch(Exception x){}
				}
			}else if (key.startsWith("create")){
				//from,to,player,x1,y1,x2,y2,type
				if (tmp.size() >= 8){
				try{
					int frTurn = Integer.parseInt((String)tmp.get(0));
					int toTurn = Integer.parseInt((String)tmp.get(1));
					if (frTurn <= gd.getTurn() && toTurn >= gd.getTurn()){
						//System.out.println("Spawning");
						int p = Integer.parseInt((String)tmp.get(2));
						if (p < 0 || p > OWNER_MAX){
							continue;
						}
						int x1 = Integer.parseInt((String)tmp.get(3));
						int y1 = Integer.parseInt((String)tmp.get(4));
						int x2 = Integer.parseInt((String)tmp.get(5));
						int y2 = Integer.parseInt((String)tmp.get(6));
						int tp = Integer.parseInt((String)tmp.get(7));
						//System.out.println(frTurn + "-" + toTurn + ":" + tp);
						boolean spawned = false;
						int tries = 0;
						while (!spawned && tries < 40){
							int x = (int)(Randomizer.getNextRandom()*(x2-x1) + x1);
							int y = (int)(Randomizer.getNextRandom()*(y2-y1) + y1);
							//MIN26 creating random unit only if no town is there
							if (buildable(x, y)){
								//System.out.println(x+":"+y);
								gd.createBase(p, tp, x, y, uloader.getDefWorkerUnit(),
									tloader, oloader, hloader, uloader, bloader, teloader);
								if (mp.isDisplayable(x, y)){
									//MIN16
									mp.repaintClipCell(x, y);
									//MIN34
									updateMiniMap();
								}
								//System.out.println(x + "/" + y);
								spawned = true;
							}else{
								tries++;
							}
						}
						if (spawned){
							Player player = ploader.getPlayer(p);
							if (player != null){
								if (p == PLAYER_OWNER){
									//printMessage("<%" + GameWorld.IMG_SCROLL +
									//	"%>We have received new re-inforcements from the loyalists.");
								}else{
									printMessage("<%" + GameWorld.IMG_SCROLL +
										"%>We received news of " + player.getName() + "'s settlement setup.");
								}
							}
						}
					}
				}catch(Exception x){}
				}
			}else if (key.startsWith("message")){
				if (tmp.size() == 3){
				try{
					int frTurn = Integer.parseInt((String)tmp.get(0));
					int toTurn = Integer.parseInt((String)tmp.get(1));
					if (frTurn <= gd.getTurn() && toTurn >= gd.getTurn()){
						String msg = (String)tmp.get(2);
						//printMessage("<%" + GameWorld.IMG_SCROLL + "%>" + msg);
						logMessage("<%" + GameWorld.IMG_IDEA + "%>" + msg);
					}
				}catch(Exception x){}
				}
			}else if (key.startsWith("song")){
				if (tmp.size() == 3){
				try{
					int frTurn = Integer.parseInt((String)tmp.get(0));
					int toTurn = Integer.parseInt((String)tmp.get(1));
					if (frTurn <= gd.getTurn() && toTurn >= gd.getTurn()){
						singer.play((String)tmp.get(2));
					}
				}catch(Exception x){}
				}
			}else if (key.startsWith("popup")){
				//System.out.println("Popup");
				if (tmp.size() == 3){
				try{
					int frTurn = Integer.parseInt((String)tmp.get(0));
					int toTurn = Integer.parseInt((String)tmp.get(1));
					if (frTurn <= gd.getTurn() && toTurn >= gd.getTurn()){
						String file = (String)tmp.get(2);
						//System.out.println(file);
						String txt = getTextFile(file);
						String msg = "";

						int start = txt.indexOf("<%");
						if (start == -1){
							msg = txt;
						}else{
							int last = 0;
							while (start > -1){
								int end = txt.indexOf("%>", start);
								if (end > -1){
									String strOut = txt.substring(last, start);
									String strIn = txt.substring(start + 2, end);
									msg += strOut + getURL(strIn);
								}else{
									//tolerate
									end = start;
								}
								last = end + 2;
								start = txt.indexOf("<%", end + 2);
							}
							if (last < txt.length()){
								String strOut = txt.substring(last, txt.length());
								msg += strOut;
							}
						}
						//System.out.println(msg);
						//String msg = (String)tmp.get(2).replaceAll("%.%", world.getURL("."));
						showScrollingText(msg);
					}
				}catch(Exception x){}
				}
			}
		}
	}

	public String getURL(String file){
		try{
			URL url = this.getClass().getResource(file);
			return url.toString();
		}catch(Exception e){
			return null;
		}
	}

	public String getTextFile(String file){
		String line, txt = "";

		BufferedReader bin = new BufferedReader(
			new InputStreamReader(this.getClass().getResourceAsStream(file + ".ini")));
		try{
		while((line = bin.readLine()) != null){
			if (txt.length()>0){
				txt += "\n";
			}
			txt += line;
		}
		}catch(Exception e){
			//System.out.println(e);
		}

		try{
			bin.close();
		}catch(Exception e){
			//System.out.println(e);
		}

		return txt;
	}

	/*public boolean putTextFile(String file, String txt){
		try{
			FileOutputStream bout = new FileOutputStream(
				new File(this.getClass().getResource(file + ".ini")));
			bout.write(txt.toByteArray());
			bout.close();
		}catch(Exception e){
			//System.out.println(e);
			return false;
		}
		return true;
	}*/

	public synchronized Image loadImage(String file){
		try{
			Image img = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/"+file));
			MediaTracker mt = new MediaTracker(frame);
			mt.addImage(img, 0);
			try{
				mt.waitForID(0);
			}catch(InterruptedException ie){
				//System.err.println(ie);
			}
			return img;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}

class UnitEditorDialog extends JDialog{
	private GameWorld world;
	private JTextPane txtData;

	private void loadUnitData(){
		txtData.setText(world.getTextFile("unit"));
	}

	public UnitEditorDialog(Frame owner, GameWorld w){
		super(owner, GameWorld.GAME_NAME + " - Unit Editor", true);

		world = w;

		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		txtData = new JTextPane();
		ListScrollPane scrPane = new ListScrollPane(txtData, 500, 420);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrPane, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(owner);
	}

	public void showEditor(){
		loadUnitData();
		show();
	}
}

class MapGenDialog extends JDialog{
	private JScrollBar scrRatio, scrWidth, scrHeight;
	private JList lstDifficulty;

	public MapGenDialog(Frame owner){
		super(owner, "Create random world", true);

		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		scrRatio = new JScrollBar(JScrollBar.HORIZONTAL);
		scrRatio.setMinimum(20);
		scrRatio.setMaximum(60);
		scrWidth = new JScrollBar(JScrollBar.HORIZONTAL);
		scrWidth.setMinimum(90);
		scrWidth.setMaximum(120);
		scrHeight = new JScrollBar(JScrollBar.HORIZONTAL);
		scrHeight.setMinimum(90);
		scrHeight.setMaximum(120);

		lstDifficulty = new JList(new Object[] {"Easy","Normal","Hard"});
		ListScrollPane scrPane = new ListScrollPane(lstDifficulty, 48, 52);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JButton btnExit = new JButton("Start new game");
		btnExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();
			}
		});

		Panel panTemp3 = new Panel(new BorderLayout());
		panTemp3.add(new DummyPanel(), BorderLayout.SOUTH);
		panTemp3.add(scrRatio, BorderLayout.NORTH);

		Panel panTemp4 = new Panel(new BorderLayout());
		panTemp4.add(new DummyPanel(), BorderLayout.SOUTH);
		panTemp4.add(scrWidth, BorderLayout.NORTH);

		Panel panTemp5 = new Panel(new BorderLayout());
		panTemp5.add(new DummyPanel(), BorderLayout.SOUTH);
		panTemp5.add(scrHeight, BorderLayout.NORTH);

		Panel panTemp = new Panel(new GridLayout(4, 2));
		panTemp.add(new JLabel("Land mass (%)"));
		panTemp.add(panTemp3);
		panTemp.add(new JLabel("Map width (square)"));
		panTemp.add(panTemp4);
		panTemp.add(new JLabel("Map height (square)"));
		panTemp.add(panTemp5);
		panTemp.add(new DummyPanel());
		panTemp.add(btnExit);

		Panel panTemp2 = new Panel(new GridLayout(1, 2));
		panTemp2.add(new JLabel("Difficulty level"));
		panTemp2.add(scrPane);

		//getContentPane().setLayout(new BorderLayout());
		//getContentPane().add(new JLabel("+Map generation parameters:"), BorderLayout.NORTH);
		getContentPane().add(panTemp2, BorderLayout.CENTER);
		getContentPane().add(panTemp, BorderLayout.SOUTH);

		pack();

		setLocationRelativeTo(owner);
	}

	public void showParameters(){
		scrWidth.setValue(90);
		scrHeight.setValue(90);
		scrRatio.setValue(40);

		lstDifficulty.setSelectedIndex(0);

		show();
	}

	public double getRatio(){
		return (double)scrRatio.getValue() / 100;
	}

	public int getDifficulty(){
		return lstDifficulty.getSelectedIndex();
	}

	public int getWidth(){
		return scrWidth.getValue();
	}

	public int getHeight(){
		return scrHeight.getValue();
	}
}

class ScrollingTextDialog extends JDialog{
	private ScrollingTextPanel panText;

	public ScrollingTextDialog(Frame owner){
		super(owner, GameWorld.GAME_NAME, true);
		setResizable(false);
		//setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener(){
			public void windowOpened(WindowEvent e){
			}
			public void windowClosing(WindowEvent e){
				panText.stop();
				hide();
			}
			public void windowClosed(WindowEvent e){
			}
			public void windowIconified(WindowEvent e){
			}
			public void windowDeiconified(WindowEvent e){
			}
			public void windowActivated(WindowEvent e){
			}
			public void windowDeactivated(WindowEvent e){
			}
		});

		panText = new ScrollingTextPanel(800, 560, 50);
		panText.setBackground(getBackground());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panText, BorderLayout.CENTER);
		pack();

		setLocationRelativeTo(owner);
	}

	public void showMessage(String msg){
		panText.setText(msg);
		//MIN27
		//panText.start();
		show();
	}
	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}
}

class Scenario{
	private Properties events;

	public Scenario(String file){
		events = new Properties();
		try{
			events.load(this.getClass().getResourceAsStream(file));
		}catch(Exception e){
		}
	}

	public String getProperty(String key){
		return events.getProperty(key);
	}

	public Enumeration getKeys(){
		return events.propertyNames();
	}
}

class SoundList implements Runnable{
	private static final int TIMER = 1000;
	private static final int WAIVE_TIMER = 2000;

	private ArrayList songs;
	private int count;
	private boolean running;
	private boolean random;
	private Sequencer player;
	private Sequence theSound;

	//waiting time left
	private long waiting = 0;

	public SoundList(){
		random = true;
		count = 0;
		songs = new ArrayList();
	}

	public void setRandom(boolean r){
		random = r;
	}

	public void loadGameSound(){
		try{
			//System.err.println("ENTRY\n" + this.getClass().getResource("sounds/."));
			URL url = this.getClass().getResource("sounds");
			if (url != null) {
				File fil = new File(new URI(url.toString()));
				String[] lst = fil.list(new FilenameFilter(){
					public boolean accept(File dir, String name){
						if (name.endsWith(".mid")){
							return true;
						}else{
							return false;
						}
					}
				});
				for (int i=0 ; i<lst.length; i++) {
					//System.err.println(lst[i]);
					add(lst[i]);
				}
			}//else{
			//	System.err.println("Can not find sounds folder");
			//}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected synchronized boolean waiting(int mil){
		try{
			waiting += mil;
			if (waiting > theSound.getMicrosecondLength() / 1000){
			//if (waiting > 10000){
				waiting = 0;
				return false;
			}else{
				return true;
			}
		}catch(Exception e){
			waiting = 0;
			e.printStackTrace();
			return false;
		}
	}

	public void add(String f){
		songs.add(f);
	}

	public void start(){
		Thread thread = new Thread(this);
		running = true;
		thread.start();
	}

	public void stop(){
		if (player != null && player.isOpen()){
			player.close();
		}
		running = false;
	}

	public void run(){
		while (running){
			//System.err.println("BEGINS");
			//between
			try{
				Thread.sleep(WAIVE_TIMER);
			}catch(Exception e){
			//	System.err.println(e);
			}
			//System.err.println("PLAYS");
			//play
			play();
			//wait
			//System.err.println("WAITS");
			while (waiting(TIMER)){
				try{
					Thread.sleep(TIMER);
				}catch(Exception e){
			//		System.err.println(e);
				}
			}
			//System.err.println("ENDS");
		}
	}

	public synchronized void play(String file){
		if (!running){
			return;
		}
		if (file.endsWith(".wav")){
			AudioClip clip = 
				Applet.newAudioClip(this.getClass().getResource("sounds/"+file));
			clip.play();
		}else if (file.endsWith(".mid")){
			try {
				if (player == null){
					player = MidiSystem.getSequencer();
				}else if (player.isOpen()){
					player.close();
				}
				theSound = 
					MidiSystem.getSequence(this.getClass().getResource("sounds/"+file));
				player.open();
				player.setSequence(theSound);
				player.start();
				waiting = 0;
			}catch (Exception ex){
				System.out.println(ex);
			}
		}
	}

	public void play(){
		if (songs.size()<=0) {
			return;
		}
		if (random) {
			count = (int)(Randomizer.getNextRandom() * songs.size());
		}
		//System.err.println("Count " + songs.size());
		//to-do uncache wav
		String file = (String)songs.get(count);
		//System.err.println("Playing song: " + file);

		play(file);

		if (!random && (++count) >= songs.size()){
			count = 0;
		}
	}
}

class NewTurnTimer implements Runnable{
	private TerrainLoader tloader;
	private UnitTypeLoader uloader;
	private HouseTypeLoader hloader;
	private OverlayLoader oloader;
	private PlayerLoader ploader;
	private TechLoader teloader;

	private GameMoveTimer mover;
	private MapPanel mp;
	private GameWorld world;
	private Scenario scenario;

	public NewTurnTimer(MapPanel m, Scenario sc, UnitTypeLoader ul,
				HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, PlayerLoader pl, TechLoader tel,	GameMoveTimer mv, GameWorld w){
		mp = m;
		scenario = sc;
		tloader = tl;
		uloader = ul;
		hloader = hl;
		oloader = ol;
		ploader = pl;
		teloader = tel;
		mover = mv;
		world = w;
	}

	protected int getNearestEnemyArmy(int x, int y, int owner){
		int d1 = 999999;
		int result = -1;

		for (int i=0; i<GameData.MASK_LENGTH; i++){
			int cx = x + GameData.MASK_X[i];
			int cy = y + GameData.MASK_Y[i];

			int enemy = world.getGD().getArmy(cx, cy);
			if (enemy > -1){
				int eo = world.getGD().getArmy(enemy).getOwner();
				if (eo != owner && world.getGD().getDiplomacy(eo, owner) < GameData.DIP_PEACE){
					int d2 = Math.abs(cx - x) + Math.abs(cy - y);
					if (d1 > d2){
						d1 = d2;
						result = enemy;
					}
				}
			}
		}

		return result;
	}

	protected int getNearestEnemyBase(int x, int y, int owner){
		int d1 = 999999;
		int result = -1;

		for (int i=0; i<GameData.MASK_LENGTH; i++){
			int cx = x + GameData.MASK_X[i];
			int cy = y + GameData.MASK_Y[i];

			int enemy = world.getGD().getBase(cx, cy);
			if (enemy > -1){
				int eo = world.getGD().getBase(enemy).getOwner();
				if (eo != owner && world.getGD().getDiplomacy(eo, owner) < GameData.DIP_PEACE){
					int d2 = Math.abs(cx - x) + Math.abs(cy - y);
					if (d1 > d2){
						d1 = d2;
						result = enemy;
					}
				}
			}
		}

		return result;
	}

	protected int getClosestBase(int x, int y, int owner, int d1){
		d1 *= d1;
		int result = -1;

		for (int i=0; i<world.getGD().getBaseSize(); i++){
			Base enemy = world.getGD().getBase(i);

			if (enemy != null && enemy.getOwner() == owner){
				int d2 = Math.abs(enemy.getX() - x) + Math.abs(enemy.getY() - y);
				if (d1 > d2){
					d1 = d2;
					result = i;
				}
			}
		}

		return result;
	}

	public void start(){
		mp.setEnabled(false);
		world.disable();
		
		world.clearConsole();

		Thread thread = new Thread(this);
		thread.start();
	}

	public void run(){
		//save
		int sx = mp.getLeft() + mp.getScreenWidth()/2;
		int sy = mp.getTop() + mp.getScreenHeight()/2;

		if (doNewTurn()) {
			//restore view
			mp.setViewCenter(sx, sy);
			world.updateMiniMap();

			//animation
			//if (Randomizer.getNextRandom() > 0.9){
			//	world.startFireWork();
			//}

			//enable
			GameWorld.printMessage("<%" + GameWorld.IMG_GLOBE + "%> Today is " + world.getDayAsString());
			world.enable();
			mp.setEnabled(true);
		}
	}
	
	protected int getFuzziness(double range){
		return (int)(range*2*Randomizer.getNextRandom() - range);
	}

	//to-do
	protected int getWeightedRelationship(int p1, int p2){
		return world.getGD().getDiplomacy(p1, p2);
	}

	//to-do
	protected int getVulnerablePoint(int player){
		try{
			Player p = ploader.getPlayer(player);
			int acount = p.getArmyCount();
			int bcount = p.getBaseCount() * 
				Integer.parseInt(p.getProp("difficulty"));

			if (acount < bcount){
				return bcount - acount;
			}else{
				return 0;
			}
		}catch(Exception e){
			return 0;
		}
	}

	//to-do
	protected int getWeightedArmySize(int player){
		try{
			Player p = ploader.getPlayer(player);
			int acount = p.getArmyCount();
			int bcount = p.getBaseCount() * 
				Integer.parseInt(p.getProp("difficulty"));

			if (acount > bcount){
				return acount - bcount + 1;
			}else{
				return 1;
			}
		}catch(Exception e){
			return 1;
		}
	}

	//to-do
	public boolean getWeightedDecision(int p1, int p2, int deal){
		double rate = deal - GameData.DIP_UNKNOWN;
		//retrieve from player setting
		try{
			Player player1 = ploader.getPlayer(p1);
			rate /= Integer.parseInt(player1.getProp("love"));
		}catch(Exception e){}
		//if stronger then give in
		if (deal > GameData.DIP_UNKNOWN){
			if (getWeightedArmySize(p2) + getFuzziness(rate) > 
				getWeightedArmySize(p1) * rate){
				return true;
			}
		}
		return false;
	}

	//to-do
	public boolean getWeightedBetrayal(int p1, int p2, int current){
		double rate = current;
		//retrieve from player setting
		try{
			Player player1 = ploader.getPlayer(p1);
			rate /= Integer.parseInt(player1.getProp("greed"));
		}catch(Exception e){}
		//if stronger then become greedy
		if (getWeightedArmySize(p1) + getFuzziness(rate) >
			getWeightedArmySize(p2) * rate){
			return true;
		}
		return false;
	}

	protected void moveArmyTo(Army a, int x, int y, boolean persist){
		if (a == null || (a.getX() == x && a.getY() == y)){
			return;
		}
		//is it viewable?
		if (world.getGD().getMask(a.getX(), a.getY()) != GameData.MASK_INVISIBLE){
			if (!mp.isDisplayable(a.getX(), a.getY())){
				mp.setViewCenter(a.getX(), a.getY());
				//give time to see what happen
				try{
					//System.out.println("Sleep 1");
					Thread.sleep(250);
				}catch(Exception e){
				}
			}
		}
		mover.setAction(a, x, y);
		mover.start();
		while (mp.isBusy()){
			try{
				//System.out.println("Sleep 2");
				Thread.sleep(20);
			}catch(Exception e){
			}
		}
		//MIN34
		//world.updateMiniMap();
		//MIN34
		//give time to update
		//try{
		//	System.out.println("Sleep 3");
		//	Thread.sleep(50);
		//}catch(Exception e){
		//}
		//System.out.println(mover.getLastResult());

		//MIN32 allow AI to persue target
		if (persist && (a.getX() != x || a.getY() != y)){
			//MH108
			//Job job = new Job(Job.ORDER_ATTACK);
			//job.setDestination(new Point(x, y));
			//a.setJob(job);
			a.setDestination(x, y);
		}
	}

	//to-do: this is a lame way of doing it
	protected Point findBuildablePlace(int x, int y, int range){
		if (world.buildable(x, y)){
			//System.out.println("ORIGINAL");
			return new Point(x, y);
		}

		int cells = range * range - 1;
		int counter = 0, rotating = 0, expander = 1, ox = x, oy = y;

		for (int m = 0; m < cells; m++){
			//first point
			if (counter == 0){
				x = ox + GameData.ROTATE_X[counter] * expander;
				y = oy + GameData.ROTATE_Y[counter] * expander;
			}else{
			//remaining points
				x += GameData.ROTATE_X[counter];
				y += GameData.ROTATE_Y[counter];
			}
			rotating++;
			if (rotating >= expander){
				rotating = 0;
				counter++;
				if (counter >= GameData.ROTATE_X.length){
					counter = 0;
				}
			}
			if (world.buildable(x, y)){
				//System.out.println("xy");
				return new Point(x, y);
			}
		}
		//System.out.println("NULL");
		return null;
	}

	public void doAITurn(int player){
		//init
		Player thePlayer = ploader.getPlayer(player);
		int favor = thePlayer.getFavorUnitType();
		//int favor = -1;
		//try{
		//	favor = Integer.parseInt(thePlayer.getProp("unit"));
		//}catch(Exception e){}
		//to-do implements difficulty
		double cheat = (95 - world.getGD().getDifficulty()*10)/100;
		try{
			cheat -= Integer.parseInt(thePlayer.getProp("difficulty")) / 10;
		}catch(Exception e){}
		String pname = thePlayer.getName();
		ArrayList targets = new ArrayList();

		GameWorld.printMessage("<%" + GameWorld.IMG_HOURGLASS + "%>" +
			pname + " is now moving.");
		
		//deal with diplomacy stuff
		//MIN25 exclude neutral, neutral can initiate diplomacy but others cant to them
		for (int i=0; i<GameWorld.PLAYABLE_OWNER_SIZE; i++){
			if (i == player){
				continue;
			}
			int current = world.getGD().getDiplomacy(i, player);
			//pending
			if (current % 2 == 1){
				if (getWeightedDecision(player, i, current)){
					world.getGD().setDiplomacy(player, i, current + 1);
					world.getGD().setDiplomacy(i, player, current + 1);
					if (i==GameWorld.PLAYER_OWNER){
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, " +	pname + " has declared " + GameData.DIP_NAME[current + 1] + " with us!");
					}else{
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, " +	pname + " has declared " + GameData.DIP_NAME[current + 1] + " with " + ploader.getPlayer(i).getName() + ".");
					}
					continue;
				}
			}
			//current hostile
			if (current < GameData.DIP_PEACE){
				if (getWeightedDecision(player, i, GameData.DIP_REC_PEACE)){
					if (i == GameWorld.PLAYER_OWNER){
						world.doTalk(player, i, GameData.DIP_PEACE);
					}else{
						world.getGD().setDiplomacy(player, i, GameData.DIP_REC_PEACE);
						world.getGD().setDiplomacy(i, player, GameData.DIP_REC_PEACE);
					}
				}else if (current > GameData.DIP_REC_WAR && getWeightedBetrayal(player, i, current)){
					world.getGD().setDiplomacy(player, i, GameData.DIP_REC_WAR);
					world.getGD().setDiplomacy(i, player, GameData.DIP_REC_WAR);
					if (i == GameWorld.PLAYER_OWNER){
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + 
							"%>My lord, " + pname + " has betrayed us and declare war!!!");
					}else{
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + 
							"%>My lord, " +	pname + " has declared war with " + ploader.getPlayer(i).getName()+".");
					}
				}
				targets.add(new Integer(i));
			//currently at peace
			}else if (getWeightedBetrayal(player, i, current)){
				world.getGD().setDiplomacy(player, i, GameData.DIP_REC_WAR);
				world.getGD().setDiplomacy(i, player, GameData.DIP_REC_WAR);
				if (i == GameWorld.PLAYER_OWNER){
					GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + 
						"%>My lord, " + pname + " has betrayed us and declare war!!!");
				}else{
					GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + 
						"%>My lord, " +	pname + " has declared war with " + ploader.getPlayer(i).getName()+".");
				}
				targets.add(new Integer(i));
			}
		}

		//dealing with units
		for (int i=0; i<world.getGD().getArmySize(); i++){
			//System.out.println(i);
			Army a = world.getGD().getArmy(i);
			// ******************************************
			// ARMY CAN BE NULL, MEANING A DEAD ARMY
			// ******************************************
			if (a != null && a.getOwner() == player){
				//priority is to build city
				if (world.buildCity(a)){
					continue;
				}
				//load supplies
				if (!world.autoLoadResource(a, GameWorld.RESOURCE_FOOD)){
					//do something? if not enough resources?
				}
				//allow entertainment
				if (!world.autoLoadResource(a, GameWorld.RESOURCE_HAPPY)){
					//do something? if not enough resources?
				}
				//protecting city, do not move?
				if (world.getGD().getBase(a.getX(), a.getY()) > -1){
					continue;
				}
				//MIN21
				if (world.hasBuilder(a)){
					//find a place to build
					Point explore = findBuildablePlace(a.getX(), a.getY(),
						GameData.MASK_LENGTH);
					if (explore != null){
						if (explore.x == a.getX() && explore.y == a.getY()){
							if (world.buildCity(a)){
								continue;
							}
						}else{
							moveArmyTo(a, explore.x, explore.y, false);
							continue;
						}
					}
				}
				//System.out.println("# " + i);
				//try to capture any nearest city
				//System.out.println("Find enemy");
				int c = getNearestEnemyBase(a.getX(), a.getY(), player);
				if (c > -1){
					//System.out.println("nearest base " + i);
					Base enemy = world.getGD().getBase(c);
					//at peace, do not attack
					if (getWeightedRelationship(player, enemy.getOwner()) > GameData.DIP_UNKNOWN){
						continue;
					}
					moveArmyTo(a, enemy.getX(), enemy.getY(), false);
				}else{
					//System.out.println("Find enemy 2");
					//System.out.println("$ " + i);
					//try to attack nearest enemy army
					int e = getNearestEnemyArmy(a.getX(), a.getY(), player);
					if (e > -1){
						//System.out.println("nearest enemy " + i);
						Army enemy = world.getGD().getArmy(e);
						//at peace, do not attack
						if (getWeightedRelationship(player, enemy.getOwner()) > GameData.DIP_UNKNOWN){
							continue;
						}
						moveArmyTo(a, enemy.getX(), enemy.getY(), false);
					}/*else if (world.hasBuilder(a)){
						//find a place to build
						Point explore = findBuildablePlace(a.getX(), a.getY(), GameData.MASK_LENGTH * 2);
						if (explore != null){
							moveArmyTo(a, explore.x, explore.y, false);
						}
					}*/else{
						//System.out.println("Find cities");
						//cheating, find a close city to attack
						for (int j =0; j<targets.size(); j++){
							int t = ((Integer)targets.get(j)).intValue();
							int b = getClosestBase(a.getX(), a.getY(), t, GameData.MASK_LENGTH);
							if (b > -1){
								//System.out.println("find them " + i);
								Base enemy = world.getGD().getBase(b);
								moveArmyTo(a, enemy.getX(), enemy.getY(), false);
								break;
							}
						}
					}
				}
			}
			//System.out.println("#$");
		}
		//bases automation
		int buildMore = getVulnerablePoint(player);
		//do it
		for (int i=0; i<world.getGD().getBaseSize(); i++){
			Base b = world.getGD().getBase(i);
			if (b != null && b.getOwner() == player){
				b.automate(world.getGD(), uloader, hloader, tloader, oloader, teloader);
				//try to form more army
				if (buildMore > 0){
					int aindex = world.getGD().getArmy(b.getX(), b.getY());
					Army a = world.getGD().getArmy(aindex);
					if (a != null){
						if (a.getCount() > 1){
							//if (Randomizer.getNextRandom() > cheat && 
							//	!a.canMove(GameWorld.SEA_LAND, uloader, false)){
							//MIN32 boat cheat is always enabled for AI
							//for units inside city
							if (!a.canMove(GameWorld.SEA_LAND, uloader, false)){
								//add a ship
								world.getGD().addDebugUnit(world.getGD().armyIndex(a), 
									uloader.getTransport(GameWorld.SEA_LAND),
									uloader);
							}
							int rx = (int)(Randomizer.getNextRandom() * 2) * 2 - 1 + b.getX();
							int ry = (int)(Randomizer.getNextRandom() * 2) * 2 - 1 + b.getY();
							moveArmyTo(a, rx, ry, false);
						}
					//MIN11
					}else if (thePlayer.getArmyCount() < GameWorld.CHEAT_LIMIT &&
						Randomizer.getNextRandom() > cheat &&
						thePlayer.getArmyCount() < GameWorld.ARMY_LIMIT){
						aindex = world.getGD().createArmy(b.getOwner(), b.getX(), b.getY());
						if (favor != -1){
							world.getGD().addDebugUnit(aindex, favor, uloader);
						}else{
							world.getGD().addDebugUnit(aindex, (int)(uloader.getSize() * Randomizer.getNextRandom()), uloader);
						}
						if (mp.isDisplayable(b.getX(), b.getY())){
							//MIN16
							mp.repaintClipCell(b.getX(), b.getY());
						}
					}
					//allow ai to cheat
					if (!world.formArmy(b, null) && Randomizer.getNextRandom() > cheat){
						if (favor != -1){
							world.getGD().addDebugUnit(aindex, favor, uloader);
						}else{
							world.getGD().addDebugUnit(aindex, uloader.getDefCombatUnit(), uloader);
						}
						if (mp.isDisplayable(b.getX(), b.getY())){
							//MIN16
							mp.repaintClipCell(b.getX(), b.getY());
						}
					}
				}
			}
		}

		GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, " +
			pname + " has finishing moving.");
	}

	public boolean doNewTurn(){
		for (int i=GameWorld.PLAYER_OWNER+1; i<GameWorld.OWNER_SIZE; i++){
			doAITurn(i);
		}
		//automate player towns if need
		for (int i=0; i<world.getGD().getBaseSize(); i++){
			Base b = world.getGD().getBase(i);
			if (b.getOwner() == GameWorld.PLAYER_OWNER && b.getFlag(Base.MASK_AUTOMATE) == Base.MASK_AUTOMATE){
				b.automate(world.getGD(), uloader, hloader, tloader, oloader, teloader);
			}
		}
		//refresh trader
		for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
			world.getGD().getTrader().setResourceDebug(i, (int)(Randomizer.getNextRandom() * GameWorld.RESOURCE_LIMIT[i]));
		}
		//jobs creation
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			//20% chance of job per turn
			int jc = (int)(Randomizer.getNextRandom() * 10 - 8);
			//available
			int ja = jc - world.getGD().getJobSize(i);
			for (int j=0; j<ja; j++) {
				Job jb = JobGenerator.getNewJob();
				world.getGD().addJob(i, jb);
			}
		}
		//standard new turn
		return world.doNewTurn();
	}
}

class ImagePanel extends JComponent{
	private Image img;
	private int width, height;

	public ImagePanel(int w, int h){
		super();
		width = w;
		height = h;
		img = null;
	}
	
	public void setImage(Image im){
		img = im;
	}

	public Dimension getPreferredSize(){
		return new Dimension(width + 1, height + 1);
	}

	//MIN06
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height){
		if (isShowing() && (infoflags & ALLBITS) != 0){
			repaint();
		}
		if (isShowing() && (infoflags & FRAMEBITS) != 0){
			repaint();
		}
		return isShowing();
	}

	public void paintComponent(Graphics g){
		if (isOpaque()){
			g.setColor(getBackground());
			g.fillRect(0,0,width,height);
		}
		if (img != null){
			g.drawImage(img, 0, 0, width, height, 0, 0, width, height, this);
		}
	}
}

class TechChooserDialog extends JDialog{
	private TechLoader teloader;
	private PlayerLoader ploader;
	private ArrayList inds;
	private int value;
	private JList lstTech;
	private JTextPane txtComment;
	private ImageCellRenderer icr;

	public TechChooserDialog(Frame owner, TechLoader tel, PlayerLoader pl){
		super(owner, "Advancements", true);

		teloader = tel;
		ploader = pl;

		lstTech = new JList();
		icr = new ImageCellRenderer(null);
		lstTech.setCellRenderer(icr);
		//lstTech.setBackground(GameWorld.COL_GREEN);
		lstTech.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				if (e.getValueIsAdjusting()){
					return;
				}
				int selected = lstTech.getSelectedIndex();
				if (selected == -1){
					return;
				}
				value = selected;
				
				if (inds.size() > value){
					Tech tech = teloader.getTech(((Integer)inds.get(value)).intValue());
					String strRange = "";
					if (tech.getRRange() > 0){
						strRange="Required range: "+Integer.toString(tech.getRRange());
					}else{
						strRange="Meelee only";
					}
					String strBonus = "";
					if (tech.getUAttack() > 0){
						strBonus+="Attack +"+Integer.toString(tech.getUAttack());
					}
					if (tech.getUDefend() > 0){
						if (strBonus.length()>0){
							strBonus += ", ";
						}
						strBonus+="Defend +"+Integer.toString(tech.getUDefend());
					}
					if (tech.getUCombat() > 0){
						if (strBonus.length()>0){
							strBonus += ", ";
						}
						strBonus+="Combat +"+Integer.toString(tech.getUCombat());
					}
					if (tech.getURange() > 0){
						if (strBonus.length()>0){
							strBonus += ", ";
						}
						strBonus+="Range +"+Integer.toString(tech.getURange());
					}
					if (tech.getDHit() > 0){
						if (strBonus.length()>0){
							strBonus += ", ";
						}
						strBonus+="Damage +"+Integer.toString(tech.getDHit());
					}
					if (tech.getDCombat() > 0){
						if (strBonus.length()>0){
							strBonus += ", ";
						}
						strBonus+="Shock +"+Integer.toString(tech.getDCombat());
					}
					String strCost = "";
					int cnt = 0;
					for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
						int res = tech.getResource(i);
						if (res > 0){
							if (cnt > 0){
								strCost += ",";
							}
							strCost += Integer.toString(res) + " " + GameWorld.RESOURCE_NAME[i];
							cnt++;
						}
					}
					String strUnique = "";
					if (tech.getRPlayer() > -1){
						strUnique += "Unique to " + ploader.getPlayer(tech.getRPlayer()).getName();
					}
					if (strCost.length() > 0){
						txtComment.setText(tech.getDescription() + "\nCost: " + strCost +
							". " + strRange + ". " + strBonus + "\n" + strUnique);
					}else{
						txtComment.setText(tech.getDescription() +
							strRange + ". " + strBonus + "\n" + strUnique);
					}
				}
			}
		});

		ListScrollPane scrPane = new ListScrollPane(lstTech, 400, 200);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		txtComment = new JTextPane();
		txtComment.setBackground(getBackground());
		ListScrollPane scrPane1 = new ListScrollPane(txtComment, 400, 60);

		JButton btnExit = new JButton("Select/Done");
		btnExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();
			}
		});

		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(btnExit, BorderLayout.EAST);

		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrPane, BorderLayout.NORTH);
		getContentPane().add(scrPane1, BorderLayout.CENTER);
		getContentPane().add(panTemp, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public void showAdvances(GameData gd, int o, boolean upgradeOnly){
		ArrayList ads = new ArrayList();
		inds = new ArrayList();
		for (int i=0; i<gd.getAdvanceSize(o); i++){
			int index = gd.getAdvance(o, i);
			Tech tech = teloader.getTech(index);
			if (tech == null || (upgradeOnly && !tech.isUpgrade())) {
				continue;
			}
			String strRange = "";
			if (tech.getRRange() > 0){
				strRange="Required range: "+Integer.toString(tech.getRRange());
			}else{
				strRange="Meelee only";
			}
			String strBonus = "";
			if (tech.getUAttack() > 0){
				strBonus+="Attack +"+Integer.toString(tech.getUAttack());
			}
			if (tech.getUDefend() > 0){
				if (strBonus.length()>0){
					strBonus += ", ";
				}
				strBonus+="Defend +"+Integer.toString(tech.getUDefend());
			}
			if (tech.getUCombat() > 0){
				if (strBonus.length()>0){
					strBonus += ", ";
				}
				strBonus+="Combat +"+Integer.toString(tech.getUCombat());
			}
			if (tech.getURange() > 0){
				if (strBonus.length()>0){
					strBonus += ", ";
				}
				strBonus+="Range +"+Integer.toString(tech.getURange());
			}
			if (tech.getDHit() > 0){
				if (strBonus.length()>0){
					strBonus += ", ";
				}
				strBonus+="Damage +"+Integer.toString(tech.getDHit());
			}
			if (tech.getDCombat() > 0){
				if (strBonus.length()>0){
					strBonus += ", ";
				}
				strBonus+="Shock +"+Integer.toString(tech.getDCombat());
			}
			String strCost = "";
			int cnt = 0;
			for (int j=0; j<GameWorld.RESOURCE_SIZE; j++){
				int res = tech.getResource(j);
				if (res > 0){
					if (cnt > 0){
						strCost += ",";
					}
					strCost += Integer.toString(res) + " " + GameWorld.RESOURCE_NAME[j];
					cnt++;
				}
			}
			if (strCost.length() > 0){
				ads.add("<html><b>" + tech.getName() + "</b><br>Cost " + 
					strCost + ". " + strRange + ".<br>" + strBonus + "</html>");
			}else{
				ads.add("<html><b>" + tech.getName()+ "</b><br>"+
					strRange+ ".<br>" + strBonus+"</html>");
			}
			//ads.add(tech.getName());
			inds.add(new Integer(index));
		}

		icr.setIconList(teloader.getIconList(inds));
		lstTech.setListData(ads.toArray());

		if (inds.size() <= 0){
			txtComment.setText("There is no discoveries or advancements at the moment.");
		}else{
			txtComment.setText(" ");
		}

		value = -1;
		show();
	}

	public int getValue(){
		return value;
	}

	public int getUpgradeValue(){
		if (value > -1 && value < inds.size()) {
			return ((Integer)inds.get(value)).intValue();
		}else{
			return -1;
		}
	}

	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}
}

class YesNoDialog extends JDialog{
	public static final int STATE_YES = 1;
	public static final int STATE_NO = 0;

	private JTextPane txtMsg;
	private ImagePanel panPortrait;
	private int state;

	public YesNoDialog(Frame owner){
		super(owner, "Confirmation", true);

		state = STATE_NO;

		JButton btnYes = new JButton("Yes");
		btnYes.setMnemonic(KeyEvent.VK_Y);
		btnYes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				state = STATE_YES;
				hide();
			}
		});
		JButton btnNo = new JButton("No");
		btnNo.setMnemonic(KeyEvent.VK_N);
		btnNo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				state = STATE_NO;
				hide();
			}
		});
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
		tmp.add(btnYes, BorderLayout.WEST);
		tmp.add(btnNo, BorderLayout.EAST);

		txtMsg= new JTextPane();
		txtMsg.setEnabled(false);
		txtMsg.setBackground(getBackground());
		ListScrollPane scrP = new ListScrollPane(txtMsg, 48 * 4, 48 * 2);

		panPortrait = new ImagePanel(48, 48);

		//getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panPortrait, BorderLayout.WEST);
		getContentPane().add(scrP, BorderLayout.EAST);
		getContentPane().add(tmp, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public void showMessage(Image img, String str){
		txtMsg.setText("");
		panPortrait.setImage(img);
		txtMsg.setText(str);

		state = STATE_NO;
		show();
	}

	public int getValue(){
		return state;
	}
}

class EnquiryDialog extends JDialog{
	private final static int IMG_W = 48, IMG_H = 48;

	private PlayerLoader ploader;
	private UnitHolderPanel panUnit;
	private TerrainLoader tloader;
	private OverlayLoader oloader;
	private GameWorld world;

	private JLabel lblMessage;
	private JTextPane txtTerrain;
	private JButton btnTalk, btnTrade;

	private Army army;
	private Base base;

	public EnquiryDialog(Frame owner, GameWorld w, UnitTypeLoader ul, PlayerLoader pl,
			TerrainLoader tl, OverlayLoader ol){
		super(owner, "Area's Report", true);

		ploader = pl;
		tloader = tl;
		oloader = ol;
		world = w;

		army = null;
		base = null;

		lblMessage = new JLabel(" ");
		txtTerrain = new JTextPane();
		txtTerrain.setEnabled(false);
		ListScrollPane scrPane = new ListScrollPane(txtTerrain, IMG_W * 5, IMG_H * 3);
		panUnit = new UnitHolderPanel(IMG_W, IMG_H, ul, world, false);
		panUnit.setEnabled(false);

		btnTalk = new JButton("Bribe");
		btnTalk.setEnabled(false);
		btnTalk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//JOptionPane.showMessageDialog(null, "Proposed feature:\n"+
				//	"Allowing conversation using RPG style with character\n"+
				//	"Currently in development, including quest, script related "+
				//	"events", GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
				world.doBribe(army);
			}
		});

		btnTrade = new JButton("Barter");
		btnTrade.setEnabled(false);
		btnTrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//JOptionPane.showMessageDialog(null, "Proposed feature:\n"+
				//	"Allowing conversation using RPG style with character\n"+
				//	"Currently in development, including quest, script related "+
				//	"events", GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
				world.doTradeWithAI(army);
			}
		});

		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(new DummyPanel(), BorderLayout.NORTH);
		panTemp.add(btnTalk, BorderLayout.EAST);
		panTemp.add(new JLabel(), BorderLayout.CENTER);
		panTemp.add(btnTrade, BorderLayout.WEST);

		setBackground(GameWorld.COL_GREEN);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(lblMessage, BorderLayout.NORTH);
		getContentPane().add(panUnit, BorderLayout.EAST);
		getContentPane().add(scrPane, BorderLayout.WEST);
		getContentPane().add(panTemp, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public void setData(int x, int y){
		GameData gd = world.getGD();
		Army a = gd.getArmy(gd.getArmy(x, y));
		Base b = gd.getBase(gd.getBase(x, y));
		int t = gd.getBaseLand(x, y);

		if (a != null){
			btnTalk.setEnabled(true);
			btnTrade.setEnabled(true);
			Player player = ploader.getPlayer(a.getOwner());
			if (player != null){
				String strText = "<html>" + player.getName();
				if (a.getOwner() == GameWorld.NEUTRAL_OWNER){
					strText += " bandits";
				}else{
					strText += "'s Army";
				}
				if (b != null){
					strText += ", fortifying <font color=\"blue\">" + b.getName() +
						"</font>";
				}
				strText += "</html>";
				lblMessage.setText(strText);
			}else{
				lblMessage.setText(" ");
			}
		}else{
			btnTalk.setEnabled(false);
			if (b != null){
				btnTrade.setEnabled(true);
				Player player = ploader.getPlayer(b.getOwner());
				String strText = "<html>";
				strText += "<font color=\"blue\">" + b.getName() + "</font>";
				if (player != null){
					strText += " (" + player.getName() + ")";
				}
				strText += "</html>";
				lblMessage.setText(strText);
			}else{
				btnTrade.setEnabled(false);
				lblMessage.setText(" ");
			}
		}

		txtTerrain.setText("");
		javax.swing.text.StyledDocument document = txtTerrain.getStyledDocument();

		//System.out.println(t + " : " + o);

		Terrain ter = tloader.getTerrain(t);
		if (ter != null){
			try{
			document.insertString(document.getLength(), "+" + ter.getProp("name"), null);
			for (int i=0; i<ter.getProductions(); i++){
				if (i > 0){
					document.insertString(document.getLength(), ", ", null);
				}else{
					document.insertString(document.getLength(), "\n", null);
				}
				document.insertString(document.getLength(), 
					Integer.toString(ter.getProdQuant(i))+ " " +
					GameWorld.RESOURCE_NAME[ter.getProdType(i)], null);
			}
			if (ter.getMove()>0) {
				document.insertString(document.getLength(), 
					"\n" + Integer.toString(ter.getMove()) + " movements cost\n", null);
			}
			//document.insertString(document.getLength(), "<html><i>" + ter.getProp("description") + "</i></html>\n", null);
			//System.out.println(ter.getProp("name"));
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
		for (int v=0; v<gd.getTopCount(x, y); v++) {
			int o = gd.getTopLand(x, y, v);	
			Overlay ovl = oloader.getOverlay(o);
			if (ovl != null){
				try{
				document.insertString(document.getLength(), "\n+" + ovl.getProp("name"), null);
				for (int i=0; i<ovl.getProductions(); i++){
					if (i > 0){
						document.insertString(document.getLength(), ", ", null);
					}else{
						document.insertString(document.getLength(), "\n", null);
					}
					document.insertString(document.getLength(), 
						Integer.toString(ovl.getProdQuant(i))+ " " +
						GameWorld.RESOURCE_NAME[ovl.getProdType(i)], null);
				}
				document.insertString(document.getLength(), "\n", null);
				if (ovl.getMove()>0) {
					document.insertString(document.getLength(), Integer.toString(ovl.getMove()) + " movements cost\n", null);
				}
				document.insertString(document.getLength(), ovl.getProp("description") + "\n", null);

				//MH110
				int bonus = ovl.getBonus();
				if (bonus>=0) {
					int bu = OverlayBonus.getBonusUpgrade(bonus);
					if (bu >= 0) {
						document.insertString(document.getLength(), world.gwGetTech(bu).getName() + " can be found here\n", null);
					}
				}

				}catch(Exception e){
					//e.printStackTrace();
				}
			}
		}

		if (a != null) {
			panUnit.setArmy(a);
			//panUnit.setVisible(true);	
			if (a.getOwner() == GameWorld.PLAYER_OWNER) {
				btnTalk.setEnabled(false);
			}
		}else{
			panUnit.setArmy(a);
			//panUnit.setVisible(false);	
		}

		//MH29 Temporarily save
		army = a;
		base = b;

		show();
	}
}

class TalkDialog extends JDialog implements ActionListener{
	private final static int IMG_W = 48, IMG_H = 48;
	//private javax.swing.text.SimpleAttributeSet[] attSet =
	//		{new javax.swing.text.SimpleAttributeSet(),
	//		new javax.swing.text.SimpleAttributeSet()};

	private ImagePanel imgLeft, imgRight, imgPortrait;
	private ArrayList buttons;
	private GameWorld world;
	private int player1, player2, offset, deal;//, attCnt1, attCnt2;
	private JTextPane txtMessage;
	private JTextPane txtConsult;
	private PrintStream prsMsg, prsConsult;

	public TalkDialog(Frame owner, GameWorld w, String[] list, int off){
		super(owner, "Requesting Your Audience", true);

		world = w;
		player1 = -1;
		player2 = -1;
		offset = off;
		imgLeft = new ImagePanel(IMG_W, IMG_H);
		imgRight = new ImagePanel(IMG_W, IMG_H);

		//javax.swing.text.StyleConstants.setForeground(attSet[0], Color.blue);
		//javax.swing.text.StyleConstants.setFontFamily(attSet[0],"Arial");
		//javax.swing.text.StyleConstants.setFontSize(attSet[0],12);

		//javax.swing.text.StyleConstants.setForeground(attSet[1], Color.red);
		//javax.swing.text.StyleConstants.setFontFamily(attSet[1],"Arial");
		//javax.swing.text.StyleConstants.setFontSize(attSet[1],12);

		//attCnt1 = 0;
		//attCnt2 = 0;

		txtMessage= new JTextPane();
		txtMessage.setEnabled(false);
		ListScrollPane scrP1 = new ListScrollPane(txtMessage, IMG_W * 5, IMG_H * 3);
		scrP1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		prsMsg = new PrintStream(new ConsoleOutStream(world, txtMessage, Color.BLUE), true);

		imgPortrait = new ImagePanel(IMG_W, IMG_H);
		imgPortrait.setImage(w.getGameIcon(GameWorld.IMG_ADVISOR_BIG));

		txtConsult= new JTextPane();
		txtConsult.setEnabled(false);
		ListScrollPane scrP2 = new ListScrollPane(txtConsult, IMG_W * 6, IMG_H * 2);
		scrP2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		prsConsult = new PrintStream(new ConsoleOutStream(world, txtConsult, Color.BLUE), true);

		JPanel panTemp3 = new JPanel(new BorderLayout());
		panTemp3.add(imgPortrait, BorderLayout.WEST);
		panTemp3.add(scrP2, BorderLayout.EAST);

		buttons = new ArrayList();
		JPanel panBut = new JPanel(new GridLayout(list.length + 1, 1));
		Color[] colors = {GameWorld.COL_GREEN, GameWorld.COL_GREEN_WHITE};
		for (int i=0; i<list.length; i++){
			JButton b = new JButton(list[i]);
			b.addActionListener(this);
			b.setBackground(colors[i % 2]);
			panBut.add(b);
			buttons.add(b);
		}

		//panBut.add(new DummyPanel());

		JButton btnDone = new JButton("I am done (Close dialog)");
		btnDone.setBackground(GameWorld.COL_HIGHLIGHT);
		btnDone.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();
			}
		});
		panBut.add(btnDone);
		
		JPanel panTemp2 = new JPanel(new BorderLayout());
		panTemp2.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
		panTemp2.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.EAST);
		panTemp2.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
		panTemp2.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);
		panTemp2.add(panBut, BorderLayout.CENTER);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);

		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(imgLeft, BorderLayout.WEST);
		panTemp.add(scrP1, BorderLayout.CENTER);
		panTemp.add(imgRight, BorderLayout.EAST);

		//getContentPane().setBackground(GameWorld.COL_GREEN);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panTemp, BorderLayout.NORTH);
		getContentPane().add(panTemp2, BorderLayout.CENTER);
		getContentPane().add(panTemp3, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	protected void finalize() throws Throwable{
		prsMsg.close();
		prsConsult.close();
	}

	public void actionPerformed(ActionEvent e){
		if (player1 == -1 || player2 == -1){
			return;
		}
		int index = buttons.indexOf(e.getSource()) + offset;
		world.doTalkAction(this, player1, player2, deal, index);
	}

	public void setMessage(String msg){
		//try{
		//	javax.swing.text.StyledDocument document = txtMessage.getStyledDocument();
		//	document.insertString(document.getLength(), msg + "\n\n", attSet[attCnt2]);
		//	if (++attCnt2 >= attSet.length){
		//		attCnt2 = 0;
		//	}
		//}catch(Exception e){
		//}
		prsMsg.println("<%" + GameWorld.IMG_SCROLL + "%>" + msg+"\n");
		//prsMsg.println();
		prsMsg.flush();
	}

	public void setConsult(String msg){
		//try{
		//	javax.swing.text.StyledDocument document = txtConsult.getStyledDocument();
		//	document.insertString(document.getLength(), msg + "\n\n", attSet[attCnt1]);
		//	if (++attCnt1 >= attSet.length){
		//		attCnt1 = 0;
		//	}
		//}catch(Exception e){
		//}
		prsConsult.println("<%" + GameWorld.IMG_SCROLL + "%>" + msg+"\n");
		//prsConsult.println();
		prsConsult.flush();
	}

	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}

	protected void clearMsg(){
		txtMessage.setText("");
		txtConsult.setText("");
	}

	public void showDialog(PlayerLoader pl, int p1, int p2){
		clearMsg();

		player1 = p1;
		player2 = p2;

		Player obj1 = pl.getPlayer(player1);
		Player obj2 = pl.getPlayer(player2);

		Image left = obj1.getIcon();
		Image right = obj2.getIcon();

		imgLeft.setImage(left);
		imgRight.setImage(right);

		if (p2 == GameWorld.PLAYER_OWNER){
			setMessage("My lord, we received a message from " + obj1.getName());
			setConsult("My lord, what do you think about this proposal?");
		}else{
			setMessage("["+obj1.getName()+"] To " + obj2.getName() + "," +
				obj2.getDescription() + ". Let us discuss some important matters.");
			setConsult("My lord, what is your plan?");
		}

		show();
	}

	public void showDialog(PlayerLoader pl, int p1, int p2, int d){
		clearMsg();

		player1 = p1;
		player2 = p2;
		deal = d;

		Player obj1 = pl.getPlayer(player1);
		Player obj2 = pl.getPlayer(player2);

		Image left = obj1.getIcon();
		Image right = obj2.getIcon();

		imgLeft.setImage(left);
		imgRight.setImage(right);

		if (p2 == GameWorld.PLAYER_OWNER){
			setMessage("My lord, we received a message from " + obj1.getName() + 
				" regarding a " + GameData.DIP_NAME[deal] + " treaty." +
				"\n\nWhat do you think?");
			setConsult("My lord, what do you think about this proposal?");
		}else{
			setMessage("["+obj1.getName()+"] To " + obj2.getName() + "," + obj2.getDescription() +
				", lets discuss about a " + GameData.DIP_NAME[deal] + " treaty.");
			setConsult("My lord, what is your plan?");
		}

		show();
	}
}

class DiplomacyDialog extends JDialog{
	private DiplomacyPanel panDip;
	private GameWorld world;
	private JLabel lblComment;

	public DiplomacyDialog(Frame owner, GameWorld w, PlayerLoader pl){
		super(owner, "Diplomacy", true);

		world = w;

		panDip = new DiplomacyPanel(pl, world);
		panDip.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				Point p = e.getPoint();
				Point p1 = panDip.getPosition(p.x, p.y);
				if (e.getButton() == MouseEvent.BUTTON1){
					if (p1.x == GameWorld.PLAYER_OWNER && 
						p1.y > GameWorld.PLAYER_OWNER &&
						p1.y < GameWorld.PLAYABLE_OWNER_SIZE){
						//to-do
						world.doTalk(GameWorld.PLAYER_OWNER, p1.y);
					}else if (p1.y == GameWorld.PLAYER_OWNER &&
						p1.x > GameWorld.PLAYER_OWNER &&
						p1.x < GameWorld.PLAYABLE_OWNER_SIZE){
						//to-do
						world.doTalk(GameWorld.PLAYER_OWNER, p1.x);
					}
				}else{
					if (p1.x == GameWorld.PLAYER_OWNER){
						//to-do
					}else if (p1.y == GameWorld.PLAYER_OWNER){
						//to-do
					}
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		lblComment = new JLabel(" ");
		
		//setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panDip, BorderLayout.CENTER);
		getContentPane().add(lblComment, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public void show(){
		lblComment.setText("To enter dialogs with other characters, click the boxes on first column");
		super.show();
	}

	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}
}

class DiplomacyPanel extends JComponent{
	private final static int ICON_SIZE = 56;

	private GameWorld world;
	private PlayerLoader ploader;
	private Image buffer;
	private Graphics2D gbuffer;

	public DiplomacyPanel(PlayerLoader pl, GameWorld w){
		//setBackground(GameWorld.COL_GREEN);
		ploader = pl;
		world = w;
	}

	public Point getPosition(int x, int y){
		return new Point((int)((x - ICON_SIZE) / ICON_SIZE), (int)((y - ICON_SIZE) / ICON_SIZE));
	}

	public Dimension getPreferredSize(){
		return new Dimension((GameWorld.OWNER_SIZE + 1) * ICON_SIZE + 1,
				(GameWorld.OWNER_SIZE + 1) * ICON_SIZE + 1);
	}

	//MIN06
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height){
		if (isShowing() && (infoflags & ALLBITS) != 0){
			repaint();
		}
		if (isShowing() && (infoflags & FRAMEBITS) != 0){
			repaint();
		}
		return isShowing();
	}

	public void paintComponent(Graphics g){
		if (buffer == null || gbuffer == null){
			buffer = createImage(getWidth(), getHeight());
			gbuffer = (Graphics2D)buffer.getGraphics();
			gbuffer.setFont(new Font("Arial", Font.PLAIN, 9));
		}
		//draw background
		int boxes = GameWorld.OWNER_SIZE + 1;
		int size =  boxes * ICON_SIZE;
		//first box
		gbuffer.setColor(GameWorld.COL_GREEN);
		gbuffer.fill3DRect(0, 0, ICON_SIZE, ICON_SIZE, false);
		//draw diplomacy
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			//if (i == GameWorld.PLAYER_OWNER || world.getGD().getDiplomacy(GameWorld.PLAYER_OWNER, i) != GameData.DIP_UNKNOWN){
				Player p = ploader.getPlayer(i);
				int x = i * ICON_SIZE + ICON_SIZE;
				//gbuffer.setColor(GameWorld.COL_GREEN_WHITE);
				gbuffer.setColor(GameWorld.OWNER_COLOR[i]);
				gbuffer.fill3DRect(x, 0, ICON_SIZE, ICON_SIZE, true);
				gbuffer.drawImage(p.getIcon(), x , 0, this);
				gbuffer.setColor(GameWorld.OWNER_TEXT_COLOR[i]);
				gbuffer.drawString(p.getName(), x+2, ICON_SIZE-2);
			//}
		}
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			//if (i == GameWorld.PLAYER_OWNER || world.getGD().getDiplomacy(GameWorld.PLAYER_OWNER, i) != GameData.DIP_UNKNOWN){
				Player p = ploader.getPlayer(i);
				int y = i * ICON_SIZE + ICON_SIZE;
				gbuffer.setColor(GameWorld.OWNER_COLOR[i]);
				//gbuffer.setColor(GameWorld.COL_GREEN_WHITE);
				gbuffer.fill3DRect(0, y, ICON_SIZE, ICON_SIZE, true);
				gbuffer.drawImage(p.getIcon(), 0, y, this);
				gbuffer.setColor(GameWorld.OWNER_TEXT_COLOR[i]);
				gbuffer.drawString(p.getName(), 2, y + ICON_SIZE-2);
			//}
		}
		for (int i=0; i<GameWorld.OWNER_SIZE; i++){
			//if (i == GameWorld.PLAYER_OWNER || world.getGD().getDiplomacy(GameWorld.PLAYER_OWNER, i) != GameData.DIP_UNKNOWN){
			for (int j=0; j<GameWorld.OWNER_SIZE; j++){
				//if (j == GameWorld.PLAYER_OWNER || world.getGD().getDiplomacy(GameWorld.PLAYER_OWNER, j) != GameData.DIP_UNKNOWN){
					int x = (i + 1) * ICON_SIZE;
					int y = (j + 1)* ICON_SIZE;

					if (i == j){
						gbuffer.setColor(GameWorld.COL_GREEN);
						gbuffer.fill3DRect(x, y, ICON_SIZE, ICON_SIZE, false);
						continue;
					}
					
					if ((i == 0 && j < GameWorld.NEUTRAL_OWNER) ||
						(j == 0 && i < GameWorld.NEUTRAL_OWNER)){
						gbuffer.setColor(getBackground());
					}else{
						gbuffer.setColor(GameWorld.COL_GREEN);
					}
					gbuffer.fill3DRect(x, y, ICON_SIZE, ICON_SIZE, true);
					gbuffer.setColor(GameData.DIP_COLOR[world.getGD().getDiplomacy(i, j)]);
					gbuffer.drawString(GameData.DIP_NAME[world.getGD().getDiplomacy(i, j)], x + 5, y + 30);
				//}
			}
			//}
		}
		//flip buffer
		g.drawImage(buffer, 0, 0, this);
	}
}

class HouseHolderPanel extends JPanel{
	public static final int MODE_TEXT = 0;
	public static final int MODE_BAR = 1;

	public static final int ROWS = 3;
	public static final int COLS = 4;
	public static final int SIZE = 32;
	public static final Color COL_WALL = new Color(169, 131, 79);

	private int mywidth, myheight, uwidth, uheight;
	private Image buffer;
	private Graphics2D gbuffer;
	private boolean running;
	private UnitTypeLoader uloader;
	private HouseTypeLoader hloader;
	private BaseTypeLoader bloader;
	private Base base;
	private MapPanel mp;
	private GameWorld world;
	private BasePanel bpan;

	private UnitSwappable select;
	private Point selection;
	private HouseType lastTipObj;

	private int quantityMode;

	public HouseHolderPanel(BasePanel bp, GameWorld wl, MapPanel m, int w, int h,
			UnitTypeLoader ul, HouseTypeLoader hl, BaseTypeLoader bl){
		super();
		bpan = bp;
		world = wl;
		mp = m;
		uloader = ul;
		hloader = hl;
		bloader = bl;
		mywidth = w;
		myheight = h;
		uwidth = SIZE;//(int)(mywidth / 2);
		uheight = SIZE;//(int)(myheight / 2);

		select = null;
		selection = null;
		lastTipObj = null;

		quantityMode = MODE_BAR;

		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (base != null){
					Point p = e.getPoint();
					int min = GameWorld.BEVEL_SIZE;
					int x = (int)((p.x - min) / mywidth);
					int y = (int)((p.y - min) / myheight);
					//MH115 Fix col/row index bug
					int index = y * COLS + x;
					//System.out.println("SELECTED: " + x + "/" + y + "=" + index);
					House h = base.getHouse(index);
					if (e.getButton()==MouseEvent.BUTTON1) {
						if (h != null){
							if (select == null){
								select = h;
								selection = new Point(x, y);
								bpan.notifySelection();
								repaint();
							}else if (select != h){
								world.showUnitTransfer(select, h, base.getOwner());
								clearSelection();
								setCursor(Cursor.getDefaultCursor());
								repaint();
							}else{
								clearSelection();
								setCursor(Cursor.getDefaultCursor());
								repaint();
							}
						}
					}else{
						if (h != null){
							int uidx = (int)((p.x - min - x * mywidth) / 16);
							//Leftover translate to max
							if (uidx>=h.getCount()) {
								uidx = h.getCount()-1;
							}
							//System.out.println(uidx);
							world.showUnitType(base, h.get(uidx), base.getOwner());
						}
					}
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				if (base != null){
					Point p = e.getPoint();
					int min = GameWorld.BEVEL_SIZE;
					int x = (int)((p.x - min) / mywidth);
					int y = (int)((p.y - min) / myheight);
					int index = y * COLS + x;
					House h = base.getHouse(index);
					if (h != null){
						HouseType ht = hloader.getHouse(h.getType());
						if (ht != lastTipObj){
							if (ht.getProduction() == GameWorld.RESOURCE_HAMMER){
								setToolTipText(ht.getName() +
									" provides for construction sites");
							}else if (ht.getProduction() == GameWorld.RESOURCE_BOOK){
								setToolTipText(ht.getName() +
									" provides for trainings");
							}else{
								setToolTipText(ht.getName() +" produces "+
									GameWorld.RESOURCE_NAME[ht.getProduction()]);
							}
							lastTipObj = ht;
						}
					}
				}
			}
			public void mouseDragged(MouseEvent e){
			}
		});
	}

	public void setMode(int m){
		quantityMode = m;
	}

	public int getMode(){
		return quantityMode;
	}

	public UnitSwappable getSelection(){
		return select;
	}

	public void clearSelection(){
		select = null;
		selection = null;
	}

	public void setBase(Base b){
		base = b;

		if (base == null){
			lastTipObj = null;
		}
	}

	public void showHouses(){
		if (gbuffer == null || base == null){
			return;
		}

		//gbuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int min = GameWorld.BEVEL_SIZE;

		Image img3 = world.getGameIcon(GameWorld.IMG_URBAN);
		gbuffer.drawImage(img3, min, min, COLS * mywidth + min, ROWS * myheight + min, 0, 0, img3.getWidth(this), img3.getHeight(this), this);

		for (int i=0; i<base.getHouseCount(); i++){
			//System.out.println("draw house");
			House h = base.getHouse(i);
			if (h != null){
				int ht = h.getType();
				if (ht != -1){
					HouseType housetype = hloader.getHouse(ht);
					Image img = housetype.getIcon();
					int y = (int)(i / COLS) * mywidth + min;
					int x = (i % COLS) * myheight + min;
					//draw house
					gbuffer.drawImage(img, x, y, x + mywidth, y + myheight,
						0, 0, mywidth, myheight, this);
					//draw unit
					for (int j = 0; j<h.getCount(); j++){
						Unit u = h.get(j);
						UnitType ut = uloader.getUnitType(u.getType());
						if (ut != null){
							Tile t = ut.getTile();
							Image img2 = t.getImage(base.getOwner());
							int ux = x + 12 * j;
							gbuffer.drawImage(img2, ux, y, ux + uwidth, y + uheight, 0, 0, mywidth, myheight, this);
						}
					}
					//draw production
					int[] p = h.getProduction();
					int r = housetype.getProduction();
					//if (p[r] > 0){
					if (p[r] != 0){
						int yb = y + myheight;
						//gbuffer.setColor(Color.red);
						//MIN40 26 -> 22
						mp.drawResource(gbuffer, this, r, x, yb - 22, 16);
						//gbuffer.drawString(Integer.toString(p[r]), x+1, yb);
						switch (quantityMode){
						 case MODE_BAR:
							int score = (int)(Math.abs(p[r]) / 15) + 5;
							if (p[r] > 0) {
								gbuffer.setColor(Color.blue);
							}else{
								gbuffer.setColor(Color.red);
							}
							gbuffer.fill3DRect(x + 1, yb-7, score, 5, true);
							break;
						 case MODE_TEXT:
							if (p[r] > 0) {
								gbuffer.setColor(Color.blue);
							}else{
								gbuffer.setColor(Color.red);
							}
							gbuffer.drawString(Integer.toString(Math.abs(p[r])), x+1, yb);
							break;
						 default:
							break;
						}
					}
				}
			}
		}
		//2
		//BaseType bt = bloader.getBaseType(base.getType());
		//if (bt != null){
		//	gbuffer.setColor(COL_WALL);
		//	for (int i=bt.getLimit(); i<ROWS*COLS; i++){
		//		int y = (int)(i / COLS) * mywidth + min;
		//		int x = (i % COLS) * myheight + min;
		//		gbuffer.fillRect(x, y, mywidth, myheight);
		//	}
		//}
		//3
		if (selection != null){
			int x = selection.x * mywidth + min;
			int y = selection.y * myheight + min;
			gbuffer.setColor(Color.blue);
			gbuffer.drawRect(x, y, mywidth-1, myheight-1);
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth * COLS + GameWorld.BEVEL_SIZE * 2,
			myheight * ROWS + GameWorld.BEVEL_SIZE * 2);
	}

	public boolean isOptimizedDrawingEnabled(){
		return true;
	}

	//MIN16 - JPanel uses paintComponent
	public void paintComponent(Graphics g){
		int min = GameWorld.BEVEL_SIZE;
		int wmx = mywidth * COLS;
		int hmx = myheight * ROWS;

		if (buffer == null || gbuffer == null){
			buffer = createImage(getWidth(), getHeight());
			gbuffer = (Graphics2D)buffer.getGraphics();
			//gbuffer.setRenderingHint(RenderingHints.KEY_RENDERING,
			//	RenderingHints.VALUE_RENDER_QUALITY);		

			gbuffer.setColor(getBackground());
			gbuffer.fillRect(0, 0, getWidth(), getHeight());

			gbuffer.setFont(new Font("Arial", Font.PLAIN, 9));
			//draw grid
			gbuffer.setColor(Color.white);
			gbuffer.fillRect(0, 0, wmx + min + min, min);
			gbuffer.fillRect(0, min, min, hmx + min);
			gbuffer.setColor(Color.gray);
			gbuffer.fillRect(min, hmx + min, wmx + min, min);
			gbuffer.fillRect(wmx + min, min, min, hmx + min);
		}
		//fill
		gbuffer.setColor(GameWorld.COL_GREEN);
		gbuffer.fillRect(min, min, wmx, hmx);
		//draw sites
		showHouses();
		g.drawImage(buffer, 0, 0, this);
	}
}

class SiteHolderPanel extends JPanel{
	public static final int ROWS = 3;
	public static final int COLS = 3;
	public static final int SIZE = 32;

	public static final int MODE_BAR = 0;
	public static final int MODE_TEXT = 1;

	private int mywidth, myheight, uwidth, uheight;
	private Image buffer;
	private Graphics2D gbuffer;
	private boolean running;
	private UnitTypeLoader uloader;
	private TerrainLoader tloader;
	private Base base;
	private MapPanel mp;

	private GameWorld world;
	private BasePanel bpan;

	private UnitSwappable select;
	private Point selection;
	private int quantityMode;

	public SiteHolderPanel(BasePanel bp, GameWorld wl, MapPanel m, int w, int h, UnitTypeLoader ul, TerrainLoader tl){
		super();
		bpan = bp;
		world = wl;
		mp = m;
		uloader = ul;
		tloader = tl;
		mywidth = w;
		uwidth = SIZE;//(int)(mywidth / 2);
		myheight = h;
		uheight = SIZE;//(int)(myheight / 2);

		select = null;
		selection = null;
		quantityMode = MODE_BAR;
		//quantityMode = MODE_TEXT;

		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (base != null){
					Point p = e.getPoint();
					int min = GameWorld.BEVEL_SIZE;
					int x = (int)((p.x - min) / mywidth);
					int y = (int)((p.y - min) / myheight);
					//MH115 Fix col/row index bug
					int index = y * COLS + x;
					Site s = base.getSite(index);

					if (e.getButton()==MouseEvent.BUTTON1) {
						if (s != null){
							if (select == null){
								select = s;
								selection = new Point(x, y);
								bpan.notifySelection();
								repaint();
							}else if (select != s){
								world.showUnitTransfer(select, s, base.getOwner());
								clearSelection();
								setCursor(Cursor.getDefaultCursor());
								repaint();
							}else{
								clearSelection();
								setCursor(Cursor.getDefaultCursor());
								repaint();
							}
						}
					}else{
						if (s != null){
							int uidx = (int)((p.x - min - x * mywidth) / 16);
							//Leftover translate to max
							if (uidx>=s.getCount()) {
								uidx = s.getCount()-1;
							}
							//System.out.println(uidx);
							world.showUnitType(base, s.get(uidx), base.getOwner());
						}
					}
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});
	}

	public void setQuantityMode(int m){
		quantityMode = m;
	}

	public int getQuantityMode(){
		return quantityMode;
	}

	public UnitSwappable getSelection(){
		return select;
	}

	public void setBase(Base b){
		base = b;
	}

	public void clearSelection(){
		select = null;
		selection = null;
	}

	public void showSites(){
		if (gbuffer == null || base == null){
			return;
		}

		int min = GameWorld.BEVEL_SIZE;
		
		for (int i=0; i<base.getSiteCount(); i++){
			Site s = base.getSite(i);
			if (s != null){
				int bl = s.getBase();
				int mx = s.getX();
				int my = s.getY();
				int y = (int)(i / COLS) * mywidth + min;
				int x = (i % COLS) * myheight + min;
				int by = y + myheight;
				//MIN40 26 -> 22
				int by2 = by - 22;

				mp.drawMapCell(gbuffer, this, mx, my, x, y);

				for (int j = 0; j<s.getCount(); j++){
					Unit u = s.get(j);
					UnitType ut = uloader.getUnitType(u.getType());
					if (ut != null){
						Tile t = ut.getTile();
						Image img = t.getImage(base.getOwner());
						int ux = x + 12 * j;
						int uy = y;
						gbuffer.drawImage(img, ux, uy, ux + uwidth, uy + uheight, 0, 0, mywidth, myheight, this);
					}
				}
				int[] p = s.getProduction();
				int pcount = 0;
				//MH131
				//for (int j=0; j<p.length && j<GameWorld.RESOURCE_HIDDEN; j++){
				for (int j=0; j<p.length && j<GameWorld.RESOURCE_SIZE; j++){
					if (p[j] != 0){
						//System.out.println(j + ":" + p[j]);
						int ux = x + 10 * pcount;
						mp.drawResource(gbuffer, this, j, ux, by2, 16);
						switch (quantityMode){
						 case MODE_BAR:
							int score = (int)(Math.abs(p[j]) / 15) + 5;
							if (p[j] > 0) {
								if (bl == GameWorld.TOWN_LAND) {
									gbuffer.setColor(Color.blue);
								}else{
									gbuffer.setColor(Color.green);
								}
							}else{
								gbuffer.setColor(Color.red);
							}
							gbuffer.fill3DRect(ux + 1, by-7, score, 5, true);
							break;
						 case MODE_TEXT:
							if (p[j] > 0) {
								gbuffer.setColor(Color.blue);
							}else{
								gbuffer.setColor(Color.red);
							}
							gbuffer.drawString(Integer.toString(Math.abs(p[j])), ux + 1, by);
							break;
						 default:
							break;
						}
						pcount++;
					}
				}
			}
		}
		if (selection != null){
			int x = selection.x * mywidth + min;
			int y = selection.y * myheight + min;
			gbuffer.setColor(Color.blue);
			gbuffer.drawRect(x, y, mywidth-1, myheight-1);
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth * COLS + GameWorld.BEVEL_SIZE * 2,
			myheight * ROWS + GameWorld.BEVEL_SIZE * 2);
	}

	//MIN06
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height){
		if (isShowing() && (infoflags & ALLBITS) != 0){
			repaint();
		}
		if (isShowing() && (infoflags & FRAMEBITS) != 0){
			repaint();
		}
		return isShowing();
	}

	public synchronized void paintComponent(Graphics g){
		if (buffer == null || gbuffer == null){
			buffer = createImage(getWidth(), getHeight());
			gbuffer = (Graphics2D)buffer.getGraphics();
			//gbuffer.setRenderingHint(RenderingHints.KEY_RENDERING,
			//	RenderingHints.VALUE_RENDER_QUALITY);		
			gbuffer.setFont(new Font("Arial", Font.PLAIN, 9));
			//init
			int min = GameWorld.BEVEL_SIZE;
			int wmx = mywidth * COLS;
			int hmx = myheight * ROWS;

			gbuffer.setColor(getBackground());
			gbuffer.fillRect(0, 0, getWidth(), getHeight());

			//draw grid
			gbuffer.setColor(Color.white);
			gbuffer.fillRect(0, 0, wmx + min + min, min);
			gbuffer.fillRect(0, min, min, hmx + min);
			gbuffer.setColor(Color.gray);
			gbuffer.fillRect(min, hmx + min, wmx + min, min);
			gbuffer.fillRect(wmx + min, min, min, hmx + min);
		}

		//draw sites
		showSites();
		g.drawImage(buffer, 0, 0, this);
	}
}

class ImageCellRenderer extends JLabel implements ListCellRenderer {
	public static final int ICON_TYPE_LIST = 0;
	public static final int ICON_TYPE_HASH = 0;

	private int type;
	private ArrayList iconList;
	private Hashtable iconHash;

	public ImageCellRenderer(ArrayList ilst){
		iconList = ilst;
		type = ICON_TYPE_LIST;
	}

	public void setIconList(ArrayList ilst){
		iconList = ilst;
		type = ICON_TYPE_LIST;
	}

	public ImageCellRenderer(int t){
		type = t;
	}

	public void setIconHash(Hashtable ilst){
		iconHash = ilst;
		type = ICON_TYPE_HASH;
	}

	public Component getListCellRendererComponent(
	   JList list,				// the list
	   Object value,            // value to display
	   int index,               // cell index
	   boolean isSelected,      // is the cell selected
	   boolean cellHasFocus)    // the list and the cell have the focus
	{
		String s = value.toString();
		setText(s);

		if (type == ICON_TYPE_LIST) {
			ImageIcon ic = (ImageIcon)iconList.get(index);
			if (ic == null) {
				//System.out.println("NULL ICON");
			}else{
				setIcon(ic);
			}
		}else if (type == ICON_TYPE_HASH) {
			setIcon((ImageIcon)iconHash.get(value));
		}

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);

		return this;
	}
}

class ListScrollPane extends JScrollPane{
	private int mywidth, myheight;

	public ListScrollPane(Component view, int w, int h){
		super(view);

		mywidth = w;
		myheight = h;
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth, myheight);
	}
	public Dimension getMaximumSize(){
		return new Dimension(mywidth, myheight);
	}
}

class BuildingPanel extends JPanel{
	private int mywidth, myheight;
	private Base base;
	private JList lstOrder;
	private JLabel lblStatus;
	private HouseTypeLoader hloader;
	private ImageCellRenderer icr;

	public BuildingPanel(int w, int h, HouseTypeLoader hl){
		super();
		mywidth = w;
		myheight = h;
		hloader = hl;

		ArrayList names = new ArrayList();
		for (int i=0; i<hloader.getSize(); i++){
			HouseType ht = hloader.getHouse(i);
			String name = ht.getName();
			int[] cost = ht.getCost();

			if (name == null){
				name = "No named house";
			}

			name += " (";
			int count = 0;
			for (int j=0; j<cost.length; j++){
				if (cost[j] > 0){
					if (count > 0){
						name += ",";
					}
					name += Integer.toString(cost[j]) + " " + GameWorld.RESOURCE_NAME[j];
					count++;
				}
			}
			name += ")";

			names.add(name);
		}

		icr = new ImageCellRenderer(hloader.getIconList());
		lstOrder = new JList(names.toArray());
		lstOrder.setCellRenderer(icr);
		lstOrder.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				if (e.getValueIsAdjusting()){
					return;
				}
				if (base != null){
					int order = lstOrder.getSelectedIndex();
					base.setBuildOrder(order);
					showStatus(order);
				}
			}
		});
		ListScrollPane scrPane = new ListScrollPane(lstOrder, mywidth, myheight - 15);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		lblStatus = new JLabel("<html>No Building Order<br></html>");

		setLayout(new BorderLayout());
		add(scrPane, BorderLayout.NORTH);
		add(lblStatus, BorderLayout.SOUTH);
	}

	public void refresh(){
		if (base != null) {
			showStatus(base.getBuildOrder());
		}
	}

	protected void showStatus(int order){
		if (order <= -1){
			lblStatus.setText("<html>No Building Order<br></html>");
			return;
		}
		HouseType ht = hloader.getHouse(order);

		if (ht != null){
			int[] cost = ht.getCost();
			int hammer = base.getResource(GameWorld.RESOURCE_HAMMER);
			int percent = (int)(hammer * 100 / cost[GameWorld.RESOURCE_HAMMER]);

			lblStatus.setText("<html>"+ ht.getName() + " " + percent + "% (" +
				hammer + " hammers)<br>" + ht.getDescription() + "</html>");	
		}
	}

	public void setBase(Base b){
		base = b;
		if (base != null){
			int order = base.getBuildOrder();
			if (order <= -1){
				lstOrder.clearSelection();
			}else{
				lstOrder.setSelectedIndex(order);
			}
			showStatus(order);
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth, myheight);
	}
}

class InventoryReportDialog extends JDialog{
	private JTextPane txtReport;
	private javax.swing.text.SimpleAttributeSet attNormal;
	private javax.swing.text.SimpleAttributeSet attHeader;

	public InventoryReportDialog(Frame owner){
		super(owner, "Inventory Report", true);

		attNormal = new javax.swing.text.SimpleAttributeSet();
		javax.swing.text.StyleConstants.setForeground(attNormal, Color.blue);
		javax.swing.text.StyleConstants.setFontFamily(attNormal,"Verdana");
		javax.swing.text.StyleConstants.setFontSize(attNormal,10);

		attHeader = new javax.swing.text.SimpleAttributeSet();
		javax.swing.text.StyleConstants.setForeground(attHeader, Color.red);
		javax.swing.text.StyleConstants.setFontFamily(attHeader,"Verdana");
		javax.swing.text.StyleConstants.setFontSize(attHeader,12);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);

		txtReport = new JTextPane();
		txtReport.setBackground(getBackground());
		txtReport.setEnabled(false);
		txtReport.setParagraphAttributes(attNormal,false);
		
		ListScrollPane scrPane = new ListScrollPane(txtReport, 320, 400);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrPane, BorderLayout.CENTER);

		pack();
	}

	public void setReport(String txt){
		int start = txt.indexOf("<%");
		if (start == -1){
			txtReport.setText(txt);
		}else{
			javax.swing.text.StyledDocument document = txtReport.getStyledDocument();
			int last = 0;
			//clear
			txtReport.setText("");
			while (start > -1){
				int end = txt.indexOf("%>", start);
				if (end > -1){
					String strOut = txt.substring(last, start);
					String strIn = txt.substring(start + 2, end);
					try{
						document.insertString(document.getLength(), strOut, attNormal);
						document.insertString(document.getLength(), strIn, attHeader);
					}catch(Exception e){
					}
				}else{
					//tolerate
					end = start;
				}
				last = end + 2;
				start = txt.indexOf("<%", end + 2);
			}
			if (last < txt.length()){
				String strOut = txt.substring(last, txt.length());
				try{
					document.insertString(document.getLength(), strOut, attNormal);
				}catch(Exception e){
				}
			}
		}
	}
	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}
}

class BasePanel extends JPanel{
//class BasePanel extends VerticalTabbedPane{
	public final static java.text.DecimalFormat CURRENCY_FORMAT = 
		new java.text.DecimalFormat("#,##0.##");

	private SiteHolderPanel panSite;
	private HouseHolderPanel panHouse;
	private ResHolderPanel panRes;
	private BuildingPanel panBuild;
	private TrainingPanel panTrain;

	private JButton btnReport, btnForm, btnTrade, btnDismiss, btnOrganize;
	private JCheckBox chkAutomate, chkTax;
	private JLabel lblResearch, lblCity, lblRes;
	private JTabbedPane jtpBase;
	private JList lstProp;

	private GameWorld world;
	private Base base;
	private int lastResIndex;

	protected String getIntTag(int v){
		if (v > 0){
			return "<%" + Integer.toString(v) + "%>";
		}else{
			return Integer.toString(v);
		}
	}

	public BasePanel(GameWorld w, MapPanel mp, UnitTypeLoader ul, TerrainLoader tl, HouseTypeLoader hl, BaseTypeLoader bl){

		super();
		//Vertical tab with tabs on left hand side
		//super(JTabbedPane.LEFT);

		world = w;
		lastResIndex = -1;

		lblCity = new JLabel("Rural & urban view");
		lblCity.setForeground(Color.blue);
		lblCity.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblCity.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (base != null){
					String newName = JOptionPane.showInputDialog("Rename town:");
					if (newName != null && newName.length() > 0){
						int len = (newName.length() > 20)?20:newName.length();
						base.setName(newName.substring(0, len)); 
						lblCity.setText("<html><b>"+base.getName()+"</b>: Rural & urban View</html>");
					}
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		lblResearch = new JLabel("Research: No orders");
		lblResearch.setForeground(Color.blue);
		lblResearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblResearch.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (base != null){
					world.doResearch(base);
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		panSite = new SiteHolderPanel(this, w, mp, 48, 48, ul, tl);
		panHouse = new HouseHolderPanel(this, w, mp, 48, 48, ul, hl, bl);
		//MIN35
		panRes = new ResHolderPanel(mp, 32, 40, 0, GameWorld.RESOURCE_TRANSFERABLE);
		panRes.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
			world.getGameIcon(GameWorld.IMG_DOLLAR), new Point(16,16), "Dollar"));
		panRes.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				//non usable
				if (base == null){
					return;
				}

				//index
				Point p = e.getPoint();
				int index = panRes.getIndex(p.x, p.y);

				//error
				if (index < 0 || index > GameWorld.RESOURCE_SIZE){
					return;
				}

				if (index == 0){
					world.doResearch(base);
				}else{
					//amount
					int amount = 10;
					boolean res;
					if (e.isShiftDown()){
						amount = 1;
					}else if (e.isAltDown()) {
						amount = 100;
					}
					//load
					if (e.getButton() == MouseEvent.BUTTON1){
						//MIN28 you cant sell hammers or books
						if (index < GameWorld.RESOURCE_HIDDEN){
							res = world.sellResource(base, index, amount);
						}
					}else{
					//unload
						res = world.buyResource(base, index, amount);
					}
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});
		panRes.addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				if (base != null){
					Point p = e.getPoint();
					int index = panRes.getIndex(p.x, p.y);
					if (index < 0 || index >= GameWorld.RESOURCE_SIZE ||
						index == lastResIndex){
						return;
					}
					if (index == GameWorld.RESOURCE_CURRENCY){
						lblRes.setText(GameWorld.RESOURCE_NAME[index]+ " (Main currency)");
					}else{
						float realCost = world.getResourceCost(index, base.getResource(index));
						//lblRes.setText(GameWorld.RESOURCE_NAME[index]+ ", price: " + CURRENCY_FORMAT.format(GameWorld.RESOURCE_COST[index]) + " " + GameWorld.RESOURCE_NAME[GameWorld.RESOURCE_CURRENCY]);
						lblRes.setText(GameWorld.RESOURCE_NAME[index]+ ", price $" + CURRENCY_FORMAT.format(realCost) + " " + GameWorld.RESOURCE_NAME[GameWorld.RESOURCE_CURRENCY] + "/" + GameWorld.RESOURCE_LIMIT[index] + " sps");
					}
					lastResIndex = index;
				}
			}
			public void mouseDragged(MouseEvent e){
			}
		});

		panBuild = new BuildingPanel(240, 134, hl);
		panTrain = new TrainingPanel(240, 134, ul);

		btnReport = new JButton("Forecast");
		btnReport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}
				String strReport = "<%Inventory Report%>\n";
				strReport += "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n";
				strReport += "<%Production today:%>\n";
				strReport += "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n";
				String strPro = "";
				String strCon = "";
				String strBal = "";

				//temporary
				int[] pro = new int[GameWorld.RESOURCE_HIDDEN];
				int[] con = new int[GameWorld.RESOURCE_HIDDEN];
				int[] bal = new int[GameWorld.RESOURCE_HIDDEN];
				//pros
				for (int i=0; i<base.getSiteCount(); i++){
					Site s = base.getSite(i);
					int[] p = s.getProduction();
					for (int j=0; j<p.length && j<pro.length; j++){
						pro[j] += p[j];
					}
				}
				for (int i=0; i<base.getHouseCount(); i++){
					House h = base.getHouse(i);
					int[] p = h.getProduction();
					for (int j=0; j<p.length && j<pro.length; j++){
						pro[j] += p[j];
					}
				}
				//cons
				for (int i=0; i<base.getSiteCount(); i++){
					Site s = base.getSite(i);
					int[] p = s.getConsumption();
					for (int j=0; j<p.length && j<con.length; j++){
						con[j] += p[j];
					}
				}
				for (int i=0; i<base.getHouseCount(); i++){
					House h = base.getHouse(i);
					int[] p = h.getConsumption();
					for (int j=0; j<p.length && j<con.length; j++){
						con[j] += p[j];
					}
				}
				//output
				for (int i=0; i<pro.length && i<con.length; i++){
					bal[i] = pro[i] - con[i];
					strPro += GameWorld.RESOURCE_NAME[i] + ":\t\t" +
							getIntTag(pro[i]) + "\n";
					strCon += GameWorld.RESOURCE_NAME[i] + ":\t\t" +
							getIntTag(con[i]) + "\n";
					strBal += GameWorld.RESOURCE_NAME[i] + ":\t\t" +
							getIntTag(bal[i]);
					if (i > 0){
						strBal += " (Est. Price: " + 
							CURRENCY_FORMAT.format(GameWorld.RESOURCE_COST[i]) + " "+
							GameWorld.RESOURCE_NAME[GameWorld.RESOURCE_CURRENCY]+")";
					}
					strBal += "\n";
				}
				int growing = (GameWorld.getResourceGrow(base.getPopulation()) - base.getResource(GameWorld.RESOURCE_FOOD)) /	bal[GameWorld.RESOURCE_FOOD];
				if (growing < 0) {
					growing = 0;
				}
				strReport += strPro;
				strReport += "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n";
				strReport += "<%Consumption today:%>\n";
				strReport += "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n";
				strReport += strCon;
				strReport += "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n";
				strReport += "<%Proposed Forward Balance:%>\n";
				strReport += "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n";
				strReport += strBal;
				strReport += "\nPopulation is growing in ";
				strReport += getIntTag(growing) + " " + GameWorld.TURN_UNIT_PLURAL;

				world.showInvReport(strReport);
			}
		});

		btnForm = new JButton("Enlist unit");
		btnForm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}

				UnitSwappable s1 = panSite.getSelection();
				UnitSwappable s2 = panHouse.getSelection();

				if (s1 != null){
					if (!world.formArmy(base, s1)){
						GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Unable to form new army, no units selected or the area is already occupied");
					}
					panSite.clearSelection();
					panSite.setCursor(Cursor.getDefaultCursor());
					panSite.repaint();
				}else if (s2 != null){
					if (!world.formArmy(base, s2)){
						GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Unable to form new army, no units selected or the area is fully occupied.");
					}
					panHouse.clearSelection();
					panHouse.setCursor(Cursor.getDefaultCursor());
					panHouse.repaint();
				}else{
					GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Please select a site or a house with residents.");
				}
			}
		});

		lblRes = new JLabel(" ");

		btnTrade = new JButton("Demolish site");
		btnTrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}
				//world.showTraderInventory();
				//MIN40
				House h = (House)panHouse.getSelection();
				if (h != null){
					if (world.doDemolition(base, h)){
						panHouse.clearSelection();
						panHouse.setCursor(Cursor.getDefaultCursor());
						panHouse.repaint();
						panRes.repaint();
					}
				}else{
					GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Please select a house.");
				}
			}
		});

		btnDismiss = new JButton("Dismiss unit");
		btnDismiss.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}

				UnitSwappable s1 = panSite.getSelection();
				UnitSwappable s2 = panHouse.getSelection();

				if (s1 != null){
					if (world.doDismissal(s1, 0)){
						panSite.clearSelection();
						panSite.setCursor(Cursor.getDefaultCursor());
						panSite.repaint();
					}
				}else if (s2 != null){
					if (world.doDismissal(s2, 0)){
						panHouse.clearSelection();
						panHouse.setCursor(Cursor.getDefaultCursor());
						panHouse.repaint();
					}
				}else{
					GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Please select a site or a house with residents.");
				}
			}
		});

		btnOrganize = new JButton("Auto-arrange");
		btnOrganize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}
				if (world.doAutoArrange(base)) {
					refresh();
				}
			}
		});

		chkAutomate = new JCheckBox("Automate Production");
		chkAutomate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}
				if (chkAutomate.isSelected()){
					base.setFlag(Base.MASK_AUTOMATE);
				}else{
					base.clearFlag(Base.MASK_AUTOMATE);
				}
			}
		});

		chkTax = new JCheckBox("Tax population (1g/p)");
		chkTax.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base == null){
					return;
				}
				if (chkTax.isSelected()){
					base.setFlag(Base.MASK_TAX);
				}else{
					base.clearFlag(Base.MASK_TAX);
				}
			}
		});

		lstProp = new JList();
		ListScrollPane scrPane = new ListScrollPane(lstProp, 48 * 9, 140);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel panTemp7 = new JPanel(new GridLayout(6, 1));

		JButton btnResearch = new JButton("Research");
		btnResearch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base != null){
					world.doResearch(base);
				}
			}
		});

		JButton btnCharity = new JButton("Charity");
		btnCharity.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base != null){
					if (world.sendCharity(base, 100)){
						refresh();
					}
				}
			}
		});

		JButton btnCurfew = new JButton("Setup curfew");
		btnCurfew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base != null){
					if (world.setupCurfew(base, 100)){
						refresh();
					}
				}
			}
		});

		panTemp7.add(chkAutomate);
		panTemp7.add(chkTax);
		panTemp7.add(btnResearch);
		panTemp7.add(btnCharity);
		panTemp7.add(btnCurfew);

		JPanel panTemp3 = new JPanel(new BorderLayout());
		panTemp3.add(lblResearch, BorderLayout.NORTH);
		panTemp3.add(scrPane, BorderLayout.WEST);
		panTemp3.add(panTemp7, BorderLayout.EAST);

		JPanel panTemp5 = new JPanel(new GridLayout(6, 1));
		panTemp5.add(btnForm);
		panTemp5.add(btnDismiss);
		panTemp5.add(btnTrade);
		panTemp5.add(btnOrganize);
		panTemp5.add(btnReport);

		JPanel panTemp4 = new JPanel();
		panTemp4.setLayout(new BorderLayout());
		panTemp4.add(panRes, BorderLayout.CENTER);
		panTemp4.add(lblRes, BorderLayout.SOUTH);
		panTemp4.add(panTemp5, BorderLayout.EAST);

		JPanel panTemp = new JPanel();
		panTemp.setLayout(new BorderLayout());
		panTemp.add(lblCity, BorderLayout.NORTH);
		panTemp.add(panSite, BorderLayout.WEST);
		panTemp.add(panHouse, BorderLayout.CENTER);
		panTemp.add(panTemp4, BorderLayout.EAST);

		JPanel panTemp8 = new JPanel(new GridLayout(6, 1));

		JButton btnHurryTraining = new JButton("Hurry training");
		btnHurryTraining.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base != null){
					if (world.hurryTraining(base)) {
						panTrain.refresh();
					}
				}
			}
		});
		JButton btnHurryBuilding = new JButton("Hurry building");
		btnHurryBuilding.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (base != null){
					if (world.hurryBuilding(base)){
						panBuild.refresh();
					}
				}
			}
		});

		panTemp8.add(btnHurryTraining);
		panTemp8.add(btnHurryBuilding);

		JPanel panTemp2 = new JPanel();
		panTemp2.setLayout(new BorderLayout());
		panTemp2.add(new JLabel("Constructions and trainings"), BorderLayout.NORTH);
		panTemp2.add(panTrain, BorderLayout.WEST);
		panTemp2.add(panBuild, BorderLayout.CENTER);
		panTemp2.add(panTemp8, BorderLayout.EAST);

		jtpBase = new JTabbedPane(JTabbedPane.LEFT);
		jtpBase.setOpaque(true);
		jtpBase.addTab("Population", panTemp);
		jtpBase.addTab("Production queue", panTemp2);
		jtpBase.addTab("Governor office", panTemp3);

		add(jtpBase);
	}

	public void refresh(){
		String txtRes = "<html>Research: " + GameWorld.TECH_NAME[base.getRType()] + " (<b>"	+ Integer.toString(base.getRRate()) + "</b> " + GameWorld.RESOURCE_NAME[GameWorld.RESOURCE_CURRENCY] + " per "+GameWorld.TURN_UNIT+")</html>";

		lblResearch.setText(txtRes);

		panTrain.refresh();
		panBuild.refresh();

		//attributes = movement, combat points, has leader
		ArrayList atts = new ArrayList();
		atts.add("<html><b>Town: " + base.getName() + "</b></html>");

		//BaseType bt = bloader.getBaseType(base.getType());
		//int limit = bt.getLimit();
		//atts.add(Integer.toString(base.getHouseCount()) + " / " + Integer.toString(limit) + " houses");
		int numHouse = base.getHouseCount();
		atts.add("<html>Quarters: " + repeatImage(numHouse, "house.gif") + "</html>");
		int numPerson = base.getPopulation();
		int num4s = (int)(numPerson / 4);
		int num1s = numPerson - num4s * 4;
		atts.add("<html>Population: " + repeatImage(num4s, "pop4s.gif") + repeatImage(num1s, "pop.gif") + "</html>");
		if (numPerson >= numHouse * House.MAX_RESIDENT + Site.MAX_RESIDENT * 9) {
			atts.add("<html>+<i>Town is currently full, no space left for new persons</i></html>");
		}
		//separator
		//atts.add(" ");

		atts.add(txtRes);

		int mpercent = (int)(base.getResource(GameWorld.RESOURCE_HAPPY) * GameWorld.RESOURCE_SLICE / GameWorld.RESOURCE_LIMIT[GameWorld.RESOURCE_HAPPY]);
		if (mpercent < 1) {
			atts.add("Town people are not happy.");
		}else{
			atts.add("<html>Happiness: " + repeatImage(mpercent, "heart.gif") + " (" + Integer.toString(base.getHappyBonus()) + "% production bonus)" +"</html>");
		}
		atts.add("<html>+<i>To increase happiness, provide more entertainment or charity</i></html>");
		//separator
		//atts.add(" ");

		int spercent = (int)(base.getResource(GameWorld.RESOURCE_SECURITY) * GameWorld.RESOURCE_SLICE / GameWorld.RESOURCE_LIMIT[GameWorld.RESOURCE_SECURITY]);
		if (spercent < 1) {
			atts.add("Town security is lacking.");
		}else{
			atts.add("<html>Security: " + repeatImage(spercent, "sword.gif") + " (" + Integer.toString(base.getSecurityBonus()) + "% defence bonus)" + "</html>");
		}
		atts.add("<html>+<i>To increase security, have more militia units in town</i></html>");

		atts.add("Job office: " + Integer.toString(world.getGD().getJobSize(base.getOwner())) + " jobs available");

		lstProp.setListData(atts.toArray());
	}

	private String repeatImage(int times, String image){
		String url = world.getURL("images/"+image);
		String result = "";
		for (int i=0; i<times; i++){
			result += "<img src=\""+url+"\" border=\"0\">";
		}
		return result;
	}

	public void setBase(Base b){
		if (b == null){
			//setVisible(false);
		}else{
			jtpBase.setVisible(false);
			base = b;

			if (base.getFlag(Base.MASK_AUTOMATE) == Base.MASK_AUTOMATE){
				chkAutomate.setSelected(true);
			}else{
				chkAutomate.setSelected(false);
			}
			if (base.getFlag(Base.MASK_TAX) == Base.MASK_TAX){
				chkTax.setSelected(true);
			}else{
				chkTax.setSelected(false);
			}

			lblCity.setText("<html><b>"+base.getName()+"</b>: Rural & urban View</html>");
			lblRes.setText(" ");

			panSite.clearSelection();
			panHouse.clearSelection();

			panSite.setCursor(Cursor.getDefaultCursor());
			panHouse.setCursor(Cursor.getDefaultCursor());

			panBuild.setBase(base);
			panTrain.setBase(base);

			panSite.setBase(base);
			panHouse.setBase(base);
			panRes.setResourceHolder(base);
			//tips
			if (base != null){
				panRes.setToolTipText("Left click to sell, Right click to buy");
			}else{
				panRes.setToolTipText("");
			}

			refresh();

			//System.out.println(base.getTrainOrder());
			jtpBase.setVisible(true);
			//setVisible(true);
		}
	}

	public void notifySelection(){
		UnitSwappable s1 = panSite.getSelection();
		UnitSwappable s2 = panHouse.getSelection();

		if (s1 != null && s2 != null){
			world.showUnitTransfer(s1, s2, base.getOwner());

			panSite.clearSelection();
			panHouse.clearSelection();

			panSite.repaint();
			panHouse.repaint();
			
			panSite.setCursor(Cursor.getDefaultCursor());
			panHouse.setCursor(Cursor.getDefaultCursor());

			
			panSite.setCursor(Cursor.getDefaultCursor());
			panHouse.setCursor(Cursor.getDefaultCursor());
		}else{
			panSite.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			panHouse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	public boolean isOptimizedDrawingEnabled(){
		return true;
	}
}

class TrainingPanel extends JPanel{
	private int mywidth, myheight;
	private Base base;
	private JList lstOrder;
	private JLabel lblStatus;
	private UnitTypeLoader uloader;
	private ImageCellRenderer icr;

	public TrainingPanel(int w, int h, UnitTypeLoader ul){
		super();
		mywidth = w;
		myheight = h;
		uloader = ul;

		icr = new ImageCellRenderer(null);
		lstOrder = new JList();
		lstOrder.setCellRenderer(icr);
		lstOrder.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				if (e.getValueIsAdjusting()){
					return;
				}
				if (base != null){
					int order = lstOrder.getSelectedIndex();
					//MIN29 - to-do lame way
					if (order <= -1){
						return;
					}
					base.setTrainOrder(order);
					showStatus(order);
					//lstOrder.setToolTipText(getDisplayItem(order));
				}
			}
		});
		//MH160
		//JTree jtrTest = new JTree(uloader);
		//ListScrollPane scrPane = new ListScrollPane(jtrTest, mywidth, myheight - 15);
		ListScrollPane scrPane = new ListScrollPane(lstOrder, mywidth, myheight - 15);
		//MH160
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		lblStatus = new JLabel("<html>No Training Order<br></html>");

		setLayout(new BorderLayout());
		add(scrPane, BorderLayout.NORTH);
		add(lblStatus, BorderLayout.SOUTH);
	}

	public void refresh(){
		if (base != null) {
			showStatus(base.getTrainOrder());
		}
	}

	protected String getDisplayItem(int i){
		UnitType ut = uloader.getCustom(base.getOwner(), i);
		String name = ut.getName();
		//int utrait = ut.getRequireTrait();
		int[] cost = ut.getCost();

		if (name == null){
			name = "No named unit";
		}

		name += " (";
		int count = 0;
		for (int j=0; j<cost.length; j++){
			if (cost[j] > 0){
				if (count > 0){
					name += ",";
				}
				name += Integer.toString(cost[j]) + " " + GameWorld.RESOURCE_NAME[j];
				count++;
			}
		}
		name += ")";

		return name;
	}

	protected void showStatus(int order){
		if (order <= -1){
			lblStatus.setText("<html>No Training Order<br></html>");
			return;
		}
		UnitType ut = uloader.getCustom(base.getOwner(), order);

		if (ut != null){
			int[] cost = ut.getCost();
			int utrait = ut.getRequireTrait();
			int book = base.getResource(GameWorld.RESOURCE_BOOK);
			int percent = (int)(book * 100 / cost[GameWorld.RESOURCE_BOOK]);

			if (utrait > -1 && utrait < GameWorld.TRAIT_NAME.length) {
				lblStatus.setText("<html>" + ut.getName() + " " + percent +
					"% (" + book + " books)<br>" + ut.getDescription() +
					" (" + GameWorld.TRAIT_NAME[utrait] + " only)"+ "</html>");
			}else{
				lblStatus.setText("<html>" + ut.getName() + " " + percent +
					"% (" + book + " books)<br>" + ut.getDescription() + "</html>");
			}
		}
	}

	public void setBase(Base b){
		base = b;
		if (base != null){
			ArrayList names = new ArrayList();
			for (int i=0; i<uloader.getCustomSize(base.getOwner()); i++){
				names.add(getDisplayItem(i));
			}

			icr.setIconList(uloader.getCustomIconList(base.getOwner()));
			lstOrder.setListData(names.toArray());

			int order = base.getTrainOrder();
			lstOrder.setSelectedIndex(order);

			showStatus(order);
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth, myheight);
	}
}

class UnitHolderPanel extends JPanel{
	public static final int ROWS = 3;
	public static final int COLS = 3;
	public static final int OFFSET = 12;

	private int mywidth, myheight;
	private Image buffer;
	private Graphics gbuffer;
	private boolean running;
	private UnitTypeLoader uloader;
	private GameWorld world;
	private Army army;

	private UnitType lastTipObj;
	private int selection;
	private boolean details;

	public UnitHolderPanel(int w, int h, UnitTypeLoader ul, GameWorld wl, boolean det){
		super();
		uloader = ul;
		world = wl;
		details = det;

		mywidth = w;
		myheight = h;

		lastTipObj = null;
		selection = -1;

		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				//MIN43
				if (!isEnabled()){
					return;
				}
				//MIN43
				if (army != null){
					Point p = e.getPoint();
					int x = (int)(p.x / mywidth);
					int y = (int)(p.y / myheight);
					int index = y * ROWS + x;

					Unit u = army.get(index);
					if (u != null){
						if (e.getButton() == MouseEvent.BUTTON1){
							if (selection > -1){
								army.swap(index, selection);
								clearSelection();
								repaint();
							}else{
								selection = index;
								repaint();
							}
						}else{
							world.showUnitType(army, u, army.getOwner());
						}
					}
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				if (army != null){
					Point p = e.getPoint();
					int x = (int)(p.x / mywidth);
					int y = (int)(p.y / myheight);
					int index = y * ROWS + x;
					Unit u = army.get(index);
					if (u != null){
						UnitType ut = uloader.getUnitType(u.getType());
						if (ut != lastTipObj){
							if (ut.getType() == UnitType.TYPE_KING){
								//MIN43
								if (!isEnabled()){
									setToolTipText(ut.getName() + " - " + ut.getDescription());
								}else{
									setToolTipText(ut.getName() + " (Right click for details)");
								}
								//MIN43
							}else{
								//MIN43
								if (!isEnabled()){
									setToolTipText(ut.getName() + " - " + ut.getDescription());
								}else{
									setToolTipText(UnitType.LEVEL_NAME[u.getLevel()] +
										" " + ut.getName() + " (Right click for details)");
								}
							}
							lastTipObj = ut;
						}
					}
				}
			}
			public void mouseDragged(MouseEvent e){
			}
		});
	}

	public void clearSelection(){
		selection = -1;
	}

	public void setArmy(Army a){
		army = a;

		if (army == null){
			lastTipObj = null;
			clearSelection();
		}
	}

	public int getSelection(){
		return selection;
	}

	public void showArmy(){
		if (gbuffer == null || army == null){
			gbuffer.setColor(GameWorld.COL_GREEN);
			gbuffer.fillRect(0, 0, mywidth * COLS, myheight * ROWS);
			return;
		}

		for (int i=0; i<army.getCount(); i++){
			Unit u = army.get(i);
			if (u != null){
				UnitType ut = uloader.getUnitType(u.getType());
				if (ut != null){
					Tile t = ut.getTile();
					Image img = t.getImage(army.getOwner());
					int y = (int)(i / COLS) * mywidth;
					int x = (i % COLS) * myheight;
					
					//MIN10
					//int xr = x + mywidth - 30;
					int xr = x + mywidth - 5;
					int yr = y + 2;
					int xl = x + 2;
					int yb2 = y + myheight;
					int yb = yb2 - 5;

					//MIN44 background
					gbuffer.setColor(GameWorld.COL_GREEN);
					gbuffer.fill3DRect(x, y, mywidth, myheight, true);

					if (i == army.getLeader()){
						gbuffer.setColor(Color.magenta);
						//gbuffer.drawRect(x, y, mywidth, myheight);
						gbuffer.fillArc(xr-5, yr, 8, 8, 45, 90);
					}

					gbuffer.setColor(Color.blue);
					gbuffer.drawImage(img, x, y, x + mywidth, y + myheight, 
						0, 0, mywidth, myheight, this);

					if (details){
						int hp = u.getHP();
						int hmax = u.getMaxHP();
						int combat = u.getCombat();
						int range = ut.getRange(u);
						
						for (int j=0; j<hp; j++){
							//gbuffer.fill3DRect(xl + 4 * j, yr, 3, 3, true);
							gbuffer.fillRect(xl + 4 * j, yr, 3, 3);
						}
						gbuffer.setColor(Color.black);
						for (int j=hp; j<hmax; j++){
							//gbuffer.fill3DRect(xl + 4 * j, yr, 3, 3, true);
							gbuffer.fillRect(xl + 4 * j, yr, 3, 3);
						}
						gbuffer.setColor(Color.red);
						for (int j=0; j<combat; j++){
							//gbuffer.fill3DRect(xl + 4 * j, yb, 3, 3, true);
							gbuffer.fillRect(xl + 4 * j, yb, 3, 3);
						}
						//MIN10
						//gbuffer.setColor(Color.blue);
						//gbuffer.drawString(UnitType.LEVEL_NAME[u.getLevel()], xr, yb2);
						gbuffer.setColor(Color.blue);
						for (int j=0; j<range; j++){
							//gbuffer.fill3DRect(xr - 4 * j, yb, 3, 3, true);
							gbuffer.fillRect(xr - 4 * j, yb, 3, 3);
						}
					}

					int trait = u.getTrait();
					gbuffer.setColor(GameWorld.TRAIT_COLOR[trait]);
					//gbuffer.drawRect(x, y, mywidth, myheight);
					gbuffer.fillArc(xl, yb - 5, 8, 8, 45, 90);
				}
			}
		}
		//MIN44 background
		for (int i=army.getCount(); i < COLS * ROWS; i++){
			int y = (int)(i / COLS) * mywidth;
			int x = (i % COLS) * myheight;
			gbuffer.setColor(GameWorld.COL_GREEN);
			if (i < army.getLimit()) {
				gbuffer.fill3DRect(x, y, mywidth, myheight, false);
			}else{
				gbuffer.fillRect(x, y, mywidth, myheight);
			}
		}
		//MIN44 selection
		if (selection > -1){
			int y = (int)(selection / COLS) * mywidth;
			int x = (selection % COLS) * myheight;

			gbuffer.setColor(Color.blue);
			gbuffer.drawRect(x, y, mywidth-1, myheight-1);
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth * COLS + 1, myheight * ROWS + 1);
	}

	public Dimension getMaximumSize(){
		return new Dimension(mywidth * COLS + 1, myheight * ROWS + 1);
	}

	public boolean isOptimizedDrawingEnabled(){
		return true;
	}

	//MIN16
	public void paintComponent(Graphics g){
		if (buffer == null || gbuffer == null){
			buffer = createImage(getWidth(), getHeight());
			gbuffer = buffer.getGraphics();
			gbuffer.setFont(new Font("Arial", Font.PLAIN, 9));
		}

		//draw background
		/*
		int wmax = mywidth * COLS;
		int hmax = myheight * ROWS;
		gbuffer.setColor(GameWorld.COL_GREEN);
		gbuffer.fillRect(0, 0, wmax, hmax);
		gbuffer.setColor(Color.gray);
		//draw grid
		for (int j=0; j<ROWS; j++){
			gbuffer.drawLine(0, j*myheight, wmax, j*myheight);
		}
		gbuffer.drawLine(0, hmax, wmax, hmax);
		for (int i=0; i<COLS; i++){
			gbuffer.drawLine(i*mywidth, 0, i*mywidth, hmax);
		}
		gbuffer.drawLine(wmax, 0, wmax, hmax);
		*/
		//draw Army
		showArmy();
		g.drawImage(buffer, 0, 0, this);
	}
}

class ResHolderPanel extends JPanel{
	public static final int ROWS = 3;
	public static final int COLS = 4;

	private int mywidth, myheight;
	private Image buffer;
	private Graphics gbuffer;
	private boolean running;
	private ResourceHolder res;
	private MapPanel mp;
	private int limit;
	private int from;

	public ResHolderPanel(MapPanel m, int w, int h, int f, int l){
		super();
		mp = m;
		mywidth = w;
		myheight = h;
		from = f;
		limit = l;
	}

	public void setResourceHolder(ResourceHolder rh){
		res = rh;
	}

	public void showResources(){
		if (res == null){
			return;
		}

		for (int j=from; j<res.getResourceCount() && j<GameWorld.RESOURCE_HIDDEN && j < limit; j++){
			int r = res.getResource(j);
			int y = (int)(j / COLS) * myheight;
			int x = (j % COLS) * mywidth;

			if (j == GameWorld.RESOURCE_CURRENCY){
				gbuffer.setColor(GameWorld.COL_GREEN);
				gbuffer.fill3DRect(x, y, mywidth, myheight, false);
			}else{
				gbuffer.setColor(GameWorld.COL_GREEN_WHITE);
				gbuffer.fill3DRect(x, y, mywidth, myheight, true);
			}

			mp.drawResource(gbuffer, this, j, x, y, mywidth);

			if (j == GameWorld.RESOURCE_CURRENCY) {
				gbuffer.setColor(Color.white);
				//gbuffer.setXORMode(Color.white);
			}else if (r > 0){
				gbuffer.setColor(Color.blue);
				//gbuffer.setXORMode(Color.white);
			}else{
				gbuffer.setColor(Color.red);
				//gbuffer.setXORMode(Color.white);
			}

			gbuffer.drawString(Integer.toString(r), x+2, y+myheight-2);
			//gbuffer.setPaintMode();
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth * COLS + 1, myheight * ROWS + 1);
	}

	public int getIndex(int x, int y){
		return ((int)(y / myheight) * COLS + (int)(x / mywidth));
	}

	public boolean isOptimizedDrawingEnabled(){
		return true;
	}

	//MIN16
	public void paintComponent(Graphics g){
		if (buffer == null || gbuffer == null){
			buffer = createImage(getWidth(), getHeight());
			gbuffer = buffer.getGraphics();
			gbuffer.setFont(new Font("Arial", Font.PLAIN, 10));
		}

		//draw background
		/*
		int wmax = mywidth * COLS;
		int hmax = myheight * ROWS;

		if (isEnabled()){
			gbuffer.setColor(getBackground());
		}else{
			gbuffer.setColor(Color.lightGray);
		}
		gbuffer.fillRect(0, 0, wmax, hmax);
		gbuffer.setColor(Color.gray);
		//draw grid
		for (int j=0; j<ROWS; j++){
			gbuffer.drawLine(0, j*myheight, wmax, j*myheight);
		}
		gbuffer.drawLine(0, hmax, wmax, hmax);
		for (int i=0; i<COLS; i++){
			gbuffer.drawLine(i*mywidth, 0, i*mywidth, hmax);
		}
		gbuffer.drawLine(wmax, 0, wmax, hmax);
		*/
		gbuffer.setColor(getBackground());
		gbuffer.fillRect(0, 0, getWidth(), getHeight());

		showResources();
		g.drawImage(buffer, 0, 0, this);
	}
}

class UnitPanel extends JPanel{
	private Army army;
	private Base base;
	private JLabel lblArmy;
	private JList lstProp;
	private JButton btnJoin, btnBuild, btnDismiss, btnJob;
	private UnitHolderPanel panUnit;
	private ResHolderPanel panRes;
	private JCheckBox chkOrder, chkLeader, chkRetreat, chkLoad, chkEntertain, chkGuard;

	private TerrainLoader tloader;
	private HouseTypeLoader hloader;
	private UnitTypeLoader uloader;
	private OverlayLoader oloader;
	private PlayerLoader ploader;
	private TechLoader teloader;
	private GameWorld world;
	private MapPanel panMap;

	private Cursor curHand;

	public UnitPanel(MapPanel mp, TerrainLoader tl, OverlayLoader ol, HouseTypeLoader hl,
			UnitTypeLoader ul, PlayerLoader pl, TechLoader tel, GameWorld w){
		super();

		panMap = mp;
		tloader = tl;
		oloader = ol;
		hloader = hl;
		uloader = ul;
		ploader = pl;
		teloader = tel;
		world = w;

		curHand = Toolkit.getDefaultToolkit().createCustomCursor(
			world.getGameIcon(GameWorld.IMG_LOAD), new Point(16,16), "Dollar");

		lblArmy = new JLabel();

		lstProp = new JList();

		btnJoin = new JButton("Join Town");
		btnJoin.setVisible(false);
		btnJoin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army != null && base != null){
					try{
					int index = panUnit.getSelection();
					if (index > -1){
						//If a leader join the town and there is no alternative leaders, we are in trouble
						//if (index == army.getLeader()) {
						//	int alternative = army.getAlternativeLeader(ul, index);
						//	if (alternative == -1) {
						//		panUnit.clearSelection();
						//		panUnit.repaint();
						//		GameWorld.printMessage(".");
						//		return;
						//	}
						//}
						boolean rslt = base.transfer(army, tloader, oloader, hloader, uloader, teloader, index);
						if (!rslt) {
							//MIN32
							refresh();
							//MIN32
							panUnit.clearSelection();
							panUnit.repaint();
							return;
						}
						if (army.getCount() <= 0){
							world.getGD().removeArmy(world.getGD().armyIndex(army));
							world.clearSelection();
							//setVisible(false);
						}else{
							//MIN32
							//System.err.println("Refresh");
							refresh();
							//MIN32
							//System.err.println("Clear selection");
							panUnit.clearSelection();
							//System.err.println("Repaint");
							panUnit.repaint();
						}
						panMap.repaint();
					}else{
						GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Select a unit from the army.");
					}
					}catch(Exception ex){
						//System.err.println(ex);
					}
				}
			}
		});

		//MH108
		btnJob = new JButton("Quest");
		btnJob.setVisible(false);
		btnJob.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army != null && base != null){
					//Job job = JobGenerator.getNewJob();
					int jind = world.showJobs(GameWorld.PLAYER_OWNER);
					if (jind > -1) {
						if (world.getGD().assignJob(army, GameWorld.PLAYER_OWNER, jind)){
							//Disappear to do the job
							world.getGD().clearArmy(army.getX(), army.getY());
							GameWorld.printMessage("<%"+ GameWorld.IMG_SCROLL +"%>My lord, we are setting out on a quest, we should be back by the next day if all goes well.");
							world.clearSelection();
						}
					}
				}
			}
		});

		btnBuild = new JButton("Build Town");
		btnBuild.setVisible(false);
		btnBuild.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army != null){
					if (world.buildCity(army)){
						if (army.getOwner() == GameWorld.PLAYER_OWNER){
							GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL +
								"%>My lord, a new town has been founded.");
						}
					}else{
						if (army.getOwner() == GameWorld.PLAYER_OWNER){
							GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL +
								"%>We can not build a town so close to others or we dont have enough " + GameWorld.RESOURCE_PIONEER + " " + GameWorld.RESOURCE_NAME[GameWorld.RESOURCE_TOOL]);
						}
					}
				}
			}
		});

		btnDismiss = new JButton("Dismiss");
		//btnDismiss.setVisible(false);
		btnDismiss.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army != null){
					int index = panUnit.getSelection();
					if (index > -1){
						if (world.doDismissal(army, index)){
							panUnit.clearSelection();
							panUnit.repaint();
							panRes.repaint();
							refresh();
						}
					}else{
						GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Select a unit from the army.");
					}
				}
			}
		});

		JPanel panBut = new JPanel(new GridLayout(6, 1));
		panBut.add(btnJoin);
		panBut.add(btnJob);
		panBut.add(btnDismiss);
		panBut.add(btnBuild);
		panBut.add(new JLabel());
		panBut.add(new JLabel());

		ListScrollPane scrPane = new ListScrollPane(lstProp, 220, 144);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		panUnit = new UnitHolderPanel(48, 48, ul, world, true);
		//MIN35
		panRes = new ResHolderPanel(panMap, 32, 40, 0, GameWorld.RESOURCE_TRANSFERABLE);
		panRes.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				//non usable
				if (army == null || base == null){
					return;
				}

				//index
				Point p = e.getPoint();
				int index = panRes.getIndex(p.x, p.y);

				//error
				if (index < 0 || index > GameWorld.RESOURCE_SIZE){
					return;
				}

				//amount
				int amount = 10;
				if (e.isShiftDown()){
					amount = 1;
				}else if (e.isAltDown()) {
					amount = 100;
				}

				boolean res = false;
				//load
				//MIN33 safe loading
				if (e.getButton() == MouseEvent.BUTTON1){
					if (amount > base.getResource(index)){
						amount = base.getResource(index);
					}
					if (amount <= 0){
						return;
					}
					if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
						"Do you want to load " + Integer.toString(amount)+ " " +
						GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
						res = world.transferResource(base, army, index, amount);
						panRes.repaint();
					}else{
						return;
					}
				}else{
				//unload
				//MIN33 safe unloading
					if (amount > army.getResource(index)){
						amount = army.getResource(index);
					}
					if (amount <= 0){
						return;
					}
					if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
						"Do you want to unload " + Integer.toString(amount)+ " " +
						GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
						res = world.transferResource(army, base, index, amount);
						panRes.repaint();
					}else{
						return;
					}
				}
				if (!res){
					GameWorld.printMessage("<%"+GameWorld.IMG_HELPER+"%>There is not enough resources or storage limit reached");
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		JPanel panCheck = new JPanel();
		panCheck.setLayout(new GridLayout(6,1));
		chkOrder = new JCheckBox("Automate formation");
		chkOrder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army == null){
					return;
				}
				if (chkOrder.isSelected()){
					army.setFlag(Army.MASK_AUTO_ORGANIZE);
				}else{
					army.clearFlag(Army.MASK_AUTO_ORGANIZE);
				}
			}
		});
		chkLeader = new JCheckBox("Leader join battle");
		chkLeader.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army == null){
					return;
				}
				if (chkLeader.isSelected()){
					army.setFlag(Army.MASK_LEADER_ATTACK);
				}else{
					army.clearFlag(Army.MASK_LEADER_ATTACK);
				}
			}
		});
		chkRetreat = new JCheckBox("Retreat if critical");
		chkRetreat.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army == null){
					return;
				}
				if (chkRetreat.isSelected()){
					army.setFlag(Army.MASK_ALLOW_RETREAT);
				}else{
					army.clearFlag(Army.MASK_ALLOW_RETREAT);
				}
			}
		});
		chkLoad = new JCheckBox("Get food automatically");
		chkLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army == null){
					return;
				}
				if (chkLoad.isSelected()){
					army.setFlag(Army.MASK_LOAD_FOOD);
				}else{
					army.clearFlag(Army.MASK_LOAD_FOOD);
				}
			}
		});
		chkEntertain = new JCheckBox("Entertain in town");
		chkEntertain.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army == null){
					return;
				}
				if (chkEntertain.isSelected()){
					army.setFlag(Army.MASK_ALLOW_ENTERTAIN);
				}else{
					army.clearFlag(Army.MASK_ALLOW_ENTERTAIN);
				}
			}
		});
		chkGuard = new JCheckBox("Town police");
		chkGuard.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (army == null){
					return;
				}
				if (chkGuard.isSelected()){
					army.setFlag(Army.MASK_ALLOW_POLICE);
				}else{
					army.clearFlag(Army.MASK_ALLOW_POLICE);
				}
			}
		});
		panCheck.add(chkOrder);
		panCheck.add(chkLeader);
		panCheck.add(chkRetreat);
		panCheck.add(chkLoad);
		panCheck.add(chkEntertain);
		panCheck.add(chkGuard);

		JPanel panTop = new JPanel();
		panTop.setLayout(new BorderLayout());
		panTop.add(lblArmy, BorderLayout.NORTH);
		//panTop.add(panCheck, BorderLayout.CENTER);
		//panTop.add(panUnit, BorderLayout.SOUTH);
		panTop.add(panUnit, BorderLayout.SOUTH);

		JPanel panMid = new JPanel();
		panMid.setLayout(new BorderLayout());
		//MIN45
		panMid.add(new DummyPanel(), BorderLayout.WEST);
		panMid.add(new JLabel("Status Report"), BorderLayout.NORTH);
		panMid.add(scrPane, BorderLayout.CENTER);

		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(new DummyPanel(), BorderLayout.WEST);
		panTemp.add(new JLabel("Current commands"), BorderLayout.NORTH);
		panTemp.add(panCheck, BorderLayout.CENTER);
		panTemp.add(panBut, BorderLayout.EAST);
		//panTemp.add(new DummyPanel(), BorderLayout.EAST);

		JPanel panBot = new JPanel();
		panBot.setLayout(new BorderLayout());
		panBot.add(new DummyPanel(), BorderLayout.WEST);
		panBot.add(new JLabel("Current Inventory"), BorderLayout.NORTH);
		panBot.add(panRes, BorderLayout.CENTER);

		JPanel panTemp2 = new JPanel(new BorderLayout());
		//MH105
		//panTemp2.setBackground(GameWorld.COL_GREEN);
		//MH105
		panTemp2.add(panBot, BorderLayout.EAST);
		panTemp2.add(panMid, BorderLayout.WEST);

		setLayout(new BorderLayout());
		//add(panTop, BorderLayout.NORTH);
		//add(panMid, BorderLayout.CENTER);
		//add(panBot, BorderLayout.SOUTH);
		add(panTop, BorderLayout.WEST);
		add(panTemp2, BorderLayout.CENTER);
		add(panTemp, BorderLayout.EAST);
	}

	public boolean isOptimizedDrawingEnabled(){
		return true;
	}

	private String repeatImage(int times, String image){
		String url = world.getURL("images/"+image);
		String result = "";
		for (int i=0; i<times; i++){
			result += "<img src=\""+url+"\" border=\"0\">";
		}
		return result;
	}

	public void refresh(){
		//System.err.println("REFRESH 001");
		//attributes = movement, combat points, has leader
		ArrayList atts = new ArrayList();
		int leader = army.getLeader();
		if (leader > -1){
			Unit uleader = army.get(leader);
			UnitType utleader = uloader.getUnitType(uleader.getType());
			atts.add("Leader: " + utleader.getName());
		}else{
			atts.add("<html>+<i>Army has no leader</i></html>");
		}
		//System.err.println("REFRESH 002");
		//atts.add("Units: " + Integer.toString(army.getCount()) + " / "+
		//	 Integer.toString(army.getLimit()));
		atts.add("<html>Units: " + 
			repeatImage(army.getCount(), "man.gif")+"&nbsp;"+
			repeatImage(army.getLimit()-army.getCount(), "manblack.gif")+"</html>");
		//atts.add("Movement: " + Integer.toString(army.getMove()) + " / "+
		//	Integer.toString(army.getMoveMax()));
		//System.err.println("REFRESH 002 a");
		atts.add("<html>Movement: " + 
			repeatImage(army.getMove(), "shoe.gif")+"&nbsp;"+
			repeatImage(army.getMoveMax()-army.getMove(), "shoeblack.gif")+"</html>");
		String strMove = "";
		//System.err.println("REFRESH 003");
		if (army.canMove(GameWorld.TOWN_LAND, uloader, false)){
			strMove += "on land";
		}
		if (army.canMove(GameWorld.SEA_LAND, uloader, false)){
			if (strMove.length() > 0){
				strMove += ", ";
			}
			strMove += "sea";
		}
		if (strMove.length() > 0){
			atts.add("Travel: " + strMove);
		}
		//System.err.println("REFRESH 004");
		//atts.add("Attacks: " + Integer.toString(army.getCombat()));
		if (army.getMove()>0) {
			atts.add("<html>Attacks: " + repeatImage(army.getCombat(), "star.gif")+"</html>");
		}else{
			atts.add("<html>+<i>No movements left for action</i></html>");
		}

		//System.err.println("REFRESH 005");
		int mpercent = (int)(army.getResource(GameWorld.RESOURCE_HAPPY) * GameWorld.RESOURCE_SLICE / GameWorld.RESOURCE_LIMIT_SMALL[GameWorld.RESOURCE_HAPPY]);
		if (mpercent < 1) {
			atts.add("Troop morale is very low.");
		}else{
			atts.add("<html>Morales: " + repeatImage(mpercent, "heart.gif")+"</html>");
			atts.add("<html>+<i>Adds recovery & charge bonus</i></html>");
		}

		//System.err.println("REFRESH 006");
		int ppercent = (int)(army.getResource(GameWorld.RESOURCE_SECURITY) * GameWorld.RESOURCE_SLICE / GameWorld.RESOURCE_LIMIT_SMALL[GameWorld.RESOURCE_SECURITY]);
		if (ppercent > 0) {
			atts.add("<html>Police bonus: " + repeatImage(ppercent, "sword.gif")+"</html>");
			atts.add("<html>+<i>Check town police command to deploy</i></html>");
		}
		//atts.add("Size Limit: " + Integer.toString(army.getLimit()));
		lstProp.setListData(atts.toArray());
		//System.err.println("REFRESH 007");

		if (panUnit.isShowing()){
			panUnit.repaint();
		}
		//System.err.println("REFRESH 008");
	}

	public void setArmy(Army a, Base b){
		if (a == army){
			return;
		}

		army = a;
		base = b;

		if (army != null){
			lblArmy.setText(ploader.getPlayer(army.getOwner()).getName() + "'s Army");

			if (army.getFlag(Army.MASK_AUTO_ORGANIZE) == Army.MASK_AUTO_ORGANIZE){
				chkOrder.setSelected(true);
			}else{
				chkOrder.setSelected(false);
			}
			if (army.getFlag(Army.MASK_LEADER_ATTACK) == Army.MASK_LEADER_ATTACK){
				chkLeader.setSelected(true);
			}else{
				chkLeader.setSelected(false);
			}
			if (army.getFlag(Army.MASK_ALLOW_RETREAT) == Army.MASK_ALLOW_RETREAT){
				chkRetreat.setSelected(true);
			}else{
				chkRetreat.setSelected(false);
			}
			if (army.getFlag(Army.MASK_LOAD_FOOD) == Army.MASK_LOAD_FOOD){
				chkLoad.setSelected(true);
			}else{
				chkLoad.setSelected(false);
			}
			if (army.getFlag(Army.MASK_ALLOW_ENTERTAIN) == Army.MASK_ALLOW_ENTERTAIN){
				chkEntertain.setSelected(true);
			}else{
				chkEntertain.setSelected(false);
			}
			if (army.getFlag(Army.MASK_ALLOW_POLICE) == Army.MASK_ALLOW_POLICE){
				chkGuard.setSelected(true);
			}else{
				chkGuard.setSelected(false);
			}
			//attributes = movement, combat points, has leader
			/*
			ArrayList atts = new ArrayList();
			if (army.getLeader() != -1){
				atts.add("Army has leader");
			}else{
				atts.add("Army has no leader");
			}
			atts.add("Units: " + Integer.toString(army.getCount()) + " / "+
				 Integer.toString(army.getLimit()));
			atts.add("Movement: " + Integer.toString(army.getMove()));
			String strMove = "";
			if (army.canMove(GameWorld.TOWN_LAND, uloader, false)){
				strMove += "on land";
			}
			if (army.canMove(GameWorld.SEA_LAND, uloader, false)){
				if (strMove.length() > 0){
					strMove += ", ";
				}
				strMove += "sea";
			}
			if (strMove.length() > 0){
				atts.add("Travel: " + strMove);
			}
			atts.add("Attacks: " + Integer.toString(army.getCombat()));
			//atts.add("Size Limit: " + Integer.toString(army.getLimit()));
			lstProp.setListData(atts.toArray());
			*/
			refresh();
			//button
			//tips
			if (base != null){
				panRes.setCursor(curHand);
				panRes.setEnabled(true);
				panRes.setToolTipText("Left click to load, Right click to unload");
				btnJoin.setVisible(true);
				btnJob.setVisible(true);
			}else{
				panRes.setCursor(Cursor.getDefaultCursor());
				panRes.setEnabled(false);
				panRes.setToolTipText("");
				btnBuild.setVisible(true);
				btnJoin.setVisible(false);
				btnJob.setVisible(false);
			}
			panUnit.setArmy(army);
			panUnit.clearSelection();

			panRes.setResourceHolder(army);
			//setVisible(true);
		}else{
			//setVisible(false);
			btnJoin.setVisible(false);
			btnBuild.setVisible(false);
			btnJob.setVisible(false);
		}
	}
}

class ColorReplaceImageFilter extends RGBImageFilter{
	private Color oldc, newc;
	private int newcCode;

	public ColorReplaceImageFilter(Color c1, Color c2){
		oldc = c1;
		newc = c2;
		newcCode = (newc.getRed() << 16	| newc.getGreen() << 8 | newc.getBlue());
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		int red = rgb >> 16;
		int green = rgb >> 8;
		int blue = rgb;
		if (red == oldc.getRed() && green == oldc.getGreen() && blue == oldc.getBlue()){
			return newcCode;
		}else{
			return rgb;
		}
	}
}

class ColorFilter extends RGBImageFilter{
	private float hue1, hue2, sat1, sat2, bright1, bright2;

	public ColorFilter(Color c1, Color c2){
		float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		hue1 = hsb1[0];
		sat1 = hsb1[1];
		bright1 = hsb1[2];
		float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
		hue2 = hsb2[0];
		sat2 = hsb2[1];
		bright2 = hsb2[2];
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		float[] hsb = Color.RGBtoHSB((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, null);
		//replace
		//hsb[0] = (float)((hsb[0] + hue) / 2);
		if (hsb[0] > hue1 - 0.003 && hsb[0] < hue1 + 0.003){
			hsb[0] = hue2;
			if (bright2 < 0.3 && sat2 < 0.3){
				hsb[1] = sat2/2;
				hsb[2] = bright2/2;
			}else if (bright2 > 0.7 && sat2 < 0.3){
				hsb[1] = sat2/2;
				hsb[2] = bright2;
			}
		}
		//return with transparency
		return (Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0xffffff) | (rgb & 0xff000000);
	}
}

class GameMoveTimer implements Runnable{
	public static final int RES_NULL = 0;
	public static final int RES_OK = 1;
	public static final int RES_BLOCK = 2;
	public static final int RES_NOT_ENOUGH = 3;
	public static final int RES_UNKNOWN = 4;

	protected static final int ROTATE_X[] = {-1,-1,0,1,1,1,0,-1};
	protected static final int ROTATE_Y[] = {0,1,1,1,0,-1,-1,-1};

	private int time;
	private MapPanel mp;
	private GameWorld world;
	private Army army;
	private int dx, dy, aindex;
	private UnitTypeLoader uloader;
	private TerrainLoader tloader;
	private OverlayLoader oloader;
	private TechLoader teloader;
	private int cw2;
	private int ch2;
	private int cw4;
	private int ch4;

	private int result;

	//private PathFinding finder;
	public GameMoveTimer(MapPanel m, UnitTypeLoader ul, TerrainLoader tl,
			OverlayLoader ol, TechLoader tel, int t, GameWorld w){
		mp = m;
		uloader = ul;
		tloader = tl;
		oloader = ol;
		teloader = tel;
		time = t;
		world = w;

		cw2 = mp.getCellWidth() / 2;
		ch2 = mp.getCellHeight() / 2;
		cw4 = cw2 / 2;
		ch4 = ch2 / 2;
	}

	public int getLastResult(){
		return result;
	}

	public void setAction(Army a, int x, int y){
		army = a;
		//work out the index
		Point p = army.getPosition();
		aindex = world.getGD().getArmy(p.x, p.y);
		//end of index reversing
		//System.out.println("from: " + p.x + "/" + p.y + " to: " + x + "/" + y);
		army.setDestination(x, y);
		dx = x;
		dy = y;
		//to-do initiate
		//finder = new PathFinding(world.getGD(), uloader, a, p.x, p.y, x, y);
	}

	public void start(){
		mp.setBusy(true);
		Thread thread = new Thread(this);
		thread.start();
	}

	protected void drawCombatSequence(Army army, Unit attacker,
										Unit defender, Point p, 
										Point dir, int cx, int cy){
		//no animation
		if (!mp.isDisplayable(cx, cy)){
			return;
		}
		/*
		if (dir.x == -1 && dir.y == 1){
			attacker.setFrame(17);
		}else if (dir.x == 0 && dir.y == 1){
			attacker.setFrame(19);
		}else if (dir.x == 1 && dir.y == 1){
			attacker.setFrame(21);
		}else if (dir.x == 1 && dir.y == 0){
			attacker.setFrame(23);
		}else if (dir.x == 1 && dir.y == -1){
			attacker.setFrame(25);
		}else if (dir.x == 0 && dir.y == -1){
			attacker.setFrame(27);
		}else if (dir.x == -1 && dir.y == -1){
			attacker.setFrame(29);
		}else{
			attacker.setFrame(31);
		}
		*/
		attacker.setFrame(GameDirection.getCombatSprite(dir.x, dir.y, 0));

		mp.drawMapCell(cx, cy);
		//draw animation at its own space
		//mp.drawArmy(army, p.x, p.y);
		//MIN02
		//draw animation partly over
		mp.drawArmy(army, p.x + dir.x * cw4, p.y + dir.y * ch4);

		mp.repaint();

		doSleep();
		/*
		if (dir.x == -1 && dir.y == 1){
			attacker.setFrame(18);
		}else if (dir.x == 0 && dir.y == 1){
			attacker.setFrame(20);
		}else if (dir.x == 1 && dir.y == 1){
			attacker.setFrame(22);
		}else if (dir.x == 1 && dir.y == 0){
			attacker.setFrame(24);
		}else if (dir.x == 1 && dir.y == -1){
			attacker.setFrame(26);
		}else if (dir.x == 0 && dir.y == -1){
			attacker.setFrame(28);
		}else if (dir.x == -1 && dir.y == -1){
			attacker.setFrame(30);
		}else{
			attacker.setFrame(32);
		}
		*/
		attacker.setFrame(GameDirection.getCombatSprite(dir.x, dir.y, 1));

		mp.drawMapCell(cx, cy);
		//MIN02
		mp.drawCell(cx + dir.x, cy + dir.y);
		if (dir.x != 0){
			mp.drawCell(cx + dir.x, cy);
		}
		if (dir.y != 0){
			mp.drawCell(cx, cy + dir.y);
		}

		mp.drawArmy(army, p.x + dir.x * cw4, p.y + dir.y * ch4);

		//mp.drawArmy(army, p.x, p.y);
		mp.repaint();

		doSleep();

		//MIN41
		attacker.setFrame(0);
		mp.drawMapCell(cx, cy);
		if (dir.x != 0){
			mp.drawCell(cx + dir.x, cy);
		}
		if (dir.y != 0){
			mp.drawCell(cx, cy + dir.y);
		}

		mp.drawArmy(army, p.x, p.y);

		doSleep();
	}

	public void drawTechEffect(Unit attacker, int x, int y){
		if (!mp.isDisplayable(x, y)){
			return;
		}
		int tech = attacker.getUpgrade();
		if (tech > -1){
			int fcount = 0;
			boolean effect = mp.drawTechEffect(tech, x, y, fcount++);
			while (effect){
				doHalfSleep();
				effect = mp.drawTechEffect(tech, x, y, fcount++);
			}
			//MIN41
			mp.drawCell(x, y);
		}
	}

	protected void doSleep(){
		//System.out.println("Do Sleep");
		try{
			Thread.sleep(time);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	protected void doHalfSleep(){
		//System.out.println("Do Sleep");
		try{
			Thread.sleep((int)(time/2));
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	public synchronized void run(){
		if (army == null){
			mp.setBusy(false);
			//MIN31
			result = RES_NULL;
			return;
		}

		//MIN31
		result = RES_OK;
		//MIN09 - u != null
		if (army.getCount() > 0){
			int sx = army.getX();
			int sy = army.getY();
			int tx = sx, ty = sy;
			Graphics g = mp.getBuffer();
			int finalTarget = world.getGD().getArmy(dx, dy);

			mp.stop();
			//System.out.println("THERE");

			while (tx != dx || ty != dy){
				int ox = tx, oy = ty;
				//System.out.println(ox + "/" + oy);
				Point dir, p;
				//current location
				p = mp.getClipPosition(ox, oy);
				//move pointer to next possible location
				//Point ptemp = finder.nextPoint();
				//if (ptemp == null){
					dir = GameWorld.getDir(tx, ty, dx, dy);
				//}else{
				//	dir = GameWorld.getDir(tx, ty, p.x, p.y);
				//}
				//proposed location
				tx += dir.x;
				ty += dir.y;

				//clipping
				if (tx < 0){
					tx = 0;
				}else if (tx >= world.getGD().getMapWidth()){
					tx = world.getGD().getMapWidth()-1;
				}
				if (ty < 0){
					ty = 0;
				}else if (ty >= world.getGD().getMapHeight()){
					ty = world.getGD().getMapHeight()-1;
				}

				//to-do need to change, add move cost
				if (army.getMove() <= 0){
					//MIN31 - a ship can be descended into water, a man can swim up
					if (army.canMove(world.getGD().getBaseLand(ox, oy), uloader, false) ||
							!army.canMove(world.getGD().getBaseLand(tx, ty), uloader, false)){
						if (army.getOwner() == GameWorld.PLAYER_OWNER){
							GameWorld.printMessage("<%" + GameWorld.IMG_STOP + "%>Not enough movement points.");
						}
						//MIN31
						result = RES_NOT_ENOUGH;
						break;
					}
				}

				if (!army.canMove(world.getGD().getBaseLand(tx, ty), uloader, false)){
					if (army.getOwner() == GameWorld.PLAYER_OWNER){
						int target = world.getGD().getArmy(tx, ty);
						//if enemy is designated
						if (target > -1){
							Army enemy = world.getGD().getArmy(target);
							if (enemy.getOwner() == army.getOwner()){
								world.showUnitTransfer(army, enemy, army.getOwner());
								if (army.getCount() <= 0){
									world.getGD().removeArmy(world.getGD().armyIndex(army));
								}
								if (enemy.getCount() <= 0){
									world.getGD().removeArmy(world.getGD().armyIndex(enemy));
								}
								//MIN31
								result = RES_OK;
								break;
							}
						}
						//human control, let human decide
						GameWorld.printMessage("<%" + GameWorld.IMG_STOP + "%>Destination is blocked.");
						//MIN31
						result = RES_BLOCK;
						break;
					}
					int current = 0;
					boolean found = false;
					for (int c=0; c<ROTATE_X.length && !found; c++){
						if (dir.x == ROTATE_X[c] && dir.y == ROTATE_Y[c]){
							current = c;
							found = true;
						}
					}
					int dcnt = current + 1;
					if (dcnt >= ROTATE_X.length){
						dcnt = 0;
					}
					//distance to final destination
					int dist = 0, maxdist = 9999999;
					//new moving direction
					int fx = ox + ROTATE_X[dcnt];
					int fy = oy + ROTATE_Y[dcnt];
					//try all direction until we find the closest
					while(dcnt != current){
						//System.out.println(ROTATE_X[dcnt] + "/" + ROTATE_Y[dcnt]);
						//process current direction
						dist = Math.abs(fx - dx) + Math.abs(fy - dy);
						if (army.canMove(world.getGD().getBaseLand(fx, fy), uloader, false) &&	maxdist > dist){
							maxdist = dist;
							tx = fx;
							ty = fy;
							dir.setLocation(ROTATE_X[dcnt], ROTATE_Y[dcnt]);
						}
						//continue rotation
						if (++dcnt >= ROTATE_X.length){
							dcnt = 0;
						}
						fx = ox + ROTATE_X[dcnt];
						fy = oy + ROTATE_Y[dcnt];
					}
					//no chance
					if (!army.canMove(world.getGD().getBaseLand(tx, ty), uloader, false)){
						//System.out.println("nochance");
						//check for enemy, if not friend
						//load troops to boat or get the boat on land to move
						int target = world.getGD().getArmy(tx, ty);
						//if enemy is designated
						if (target > -1){
							Army enemy = world.getGD().getArmy(target);
							if (enemy.getOwner() == army.getOwner()){
								//MIN21 to-do: try to move them
								world.doAIUnitTransfer(enemy, army);
								if (army.getCount() <= 0){
									world.getGD().removeArmy(world.getGD().armyIndex(army));
								}
								if (enemy.getCount() <= 0){
									world.getGD().removeArmy(world.getGD().armyIndex(enemy));
								}
								//MIN31
								result = RES_OK;
								break;
							}
						}
						//MIN31
						result = RES_BLOCK;
						break;
					}
					//System.out.println("here");
				}

				//check for enemy
				int target = world.getGD().getArmy(tx, ty);
				//if enemy is designated
				if (target > -1 && target == finalTarget){
					//attack sequence
					Army enemy = world.getGD().getArmy(target);
					//only attack if owners are different
					if (enemy != null){
					if (enemy.getOwner() != army.getOwner()){
						//change relationship to war if already at peace
						if (world.getGD().getDiplomacy(army.getOwner(), enemy.getOwner()) > GameData.DIP_UNKNOWN){
							//MH142 Allow decision
							if (army.getOwner() == GameWorld.PLAYER_OWNER) {
								if (world.showYesNoDialog(GameWorld.IMG_ADVISOR_BIG, "Do you want to attack?") == YesNoDialog.STATE_NO){
									result = RES_OK;
									break;
								}			
							}
							//End of MH142
							world.getGD().setDiplomacy(army.getOwner(), enemy.getOwner(), GameData.DIP_REC_WAR);
							world.getGD().setDiplomacy(enemy.getOwner(), army.getOwner(), GameData.DIP_REC_WAR);
							if (enemy.getOwner() == GameWorld.PLAYER_OWNER || army.getOwner() == GameWorld.PLAYER_OWNER){
								GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>War has been declared (by our action)");
							}
						}
						//MH112 Attack, Defence bonus
						//Defender bonus: police
						float attackerAttackBonus = (float)(1 + army.getMoraleBonus() / 100);
						float attackerDefendBonus = 1.0f;
						float defenderAttackBonus = (float)(1 + enemy.getMoraleBonus() / 100);
						float defenderDefendBonus = 1.0f;
						int defbs = world.getGD().getBase(tx, ty);
						if (defbs > -1) {
							Base defBase = world.getGD().getBase(defbs);
							if (defBase != null) {
								defenderDefendBonus += (float)(defBase.getSecurityBonus() / 100);
							}
						}
						//defender position
						Point p2 = mp.getClipPosition(tx, ty);
						//defender facing is opposite of attacker
						Point dir2 = new Point(-dir.x, -dir.y);
						//combat length
						int attackLimit = army.getCombat();
						int attackRound = 0;
						while (army.getCount() > 0 && enemy.getCount() > 0 && attackRound < attackLimit){
							//System.out.println("Attack " + attackLimit + " rounds, round #" + attackRound);
							army.moveCombatUnitToFront(uloader);
							enemy.moveCombatUnitToFront(uloader);

							Unit attacker = army.get(0);
							Unit defender = enemy.get(0);
							if (attacker == null || defender == null ||
									attacker.getCombat() <= 0){
								break;
							}

							UnitType attackerType = uloader.getUnitType(attacker.getType());
							UnitType defenderType = uloader.getUnitType(defender.getType());
							
							if (attackerType == null || defenderType == null){
								break;
							}

							//MIN03 - capturing
							if (!defenderType.isCombatUnit()){
								if (army.capture(enemy, uloader)){
									if (mp.isDisplayable(ox, oy)){
										attackerType.playSound(1);
										doSleep();
									}

									if (enemy.getOwner() == GameWorld.PLAYER_OWNER){
										GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, some of our units have been captured");
									}else if (army.getOwner() == GameWorld.PLAYER_OWNER){
										GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, we have captured some enemy units");
									}
									attackRound++;
									break;
								}
							}
						
							int attackerRange = attackerType.getRange(attacker);
							int defenderRange = defenderType.getRange(defender);

							int attackerSpeed = attackerType.getSpeed(attacker);
							int defenderSpeed = defenderType.getSpeed(defender);
							//MIN02
							//if (attackerType != null){
								if (mp.isDisplayable(ox, oy)){
									attackerType.playSound(1);
									doSleep();
								}
							//}
							//reduce combatable this turn
							attacker.doCombat();
							//attack & counter attack if in range and survived
							drawCombatSequence(army, attacker, defender, p, dir, ox, oy);
							drawTechEffect(attacker, tx, ty);
							world.doTechEffect(defender, attacker.getUpgrade());

							//calculating chances
							int attackChance, defendChance;
							attackChance = (int)(attackerType.getAttackChance(attacker) * attackerAttackBonus);
							//MH112 Defence bonus
							defendChance = (int)(defenderType.getDefendChance(defender) * defenderDefendBonus);

							//Attacker with spying effects
							for (int k=0; k<attackerType.getXAttacks(); k++){
								int type = attackerType.getXAttack(k);
								int quant = attackerType.getXAQuant(k);
								enemy.reduce(type, quant);
							}							
							//Combat damange
							if (attackChance > defendChance){
								//if die in combat, what to do?
								if (defender.hurt(attackerType)){
									//draw the old die
									if (mp.isDisplayable(tx, ty)){
										//MH120
										world.startSpriteEffect(GameWorld.IMG_GET_HIT, p2.x, p2.y);
										//MH120
									}
									//remove
									enemy.remove(0, uloader);
									//draw new
									if (mp.isDisplayable(tx, ty)){
										mp.drawCell(tx, ty);
									}
									//MIN08
									if (attacker.reward() && attackerType.getPromotion()>-1){
										attacker.setType(attackerType.getPromotion());
									}
									defender = null;
									//MIN02
									//if (attackerType != null){
										if (mp.isDisplayable(ox, oy)){
											attackerType.playSound(2);
											doSleep();
										}
									//}
								}
							}							

							if (defender != null && defenderRange >= attackerRange 
									&& defender.getCombat() > 0){
								//MIN02
								//if (defenderType != null){
									if (mp.isDisplayable(tx, ty)){
										defenderType.playSound(1);
										doSleep();
									}
								//}

								//reduce combatable this turn
								defender.doCombat();
								//draw sequence
								drawCombatSequence(enemy, defender, attacker, p2, dir2, tx, ty);
								drawTechEffect(defender, ox, oy);
								world.doTechEffect(attacker, defender.getUpgrade());
								
								//MH112 Bonus
								attackChance = (int)(defenderType.getAttackChance(defender) * defenderAttackBonus);
								defendChance = (int)(attackerType.getDefendChance(attacker) * attackerDefendBonus);

								if (attackChance > defendChance){
									//if die in combat, what to do?
									if (attacker.hurt(defenderType)){
										if (mp.isDisplayable(ox, oy)){
											//MH120
											world.startSpriteEffect(GameWorld.IMG_GET_HIT, p.x, p.y);
											//MH120
										}
										army.remove(0, uloader);
										if (mp.isDisplayable(ox, oy)){
											mp.drawCell(ox, oy);
										}
										//MIN08
										if (defender.reward() && defenderType.getPromotion()>-1){
											defender.setType(defenderType.getPromotion());
										}
										attacker = null;
										//MIN02
										//if (defenderType != null){
											if (mp.isDisplayable(tx, ty)){
												defenderType.playSound(2);
												doSleep();
											}
										//}
									}
								}							
							}

							//retreat is possible when hp low to 1 and speed is better
							//and player has not force army to fight to dead
							if (attacker != null && attackerSpeed > defenderSpeed &&
								((attacker.getHP() < attacker.getMaxHP() && army.getCount()>1) || attacker.getHP()<2) &&
								army.getFlag(Army.MASK_ALLOW_RETREAT) == Army.MASK_ALLOW_RETREAT){
								if (army.getCount() > 1){
									//allow attacker to reorganize since this unit make retreat
									army.reorganize(uloader, true);
									enemy.reorganize(uloader, false);
								}else{
									//make retreat
									enemy.reorganize(uloader, false);
									break;
								}
							}else{
								//allow attacker leader to reorganize
								army.reorganize(uloader, false);
								enemy.reorganize(uloader, false);
							}

							attackRound++;
						} //end of combat loop

						//if the whole army eliminated or DROWN!
						//canMove visible=true for the attackers
						//MIN30
						if (army.getCount() == 0 || 
							!army.canMove(world.getGD().getBaseLand(ox, oy), uloader, attackRound > 0)){
							world.getGD().removeArmy(world.getGD().armyIndex(army));
							if (army.getOwner() == GameWorld.PLAYER_OWNER){
								GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, we have lost the entire company");
							}
							//Playing defeat animations
							if (mp.isDisplayable(ox, oy)){
								world.startSpriteEffect(GameWorld.IMG_DEFEAT01, p.x, p.y);
							}
						}else{
							//Combat happen so preset destination is clear now
							army.setDestination(ox, oy);
						}
						//if the whole army eliminated or DROWN!
						//MIN30
						if (enemy.getCount() == 0 || 
							!enemy.canMove(world.getGD().getBaseLand(tx, ty), uloader, attackRound > 0)){
							world.getGD().removeArmy(world.getGD().armyIndex(enemy));
							if (enemy.getOwner() == GameWorld.PLAYER_OWNER){
								GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, we have lost the entire company");
							}
							//Playing defeat animations
							if (mp.isDisplayable(tx, ty)){
								world.startSpriteEffect(GameWorld.IMG_DEFEAT01, p2.x, p2.y);
							}
						}
						//restore frame here
						for (int w=0; w<army.getCount(); w++){
							army.get(w).setFrame(0);
						}
						for (int w=0; w<enemy.getCount(); w++){
							enemy.get(w).setFrame(0);
						}

						if (attackRound > 0){
							army.chargeMove(Army.ATTACK_MOVE_COST);
						}else{
							if (army.getOwner() == GameWorld.PLAYER_OWNER){
								GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, our army doesnt have enough combat points to attack");
							}
						}
					}else{
						if (army.getOwner() == GameWorld.PLAYER_OWNER){
							world.showUnitTransfer(army, enemy, army.getOwner());
						}else{
							//MIN21 to-do: try to move them
							world.doAIUnitTransfer(enemy, army);
						}
						if (army.getCount() <= 0){
							world.getGD().removeArmy(world.getGD().armyIndex(army));
						}
						if (enemy.getCount() <= 0){
							world.getGD().removeArmy(world.getGD().armyIndex(enemy));
						}
					}
					}
				}else if (target == -1){
					//MIN32 make sure display is right
					//army.canMove(world.getGD().getBaseLand(tx, ty), uloader, true);
					//MIN37
					//If it is visible on player map?
					if (mp.isDisplayable(ox, oy)){
					//Draw all moves
					for (int w=0; w<army.getCount(); w++){
					//MIN37
					Unit u = army.get(w);
					UnitType attackerType = uloader.getUnitType(u.getType());
					if (!attackerType.canMove(world.getGD().getBaseLand(tx, ty))){
						continue;
					}
					army.swap(0, w);
					//MIN09 - move here from out of loop, show animation
					u = army.get(0);
					attackerType = uloader.getUnitType(u.getType());

					//playing sound
						if (attackerType != null){
							attackerType.playSound(0);
						}
					//move sequence
					/*
						if (dir.x == -1 && dir.y == 1){
							u.setFrame(1);
						}else if (dir.x == 0 && dir.y == 1){
							u.setFrame(3);
						}else if (dir.x == 1 && dir.y == 1){
							u.setFrame(5);
						}else if (dir.x == 1 && dir.y == 0){
							u.setFrame(7);
						}else if (dir.x == 1 && dir.y == -1){
							u.setFrame(9);
						}else if (dir.x == 0 && dir.y == -1){
							u.setFrame(11);
						}else if (dir.x == -1 && dir.y == -1){
							u.setFrame(13);
						}else{
							u.setFrame(15);
						}
					*/
						u.setFrame(GameDirection.getMoveSprite(dir.x, dir.y, 0));
						//1
						mp.drawMapCell(ox, oy);
						mp.drawMapCell(tx, ty);
						//there could be more cells affected
						if (dir.x != 0 && dir.y != 0){
							mp.drawCell(ox + dir.x, oy);
							mp.drawCell(ox, oy + dir.y);
						}
						mp.drawArmy(army, p.x + dir.x * cw4, p.y + dir.y * ch4);
						mp.repaint();

						doSleep();
					/*
						if (dir.x == -1 && dir.y == 1){
							u.setFrame(2);
						}else if (dir.x == 0 && dir.y == 1){
							u.setFrame(4);
						}else if (dir.x == 1 && dir.y == 1){
							u.setFrame(6);
						}else if (dir.x == 1 && dir.y == 0){
							u.setFrame(8);
						}else if (dir.x == 1 && dir.y == -1){
							u.setFrame(10);
						}else if (dir.x == 0 && dir.y == -1){
							u.setFrame(12);
						}else if (dir.x == -1 && dir.y == -1){
							u.setFrame(14);
						}else{
							u.setFrame(16);
						}
					*/
						u.setFrame(GameDirection.getMoveSprite(dir.x, dir.y, 1));

						//restore cells
						mp.drawMapCell(ox, oy);
						mp.drawMapCell(tx, ty);
						//there could be more cells affected
						if (dir.x != 0 && dir.y != 0){
							mp.drawCell(ox + dir.x, oy);
							mp.drawCell(ox, oy + dir.y);
						}
						//2
						mp.drawArmy(army, p.x + dir.x * cw2, p.y + dir.y * ch2);
						mp.repaint();

						doSleep();
					/*
						if (dir.x == -1 && dir.y == 1){
							u.setFrame(1);
						}else if (dir.x == 0 && dir.y == 1){
							u.setFrame(3);
						}else if (dir.x == 1 && dir.y == 1){
							u.setFrame(5);
						}else if (dir.x == 1 && dir.y == 0){
							u.setFrame(7);
						}else if (dir.x == 1 && dir.y == -1){
							u.setFrame(9);
						}else if (dir.x == 0 && dir.y == -1){
							u.setFrame(11);
						}else if (dir.x == -1 && dir.y == -1){
							u.setFrame(13);
						}else{
							u.setFrame(15);
						}
					*/
						u.setFrame(GameDirection.getMoveSprite(dir.x, dir.y, 0));
						
						//restore cells
						mp.drawMapCell(ox, oy);
						mp.drawMapCell(tx, ty);
						//there could be more cells affected
						if (dir.x != 0 && dir.y != 0){
							mp.drawCell(ox + dir.x, oy);
							mp.drawCell(ox, oy + dir.y);
						}
						//3
						mp.drawArmy(army, p.x + 3 * dir.x * cw4, p.y + 3 * dir.y * ch4);
						mp.repaint();

						doSleep();
					/*
						if (dir.x == -1 && dir.y == 1){
							u.setFrame(2);
						}else if (dir.x == 0 && dir.y == 1){
							u.setFrame(4);
						}else if (dir.x == 1 && dir.y == 1){
							u.setFrame(6);
						}else if (dir.x == 1 && dir.y == 0){
							u.setFrame(8);
						}else if (dir.x == 1 && dir.y == -1){
							u.setFrame(10);
						}else if (dir.x == 0 && dir.y == -1){
							u.setFrame(12);
						}else if (dir.x == -1 && dir.y == -1){
							u.setFrame(14);
						}else{
							u.setFrame(16);
						}
					*/
						u.setFrame(GameDirection.getMoveSprite(dir.x, dir.y, 1));

						//MIN38 restore cells
						mp.drawMapCell(ox, oy);
						mp.drawMapCell(tx, ty);
						//MIN38 there could be more cells affected
						if (dir.x != 0 && dir.y != 0){
							mp.drawCell(ox + dir.x, oy);
							mp.drawCell(ox, oy + dir.y);
						}

						//MIN38 draw new location
						mp.drawArmy(army, p.x + 2 * dir.x * cw2, p.y + 2 * dir.y * ch2);
						mp.repaint();
						world.updateMiniMap();
						//MIN38
						doSleep();
					}
					//MIN37
					//u.setFrame(0);
					//MIN37
					}
					//MIN32, MIN37 make sure display is right
					army.canMove(world.getGD().getBaseLand(tx, ty), uloader, true);
					//change position
					world.getGD().clearArmy(ox, oy);
					world.getGD().setArmyDynamic(aindex, tx, ty, uloader);
					//System.out.println(tx + "/" + ty);
					army.chargeMove(world.getGD().getMoveCost(tx, ty, tloader, oloader));

					//MIN04 - check for city underneath, capture if allowed
					int b = world.getGD().getBase(tx, ty);
					if (b > -1){
						Base base = world.getGD().getBase(b);
						if (army.getOwner() != base.getOwner()){
							world.doConquer(base, army);
						}
					}

					//restore cells
					/*
					if (mp.isDisplayable(tx, ty) || mp.isDisplayable(ox, oy)){
						mp.drawMapCell(ox, oy);
						mp.drawMapCell(tx, ty);
						//there could be more cells affected
						if (dir.x != 0 && dir.y != 0){
							mp.drawCell(ox + dir.x, oy);
							mp.drawCell(ox, oy + dir.y);
						}

						//draw new location
						p = mp.getClipPosition(tx, ty);
						mp.drawArmy(army, p.x, p.y);
						mp.repaint();
						//MIN34
						world.updateMiniMap();
						//MIN34
						doSleep();
					}
					*/
					//restore frame
					for (int w=0; w<army.getCount(); w++){
						army.get(w).setFrame(0);
					}
					//u.setFrame(0);

					//***************** Acquire items and trigger events *************
					//MH110
					//Get item from Overlay
					for (int v=0; v<world.getGD().getTopCount(tx,ty); v++) {
						int oind = world.getGD().getTopLand(tx,ty,v);
						if (oind != -1) {
							Overlay ovl = oloader.getOverlay(oind);
							if (ovl != null) {
								int bonus = ovl.getBonus();
								int upgrade = OverlayBonus.getBonusUpgrade(bonus);
								if (upgrade>-1) {
									Tech tech = world.gwGetTech(upgrade);
									if (tech.isUpgrade()) {
										int lind = army.getLeader();
										if (lind>-1) {
											Unit leader = army.get(lind);
											if (leader != null && leader.getUpgrade()==-1) {
												//check range requirements
												UnitType ut = uloader.getUnitType(leader.getType());
												if (ut != null) {
													int crange = ut.getRange(leader);
													if ((tech.getProdBonus()>-1) || (tech.getRRange() == 0 && crange == 0) || (crange > 0 && crange >= tech.getRRange())) {
														leader.setUpgrade(upgrade);
														//Acquire technology
														world.getGD().addAdvance(army.getOwner(), upgrade);
														//Destroy the overlay
														if (OverlayBonus.getBonusType(bonus) == OverlayBonus.BONUS_ONCE) {
															//Clear
															//world.getGD().setTopLand(tx, ty, -1);
															world.getGD().removeTopLand(tx, ty, v, tloader, oloader, uloader, teloader);
														}
													}else{
														if (army.getOwner() == GameWorld.PLAYER_OWNER) {
															GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>There are some rare items lying on the ground. However, the fleet commander doesnt have the required range to retrieve the item");
														}
													}
												}
											}else{
												if (army.getOwner() == GameWorld.PLAYER_OWNER) {
													GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>There are some rare items lying on the ground. However, the fleet commander already has an upgrade and can not carry more. Change the fleet commander to retrieve the item.");
												}
											}
										}else{
											Unit first = army.get(0);
											if (first != null && first.getUpgrade()==-1) {
												//check range requirements
												UnitType ut = uloader.getUnitType(first.getType());
												if (ut != null) {
													int crange = ut.getRange(first);
													if ((tech.getProdBonus()>-1) || (tech.getRRange() == 0 && crange == 0) || (crange > 0 && crange >= tech.getRRange())) {
														first.setUpgrade(upgrade);
														//Acquire technology
														world.getGD().addAdvance(army.getOwner(), upgrade);
														//Destroy the overlay
														if (OverlayBonus.getBonusType(bonus) == OverlayBonus.BONUS_ONCE) {
															//Clear
															//world.getGD().setTopLand(tx, ty, -1);
															world.getGD().removeTopLand(tx, ty, v, tloader, oloader, uloader, teloader);
														}
													}else{
														if (army.getOwner() == GameWorld.PLAYER_OWNER) {
															GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Our front-liner doesnt have the require range to retrieve the item");
														}
													}
												}
											}else{
												if (army.getOwner() == GameWorld.PLAYER_OWNER) {
													GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>Our front-liner already has an upgrade");
												}
											}
										}
									}
								}
							}
						}
					}
				}else{
					//MIN31
					result = RES_UNKNOWN;
					break;
				}
			}
			world.updateMiniMap();
			mp.start();
		}

		mp.setBusy(false);
	}
}

class GameBombardTimer implements Runnable{
	private int time;
	private MapPanel mp;
	private GameWorld world;
	private Army army;
	private int dx, dy, aindex;
	private UnitTypeLoader uloader;
	private TerrainLoader tloader;
	private int cw2;
	private int ch2;
	private int cw4;
	private int ch4;

	public GameBombardTimer(MapPanel m, UnitTypeLoader ul, TerrainLoader tl, int t, GameWorld w){
		mp = m;
		uloader = ul;
		tloader = tl;
		time = t;
		world = w;

		cw2 = mp.getCellWidth() / 2;
		ch2 = mp.getCellHeight() / 2;
		cw4 = cw2 / 2;
		ch4 = ch2 / 2;
	}

	public void setAction(Army a, int x, int y){
		army = a;
		//work out the index
		Point p = army.getPosition();
		aindex = world.getGD().getArmy(p.x, p.y);
		//end of index reversing
		dx = x;
		dy = y;
	}

	public void start(){
		mp.setBusy(true);
		Thread thread = new Thread(this);
		thread.start();
	}

	protected void drawCombatSequence(Army army, Unit attacker,
										Unit defender, Point p, Point dir, int cx, int cy){
		//no animation
		if (!mp.isDisplayable(cx, cy)){
			return;
		}
		/*
		if (dir.x == -1 && dir.y == 1){
			attacker.setFrame(17);
		}else if (dir.x == 0 && dir.y == 1){
			attacker.setFrame(19);
		}else if (dir.x == 1 && dir.y == 1){
			attacker.setFrame(21);
		}else if (dir.x == 1 && dir.y == 0){
			attacker.setFrame(23);
		}else if (dir.x == 1 && dir.y == -1){
			attacker.setFrame(25);
		}else if (dir.x == 0 && dir.y == -1){
			attacker.setFrame(27);
		}else if (dir.x == -1 && dir.y == -1){
			attacker.setFrame(29);
		}else{
			attacker.setFrame(31);
		}
		*/
		attacker.setFrame(GameDirection.getCombatSprite(dir.x, dir.y, 0));

		mp.drawMapCell(cx, cy);
		//draw animation at its own space
		mp.drawArmy(army, p.x, p.y);

		mp.repaint();

		doSleep();
		/*
		if (dir.x == -1 && dir.y == 1){
			attacker.setFrame(18);
		}else if (dir.x == 0 && dir.y == 1){
			attacker.setFrame(20);
		}else if (dir.x == 1 && dir.y == 1){
			attacker.setFrame(22);
		}else if (dir.x == 1 && dir.y == 0){
			attacker.setFrame(24);
		}else if (dir.x == 1 && dir.y == -1){
			attacker.setFrame(26);
		}else if (dir.x == 0 && dir.y == -1){
			attacker.setFrame(28);
		}else if (dir.x == -1 && dir.y == -1){
			attacker.setFrame(30);
		}else{
			attacker.setFrame(32);
		}
		*/
		attacker.setFrame(GameDirection.getCombatSprite(dir.x, dir.y, 1));

		mp.drawMapCell(cx, cy);
		mp.drawArmy(army, p.x, p.y);
		mp.repaint();

		doSleep();

		attacker.setFrame(0);

		mp.drawMapCell(cx, cy);
		mp.drawArmy(army, p.x, p.y);
		mp.repaint();

		doSleep();
	}

	public void drawTechEffect(Unit attacker, int x, int y){
		if (!mp.isDisplayable(x, y)){
			return;
		}
		int tech = attacker.getUpgrade();
		if (tech > -1){
			int fcount = 0;
			boolean effect = mp.drawTechEffect(tech, x, y, fcount++);
			while (effect){
				doHalfSleep();
				effect = mp.drawTechEffect(tech, x, y, fcount++);
			}
			//MIN41
			mp.drawCell(x, y);
		}
	}

	protected void doSleep(){
		try{
			Thread.sleep(time);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	protected void doHalfSleep(){
		//System.out.println("Do Sleep");
		try{
			Thread.sleep((int)(time/2));
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	public synchronized void run(){
		if (army == null){
			world.enable();
			return;
		}

		mp.stop();
		//check for enemy
		int target = world.getGD().getArmy(dx, dy);
		//if enemy is designated
		if (target > -1){
		//attack sequence
			int ox = army.getX();
			int oy = army.getY();
			Point dir = GameWorld.getDir(ox, oy, dx, dy);
			int range = 0;
			int sx = Math.abs(dx - ox);
			int sy = Math.abs(dy - oy);
			if (sx > sy){
				range = sx;
			}else{
				range = sy;
			}
			//System.out.println(range);
			//current location
			Point p = mp.getClipPosition(ox, oy);
			//check owner
			Army enemy = world.getGD().getArmy(target);
			//only attack if owners are different
			if (enemy != null && enemy.getOwner() != army.getOwner()){
				//change relationship to unknown if already at peace
				if (world.getGD().getDiplomacy(army.getOwner(), enemy.getOwner()) > GameData.DIP_UNKNOWN){
					//MH142 Allow decision
					if (army.getOwner() == GameWorld.PLAYER_OWNER) {
						if (world.showYesNoDialog(GameWorld.IMG_ADVISOR_BIG, "Do you want to attack?") == YesNoDialog.STATE_NO){
							mp.start();
							mp.setBusy(false);
							return;
						}			
					}
					//End of MH142

					world.getGD().setDiplomacy(army.getOwner(), enemy.getOwner(), GameData.DIP_REC_WAR);
					world.getGD().setDiplomacy(enemy.getOwner(), army.getOwner(), GameData.DIP_REC_WAR);
					if (enemy.getOwner() == GameWorld.PLAYER_OWNER || army.getOwner() == GameWorld.PLAYER_OWNER){
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>War has been declared");
					}
				}
				//defender position
				Point p2 = mp.getClipPosition(dx, dy);
				//defender facing is opposite of attacker
				Point dir2 = new Point(-dir.x, -dir.y);
				//combat length
				int attackLimit = army.getCombat();
				int attackRound = 0;
				while (army.getCount() > 0 && enemy.getCount() > 0 && attackRound < attackLimit){
					//System.out.println("R"+attackRound);
					army.moveRangeUnitToFront(uloader, range);
					enemy.moveRangeUnitToFront(uloader, range);

					Unit attacker = army.get(0);
					Unit defender = enemy.get(0);
					if (attacker == null || defender == null || attacker.getCombat() <= 0){
						//System.out.println("A");
						break;
					}

					UnitType attackerType = uloader.getUnitType(attacker.getType());
					UnitType defenderType = uloader.getUnitType(defender.getType());
					
					if (attackerType == null || defenderType == null){
						//System.out.println("B");
						break;
					}
					
					int attackerRange = attackerType.getRange(attacker);
					int defenderRange = defenderType.getRange(defender);

					//not in range
					if (attackerRange < range){
						//System.out.println("C"+attackerRange);
						break;
					}

					//if (attackerType != null){
						if (mp.isDisplayable(ox, oy)){
							attackerType.playSound(1);
						}
					//}
					//reduce combatable this turn
					attacker.doCombat();
					//attack & counter attack if in range and survived
					drawCombatSequence(army, attacker, defender, p, dir, ox, oy);
					drawTechEffect(attacker, dx, dy);
					world.doTechEffect(defender, attacker.getUpgrade());

					//calculating chances
					int attackChance, defendChance;
					attackChance = attackerType.getAttackChance(attacker);
					defendChance = defenderType.getDefendChance(defender);
					if (attackChance > defendChance){
						//if die in combat, what to do?
						if (defender.hurt(attackerType)){
							if (mp.isDisplayable(dx, dy)){
								//MH120
								world.startSpriteEffect(GameWorld.IMG_GET_HIT, p2.x, p2.y);
								//MH120
							}
							enemy.remove(0, uloader);
							if (mp.isDisplayable(dx, dy)){
								mp.drawCell(dx, dy);
							}
							//MIN08
							if (attacker.reward() && attackerType.getPromotion()>-1){
								attacker.setType(attackerType.getPromotion());
							}
							defender = null;
							//if (attackerType != null){
								if (mp.isDisplayable(ox, oy)){
									attackerType.playSound(2);
									doSleep();
								}
							//}
						}
					}							

					if (defender != null && defenderRange >= attackerRange && defender.getCombat() > 0){
						//if (defenderType != null){
							if (mp.isDisplayable(dx, dy)){
								defenderType.playSound(1);
							}
						//}

						//reduce combatable this turn
						defender.doCombat();
						//draw sequence
						drawCombatSequence(enemy, defender, attacker, p2, dir2, dx, dy);
						drawTechEffect(defender, ox, oy);
						world.doTechEffect(attacker, defender.getUpgrade());
						
						attackChance = defenderType.getAttackChance(defender);
						defendChance = attackerType.getDefendChance(attacker);
						if (attackChance > defendChance){
							//if die in combat, what to do?
							if (attacker.hurt(defenderType)){
								if (mp.isDisplayable(ox, oy)){
									//MH120
									world.startSpriteEffect(GameWorld.IMG_GET_HIT, p.x, p.y);
									//MH120
								}
								army.remove(0, uloader);
								if (mp.isDisplayable(ox, oy)){
									mp.drawCell(ox, oy);
								}
								//MIN08
								if (defender.reward() && defenderType.getPromotion()>-1){
									defender.setType(defenderType.getPromotion());
								}
								attacker = null;
								//if (defenderType != null){
									if (mp.isDisplayable(dx, dy)){
										defenderType.playSound(2);
										doSleep();
									}
								//}
							}
						}							
					}

					//retreat is possible when hp low to 1 and speed is better
					//and play has not force army to fight to dead
					if (attacker != null && attacker.getHP() < 2 &&
						army.getFlag(Army.MASK_ALLOW_RETREAT) == Army.MASK_ALLOW_RETREAT){
						if (army.getCount() > 1){
							//allow attacker to reorganize since this unit make retreat
							army.reorganize(uloader, true);
							enemy.reorganize(uloader, false);
						}else{
							//make retreat
							enemy.reorganize(uloader, false);
							break;
						}
					}else{
						//allow attacker leader to reorganize
						army.reorganize(uloader, false);
						enemy.reorganize(uloader, false);
					}

					attackRound++;
				}
				//dead or drown
				//canMove visible=true for the attackers
				if (army.getCount() == 0 || 
					!army.canMove(world.getGD().getBaseLand(ox, oy), uloader, attackRound > 0)){
					world.getGD().removeArmy(world.getGD().armyIndex(army));
					if (army.getOwner() == GameWorld.PLAYER_OWNER){
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, we have lost the entire company");
					}
					//Playing defeat animations
					if (mp.isDisplayable(ox, oy)){
						world.startSpriteEffect(GameWorld.IMG_DEFEAT01, p.x, p.y);
					}
				}
				//dead or drown
				if (enemy.getCount() == 0 || 
					!enemy.canMove(world.getGD().getBaseLand(dx, dy), uloader, attackRound > 0)){
					world.getGD().removeArmy(world.getGD().armyIndex(enemy));
					if (enemy.getOwner() == GameWorld.PLAYER_OWNER){
						GameWorld.printMessage("<%" + GameWorld.IMG_ADVISOR_SMALL + "%>My lord, we have lost the entire company");
					}
					//Playing defeat animations
					if (mp.isDisplayable(dx, dy)){
						world.startSpriteEffect(GameWorld.IMG_DEFEAT01, p2.x, p2.y);
					}
				}

				army.canMove(world.getGD().getBaseLand(ox, oy), uloader, true);

				//bombardment doesnt take movement
				//if (attackRound > 0){
				//	army.chargeMove(Army.ATTACK_MOVE_COST);
				//}
			}
		}
		mp.start();
		mp.setBusy(false);
	}
}

class House implements Serializable, UnitSwappable{
	public static final int MAX_RESIDENT = 3;
	protected int type;
	protected ArrayList residents;

	private int[] production;
	private int[] consume;

	public House(int t){
		type = t;
		residents = new ArrayList();
		production = new int[GameWorld.RESOURCE_SIZE];
		consume = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
			production[i] = 0;
			consume[i] = 0;
		}
	}

	public boolean transfer(UnitSwappable other, int i, UnitTypeLoader ul,
							HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, TechLoader tel){
		if (other instanceof Army && ((Army)other).getCount() < 2){
			return false;
		}
		Unit u = other.get(i);
		boolean result = add(u);
		if (result){
			recalProduction(hl, ul, tel);
			//MIN32
			if (other instanceof Army){
				((Army)other).remove(i, ul);
			}else{
				other.remove(i);
			}
			//other.remove(i);
			if (other instanceof House){
				((House)other).recalProduction(hl, ul, tel);
			}else if (other instanceof Site){
				((Site)other).recalProduction(tl, ol, ul, tel);
			}
		}
		return result;
	}

	public int getCount(){
		return residents.size();
	}

	public void recalProduction(HouseTypeLoader hloader, UnitTypeLoader uloader, TechLoader tloader){
		for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
			production[i] = 0;
			consume[i] = 0;
		}
		//calculate rate
		HouseType ht = hloader.getHouse(type);
		if (ht != null){
			int allow = ht.getProduction();
			//loop through units and add to matrix
			for (int i=0; i<residents.size(); i++){
				UnitType ut = uloader.getUnitType(((Unit)residents.get(i)).getType());
				if (ut != null){
					Tech tch = tloader.getTech(((Unit)residents.get(i)).getUpgrade());
					int tbonus = -1;
					if (tch != null) {
						tbonus = tch.getProdBonus();
					}
					for (int j=0; j<ut.getProductions(); j++){
						int t = ut.getProdType(j);
						if (t == allow && t>= 0 && t <GameWorld.RESOURCE_SIZE){
							int q = ut.getProdQuant(j);
							if (tbonus == j) {
								//Production bonus 50%
								q = (int)(q * 1.5);
							}
							production[t] += q;
						}
					}
					//consumption
					for (int j=0; j<ut.getConsumptions(); j++){
						int c = ut.getConsume(j);
						if (c>= 0 && c <GameWorld.RESOURCE_SIZE){
							int q = ut.getConQuant(j);
							consume[c] += q;
						}
					}
				}
			}
		}
	}

	public int[] getProduction(){
		return production;
	}

	public int[] getConsumption(){
		return consume;
	}

	public int getType(){
		return type;
	}

	public boolean add(Unit u){
		if (residents.size() < MAX_RESIDENT && u != null){
			residents.add(u);
			return true;
		}else{
			return false;
		}
	}

	public Unit get(int i){
		if (i >= 0 && i < residents.size()){
			return (Unit)residents.get(i);
		}else{
			return null;
		}
	}

	public void remove(int i){
		residents.remove(i);
	}
}

class Site implements Serializable, UnitSwappable{
	public static final int MAX_RESIDENT = 3;
	protected int posx, posy, base, topCount;
	protected int[] top;
	protected ArrayList residents;

	private int[] production;
	private int[] consume;

	public Site(int x, int y, GameData gd){
		posx = x;
		posy = y;
		base = gd.getBaseLand(posx, posy);
		top = new int[GameData.MAX_TOP_TILES];
		topCount = gd.getTopCount(posx , posy);
		for (int v=0; v<topCount; v++) {
			top[v] = gd.getTopLand(posx, posy, v);
		}

		residents = new ArrayList();
		production = new int[GameWorld.RESOURCE_SIZE];
		consume = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
			production[i] = 0;
			consume[i] = 0;
		}
		//System.out.println("Created site");
	}

	public void updateTopLand(GameData gd){
		topCount = gd.getTopCount(posx , posy);
		for (int v=0; v<topCount; v++) {
			top[v] = gd.getTopLand(posx, posy, v);
		}
	}

	public boolean transfer(UnitSwappable other, int i, UnitTypeLoader ul,
							HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, TechLoader tel){
		if (other instanceof Army && ((Army)other).getCount() < 2){
			return false;
		}
		Unit u = other.get(i);
		boolean result = add(u);
		if (result){
			recalProduction(tl, ol, ul, tel);
			//MIN32
			if (other instanceof Army){
				((Army)other).remove(i, ul);
			}else{
				other.remove(i);
			}
			//other.remove(i);
			if (other instanceof House){
				((House)other).recalProduction(hl, ul, tel);
			}else if (other instanceof Site){
				((Site)other).recalProduction(tl, ol, ul, tel);
			}
		}
		return result;
	}

	public void recalProduction(TerrainLoader tloader, OverlayLoader oloader,
		UnitTypeLoader uloader, TechLoader teloader){
		int[] rate = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<GameWorld.RESOURCE_SIZE; i++){
			production[i] = 0;
			consume[i] = 0;
			rate[i] = 0;
		}
		//calculate rate
		Terrain bt = tloader.getTerrain(base);
		if (bt != null){
			for (int j=0; j<bt.getProductions(); j++){
				int t = bt.getProdType(j);
				if (t>= 0 && t <GameWorld.RESOURCE_SIZE){
					int q = bt.getProdQuant(j);
					rate[t] += q;
					//System.out.println("base: " + j + " : " + rate[t]);
				}
			}
		}
		for (int v=0; v<topCount; v++) {
			Overlay tt = oloader.getOverlay(top[v]);
			if (tt != null){
				for (int j=0; j<tt.getProductions(); j++){
					int t = tt.getProdType(j);
					if (t>= 0 && t <GameWorld.RESOURCE_SIZE){
						int q = tt.getProdQuant(j);
						rate[t] += q;
						//System.out.println("top: " + t + ":" + q);
					}
				}
			}
		}
		//loop through units and add to matrix
		for (int i=0; i<residents.size(); i++){
			UnitType ut = uloader.getUnitType(((Unit)residents.get(i)).getType());
			//MH110 check if a unit can work in the type of terrain
			if (ut != null && ut.canWork(base)){
				Tech tch = teloader.getTech(((Unit)residents.get(i)).getUpgrade());
				int tbonus = -1;
				if (tch != null) {
					tbonus = tch.getProdBonus();
				}
				//production
				for (int j=0; j<ut.getProductions(); j++){
					int t = ut.getProdType(j);
					if (t>= 0 && t <GameWorld.RESOURCE_SIZE){
						int q = ut.getProdQuant(j);
						//Production bonus 50%
						if (tbonus == j) {
							q = (int)(q * 1.5);
						}
						production[t] += q * rate[t];
					}
				}
				//consumption
				for (int j=0; j<ut.getConsumptions(); j++){
					int c = ut.getConsume(j);
					if (c>= 0 && c <GameWorld.RESOURCE_SIZE){
						int q = ut.getConQuant(j);
						consume[c] += q;
					}
				}
			}
		}
	}

	public int getBase(){
		return base;
	}

	public int[] getProduction(){
		return production;
	}

	public int[] getConsumption(){
		return consume;
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

	public boolean add(Unit u){
		if (residents.size() < MAX_RESIDENT && u != null){
			residents.add(u);
			return true;
		}else{
			return false;
		}
	}

	public Unit get(int i){
		if (i >= 0 && i < residents.size()){
			return (Unit)residents.get(i);
		}else{
			return null;
		}
	}

	public int getCount(){
		return residents.size();
	}

	public void remove(int i){
		residents.remove(i);
	}
}

class HouseTypeLoader extends GameObjectLoader{
	private int defTrainer, defBuilder;

	public HouseTypeLoader(GameWorld world){
		super(world, "house.ini");

		defTrainer = -1;
		defBuilder = -1;
	}

	protected void add(Hashtable section, Image icon){
		HouseType ht = new HouseType(icon);

		for (Enumeration ec = section.keys(); ec.hasMoreElements();){
			String k = (String)ec.nextElement();
			String v = (String)section.get(k);
			ht.addProp(k, v);
		}

		objects.add(ht);
	}

	public ArrayList getIconList(){
		ArrayList iconList = new ArrayList();

		for (int i=0; i<getSize(); i++){
			iconList.add(new ImageIcon(getHouse(i).getSmallIcon()));
			//iconList.add(new ImageIcon(getHouse(i).getIcon()));
		}

		return iconList;
	}

	public int getDefTrainer(){
		//adding after
		if (defTrainer == -1){
			for (int i=0; i<objects.size(); i++){
				HouseType ht = (HouseType)objects.get(i);
				if (ht.getProduction() == GameWorld.RESOURCE_BOOK){
					defTrainer = i;
					break;
				}
			}
		}
		return defTrainer;
	}

	public int getDefBuilder(){
		//adding after
		if (defBuilder == -1){
			for (int i=0; i<objects.size(); i++){
				HouseType ht = (HouseType)objects.get(i);
				if (ht.getProduction() == GameWorld.RESOURCE_HAMMER){
					defBuilder = i;
					break;
				}
			}
		}
		return defBuilder;
	}

	public HouseType getHouse(int h){
		if (h < 0 || h >= objects.size()){
			return null;
		}else{
			return (HouseType)objects.get(h);
		}
	}
}

class HouseType extends GameObjectLight{
	protected int prod, mask;

	public HouseType(Image icon){
		super(icon);

		mask = 0;
	}

	public int getProduction(){
		return prod;
	}

	public int getMask(){
		return mask;
	}

	public void addProp(String k, String v){
		if (k.compareToIgnoreCase("name") == 0){
			name = v;
		}else if (k.compareToIgnoreCase("description") == 0){
			description = v;
		}else if (k.compareToIgnoreCase("production") == 0){
			try{
				prod = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("mask") == 0){
			try{
				mask = Integer.parseInt(v);
			}catch(Exception e){
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
		}else{
			properties.put(k, v);
		}
	}
}

class BaseTypeLoader extends GameObjectLoader{
	public BaseTypeLoader(GameWorld world){
		super(world, "base.ini");
	}

	protected void add(Hashtable section, Image icon){
		BaseType ht = new BaseType(icon);

		for (Enumeration ec = section.keys(); ec.hasMoreElements();){
			String k = (String)ec.nextElement();
			String v = (String)section.get(k);
			ht.addProp(k, v);
		}

		objects.add(ht);
	}

	public int getDefBaseType(){
		if (objects.size() > 0){
			return 0;
		}
		return -1;
	}

	public int getDefBaseType(int t){
		if (t < 0 || objects.size() <= t){
			return -1;
		}
		return t;
	}

	public BaseType getBaseType(int h){
		if (h < 0 || h >= objects.size()){
			return null;
		}else{
			return (BaseType)objects.get(h);
		}
	}
}
///////////////////////////////////////////////////////////////////////
class BaseType extends GameObjectLight{
	private int hidden;
	private int limit;

	public BaseType(Image icon){
		super(icon);

		hidden = -1;
		limit = Base.LIMIT;
	}

	public int getHiddenTech(){
		return hidden;
	}

	public int getLimit(){
		return limit;
	}

	public void addProp(String k, String v){
		if (k.compareToIgnoreCase("name") == 0){
			name = v;
		}else if (k.compareToIgnoreCase("description") == 0){
			description = v;
		}else if (k.compareToIgnoreCase("hidden") == 0){
			try{
				hidden = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("limit") == 0){
			try{
				limit = Integer.parseInt(v);
			}catch(Exception e){
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
		}else{
			properties.put(k, v);
		}
	}
}
///////////////////////////////////////////////////////////////////////
class TechLoader extends GameObjectLoader{
	public TechLoader(GameWorld world){
		super(world, "tech.ini");
	}

	protected void add(Hashtable section, Image icon){
		//System.out.println("Adding");
		Image eff = null;
		String file = (String)section.get("effect");
		if (file != null){
			eff = world.loadImage(file);
		}
		Tech t = new Tech(icon, eff);

		for (Enumeration ec = section.keys(); ec.hasMoreElements();){
			String k = (String)ec.nextElement();
			String v = (String)section.get(k);
			t.addProp(k, v);
		}

		objects.add(t);
		if (t.isBuildable()){
			buildable.add(t);
		}
	}

	//standard version
	public int getBuildableSize(){
		return buildable.size();
	}

	public Tech getBuildable(int ind){
		return (Tech)buildable.get(ind);
	}

	public int getTechIndex(int bld){
		return buildable.indexOf(getBuildable(bld));
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
	//end of standard

	public boolean damage(Unit u, int t){
		Tech tech = getTech(t);
		if (tech != null){
			return tech.damage(u);
		}else{
			return false;
		}
	}

	public ArrayList getIconList(){
		ArrayList iconList = new ArrayList();

		for (int i=0; i<getSize(); i++){
			iconList.add(new ImageIcon(getTech(i).getSmallIcon()));
			//iconList.add(new ImageIcon(getTech(i).getIcon()));
		}

		return iconList;
	}

	public ArrayList getIconList(ArrayList other){
		ArrayList iconList = new ArrayList();

		try{
		for (int i=0; i<other.size(); i++){
			//iconList.add(new ImageIcon(getTech(((Integer)other.get(i)).intValue()).getSmallIcon()));
			iconList.add(new ImageIcon(getTech(((Integer)other.get(i)).intValue()).getIcon()));
		}
		}catch(Exception e){
		}

		return iconList;
	}

	public Tech getTech(int h){
		if (h < 0 || h >= objects.size()){
			return null;
		}else{
			return (Tech)objects.get(h);
		}
	}
}

class Tech extends GameObjectLight{
	public static final int DEFAULT_EFFECT_LEN = 4;
	public static final int TYPE_BUILDABLE = 0;
	public static final int TYPE_HIDDEN = 1;

	//u_ unit, h_ house, r_ requirement, d_ damage
	private int u_upgrade, u_attack, u_defend;
	private int u_speed, u_move, h_upgrade;
	private int d_hit, d_combat, type;
	private int u_combat, u_range, r_range, r_tech;
	private int r_unit, r_armies, r_cities, r_player;
	private int[] cost, resource;
	private Image effect;
	private int effectlen;

	private int prod_bonus;

	public Tech(Image icon, Image eff){
		super(icon);

		effect = eff;

		cost = new int[GameWorld.TECH_SIZE];
		for (int i=0; i<cost.length; i++){
			cost[i] = 0;
		}
		resource = new int[GameWorld.RESOURCE_SIZE];
		for (int i=0; i<resource.length; i++){
			resource[i] = 0;
		}
		r_range = 0;
		u_upgrade = -1;
		r_unit = -1;
		r_tech = -1;
		r_player = -1;
		u_combat = 0;
		d_hit = 0;
		d_combat = 0;
		prod_bonus = -1;
		effectlen = DEFAULT_EFFECT_LEN;
		type = TYPE_BUILDABLE;
	}

	public boolean isUpgrade(){
		if (getUAttack() < 1 && getUDefend() < 1 && getUCombat() < 1 &&
			getUSpeed() < 1 && getUMove() < 1 && getURange() < 1 &&
			getDHit() < 1 && getDCombat() < 1 && getProdBonus()==-1){
			return false;
		}else{
			return true;
		}
	}

	public boolean isBuildable(){
		return type == TYPE_BUILDABLE;
	}

	public int getRUnit(){
		return r_unit;
	}

	public int getRRange(){
		return r_range;
	}

	public int getRTech(){
		return r_tech;
	}

	public int getRArmies(){
		return r_armies;
	}

	public int getRCities(){
		return r_cities;
	}

	public int getRPlayer(){
		return r_player;
	}

	public int getUUpgrade(){
		return u_upgrade;
	}

	public int getUAttack(){
		return u_attack;
	}

	public int getUDefend(){
		return u_defend;
	}

	public int getUSpeed(){
		return u_speed;
	}

	public int getUMove(){
		return u_move;
	}

	public int getUCombat(){
		//System.err.println(getName() + " : " + u_combat);
		return u_combat;
	}

	public int getURange(){
		return u_range;
	}

	public int getHUpgrade(){
		return h_upgrade;
	}

	public int getDHit(){
		return d_hit;
	}

	public int getDCombat(){
		return d_combat;
	}

	public int getProdBonus(){
		return prod_bonus;
	}

	public boolean damage(Unit u){
		int i = 0,j = 0;
		if (d_hit > 0){
			int hits = (int)((d_hit+1) * Randomizer.getNextRandom());
			for (i=0; i<hits; i++){
				u.hurt();
			}
		}
		if (d_combat > 0){
			int wears = (int)((d_combat+1) * Randomizer.getNextRandom());
			for (j=0; j<wears; j++){
				u.doCombat();
			}
		}
		return i+j>0;
	}

	public int[] getCost(){
		return cost;
	}

	public int getCost(int t){
		return cost[t];
	}

	public int getResource(int r){
		return resource[r];
	}

	public boolean hasEffect(){
		return effect != null;
	}

	public Image getEffectImage(){
		return effect;
	}

	public int getEffectLen(){
		return effectlen;
	}

	public void addProp(String k, String v){
		if (k.compareToIgnoreCase("name") == 0){
			name = v;
		}else if (k.compareToIgnoreCase("description") == 0){
			description = v;
		}else if (k.startsWith("cost")){
			try{
				int cnt = 0;
				StringTokenizer st = new StringTokenizer(v, ",");
				while (st.hasMoreTokens() && cnt < cost.length) {
					cost[cnt] = Integer.parseInt((String)st.nextToken());
					cnt++;
				}
			}catch(Exception e){
			}
		}else if (k.startsWith("resource")){
			try{
				int cnt = 0;
				StringTokenizer st = new StringTokenizer(v, ",");
				while (st.hasMoreTokens() && cnt < resource.length) {
					resource[cnt] = Integer.parseInt((String)st.nextToken());
					cnt++;
				}
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("production") == 0){
			try{
				prod_bonus = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_range") == 0){
			try{
				u_range = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_combat") == 0){
			try{
				u_combat = Integer.parseInt(v);
				//System.err.println(getName() + " : " + getUCombat());
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_upgrade") == 0){
			try{
				u_upgrade = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("h_upgrade") == 0){
			try{
				h_upgrade = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_attack") == 0){
			try{
				u_attack = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_defend") == 0){
			try{
				u_defend = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_move") == 0){
			try{
				u_move = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("u_speed") == 0){
			try{
				u_speed = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("r_range") == 0){
			try{
				r_range = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("r_tech") == 0){
			try{
				r_tech = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("r_unit") == 0){
			try{
				r_unit = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("r_armies") == 0){
			try{
				r_armies = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("r_cities") == 0){
			try{
				r_cities = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("r_player") == 0){
			try{
				r_player = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("d_hit") == 0){
			try{
				d_hit = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("d_combat") == 0){
			try{
				d_combat = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("effectlen") == 0){
			try{
				effectlen = Integer.parseInt(v);
			}catch(Exception e){
			}
		}else if (k.compareToIgnoreCase("type") == 0){
			if (v.compareToIgnoreCase("buildable") == 0){
				type = TYPE_BUILDABLE;
			}else if (v.compareToIgnoreCase("hidden") == 0){
				type = TYPE_HIDDEN;
			}
		}else{
			properties.put(k, v);
		}
	}
}
///////////////////////////////////////////////////////////////////////
class ConsoleOutStream extends ByteArrayOutputStream
{
	private JTextPane textPane;
	private Color textColor;
	private javax.swing.text.StyledDocument document;
	private javax.swing.text.SimpleAttributeSet[] attSet = {
		new javax.swing.text.SimpleAttributeSet(),
		new javax.swing.text.SimpleAttributeSet(),
		new javax.swing.text.SimpleAttributeSet()};
	private int attCounter;
	//private ArrayList icons;
	private GameIconStorage gis;

	//public ConsoleOutStream(ArrayList il, JTextPane textPane){
	public ConsoleOutStream(GameIconStorage il, JTextPane tp){
		this(il, tp, tp.getForeground());
	}

	//public ConsoleOutStream(ArrayList il, JTextPane textPane, Color textColor){
	public ConsoleOutStream(GameIconStorage il, JTextPane tp, Color textColor){
		//icons = il;
		gis = il;
		textPane = tp;
		document = textPane.getStyledDocument();

		javax.swing.text.StyleConstants.setForeground(attSet[0], Color.blue);
		//javax.swing.text.StyleConstants.setBackground(attSet[0], Color.white);
		javax.swing.text.StyleConstants.setFontFamily(attSet[0],"Verdana");
		javax.swing.text.StyleConstants.setFontSize(attSet[0],9);

		javax.swing.text.StyleConstants.setForeground(attSet[1], Color.red);
		//javax.swing.text.StyleConstants.setBackground(attSet[1], Color.green);
		javax.swing.text.StyleConstants.setFontFamily(attSet[1],"Verdana");
		javax.swing.text.StyleConstants.setFontSize(attSet[1],9);

		javax.swing.text.StyleConstants.setForeground(attSet[2], Color.black);
		//javax.swing.text.StyleConstants.setBackground(attSet[2], Color.green);
		javax.swing.text.StyleConstants.setFontFamily(attSet[2],"Verdana");
		javax.swing.text.StyleConstants.setFontSize(attSet[2],9);

		attCounter = 0;
	}

	synchronized public void flush()
	{
		try {
			String txt = toString();

			int start = txt.indexOf("<%");
			if (start == -1){
				document.insertString(document.getLength(), txt,attSet[attCounter]);
			}else if (gis != null){//if (icons != null) {
				int last = 0;
				while (start > -1){
					int end = txt.indexOf("%>", start);
					if (end > -1){
						String strOut = txt.substring(last, start);
						String strIn = txt.substring(start + 2, end);
						document.insertString(document.getLength(), strOut,attSet[attCounter]);
						try{
							int ind = Integer.parseInt(strIn);
							//textPane.insertIcon(new ImageIcon((Image)icons.get(ind)));
							//System.err.println("SHOW PICTURE " + ind);
							Image img = gis.getGameIcon(ind);
							if (img != null) {
								textPane.insertIcon(new ImageIcon(img));
							}
						}catch(NumberFormatException e){
						}
					}else{
						//tolerate
						end = start;
					}
					last = end + 2;
					start = txt.indexOf("<%", end + 2);
				}
				if (last < txt.length()){
					String strOut = txt.substring(last, txt.length());
					document.insertString(document.getLength(), strOut,attSet[attCounter]);
				}
			}

			attCounter++;
			if (attCounter >= attSet.length){
				attCounter = 0;
			}
		}catch (javax.swing.text.BadLocationException ble){
		}
		textPane.getCaret().setDot(Integer.MAX_VALUE);
		reset();
	}
}
///////////////////////////////////////////////////////////////////////
class DummyPanel extends JComponent{
	public Dimension getPreferredSize(){
		return new Dimension(5, 5);
	}
	public boolean isOpaque(){
		return true;
	}
}
///////////////////////////////////////////////////////////////////////
class Randomizer{
 private static final int MAX_RANDOM = 5000;
 private static double[] randoms;
 private static int pointer;
 
 static {
  randoms = new double[MAX_RANDOM];
  pointer = 0;
  for (int i=0; i<MAX_RANDOM; i++){
   randoms[i] = Math.random();
  }
 }
 
 public static double getNextRandom(){
  if (pointer >= MAX_RANDOM || pointer < 0){
   pointer = 0;
  }
  return randoms[pointer++];
 }
}

class NameDatabase{
 private static final int MAX_NAMES = 50;
 private static final int MAX_FNAMES = 10;
 private static final int MAX_LNAMES = 10;

 private static String names[];
 private static String fnames[];
 private static String lnames[];
 private static int pointer;
 
 static {
	fnames = new String[MAX_FNAMES];
	fnames[0] = "Joe";
	fnames[1] = "Daved";
	fnames[2] = "Robeart";
	fnames[3] = "Lockee";
	fnames[4] = "Roe";
	fnames[5] = "Kyle";
	fnames[6] = "Bane";
	fnames[7] = "Yan";
	fnames[8] = "Jin";
	fnames[9] = "Simone";

	lnames = new String[MAX_LNAMES];
	lnames[0] = "Ruumble";
	lnames[1] = "Hallowfield";
	lnames[2] = "Miller";
	lnames[3] = "Tonberry";
	lnames[4] = "Psycheer";
	lnames[5] = "Baranzki";
	lnames[6] = "Lordien";
	lnames[7] = "Marshmellowe";
	lnames[8] = "Troicen";
	lnames[9] = "Quinjan";

	names = new String[MAX_NAMES];
	pointer = 0;

	names[0] = "Porto Roco";
	names[1] = "New Heaven";
	names[2] = "Damper Town";
	names[3] = "Crossway Citadel";
	names[4] = "Fairland";
	names[5] = "Water Pass";
	names[6] = "Simpleton Town";
	names[7] = "Cold Wall";
	names[8] = "Rigibigid";
	names[9] = "Korn Barnyard";
	names[10] = "Arrowhead";
	names[11] = "Rosswell";
	names[12] = "The Foresters";
	names[13] = "Monaville";
	names[14] = "Adelaiden";
	names[15] = "Woolmooloo";
	names[16] = "Randle Wall";
	names[17] = "Flints Hill";
	names[18] = "Scicilia";
	names[19] = "Passageway Citadel";
	names[20] = "Gingerhall";
	names[21] = "Barans Height";
	names[22] = "Lockheal Meadowe";
	names[23] = "Townsville";
	names[24] = "Lilyfield Height";
	names[25] = "Highland Citadel";
	names[26] = "Hampstonshire";
	names[27] = "Portland Ville";
	names[28] = "Danstonfield";
	names[29] = "Stonehead Crossway";
	names[30] = "Boxgrain Citadel";
	names[31] = "Bostonshire Height";
	names[32] = "Maniwell";
	names[33] = "Tonoloomoo";
	names[34] = "Windhill";
	names[35] = "Kingsport";
	names[36] = "Sandyhill";
	names[37] = "Riverville";
	names[38] = "Riverwood Meadowe";
	names[39] = "Sandlewood";
	names[40] = "Foxwood Ville";
	names[41] = "Hanon Rowes";
	names[42] = "Magnon Villa";
	names[43] = "Cross Citadel";
	names[44] = "Harbour Ville";
	names[45] = "Transquilities";
	names[46] = "Zilions Town";
	names[47] = "Fire Port";
	names[48] = "Rockwood Height";
	names[49] = "Goldenflower Ville";
 }
 
 public static String getNextName(){
  if (pointer >= MAX_NAMES || pointer < 0){
   pointer = 0;
  }
  return names[pointer++];
 }

 public static int getRandomFirstName(){
	 return (int)(Randomizer.getNextRandom()*MAX_FNAMES);
 }

 public static int getRandomLastName(){
	 return (int)(Randomizer.getNextRandom()*MAX_LNAMES);
 }

 public static String getCompositeName(int f, int l){
	 if (f>=0 && f<MAX_FNAMES && l>=0 && l<MAX_LNAMES) {
		return fnames[f] + " " +lnames[l];
	 }else{
		 return "Unknown";
	 }
 }
}

class ClipLoader{
	private Hashtable cache;

	public ClipLoader(){
		cache = new Hashtable();
	}

	public AudioClip getClip(String file){
		if (!cache.containsKey(file)){
			////System.err.println(file);
			AudioClip clip = loadSound(file);
			cache.put(file, clip);
			return clip;
		}else{
			return (AudioClip)cache.get(file);
		}
	}

	//to-do uncache wav
	protected AudioClip loadSound(String file){
		try{
			AudioClip clip = 
				java.applet.Applet.newAudioClip(this.getClass().getResource("sounds/"+file));
			return clip;
		}catch(Exception e){
			return null;
		}
	}
}

class JobChooserDialog extends JDialog{
	//private TechLoader teloader;
	//private PlayerLoader ploader;
	//private ArrayList inds;
	private int value;
	private JList lstJob;
	//private JTextPane txtComment;
	private ImageCellRenderer icr;
	private GameWorld world;

	public JobChooserDialog(Frame owner, GameWorld w){
		super(owner, "Select a quest", true);

		world = w;

		lstJob = new JList();
		icr = new ImageCellRenderer(null);
		lstJob.setCellRenderer(icr);
		//lstJob.setBackground(GameWorld.COL_GREEN);
		lstJob.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				if (e.getValueIsAdjusting()){
					return;
				}
				int selected = lstJob.getSelectedIndex();
				if (selected == -1){
					return;
				}
				value = selected;
			}
		});

		ListScrollPane scrPane = new ListScrollPane(lstJob, 500, 300);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrPane, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(owner);
	}

	public void showJobs(GameData gd, int o){
		int jc = gd.getJobSize(o);
		if (jc <= 0) {
			if (o == GameWorld.PLAYER_OWNER) {
				GameWorld.printMessage("<%"+ GameWorld.IMG_WARN +"%>No jobs available at the moment");
			}
			return;
		}
		ArrayList ads = new ArrayList();
		ArrayList ims = new ArrayList();

		for (int i=0; i<jc; i++) {
			Job jb = gd.getJob(o, i);
			if (jb != null) {
				//System.out.println("JOB ICON: " + jb.getIcon());
				ims.add(new ImageIcon(world.getGameIcon(jb.getIcon())));
				ads.add(jb.getDescription());
			}
		}
		icr.setIconList(ims);
		lstJob.setListData(ads.toArray());

		value = -1;
		show();
	}

	public int getValue(){
		return value;
	}

	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}
}

class CurrentJobDialog extends JDialog{
	private int value;
	private JList lstJob;
	private GameWorld world;

	public CurrentJobDialog(Frame owner, GameWorld w){
		super(owner, "Current quests", true);

		world = w;

		lstJob = new JList();
		lstJob.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				if (e.getValueIsAdjusting()){
					return;
				}
				int selected = lstJob.getSelectedIndex();
				if (selected == -1){
					return;
				}
				value = selected;
			}
		});

		ListScrollPane scrPane = new ListScrollPane(lstJob, 500, 300);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrPane, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(owner);
	}

	public void showJobs(GameData gd, int o){
		ArrayList ads = new ArrayList();
		for (int i=0; i<gd.getArmySize(); i++) {
			Army current = gd.getArmy(i);
			if (current != null && current.getOwner() == o) {
				Job quest = current.getJob();
				if (quest != null) {
					String names = "";
					for (int j=0; j<current.getCount(); j++) {
						Unit bloke = current.get(j);
						if (bloke != null) {
							if (names.length()>0) {
								names += ", ";
							}
							names += bloke.getName();
						}
					}
					String desc = "<html>Troops: <b>" + names + "</b><br>Quest: <i>" + quest.getDescription() + "</i></html>";

					ads.add(desc);
				}
			}
		}
		lstJob.setListData(ads.toArray());

		value = -1;
		show();
	}

	public int getValue(){
		return value;
	}

	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		};
		//Escape key
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//Assign the ESCAPE label to closing action
		layeredPane.getActionMap().put("ESCAPE", closeAction);
		//Assign the escape key to ESCAPE label
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "ESCAPE");
	}
}

class GameTrader implements ResourceHolder, Serializable{
	private int resources[];

	public GameTrader(){
		resources = new int[GameWorld.RESOURCE_SIZE];
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
}

class GameDirection{
	public final static int MOVE_SPRITE_NUMBER = 2;
	public final static int COMBAT_SPRITE_NUMBER = 2;
	private static int[][][] MOVE_SPRITE;
	private static int[][][] COMBAT_SPRITE;

	static {
		//1,1 correspond to 0,0 which is staying still so no sprite involve here
		MOVE_SPRITE = new int[MOVE_SPRITE_NUMBER][3][3];
		MOVE_SPRITE[0][0][2] = 1;
		MOVE_SPRITE[0][1][2] = 3;
		MOVE_SPRITE[0][2][2] = 5;
		MOVE_SPRITE[0][2][1] = 7;
		MOVE_SPRITE[0][2][0] = 9;
		MOVE_SPRITE[0][1][0] = 11;
		MOVE_SPRITE[0][0][0] = 13;
		MOVE_SPRITE[0][0][1] = 15;

		MOVE_SPRITE[1][0][2] = 2;
		MOVE_SPRITE[1][1][2] = 4;
		MOVE_SPRITE[1][2][2] = 6;
		MOVE_SPRITE[1][2][1] = 8;
		MOVE_SPRITE[1][2][0] = 10;
		MOVE_SPRITE[1][1][0] = 12;
		MOVE_SPRITE[1][0][0] = 14;
		MOVE_SPRITE[1][0][1] = 16;

		//1,1 correspond to 0,0 which is staying still so no sprite involve here
		COMBAT_SPRITE = new int[COMBAT_SPRITE_NUMBER][3][3];
		COMBAT_SPRITE[0][0][2] = 17;
		COMBAT_SPRITE[0][1][2] = 19;
		COMBAT_SPRITE[0][2][2] = 21;
		COMBAT_SPRITE[0][2][1] = 23;
		COMBAT_SPRITE[0][2][0] = 25;
		COMBAT_SPRITE[0][1][0] = 27;
		COMBAT_SPRITE[0][0][0] = 29;
		COMBAT_SPRITE[0][0][1] = 31;

		COMBAT_SPRITE[1][0][2] = 18;
		COMBAT_SPRITE[1][1][2] = 20;
		COMBAT_SPRITE[1][2][2] = 22;
		COMBAT_SPRITE[1][2][1] = 24;
		COMBAT_SPRITE[1][2][0] = 26;
		COMBAT_SPRITE[1][1][0] = 28;
		COMBAT_SPRITE[1][0][0] = 30;
		COMBAT_SPRITE[1][0][1] = 32;
	}

	public static int getMoveSprite(int dx, int dy, int index){
		dx++;
		dy++;
		if (index<0 || index>=MOVE_SPRITE_NUMBER || dx<0 || dy<0 || dx>2 || dy>2) {
			return 0;
		}else{
			return MOVE_SPRITE[index][dx][dy];
		}
	}

	public static int getCombatSprite(int dx, int dy, int index){
		dx++;
		dy++;
		if (index<0 || index>=COMBAT_SPRITE_NUMBER || dx<0 || dy<0 || dx>2 || dy>2) {
			return 0;
		}else{
			return COMBAT_SPRITE[index][dx][dy];
		}
	}
}