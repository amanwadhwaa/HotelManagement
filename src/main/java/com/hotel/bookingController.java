package com.hotel;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class bookingController {

    private List<Room> rooms = DataStore.loadRooms();
    private List<Booking> bookings = DataStore.loadBookings();
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);

    public VBox getView() {
        TextField nameField = new TextField();
        nameField.setPromptText("Enter customer name");

        TextField contactField = new TextField();
        contactField.setPromptText("Enter contact number");

        ComboBox<Integer> roomBox = new ComboBox<>();
        roomBox.setPromptText("Select available room");
        roomBox.setPrefWidth(200);
        refreshRoomBox(roomBox);

        DatePicker checkInPicker = new DatePicker();
        checkInPicker.setPromptText("Check-in Date");

        DatePicker checkOutPicker = new DatePicker();
        checkOutPicker.setPromptText("Check-out Date");

        Button bookBtn = new Button("Book Room");
        bookBtn.getStyleClass().add("success-btn");
        bookBtn.setPrefWidth(150);

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("panel-card");

        Label formTitle = new Label("Create New Booking");
        formTitle.getStyleClass().add("section-title");
        form.add(formTitle, 0, 0, 2, 1);

        form.add(new Label("Customer Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Contact No:"), 0, 2);
        form.add(contactField, 1, 2);
        form.add(new Label("Available Room:"), 0, 3);
        form.add(roomBox, 1, 3);
        form.add(new Label("Check-in Date:"), 0, 4);
        form.add(checkInPicker, 1, 4);
        form.add(new Label("Check-out Date:"), 0, 5);
        form.add(checkOutPicker, 1, 5);
        form.add(bookBtn, 1, 6);

        Button checkoutBtn = new Button("Checkout Selected");
        checkoutBtn.setPrefWidth(150);
        checkoutBtn.getStyleClass().add("danger-btn");

        HBox btnBar = new HBox(15, checkoutBtn);
        btnBar.setPadding(new Insets(15, 20, 15, 20));
        btnBar.getStyleClass().add("panel-card");

        TableColumn<Booking, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        idCol.setPrefWidth(60);

        TableColumn<Booking, String> nameCol = new TableColumn<>("Customer");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        nameCol.setPrefWidth(120);

        TableColumn<Booking, Integer> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomCol.setPrefWidth(70);

        TableColumn<Booking, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        typeCol.setPrefWidth(90);

        TableColumn<Booking, LocalDate> inCol = new TableColumn<>("Check-in");
        inCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        inCol.setPrefWidth(110);

        TableColumn<Booking, LocalDate> outCol = new TableColumn<>("Check-out");
        outCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        outCol.setPrefWidth(110);

        TableColumn<Booking, Boolean> activeCol = new TableColumn<>("Status");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(90);
        activeCol.setCellFactory(column -> new TableCell<Booking, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item ? "Active ✓" : "Completed ✗");
                    setStyle(item ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : "-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                }
            }
        });

        TableView<Booking> table = new TableView<>();
        table.getColumns().addAll(idCol, nameCol, roomCol, typeCol, inCol, outCol, activeCol);
        table.setItems(bookingList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        bookBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            Integer selectedRoom = roomBox.getValue();
            LocalDate checkIn = checkInPicker.getValue();
            LocalDate checkOut = checkOutPicker.getValue();

            if (name.isEmpty() || contact.isEmpty() || selectedRoom == null || checkIn == null || checkOut == null) {
                showAlert("Please fill in all fields.", Alert.AlertType.WARNING);
                return;
            }

            if (!checkOut.isAfter(checkIn)) {
                showAlert("Check-out date must be after check-in date.", Alert.AlertType.WARNING);
                return;
            }

            Room room = rooms.stream()
                .filter(r -> r.getRoomNumber() == selectedRoom && r.isAvailable())
                .findFirst().orElse(null);

            if (room == null) {
                showAlert("Selected room is not available.", Alert.AlertType.ERROR);
                return;
            }

            long days = checkIn.until(checkOut).getDays();
            int id = DataStore.getNextBookingId(bookings);
            Booking booking = new Booking(id, name, room.getRoomNumber(), room.getRoomType(), room.getPricePerDay(), checkIn, checkOut);
            bookings.add(booking);
            bookingList.add(booking);

            room.setAvailable(false);
            DataStore.saveBookings(bookings);
            DataStore.saveRooms(rooms);

            refreshRoomBox(roomBox);
            nameField.clear();
            contactField.clear();
            checkInPicker.setValue(null);
            checkOutPicker.setValue(null);
            showAlert("Booking confirmed! ✓\nGuest: " + name + " | Room: " + room.getRoomNumber() + " | Days: " + days, Alert.AlertType.INFORMATION);
        });

        checkoutBtn.setOnAction(e -> {
            Booking selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Please select a booking to checkout.", Alert.AlertType.WARNING);
                return;
            }
            if (!selected.isActive()) {
                showAlert("This booking is already checked out.", Alert.AlertType.WARNING);
                return;
            }

            selected.setActive(false);
            rooms.stream()
                .filter(r -> r.getRoomNumber() == selected.getRoomNumber())
                .findFirst()
                .ifPresent(r -> r.setAvailable(true));

            DataStore.saveBookings(bookings);
            DataStore.saveRooms(rooms);
            table.refresh();
            refreshRoomBox(roomBox);
            showAlert("Checkout successful! ✓\nRoom " + selected.getRoomNumber() + " is now available.", Alert.AlertType.INFORMATION);
        });

        VBox layout = new VBox(15, form, btnBar, table);
        layout.setPadding(new Insets(15));
        layout.getStyleClass().add("view-root");
        VBox.setVgrow(table, Priority.ALWAYS);
        return layout;
    }

    private void refreshRoomBox(ComboBox<Integer> roomBox) {
        roomBox.getItems().clear();
        rooms = DataStore.loadRooms();
        rooms.stream()
            .filter(Room::isAvailable)
            .forEach(r -> roomBox.getItems().add(r.getRoomNumber()));
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}