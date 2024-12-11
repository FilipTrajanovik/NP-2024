package mk.ukim.finki.kolokviumski;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class LineProcessor{
    public void readLines(InputStream is, OutputStream out, char c) throws IOException {
        Scanner scanner = new Scanner(is);
        PrintWriter pw=new PrintWriter(out);

        String maxLine="";
        int max=0;
        while(scanner.hasNextLine())
        {
            String line=scanner.nextLine();
            if(line.equals("END"))
            {
                break;
            }
            int count= (int) line.toLowerCase().chars().filter(bukva->bukva == Character.toLowerCase(c)).count();
            if(count  >= max)
            {
                max=count;
                maxLine=line;
            }
        }
        pw.println(maxLine);


        pw.flush();
    }

}
public class LineProcessorTest {
    public static void main(String[] args) {
        LineProcessor lineProcessor = new LineProcessor();

        try {
            lineProcessor.readLines(System.in, System.out, 'a');
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}