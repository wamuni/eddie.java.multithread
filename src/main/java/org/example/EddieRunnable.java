package org.example;

public class EddieRunnable extends EddieTask implements Runnable {
    @Override
    public void run() {
        // 通过继承其他类，该类中有已经需要的business logic，那么这里可以直接在run方法中调用
        this.runTask();
    }
}
