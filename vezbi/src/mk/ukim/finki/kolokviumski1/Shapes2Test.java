package mk.ukim.finki.kolokviumski1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class IrregularCanvasException extends Exception {
    public IrregularCanvasException(String message) {
        super(message);
    }
}

abstract class Shape {
    public String type;
    public double side;
    public double maxArea;

    public Shape(String type, double side, double maxArea) {
        this.type = type;
        this.side = side;
        this.maxArea = maxArea;
    }

    abstract public double getArea();
}

class Circle extends Shape {

    public Circle(String type, double side, double maxArea) {
        super(type, side, maxArea);
    }

    @Override
    public double getArea() {
        return side * side * Math.PI;
    }

}

class Square extends Shape {

    public Square(String type, double side, double maxArea) {
        super(type, side, maxArea);
    }

    @Override
    public double getArea() {
        return side * side;
    }
}

class Prozorec {
    public List<Shape> shapes;
    public double maxArea;
    public String canvasId;
    public int counterCircles;
    public int counterSquares;

    public Prozorec(String line, double maxArea) throws IrregularCanvasException {
        this.maxArea = maxArea;
        this.shapes = new ArrayList<>();
        this.counterCircles = 0;
        this.counterSquares = 0;

        //5960017f C 30 S 15 S 588 C 25 C 14 S 14 S 17 C 19

        String[] parts = line.split(" ");
        this.canvasId = parts[0];
        for (int i = 1; i < parts.length; i += 2) {
            String type = parts[i];
            double side = Double.parseDouble(parts[i + 1]);
            if (type.contains("C")) {
                Circle circle = new Circle(type, side, maxArea);
                if (circle.getArea() < maxArea) {
                    shapes.add(circle);
                    counterCircles++;
                } else {
                    throw new IrregularCanvasException(String.format("Canvas %s has a shape with area larger than %.2f", canvasId, maxArea));
                }
            }
            if (type.contains("S")) {
                Square square = new Square(type, side, maxArea);
                if (square.getArea() < maxArea) {
                    shapes.add(square);
                    counterSquares++;
                } else {
                    throw new IrregularCanvasException(String.format("Canvas %s has a shape with area larger than %.2f", canvasId, maxArea));
                }
            }

        }
    }

    public double totalArea() {
        return shapes.stream().mapToDouble(Shape::getArea).sum();
    }

    public double averageArea() {
        return shapes.stream().mapToDouble(Shape::getArea).average().getAsDouble();
    }

    public double maxArea() {
        return shapes.stream().mapToDouble(Shape::getArea).max().getAsDouble();
    }

    public double minArea() {
        return shapes.stream().mapToDouble(Shape::getArea).min().getAsDouble();
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %.2f %.2f %.2f", canvasId, shapes.size(), counterCircles, counterSquares, minArea(), maxArea(), averageArea());
    }
}


class ShapesApplication {
    public double maxArea;
    public List<Prozorec> canvas;

    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        canvas = new ArrayList<>();
    }

    public void readCanvases(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = br.lines().collect(Collectors.toList());
        lines.forEach(line -> {
            try {
                canvas.add(new Prozorec(line, maxArea));
            } catch (IrregularCanvasException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void printCanvases(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        canvas.sort(Comparator.comparing(Prozorec::totalArea).reversed());
        canvas.forEach(c -> pw.println(c.toString()));
        pw.flush();
    }
}

public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}