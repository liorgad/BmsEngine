package com.danenergy.common;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Created by Lior Gad on 2/28/2017.
 */
public class EventQueue <T extends Object>
{
    private boolean stop = false;
    ConcurrentLinkedQueue<T> queue;

    Semaphore addSignal;


    Consumer<T> action;

    ExecutorService task;

    //Thread th;

    public EventQueue(Consumer<T> action)
    {
//        th = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        addSignal.acquire();
//                        if (stop) {
//                            break;
//                        }
//
//                        if (!queue.isEmpty()) {
//                            T item = queue.poll();
//                            if (null != item) {
//                                action.accept(item);
//                            }
//                        }
//
//                    }
//                } catch (Exception e) {
//
//                }
//            }} );

        this.action = action;
        queue = new ConcurrentLinkedQueue<T>();
        addSignal = new Semaphore(0);

        //th.start();

        task = Executors.newSingleThreadExecutor();

        task.execute(() -> {
            try {
                while (true) {
                    addSignal.acquire();
                    if (stop) {
                        break;
                    }

                    if (!queue.isEmpty()) {
                        T item = queue.poll();
                        if (null != item) {
                            action.accept(item);
                        }
                    }

                }
            } catch (Exception e) {

            }
        });

    }

    public void add(T item)
    {
        queue.add(item);
        addSignal.release();
    }

    public void stop()
    {
        stop = true;
        addSignal.release();
        task.shutdown();

    }

    public void dispose()
    {
        stop();
        queue.clear();
        //shutdownAndAwaitTermination(task);
    }

//    void shutdownAndAwaitTermination(ExecutorService pool) {
//        pool.shutdown(); // Disable new tasks from being submitted
//        try {
//            // Wait a while for existing tasks to terminate
//            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
//                pool.shutdownNow(); // Cancel currently executing tasks
//                // Wait a while for tasks to respond to being cancelled
//                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
//                    System.err.println("Pool did not terminate");
//            }
//        } catch (InterruptedException ie) {
//            // (Re-)Cancel if current thread also interrupted
//            pool.shutdownNow();
//            // Preserve interrupt status
//            Thread.currentThread().interrupt();
//        }
//    }

}
