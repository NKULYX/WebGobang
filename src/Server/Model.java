package Server;

import GameFrame.Chess;

import java.util.LinkedList;

public class Model {
    private static Model instance;

    public static Model getInstance() {
        if(instance==null){
            instance = new Model();
        }
        return instance;
    }

    public Model() {
        chessStack = new LinkedList<Chess>();
    }

    private static final int SPACE = 0;
    private static final int WHITE = -1;
    private static final int BLACK = 1;
    private static final int WIDTH = 15;
    private static final int[][] chessBoard = new int[WIDTH][WIDTH];
    private static LinkedList<Chess> chessStack;
    private static String chetInfo;
    private static boolean needUpdate;

    public static boolean isNeedUpdate() {
        return needUpdate;
    }

    public static void setNeedUpdate(boolean needUpdate) {
        Model.needUpdate = needUpdate;
    }

    public void putChess(int chessColor, int x, int y) {
        Chess chess = new Chess(x,y,chessColor);
        chessBoard[x][y] = chessColor;
        chessStack.push(chess);
        System.out.println(chessColor+" "+x+" "+y);
        this.needUpdate = true;
    }

    public void regretChess() {
        Chess chess = chessStack.pop();
        chessBoard[chess.getPosX()][chess.getPosY()] = SPACE;
    }

    public LinkedList<Chess> getChessStack() {
        return this.chessStack;
    }

    public String getChetInfo() {
        return this.chetInfo;
    }
}
