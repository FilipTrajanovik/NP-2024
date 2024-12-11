package mk.ukim.finki.kolokviumski1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

enum CAKE_TYPE {
    CAKE, PIE
}

class InvalidOrderException extends Exception {
    public InvalidOrderException(String message) {
        super(message);
    }
}

class Cake {
    public String cakeName;
    public double cakePrice;
    public CAKE_TYPE cakeType;

    public Cake(String cakeName, double cakePrice, CAKE_TYPE cakeType) {
        this.cakeName = cakeName;
        this.cakePrice = cakePrice;
        this.cakeType = cakeType;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f %s", cakeName, cakePrice, cakeType);
    }
}

class Order {
    public List<Cake> cakes;
    public String orderId;
    int counterCake;
    int counterPies;

    public Order(String line) {
        counterCake=0;
        counterPies=0;
        cakes = new ArrayList<Cake>();
        String[] parts = line.split(" ");
        this.orderId = parts[0];
        for (int i = 1; i < parts.length; i += 2) {
            String cakeName = parts[i];
            double cakePrice = Double.parseDouble(parts[i + 1]);
            CAKE_TYPE cakeType = null;
            if (cakeName.toLowerCase().charAt(0) == 'c') {
                cakeType = CAKE_TYPE.CAKE;
                counterCake++;
            } else {
                cakeType = CAKE_TYPE.PIE;
                counterPies++;
                cakePrice+=50;
            }
            Cake cake = new Cake(cakeName, cakePrice, cakeType);
        }
    }
    public double getTotalPrice() {
        return cakes.stream().mapToDouble(cake->cake.cakePrice).sum();
    }

    @Override
    public String toString() {
       return String.format("%s %d %d %.2f", orderId, counterPies, counterCake, getTotalPrice());
    }
}

class CakeShopApplication {
    public int minOrderItems;
    public List<Order> orders;
    public CakeShopApplication(int minOrderItems) {
        this.minOrderItems = minOrderItems;
        this.orders = new ArrayList<Order>();
    }

    public int readCakeOrders(InputStream inputStream) throws InvalidOrderException {
        BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines=br.lines().collect(Collectors.toList());
        for (String line : lines) {
            Order order = new Order(line);
            if(order.cakes.size()<minOrderItems) {
                throw new InvalidOrderException("The order with id " + order.orderId + " has less items than minimum is " + minOrderItems);
            }
            orders.add(order);
        }
        return orders.size();
    }

    public void printAllOrders(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        orders.sort(Comparator.comparing(Order::getTotalPrice).reversed());
        orders.forEach(order -> pw.println(order.toString()));
        pw.flush();
    }
}

public class CakeShopApplicationTest {
    public static void main(String[] args) {
        CakeShopApplication cakeShopApplication = new CakeShopApplication(4);
        System.out.println("--- READING FROM INPUT STREAM ---");
        try {
            cakeShopApplication.readCakeOrders(System.in);
        } catch (InvalidOrderException e) {
            throw new RuntimeException(e);
        }
        System.out.println("--- PRINTING TO OUTPUT STREAM ---");
        cakeShopApplication.printAllOrders(System.out);
    }
}
