package mk.ukim.finki.kolokviumski2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class InvalidDimensionException extends Exception {
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

class InvalidIDException extends Exception {
    public InvalidIDException() {
        super("ID is not valid!");
    }
}

interface Form {
    double perimeter();

    double area();

    void scale(double coef);
}


class User {
    public String id;
    public int totalShapes;
    public double sumOfAreas;

    public User(String id) {
        this.id = id;
        totalShapes = 0;
        sumOfAreas = 0;
    }

    public void addShape() {
        totalShapes++;
    }

    public void addArea(double area) {
        sumOfAreas += area;
    }

    public String getId() {
        return id;
    }

    public int getTotalShapes() {
        return totalShapes;
    }

    public double getSumOfAreas() {
        return sumOfAreas;
    }
}

class Rectangle implements Form {

    public double width;
    public double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double perimeter() {
        return (width + height)*2;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public void scale(double coef) {
        width *= coef;
        height *= coef;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", width, height, area(), perimeter());
    }
}

class Square implements Form {

    public double side;

    public Square(double side) {
        this.side = side;
    }

    @Override
    public double perimeter() {
        return side * 4;
    }

    @Override
    public double area() {
        return side * side;
    }

    @Override
    public void scale(double coef) {
        side *= coef;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", side, area(), perimeter());
    }
}

class Circle implements Form {

    public double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double perimeter() {
        return 2 * radius * Math.PI;
    }

    @Override
    public double area() {
        return Math.pow(radius, 2) * Math.PI;
    }

    @Override
    public void scale(double coef) {
        radius *= coef;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, area(), perimeter());
    }
}

class Canvas {
    public Map<User, Set<Form>> userForms;
    public List<Form> forms;

    public Canvas() {
        userForms = new HashMap<>();
        forms = new ArrayList<>();
    }

    public void readShapes(InputStream is) throws InvalidIDException, InvalidDimensionException {
        Scanner sc = new Scanner(is);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String id = parts[0];
            String username = parts[1];
            if (username.length() != 6) {
                throw new InvalidIDException();
            }
            char[] chars = username.toCharArray();
            for (char c : chars) {
                if (!Character.isLetterOrDigit(c)) {
                    throw new InvalidIDException();
                }
            }
            User user = null;
            if (!userForms.containsKey(username)) {
                user = new User(username);
                userForms.put(user, new HashSet<>());
            } else {
                user = (User) userForms.get(username);
            }

            if (id.equals("1")) {
                double radius = Double.parseDouble(parts[2]);
                if (radius == 0) {
                    throw new InvalidDimensionException();
                }
                forms.add(new Circle(radius));
                userForms.get(user).add(new Circle(radius));
                user.addShape();
                user.addArea(2 * radius * Math.PI);
            }
            if (id.equals("2")) {
                double side = Double.parseDouble(parts[2]);
                if (side == 0) {
                    throw new InvalidDimensionException();
                }
                forms.add(new Square(side));
                userForms.get(user).add(new Square(side));
                user.addShape();
                user.addArea(side * side);
            }
            if (id.equals("3")) {
                double width = Double.parseDouble(parts[2]);
                double height = Double.parseDouble(parts[3]);
                if (width == 0 || height == 0) {
                    throw new InvalidDimensionException();
                }
                forms.add(new Rectangle(width, height));
                userForms.get(user).add(new Rectangle(width, height));
                user.addShape();
                user.addArea(width * height);
            }

        }
    }

    public void scaleShapes(String userID, double coef) {
        User user = userForms.keySet().stream()
                .filter(u -> u.getId().equals(userID))
                .findFirst()
                .orElse(null);
        Set<Form> forms = userForms.get(user);
        for (Form form : forms) {
            form.scale(coef);
        }
    }

    public void printAllShapes(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        Comparator<Form> comparator = Comparator.comparing(Form::area);
        userForms.values().stream().flatMap(Set::stream).sorted(comparator).forEach(form -> pw.println(form.toString()));
        pw.flush();
    }

    public void printByUserId(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        Comparator<User> comparator = Comparator.comparing(User::getTotalShapes).thenComparing(User::getSumOfAreas);
        Comparator<Form> comparator2 = Comparator.comparing(Form::area).reversed();
        userForms.keySet().stream().sorted(comparator).forEach(user -> {
            pw.println("Shapes of user: " + user.getId());
            userForms.get(user).stream().sorted(comparator2).forEach(form -> pw.println(form.toString()));
        });
        pw.flush();
    }

    public void statistics(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        DoubleSummaryStatistics statistics = forms.stream().mapToDouble(Form::area).summaryStatistics();
        pw.println("count: " + statistics.getCount());
        pw.println("sum: " + statistics.getSum());
        pw.println("min: " + statistics.getMin());
        pw.println("average: " + statistics.getAverage());
        pw.println("max: " + statistics.getMax());
        pw.flush();
    }
}

public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        try {
            canvas.readShapes(System.in);
        } catch (InvalidIDException | InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}