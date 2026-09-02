package ni.edu.uam.reto3.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ni.edu.uam.reto3.model.Product;
import ni.edu.uam.reto3.model.Sale;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class MainController {
    @FXML
    private ToolBar actionToolbar;

    @FXML
    private VBox catalogView;

    @FXML
    private VBox salesView;

    @FXML
    private VBox helpView;

    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TableColumn<Product, Image> imageColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Number> priceColumn;

    @FXML
    private TableColumn<Product, Number> stockColumn;

    @FXML
    private TextField productNameField;

    @FXML
    private ComboBox<String> productCategoryCombo;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField productStockField;

    @FXML
    private TextField productSearchField;

    @FXML
    private ComboBox<Product> saleProductCombo;

    @FXML
    private TextField customerField;

    @FXML
    private Spinner<Integer> saleQuantitySpinner;

    @FXML
    private Label saleTotalLabel;

    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private TableColumn<Sale, Number> saleNumberColumn;

    @FXML
    private TableColumn<Sale, String> saleDateColumn;

    @FXML
    private TableColumn<Sale, String> saleCustomerColumn;

    @FXML
    private TableColumn<Sale, String> saleProductColumn;

    @FXML
    private TableColumn<Sale, Number> saleQuantityColumn;

    @FXML
    private TableColumn<Sale, Number> saleTotalColumn;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    private final ObservableList<Sale> sales = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList();

    private final Map<String, Image> productImages = Map.of(
            "textil", createProductImage(Color.web("#2f6f73"), Color.web("#f2d16b")),
            "ceramica", createProductImage(Color.web("#a4472d"), Color.web("#f0b27a")),
            "madera", createProductImage(Color.web("#6b4f2a"), Color.web("#d8b56d")),
            "bisuteria", createProductImage(Color.web("#7b3f8c"), Color.web("#f5b7d3"))
    );

    @FXML
    private void initialize() {
        categories.addAll("Textil", "Ceramica", "Madera", "Bisuteria");
        products.addAll(
                new Product("ART-001", "Hamaca de Masaya", "Textil", 850.00, 8, "textil"),
                new Product("ART-002", "Vasija de San Juan de Oriente", "Ceramica", 420.00, 14, "ceramica"),
                new Product("ART-003", "Cofre tallado", "Madera", 650.00, 5, "madera"),
                new Product("ART-004", "Pulsera artesanal", "Bisuteria", 120.00, 30, "bisuteria")
        );
        filteredProducts.setAll(products);

        configureCatalogTable();
        configureCatalogControls();
        configureSalesControls();
        configureSalesTable();

        productsTable.setItems(filteredProducts);
        saleProductCombo.setItems(products);
        productCategoryCombo.setItems(categories);

        showCatalog();
    }

    private void configureCatalogControls() {
        productPriceField.setTextFormatter(new TextFormatter<>(decimalFilter()));
        productStockField.setTextFormatter(new TextFormatter<>(integerFilter()));
    }

    private void configureCatalogTable() {
        imageColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(productImages.get(cellData.getValue().getImageKey()))
        );
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(64);
                imageView.setFitHeight(64);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);
                if (empty || image == null) {
                    setGraphic(null);
                    return;
                }
                imageView.setImage(image);
                setGraphic(imageView);
            }
        });

        nameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getName())
        );
        categoryColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getCategory())
        );
        priceColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice())
        );
        stockColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getStock())
        );
    }

    private void configureSalesControls() {
        saleQuantitySpinner.getEditor().setTextFormatter(new TextFormatter<>(integerFilter()));
        saleProductCombo.valueProperty().addListener((observable, oldValue, newValue) -> updateSaleTotal());
        saleQuantitySpinner.valueProperty().addListener((observable, oldValue, newValue) -> updateSaleTotal());
        saleQuantitySpinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> updateSaleTotal());
        updateSaleTotal();
    }

    private void configureSalesTable() {
        saleNumberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getNumber())
        );
        saleDateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDate())
        );
        saleCustomerColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getCustomer())
        );
        saleProductColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getProductName())
        );
        saleQuantityColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getQuantity())
        );
        saleTotalColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getTotal())
        );
        salesTable.setItems(sales);
    }

    @FXML
    private void showCatalog() {
        showOnly(catalogView);
        actionToolbar.getItems().setAll(
                new Button("Nuevo"),
                new Button("Guardar"),
                new Button("Buscar"),
                new Button("Eliminar"),
                new Button("Anadir categoria")
        );
        ((Button) actionToolbar.getItems().get(0)).setOnAction(event -> newProduct());
        ((Button) actionToolbar.getItems().get(1)).setOnAction(event -> saveProduct());
        ((Button) actionToolbar.getItems().get(2)).setOnAction(event -> searchProduct());
        ((Button) actionToolbar.getItems().get(3)).setOnAction(event -> deleteSelectedProduct());
        ((Button) actionToolbar.getItems().get(4)).setOnAction(event -> addCategory());
    }

    @FXML
    private void showSales() {
        showOnly(salesView);
        actionToolbar.getItems().setAll(
                new Button("Registrar venta"),
                new Button("Eliminar venta"),
                new Button("Ver catalogo")
        );
        ((Button) actionToolbar.getItems().get(0)).setOnAction(event -> registerSale());
        ((Button) actionToolbar.getItems().get(1)).setOnAction(event -> deleteSelectedSale());
        ((Button) actionToolbar.getItems().get(2)).setOnAction(event -> showCatalog());
    }

    @FXML
    private void showHelp() {
        showOnly(helpView);
        actionToolbar.getItems().setAll(
                new Button("Ir a catalogo"),
                new Button("Ir a ventas")
        );
        ((Button) actionToolbar.getItems().get(0)).setOnAction(event -> showCatalog());
        ((Button) actionToolbar.getItems().get(1)).setOnAction(event -> showSales());
    }

    @FXML
    private void newProduct() {
        clearCatalogForm();
        productNameField.requestFocus();
    }

    @FXML
    private void saveProduct() {
        String name = productNameField.getText().trim();
        String category = productCategoryCombo.getValue();
        String priceText = productPriceField.getText().trim();
        String stockText = productStockField.getText().trim();

        if (name.isEmpty() || category == null || category.isBlank() || priceText.isEmpty() || stockText.isEmpty()) {
            showWarning("Catalogo", "Completa nombre, categoria, precio y stock.");
            return;
        }

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceText);
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException exception) {
            showWarning("Catalogo", "Precio y stock deben ser numericos.");
            return;
        }

        if (price <= 0 || stock < 0) {
            showWarning("Catalogo", "El precio debe ser mayor que cero y el stock no puede ser negativo.");
            return;
        }

        String imageKey = resolveImageKey(category);
        String code = "ART-" + String.format("%03d", products.size() + 1);
        Product product = new Product(code, name, category, price, stock, imageKey);
        products.add(product);
        searchProduct();
        saleProductCombo.getSelectionModel().select(product);
        clearCatalogForm();
        productsTable.refresh();
        showMessage("Catalogo", "Producto guardado correctamente.");
    }

    @FXML
    private void searchProduct() {
        String query = productSearchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredProducts.setAll(products);
            return;
        }
        filteredProducts.setAll(products.filtered(product ->
                product.getName().toLowerCase().contains(query)
                        || product.getCategory().toLowerCase().contains(query)
                        || product.getCode().toLowerCase().contains(query)
        ));
    }

    @FXML
    private void registerSale() {
        Product product = saleProductCombo.getValue();
        String customer = customerField.getText().trim();
        Integer quantity = readSaleQuantity();

        if (product == null) {
            showWarning("Ventas", "Selecciona el producto vendido.");
            return;
        }
        if (customer.isEmpty()) {
            showWarning("Ventas", "Escribe el nombre del cliente.");
            return;
        }
        if (quantity == null || quantity <= 0) {
            showWarning("Ventas", "La cantidad debe ser mayor que cero.");
            return;
        }
        if (quantity > product.getStock()) {
            showWarning("Ventas", "La cantidad supera el stock disponible de " + product.getStock() + ".");
            return;
        }

        product.reduceStock(quantity);
        sales.add(new Sale(
                sales.size() + 1,
                product.getCode(),
                customer,
                product.getName(),
                quantity,
                product.getPrice(),
                LocalDateTime.now()
        ));

        productsTable.refresh();
        salesTable.refresh();
        clearSaleForm();
        showMessage("Ventas", "Venta registrada y stock actualizado.");
    }

    @FXML
    private void clearCatalogForm() {
        productNameField.clear();
        productCategoryCombo.getSelectionModel().clearSelection();
        productPriceField.clear();
        productStockField.clear();
        productSearchField.clear();
        filteredProducts.setAll(products);
    }

    @FXML
    private void clearSaleForm() {
        customerField.clear();
        saleQuantitySpinner.getValueFactory().setValue(1);
        saleProductCombo.getSelectionModel().clearSelection();
        updateSaleTotal();
    }

    @FXML
    private void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Anadir categoria");
        dialog.setHeaderText(null);
        dialog.setContentText("Nombre de la categoria:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String category = result.get().trim();
        if (category.isEmpty()) {
            showWarning("Categoria", "La categoria no puede estar vacia.");
            return;
        }
        boolean exists = categories.stream().anyMatch(existing -> existing.equalsIgnoreCase(category));
        if (exists) {
            showWarning("Categoria", "Esa categoria ya existe.");
            return;
        }

        categories.add(category);
        productCategoryCombo.getSelectionModel().select(category);
        showMessage("Categoria", "Categoria agregada correctamente.");
    }

    @FXML
    private void deleteSelectedProduct() {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        if (product == null) {
            showWarning("Catalogo", "Selecciona un producto del catalogo para eliminar.");
            return;
        }

        if (product.getStock() <= 1) {
            if (confirm("Eliminar producto", "Deseas eliminar el producto seleccionado?")) {
                products.remove(product);
                filteredProducts.remove(product);
                saleProductCombo.getSelectionModel().clearSelection();
                productsTable.refresh();
                showMessage("Catalogo", "Producto eliminado.");
            }
            return;
        }

        ButtonType deleteSome = new ButtonType("Borrar cantidad");
        ButtonType deleteAll = new ButtonType("Borrar todas");
        ButtonType cancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar producto");
        alert.setHeaderText(null);
        alert.setContentText("El producto tiene " + product.getStock() + " unidades. Que deseas eliminar?");
        alert.getButtonTypes().setAll(deleteSome, deleteAll, cancel);

        Optional<ButtonType> selected = alert.showAndWait();
        if (selected.isEmpty() || selected.get() == cancel) {
            return;
        }

        if (selected.get() == deleteAll) {
            products.remove(product);
            filteredProducts.remove(product);
            productsTable.refresh();
            showMessage("Catalogo", "Producto eliminado por completo.");
            return;
        }

        askQuantityToDelete(product).ifPresent(quantity -> {
            product.reduceStock(quantity);
            if (product.getStock() == 0) {
                products.remove(product);
                filteredProducts.remove(product);
            }
            productsTable.refresh();
            updateSaleTotal();
            showMessage("Catalogo", "Inventario actualizado.");
        });
    }

    @FXML
    private void deleteSelectedSale() {
        Sale sale = salesTable.getSelectionModel().getSelectedItem();
        if (sale == null) {
            showWarning("Ventas", "Selecciona una venta para eliminar.");
            return;
        }

        if (!confirm("Eliminar venta", "Deseas eliminar la venta seleccionada?")) {
            return;
        }

        findProductByCode(sale.getProductCode()).ifPresent(product -> product.increaseStock(sale.getQuantity()));
        sales.remove(sale);
        productsTable.refresh();
        salesTable.refresh();
        updateSaleTotal();
        showMessage("Ventas", "Venta eliminada.");
    }

    private void showOnly(VBox activeView) {
        catalogView.setVisible(false);
        catalogView.setManaged(false);
        salesView.setVisible(false);
        salesView.setManaged(false);
        helpView.setVisible(false);
        helpView.setManaged(false);

        activeView.setVisible(true);
        activeView.setManaged(true);
    }

    private void updateSaleTotal() {
        Product product = saleProductCombo == null ? null : saleProductCombo.getValue();
        Integer quantity = saleQuantitySpinner == null ? 1 : readSaleQuantity();
        double total = product == null || quantity == null ? 0 : product.getPrice() * quantity;
        saleTotalLabel.setText("Total: C$ " + String.format("%.2f", total));
    }

    private Integer readSaleQuantity() {
        String text = saleQuantitySpinner.getEditor().getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            int quantity = Integer.parseInt(text);
            saleQuantitySpinner.getValueFactory().setValue(quantity);
            return quantity;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Optional<Product> findProductByCode(String code) {
        return products.stream()
                .filter(product -> product.getCode().equals(code))
                .findFirst();
    }

    private Optional<Integer> askQuantityToDelete(Product product) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Eliminar unidades");
        dialog.setHeaderText(null);
        dialog.setContentText("Cantidad a borrar de " + product.getName() + ":");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return Optional.empty();
        }

        String text = result.get().trim();
        if (!text.matches("\\d+")) {
            showWarning("Catalogo", "La cantidad debe ser numerica.");
            return Optional.empty();
        }

        int quantity = Integer.parseInt(text);
        if (quantity <= 0 || quantity > product.getStock()) {
            showWarning("Catalogo", "La cantidad debe estar entre 1 y " + product.getStock() + ".");
            return Optional.empty();
        }

        return Optional.of(quantity);
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private UnaryOperator<TextFormatter.Change> integerFilter() {
        return change -> change.getControlNewText().matches("\\d*") ? change : null;
    }

    private UnaryOperator<TextFormatter.Change> decimalFilter() {
        return change -> change.getControlNewText().matches("\\d*(\\.\\d*)?") ? change : null;
    }

    private String resolveImageKey(String category) {
        String normalized = category.toLowerCase();
        if (normalized.contains("ceram")) {
            return "ceramica";
        }
        if (normalized.contains("madera")) {
            return "madera";
        }
        if (normalized.contains("bisuter")) {
            return "bisuteria";
        }
        return "textil";
    }

    private void showMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static Image createProductImage(Color baseColor, Color detailColor) {
        int size = 80;
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                boolean border = x < 4 || y < 4 || x >= size - 4 || y >= size - 4;
                boolean diagonal = Math.abs(x - y) < 4 || Math.abs((size - x) - y) < 4;
                boolean center = x > 24 && x < 56 && y > 24 && y < 56;

                if (border) {
                    writer.setColor(x, y, Color.web("#2b2b2b"));
                } else if (diagonal || center) {
                    writer.setColor(x, y, detailColor);
                } else {
                    writer.setColor(x, y, baseColor);
                }
            }
        }

        return image;
    }
}
