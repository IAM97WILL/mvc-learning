package com.bailiban.socket.nioHttp.MyMVC;


import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {

//    public static Map<String,String> contentMap=new HashMap<>();

    //路径以及该路径对应的方法
    public static Map<String,Method> methodMap=new HashMap<>();
//    static {
//        contentMap.put("/index","welcome to newyork");
//        contentMap.put("/","welcome to newyork");
//        contentMap.put("/hello","from the other side");
//    }

    @MyRequestMapping("/hello")
    public String Hello(){
        return "hello it's me !";
    }

    @MyRequestMapping("/")
    public String Index(){
        return "heyheyhey !";
    }
    static {
        Class<Controller> controllerClass=Controller.class;
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method m:methods) {
            MyRequestMapping annotation = m.getAnnotation(MyRequestMapping.class);
            methodMap.put(annotation.value(),m);
        }
    }

}
