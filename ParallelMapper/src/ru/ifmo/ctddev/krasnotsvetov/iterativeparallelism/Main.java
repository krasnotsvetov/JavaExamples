package ru.ifmo.ctddev.krasnotsvetov.iterativeparallelism;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;
import ru.ifmo.ctddev.krasnotsvetov.mapper.ParallelMapperImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Krasnotsvetov on 20.03.2016.
 */
public class Main {
    public static void main(String[] args) {
       /* ParallelMapper mapper = new ParallelMapperImpl(5);
        IterativeParallelism it = new IterativeParallelism(mapper);
        List<Integer> a = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < 1013; i++) {
            a.add(i);
        }

        Comparator<Integer> t = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };
        try {
            System.out.println(it.maximum(5, a, t));
            System.out.println(Runtime.getRuntime().availableProcessors());
        } catch (InterruptedException exp){

        }*/

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1526; i++) {
            list.add(i);
        }
        Function<Integer, Integer> add = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer * 2;
            }
        };
        List<Integer> ans = null;
        try (ParallelMapper pm = new ParallelMapperImpl(2)){
            ans = pm.map(add, list);
        } catch (InterruptedException e) {

        }
        for (int i : ans) {
            System.out.print(i + " ");
        }
    }
}
