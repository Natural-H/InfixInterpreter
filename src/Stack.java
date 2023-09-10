import java.util.StringJoiner;

public class Stack<T> {
    private Object[] data;
    private int used;
    private static final int DEFAULT_CAPACITY = 10;

    public Stack() {
        data = new Object[DEFAULT_CAPACITY];
        used = 0;
    }

    public Stack(int size) {
        data = new Object[size];
        used = 0;
    }

    public void push(T item) {
        if (used == data.length) {
            ensureCapacity(used * 2);
        }
        data[used++] = item;
    }

    public void pop(boolean printError) {
        if (isEmpty()) {
            if (printError) System.out.println("Couldn't remove element, stack is empty!");
            return;
        }
        --used;
        data[used] = null;
    }

    public T getTop() {
        if (isEmpty()) {
            System.out.println("There's no elements, stack is empty!");
            return null;
        }
        @SuppressWarnings("unchecked")
        T item = (T) data[used - 1];
        return item;
    }

    public boolean isEmpty() {
        return used == 0;
    }

    public int getUsed() {
        return used;
    }

    public void clear() {
        for (int i = 0; i < used; i++) {
            data[i] = null;
        }
        used = 0;
    }

    // Used to increase the stack capacity when it's necessary
    private void ensureCapacity(int minCapacity) {
        int newSize = Math.max(minCapacity, DEFAULT_CAPACITY);
        Object[] newArray = new Object[newSize];
        System.arraycopy(data, 0, newArray, 0, used);
        data = newArray;
    }

    @Override
    public String toString() {
        // Can't use Arrays.toString(), it'll get the null data
        StringJoiner result = new StringJoiner(", ", "[", "]");
        for (Object item : data) {
            if (item == null) break;
            result.add(item.toString());
        }
        return result.toString();
    }
}
