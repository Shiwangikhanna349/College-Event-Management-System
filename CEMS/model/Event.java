package model;

import java.time.LocalDate;

public class Event implements Comparable<Event> {
    private int id;
    private String name;
    private LocalDate date;
    private String venue;
    private String organizer;
    private String type;
    private int capacity;
    private int currentRegistrations;

    public static final int SORT_BY_ID = 0;
    public static final int SORT_BY_DATE = 1;
    public static final int SORT_BY_CAPACITY = 2;
    public static final int SORT_BY_TYPE = 3;

    private static int currentSortCriteria = SORT_BY_ID;

    public static void setSortCriteria(int criteria) {
        currentSortCriteria = criteria;
    }

    public Event(int id, String name, LocalDate date, String venue, String organizer, String type, int capacity) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.organizer = organizer;
        this.type = type;
        this.capacity = capacity;
        this.currentRegistrations = 0;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentRegistrations() {
        return currentRegistrations;
    }

    public void setCurrentRegistrations(int currentRegistrations) {
        this.currentRegistrations = currentRegistrations;
    }

    public boolean isFull() {
        return currentRegistrations >= capacity;
    }

    public boolean registerParticipant() {
        if (!isFull()) {
            currentRegistrations++;
            return true;
        }
        return false;
    }

    public void cancelRegistration() {
        if (currentRegistrations > 0) {
            currentRegistrations--;
        }
    }

    @Override
    public String toString() {
        return "Event ID: " + id + "\n" +
               "Name: " + name + "\n" +
               "Date: " + date + "\n" +
               "Venue: " + venue + "\n" +
               "Organizer: " + organizer + "\n" +
               "Type: " + type + "\n" +
               "Capacity: " + capacity + "\n" +
               "Current Registrations: " + currentRegistrations;
    }

    @Override
    public int compareTo(Event other) {
        switch (currentSortCriteria) {
            case SORT_BY_DATE:
                return this.date.compareTo(other.date);
            case SORT_BY_CAPACITY:
                return Integer.compare(this.capacity, other.capacity);
            case SORT_BY_TYPE:
                return this.type.compareTo(other.type);
            case SORT_BY_ID:
            default:
                return Integer.compare(this.id, other.id);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event other = (Event) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
} 