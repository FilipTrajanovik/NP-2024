package mk.ukim.finki.labs.lab1;

import java.util.Scanner;
import java.util.stream.IntStream;

public class RomanConverterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        IntStream.range(0, n)
                .forEach(x -> System.out.println(RomanConverter.toRoman(scanner.nextInt())));
        scanner.close();
    }
}


class RomanConverter {

    public static String []romanianNumbers={"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X","IX", "V", "IV", "I"};
    public static  int []numbers={1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    public static String toRoman(int n) {
        StringBuilder sb=new StringBuilder();
        int i=0;
        while(n>0 && i<numbers.length)
        {
            if(n>=numbers[i])
            {
                sb.append(romanianNumbers[i]);
                n-=numbers[i];
            }else{
                i++;
            }
        }

        return sb.toString();
    }

}
