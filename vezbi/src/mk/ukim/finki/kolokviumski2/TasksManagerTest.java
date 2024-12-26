package mk.ukim.finki.kolokviumski2;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(String message) {
        super(message);
    }
}


class Task {
    public String category;
    public String name;
    public String description;
    public LocalDateTime due;
    public Integer priority;

    public Task() {

    }

    @Override
    public String toString() {
        if (due != null && priority != null) {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", deadline=" + due +
                    ", priority=" + priority +
                    '}';
        } else if (due == null && priority != null) {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", priority=" + priority +
                    '}';
        } else if (due != null && priority == null) {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", deadline=" + due +
                    '}';
        } else {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDue() {
        return due;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDue(LocalDateTime due) {
        this.due = due;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public Duration getMomentalDuration() {
        if (due == null) {
            return Duration.ofSeconds(Long.MAX_VALUE); // Represents no deadline
        }
        return Duration.between(LocalDateTime.now(), due);
    }
}


class TaskManager {

    public Map<String, List<Task>> tasks;
    public List<Task> taskList;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.taskList = new ArrayList<>();
    }

    private boolean isValidDateTime(String dateTimeStr) {

        return dateTimeStr.contains("T");
    }


    public void readTasks(InputStream inputStream) throws DeadlineNotValidException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = br.lines().collect(Collectors.toList());

        for (String line : lines) {
            Task task = new Task();
            String[] parts = line.split(",", -1);

            String category = parts[0].trim();
            String name = parts[1].trim();
            String description = parts[2].trim();

            task.setCategory(category);
            task.setName(name);
            task.setDescription(description);

            LocalDateTime dueDateTime = null;
            Integer priority = null;

            if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                String fourthField = parts[3].trim();
                if (isValidDateTime(fourthField)) {
                    dueDateTime = LocalDateTime.parse(fourthField);
                    if (dueDateTime.toLocalDate().isBefore(LocalDate.of(2020, 6, 3))) {
                        throw new DeadlineNotValidException("The deadline " + dueDateTime + " has already passed.");
                    }
                    task.setDue(dueDateTime);
                } else {
                    priority = Integer.parseInt(fourthField);
                    task.setPriority(priority);
                }
            }

            if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                String fifthField = parts[4].trim();
                try {
                    priority = Integer.parseInt(fifthField);
                    task.setPriority(priority);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid priority format: " + fifthField);
                }
            }

            this.tasks.putIfAbsent(category, new ArrayList<>());
            this.tasks.get(category).add(task);
            this.taskList.add(task);
        }
    }


    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(os);

        if (includeCategory) {
            Map<String, List<Task>> groupedTasks = this.taskList.stream()
                    .collect(Collectors.groupingBy(Task::getCategory, TreeMap::new, Collectors.toList()));

            for (Map.Entry<String, List<Task>> entry : groupedTasks.entrySet()) {
                String category = entry.getKey();
                List<Task> tasksInCategory = entry.getValue();

                pw.println(category.toUpperCase());

                Comparator<Task> comparator = Comparator.comparing(Task::getMomentalDuration);
                if (includePriority) {
                    comparator = Comparator.comparing(Task::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(Task::getMomentalDuration);
                }

                tasksInCategory.sort(comparator);

                for (Task task : tasksInCategory) {
                    pw.println(task.toString());
                }
            }
        } else {
            Comparator<Task> comparator = Comparator.comparing(Task::getMomentalDuration);
            if (includePriority) {
                comparator = Comparator.comparing(Task::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Task::getMomentalDuration);
            }

            List<Task> sortedTasks = new ArrayList<>(this.taskList);
            sortedTasks.sort(comparator);

            for (Task task : sortedTasks) {
                pw.println(task.toString());
            }
        }

        pw.flush();
    }

}

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        try {
            manager.readTasks(System.in);
        } catch (DeadlineNotValidException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}
