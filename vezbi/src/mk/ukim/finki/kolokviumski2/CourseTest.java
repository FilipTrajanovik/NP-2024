package mk.ukim.finki.kolokviumski2;//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;

class Student {
    public String index;
    public String name;
    public int pointsFirstExam;
    public int pointsSecondExam;
    public int pointsLabs;
    public int grade;

    public Student(String index, String name) {
        this.index = index;
        this.name = name;
        this.pointsFirstExam = 0;
        this.pointsSecondExam = 0;
        this.pointsLabs = 0;
        this.grade = 5;
    }

    public void update(String activity, int points) {
        if (activity.equals("midterm1")) {
            pointsFirstExam += points;
        } else if (activity.equals("midterm2")) {
            pointsSecondExam += points;
        } else if (activity.equals("labs")) {
            pointsLabs += points;
        }
        this.grade=getGrade();
    }

    public double getSummaryPoints() {
        return pointsFirstExam * 0.45 + pointsSecondExam * 0.45 + pointsLabs;

    }


    public int getGrade() {
        double points = getSummaryPoints();
        if (points >= 50 && points < 60) {
            grade = 6;
        } else if (points >= 60 && points < 70) {
            grade = 7;
        } else if (points >= 70 && points < 80) {
            grade = 8;
        } else if (points >= 80 && points < 90) {
            grade = 9;
        } else if (points >= 90 && points <= 100) {
            grade = 10;
        } else {
            grade = 5;
        }
        return grade;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                index, name, pointsFirstExam, pointsSecondExam, pointsLabs, getSummaryPoints(), grade);
    }
}

class AdvancedProgrammingCourse {
    public Map<String, Student> students;

    public AdvancedProgrammingCourse() {
        students = new HashMap<>();
    }

    public void addStudent(Student s) {
        students.put(s.index, s);
    }

    public void updateStudent(String idNumber, String activity, int points) {
        students.get(idNumber).update(activity, points);
    }

    public List<Student> getFirstNStudents(int n) {
        Comparator<Student> comparator = Comparator.comparing(Student::getSummaryPoints).reversed();
        return students.values().stream().sorted(comparator).limit(n).collect(Collectors.toList());

    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> distribution = new TreeMap<>();
        students.values().forEach(student -> {
            int grade = student.getGrade();
            distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
        });
        distribution.putIfAbsent(5, 0);
        distribution.putIfAbsent(6, 0);
        distribution.putIfAbsent(7, 0);
        distribution.putIfAbsent(8, 0);
        distribution.putIfAbsent(9, 0);
        distribution.putIfAbsent(10, 0);

        return distribution;
    }

    public void printStatistics() {
        DoubleSummaryStatistics statistics = students.values().stream().filter(student -> student.grade >= 6).mapToDouble(Student::getSummaryPoints).summaryStatistics();
        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f", statistics.getCount(), statistics.getMin(), statistics.getAverage(), statistics.getMax());

    }
}

public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}
