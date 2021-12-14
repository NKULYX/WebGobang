package GameFrame;

import Client.ClientPlayer;
import GameLobby.GameLobby;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {

    private static GameFrame instance;

    public static GameFrame getInstance() {
        if(instance == null){
            instance = new GameFrame();
        }
        return instance;
    }

    private static ChessPanel chessPanel;
    private static ChetPanel chetPanel;
    private static JMenuBar menuBar;
    private static JMenu menu;
    private static JMenuItem returnMenu;

    private GameFrame() throws HeadlessException {

        this.setBounds(300,100,710,480);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        menu = new JMenu("操作");
        returnMenu = new JMenuItem("返回游戏大厅");
        menu.add(returnMenu);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        initActionListener();

        chessPanel = ChessPanel.getInstance();
        chetPanel = ChetPanel.getInstance();
        this.getContentPane().add(chessPanel);
        this.getContentPane().add(chetPanel);
        this.setVisible(true);
    }

    /**
     * 初始化事件监听
     */
    private void initActionListener() {
        returnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("选择返回游戏大厅");
                ClientPlayer.getInstance().returnGameLobby();
            }
        });
    }

    /**
     * 展示胜利提示
     */
    public void showWin() {
        JOptionPane.showMessageDialog(null, "恭喜你胜利了！","胜利！",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 展示失败提示
     */
    public void showLose() {
        JOptionPane.showMessageDialog(null, "很遗憾你失败了！","失败！",JOptionPane.ERROR_MESSAGE);
    }

    public void showGameOver(int winner) {
        String winnerColor;
        if(winner == 1){
            winnerColor = "BLACK";
        } else{
            winnerColor = "WHITE";
        }
        JOptionPane.showMessageDialog(null, "对局结束, "+winnerColor+" 获胜!","对局结束",JOptionPane.INFORMATION_MESSAGE);
    }

//    public static void main(String[] args) {
//        getInstance();
//    }


}
