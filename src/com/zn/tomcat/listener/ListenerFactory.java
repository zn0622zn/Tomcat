package com.zn.tomcat.listener;

import com.zn.servlet.HttpServletRequest;
import com.zn.servlet.HttpSession;
import com.zn.servlet.ServletContext;
import com.zn.servlet.listener.*;
import com.zn.tomcat.exception.NoListenerException;

/**
 * @author 张男
 * @date: 2024/3/12---17:01
 */
public class ListenerFactory {
    private static Class<ServletRequestListener> requestListenerClass;
    private static Class<ServletRequestAttributeListener> requestAttributeListenerClass;
    private static Class<HttpSessionListener> sessionListenerClass;
    private static Class<HttpSessionAttributeListener> sessionAttributeListenerClass;
    private static Class<ServletContextListener> servletContextListenerClass;
    private static Class<ServletContextAttributeListener> servletContextAttributeListenerClass;

    //用于标记是否有lisener的实现
    private static boolean notListener = true;

    public static void init(Class aClass) throws NoListenerException {
        if (ServletRequestListener.class.isAssignableFrom(aClass)) {
            requestListenerClass = aClass;
            notListener = false;
        }
        if (HttpSessionListener.class.isAssignableFrom(aClass)) {
            sessionListenerClass = aClass;
            notListener = false;
        }
        if (ServletContextListener.class.isAssignableFrom(aClass)){
            servletContextListenerClass = aClass;
            notListener = false;
        }
        if (ServletRequestAttributeListener.class.isAssignableFrom(aClass)) {
            requestAttributeListenerClass = aClass;
            notListener = false;
        }
        if (HttpSessionAttributeListener.class.isAssignableFrom(aClass)) {
            sessionAttributeListenerClass = aClass;
            notListener = false;
        }
        if (ServletContextAttributeListener.class.isAssignableFrom(aClass)) {
            servletContextAttributeListenerClass = aClass;
            notListener = false;
        }

        if (notListener) {
            throw new NoListenerException("此类不是监听器类型");
        }
    }

    public static Listener getListener(Object o) throws InstantiationException, IllegalAccessException {
        if (o instanceof HttpServletRequest && requestListenerClass != null) {
            return requestListenerClass.newInstance();
        }

        if (o instanceof HttpSession && sessionListenerClass != null) {
            return sessionListenerClass.newInstance();
        }

        if (o instanceof ServletContext && servletContextListenerClass != null) {
            return servletContextListenerClass.newInstance();
        }
        return new TomcatListener();
    }

    public static Listener getAttributeListener(Object o) throws InstantiationException, IllegalAccessException {
        if (o instanceof HttpServletRequest && requestAttributeListenerClass != null) {
            return requestAttributeListenerClass.newInstance();
        }

        if (o instanceof HttpSession && sessionAttributeListenerClass != null) {
            return sessionAttributeListenerClass.newInstance();
        }

        if (o instanceof ServletContext && servletContextAttributeListenerClass != null) {
            return servletContextAttributeListenerClass.newInstance();
        }

        return new TomcatListener();
    }
}
