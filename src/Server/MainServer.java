package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MainServer {

    private static MainServer instance;
    public static MainServer getInstance() {
        if(instance==null){
            instance = new MainServer();
        }
        return instance;
    }

    private static final int PORT = 8080;
    private static ServerSocket serverSocket;
    private static final int roomNum = 12;
    private static HashMap<Integer, RoomServer> roomList;
    private static ArrayList<Integer> roomMemberNum;

    private MainServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            roomList = new HashMap<Integer, RoomServer>();
            roomMemberNum = new ArrayList<Integer>();
            for(int i = 0; i < roomNum; i++){
                roomList.put(i,new RoomServer(i));
                roomMemberNum.add(0);
            }
//            roomBegined = new ArrayList<Boolean>();
//            for(int i = 0; i < 12 ; i++){
//                roomBegined.add(false);
//            }
            startThread();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startThread() {
        new Thread(){
            @Override
            public void run() {
                System.out.println("主服务端监听线程启动");
                try {
                    while(true) {
                        Socket socket = serverSocket.accept();  // 监听客户端发来的房间选择
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                                    String line = in.readLine();
                                    System.out.println("收到客户端消息:"+line);
                                    // 发送房间列表信息
                                    if(line.contains("GET_ROOM_MEMBER_NUM")){
                                        ObjectOutputStream o = new ObjectOutputStream(socket.getOutputStream());
                                        o.writeObject(roomMemberNum);
                                    }
                                    // 读取用户选择的房间号
            //                        String line = in.readLine();
                                    else if (line.startsWith("SET_ROOMID")){
                                        String[] info = line.split(":");
                                        Integer roomId = Integer.parseInt(info[1]);  // 获取房间号
                //                        if(!roomBegined.get(roomId)){
                //                            RoomServer room = new RoomServer(roomId,PORT+1+roomId);  // 创建对应房间 roomPort = 8081 + i
                //                            roomList.put(roomId, room);   // 将服务端加入roomServer
                //                            roomBegined.set(roomId,true);
                //                        }
                                        roomList.get(roomId).startSever(PORT+1+roomId);
                                        roomMemberNum.set(roomId,roomMemberNum.get(roomId)+1);
                                        int newRoomPort = roomId+PORT+1;
                                        out.println("a:"+newRoomPort);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
//                        new Thread(){
//                            @Override
//                            public void run() {
//                                try {
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }.start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}