package com.studyhaven;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Note {

    private int id;
    private String content;
    private String title; // Store title as String
    private Integer workspaceId; // Store workspace_id as Integer

    // Constructors
    public Note() {}

    public Note(int id, String content, String title, Integer workspaceId) {
        this.id = id;
        this.content = content;
        this.title = title;
        this.workspaceId = workspaceId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", workspaceId=" + workspaceId +
                '}';
    }

    // Main method to start the server
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0); // Note server on port 8080
        server.createContext("/get-notes", new GetNotesHandler());
        server.createContext("/add-note", new AddNoteHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Note server started on port 8080");
    }

    // Database Connection Helper
    static class DatabaseConnection {
        public static Connection getConnection() throws SQLException  {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/studyhaven", "root", "P@ssw0rd");
        }
    }

    // Handler to retrieve all notes
    static class GetNotesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("GET".equals(exchange.getRequestMethod())) {
                List<Note> notes = new ArrayList<>();
                try (Connection connection = DatabaseConnection.getConnection();
                     Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM notes")) {

                    while (rs.next()) {
                        Note note = new Note();
                        note.setId(rs.getInt("id"));
                        note.setContent(rs.getString("content"));
                        note.setTitle(rs.getString("title"));
                        note.setWorkspaceId(rs.getInt("workspace_id")); // Assuming workspace_id is an Integer
                        notes.add(note);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String jsonResponse = new Gson().toJson(notes);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            }
        }
    }

    // Handler to add a new note
    // Handler to add a new note
    static class AddNoteHandler implements HttpHandler {
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
    
                Gson gson = new Gson();
                Note newNote = gson.fromJson(body.toString(), Note.class);
                String content = newNote.getContent();
                String title = newNote.getTitle();
    
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String sql = "INSERT INTO notes (content, title, workspace_id) VALUES (?, ?, NULL)";
                    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, content);
                        stmt.setString(2, title);
                        int rowsAffected = stmt.executeUpdate();
    
                        if (rowsAffected > 0) {
                            ResultSet rs = stmt.getGeneratedKeys();
                            if (rs.next()) {
                                int generatedId = rs.getInt(1);
                                System.out.println("Note added successfully with ID: " + generatedId);
    
                                String jsonResponse = "{\"status\": \"success\", \"id\": " + generatedId + "}";
                                exchange.getResponseHeaders().set("Content-Type", "application/json");
                                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
    
                                OutputStream os = exchange.getResponseBody();
                                os.write(jsonResponse.getBytes());
                                os.close();
                            }
                        } else {
                            System.out.println("Note was not added.");
                            String jsonResponse = "{\"status\": \"error\", \"message\": \"Failed to add note\"}";
                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(500, jsonResponse.getBytes().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(jsonResponse.getBytes());
                            os.close();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error during database operation: " + e.getMessage());
                    e.printStackTrace();
    
                    String jsonResponse = "{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, jsonResponse.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                }
            }
        }
    }
    
    
}
