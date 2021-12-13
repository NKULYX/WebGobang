package Server;

import GameFrame.Chess;

import java.io.Serializable;
import java.util.LinkedList;

public class Model implements Serializable {
//    private static Model instance;

//    public static Model getInstance() {
//        if(instance==null){
//            instance = new Model();
//        }
//        return instance;
//    }

    public Model() {
        chessStack = new LinkedList<Chess>();
    }

    private final int SPACE = 0;
    private final int WHITE = -1;
    private final int BLACK = 1;
    private final int WIDTH = 15;
    private final int[][] chessBoard = new int[WIDTH][WIDTH];
    private LinkedList<Chess> chessStack;
    private String chetInfo;
    private int winner = SPACE;
    private int regretChessColor;
    private boolean agreeRegret = false;
    private int surrenderChessColor;
    private boolean agreeSurrender = false;
    private int reshowIndex = 0;

    public int getReshowIndex() {
        return reshowIndex;
    }

    public void setReshowIndex(int reshowIndex) {
        this.reshowIndex = reshowIndex;
    }

    public boolean isAgreeSurrender() {
        return agreeSurrender;
    }

    public void setAgreeSurrender(boolean agreeSurrender) {
        this.agreeSurrender = agreeSurrender;
    }

    public int getSurrenderChessColor() {
        return surrenderChessColor;
    }

    public void setSurrenderChessColor(int surrenderChessColor) {
        this.surrenderChessColor = surrenderChessColor;
    }

    public int getRegretChessColor() {
        return regretChessColor;
    }

    public void setRegretChessColor(int regretChessColor) {
        this.regretChessColor = regretChessColor;
    }

    public boolean isAgreeRegret() {
        return agreeRegret;
    }

    public void setAgreeRegret(boolean agreeRegret) {
        this.agreeRegret = agreeRegret;
        regretChessColor = Chess.SPACE;
    }

    public void putChess(int chessColor, int x, int y) {
        Chess chess = new Chess(x,y,chessColor);
        chessBoard[x][y] = chessColor;
        chessStack.add(chess);
        System.out.println(chessColor+" "+x+" "+y);
    }

    public void regretChess() {
        Chess chess = chessStack.removeLast();
        chessBoard[chess.getPosX()][chess.getPosY()] = SPACE;
        chess = chessStack.removeLast();
        chessBoard[chess.getPosX()][chess.getPosY()] = SPACE;
    }

    public LinkedList<Chess> getChessStack() {
        return this.chessStack;
    }

    public String getChetInfo() {
        return this.chetInfo;
    }

    public boolean checkIndex(int curX, int curY) {
        if((curX>=0&&curX<WIDTH)&&(curY>=0&&curY<WIDTH)){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkIndex(int curX, int curY, int i) {
        if((curX>=0&&curX<WIDTH)&&(curY>=0&&curY<WIDTH)&&(chessBoard[curX][curY] == SPACE)){
            return true;
        }else{
            return false;
        }
    }

    public int[][] getBoard() {
        return this.chessBoard;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winChessColor) {
        this.winner = winChessColor;
        this.surrenderChessColor = Chess.SPACE;
    }

    public void updateChetInfo(String userName, String chetStr) {
        StringBuilder builder = new StringBuilder();
        if(chetInfo!=null){
            builder.append(chetInfo);
            builder.append('\n');
        }
        builder.append(userName+":");
        builder.append('\n');
        builder.append(chetStr);
        chetInfo = builder.toString();
    }
}
