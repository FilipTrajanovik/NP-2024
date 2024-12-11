package mk.ukim.finki.kolokviumski;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class NonExistingItemException extends Exception {
    public NonExistingItemException(String msg) {
        super(msg);
    }
}

abstract class Archive{
    public int id;
    public LocalDate dateArchive;

    public Archive(int id) {
        this.id = id;
        this.dateArchive = LocalDate.now();
    }
   abstract public boolean checkToOpen(LocalDate date);
}
class LockedArchive extends Archive{
    public LocalDate dateToOpen;
    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    @Override
    public boolean checkToOpen(LocalDate date) {
        return date.isAfter(dateToOpen);
    }
}

class SpecialArchive extends Archive{
    public int totalOpens;
    public int maxOpens;

    public SpecialArchive(int id, int maxOpens) {
        super(id);
        totalOpens = 0;
        this.maxOpens = maxOpens;
    }

    @Override
    public boolean checkToOpen(LocalDate date) {
        if(totalOpens < maxOpens) {
            totalOpens++;
            return true;
        }
        return false;
    }
}

class ArchiveStore{
    public StringBuilder log;
    public List<Archive> archives;

    public ArchiveStore() {
        log = new StringBuilder();
        archives=new ArrayList<>();
    }
    public void archiveItem(Archive item, LocalDate date){
        archives.add(item);
        item.dateArchive = date;
        log.append(String.format("Item %s archived at %s", item.id, date)).append("\n");
    }
    public void openItem(int id, LocalDate date) throws NonExistingItemException {
        Archive archiveToOpen = null;
        for (Archive archive : archives) {
            if(archive.id == id)
            {
                archiveToOpen = archive;
                break;
            }
        }
        if(archiveToOpen == null){
            throw new NonExistingItemException(String.format("Item with id %s doesn't exist", id));
        }
        if(archiveToOpen.checkToOpen(date))
        {
            log.append(String.format("Item %s opened at %s", id, date)).append("\n");
        }else
        {
            if( archiveToOpen instanceof LockedArchive )
            {
                log.append(String.format("Item %s cannot be opened before %s", id, ((LockedArchive) archiveToOpen).dateToOpen)).append("\n");
            }else{
                log.append(String.format("Item %s cannot be opened more than %d times", id, ((SpecialArchive) archiveToOpen).maxOpens)).append("\n");
            }
        }
    }
    public String getLog(){
        return log.toString();
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}