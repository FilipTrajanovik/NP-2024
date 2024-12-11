package mk.ukim.finki.kolokviumski;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Risk1 {
    public void processAttacksData(InputStream is) {
        Scanner scanner = new Scanner(is);
        while (scanner.hasNext()) {
            int attackersCount = 0;
            int defendersCounter = 0;

            String line = scanner.nextLine();
            String[] parts = line.split(";");
            //5 3 4; <--parts[0] attacker
            // 2 4 1 <--parts[1] defender
            List<String> atts = List.of(parts[0].split(" "));
            List<String> defends = List.of(parts[1].split(" "));
            List<Integer> attsParsed = new ArrayList<>();
            List<Integer> defendsParsed = new ArrayList<>();
            atts.forEach(att -> attsParsed.add(Integer.parseInt(att)));
            defends.forEach(defend -> defendsParsed.add(Integer.parseInt(defend)));
            attsParsed.sort(Comparator.naturalOrder());
            defendsParsed.sort(Comparator.naturalOrder());
            for (int i = 0; i < attsParsed.size(); i++) {
                if (attsParsed.get(i)  > defendsParsed.get(i)) {

                    attackersCount++;
                } else {
                    defendersCounter++;
                }
            }
            System.out.println(attackersCount + " " + defendersCounter);
        }
    }
}

public class RiskTester1 {
    public static void main(String[] args) {
        Risk1 risk = new Risk1();
        risk.processAttacksData(System.in);
    }
}