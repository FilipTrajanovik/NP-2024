package mk.ukim.finki.kolokviumski1;

import java.util.Scanner;

class ZeroDenominatorException extends Exception {
    public ZeroDenominatorException(String s) {
        super(s);
    }
}

class GenericFraction <T extends Number, U extends Number> {
    public T numerator;
    public U denominator;

    public GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
        this.numerator = numerator;
        if(denominator.doubleValue() == 0)
        {
            throw new ZeroDenominatorException("Denominator cannot be zero");
        }
        this.denominator = denominator;
    }
    public GenericFraction<Double, Double> add(GenericFraction<? extends Number, ? extends Number> gf){
        double broitel1=numerator.doubleValue();
        double imenitel1=denominator.doubleValue();

        double broitel2=gf.numerator.doubleValue();
        double imenitel2=gf.denominator.doubleValue();

        double zaednickiImenitel=imenitel1 * imenitel2;
        broitel1*=imenitel2;
        broitel2*=imenitel1;

        double zaednickiBroitel=broitel1 + broitel2;
        for(int i = (int) Math.max(zaednickiBroitel, zaednickiImenitel); i>1; i--)
        {
            if(zaednickiBroitel % i == 0 && zaednickiImenitel % i == 0)
            {
                zaednickiBroitel/=i;
                zaednickiImenitel/=i;
            }
        }
        try {
            return new GenericFraction<>(zaednickiBroitel, zaednickiImenitel);
        } catch (ZeroDenominatorException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public double toDouble(){
        return numerator.doubleValue()/denominator.doubleValue();
    }

    @Override
    public String toString() {
        return String.format("%.2f / %.2f", numerator.doubleValue(), denominator.doubleValue());
    }
}


public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<Double, Double>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<Float, Float>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<Integer, Integer>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<Integer, Integer>(n3, 0);
        } catch(ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

}

// вашиот код овде
