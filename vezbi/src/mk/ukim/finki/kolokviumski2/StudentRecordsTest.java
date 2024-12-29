package mk.ukim.finki.kolokviumski2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


class RecStudent {
    public String code;
    public String major;
    public List<Integer> grades;

    public String getCode() {
        return code;
    }

    public RecStudent(String code, String major, List<Integer> grades) {
        this.code = code;
        this.major = major;
        this.grades = grades;
    }

    public double averageGrade() {
        return grades.stream().mapToDouble(i -> i).average().orElse(0.0);
    }

    public int get10s() {
        return grades.stream().filter(grade -> grade == 10).mapToInt(i -> 1).sum();
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", code, averageGrade());
    }
}

class StudentRecords {

    Map<String, List<RecStudent>> map;

    public StudentRecords() {
        map = new TreeMap<>();
    }

    public int readRecords(InputStream inputStream) {
        int counter = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = br.lines().collect(Collectors.toList());

        for (String line : lines) {
            String[] parts = line.split("\\s+");
            String code = parts[0];
            String major = parts[1];
            List<Integer> grades = new ArrayList<>();
            for (int i = 2; i < parts.length; i++) {
                grades.add(Integer.parseInt(parts[i]));
            }
            RecStudent student = new RecStudent(code, major, grades);
            map.putIfAbsent(major, new ArrayList<>());
            map.get(major).add(student);
            counter++;
        }
        return counter;
    }

    public void writeTable(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        map.forEach((k, v) -> {
            v.sort(Comparator.comparing(RecStudent::averageGrade, Comparator.reverseOrder()).thenComparing(RecStudent::getCode));
            System.out.println(k);
            v.forEach(st -> System.out.println(st.toString()));
        });
        pw.flush();
    }

    public void writeDistribution(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        map.entrySet().stream().sorted(Comparator.comparing((Map.Entry<String, List<RecStudent>> entry) -> entry.getValue().stream().mapToInt(RecStudent::get10s).sum()).reversed())
                .forEach(entry -> {
                    String major = entry.getKey();
                    List<RecStudent> students = entry.getValue();
                    Map<Integer, Long> distribution = students.stream().flatMap(student -> student.grades.stream()).collect(Collectors.groupingBy(
                            grade -> grade, Collectors.counting()
                    ));
                    pw.println(major);
                    distribution.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(e -> {
                                int grade = e.getKey();
                                long count = e.getValue();
                                String stars="";
                                if(count%10 != 0)
                                {
                                    stars = "*".repeat((int) (count / 10)+1);
                                }else{
                                    stars = "*".repeat((int) (count / 10));
                                }

                                pw.printf("%2d | %s(%d)%n", grade, stars, count);
                            });
                });

        pw.flush();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here