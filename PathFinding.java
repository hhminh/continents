import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class PathFinding{
	//protected final static int STEP = 45;
	//protected final static double SHIFT = Math.PI / STEP;

	protected int x1, y1, x2, y2, cx, cy, current;
	protected ArrayList path;
	protected GameData gd;
	protected Army army;
	protected UnitTypeLoader uloader;

	public PathFinding(GameData gdat, UnitTypeLoader ul, Army a, int cx1, int cy1,
			int cx2, int cy2){
		gd = gdat;
		uloader = ul;
		army = a;

		x1 = cx1;
		x2 = cx2;
		y1 = cy1;
		y2 = cy2;
		current = 0;

		int dx = x2 - x1;
		int dy = y2 - y1;

		LineIterator li1 = new LineIterator(x1, y1, x2, y2);
		path = new ArrayList();

		if (!checkLine(li1, true)){
			if (dx == 0){
				for (int i=-1; i>-10; i--){
					if (i == 0){
						continue;
					}
					int y3 = y2 + i;
					LineIterator li2 = new LineIterator(x1, y1, x2, y3);
					if (checkPath(li2, x2, y2)){
						return;
					}
				}
				for (int i=1; i<10; i++){
					if (i == 0){
						continue;
					}
					int y3 = y2 + i;
					LineIterator li2 = new LineIterator(x1, y1, x2, y3);
					if (checkPath(li2, x2, y2)){
						return;
					}
				}
				System.out.println("A");
			}else if (dy == 0){
				for (int i=-1; i>-10; i--){
					if (i == 0){
						continue;
					}
					int x3 = x2 + i;
					LineIterator li2 = new LineIterator(x1, y1, x3, y2);
					if (checkPath(li2, x2, y2)){
						return;
					}
				}
				for (int i=1; i<10; i++){
					if (i == 0){
						continue;
					}
					int x3 = x2 + i;
					LineIterator li2 = new LineIterator(x1, y1, x3, y2);
					if (checkPath(li2, x2, y2)){
						return;
					}
				}
				System.out.println("B");
			}else{
				if (Math.abs(dx) > Math.abs(dy)){
					for (int i=-1; i>-10; i--){
						if (i == 0){
							continue;
						}
						int y3 = y2 + i;
						LineIterator li2 = new LineIterator(x1, y1, x2, y3);
						if (checkPath(li2, x2, y2)){
							return;
						}
					}
					for (int i=1; i<10; i++){
						if (i == 0){
							continue;
						}
						int y3 = y2 + i;
						LineIterator li2 = new LineIterator(x1, y1, x2, y3);
						if (checkPath(li2, x2, y2)){
							return;
						}
					}
					System.out.println("C");
				}else{
					for (int i=-1; i>-10; i--){
						if (i == 0){
							continue;
						}
						int x3 = x2 + i;
						LineIterator li2 = new LineIterator(x1, y1, x3, y2);
						if (checkPath(li2, x2, y2)){
							return;
						}
					}
					for (int i=1; i<10; i++){
						if (i == 0){
							continue;
						}
						int x3 = x2 + i;
						LineIterator li2 = new LineIterator(x1, y1, x3, y2);
						if (checkPath(li2, x2, y2)){
							return;
						}
					}
					System.out.println("D");
				}
			}
		}
	}

	public Point nextPoint(){
		if (path == null || path.size() == 0){
			return null;
		}
		return (Point)path.get(current++);
	}

	public void reset(){
		current = 0;
	}

	protected boolean checkLine(LineIterator li, boolean add){
		li.reset();
		Point p = li.nextPoint();
		while (!li.end()){
			if (gd.getArmy(p.x, p.y) > -1 || gd.getBase(p.x, p.y) > -1 ||
				!army.canMove(gd.getBaseLand(p.x, p.y), uloader, false)){
				return false;
			}
			if (add && path != null){
				path.add(p);
			}
			p = li.nextPoint();
		}
		return true;
	}

	protected boolean checkPath(LineIterator li, int x, int y){
		ArrayList ipath = new ArrayList();

		li.reset();
		Point p = li.nextPoint();
		while (!li.end()){
			path = new ArrayList();

			if (gd.getArmy(p.x, p.y) > -1 || gd.getBase(p.x, p.y) > -1 ||
				!army.canMove(gd.getBaseLand(p.x, p.y), uloader, false)){
				return false;
			}else{
				ipath.add(p);
				LineIterator li2 = new LineIterator(p.x, p.y, x, y);
				if (checkLine(li2, true)){
					path.addAll(0, ipath);
					return true;
				}
			}
			p = li.nextPoint();
		}
		return false;
	}
}

class LineIterator{
	protected int x1, y1, x2, y2, cx, cy;

	public LineIterator(int cx1, int cy1, int cx2, int cy2){
		x1 = cx1;
		x2 = cx2;
		y1 = cy1;
		y2 = cy2;
		cx = x1;
		cy = y1;
	}

	public void reset(){
		cx = x1;
		cy = y1;
	}

	public Point nextPoint(){
		getNextPoint();
		return new Point(cx, cy);
	}

	public boolean end(){
		if (cx == x2 && cy == y2){
			return true;
		}
		return false;
	}

	protected void getNextPoint(){
		int dx, dy;
		double dc;

		dx = x2 - cx;
		dy = y2 - cy;

		if (dx ==0 && dy ==0){
			return;
		}else{
			if (dx == 0){
				dy /= Math.abs(dy);
				dc = 0.0;
			}else if (dy == 0){
				dx /= Math.abs(dx);
				dc = 0.0;
			}else{
				dc = dx / dy;
				if (dc > 1.4 || dc < -1.4){
					dy = 0;
				}else if (dc > 0.7 || dc < -0.7){
					if (dx > 0){
						dx = 1;
					}else{
						dx = -1;
					}
					if (dy > 0){
						dy = 1;
					}else{
						dy = -1;
					}
				}else{
					dx = 0;
					if (dy > 0){
						dy = 1;
					}else{
						dy = -1;
					}
				}
			}
		}
		cx += dx;
		cy += dy;
	}
}