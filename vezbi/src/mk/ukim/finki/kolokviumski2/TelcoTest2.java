package mk.ukim.finki.kolokviumski2;//package mk.ukim.finki.midterm;


import java.awt.*;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}

abstract class State {
    PhoneCall call;

    public State(PhoneCall call) {
        this.call = call;
    }

    abstract public void answer();

    abstract public void end();

    abstract public void hold();

    abstract public void resume();

}

class RingingState extends State {

    public RingingState(PhoneCall call) {
        super(call);
    }

    @Override
    public void answer() {
        call.startTime = call.timestamp;
        call.state = new AnswerState(call);
    }

    @Override
    public void end() {
        call.endTime = call.timestamp;
        call.state = new EndedState(call);
    }

    @Override
    public void hold() {
        //todo nothing
    }

    @Override
    public void resume() {
        //todo nothing

    }
}

class AnswerState extends State {

    public AnswerState(PhoneCall call) {
        super(call);
    }

    @Override
    public void answer() {
        //todo nothing
    }

    @Override
    public void end() {
        call.endTime = call.timestamp;
        call.state = new EndedState(call);
    }

    @Override
    public void hold() {
        call.holdStart = call.timestamp;
        call.state = new PauseState(call);
    }

    @Override
    public void resume() {
        //todo nothing

    }
}

class EndedState extends State {

    public EndedState(PhoneCall call) {
        super(call);
    }

    @Override
    public void answer() {
        //todo nothing

    }

    @Override
    public void end() {
        //todo nothing

    }

    @Override
    public void hold() {
        //todo nothing

    }

    @Override
    public void resume() {
        //todo nothing

    }
}

class PauseState extends State {


    public PauseState(PhoneCall call) {
        super(call);
    }

    @Override
    public void answer() {
        //todo nothing

    }

    @Override
    public void end() {
        call.endTime = call.timestamp;
        call.state = new EndedState(call);
    }

    @Override
    public void hold() {
        //todo nothing
    }

    @Override
    public void resume() {
        call.totalHoldTime += (call.timestamp-call.holdStart);
        call.state = new AnswerState(call);
    }
}

class PhoneCall {
    public String uuid;
    public String dialer;
    public String reciever;
    public long timestamp;

    long startTime;
    long endTime;

    long holdStart;
    long totalHoldTime;


    public State state;

    public PhoneCall(String uuid, String dialer, String reciever, long timestamp) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.reciever = reciever;
        this.timestamp = timestamp;
        this.state = new RingingState(this);
        this.totalHoldTime=0;
    }
    public long getDuration() {
        if (state instanceof EndedState) {
            return (endTime - startTime) - totalHoldTime;
        }
        return 0;
    }
}

class TelcoApp {

    public Map<String, PhoneCall> calls;

    public TelcoApp() {
        this.calls=new HashMap<>();
    }

    public void addCall(String uuid, String dialer, String receiver, long timestamp) {
        PhoneCall call = new PhoneCall(uuid, dialer, receiver, timestamp);
        calls.put(uuid, call);
    }

    public void updateCall(String uuid, long timestamp, String action) {
        PhoneCall call=calls.get(uuid);
        call.timestamp=timestamp;
        if(action.equals("ANSWER")){
            call.state.answer();
        }else if(action.equals("END")){
            call.state.end();
        }else if(action.equals("HOLD")){
            call.state.hold();
        }else{
            call.state.resume();
        }
    }

    public void printChronologicalReport(String phoneNumber) {
        calls.values().stream().filter(call -> call.dialer.equals(phoneNumber) || call.reciever.equals(phoneNumber))
                .sorted(Comparator.comparing(call->call.timestamp)).forEach(call->{
                    String role = call.dialer.equals(phoneNumber) ? "D" : "R";
                    String otherNumber = call.dialer.equals(phoneNumber) ? call.dialer : call.reciever;
                    String start = call.endTime > 0 ? String.valueOf(call.startTime) : "MISSED CALL";
                    String end = call.endTime > 0 ? String.valueOf(call.endTime) : "MISSED CALL";
                    String duration =  call.getDuration() > 0 ? DurationConverter.convert(call.getDuration()) : "00:00";
                    System.out.printf("%s %s %s %s %s\n", role, otherNumber, start, end, duration);
                });
    }

    public void printReportByDuration(String phoneNumber) {
        calls.values().stream().filter(call -> call.dialer.equals(phoneNumber) || call.reciever.equals(phoneNumber))
                .sorted(Comparator.comparing(PhoneCall::getDuration).reversed()).forEach(call->{
                    String role = call.dialer.equals(phoneNumber) ? "D" : "R";
                    String otherNumber = call.dialer.equals(phoneNumber) ? call.dialer : call.reciever;
                    String start = call.endTime > 0 ? String.valueOf(call.startTime) : "MISSED CALL";
                    String end = call.endTime > 0 ? String.valueOf(call.endTime) : "MISSED CALL";
                    String duration =  call.getDuration() > 0 ? DurationConverter.convert(call.getDuration()) : "00:00";
                    System.out.printf("%s %s %s %s %s\n", role, otherNumber, start, end, duration);
                });
    }

    public void printCallsDuration() {
        Map<String, Long> durations=new HashMap<>();
        for (PhoneCall value : calls.values()) {
            if(value.getDuration() > 0)
            {
                String key=value.dialer + " <-> " + value.reciever;
                durations.put(key, durations.getOrDefault(key, 0L) +value.getDuration());
            }
        }
        durations.forEach((key, duration) -> System.out.printf("%s %s%n", key, DurationConverter.convert(duration)));
    }
}


public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }

    }
}
