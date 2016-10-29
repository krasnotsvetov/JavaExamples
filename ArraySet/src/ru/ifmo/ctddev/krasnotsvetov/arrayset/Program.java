package ru.ifmo.ctddev.krasnotsvetov.arrayset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Krasnotsvetov on 29.02.2016.
 */
public class Program {
    public static void main(String[] args)
    {
       // -1290293228, 275368208, 439197409, 2019629246
       // int[] a = {1, 1, 2, 2, 3, 5, 5};
        int[] a = {-1290293228, 275368208, 439197409, 2019629246};
        List<Integer> list = new ArrayList<>();

        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        };
        for (int i : a){
            list.add(i);
        }

        ArraySet<Integer> set = new ArraySet<>(list, comparator);

        ArraySet<Integer> setD = (ArraySet<Integer>)set.descendingSet();

        System.out.println(set);
        System.out.println(setD);
        System.out.println(set.tailSet(-1290293228, true));
        //expected:<[275368208, 439197409, 2019629246]> but was:<[-1290293228, 275368208, 439197409]>
    }

}
