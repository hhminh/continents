import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.net.*;
import java.awt.geom.*;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

public class ScrollingTextPanel extends JPanel{
	//private JTextPane txtPanel;
	private JEditorPane txtPanel;
	private JScrollPane scrPanel;
	private JScrollBar scbVertical;

	private int width, height, delay, offset, direction, lastDir;
	private javax.swing.Timer tmrScroll;

	public ScrollingTextPanel(int w, int h, int d){
		width = w;
		height = h;
		delay = d;
		/*
		txtPanel = new JTextPane(){
			public void paintComponent(Graphics g){
				Rectangle drawHere = g.getClipBounds();
				int lineHeight = g.getFontMetrics().getHeight();
				int startOffset = getInsets().top;
				int start = (drawHere.y / lineHeight) * lineHeight + startOffset;

				int startLineNumber = (drawHere.y / lineHeight);
				int endLineNumber = startLineNumber + (drawHere.height / lineHeight) + 1;

				for (int i = startLineNumber; i <= endLineNumber; i++){
					g.setColor((i % 2 == 0) ? Color.white : Color.lightGray);
					g.fillRect(drawHere.x, start, drawHere.width, lineHeight);
					start += lineHeight;
				}

				super.paintComponent(g);
			}
		};
		txtPanel.setOpaque(false);
		*/
		txtPanel = new JEditorPane("text/html","");
		txtPanel.setEditable(false);
		//txtPanel.setToolTipText("Click and hold to freeze, release to change scrolling direction");
		//txtPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		txtPanel.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e){
			}
			public void mouseReleased(MouseEvent e){
				direction = -lastDir;
				txtPanel.setSelectionStart(0);
				txtPanel.setSelectionEnd(0);
			}
			public void mousePressed(MouseEvent e){
				lastDir = direction;
				direction = 0;
				txtPanel.setSelectionStart(0);
				txtPanel.setSelectionEnd(0);
			}
			public void mouseEntered(MouseEvent e){
			}
			public void mouseExited(MouseEvent e){
			}
		});

		scrPanel = new JScrollPane(txtPanel);
		scrPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//scrPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		//MIN27
		scrPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrPanel.setSize(width, height);
		//scrPanel.setEnabled(false);

		scbVertical = scrPanel.getVerticalScrollBar();

		setDoubleBuffered(true);
		setLayout(new BorderLayout());
		add(scrPanel, BorderLayout.CENTER);

		ActionListener actScroll = new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if (direction == 1){
					JViewport view = scrPanel.getViewport();
					if (offset < view.getViewSize().height - scrPanel.getSize().height){
						scbVertical.setValue(offset);
						offset += direction;
					}
				}else{
					if (offset > 0){
						scbVertical.setValue(offset);
						offset += direction;
					}
				}
			}
		};
		tmrScroll = new javax.swing.Timer(delay, actScroll);
	}

	public boolean isOptimizedDrawingEnabled(){
		return true;
	}

	public void start(){
		offset = 0;
		direction = 1;
		scrPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		tmrScroll.start();
	}

	public void stop(){
		tmrScroll.stop();
		scrPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}

	public Dimension getPreferredSize(){
		return new Dimension(width, height);
	}

	public void setText(String txt){
		txtPanel.setBackground(getBackground());
		txtPanel.setText(txt);
	}
}