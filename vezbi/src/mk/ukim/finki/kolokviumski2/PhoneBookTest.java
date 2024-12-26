package mk.ukim.finki.kolokviumski2;

import java.util.*;


class DuplicateNumberException extends Exception {
    public DuplicateNumberException(String s) {
        super(s);
    }
}
class Contact{
    public String name;
    public String phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String toString() {
       return String.format("%s %s", name, phone);
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}

class PhoneBook{

    public Map<String, String> map;

    public PhoneBook() {
        this.map = new HashMap<>();
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
       // Contact contact=new Contact(name, number);
        if(map.containsKey(number)){
            throw new DuplicateNumberException("Duplicate number: " + number);
        }
        map.put(number, name);
    }
    public void contactsByNumber(String number){
        Comparator<Contact> comparator=Comparator.comparing(Contact::getName).thenComparing(Contact::getPhone);

        if(number.length() >= 3)
        {
            List<Contact> contactsToPrint = new ArrayList<>();
            map.forEach((key, value) -> {
                if (key.contains(number)) {
                    contactsToPrint.add(new Contact(value, key));
                }
            });
            contactsToPrint.sort(comparator);

            //System.out.println("NUM: " + number);
            if(!contactsToPrint.isEmpty()){
                contactsToPrint.forEach(System.out::println);
            }else{
                System.out.println("NOT FOUND");
            }
        }

    }
    public void contactsByName(String name){
        Comparator<Contact> comparator=Comparator.comparing(Contact::getName).thenComparing(Contact::getPhone);
        List<Contact> contactsToPrint = new ArrayList<>();
        map.forEach((key, value) -> {
            if (value.contains(name)) {
                contactsToPrint.add(new Contact(value, key));
            }
        });
        //System.out.println(name);
        contactsToPrint.sort(comparator);
        if(!contactsToPrint.isEmpty()){
            contactsToPrint.forEach(System.out::println);
        }else{
            System.out.println("NOT FOUND");
        }
    }
}

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

// Вашиот код овде

