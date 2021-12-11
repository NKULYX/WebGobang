package GameFrame;

import Client.ClientPlayer;
import Server.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

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

    private static final int BASEX = 41;
    private static final int BASEY = 41;
    private static final int BLOCKWIDTH = 24;
    private static Image whiteChess;
    private static Image blackChess;
    private static Image background;
    private static CursorTriger cursorTriger;

    private static LinkedList<Chess> chessStack = new LinkedList<Chess>();
    private static String chetInfo;

    public ChessPanel() {

        whiteChess = new ImageIcon("image/white.png").getImage();
        blackChess = new ImageIcon("image/black.png").getImage();
        background = new ImageIcon("image/board.png").getImage();
        cursorTriger = new CursorTriger();

        initActionListener();

//        this.setPreferredSize(new Dimension(420, 420));
//        this.setSize(420,420);
        this.setBounds(0,0,background.getWidth(null), background.getHeight(null));
//        this.setBounds(0,0,420, 420);
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
        if(cursorTriger.isDraw()){
            g.drawImage(cursorTriger.getCursor(), cursorTriger.getX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, cursorTriger.getY() * BLOCKWIDTH + BASEY - BLOCKWIDTH / 2, cursorTriger.getCursor().getWidth(null),cursorTriger.getCursor().getHeight(null),null);
        }
        if(chessStack!=null){
            for(int i=0;i<chessStack.size();i++){
                Chess c = chessStack.get(i);
                if(c.getColor()==Chess.WHITE){
                    g.drawImage(whiteChess,c.getPosX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, c.getPosY() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, Chess.WIDTH, Chess.HEIGHT, null);
                } else {
                    g.drawImage(blackChess,c.getPosX() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, c.getPosY() * BLOCKWIDTH + BASEX - BLOCKWIDTH / 2, Chess.WIDTH, Chess.HEIGHT, null);
                }
            }
        }
    }

    public void update(LinkedList<Chess> chessStack, String chetInfo) {
        this.chessStack = chessStack;
        this.chetInfo = chetInfo;
        repaint();
    }
}
