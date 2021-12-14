package Server;

import DataBase.DataBase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MainServer {

    private static MainServer instance;

    public static MainServer getInstance() {
        if (instance == null) {
            instance = new MainServer();
        }
        return instance;
    }

    private static final int PORT = 8080;       // 主服务器端口 8080
    private static ServerSocket serverSocket;       // 主服务器 ServerSocket 用来监听客户端请求
    private static final int roomNum = 12;      // 初始房间数量
    private static HashMap<Integer, RoomServer> roomList;       // 房间列表
    private static ArrayList<Integer> roomMemberNum;        // 房间内人数列表

    /**
     * 构造函数:<br>
     * 在构造函数内对 serverSocket roomList roomMemberNum 进行初始化赋值<br>
     * 启动主服务器线程，持续接受客户端请求
     */
    private MainServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            roomList = new HashMap<Integer, RoomServer>();
            roomMemberNum = new ArrayList<Integer>();
            for (int i = 0; i < roomNum; i++) {
                roomList.put(i, new RoomServer(i));
                roomMemberNum.add(0);
            }
            startThread();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 主服务端线程启动:<br>
     * 单独拉起一条线程不断接受客户端的请求:<br>
     *     1. 客户端请求房间列表信息 <br>
     *     2. 客户端选择房间，并获取对应房间的端口号 <br>
     */
    private void startThread() {
        new Thread() {
            @Override
            public void run() {
                System.out.println("主服务端监听线程启动");
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();  // 监听客户端发来的房间选择
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                    String line = in.readLine();
                                    System.out.println("收到客户端消息:" + line);
                                    // 发送房间列表信息  请求为 "GET_ROOM_MEMBER_NUM"
                                    if (line.contains("GET_ROOM_MEMBER_NUM")) {
                                        ObjectOutputStream o = new ObjectOutputStream(socket.getOutputStream());
                                        o.writeObject(roomMemberNum);
                                    }
                                    // 读取用户的注册验证  请求为 "REGISTER_VERIFY:username:password"
                                    else if(line.startsWith("REGISTER_VERIFY")){
                                        String[] info = line.split(":");
                                        String username = info[1];
                                        String password = info[2];
                                        if(DataBase.getInstance().registerVerify(username,password)){
                                            out.println("TRUE");
                                        } else {
                                            out.println("FALSE");
                                        }
                                    }
                                    // 读取用户的登录验证 请求为 "LOGIN_VERIFY:username:password"
                                    else if(line.startsWith("LOGIN_VERIFY")){
                                        String[] info = line.split(":");
                                        String username = info[1];
                                        String password = info[2];
                                        if(DataBase.getInstance().loginVerify(username, password)){
                                            out.println("TRUE");
                                        }else{
                                            out.println("FALSE");
                                        }
                                    }
                                    // 读取用户选择的房间号  请求为 "SET_ROOMID:房间号"
                                    else if (line.startsWith("SET_ROOMID")) {
                                        String[] info = line.split(":");
                                        Integer roomId = Integer.parseInt(info[1]);  // 获取房间号
                                        roomList.get(roomId).startSever(PORT + 1 + roomId);
                                        roomMemberNum.set(roomId, roomMemberNum.get(roomId) + 1);
                                        int newRoomPort = roomId + PORT + 1;
                                        out.println("a:" + newRoomPort);
                                    }
                                    // 读取用户退出房间  请求为 "EXIT:房间号"
                                    else if (line.startsWith("EXIT")) {
                                        String[] info = line.split(":");
                                        int roomId = Integer.parseInt(info[1]);
                                        roomMemberNum.set(roomId, roomMemberNum.get(roomId) - 1);
                                        roomList.get(roomId).init();
                                    }
                                    // 悬空处理
                                    else{

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}