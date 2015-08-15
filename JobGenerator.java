import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

//Since the number of jobs are small
public class JobGenerator{
	public static final int MAX_JOBS = 10;

	private static String[] jdes;
	private static int[] jrew;
	private static int[] jrev;
	private static int[] jico;
	private static int[] jdff;

	static{
		jdes = new String[MAX_JOBS];
		jrew = new int[MAX_JOBS];
		jrev = new int[MAX_JOBS];
		jico = new int[MAX_JOBS];
		jdff = new int[MAX_JOBS];

		jdes[0] = "<html><b>Explore a dungeon.</b><br>Many treasures lie deep down the last levels.<br>This job requires alot of strength and courage.<br>Only suitable for experienced adventurers.</html>";
		jrew[0] = Job.REWARD_RESOURCE;
		jrev[0] = -1;
		jico[0] = GameWorld.IMG_JOB001;
		jdff[0] = Job.DIFF_HARD;

		jdes[1] = "<html><b>Find missing children.</b><br>Some children from a nearby small village have been missing.<br>Talk to the locals to find some clues.<br>It's an easy job for starters.</html>";
		jrew[1] = Job.REWARD_RESOURCE;
		jrev[1] = -1;
		jico[1] = GameWorld.IMG_JOB002;
		jdff[1] = Job.DIFF_EASY;

		jdes[2] = "<html><b>Hunt down a pack of killer wolves.</b><br>The wolves are deadly at night but during the day,<br>they should be easy targets.<br>The villages have some nice rewards for the job.</html>";
		jrew[2] = Job.REWARD_RESOURCE;
		jrev[2] = -1;
		jico[2] = GameWorld.IMG_JOB001;
		jdff[2] = Job.DIFF_EASY;

		jdes[3] = "<html><b>Defeat a group of bandits.</b><br>Becarefull, some mentioned a magician is with the bandit.<br>The bandits may have something nice hidden as well.</html>";
		jrew[3] = Job.REWARD_RESOURCE;
		jrev[3] = -1;
		jico[3] = GameWorld.IMG_JOB003;
		jdff[3] = Job.DIFF_MEDIUM;

		jdes[4] = "<html><b>Explore the hidden forest.</b><br>Nothing is known about this area.</html>";
		jrew[4] = Job.REWARD_RESOURCE;
		jrev[4] = -1;
		jico[4] = GameWorld.IMG_JOB001;
		jdff[4] = Job.DIFF_IMPOSSIBLE;

		jdes[5] = "<html><b>Deliver goods to foreign merchants.</b><br>There might be bandits on the way, so please be carefull.</html>";
		jrew[5] = Job.REWARD_RESOURCE;
		jrev[5] = -1;
		jico[5] = GameWorld.IMG_JOB003;
		jdff[5] = Job.DIFF_EASY;

		jdes[6] = "<html><b>Most wanted.</b><br>Two criminals escaped from jail a few days ago.<br>Expect they have company, whom we suspected of helping them.</html>";
		jrew[6] = Job.REWARD_RESOURCE;
		jrev[6] = -1;
		jico[6] = GameWorld.IMG_JOB002;
		jdff[6] = Job.DIFF_EASY;

		jdes[7] = "<html><b>Explore the haunted house near the river.</b><br>Some suspected a monster is living inside the house.</html>";
		jrew[7] = Job.REWARD_RESOURCE;
		jrev[7] = -1;
		jico[7] = GameWorld.IMG_JOB002;
		jdff[7] = Job.DIFF_MEDIUM;

		jdes[8] = "<html><b>Explore an Icanus ruin.</b><br>Nothing is known about this area.</html>";
		jrew[8] = Job.REWARD_RESOURCE;
		jrev[8] = -1;
		jico[8] = GameWorld.IMG_JOB001;
		jdff[8] = Job.DIFF_HARD;

		jdes[9] = "<html><b>Find the comet crash site and retrieve the specimen.</b><br>It is believed that the comet is the source to<br> the creatures recently terrorising the area.</html>";
		jrew[9] = Job.REWARD_UNIT;
		jrev[9] = 19;
		jico[9] = GameWorld.IMG_JOB003;
		jdff[9] = Job.DIFF_IMPOSSIBLE;
}

	public static Job getNewJob(){
		int ind = (int)(Randomizer.getNextRandom()*MAX_JOBS);
		Job job = new Job(Job.ORDER_CUSTOM);
		job.setIcon(jico[ind]);
		job.setDescription(jdes[ind]);
		job.setReward(jrew[ind]);
		job.setRewardValue(jrev[ind]);
		job.setDifficulty(jdff[ind]);
		if (jrew[ind] == Job.REWARD_RESOURCE) {
			for (int i=0 ; i<GameWorld.RESOURCE_SIZE; i++) {
				job.setRewardResource(i, (int)(Randomizer.getNextRandom() * GameWorld.RESOURCE_REWARD_LIMIT * (Job.DIFF_IMPOSSIBLE + 1) / Job.DIFF_MAX));
			}
		}

		return job;
	}
}
