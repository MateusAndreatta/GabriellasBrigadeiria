package com.mateusandreatta.gabriellasbrigadeiria.model;

public class NotificationRequest {
    private Notification notification;
    private String to;

    public NotificationRequest(String title, String text, String token) {
        this.notification = new Notification(title,text);
        this.to = token;
    }
}

class Notification {
    String title;
    String body;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
