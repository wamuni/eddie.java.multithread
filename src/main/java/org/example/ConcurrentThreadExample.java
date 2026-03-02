package org.example;

public class ConcurrentThreadExample {
    public ConcurrentThreadExample() {}
    public void example() throws InterruptedException {
        Thread threadA = new Thread(() -> {
            try {
                System.out.println("thread A starting now");
                Thread.sleep(2000);
                System.out.println("thread A finished");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                System.out.println("thread B starting now");
                Thread.sleep(2000);
                System.out.println("tread B finished");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // 如果需要thread按照应有的顺序来执行，那么就需要一次启动线程，然后join当前线程
        // join的作用，是为了让当前的线程等待该线程完成的结果
        // 当工作线程还没有完成工作时，当前的线程需要等待
        // 当工作线程已经结束，并且返回了结果，那么当前的线程将继续等待
        System.out.println("[1] Thread A started");
        threadA.start();
        System.out.println("[2] Main thread is waiting for Thread A to finish");
        threadA.join();
        System.out.println("[3] Thread B started");
        threadB.start();
        System.out.println("[4] Main thread is waiting for Thread B to finish");
        threadB.join();
        System.out.println("[5] Main thread finished");

    }
}
