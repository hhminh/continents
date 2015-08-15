import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

//Since the number of bonuses are small
//We have them hardwired in here
public class OverlayBonus{
	public static final int BONUS_ONCE = 0;
	public static final int BONUS_AGAIN = 1;

	private static final int MAX_BONUS = 5;

	protected static String name[];
	protected static String description[];
	protected static int upgrade[];
	protected static int type[];

	static{
		name = new String[MAX_BONUS];
		description = new String[MAX_BONUS];
		upgrade = new int[MAX_BONUS];
		type = new int[MAX_BONUS];

		type[0] = BONUS_ONCE;
		name[0] = "Bonus";
		description [0] = "Bonus";
		upgrade[0] = 0;

		type[1] = BONUS_ONCE;
		name[1] = "Bonus";
		description [1] = "Bonus";
		upgrade[1] = 25;

		type[2] = BONUS_ONCE;
		name[2] = "Bonus";
		description [2] = "Bonus";
		upgrade[2] = 26;

		type[3] = BONUS_ONCE;
		name[3] = "Bonus";
		description [3] = "Bonus";
		upgrade[3] = 22;

		type[4] = BONUS_ONCE;
		name[4] = "Bonus";
		description [4] = "Bonus";
		upgrade[4] = 10;
	}

	public static int getBonusType(int i){
		if (i>=0 && i<MAX_BONUS) {
			return type[i];
		}else{
			return BONUS_ONCE;
		}
	}

	public static int getBonusUpgrade(int i){
		if (i>=0 && i<MAX_BONUS) {
			return upgrade[i];
		}else{
			return -1;
		}
	}
}