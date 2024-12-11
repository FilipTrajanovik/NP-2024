package mk.ukim.finki.labs.lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PizzaOrderTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }

}

interface Item {
    int getPrice();

    String getType();
}

class InvalidExtraTypeException extends Exception {
    public InvalidExtraTypeException(String message) {
        super(message);
    }

}

class InvalidPizzaTypeException extends Exception {
    public InvalidPizzaTypeException(String message) {
        super(message);
    }

}

class ExtraItem implements Item {
    private String type;
    private int price;

    public ExtraItem(String type) throws InvalidExtraTypeException {
        if (type.equals("Coke")) {
            this.type = type;
            this.price = 5;
        } else if (type.equals("Ketchup")) {
            this.type = type;
            this.price = 3;
        } else throw new InvalidExtraTypeException("");
    }

    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        return price;
    }
}

class PizzaItem implements Item {
    private String type;
    private int price;


    public PizzaItem(String type) throws InvalidPizzaTypeException {
        if (type.equals("Standard")) this.price = 10;
        else if (type.equals("Pepperoni")) price = 12;
        else if (type.equals("Vegetarian")) price = 8;
        else throw new InvalidPizzaTypeException("");
        this.type = type;

    }

    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        return price;
    }
}

class ItemOutOfStockException extends Exception {
    public ItemOutOfStockException(Item item) {
        super(item.toString());
    }
}

class Order {
    private List<Item> itemList;
    private boolean locked = false;

    public Order() {
        this.itemList = new ArrayList<>();
    }

    public void addItem(Item item, int count) throws ItemOutOfStockException, OrderLockedException {
        if (locked) throw new OrderLockedException();
        if (count > 10) throw new ItemOutOfStockException(item);
        int firstApp = -1;
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getType().equals(item.getType())) {
                firstApp = i;
                break;
            }
        }
        // ги отстранува сите од ист тип
        itemList = itemList.stream().filter(x -> !x.getType().equals(item.getType())).collect(Collectors.toList());
        for (int i = 0; i < count; i++) {
            if (firstApp == -1) { // се додава на крај на листа бидејќи не е најден
                itemList.add(item);
            } else {
                itemList.add(firstApp, item);
            }
        }
    }

    public int getPrice() {
        int price = 0;
        for (Item i : itemList) price += i.getPrice();
        return price;
    }

    public void displayOrder() {
        int i = 1;
        for (List<Item> item : categorize()) {
            int price = item.size() * item.get(0).getPrice();
            System.out.printf("%3s.%-15sx%2s%5s$\n", i++, item.get(0).getType(), item.size(), price);
        }
        System.out.printf("%-22s%5s$\n", "Total:", this.getPrice());
    }

    // групира items од ист тип
    private List<List<Item>> categorize() {
        List<List<Item>> categorized = new ArrayList<List<Item>>();
        for (Item i : itemList) {
            boolean found = false;
            for (List<Item> cat : categorized) {
                if (cat.get(0).getType().equals(i.getType())) {
                    cat.add(i);
                    found = true;
                }
            }
            if (!found) {
                List<Item> newList = new ArrayList<Item>();
                newList.add(i);
                categorized.add(newList);
            }
        }
        return categorized;
    }

    public void removeItem(int idx) throws OrderLockedException, ArrayIndexOutOfBoundsException {
        if (locked) throw new OrderLockedException();
        if (idx < 0 || idx > itemList.size() - 1) throw new ArrayIndexOutOfBoundsException("");
        String type = itemList.get(idx).getType();
        itemList = itemList.stream().filter(x -> !x.getType().equals(type)).collect(Collectors.toList());
    }

    public void lock() throws EmptyOrder {
        if (itemList.size() > 0) this.locked = true;
        else throw new EmptyOrder();
    }


}

class EmptyOrder extends Exception {
    public EmptyOrder() {
        super();
    }
}

class OrderLockedException extends Exception {
    public OrderLockedException() {
        super();
    }
}