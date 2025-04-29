package model;

public class Participant {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int eventId;

    public Participant(int id, String name, String email, String phone, int eventId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.eventId = eventId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Participant ID: " + id + "\n" +
               "Name: " + name + "\n" +
               "Email: " + email + "\n" +
               "Phone: " + phone + "\n" +
               "Event ID: " + eventId;
    }
} 