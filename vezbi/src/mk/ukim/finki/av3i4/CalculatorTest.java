package mk.ukim.finki.av3i4;

import java.util.Scanner;

class Calculator{
    public double state;
    public boolean flag;

    public Calculator() {
        state = 0.0;
        flag = false;
    }
    public void print()
    {
        System.out.println("result= " + state);
    }

    public void execute(String command) throws Exception { // +5

        if(flag)
        {
            if(command.equalsIgnoreCase("y"))
            {
                state = 0.0;
                flag = false;
            }else{
                System.out.println("End of Program");
                System.exit(0);
            }
        }else{
            if(command.equals("r")){
                System.out.println("Final Result: "+ state);
                System.out.println("Again?(Y/N)");
                flag = true;
            }else{
                String operation= String.valueOf(command.charAt(0));
                double value=Double.parseDouble(String.valueOf(command.charAt(1)));
                switch(operation){
                    case "+": state+=value; break;
                    case "-": state-=value; break;
                    case "*": state*=value; break;
                    case "/": state/=value; break;
                    default:
                        throw new Exception("Invalid Operator, enter new one");

                }
            }
        }

    }

}
public class CalculatorTest {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Scanner sc = new Scanner(System.in);
        System.out.println("Calculator is on.");
        calculator.print();
        while(sc.hasNextLine()) {
            String command=sc.nextLine();
            try {

                calculator.execute(command);
                if(!command.equalsIgnoreCase("r"))
                    calculator.print();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
