package mk.ukim.finki.kolokviumski1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class ShapesTest {
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
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}


enum Color {
    RED, GREEN, BLUE
}

interface Scalable {
    void scale(float scaleFactor);
}

interface Stackable {
    float weight();
}


abstract class Shape1 implements Stackable, Scalable {
    public String id;
    public Color color;

    public Shape1(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("%-5s%-10s%10.2f", id, color, weight());
    }
}

class Circle1 extends Shape1 {

    public float radius;

    public Circle1(String id, Color color, float radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        radius *= scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (radius * radius * Math.PI);
    }

    @Override
    public String toString() {
        return "C: " + super.toString();
    }
}


class Square1 extends Shape1 {

    public float width;
    public float height;

    public Square1(String id, Color color, float width, float height) {
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
        return width * height;
    }

    @Override
    public String toString() {
        return "R: " + super.toString();
    }
}

class Canvas {
    List<Shape1> shapes;

    public Canvas() {
        shapes = new ArrayList<>();
    }

    public void add(String id, Color color, float radius) {
        Circle1 c=new Circle1(id, color, radius);
        addToCorrectPosition(c);
    }


    public void add(String id, Color color, float width, float height) {
        Square1 s=new Square1(id, color, width, height);
        addToCorrectPosition(s);
    }

    public void scale(String id, float scaleFactor) {
        for (Shape1 shape : shapes) {
            if(shape.id.equals(id)) {
                shape.scale(scaleFactor);
                shapes.remove(shape);
                addToCorrectPosition(shape);
                return;
            }
        }
    }
    private void addToCorrectPosition(Shape1 shape) {
        Optional<Shape1> optional=shapes.stream().filter(s->s.weight() < shape.weight() ).findFirst();
        if (optional.isPresent()) {
            shapes.add(shapes.indexOf(optional.get()),shape);
        }else{
            shapes.add(shapes.size(),shape);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        shapes.forEach(s->sb.append(s.toString()).append("\n"));
        return sb.toString();
    }
}