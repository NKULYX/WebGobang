package GameLobby;

import Client.ClientPlayer;
import GameFrame.Chess;
import Server.RoomServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLobby extends JFrame {
    private static GameLobby instance;

    public static GameLobby getInstance() {
        if(instance == null) {
            instance = new GameLobby();
        }
        return instance;
    }

    private static JPanel mainPanel;
    private static JScrollPane scrollPane;
    private static int roomNum = 12;
    private static ArrayList<JLabel> roomMemberNumLabel;
    private static ArrayList<JButton> roomEnterButton;
    private static ArrayList<Integer> roomMemberNum;

    private GameLobby() {

        roomMemberNum = ClientPlayer.getInstance().getRoomList();

        roomMemberNumLabel = new ArrayList<JLabel>();
        roomEnterButton = new ArrayList<JButton>();

        this.setLocation(250,0);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(800,650));
        mainPanel.setLayout(new FlowLayout());
        mainPanel.setBackground(new Color(0,98,132));

        for(int i=0;i<roomNum;i++){
            JPanel roomPanel = new JPanel();
            roomPanel.setLayout(null);
            roomPanel.setPreferredSize(new Dimension(180,210));

            JLabel imageLabel = new JLabel();
            imageLabel.setBounds(0, 0, 180, 180);
            imageLabel.setIcon(new ImageIcon("image/room.png"));

            JPanel infoPanel = new JPanel();
            infoPanel.setBounds(0,180,180,30);
            infoPanel.setLayout(null);
            String curNum = "当前人数:" + roomMemberNum.get(i);
            JLabel clientNum = new JLabel(curNum,SwingConstants.CENTER);
            roomMemberNumLabel.add(clientNum);
            clientNum.setBounds(0,0,90,30);
            clientNum.setOpaque(true);
            clientNum.setBackground(new Color(0,98,132));

            JButton enterButton = new JButton();
            if(roomMemberNum.get(i)<2){
                enterButton.setText("加入房间");
            } else {
                enterButton.setText("加入观战");
            }
            roomEnterButton.add(enterButton);

            enterButton.setName(Integer.toString(i));
            enterButton.setBounds(90,0,90,30);
            enterButton.setBackground(new Color(0,98,132));
            enterButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    int buttonID = Integer.parseInt(button.getName());
                    ClientPlayer.getInstance().setRoomID(buttonID);     // 设置客户端的房间号
                    int currMemberNum = GameLobby.getInstance().updateRoomMemberNum(buttonID,1);
                    if(currMemberNum<=2){
                        int chessColor;
                        if(currMemberNum==1){
                            chessColor = Chess.WHITE;
                        }else{
                            chessColor = Chess.BLACK;
                        }
                        System.out.println("chessColor"+chessColor);
                        ClientPlayer.getInstance().init(buttonID,chessColor);
                    } else {
                        ClientPlayer.getInstance().init(buttonID,Chess.SPACE);
                    }

                }
            });

            infoPanel.add(clientNum);
            infoPanel.add(enterButton);

            roomPanel.add(imageLabel);
            roomPanel.add(infoPanel);

            mainPanel.add(roomPanel);

        }

        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(800,650));

        this.getContentPane().add(scrollPane);

        this.pack();
        this.setVisible(true);
    }

    private int updateRoomMemberNum(int roomId, int change) {
        JLabel label = this.roomMemberNumLabel.get(roomId);
        String info = label.getText();
        String[] list = info.split(":");
        StringBuilder builder = new StringBuilder();
        builder.append(list[0]);
        builder.append(":");
        String num;
        if(list[1].charAt(0)==' '){
            num = list[1].substring(1,list[1].length());
        } else{
            num = list[1];
        }
        int prensentNum = Integer.parseInt(num);
        prensentNum += change;
        builder.append(prensentNum);
        label.setText(builder.toString());
        return prensentNum;
    }

    public static void main(String[] args) {
        GameLobby.getInstance();
    }

    public void start() {
        System.out.println("大厅开始发起更新请求");
        new Thread(){
            @Override
            public void run() {
                System.out.println(ClientPlayer.getInstance().isGaming());
                while(!ClientPlayer.getInstance().isGaming()){
                    System.out.println("向主服务器请求房间信息");
                    try {
                        roomMemberNum = ClientPlayer.getInstance().getRoomList();
                        updataRoomInfo();
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void updataRoomInfo() {
        for(int i=0;i<roomNum;i++){
            int num = roomMemberNum.get(i);
            roomMemberNumLabel.get(i).setText("当前人数:"+num);
            if(num<2){
                roomEnterButton.get(i).setText("加入房间");
            } else {
                roomEnterButton.get(i).setText("加入观战");
            }
        }
    }
}
