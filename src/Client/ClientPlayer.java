package Client;

import GameFrame.Chess;
import GameFrame.GameFrame;
import GameFrame.ChessPanel;
import GameFrame.LoginFrame;
import GameLobby.GameLobby;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

class CommandOption{
    public static final String GET_ROOM_MEMBER_NUM = "GET_ROOM_MEMBER_NUM";
    public static final String PUT_CHESS = "PUT_CHESS";
    public static final String CHECK_UPDATE = "CHECK_UPDATE";
    public static final String REGRET_CHESS = "REGRET_CHESS";
    public static final String SEND_MESSAGE = "SEND_MESSAGE";
}

public class ClientPlayer {

    private static ClientPlayer instance;

    public static ClientPlayer getInstance() {
        if(instance == null){
            instance = new ClientPlayer();
        }
        return instance;
    }

    private static String clientName;
    private int chessColor;
    private int roomID;
    private int roomPort;
    private Socket socket;
    private boolean isGaming = false;

    /**
     * 和主服务器建立连接
     */
    public void connectMainServer() {
        try {
            socket = new Socket("localhost", 8080);
            System.out.println("客户端线程启动");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入登录界面
     */
    private void login() {
        LoginFrame.getInstance();
    }

    /**
     * 设置玩家的用户名
     * @param userName 玩家用户名
     */
    public void setUserName(String userName) {
        this.clientName = userName;
    }

    /**
     * 进入游戏大厅
     */
    public void enterGameLobby() {
        GameLobby.getInstance();
    }

    /**
     * 获取房间信息列表
     * @return
     */
    public ArrayList<Integer> getRoomList() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.GET_ROOM_MEMBER_NUM);
            out.println(builder.toString());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return (ArrayList<Integer>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置玩家的房间号
     * @param roomID 玩家所在房间号
     */
    public void setRoomID(int roomID) {
        this.roomID = roomID;
        System.out.println(roomID);
    }

    /**
     * 对玩家的基本信息进行初始化
     * @param roomId 玩家所处的房间号
     * @param chessColor 玩家棋子的颜色
     */
    public void init(int roomId, int chessColor) {
        this.roomID = roomId;
        this.chessColor = chessColor;
        connectRoomServer();
        enterGame();
    }

    /**
     * 和房间服务器建立连接
     */
    private void connectRoomServer() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println(roomID);
            while(true){
                String line = in.readLine();
                if(line.startsWith("a:")){
                    roomPort = Integer.parseInt(line.substring(2));
                    System.out.println("加入房间的端口号:"+roomPort);
//                    socket = new Socket("localhost",roomPort);  // 和房间服务器建立连接
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入游戏界面
     */
    private void enterGame() {
        this.isGaming = true;
        GameFrame.getInstance();
        new Thread(){
            @Override
            public void run() {
                System.out.println("开始检查更新");
                while(isGaming){
                    try {
                        checkUpdate();
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 检查棋局状态是否发生更新
     */
    private void checkUpdate() {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.CHECK_UPDATE);
            String info = builder.toString();
            System.out.println("向服务端发送请求: "+ info);
            out.println(info);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            System.out.println("收到服务端返回状态信息");
            LinkedList<Chess> chessStack = (LinkedList<Chess>) in.readObject();
            String chetInfo = (String) in.readObject();
            ChessPanel.getInstance().update(chessStack,chetInfo);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取玩家的棋子颜色
     * @return 棋子颜色
     */
    public int getChessColor() {
        return this.chessColor;
    }

    /**
     * 玩家用户下棋操作
     * @param chessColor
     * @param curX
     * @param curY
     * @return
     */
    public LinkedList<Chess> putChess(int chessColor, int curX, int curY) {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.PUT_CHESS);
            builder.append(":");
            builder.append(chessColor);
            builder.append(":");
            builder.append(curX);
            builder.append(":");
            builder.append(curY);
            String info = builder.toString();
            System.out.println(info);
            out.println(info);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            LinkedList<Chess> chessStack = (LinkedList<Chess>) in.readObject();
            System.out.println("收到服务端返回的 chessStack" + chessStack);
            return chessStack;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 玩家客户端启动<br>
     * 和主服务器建立连接<br>
     * 进入登录界面
     * @param args
     */
    public static void main(String[] args) {
        ClientPlayer.getInstance().connectMainServer();
        ClientPlayer.getInstance().login();
    }

}
