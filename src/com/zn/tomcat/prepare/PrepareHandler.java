package com.zn.tomcat.prepare;

import com.zn.servlet.Servlet;
import com.zn.servlet.annotation.WebListener;
import com.zn.servlet.annotation.WebServlet;
import com.zn.tomcat.exception.NoServletException;
import com.zn.tomcat.listener.ListenerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
  我们作为tomcat的开发者，并不知道用户会创建多少个Servlet类
  并且每一个URI对应一个servlet类应该有一一绑定的关系，这些都是我们无法得知的
  所以我们应该运行期，通过反射得到所有被WebServlet注解修饰的类得到里面的uri和servlet绑定
  想要找到这些类，首先我们应该得到所有类的全限定类名，才能通过反射得到对应的类，进行后续的操作
 */

/**
 * 服务启动时预处理
 */
public class PrepareHandler {
    /**
     * 用于储存所有的全限定类名
     */
    private final List<String> allClasses = new ArrayList<>();

    /**
     * 用于记录URI和全限定类名的一一映射关系
     */
    public static Map<String, String> URIMapping = new HashMap<>();

    /**
     * 通过递归来获取一个路径下的所有全限定类名，并将其放入list集合中
     *
     * @param folder 外部传进来一个路径(文件对象)
     * @return 返回folder下所有类的全限定类名
     */
    public List<String> getAllClasses(File folder) {
        //如果这个folder本身是文件夹
        if (folder.isDirectory()) {
            //拿到这个folder下的所有文件
            File[] files = folder.listFiles();
            //如果folder下存在文件
            if (files != null) {
                //对里面的文件夹依次进行遍历,如果仍然是文件夹递归，如果不是得到名字
                for (File file : files) {
                    if (file.isDirectory()) {
                        //递归处理这个文件夹
                        getAllClasses(file);
                    } else if (file.getName().endsWith(".java")) {
                        //如果是.java结尾的文件(即是类),则获取类的信息
                        String className = getClassInfo(file);
                        allClasses.add(className);
                    }
                }
            }
        }
        return allClasses;
    }

    /**
     * 用于返回全限定类名
     *
     * @param file 传进来的一个类
     * @return 剥离出来的全限定类名(从src下的包开始 ， 到最终类名结束)
     */
    private String getClassInfo(File file) {
        //拿到传进来这个类所在的完整的路径
        String parent = file.getParent();
        //拿到从src下第一个包开始的路径
        String src = parent.substring(parent.indexOf("src")+4);
        //把这个路径的\换成.
        String replace = src.replace("\\", ".");
        //擦混进来所属的包+当前类名.java
        String temp = replace + "." + file.getName();
        //除掉最后的 "." 返回全限定类名
        return temp.substring(0, temp.lastIndexOf("."));
    }

    /**
     * 遍历所有的全限定类名反射出来的类
     * 检验是否有WebServlet注解，如果有将URI和这个全限定类名绑定起来，并储存起来
     * @param AllClassName 全部的类的全限定类名
     * @throws ClassNotFoundException
     */
    public Map<String,String> initURIMapping(List<String> AllClassName) throws Exception {
        for (String className : AllClassName) {
            Class<?> aClass = Class.forName(className);
            WebServlet webServletAnnotation = aClass.getAnnotation(WebServlet.class);
            WebListener webListenerAnnotation = aClass.getAnnotation(WebListener.class);
            if (webServletAnnotation != null) {
                if (Servlet.class.isAssignableFrom(aClass)) {
                    URIMapping.put(webServletAnnotation.value(), className);
                }else{
                    //如果这个类虽然有@WebServlet注解，但是不是Servlet即其子类
                    throw new NoServletException("当前类不是一个Servlet类");
                }
            }
            //如果这个类上存在@WebListener注解
            if (webListenerAnnotation != null) {
                ListenerFactory.init(aClass);
            }
        }
        return URIMapping;
    }
}
