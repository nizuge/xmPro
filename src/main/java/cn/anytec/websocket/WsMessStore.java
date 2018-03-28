package cn.anytec.websocket;

import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

public class WsMessStore {
    private static final Logger logger = Logger.getLogger(WsMessStore.class);

    private static volatile Map<String,WebSocketSession> sessionsMap = new HashMap<>();
    private static List<Object> message = new ArrayList<Object>();
    private Thread pushMessageThread;

    private static WsMessStore instance;
    public static WsMessStore getInstance() {    //对获取实例的方法进行同步
        if (instance == null) {
            synchronized (WsMessStore.class) {
                if (instance == null)
                    instance = new WsMessStore();
            }
        }
        return instance;
    }




    public void addSession(WebSocketSession session){

        sessionsMap.put(session.getId(),session);
    }

    public WebSocketSession getSession(String sessionId){

        return sessionsMap.get(sessionId);
    }
    public void removeSession(WebSocketSession session){
        logger.debug("removeWebSocketSession");
        sessionsMap.remove(session.getId());
    }
    public void addMessage(Object data){

        synchronized (this){
            message.add(data);
            logger.debug("添加一条数据成功，唤醒推送线程");
            this.notifyAll();
        }

    }
    public void startPushMessThread(){
        if(null!=pushMessageThread&&pushMessageThread.isAlive()){
            logger.debug("推送线程活跃中。。。");
            return;
        }
        pushMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    pushMessage();
                }
            }
        });

        pushMessageThread.start();

        logger.debug("推送线程启动，开始推送数据");
    }

    public void pushMessage(){

        try {
            synchronized (this) {
                while (message.size() == 0) {
                    try {
                        logger.debug("没有数据推送，等待中。。。");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Object object = message.get(0);
                if(object instanceof byte[]){
                    String data = Base64.getEncoder().encodeToString((byte[])object);
                    WebSocketSession webSocketSession = sessionsMap.get("0");
                    if(webSocketSession == null)
                        return;
                    webSocketSession.sendMessage(new TextMessage(data));
                    message.remove(0);
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }





}
