package mk.ukim.finki.vezbiKniga;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Color {
    RED, GREEN, BLUE
}

abstract class Shape implements Scalable, Stackable {
    public String id;
    public Color color;

    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }
}

interface Scalable {
    void scale(float scaleFactor);
}

interface Stackable {
    float weight();
}

class Circle extends Shape {
    private float radius;

    public Circle(String id, Color color, float radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        radius *= scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (Math.PI * radius * radius);
    }

    @Override
    public String toString() {
        return String.format("C: [id:\\%5s] [color:\\%10s] [weight:\\%10.2f] ", id, color, weight());
    }
}

class Rectangle extends Shape {
    private float width;
    private float height;

    public Rectangle(String id, Color color, float width, float height) {
        super(id, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void scale(float scaleFactor) {
        width *= scaleFactor;
        height *= scaleFactor;
    }

    @Override
    public float weight() {
        return (float) width * height;
    }

    @Override
    public String toString() {
        return String.format("R: [id:\\%5s] [color:\\%10s] [weight:\\%10.2f] ", id, color, weight());
    }
}

class Canvas {
    public List<Shape> shapeList;

    public Canvas() {
        shapeList = new ArrayList<Shape>();
    }

    public int find(float weight) {
        for (int i = 0; i < shapeList.size(); i++) {
            if (weight == shapeList.get(i).weight()) {
                return i;
            }
        }
        return shapeList.size();
    }

    public void add(String id, Color color, float radius) {
        Circle c = new Circle(id, color, radius);
        this.shapeList.add(c);
    }

    public void add(String id, Color color, float width, float height) {
        Rectangle r = new Rectangle(id, color, width, height);
        this.shapeList.add(r);

    }

    public void scale(String id, float scaleFactor) {
        Shape s = null;
        for (int i = shapeList.size() - 1; i >= 0; i--) {
            s = shapeList.get(i);
            if (s.id.equals(id)) {
                s = shapeList.get(i);
                shapeList.remove(i);
                break;
            }
        }
        s.scale(scaleFactor);
        shapeList.add(s);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        shapeList.forEach(s -> sb.append(s.toString()));
        return sb.toString();
    }
}

public class CanvasTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println(" ORIGNAL :");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf(" AFTER SCALING : %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }
        }
    }
}