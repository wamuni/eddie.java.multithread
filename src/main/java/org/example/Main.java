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

        // 下面是详细说明join如何工作，详细解释进入`ConcurrentThreadExample.class` 进行查看
        ConcurrentThreadExample example = new ConcurrentThreadExample();
        try {
            example.example();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 线程的不同状态
        ThreadStateExample threadStateExample = new ThreadStateExample();
        try {
            threadStateExample.example();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 子线程不会停止工作的原因
        // 子线程并不会依赖于主线程，主线程结束不等于进程退出
        // 只要还有用户线程在运行，JVM就不会退出，直到所有的用户线程结束之后，JVM会自动退出
        // Java中的线程非为两类，用户线程和守护线程（daemon thread）
        // 在默认情况下，所有的线程都是用户线程，用来处理业务逻辑，可以称之为非守护线程
        // 守护线程，通常用于后台任务，比如日志，垃圾回收，监控等，他们不影响主线程的逻辑，但是需要在后台持续运行
        // 当所有的用户线程完成任务时，JVM会退出，守护线程在JVM退出时结束任务
        Thread daemonThread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread Daemon is running....");
        });
        // 如果将线程变成daemon thread，在主线程结束之后，程序就会立即停止，不会等daemon线程结束
        daemonThread.setDaemon(true);
        daemonThread.start();

        Thread daemonThreadLoop = new Thread(() -> {
            while(true) {
                System.out.println("Daemon Thread is running, this message is printing out every 0.5s");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        daemonThreadLoop.setDaemon(true);
        daemonThreadLoop.start();

        // 由此例子可以看出
        // 当还有用户线程在执行时，Jvm会持续运行下去
        // 当用户线程执行完之后，jvm会退出，守护线程也会同时被中断
        new Thread(() -> {
            System.out.println("This is an user thread and it's running....");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("User thread ends");
        }).start();


        // Java中可以主动停止一个线程让它继续执行任务
        // 但是Java并没有提供一个主动杀死线程的方法，但是Java提供一个中断方法，就是发生中断信号
        Thread counterThread = new Thread(() -> {
            long count = 0;
            // 线程必须主动检查中断信号，否则线程不会退出
            while(!Thread.currentThread().isInterrupted()) {
                count++;
                if (count % 1_000_000 == 0) {
                    System.out.print("\rCount: " + count);
                }
            }
            // 或者可以捕获sleep的异常InterruptException而优雅地退出
//            try {
//                while (!Thread.currentThread().isInterrupted()) {
//                    count++;
//                    System.out.println("\rCount: " + count);
//                    Thread.sleep(500);
//                }
//            } catch (InterruptedException e) {
//                System.out.println("Exit");
//            }
            System.out.println("CounterThread Interrupted");
        });

        counterThread.start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Interrupting the counter thread...");
            // 检查中断信号前的state
            System.out.println("Current Counter Thread State: " + counterThread.getState().name());
            // 调用需要中断的线程的interrupt方法，这就是在给counterThread发送中断信号
            // 线程不会立刻停止，直到该线程遇到处理中断信号的逻辑，才会根据相应的中断逻辑来判断并且中断当前线程任务
            counterThread.interrupt();
            try {
                counterThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("After Counter Thread Interrupted State " + counterThread.getState().name());
        }).start();

        System.out.println("Main Thread Finally Finished");
    }
}