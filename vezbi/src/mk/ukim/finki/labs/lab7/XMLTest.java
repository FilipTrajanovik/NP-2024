package mk.ukim.finki.labs.lab7;

import java.util.*;

interface XMLComponent {
    void addAttribute(String key, String value);
    void addComponent(XMLComponent component);
    String toXML(int indentLevel);
}

class XMLLeaf implements XMLComponent {
    private final String tag;
    private final String value;
    private final Map<String, String> attributes;

    public XMLLeaf(String tag, String value) {
        this.tag = tag;
        this.value = value;
        this.attributes = new LinkedHashMap<>();
    }

    @Override
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Override
    public void addComponent(XMLComponent component) {
        throw new UnsupportedOperationException("Leaf elements cannot have child components.");
    }

    @Override
    public String toXML(int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indent = "\t".repeat(indentLevel);
        sb.append(indent).append("<").append(tag);
        attributes.forEach((key, value) -> sb.append(" ").append(key).append("=\"").append(value).append("\""));
        sb.append(">").append(value).append("</").append(tag).append(">\n");
        return sb.toString();
    }
}

class XMLComposite implements XMLComponent {
    private final String tag;
    private final Map<String, String> attributes;
    private final List<XMLComponent> children;

    public XMLComposite(String tag) {
        this.tag = tag;
        this.attributes = new LinkedHashMap<>();
        this.children = new ArrayList<>();
    }

    @Override
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Override
    public void addComponent(XMLComponent component) {
        children.add(component);
    }

    @Override
    public String toXML(int indentLevel) {
        StringBuilder sb = new StringBuilder();
        String indent = "\t".repeat(indentLevel);
        sb.append(indent).append("<").append(tag);
        attributes.forEach((key, value) -> sb.append(" ").append(key).append("=\"").append(value).append("\""));
        sb.append(">\n");
        for (XMLComponent child : children) {
            sb.append(child.toXML(indentLevel + 1));
        }
        sb.append(indent).append("</").append(tag).append(">\n");
        return sb.toString();
    }
}

public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        sc.nextLine(); // Consume the newline

        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");

        if (testCase == 1) {
            System.out.println(component.toXML(0));
        } else if (testCase == 2) {
            System.out.println(composite.toXML(0));
        } else if (testCase == 3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level", "1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level", "2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level", "3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            System.out.println(main.toXML(0));
        }
    }
}
