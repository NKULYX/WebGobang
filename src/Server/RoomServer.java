package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


class CommandOption{
    public static final String PUT_CHESS = "PUT_CHESS";
    public static final String REGRET_CHESS = "REGRET_CHESS";
    public static final String CHECK_UPDATE = "CHECK_UPDATE";
    public static final String WIN = "WIN";
    public static final String SEND_CHET_MESSAGE = "SEND_CHET_MESSAGE";
}

public class RoomServer implements Serializable {

//    private static int roomMemberNum = 0;
//
//    public static int getRoomMemberNum() {
//        return roomMemberNum;
//    }
//
//    public static void setRoomMemberNum(int roomMemberNum) {
//        RoomServer.roomMemberNum = roomMemberNum;
//    }



    private ServerSocket roomServerSocket = null;
    private int roomID;
    private int roomPort;

    public RoomServer(int roomId){
        this.roomID = roomId;
    }

//    public RoomServer(int roomId, int roomPort) {
//        try {
//            this.roomID = roomId;
//            this.roomPort = roomPort;
//            this.roomServerSocket = new ServerSocket(roomPort);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void startSever(int port) {
        if(roomServerSocket==null){
            try {
                this.roomPort = port;
                this.roomServerSocket = new ServerSocket(roomPort);
                beginServer();
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
        /**
         * 监听房间内所有的用户请求并进行处理
         */
        new Thread(){
            @Override
            public void run() {
                synchronized(Model.getInstance()){
                    while(true){
                        try {
                            Socket socket = roomServerSocket.accept();
                            System.out.println("收到客户端请求");
                            controlProcess(socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * 客户端请求处理过程
     * @param socket 客户端套接字
     * @throws IOException
     */
    private void controlProcess(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        String acquire = in.readLine();
        System.out.println("收到客户端请求: "+ acquire);

        // acquire = "PUT_CHESS:chessColor:x:y"
        if(acquire.startsWith(CommandOption.PUT_CHESS)){
            String[] info = acquire.split(":");
            Model.getInstance().putChess(Integer.parseInt(info[1]),Integer.parseInt(info[2]),Integer.parseInt(info[3]));
            out.writeObject(Model.getInstance().getChessStack());
        }
        // acquire = "REGRET_CHESS"
        else if(acquire.startsWith(CommandOption.REGRET_CHESS)) {
            Model.getInstance().regretChess();
        }
        // acquire = "CHECK_UPDATE"
        else if(acquire.startsWith(CommandOption.CHECK_UPDATE)){
            System.out.println("向客户端返回棋局信息"+Model.getInstance().getChessStack());
            out.writeObject(Model.getInstance());
        }
        // acquire = "WIN:chessColor"
        else if(acquire.startsWith(CommandOption.WIN)){
            String[] info = acquire.split(":");
            int winChessColor = Integer.parseInt(info[1]);
            System.out.println(winChessColor+"赢了");
            Model.getInstance().setWinner(winChessColor);
        }
        // acquire = "SEND_CHET_MESSAGE:userName:chetInfo"
        else if(acquire.startsWith(CommandOption.SEND_CHET_MESSAGE)){
            String[] info = acquire.split(":");
            String userName = info[1];
            String chetStr = info[2];
            Model.getInstance().updateChetInfo(userName,chetStr);
        }
    }




}
