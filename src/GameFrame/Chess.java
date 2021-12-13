package GameFrame;

import java.io.Serializable;

public class Chess implements Serializable {
    public static final int SPACE = 0;
    public static final int WHITE = -1;
    public static final int BLACK = 1;
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    private final int posX;
    private final int posY;
    private final int color;

    public Chess(int posX, int posY, int color) {
        this.posX = posX;
        this.posY = posY;
        this.color = color;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getColor() {
        return color;
    }
}
