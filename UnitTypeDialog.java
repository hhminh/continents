//Experimental

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class UnitTypeDialog extends JDialog{
	private GameWorld world;
	private TechLoader teloader;
	private PlayerLoader ploader;

	private Unit unit;
	private int owner;
	private ResourceHolder base;

	private ImagePanel panPortrait, panUpgrade;
	private JList lstAttribute;
	private UnitTypeLoader uloader;
	private JLabel lblName;
	private JButton btnUpgrade, btnTalk;

	public UnitTypeDialog(Frame fowner, GameWorld w, TechLoader tel, PlayerLoader pl,
						UnitTypeLoader ul){
		super(fowner, "Details", true);

		world = w;
		uloader = ul;
		ploader = pl;
		teloader = tel;

		lblName = new JLabel();
		panPortrait = new ImagePanel(48, 48);
		panUpgrade = new ImagePanel(48, 48);
		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(panPortrait, BorderLayout.NORTH);
		panTemp.add(new JLabel("Upgrade:"), BorderLayout.CENTER);
		panTemp.add(panUpgrade, BorderLayout.SOUTH);

		lstAttribute = new JList();
		lstAttribute.setBackground(getBackground());

		ListScrollPane scrPane = new ListScrollPane(lstAttribute, 320, 48 * 3);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		btnUpgrade = new JButton("Upgrade Equipments");
		btnUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (unit != null && base != null){
					if (world.showUpgrade(base, unit, owner)){
						if (drawDetails()){
							hide();
							show();
						}
					}else{
						//nothing
					}
				}
			}
		});

		btnTalk = new JButton("Conversation");
		btnTalk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (unit != null){
					JOptionPane.showMessageDialog(null, "Proposed feature:\n"+
						"Allowing conversation using RPG style with character\n"+
						"Currently in development, including quest, script related "+
						"events", GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
				}
			}
		});

		JButton btnExit = new JButton("Done");
		btnExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();
			}
		});

		JPanel panTemp2 = new JPanel(new BorderLayout());
		panTemp2.add(new DummyPanel(), BorderLayout.NORTH);
		panTemp2.add(btnUpgrade, BorderLayout.WEST);
		//panTemp2.add(btnTalk, BorderLayout.EAST);
		panTemp2.add(btnExit, BorderLayout.EAST);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(lblName, BorderLayout.NORTH);
		getContentPane().add(panTemp, BorderLayout.WEST);
		getContentPane().add(scrPane, BorderLayout.EAST);
		getContentPane().add(panTemp2, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(fowner);
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

	protected String symbol(String s, int t){
		String tmp = "";
		for (int i=0; i<t; i++){
			tmp += s;
		}
		return tmp;
	}

	private void setUpgradeIcon(){
		if (unit.getUpgrade() > -1){
			Image img2 = teloader.getTech(unit.getUpgrade()).getIcon();
			panUpgrade.setImage(img2);
		}else{
			panUpgrade.setImage(null);
		}
	}

	private String repeatImage(int times, String image){
		String url = world.getURL("images/"+image);
		String result = "";
		for (int i=0; i<times; i++){
			result += "<img src=\""+url+"\" border=\"0\">";
		}
		return result;
	}

	private boolean drawDetails(){
		UnitType ut = uloader.getUnitType(unit.getType());
		if (ut != null){
			Tile t = ut.getTile();
			Image img = t.getImage(owner);
			panPortrait.setImage(img);

			setUpgradeIcon();

			ArrayList atts = new ArrayList();
			atts.add("+Name: " + unit.getName());
			//atts.add("+Description:");
			atts.add("+Job: " + ut.getDescription());

			if (unit.getTrait()<0 || unit.getTrait()>GameWorld.TRAIT_NAME.length) {
				atts.add("+Trait: Unknown");
			}else{
				atts.add("+Trait: " + GameWorld.TRAIT_NAME[unit.getTrait()]);
				atts.add("<html>+Loyalty: " + repeatImage(GameWorld.TRAIT_LOYALTY[unit.getTrait()], "loyal.gif") + "</html>");
				atts.add("<html><i>Loyal units are harder to bribe and braver in battles</i></html>"); 
			}

			atts.add(" ");
			atts.add("+Attributes:");
			if (ut.getLeaderLevel() > 0){
				//atts.add("Leadership: " + Integer.toString(ut.getLeaderLevel()));
				atts.add("<html>Leadership: " + 
					repeatImage(ut.getLeaderLevel(), "flag.gif")+"</html>");
			}
			if (ut.getPhysical() > 0){
				//atts.add("Strength: " + Integer.toString(ut.getPhysical()));
				atts.add("<html>Damage: " + 
					repeatImage(ut.getPhysical(), "hammer.gif")+"</html>");
			}
			if (ut.getToughness() > 0){
				//atts.add("Toughness: " + Integer.toString(ut.getToughness()));
				atts.add("<html>Armour: " + 
					repeatImage(ut.getToughness(), "bluehammer.gif")+"</html>");
			}
			//atts.add("<html>Hit Pts: " + Integer.toString(unit.getHP()) + " / " +
			//	Integer.toString(unit.getMaxHP())+"</html>");
			int baseHP = unit.getHP();
			int extraHP = unit.getMaxHP() - ut.getXHP() - UnitType.HP_START;
			String strHP = "<html>Hit Pts: " + repeatImage(baseHP, "heart.gif")+ "&nbsp;" + repeatImage(unit.getMaxHP()-unit.getHP(), "heartblack.gif");
			if (extraHP > 0) {
				strHP += "&nbsp;(" + repeatImage(extraHP, "heartup.gif")+ ")";
			}
			strHP += "</html>";
			atts.add(strHP);

			atts.add("Skill Lvl: " + UnitType.LEVEL_NAME[unit.getLevel()]);

			atts.add("XP: " + Integer.toString(unit.getXP()));
			atts.add("<html><i>XP can be earned through battles or by completing buildings and trainings</i></html>"); 

			atts.add(" ");
			atts.add("+Skills:");
			if (ut.getAttack(unit) > 0){
				//atts.add("Offense: " + Integer.toString(ut.getAttack(unit)));
				atts.add("<html>Offense: " + 
					repeatImage(ut.getAttack(unit), "sword.gif")+"</html>");
			}
			if (ut.getDefend(unit) > 0){
				//atts.add("Defence: " + Integer.toString(ut.getDefend(unit)));
				atts.add("<html>Defence: " + 
					repeatImage(ut.getDefend(unit),"shield.gif")+"</html>");
			}
			if (ut.getRange(unit) > 0){
				//atts.add("Range: " + Integer.toString(ut.getRange(unit)));
				atts.add("<html>Range: " + 
					repeatImage(ut.getRange(unit), "bow.gif")+"</html>");
			}else{
				atts.add("<html><i>Meelee combat unit</i></html>"); 
			}
			//MH110
			String strMove = "";
			if (ut.canMove(GameWorld.TOWN_LAND)){
				strMove += "on land";
			}
			if (ut.canMove(GameWorld.SEA_LAND)){
				if (strMove.length() > 0){
					strMove += ", ";
				}
				strMove += "sea";
			}
			if (strMove.length() > 0){
				atts.add("Travel: " + strMove);
			}
			if (ut.getMove(unit) > 0){
				//atts.add("Movement: " + Integer.toString(ut.getMove(unit)));
				atts.add("<html>Movement: " + 
					repeatImage(ut.getMove(unit),"shoe.gif")+"</html>");
			}

			//atts.add("Combat Speed: " + Integer.toString(ut.getSpeed(unit)));
			atts.add("<html>Combat Speed: " + 
				repeatImage(ut.getSpeed(unit),"star.gif")+"</html>");

			if (ut.getCombat(unit) > 0){
				//atts.add("Combat Points: " + Integer.toString(unit.getCombat()) + " / " +
				//	Integer.toString(ut.getCombat(unit)+unit.getLevel()));
				atts.add("<html>Combat Points: " + 
					repeatImage(unit.getCombat(),"diamond.gif")+"&nbsp;"+
					repeatImage(ut.getCombat(unit)+unit.getLevel()-unit.getCombat(),
					"diamondblack.gif")+"</html>");
			}

			if (unit.getUpgrade() > -1){
				Tech tech = teloader.getTech(unit.getUpgrade());
				atts.add(" ");
				atts.add("+Upgrade: " + tech.getName());
				atts.add(" (" + tech.getDescription() + ")");
				if (tech.getDHit() > 0){
					atts.add("Xtra Damage: " + Integer.toString(tech.getDHit()));
				}
				if (tech.getDCombat() > 0){
					atts.add("Stunning: " + Integer.toString(tech.getDCombat()));
				}
				int tbonus = tech.getProdBonus();
				if (tbonus > -1 && tbonus < GameWorld.RESOURCE_SIZE) {
					atts.add("Production bonus: " + GameWorld.RESOURCE_NAME[tbonus] + " (50%)");
				}
			}

			atts.add(" ");
			atts.add("+Spying:");
			for (int i=0; i<ut.getXAttacks(); i++){
				int type = ut.getXAttack(i);
				int quant = ut.getXAQuant(i);

				atts.add("Damages " + Integer.toString(quant) + " x " + GameWorld.RESOURCE_NAME[type] + " when attack");
			}

			atts.add(" ");
			atts.add("+Productions:");
			//MH110
			String strWork = "";
			if (ut.canWork(GameWorld.TOWN_LAND)){
				strWork += "on land";
			}
			if (ut.canWork(GameWorld.SEA_LAND)){
				if (strWork.length() > 0){
					strWork += ", ";
				}
				strWork += "sea";
			}
			if (strWork.length() > 0){
				atts.add("Working: " + strWork);
			}
			
			for (int i=0; i<ut.getProductions(); i++){
				int type = ut.getProdType(i);
				int quant = ut.getProdQuant(i);

				if (type == GameWorld.RESOURCE_HAMMER && quant > 0){
					atts.add("<html><i>Can participate in building</i></html>");
				}else if (type == GameWorld.RESOURCE_BOOK && quant > 0){
					atts.add("<html><i>Can be trained or do research</i></html>");
				}else{
					atts.add(Integer.toString(quant) + " x " +
						GameWorld.RESOURCE_NAME[type] + " per turn");
				}
			}

			atts.add(" ");
			atts.add("+Consumption:");
			for (int i=0; i<ut.getConsumptions(); i++){
				int type = ut.getConsume(i);
				int quant = ut.getConQuant(i);

				atts.add(Integer.toString(quant) + " x " +
					GameWorld.RESOURCE_NAME[type] + " per turn");
			}

			if (ut.getPromotion() > 0){
				UnitType ut2 = uloader.getUnitType(ut.getPromotion());
				if (ut2 != null){
					atts.add(" ");
					atts.add("+Promotions:" + ut2.getName());
					atts.add("<html><i>Must reach highest level for promotion</i></html>");
				}
			}

			lstAttribute.setListData(atts.toArray());

			if (ut.isCombatUnit()){
				if (ut.getType() == UnitType.TYPE_KING){
					lblName.setText(ploader.getPlayer(owner).getName() + "'s " + ut.getName());
					//setTitle(ploader.getPlayer(owner).getName() + "'s " + ut.getName());
				}else{
					lblName.setText(ploader.getPlayer(owner).getName() + "'s " + UnitType.LEVEL_NAME[unit.getLevel()] + " " + ut.getName());
					//setTitle(ploader.getPlayer(owner).getName() + "'s " + UnitType.LEVEL_NAME[unit.getLevel()] + " " + ut.getName());
				}
			}else{
				lblName.setText(ploader.getPlayer(owner).getName() + "'s " + ut.getName());
				//setTitle(ploader.getPlayer(owner).getName() + "'s " + ut.getName());
			}
			return true;
		}else{
			return false;
		}
	}

	public void showUnitType(Unit u, ResourceHolder res, int o){
		unit = u;
		base = res;
		owner = o;

		if (drawDetails()){
			//Show hide button
			if (res != null && u != null){// && res instanceof Base) {
				//System.err.println("VISIBLE");
				btnUpgrade.setEnabled(true);
			}else{
				//System.err.println("inVISIBLE");
				btnUpgrade.setEnabled(false);
			}
			//Clear selection
			lstAttribute.setSelectedIndex(-1);
			//show attributes
			show();
		}
	}
}