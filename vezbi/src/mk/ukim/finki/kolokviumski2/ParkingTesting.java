package mk.ukim.finki.kolokviumski2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class DateUtil {
    public static long durationBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes();
    }
}

class Car{
    public String registration;
    public String spot;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public int numParked;

    public Car(String registration, String spot, LocalDateTime startDate) {
        this.registration = registration;
        this.spot = spot;
        this.startDate = startDate;
        this.endDate = null;
        this.numParked=0;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
    public double getDuration()
    {
        return DateUtil.durationBetween(startDate, endDate);
    }

    @Override
    public String toString() {
        if(endDate == null) {
            return String.format("Registration number: %s Spot: %s Start timestamp: %s", registration, spot, startDate);
        }else{
            return String.format("Registration number: %s " +
                            "Spot: %s " +
                            "Start timestamp: %s " +
                            "End timestamp: %s " +
                            "Duration in minutes: %d", registration, spot,
                    startDate, endDate, DateUtil.durationBetween(startDate, endDate));

        }
    }
}

class Parking  {

    public int capacity;

    public Map<String, Car> cars;
    public List<Car> historyCars;


    public Parking(int capacity) {
        this.capacity = capacity;
        this.cars = new HashMap<>();
        this.historyCars = new ArrayList<>();
    }
    public void update (String registration, String spot, LocalDateTime timestamp, boolean entry){
        if(entry) {
            cars.put(registration, new Car(registration, spot, timestamp));
            cars.get(registration).numParked++;
        }else {

            Car car = cars.get(registration);
            if(car!=null)
            {
                car.setEndDate(timestamp);
                historyCars.add(car);
                cars.remove(car.registration);
            }

        }
    }
    public void currentState(){
        double capacityFilled= (double) cars.size() /  capacity * 100.0;
        System.out.printf("Capacity filled: %.2f%%\n", capacityFilled);
        Comparator<Car> comparator = Comparator.comparing(Car::getStartDate).reversed();
        List<Car> carList = cars.values().stream().sorted(comparator).collect(Collectors.toList());
        for (Car car : carList) {
            System.out.println(car.toString());
        }
    }
    public void history(){
        Comparator<Car> comparator = Comparator.comparing(Car::getDuration).reversed();
        historyCars.sort(comparator);
        historyCars.forEach(System.out::println);
    }
    public Map<String, Integer> carStatistics(){
        Map<String, Integer> statistics = new TreeMap<>();

        // Count the ones currently in 'cars'
        for (Car value : cars.values()) {
            statistics.put(value.registration,
                    statistics.getOrDefault(value.registration, 0) + 1);
            statistics.putIfAbsent(value.spot, 0);
           // statistics.put(value.spot, statistics.getOrDefault(value.spot, 0) + 1);
        }

        // Also count the ones that have already left
        for (Car value : historyCars) {
            statistics.put(value.registration,
                    statistics.getOrDefault(value.registration, 0) + 1);
        }

        return statistics;
    }
    public Map<String,Double> spotOccupancy (LocalDateTime start, LocalDateTime end){
        Map<String, Double> spotMap = new HashMap<>();
        long duration= DateUtil.durationBetween(start, end);

        for (Car historyCar : historyCars) {
            String spot= historyCar.spot;
            spotMap.putIfAbsent(spot, 0.0);
            long minDuration= DateUtil.durationBetween(historyCar.startDate, historyCar.endDate);
            spotMap.put(spot, spotMap.get(spot) + minDuration);
        }

        for (Car value : cars.values()) {
            String spot = value.spot;
            spotMap.putIfAbsent(spot, 0.0);
            long minDuration= DateUtil.durationBetween(value.startDate, end);
            spotMap.put(spot, spotMap.get(spot) + minDuration);
        }

        for (Map.Entry<String, Double> stringDoubleEntry : spotMap.entrySet()) {
            spotMap.put(stringDoubleEntry.getKey(), (stringDoubleEntry.getValue() / duration * 100));
        }
        return spotMap;
    }
}

public class ParkingTesting {

    public static <K, V extends Comparable<V>> void printMapSortedByValue(Map<K, V> map) {
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.println(String.format("%s -> %s", entry.getKey().toString(), entry.getValue().toString())));

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int capacity = Integer.parseInt(sc.nextLine());

        Parking parking = new Parking(capacity);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equals("update")) {
                String registration = parts[1];
                String spot = parts[2];
                LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
                boolean entrance = Boolean.parseBoolean(parts[4]);
                parking.update(registration, spot, timestamp, entrance);
            } else if (parts[0].equals("currentState")) {
                System.out.println("PARKING CURRENT STATE");
                parking.currentState();
            } else if (parts[0].equals("history")) {
                System.out.println("PARKING HISTORY");
                parking.history();
            } else if (parts[0].equals("carStatistics")) {
                System.out.println("CAR STATISTICS");
                printMapSortedByValue(parking.carStatistics());
            } else if (parts[0].equals("spotOccupancy")) {
                LocalDateTime start = LocalDateTime.parse(parts[1]);
                LocalDateTime end = LocalDateTime.parse(parts[2]);
                printMapSortedByValue(parking.spotOccupancy(start, end));
            }
        }
    }
}
