package com.zn.tomcat.impl;

import com.zn.servlet.HttpSession;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * @author 张男
 * @date: 2024/3/11---19:46
 */
public class SessionProAndCon {

    /**
     * 用于退出循环
     */
    public volatile boolean flag = true;

    private BlockingQueue<Integer> blockingQueue;

    public SessionProAndCon(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    /**
     * 生产者
     */
    public void doProduct() throws InterruptedException {
        while (this.flag) {
            Random random = new Random();
            Set<Integer> sessionIdSet = SessionManager.sessionContainer.keySet();
            Integer[] sessionIdArr = sessionIdSet.toArray(new Integer[0]);
            //每次随机取样的个数
            int checkNumber;
            //
            int scheduledTime;
            int threshold = sessionIdArr.length % 100;
            switch (threshold) {
                case 0:
                    checkNumber = 10;
                    scheduledTime = 500;
                case 1:
                    checkNumber = 20;
                    scheduledTime = 750;
                case 2:
                    checkNumber = 30;
                    scheduledTime = 1000;
                case 3:
                    checkNumber = 40;
                    scheduledTime = 1250;
                case 4:
                    scheduledTime = 1500;
                default:
                    checkNumber = 100;
                    scheduledTime = 3000;
            }
            if (sessionIdArr.length > 0) {
                for (int i = 0; i < checkNumber; i++) {
                    int temp = random.nextInt(sessionIdArr.length);
                    Integer sessionIdIndex = sessionIdArr[temp];
                    this.blockingQueue.put(sessionIdIndex);
                }
            }
            Thread.sleep(scheduledTime);
        }
    }

    public void doConsume() throws InterruptedException {
        Integer sessionId;
        long currentTime;
        while (this.flag) {
            currentTime = System.currentTimeMillis();
            sessionId = this.blockingQueue.take();
            TomcatSession httpSession = (TomcatSession) SessionManager.sessionContainer.get(sessionId);
            if ((currentTime - httpSession.getCreateTime()) >= httpSession.getTtl() && !httpSession.getTtlMark()) {
                httpSession.setTtlMark(true);
                httpSession.getListener().destroyed(httpSession.getHttpSessionEvent());
            }
        }
    }

}
