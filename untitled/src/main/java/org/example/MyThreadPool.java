package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class MyThreadPool implements Executor {
    private final LinkedList<Runnable> queue = new LinkedList<>();
    private volatile boolean isRunning = true;
    Thread[] threads;
    private final Object lock = new Object();

    public MyThreadPool(int countThreadsPool) {
        threads = new Thread[countThreadsPool];
        for (int i = 0; i < countThreadsPool; i++) {
            threads[i] = new Thread(new MyRunnable());
            threads[i].start();
        }

        System.out.println("Потоки запущены, количество: " + countThreadsPool);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Queue<Runnable> getQueue() {
        return queue;
    }

    @Override
    public void execute(Runnable runnable) {
        if (isRunning) {
            this.queue.add(runnable);
            synchronized (queue) {
                queue.notifyAll();
            }
        }
        else throw new IllegalStateException("Очередь потоков остановлена, добавление задач невозможно");
    }

    public void shutdown() {
        isRunning = false;
        for (Thread t:threads) {
            t.interrupt();
        }
    }

    public class MyRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning | !queue.isEmpty()) {
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " ждем");
                            queue.wait();
                        } catch (InterruptedException e) {
                            //throw new RuntimeException(e);
                            Thread.currentThread().interrupt();
                        }
                    }

                    Runnable nextTask = null;
                    nextTask = queue.poll();

                    if (nextTask != null) {
                        System.out.println("Executing: " + Thread.currentThread().getName());
                        nextTask.run();
//                            if (queue.isEmpty()) {
//                                break;
//                            }
                    }
                }
            }
        }
    }

    //    public Future<?> submit(Runnable task) {
    //        RunnableFuture<?> future = new FutureTask<>(task, null);
    //        queue.add(future);
    //        return future;
    //    }
//    boolean awaitTermination(long timeout, TimeUnit unit)
//            throws InterruptedException {
//        // тут надо останавливать без таймаута
//    }
}