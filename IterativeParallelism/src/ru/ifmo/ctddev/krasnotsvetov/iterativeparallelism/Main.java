package ru.ifmo.ctddev.krasnotsvetov.iterativeparallelism;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Created by Krasnotsvetov on 20.03.2016.
 */
public class Main {
    public static void main(String[] args) {
        IterativeParallelism it = new IterativeParallelism();
        List<Boolean> a = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < 1013; i++) {
            a.add(rnd.nextInt(2) == 0 ? true : false);
        }

        Comparator<Boolean> t = new Comparator<Boolean>() {
            @Override
            public int compare(Boolean o1, Boolean o2) {
                return o1.compareTo(o2);
            }
        };
        try {
            System.out.println(it.filter(25, a, Predicate.isEqual(true)));
            System.out.println(Runtime.getRuntime().availableProcessors());
        } catch (InterruptedException exp){

        }
    }
}
