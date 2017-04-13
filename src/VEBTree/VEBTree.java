package VEBTree;

import java.util.Arrays;

public class VEBTree implements IntegerSet {

    private int shift;

    private long min, max;
    private IntegerSet[] ch;
    private IntegerSet aux;


    VEBTree(int w) {
        shift = (w + 1) / 2;
        min = max = NO;
        if (w == 2) {
            aux = new NaiveVEBTree(w >> 1);
            ch = new NaiveVEBTree[w];
            for (int i = 0; i < w; i++) {
                ch[i] = new NaiveVEBTree(w >> 1);
            }
        } else {
            aux = new VEBTree(shift);
            ch = new VEBTree[1 << (w >> 1)];
            for (int i = 0; i < (1 << (w >> 1)); i++) {
                ch[i] = new VEBTree(shift);
            }
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
        if (isEmpty(ch[(int) high])) {
            aux.add(high);
        }
        ch[(int) high].add(low);
    }

    @Override
    public void remove(long x) {
        if (x == min)  {
            if (isEmpty(aux)) {
                max = min = NO;
                return;
            }
            long minHigh = aux.getMin();
            min = merge(minHigh, ch[(int) minHigh].getMin());
            ch[(int) minHigh].remove(ch[(int) minHigh].getMin());
            if (isEmpty(ch[(int) minHigh])) {
                aux.remove(minHigh);
            }
            return;
        }
        long high = high(x), low = low(x);
        ch[(int) high].remove(low);
        if (isEmpty(ch[(int) high])) {
            aux.remove(high);
        }
        if (isEmpty(aux)) {
            max = min;
        } else {
            max = merge(aux.getMax(), ch[(int) aux.getMax()].getMax());
        }
    }

    @Override
    public long next(long x) {
        if (x < min) {
            return min;
        }
        long high = high(x), low = low(x);
        if (!isEmpty(ch[(int) high]) && low < ch[(int) high].getMin()) {
            return merge(high, ch[(int) high].next(low));
        }
        long nextHigh = aux.next(high);
        if (nextHigh == NO) {
            return NO;
        }
        return merge(nextHigh, ch[(int) nextHigh].getMin());
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
        if (!isEmpty(ch[(int) high]) && low > ch[(int) high].getMin()) {
            return merge(high, ch[(int) high].prev(low));
        }
        long prevHigh = aux.prev(high);
        if (prevHigh == NO) {
            return min;
        }
        return merge(prevHigh, ch[(int) prevHigh].getMax());
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