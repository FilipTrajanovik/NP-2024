package mk.ukim.finki.kolokviumski;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class InvalidOperationException1 extends Exception {
    public InvalidOperationException1(String msg) {
        super(msg);
    }
}

abstract class Product {
    private String productId;
    private String productName;
    private double productPrice;
    private double quantity;


    public Product(String productId, String productName, double productPrice, double quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    abstract public double calculateTotal();
}

class WS extends Product {

    public WS(String productId, String productName, double productPrice,double quantity) {
        super(productId, productName, productPrice ,quantity);
    }

    @Override
    public double calculateTotal() {
        return getQuantity() * getProductPrice();
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f\n", getProductId(), calculateTotal());
    }
}

class PS extends Product{

    public PS(String productId, String productName, double productPrice, double quantity) {
        super(productId, productName, productPrice, quantity);
    }

    @Override
    public double calculateTotal() {
        return getProductPrice() * getQuantity()/1000;
    }
    @Override
    public String toString() {
        return String.format("%s - %.2f\n", getProductId(), calculateTotal());
    }
}

class ShoppingCart {
    public List<Product> products;
    public ShoppingCart() {
        this.products=new ArrayList<>();
    }

    public void addItem(String itemData) throws InvalidOperationException1 {
        //PS;107965;Flour;409;800.78
        //WS;101569;Coca Cola;970;64
        String []parts=itemData.split(";");
        String type=parts[0];
        String productId=parts[1];
        String productName=parts[2];
        double productPrice=Double.parseDouble(parts[3]);
        double quantity=Double.parseDouble(parts[4]);
        if(quantity == 0)
        {
            throw new InvalidOperationException1(String.format("The quantity of the product with id %s can not be 0.", productId));
        }
        if(type.equals("WS"))
        {
            products.add(new WS(productId,productName,productPrice,quantity));
        }else{
            products.add(new PS(productId,productName,productPrice,quantity));
        }
    }

    public void printShoppingCart(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        products.sort(Comparator.comparing(Product::calculateTotal).reversed());
        products.forEach(product -> pw.print(product.toString()));
        pw.flush();
    }

    public void blackFridayOffer(List<Integer> discountItems, OutputStream os) throws InvalidOperationException1 {
        PrintWriter pw = new PrintWriter(os);
        if(discountItems.isEmpty())
        {
            throw new InvalidOperationException1("There are no products with discount.");
        }

        List<Product> onDiscount = new ArrayList<>();
        for(Product product : products)
        {
            int productId=Integer.parseInt(product.getProductId());
            for(Integer discountItem : discountItems)
            {
                if(productId==discountItem)
                {
                    product.setProductPrice(product.getProductPrice()*0.1);
                    onDiscount.add(product);
                }
            }
        }
        onDiscount.forEach(product -> pw.print(product.toString()));
        pw.flush();
    }
}

public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException1 e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}