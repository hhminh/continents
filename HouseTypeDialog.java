//Experimental
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class HouseTypeDialog extends JDialog{
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
	private JButton btnUpgrade;

	public HouseTypeDialog(Frame fowner, GameWorld w, TechLoader tel, PlayerLoader pl,
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
		panTemp.add(panUpgrade, BorderLayout.SOUTH);

		lstAttribute = new JList();
		lstAttribute.setBackground(getBackground());

		ListScrollPane scrPane = new ListScrollPane(lstAttribute, 48 * 3, 48 * 3);
		scrPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		btnUpgrade = new JButton("Upgrade Equipments");
		btnUpgrade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (unit != null){
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

		JPanel panTemp2 = new JPanel(new BorderLayout());
		panTemp2.add(new DummyPanel(), BorderLayout.NORTH);
		panTemp2.add(btnUpgrade, BorderLayout.SOUTH);

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

	private boolean drawDetails(){
		UnitType ut = uloader.getUnitType(unit.getType());
		if (ut != null){
			Tile t = ut.getTile();
			Image img = t.getImage(owner);
			panPortrait.setImage(img);

			setUpgradeIcon();

			ArrayList atts = new ArrayList();
			if (ut.getLeaderLevel() > 0){
				atts.add("Leadership: " + Integer.toString(ut.getLeaderLevel()));
			}
			if (ut.getAttack(unit) > 0){
				atts.add("Offense: " + Integer.toString(ut.getAttack(unit)));
			}
			if (ut.getDefend(unit) > 0){
				atts.add("Defence: " + Integer.toString(ut.getDefend(unit)));
			}
			if (ut.getPhysical() > 0){
				atts.add("Strength: " + Integer.toString(ut.getPhysical()));
			}
			if (ut.getRange(unit) > 0){
				atts.add("Range: " + Integer.toString(ut.getRange(unit)));
			}
			if (ut.getMove(unit) > 0){
				atts.add("Movement: " + Integer.toString(ut.getMove(unit)));
			}
			atts.add("Combat Spd: " + Integer.toString(ut.getSpeed(unit)));
			if (ut.getCombat(unit) > 0){
				atts.add("Combat Pts: " + Integer.toString(unit.getCombat()) + " / " + Integer.toString(ut.getCombat(unit)));
			}
			atts.add("Hit Pts: " + Integer.toString(unit.getHP()) + " / " + Integer.toString(unit.getMaxHP()));

			lstAttribute.setListData(atts.toArray());

			if (ut.isCombatUnit()){
				if (ut.getType() == UnitType.TYPE_KING){
					lblName.setText(ploader.getPlayer(owner).getName() + "'s " + ut.getName());
				}else{
					lblName.setText(ploader.getPlayer(owner).getName() + "'s " + UnitType.LEVEL_NAME[unit.getLevel()] + " " + ut.getName());
				}
			}else{
				lblName.setText(ploader.getPlayer(owner).getName() + "'s " + ut.getName());
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
			show();
		}
	}
}