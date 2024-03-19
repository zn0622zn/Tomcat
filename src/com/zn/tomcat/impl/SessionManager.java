package com.zn.tomcat.impl;

import com.zn.servlet.HttpSession;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 张男
 * @date: 2024/3/11---19:16
 */
public class SessionManager {
    /**
     * sessionId 生成器
     */
    public static AtomicInteger SessionId = new AtomicInteger(0);

    /**
     * 用于存放sessionId---Session的容器
     */
    public static Map<Integer, HttpSession> sessionContainer = new ConcurrentHashMap<>();

    public static SessionProAndCon sessionProAndCon = new SessionProAndCon(new ArrayBlockingQueue<>(200));

    /**
     * 类初始化代码块，生成字节码文件的时候最先执行(服务启动时执行)
     */
    static {
        //开启生产者后台线程
        new Thread(() -> {
            try {
                sessionProAndCon.doProduct();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        //开启消费者后台线程
        new Thread(() -> {
            try {
                sessionProAndCon.doConsume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    for (Integer sessionId : sessionContainer.keySet()) {
                        if (((TomcatSession) sessionContainer.get(sessionId)).getTtlMark()) {
                            sessionContainer.remove(sessionId);
                        }
                    }
                    //每隔一段时间将session容器中过期的内容删除
                    Thread.sleep(20000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static HttpSession getSession(Integer sessionId) {
        TomcatSession httpSession = (TomcatSession) sessionContainer.get(sessionId);
        if (httpSession != null) {
            //虽然容器中拿到了session，但是被标记为了过期
            if (httpSession.getTtlMark()) {
                return null;
            }
            //惰性检查 检查此时要拿的这个session是否超过过期时间
            long nowTime = System.currentTimeMillis();
            //如果当前时间-session的创建时间>=过期时间，将这个session标记为过期状态
            if (nowTime - httpSession.getCreateTime() >= httpSession.getTtl()) {
                httpSession.getListener().destroyed(httpSession.getHttpSessionEvent());
                httpSession.setTtlMark(true);
                return null;
            }
            //否则证明容器中有session，并且也未过期，对session对象续期
            //直接将这个session的创建时间修改未现在的时间即可
            httpSession.setCreateTime(System.currentTimeMillis());
        }
        return httpSession;
    }

    /**
     * 创建新的session
     *
     * @return
     */
    public static HttpSession initAndGetSession(){
        //将session对象new出来
        TomcatSession tomcatSession = null;
        try {
            tomcatSession = new TomcatSession();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //将新建的session存入容器
        sessionContainer.put(tomcatSession.getId(), tomcatSession);
        return tomcatSession;
    }
}
