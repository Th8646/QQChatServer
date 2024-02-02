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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
    //创建集合，存放多个用户，如果登录认为合法的
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static {//在静态代码块，初始化users
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("至尊宝", new User("至尊宝", "123456"));
        validUsers.put("紫霞仙子", new User("紫霞仙子", "123456"));
        validUsers.put("奔波霸", new User("奔波霸", "123456"));

    }

    //验证用户是否有效的方法
    private boolean checkUser(String userID, String passwd) {
        User user = validUsers.get(userID);
        if (user == null) {//说明userID没有存在validUser中
            return false;
        }
        if (!(user.getPasswd().equals(passwd))) {
            return false;
        }
        return true;
    }

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

                if (checkUser(user.getUserID(),user.getPasswd())) {
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
                    System.out.println("用户" + user.getUserID() + "登录失败");
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
