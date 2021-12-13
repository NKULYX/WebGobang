package Client;

import GameFrame.Chess;
import GameFrame.GameFrame;
import GameFrame.ChessPanel;
import GameFrame.ChetPanel;
import GameFrame.LoginFrame;
import GameLobby.GameLobby;
import Server.Model;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 请求控制命令，包含:<br>
 * <code>GET_ROOM_MEMBER_NUM</code> 请求房间人数信息<br>
 * <code>CHECK_UPDATE</code> 请求更新命令<br>
 * <code>PUT_CHESS</code> 下棋命令<br>
 * <code>REGRET_CHESS</code> 悔棋命令<br>
 * <code>AGREE_REGRET</code> 同意悔棋命令<br>
 * <code>SEND_CHET_MESSAGE</code> 发送聊天信息命令<br>
 * <code>WIN</code> 获胜命令<br>
 * <code>SURRENDER</code> 认输命令<br>
 * <code>EXIT</code> 退出命令<br>
 */
class CommandOption{
    public static final String GET_ROOM_MEMBER_NUM = "GET_ROOM_MEMBER_NUM";
    public static final String CHECK_UPDATE = "CHECK_UPDATE";
    public static final String PUT_CHESS = "PUT_CHESS";
    public static final String REGRET_CHESS = "REGRET_CHESS";
    public static final String AGREE_REGRET = "AGREE_REGRET";
    public static final String SEND_CHET_MESSAGE = "SEND_CHET_MESSAGE";
    public static final String WIN = "WIN";
    public static final String SURRENDER = "SURRENDER";
    public static final String EXIT = "EXIT";
}

public class ClientPlayer {

    private static ClientPlayer instance;

    public static ClientPlayer getInstance() {
        if(instance == null){
            instance = new ClientPlayer();
        }
        return instance;
    }

    private String userName;        // 用户名
    private int chessColor;     // 用户棋子颜色
    private int roomID;     // 房间号
    private int roomPort;       // 房间端口号
    private Socket socket;      // 通信套接字
    private Model model;        // 棋局 model
    private boolean isGaming = false;       // 是否正在游戏
    private boolean isTurn;     // 是否轮到本方

    /**
     * 获取玩家的棋子颜色
     * @return 棋子颜色
     */
    public int getChessColor() {
        return this.chessColor;
    }

    /**
     * 设置玩家的用户名
     * @param userName 玩家用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取用户名
     * @return 获取用户名
     */
    public String getUserName() {
        return userName;
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
     * 返回当前是否正在游戏
     * @return 返回当前是否正在游戏
     */
    public boolean isGaming() {
        return isGaming;
    }

    /**
     * 返回是否轮到本方
     * @return 返回是否轮到本方
     */
    public boolean isTurn() {
        return this.isTurn;
    }

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
        LoginFrame.getInstance().setVisible(true);
    }

    /**
     * 进入游戏大厅
     */
    public void enterGameLobby() {
        System.out.println("进入游戏大厅");
        GameLobby.getInstance().start();
    }

    /**
     * 获取房间信息列表
     * @return 获取房间信息列表
     */
    public ArrayList<Integer> getRoomList() {
        try {
            socket = new Socket("localhost",8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.GET_ROOM_MEMBER_NUM);
            // 向主服务请请求房间人数信息
            out.println(builder.toString());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            // 读取主服务器返回的房间人数信息
            ArrayList<Integer> list = (ArrayList<Integer>) in.readObject();
            System.out.println(list);
            return list;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
            socket = new Socket("localhost",8080);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println("SET_ROOMID:"+roomID);
//            while(true){
                String line = in.readLine();
                if(line.startsWith("a:")){
                    roomPort = Integer.parseInt(line.substring(2));
                    System.out.println("加入房间的端口号:"+roomPort);
//                    socket = new Socket("localhost",roomPort);  // 和房间服务器建立连接
//                    break;
//                }
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
        GameFrame.getInstance().setVisible(true);
        // 进入游戏之后每 0.5 秒向房间服务器请求更新
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
            // 向房间服务器发起一次请求
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.CHECK_UPDATE);
            String info = builder.toString();
            System.out.println("向服务端发送请求: "+ info);
            out.println(info);
            // 读取房间服务器返回的棋局 model
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            model = (Model) in.readObject();
            LinkedList<Chess> chessStack = model.getChessStack();;
            System.out.println("收到服务端返回状态信息" + chessStack);
            String chetInfo = model.getChetInfo();

            // 重绘棋局信息并更新聊天信息
            ChessPanel.getInstance().update(chessStack,chetInfo);

            // 如果当前棋子栈上的最后一个棋子的颜色和自己不一样，则轮到自己下棋了，更新 isTurn 为 true 并调用更新重绘界面
            if((!chessStack.isEmpty())&&(chessStack.getLast().getColor()!=this.chessColor)&&(this.chessColor!=Chess.SPACE)){
                this.isTurn = true;
            }

            // 检查当前的胜负状况 如果有胜负发生，则停止更新请求，并准备复盘
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
                initRePlay();       // 准备复盘
            }

            // 检查是否有悔棋请求 如果悔棋者的颜色和自己不同并且不是SPACE 则需要本方去确认是否同意悔棋请求
            if((model.getRegretChessColor()!=Chess.SPACE)&&model.getRegretChessColor()==(-1*this.chessColor)){
                askAgreeRegret();
            }

            // 检查是否有认输请求 如果认输者的颜色和自己不同并且不是SAPCE 则需要本方去确认是否同意认输请求
            if((model.getSurrenderChessColor()!=Chess.SPACE)&&model.getSurrenderChessColor()==(-1*this.chessColor)){
                askAgreeSurrender();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备复盘操作
     */
    private void initRePlay() {
        ChetPanel.getInstance().initRePlay();
    }

    /**
     * 请求是否同意悔棋
     */
    private void askAgreeRegret() {
        int option = JOptionPane.showConfirmDialog(null, "对方想要悔棋，是否同意悔棋请求","悔棋请求",JOptionPane.OK_CANCEL_OPTION);
        System.out.println(option);
        sendAgreeRegretMessage(option);
    }

    /**
     * 请求是否同意认输
     */
    private void askAgreeSurrender() {
        int option = JOptionPane.showConfirmDialog(null, "对方想要认输，是否同意认输请求","认输请求",JOptionPane.OK_CANCEL_OPTION);
        System.out.println(option);
        if(option == 0){
            sendWinMessage(this.chessColor);
        } else{
            sendWinMessage(Chess.SPACE);
        }
    }

    /**
     * 玩家用户下棋操作
     * @param chessColor 棋子颜色
     * @param curX x坐标
     * @param curY y坐标
     * @return 返回更新后的棋子栈
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

    /**
     * 发送同意悔棋的请求
     * @param option 是否同意悔棋 0-同意 1-不同意
     */
    private void sendAgreeRegretMessage(int option) {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.AGREE_REGRET);
            builder.append(":");
            builder.append(option);
            out.println(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向房间服务器发送胜负信息
     * @param chessColor 获胜方的棋子颜色
     */
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

    /**
     * 检查当前是否有胜负发生
     * @param curX 当前x坐标
     * @param curY 当前y坐标
     * @param chessColor 当前棋子颜色
     * @return 是否获胜
     */
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
     * 检查当前下棋位置是否合理
     * @param curX x坐标
     * @param curY y坐标
     * @return 当前位置是否合理
     */
    public boolean checkIndex(int curX, int curY) {
        return model.checkIndex(curX, curY);
    }

    public boolean checkIndex(int curX, int curY, int i) {
        return model.checkIndex(curX, curY, i);
    }

    /**
     * 向房间服务器发送聊天信息
     * @param inputStr 聊天信息
     */
    public void sendChetMessage(String inputStr) {
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

    /**
     * 向房间服务器发送悔棋请求
     */
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

    /**
     * 向房间服务器发送认输请求
     */
    public void surrender() {
        try {
            socket = new Socket("localhost",roomPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.SURRENDER);
            builder.append(":");
            builder.append(this.chessColor);
            String info = builder.toString();
            System.out.println("客户端发送了:"+info);
            out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 本地进行复盘操作
     */
    public void reShow() {
        ChessPanel.getInstance().reShow();
    }

    /**
     * 完成复盘操作
     */
    public void finishReshow() {
        JOptionPane.showMessageDialog(null, "已经完成复盘操作","复盘结束",JOptionPane.OK_OPTION);
    }

    /**
     * 返回游戏大厅 并向主服务器发送退出游戏进入游戏大厅的请求
     */
    public void returnGameLobby() {
        // 如果还在游戏中需要先结束游戏
        if (isGaming) {
            JOptionPane.showMessageDialog(null,"您正在游戏中，不能返回","返回游戏大厅",JOptionPane.OK_OPTION);
        }
        // 重置 isGaming 和 isTurn
        else {
            this.isGaming = false;
            this.isTurn = false;
            GameFrame.getInstance().setVisible(false);
            GameLobby.getInstance().setVisible(true);
            // 向主服务器发送退出信息
            sendExitMessage();
            GameLobby.getInstance().start();
        }
    }

    /**
     * 向主服务器发送退出游戏进入大厅请求
     */
    private void sendExitMessage() {
        try {
            // 和主服务器建立请求
            socket = new Socket("localhost",8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder = new StringBuilder();
            builder.append(CommandOption.EXIT);
            builder.append(":");
            builder.append(this.roomID);
            String info = builder.toString();
            System.out.println("客户端发送了:"+info);
            out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
