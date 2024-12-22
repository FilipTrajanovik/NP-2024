package mk.ukim.finki.kolokviumski2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class QuizProcessor {
    public static  Map<String, Double> processAnswers(InputStream is){
        Map<String, Double> map = new TreeMap<>();

        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        
        List<String> lines=br.lines().collect(Collectors.toList());
        for (String line : lines) {
            String []parts=line.split(";");
            String id = parts[0];
            if(parts[1].length() != parts[2].length()){
                System.out.println("A quiz must have same number of correct and selected answers");
                continue;
            }

            double points=0.0;
            String []answers1=parts[1].split(", ");
            String []answers2=parts[2].split(", ");
            for(int i=0;i<answers1.length;i++){
                if(answers1[i].equals(answers2[i])){
                    points+=1;
                }else{
                    points-=0.25;
                }
            }
            map.put(id, points);
        }

        return map;
    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}