package mk.ukim.finki.kolokviumski2;

import java.util.*;


class Participant{
    String city;
    String code;
    String name;
    int age;

    public Participant(String city, String code, String name, int age) {
        this.city = city;
        this.code = code;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
       return String.format("%s %s %d", code, name, age);
    }
}

class Audition{
    public Comparator<Participant> comparator=Comparator.comparing((Participant p)->p.name).thenComparing((Participant p) -> p.age).thenComparing((Participant p) -> p.code);
    public Map<String, TreeSet<Participant>> participants;

    public Audition() {
        participants = new HashMap<>();
    }
    public void addParticpant(String city, String code, String name, int age)
    {
        participants.putIfAbsent(city, new TreeSet<>(comparator));
        participants.get(city).add(new Participant(city, code, name, age));
    }
    public void listByCity(String city){
        participants.get(city).stream().forEach(participant -> System.out.println(participant.toString()));
    }
}

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}