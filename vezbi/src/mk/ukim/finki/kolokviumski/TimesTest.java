package mk.ukim.finki.kolokviumski;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String s) {
        super(s);
    }
}
class InvalidTimeException extends Exception {
    public InvalidTimeException(String s) {
        super(s);
    }
}



class Time{
    public int hours;
    public int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getTotalMinutes(){
        return hours * 60 + minutes;
    }
    public String formatAM_PM(){
        String s="AM";
        if(hours == 0)
        {
            hours += 12;
            s="AM";
        }else if (hours >= 1 && hours <= 11)
        {
            s="AM";
        }else if(hours == 12)
        {
            s="PM";
        }else if(hours >= 13 && hours <= 23)
        {
            s="PM";
            hours-=12;
        }
        return String.format("%2d:%02d %s", hours, minutes, s);
    }
    public String pecati(TimeFormat format)
    {
        if(format.equals(TimeFormat.FORMAT_24))
        {
            return String.format("%2d:%02d", hours, minutes);
        }else{
            return formatAM_PM();
        }
    }
}
class TimeTable{
    public List<Time> times;
    public TimeTable() {
        times = new ArrayList<>();
    }
    public void readTimes(InputStream inputStream) throws UnsupportedFormatException, InvalidTimeException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines=br.lines().collect(Collectors.toList());
        for (String line : lines) {
            if(!(line.contains(":") || line.contains("."))) {
                throw new UnsupportedFormatException(String.format("UnsupportedFormatException: %s", line));
            }else{
                line=line.replace(".", ":");
                String []parts = line.split(":");
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                if(hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                    throw new InvalidTimeException("");
                }
                times.add(new Time(hours, minutes));
            }
        }

    }
    public void writeTimes(OutputStream outputStream, TimeFormat format){
        PrintWriter pw=new PrintWriter(new OutputStreamWriter(outputStream));
        times.sort(Comparator.comparing(Time::getTotalMinutes));
        times.forEach(time -> pw.println(time.pecati(format)));
        pw.flush();
    }

}

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}