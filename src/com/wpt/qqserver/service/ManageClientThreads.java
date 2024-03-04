package com.wpt.qqserver.service;/**
 * @author wpt@onlying.cn
 * @date 2024/2/2 21:29
 */

import java.util.HashMap;
import java.util.Iterator;

/**
 * @projectName: QQServer
 * @package: com.wpt.qqserver.service
 * @className: ManageClientThreads
 * @author: wpt
 * @description: 用于管理和客户端通讯的线程
 * @date: 2024/2/2 21:29
 * @version: 1.0
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap();

    //添加线程对象到hm集合
    public static void addClientThread(String userID, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userID, serverConnectClientThread);
    }

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static ServerConnectClientThread getClientThread(String userID) {
        return hm.get(userID);
    }

    //返回在线用户列表
    public static String getOnlineUser() {
        //集合遍历，遍历hashmap的key
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";

        }
        return onlineUserList;
    }

    //从集合中删除某个线程对象
    public static void removeServerconnectClientThread(String userId) {
        hm.remove(userId);
    }
}
