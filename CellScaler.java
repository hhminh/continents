
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.*;

public class CellScaler extends Component{
	private Image original;
	private BufferedImage scaled;
	private int swidth, sheight, dwidth, dheight, saveMask;

	public CellScaler(String file, int w1, int h1, int w2, int h2){
		swidth = w1;
		sheight = h1;
		dwidth = w2;
		dheight = h2;
		saveMask = 0;

		original = loadImage(file);
	}

	public CellScaler(Image img, int w1, int h1, int w2, int h2){
		swidth = w1;
		sheight = h1;
		dwidth = w2;
		dheight = h2;

		original = img;
	}

	public CellScaler(){
	}

	public void setFile(String file){
		original = loadImage(file);
	}

	public void setOldSize(int w1, int h1){
		swidth = w1;
		sheight = h1;
	}

	public void setNewSize(int w2, int h2){
		dwidth = w2;
		dheight = h2;
	}

	public void setSaveMask(int val){
		saveMask = val;
	}

	public Image loadImage(String file){
		try{
			Image img = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/"+file));
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img, 0);
			try{
				mt.waitForID(0);
			}catch(InterruptedException ie){
				System.err.println(ie);
			}
			return img;
		}catch(Exception e){
			System.out.println(e);
			return null;
		}
	}

	public void doScaling(){
		if (original != null){
			if (saveMask == 0){
				scaled = new BufferedImage(original.getWidth(this),
					original.getHeight(this),BufferedImage.TYPE_INT_ARGB);
			}else{
				scaled = new BufferedImage(original.getWidth(this),
					original.getHeight(this),BufferedImage.TYPE_INT_RGB);
			}
			Graphics2D g = (Graphics2D)scaled.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);		
			//g.setRenderingHint(RenderingHints.KEY_RENDERING,
			//	RenderingHints.VALUE_RENDER_SPEED);


			int maxw = original.getWidth(this) / swidth;
			int maxh = original.getHeight(this) / sheight;
			int xoff = (swidth - dwidth) / 2;
			int yoff = (sheight - dheight) / 2;

			if (saveMask == 1){
				g.setColor(new Color(255,0,255));
				g.fillRect(0, 0, original.getWidth(this), original.getHeight(this));
			}

			for (int i=0; i<maxw; i++){
				for (int j=0; j<maxh; j++){
					int x = i * swidth;
					int y = j * sheight;
					int x1 = x + xoff;
					int y1 = y + yoff;

					g.drawImage(original, x1, y1, x1 + dwidth, y1 + dheight, x, y, x + swidth, y + sheight, this);
				}
			}
		}
	}

	public void doChopping(){
		if (original != null){
			int maxw = original.getWidth(this) / swidth;
			int maxh = original.getHeight(this) / sheight;
			int xoff = (swidth - dwidth) / 2;
			int yoff = (sheight - dheight) / 2;

			if (saveMask == 0){
				scaled = new BufferedImage(maxw * dwidth,
					maxh * dheight, BufferedImage.TYPE_INT_ARGB);
			}else{
				scaled = new BufferedImage(maxw * dwidth,
					maxh * dheight, BufferedImage.TYPE_INT_RGB);
			}
			Graphics2D g = (Graphics2D)scaled.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);		
			//g.setRenderingHint(RenderingHints.KEY_RENDERING,
			//	RenderingHints.VALUE_RENDER_SPEED);

			if (saveMask == 1){
				g.setColor(new Color(255,0,255));
				g.fillRect(0, 0, original.getWidth(this), original.getHeight(this));
			}

			for (int i=0; i<maxw; i++){
				for (int j=0; j<maxh; j++){
					int x = i * swidth + xoff;
					int y = j * sheight + yoff;
					int x1 = i * dwidth;
					int y1 = j * dheight;

					g.drawImage(original, x1, y1, x1 + dwidth, y1 + dheight, x, y, x + dwidth, y + dheight, this);
				}
			}
		}
	}

	public Image getScaledImage(){
		return scaled.getSubimage(0, 0, scaled.getWidth(this), scaled.getHeight(this));
	}

	public void saveImage(String fileName){
		if (scaled != null){
			try{
				FileOutputStream fileStream = new FileOutputStream("images/"+fileName);
				JPEGEncodeParam encodeParam = JPEGCodec.getDefaultJPEGEncodeParam(scaled);
				encodeParam.setQuality(1.0f, false);
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fileStream);
				encoder.encode(scaled,encodeParam);
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}

	public static void main(String[] args){
		if (args.length > 6){
			CellScaler cs = new CellScaler(
				args[0],
				Integer.parseInt(args[2]),
				Integer.parseInt(args[3]),
				Integer.parseInt(args[4]),
				Integer.parseInt(args[5])
			);
			cs.setSaveMask(1);
			if (args[6].compareToIgnoreCase("chop") == 0){
				cs.doChopping();
			}else{
				cs.doScaling();
			}
			cs.saveImage(args[1]);
			System.out.println("New image saved to " + args[1]);
		}
	}
}