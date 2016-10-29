package ru.ifmo.ctddev.krasnotsvetov.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

/**
 * Created by Krasnotsvetov on 28.03.2016.
 */
public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threads;
    private final TaskQueue queue = new TaskQueue();

    /**
     * Create ParallelMapper.
     * Create ParalleMapper with {@code count} number of threads.
     *
     * @param count number of threads
     */
    public ParallelMapperImpl(int count) {
        threads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            threads.add(new Thread(new Executor(queue)));
            threads.get(i).start();
        }
    }


    /**
     * Apply function to all elements in list and return result.
     *
     * @param f    function which applies to ele,ents
     * @param args list to apply
     * @return list with result
     * @throws InterruptedException
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<Task<T, R>> tasks = new ArrayList<>();
        args.forEach(t -> tasks.add(queue.add(f, t)));
        List<R> result = new ArrayList<>();
        tasks.forEach(t -> result.add(t.getResult()));
        return result;
    }

    /**
     * Stop all threads
     *
     * @throws InterruptedException
     */
    @Override
    public void close() throws InterruptedException {
        threads.stream().forEach(Thread::interrupt);
    }

    private class Executor implements Runnable {
        private final TaskQueue queue;

        /**
         * Create runnable object which use in thread
         *
         * @param queue
         */
        public Executor(TaskQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Task task;
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    task = queue.poll();
                }
                task.run();
                //   Thread.currentThread().interrupt();
            }
        }
    }

    private class Task<T, R> {
        private Function<? super T, ? extends R> function;
        private T arg;
        private R result;
        private boolean finish;

        public Task(Function<? super T, ? extends R> function, T arg) {
            this.function = function;
            this.finish = false;
            this.arg = arg;
        }

        public synchronized void run() {
            result = function.apply(arg);
            finish = true;
            notifyAll();
        }

        public synchronized R getResult() {
            while (!finish) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    break;
                }
            }
            return result;
        }
    }

    private class TaskQueue<T, R> {
        private final Queue<Task<T, R>> queue;

        /**
         * Create task queue.
         */
        public TaskQueue() {
            queue = new LinkedList<>();
        }

        /**
         * Get task to queue
         *
         * @return task from queue
         */
        public synchronized Task<T, R> poll() {
            return queue.poll();
        }

        /**
         * check queue for empty
         *
         * @return true if empty, otherwise false
         */
        public synchronized boolean isEmpty() {
            return queue.isEmpty();
        }

        /**
         * Create tasks with function and arguments.
         *
         * @param function function in task.
         * @param arg      arguments in task.
         * @return task which was added to queue
         */
        public synchronized Task<T, R> add(Function<? super T, ? extends R> function, T arg) {
            Task<T, R> task = new Task<>(function, arg);
            queue.add(task);
            notifyAll();
            return task;
        }
    }
}
