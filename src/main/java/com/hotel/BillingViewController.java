package com.hotel;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class BillingViewController implements Initializable {

    @FXML
    private TableView<Booking> billingTable;
    @FXML
    private TableColumn<Booking, Integer> bookingIdCol;
    @FXML
    private TableColumn<Booking, String> customerCol;
    @FXML
    private TableColumn<Booking, Integer> roomNoCol;
    @FXML
    private TableColumn<Booking, String> typeCol;
    @FXML
    private TableColumn<Booking, Double> pricePerDayCol;
    @FXML
    private TableColumn<Booking, String> checkInCol;
    @FXML
    private TableColumn<Booking, String> checkOutCol;
    @FXML
    private TableColumn<Booking, Double> totalBillCol;

    @FXML
    private Label billSummaryLabel;

    private List<Booking> bookings;
    private ObservableList<Booking> bookingList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        roomNoCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        pricePerDayCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        totalBillCol.setCellValueFactory(new PropertyValueFactory<>("totalBill"));

        loadBookings();

        billingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                long days = selected.getCheckIn().until(selected.getCheckOut()).getDays();
                billSummaryLabel.setText(
                    "Customer: " + selected.getCustomerName() +
                    " | Room: " + selected.getRoomNumber() +
                    " | Days: " + days +
                    " | Total Bill: Rs " + String.format("%,.2f", selected.getTotalBill())
                );
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadBookings();
        billSummaryLabel.setText("Select a booking to see bill details");
    }

    @FXML
    private void handlePrintBill() {
        Booking selected = billingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a booking first.", Alert.AlertType.WARNING);
            return;
        }

        long days = selected.getCheckIn().until(selected.getCheckOut()).getDays();
        String billText = "=======================================\n" +
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

        TextArea textArea = new TextArea(billText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px;");
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(400);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteBill() {
        Booking selectedBooking = billingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Please select a bill to delete", Alert.AlertType.WARNING);
            return;
        }

        bookings.remove(selectedBooking);
        DataStore.saveBookings(bookings);
        loadBookings();
        billSummaryLabel.setText("Select a booking to see bill details");
        showAlert("Bill record deleted successfully", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportCsv() {
        try {
            List<String> headers = List.of("Booking ID", "Customer Name", "Room Number", "Room Type", "Price Per Day", "Check-in", "Check-out", "Total Bill", "Active");
            List<List<String>> rows = bookingList.stream().map(booking -> List.of(
                String.valueOf(booking.getBookingId()),
                booking.getCustomerName(),
                String.valueOf(booking.getRoomNumber()),
                booking.getRoomType(),
                String.format("%.2f", booking.getPricePerDay()),
                String.valueOf(booking.getCheckIn()),
                String.valueOf(booking.getCheckOut()),
                String.format("%.2f", booking.getTotalBill()),
                String.valueOf(booking.isActive())
            )).toList();

            if (CsvExportUtil.saveCsv(billingTable.getScene().getWindow(), "billing.csv", headers, rows) != null) {
                showAlert("Bills exported successfully.", Alert.AlertType.INFORMATION);
            }
        } catch (IOException ex) {
            showAlert("Failed to export bills: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadBookings() {
        bookings = DataStore.loadBookings();
        bookingList = FXCollections.observableArrayList(bookings);
        billingTable.setItems(bookingList);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}
