package mk.ukim.finki.kolokviumski1;


import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class Lap{
    public int minutes;
    public int seconds;
    public int miliseconds;

    public Lap(int minutes, int seconds, int miliseconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.miliseconds = miliseconds;
    }
    public int getTotalMiliseconds()
    {
        return miliseconds + seconds*1000 + minutes*60*1000;
    }

    @Override
    public String toString() {
        return String.format("%1d:%02d:%03d", minutes, seconds, miliseconds);
    }
}

class Pilot{
    public String name;
    public List<Lap> laps;
    public Pilot(String line){
        laps = new ArrayList<Lap>();
        //Webber <-- parts[0]
        // 2:32:103 <-- parts[1]
        // 2:49:182 <-- parts[2]
        // 2:18:132 <-- parts[3]
        String []parts=line.split(" ");
        this.name=parts[0];
        for(int i=1;i<parts.length;i++){
            String []timeParts=parts[i].split(":");
            int minutes=Integer.parseInt(timeParts[0]);
            int seconds=Integer.parseInt(timeParts[1]);
            int miliseconds=Integer.parseInt(timeParts[2]);
            laps.add(new Lap(minutes,seconds,miliseconds));
        }
    }
    public Lap getBestLap()
    {
         laps.sort(Comparator.comparing(Lap::getTotalMiliseconds));
        return laps.get(0);
    }

    @Override
    public String toString() {
        return String.format("%-10s%10s", name, getBestLap().toString());
    }
}

class F1Race{
    public List<Pilot> pilots;
    public F1Race() {
        pilots = new ArrayList<Pilot>();
    }
    public void readResults(InputStream inputStream){
        BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines=br.lines().collect(Collectors.toList());
        lines.forEach(line->pilots.add(new Pilot(line)));
        pilots.sort(Comparator.comparing(pilot -> pilot.getBestLap().getTotalMiliseconds()));
    }
    public void printSorted(OutputStream outputStream){
        PrintWriter pw = new PrintWriter(outputStream);
        AtomicInteger i=new AtomicInteger(1);
        for (Pilot pilot : pilots) {
            pw.println(String.format("%d. %s", i.getAndIncrement(), pilot.toString()));
        }
        pw.flush();
    }
}
public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}

