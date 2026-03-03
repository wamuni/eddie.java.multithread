package org.example;

import java.time.LocalDateTime;

/*
* 该类是为了帮助了解thread的不同阶段的状态来举例说明的
* Thread中的状态有以下几种
* 1. NEW
* 2. RUNNABLE
* 3. BLOCKED
* 4. WAITING
* 5. TIMED_WAITING
* 6. TERMINATED
* */
public class ThreadStateExample implements Example {
    public ThreadStateExample() {}

    @Override
    public void example() {
        Thread thread = new Thread(() -> {
           try {
               Thread.sleep(2000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
        });

        printState(thread.getState());
        thread.start();
        printState(thread.getState());

        Thread.State preState = null;
        while (thread.isAlive()) {
            Thread.State currentState = thread.getState();
            if (currentState != preState) {
                printState(currentState);
                preState = currentState;
            }
        }
        printState(thread.getState());
    }

    private void printState(Thread.State currentState) {
        System.out.println(LocalDateTime.now() + " - Current Thread [" + currentState.name() + "]");
    }
}
