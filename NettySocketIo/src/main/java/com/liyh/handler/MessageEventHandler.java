package com.liyh.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.liyh.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class MessageEventHandler {

    @Autowired
    private SocketIOServer socketIoServer;

    public static ConcurrentMap<String, SocketIOClient> socketIOClientMap = new ConcurrentHashMap<>();

    /**
     * 客户端连接的时候触发
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        String mac = client.getHandshakeData().getSingleUrlParam("mac");
        // 存储SocketIOClient，用于发送消息
        socketIOClientMap.put(mac, client);
        // 回发消息
        client.sendEvent("message", "onConnect back");
        System.out.println("客户端：" + client.getSessionId() + "已连接,mac=" + mac);
    }

    /**
     * 客户端关闭连接时触发
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        System.out.println("客户端：" + client.getSessionId() + "断开连接");
    }

    /**
     * 客户端事件
     *
     * @param client  　客户端信息
     * @param request 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "messageevent")
    public void onEvent(SocketIOClient client, AckRequest request, Message data) {
        System.out.println("发来消息：" + data);
        // 回发消息
        client.sendEvent("messageevent", "你好啊，我是服务端");
        // 广播消息
        sendBroadcast();
    }

    /**
     * 广播消息
     */
    public void sendBroadcast() {
        for (SocketIOClient client : socketIOClientMap.values()) {
            if (client.isChannelOpen()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                client.sendEvent("Broadcast", "当前时间：" + simpleDateFormat.format(new Date()));
            }
        }

    }
}
