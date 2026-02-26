package org.example;

public class Main {
    public static void main(String[] args) {
        //通过Thread类中的静态方法，可以得到现在所在线程的名称，线程的名称会自己分
        //比如Thread01， Thread02， Main等等
        //Main线程是Jvm在启动时，默认的初始线程
        //Thread类提供setName方法，可以对名字进行更该，在创建自己的线程或者debug时方便区分
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName());

        //Java允许创建自己的线程，只需要extends Thread类就可以
        //非常直观，但是这样会有很多的限制，适合小型的任务
        //1. Java不提供多种继承，意味着如果Thread想继承其他类，那就是不可能的，扩展性比较差
        //2. 任务逻辑被写在类中，缺乏灵活性
        Thread newThread = new EddieThread();
        newThread.setName("Eddie Thread 01");
        //启动该线程
        newThread.start();

        // 在Java中，可以直接提供Runnable类来
    }
}