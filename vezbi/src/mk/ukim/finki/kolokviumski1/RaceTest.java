package mk.ukim.finki.kolokviumski1;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Runner {
    public String id;
    public LocalTime startTime;
    public LocalTime endTime;

    public Runner(String id, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
}

class TeamRace {


    public TeamRace() {
    }

    public static void findBestTeam(InputStream is, OutputStream os) throws IOException {
        Scanner sc = new Scanner(is);
        List<Runner> runners = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            //1001 08:00:12 08:45:29
            String[] parts = line.split(" ");
            String id = parts[0];
            LocalTime startTime = LocalTime.parse(parts[1]);
            LocalTime endTime = LocalTime.parse(parts[2]);
            runners.add(new Runner(id, startTime, endTime));
        }

        PrintWriter pw = new PrintWriter(os);
        Duration totalDuration=Duration.ZERO;
        List<Runner> bestRunners = runners.stream().sorted(Comparator.comparing(Runner::getDuration)).limit(4).collect(Collectors.toList());
        if (bestRunners.size() < 4) {
            throw new IOException();
        }
        for (Runner runner : bestRunners) {
            totalDuration=totalDuration.plus(runner.getDuration());
        }
        bestRunners.forEach(runner -> pw.println(String.format("%s %02d:%02d:%02d", runner.id, runner.getDuration().toHoursPart(), runner.getDuration().toMinutesPart(), runner.getDuration().toSecondsPart())));
        pw.println(String.format("%02d:%02d:%02d", totalDuration.toHoursPart(), totalDuration.toMinutesPart(), totalDuration.toSecondsPart()));
        pw.flush();
    }
}

public class RaceTest {
    public static void main(String[] args) {
        try {
            TeamRace.findBestTeam(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}