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
import java.util.HashMap;
import java.util.Iterator;

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

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("服务端和客户端" + userID + "保持通信，读取数据");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //根据Message的类型做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端在线用户列表

                    System.out.println(message.getSender() + "索要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回message对象
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //返回客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMN_MES)) {
                    //根据message获取getterid，在获取对应线程
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getClientThread(message.getGetter());
                    //得到socket的对象输出流，将message对象转发给指定的客户
                    ObjectOutputStream oos =
                            new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);//客户不在线，可以保存到数据库，实现离线留言

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //遍历管理线程的集合将所有线程的socket得到后，将message转发
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        // 取出在线用户ID
                        String onLineUserId = iterator.next().toString();

                        if (!(onLineUserId.equals(message.getSender()))) {//排除群发消息的用户本人
                            //转发message
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }

                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getClientThread(message.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {//客户端退出
                    System.out.println(message.getSender() + " 退出客户端 ");
                    //将客户端对应的线程从集合中删除
                    ManageClientThreads.removeServerconnectClientThread(message.getSender());
                    socket.close();//关闭连接
                    //退出线程
                    break;
                } else {
                    System.out.println("其他类型逻辑，带完善");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
