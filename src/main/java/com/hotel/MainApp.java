package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");

        VBox sidebar = createSidebar();
        sidebar.setStyle("-fx-background-color: #111827;");

        HBox welcomeBar = createWelcomeBar();
        StackPane contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        BorderPane centerPane = new BorderPane();
        centerPane.setTop(welcomeBar);
        centerPane.setCenter(contentArea);

        Button roomsBtn = new Button("🏠 Rooms");
        Button customersBtn = new Button("👤 Customers");
        Button bookingsBtn = new Button("📅 Bookings");
        Button billingBtn = new Button("💰 Billing");

        styleNavButton(roomsBtn);
        styleNavButton(customersBtn);
        styleNavButton(bookingsBtn);
        styleNavButton(billingBtn);

        sidebar.getChildren().addAll(roomsBtn, customersBtn, bookingsBtn, billingBtn);

        Runnable showRooms = () -> {
            contentArea.getChildren().setAll(loadView("/com/hotel/RoomView.fxml"));
            setActiveNavButton(roomsBtn, customersBtn, bookingsBtn, billingBtn);
        };
        Runnable showCustomers = () -> {
            contentArea.getChildren().setAll(loadView("/com/hotel/CustomerView.fxml"));
            setActiveNavButton(customersBtn, roomsBtn, bookingsBtn, billingBtn);
        };
        Runnable showBookings = () -> {
            contentArea.getChildren().setAll(loadView("/com/hotel/BookingView.fxml"));
            setActiveNavButton(bookingsBtn, roomsBtn, customersBtn, billingBtn);
        };
        Runnable showBilling = () -> {
            contentArea.getChildren().setAll(loadView("/com/hotel/BillingView.fxml"));
            setActiveNavButton(billingBtn, roomsBtn, customersBtn, bookingsBtn);
        };

        roomsBtn.setOnAction(e -> showRooms.run());
        customersBtn.setOnAction(e -> showCustomers.run());
        bookingsBtn.setOnAction(e -> showBookings.run());
        billingBtn.setOnAction(e -> showBilling.run());

        showRooms.run();

        root.setLeft(sidebar);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Wadhwa's & Wadhwa's");
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #111827;");
        sidebar.setPrefWidth(200);
        sidebar.setMinWidth(200);
        sidebar.setMaxWidth(200);

        Label brandTitle = new Label("W&W");
        brandTitle.setStyle("-fx-text-fill: #c9a84c; -fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Georgia'; -fx-padding: 20 10 20 10;");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #2a3145;");

        sidebar.getChildren().addAll(brandTitle, separator);

        return sidebar;
    }

    private HBox createWelcomeBar() {
        Label welcomeLabel = new Label("Wadhwa's & Wadhwa's");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: \"Georgia\";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox welcomeBar = new HBox(10, welcomeLabel, spacer);
        welcomeBar.setStyle("-fx-background-color: #0a0a0f; -fx-padding: 10 20 10 20;");
        return welcomeBar;
    }

    private void styleNavButton(Button button) {
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefWidth(180);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #8892a4; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16 12 16;");
    }

    private Parent loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return new VBox();
        }
    }

    private void setActiveNavButton(Button active, Button... others) {
        active.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16 12 16;");
        for (Button button : others) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #8892a4; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16 12 16;");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}