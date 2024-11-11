package com.studyhaven;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import com.google.gson.Gson;

public class Task {
    @SuppressWarnings("unused")
    private int id;
    @SuppressWarnings("unused")
    private String title;
    @SuppressWarnings("unused")
    private boolean completed;
    @SuppressWarnings("unused")
    private int workspaceId;

    // Getters and Setters
    // ...
    public static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            List<Task> tasks = new ArrayList<>();
            StringBuilder response = new StringBuilder();
            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {
    
                ResultSet rs = statement.executeQuery("SELECT * FROM tasks");
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitle(rs.getString("title"));
                    task.setCompleted(rs.getBoolean("completed"));
                    tasks.add(task);
                }
            } catch (Exception e) {
                response.append("Database connection error: ").append(e.getMessage());
            }
    
            String jsonResponse = new Gson().toJson(tasks);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        }
    }    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/handletasks", new GetTasksHandler());
        server.createContext("/add-task", new AddTaskHandler());
        server.createContext("/delete-task", new DeleteTaskHandler());
        // server.createContext("/update-task", new UpdateTaskHandler());
        server.createContext("/toggle-task", new ToggleTaskHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class DatabaseConnection {
        public static Connection getConnection() throws Exception {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/studyhaven", "root", "P@ssw0rd");
        }
    }

    static class GetTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("GET".equals(exchange.getRequestMethod())) {
                List<Task> tasks = new ArrayList<>();
                try (Connection connection = DatabaseConnection.getConnection();
                        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tasks");
                        ResultSet rs = stmt.executeQuery()) {
                    
                    while (rs.next()) {

                        Task task = new Task();
                        task.setId(rs.getInt("id"));
                        task.setTitle(rs.getString("title"));
                        task.setCompleted(rs.getBoolean("completed"));
                        System.out.println("id:" + rs.getInt("id") + "title:" + rs.getString("title") + "completed:" + rs.getBoolean("completed") );
                        tasks.add(task);
                        
                    }
                    System.out.println("Number of tasks retrieved: " + tasks.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String jsonResponse = new Gson().toJson(tasks);
                exchange.sendResponseHeaders(200, jsonResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            }
        }
    }

    static class AddTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }

                String taskTitle = body.toString();
                try (Connection connection = DatabaseConnection.getConnection();
                        PreparedStatement stmt = connection
                                .prepareStatement("INSERT INTO tasks (title, completed) VALUES (?, ?)")) {
                    stmt.setString(1, taskTitle);
                    stmt.setBoolean(2, false);
                    stmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
            }
        }
    }

    static class DeleteTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("DELETE".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }

                int taskId = Integer.parseInt(body.toString());
                try (Connection connection = DatabaseConnection.getConnection();
                        PreparedStatement stmt = connection.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
                    stmt.setInt(1, taskId);
                    stmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
            }
        }
    }

    static class ToggleTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle preflight request for CORS
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1); // No content
                return;
            }
    
            // Handle PUT request to toggle task completion
            if ("PUT".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Allow CORS
    
                InputStream inputStream = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
    
                if (body.toString().trim().isEmpty()) {
                    exchange.sendResponseHeaders(400, 0); // Bad Request if body is empty
                    return;
                }
    
                int taskId;
                try {
                    taskId = Integer.parseInt(body.toString());
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, 0); // Bad Request if task ID is invalid
                    return;
                }
    
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement selectStmt = connection.prepareStatement("SELECT completed FROM tasks WHERE id = ?");
                     PreparedStatement updateStmt = connection.prepareStatement("UPDATE tasks SET completed = ? WHERE id = ?")) {
    
                    selectStmt.setInt(1, taskId);
                    ResultSet rs = selectStmt.executeQuery();
                    boolean currentStatus = false;
    
                    if (rs.next()) {
                        currentStatus = rs.getBoolean("completed");
                    } else {
                        // Task not found in the database, return error
                        exchange.sendResponseHeaders(404, 0); // Not Found
                        return;
                    }
    
                    // Toggle the status
                    boolean newStatus = !currentStatus;
                    updateStmt.setBoolean(1, newStatus);
                    updateStmt.setInt(2, taskId);
                    updateStmt.executeUpdate();
    
                    // Log status change
                    System.out.println("Toggled task ID " + taskId + " to " + (newStatus ? "completed" : "not completed"));
    
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0); // Internal Server Error
                    return;
                }
    
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
            }
        }
    }
    
    static class DeleteCompletedTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle CORS: Allow requests from any origin
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    
            // Handle preflight requests (OPTIONS)
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1); // Respond with status 200 for OPTIONS request
                return;
            }
    
            // Handle DELETE requests
            if ("DELETE".equals(exchange.getRequestMethod())) {
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement stmt = connection.prepareStatement("DELETE FROM tasks WHERE completed = ?")) {
                    stmt.setBoolean(1, true); // Delete all completed tasks
                    stmt.executeUpdate();
    
                    exchange.sendResponseHeaders(200, 0); // Successful deletion
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0); // Internal Server Error
                }
                exchange.getResponseBody().close();
            }
        }
    }
    
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}

