package com.studyhaven;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/*")
public class StudyHavenServlet extends HttpServlet {
    private StudyHavenDAO dao = new StudyHavenDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // Handle no specific entity path
        if (pathInfo == null || pathInfo.equals("/")) {
            response.getWriter().write("Welcome to StudyHaven API");
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path.");
            return;
        }

        String entity = splits[1];
        int id = splits.length > 2 ? Integer.parseInt(splits[2]) : -1;

        try {
            switch (entity) {
                case "tasks":
                    if (id != -1) {
                        List<Task> tasks = dao.getTasksForWorkspace(id);
                        sendAsJson(response, tasks);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task ID missing.");
                    }
                    break;
                // Future entities (e.g., notes, events) can be added here
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity not found.");
                    break;
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // Handle no specific entity path
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path.");
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Entity path missing.");
            return;
        }

        String entity = splits[1];

        try {
            switch (entity) {
                case "users":
                    // Parse the incoming user JSON
                    User user = gson.fromJson(request.getReader(), User.class);

                    // Validate the required fields (e.g., username, email)
                    if (user.getUsername() == null || user.getEmail() == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields.");
                        return;
                    }

                    // Secure password handling should be implemented here
                    User createdUser = dao.createUser(user.getUsername(), "defaultPassword", user.getEmail());
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    sendAsJson(response, createdUser);
                    break;

                case "tasks":
                    // Parse the incoming task JSON
                    Task task = gson.fromJson(request.getReader(), Task.class);

                    // Validate required fields for Task
                    if (task.getTitle() == null || task.getWorkspaceId() == 0) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required task fields.");
                        return;
                    }

                    // Create the task in the database
                    dao.createTask(task);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    sendAsJson(response, task);
                    break;

                // Add more cases for other entities like notes, events, etc.
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity not found.");
                    break;
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format.");
        }
    }

    // Helper method to send objects as JSON response
    private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = gson.toJson(obj);
        response.getWriter().write(jsonResponse);
    }
}

