package mk.ukim.finki.kolokviumski2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

interface ILocation{
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}


class UserAlreadyExistException extends Exception{
    public UserAlreadyExistException(String message){
        super(message);
    }
}

class Korisnik {
    public String name;
    public String id;
    public List<ILocation> locations;
    public LocalDateTime dateRegistered;
    public boolean isSick;

    public Korisnik(String name, String id) {
        this.name = name;
        this.id=id;
        this.locations = new ArrayList<ILocation>();
        this.isSick=false;
    }

    public boolean isSick() {
        return isSick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ILocation> getLocations() {
        return locations;
    }

    public void setLocations(List<ILocation> locations) {
        this.locations = locations;
    }

    public LocalDateTime getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(LocalDateTime dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", name, id, dateRegistered);
    }
    public boolean isClose (Korisnik other)
    {
        if(closeEnoughTimes(other) != 0)
        {
            return true;
        }
        return false;
    }
    public int closeEnoughTimes(Korisnik o)
    {
        int counter=0;
        List<ILocation> locs=o.locations;
        for (ILocation location : this.locations) {
            for (ILocation loc : locs) {
                double q1 = location.getLatitude();
                double q2 = location.getLongitude();

                double p1 = loc.getLatitude();
                double p2 = loc.getLongitude();

                double euc=Math.sqrt(Math.pow(q2-q1,2)+Math.pow(p2-p1,2));
                Duration duration=Duration.between(loc.getTimestamp(),LocalDateTime.now());
                if(euc <= 2 || duration.getSeconds()<300)
                {
                    counter++;
                }
            }
        }
        return counter;
    }
}


class StopCoronaApp{


    public static Map<String, Korisnik> map;
    public Set<Korisnik> users;
    public StopCoronaApp() {
        map = new HashMap<>();
        users = new HashSet<>();
    }
    public void addUser(String name, String id) throws UserAlreadyExistException {
        if(!map.containsKey(id)) {
            throw new UserAlreadyExistException("UserIdAlreadyExistsException");
        }
        map.put(id, new Korisnik(name, id));

    }
    public void addLocations (String id, List<ILocation> iLocations) {

        map.get(id).setLocations(iLocations);
    }
    public void detectNewCase (String id, LocalDateTime timestamp){
        map.get(id).setDateRegistered(timestamp);
        users.add(map.get(id));
    }
    public  static Map<Korisnik, Integer> getDirectContacts (Korisnik u){
        return map.values().stream().filter(u::isClose).collect(Collectors.toMap(
                user->user, user -> user.closeEnoughTimes(u)
        ));
    }
    public static Collection<Korisnik> getIndirectContacts (Korisnik u){
        return getDirectContacts(u).keySet().stream().map(StopCoronaApp::getDirectContacts).map(Map::keySet).flatMap(Collection::stream).filter(us-> !getIndirectContacts(u).contains(us)).collect(Collectors.toList());
    }
    public void createReport (){
        for (Korisnik user : users) {
            System.out.println(user);
            System.out.println("Direct contacts:");
            long countDirect = StopCoronaApp.getDirectContacts(user).size();

            StopCoronaApp.getDirectContacts(user).entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(userIntegerEntry -> {
                        String name = userIntegerEntry.getKey().getName();
                        String firstFiveLettersId = userIntegerEntry.getKey().getId().substring(0,6);
                        int n = userIntegerEntry.getValue();
                        System.out.println(String.format("%s %s %d",name,firstFiveLettersId,n));
                    });
            System.out.println(String.format("Count of direct contacts: %d",countDirect));

            System.out.println("Indirect contacts:");
            long countInDirect = StopCoronaApp.getIndirectContacts(user).size();

            StopCoronaApp.getIndirectContacts(user).stream()
                    .sorted(Comparator.comparing(Korisnik::getName).thenComparing(Korisnik::getId))
                    .forEach(u -> {
                        String name = u.getName();
                        String firstFiveLettersId = u.getId() ;
                        System.out.println(String.format("%s %s",name,firstFiveLettersId));
                    });
            System.out.println(String.format("Count of indirect contacts: %d",countInDirect));

        }
    }

}

public class StopCoronaTest {

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
