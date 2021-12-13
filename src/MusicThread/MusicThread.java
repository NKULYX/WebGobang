package MusicThread;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;

public class MusicThread {
    private static File musicFile;
    private static AudioClip player;

    public MusicThread() {
        musicFile = new File("music\\bgm.wav");
        try {
            player = Applet.newAudioClip(musicFile.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void start() {
        player.loop();
    }

    public static void stop(){
        player.stop();
    }

    public static void main(String[] args) throws InterruptedException {
        new MusicThread();
        MusicThread.start();
        Thread.sleep(10000);
    }
}
