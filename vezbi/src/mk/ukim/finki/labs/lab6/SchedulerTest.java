package mk.ukim.finki.labs.lab6;

import java.util.*;

class Scheduler<T> {
    private TreeMap<Date, T> schedule;

    // Constructor
    public Scheduler() {
        schedule = new TreeMap<>();
    }

    // Add a new object to the scheduler
    public void add(Date d, T obj) {
        schedule.put(d, obj);
    }

    // Remove an object by date
    public boolean remove(Date d) {
        return schedule.remove(d) != null;
    }

    // Get the next object (closest future date)
    public T next() {
        Date now = new Date();
        return schedule.tailMap(now, false).values().stream().findFirst().orElse(null);
    }

    // Get the last object (closest past date)
    public T last() {Date now = new Date();
        return schedule.headMap(now, false).values().stream().reduce((first, second) -> second).orElse(null);

    }

    // Get all objects between two dates
    public ArrayList<T> getAll(Date begin, Date end) {
        ArrayList<T> result = new ArrayList<>();
        schedule.subMap(begin, true, end, true).values().forEach(result::add);
        return result;
    }

    // Get the object with the smallest date
    public T getFirst() {
        return schedule.isEmpty() ? null : schedule.firstEntry().getValue();
    }

    // Get the object with the largest date
    public T getLast() {
        return schedule.isEmpty() ? null : schedule.lastEntry().getValue();
    }
}

public class SchedulerTest {
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();

        if (k == 0) {
            Scheduler<String> scheduler = new Scheduler<>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime() - 7200000), jin.next());
            scheduler.add(new Date(now.getTime() - 3600000), jin.next());
            scheduler.add(new Date(now.getTime() - 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 7200000), jin.next());
            scheduler.add(new Date(now.getTime() + 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 3600000), jin.next());
            scheduler.add(new Date(now.getTime() + 18000000), jin.next());
            System.out.println(scheduler.getFirst());
            System.out.println(scheduler.getLast());
        }

        if (k == 3) { // Test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime() - 7200000), jin.next());
            scheduler.add(new Date(now.getTime() - 3600000), jin.next());
            scheduler.add(new Date(now.getTime() - 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 7200000), jin.next());
            scheduler.add(new Date(now.getTime() + 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 3600000), jin.next());
            scheduler.add(new Date(now.getTime() + 18000000), jin.next());
            System.out.println(scheduler.next());
            System.out.println(scheduler.last());

            ArrayList<String> res = scheduler.getAll(new Date(now.getTime() - 10000000), new Date(now.getTime() + 17000000));
            Collections.sort(res);
            for (String t : res) {
                String value = t.replace("UTC", "GMT");
                System.out.print(value + " , ");
            }
        }

        if (k == 4) { // Test Scheduler with Integer (complex)
            Scheduler<Integer> scheduler = new Scheduler<>();
            int counter = 0;
            ArrayList<Date> to_remove = new ArrayList<>();

            while (jin.hasNextLong()) {
                Date d = new Date(jin.nextLong());
                int i = jin.nextInt();
                if ((counter & 7) == 0) {
                    to_remove.add(d);
                }
                scheduler.add(d, i);
                ++counter;
            }
            jin.next();

            while (jin.hasNextLong()) {
                Date l = new Date(jin.nextLong());
                Date h = new Date(jin.nextLong());
                ArrayList<Integer> res = scheduler.getAll(l, h);
                Collections.sort(res);
                System.out.println(l.toString().replace("UTC","GMT") + " <: " + print(res).replace("UTC","GMT") + " >: " + h.toString().replace("UTC","GMT"));
            }

            System.out.println("test");
            ArrayList<Integer> res = scheduler.getAll(new Date(0), new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res).replace("UTC","GMT"));
            for (Date d : to_remove) {
                scheduler.remove(d);
            }
            res = scheduler.getAll(new Date(0), new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res).replace("UTC","GMT"));
        }
    }

    private static <T> String print(ArrayList<T> res) {
        if (res == null || res.isEmpty()) return "NONE";
        StringBuilder sb = new StringBuilder();
        for (T t : res) {
            sb.append(t).append(" , ");
        }
        return sb.substring(0, sb.length() - 3);
    }
}