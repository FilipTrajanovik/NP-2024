package mk.ukim.finki.kolokviumski2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

abstract class Employee{
    public String ID;
    public String level;
    public static final Comparator<Employee> EMPLOYEE_COMPARATOR =
            Comparator.comparing(Employee::calculateSalary).reversed()
                    .thenComparing(Employee::getLevel);

    public Employee(String ID, String level) {
        this.ID = ID;
        this.level = level;
    }
    public abstract double calculateSalary();
    public abstract String getLevel();
}

class HourlyEmployee extends Employee{

    public double hours;
    public double rate;
    public double regularHours;
    public double overtimeHours;
    public HourlyEmployee(String ID, String level, double hours, double rate) {
        super(ID, level);
        this.hours=hours;
        this.rate=rate;
        if(hours <= 40)
        {
            regularHours=hours;
            overtimeHours=0;
        }else{
            regularHours=40;
            overtimeHours=hours-40;
        }
    }


    @Override
    public double calculateSalary() {
        return (regularHours * rate + overtimeHours * rate * 1.5);
    }

    @Override
    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                ID, level, calculateSalary(), regularHours, overtimeHours);
    }
}

class FreelanceEmployee extends Employee{
    public List<Integer> tickets;
    public double ticketRate;
    public int ticketSum;
    public FreelanceEmployee(String ID, String level, List<Integer> tickets, double ticketRate) {
        super(ID, level);
        this.tickets = tickets;
        this.ticketRate=ticketRate;
        ticketSum=tickets.stream().mapToInt(t->t).sum();
    }

    @Override
    public double calculateSalary() {
        return ticketSum*ticketRate;
    }

    @Override
    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                ID, level, calculateSalary(), tickets.size(), ticketSum);
    }
}

class PayrollSystem{
    public Map<String, Double> hourlyRateByLevel;
    public Map<String, Double> ticketRateByLevel;
    public List<Employee> employees;
    public PayrollSystem(Map<String,Double> hourlyRateByLevel, Map<String,Double> ticketRateByLevel) {
        this.hourlyRateByLevel=hourlyRateByLevel;
        this.ticketRateByLevel=ticketRateByLevel;
        this.employees=new ArrayList<>();
    }
    public void readEmployees (InputStream is){
        Scanner scanner=new Scanner(is);
        while(scanner.hasNext()){
            String line=scanner.nextLine();
            String []parts=line.split(";");
            if(parts[0].equals("H") && parts.length == 4){
                String ID=parts[1];
                String level=parts[2];
                double hours=Double.parseDouble(parts[3]);
                double rate=hourlyRateByLevel.get(level);
                employees.add(new HourlyEmployee(ID, level, hours, rate));
            }else{
                String ID=parts[1];
                String level=parts[2];
                List<Integer> tickets=new ArrayList<>();
                for(int i=3;i<parts.length;i++){
                    int ticket=Integer.parseInt(parts[i]);
                    tickets.add(ticket);
                }
                double rate=ticketRateByLevel.get(level);
                employees.add(new FreelanceEmployee(ID, level, tickets, rate));
            }
        }
    }
    public Map<String, Set<Employee>> printEmployeesByLevels (OutputStream os, Set<String> levels){

        Map<String, Set<Employee>> levelToEmployees = new LinkedHashMap<>();
        levels.forEach(l->{
            employees.stream()
                    .filter(e->e.getLevel().equals(l))
                    .forEach(e->
                    {
                        levelToEmployees.putIfAbsent(l,new TreeSet<>(Employee.EMPLOYEE_COMPARATOR));
                        levelToEmployees.get(l).add(e);
                    });
        });
        return levelToEmployees;
    }
}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        levels.add("level10");
        for (int i=5;i<=9;i++) {
            levels.add("level"+i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: "+ level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });



    }
}