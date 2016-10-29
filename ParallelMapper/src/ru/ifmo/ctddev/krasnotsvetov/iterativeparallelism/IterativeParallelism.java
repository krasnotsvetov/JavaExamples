package ru.ifmo.ctddev.krasnotsvetov.iterativeparallelism;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;
import ru.ifmo.ctddev.krasnotsvetov.mapper.ParallelMapperImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Krasnotsvetov on 20.03.2016.
 */
public class IterativeParallelism implements ListIP, ScalarIP {

    private final ParallelMapper mapper;

    public IterativeParallelism() {
        this.mapper = null;
    }

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }


    /**
     * Concatenates elements of the list.
     * Concatenates elements of the list with using several threads.
     *
     * @param threadCount number of threads which use to concatenate elements in {@code list}
     * @param list        list concatenate elements
     * @return {@code String} - the concatenation of the elements of the list
     * @throws InterruptedException
     */

    @Override
    public String join(int threadCount, List<?> list) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        Function<List<?>, String> join = data -> {
            StringBuilder builder = new StringBuilder();
            data.stream().map(Object::toString).forEach(builder::append);
            return builder.toString();
        };
        List<CharSequence> t = new ArrayList<>();
        new TaskManager<>(mapper, threadCount, list, join).start().forEach(sb::append);
        return sb.toString();
    }

    /**
     * Filter the {@code list}.
     * Filter the {@code list} with {@code predicate} by using {@code threadCount} threads.
     *
     * @param threadCount number of threads which use to filter {@code list} with {@code predicate}
     * @param list        list to filter
     * @param predicate   predicate for filter
     * @return List with elements which matched to {@code predicate}
     * @throws InterruptedException
     */

    @Override
    public <T> List<T> filter(int threadCount, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        List<T> result = new ArrayList<>();
        Function<List<? extends T>, List<T>> filter = data -> {
            List<T> r = new ArrayList<>();
            data.stream().filter(predicate).forEach(r::add);
            return r;
        };
        new TaskManager<T, List<T>>(mapper, threadCount, list, filter).start().forEach(result::addAll);
        return result;
    }

    /**
     * Applies function to list.
     * Applies {@code function} to {@code list} and return list with functions result.
     *
     * @param threadCount number of threads which use to apply {@code function} to {@code list}
     * @param list        list to map
     * @param function    function to apply
     * @return
     * @throws InterruptedException
     */
    @Override
    public <T, R> List<R> map(int threadCount, List<? extends T> list, Function<? super T, ? extends R> function) throws InterruptedException {
        List<R> result = new ArrayList<>();
        Function<List<? extends T>, List<R>> map = data -> {
            List<R> r = new ArrayList<>();
            data.stream().map(function).forEach(r::add);
            return r;
        };
        new TaskManager<T, List<R>>(mapper, threadCount, list, map).start().forEach(result::addAll);
        return result;
    }

    /**
     * Find maximum in the list.
     * Find maximum in the list by using several threads and return it.
     *
     * @param threadCount number of threads which use to find maximum to {@code list}
     * @param list        list to find
     * @param comparator  comporator which is using to compare elements
     * @return maximum in the {@code list}
     * @throws InterruptedException
     */


    @Override
    public <T> T maximum(int threadCount, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        Function<List<? extends T>, T> max = data -> data.stream().max(comparator).get();
        return max.apply(new TaskManager<T, T>(mapper, threadCount, list, max).start());
    }

    /**
     * Find minimum in the list.
     * Find minimum in the list by using several threads and return it.
     *
     * @param threadCount number of threads which is using to find minimum to {@code list}
     * @param list        list to find
     * @param comparator  comporator which is using to compare elements
     * @return minimum in the {@code list}
     * @throws InterruptedException
     */


    @Override
    public <T> T minimum(int threadCount, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threadCount, list, comparator.reversed());
    }

    /**
     * Check {@code list} for matching with predicate.
     * Use several threads for check that all elements in {@code list} matching with predicate
     *
     * @param threadCount number of threads which is using
     * @param list        list to match with predicate
     * @param predicate   predicate to match
     * @return {@code true} if every element matches with predicate, {@code false} otherwise
     * @throws InterruptedException
     */

    @Override
    public <T> boolean all(int threadCount, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return new TaskManager<T, Boolean>(mapper, threadCount, list, data -> data.stream().allMatch(predicate)).start().stream().allMatch(Predicate.isEqual(true));
    }

    /**
     * Check {@code list} for matching with predicate.
     * Use several threads for check that any element in {@code list} matching with predicate
     *
     * @param threadCount number of threads which is using
     * @param list        list to match with predicate
     * @param predicate   predicate to match
     * @return {@code true} if any element matches with predicate, {@code false} otherwise
     * @throws InterruptedException
     */

    @Override
    public <T> boolean any(int threadCount, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return !(all(threadCount, list, predicate.negate()));
    }

    private class Task<T, R> implements Runnable {

        private volatile R value;
        private final List<? extends T> list;
        private final Function<List<? extends T>, R> function;

        public Task(List<? extends T> list, Function<List<? extends T>, R> function) {
            this.list = list;
            this.function = function;
        }

        @Override
        public void run() {
            value = function.apply(list);
        }

        public R getValue() {
            return value;
        }
    }


    private class TaskManager<T, R> {
        private final List<Task<T, R>> tasks;
        private final Function<List<? extends T>, R> function;
        private ParallelMapper parallelMapper;
        private final List<List<? extends T>> splitList;


        private TaskManager(ParallelMapper parallelMapper, int taskCount, List<? extends T> list, Function<List<? extends T>, R> function) {

            taskCount = Math.max((int) (taskCount / 1.8f), 1);
            if (parallelMapper != null) {

                this.parallelMapper = parallelMapper;
                tasks = null;
                this.function = function;
                splitList = splitList(taskCount, list);
                return;
            }
            splitList = null;
            // System.out.println(taskCount + " " + list.size());
            List<Task<T, R>> tasks = new ArrayList<Task<T, R>>();

            int stepSize = Math.max(list.size() / taskCount, 1);
            if (list.size() < taskCount) {
                taskCount = list.size();
            }
            int finalIndex = taskCount - 1;
            for (int i = 0; i < finalIndex; i++) {
                tasks.add(new Task<T, R>(list.subList(i * stepSize, (i + 1) * stepSize), function));
            }

            tasks.add(new Task<T, R>(list.subList(finalIndex * stepSize, list.size()), function));
            this.tasks = tasks;
            this.function = function;
        }


        private List<List<? extends T>> splitList(int partCount, List<? extends T> list) {
            List<List<? extends T>> result = new ArrayList<>();
            int stepSize = Math.max(list.size() / partCount, 1);


            if (list.size() < partCount) {
                partCount = list.size();
            }
            int finalIndex = partCount - 1;


            int lastCount = list.size() - finalIndex * stepSize;
            while (lastCount > finalIndex) {
                stepSize++;
                lastCount -= finalIndex;
            }

            for (int i = 0; i < finalIndex; i++) {
                result.add(list.subList(i * stepSize, Math.min((i + 1) * stepSize, list.size())));
            }
            if (finalIndex * stepSize < list.size()) {
                result.add(list.subList(finalIndex * stepSize, list.size()));
            }

            return result;
        }

        private List<R> start() throws InterruptedException {
            if (parallelMapper != null) {
                return parallelMapper.map(function, splitList);
            }
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                threads.add(new Thread(tasks.get(i)));
                threads.get(i).start();
            }

            for (Thread t : threads) {
                t.join();
            }

            List<R> result = new ArrayList<>();
            for (Task<T, R> t : tasks) {
                result.add(t.getValue());
            }
            return result;
        }

    }
}
