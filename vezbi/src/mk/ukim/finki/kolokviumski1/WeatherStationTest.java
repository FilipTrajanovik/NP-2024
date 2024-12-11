package mk.ukim.finki.kolokviumski1;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


class Measurment {
    public double temperature;
    public double humidity;
    public double wind;
    public double visibility;
    public Date date;

    public Measurment(double temperature, double humidity, double wind, double visibility, Date date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.visibility = visibility;
        this.date = date;
    }

    public int getMilis() {
        return (int) date.getTime();
    }

    @Override
    public String toString() {
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss 'GMT' yyyy");
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s", temperature, wind, humidity, visibility, formatter.format(date));
    }
}

class WeatherStation {
    public int days;
    public List<Measurment> measurments;

    public WeatherStation(int days) {
        this.days = days;
        this.measurments = new ArrayList<Measurment>();
    }

    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date) {
        Measurment m = new Measurment(temperature, humidity, wind, visibility, date);
        for (Measurment measurment : measurments) {
            long razlika = Math.abs(measurment.date.getTime() - date.getTime());
            if (razlika < 150000) {
                return;
            }
        }
        measurments.removeIf(i -> Math.abs(i.date.getTime() - m.date.getTime()) > 86400000L * days);
        measurments.add(m);
    }

    public int total() {
        return measurments.size();
    }

    public void status(Date from, Date to) throws RuntimeException {
        List<Measurment> result = new ArrayList<>();
        for (Measurment measurment : measurments) {
            if ((measurment.date.equals(from) || measurment.date.after(from)) &&
                    (measurment.date.equals(to) || measurment.date.before(to))) {
                result.add(measurment);
            }
        }
        if (result.isEmpty()) {
            throw new RuntimeException();
        }

        double temperature = result.stream().mapToDouble(i -> i.temperature).average().getAsDouble();
        result.sort(Comparator.comparing(i -> i.date.getTime()));
        result.forEach(System.out::println);
        System.out.println(String.format("Average temperature: %.2f", temperature));
    }

}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

// vashiot kod ovde