package com.candy.first;
import com.sun.org.apache.xml.internal.security.Init;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MotherBoard {
    private static Properties p = new Properties(); //Properties是对配置文件的映射

    //储存安装的所有插件
    private static Map<String,IUSB> plugins = new HashMap<>();


    //静态代码用于加载配置文件,之后有新的类，只要在配置文件中加一个名字就可以了
    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            //从classpath的根路径去寻找 plugins.properties
            InputStream inStream = loader.getResourceAsStream("plugins.properties");
            init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //创建plugins.properties中配置的插件对象，并把插件对象储存起来
    private static void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Set<Object> keys = p.keySet();
        for (Object key : keys){
            String name = (String) key; //获取key
            String className = p.getProperty(name); //获取value



            //这就是反射，用类名直接创建了一个对象
            Object obj = Class.forName(className).newInstance();    //这里使用了反射，保证所有类都有公共无参数的构造器

            //判断当前对象是否实现了 IUSB 规范
            if (!(obj instanceof IUSB)){
                throw new  RuntimeException(name+"没有遵守IUSB规范");
            }
            plugins.put(name,(IUSB) obj);
        }
    }

     public static void work(){
        for (IUSB plugin : plugins.values()){
            plugin.swapData();
        }
     }

    public static void install(IUSB m){ //面向接口编程
        System.out.println("安装"+m.getClass().getSimpleName());  //当前对象字节码的简单名称
        m.swapData();
    }

    public static void main(String[] args){
        System.out.println(plugins);
    }
}
