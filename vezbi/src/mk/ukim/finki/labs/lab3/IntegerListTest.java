package mk.ukim.finki.labs.lab3;

import java.util.*;
import java.util.stream.Collectors;

class ArrayIndexOutOfBoundsException extends Exception{
    public ArrayIndexOutOfBoundsException(String message){
        super(message);
    }
}
class IntegerList {
    private ArrayList<Integer> list;

    IntegerList() {
        list = new ArrayList<>();
    }

    IntegerList(Integer... numbers) {
        list = new ArrayList<>(Arrays.asList(numbers));
    }

    public void add(int el, int idx) {
        if(idx > list.size()) { // ако индекс е поголем од големина на листа, додаваме 0 до местото каде што треба да се додаде елементот
            int len = idx - list.size();// толку 0 додаваме
            for(int i = 0; i < len; i++) {
                list.add(0);
            }
            list.add(el);
            return;
        }

        list.add(idx, el);
    }

    public int remove(int idx) throws ArrayIndexOutOfBoundsException {
        if(idx < 0 || idx >= list.size()) throw new ArrayIndexOutOfBoundsException("Invalid index");
        return list.remove(idx);
    }

    public void set(int el, int idx) throws ArrayIndexOutOfBoundsException {
        if(idx < 0 || idx >= list.size()) throw new ArrayIndexOutOfBoundsException("Invalid index");
        list.set(idx, el);
    }

    public int get(int idx) throws ArrayIndexOutOfBoundsException {
        if(idx < 0 || idx >= list.size()) throw new ArrayIndexOutOfBoundsException("Invalid index");
        return list.get(idx);
    }

    public int size() {
        return list.size();
    }

    public int count(int el) {
        return (int) list.stream()
                .filter(e -> e == el)
                .count();
    }

    public void removeDuplicates() {
//        list = (ArrayList<Integer>) list.stream()
//                .distinct()
//                .collect(Collectors.toList());

        for(int i = list.size() - 1; i >= 1; i--) {
            for(int j = i - 1; j >= 0; j--) {
                if(Objects.equals(list.get(i), list.get(j))) {
                    list.remove(j);
                    j++;
                    break;
                }
            }
        }
    }

    public int sumFirst(int k) {
        if(k < 0) { return 0; }

        k = Math.min(k,list.size()); // ако к е поголемо од големина на листата,
        // ќе работи со толку елементи колку што има во листата
        return list.stream()
                .limit(k) // ја ограничува на први к елементи
                .mapToInt(Integer::intValue)
                .sum();

    }

    public int sumLast(int k) {
        if(k < 0) { return 0; }

        k = Math.min(k,list.size());
        return list.stream()
                .skip(list.size() - k)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public void shiftRight(int idx, int k) throws ArrayIndexOutOfBoundsException {
        if(idx < 0 || idx >= list.size()) throw new ArrayIndexOutOfBoundsException("Invalid index");
        int targetIdx = (idx + k) % list.size(); // со % list.size() гарантира дека ако поместувањето ја поине границата на листата,
        // ќе се врати на почеток и од таму ке брои
        Integer el = list.remove(idx);
        list.add(targetIdx, el);
    }

    public void shiftLeft(int idx, int k) throws ArrayIndexOutOfBoundsException {
        if(idx < 0 || idx >= list.size()) throw new ArrayIndexOutOfBoundsException("Invalid index");
        int targetIdx = (idx - k) % list.size();
        if(targetIdx < 0) targetIdx = list.size() + targetIdx;
        Integer el = list.remove(idx);
        list.add(targetIdx, el);
    }

    public IntegerList addValue(int value) {
        IntegerList list = new IntegerList();
        for(Integer el : this.list) {
            list.add(el + value, list.size());
        }
        return list;
    }

}

public class IntegerListTest {

    public static void main(String[] args) throws ArrayIndexOutOfBoundsException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) { //test standard methods
            int subtest = jin.nextInt();
            if ( subtest == 0 ) {
                IntegerList list = new IntegerList();
                while ( true ) {
                    int num = jin.nextInt();
                    if ( num == 0 ) {
                        list.add(jin.nextInt(), jin.nextInt());
                    }
                    if ( num == 1 ) {
                        list.remove(jin.nextInt());
                    }
                    if ( num == 2 ) {
                        print(list);
                    }
                    if ( num == 3 ) {
                        break;
                    }
                }
            }
            if ( subtest == 1 ) {
                int n = jin.nextInt();
                Integer a[] = new Integer[n];
                for ( int i = 0 ; i < n ; ++i ) {
                    a[i] = jin.nextInt();
                }
                IntegerList list = new IntegerList(a);
                print(list);
            }
        }
        if ( k == 1 ) { //test count,remove duplicates, addValue
            int n = jin.nextInt();
            Integer a[] = new Integer[n];
            for ( int i = 0 ; i < n ; ++i ) {
                a[i] = jin.nextInt();
            }
            IntegerList list = new IntegerList(a);
            while ( true ) {
                int num = jin.nextInt();
                if ( num == 0 ) { //count
                    System.out.println(list.count(jin.nextInt()));
                }
                if ( num == 1 ) {
                    list.removeDuplicates();
                }
                if ( num == 2 ) {
                    print(list.addValue(jin.nextInt()));
                }
                if ( num == 3 ) {
                    list.add(jin.nextInt(), jin.nextInt());
                }
                if ( num == 4 ) {
                    print(list);
                }
                if ( num == 5 ) {
                    break;
                }
            }
        }
        if ( k == 2 ) { //test shiftRight, shiftLeft, sumFirst , sumLast
            int n = jin.nextInt();
            Integer a[] = new Integer[n];
            for ( int i = 0 ; i < n ; ++i ) {
                a[i] = jin.nextInt();
            }
            IntegerList list = new IntegerList(a);
            while ( true ) {
                int num = jin.nextInt();
                if ( num == 0 ) { //count
                    list.shiftLeft(jin.nextInt(), jin.nextInt());
                }
                if ( num == 1 ) {
                    list.shiftRight(jin.nextInt(), jin.nextInt());
                }
                if ( num == 2 ) {
                    System.out.println(list.sumFirst(jin.nextInt()));
                }
                if ( num == 3 ) {
                    System.out.println(list.sumLast(jin.nextInt()));
                }
                if ( num == 4 ) {
                    print(list);
                }
                if (num == 5 ) {
                    break;
                }
            }
        }
    }

    public static void print(IntegerList il) throws ArrayIndexOutOfBoundsException {
        if ( il.size() == 0 ) System.out.print("EMPTY");
        for ( int i = 0 ; i < il.size() ; ++i ) {
            if ( i > 0 ) System.out.print(" ");
            System.out.print(il.get(i));
        }
        System.out.println();
    }

}