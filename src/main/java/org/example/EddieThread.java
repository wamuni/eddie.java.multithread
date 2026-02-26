package org.example;
// 可以选择创建自己的线程，只需要extends Thread类
// 并且将run() 方法进行override
public class EddieThread extends Thread {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is running...");
    }
}
