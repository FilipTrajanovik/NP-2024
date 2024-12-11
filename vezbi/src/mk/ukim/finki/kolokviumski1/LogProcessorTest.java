package mk.ukim.finki.kolokviumski1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Log{
    public int id;
    public String text;

    public Log(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
       return String.format("%d:%s", id, text);
    }
}

class LogProcessorr{
    public List<Log> logs;
    public LogProcessorr() {
        logs = new ArrayList<Log>();
    }
    public void readLines(InputStream is, OutputStream os, int processId) throws IOException{
        Scanner scanner = new Scanner(is);
        while(scanner.hasNextLine()){

            String line = scanner.nextLine();

            if(line.equals("END"))
            {
                break;
            }
            String []parts=line.split(":");
            int id=Integer.parseInt(parts[0]);
            String text=parts[1];
            logs.add(new Log(id, text));
        }
        logs.sort(Comparator.comparing(i->i.text.length()));
        PrintWriter pw=new PrintWriter(new OutputStreamWriter(os));
        logs.stream().filter(log->log.id == processId).forEach(log->pw.println(log.toString()));
        pw.flush();
    }
}

public class LogProcessorTest {
    public static void main(String[] args) {
        LogProcessorr lineProcessor=new LogProcessorr();
        try{
            lineProcessor.readLines(System.in, System.out, 12334);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
