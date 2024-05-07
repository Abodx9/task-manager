package com.softwareEngineering.taskmanager.Admin;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import java.time.LocalDateTime;

public class ControllerTsk implements Initializable {
    public ComboBox<String> deleteAccountComboBox;
    public TextField task_id;
    public TextField task_title;
    public TextField deleteTaskID;
    public ComboBox workerID;
    public DatePicker deadlined;
    public ComboBox prior;
    public TextArea tsk_disc;
    public ComboBox stuts;

    private void handleException(Exception e, String errorMessage) {
        System.err.println(errorMessage + ": " + e.getMessage());
        msgbox("An error occurred. Please try again.");
        e.printStackTrace();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager", "taskmanager_manager", "cC314bj4d")) {

            // fixed to get them from the accounts table
            String query = "SELECT account_id FROM \"Accounts\"";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

                ObservableList<String> assigneeIds = FXCollections.observableArrayList();

                // Store the retrieved values in a collection
                while (resultSet.next()) {
                    String assigneeId = resultSet.getString("account_id");
                    assigneeIds.add(assigneeId);
                }

                // Set the collection as items for the ComboBox
                workerID.setItems(assigneeIds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<String> priorities = FXCollections.observableArrayList("High", "Medium", "Low");

        prior.setItems(priorities);

        ObservableList<String> accountList = FXCollections.observableArrayList("AccountID1", "AccountID2", "AccountID3");

        deleteAccountComboBox = new ComboBox<>(accountList);
        deleteAccountComboBox.setItems(accountList);

        ObservableList<String> statuses = FXCollections.observableArrayList("Not Started", "In Progress");

        stuts.setItems(statuses);
    }

    public void erstellen(ActionEvent actionEvent) {

        String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
        String username = "taskmanager_manager";
        String password = "cC314bj4d";

        String insertQuery = "INSERT INTO \"Tasks\" (task_id, task_title, task_description, assignee_id, task_deadline, task_created_time, task_priority, task_status) VALUES (?, ?, ?, ?, ?, ?, ?::priorities, ?::status)";

        // Checking if there is an empty field
        if (task_id.getText().isEmpty() || task_title.getText().isEmpty() || tsk_disc.getText().isEmpty()

                || workerID.getSelectionModel().isEmpty() || deadlined.getValue() == null

                || prior.getSelectionModel().isEmpty() || stuts.getSelectionModel().isEmpty()) {
            msgbox("Please fill in all the fields.");
        } else {
            try (Connection connection = DriverManager.getConnection(url, username, password); PreparedStatement statement = connection.prepareStatement(insertQuery)) {

                // Set the parameter values
                statement.setLong(1, Long.parseLong(task_id.getText()));
                statement.setString(2, task_title.getText());
                statement.setString(3, tsk_disc.getText());

                statement.setLong(4, Long.parseLong(workerID.getValue().toString()));

                statement.setTimestamp(5, Timestamp.valueOf(deadlined.getValue().atStartOfDay()));
                statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                statement.setObject(7, prior.getValue().toString());
                statement.setObject(8, stuts.getValue().toString());
                System.out.println(statement);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    msgbox("Task created successfully!");
                }
            } catch (SQLException e) {
                System.err.println("Error creating task: " + e.getMessage());
                msgbox("Failed to create task. Please check your internet connection or try again.");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.err.println("Invalid task ID or worker ID: " + e.getMessage());

                msgbox("Please enter valid IDs for Task and Worker.");
                e.printStackTrace();
            }
        }
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
        messageLabel.setStyle(" -fx-font-size: 11pt;\n" + "    -fx-font-family: \"Segoe UI Semibold\";\n" + "    -fx-text-fill: white;\n" + "    -fx-opacity: 1;");

        alert.showAndWait();
    }


    public void deleteTask(ActionEvent actionEvent) {

        String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
        String username = "taskmanager_manager";
        String password = "cC314bj4d";


        String editedTask = deleteTaskID.getText();

        if (editedTask.isEmpty()) {
            msgbox("There is no Task to delete");
            return;
        }


        String selectQuery = "DELETE FROM \"Tasks\" WHERE task_id = ?";
        try {
            Connection con = DriverManager.getConnection(url, username, password);

            PreparedStatement selectStatement = con.prepareStatement(selectQuery);
            selectStatement.setLong(1, Long.parseLong(editedTask));
            int rowsAffected = selectStatement.executeUpdate();

            msgbox((rowsAffected > 0) ? "Task deleted successfully!" : "Failed to delete Task. Task ID not found.");

        } catch (SQLException e) {
            System.err.println("Error editing task: " + e.getMessage());
            msgbox("Failed to delete task.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid task ID: " + e.getMessage());
            msgbox("Please enter valid Task ID.");
            e.printStackTrace();
        }
    }





    public void editTask(ActionEvent actionEvent) {
        String url = "jdbc:postgresql://psql.f4.htw-berlin.de:5432/taskmanager";
        String username = "taskmanager_manager";
        String password = "cC314bj4d";


        String editedTask = task_id.getText();

        if (editedTask.isEmpty()) {
            msgbox("There is no Task to edit");
            return;
        }

        String selectQuery = "SELECT * FROM \"Tasks\" WHERE task_id = ?";
        String updateQuery = "UPDATE \"Tasks\" SET task_title = ?, task_description = ?, " +
                "assignee_id = ?, task_deadline = ?, task_created_time = ?, task_priority = ?::priorities, " +
                "task_status = ?::status WHERE task_id = ?";

        String insertQuery = "INSERT INTO \"Tasks\" (task_id, task_title, task_description, assignee_id, task_deadline, task_created_time, task_priority, task_status) VALUES (?, ?, ?, ?, ?, ?, ?::priorities, ?::status)";



        try
        {
            Connection con = DriverManager.getConnection(url, username, password);

            PreparedStatement selectStatement = con.prepareStatement(selectQuery);
            PreparedStatement updateStatement = con.prepareStatement(updateQuery);

            selectStatement.setLong(1, Long.parseLong(editedTask));
            ResultSet rs = selectStatement.executeQuery();

            if (rs.next()) {

                //task_title.setText(rs.getString("task_title"));
                //tsk_disc.setText(rs.getString("task_description"));

                //workerID.setText(rs.getString("assignee_id"));
                Long workerIDAsLong = rs.getLong("assignee_id");
                updateStatement.setString(1, task_title.getText());
                updateStatement.setString(2, tsk_disc.getText());
                updateStatement.setLong(3, workerIDAsLong);
                updateStatement.setTimestamp(4, Timestamp.valueOf(deadlined.getValue().atStartOfDay()));
                updateStatement.setTimestamp(5, rs.getTimestamp("task_created_time")); // Keep the original created time
                updateStatement.setObject(6, prior.getValue().toString());
                updateStatement.setObject(7, stuts.getValue().toString());
                updateStatement.setLong(8, Long.parseLong(editedTask));

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected == 0) {
                    msgbox("Update Task failed");
                } else {
                    msgbox("Task updated");
                }
            } else {
                msgbox("Task not found");
            }
        } catch(
                SQLException e)

        {
            System.err.println("Error editing task: " + e.getMessage());
            msgbox("Failed to edit task.");
            e.printStackTrace();
        } catch(
                NumberFormatException e)

        {
            System.err.println("Invalid task ID or worker ID: " + e.getMessage());
            msgbox("Please enter valid Task ID or Worker ID.");
            e.printStackTrace();
        }
    }
}

