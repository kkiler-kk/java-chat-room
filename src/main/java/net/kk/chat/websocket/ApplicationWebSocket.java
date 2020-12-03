package net.kk.chat.websocket;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kk.chat.utils.MessageUtil;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.kk.chat.entity.Message;

/**
 * @author KK
 */
@ServerEndpoint(value = "/websocket")
@Component
public class ApplicationWebSocket {
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static final Map<String, ApplicationWebSocket> webSocket = new ConcurrentHashMap<>(); //存储在线人数
    private Session session;
    private String sendName;

    @OnOpen
    public void onOpen(Session session) {
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        this.session = session;
        //重新把聊天列表推送给客户端
        sendMessNames();
    }

    @OnMessage
    public void onMessage(String text) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Message message = mapper.readValue(text, Message.class);
            String type = message.getType();
            Method method = this.getClass().getDeclaredMethod(type, Message.class);
            method.invoke(this, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @OnClose
    public void onClose() {
        System.out.println(sendName + "退出了聊天室!");
        if(sendName != null){
            ApplicationWebSocket.webSocket.remove(sendName);
        }
        subOnlineCount();
        //重新把聊天列表推送给客户端
        sendMessNames();
        StringBuilder stringBuilder = new StringBuilder();
    }

    public Set<String> getNames() {
        return webSocket.keySet();
    }

    public void sendMessageAll(String jsonMessage)  {
        try {
            Set<String> names = getNames();
            for (String name : names) {
                ApplicationWebSocket.webSocket.get(name).session.getBasicRemote().sendText(jsonMessage);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageAll( Message message) {
        message.setText(message.getText().replaceAll("\n", "<br>"));
        String jsonMessage = JSON.toJSONString(message);
        sendMessageAll(jsonMessage);
    }

    public void sendMessage(Message message) throws IOException {
        message.setText(message.getText().replaceAll("\n", "<br>"));
        String jsonMessage = JSON.toJSONString(message);
        ApplicationWebSocket.webSocket.get(message.getReceiveName()).session.getBasicRemote().sendText(jsonMessage);
    }

    public void setting(Message message) {
        if(Objects.equals(null, message.getSendName())) return;
        this.sendName = message.getSendName();
        ApplicationWebSocket.webSocket.put(this.sendName, this);
        sendMessNames();
    }
    public void sendMessNames(){
        String json = MessageUtil.getMessNames(getNames());
        sendMessageAll(json);
    }
    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        ApplicationWebSocket.onlineCount.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        ApplicationWebSocket.onlineCount.getAndDecrement();
    }
}
