package mk.ukim.finki.kolokviumski2;
//ezMODE

import java.util.*;
import java.util.stream.Collectors;

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student2(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}
class Student2{
    private String index;
    private List<Integer> points;

    public Student2(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
    }
    public double summingPoints(){
        return points.stream().mapToInt(p->p).sum()/10.0;
    }

    public String getIndex() {
        return index;
    }

    public List<Integer> getPoints() {
        return points;
    }
    public boolean signatureTF(){
        return points.size()>=8;
    }
    public int yearOfStudies(){
        int y = Integer.parseInt(index.substring(0,2));
        return 20-y;
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f",index,signatureTF()?"YES":"NO",summingPoints());
    }
}
class LabExercises{
    List<Student2> students;

    public LabExercises() {
        students = new ArrayList<>();
    }

    public void addStudent (Student2 student){
        students.add(student);
    }
    public void printByAveragePoints (boolean ascending, int n){
        Comparator<Student2> comparator =
                Comparator.comparing(Student2::summingPoints)
                        .thenComparing(Student2::getIndex);
        if (!ascending)comparator = comparator.reversed();
        students.stream()
                .sorted(comparator)
                .limit(n)
                .forEach(student -> System.out.println(student));
    }
    public List<Student2> failedStudents (){
        return students.stream()
                .filter(student -> !student.signatureTF())
                .sorted(Comparator.comparing(Student2::getIndex).thenComparing(Student2::summingPoints))
                .collect(Collectors.toList());
    }
    public Map<Integer,Double> getStatisticsByYear(){
        return students.stream()
                .filter(Student2::signatureTF)
                .collect(Collectors.groupingBy(
                        Student2::yearOfStudies,
                        Collectors.averagingDouble(Student2::summingPoints)
                ));
    }
}