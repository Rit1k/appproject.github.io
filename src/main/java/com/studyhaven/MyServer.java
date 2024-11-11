package com.studyhaven;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.mysql.cj.xdevapi.Statement;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.*;

public class MyServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/data", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder();

            try (Connection connection = DatabaseConnection.getConnection();
                @SuppressWarnings("rawtypes")
                Statement statement = connection.createStatement()) {

                ResultSet rs = statement.executeQuery("SELECT * FROM your_table");
                while (rs.next()) {
                    response.append(rs.getString("column_name")).append("\n");
                }

            } catch (Exception e) {
                response.append("Database connection error: ").append(e.getMessage());
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }
}
