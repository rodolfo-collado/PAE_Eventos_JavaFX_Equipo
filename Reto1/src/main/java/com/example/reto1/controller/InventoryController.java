package com.example.reto1.controller;

import com.example.reto1.model.Product;
import com.example.reto1.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class InventoryController {

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPrice;

    @FXML
    private TextField tfStock;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnClear;

    @FXML
    private RadioButton rbSearchByName;

    @FXML
    private RadioButton rbSearchByCode;

    @FXML
    private ToggleGroup filterGroup;

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbPriceCondition;

    @FXML
    private TextField tfFilterPrice;

    @FXML
    private ComboBox<String> cbStockCondition;

    @FXML
    private TextField tfFilterStock;

    @FXML
    private Button btnApplyFilters;

    @FXML
    private Button btnResetFilter;

    @FXML
    private ListView<Product> lvProducts;

    private final ProductService productService = new ProductService();
    private final ObservableList<Product> observableProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupListView();
    }

    private void setupListView() {
        lvProducts.setItems(observableProducts);
        lvProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFormWithProduct(newVal);
            }
        });
    }

    @FXML
    public void onSaveProduct(ActionEvent event) {
        Product product = getValidatedProductFromForm();
        if (product != null) {
            saveProduct(product);
        }
    }

    @FXML
    public void onUpdateProduct(ActionEvent event) {
        Product product = getValidatedProductFromForm();
        if (product != null) {
            updateProduct(product);
        }
    }

    @FXML
    public void onDeleteProduct(ActionEvent event) {
        String code = getTrimmedText(tfCode);
        if (code.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Código Requerido", "Ingrese o seleccione el código del producto a eliminar.");
            return;
        }
        deleteProductByCode(code);
    }

    @FXML
    public void onClearFields(ActionEvent event) {
        clearFormFields();
    }

    @FXML
    public void onSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeSearchAndFilters();
        }
    }

    @FXML
    public void onApplyFilters(ActionEvent event) {
        executeSearchAndFilters();
    }

    @FXML
    public void onResetFilter(ActionEvent event) {
        resetFilterInputs();
        refreshProductList();
    }

    private Product getValidatedProductFromForm() {
        String code = getTrimmedText(tfCode);
        String name = getTrimmedText(tfName);
        String priceText = getTrimmedText(tfPrice);
        String stockText = getTrimmedText(tfStock);

        if (!validateRequiredFields(code, name, priceText, stockText)) {
            return null;
        }

        Double price = parsePositiveNumber(priceText, "Precio");
        if (price == null) {
            return null;
        }

        Double stock = parsePositiveNumber(stockText, "Stock");
        if (stock == null) {
            return null;
        }

        return new Product(code, price, name, stock);
    }

    private boolean validateRequiredFields(String code, String name, String priceText, String stockText) {
        if (code.isEmpty() || name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, complete todos los campos del producto.");
            return false;
        }
        return true;
    }

    private Double parsePositiveNumber(String text, String fieldName) {
        try {
            double value = Double.parseDouble(text);
            if (value < 0) {
                showAlert(Alert.AlertType.WARNING, "Valor Inválido", "El campo '" + fieldName + "' no puede ser negativo.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Formato Incorrecto", "El campo '" + fieldName + "' debe ser un número válido.");
            return null;
        }
    }

    private Double parseOptionalFilterNumber(TextField textField, String fieldName) {
        String text = getTrimmedText(textField);
        if (text.isEmpty()) {
            return null;
        }
        return parsePositiveNumber(text, fieldName);
    }

    private String getTrimmedText(TextField textField) {
        return (textField != null && textField.getText() != null) ? textField.getText().trim() : "";
    }

    private void saveProduct(Product product) {
        boolean added = productService.addProduct(product);
        if (added) {
            refreshProductList();
            clearFormFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Producto agregado exitosamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Código Duplicado", "Ya existe un producto registrado con el código: " + product.getId());
        }
    }

    private void updateProduct(Product product) {
        boolean updated = productService.updateProduct(product);
        if (updated) {
            refreshProductList();
            clearFormFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Producto actualizado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "No Encontrado", "No se encontró ningún producto con el código: " + product.getId());
        }
    }

    private void deleteProductByCode(String code) {
        boolean deleted = productService.deleteByCode(code);
        if (deleted) {
            refreshProductList();
            clearFormFields();
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Producto eliminado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "No Encontrado", "No se encontró ningún producto con el código: " + code);
        }
    }

    private void executeSearchAndFilters() {
        String searchText = getTrimmedText(tfSearch);
        boolean searchByName = rbSearchByName == null || rbSearchByName.isSelected();

        Double targetPrice = parseOptionalFilterNumber(tfFilterPrice, "Precio Filtro");
        if (hasParsingError(tfFilterPrice, targetPrice)) {
            return;
        }

        Double targetStock = parseOptionalFilterNumber(tfFilterStock, "Stock Filtro");
        if (hasParsingError(tfFilterStock, targetStock)) {
            return;
        }

        String priceCondition = getEffectiveCondition(cbPriceCondition, targetPrice);
        String stockCondition = getEffectiveCondition(cbStockCondition, targetStock);

        List<Product> filtered = productService.filter(
                searchText,
                searchByName,
                priceCondition,
                targetPrice,
                stockCondition,
                targetStock
        );

        observableProducts.setAll(filtered);
    }

    private boolean hasParsingError(TextField field, Double parsedValue) {
        String text = getTrimmedText(field);
        return !text.isEmpty() && parsedValue == null;
    }

    private String getEffectiveCondition(ComboBox<String> comboBox, Double targetValue) {
        if (comboBox == null) return null;
        String condition = comboBox.getValue();
        if (targetValue != null && condition == null) {
            condition = "Mayor que (>)";
            comboBox.setValue(condition);
        }
        return condition;
    }

    private void refreshProductList() {
        observableProducts.setAll(productService.getAllProducts());
    }

    private void populateFormWithProduct(Product product) {
        tfCode.setText(product.getId());
        tfName.setText(product.getName());
        tfPrice.setText(String.valueOf(product.getPrice()));
        tfStock.setText(String.valueOf(product.getStock()));
    }

    private void clearFormFields() {
        tfCode.clear();
        tfName.clear();
        tfPrice.clear();
        tfStock.clear();
        lvProducts.getSelectionModel().clearSelection();
    }

    private void resetFilterInputs() {
        if (tfSearch != null) tfSearch.clear();
        if (tfFilterPrice != null) tfFilterPrice.clear();
        if (tfFilterStock != null) tfFilterStock.clear();
        if (cbPriceCondition != null) cbPriceCondition.setValue(null);
        if (cbStockCondition != null) cbStockCondition.setValue(null);
        if (rbSearchByName != null) rbSearchByName.setSelected(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
