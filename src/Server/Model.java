package Server;

import GameFrame.Chess;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 模型类，实现了 Serializable 接口，方便通过序列化进行传输
 */
public class Model implements Serializable {

    public Model() {
        chessStack = new LinkedList<Chess>();
    }

    private final int SPACE = 0;
    private final int WHITE = -1;
    private final int BLACK = 1;
    private final int WIDTH = 15;
    private final int[][] chessBoard = new int[WIDTH][WIDTH];       //  棋盘
    private LinkedList<Chess> chessStack;       // 棋子栈
    private String chetInfo;        // 聊天信息
    private int winner = SPACE;     // 获胜方
    private int regretChessColor;       // 悔棋方
    private int surrenderChessColor;        // 认输方
    private int reshowIndex = 0;        // 复盘栈指针

    // get && set

    public LinkedList<Chess> getChessStack() {
        return this.chessStack;
    }

    public String getChetInfo() {
        return this.chetInfo;
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

    /**
     * 下棋操作 将对应的新棋子加入到棋子栈中，并在棋盘中进行标记
     * @param chessColor 棋子颜色
     * @param x x坐标
     * @param y y坐标
     */
    public void putChess(int chessColor, int x, int y) {
        Chess chess = new Chess(x,y,chessColor);
        chessBoard[x][y] = chessColor;
        chessStack.add(chess);
        System.out.println(chessColor+" "+x+" "+y);
    }

    /**
     * 悔棋操作
     */
    public void regretChess() {
        if(chessStack.size()<2){
            return;
        }
        Chess chess = chessStack.removeLast();
        chessBoard[chess.getPosX()][chess.getPosY()] = SPACE;
        chess = chessStack.removeLast();
        chessBoard[chess.getPosX()][chess.getPosY()] = SPACE;
    }

    /**
     * 检查指针是否在棋盘范围内
     * @param curX x坐标
     * @param curY y坐标
     * @return 返回是否在棋盘范围内
     */
    public boolean checkIndex(int curX, int curY) {
        if((curX>=0&&curX<WIDTH)&&(curY>=0&&curY<WIDTH)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 检查当前的下棋位置是否合理
     * @param curX x坐标
     * @param curY y坐标
     * @param i 标志区分占位
     * @return 返回当前下棋位置是否合理
     */
    public boolean checkIndex(int curX, int curY, int i) {
        // 如果当前位置在棋盘范围内，并且棋盘上还没有其他棋子
        if((curX>=0&&curX<WIDTH)&&(curY>=0&&curY<WIDTH)&&(chessBoard[curX][curY] == SPACE)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 更新聊天信息
     * @param userName 发送聊天信息的用户名
     * @param chetStr 发送的聊天信息
     */
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
