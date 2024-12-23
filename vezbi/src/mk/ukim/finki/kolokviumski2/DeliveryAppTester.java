package mk.ukim.finki.kolokviumski2;

import java.util.*;


interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}

abstract class Object {
    public String id;
    public String name;


    public Object(String id, String name) {
        this.id = id;
        this.name = name;

    }
}

class DeliveryPerson extends Object {
    public Location currentLocation;
    public int numDeliveries;
    public double totalDeliveryFee;

    public DeliveryPerson(String id, String name, Location currentLocation) {
        super(id, name);
        this.currentLocation = currentLocation;
        this.numDeliveries = 0;
        this.totalDeliveryFee = 0;
    }

    public void addDelivery() {
        this.numDeliveries++;
    }

    public void addFee(double fee) {
        totalDeliveryFee += fee;
    }

    public double getAverageDeliveryFee() {
        if (totalDeliveryFee == 0 || numDeliveries == 0) {
            return 0.00;
        }
        return totalDeliveryFee / numDeliveries;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f", id, name, numDeliveries, totalDeliveryFee, getAverageDeliveryFee());
    }

    public double getTotalDeliveryFee() {
        return totalDeliveryFee;
    }
}

class Restaurant extends Object {

    public Location currentLocation;
    public int totalOrders;
    public double amountEarned;

    public Restaurant(String id, String name, Location currentLocation) {
        super(id, name);
        this.currentLocation = currentLocation;
        this.totalOrders = 0;
        this.amountEarned = 0;
    }

    public void addOrder() {
        totalOrders++;
    }

    public void increaseAmountEarned(double amount) {
        amountEarned += amount;
    }

    public double getAverageAmountEarned() {
        if (amountEarned == 0 || totalOrders == 0) {
            return 0.00;
        }
        return amountEarned / totalOrders;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f", id, name, totalOrders, amountEarned, getAverageAmountEarned());

    }
}

class AppUser extends Object {
    public List<Address> addresses;
    public double amountSpent;
    public int totalOrders;


    public AppUser(String id, String name) {
        super(id, name);
        this.addresses = new ArrayList<Address>();
        this.amountSpent = 0;
        this.totalOrders = 0;

    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public void addOrder() {
        totalOrders++;
    }

    public void increaseAmountSpent(double amount) {
        amountSpent += amount;
    }

    public double getAverageAmountSpent() {
        if (amountSpent == 0 || totalOrders == 0) {
            return 0.00;
        }
        return amountSpent / totalOrders;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f", id, name, totalOrders, amountSpent, getAverageAmountSpent());

    }

    public double getAmountSpent() {
        return amountSpent;
    }
}

class Address extends Object {

    public Location addressLocation;

    public Address(String id, String name, Location location) {
        super(id, name);
        this.addressLocation = location;
    }
}

class DeliveryApp {
    public String name;

    public Map<String, DeliveryPerson> mapDelivery;
    public Map<String, Restaurant> mapRestaurant;
    public Map<String, AppUser> mapAppUser;

    public DeliveryApp(String name) {
        this.name = name;
        mapDelivery = new HashMap<String, DeliveryPerson>();
        mapRestaurant = new HashMap<String, Restaurant>();
        mapAppUser = new HashMap<String, AppUser>();
    }

    public void registerDeliveryPerson(String id, String name, Location currentLocation) {
        DeliveryPerson deliveryPerson = new DeliveryPerson(id, name, currentLocation);
        mapDelivery.putIfAbsent(id, deliveryPerson);
    }

    public void addRestaurant(String id, String name, Location location) {
        Restaurant restaurant = new Restaurant(id, name, location);
        mapRestaurant.putIfAbsent(id, restaurant);
    }

    public void addUser(String id, String name) {
        AppUser appUser = new AppUser(id, name);
        mapAppUser.putIfAbsent(id, appUser);
    }

    public void addAddress(String id, String addressName, Location location) {
        Address address = new Address(id, addressName, location);
        mapAppUser.get(id).addAddress(address);
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        AppUser user = mapAppUser.get(userId);
        Restaurant restaurant = mapRestaurant.get(restaurantId);

        Address userAddress = null;

        for (Address adr : user.addresses) {
            if (adr.name.equals(userAddressName)) {
                userAddress = adr;
                break;
            }
        }

        if (userAddress == null) {
            System.out.println("NEMA TAKVA ADRESA");
        }

        int minDistance = Integer.MAX_VALUE;
        int minDeliveries = Integer.MAX_VALUE;

        DeliveryPerson chosen = null;
        List<DeliveryPerson> drivers = new ArrayList<>(mapDelivery.values());
        for (DeliveryPerson value : drivers) {
            int distance = value.currentLocation.distance(restaurant.currentLocation);
            if (distance < minDistance) {
                minDistance = distance;
                minDeliveries = value.numDeliveries;
                chosen = value;
            } else if (distance == minDistance) {
                if (value.numDeliveries < minDeliveries) {
                    minDeliveries = value.numDeliveries;
                    chosen = value;
                }
            }
        }
        int distanceToRestoraunt = restaurant.currentLocation.distance(userAddress.addressLocation);

        int totalEarned = 90 + ((int)( distanceToRestoraunt / 10))*10;
        chosen.addFee(totalEarned);
        chosen.addDelivery();
        user.increaseAmountSpent(cost);
        user.addOrder();
        restaurant.increaseAmountEarned(cost);
        restaurant.addOrder();

        chosen.currentLocation = userAddress.addressLocation;


    }

    public void printUsers() {
        Comparator<AppUser> comparator = Comparator.comparing(AppUser::getAmountSpent).thenComparing(u -> u.name).reversed();
        mapAppUser.values().stream().sorted(comparator).forEach(System.out::println);
    }

    public void printRestaurants() {
        Comparator<Restaurant> comparator = Comparator.comparing(Restaurant::getAverageAmountEarned).thenComparing(u -> u.name).reversed();
        mapRestaurant.values().stream().sorted(comparator).forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        Comparator<DeliveryPerson> comparator = Comparator.comparing(DeliveryPerson::getTotalDeliveryFee).thenComparing(u -> u.name).reversed();
        mapDelivery.values().stream().sorted(comparator).forEach(System.out::println);
    }
}
