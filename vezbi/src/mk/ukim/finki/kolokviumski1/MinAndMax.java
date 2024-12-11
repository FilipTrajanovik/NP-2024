package mk.ukim.finki.kolokviumski1;

import java.util.Scanner;

class MinMax<T extends Comparable<T>> {
    public T max;
    public T min;
    public int countDifference;
    public int countMin;
    public int countMax;
    public MinMax() {
        countMin=0;
        countMax=0;
        countDifference=0;
        max=null;
        min=null;
    }
    public void update(T element)
    {
        if(max==null || min==null)
        {
            max=element;
            min=element;
        }
        if(max.compareTo(element)<0)
        {
            max=element;
            countMax=1;
        }else if(max.compareTo(element) == 0)
        {
            countMax++;
        }
        if(min.compareTo(element)>0)
        {
            min=element;
            countMin=1;
        }else if(min.compareTo(element)==0)
        {
            countMin++;
        }
        countDifference++;
    }


    public T max() {
        return max;
    }

    public T min() {
        return min;
    }

    @Override
    public String toString() {
        return min + " " + max + " " +(countDifference - countMin - countMax) +"\n";
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for (int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for (int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}