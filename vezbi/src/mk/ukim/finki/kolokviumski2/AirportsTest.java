package mk.ukim.finki.kolokviumski2;

import java.util.*;

class Flight {
    public String from;
    public String to;
    public int time; // Departure time in minutes from 0:00
    public int duration; // Duration in minutes

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    // Метод за пресметување на пристигнување време
    public int getArrivalTime() {
        return this.time + this.duration;
    }

    // Метод за дали пристигнува следниот ден
    public boolean isNextDay() {
        return getArrivalTime() >= 1440;
    }

    // Форматирање на време во HH:MM
    public String formatTime(int timeInMinutes) {
        int totalMinutes = timeInMinutes;
        int days = 0;
        if (timeInMinutes >= 1440) {
            days = timeInMinutes / 1440;
            totalMinutes = timeInMinutes % 1440;
        }
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (days > 0) {
            return String.format("%02d:%02d +%dd", hours, minutes, days);
        } else {
            return String.format("%02d:%02d", hours, minutes);
        }
    }

    // Форматирање на пристигнувачко време
    public String getFormattedArrivalTime() {
        if (isNextDay()) {
            return String.format("%02d:%02d +1d", (getArrivalTime() % 1440) / 60, (getArrivalTime() % 1440) % 60);
        } else {
            return String.format("%02d:%02d", getArrivalTime() / 60, getArrivalTime() % 60);
        }
    }

    // Форматирање на времетраење во XhYm
    public String formatDuration() {
        int hours = this.duration / 60;
        int minutes = this.duration % 60;
        return String.format("%dh%02dm", hours, minutes);
    }
}

class Airport {
    public String name;
    public String country;
    public String code;
    public int passengers;
    public List<Flight> flightsFrom;
    public List<Flight> flightsTo;

    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        this.flightsFrom = new ArrayList<>();
        this.flightsTo = new ArrayList<>();
    }
}

class Airports {

    public Map<String, Airport> airports;
    public List<Flight> flights; // Промениено од Map на List

    public Airports() {
        this.airports = new HashMap<>();
        this.flights = new ArrayList<>();
    }

    public void addAirport(String name, String country, String code, int passengers) {
        Airport airport = new Airport(name, country, code, passengers);
        airports.put(code, airport); // Клучот е кодот на аеродромот
    }

    public void addFlights(String from, String to, int time, int duration) {
        if (!airports.containsKey(from) || !airports.containsKey(to)) {
            // Ако еден од аеродромите не постои, нема да додадеме летот
            return;
        }
        Flight flight = new Flight(from, to, time, duration);
        flights.add(flight); // Додавање на летот во листата на сите летови

        // Додавање во flightsFrom на 'from' аеродромот
        Airport fromAirport = airports.get(from);
        fromAirport.flightsFrom.add(flight);

        // Додавање во flightsTo на 'to' аеродромот
        Airport toAirport = airports.get(to);
        toAirport.flightsTo.add(flight);
    }

    public void showFlightsFromAirport(String code) {
        if (!airports.containsKey(code)) {
            System.out.println("Аеродромот не постои.");
            return;
        }
        Airport airport = airports.get(code);
        System.out.printf("%s (%s)\n", airport.name, airport.code);
        System.out.println(airport.country);
        System.out.println(airport.passengers);

        List<Flight> flightsFrom = new ArrayList<>(airport.flightsFrom);

        // Сортирање најпрво лексикографски по 'to', потоа по 'time'
        flightsFrom.sort(Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime));

        // Бројач за номерирање на летовите
        int count = 1;
        for (Flight flight : flightsFrom) {
            String departureTime = String.format("%02d:%02d", flight.time / 60, flight.time % 60);
            String arrivalTime = flight.getFormattedArrivalTime();
            String duration = flight.formatDuration();

            if (flight.isNextDay()) {
                System.out.printf("%d. %s-%s %s-%s %s\n",
                        count++,
                        flight.from,
                        flight.to,
                        departureTime,
                        arrivalTime,
                        duration);
            } else {
                System.out.printf("%d. %s-%s %s-%s %s\n",
                        count++,
                        flight.from,
                        flight.to,
                        departureTime,
                        arrivalTime,
                        duration);
            }
        }
    }

    public void showDirectFlightsFromTo(String from, String to) {
        if (!airports.containsKey(from) || !airports.containsKey(to)) {
            System.out.println("No flights from " + from + " to " + to);
            return;
        }
        List<Flight> directFlights = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.from.equals(from) && flight.to.equals(to)) {
                directFlights.add(flight);
            }
        }

        if (directFlights.isEmpty()) {
            System.out.println("No flights from " + from + " to " + to);
            return;
        }

        // Сортирање по 'time'
        directFlights.sort(Comparator.comparingInt(Flight::getTime));

        // Прикажување на директните летови
        for (Flight flight : directFlights) {
            String departureTime = String.format("%02d:%02d", flight.time / 60, flight.time % 60);
            String arrivalTime = flight.getFormattedArrivalTime();
            String duration = flight.formatDuration();
            System.out.printf("%s-%s %s-%s %s\n",
                    flight.from,
                    flight.to,
                    departureTime,
                    arrivalTime,
                    duration);
        }
    }

    public void showDirectFlightsTo(String to) {
        if (!airports.containsKey(to)) {
            System.out.println("No flights to " + to);
            return;
        }
        List<Flight> flightsTo = new ArrayList<>(airports.get(to).flightsTo);

        if (flightsTo.isEmpty()) {
            System.out.println("No flights to " + to);
            return;
        }

        // Сортирање по 'time'
        flightsTo.sort(Comparator.comparingInt(Flight::getTime));

        // Прикажување на директните летови
        for (Flight flight : flightsTo) {
            String departureTime = String.format("%02d:%02d", flight.time / 60, flight.time % 60);
            String arrivalTime = flight.getFormattedArrivalTime();
            String duration = flight.formatDuration();
            System.out.printf("%s-%s %s-%s %s\n",
                    flight.from,
                    flight.to,
                    departureTime,
                    arrivalTime,
                    duration);
        }
    }
}

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %s =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %s TO %s =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %s =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}
