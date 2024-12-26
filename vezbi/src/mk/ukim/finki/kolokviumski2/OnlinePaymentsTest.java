package mk.ukim.finki.kolokviumski2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Item {

    public String itemName;
    public int price;

    public Item(String item_name, int price) {
        this.itemName = item_name;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s %d", itemName, price);
    }

    public int getPrice() {
        return price;
    }

    public String getItemName() {
        return itemName;
    }
}

class OnlinePayments {

    public Map<String, Set<Item>> map;

    public OnlinePayments() {
        map = new HashMap<>();
    }

    public void readItems(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        lines = br.lines().collect(Collectors.toList());

        for (String line : lines) {
            String[] parts = line.split(";");
            String studentId = parts[0];
            String itemText = parts[1];
            int price = Integer.parseInt(parts[2]);

            Item item = new Item(itemText, price);
            map.putIfAbsent(studentId, new HashSet<>());
            map.get(studentId).add(item);

        }
    }

    public void printStudentReport(String id, PrintStream out) {
        PrintWriter pw = new PrintWriter(out);

        if (!map.containsKey(id)) {
            System.out.println("Student " + id + " not found!");
            return;
        }

        List<Item> items = map.get(id).stream().collect(Collectors.toList());
        Comparator<Item> comparator = Comparator.comparing(Item::getPrice).thenComparing(Item::getItemName).reversed();

        items.sort(comparator);

        int net = items.stream().mapToInt(item -> item.price).sum();
        double provision = net * 0.0114;
        if (provision < 3) {
            provision = 3;
        } else if (provision > 300) {
            provision = 300;
        }
        double total=net+provision;


        pw.println(String.format("Student: %s Net: %d Fee: %d Total: %d", id, net, Math.round(provision), Math.round(total)));
        pw.println("Items: ");
        for(int i=0;i<items.size();i++) {
            Item item=items.get(i);
            pw.println(i+1 +". " + item);
        }
        pw.flush();
    }
}

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}