/**
 * Created by Minh on 16/08/2015.
 */
import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;

public class SongList
{
    private ArrayList songs = new ArrayList();
    private int count = 0;

    public SongList()
    {
        this.songs.add("song0.wav");
        this.songs.add("ambient01.wav");
    }

    public void play()
    {
        AudioClip localAudioClip = Applet.newAudioClip(getClass().getResource("sounds/" + (String)this.songs.get(this.count)));
        if (localAudioClip != null)
        {
            localAudioClip.play();
            if (++this.count >= this.songs.size()) {
                this.count = 0;
            }
        }
    }
}

