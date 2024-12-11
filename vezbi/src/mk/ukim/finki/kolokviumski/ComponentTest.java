package mk.ukim.finki.kolokviumski;

import java.util.*;


class InvalidPositionException extends Exception {
    public InvalidPositionException(String s) {
        super(s);
    }
}
class Component {
    public String color;
    public int weight;
    public List<Component> componentList;
    public int position;
    public Component(String color, int weight){
        this.color = color;
        this.weight = weight;
        this.componentList = new ArrayList<Component>();
        position = 0;
    }
    public void addComponent(Component component){
        this.componentList.add(component);
        this.componentList.sort(Comparator.comparing(Component::getWeight).thenComparing(Component::getColor));
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
       return toStringHelper(0);
    }

    private String toStringHelper(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("---".repeat(index)).append(String.format("%d:%s", weight, color)).append("\n");;
       // sb.append(String.format("%d:%s", weight, color));
        for(Component component : componentList){
            sb.append(component.toStringHelper(index + 1));
        }
        return sb.toString();
    }
}

class Window {
    public String name;
    public List<Component> componentList;
    public Window(String name){
        this.name = name;
        this.componentList = new ArrayList<Component>();
    }
    public void addComponent(int position, Component component) throws InvalidPositionException {

        if(this.componentList.stream().anyMatch(comp -> comp.getPosition() == position)){
            throw new InvalidPositionException(String.format("Invalid position %d, already taken!", position));
        }
        component.setPosition(position);
        this.componentList.add(component);
        componentList.sort(Comparator.comparing(Component::getPosition));

    }
    public void changeColor (int weight, String color){
        changeColorHelper(weight, color, this.componentList);
    }
    public void changeColorHelper(int weight, String color, List<Component> componentList){
        if(componentList.isEmpty()){
            return;
        }
        for(Component component : componentList){
            if(component.getWeight() == weight){
                component.setColor(color);
            }
            changeColorHelper(weight, color, component.componentList);
        }
    }
    public void swichComponents(int pos1, int pos2){
        Component tmp1=componentList.stream().filter(i->i.getPosition() == pos1).findFirst().get();
        Component tmp2=componentList.stream().filter(i->i.getPosition() == pos2).findFirst().get();
        tmp1.setPosition(pos2);
        tmp2.setPosition(pos1);
        this.componentList.sort(Comparator.comparing(Component::getPosition));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW ").append(name).append("\n");
        componentList.forEach(component -> String.format("%d:%d:%s\n", component.getPosition(), component.getWeight(), component.getColor()));
        componentList.forEach(component -> sb.append(component.toString()).append("\n"));
        return sb.toString();
    }
}

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if (what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}

// вашиот код овде