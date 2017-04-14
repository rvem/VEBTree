package VEBTree;

import java.util.HashMap;

public class VEBTree implements IntegerSet {

    private int w, shift;

    private long min, max;
    //    private IntegerSet[] ch;
    HashMap<Long, IntegerSet> ch;
    private IntegerSet aux;


    VEBTree(int w) {
        this.w = w;
        shift = (w + 1) / 2;
        min = max = NO;
        ch = new HashMap<>();
        if (w == 2) {
            aux = new NaiveVEBTree(w >> 1);
        } else {
            aux = new VEBTree(shift);
        }
    }

    private long high(long x) {
        return x >> shift;
    }

    private long low(long x) {
        return x & ((1L << shift) - 1);
    }

    private long merge(long high, long low) {
        return (high << shift) | low;
    }

    private boolean isEmpty(IntegerSet set) {
        return set.getMin() == NO;
    }

    private IntegerSet getChild() {
        if (w == 2) {
            return new NaiveVEBTree(w >> 1);
        } else {
            return new VEBTree(shift);
        }
    }


    @Override
    public void add(long x) {
        if (isEmpty(this)) {
            min = x;
            max = x;
            return;
        }
        if (x < min) {
            long tmp = min;
            min = x;
            x = tmp;
        }

        if (x > max) {
            max = x;
        }

        long high = high(x), low = low(x);
        ch.putIfAbsent(high, getChild());
        if (isEmpty(ch.get(high))) {
            aux.add(high);
        }
        ch.get(high).add(low);
    }

    @Override
    public void remove(long x) {
        if (x == min) {
            if (isEmpty(aux)) {
                max = min = NO;
                return;
            }
            long minHigh = aux.getMin();
            ch.putIfAbsent(minHigh, getChild());
            min = merge(minHigh, ch.get(minHigh).getMin());
            ch.get(minHigh).remove(ch.get(minHigh).getMin());
            if (isEmpty(ch.get(minHigh))) {
                aux.remove(minHigh);
            }
            return;
        }
        long high = high(x), low = low(x);
        ch.putIfAbsent(high, getChild());
        ch.get(high).remove(low);
        if (isEmpty(ch.get(high))) {
            aux.remove(high);
        }
        if (isEmpty(aux)) {
            max = min;
        } else {
            max = merge(aux.getMax(), ch.get(aux.getMax()).getMax());
        }
    }

    @Override
    public long next(long x) {
        if (x < min) {
            return min;
        }
        long high = high(x), low = low(x);
        ch.putIfAbsent(high, getChild());
        if (!isEmpty(ch.get(high)) && low < ch.get(high).getMin()) {
            return merge(high, ch.get(high).next(low));
        }
        long nextHigh = aux.next(high);
        if (nextHigh == NO) {
            return NO;
        }
        ch.putIfAbsent(nextHigh, getChild());
        return merge(nextHigh, ch.get(nextHigh).getMin());
    }

    @Override
    public long prev(long x) {
        if (x > max) {
            return max;
        }
        if (x <= min || min == NO) {
            return NO;
        }
        long high = high(x), low = low(x);
        ch.putIfAbsent(high, getChild());
        if (!isEmpty(ch.get(high)) && low > ch.get(high).getMin()) {
            return merge(high, ch.get(high).prev(low));
        }
        long prevHigh = aux.prev(high);
        if (prevHigh == NO) {
            return min;
        }
        ch.putIfAbsent(prevHigh, getChild());
        return merge(prevHigh, ch.get(prevHigh).getMax());
    }

    @Override
    public long getMin() {
        return min;
    }

    @Override
    public long getMax() {
        return max;
    }

    private class NaiveVEBTree implements IntegerSet {
        boolean[] a;

        NaiveVEBTree(int s) {
            a = new boolean[1 << s];
        }

        @Override
        public void add(long x) {
            a[(int) x] = true;
        }

        @Override
        public void remove(long x) {
            a[(int) x] = false;
        }

        @Override
        public long next(long x) {
            int current = (int) (x + 1);
            while (current < a.length && !a[current]) {
                current++;
            }
            return current < a.length ? current : NO;
        }

        @Override
        public long prev(long x) {
            int current = (int) (x - 1);
            while (current >= 0 && !a[current]) {
                --current;
            }
            return current >= 0 ? current : NO;
        }

        @Override
        public long getMin() {
            int current = 0;
            while (current < a.length && !a[current]) {
                ++current;
            }
            return current < a.length ? current : NO;
        }

        @Override
        public long getMax() {
            int current = a.length - 1;
            while (current >= 0 && !a[current]) {
                --current;
            }
            return current < a.length ? current : NO;
        }
    }
}