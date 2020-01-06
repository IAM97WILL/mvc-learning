package com.bailiban.socket.nioHttp.MyMVC;



import com.bailiban.socket.nioHttp.MyMVC.Model.MethodInfo;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerTest {
    //ioc容器
    public static Map<String,Object> beanMap=new HashMap<>();
    //url映射
    public static Map<String, MethodInfo> methodMap=new HashMap<>();

    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        //扫描该包,将该路径下的 类以及类的方法 放入beanmap和methodmap
        refreshBeanFactory("com.bailiban.socket.nioHttp.MyMVC");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(80));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true)
        {
            if (selector.select(3000)<=0)
            {
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                handler(next);
                iterator.remove();
            }
        }
    }

    private static void handler(SelectionKey next) throws IOException, InvocationTargetException, IllegalAccessException {
        if (next.isAcceptable())
        {
            acceptHandler(next);
        }else if (next.isReadable())
        {
            requestHandler(next);
        }
    }

    private static void requestHandler(SelectionKey key) throws IOException, InvocationTargetException, IllegalAccessException {
        SocketChannel socketChannel=(SocketChannel) key.channel();
        ByteBuffer byteBuffer=(ByteBuffer)key.attachment();
        //byteBuffer设置为 写 模式
        byteBuffer.clear();
        if (socketChannel.read(byteBuffer)==-1)
        {
            socketChannel.close();
            return;
        }
        //设置为读模式
        byteBuffer.flip();
        String requestMsg=new String(byteBuffer.array());
        String url=requestMsg.split("\r\n")[0].split(" ")[1];


        ArrayList<String> urlParams = new ArrayList<>();
        urlParamsParase(url,urlParams);

        url=url.split("\\?")[0];
        MethodInfo methodInfo = methodMap.get(url);
        String content=null;
        content=methondInvoke(url,urlParams);
        if (content == null)
            content = "404";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 200 OK\r\n");
        stringBuilder.append("Content-Type:text/html;charset=utf-8\r\n\r\n");
//        stringBuilder.append("<html><head><title>HttpTest</title></head><body>\r\n");
        stringBuilder.append(content);
//        stringBuilder.append("</body></html>");
        socketChannel.write(ByteBuffer.wrap(stringBuilder.toString().getBytes()));
        socketChannel.close();
    }

    private static String methondInvoke(String url, ArrayList<String> urlParams) throws InvocationTargetException, IllegalAccessException {
        MethodInfo methodInfo = methodMap.get(url);
        if (methodInfo==null)
        {
            return "404";
        }
        String className=methodInfo.getName();
        Method method = methodInfo.getMethod();
        Object beanObj = beanMap.get(className);
        Object[] params=new Object[urlParams.size()];
        Parameter[] parameters = method.getParameters();
        if (params.length!=parameters.length)
        {
            return "参数个数不匹配";
        }
        int i=0;
        for (Parameter p:parameters)
        {
            String type = p.getType().getSimpleName();
            String pName = p.getName();
            boolean flag=false;
            for (String p2:urlParams) {
                String pp[]=p2.split("=");
                if (pName.equals(pp[0].trim()))
                {
                    Object pValue=paramTranslate(type,pp[1]);
                    params[i++]=pValue;
                    flag=true;
                    continue;
                }
            }
            if (!flag)
            {
                return "参数名称不匹配";
            }
        }
        return (String) method.invoke(beanObj,params);
    }

    private static Object paramTranslate(String type, String s) {
        switch (type){
            case "int":
                return Integer.valueOf(s);
            case "double":
                return Double.valueOf(s);
            case "float":
                return Float.valueOf(s);
            default:
                return s;
        }
    }

    private static void urlParamsParase(String url, ArrayList<String> urlParams) {
        if (!url.contains("?"))
        {
            return;
        }
        String[] ps = url.replaceFirst(".*?\\?", "").split("&");
        for (String p:ps)
        {
            if (!p.contains("="))
            {
                continue;
            }
            urlParams.add(p);
        }
    }

    private static void acceptHandler(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(),SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }


    private static void refreshBeanFactory(String s) {
        String path = s.replace(".", "/");
        URL url = ServerTest.class.getClassLoader().getResource(path);
        File file = null;
        try {
            file = new File(URLDecoder.decode(url.getPath(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        beanParase(file);
    }

    private static void beanParase(File file) {
        //如果不是文件夹，就返回。
        if (!file.isDirectory())
            return;
        File[] files=file.listFiles(pathname -> {
            if (pathname.isDirectory())
            {
                beanParase(pathname);
                return false;
            }
            return pathname.getName().endsWith(".class");
        });
        for (File f:files) {
            String path = f.getAbsolutePath();
            String classname=path.split("classes\\\\")[1].replace("\\",".").split("\\.class")[0];
            try {
                Class<?> cls=Class.forName(classname);
                MyRestController annotation = cls.getAnnotation(MyRestController.class);
                if (annotation!=null)
                {
                    controllerParase(cls);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void controllerParase(Class<?> cls) {
        try {
            //ioc容器注入，类名和类的实例
            beanMap.put(cls.getSimpleName(),cls.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Method[] methods = cls.getDeclaredMethods();
        for (Method m:methods) {
            MyRequestMapping annotation = m.getAnnotation(MyRequestMapping.class);
            if (annotation==null)
                continue;
            String value = annotation.value();
            methodMap.put(value, new MethodInfo(m, cls.getSimpleName()));
        }
    }
}
