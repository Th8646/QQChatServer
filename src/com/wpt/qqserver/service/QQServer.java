package com.wpt.qqserver.service;/**
 * @author wpt@onlying.cn
 * @date 2024/2/2 20:57
 */

import com.wpt.qqcommon.Message;
import com.wpt.qqcommon.MessageType;
import com.wpt.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @projectName: QQServer
 * @package: com.wpt.qqserver.service
 * @className: QQServer
 * @author: wpt
 * @description: TODO
 * @date: 2024/2/2 20:57
 * @version: 1.0
 */
public class QQServer {
    private ServerSocket ss = null;

    public QQServer() {
        try {
            System.out.println("服务器在9999端口监听");
            ss = new ServerSocket(9999);

            while (true) {//当和某个客户端建立链接后，应该继续监听，因此使用while循环
                Socket socket = ss.accept();
                //得到socket关联的对象输入刘
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //得到socket关联的对象输出流，用于回复消息
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User user = (User) ois.readObject();
                //构建Message对象，准备回复客户端
                Message message = new Message();

                if (user.getUserID().equals("100") && user.getPasswd().equals("123456")) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复给客户端
                    oos.writeObject(message);
                    //创建线程，和服务器保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, user.getUserID());
                    serverConnectClientThread.start();
                    //使用集合统一管理集合
                    ManageClientThreads.addClientThread(user.getUserID(), serverConnectClientThread);
                } else {
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            //如果服务器退出了while，服务器不在监听，因此需要关闭资源
            try {
                ss.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
