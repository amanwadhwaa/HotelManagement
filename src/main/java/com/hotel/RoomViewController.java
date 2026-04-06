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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class RoomViewController implements Initializable {

    @FXML
    private Label totalRoomsLabel;
    @FXML
    private Label availableRoomsLabel;
    @FXML
    private Label occupiedRoomsLabel;

    @FXML
    private TextField roomNumField;
    @FXML
    private ComboBox<String> typeBox;
    @FXML
    private TextField priceField;

    @FXML
    private TableView<Room> roomTable;
    @FXML
    private TableColumn<Room, Integer> roomNumCol;
    @FXML
    private TableColumn<Room, String> roomTypeCol;
    @FXML
    private TableColumn<Room, Double> roomPriceCol;
    @FXML
    private TableColumn<Room, Boolean> roomStatusCol;

    private List<Room> rooms;
    private ObservableList<Room> roomList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rooms = DataStore.loadRooms();
        roomList = FXCollections.observableArrayList(rooms);

        typeBox.getItems().setAll("Single", "Double", "Deluxe", "Suite");

        roomNumCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        roomStatusCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        roomStatusCol.setCellFactory(column -> new TableCell<Room, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item ? "Available" : "Booked");
                    setStyle(item
                        ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        roomTable.setItems(roomList);
        adjustTableHeight(roomList);
        refreshStats();
    }

    @FXML
    private void handleAddRoom() {
        try {
            int roomNumber = Integer.parseInt(roomNumField.getText().trim());
            String roomType = typeBox.getValue();
            double pricePerDay = Double.parseDouble(priceField.getText().trim());

            if (roomType == null || roomType.isBlank()) {
                showAlert("Please select a room type.", Alert.AlertType.WARNING);
                return;
            }

            boolean exists = rooms.stream().anyMatch(room -> room.getRoomNumber() == roomNumber);
            if (exists) {
                showAlert("Room number already exists.", Alert.AlertType.WARNING);
                return;
            }

            Room room = new Room(roomNumber, roomType, pricePerDay);
            rooms.add(room);
            roomList.add(room);
            DataStore.saveRooms(rooms);

            roomNumField.clear();
            typeBox.setValue(null);
            priceField.clear();
            adjustTableHeight(roomTable.getItems());
            refreshStats();
            showAlert("Room added successfully!", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException ex) {
            showAlert("Invalid room number or price.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleShowAll() {
        roomTable.setItems(roomList);
        adjustTableHeight(roomList);
    }

    @FXML
    private void handleShowAvailable() {
        ObservableList<Room> availableRooms = FXCollections.observableArrayList(
            rooms.stream().filter(Room::isAvailable).toList()
        );
        roomTable.setItems(availableRooms);
        adjustTableHeight(availableRooms);
    }

    @FXML
    private void handleDeleteRoom() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Please select a room to delete", Alert.AlertType.WARNING);
            return;
        }

        if (!selectedRoom.isAvailable()) {
            showAlert("Cannot delete a booked room", Alert.AlertType.WARNING);
            return;
        }

        rooms.remove(selectedRoom);
        roomList.remove(selectedRoom);
        DataStore.saveRooms(rooms);
        adjustTableHeight(roomTable.getItems());
        updateStats();
        showAlert("Room deleted successfully", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportCsv() {
        try {
            List<String> headers = List.of("Room Number", "Room Type", "Price Per Day", "Available");
            List<List<String>> rows = roomList.stream().map(room -> List.of(
                String.valueOf(room.getRoomNumber()),
                room.getRoomType(),
                String.format("%.2f", room.getPricePerDay()),
                String.valueOf(room.isAvailable())
            )).toList();

            if (CsvExportUtil.saveCsv(roomTable.getScene().getWindow(), "rooms.csv", headers, rows) != null) {
                showAlert("Rooms exported successfully.", Alert.AlertType.INFORMATION);
            }
        } catch (IOException ex) {
            showAlert("Failed to export rooms: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshStats() {
        long availableCount = rooms.stream().filter(Room::isAvailable).count();
        long occupiedCount = rooms.size() - availableCount;
        totalRoomsLabel.setText(String.valueOf(rooms.size()));
        availableRoomsLabel.setText(String.valueOf(availableCount));
        occupiedRoomsLabel.setText(String.valueOf(occupiedCount));
    }

    private void updateStats() {
        refreshStats();
    }

    private void adjustTableHeight(ObservableList<Room> sourceList) {
        int rowCount = sourceList == null ? 0 : sourceList.size();
        double rowHeight = 36;
        double headerHeight = 30;
        double minHeight = 140;
        double maxHeight = 420;
        double targetHeight = Math.max(minHeight, Math.min(maxHeight, headerHeight + (rowCount * rowHeight)));
        roomTable.setPrefHeight(targetHeight);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}
