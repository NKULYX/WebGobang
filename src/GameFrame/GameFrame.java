package GameFrame;

import javax.swing.*;
import java.awt.*;

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

    private GameFrame() throws HeadlessException {

//        this.setSize(750,480);
//        this.setLocation(300,100);
        this.setBounds(300,100,710,480);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        menu = new JMenu("操作");
        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        chessPanel = ChessPanel.getInstance();
        chetPanel = ChetPanel.getInstance();
        this.getContentPane().add(chessPanel);
        this.getContentPane().add(chetPanel);
//        this.pack();
        this.setVisible(true);
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
