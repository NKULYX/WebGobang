package GameFrame;

import Client.ClientPlayer;
import MusicThread.MusicThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

/**
 * 鼠标光标类<br>
 * 方便再游戏界面内绘制鼠标的信息
 */
class CursorTriger{
    int x;
    int y;
    Image cursor;
    boolean draw;

    public CursorTriger() {
        cursor = new ImageIcon("image/cursor.png").getImage();
        draw = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public Image getCursor() {
        return cursor;
    }
}

public class ChessPanel extends JPanel {

    private static ChessPanel instance;

    public static ChessPanel getInstance() {
        if(instance == null){
            instance = new ChessPanel();
        }
        return instance;
    }

    private static final int BASEX = 41;        // 棋盘的基准 BASEX
    private static final int BASEY = 41;        // 棋盘的基准 BASEY
    private static final int BLOCKWIDTH = 24;       // 棋盘每个格子的宽度
    private static Image whiteChess;
    private static Image blackChess;
    private static Image background;
    private static CursorTriger cursorTriger;

    private static LinkedList<Chess> chessStack = new LinkedList<Chess>();      // 本地棋子栈副本
    private static String chetInfo;     // 本地聊天信息副本

    private static int reshowIndex = 0;     // 复盘栈指针 无需复盘操作时值为0

    public ChessPanel() {

        whiteChess = new ImageIcon("image/white.png").getImage();
        blackChess = new ImageIcon("image/black.png").getImage();
        background = new ImageIcon("image/board.png").getImage();
        cursorTriger = new CursorTriger();

        initActionListener();

        this.setBounds(0,0,background.getWidth(null), background.getHeight(null));
    }

    private void initActionListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(ClientPlayer.getInstance().isTurn()){
                    int curX = (e.getX() - BASEX + BLOCKWIDTH / 2) / BLOCKWIDTH;
                    int curY = (e.getY() - BASEY + BLOCKWIDTH / 2) / BLOCKWIDTH;
                    if(ClientPlayer.getInstance().checkIndex(curX,curY,0)){
                        chessStack = ClientPlayer.getInstance().putChess(ClientPlayer.getInstance().getChessColor(),curX,curY);
                        MusicThread.getInstance().putChess();
                        repaint();
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                int curX = (e.getX() - BASEX + BLOCKWIDTH / 2) / BLOCKWIDTH;
                int curY = (e.getY() - BASEY + BLOCKWIDTH / 2) / BLOCKWIDTH;
                if(curX>=0 && curX<=14 && curY>=0 && curY<=14 &&
                        (curX!=cursorTriger.getX()||curY!=cursorTriger.getY())){
                    cursorTriger.setX(curX);
                    cursorTriger.setY(curY);
                    cursorTriger.setDraw(true);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background,0,0,background.getWidth(null),background.getHeight(null),null);
        // 绘制鼠标位置 焦点信息
        if(cursorTriger.isDraw()){
            g.drawImage(cursorTriger.getCursor(), cursorTriger.getX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, cursorTriger.getY() * BLOCKWIDTH + BASEY - BLOCKWIDTH / 2, cursorTriger.getCursor().getWidth(null),cursorTriger.getCursor().getHeight(null),null);
        }
        // 如果棋子栈非空并且复盘栈指针为0 则绘制棋子
        if(chessStack!=null&&(reshowIndex==0)){
            for(int i=0;(i<chessStack.size());i++){
                Chess c = chessStack.get(i);
                if(c.getColor()==Chess.WHITE){
                    g.drawImage(whiteChess,c.getPosX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, c.getPosY() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, Chess.WIDTH, Chess.HEIGHT, null);
                } else {
                    g.drawImage(blackChess,c.getPosX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, c.getPosY() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, Chess.WIDTH, Chess.HEIGHT, null);
                }
            }
        }
        // 如果棋子栈不为空，且当前正在进行复盘操作
        else if(chessStack!=null&&(reshowIndex!=0)){
            for(int i=0;(i<reshowIndex);i++){
                Chess c = chessStack.get(i);
                if(c.getColor()==Chess.WHITE){
                    g.drawImage(whiteChess,c.getPosX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, c.getPosY() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, Chess.WIDTH, Chess.HEIGHT, null);
                } else {
                    g.drawImage(blackChess,c.getPosX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, c.getPosY() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, Chess.WIDTH, Chess.HEIGHT, null);
                }
            }
        }
    }

    /**
     * 更新本地的棋子栈和聊天信息
     * @param chessStack 棋子栈
     * @param chetInfo 聊天信息
     */
    public void update(LinkedList<Chess> chessStack, String chetInfo) {
        this.chessStack = chessStack;
        this.chetInfo = chetInfo;
        // 重绘游戏界面 展示更新内容
        repaint();
        ChetPanel.getInstance().updateText(chetInfo);
    }

    /**
     * 初始化复盘信息
     */
    public void initRePlay() {
        this.reshowIndex = 0;
    }

    /**
     * 复盘操作
     */
    public void reShow() {
        if(reshowIndex<chessStack.size()){
            this.reshowIndex++;
            repaint();
        } else{
            ClientPlayer.getInstance().finishReshow();
        }
    }

}
