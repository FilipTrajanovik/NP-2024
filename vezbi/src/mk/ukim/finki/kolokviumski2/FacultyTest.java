package mk.ukim.finki.kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Exception thrown when an operation is not allowed.
 */
class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}

/**
 * Represents a student in the faculty.
 */
class FacultyStudent {
    private String id;
    private int yearsOfStudies;
    private Map<Integer, Map<String, Integer>> termGrade; // term -> (courseName -> grade)

    public FacultyStudent(String id, int yearsOfStudies) {
        this.id = id;
        this.yearsOfStudies = yearsOfStudies;
        this.termGrade = new HashMap<>();
    }

    /**
     * Calculates the total number of courses passed by the student.
     * A course is considered passed if the grade is greater than 5.
     *
     * @return the number of courses passed
     */
    public int coursesPassed() {
        return termGrade.values().stream()
                .flatMap(termMap -> termMap.values().stream())
                .filter(grade -> grade > 5)
                .mapToInt(grade -> 1)
                .sum();
    }

    /**
     * Calculates the average grade of all passed courses.
     *
     * @return the average grade, or 0 if no courses passed
     */
    public double average() {
        return termGrade.values().stream()
                .flatMap(termMap -> termMap.values().stream())
                .filter(grade -> grade > 5)
                .mapToInt(grade -> grade)
                .average()
                .orElse(0);
    }

    /**
     * Retrieves all attended courses, sorted lexicographically.
     *
     * @return a sorted list of course names
     */
    public List<String> getAllAttendedCourses() {
        return termGrade.values().stream()
                .flatMap(termMap -> termMap.keySet().stream())
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the student's ID.
     *
     * @return the student ID
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the number of years the student has been studying.
     *
     * @return the years of studies
     */
    public int getYearsOfStudies() {
        return yearsOfStudies;
    }

    /**
     * Retrieves the grades per term and course.
     *
     * @return the termGrade map
     */
    public Map<Integer, Map<String, Integer>> getTermGrade() {
        return termGrade;
    }

    @Override
    public String toString() {
        return String.format("Student: %s Courses passed: %d Average grade: %.2f", id, coursesPassed(), average());
    }
}


class Faculty {

    private Map<String, FacultyStudent> students; // Maps student ID to FacultyStudent
    private List<String> logs; // Logs for graduated students
    private Map<String, List<Integer>> courseMap; // Maps course name to list of grades

    public Faculty() {
        this.students = new LinkedHashMap<>(); // Preserves insertion order
        this.logs = new ArrayList<>();
        this.courseMap = new HashMap<>();
    }


    public void addStudent(String id, int yearsOfStudies) {
        if (students.containsKey(id)) {
            logs.add(String.format("Attempted to add existing student ID: %s", id));
            return;
        }
        FacultyStudent student = new FacultyStudent(id, yearsOfStudies);
        students.put(id, student);
    }


    public void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        FacultyStudent student = students.get(studentId);
        if (student == null) {
            throw new OperationNotAllowedException("Student with ID " + studentId + " does not exist.");
        }

        // Determine maximum term based on years of studies
        int maxTerm = (student.getYearsOfStudies() == 3) ? 6 : 8;
        if (term < 1 || term > maxTerm) {
            throw new OperationNotAllowedException("Term " + term + " is not possible for student with ID " + studentId);
        }

        // Initialize the term if it doesn't exist
        student.getTermGrade().putIfAbsent(term, new HashMap<>());

        // Check if the term already has 3 courses
        if (student.getTermGrade().get(term).size() >= 3) {
            throw new OperationNotAllowedException("Student " + studentId + " already has 3 grades in term " + term);
        }

        // Check if the course already exists in the term
        if (student.getTermGrade().get(term).containsKey(courseName)) {
            throw new OperationNotAllowedException("Course " + courseName + " already exists for term " + term + " for student " + studentId);
        }

        // Add the grade to the student's term
        student.getTermGrade().get(term).put(courseName, grade);

        // Update courseMap for printCourses
        courseMap.putIfAbsent(courseName, new ArrayList<>());
        courseMap.get(courseName).add(grade);


        // Check for graduation
        int requiredCourses = (student.getYearsOfStudies() == 3) ? 18 : 24;
        if (student.coursesPassed() >= requiredCourses) {
            double averageGrade = student.average();
            logs.add(String.format("Student with ID %s graduated with average grade %.2f in %d years.", studentId, averageGrade, student.getYearsOfStudies()));
            // Remove student upon graduation
            students.remove(studentId);
        }
    }

    /**
     * Retrieves the faculty's operation logs.
     *
     * @return a string containing all logs
     */
    public String getFacultyLogs() {
        return String.join("\n", logs);
    }

    /**
     * Generates a detailed report for a specific student.
     *
     * @param id the student's ID
     * @return a string containing the detailed report
     */
    public String getDetailedReportForStudent(String id) {
        FacultyStudent student = students.get(id);
        if (student == null) {
            return "Student " + id + " not found!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(id).append("\n");

        for (Map.Entry<Integer, Map<String, Integer>> entry : student.getTermGrade().entrySet()) {
            int term = entry.getKey();
            Map<String, Integer> courses = entry.getValue();
            double averageTerm = courses.values().stream()
                    .filter(grade -> grade > 5)
                    .mapToDouble(grade -> grade)
                    .average()
                    .orElse(5.0);
            sb.append(String.format("Term %d:\n", term));
            sb.append(String.format("Courses for term: %d\n", courses.size()));
            sb.append(String.format("Average grade for term: %.2f\n", averageTerm));
        }

        sb.append(String.format("Average grade: %.2f\n", student.average()));

        // Collect all attended courses sorted lexicographically
        List<String> allCourses = student.getAllAttendedCourses();
        String coursesAttended = String.join(", ", allCourses);
        sb.append(String.format("Courses attended: %s\n", coursesAttended));

        return sb.toString();
    }

    /**
     * Prints the first N students based on the number of courses passed and average grade.
     *
     * @param n the number of students to print
     */
    public void printFirstNStudents(int n) {
        Comparator<FacultyStudent> comparator = Comparator
                .comparing(FacultyStudent::coursesPassed, Comparator.reverseOrder())
                .thenComparing(FacultyStudent::average, Comparator.reverseOrder());
        students.values().stream()
                .sorted(comparator)
                .limit(n)
                .forEach(System.out::println);
    }

    /**
     * Prints all courses with the number of students enrolled and average grades.
     * Sorted by the number of students descending, then by average grade descending.
     */
    public void printCourses() {
        // Calculate count of students and average grade for each course
        Map<String, Integer> courseCount = new HashMap<>();
        Map<String, Double> courseAverage = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entry : courseMap.entrySet()) {
            String courseName = entry.getKey();
            List<Integer> grades = entry.getValue();
            courseCount.put(courseName, grades.size());

            double avg = grades.stream()
                    .mapToInt(g -> g)
                    .average()
                    .orElse(0);
            courseAverage.put(courseName, avg);
        }

        // Sort the courses as per the requirements
        List<String> sortedCourses = courseCount.keySet().stream()
                .sorted(Comparator
                        .comparing((String c) -> courseCount.get(c), Comparator.reverseOrder())
                        .thenComparing(courseAverage::get, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // Print the courses
        for (String courseName : sortedCourses) {
            System.out.printf("%s %d %.2f\n", courseName, courseCount.get(courseName), courseAverage.get(courseName));
        }
    }
}

/**
 * Contains test cases to validate the functionalities of the Faculty and FacultyStudent classes.
 */
public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        sc.nextLine(); // Consume the newline

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), (i % 5) + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase == 10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int gradeInput = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), gradeInput);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase == 11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            // Adding students 1 to 10
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        if (!sc.hasNextInt()) break; // Prevent infinite loop if insufficient input
                        int gradeInput = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), gradeInput);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }

            // Adding students 11 to 14
            for (int i = 11; i < 15; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        if (!sc.hasNextInt()) break; // Prevent infinite loop if insufficient input
                        int gradeInput = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), gradeInput);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            String report = faculty.getDetailedReportForStudent("student11");
            if (report.equals("Student student11 not found!")) {
                System.out.println("The graduated students are really deleted");
            } else {
                System.out.println(report);
                System.out.println("The graduated students should be deleted!!!");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
