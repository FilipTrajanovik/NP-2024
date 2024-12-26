package mk.ukim.finki.kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

class Names{
    public Map<String, Integer> map;

    public Names() {
        this.map=new TreeMap<>();
    }

    public void addName(String name){
        map.put(name, map.getOrDefault(name, 0)+1);
    }
    public void printN(int n){
        map.forEach((key, value) -> {
            if (value >= n) {
                Set<Character> chars=new HashSet<>();
                char[] bukvi=key.toLowerCase().toCharArray();
                for (char c : bukvi) {
                    chars.add(c);
                }
                System.out.println(key + " (" + value + ") " + chars.size());
            }
        });
    }
    public  String findName(int len, int x){
        List<String> names = map.keySet().stream().collect(Collectors.toList());
        List<String> finalNames=new ArrayList<>();
        for (String name : names) {
            if(name.length() < len)
            {
                finalNames.add(name);
            }
        }
        finalNames.sort(Comparator.naturalOrder());
        if(finalNames.size() < x)
        {
            return finalNames.get(x % finalNames.size());
        }else{
            return finalNames.get(x);
        }
    }
}

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

// vashiot kod ovde