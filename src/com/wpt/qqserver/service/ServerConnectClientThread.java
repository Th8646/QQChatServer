package com.wpt.qqserver.service;/**
 * @author wpt@onlying.cn
 * @date 2024/2/2 21:11
 */

import com.wpt.qqcommon.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        while (true){
            System.out.println("服务端和客户端保持通信，读取数据");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
