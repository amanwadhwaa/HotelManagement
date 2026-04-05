package com.hotel;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BillingController {

    private List<Booking> bookings = DataStore.loadBookings();
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);

    public VBox getView() {
        TableColumn<Booking, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        idCol.setPrefWidth(90);

        TableColumn<Booking, String> nameCol = new TableColumn<>("Customer");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        nameCol.setPrefWidth(130);

        TableColumn<Booking, Integer> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomCol.setPrefWidth(70);

        TableColumn<Booking, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        typeCol.setPrefWidth(90);

        TableColumn<Booking, Double> priceCol = new TableColumn<>("Price/Day");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        priceCol.setPrefWidth(100);

        TableColumn<Booking, String> checkInCol = new TableColumn<>("Check-in");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkInCol.setPrefWidth(110);

        TableColumn<Booking, String> checkOutCol = new TableColumn<>("Check-out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        checkOutCol.setPrefWidth(110);

        TableColumn<Booking, Double> billCol = new TableColumn<>("Total Bill");
        billCol.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        billCol.setPrefWidth(120);
        billCol.setCellFactory(column -> new TableCell<Booking, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("₹" + String.format("%,.2f", item));
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        TableView<Booking> table = new TableView<>();
        table.getColumns().addAll(idCol, nameCol, roomCol, typeCol, priceCol, checkInCol, checkOutCol, billCol);
        table.setItems(bookingList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        Label totalLabel = new Label("Select a booking to see bill details");
        totalLabel.getStyleClass().add("total-banner");

        Button refreshBtn = new Button("Refresh");
        Button printBtn = new Button("Print Bill");
        refreshBtn.setPrefWidth(130);
        printBtn.setPrefWidth(130);
        printBtn.getStyleClass().add("success-btn");

        HBox btnBar = new HBox(15, refreshBtn, printBtn);
        btnBar.setPadding(new Insets(15, 20, 15, 20));
        btnBar.getStyleClass().add("panel-card");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                long days = newVal.getCheckIn().until(newVal.getCheckOut()).getDays();
                totalLabel.setText(
                    "Customer: " + newVal.getCustomerName() +
                    " | Room: " + newVal.getRoomNumber() +
                    " | Days: " + days +
                    " | Total Bill: Rs " + String.format("%,.2f", newVal.getTotalBill())
                );
            }
        });

        refreshBtn.setOnAction(e -> {
            bookings = DataStore.loadBookings();
            bookingList.setAll(bookings);
            totalLabel.setText("Select a booking to see bill details");
        });

        printBtn.setOnAction(e -> {
            Booking selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Please select a booking first.", Alert.AlertType.WARNING);
                return;
            }
            long days = selected.getCheckIn().until(selected.getCheckOut()).getDays();
            String bill = "=======================================\n" +
                "           HOTEL BILL - INVOICE\n" +
                "=======================================\n\n" +
                String.format("  Booking ID      : %d\n", selected.getBookingId()) +
                String.format("  Customer Name   : %s\n", selected.getCustomerName()) +
                String.format("  Room Number     : %d\n", selected.getRoomNumber()) +
                String.format("  Room Type       : %s\n", selected.getRoomType()) +
                String.format("  Check-in Date   : %s\n", selected.getCheckIn()) +
                String.format("  Check-out Date  : %s\n", selected.getCheckOut()) +
                String.format("  Number of Days  : %d\n", days) +
                String.format("  Price Per Night : Rs %.2f\n", selected.getPricePerDay()) +
                "---------------------------------------\n" +
                String.format("  TOTAL BILL      : Rs %,.2f\n", selected.getTotalBill()) +
                "=======================================\n\n" +
                "        Thank you for your stay!";
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invoice");
            alert.setHeaderText("Hotel Bill Receipt");
            alert.setContentText(bill);
            
            // Set monospace font for bill
            TextArea textArea = new TextArea(bill);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px;");
            textArea.setPrefWidth(500);
            textArea.setPrefHeight(400);
            
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        });

        VBox layout = new VBox(15, btnBar, table, totalLabel);
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