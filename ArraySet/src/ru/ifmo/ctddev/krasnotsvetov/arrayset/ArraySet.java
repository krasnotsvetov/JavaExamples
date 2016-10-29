package ru.ifmo.ctddev.krasnotsvetov.arrayset;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Krasnotsvetov on 29.02.2016.
 */
public class ArraySet<T> extends AbstractSet<T> implements NavigableSet<T> {


    private final List<T> array;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        this(Collections.emptyList(), null, true);
    }

    public ArraySet(Collection<? extends T> collection) {
        this(collection, null);
    }


    public ArraySet(Collection<? extends T> collection, Comparator<? super T> comparator) {
        array = new ArrayList<>();
        Set<T> temp = new TreeSet<>(comparator);
        temp.addAll(collection);
        array.addAll(temp);
        this.comparator = comparator;
    }

    private ArraySet(List<T> array, Comparator<? super T> comparator, boolean copy) {
        this.array = array;
        this.comparator = comparator;
    }

    private Comparator<? super T> bsComporator() {
        if (comparator == null) {
            return (T a, T b) -> ((Comparable<? super T>) a).compareTo(b);
        }
        return comparator;
    }

    private int binarySearch(Predicate<T> predicate, boolean type) {
        int l = -1;
        int r = array.size();
        while (l < r - 1) {
            int m = (l + r) >> 1;
            if (predicate.test(array.get(m))) {
                if (type) {
                    r = m;
                } else {
                    l = m;
                }
            } else {
                if (type) {
                    l = m;
                } else {
                    r = m;
                }
            }
        }
        return type ? r : l;
    }


    @Override
    public T lower(T t) {
        int index = lowerIndex(t);
        if (index < 0 || index >= array.size()) {
            return null;
        }
        return array.get(index);
    }

    private int lowerIndex(T t) {
        return binarySearch(a -> bsComporator().compare(a, t) < 0, false);
    }

    @Override
    public T higher(T t) {

        int index = higherIndex(t);
        if (index < 0 || index >= array.size()) {
            return null;
        }
        return array.get(index);
    }

    private int higherIndex(T t) {
        return binarySearch(a -> bsComporator().compare(a, t) > 0, true);
    }

    @Override
    public T floor(T t) {
        int index = floorIndex(t);
        if (index < 0 || index >= array.size()) {
            return null;
        }
        return array.get(index);
    }

    private int floorIndex(T t) {
        return binarySearch(a -> bsComporator().compare(a, t) <= 0, false);
    }

    @Override
    public T ceiling(T t) {
        int index = ceilingIndex(t);
        if (index < 0 || index >= array.size()) {
            return null;
        }
        return array.get(index);
    }


    private int ceilingIndex(T t) {
        return binarySearch(a -> bsComporator().compare(a, t) >= 0, true);
    }


    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(array, o, (Comparator<Object>) comparator) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(array).iterator();
    }


    @Override
    public NavigableSet<T> descendingSet() {
        return new ArraySet<>(new ReversedArrayList<>(array), Collections.reverseOrder(comparator), true);
    }

    @Override
    public Iterator<T> descendingIterator() {
        return new ReversedArrayList<>(array).iterator();
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {

        if (fromElement == null || fromElement == null) {
            throw new NullPointerException();
        }

        int s = fromInclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        int e = toInclusive ? floorIndex(toElement) : lowerIndex((toElement));
        if (s > e) {
            if (comparator != null && comparator.compare(toElement, fromElement) == 0 ||
                    comparator == null && fromElement.equals(toElement)) {
                return new ArraySet<>(Collections.emptyList(), comparator, true);
            }
        }
        return new ArraySet<>(array.subList(s, e + 1), comparator);
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        if (array.size() == 0) {
            return new ArraySet<>(Collections.emptyList(), comparator, true);
        }
        int e = inclusive ? floorIndex(toElement) : lowerIndex(toElement);
        return new ArraySet<>(array.subList(0, e + 1), comparator, true);
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        if (array.size() == 0) {
            return new ArraySet<>(Collections.emptyList(), comparator, true);
        }
        int s = inclusive ? ceilingIndex(fromElement) : higherIndex(fromElement);
        return new ArraySet<>(array.subList(s, size()), comparator, true);
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public T first() {
        if (array.size() != 0) {
            return array.get(0);

        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T last() {
        if (array.size() != 0) {
            return array.get(array.size() - 1);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int size() {
        return array.size();
    }

    private class ReversedArrayList<T> extends AbstractList<T> implements RandomAccess {

        private final List<T> array;
        private final boolean reverse;

        ReversedArrayList(List<T> array) {
            if (array instanceof ReversedArrayList) {
                ReversedArrayList<T> t = (ReversedArrayList<T>) array;
                reverse = !t.reverse;
                this.array = t.array;
            } else {
                this.array = array;
                reverse = true;

            }
        }

        @Override
        public T get(int index) {
            if (reverse) {
                return array.get(array.size() - index - 1);
            } else {
                return array.get(index);
            }
        }

        @Override
        public int size() {
            return array.size();
        }

    }
}
