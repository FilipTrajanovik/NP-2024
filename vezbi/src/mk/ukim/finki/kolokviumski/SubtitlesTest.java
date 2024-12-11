package mk.ukim.finki.kolokviumski;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Vreme{
    public int hours;
    public int minutes;
    public int seconds;
    public int miliseconds;

    public Vreme(int hours, int minutes, int seconds, int miliseconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.miliseconds = miliseconds;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, miliseconds);
    }
    public void shift(int ms)
    {
        miliseconds+=ms;
        if(miliseconds > 999)
        {
            miliseconds-=1000;
            seconds++;
        }else if(miliseconds < 0)
        {
            miliseconds+=1000;
            seconds--;
        }
        if(seconds > 59)
        {
            seconds -=60;
            minutes++;
        }else if(seconds < 0)
        {
            seconds+=60;
            minutes--;
        }
        if(minutes > 59)
        {
            minutes-=60;
            hours++;
        }else if(minutes < 0)
        {
            minutes+=60;
            hours--;
        }
    }
}
class Subtitle{
    public int redenBr;
    public Vreme vremeOd;
    public Vreme vremeDo;
    public String tekst;

    public Subtitle(int redenBr, Vreme vremeOd, Vreme vremeDo, String tekst) {
        this.redenBr = redenBr;
        this.vremeOd = vremeOd;
        this.vremeDo = vremeDo;
        this.tekst = tekst;
    }

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append(redenBr).append("\n");
       sb.append(vremeOd).append(" --> ").append(vremeDo).append("\n");
       sb.append(tekst);
       return sb.toString();
    }
    public void shift(int ms)
    {
        vremeOd.shift(ms);
        vremeDo.shift(ms);
    }

}

class Subtitles{
    public List<Subtitle> subtitles;
    public Subtitles() {
        subtitles = new ArrayList<>();
    }
    public int loadSubtitles(InputStream inputStream) {
        int counter = 0;
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNextLine())
        {
            //17
            //00:01:53,468 <-- parts[0]
            // -->
            // 00:01:54,745 <-- parts[1]
            //All right.
            int redenBr=Integer.parseInt(scanner.nextLine());
            StringBuilder sb=new StringBuilder();
            String []parts=scanner.nextLine().split(" --> ");
            String []partsVremOd=parts[0].split("[:,]");
            String []partsVremDo=parts[1].split("[:,]");
            int hours=Integer.parseInt(partsVremOd[0]);
            int minutes=Integer.parseInt(partsVremOd[1]);
            int seconds=Integer.parseInt(partsVremOd[2]);
            int miliseconds=Integer.parseInt(partsVremOd[3]);

            Vreme vremeOd=new Vreme(hours,minutes,seconds,miliseconds);

            hours=Integer.parseInt(partsVremDo[0]);
            minutes=Integer.parseInt(partsVremDo[1]);
            seconds=Integer.parseInt(partsVremDo[2]);
            miliseconds=Integer.parseInt(partsVremDo[3]);

            Vreme vremeDo=new Vreme(hours,minutes,seconds,miliseconds);
            while(scanner.hasNextLine())
            {
                String line=scanner.nextLine();
                if(line.isEmpty())
                {
                    break;
                }
                sb.append(line).append("\n");
            }
            subtitles.add(new Subtitle(redenBr, vremeOd, vremeDo, sb.toString()));
            counter++;

        }
        return counter;
    }
    public void print(){
        subtitles.forEach(System.out::println);
    }
    public void shift(int ms){
        subtitles.forEach(s -> s.shift(ms));
    }


}

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}

// Вашиот код овде
