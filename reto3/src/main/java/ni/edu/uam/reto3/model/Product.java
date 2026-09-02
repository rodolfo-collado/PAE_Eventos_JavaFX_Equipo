package ni.edu.uam.reto3.model;

public class Product {
    private final String code;
    private final String name;
    private final String category;
    private final double price;
    private final String imageKey;
    private int stock;

    public Product(String code, String name, String category, double price, int stock, String imageKey) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.imageKey = imageKey;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (quantity > stock) {
            throw new IllegalArgumentException("No hay suficiente stock disponible.");
        }
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        stock += quantity;
    }

    public String getImageKey() {
        return imageKey;
    }

    @Override
    public String toString() {
        return name;
    }
}
