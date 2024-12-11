package mk.ukim.finki.labs.lab4;

import java.util.*;
import java.util.stream.Collectors;
class StringObject{
    String s;
    int order;

    public StringObject(String s, int order) {
        this.s = s;
        this.order = order;
    }
    public void reverseString(){
        s = new StringBuilder(s).reverse().toString();
    }
    @Override
    public String toString() {
        return s;
    }
    public int getOrder(){
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringObject that = (StringObject) o;
        return order == that.order && Objects.equals(s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s, order);
    }
}
class SuperString{
    LinkedList<StringObject> list;

    public SuperString() {
        this.list = new LinkedList<>();
    }
    public void append(String s){
        list.add(new StringObject(s, list.size()));
    }
    public void insert(String s){
        list.addFirst(new StringObject(s, list.size()));
    }
    public boolean contains(String s){
        StringBuilder sb = new StringBuilder();
        for(StringObject str : list)
            sb.append(str.toString());
        return sb.toString().contains(s);
    }
    public void reverse(){
        Collections.reverse(list);
        for(StringObject s : list)
            s.reverseString();
    }
    public void removeLast(int k){
        if(k==0)
            return;
        list = list.stream()
                .filter(x->x.getOrder()<list.size()-k)
                .collect(Collectors.toCollection(LinkedList::new));
    }
    public int getSize(){
        return list.size();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(StringObject s : list)
            sb.append(s);
        return sb.toString();
    }
}
public class SuperStringTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (  k == 0 ) {
            SuperString s = new SuperString();
            while ( true ) {
                int command = jin.nextInt();
                if ( command == 0 ) {//append(String s)
                    s.append(jin.next());
                }
                if ( command == 1 ) {//insert(String s)
                    s.insert(jin.next());
                }
                if ( command == 2 ) {//contains(String s)
                    System.out.println(s.contains(jin.next()));
                }
                if ( command == 3 ) {//reverse()
                    s.reverse();
                }
                if ( command == 4 ) {//toString()
                    System.out.println(s);
                }
                if ( command == 5 ) {//removeLast(int k)
                    s.removeLast(jin.nextInt());
                }
                if ( command == 6 ) {//end
                    break;
                }
            }
        }
    }

}