package GameFrame;

import Client.ClientPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChetPanel extends JPanel {
    private static ChetPanel instance;

    public static ChetPanel getInstance() {
        if(instance == null){
            instance = new ChetPanel();
        }
        return instance;
    }

    private static JTextPane chetPane;
    private static JTextPane inputPane;
    private static JScrollPane scrollPane;
    private static JButton sendButton;
    private static JButton regretButton;
    private static JButton surrenderButton;


    public ChetPanel() {
        this.setLocation(420,0);
        this.setSize(200,420);
        this.setPreferredSize(new Dimension(200,420));
        this.setBackground(new Color(147, 98, 31, 255));
        this.setLayout(null);

        chetPane = new JTextPane();
        chetPane.setEditable(false);
        chetPane.setBounds(0,0,200,200);
        chetPane.setBackground(new Color(190, 115, 80, 255));

        scrollPane = new JScrollPane(chetPane);
        scrollPane.setBounds(460,20,200,200);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0),2),
                BorderFactory.createLineBorder(new Color(128,64,0),4)
        ));

        inputPane = new JTextPane();
        inputPane.setBounds(460,250,200,100);
        inputPane.setBackground(new Color(190, 115, 80, 255));
        inputPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0),2),
                BorderFactory.createLineBorder(new Color(128,64,0),4)
        ));

        sendButton = new JButton("发送");
        sendButton.setBounds(580,360,80,20);
        sendButton.setBackground(new Color(192,128,64));

        regretButton = new JButton("悔棋");
        regretButton.setBounds(460,390,80,20);
        regretButton.setBackground(new Color(192,128,64));

        surrenderButton = new JButton("认输");
        surrenderButton.setBounds(580,390,80,20);
        surrenderButton.setBackground(new Color(192,128,64));

        this.add(scrollPane);
        this.add(inputPane);
        this.add(sendButton);
        this.add(regretButton);
        this.add(surrenderButton);

        initActionListener();
    }

    private void initActionListener() {
        // 聊天信息输入框
        inputPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    localUpdateText();
                }
            }
        });

        // 发送按钮
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                localUpdateText();
            }
        });

        // 悔棋按钮
        regretButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(regretButton.getText().contains("悔棋")){
                    ClientPlayer.getInstance().regretChess();
                } else{
                    if (regretButton.getText().contains("复盘")){
                        regretButton.setText("下一步");
                    }
                    ClientPlayer.getInstance().reShow();
                }
            }
        });

        // 认输按钮
        surrenderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientPlayer.getInstance().surrender();
            }
        });
    }

    /**
     * 本地聊天信息刷新
     */
    private void localUpdateText() {
        String inputStr = inputPane.getText();
        inputPane.setText(null);
        ClientPlayer.getInstance().sendChetMessage(inputStr);
        String chetInfo = chetPane.getText();
        StringBuilder builder = new StringBuilder();
        builder.append(chetInfo);
        if(chetInfo.length()!=0){
            builder.append('\n');
        }
        builder.append(ClientPlayer.getInstance().getUserName()+":");
        builder.append('\n');
        builder.append(inputStr);
        chetPane.setText(builder.toString());
    }

    /**
     * 通过远程请求后的聊天信息刷新
     * @param chetInfo
     */
    public void updateText(String chetInfo) {
        chetPane.setText(chetInfo);
    }

    /**
     * 初始化复盘操作
     */
    public void initRePlay() {
        regretButton.setText("复盘");
        ChessPanel.getInstance().initRePlay();
    }
}
