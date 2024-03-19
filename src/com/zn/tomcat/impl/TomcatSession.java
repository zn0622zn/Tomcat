package com.zn.tomcat.impl;

import com.zn.servlet.HttpSession;
import com.zn.servlet.listener.HttpSessionAttributeEvent;
import com.zn.servlet.listener.HttpSessionAttributeListener;
import com.zn.servlet.listener.HttpSessionEvent;
import com.zn.servlet.listener.HttpSessionListener;
import com.zn.tomcat.listener.ListenerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张男
 * @date: 2024/3/11---18:04
 */
public class TomcatSession implements HttpSession {

    /**
     * Session存值的Map
     */
    private Map<String, Object> attributeMap;

    /**
     * session的唯一标识
     */
    private Integer id;

    /**
     * 第一次getSession时 这个属性就是session对象的创建时间
     * 后续请求再获取session时 会给这个创建时间续期 它的含义其实就变成了最后一次使用时间
     */
    private Long createTime;
    /**
     * 过期时间
     */
    private Long ttl;

    /**
     * 当前这个session是否是过期的标记符
     */
    private Boolean ttlMark;

    /**
     * session的默认过期销毁时间是30min
     */
    private final static Long DEFAULT_SESSION_TTL = 1800000L;

    private HttpSessionListener listener;

    private HttpSessionEvent httpSessionEvent;
    private HttpSessionAttributeListener attributeListener;

    public TomcatSession() throws InstantiationException, IllegalAccessException {
        this.id = SessionManager.SessionId.getAndIncrement();
        this.createTime = System.currentTimeMillis();
        this.ttl = DEFAULT_SESSION_TTL;
        this.ttlMark = false;
        this.attributeMap = new HashMap<>();

        this.httpSessionEvent = new HttpSessionEvent(this);
        this.listener = (HttpSessionListener) ListenerFactory.getListener(this);
        if (this.listener != null) {
            this.listener.initSession(this.httpSessionEvent);
        }

        this.attributeListener = (HttpSessionAttributeListener) ListenerFactory.getAttributeListener(this);
    }

    public HttpSessionListener getListener() {
        return listener;
    }

    public HttpSessionEvent getHttpSessionEvent() {
        return httpSessionEvent;
    }

    public HttpSessionAttributeListener getAttributeListener() {
        return attributeListener;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(Map<String, Object> attributeMap) {
        this.attributeMap = attributeMap;
    }

    @Override
    public void setAttribute(String key, Object value) {
        HttpSessionAttributeEvent httpSessionAttributeEvent;
        for (String temp : this.attributeMap.keySet()) {
            if (temp.equals(key)) {
                httpSessionAttributeEvent = new HttpSessionAttributeEvent(this, temp, this.attributeMap.get(temp));
                this.attributeListener.updateAttribute(httpSessionAttributeEvent);
                this.attributeMap.put(key, value);
                return;
            }
        }
        httpSessionAttributeEvent = new HttpSessionAttributeEvent(this, key, value);
        this.attributeListener.addAttribute(httpSessionAttributeEvent);
        this.attributeMap.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return this.attributeMap.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        HttpSessionAttributeEvent httpSessionAttributeEvent = new HttpSessionAttributeEvent(this, key, this.attributeMap.get(key));
        this.attributeListener.removeAttribute(httpSessionAttributeEvent);
        this.attributeMap.remove(key);
    }

    @Override
    public void invalidate() {
        this.listener.destroyed(this.httpSessionEvent);
        this.ttlMark = true;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public Boolean getTtlMark() {
        return ttlMark;
    }

    public void setTtlMark(Boolean ttlMark) {
        this.ttlMark = ttlMark;
    }
}
