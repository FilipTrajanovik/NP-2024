package mk.ukim.finki.kolokviumski;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Risk{
    public int processAttacksData (InputStream is){
        Scanner scanner=new Scanner(is);
        int counter=0;
        while(scanner.hasNextLine()){
            int brojac=0;
            String line=scanner.nextLine();
            //5 3 4;2 4 1
            String []parts=line.split(";");
            List<String> att=List.of(parts[0].split(" "));
            List<String> deff=List.of(parts[1].split(" "));
            List<Integer> attParsed=new ArrayList<>();
            List<Integer> deffParsed=new ArrayList<>();
            for(int i=0;i<att.size();i++)
            {
                attParsed.add(Integer.parseInt(att.get(i)));
                deffParsed.add(Integer.parseInt(deff.get(i)));
            }
            attParsed.sort(Comparator.naturalOrder());
            deffParsed.sort(Comparator.naturalOrder());
            for(int i=0;i<att.size();i++)
            {
                if(attParsed.get(i) > deffParsed.get(i))
                {
                    brojac++;
                }
            }
            if(brojac == 3)
            {
                counter++;
            }
        }
        return counter;
    }
}

public class RiskTester {
    public static void main(String[] args) {

        Risk risk = new Risk();

        System.out.println(risk.processAttacksData(System.in));

    }
}