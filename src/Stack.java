import java.util.StringJoiner;

public class Stack<T> {
    private final Object[] data;
    private int size;

    public Stack() {
        data = new Object[30];
        size = 0;
    }

    public void push(T item) {
        if (!isFull()) {
            data[size] = item;
            size++;
        }
        else {
            System.out.println("No se pudo añadir, la cola está llena!");
        }
    }

    public T pop() {
        if (isEmpty()) {
            System.out.println("No se pudo quitar, la cola está vacía!");
            return null;
        }

        size--;
        @SuppressWarnings("unchecked")
        T item = (T) data[size];

        data[size] = null;
        return item;
    }

    // obtenerCima() en el pseudocódigo
    public T peek() {
        if (isEmpty()) {
            System.out.println("There's no elements, stack is empty!");
            return null;
        }
        @SuppressWarnings("unchecked")
        T item = (T) data[size - 1];
        return item;
    }

    public boolean isFull() {
        return size >= data.length;
    }
    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        StringJoiner result = new StringJoiner(", ", "[", "]");
        for (Object item : data) {
            if (item == null) break;
            result.add(item.toString());
        }
        return result.toString();
    }
}
