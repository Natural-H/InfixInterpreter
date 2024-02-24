import java.util.StringJoiner;

public class CustomStack<T> {
    private final Object[] data;
    private int size;
    private boolean logging;
    private final String name;

    public CustomStack(String name) {
        logging = false;
        data = new Object[30];
        size = 0;
        this.name = name;
    }

    public void push(T item) {
        if (isFull()) {
            System.out.println("No se pudo añadir, la cola está llena!");
            return;
        }

        data[size] = item;
        size++;

        if (logging)
            logPushing(item);
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

        if (logging)
            logPopping(item);
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

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    private void logPushing(T element) {
        System.out.println(name + ": Se añadió el elemento: " + element);
        System.out.println("Pila actual: " + this);
    }

    private void logPopping(T element) {
        System.out.println(name + ": Se quitó el elemento: " + element);
        System.out.println("Pila actual: " + this);
    }
}
