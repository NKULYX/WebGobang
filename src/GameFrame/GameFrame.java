package GameFrame;

import Client.ClientPlayer;

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

//        this.setSize(750,480);
//        this.setLocation(300,100);
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
//        this.pack();
        this.setVisible(true);
    }

    private void initActionListener() {
        returnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem rm = (JMenuItem) e.getSource();
                if(rm.isSelected()){
                    ClientPlayer.getInstance().returnGameLobby();
                }
            }
        });
    }


    public static void main(String[] args) {
        getInstance();
    }

    public void showWin() {
        JOptionPane.showMessageDialog(null, "恭喜你胜利了！","胜利！",JOptionPane.YES_OPTION);
    }

    public void showLose() {
        JOptionPane.showMessageDialog(null, "很遗憾你失败了！","失败！",JOptionPane.YES_OPTION);
    }
}
