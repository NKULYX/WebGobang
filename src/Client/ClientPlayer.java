package Client;

import GameFrame.Chess;
import GameFrame.GameFrame;
import GameFrame.ChessPanel;
import GameFrame.LoginFrame;
import GameLobby.GameLobby;
import Server.Model;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

class CommandOption{
    public static final String GET_ROOM_MEMBER_NUM = "GET_ROOM_MEMBER_NUM";
    public static final String PUT_CHESS = "PUT_CHESS";
    public static final String CHECK_UPDATE = "CHECK_UPDATE";
    public static final String WIN = "WIN";
    public static final String SEND_CHET_MESSAGE = "SEND_CHET_MESSAGE";
    public static final String REGRET_CHESS = "REGRET_CHESS";
}

public class ClientPlayer {

    private static ClientPlayer instance;
    private String userName;

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
    private Model model;
    private boolean isGaming = false;
    private boolean isTurn;

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
        GameLobby.getInstance().start();
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
        if(chessColor == Chess.BLACK){
            isTurn = true;
        } else if(chessColor == Chess.WHITE){
            isTurn = false;
        } else {
            // 观战者永世不得turn
            isTurn = false;
        }
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
            out.println("SET_ROOMID:"+roomID);
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
            model = (Model) in.readObject();
//            LinkedList<Chess> chessStack = (LinkedList<Chess>) in.readObject();
//            String chetInfo = (String) in.readObject();
            LinkedList<Chess> chessStack = model.getChessStack();;
            System.out.println("收到服务端返回状态信息" + chessStack);
            String chetInfo = model.getChetInfo();
            // 如果当前棋子栈上的最后一个棋子的颜色和自己不一样，则轮到自己下棋了，更新 isTurn 为 true 并调用更新重绘界面
            if((!chessStack.isEmpty())&&(chessStack.getLast().getColor()!=this.chessColor)&&(this.chessColor!=Chess.SPACE)){
                this.isTurn = true;
            }
            ChessPanel.getInstance().update(chessStack,chetInfo);
            // 检查当前的胜负状况
            System.out.println("当前的胜负情况:"+model.getWinner());
            if(model.getWinner()!=Chess.SPACE){
                if(model.getWinner()==this.chessColor){
                    GameFrame.getInstance().showWin();
                    this.isGaming = false;
                    this.isTurn = false;
                } else {
                    GameFrame.getInstance().showLose();
                    this.isGaming = false;
                    this.isTurn = false;
                }
            }
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
            this.isTurn = false;
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
            // 检查当前是否发生胜利
            if(checkWin(curX,curY,chessColor)){
                sendWinMessage(this.chessColor);
            }
            return chessStack;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendWinMessage(int chessColor) {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.WIN);
            builder.append(":");
            builder.append(this.chessColor);
            String info = builder.toString();
            System.out.println("客户端胜利了:"+info);
            out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkWin(int curX, int curY, int chessColor) {
        int[] x = {0,1,1,1,0,-1,-1,-1};
        int[] y = {1,1,0,-1,-1,-1,0,1};
        int[] cnt = {0,0,0,0,0,0,0,0};
        int[][] chessBoard = model.getBoard();
        for(int i=0;i<8;i++) {
            int j=1;
            for(; checkIndex(curX+x[i]*j,curY+y[i]*j)
                    &&chessBoard[curX+x[i]*j][curY+y[i]*j] == chessColor; j++) {
                cnt[i]++;
            }
        }
        for(int i=0;i<4;i++) {
            if(cnt[i]+cnt[i+4]+1>=5) {
                System.out.println(this.chessColor+"赢了");
                return true;
            }
        }
        return false;
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

    /**
     * 检查当前下棋位置是否合理
     * @param curX x坐标
     * @param curY y坐标
     * @return
     */
    public boolean checkIndex(int curX, int curY) {
        return model.checkIndex(curX, curY);
    }

    public boolean checkIndex(int curX, int curY, int i) {
        return model.checkIndex(curX, curY, i);
    }

    public boolean isTurn() {
        return this.isTurn;
    }

    public void sendText(String inputStr) {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.SEND_CHET_MESSAGE);
            builder.append(":");
            builder.append(this.userName);
            builder.append(":");
            builder.append(inputStr);
            String info = builder.toString();
            System.out.println("客户端发送了:"+info);
            out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void regretChess() {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.REGRET_CHESS);
            builder.append(":");
            builder.append(this.chessColor);
            String info = builder.toString();
            System.out.println("客户端发送了:"+info);
            out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isGaming() {
        return isGaming;
    }
}
