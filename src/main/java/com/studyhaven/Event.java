package com.studyhaven;

public class Event {
    private int id;
    private String title;
    private String description;
    private String date; // Event date as String, or use LocalDate if preferred
    private int workspaceId; // Foreign key to link the event to a workspace

    // Constructors
    public Event() {
    }

    public Event(int id, String title, String description, String date, int workspaceId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.workspaceId = workspaceId;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(int workspaceId) {
        this.workspaceId = workspaceId;
    }

    // Optional: toString method for easy logging
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", workspaceId=" + workspaceId +
                '}';
    }
}
