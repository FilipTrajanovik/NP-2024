package mk.ukim.finki.av2;

import java.util.Objects;

class Date implements Comparable<Date> {
    private final static int FIRST_YEAR = 1800;
    private final static int LAST_YEAR = 2500;
    private final static int DAYS_IN_YEAR = 365;

    private static final int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int[] daysTillFirstOfMonth;
    public static final int[] daysTillFirstOfYear;

    public int days;

    static {
        daysTillFirstOfMonth = new int[12];

        for (int i = 1; i < 12; i++) {
            daysTillFirstOfMonth[i] = daysTillFirstOfMonth[i - 1] + daysInMonth[i];
        }

        int totalYears = LAST_YEAR - FIRST_YEAR + 1;
        daysTillFirstOfYear = new int[totalYears];

        int currentYear = FIRST_YEAR;
        for (int i = 1; i < totalYears; i++) {
            if (isLeap(currentYear)) {
                daysTillFirstOfYear[i] = daysTillFirstOfYear[i - 1] + DAYS_IN_YEAR + 1;
            } else {
                daysTillFirstOfYear[i] = daysTillFirstOfYear[i - 1] + DAYS_IN_YEAR;
            }
            currentYear++;
        }

    }

    public static boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public Date(int days) {
        this.days = days;
    }

    public Date(int day, int month, int year) {
        int days = 0;
        if (!isDateValid(year)) {
            throw new IllegalArgumentException("Invalid year");
        }
        days += daysTillFirstOfYear[year - FIRST_YEAR];
        days += daysTillFirstOfMonth[month - 1];
        if(isLeap(year) && month >=2) {
            days+=1;
        }
        days+=day;
        this.days = days;
    }

    private boolean isDateValid(int year) {
        return !(year < FIRST_YEAR || year > LAST_YEAR);
    }

    public Date incrementDate(int days){
        return new Date(this.days + days);
    }
    public int substractDate(Date date)
    {
        return Math.abs(this.days - date.days);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return days == date.days;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(days);
    }

    @Override
    public int compareTo(Date o) {
        return Integer.compare(this.days, o.days);
    }

    @Override
    public String toString() {
        int allDays=days;
        int i;
        for(i=0;i<daysTillFirstOfYear.length;i++){
            if(daysTillFirstOfYear[i]>=allDays){
                break;
            }
        }
        allDays-=daysTillFirstOfYear[i-1];

        int year=i-1+FIRST_YEAR;

        if(isLeap(year)){
            allDays--;
        }
        for(i=0;i<daysTillFirstOfMonth.length;i++){
            if(daysTillFirstOfMonth[i]>=allDays){
                break;
            }
        }
        allDays-=daysTillFirstOfMonth[i-1];
        int months=i-1;

        return allDays+"."+months+"."+year;
    }
    public static void main(String[] args) {
        Date sample = new Date(1, 10, 2012);
        System.out.println(sample.substractDate(new Date(1, 1, 2000)));
        System.out.println(sample);
        sample = new Date(1, 1, 1800);
        System.out.println(sample);
        sample = new Date(31, 12, 2500);
        System.out.println(daysTillFirstOfYear[daysTillFirstOfYear.length - 1]);
        System.out.println(sample.days);
        System.out.println(sample);
        sample = new Date(30, 11, 2012);
        System.out.println(sample);
        sample = sample.incrementDate(100);
        System.out.println(sample);
    }
}


