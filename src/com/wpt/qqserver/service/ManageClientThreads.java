package com.wpt.qqserver.service;/**
 * @author wpt@onlying.cn
 * @date 2024/2/2 21:29
 */

import java.util.HashMap;

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

    public static ServerConnectClientThread getClientThread(String userID) {
        return hm.get(userID);
    }
}
