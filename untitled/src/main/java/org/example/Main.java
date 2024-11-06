package org.example;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        // Создадим и запустим 3 исполняющих потока
        MyThreadPool myThreadPool = new MyThreadPool(3);

        // создадим 10 задач и добавим их в очередь нашего myThreadPool
        for (int i = 0; i < 10; i++) {
            Runnable task = () -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Задача исполнена");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };

            System.out.println("Задачу " + i + " ставим в очередь на исполнение");
            myThreadPool.execute(task);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // остановим исполнение
        myThreadPool.shutdown();

        // Эти задачи в пул уже не попадут, т.к. был shutdown
        for (int i = 0; i < 1; i++) {
            Runnable task = () -> {
                try {
                    Thread.sleep(100);
                    System.out.println("Задача исполнена");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };

            System.out.println("Задачу " + i + " ставим в очередь на исполнение");
            myThreadPool.execute(task);
        }

    }
}