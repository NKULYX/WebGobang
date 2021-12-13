package Server;

import GameFrame.Chess;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 请求控制命令，包含:<br>
 * <code>CHECK_UPDATE</code> 请求更新命令<br>
 * <code>PUT_CHESS</code> 下棋命令<br>
 * <code>REGRET_CHESS</code> 悔棋命令<br>
 * <code>AGREE_REGRET</code> 同意悔棋命令<br>
 * <code>SEND_CHET_MESSAGE</code> 发送聊天信息命令<br>
 * <code>WIN</code> 获胜命令<br>
 * <code>SURRENDER</code> 认输命令<br>
 */
class CommandOption{
    public static final String CHECK_UPDATE = "CHECK_UPDATE";       // 请求更新命令
    public static final String PUT_CHESS = "PUT_CHESS";     // 下棋命令
    public static final String REGRET_CHESS = "REGRET_CHESS";       // 悔棋命令
    public static final String AGREE_REGRET = "AGREE_REGRET";       // 同意悔棋命令
    public static final String SEND_CHET_MESSAGE = "SEND_CHET_MESSAGE";     // 发送聊天信息命令
    public static final String WIN = "WIN";     // 获胜命令
    public static final String SURRENDER = "SURRENDER";     // 认输命令
}

/**
 * 房间服务类<br>
 * 作为子服务端，负责处理房间内用户发来的各种请求 <br>
 */
public class RoomServer implements Serializable {

    private ServerSocket roomServerSocket = null;       // 房间服务端的 ServerSocket 用来监听用户请求
    private int roomID;     // 房间号
    private int roomPort;       // 房间的端口号
    private Model model;        // 房间内的棋局模型

    /**
     * 构造函数
     * @param roomId 房间号
     */
    public RoomServer(int roomId){
        this.roomID = roomId;
    }

    /**
     * 启动房间服务端
     * @param port 房间服务端端口号
     */
    public void startSever(int port) {
        // 如果当前房间还未开启服务，则将其初始化
        if(roomServerSocket==null){
            try {
                this.roomPort = port;
                this.roomServerSocket = new ServerSocket(roomPort);
                model = new Model();
                beginServer(); // 启动服务
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动房间服务器<br>
     * 并且启动线程持续监听房间内的用户请求
     */
    public void beginServer(){
        System.out.println("房间号:"+roomID+",端口号:"+roomPort+"启动监听");
        // 监听房间中全部用户请求，并进行相应处理
        new Thread(){
            @Override
            public void run() {
                // 对房间中的 model 上锁，防止多个用户同时请求时出现数据信息不同步的错误
                synchronized(model){
                    while(true){
                        try {
                            Socket socket = roomServerSocket.accept();      // 接受客户端的请求
                            System.out.println("收到客户端请求");
                            controlProcess(socket);     // 处理客户端请求
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * 初始化房间信息
     */
    public void init() {
        model.setWinner(Chess.SPACE);
        model.setSurrenderChessColor(Chess.SPACE);
        model.setRegretChessColor(Chess.SPACE);
    }

    /**
     * 客户端请求处理过程
     * @param socket 客户端套接字
     * @throws IOException 输入输出异常
     */
    private void controlProcess(Socket socket) throws IOException {
        System.out.println("房间服务端处理请求");
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        String acquire = in.readLine();
        System.out.println("收到客户端请求: "+ acquire);
        // 下棋请求
        // 解析请求字符串 获得 棋子的颜色 和 位置信息
        // 调用 model 的 putChess 函数，让 model 内部更新信息
        // 并将更新后的棋子栈返回给客户端
        // acquire = "PUT_CHESS:chessColor:x:y"
        if(acquire.startsWith(CommandOption.PUT_CHESS)){
            String[] info = acquire.split(":");
            model.putChess(Integer.parseInt(info[1]),Integer.parseInt(info[2]),Integer.parseInt(info[3]));
            out.writeObject(model.getChessStack());
        }
        // 检查更新请求
        // 将服务端的 model 返回给客户端
        // acquire = "CHECK_UPDATE"
        else if(acquire.startsWith(CommandOption.CHECK_UPDATE)){
            System.out.println("向客户端返回棋局信息"+model.getChessStack());
            out.writeObject(model);
        }
        // 获胜请求
        // 解析请求字符串 获得获胜方的 chessColor
        // 调用 model 的 setWinner 函数，更新获胜者信息
        // acquire = "WIN:chessColor"
        else if(acquire.startsWith(CommandOption.WIN)){
            String[] info = acquire.split(":");
            int winChessColor = Integer.parseInt(info[1]);
            System.out.println(winChessColor+"赢了");
            model.setWinner(winChessColor);
        }
        // 发送聊天信息请求
        // 解析请求字符串 获得发送者的 userName 和 发送的信息 chetStr
        // 调用 model 中的 updateChetInfo 函数更新聊天信息
        // acquire = "SEND_CHET_MESSAGE:userName:chetInfo"
        else if(acquire.startsWith(CommandOption.SEND_CHET_MESSAGE)){
            String[] info = acquire.split(":");
            String userName = info[1];
            String chetStr = info[2];
            model.updateChetInfo(userName,chetStr);
        }
        // 悔棋请求
        // 解析请求字符串 获得认输一方的棋子颜色 chessColor
        // 调用 model 中的 setRegretChessColor 函数，更新 model 中的悔棋方信息
        // acquire = "REGRET_CHESS:chessColor"
        else if(acquire.startsWith(CommandOption.REGRET_CHESS)){
            String[] info = acquire.split(":");
            int chessColor = Integer.parseInt(info[1]);
            System.out.println(chessColor+"想悔棋");
            model.setRegretChessColor(chessColor);
        }
        // 同意悔棋请求
        // 解析请求字符串 获得是否同意悔棋
        // 调用 model 中的 setAgreeRegret 函数
        // 如果同意悔棋，则执行 model 中的 regretChess 函数
        // acquire = "AGREE_REGRET:0/1";
        else if(acquire.startsWith(CommandOption.AGREE_REGRET)){
            String[] info = acquire.split(":");
            if(Integer.parseInt(info[1]) == 0){
                model.regretChess();
            }
            model.setRegretChessColor(Chess.SPACE);
        }
        // 认输请求
        // 解析请求字符串 获得认输方的棋子颜色 chessColor
        // 调用 model 中的 setSurrenderChessColor 函数，更新认输信息
        // acquire = "SURRENDER:chessColor"
        else if(acquire.startsWith(CommandOption.SURRENDER)){
            String[] info = acquire.split(":");
            int chessColor = Integer.parseInt(info[1]);
            System.out.println(chessColor+"想认输");
            model.setSurrenderChessColor(chessColor);
        }
        // 悬空处理
        else{

        }
    }


}
