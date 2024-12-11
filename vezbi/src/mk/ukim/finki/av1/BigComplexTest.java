package mk.ukim.finki.av1;

import java.math.BigDecimal;

class BigComplex{
    private BigDecimal real;
    private BigDecimal imag;

    public BigComplex(BigDecimal real, BigDecimal imag){
        this.real = real;
        this.imag = imag;
    }

    public BigComplex add(BigComplex complex){
        return new BigComplex(real.add(complex.real), imag.add(complex.imag));
    }
    public BigComplex sub(BigComplex complex){
        return new BigComplex(real.subtract(complex.real), imag.subtract(complex.imag));
    }

    @Override
    public String toString() {
        return this.real + "," + this.imag;
    }
}


public class BigComplexTest{
    public static void main(String[] args) {
        BigComplex bigComplex=new BigComplex(new BigDecimal("1.2"), new BigDecimal("1.2"));
        System.out.println(bigComplex);
        BigComplex bigComplex2=new BigComplex(new BigDecimal("1.3"), new BigDecimal("1.3"));
        BigComplex zbir=bigComplex.add(bigComplex2);
        System.out.println();
        BigComplex zbir2=bigComplex.sub(bigComplex2);
        System.out.println(zbir2);
    }
}
