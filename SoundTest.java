import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.event.*;
import java.io.*;
import java.applet.*;

public class SoundTest{
	public SoundTest(){
		AudioClip clip = java.applet.Applet.newAudioClip(this.getClass().getResource("sounds/ambient01.wav"));
		clip.play();
	}

	public static void main(String[] args){
		SongList s = new SongList();
		s.play();
		new SoundTest();
	}
}