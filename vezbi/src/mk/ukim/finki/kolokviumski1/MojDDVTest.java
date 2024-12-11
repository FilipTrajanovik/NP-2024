package mk.ukim.finki.kolokviumski1;

import java.io.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

enum TAX_TYPE{
    A,B ,V
}

class AmountNotAllowedException extends Exception{
    public AmountNotAllowedException(String msg){
        super(msg);
    }
}

class Item{
    public int price;
    public TAX_TYPE type;

    public Item(int price, TAX_TYPE type) {
        this.price = price;
        this.type = type;
    }
    public double dodadenaVrednost(){
        double multiplier=0;
        if(type==TAX_TYPE.A){
            multiplier=0.18;
        }else if(type==TAX_TYPE.B){
            multiplier=0.05;
        }else{
            multiplier=0.0;
        }
        return price*multiplier;
    }
}

class Reciept{
    public String id;
    public List<Item> items;
    public Reciept(String line) throws AmountNotAllowedException {
        //140819 709 A 709 B 709 V 1628 A 1628 B 1628 V 680 V
        this.items=new ArrayList<>();
        String []parts=line.split("\\s+");
        this.id=parts[0];
        for(int i=1;i<parts.length;i+=2){
            int price=Integer.parseInt(parts[i]);
            TAX_TYPE type=TAX_TYPE.valueOf(parts[i+1]);

            this.items.add(new Item(price,type));
        }
        if(totalSum() > 30000)
        {
            throw new AmountNotAllowedException(String.format("Receipt with amount %d is not allowed to be scanned", (int)totalSum()));
        }
    }
    public int totalSum()
    {
        return items.stream().mapToInt(i->i.price).sum();
    }
    public double povratokDanok()
    {
        return items.stream().mapToDouble(i->i.dodadenaVrednost()*0.15).sum();
    }

    @Override
    public String toString() {
       return String.format("%10s\t%10d\t%10.5f", id, totalSum(), povratokDanok());
    }
}

class MojDDV{
    public List<Reciept> reciepts;
    public MojDDV() {
        reciepts = new ArrayList<>();
    }
    public void readRecords (InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines=reader.lines().collect(Collectors.toList());
        lines.forEach(line-> {
            try {
                reciepts.add(new Reciept(line));
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        });
    }
    public void printTaxReturns (OutputStream outputStream){
        PrintWriter pw=new PrintWriter(outputStream);
        reciepts.forEach(reciept -> pw.println(reciept.toString()));
        pw.flush();
    }
    public void printStatistics (OutputStream outputStream){
        PrintWriter pw=new PrintWriter(outputStream);
        DoubleSummaryStatistics statistics=reciepts.stream().mapToDouble(Reciept::povratokDanok).summaryStatistics();
        pw.println(String.format("min:\t%5.3f", statistics.getMin()));
        pw.println(String.format("max:\t%5.3f", statistics.getMax()));
        pw.println(String.format("sum:\t%5.3f", statistics.getSum()));
        pw.println(String.format("count:\t%d", statistics.getCount()));
        pw.println(String.format("avg:\t%5.3f", statistics.getAverage()));
        pw.flush();
    }
}

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}