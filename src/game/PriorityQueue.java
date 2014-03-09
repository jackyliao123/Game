package game;

import java.util.ArrayList;

public class PriorityQueue<T extends Comparable<T>> {

    private ArrayList<T> list = new ArrayList<T>();

    public void add(T r) {
        if (list.size() > 0) {
            boolean insert = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).compareTo(r) >= 0) {
                    list.add(i, r);
                    insert = true;
                    break;
                }
            }
            if (!insert) {
                list.add(r);
            }
        } else {
            list.add(r);
        }
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

}
