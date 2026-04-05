package com.hotel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerViewController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String> contactCol;
    @FXML
    private TableColumn<Customer, Integer> roomNoCol;

    private List<Customer> customers;
    private ObservableList<Customer> customerList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerList = FXCollections.observableArrayList();

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        roomNoCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        customerTable.setItems(customerList);
        handleRefresh();
    }

    @FXML
    private void handleRefresh() {
        customers = DataStore.loadCustomers();
        customerList.setAll(customers);
    }

    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Please select a guest to delete", Alert.AlertType.WARNING);
            return;
        }

        customers.remove(selectedCustomer);
        DataStore.saveCustomers(customers);
        handleRefresh();
        showAlert("Guest deleted successfully", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : (type == Alert.AlertType.WARNING ? "Warning" : "Success"));
        alert.showAndWait();
    }
}
