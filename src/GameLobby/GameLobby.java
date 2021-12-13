package GameLobby;

import Client.ClientPlayer;
import GameFrame.Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private static ArrayList<JLabel> roomMemberNumLabel;        // 房间人数的标签
    private static ArrayList<JButton> roomEnterButton;      // 进入房间的按钮
    private static ArrayList<Integer> roomMemberNum;

    private GameLobby() {

        roomMemberNum = ClientPlayer.getInstance().getRoomList();       // 获取房间人数信息

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
            // 如果当前房间中的人数小于2人 则可以加入游戏 否则只能加入观战
            if(roomMemberNum.get(i)<2){
                enterButton.setText("加入房间");
            } else {
                enterButton.setText("加入观战");
            }
            roomEnterButton.add(enterButton);

            enterButton.setName(Integer.toString(i));
            enterButton.setBounds(90,0,90,30);
            enterButton.setBackground(new Color(0,98,132));
            enterButton.addActionListener(new ActionListener() {        // 给加入房间的按钮添加事件监听
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    int buttonID = Integer.parseInt(button.getName());
                    ClientPlayer.getInstance().setRoomID(buttonID);     // 设置客户端的房间号
                    int currMemberNum = GameLobby.getInstance().updateRoomMemberNum(buttonID,1);        // 更新房间用户数量
                    // 设置当前进入房间的用户身份
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
                    GameLobby.getInstance().setVisible(false);
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

    /**
     * 更新房间人数标签上的信息
     * @param roomId 房间号
     * @param change 变化
     * @return 返回当前房间中的人数
     */
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

    /**
     * 启动大厅线程，不断向主服务器请求信息更新
     */
    public void start() {
        System.out.println("大厅开始发起更新请求");
        new Thread(){
            @Override
            public void run() {
                // 当用户还没有进入游戏时，也就是还在选择房间的阶段时
                while(!ClientPlayer.getInstance().isGaming()){
                    System.out.println("向主服务器请求房间信息");
                    // 每 0.1 秒向主服务器请求一次房间信息
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

    /**
     * 更新房间人数 label 和 进入按钮的提示信息
     */
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

//    public static void main(String[] args) {
//        GameLobby.getInstance();
//    }

}
