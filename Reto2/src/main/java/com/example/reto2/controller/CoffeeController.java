package com.example.reto2.controller;

import com.example.reto2.model.Batch;
import com.example.reto2.service.BatchService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class CoffeeController {

    @FXML
    private TextField tfId;

    @FXML
    private TextField tfProvider;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfWeight;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnClear;

    @FXML
    private TableView<Batch> batchesTable;

    @FXML
    private TableColumn<Batch, String> colProvider;

    @FXML
    private TableColumn<Batch, String> colDate;

    @FXML
    private TableColumn<Batch, Double> colWeight;

    @FXML
    private Label lblTotalBatches;

    private final BatchService batchService = new BatchService();
    private final ObservableList<Batch> observableBatches = FXCollections.observableArrayList();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    private void initialize() {
        setupTableColumns();

        MenuItem editItem = new MenuItem("Editar lote");
        MenuItem deleteItem = new MenuItem("Eliminar lote");

        editItem.setOnAction(e -> editSelectedBatch());
        deleteItem.setOnAction(e -> deleteSelectedBatch());

        ContextMenu contextMenu = new ContextMenu(editItem, deleteItem);
        batchesTable.setContextMenu(contextMenu);

        batchesTable.setOnMouseClicked(this::onBatchesTableMouseClicked);

        loadSampleData();
        refreshTable();
    }

    private void setupTableColumns() {
        batchesTable.setItems(observableBatches);

        colProvider.setCellValueFactory(new PropertyValueFactory<>("provider"));

        colDate.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getDate();
            return new SimpleStringProperty(date != null ? dateFormat.format(date) : "N/A");
        });

        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        colWeight.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f kg", item));
                }
            }
        });
    }

    @FXML
    private void onBatchesTableMouseClicked(MouseEvent event) {
        if (event == null) return;
        if (event.getClickCount() == 2) {
            Batch selected = batchesTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showBatchDetailsAlert(selected);
            }
        }
    }

    private void editSelectedBatch() {
        Batch selected = batchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, seleccione un lote de la tabla para editar.");
            return;
        }

        openEditDialog(selected);
    }

    private void deleteSelectedBatch() {
        Batch selected = batchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, seleccione un lote de la tabla para eliminar.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Está seguro de eliminar el lote '" + selected.getId() + "'?");
        confirmAlert.setContentText(
                "Proveedor: " + selected.getProvider() + "\n" +
                "Fecha de Recepción: " + (selected.getDate() != null ? dateFormat.format(selected.getDate()) : "N/A") + "\n" +
                "Peso: " + (selected.getWeight() != null ? String.format("%.2f kg", selected.getWeight()) : "N/A") + "\n\n" +
                "Esta acción no se puede deshacer. ¿Desea proceder?"
        );

        Optional<ButtonType> response = confirmAlert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            boolean deleted = batchService.deleteById(selected.getId());
            if (deleted) {
                refreshTable();
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Lote Eliminado", "El lote '" + selected.getId() + "' ha sido eliminado exitosamente.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el lote seleccionado.");
            }
        }
    }

    private void openEditDialog(Batch batch) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Lote de Café");
        dialog.setHeaderText("Modificando Lote: " + batch.getId());

        ButtonType btnSave = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField tfEditId = new TextField(batch.getId());
        tfEditId.setDisable(true);

        TextField tfEditProvider = new TextField(batch.getProvider() != null ? batch.getProvider() : "");
        DatePicker dpEditDate = new DatePicker();
        if (batch.getDate() != null) {
            dpEditDate.setValue(batch.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        TextField tfEditWeight = new TextField(batch.getWeight() != null ? String.valueOf(batch.getWeight()) : "");

        grid.add(new Label("Código / ID:"), 0, 0);
        grid.add(tfEditId, 1, 0);
        grid.add(new Label("Proveedor:"), 0, 1);
        grid.add(tfEditProvider, 1, 1);
        grid.add(new Label("Fecha:"), 0, 2);
        grid.add(dpEditDate, 1, 2);
        grid.add(new Label("Peso (kg):"), 0, 3);
        grid.add(tfEditWeight, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == btnSave) {
            String newProvider = tfEditProvider.getText().trim();
            LocalDate newLocalDate = dpEditDate.getValue();
            String newWeightStr = tfEditWeight.getText().trim();

            if (newProvider.isEmpty() || newLocalDate == null || newWeightStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Campos Incompletos", "Todos los campos son obligatorios para actualizar el lote.");
                return;
            }

            Double newWeight;
            try {
                newWeight = Double.parseDouble(newWeightStr);
                if (newWeight <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Valor Inválido", "El peso debe ser mayor a 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Formato Incorrecto", "El peso debe ser un número decimal válido.");
                return;
            }

            Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Batch updatedBatch = new Batch(newProvider, batch.getId(), newDate, newWeight);

            boolean success = batchService.updateBatch(updatedBatch);
            if (success) {
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Lote '" + batch.getId() + "' actualizado correctamente.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No fue posible actualizar el lote.");
            }
        }
    }

    private void showBatchDetailsAlert(Batch batch) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Lote de Café");
        alert.setHeaderText("Ficha de Recepción: " + batch.getId());
        alert.setContentText(
                "Código / ID: " + batch.getId() + "\n" +
                "Proveedor / Finca: " + (batch.getProvider() != null ? batch.getProvider() : "N/A") + "\n" +
                "Fecha de Recepción: " + (batch.getDate() != null ? dateFormat.format(batch.getDate()) : "N/A") + "\n" +
                "Peso Total: " + (batch.getWeight() != null ? String.format("%.2f kg", batch.getWeight()) : "N/A")
        );
        alert.showAndWait();
    }

    @FXML
    private void onRegisterBatch(ActionEvent event) {
        Batch batch = getValidatedBatchFromForm();
        if (batch == null) return;

        boolean added = batchService.addBatch(batch);
        if (added) {
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "El lote '" + batch.getId() + "' fue registrado correctamente.");
        } else {
            showAlert(Alert.AlertType.ERROR, "ID Duplicado", "Ya existe un lote registrado con el código: " + batch.getId());
        }
    }

    @FXML
    private void onClearFields(ActionEvent event) {
        clearForm();
        batchesTable.getSelectionModel().clearSelection();
    }

    private Batch getValidatedBatchFromForm() {
        String id = tfId.getText() != null ? tfId.getText().trim() : "";
        String provider = tfProvider.getText() != null ? tfProvider.getText().trim() : "";
        LocalDate localDate = dpDate.getValue();
        String weightStr = tfWeight.getText() != null ? tfWeight.getText().trim() : "";

        if (id.isEmpty() || provider.isEmpty() || localDate == null || weightStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor complete todos los campos del lote.");
            return null;
        }

        Double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if (weight <= 0) {
                showAlert(Alert.AlertType.WARNING, "Valor Inválido", "El peso debe ser mayor a 0 kg.");
                return null;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Formato Incorrecto", "El peso debe ser un número decimal válido.");
            return null;
        }

        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Batch(provider, id, date, weight);
    }

    private void clearForm() {
        tfId.clear();
        tfProvider.clear();
        dpDate.setValue(null);
        tfWeight.clear();
    }

    private void refreshTable() {
        observableBatches.setAll(batchService.getAllBatches());
        lblTotalBatches.setText(batchService.getBatchCount() + " lotes");
    }

    private void loadSampleData() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, -3);
        batchService.addBatch(new Batch("Finca Santa Rosa", "BATCH-001", calendar.getTime(), 150.50));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        batchService.addBatch(new Batch("Cooperativa Las Brisas", "BATCH-002", calendar.getTime(), 98.75));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        batchService.addBatch(new Batch("Hacienda Monte Verde", "BATCH-003", calendar.getTime(), 245.00));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        batchService.addBatch(new Batch("Don Café & Hnos", "BATCH-004", calendar.getTime(), 110.20));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
