package mk.ukim.finki.labs.lab2;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

abstract class Contact {
    public String date;

    public Contact(String date) {
        this.date = date;
    }

    public boolean isNewerThan(Contact c) {
        LocalDate d1 = LocalDate.parse(this.date);
        LocalDate d2 = LocalDate.parse(c.date);
        return d1.isBefore(d2);
    }

    public abstract String getType();
}

class EmailContact extends Contact {
    public String email;

    public EmailContact(String date, String email) {
        super(date);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return "Email";
    }
}


enum Operator {VIP, ONE, TMOBILE}


class PhoneContact extends Contact {
    public String phone;

    public PhoneContact(String date, String phone) {
        super(date);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public Operator getOperator() {
        String prefix = phone.substring(0, 3);
        switch (prefix) {
            case "070":
                return Operator.TMOBILE;
            case "072":
                return Operator.TMOBILE;
            case "071":
                return Operator.TMOBILE;
            case "075":
                return Operator.ONE;
            case "076":
                return Operator.ONE;
            case "077":
                return Operator.VIP;
            case "078":
                return Operator.VIP;
            default:
                return null;
        }
    }

    @Override
    public String getType() {
        return "Phone";
    }
}

class Student {
    public String firstName;
    public String lastName;
    public String city;
    public int age;
    public long index;

    public int COUNT;

    public Contact[] contacts = new Contact[100];

    public Student(String firstName, String lastName, String city, int age, long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        this.COUNT = 0;

    }

    public void addEmailContact(String date, String email) {
        Contact e = new EmailContact(date, email);
        contacts[COUNT] = e;
        COUNT++;
    }

    public void addPhoneContact(String date, String phone) {
        Contact p = new PhoneContact(date, phone);
        contacts[COUNT] = p;
        COUNT++;
    }

    public Contact[] getEmailContacts() {
        int count = 0;
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                if (contacts[i].getType().equals("Email")) {
                    count++;
                }
            }
        }
        int f = 0;
        Contact[] contacts = new Contact[count];
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                if (contacts[i].getType().equals("Email")) {
                    contacts[f] = contacts[i];
                    f++;
                }
            }
        }
        return contacts;
    }

    public Contact[] getPhoneContacts() {
        int count = 0;
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                if (contacts[i].getType().equals("Phone")) {
                    count++;
                }
            }
        }
        int f = 0;
        Contact[] contacts = new Contact[count];
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                if (contacts[i].getType().equals("Phone")) {
                    contacts[f] = contacts[i];
                    f++;
                }
            }
        }
        return contacts;
    }

    public String getCity() {
        return city;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getIndex() {
        return index;
    }

    public Contact getLatestContact() {
        Contact newestContact = contacts[0];
        LocalDate newestDate = LocalDate.parse(newestContact.date);
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                Contact c = contacts[i];
                LocalDate cDate = LocalDate.parse(c.date);
                if (cDate.isAfter(newestDate)) {
                    newestContact = c;
                    newestDate = cDate;
                }
            }
        }
        return newestContact;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"ime\":").append(firstName).append(", ");
        sb.append("\"prezime\":").append(lastName).append(", ");
        sb.append("\"vozrast\":").append(age).append(", ");
        sb.append("\"grad\":").append(city).append(", ");
        sb.append("\"indeks\":").append(index).append(", ");
        sb.append("\"telefonskiKontakti:\":[");
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                if (i == contacts.length - 1) {
                    if (contacts[i].getType().equals("Phone")) {
                        PhoneContact p = (PhoneContact) contacts[i];
                        sb.append("\"").append(p.phone).append("\"");
                        break;
                    }
                }
                if (contacts[i].getType().equals("Phone")) {
                    PhoneContact p = (PhoneContact) contacts[i];
                    sb.append("\"").append(p.phone).append("\", ");
                }
            }
        }
        sb.append("],");
        sb.append("\"emailKontakti:\":[");
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] != null) {
                if (i == contacts.length - 1) {
                    if (contacts[i].getType().equals("Email")) {
                        PhoneContact p = (PhoneContact) contacts[i];
                        sb.append("\"").append(p.phone).append("\"");
                        break;
                    }
                }
                if (contacts[i].getType().equals("Email")) {
                    PhoneContact p = (PhoneContact) contacts[i];
                    sb.append("\"").append(p.phone).append("\", ");
                }
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}

class Faculty {
    public String name;
    public List<Student> students;

    public Faculty(String name, Student[] students) {
        this.name = name;
        this.students = Arrays.asList(students);
    }

    public int countStudentsFromCity(String city) {
        return (int) students.stream().filter(student -> student.getCity().compareTo(city) == 0).count();
    }

    public Student getStudent(long index) {
        return students.stream().filter(student -> student.getIndex() == index).findFirst().get();
    }

    public double getAverageNumberOfContacts() {
        int sum = 0;
        for (Student student : students) {
            sum += student.contacts.length;
        }
        return (double) sum / students.size();
    }

    public Student getStudentWithMostContacts() {
        Student maxStudent = this.students.getFirst();
        int maxContacts = maxStudent.contacts.length;
        long maxIndex = maxStudent.getIndex();
        for (Student student : students) {
            if (student.contacts.length > maxContacts) {
                maxStudent = student;
                maxContacts = student.contacts.length;
            } else if (student.contacts.length == maxContacts) {
                if (student.getIndex() > maxIndex) {
                    maxStudent = student;
                    maxContacts = maxStudent.contacts.length;
                    maxIndex = maxStudent.getIndex();
                }
            }
        }
        return maxStudent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"fakultet\":").append(name).append(", ");
        sb.append("\"studenti\":[");
        for (Student student : students) {
            if (student.equals(students.getLast())) {
                sb.append(student).append("]");
                break;
            }
            sb.append(student).append(", ");
        }
        sb.append("}");
        return sb.toString();
    }
}


public class ContactsTester {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhone()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}

