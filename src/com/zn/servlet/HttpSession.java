package com.zn.servlet;

/**
 * @author 张男
 * @date: 2024/3/11---16:13
 */
public interface HttpSession {
    /**
     * 获取session的id
     *
     * @return
     */
    int getId();

    /**
     * 设置域对象session的key 和value
     *
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

    /**
     * 根据key获取对应的value
     *
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * 葛奴key删除对应的值
     *
     * @param key
     */
    void removeAttribute(String key);

    /**
     * 手动删除session(当前对象标记为过期)
     */
    void invalidate();
}
