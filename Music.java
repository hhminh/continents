import javax.sound.sampled.*;
import java.io.*;

public class Music implements LineListener{
	private Clip MClip;
	private AudioInputStream audioInput;
	public Music(InputStream clipFile){
		try{
			audioInput = AudioSystem.getAudioInputStream(clipFile);
		}catch (Exception e){
			audioInput = null;
			System.out.println(e);
		}
	}

	public void play(int nLoopCount){
		if (audioInput != null){
			AudioFormat format = audioInput.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			try{
				MClip = (Clip) AudioSystem.getLine(info);
				MClip.addLineListener(this);
				MClip.open(audioInput);
				System.out.println(MClip.getLineInfo());
			}catch (LineUnavailableException e){
				System.out.println(e);
			}catch (IOException e){
				System.out.println(e);
			}
			MClip.loop(nLoopCount);
		}else{
			System.out.println("NOT PLAYING");
		}
	}

	public void update(LineEvent event){
		if (event.getType().equals(LineEvent.Type.STOP)){
			MClip.close();
		}else if (event.getType().equals(LineEvent.Type.CLOSE)){
			System.exit(0);
		}
	}
}