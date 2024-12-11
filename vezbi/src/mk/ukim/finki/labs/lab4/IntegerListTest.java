package mk.ukim.finki.labs.lab4;

import java.util.*;
import java.util.stream.Collectors;

class IntegerList {
    public List<Integer> list;

    public IntegerList() {
        list = new ArrayList<>();
    }

    public IntegerList(Integer... numbers) {
        list = new ArrayList<>();
        list.addAll(Arrays.asList(numbers));
    }

    public void add(int el, int idx) {
        if (idx < 0) {
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + idx);
        }

        if (idx <= list.size()) {
            list.add(idx, el); // Insert the element at the specified index
        } else {
            while (list.size() < idx) {
                list.add(0); // Add zeros to fill up the gap
            }
            list.add(el); // Add the element at the extended index
        }
    }

    public void set(int el, int idx) {
        if (idx < 0 || idx >= list.size()) {
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + idx);
        }
        list.set(idx, el); // Replace the element at the specified index
    }

    public int remove(int idx) {
        if (idx < 0 || idx >= list.size()) {
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + idx);
        }
        int vrati=list.get(idx);
        list.remove(idx);
        return vrati; // Remove and return the element
    }

    public int get(int idx) {
        if (idx < 0 || idx >= list.size()) {
            throw new ArrayIndexOutOfBoundsException("Invalid index: " + idx);
        }
        return list.get(idx);
    }

    public int size() {
        return list.size();
    }

    public int count(int el) {
        return (int) list.stream().filter(num -> num == el).count();
    }

    public void removeDuplicates() {
        Collections.reverse(list);
        list = list.stream().distinct().collect(Collectors.toList());
        Collections.reverse(list);
    }

    public int sumFirst(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative");
        }
        k = Math.min(k, list.size()); // Limit `k` to the size of the list
        int res = 0;
        for (int i = 0; i < k; i++) {
            res += list.get(i);
        }
        return res;
    }

    public int sumLast(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative");
        }
        k = Math.min(k, list.size()); // Limit `k` to the size of the list
        int res = 0;
        for (int i = list.size() - k; i < list.size(); i++) {
            res += list.get(i);
        }
        return res;
    }

    public IntegerList addValue(int value) {
        IntegerList il = new IntegerList();

        il.list = list.stream().map(i -> i + value).collect(Collectors.toList());

        return il;
    }

    public void shiftRight(int i, int k) {
        if (i < 0 || i >= list.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int a = list.get(i);

        list.remove(i);

        list.add((i + k) % (list.size() + 1), a);
    }

    public void shiftLeft(int i, int k) {
        if (i < 0 || i >= list.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int a = list.get(i);

        list.remove(i);

        list.add(Math.floorMod(i - k, list.size() + 1), a);
    }
}

public class IntegerListTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { // Test standard methods
            int subtest = jin.nextInt();
            if (subtest == 0) {
                IntegerList list = new IntegerList();
                while (true) {
                    int num = jin.nextInt();
                    if (num == 0) {
                        list.add(jin.nextInt(), jin.nextInt());
                    }
                    if (num == 1) {
                        list.remove(jin.nextInt());
                    }
                    if (num == 2) {
                        print(list);
                    }
                    if (num == 3) {
                        break;
                    }
                }
            }
            if (subtest == 1) {
                int n = jin.nextInt();
                Integer a[] = new Integer[n];
                for (int i = 0; i < n; ++i) {
                    a[i] = jin.nextInt();
                }
                IntegerList list = new IntegerList(a);
                print(list);
            }
        }
        if (k == 1) { // Test count, remove duplicates, addValue
            int n = jin.nextInt();
            Integer a[] = new Integer[n];
            for (int i = 0; i < n; ++i) {
                a[i] = jin.nextInt();
            }
            IntegerList list = new IntegerList(a);
            while (true) {
                int num = jin.nextInt();
                if (num == 0) {
                    System.out.println(list.count(jin.nextInt()));
                }
                if (num == 1) {
                    list.removeDuplicates();
                }
                if (num == 2) {
                    print(list.addValue(jin.nextInt()));
                }
                if (num == 3) {
                    list.add(jin.nextInt(), jin.nextInt());
                }
                if (num == 4) {
                    print(list);
                }
                if (num == 5) {
                    break;
                }
            }
        }
        if (k == 2) { // Test shiftRight, shiftLeft, sumFirst, sumLast
            int n = jin.nextInt();
            Integer a[] = new Integer[n];
            for (int i = 0; i < n; ++i) {
                a[i] = jin.nextInt();
            }
            IntegerList list = new IntegerList(a);
            while (true) {
                int num = jin.nextInt();
                if (num == 0) {
                    list.shiftLeft(jin.nextInt(), jin.nextInt());
                }
                if (num == 1) {
                    list.shiftRight(jin.nextInt(), jin.nextInt());
                }
                if (num == 2) {
                    System.out.println(list.sumFirst(jin.nextInt()));
                }
                if (num == 3) {
                    System.out.println(list.sumLast(jin.nextInt()));
                }
                if (num == 4) {
                    print(list);
                }
                if (num == 5) {
                    break;
                }
            }
        }
    }

    public static void print(IntegerList il) {
        if (il.size() == 0) {
            System.out.println("EMPTY");
            return;
        }
        for (int i = 0; i < il.size(); ++i) {
            if (i > 0) System.out.print(" ");
            System.out.print(il.get(i));
        }
        System.out.println();
    }
}
