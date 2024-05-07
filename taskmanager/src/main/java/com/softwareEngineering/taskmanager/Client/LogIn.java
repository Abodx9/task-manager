package com.softwareEngineering.taskmanager.Client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;

public class LogIn extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
    private static final String user = "taskmanager_admin";
    private static final String password = "iLUqbipQL";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // GridPane for layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.setStyle("-fx-background-color: #02030A");

        // First Name Label and Textfield
        Label IDNameLabel = new Label("ID:");
        TextField IDNameField = new TextField();
        grid.add(IDNameLabel, 0, 0);
        grid.add(IDNameField, 1, 0);

        // Password textfield
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField(); // Use PasswordField for password entry
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        // Login Button
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 3);
       

        // Event handling for the login button
        loginButton.setOnAction(e -> {
            String ID = IDNameField.getText();
            String password = passwordField.getText();

            // login message

            // Check login credentials
            if (validateLogin(ID, password)) {

                // Show a welcome message
              //  showWelcomeMessage(ID);
                msgbox("Hello: "+ID);

                TaskTableView taskTableView = new TaskTableView(Integer.parseInt(ID));
                // If credentials are valid, show the initial table view
                taskTableView.showTaskTableView();
            } else {

                msgbox("Invalid ID or Password, please try again");
                // Otherwise, display an error message or handle accordingly
               // System.out.println("Invalid login credentials");
              //  showAlert("Invalid Login", "Invalid username or password. Please try again.");

            }

        });


        // Scene
        Scene scene = new Scene(grid, 300, 200);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        // Set the scene to the stage
        primaryStage.setScene(scene);

        // Show the stage
        primaryStage.show();
    }

    // Method to validate login credentials
    private boolean validateLogin(String id, String password) {
        try (Connection connection = connect()) {
            if (connection != null) {
                String sql = "SELECT * FROM \"Accounts\" WHERE (account_id = ? AND password IN(?));";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, Integer.parseInt(id));
                    preparedStatement.setString(2, password);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    return resultSet.next(); // If there is a match, return true
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            msgbox("Cannot connect to the sql server, check your connection");
        }
        return false; // Default to false if an exception occurs
    }

    private void showWelcomeMessage(String id) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText("Welcome, User " + id + "!");
        alert.setContentText("You have successfully logged in.");
        alert.showAndWait();
    }

    // Method to show an alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void msgbox(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info..");
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Get the dialog pane and message label
        DialogPane dialogPane = alert.getDialogPane();
        Label messageLabel = (Label) dialogPane.lookup(".content.label");
        // Apply CSS styles to the message label
        dialogPane.setStyle("-fx-background-color: #1d1d1d;");
        messageLabel.setStyle(" -fx-font-size: 11pt;\n" +
                "    -fx-font-family: \"Segoe UI Semibold\";\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-opacity: 1;");

        alert.showAndWait();
    }
}
