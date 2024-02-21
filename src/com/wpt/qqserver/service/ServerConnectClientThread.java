package com.wpt.qqserver.service;/**
 * @author wpt@onlying.cn
 * @date 2024/2/2 21:11
 */

import com.wpt.qqcommon.Message;
import com.wpt.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @projectName: QQServer
 * @package: com.wpt.qqserver.service
 * @className: ServerConnectClientThread
 * @author: wpt
 * @description: 和客户端保持连接通信的类
 * @date: 2024/2/2 21:11
 * @version: 1.0
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userID;

    public ServerConnectClientThread(Socket socket, String userID) {
        this.socket = socket;
        this.userID = userID;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("服务端和客户端" + userID + "保持通信，读取数据");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //根据Message的类型做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    //客户端在线用户列表

                    System.out.println(message.getSender()+"索要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回message对象
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                }else {
                    System.out.println("其他类型逻辑，带完善");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
