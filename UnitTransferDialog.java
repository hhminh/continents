import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class UnitTransferDialog extends JDialog{
	private HolderPanel panLeft, panRight, panSource;
	private JLabel lblStatus;
	private JButton btnRes, btnAllRight, btnAllLeft, btnClose;

	private UnitTypeLoader uloader;
	private HouseTypeLoader hloader;
	private TerrainLoader tloader;
	private OverlayLoader oloader;
	private TechLoader teloader;
	private GameWorld world;

	private UnitSwappable us1, us2;
	private int index;

	public UnitTransferDialog(Frame owner, UnitTypeLoader ul,
			HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, TechLoader tel, int num, GameWorld w){
		super(owner, "Moving units ...", true);

		uloader = ul;
		hloader = hl;
		tloader = tl;
		oloader = ol;
		teloader = tel;
		world = w;

		us1 = null;
		us2 = null;

		panLeft = new HolderPanel(this, 48, 48, uloader);
		panRight = new HolderPanel(this, 48, 48, uloader);

		lblStatus = new JLabel("Click on a unit to transfer");

		btnRes = new JButton("Resources");
		btnRes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (us1 instanceof ResourceHolder && us2 instanceof ResourceHolder){
					world.showResTransfer((ResourceHolder)us1, (ResourceHolder)us2, true);
				}
			}
		});

		btnClose = new JButton("Done");
		btnClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				performClose();
			}
		});

		btnAllLeft = new JButton("All! left");
		btnAllLeft.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (us1 != null && us2 != null) {
					if (us1 instanceof Army && us2 instanceof Army) {
						Army army = (Army)us2;
						if (army.getMove()<=0) {
							JOptionPane.showMessageDialog(null, 
								"This fleet has exhausted its movements.\n"+
								"Please wait for tomorrow.",
								GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}
					while (us2.getCount() > 0) {
						if (validMove(us1, us2)){
							if (us1.transfer(us2, 0, uloader, hloader, tloader, oloader, teloader)) {
								panLeft.repaint();
								panRight.repaint();
							}else{
								break;
							}
						}
					}				
				}
			}
		});

		btnAllRight = new JButton("All! right");
		btnAllRight.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (us1 != null && us2 != null) {
					if (us1 instanceof Army && us2 instanceof Army) {
						Army army = (Army)us1;
						if (army.getMove()<=0) {
							JOptionPane.showMessageDialog(null, 
								"This fleet has exhausted its movements.\n"+
								"Please wait for tomorrow.",
								GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
							return;
						}
					}
					while (us1.getCount() > 0) {
						if (validMove(us2, us1)){
							if (us2.transfer(us1, 0, uloader, hloader, tloader, oloader, teloader)) {
								panLeft.repaint();
								panRight.repaint();
							}else{
								break;
							}
						}
					}				
				}
			}
		});

		JPanel panTemp2 = new JPanel(new GridLayout(1,4));
		panTemp2.add(btnAllLeft);
		panTemp2.add(btnRes);
		panTemp2.add(btnAllRight);
		panTemp2.add(btnClose);

		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(lblStatus, BorderLayout.NORTH);
		panTemp.add(panTemp2, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowListener(){
			public void windowOpened(WindowEvent e){
			}
			public void windowClosing(WindowEvent e){
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

		getContentPane().setBackground(GameWorld.COL_GREEN_WHITE);
		//getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panLeft, BorderLayout.WEST);
		//getContentPane().add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.CENTER);
		//getContentPane().add(new JLabel("<html><center>Transfer<br>to</center></html>"), BorderLayout.CENTER);
		getContentPane().add(new DummyPanel(), BorderLayout.CENTER);
		getContentPane().add(panRight, BorderLayout.EAST);
		getContentPane().add(panTemp, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public void show(UnitSwappable c1, UnitSwappable c2, int owner){
		us1 = c1;
		us2 = c2;
		panSource = null;
		index = -1;

		panLeft.setCursor(Cursor.getDefaultCursor());
		panRight.setCursor(Cursor.getDefaultCursor());

		panLeft.setHolder(us1, owner);
		panRight.setHolder(us2, owner);

		if (us1 instanceof ResourceHolder && us2 instanceof ResourceHolder){
			btnRes.setVisible(true);
		}else{
			btnRes.setVisible(false);
		}

		show();

		//panLeft.start();
		//panRight.start();
	}

	protected boolean validMove(UnitSwappable us1, UnitSwappable us2){
		//MIN31 avoid moving man on sea or boat onto land without man carrying it
		if (us1 instanceof Army && us2 instanceof Army){
			Unit who = us2.get(index);
			//System.err.println(index);
			if (who != null){
				UnitType whoType = uloader.getUnitType(who.getType());
				if (whoType != null){
					Army army1 = (Army)us1;
					Army army2 = (Army)us2;
					//System.err.println(army2.getMove() + " : " + army1.getMove());
					//this guy can not move
					if (army2.getMove()<=0) {
						return false;
					}
					//continue
					int landType1 = world.getGD().getBaseLand(army1.getX(),
						army1.getY());
					int landType2 = world.getGD().getBaseLand(army2.getX(),
						army2.getY());
					//check moveability
					if (!whoType.canMove(landType1)){
						//no one else to support me
						if (army1.getCount() <= 0){
							return false;
						}
						//if i move, no one will supports the other
						int failed = 0;
						for (int j=0; j<army2.getCount(); j++){
							if (j == index){
								continue;
							}
							Unit other = army2.get(j);
							if (other == null){
								continue;
							}
							UnitType otherType = uloader.getUnitType(other.getType());
							if (otherType != null && !otherType.canMove(landType2) &&
								(++failed) >= army2.getCount()-1){
								return false;
							}
						}
					}
				}
			}
		}
		return true;
		//MIN31 avoid moving man on sea or boat onto land without man carrying it
	}

	public void setTransfer(HolderPanel source, int ind){
		if (panSource == null){
			lblStatus.setText("Click on the other panel to transfer.");

			index = ind;
			panSource = source;

			panLeft.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			panRight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}else if (panSource != source){
			lblStatus.setText("Click on a unit to transfer");
			panLeft.setCursor(Cursor.getDefaultCursor());
			panRight.setCursor(Cursor.getDefaultCursor());
			panSource = null;
		//try{
			if (source == panLeft){
				if (validMove(us1, us2)){
					us1.transfer(us2, index, uloader, hloader, tloader, oloader, teloader);
					panLeft.repaint();
					panRight.repaint();
				}else{
					JOptionPane.showMessageDialog(null, 
						"Movement is not allowed as either unit can not move\n"+
						" or left-over units might be drown or become stuck.",
						GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
				}
			}else{
				if (validMove(us2, us1)){
					us2.transfer(us1, index, uloader, hloader, tloader, oloader, teloader);
					panLeft.repaint();
					panRight.repaint();
				}else{
					JOptionPane.showMessageDialog(null, 
						"Movement is not allowed as either unit can not move\n"+
						" or left-over units might be drown or become stuck.",
						GameWorld.GAME_NAME, JOptionPane.INFORMATION_MESSAGE); 
				}
			}
		//}catch(Exception e){
		//	e.printStackTrace();
		//}
			//panLeft.repaint();
			//panRight.repaint();
			//panLeft.repaint();
			//panRight.repaint();
		}else{
			lblStatus.setText("Click on a unit to transfer");

			panLeft.setCursor(Cursor.getDefaultCursor());
			panRight.setCursor(Cursor.getDefaultCursor());
			panSource = null;
		}
	}
	protected void performClose(){
		if (us1 instanceof Army){
			Army army1 = (Army)us1;
			int landType1 = world.getGD().getBaseLand(army1.getX(), army1.getY());
			army1.canMove(landType1, uloader, true);
		}
		if (us2 instanceof Army) {
			Army army2 = (Army)us2;
			int landType2 = world.getGD().getBaseLand(army2.getX(), army2.getY());
			army2.canMove(landType2, uloader, true);
		}
		hide();
	}

	protected void dialogInit() {
		super.dialogInit();
		JLayeredPane layeredPane = getLayeredPane();
		//Closing action
		Action closeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				performClose();
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

class HolderPanel extends JPanel{ // implements Runnable{
	public static final int COLS = 3;
	public static final int ROWS = 3;

	private int mywidth, myheight;
	private Image buffer;
	private Graphics gbuffer;
	//private boolean running;
	private UnitTypeLoader uloader;
	private UnitSwappable holder;

	private int owner;
	//private Thread thread;
	private UnitTransferDialog dlg;

	public HolderPanel(UnitTransferDialog d, int w, int h, UnitTypeLoader ul){
		super();

		dlg = d;
		uloader = ul;
		mywidth = w;
		myheight = h;

		addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				if (holder != null){
					Point p = e.getPoint();
					int x = (int)(p.x / mywidth);
					int y = (int)(p.y / myheight);
					int index = y * ROWS + x;
					//System.out.println(index);
					if (index < holder.getCount()){
						dlg.setTransfer(HolderPanel.this, index);
					}else{
						dlg.setTransfer(HolderPanel.this, -1);
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

	public void setHolder(UnitSwappable h, int o){
		holder = h;
		owner = o;
	}

	public Dimension getPreferredSize(){
		return new Dimension(mywidth * COLS + 1, myheight * ROWS + 1);
	}

	//public void start(){
	//	running = true;
	//	thread = new Thread(this);
	//	thread.start();
	//}

	//public void run(){
	//	while (running){
	//		repaint();
	//		try{
	//			thread.sleep(500);
	//		}catch(Exception e){
	//			System.err.println(e);
	//		}
	//	}
	//}

	//public void stop(){
	//	running = false;
	//}

	//MIN04 - to-do
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
		gbuffer.setColor(getBackground());
		gbuffer.fillRect(0, 0, wmax, hmax);
		gbuffer.setColor(GameWorld.COL_GREEN);
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
		//MH112 fill spaces with background
		gbuffer.setColor(GameWorld.COL_GREEN);
		gbuffer.fillRect(0, 0, getWidth(), getHeight());
		//draw unit
		for (int i=0; i<holder.getCount(); i++){
			Unit u = holder.get(i);
			if (u != null){
				UnitType ut = uloader.getUnitType(u.getType());
				if (ut != null){
					Tile t = ut.getTile();
					Image img = t.getImage(owner);
					int y = (int)(i / COLS) * mywidth;
					int x = (i % COLS) * myheight;
					int yb = y + myheight;
					int xr = x + mywidth;
					
					//MIN44 background
					gbuffer.setColor(GameWorld.COL_GREEN);
					gbuffer.fill3DRect(x, y, mywidth, myheight, true);

					gbuffer.drawImage(img, x, y, xr, yb, 0, 0, mywidth, myheight, this);

					int trait = u.getTrait();
					gbuffer.setColor(GameWorld.TRAIT_COLOR[trait]);
					//gbuffer.drawRect(x, y, mywidth, myheight);
					gbuffer.fillArc(x + 2, y + 8, 8, 8, 45, 90);
				}
			}
		}
		//MIN44 background
		for (int i=holder.getCount(); i < COLS * ROWS; i++){
			int y = (int)(i / COLS) * mywidth;
			int x = (i % COLS) * myheight;

			if (holder instanceof Army && i < ((Army)holder).getLimit()){
				gbuffer.setColor(GameWorld.COL_GREEN);
				gbuffer.fill3DRect(x, y, mywidth, myheight, false);
			}else if (i < GameWorld.SMALL_CELL_LIMIT){
				gbuffer.setColor(GameWorld.COL_GREEN);
				gbuffer.fill3DRect(x, y, mywidth, myheight, false);
			}
			//No need for this block now since we have MH112
			//else{
			//	gbuffer.setColor(GameWorld.COL_GREEN);
			//	gbuffer.fillRect(x, y, mywidth, myheight);
			//}
		}

		g.drawImage(buffer, 0, 0, this);
	}
}