package MusicThread;

import java.applet.Applet;
import java.io.File;
import java.net.MalformedURLException;

public class MusicThread {
    private static File musicFile;

    public MusicThread() {
        musicFile = new File("music\\bgm.wav");
        try {
            Applet.newAudioClip(musicFile.toURI().toURL()).loop();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MusicThread();
        Thread.sleep(10000);
    }
}
