package MusicThread;

import Client.ClientPlayer;
import GameFrame.Chess;

import javax.sound.sampled.*;
import java.io.File;

public class MusicThread {

    private static MusicThread instance;

    public static MusicThread getInstance() {
        if(instance == null) {
            instance = new MusicThread();
        }
        return instance;
    }

    private static String bgmPath = "music/bgm.wav";
    private static String chessMusic = "music/putchess.wav";

    public void start(){
        if(ClientPlayer.getInstance().getChessColor()== Chess.WHITE){
            return;
        }
        System.out.println("开始播放背景音乐");
        new Thread() {
            public void run() {
                while(ClientPlayer.getInstance().isGaming()) {
                    playMusic(bgmPath);
                }
            }
        }.start();
    }


    public void putChess() {
        new Thread() {
            public void run() {
                playMusic(chessMusic);
            }
        }.start();
    }


    private static void playMusic(String musicPath) {// 背景音乐播放

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(musicPath));
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            // value可以用来设置音量，从0-2.0
            double value = 0.5;
            float dB = (float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
            int nByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];
            while (true) {
                nByte = ais.read(buffer, 0, SIZE);
                if(nByte == -1){
                    break;
                }
                sdl.write(buffer, 0, nByte);
            }
            sdl.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
