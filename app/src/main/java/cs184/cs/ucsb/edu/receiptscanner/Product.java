package cs184.cs.ucsb.edu.receiptscanner;


public class Product {

    public String name;
    public double price;


    Product(String itemName, double itemPrice) {
        this.name = itemName;
        this.price = itemPrice;
    }

    public String getName(){return this.name;}

    public double getPrice(){return this.price;}

}
