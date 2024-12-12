package mk.ukim.finki.kolokviumski2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Discounts
 */

class Product {
    public int price;
    public int discountedPrice;

    public Product(int price, int discountedPrice) {
        this.price = price;
        this.discountedPrice = discountedPrice;
    }

    public int calculateDiscount() {
        return (int) (100 - ((double) discountedPrice / price) * 100);
    }

    public int calculateDifference() {
        return Math.abs(price - discountedPrice);
    }

    @Override
    public String toString() {
        return String.format("%2d%% %d/%d", calculateDiscount(), discountedPrice, price);
    }
}

class Store {
    public String name;
    public Set<Product> products;
    public Comparator<Product> comparator = Comparator.comparing(Product::calculateDiscount).reversed();
    public static final Comparator<Store> STORE_COMPARATOR1 =
            Comparator.comparing(Store::averageDiscount).reversed()
                    .thenComparing(s->s.name);
    public static final Comparator<Store> STORE_COMPARATOR2 =
            Comparator.comparing(Store::calculateTotalDiscount).reversed()
                    .thenComparing(s->s.name);
    public Store() {
        products = new TreeSet<Product>(comparator);
        this.name = "";
    }

    public void addProduct(String line) {
        //GAP 501:593  6135:7868  1668:2582  3369:4330  9702:15999  8252:13674  3944:5707  2896:4392  9169:17391
        String[] parts = line.split("\\s+");
        name = parts[0];
        //Arrays.stream(parts).skip(1).forEach(part -> products.add(new Product(Integer.parseInt(part.split(":")[0]), Integer.parseInt(part.split(":")[1]))));
        for (int i = 1; i < parts.length; i++) {
            String[] prices = parts[i].split(":");
            int discountedPrice = Integer.parseInt(prices[0]);
            int price = Integer.parseInt(prices[1]);
            products.add(new Product(price, discountedPrice));
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append(String.format("Average discount: %.1f%%", averageDiscount())).append("\n");
        sb.append(String.format("Total discount: %d", calculateTotalDiscount())).append("\n");
        products.forEach(product -> sb.append(product).append("\n"));
        return sb.toString();
    }

    public float averageDiscount() {
        return (float) products.stream()
                .mapToInt(Product::calculateDiscount)
                .average().orElse(0.0);
    }

    public int calculateTotalDiscount() {
        return products.stream().mapToInt(Product::calculateDifference).sum();
    }
}

class Discounts {
    public Set<Store> stores;

    public Discounts() {
        stores = new HashSet<Store>();
    }

    public int readStores(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = br.lines().collect(Collectors.toList());
        lines.forEach(line -> {
            Store store = new Store();
            store.addProduct(line);
            stores.add(store);
        });
        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream().sorted(Comparator.comparing(Store::averageDiscount).reversed().thenComparing(s->s.name)).collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
       return stores.stream().sorted(Comparator.comparing(Store::calculateTotalDiscount).reversed().thenComparing(s->s.name)).collect(Collectors.toList());
    }

}

public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::print);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::print);
    }
}

// Vashiot kod ovde