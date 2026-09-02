package ni.edu.uam.reto3.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int number;
    private final String productCode;
    private final String customer;
    private final String productName;
    private final int quantity;
    private final double unitPrice;
    private final LocalDateTime dateTime;

    public Sale(int number, String productCode, String customer, String productName, int quantity, double unitPrice, LocalDateTime dateTime) {
        this.number = number;
        this.productCode = productCode;
        this.customer = customer;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.dateTime = dateTime;
    }

    public int getNumber() {
        return number;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getCustomer() {
        return customer;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotal() {
        return quantity * unitPrice;
    }

    public String getDate() {
        return FORMATTER.format(dateTime);
    }
}
