import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class ResourceTransferDialog extends JDialog{
	private ResourceTransferContainer panRes;

	public ResourceTransferDialog(Frame owner, GameWorld w, MapPanel mp){
		super(owner, "Resource trading ...", true);

		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		panRes = new ResourceTransferContainer(w, mp);

		getContentPane().add(panRes);
		pack();
		setLocationRelativeTo(owner);
	}

	public void show(ResourceHolder c1, ResourceHolder c2, boolean full){
		panRes.show(c1, c2, full);
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

class ResourceTransferContainer extends JComponent{
	private ResHolderPanel panLeft, panRight, panSource;
	//private JLabel lblStatus;
	private boolean mode;

	private ResourceHolder us1, us2;
	private GameWorld world;

	public ResourceTransferContainer(GameWorld w, MapPanel mp){
		world = w;
		us1 = null;
		us2 = null;
		mode = true;

		panLeft = new ResHolderPanel(mp, 32, 36, 0, GameWorld.RESOURCE_TRANSFERABLE);
		panRight = new ResHolderPanel(mp, 32, 36, 0, GameWorld.RESOURCE_TRANSFERABLE);
		//lblStatus = new JLabel("<html><center><br>Left click on resource to load, right click to unload<br>Hold Shift to move in small quantities</center></html>");

		panLeft.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				//non usable
				if (us1 == null || us2 == null){
					return;
				}

				//index
				Point p = e.getPoint();
				int index = panLeft.getIndex(p.x, p.y);

				//error
				if (index < 0 || index > GameWorld.RESOURCE_SIZE){
					return;
				}

				//amount
				int amount = 10;
				if (e.isShiftDown()){
					amount = 1;
				}

				boolean res = false;
				//load
				//MIN33 safe loading
				if (e.getButton() == MouseEvent.BUTTON1){
					if (amount > us2.getResource(index)){
						amount = us2.getResource(index);
					}
					if (amount <= 0){
						return;
					}
					if (!mode){
						if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
							"Do you want to buy " + Integer.toString(amount)+ " " +
							GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
							res = world.buyResource(us2, us1, index, amount);
							panRight.repaint();
						}else{
							return;
						}
					}else{
						if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
							"Do you want to take " + Integer.toString(amount)+ " " +
							GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
							res = world.transferResource(us2, us1, index, amount);
							panLeft.repaint();
						}else{
							return;
						}
					}
				}else{
				//unload
				//MIN33 safe unloading
					if (amount > us1.getResource(index)){
						amount = us1.getResource(index);
					}
					if (amount <= 0){
						return;
					}
					if (!mode){
						if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
							"Do you want to sell " + Integer.toString(amount)+ " " +
							GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
							res = world.sellResource(us1, us2, index, amount);
							panRight.repaint();
						}else{
							return;
						}
					}else{
						if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
							"Do you want to give " + Integer.toString(amount)+ " " +
							GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
							res = world.transferResource(us1, us2, index, amount);
							panLeft.repaint();
						}else{
							return;
						}
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

		panRight.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				//non usable
				if (us1 == null || us2 == null){
					return;
				}

				//index
				Point p = e.getPoint();
				int index = panRight.getIndex(p.x, p.y);

				//error
				if (index < 0 || index > GameWorld.RESOURCE_SIZE){
					return;
				}

				//amount
				int amount = 10;
				if (e.isShiftDown()){
					amount = 1;
				}

				boolean res = false;
				//load
				//MIN33 safe loading
				if (e.getButton() == MouseEvent.BUTTON1){
					if (amount > us1.getResource(index)){
						amount = us1.getResource(index);
					}
					if (amount <= 0){
						return;
					}
					if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
						"Do you want to take " + Integer.toString(amount)+ " " +
						GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
						res = world.transferResource(us1, us2, index, amount);
						panRight.repaint();
					}else{
						return;
					}
				}else{
				//unload
				//MIN33 safe unloading
					if (amount > us2.getResource(index)){
						amount = us2.getResource(index);
					}
					if (amount <= 0){
						return;
					}
					if (world.showYesNoDialog(GameWorld.IMG_TRADER, 
						"Do you want to give " + Integer.toString(amount)+ " " +
						GameWorld.RESOURCE_NAME[index]+	"?") == YesNoDialog.STATE_YES){
						res = world.transferResource(us2, us1, index, amount);
						panRight.repaint();
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

		setBackground(GameWorld.COL_GREEN_WHITE);
		setLayout(new BorderLayout());
		add(panLeft, BorderLayout.WEST);
		add(new DummyPanel(), BorderLayout.CENTER);
		add(panRight, BorderLayout.EAST);
		//add(lblStatus, BorderLayout.SOUTH);
	}

	public void show(ResourceHolder c1, ResourceHolder c2, boolean full){
		us1 = c1;
		us2 = c2;
		mode = full;
		panSource = null;

		panLeft.setResourceHolder(us1);
		panRight.setResourceHolder(us2);
		panRight.setEnabled(mode);
	}
}