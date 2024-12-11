package mk.ukim.finki.labs.lab3;

import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.stream.IntStream;

class ResizableArray<T> {
    private T[] elements;
    private int size;

    @SuppressWarnings("unchecked")

    public ResizableArray() {
        elements = (T[]) new Object[10];
        size = 0;
    }

    public void addElement(T element) {
        if (size == elements.length) { // ако е полна низата, ја зголемува двојно
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
        elements[size++] = element;
    }

    public boolean removeElement(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                // преместува елементи десно од избришаниот за една позиција лево
                System.arraycopy(elements, i + 1, elements, i, size - i - 1);
                elements[--size] = null;
                if (size < elements.length / 4) {
                    elements = Arrays.copyOf(elements, elements.length / 2);
                }
                return true;
            }
        }
        return false;
    }

    public boolean contains(T element) {
        return IntStream.range(0, size).anyMatch(i -> elements[i].equals(element));
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int count() {
        return size;
    }

    public T elementAt(int idx) {
        if (idx < 0 || idx >= size) {
            try {
                throw new ArrayIndexOutOfBoundsException("Index out of bounds: " + idx);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException(e);
            }
        }
        return elements[idx];
    }

    public static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src) {
        // ги копира сите елементи од src во dest
        IntStream.range(0, src.count()).forEach(i -> dest.addElement(src.elementAt(i)));
    }
}

class IntegerArray extends ResizableArray<Integer> {
    public double sum() {
        return IntStream.range(0, count()).mapToDouble(this::elementAt).sum();
    }

    public double mean() {
        return count() == 0 ? 0 : sum() / count();
    }

    public int countNonZero() {
        return (int) IntStream.range(0, count()).filter(i -> elementAt(i) != 0).count();
    }

    public IntegerArray distinct() {
        IntegerArray result = new IntegerArray();
        IntStream.range(0, count())
                .mapToObj(this::elementAt) // го зема елементот на секој индекс и го претвара во Stream<Integer>
                .distinct() // отстранува дупликати
                .forEach(result::addElement); // преку метод addElement го додава секој уникатен елемент
        return result;
    }

    public IntegerArray increment(int offset) {
        IntegerArray result = new IntegerArray();
        IntStream.range(0, count())
                .mapToObj(i -> elementAt(i) + offset)
                .forEach(result::addElement);
        return result;
    }
}

public class ResizableArrayTest {
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if (test == 0) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while (jin.hasNextInt()) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if (test == 1) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for (int i = 0; i < 4; ++i) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if (test == 2) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while (jin.hasNextInt()) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if (a.sum() > 100)
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if (test == 3) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for (int w = 0; w < 500; ++w) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k = 2000;
                int t = 1000;
                for (int i = 0; i < k; ++i) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for (int i = 0; i < t; ++i) {
                    a.removeElement(k - i - 1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }
}