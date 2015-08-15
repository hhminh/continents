//Experimental
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

public class Job implements Serializable{
	public static final int ORDER_FOLLOW = 0;
	public static final int ORDER_ATTACK = 1;
	public static final int ORDER_CONQUER = 2;
	public static final int ORDER_DEFEND = 3;
	public static final int ORDER_GUARD = 4;
	public static final int ORDER_PICKUP = 5;

	public static final int ORDER_SPY_TECH = 6;
	public static final int ORDER_SPY_INCITE = 7;
	public static final int ORDER_SPY_BRIBE = 8;

	public static final int ORDER_DIPLOMACY_PACKAGE = 9;

	public static final int ORDER_CUSTOM = 10;

	public static final int DIFF_EASY = 0;
	public static final int DIFF_MEDIUM = 1;
	public static final int DIFF_HARD = 2;
	public static final int DIFF_IMPOSSIBLE = 3;
	public static final int DIFF_MAX = 10;

	public static final int REWARD_RESOURCE = 0;
	public static final int REWARD_UNIT = 1;
	public static final int REWARD_TECH = 2;

	private Point destination;
	private int order;
	private int targetBase;
	private int targetArmy;
	private int difficulty;
	private String description;
	private int icon;
	private int reward;
	private int rewardValue;
	private int[] rewardResource;

	public Job(int o){
		destination = null;
		order = o;
		targetBase = -1;
		targetArmy = -1;
		difficulty = DIFF_EASY;
		description = "Unknown";
		icon = -1;
		reward = REWARD_RESOURCE;
		rewardValue = -1;
		rewardResource = new int[GameWorld.RESOURCE_SIZE];
	}

	public void setDestination(Point p){
		destination = p;
	}

	public Point getDestination(){
		return destination;
	}

	public int getOrder(){
		return order;
	}

	public void setTargetBase(int t){
		targetBase = t;
	}

	public int getTargetBase(){
		return targetBase;
	}

	public void setTargetArmy(int t){
		targetArmy = t;
	}

	public int getTargetArmy(){
		return targetArmy;
	}

	public void setDescription(String d){
		description = d;
	}

	public String getDescription(){
		return description;
	}

	public void setIcon(int i){
		icon = i;
	}

	public int getIcon(){
		return icon;
	}

	public int getDifficulty(){
		return difficulty;
	}

	public void setDifficulty(int d){
		difficulty = d;
	}

	public void setReward(int r){
		reward = r;
	}

	public int getReward(){
		return reward;
	}

	public void setRewardValue(int v){
		rewardValue = v;
	}

	public int getRewardValue(){
		return rewardValue;
	}

	public void setRewardResource(int[] all){
		if (all == null || all.length <= 0 || all.length> rewardResource.length) {
			return;
		}else{
			rewardResource = all;
		}
	}

	public int[] getRewardResource(){
		return rewardResource;
	}

	public void setRewardResource(int i, int v){
		if (i <0 || i>= rewardResource.length) {
			return;
		}else{
			rewardResource[i] = v;
		}
	}

	public int getRewardResource(int i){
		if (i <0 || i>= rewardResource.length) {
			return 0;
		}else{
			return rewardResource[i];
		}
	}
}