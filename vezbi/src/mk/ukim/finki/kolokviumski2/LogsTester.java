package mk.ukim.finki.kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

class Log {
    public String logType;
    public String message;
    public long timestamp;

    public Log(String logType, String message, long timestamp) {
        this.logType = logType;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getSeverity() {
        int severity = 0;
        if (logType.equals("INFO")) {
            severity = 0;
        } else if (logType.equals("WARN")) {
            severity = 1;
            if (message.contains("might") || message.contains("cause") || message.contains("error")) {
                severity += 1;
            }
        } else if (logType.equals("ERROR")) {
            severity = 3;
            if (message.contains("fatal")) {
                severity += 2;
            } else if (message.contains("exception")) {
                severity += 3;
            }
        }
        return severity;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

class Microservice {
    public String name;
    public List<Log> logs;

    public Microservice(String name) {
        this.name = name;
        this.logs = new ArrayList<Log>();
    }

    public void addLog(Log log) {
        logs.add(log);
    }

    public double averageSeverity() {
        return logs.stream().mapToDouble(Log::getSeverity).average().orElse(0.0);
    }

    public double sumSeverity() {
        return logs.stream().mapToDouble(Log::getSeverity).sum();
    }

}

class LogCollector {
    public Map<String, Set<Microservice>> map;

    public LogCollector() {
        map = new HashMap<>();
    }

    public void addLog(String line) {
        String[] parts = line.split("\\s+");
        String serviceName = parts[0];
        String microServiceName = parts[1];
        String logType = parts[2];
        StringBuilder message = new StringBuilder();
        for (int i = 3; i < parts.length - 1; i++) {
            message.append(parts[i]).append(" ");
        }
        long timestamp = Long.parseLong(parts[parts.length - 1]);

        Comparator<Microservice> comparator = Comparator.comparing(Microservice::averageSeverity).thenComparing(microservice -> microservice.name);
        map.putIfAbsent(serviceName, new TreeSet<>(comparator));
        Microservice microservice = map.get(serviceName).stream()
                .filter(m -> m.name.equals(microServiceName))
                .findFirst()
                .orElseGet(() -> {
                    Microservice newMicroservice = new Microservice(microServiceName);
                    map.get(serviceName).add(newMicroservice);
                    return newMicroservice;
                });

        Log log = new Log(logType, message.toString().trim(), timestamp);
        microservice.addLog(log);
    }


    public void printServicesBySeverity() {
        map.forEach((key, value) -> {
            // Calculate total severity and total logs
            double totalSeverity = value.stream()
                    .flatMap(m -> m.logs.stream())
                    .mapToDouble(Log::getSeverity)
                    .sum();

            int totalLogs = value.stream()
                    .mapToInt(m -> m.logs.size())
                    .sum();

            double averageSeverity = totalLogs == 0 ? 0.0 : totalSeverity / totalLogs;

            // Debugging output
            // System.out.printf("Service: %s, Total Severity: %.2f, Total Logs: %d%n", key, totalSeverity, totalLogs);

            System.out.printf("Count of microservices %d " +
                            "Total logs in service %d " +
                            "Average severity for all logs %.2f " +
                            "Average number of logs per microservice %.2f%n",
                    value.size(),
                    totalLogs,
                    averageSeverity,
                    value.stream().mapToInt(m -> m.logs.size()).average().orElse(0.0));
        });
    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String micro) {
        Map<Integer, Integer> distribution = new HashMap<>();
        Microservice microservice = map.get(service).stream().filter(microservice1 -> microservice1.name.equals(micro)).findFirst().orElse(null);
        if (microservice != null) {
            microservice.logs.forEach(log -> {
                int severity = log.getSeverity();
                distribution.put(severity, distribution.getOrDefault(severity, 0) + 1);
            });
        } else {
            Set<Microservice> ms = map.get(service);
            ms.stream().forEach(m -> m.logs.forEach(log -> {
                int severity = log.getSeverity();
                distribution.put(severity, distribution.getOrDefault(severity, 0) + 1);
            }));
        }
        return distribution;
    }

    public void displayLogs(String service, String micro, String order) {
        if (!map.containsKey(service)) {
            System.out.println("Service not found.");
            return;
        }

        // Find the microservice
        Microservice microservice = map.get(service).stream()
                .filter(m -> m.name.equals(micro))
                .findFirst()
                .orElse(null);

        // Check if the microservice exists
        if (microservice == null) {
            System.out.println("Microservice not found.");
            return;
        }

        // Sort and display logs based on the order
        List<Log> logs = new ArrayList<>(microservice.logs);
        Comparator<Log> comparator;

        switch (order) {
            case "NEWEST_FIRST":
                comparator = Comparator.comparing(Log::getTimestamp).reversed();
                break;
            case "OLDEST_FIRST":
                comparator = Comparator.comparing(Log::getTimestamp);
                break;
            case "MOST_SEVERE_FIRST":
                comparator = Comparator.comparing(Log::getSeverity).reversed();
                break;
            case "LEAST_SEVERE_FIRST":
                comparator = Comparator.comparing(Log::getSeverity);
                break;
            default:
                System.out.println("Invalid order specified.");
                return;
        }

        logs.stream()
                .sorted(comparator)
                .forEach(log -> System.out.printf("%s %d%n", log.message, log.timestamp));
    }
}

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order = null;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                collector.displayLogs(service, microservice, order);
            }
        }
    }
}