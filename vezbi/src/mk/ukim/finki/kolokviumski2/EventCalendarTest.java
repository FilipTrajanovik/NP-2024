package mk.ukim.finki.kolokviumski2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class WrongDateException extends Exception {
    public WrongDateException(String message) {
        super(message);
    }
}

class Event {
    public String name;
    public String location;
    public LocalDateTime date;

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm");

    public Comparator<Event> comparator = Comparator.comparing(Event::getDate).thenComparing(Event::getName);


    public Event(String name, String location, LocalDateTime date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s at %s, %s", df.format(date), location, name);
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }
}

class EventCalendar {
    public int year;

    public Map<LocalDate, Set<Event>> events;
    public Map<Integer, Set<Event>> monthEvents;

    public DateTimeFormatter df = DateTimeFormatter.ofPattern("eee MMM dd HH:mm:ss 'UTC' yyyy");


    public EventCalendar(int year) {
        this.year = year;
        this.events = new HashMap<>();
        this.monthEvents = new HashMap<>();
    }

    public void addEvent(String name, String location, LocalDateTime date) throws WrongDateException {
        if (this.year != date.getYear()) {
            throw new WrongDateException("Wrong date: " + df.format(date));
        }
        Event event = new Event(name, location, date);
        LocalDate localDate=date.toLocalDate();
        events.putIfAbsent(localDate, new HashSet<>());
        events.get(localDate).add(event);

        monthEvents.putIfAbsent(date.getMonthValue(), new HashSet<>());
        monthEvents.get(date.getMonthValue()).add(event);
    }

    public void listEvents(LocalDateTime date) {
        Comparator<Event> comparator = Comparator.comparing(Event::getDate).thenComparing(Event::getName);
        if (!events.containsKey(date.toLocalDate())) {
            System.out.println("No events on this day!");
            return;
        }
        List<Event> list = new ArrayList<>(events.get(date.toLocalDate()));
        list.sort(comparator);
        list.forEach(l -> System.out.println(l.toString()));
    }

    public void listByMonth() {
        monthEvents.putIfAbsent(1, new HashSet<>());
        monthEvents.putIfAbsent(2, new HashSet<>());
        monthEvents.putIfAbsent(3, new HashSet<>());
        monthEvents.putIfAbsent(4, new HashSet<>());
        monthEvents.putIfAbsent(5, new HashSet<>());
        monthEvents.putIfAbsent(6, new HashSet<>());
        monthEvents.putIfAbsent(7, new HashSet<>());
        monthEvents.putIfAbsent(8, new HashSet<>());
        monthEvents.putIfAbsent(9, new HashSet<>());
        monthEvents.putIfAbsent(10, new HashSet<>());
        monthEvents.putIfAbsent(11, new HashSet<>());
        monthEvents.putIfAbsent(12, new HashSet<>());
       for(int i=1;i<=12;i++)
       {
           System.out.println(i + " : " + monthEvents.get(i).size());
       }
    }
}

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
//        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            LocalDateTime date = LocalDateTime.parse(parts[2], dtf);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        String nl = scanner.nextLine();
        LocalDateTime date = LocalDateTime.parse(nl, dtf);
//        LocalDateTime date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}

// vashiot kod ovde