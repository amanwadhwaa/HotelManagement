package com.hotel;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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

public class RoomController {

    private List<Room> rooms = DataStore.loadRooms();
    private ObservableList<Room> roomList = FXCollections.observableArrayList(rooms);
    private TableView<Room> table = new TableView<>();

    public VBox getView() {
        TextField roomNumField = new TextField();
        roomNumField.setPromptText("Room Number");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Single", "Double", "Deluxe", "Suite");
        typeBox.setPromptText("Select Room Type");
        typeBox.setPrefWidth(200);

        TextField priceField = new TextField();
        priceField.setPromptText("Price Per Day (₹)");

        Button addBtn = new Button("Add Room");
        addBtn.getStyleClass().add("success-btn");
        addBtn.setPrefWidth(150);

        Label totalRoomsValue = new Label();
        totalRoomsValue.getStyleClass().add("stat-value");
        Label availableRoomsValue = new Label();
        availableRoomsValue.getStyleClass().add("stat-value");
        Label occupiedRoomsValue = new Label();
        occupiedRoomsValue.getStyleClass().add("stat-value");

        VBox totalCard = createStatCard("Total Rooms", totalRoomsValue);
        VBox availableCard = createStatCard("Available Rooms", availableRoomsValue);
        VBox occupiedCard = createStatCard("Occupied Rooms", occupiedRoomsValue);
        HBox statsRow = new HBox(14, totalCard, availableCard, occupiedCard);
        statsRow.getStyleClass().add("stats-row");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("panel-card");

        Label formTitle = new Label("Add New Room");
        formTitle.getStyleClass().add("section-title");
        form.add(formTitle, 0, 0, 2, 1);

        form.add(new Label("Room Number:"), 0, 1);
        form.add(roomNumField, 1, 1);
        form.add(new Label("Room Type:"), 0, 2);
        form.add(typeBox, 1, 2);
        form.add(new Label("Price Per Day:"), 0, 3);
        form.add(priceField, 1, 3);
        form.add(addBtn, 1, 4);

        Button showAllBtn = new Button("Show All Rooms");
        showAllBtn.setPrefWidth(140);
        Button showAvailableBtn = new Button("Available Only");
        showAvailableBtn.setPrefWidth(140);

        HBox filterBar = new HBox(15, showAllBtn, showAvailableBtn);
        filterBar.setPadding(new Insets(15, 20, 15, 20));
        filterBar.getStyleClass().add("panel-card");

        TableColumn<Room, Integer> numCol = new TableColumn<>("Room No");
        numCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        numCol.setPrefWidth(100);

        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        typeCol.setPrefWidth(100);

        TableColumn<Room, Double> priceCol = new TableColumn<>("Price/Day");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        priceCol.setPrefWidth(120);

        TableColumn<Room, Boolean> availCol = new TableColumn<>("Status");
        availCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        availCol.setPrefWidth(100);
        availCol.setCellFactory(column -> new TableCell<Room, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item ? "Available ✓" : "Booked ✗");
                    setStyle(item ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        table.getColumns().addAll(numCol, typeCol, priceCol, availCol);
        table.setItems(roomList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        Runnable refreshStats = () -> {
            long availableCount = rooms.stream().filter(Room::isAvailable).count();
            long occupiedCount = rooms.size() - availableCount;
            totalRoomsValue.setText(String.valueOf(rooms.size()));
            availableRoomsValue.setText(String.valueOf(availableCount));
            occupiedRoomsValue.setText(String.valueOf(occupiedCount));
        };
        refreshStats.run();

        // Events
        addBtn.setOnAction(e -> {
            try {
                int num = Integer.parseInt(roomNumField.getText());
                String type = typeBox.getValue();
                double price = Double.parseDouble(priceField.getText());

                if (type == null) {
                    showAlert("Please select a room type.", Alert.AlertType.WARNING);
                    return;
                }

                boolean exists = rooms.stream().anyMatch(r -> r.getRoomNumber() == num);
                if (exists) {
                    showAlert("Room number already exists.", Alert.AlertType.WARNING);
                    return;
                }

                Room room = new Room(num, type, price);
                rooms.add(room);
                roomList.add(room);
                DataStore.saveRooms(rooms);

                roomNumField.clear();
                typeBox.setValue(null);
                priceField.clear();
                refreshStats.run();
                showAlert("Room added successfully! ✓", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                showAlert("Invalid input. Check room number and price.", Alert.AlertType.ERROR);
            }
        });

        showAllBtn.setOnAction(e -> table.setItems(roomList));

        showAvailableBtn.setOnAction(e -> {
            ObservableList<Room> available = FXCollections.observableArrayList(
                rooms.stream().filter(Room::isAvailable).toList()
            );
            table.setItems(available);
        });

        VBox layout = new VBox(15, statsRow, form, filterBar, table);
        layout.setPadding(new Insets(15));
        layout.getStyleClass().add("view-root");
        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    private VBox createStatCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        VBox card = new VBox(6, titleLabel, valueLabel);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}