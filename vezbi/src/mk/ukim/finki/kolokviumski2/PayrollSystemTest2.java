package mk.ukim.finki.kolokviumski2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

class BonusNotAllowedException extends Exception {
    public BonusNotAllowedException(String msg) {
        super(msg);
    }
}

abstract class Employee2 {
    public String ID;
    public String level;
    public static final Comparator<Employee2> EMPLOYEE_COMPARATOR =
            Comparator.comparing(Employee2::calculateSalary).reversed()
                    .thenComparing(Employee2::getLevel);

    public Employee2(String ID, String level) {
        this.ID = ID;
        this.level = level;
    }

    public abstract double calculateSalary();

    public abstract String getLevel();
}

class HourlyEmployee2 extends Employee2 {

    public double hours;
    public double rate;
    public double regularHours;
    public double overtimeHours;

    public HourlyEmployee2(String ID, String level, double hours, double rate) {
        super(ID, level);
        this.hours = hours;
        this.hours = hours;
        this.rate = rate;
        if (hours <= 40) {
            regularHours = hours;
            overtimeHours = 0;
        } else {
            regularHours = 40;
            overtimeHours = hours - 40;
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

class FreelanceEmployee2 extends Employee2 {
    public List<Integer> tickets;
    public double ticketRate;
    public int ticketSum;

    public FreelanceEmployee2(String ID, String level, List<Integer> tickets, double ticketRate) {
        super(ID, level);
        this.tickets = tickets;
        this.ticketRate = ticketRate;
        ticketSum = tickets.stream().mapToInt(t -> t).sum();
    }

    @Override
    public double calculateSalary() {
        return ticketSum * ticketRate;
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

class PayrollSystem2 {
    public Map<String, Double> hourlyRateByLevel;
    public Map<String, Double> ticketRateByLevel;
    public List<Employee2> employees;

    public PayrollSystem2(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        this.employees = new ArrayList<>();
    }

    public Employee2 createEmployee(String line) throws BonusNotAllowedException {
        String[] parts = line.split(";");
        String ID = parts[1];
        String level = parts[2];
        String []partsBonus = line.split(" ");
        if(partsBonus[1]!=null)
        {
            if(partsBonus[1].contains("%")){
                partsBonus[1].replace("%", "");
                if(Integer.parseInt(partsBonus[1]) > 20){
                    throw new BonusNotAllowedException("Bonus not allowed");
                }
            }else{
                if(Integer.parseInt(partsBonus[1]) > 1000){
                    throw new BonusNotAllowedException("Bonus not allowed");
                }
            }

        }
        if (parts[0].equals("H") && parts.length == 4) {

            double hours = Double.parseDouble(parts[3]);
            double rate = hourlyRateByLevel.get(level);
            employees.add(new HourlyEmployee2(ID, level, hours, rate));
            return new HourlyEmployee2(ID, level, hours, rate);
        } else {

            List<Integer> tickets = new ArrayList<>();
            for (int i = 3; i < parts.length; i++) {
                int ticket = Integer.parseInt(parts[i]);
                tickets.add(ticket);
            }
            double rate = ticketRateByLevel.get(level);
            employees.add(new FreelanceEmployee2(ID, level, tickets, rate));
            return new FreelanceEmployee2(ID, level, tickets, rate);
        }
    }

    public Map<String, Double> getOvertimeSalaryForLevels() {
        return null;
    }

    public void printStatisticsForOvertimeSalary() {

    }

    public Map<String, Integer> ticketsDoneByLevel() {
        return null;
    }

    public Collection<Employee> getFirstNEmployeesByBonus(int n) {
        return null;
    }


    public Map<String, Set<Employee2>> printEmployeesByLevels(OutputStream os, Set<String> levels) {

        Map<String, Set<Employee2>> levelToEmployees = new LinkedHashMap<>();
        levels.forEach(l -> {
            employees.stream()
                    .filter(e -> e.getLevel().equals(l))
                    .forEach(e ->
                    {
                        levelToEmployees.putIfAbsent(l, new TreeSet<>(Employee2.EMPLOYEE_COMPARATOR));
                        levelToEmployees.get(l).add(e);
                    });
        });
        return levelToEmployees;
    }
}

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem2 ps = new PayrollSystem2(hourlyRateByLevel, ticketRateByLevel);
        Employee2 emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}