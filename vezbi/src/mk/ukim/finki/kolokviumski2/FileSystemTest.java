package mk.ukim.finki.kolokviumski2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Partial exam II 2016/2017
 */
class File implements Comparable<File> {
    public String name;
    public int size;
    public LocalDateTime dateCreated;
    public static Comparator<File> comparator=Comparator.comparing(File::getDateCreated).thenComparing(File::getName).thenComparing(File::getSize);
    public File(String name, int size, LocalDateTime dateCreated) {
        this.name = name;
        this.size = size;
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }
    public int getYear()
    {
        return dateCreated.getYear();
    }

    @Override
    public String toString() {
        return String.format("%-10s %5dB %s", name, size, dateCreated);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return size == file.size && Objects.equals(name, file.name) && Objects.equals(dateCreated, file.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, dateCreated);
    }

    @Override
    public int compareTo(File o) {
        return Comparator.comparing(File::getDateCreated).thenComparing(File::getName).thenComparing(File::getSize).compare(this, o);
    }
}

class Folder{
    public Set<File> files;

    public Folder() {
        this.files = new TreeSet<>(File.comparator);
    }
    public void addFile(File file) {
        this.files.add(file);
    }
}

class FileSystem{

    public Map<Character, Folder> folders;

    public FileSystem() {
        this.folders = new HashMap<>();
    }

    public void addFile(char folder, String name, int size, LocalDateTime createdAt){
      folders.putIfAbsent(folder, new Folder());
      folders.get(folder).addFile(new File(name, size, createdAt));
    }
    public List<File> findAllHiddenFilesWithSizeLessThen(int size){
        return folders.values().stream()
                .flatMap(folder->folder.files.stream().filter(file -> file.name.startsWith(".") && file.size < size))
                .collect(Collectors.toList());
    }
    public int totalSizeOfFilesFromFolders(List<Character> list){
        return folders.entrySet().stream().filter(f->list.contains(f.getKey()))
                .flatMapToInt(f->f.getValue().files.stream().mapToInt(i->i.size))
                .sum();
    }
    public Map<Integer, Set<File>> byYear(){
        Map<Integer, Set<File>> result=new HashMap<>();
        folders.values().forEach(folder->folder.files.forEach(file -> {
            result.putIfAbsent(file.getYear(), new TreeSet<>(File.comparator));
            result.get(file.getYear()).add(file);
        }));

        return result;
    }
    public Map<String, Long> sizeByMonthAndDay(){
        Map<String, Long>  result=new HashMap<>();
        folders.values().forEach(folder->folder.files.forEach(file->{
            result.putIfAbsent(file.dateCreated.getMonth() + "-" + file.dateCreated.getDayOfMonth(), 0L);
            result.put(file.dateCreated.getMonth() + "-" + file.dateCreated.getDayOfMonth(),result.get(file.dateCreated.getMonth() + "-" + file.dateCreated.getDayOfMonth()) + file.size);
        }));
        return result;
    }
}
public class FileSystemTest {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThen(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }
}

// Your code here

