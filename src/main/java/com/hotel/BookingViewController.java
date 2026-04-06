package com.hotel;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookingViewController implements Initializable {

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField contactField;
    @FXML
    private ComboBox<Integer> availableRoomBox;
    @FXML
    private DatePicker checkInPicker;
    @FXML
    private DatePicker checkOutPicker;
    @FXML
    private CheckBox breakfastIncludedBox;
    @FXML
    private TextField specialRequestsField;

    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, Integer> idCol;
    @FXML
    private TableColumn<Booking, String> customerCol;
    @FXML
    private TableColumn<Booking, Integer> roomNoCol;
    @FXML
    private TableColumn<Booking, String> typeCol;
    @FXML
    private TableColumn<Booking, Boolean> breakfastCol;
    @FXML
    private TableColumn<Booking, LocalDate> checkInCol;
    @FXML
    private TableColumn<Booking, LocalDate> checkOutCol;
    @FXML
    private TableColumn<Booking, Boolean> activeCol;

    private List<Room> rooms;
    private List<Booking> bookings;
    private ObservableList<Booking> bookingList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rooms = DataStore.loadRooms();
        bookings = DataStore.loadBookings();
        bookingList = FXCollections.observableArrayList(bookings);

        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        roomNoCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        breakfastCol.setCellValueFactory(new PropertyValueFactory<>("breakfastIncluded"));
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));

        bookingTable.setItems(bookingList);
        refreshAvailableRoomBox();
    }

    @FXML
    private void handleBookRoom() {
        String customerName = customerNameField.getText().trim();
        String contact = contactField.getText().trim();
        Integer selectedRoom = availableRoomBox.getValue();
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();
        boolean breakfastIncluded = breakfastIncludedBox.isSelected();
        String specialRequests = specialRequestsField.getText().trim();

        if (customerName.isEmpty() || contact.isEmpty() || selectedRoom == null || checkIn == null || checkOut == null) {
            showAlert("Please fill in all fields.", Alert.AlertType.WARNING);
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            showAlert("Check-out date must be after check-in date.", Alert.AlertType.WARNING);
            return;
        }

        Room room = rooms.stream()
            .filter(r -> r.getRoomNumber() == selectedRoom && r.isAvailable())
            .findFirst()
            .orElse(null);

        if (room == null) {
            showAlert("Selected room is not available.", Alert.AlertType.ERROR);
            return;
        }

        int bookingId = DataStore.getNextBookingId(bookings);
        Booking booking = new Booking(
            bookingId,
            customerName,
            room.getRoomNumber(),
            room.getRoomType(),
            room.getPricePerDay(),
            checkIn,
            checkOut,
            breakfastIncluded,
            specialRequests
        );

        bookings.add(booking);
        bookingList.add(booking);

        Customer customer = new Customer(customerName, contact, selectedRoom);
        List<Customer> customers = DataStore.loadCustomers();
        customers.add(customer);
        DataStore.saveCustomers(customers);

        room.setAvailable(false);
        DataStore.saveBookings(bookings);
        DataStore.saveRooms(rooms);

        customerNameField.clear();
        contactField.clear();
        availableRoomBox.setValue(null);
        checkInPicker.setValue(null);
        checkOutPicker.setValue(null);
        breakfastIncludedBox.setSelected(false);
        specialRequestsField.clear();

        refreshAvailableRoomBox();
        showAlert("Booking confirmed successfully!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleCheckoutSelected() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Please select a booking to checkout.", Alert.AlertType.WARNING);
            return;
        }

        if (!selectedBooking.isActive()) {
            showAlert("Selected booking is already checked out.", Alert.AlertType.WARNING);
            return;
        }

        selectedBooking.setActive(false);
        rooms.stream()
            .filter(room -> room.getRoomNumber() == selectedBooking.getRoomNumber())
            .findFirst()
            .ifPresent(room -> room.setAvailable(true));

        DataStore.saveBookings(bookings);
        DataStore.saveRooms(rooms);

        bookingTable.refresh();
        refreshAvailableRoomBox();
        showAlert("Checkout completed.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDeleteBooking() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Please select a booking to delete", Alert.AlertType.WARNING);
            return;
        }

        if (selectedBooking.isActive()) {
            List<Room> allRooms = DataStore.loadRooms();
            allRooms.stream()
                .filter(room -> room.getRoomNumber() == selectedBooking.getRoomNumber())
                .findFirst()
                .ifPresent(room -> room.setAvailable(true));
            DataStore.saveRooms(allRooms);
            rooms = allRooms;
        }

        bookings.remove(selectedBooking);
        bookingList.remove(selectedBooking);
        DataStore.saveBookings(bookings);

        bookingTable.refresh();
        refreshAvailableRoomBox();
        showAlert("Booking deleted successfully", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportCsv() {
        try {
            List<String> headers = List.of("Booking ID", "Customer Name", "Room Number", "Room Type", "Breakfast Included", "Special Requests", "Price Per Day", "Check-in", "Check-out", "Active");
            List<List<String>> rows = bookingList.stream().map(booking -> List.of(
                String.valueOf(booking.getBookingId()),
                booking.getCustomerName(),
                String.valueOf(booking.getRoomNumber()),
                booking.getRoomType(),
                String.valueOf(booking.isBreakfastIncluded()),
                booking.getSpecialRequests(),
                String.format("%.2f", booking.getPricePerDay()),
                String.valueOf(booking.getCheckIn()),
                String.valueOf(booking.getCheckOut()),
                String.valueOf(booking.isActive())
            )).toList();

            if (CsvExportUtil.saveCsv(bookingTable.getScene().getWindow(), "bookings.csv", headers, rows) != null) {
                showAlert("Bookings exported successfully.", Alert.AlertType.INFORMATION);
            }
        } catch (IOException ex) {
            showAlert("Failed to export bookings: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshAvailableRoomBox() {
        rooms = DataStore.loadRooms();
        availableRoomBox.getItems().clear();
        rooms.stream()
            .filter(Room::isAvailable)
            .forEach(room -> availableRoomBox.getItems().add(room.getRoomNumber()));
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}
