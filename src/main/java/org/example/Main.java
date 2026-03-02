package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
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
        // 这样的好处就是
        // 1. EddieRunnable类可以继承其他的类，好处就是可以直接调用该类中的方法
        // 2. 这样具有极高的拓展性和代码重复利用率
        Thread runnableThread = new Thread(new EddieRunnable(), "EddieRunnable Thread 01");
        runnableThread.start();

        // 在Java中也可以是用lambda表达式来实现，因为runnable是一个函数时接口 FunctionalInterface
        Thread lambdaThread = new Thread(() ->
                System.out.println(Thread.currentThread().getName() + " is running...")
                , "Lambda Thread");
        lambdaThread.start();

        // 线程是异步执行的，线程之间互不干扰，线程不会因为其他线程完成并且停止工作而停止
        new Thread(() -> {
            try {
                System.out.println("Thread 1 is initialized");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 1 has executed");
        }, "Thread 1").start();

        new Thread(() -> {
            try {
                System.out.println("Thread 2 is initialized");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 2 has executed");
        }, "Thread 2").start();

        // 异步执行并不是在任何情况下都是完美无缺的
        // 在一个线程依赖另一个线程的结果作为基础时，如果不加任何的约束，那么就会出现抢跑的情况
        // 在这用情况下，可以使用join来对有依赖的线程进行管理
        // 当线程A调用了线程B的join时，线程A会被阻塞，直到B执行完毕
        Thread blockingThread = new Thread(() -> {
            try {
                System.out.println("blocking thread is executing");
                Thread.sleep(2000);
                System.out.println("blocking thread finish the job");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

//        blockingThread.start();
//        System.out.println("blocking thread started");
//        System.out.println("Main thread is waiting for blocking Thread to be finished");
//        // 在main thread中调用blockingThread的join方法，就会阻断main线程
//        blockingThread.join();
//        System.out.println("Main thread is executed");

        new Thread(() -> {
            System.out.println("Thread - A is running....");
            blockingThread.start();
            // 这里会阻断Thread A，直到blockingThread结束
            try {
                blockingThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread - A finished");
        }).start();

        ConcurrentThreadExample example = new ConcurrentThreadExample();
        example.example();
    }
}