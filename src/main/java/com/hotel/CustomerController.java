package com.hotel;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CustomerController {

    private List<Customer> customers = DataStore.loadCustomers();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList(customers);

    public VBox getView() {
        TextField nameField = new TextField();
        nameField.setPromptText("Enter customer name");

        TextField contactField = new TextField();
        contactField.setPromptText("Enter contact number");

        TextField roomNumField = new TextField();
        roomNumField.setPromptText("Room number");

        Button addBtn = new Button("Add Customer");
        addBtn.getStyleClass().add("success-btn");
        addBtn.setPrefWidth(160);

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("panel-card");

        Label formTitle = new Label("Register New Customer");
        formTitle.getStyleClass().add("section-title");
        form.add(formTitle, 0, 0, 2, 1);

        form.add(new Label("Full Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Contact No:"), 0, 2);
        form.add(contactField, 1, 2);
        form.add(new Label("Room Number:"), 0, 3);
        form.add(roomNumField, 1, 3);
        form.add(addBtn, 1, 4);

        // Table
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);

        TableColumn<Customer, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        contactCol.setPrefWidth(150);

        TableColumn<Customer, Integer> roomCol = new TableColumn<>("Room No");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomCol.setPrefWidth(100);

        TableView<Customer> table = new TableView<>();
        table.getColumns().addAll(nameCol, contactCol, roomCol);
        table.setItems(customerList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String roomText = roomNumField.getText().trim();

            if (name.isEmpty() || contact.isEmpty() || roomText.isEmpty()) {
                showAlert("Please fill in all fields.", Alert.AlertType.WARNING);
                return;
            }

            try {
                int roomNum = Integer.parseInt(roomText);
                Customer customer = new Customer(name, contact, roomNum);
                customers.add(customer);
                customerList.add(customer);
                DataStore.saveCustomers(customers);

                nameField.clear();
                contactField.clear();
                roomNumField.clear();
                showAlert("Customer added successfully! ✓", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                showAlert("Invalid room number.", Alert.AlertType.ERROR);
            }
        });

        VBox layout = new VBox(15, form, table);
        layout.setPadding(new Insets(15));
        layout.getStyleClass().add("view-root");
        VBox.setVgrow(table, Priority.ALWAYS);
        return layout;
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}