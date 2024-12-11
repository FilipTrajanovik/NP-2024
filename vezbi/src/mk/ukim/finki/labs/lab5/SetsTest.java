package mk.ukim.finki.labs.lab5;

import java.util.*;

class AlreadyExistException extends Exception {
    public AlreadyExistException(String id) {
        super(String.format("Student with ID %s already exists", id));
    }
}

class Student implements Comparable<Student> {
    private String id;
    private List<Integer> grades;

    public Student(String id, List<Integer> grades) {
        this.id = id;
        this.grades = new ArrayList<>(grades);
    }

    public String getId() {
        return id;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    @Override
    public String toString() {
        return String.format("Student{id='%s', grades=%s}", id, grades);
    }

    @Override
    public int compareTo(Student other) {
        // Calculate average grades
        double avg1 = this.grades.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avg2 = other.grades.stream().mapToInt(Integer::intValue).average().orElse(0);

        // Primary: Compare by average grade (descending)
        int compareAvg = Double.compare(avg2, avg1);
        if (compareAvg != 0) {
            return compareAvg;
        }

        // Secondary: Compare by number of passed courses (descending)
        long passed1 = this.grades.stream().filter(grade -> grade > 5).count();
        long passed2 = other.grades.stream().filter(grade -> grade > 5).count();
        int compareCourses = Long.compare(passed2, passed1);
        if (compareCourses != 0) {
            return compareCourses;
        }

        // Tertiary: Compare by ID (descending)
        return other.id.compareTo(this.id);
    }
}

class StudentComparators {
    // Comparator for sorting by average grade
    public static Comparator<Student> byAverageGrade() {
        return (s1, s2) -> {
            double avg1 = s1.getGrades().stream().mapToInt(Integer::intValue).average().orElse(0);
            double avg2 = s2.getGrades().stream().mapToInt(Integer::intValue).average().orElse(0);

            // Primary: Compare by average grade (descending)
            int compareAvg = Double.compare(avg2, avg1);
            if (compareAvg != 0) {
                return compareAvg;
            }

            // Secondary: Compare by number of passed courses (descending)
            long passed1 = s1.getGrades().stream().filter(grade -> grade > 5).count();
            long passed2 = s2.getGrades().stream().filter(grade -> grade > 5).count();
            int compareCourses = Long.compare(passed2, passed1);
            if (compareCourses != 0) {
                return compareCourses;
            }

            // Tertiary: Compare by ID (descending)
            return s2.getId().compareTo(s1.getId());
        };
    }

    // Comparator for sorting by number of courses passed
    public static Comparator<Student> byCoursesPassed() {
        return (s1, s2) -> {
            long passed1 = s1.getGrades().stream().filter(grade -> grade > 5).count();
            long passed2 = s2.getGrades().stream().filter(grade -> grade > 5).count();

            // Primary: Compare by number of passed courses (descending)
            int compareCourses = Long.compare(passed2, passed1);
            if (compareCourses != 0) {
                return compareCourses;
            }

            // Secondary: Compare by average grade (descending)
            double avg1 = s1.getGrades().stream().mapToInt(Integer::intValue).average().orElse(0);
            double avg2 = s2.getGrades().stream().mapToInt(Integer::intValue).average().orElse(0);
            int compareAvg = Double.compare(avg2, avg1);
            if (compareAvg != 0) {
                return compareAvg;
            }

            // Tertiary: Compare by ID (descending)
            return s2.getId().compareTo(s1.getId());
        };
    }
}

class Faculty {
    private Map<String, List<Integer>> students;

    public Faculty() {
        students = new HashMap<>();
    }

    public void addStudent(String id, List<Integer> grades) throws AlreadyExistException {
        if (!students.containsKey(id)) {
            students.put(id, new ArrayList<>(grades));
        } else {
            throw new AlreadyExistException(id);
        }
    }

    public void addGrade(String id, int grade) {
        if (students.containsKey(id)) {
            students.get(id).add(grade);
        }
    }

    public Set<Student> getStudentsSortedByAverageGrade() {
        TreeSet<Student> studentsSortedByAverageGrade = new TreeSet<>(StudentComparators.byAverageGrade());
        for (Map.Entry<String, List<Integer>> entry : students.entrySet()) {
            String id = entry.getKey();
            List<Integer> grades = entry.getValue();
            studentsSortedByAverageGrade.add(new Student(id, grades));
        }
        return studentsSortedByAverageGrade;
    }

    public Set<Student> getStudentsSortedByCoursesPassed() {
        TreeSet<Student> studentsSortedByCoursesPassed = new TreeSet<>(StudentComparators.byCoursesPassed());
        for (Map.Entry<String, List<Integer>> entry : students.entrySet()) {
            String id = entry.getKey();
            List<Integer> grades = entry.getValue();
            studentsSortedByCoursesPassed.add(new Student(id, grades));
        }
        return studentsSortedByCoursesPassed;
    }
}

public class SetsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Faculty faculty = new Faculty();

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "addStudent":
                    String id = tokens[1];
                    List<Integer> grades = new ArrayList<>();
                    for (int i = 2; i < tokens.length; i++) {
                        grades.add(Integer.parseInt(tokens[i]));
                    }
                    try {
                        faculty.addStudent(id, grades);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case "addGrade":
                    String studentId = tokens[1];
                    int grade = Integer.parseInt(tokens[2]);
                    faculty.addGrade(studentId, grade);
                    break;

                case "getStudentsSortedByAverageGrade":
                    System.out.println("Sorting students by average grade");
                    Set<Student> sortedByAverage = faculty.getStudentsSortedByAverageGrade();
                    for (Student student : sortedByAverage) {
                        System.out.println(student);
                    }
                    break;

                case "getStudentsSortedByCoursesPassed":
                    System.out.println("Sorting students by courses passed");
                    Set<Student> sortedByCourses = faculty.getStudentsSortedByCoursesPassed();
                    for (Student student : sortedByCourses) {
                        System.out.println(student);
                    }
                    break;

                default:
                    break;
            }
        }

        scanner.close();
    }
}
