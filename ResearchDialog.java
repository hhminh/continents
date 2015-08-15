//Experimental
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class ResearchDialog extends JDialog{
	public final static int SIZE = 64;

	private ResearchImagePanel panDesc;
	private ResearchPanel panResearch;
	private JLabel lblMsg, lblRate;
	private JScrollBar scrRate;
	private JButton btnExit, btnClear;

	public ResearchDialog(Frame owner){
		super(owner, "Research & Advancements", true);

		panDesc = new ResearchImagePanel(SIZE * GameWorld.TECH_SIZE, SIZE){
			public boolean isOpaque(){
				return true;
			}
		};
		panDesc.setBackground(GameWorld.COL_GREEN);
		panDesc.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				Point p = e.getPoint();
				int index = p.x / SIZE;

				panDesc.setValue(index);
				panDesc.repaint();
				if (index > -1 && index < GameWorld.TECH_SIZE) {
					lblMsg.setText(GameWorld.TECH_NAME[index]);
				}
			}
			public void mousePressed(MouseEvent e){
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		panResearch = new ResearchPanel();
		panResearch.setBackground(Color.black);
		lblMsg = new JLabel(" ");
		lblRate = new JLabel("         ");

		scrRate = new JScrollBar(JScrollBar.HORIZONTAL,0,0,0,GameWorld.RESOURCE_LIMIT[GameWorld.RESOURCE_CURRENCY]);
		scrRate.addAdjustmentListener(new AdjustmentListener(){
			public void adjustmentValueChanged(AdjustmentEvent e){
				lblRate.setText(Integer.toString(scrRate.getValue()) + " " +
					GameWorld.RESOURCE_NAME[GameWorld.RESOURCE_CURRENCY]);
			}
		});

		btnExit = new JButton("Apply funds");
		btnExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();
			}
		});

		btnClear = new JButton("Clear funds");
		btnClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				scrRate.setValue(0);
			}
		});


		JPanel panTemp2 = new JPanel(new BorderLayout());
		panTemp2.add(scrRate, BorderLayout.WEST);
		panTemp2.add(new DummyPanel(), BorderLayout.CENTER);
		panTemp2.add(lblRate, BorderLayout.EAST);

		JPanel panTemp3 = new JPanel(new GridLayout(1,3));
		panTemp3.add(btnClear);
		panTemp3.add(new DummyPanel());
		panTemp3.add(btnExit);

		JPanel panTemp = new JPanel(new BorderLayout());
		panTemp.add(new DummyPanel(), BorderLayout.NORTH);
		panTemp.add(lblMsg, BorderLayout.WEST);
		panTemp.add(panTemp2, BorderLayout.EAST);
		panTemp.add(panTemp3, BorderLayout.SOUTH);

		setResizable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panDesc, BorderLayout.CENTER);
		getContentPane().add(panResearch, BorderLayout.NORTH);
		getContentPane().add(panTemp, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public int getValue(){
		return panDesc.getValue();
	}

	public int getRate(){
		return scrRate.getValue();
	}

	public void showPlayer(Image icon, GameData gd, int o){
		panDesc.setImage(icon);
		panDesc.setValue(-1);
		panResearch.setValue(gd.getTech(o));
		//lblMsg.setText(GameWorld.TECH_NAME[panDesc.getValue()]);
		lblMsg.setText(" ");
		scrRate.setVisible(false);
		lblRate.setVisible(false);
		btnExit.setEnabled(false);
		btnClear.setEnabled(false);

		pack();

		show();
	}

	public void showChooser(Image icon, GameData gd, int o, int t, int v){
		panDesc.setImage(icon);
		panDesc.setValue(t);
		panResearch.setValue(gd.getTech(o));
		if (t > -1 && t < GameWorld.TECH_SIZE) {
			lblMsg.setText(GameWorld.TECH_NAME[t]);
		}
		scrRate.setValue(v);
		scrRate.setVisible(true);
		lblRate.setVisible(true);
		btnExit.setEnabled(true);
		btnClear.setEnabled(true);

		pack();

		show();
	}

	public void showChooser(Image icon, GameData gd, int o){
		panDesc.setImage(icon);
		panResearch.setValue(gd.getTech(o));
		lblMsg.setText("Click on the technology icons to select research:");
		scrRate.setValue(10);
		scrRate.setVisible(true);
		lblRate.setVisible(true);
		btnExit.setVisible(true);
		btnClear.setVisible(true);

		pack();

		show();
	}
}

class ResearchImagePanel extends ImagePanel{
	private int value;

	public ResearchImagePanel(int w, int h){
		super(w, h);
	}

	public void setValue(int v){
		value = v;
	}

	public int getValue(){
		return value;
	}
	
	public void paint(Graphics g){
		super.paint(g);

		if (value > -1) {
			int x = value * ResearchDialog.SIZE;
			g.setColor(Color.red);
			g.drawRect(x, 0, ResearchDialog.SIZE, ResearchDialog.SIZE);
		}
	}
}

class ResearchPanel extends JComponent{
	private final static int THICK = 1;
	private final static int PAD = 15;

	private int width, height;
	private int[] value;

	public ResearchPanel(){
		super();

		width = ResearchDialog.SIZE * GameWorld.TECH_SIZE;
		height = THICK * GameWorld.TECH_LIMIT + PAD;
		//init to 0
		value = new int[GameWorld.TECH_SIZE];
	}

	public void setValue(int[] array){
		if (array != null){
			value = array;
		}
	}

	public Dimension getPreferredSize(){
		return new Dimension(width + 1, height + 1);
	}

	public void paintComponent(Graphics g){
		g.setColor(getBackground());
		g.fillRect(0,0,width,height);

		for (int i=0; i<GameWorld.TECH_SIZE; i++){
			int x = i*ResearchDialog.SIZE;
			int h = value[i] * THICK + PAD;
			int y = height - h;

			if (i % 2 == 0){
				g.setColor(GameWorld.COL_GREEN_WHITE);
			}else{
				g.setColor(GameWorld.COL_GREEN);
			}
			g.fill3DRect(x, y, ResearchDialog.SIZE, h, true);
			if (i % 2 == 0){
				g.setColor(Color.black);
			}else{
				g.setColor(Color.blue);
			}
			g.drawString(Integer.toString(value[i]) + " Points", x + 2, height - 2);
		}
		//connecting lines
		//g.setColor(Color.black);
		//int half = ResearchDialog.SIZE / 2;
		//for (int i=0; i<GameWorld.TECH_SIZE-1; i++){
		//	int x1 = i*ResearchDialog.SIZE + half;
		//	int h1 = value[i] * THICK + PAD;
		//	int y1 = height - h1;
		//	int x2 = i*ResearchDialog.SIZE + ResearchDialog.SIZE + half;
		//	int h2 = value[i+1] * THICK + PAD;
		//	int y2 = height - h2;
		//	g.drawLine(x1, y1, x2, y2);
		//}
	}
}